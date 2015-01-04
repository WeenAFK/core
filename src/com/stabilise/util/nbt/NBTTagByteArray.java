package com.stabilise.util.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An NBT tag containing an arbitrary-length byte array as it's payload.
 * 
 * @see NBTTag
 */
public class NBTTagByteArray extends NBTTag {
	
	/** The tag's data. */
	public byte[] data;
	
	
	/**
	 * Creates a new unnamed byte array tag.
	 */
	public NBTTagByteArray() {
		super();
	}
	
	/**
	 * Creates a new byte array tag.
	 * 
	 * @param tagName The tag's name.
	 */
	public NBTTagByteArray(String tagName) {
		super(tagName);
	}
	
	/**
	 * Creates a new byte array tag.
	 * 
	 * @param tagName The tag's name.
	 * @param data The tag's data payload.
	 */
	public NBTTagByteArray(String tagName, byte[] data) {
		super(tagName);
		this.data = data;
	}
	
	@Override
	void write(DataOutputStream out) throws IOException {
		out.writeInt(data.length);
		out.write(data);
	}
	
	@Override
	void load(DataInputStream in) throws IOException {
        data = new byte[in.readInt()];
        in.readFully(data);
	}
	
	@Override
	byte getId() {
		return NBTTag.BYTE_ARRAY;
	}
	
	@Override
	public NBTTag copy() {
		return new NBTTagByteArray(name, data);
	}
	
	@Override
	public String toString() {
		return "[" + data.length + " bytes]";
	}
	
}
