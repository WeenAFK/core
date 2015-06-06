package com.stabilise.util.maths;

import com.badlogic.gdx.math.MathUtils;
import com.stabilise.util.annotation.NotThreadSafe;

/**
 * A 2x2 row-major matrix. Such a matrix takes the form:
 * 
 * <pre>
 * | m00 m01 |
 * | m10 m11 |</pre>
 * 
 * <p>These entries are stored in an array of the form <tt>{m00, m01, m10,
 * m11}</tt>.
 */
@NotThreadSafe
public class Matrix2 {
	
	/** Array indices for each matrix entry as stored in {@link #val}. */
	public static final int M00 = 0, M01 = 1, M10 = 2, M11 = 3;
	
	
	/** The matrix's entries. */
	public float[] val;
	
	
	/**
	 * Creates an identity matrix.
	 */
	public Matrix2() {
		val = new float[] {
			1f, 0f,
			0f, 1f
		};
	}
	
	/**
	 * Creates a matrix with the specified entries.
	 */
	public Matrix2(float m00, float m01, float m10, float m11) {
		val = new float[] {
			m00, m01,
			m10, m11
		};
	}
	
	/**
	 * Creates a new matrix with the specified entries.
	 * 
	 * @param vals The entries. This should have a length of at least 4.
	 */
	public Matrix2(float... vals) {
		this.val = vals;
	}
	
	/**
	 * Sets the entries of this matrix.
	 * 
	 * @return This matrix, for chaining operations.
	 */
	public Matrix2 set(float m00, float m01, float m10, float m11) {
		val[M00] = m00;
		val[M01] = m01;
		val[M10] = m10;
		val[M11] = m11;
		return this;
	}
	
	/**
	 * Copies the values of the provided matrix to this matrix.
	 * 
	 * @param m The matrix to copy.
	 * 
	 * @return This matrix, for chaining operations.
	 */
	public Matrix2 set(Matrix2 m) {
		System.arraycopy(m.val, 0, val, 0, val.length);
		return this;
	}
	
	/**
	 * Sets this matrix to the identity matrix.
	 * 
	 * @return This matrix, for chaining operations.
	 */
	public Matrix2 identity() {
		return set(1f, 0f, 0f, 1f);
	}
	
	/**
	 * Gets the transpose of this matrix. This matrix will remain unmodified.
	 * 
	 * @return The transpose of this matrix.
	 */
	public Matrix2 transpose() {
		return new Matrix2(new float[] {
				val[M00], val[M10],
				val[M01], val[M11]
		});
	}
	
	/**
	 * Gets the determinant of this matrix.
	 */
	public float det() {
		return val[M00] * val[M11] - val[M01] * val[M10];
	}
	
	/**
	 * Gets the inverse of this matrix.
	 * 
	 * @throws ArithmeticException if this matrix does not have an inverse.
	 */
	public Matrix2 inverse() {
		float det = det();
		if(det == 0)
			throw new ArithmeticException("Determinant is zero");
		return doInverse(1 / det);
	}
	
	private Matrix2 doInverse(float invDet) {
		return new Matrix2(
				invDet * val[M11],  -invDet * val[M01],
				-invDet * val[M10], invDet * val[M00]
		);
	}
	
	/**
	 * Postmultiplies this matrix (A) with the specified matrix (B) and stores
	 * the result in this matrix. i.e. A = AB.
	 * 
	 * @return This matrix, for chaining operations.
	 * @throws NullPointerException if {@code m} is {@code null}.
	 */
	public Matrix2 mul(Matrix2 m) {
		return multiply(this, m, this);
	}
	
	/**
	 * Premultiplies this matrix (A) with the specified matrix (B) and stores
	 * the result in this matrix. i.e. A = BA.
	 * 
	 * @return This matrix, for chaining operations.
	 * @throws NullPointerException if {@code m} is {@code null}.
	 */
	public Matrix2 mulLeft(Matrix2 m) {
		return multiply(m, this, this);
	}
	
	/**
	 * Transforms the specified vector (V) by this matrix (M) and returns the
	 * resulting vector. The supplied vector will not be modified.
	 * 
	 * @param vec The vector to multiply.
	 * 
	 * @return The resulting vector.
	 * @throws NullPointerException if {@code vec} is {@code null}.
	 */
	public Vec2 transform(Vec2 vec) {
		return new Vec2(
				val[M00]*vec.x + val[M01]*vec.y,
				val[M10]*vec.x + val[M11]*vec.y
		);
	}
	
	/**
	 * Sets this matrix to a rotation matrix, which will rotate a vector
	 * anticlockwise about (0,0).
	 * 
	 * @param rad The angle, in radians.
	 * 
	 * @return This matrix, for chaining operations.
	 */
	public Matrix2 setToRotation(float rad) {
		float cos = MathUtils.cos(rad); //(float)Math.cos(rad);
		float sin = MathUtils.sin(rad); //(float)Math.sin(rad);
		return set(cos, -sin, sin, cos);
	}
	
	/**
	 * Transforms the specified vector (V) by this matrix (M) and stores it in
	 * the specified destination vector (D). i.e. D = MV.
	 * 
	 * @param vec The vector to multiply.
	 * @param dest The destination vector.
	 * 
	 * @return The destination vector.
	 * @throws NullPointerException if either {@code vec} or {@code dest} are
	 * {@code null}.
	 */
	/*
	public Vec2 transform(Vec2 vec, Vec2 dest) {
		float x = val[M00]*vec.x + val[M01]*vec.y;
		float y = val[M10]*vec.x + val[M11]*vec.y;
		return dest.set(x, y);
	}
	*/
	
	//--------------------==========--------------------
	//------------=====Static Functions=====------------
	//--------------------==========--------------------
	
	/**
	 * Multiplies the left matrix (A) by the right matrix (B) and stores the
	 * result in the destination matrix (C). i.e. C = AB.
	 * 
	 * @return The destination matrix.
	 * @throws NullPointerException if any argument is {@code null}.
	 */
	public static Matrix2 multiply(Matrix2 left, Matrix2 right, Matrix2 dest) {
		return dest.set(
				left.val[M00]*right.val[M00] + left.val[M01]*right.val[M10],
				left.val[M00]*right.val[M01] + left.val[M01]*right.val[M11],
				left.val[M10]*right.val[M00] + left.val[M11]*right.val[M10],
				left.val[M10]*right.val[M01] + left.val[M11]*right.val[M11]
		);
	}
	
}
