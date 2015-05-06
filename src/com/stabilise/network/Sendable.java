package com.stabilise.network;

import java.io.IOException;

import com.stabilise.util.io.DataInStream;
import com.stabilise.util.io.DataOutStream;

/**
 * Defines the two complementary methods {@code readData} and {@code writeData}
 * which allow an object to export its fields to a data stream, across which it
 * may be reconstructed.
 * 
 * <p>As a general guideline, if {@code obj1} is an object whose data is
 * exported through {@code writeData}, and {@code obj2} is an object which
 * reads in {@code obj1}'s exported data through {@code readData}, then {@code
 * obj1.equals(obj2)} should return {@code true}.
 */
public interface Sendable {
	
	/**
	 * Reads this object's data from the given DataInStream.
	 * 
	 * @throws NullPointerException if {@code in} is {@code null}.
	 * @throws IOException if an I/O error occurs.
	 */
	void readData(DataInStream in) throws IOException;
	
	/**
	 * Writes this object's data to the given DataOutStream.
	 * 
	 * @throws NullPointerException if {@code out} is {@code null}.
	 * @throws IOException if an I/O error occurs.
	 */
	void writeData(DataOutStream out) throws IOException;
	
}
