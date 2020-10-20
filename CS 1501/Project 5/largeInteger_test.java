import java.math.BigInteger;
import java.util.Random;

/**************************************************************************
 * Large Integer Tester:                                                  *
 * Author: Gordon Lu                                                      *
 *                                                                        *
 * Description:                                                           *
 * This program serves as a test to ensure that the operations            *
 * to be implemented yields the same result as the result from            *
 * Big Integer.                                                           *
 *                                                                        * 
 * Utilizes the .nextBytes() method from the Random Class in order        *
 * to fill byte arrays, to be used to initialize random Large Integers    *
 * and Big Integers.                                                      *
 *                                                                        *
 * This program will print out 'true' if on the current iteration of      *
 * the loop, the results match.                                           *
 *                                                                        *
 * It will print out the two numbers when they differ.                    *
 *                                                                        *
 *                                                                        *
 * Usage: java largeInteger_test                                          *
**************************************************************************/
public class largeInteger_test
{
    private static final byte[] ZERO = {(byte) 0}; //Represents 0 as byte array...

    public static void main(String[] args) throws Exception
    {
        Random genRandomBytes = new Random();

        for(int j = 0; j < 500; j++){ //Simulate any operation you want 100 times!
        byte[] randomBytes = new byte[8]; //Mutate the sizes of these to test varying lengths for multiply!
        byte[] randomBytes2 = new byte[8];
        byte[] randomBytes3 = new byte[8];

        genRandomBytes.nextBytes(randomBytes);  //Fills the corresponding byte arrays with random bytes!
        genRandomBytes.nextBytes(randomBytes2);
        genRandomBytes.nextBytes(randomBytes3);
            
        //Mod works with positive numbers!! (As to be expected with unsigned numbers!!)
        randomBytes[0] = (byte)0;    //Convert the random number into a positive number..       
        randomBytes2[0] = (byte)0;   //Changes MSB to Zero -> remove these statements to allow Negative Numbers
        randomBytes3[0] = (byte)0; 

        LargeInteger largeInt_test1 = new LargeInteger(randomBytes);
        LargeInteger largeInt_test2 = new LargeInteger(randomBytes2);
        LargeInteger largeInt_test3 = new LargeInteger(randomBytes3);

        BigInteger bigInt_test1 = new BigInteger(randomBytes);
        BigInteger bigInt_test2 = new BigInteger(randomBytes2);
        BigInteger bigInt_test3 = new BigInteger(randomBytes3);

        LargeInteger multResult_largeInt = largeInt_test1.multiply(largeInt_test2);

        BigInteger multResult_bigInt = bigInt_test1.multiply(bigInt_test2);

        //LargeInteger[] result = largeInt_test1.divide(largeInt_test2);
        //LargeInteger actual = result[0];
       // LargeInteger multResult_largeInt = new LargeInteger(largeInt_test1.getVal());
        //if(Math.max(randomBytes2.length*8, randomBytes.length*8) >  2400)
        //  multResult_largeInt = multResult_largeInt.performKaratsuba(largeInt_test1, largeInt_test2);
        //else if(Math.max(randomBytes2.length*8, randomBytes.length*8) >  10000)
        //  multResult_largeInt = multResult_largeInt.performSchonhageStrassen(largeInt_test1, largeInt_test2);
        
        //Since we don't want to make a toString() yet, just send in the corresponding byte array generated 
        //from the designated operation to create a new Big Integer!
        BigInteger parsed_multResult_largeInt = new BigInteger(multResult_largeInt.getVal());

        if(parsed_multResult_largeInt.equals(multResult_bigInt) == false){
            System.out.println("Uh oh... BigInteger and LargeInteger results differed!\nBig Int's: " + multResult_bigInt + "\nLarge Int's: " + parsed_multResult_largeInt);
            System.out.println(bigInt_test1 + "\n" + bigInt_test2);
         }
 
       /*  LargeInteger[] xgcd = new LargeInteger[3];
        xgcd = largeInt_test1.XGCD(largeInt_test2);

        BigInteger[] transferMechanism = new BigInteger[3];
        int i = 0;
        for(LargeInteger contents : xgcd)
        {
            BigInteger bezout = new BigInteger(contents.getVal());
            transferMechanism[i] = new BigInteger(contents.getVal());
            i++;

            System.out.println(bezout + "\n");
        }  
        System.out.println("gcd is: " + transferMechanism[0] + "\nsum of bezout_a and bezout_b: " + (bigInt_test1.multiply(transferMechanism[1]).add(bigInt_test2.multiply(transferMechanism[2])))); */
    }
    }
}