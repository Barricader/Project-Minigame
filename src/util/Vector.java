package util;

import java.lang.Math;

/**
 * Vector class, holds x and y. Can use this to perform math on the vector(s).
 * 
 * @author jojones
 */
public class Vector {
	protected double dX;
	protected double dY;

	public Vector() {
		dX = dY = 0.0;
	}

	public Vector(double dX, double dY) {
		this.dX = dX;
		this.dY = dY;
	}

	/**
	 * Convert vector to a string
	 * 
	 * @return String - "Vector(x + y)"
	 */
	public String toString() {
		return "Vector(" + dX + ", " + dY + ")";
	}

	/**
	 * Compute magnitude of this vector
	 * 
	 * @return Double - Length of vector
	 */
	public double length() {
		return Math.sqrt(dX * dX + dY * dY);
	}

	/**
	 * Sum of two vectors
	 * 
	 * @param v1
	 *            - A vector to add to this vector
	 * @return Vector - Returns a sum vector
	 */
	public Vector add(Vector v1) {
		Vector v2 = new Vector(this.dX + v1.dX, this.dY + v1.dY);
		return v2;
	}

	/**
	 * Difference of two vectors
	 * 
	 * @param v1
	 *            - Subtracts from this vector
	 * @return Vector - Returns a difference vector
	 */
	public Vector sub(Vector v1) {
		Vector v2 = new Vector(this.dX - v1.dX, this.dY - v1.dY);
		return v2;
	}

	/**
	 * Scales this vector by a constant
	 * 
	 * @param scaleFactor
	 *            - Constant to scale vector by
	 * @return Vector - Returns a vector that has been scaled
	 */
	public Vector scale(double scaleFactor) {
		Vector v2 = new Vector(this.dX * scaleFactor, this.dY * scaleFactor);
		return v2;
	}

	/**
	 * Normalizes a vector's length
	 * 
	 * @return Vector - Returns a vector that has been normalized
	 */
	public Vector normalize() {
		Vector v2 = new Vector();

		double length = length();
		if (length != 0) {
			v2.dX = this.dX / length;
			v2.dY = this.dY / length;
		}

		return v2;
	}

	/**
	 * Dot product of two vectors
	 * 
	 * @param v1
	 *            - Vector to be multiplied with
	 * @return Double - Returns the product of the two vectors
	 */
	public double dotProduct(Vector v1) {
		return this.dX * v1.dX + this.dY * v1.dY;
	}

	public double getdX() {
		return dX;
	}

	public double getdY() {
		return dY;
	}
}