package com.stabilise.network;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.stabilise.network.packet.Packet;
import com.stabilise.util.Log;
import com.stabilise.util.annotation.UserThread;

/**
 * A TCPConnection instance maintains a connection between a server and a
 * client, and is the gateway for interaction between the two.
 */
public class TCPConnection {
	
	protected final Socket socket;
	
	private final DataInputStream in;
	private final DataOutputStream out;
	
	private final Queue<Packet> packetQueueIn = new ConcurrentLinkedQueue<Packet>();
	private final Queue<Packet> packetQueueOut = new ConcurrentLinkedQueue<Packet>();
	
	/** {@code true} if this is a server-side connection. */
	private final boolean server;
	
	/** {@code true} if this connection is active. This is volatile. */
	private volatile boolean running = true;
	/** {@code true} if the connection has been terminated. This is volatile. */
	private volatile boolean terminated = false;
	
	private TCPReadThread readThread;
	private TCPWriteThread writeThread;
	
	private int totalPacketsSent = 0;
	private int totalPacketsReceived = 0;
	
	private Log log;
	
	
	/**
	 * Creates a new TCPConnection.
	 * 
	 * @param socket The socket upon which to base the connection.
	 * @param server Whether or not this is a server-side connection.
	 * 
	 * @throws IOException if the connection could not be established.
	 */
	public TCPConnection(Socket socket, boolean server) throws IOException {
		this.socket = socket;
		this.server = server;
		
		log = Log.getAgent(server ? "SERVER" : "CLIENT");
		
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		
		readThread = new TCPReadThread(this, server ? "ServerSocket" : "ClientSocket");
		writeThread = new TCPWriteThread(this, server ? "ServerSocket" : "ClientSocket");
		
		readThread.start();
		writeThread.start();
	}
	
	/**
	 * Queues a packet for sending.
	 */
	@UserThread("MainThread")
	public void queuePacket(Packet packet) {
		if((!server && packet.isClientPacket()) || (server && packet.isServerPacket())) {
			packetQueueOut.offer(packet);
		} else {
			if(server)
				log.postWarning("Attempting to send a client-only packet!");
			else
				log.postWarning("Attempting to send a server-only packet!");
		}
	}
	
	/**
	 * Polls the input packet queue for the next packet.
	 * 
	 * @return The oldest queued packet, or null if the queue is empty.
	 */
	@UserThread("MainThread")
	public Packet getPacket() {
		return packetQueueIn.poll();
	}
	
	/**
	 * Blocks the current thread until a packet is received, or this thead is
	 * interrupted.
	 * 
	 * @return The oldest queued packet, or the first packet to be added if the
	 * queue is empty.
	 */
	public Packet getPacketWithBlock() {
		Packet packet;
		while((packet = getPacket()) == null) {
			try {
				synchronized(this) {
					wait(); // wait until the read thread invokes notifyAll
				}
			} catch(InterruptedException e) {
				log.postWarning("Interrupted while waiting for packet");
			}
		}
		return packet;
	}
	
	/**
	 * Reads a packet from the socket's input stream. A return value of {@code
	 * false} indicates either an I/O exception or the end of the stream having
	 * been reached.
	 * 
	 * @return {@code true} if a packet was successfully read; {@code false}
	 * otherwise.
	 */
	@UserThread("ReadThread")
	public boolean readPacket() {
		if(terminated)
			return false;
		try {
			Packet packet = Packet.readPacket(in);
			if(packet == null)
				return false;
			packetQueueIn.offer(packet);
			totalPacketsReceived++;
			return true;
		} catch(IOException e) {
			log.postSevere("Exception while reading packet", e);
		}
		return false;
	}
	
	/**
	 * Writes a packet to the socket's output stream.
	 * 
	 * @return {@code true} if the packet was sent; {@code false} otherwise.
	 */
	@UserThread("WriteThread")
	public boolean writePacket() {
		Packet packet = packetQueueOut.poll();
		if(packet == null)
			return false;
		try {
			Packet.writePacket(out, packet);
			totalPacketsSent++;
			return true;
		} catch(IOException e) {
			log.postSevere("Exception while writing packet", e);
		}
		return false;
	}
	
	/**
	 * Closes the connection and releases any resources held by it.
	 */
	public void closeConnection() {
		if(terminated) {
			log.postWarning("Connection already closed!");
			return;
		}
		
		terminated = true;
		
		log.postInfo("Closing connection...");
		
		int written = out.size();
		
		try {
			in.close();
		} catch(IOException e) {
			log.postSevere("Could not close input stream!");
		}
		
		try {
			out.close();
		} catch(IOException e) {
			log.postSevere("Could not close output stream!");
		}
		
		running =  false;
		
		// This needs to go before the thread-removing because only the closing
		// of the socket will wake the read thread from its constant blocking
		// from the read() method of the DataInputStream class.
		try {
			socket.close();
		} catch (IOException e) {
			log.postSevere("Connection socket could not close!");
		}
		
		readThread.interrupt();
		writeThread.interrupt();
		
		try {
			readThread.join();
		} catch (InterruptedException e) {
			log.postWarning("Interrupted while waiting for read thread to join!");
		}
		
		try {
			writeThread.join();
		} catch (InterruptedException e) {
			log.postWarning("Interrupted while waiting for write thread to join!");
		}
		
		readThread = null;
		writeThread = null;
		
		log.postInfo("Connection closed; "
				+ totalPacketsSent + " packet(s) sent (" + written + " bytes), "
				+ totalPacketsReceived + " packet(s) received .");
	}
	
	//--------------------==========--------------------
	//-------------=====Getters/Setters=====------------
	//--------------------==========--------------------
	
	/**
	 * @return This connection's output stream.
	 */
	public DataOutputStream getOutputStream() {
		return out;
	}
	
	/**
	 * @return {@code true} if the connection is active; {@code false}
	 * otherwise.
	 */
	public boolean isRunning() {
		return running;
	}
	
}
