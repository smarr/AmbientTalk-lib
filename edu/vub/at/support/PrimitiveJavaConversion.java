/**
 * AmbientTalk/2 Project
 * PrimitiveJavaConversion.java created on 14-apr-2007 at 11:09:52
 * (c) Programming Technology Lab, 2006 - 2007
 * Authors: Tom Van Cutsem & Stijn Mostinckx
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.vub.at.support;

/**
 * Auxiliary class to enable less painful construction of Long, Byte, Char and Float
 * objects from within AmbientTalk.
 * 
 * @author tvcutsem
 */
public class PrimitiveJavaConversion {

	/**
	 * Auxiliary conversion function to symbiotically create bytes from numbers.
	 * @param number an AmbientTalk number to be converted
	 * @return the byte representation of the number
	 * @throws IllegalArgumentException if the number is not in the proper range of byte values
	 */
	public static Byte intToByte(int number) throws IllegalArgumentException {
		if (number < Byte.MIN_VALUE | number > Byte.MAX_VALUE) {
			throw new IllegalArgumentException("Number " + number + " cannot be converted to byte");
		}
		return new Byte((byte) number);
	}
	
	/**
	 * Auxiliary conversion function to symbiotically create floats from fractions.
	 * @param fraction an AmbientTalk fraction to be converted
	 * @return the float representation of the fraction
	 * @throws IllegalArgumentException if the fraction is not in the proper range of float values
	 */
	public static Float doubleToFloat(double fraction) throws IllegalArgumentException {
		if (fraction < Float.MIN_VALUE | fraction > Float.MAX_VALUE) {
			throw new IllegalArgumentException("Fraction " + fraction + " cannot be converted to float");
		}
		return new Float((float) fraction);
	}
	
	/**
	 * Auxiliary conversion function to symbiotically create longs from numbers.
	 * This operation should not fail as long values subsume numbers.
	 * @param number an AmbientTalk number to be converted
	 * @return the long representation of the number
	 */
	public static Long intToLong(int number) throws IllegalArgumentException {
		return new Long((long) number);
	}
	
	/**
	 * Auxiliary conversion function to symbiotically create shorts from numbers.
	 * @param number an AmbientTalk number to be converted
	 * @return the short representation of the number
	 * @throws IllegalArgumentException if the number is not in the proper range of short values
	 */
	public static Short intToShort(int number) throws IllegalArgumentException {
		if (number < Short.MIN_VALUE | number > Short.MAX_VALUE) {
			throw new IllegalArgumentException("Number " + number + " cannot be converted to short");
		}
		return new Short((short) number);
	}
	
}
