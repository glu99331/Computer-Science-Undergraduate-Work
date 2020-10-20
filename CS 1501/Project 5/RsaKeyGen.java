import java.util.*;
import java.io.*;
import java.math.BigInteger;
public class RsaKeyGen
{
    private static final byte[] ONE = {(byte) 1}; //Represents 1 as byte array...
    private static final byte[] ZERO = {(byte) 0}; //Represents 0 as byte array...

    public static void main(String[] args) throws ArithmeticErrorException
    {
      
        //Generate p and q to be random Large Integers... (512 bit keys...)
        Random primeGenerator = new Random();
        LargeInteger p = new LargeInteger(256, primeGenerator);
        LargeInteger q = new LargeInteger(256, primeGenerator);

        //Calculate n as p*q...
        LargeInteger n = p.multiply(q);
        
        //Calculate Euler's Totient as (p-1) * (q-1);
        //Intitialize a new large integer as one...
        //Calculate sub-expressions...
        LargeInteger one = new LargeInteger(ONE);
        LargeInteger eulers_totient_multiplier = p.subtract(one);
        LargeInteger eulers_totient_multiplicand = q.subtract(one);
        //Calculate overall totient...
        LargeInteger eulers_totient = eulers_totient_multiplier.multiply(eulers_totient_multiplicand);

        //Choose an e such that 1 < e < φ(n) and gcd(e, φ(n)) = 1 (e must not share a factor with φ(n))
        //LargeInteger e = new LargeInteger(ZERO);
        //LargeInteger e = new LargeInteger(512, primeGenerator);  //Generate a new value for e....
        LargeInteger e = new LargeInteger(8, primeGenerator);
        LargeInteger[] totientXGCD = eulers_totient.XGCD(e);
        LargeInteger d = totientXGCD[2]; 
        //Keep searching for e while the gcd of the totient and the e isn't zero, e is less than or equal to one AND e is greater than eulers totient 
        //boolean less_than_one = (new LargeInteger(ONE).compareTo(e) == -1);
        boolean greater_than_one = (e.compareTo(new LargeInteger(ONE)) == 1 ); //e < 1
        boolean less_than_totient = (e.compareTo(eulers_totient) == -1); // e < totient(n)
        boolean gcd_is_one = (eulers_totient.XGCD(e)[0].compareTo(new LargeInteger(ONE)) == 0);
        //boolean gcd_not_one = e.XGCD(eulers_totient)[1].compareTo(new LargeInteger(ONE)) == 0;

        //If e fails to abide by the following conditions:
        /* I) 1 < e < φ(n)
          II) GCD(φ(n), e) = 1
          OR
          The second bezout number generated from running XGCD is negative:
          I.e. GCD(φ(n), e) = 1 = φ(n) * (-z) + e * d 
        */
        // Calculate e, run XGCD until the conditions are fulfilled..
        while(!(greater_than_one && gcd_is_one && less_than_totient)) //Essentially, unless e's bit length is greater than or equal to the sum of p and q's bitlength, we should never hit this statement!
        {
            e = new LargeInteger(8, primeGenerator);  //Generate a new value for e....
            totientXGCD = eulers_totient.XGCD(e);       //Recalculate the gcd...
            //d = totientXGCD[2];
            greater_than_one = (e.compareTo(new LargeInteger(ONE)) == 1); //recalculate the boolean values on each iteration
            less_than_totient = (e.compareTo(eulers_totient) == -1); //recalculate the boolean values on each iteration
            gcd_is_one = (eulers_totient.XGCD(e)[0].compareTo(new LargeInteger(ONE)) == 0); //recalculate the boolean values on each iteration
        } 
        while(d.isNegative())
        {
            d = d.add(eulers_totient);
        }

        //GCD(φ(n), e) = 1 = φ(n) * (-z) + e * d -> d is bezout_b..
        //totientXGCD = e.XGCD(eulers_totient);
        //LargeInteger d = totientXGCD[2];
        //LargeInteger e = new LargeInteger(8, primeGenerator);
        //LargeInteger[] totientXGCD = e.XGCD(eulers_totient);
        //After generating e, d, and n, save e and n to pubkey.rsa, and d and n to privkey.rsa
        try
        {
            String pubkey_insert = e + "\n" + n + "\n";
            String privkey_insert = d + "\n" + n + "\n";

            File pubkey = new File("pubkey.rsa");
            File privkey = new File("privkey.rsa");

            /* //If the file doesn't already exist, create it!
            if (!pubkey.exists()) {
                pubkey.createNewFile();
            } 
            if (!privkey.exists()) {
                privkey.createNewFile();
            }  */
            //FileWriter objects to write to 'pubkey.rsa' and 'privkey.rsa'
            FileWriter pubkey_writer = new FileWriter(pubkey.getAbsoluteFile());
            FileWriter privkey_writer = new FileWriter(privkey.getAbsoluteFile());
            
            //Create BufferedWriters for each corresponding FileWriter...
            BufferedWriter pubkey_bw = new BufferedWriter(pubkey_writer);
            BufferedWriter privkey_bw = new BufferedWriter(privkey_writer);

            //Now write to the corresponding files...
            pubkey_bw.write(pubkey_insert);
            privkey_bw.write(privkey_insert);

            //Close the BufferedWriters!
            pubkey_bw.close();
            privkey_bw.close();
        } 
        catch (IOException io) 
        {
            io.printStackTrace();
        } 
    }
}