/**************************************************************
 * Author: Gordon Lu										  *
 * Designed for Dr.Garrison's Algorithm Implementation class  *
 * 															  *
 * Description: RSA Encryption								  *
 *************************************************************/

/******************************************************************************************
 * CS/COE 1501 Assignment 5:
 * 
 * Goal: 
 * To get hands on experience with algorithms to perform mathematical operations on 
 * large integers, using RSA for instance
 * 
 * Important Note: This project should NEVER be used for any security applications.
 * - It is purely academic. Always use trustted and tested crypto libraries!
 * 
******************************************************************************************/
import java.util.Random;
import java.math.BigInteger;
import java.util.Arrays;
import java.nio.*;
public class LargeInteger{
	
	/**************************Global Statics***********************/
	/**/private final byte[] ONE = {(byte) 1};					 /**/				  
	/**/private final byte[] ZERO = {(byte) 0};				 	 /**/
	/**/private final byte[] NEGATIVE_ONE = {(byte) -1};		 /**/
	/**/private final int BYTE_SIZE = 8;						 /**/
	/**/private byte[] val;										 /**/
	/***************************************************************/

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		val = b;
	}
	/**
	 * Secondary Constructor. Generates a LargeInreger from a given Byte array. 
	 * <p>Utilized in RsaSign, upon reading bytes from the file, a Vector is utilized to parse data...</p>
	 * @param b represents the Byte Array parsed from the Vector Object into a primitive byte array
	 */
    public LargeInteger(Byte[] b) {
		byte[] temp = new byte[b.length];
		int j = 0;
		for(Byte bit: b)
		{
			temp[j++] = bit.byteValue();
		}
        val = temp;
    }
	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		//Root problem of performance degradation???
		return this.add(other.negate());
	}
	

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
    public LargeInteger multiply(LargeInteger other) 
    {
		LargeInteger product = new LargeInteger(ZERO); //represents the result of the multiplier and the multiplicand
        LargeInteger multiplier = new LargeInteger(ZERO);	//represents the multiplier!
		LargeInteger multiplicand = new LargeInteger(ZERO); //represents the multiplicand!
		LargeInteger zero = new LargeInteger(ZERO);
		LargeInteger determine_inversion = new LargeInteger(ZERO);	//Determine whether we need to invert!
		boolean performOnesComplement = false; //Determine whether or not we need to perform One's Complement!
		if(other.isZero() || this.isZero())	//First Check: any number times zero is zero! Therefore, before even attempting multiplication, first check if either the multiplicand or multiplier are zero!
		{
			return new LargeInteger(ZERO);
		}
		multiplier = handle_mult_u_logic(this, other, multiplier, multiplicand, performOnesComplement, "multiplier");	//Simply determine whether or not the multiplier needs to be negated in order to perform unsigned multiplication!!
		multiplicand = handle_mult_u_logic(this, other, multiplier, multiplicand, performOnesComplement, "multiplicand");	//Simply determine whether or not the multiplicand needs to be negated in order to perform unsigned multiplication!!
		zero = handle_mult_u_logic(this, other, multiplier, multiplicand, performOnesComplement, "zero");
		determine_inversion = handle_mult_u_logic(this, other, multiplier, multiplicand, performOnesComplement, "one's_complement");	//Determine whether the final result needs to be negated! 
		
		performOnesComplement = determine_ones_complement(determine_inversion);		//extract boolean from the mult_u_logic function!
        product = performGradeSchoolAlgorithm(multiplier, multiplicand, product);	//Perform the rudimentary gradeschool algorithm

		if (performOnesComplement) //if the final multiplication was determined to be negative, invert the bits!
		{
            product = product.negate();	//If one of the operands was negative, invert the bits of the resultant product.
        }
        return product.truncate();	//Trim any excess 0s and Fs from the output (Java will pad accordingly to make the bits multiplied the same length)
		
	}
	
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	public LargeInteger[] XGCD(LargeInteger other) throws ArithmeticErrorException
	{
		if (other.isZero()) 
		{
            return new LargeInteger[]{this, new LargeInteger(ONE), new LargeInteger(ZERO)};
		} 
		else 
		{
            LargeInteger[] vals = other.XGCD(this.performModulus(other));
            LargeInteger gcd = vals[0];
            LargeInteger p = vals[2];
			LargeInteger q = vals[1].subtract(this.divide(other).multiply(vals[2]));
			
            return new LargeInteger[]{gcd.truncate(), p.truncate(), q.truncate()};
        }
	}
	 
	 /**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n modulus value to use
	  * @return this^y mod n
	  */
	 public LargeInteger modularExp(LargeInteger y, LargeInteger n) throws ArithmeticErrorException {
		LargeInteger modExpResult = new LargeInteger(ONE);
		LargeInteger base = new LargeInteger(copyByteArray(this.getVal()));
		LargeInteger exponent = new LargeInteger(copyByteArray(y.getVal()));
		if(n.isNegative())
		{
			throw new ArithmeticErrorException("LargeInteger: Arithmetic Error. Modulus not positive.");
		}
		/*
		CS 1501 Algorithm as described in Class:
		while(bits remain in y):
			if(bit == 1):
			ans = ans * ans mod(n)

			y <<= (byte)1
			y = y * y mod(n)
		*/
		while(!(exponent.isZero()))	//if the exponent is not
		{
			if(!exponent.isLSBZero()) //if the exponent is odd...
			{
				modExpResult = modExpResult.multiply(base).performModulus(n);
			}
			exponent = exponent.rightShift(1);
			base = base.multiply(base).performModulus(n);

		}
		return modExpResult;
	 }
	 
	 /**************************************HELPER*******************METHODS*************************************/
	 
	 //CS 0447 Division Algorithm: Courtesy of Jarrett Billinglsey :)
	 public LargeInteger divide(LargeInteger other) throws ArithmeticErrorException
	 {
		return divide_and_mod(other)[0];
		/* LargeInteger divisor = new LargeInteger(ZERO);
		LargeInteger dividend = new LargeInteger(ZERO);
		LargeInteger remainder = new LargeInteger(ZERO);
		LargeInteger quotient = new LargeInteger(ZERO);
		LargeInteger determine_inversion = new LargeInteger(ZERO);
		boolean performOnesComplement = false;

		divisor = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "divisor");
		dividend = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "dividend");
		determine_inversion = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "one's_complement");
		performOnesComplement = determine_ones_complement(determine_inversion);

		quotient = performGradeSchoolDivsionAlgorithm(dividend, divisor, quotient, remainder, performOnesComplement, "quotient");
		// if the finalresult should be negative
		if(performOnesComplement)
		{
			// negate remainder
			quotient = quotient.negate();
		}


		// return remainder
		return quotient.truncate();  */
	}
   	 public LargeInteger performModulus(LargeInteger other) throws ArithmeticErrorException
   	 {
		if(other.isNegative())
		{
			throw new ArithmeticErrorException("LargeInteger: Arithmetic Error. Modulus not positive.");
		}
		return divide_and_mod(other)[1];
		/*LargeInteger divisor = new LargeInteger(ZERO);
		LargeInteger dividend = new LargeInteger(ZERO);
		LargeInteger remainder = new LargeInteger(ZERO);
		LargeInteger quotient = new LargeInteger(ZERO);
		LargeInteger determine_inversion = new LargeInteger(ZERO);
		boolean performOnesComplement = false;

		divisor = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "divisor");
		dividend = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "dividend");
		determine_inversion = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "one's_complement");
		performOnesComplement = determine_ones_complement(determine_inversion);

		remainder = performGradeSchoolDivsionAlgorithm(dividend, divisor, quotient, remainder, performOnesComplement, "remainder");

		if(performOnesComplement)
		{
			remainder = remainder.negate();
		}
		return remainder.truncate(); */
   		// divide the integer that called with method by the integer passed into this method, return remainder
		// divisor is a clone of the large int that was passed into the method
		/* LargeInteger divisor = new LargeInteger(ZERO);
		LargeInteger dividend = new LargeInteger(ZERO);
		LargeInteger remainder = new LargeInteger(ZERO);
		LargeInteger quotient = new LargeInteger(ZERO);
		LargeInteger determine_inversion = new LargeInteger(ZERO);
		boolean performOnesComplement = false;

		divisor = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "divisor");
		dividend = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "dividend");
		determine_inversion = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "one's_complement");
		performOnesComplement = determine_ones_complement(determine_inversion);

		remainder = performGradeSchoolDivsionAlgorithm(dividend, divisor, quotient, remainder, performOnesComplement, "remainder");
		// if the finalresult should be negative
		if(performOnesComplement)
		{
			// negate remainder
			remainder = remainder.negate();
		}


		// return remainder
		return remainder.truncate(); */
		}
	 //Systematically handle both division and mod in a single function!
	 public LargeInteger[] divide_and_mod(LargeInteger other) throws ArithmeticErrorException
	 {
		LargeInteger divisor = new LargeInteger(ZERO);
		LargeInteger dividend = new LargeInteger(ZERO);
		LargeInteger remainder = new LargeInteger(ZERO);
		LargeInteger quotient = new LargeInteger(ZERO);
		LargeInteger determine_inversion = new LargeInteger(ZERO);
		boolean performOnesComplement = false;
		if(other.compareTo(new LargeInteger(ZERO)) == 0)
		{
			throw new ArithmeticErrorException("LargeInteger: A fatal error occured. DIVIDE BY ZERO");
		}
		divisor = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "divisor");
		dividend = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "dividend");
		determine_inversion = handle_division_algorithm_logic(this, other, dividend, divisor, performOnesComplement, "one's_complement");
		performOnesComplement = determine_ones_complement(determine_inversion);

		quotient = performGradeSchoolDivsionAlgorithm(dividend, divisor, quotient, remainder, performOnesComplement, "quotient");
		remainder = performGradeSchoolDivsionAlgorithm(dividend, divisor, quotient, remainder, performOnesComplement, "remainder");
		if(performOnesComplement)
		{
			quotient = quotient.negate();
			remainder = remainder.negate();
		}
		
		return new LargeInteger[]{quotient.truncate(), remainder.truncate()};
	 }
	 public LargeInteger handle_division_algorithm_logic(LargeInteger curr, LargeInteger other, LargeInteger dividend, LargeInteger divisor, boolean performOnesComplement, String sentinelKey)
	 {
		LargeInteger dividendRetVal = new LargeInteger(ZERO);
		LargeInteger divisorRetVal = new LargeInteger(ZERO);

		if(curr.isNegative())
		{
			dividend = dividend.negate();
			dividendRetVal = dividend;
			performOnesComplement = !performOnesComplement;
		}
		else 
		{
			dividend = new LargeInteger(copyByteArray(curr.getVal())); 
			dividendRetVal = dividend;
        }

		if(other.isNegative())
		{
			divisor = divisor.negate();
			divisorRetVal = divisor;
			performOnesComplement = !performOnesComplement;
		}
		else
		{
			divisor = new LargeInteger(copyByteArray(other.getVal()));
			divisorRetVal = divisor;
		}


		switch(sentinelKey)
		{
			case "dividend":
				return dividendRetVal;
			case "divisor":
				return divisorRetVal;
			case "one's_complement":
				if(performOnesComplement)
				{
					return new LargeInteger(ONE);	//If we have to invert the product, indicate that we need to!
				}
				else
				{
					return new LargeInteger(ZERO); 	//If we don't need to invert the product, indicate that we don't!
				}				
			default:
				return new LargeInteger(ZERO);
		}
	 }
	 public LargeInteger performGradeSchoolDivsionAlgorithm(LargeInteger dividend, LargeInteger divisor, LargeInteger quotient, LargeInteger remainder, boolean performOnesComplement, String sentinelKey)
	 {
		LargeInteger dividend_deep_copy = new LargeInteger(dividend.getVal());
		LargeInteger divisor_deep_copy = new LargeInteger(divisor.getVal());
		LargeInteger quotient_deep_copy = new LargeInteger(quotient.getVal());
		LargeInteger remainder_deep_copy = new LargeInteger(remainder.getVal());
		LargeInteger remainder_ret_val = new LargeInteger(ZERO);
		LargeInteger quotient_ret_val = new LargeInteger(ZERO);

		int max_byte_size_allocation = Math.max(dividend_deep_copy.length(), divisor_deep_copy.length());
		
		remainder_deep_copy = new LargeInteger(new byte[max_byte_size_allocation]);
		quotient_deep_copy = new LargeInteger(new byte[max_byte_size_allocation]);

		int num_loop_iterations = dividend_deep_copy.length() * BYTE_SIZE + 1;
	
		// for each bit in the dividend
		for(int i = 0; i < num_loop_iterations; i++)
		{
			// shift quotient left by one bit
			quotient_deep_copy = quotient_deep_copy.shiftLeft("logical");
			// if divisor is less than or equal to remainder
			if(!(divisor_deep_copy.compareTo(remainder_deep_copy) == 1))
			{
				// remainder = remainder - divisor
				remainder_deep_copy = remainder_deep_copy.subtract(divisor_deep_copy);
				// set quotient's least significant bit to be 1
				quotient_deep_copy.setLSB(true);
			}
			//For some reason, the shift is off by 2, therefore just do this for all of the iterations except for the last one!
			if(i < (num_loop_iterations - 1))	//Do this on every iteration except the last one!!
			{
				// shift remainder left by 1 bit
				remainder_deep_copy = remainder_deep_copy.shiftLeft("logical");
				// set remainder's least significant bit to be dividend's most significant bit
				remainder_deep_copy.setLSB(dividend_deep_copy.isNegative());
				dividend_deep_copy = dividend_deep_copy.shiftLeft("logical");
			}
		}
		quotient_ret_val = quotient_deep_copy;
		remainder_ret_val = remainder_deep_copy;

		
		switch(sentinelKey)
		{
			case "quotient":
				return quotient_ret_val;
			case "remainder":
				return remainder_ret_val;
			default:
				return new LargeInteger(ZERO);
		}
	 }

	
	 public LargeInteger divide_cs0447(LargeInteger divisor)
	 {
		//Need to account for MSB...
		if (divisor.isZero()) {
			return null;
		}
		if (this.compareTo(divisor) == 0) {
			return new LargeInteger(ONE);
		}
		if (this.compareTo(divisor) == -1) { // divisor is less than dividend
			return new LargeInteger(ZERO);
		}
		LargeInteger quotient = new LargeInteger(copyByteArray(ZERO));
        quotient = performDivisionAlgorithm(this, divisor);

		return quotient; 
	 }
	 
	 //CS 0447 Division Algorithm: Courtesy of Jarrett Billinglsey :)
	 public LargeInteger performDivisionAlgorithm(LargeInteger curr, LargeInteger other)
	 {
		LargeInteger base_logic = new LargeInteger(ZERO);
		LargeInteger equality_logic = new LargeInteger(ZERO);
		LargeInteger dividend = new LargeInteger(ZERO);
		LargeInteger divisor = new LargeInteger(ZERO);
		LargeInteger deep_copy_current = new LargeInteger(copyByteArray(curr.getVal()));
		boolean performOnesComplement = false;
		
		base_logic = handle_div_logic(deep_copy_current, other, dividend, divisor, performOnesComplement, "base_case");
		equality_logic = handle_div_logic(deep_copy_current, other, dividend, divisor, performOnesComplement, "equality");
		dividend = handle_div_logic(deep_copy_current, other, dividend, divisor, performOnesComplement, "dividend");
		divisor = handle_div_logic(deep_copy_current, other, dividend, divisor, performOnesComplement, "divisor");
		
		LargeInteger quotient = performUnsignedDivisionAlgorithm(dividend, divisor);
		
		if(performOnesComplement)
		{
			quotient = quotient.negate();
		}
		return quotient;
		
	 }


	 //Perform Jarrett's Division Algorithm for Unsigned Division!
	 public LargeInteger performUnsignedDivisionAlgorithm(LargeInteger dividend, LargeInteger divisor)
	 {
		//Generate Shallow Copies of the Dividend and Divisor, merely to generate the appropriate lengths for the Quotient and Remainder objects!
		byte[] shallow_copy_remainder = new byte[Math.max(dividend.length(), divisor.length())];
		byte[] shallow_copy_quotient =new byte[Math.max(dividend.length(), divisor.length())];
		LargeInteger remainder = new LargeInteger(copyByteArray(shallow_copy_remainder));
		LargeInteger quotient = new LargeInteger(copyByteArray(shallow_copy_quotient));

		quotient = quotient.generate_shallow_copy(bitsInByteArray(dividend.getVal()) / BYTE_SIZE); 
		remainder = handle_div_u_logic(dividend, divisor, remainder, quotient, "remainder");
		quotient = handle_div_u_logic(dividend, divisor, remainder, quotient, "quotient");

		return quotient;		
	 }
	 
     public boolean isZero()
     {
         for(int i = 0; i < val.length; i++)
         {
             if(val[i] != (byte)0)
             {
                 return false;
             }
         }
         return true;
     }
     public boolean isLSBZero()
     {
         return (this.val[this.length() - 1] & 0x1) == 0;	//Determine whether the LSB is zero, if it is not, continue the multiplication algorithm.
     }
	//Creates a deep copy of the original byte array, padded with an extra byte...
    public byte[] padBytes(byte[] original, int padBytes)
	{
		byte[] paddedArray = new byte[ original.length + padBytes];
		for(int bit = 0; bit < original.length; bit++)
		{
			paddedArray[bit + padBytes] = original[bit];
		}
		return paddedArray;	
	}
	//Creates a deep copy of the original byte array 
	public byte[] copyByteArray(byte[] original)
	{
		return Arrays.copyOf(original, original.length);
	}
/* 	//Perform a N-bit left shift
    public LargeInteger leftShift(int shamt)
    {
        byte[] deep_current_copy = copyByteArray(this.getVal()); //Deep copy of the byte array :)
		//This algorithm to shift left is simply a modified ripple carry....
		for(int currentBit = 0; currentBit < shamt; currentBit++)
        {	 
			boolean cOut = false;	//Is there CarryOut?
			boolean cIn = ((byte)deep_current_copy[0] & 0x80) != 0;  //Retrieve the MSB, 
			if(cIn)    //If there is carry in, extend the number of bits!
			{
				deep_current_copy = padOneByte(deep_current_copy);   //array is now bit extended!
			} 
			handleLeftShiftRippleCarry(cIn, cOut, deep_current_copy);
        } 
        return new LargeInteger(deep_current_copy);
    } */
	//Perform a N-bit right shift
    public LargeInteger rightShift(int shamt)
    {
		byte[] deep_current_copy = copyByteArray(this.getVal()); //Deep copy of the byte array!
		//Continually shift until we've shifted as much as the designated shift amount!
        for(int currBit = 0; currBit < shamt; currBit++)
        {
            handleRightShiftRippleCarry(deep_current_copy);
            deep_current_copy[0] &= 0x7F; //All 1's except for the MSB, ignore the MSB, and just look at the other bits!
        }
         return new LargeInteger(deep_current_copy);
	}

	public void handleLeftShiftRippleCarry(boolean carryIn, boolean carryOut, byte[] deep_copy)
	{
		for(int currMSB_bit = deep_copy.length - 1; currMSB_bit >= 0; currMSB_bit--) //Now start performing shifts!
		{
			boolean msb = ((byte)deep_copy[currMSB_bit] & 0x80) != 0;  //Retrieve the MSB...
			//Shift left!
			deep_copy[currMSB_bit] <<= 1;
			
			if(carryOut)
			{
				deep_copy[currMSB_bit] |= 1;		//If there's a carry out, set the current MSB to 1...
			}
			//Determine whether there's carryOut...
			if(msb)
			{
				carryOut = true;
			}
			else
			{
				carryOut = false;
			}
			
		}		
	}

	public void handleRightShiftRippleCarry(byte[] deep_copy)
	{
		boolean prev_lsb_carry_out = false; // reset carryOut on each iteration!			
        for(int fullCurrBit = 0; fullCurrBit < deep_copy.length; fullCurrBit++)
        {
			// handleRippleCarry(fullCurrBit, deep_current_copy, cOut);
        	boolean prev_lsb = (deep_copy[fullCurrBit] & 0x1) == 0; //Extract the LSB!
           	//Shift right!
           	deep_copy[fullCurrBit] = (byte)((deep_copy[fullCurrBit] & 0xFF) >> 1); 
            //Determine whether there is carry out from the LSB
            if(prev_lsb_carry_out)
				deep_copy[fullCurrBit] |= 0x80; //Ignoring the MSB, there are only 7 bits to operate at a time!
            if(!prev_lsb)
            {
				prev_lsb_carry_out = true;
            }
            else
            {
				prev_lsb_carry_out = false;
            }
        }
	}
	

	//Gradeschool Algorithm: Courtesy of Jarrett Billingsley's CS 447 class!
	public LargeInteger performGradeSchoolAlgorithm(LargeInteger multiplier, LargeInteger multiplicand, LargeInteger product)
	{
		LargeInteger multiplier_deep_copy = new LargeInteger(multiplier.getVal());
		LargeInteger multiplicand_deep_copy = new LargeInteger(multiplicand.getVal());
		LargeInteger product_deep_copy = new LargeInteger(product.getVal());

		while (!multiplicand_deep_copy.isZero()) 	//While there are still bits left to perform repeated addition on...
		{ 
            if (!multiplicand_deep_copy.isLSBZero()) {	//While the current LSB is odd....
                product_deep_copy = product_deep_copy.add(new LargeInteger(padBytes(multiplier_deep_copy.getVal(), 1))); //Perform repeated addition
            }
            //multiplier_deep_copy = multiplier_deep_copy.shiftLeft("arithmetic");	//Left shift the multiplier by one...
			multiplier_deep_copy = multiplier_deep_copy.shiftLeft("arithmetic");
			multiplicand_deep_copy = multiplicand_deep_copy.rightShift(1);	//Right shift the multiplicand by one...
		}
		return product_deep_copy;
	}

	//Fast multiplication via Karatsuba's!
	public LargeInteger performKaratsuba(LargeInteger multiplier, LargeInteger multiplicand)
	{
		//Cut off for Local Work
		int N = Math.max(bitsInByteArray(multiplier.getVal()), bitsInByteArray(multiplicand.getVal()));

		//Perform Grade School Algorithm up to a certain threshold
		//Start switching to Karatsuba at values exceeding 300 bytes!
		if(N <= 2400)
			return multiplier.multiply(multiplicand);
		//Num bits divided by 2 rounded up!
		N = (N/2) + (N % 2);


		LargeInteger a = new LargeInteger(ZERO);
		LargeInteger b = new LargeInteger(ZERO);
		LargeInteger c = new LargeInteger(ZERO);
		LargeInteger d = new LargeInteger(ZERO);

		// x = a + 2^N*b, y = c + 2^N*d
		while(!(multiplier.isZero()))
		{
			b = multiplier.rightShift(1);
			while(!(b.isZero()))
			{
				a = multiplier.subtract(b.shiftLeft("arithmetic"));
			}
			while(!(multiplicand.isZero()))
			{
				d = multiplicand.rightShift(1);
			}
			while(!(d.isZero()))
			{
				c = multiplicand.subtract(d.shiftLeft("arithmetic"));
			}
		}
		

		// Compute the Delegated work!
		LargeInteger ac = performKaratsuba(a, c);
		LargeInteger bd = performKaratsuba(b, d);
		LargeInteger abcd = performKaratsuba(a.add(b), c.add(d));
		LargeInteger left_shifted_bd_reg =  new LargeInteger(ZERO);
		LargeInteger left_shifted_bd_twice = new LargeInteger(ZERO);
		while(!(bd.isZero()))
		{
			left_shifted_bd_reg = bd.shiftLeft("arithmetic");
			left_shifted_bd_twice = bd.shiftLeft("arithmetic");
		 	left_shifted_bd_twice = bd.shiftLeft("arithmetic");
		}
		return ac.add(abcd.subtract(ac).subtract(left_shifted_bd_reg).add(left_shifted_bd_twice));
	}

	public LargeInteger handle_mult_u_logic(LargeInteger curr, LargeInteger other, LargeInteger multiplier, LargeInteger multiplicand, boolean performOnesComplement, String sentinelKey)
	{
		LargeInteger ZeroReturnVal = new LargeInteger(ZERO);
		LargeInteger multiplierRetVal = new LargeInteger(ZERO);
		LargeInteger multiplicandRetVal = new LargeInteger(ZERO);
		if(curr.isZero() || other.isZero())
		{
			ZeroReturnVal =  new LargeInteger(ZERO);
		}
        if (curr.isNegative()) {
			multiplier = curr.negate(); //this is neg so negate
			multiplierRetVal = multiplier;
            performOnesComplement = !performOnesComplement;
        }
        else {
			multiplier = new LargeInteger(copyByteArray(curr.getVal())); //this is pos so just copy
			multiplierRetVal = multiplier;
        }

        if (other.isNegative()) {
			multiplicand = other.negate(); //other is neg so negate
			multiplicandRetVal = multiplicand;
            performOnesComplement = !performOnesComplement;
        }
        else {
            multiplicand = new LargeInteger(copyByteArray(other.getVal())); //other is pos so just copy
			multiplicandRetVal = multiplicand;
		}
		switch(sentinelKey)
		{
			case "multiplicand":
				return multiplicandRetVal;
			case "multiplier":
				return multiplierRetVal;
			case "zero":
				return ZeroReturnVal;
			case "one's_complement":
				if(performOnesComplement)
				{
					return new LargeInteger(ONE);	//If we have to invert the product, indicate that we need to!
				}
				else
				{
					return new LargeInteger(ZERO); 	//If we don't need to invert the product, indicate that we don't!
				}				
			default:
				return ZeroReturnVal;
		}
	}
	
	public boolean determine_ones_complement(LargeInteger inversion)
	{
		if(inversion.compareTo(new LargeInteger(ZERO)) == 0)
		{
			return false;
		}
		else if(inversion.compareTo(new LargeInteger(ONE)) == 0)
		{
			return true;
		}
		return false;
	}
	public boolean premature_exit(LargeInteger zero)
	{
		if(zero.compareTo(new LargeInteger(ZERO)) == 0)
		{
			return true;
		}
		return false;
	}

	public boolean equality(LargeInteger result)
	{
		if(result.compareTo(new LargeInteger(ONE)) == 0)
		{
			return true;
		}
		return false;
	}
	public LargeInteger handle_division_logic(LargeInteger curr, LargeInteger other, LargeInteger divisor, LargeInteger dividend, boolean performOnesComplement, String sentinelKey)
	{
		LargeInteger divisorRetVal = new LargeInteger(ZERO);
		LargeInteger dividendRetVal = new LargeInteger(ZERO);
		LargeInteger equalityRetVal = new LargeInteger(ZERO);

		if(this.compareTo(other) == 0)
		{
			equalityRetVal = new LargeInteger(ONE);
		}

		if(curr.isNegative())
		{
			divisor = new LargeInteger(curr.negate().getVal());
			performOnesComplement = !performOnesComplement;
			divisorRetVal = divisor;
		}
		else
		{
			//Deep copy of curr...
			divisor = new LargeInteger(curr.getVal());
			divisorRetVal = divisor;
		}

		if(other.isNegative())
		{
			dividend = new LargeInteger(other.negate().getVal());
			performOnesComplement = !performOnesComplement;
			dividendRetVal = dividend;
		}
		else
		{
			dividend = new LargeInteger(other.getVal());
			dividendRetVal = dividend;
		}

		switch(sentinelKey)
		{
			case "equality":
				return equalityRetVal;
			case "divisor":
				return divisorRetVal;
			case "dividend":
				return dividendRetVal;
			case "one's_complement":
				if(performOnesComplement)
				{
					return new LargeInteger(ONE);	//If we have to invert the product, indicate that we need to!
				}
				else
				{
					return new LargeInteger(ZERO); 	//If we don't need to invert the product, indicate that we don't!
				}				
			default:
				return new LargeInteger(ZERO);
		}
	}

	public LargeInteger handle_div_logic(LargeInteger deep_copy_current, LargeInteger other, LargeInteger dividend, LargeInteger divisor, boolean performOnesComplement, String sentinelKey)
	{
	   LargeInteger baseCaseRetVal = new LargeInteger(ZERO);
	   LargeInteger equalityCaseRetVal = new LargeInteger(ZERO);
	   LargeInteger dividendReturnVal = new LargeInteger(ZERO);
	   LargeInteger divisorReturnVal = new LargeInteger(ZERO);

	   if (deep_copy_current == null || other == null || other.getVal() == null) {
		   baseCaseRetVal = null;
	   }
	   if (deep_copy_current.compareTo(other) == 0)
		   equalityCaseRetVal = new LargeInteger(ONE);
	   
	   if (deep_copy_current.isNegative()) 
	   {
		   dividendReturnVal = deep_copy_current.negate();
		   if (other.isNegative())
		   {
			   divisorReturnVal = other.negate();
		   } 
		   else 
		   {
			   performOnesComplement = true;
			   divisorReturnVal = other;
		   }
	   } 
	   else 
	   {
		   dividendReturnVal = deep_copy_current;
		   if (other.isNegative()) 
		   {
			   performOnesComplement = true;
			   divisorReturnVal = other.negate();
		   } 
		   else 
		   {
			   divisorReturnVal = other;
		   }
	   }
	   switch(sentinelKey)
		{
			case "base_case": 
				return baseCaseRetVal;
			case "equality":
				return equalityCaseRetVal; 
			case "dividend":
				return dividendReturnVal;
			case "divisor":
				return divisorReturnVal;
			default:
				return null;
		}
	}

	public LargeInteger handle_div_u_logic(LargeInteger dividend, LargeInteger divisor, LargeInteger remainder, LargeInteger quotient, String sentinelKey)
	{
		LargeInteger remainderRetVal = new LargeInteger(ZERO);
		LargeInteger quotientRetVal = new LargeInteger(ZERO);


		for (int i = 0; i < bitsInByteArray(dividend.getVal()); i++) { 
			remainder = remainder.shiftLeft("arithmetic");
			if (dividend.juxtapose_bits(i) == true)
			{
				remainderRetVal = remainder.generateLSB_deepCopy();
			}
			LargeInteger remainderCopy = remainder.truncate();
			if (remainderCopy.compareTo(divisor) >= 0) {
				remainderRetVal = remainder.subtract(divisor);
				quotientRetVal = quotient.generate_deep_copy_at_index(i);
			}
		}
		switch(sentinelKey)
		{
			case "remainder":
				return remainderRetVal;
			case "quotient":
				return quotientRetVal;
			default:
				return null;
		}
	}
	//truncate leading zero's or FFs from the byte array!
	public LargeInteger truncate()
	{
		if((val[0] == (byte) 0xFF) || (val[0] == 0))
        {
            int num_bytes_to_truncate = 0;
            while((num_bytes_to_truncate <  val.length - 1) && val[num_bytes_to_truncate] == val[num_bytes_to_truncate + 1])
            {
                num_bytes_to_truncate++;
            }
            byte[] truncated = new byte[val.length - num_bytes_to_truncate];
            for(int truncated_index = 0; truncated_index < truncated.length; truncated_index++)
            {
                truncated[truncated_index] = val[truncated_index + num_bytes_to_truncate];
            }
            val = truncated;
		}
		return new LargeInteger(val);
	}

	public LargeInteger shiftLeft(String sentinelKey)
	{
		switch(sentinelKey)
		{
			case "arithmetic":
				byte[] deep_current_copy = copyByteArray(this.getVal()); //Deep copy of the byte array :)
					//This algorithm to shift left is simply a modified ripple carry....
					boolean cOut = false;	//Is there CarryOut?
					boolean cIn = ((byte)deep_current_copy[0] & 0x80) != 0;  //Retrieve the MSB, 
					if(cIn)    //If there is carry in, extend the number of bits!
					{
						deep_current_copy = padBytes(deep_current_copy, 1);   //array is now bit extended!
					} 
					handleLeftShiftRippleCarry(cIn, cOut, deep_current_copy);
				return new LargeInteger(deep_current_copy);

			case "logical":
				//No need to worry about padding here!! :)
				boolean carryIn = false;
				byte[] deep_copy = copyByteArray(this.getVal()); //Deep copy of the byte array :)
				deep_copy = handleLeftShiftLogicalRippleCarry(deep_copy, carryIn);
				return new LargeInteger(deep_copy);

			default:
				return new LargeInteger(ZERO);
		}
	}
	public int bitsInByteArray(byte[] b)
	{
		return b.length * BYTE_SIZE;
	}
	public LargeInteger generate_shallow_copy(int new_size)
	{
		return new LargeInteger(new byte[new_size]);
	}

	public LargeInteger generate_deep_copy_at_index(int bit){
		int correspondingByte = bit/BYTE_SIZE;
		int bitLocation = 7 - (bit - (correspondingByte*BYTE_SIZE));
		int bitLocationElemVal = (int) Math.pow(2, bitLocation); 

		byte[] deep_copy = copyByteArray(val);
		deep_copy[correspondingByte] |= bitLocationElemVal; //Find that specific byte of the bit and OR it at the appropriate bit value
		return new LargeInteger(deep_copy);
	}
	
	public boolean juxtapose_bits(int bit){
		//Calculate the appropriate index of the bit and the byte index within the byte array
		int correspondingByte = bit/BYTE_SIZE;
		int bitLocation = 7 - (bit - (correspondingByte*BYTE_SIZE));
		int bitLocationElemVal = (int) Math.pow(2, bitLocation);

		byte comparison_result = (byte) (val[correspondingByte] & bitLocationElemVal); //If result = 0, the bit is 0; otherwise, the bit is 1
		return comparison_result != 0;
	}
	public LargeInteger generateLSB_deepCopy()
	{
		byte[] toInherit = copyByteArray(val);
		toInherit[toInherit.length - 1] |= 0x1;
		return new LargeInteger(toInherit);
	}
	

	/* //compareTo method, returns -1 if less than, 1 if greater than, or 0 if equal to
	public int compareTo(LargeInteger other)
	{
		//Generate deep copies of the byte arrays...
		//No need to truncate!
		byte[] deep_copy_current = copyByteArray(this.getVal());
		byte[] deep_copy_other = copyByteArray(other.getVal());

		if(deep_copy_current.length > deep_copy_other.length)
			return 1;	//if the length of this is greater, obviously should be bigger!
		else if(deep_copy_current.length < deep_copy_other.length)
			return -1; //if the length of this is less, obviously should be bigger!
		else	//Otherwise, compare bit by bit
		{
			for(int currBit = 0; currBit < deep_copy_current.length; currBit++){
				if(((int)deep_copy_current[currBit] & 0xFF) > ((int)deep_copy_other[currBit] & 0xFF)){	//If the current MSB is greater than the other's current MSB, then the current is larger!
					return 1;
				}
				else if(((int)deep_copy_current[currBit] & 0xFF) < ((int)deep_copy_other[currBit] & 0xFF)){	//If the current MSB is less than the other's current MSB, then the current is smaller!
					return -1;
				}
			}
			return 0;
		}
	} */
	public int compareTo(LargeInteger other)
	{
		int larger = Math.max(this.length(), other.length());

		LargeInteger this_deep_copy = new LargeInteger(copyByteArray(this.getVal()));
		LargeInteger other_deep_copy = new LargeInteger(copyByteArray(other.getVal()));

		if(this_deep_copy.getVal().length < larger)
		{
			//this has a smaller length than other!
			this_deep_copy = new LargeInteger(this_deep_copy.padBytes(this_deep_copy.getVal(), larger - this_deep_copy.getVal().length));
		}
		else
		{
			other_deep_copy = new LargeInteger(other_deep_copy.padBytes(other_deep_copy.getVal(),larger - other_deep_copy.getVal().length));
		}
		for(int i = 0; i < this_deep_copy.getVal().length; i++)
		{
			//Handle less than case
			if((int)(this_deep_copy.getVal()[i] & 0xFF) < (int)(other_deep_copy.getVal()[i] & 0xFF))
			{
				return -1;
			}
			//Handle greater than case!
			if((int)(this_deep_copy.getVal()[i] & 0xFF) > (int)(other_deep_copy.getVal()[i] & 0xFF))
			{
				return 1;
			}

		}
		return 0;
	}

	//Simple Ripple Carry, without Extending... -> Know that in division and modulus, we always have sufficient bytes to represent the data!
	public byte[] handleLeftShiftLogicalRippleCarry(byte[] original, boolean carryIn)
	{
		byte[] deep_copy = copyByteArray(original);
		for(int i = deep_copy.length - 1; i >= 0; i--)
		{
			boolean curr_bit_not_zero = (deep_copy[i] < (byte)0);
			deep_copy[i] <<= (byte) 1;
			if(carryIn)
			{
				deep_copy[i] &= (byte)(deep_copy[i] & 0xFE);
				deep_copy[i] += (byte) 1;
				//deep_copy[i] = (byte) ((deep_copy[i] & 0xFF) + 1);
			}
			carryIn = curr_bit_not_zero;
		}
		return deep_copy;
	}	
    // change the least significant bit to be whatever is passed in, 1 if true, 0 if false
	public void setLSB(boolean setLSB)
	{
		val[val.length - 1] = (byte)(val[val.length - 1] & 0xFC + 0x02);
		if(setLSB)
		{
			val[val.length - 1] = (byte) ((val[val.length - 1] & 0xFE + 0x01) + 1);
		}
	}
	public String toString()
    {
        StringBuilder to_byte_representation = new StringBuilder();
        for(int index = 0; index < val.length; ++index)
        {
			if(index != val.length - 1)
			{
				to_byte_representation.append(String.format("%d", val[index])).append("->");
			}
			else
			{
				to_byte_representation.append(String.format("%d", val[index]));
			}
			
        }
        return to_byte_representation.toString();
	}
}

class ArithmeticErrorException extends Exception
{
	public ArithmeticErrorException(String msg)
	{
		super(msg);
	}
}