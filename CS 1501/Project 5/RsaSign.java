import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Vector;
@SuppressWarnings("deprecation") //Byte Parse is deprecated following Java Version 9 :)))
public class RsaSign
{
    private static final byte[] ZERO = {(byte) 0}; //Represents 0 as byte array...
    public static void main(String[] args) throws Exception
    {
        
        /*
        Once you have your RSA keys generated, write a second program named RsaSign to sign files and verify signatures. 
        This program should accept two command-line arguments: 
        1) A flag to specify whether to sign or verify (s or v), 
        2) and the name of the file to sign/verify.
        */
        try 
        {
            /*********************************
             * 
             * 
             * 
             * 
             * If called to sign (e.g., java RsaSign s myfile.txt) your program should:
             * 1. Generate a SHA-256 hash of the contents of the specified file (e.g., myfile.txt).
             * 2. "Decrypt" this hash value using the private key stored in privkey.rsa (i.e., raise the hashvalue to the d power mod n).Note: Your program should exit and display an error if privkey.rsa is not found in thecurrent directory.
             * 3. Write out the signature to a file named as the original, with an extra .sig extension (e.g.,myfile.txt.sig)
             * 
             * 
             ***********************************/
            if(args.length == 2 && args[0].equals("s"))
            {
                //Generate a SHA-256 hash of the contents of the specified file
                Path path = Paths.get(args[1]);
                byte[] data = Files.readAllBytes(path);

                // create class instance to create SHA-256 hash
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                // process the file
                md.update(data);
                // generate a hash of the file
                byte[] digest = md.digest();

                //Read in values rom privkey.rsa
                try
                {
                    BufferedReader fHand = new BufferedReader(new FileReader("privkey.rsa"));
                    Vector<Byte> byteParser = new Vector<Byte>();
                    //Join back individual bytes back as byte array...
                    String fLine = fHand.readLine();
                    String[] d_byte_vals = fLine.split("->");
                    for(String tokenizer : d_byte_vals)
                    {
                        byteParser.add(new Byte(Byte.parseByte(tokenizer)));
                    }
                    LargeInteger d =  new LargeInteger(byteParser.toArray(new Byte[0]));

                    fLine = fHand.readLine();
                    String[] n_byte_vals = fLine.split("->");
                    byteParser.clear();
                    for(String tokenizer : n_byte_vals)
                    {
                        byteParser.add(new Byte(Byte.parseByte(tokenizer)));
                    }
                    
                    LargeInteger n = new LargeInteger(byteParser.toArray(new Byte[0]));
                    LargeInteger hash_value = new LargeInteger(digest);
                    //raise the hashvalue to the d power mod n
                    LargeInteger decrypt = hash_value.modularExp(d, n);
                    fHand.close();
                    try
                    {
                        // /3. Write out the signature to a file named as the original, with an extra .sig extension (e.g.,myfile.txt.sig)
                        File signature = new File(args[1]+".sig");
                        String signature_toInsert = decrypt + "\n";
                        FileWriter signature_writer = new FileWriter(signature.getAbsoluteFile());
                        BufferedWriter signature_bw = new BufferedWriter(signature_writer);
                        signature_bw.write(signature_toInsert);
                        signature_bw.close();
                    }
                    catch(FileNotFoundException | NullPointerException f)
                    {
                        System.out.println("Error. " + args[1] + "was not found...");
                        System.exit(0);
                    }
                }
                catch(FileNotFoundException | NullPointerException f)
                {
                    System.out.println("Error. privkey.rsa was not found in the current directory.");
                    System.exit(0);
                }

            }
            else if(args.length == 2 && args[0].equals("v") && args[1] != null)
            {
                /**********************
                 * 
                 * If called to verify (e.g., java RsaSign v myfile.txt) your program should:
                 * 1. Read the contents of the original file (e.g., myfile.txt).
                 * 2. Generate a SHA-256 hash of the contents of the original file.
                 * 3. Read the signed hash of the original file from the corresponding .sig file (e.g.,myfile.txt.sig).
                 * - Note: Your program should exit and display an error if the .sig file is not found in thecurrent directory.
                 * 4. "Encrypt" this value with the key from pubkey.rsa (i.e., raise it to the e power mod n).
                 * - Your program should exit and display an error if pubkey.rsa is not found in the current directory.
                 * 5. Compare the hash value that was generated from myfile.txt to the one that was recovered from the signature. 
                 * - Print a message to the console indicating whether the signature isvalid (i.e., whether the values are the same).
                 * 
                 */
                //Generate a SHA-256 hash of the contents of the specified file
                Path path = Paths.get(args[1]);
                byte[] data = Files.readAllBytes(path);

                // create class instance to create SHA-256 hash
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                // process the file
                md.update(data);
                // generate a hash of the file
                byte[] digest = md.digest();    
                
                //Read in values rom privkey.rsa
                //1. Read the contents of the original file (e.g., myfile.txt).
                LargeInteger modified_signed_hash = new LargeInteger(digest);

                LargeInteger encrypted = new LargeInteger(ZERO);
                LargeInteger e = new LargeInteger(ZERO);
                LargeInteger n = new LargeInteger(ZERO); 

                try
                {
                    /*********Read in from pubkey.rsa to retrieve e and n...*******/                    
                    //Read in the Signed Hash...
                    BufferedReader signed_hash = new BufferedReader(new FileReader(args[1]+".sig"));
                    Vector<Byte> byteParser = new Vector<Byte>();
                    //Read in hexvalue back as byte array...
                    String fLine = signed_hash.readLine();
                    String[] hash_vals  = fLine.split("->");

                    for(String tokenizer : hash_vals)
                    {
                        byteParser.add(new Byte(Byte.parseByte(tokenizer)));
                    }
                    encrypted = new LargeInteger(byteParser.toArray(new Byte[0]));
                    byteParser.clear();
                    signed_hash.close();
                }
                catch(FileNotFoundException | NullPointerException f)
                {
                    System.out.println("Error. " + args[1]+".sig" + " was not found in the current directory...");
                    System.exit(0);
                }
                try
                {
                    //4. "Encrypt" this value with the key from pubkey.rsa (i.e., raise it to the e power mod n).
                    BufferedReader pubkey_reader = new BufferedReader(new FileReader("pubkey.rsa"));
                    Vector<Byte> byteParser = new Vector<Byte>();
                    String pubLine = pubkey_reader.readLine();
                    String[] e_pubkey_vals = pubLine.split("->");

                    for(String tokenizer : e_pubkey_vals)
                    {
                        byteParser.add(new Byte(Byte.parseByte(tokenizer)));
                    }
                    e = new LargeInteger(byteParser.toArray(new Byte[0]));
                    byteParser.clear();

                    pubLine = pubkey_reader.readLine();
                    String[] n_pubkey_vals = pubLine.split("->");

                    for(String tokenizer : n_pubkey_vals)
                    {
                        byteParser.add(new Byte(Byte.parseByte(tokenizer)));
                    }
                    n = new LargeInteger(byteParser.toArray(new Byte[0]));
                    //raise the hashvalue to the e power mod n
                    pubkey_reader.close();
                }
                catch(FileNotFoundException | NullPointerException ff)
                {
                    System.out.println("Error. 'pubkey.rsa' was not found in the current directory...");
                    System.exit(0);
                }
                 //4. "Encrypt" this value with the key from pubkey.rsa (i.e., raise it to the e power mod n).
                LargeInteger final_encryption_result = encrypted.modularExp(e, n);
                
                //Extra leading zero in decrypted...
                //LargeInteger trimmed_decrypt = new LargeInteger(ZERO);
                
                byte[] trimmed_decrypt = new byte[final_encryption_result.length() - 1];
                //Account for extra padding...
                if((final_encryption_result.getVal()[0] == 0) && (modified_signed_hash.length() == final_encryption_result.length() - 1))
                {
                    trimmed_decrypt = Arrays.copyOfRange(final_encryption_result.getVal(), 1, final_encryption_result.length());
                }
                LargeInteger temp_decrypt = new LargeInteger(trimmed_decrypt);
                LargeInteger temp_encrypt = new LargeInteger(modified_signed_hash.getVal());

                if(temp_decrypt.compareTo(temp_encrypt) == 0)
                {
                    System.out.println("<---------------------------------->");
                    System.out.println("||=========Congratulations========||");
                    System.out.println("||================================||");
                    System.out.println("||     The signature is valid!    ||");
                    System.out.println("||================================||");
                    System.out.println("<---------------------------------->");

                }
                else
                {
                    System.out.println("<---------------------------------->");
                    System.out.println("||=======Invalid===Signature======||");
                    System.out.println("||================================||");
                    System.out.println("||    The signature is invalid!   ||");
                    System.out.println("||================================||");
                    System.out.println("<---------------------------------->");
                }
                
            }
            else
            {
                throw new ArrayIndexOutOfBoundsException();
            }
            
        }
        catch(ArrayIndexOutOfBoundsException a)
        {
            a.printStackTrace();
            System.out.println("<------Invalid Arguments Provided------->");
            System.out.println("Expected Usage is as followed: ");
            System.out.println("C:\\> java RsaSign [s or v] [filename] ");
            
            System.exit(0); 
        }

    }
    static class InvalidSignatureException extends Exception
    {
        public InvalidSignatureException(String msg)
        {
            super(msg);
        }
    }
}

