import java.util.*;        
/******************************************************************
* @author: Gordon Lu                                              *
* @email: GOL6@pitt.edu                                           *
* @PSID: 4191042                                                  *
*                                                                 *
* Table of Contents:                                              *
* 1) Project Overview                                             *
* 2) Description LZW.java                                         *
* 3) Description of BinaryStdIn.java                              *
* 4) Description of BinaryStdOut.java                             *
* 5) Description of LZW_HybridString.java                         *
* 6) Description of HybridTST.java                                *
* 7) Description of HybridQueue.java                              * 
* 8) Implementation of Hybrid LZW Compression                     *
*                                                                 *
******************************************************************/

/*<~---------------------------------@documentation, @section: Project Overview:------------------------------------------~>*/
/*************************************************************************************************************************
  * <p><b>Author:</b> Gordon Lu</p>
  * <p>1) Implement LZW variable-width code words (dynamically increase the size of codewords as dictionary fills up)
  * </p>                                                                                                             
  * 
  * <p>2) Once the dictionary is filled up, the algorithm can perform one of the following options:  
  *  <ul>                   
  * <li> Stop adding patterns </li>                                                                                       
  * <li> Continue compression with only patterns encountered </li>                                                           
  * <li> Reset codebook to find new patterns                 </li>  </ul> </p>                                                                                                                                                                                  
  * <p>The code provided by the Textbook authors (Robert Sedgewick and Kevin Wayne)  
  * <ul><li>Simply continues to use patterns that have already been added to the codebook</li></ul> </p>                                                          
  *
  *
  * <p><b>Purpose/Goal of the Project: </b>                                                                                    
  * 
  * <p>1) Modify the LZW source code provided by Robert Sedgewick and Kevin Wayne, to utilize           
  * variable-width codewords, and optionally reset the codebook under <b><i>CERTAIN CONDITIONS  </b></i>                           
  * <p>2) Once the said changes have been implemented, compare the performance of Hybrid LZW Compression code to the   
  * initial LZW code, and juxtapose the Hybrid LZW Compression code with a widely used Compression application of    
  * choice. </p>                                                                                                          
  *            
*************************************************************************************************************************/                                                                                                         
public class MyLZW
{
        /*<~-------------------------------------------ANSI_COLOR_CODES------------------------------------------------------>   
        *  The purpose of declaring the ANSI Color Codes is to aesthetically print out some usage box when the user does not *
        *  enter the correct arguments.                                                                                      *
        *                                                                                                                    *
        *  Sample Usage: System.out.println(ANSI_RED + "Usage of Arguments is like the following: " + ANSI_RESET + "\n" +);  *
        *                                                                                                                    *
        *  The variables are global to ensure when a function uses any of the ANSI color codes, there won't be a             *
        *  problem with the scope of the program                                                                             *
        *                                                                                                                    *
        <~-------------------------------------------ANSI_COLOR_CODES------------------------------------------------------>*/ 
        static final String ANSI_RESET = "\u001B[0m";
        static final String ANSI_BLACK = "\u001B[30m";
        static final String ANSI_RED = "\u001B[31m";
        static final String ANSI_GREEN = "\u001B[32m";
        static final String ANSI_YELLOW = "\u001B[33m";
        static final String ANSI_BLUE = "\u001B[34m";
        static final String ANSI_PURPLE = "\u001B[35m";
        static final String ANSI_CYAN = "\u001B[36m";
        static final String ANSI_WHITE = "\u001B[37m";
        static final String ANSI_UNDERLINE = "\u001B[4m";


        /*<~-----------------------------------------------Globals----------------------------------------------------------->   
        * Purpose: Each global in this section is necessary for LZW compression to properly function...                      *
        *                                                                                                                    *
        * ASCII_LENGTH: Refers to the ASCII alphabet, each character maps to a value from 0 to 255.                          *
        *                                                                                                                    *
        * MIN_CODEWORD_WIDTH: Refers to the minimum codeword width, as described in the project description                  *                                                                                                                   
        *                                                                                                                    *
        * MAX_CODEWORD_WIDTH: Refers to the maximum codeword width, if the codeword width is currently at this width, and    * 
        * there are still codewords to add to the codebook, the code will be required to perform a reset...                  *                                                                                                 
        *                                                                                                                    *
        * MAX_THRESHOLD: Refers to the threshold in which to reset ratios, codeword width, and codebook width. Will only     *
        * occur during monitor mode. If the ratio between initialRatio and currentRatio exceed this threshold, the program   *
        * will perform a reset, along with a reset of ratios.                                                                *                                                    
        *                                                                                                                    *
        * VW: Refers to the initial codeword width: It will begin at 9, and continually expand if the codebook does not have *
        * sufficient capacity, until it reaches the max of 16.                                                               *
        *                                                                                                                    *
        * L: Refers to the number of codewords, calculated as 2^W, this is necessary to determine the instances in which the *
        * the codebook needs to be resized, in addition to the conditions on which to reset, monitor, and set the maximum    *
        * capacity of the String Array to store the codewords for expansion..                                                *                                                                
        *                                                                                                                    *
        * initialRatio: Refers to the ratio in which the codebook was last filled.                                           *
        *                                                                                                                    *
        * currRatio: Refers to the current ratio of the uncompressed and compressed data                                     *
        *                                                                                                                    *
        * uncompressedDataSize: Refers to the size of the data prior to compression.                                         *                                                                               
        *                                                                                                                    *
        * compressedDataSize: Refers to the size of the data following compression.                                          *                                                                          
        *                                                                                                                    *
        * mode: Indicates the type of mode the User seeks to invoke on files.                                                *                                                                                                                    
        *                                                                                                                    *
        * compressionFlag: Indicates the type of mode the User entered during compression.                                   *
        *                                                                                                                    *                                                                                                                                                                                          
        <~-----------------------------------------------Globals----------------------------------------------------------->*/   
        static final int ASCII_LENGTH = 256;        // number of input chars
        static final int MIN_CODEWORD_WIDTH = 9;    // min codeword width
        static final int MAX_CODEWORD_WIDTH = 16;   // max codeword width
        static final double MAX_THRESHOLD = 1.1;    // threshold to determine reset 

        static int VW = MIN_CODEWORD_WIDTH;         // codeword width initially begins at 9
        static int L = (int)Math.pow(2, VW);        // number of codewords = 2^W
        static double initialRatio;                 // initialRatio determining last time codebook was filled, will be loaded with default value 0.0             
        static double currRatio;                    // currentRatio indicating the ratio of uncompressed to compressed, will be loaded with default value 0.0
        static int uncompressedDataSize;            // indicates uncompressedData size before being sent, will be loaded with default value 0
        static int compressedDataSize;              // indicates compressedData size after being sent, will be loaded with default value 0
        
        static String mode;                         // indicates the mode the user enters
        static char compressionFlag;                // during expansion, will determine what the user entered during compression...
                
        /* 
        <~---------------------------------@documentation, @section: LZW.java Overview:------------------------------------~>
        *  Compilation:  javac LZW.java                                                                                     *
        *  Execution:    java LZW - < input.txt   (compress)                                                                *
        *  Execution:    java LZW + < input.txt   (expand)                                                                  *
        *  Dependencies: BinaryStdIn.java BinaryStdOut.java                                                                 *
        *  - Utilizes BinaryIn.java and BinaryOut.java in order to shift bits and store output in file                      *
        *                                                                                                                   *
        *  Compress or expand binary input from standard input using LZW.                                                   *
        *                                                                                                                   *
        <~---------------------------------@documentation, @section: LZW.java Methods:-------------------------------------~>
        * compress:                                                                                                         *           
        * 1) Compresses using FIXED-WIDTH codewords...                                                                      *                                                                                                                  
        * 2) Takes in the bytes from input as a string ~> input = BinaryStdIn.readString()                                  *                                                                                                                                                                                                     
        * 3) Fills a TST with the codewords ~> Making it the codebook                                                       *                                                           
        * 4) Initializes a variable to indicate the EOF, since R + 1 -> would exceed the ASCII length...                    *                                                                                               
        * 5) Loop through the input, and add codewords to the ST until there are no more characters to add from the input   *                                                                                                               
        * 6) Afterwords, Write r-bit integer to standard output, optimally write if word aligned, otherwise write one at a  *
        * time.                                                                                                             *                                                                                                                  
        *                                                                                                                   *
        * expand:                                                                                                           *
        * 1) Since, number of codewords has already been predetermined following compression, use a String[], rather than   *                                                                                                                  
        * TST. Initialize it to length of 2^W (I.e., L ~> String[] st = new String[L]);                                     *                                                                                                                                                                                                                                    
        * 2) Fill the String[] with all (0 to 255) ASCII characters.                                                        *                                                                                                                   
        * 3) Read the expanded message from BinaryStdIn, and put it into the String[]                                       *                                                                         
        * 4) Write out the codeword to StdOut until there isn't anything left to write...                                   *                                                                               
        *                                                                                                                   *                                                                                                                 
        <~******************************************************************************************************************~>
        /*
    
        <~******************BinaryStdIn.java*******************~>
        *-------------------Basic-Synposis----------------------*
        *  Compilation:  javac BinaryStdIn.java                 *
        *  Execution:    java BinaryStdIn < input > output      *
        *                                                       *        
        *  Supports reading binary data from standard input.    *
        *                                                       *
        *  Sample Execution of BinaryStdIn.java:                *
        *  - java BinaryStdIn < input.jpg > output.jpg          *
        *  Sample diff input.jpg output.jpg                     *
        *                                                       *
        *-------------------------------------------------------*
        *---------------Purpose-of-BinaryStd.java---------------*
        * Provides methods for reading in bits from standard    *
        * input, as one of the following...                     *
        *-------------------------------------------------------*                                                      
        * 1) One bit at a time (as a boolean)                   *
        * 2) 8 bits at a time (as a byte or a char)             *
        * 3) 16 bits at a time (as a short)                     *
        * 4) 32 bits at a time (as an int or a float)           *
        * 5) 64 bits at a time (as a double or a long)          *
        *                                                       *
        *-------------------------------------------------------*
        *                                                       *                                                      
        *----Assumptions regarding in-memory representation:----*    
        * All primitives are assumed to be represented using the*
        * standard Java representation, i.e., big-endian, (the  *
        * significant byte first)                               *
        *-------------------------------------------------------* 
        *                                                       *                                                     
        *---------------Notes-Regarding-Client-Usage------------*
        * When implementing BinaryStdIn, the client should not  *
        * mix up System.in with StdIn, doing so will result in  *
        * unexpected behavior!                                  *
        *                                                       *
        *-------------------------------------------------------*
        *                                                       *
        *--------------------Method-Analysis--------------------*
        * This part serves to analyze each of the methods in    *
        * BinaryStdIn.java.                                     *
        *                                                       *
        *                  <----fillBuffer--->                  *
        * Description: Attempts to fill buffer with 8 bits from *
        * Standard Input...                                     *
        *                  <------close------>                  *
        * Description: Closes the associated input stream       *
        *                  <-----isEmpty----->                  *
        * Description: Determines if standard input is empty!   *
        *                  <----The-Read-Methods-->             *
        * Description: Will take in n bits, and return an n bit *
        * data type, based on data passed in!                   *
        * Example: readString will ready bytes of data, and     *
        * will return a String                                  *
        <~*****************************************************~>

        <~---------------------------------@documentation, @section: BinaryStdOut.java Overview:------------------------------------~>
        *  Compilation:  javac BinaryStdOut.java                                                                                     *
        *  Execution:    java BinaryStdOut                                                                                           *
        *                                                                                                                            *
        *  Write binary data to standard output, either one 1-bit boolean, one 8-bit char, one 32-bit int, one 64-bit double, one    *
        *  32-bit float, or one 64-bit long at a time.                                                                               *
        *                                                                                                                            *
        *  The bytes written are not aligned.                                                                                        *
        *                                                                                                                            *
        <~---------------------------------@documentation, @section: BinaryStdOut.java Description:---------------------------------~>
        * Provides methods for converting primitives to sequences of bits (bitstreams) and writing them to standard output.          *
        *                                                                                                                            *                                                                                                                           
        * Operates under the assumption of Big-Endian (MSB first)                                                                    *                                                       
        *                                                                                                                            *
        * Upon completion of writing bits, user must flush the output stream                                                         *                                                                                                                           
        *                                                                                                                            *
        * The client should avoid intermixing StdOut and BinaryStdOut, as StdOut processes character streams, while Binary StdOut    *                                                                                                                           
        * will process bit/bytestreams                                                                                               *
        *                                                                                                                            *
        <~---------------------------------@documentation, @section: BinaryStdOut.java Methods:-------------------------------------~>
        * write Methods:                                                                                                             *                                                                        
        * 1) Generally take in n bits/bytes and writes it out to standard output.                                                    *                                                                       
        *                                                                                                                            *
        * clearBuffer:                                                                                                               *                                                                                                                                  
        * 1) Writes out any remaining bits in buffer to standard output, padding with 0s (similar to zero extending)                 *                                                                                                                          
        *                                                                                                                            *
        * flush:                                                                                                                     *      
        * 1) Flushes standard output, padding 0s if number of bits written so far is not byte-aligned.                               *
        *                                                                                                                            *
        * close:                                                                                                                     *       
        * 1) Flush and close standard output. Once standard output is closed, access to write bits is prohibited.                    *                                                                                                                                                                                                    
        *                                                                                                                            *
        <~**************************************************************************************************************************~>

        <~---------------------------------@documentation, @section: LZW_HybridString.java Overview:--------------------------------~>
        * Description: A modification of the current String class, no longer operating under the assumption string length is sparse  *                                                                                                                        
        * - I.e., using Java6 Strings instead!                                                                                       *                                    
        *                                                                                                                            *
        * Synopsis:                                                                                                                  *
        * Substring in the current version of Java operates under the assumption that string lengths will remain relatively small,   *
        * however with large strands of strings to be compressed with LZW, a hybrid approach to extracting substrings, as well as    *
        * other functionality must be implemented and utilized to optimize the performance of string operations...                   *                                                                                                       
        *                                                                                                                            *
        *  Results:                                                                                                                  *
        * 1) This class successfully completes operations much faster than the current Java String class...                          *
        * 2) This class will be utilized alongside the LZW compression to drastically improve the runtime of the MyLZW program       *                                                                                                                    
        *                                                                                                                            *                                                                                                                            
        <~**************************************************************************************************************************~>                                                                                                                           

        <~-------------------------------------@documentation, @section: HybridTST.java Overview:-----------------------------------~>
        * Dependencies: StdIn.java, LZW_HybridString, HybridQueue                                                                    *
        *                                                                                                                            *
        * Purpose: In order to produce efficient runtime with String operations performed on strings & chars inserted into the TST,  *
        * the TST class is modified to implement LZW_HybridString (Java 6 String)                                                    *                                                                  
        *                                                                                                                            *
        * Results:                                                                                                                   *        
        * 1) The performance of the longestPrefixOf and put methods are drastically improved!                                         *
        * 2) During compression, such an optimization cuts the time of compression by a lot!                                         *                                                                                   
        *                                                                                                                            *                                                                                                                           
        <~**************************************************************************************************************************~>

        <~-------------------------------------@documentation, @section: HybridQueue.java Overview:--------------------------------~>
        * Description:                                                                                                               *
        * A generic queue, implemented using a linked list.                                                                          *                                                 
        *                                                                                                                            *
        * Purpose: In order to produce efficient runtime with String operations performed on strings & chars inserted into the TST,  *
        * the Queue class, which is implemented in TST is modified to implement LZW_HybridString (Java 6 String)                     *                                                                                                         *
        *                                                                                                                            *
        <~**************************************************************************************************************************~>
    */
    public static void main(String[] args) throws Exception
    {
        /*************************************************************************************************************************** 
        * 3 Compression Modes:                                                                                                     *
        * 1) Do Nothing mode: Do nothing and continue to use the full codebook (implemented by LZW.java)                           *
        *                                                                                                                          *
        * 2) Reset mode: Reset dictionary back to initial state, so new codewords can be added.                                    *
        * - Make sure both compression and expansion are both in sync!                                                             * 
        *                                                                                                                          *         
        * 3) Monitor mode: Initially do nothing, but begin monitoring the compression ratio whenever the codebook is filled.       *
        * - Compression Ratio -> (uncompressed data size) / (compressed data size)                                                 *
        * - If compression ratio > 1.1 -> reset dictionary back to initial state                                                   *
        * - Ratio of Ratios -> [old/new] -> old is ratio last recorded when program last filled the codebook, new is current ratio *
        * - Make sure to coordinate code for BOTH compression and expansion!!!                                                     * 
        ***************************************************************************************************************************/

        /*****************************************************************************************************************
        * Simple try-catch block to ensure user input matches criteria of project description...                         *
        * - If there is any exception, ignore it, and display what the user should enter in as valid input!              *
        *                                                                                                                *
        *****************************************************************************************************************/
        try
        {
             /*********************************************************************************************************************
             * If the user enters - followed by one of the characters "n", "r", or "m", perform the desired mode.
             * 1) "- n" indicates that the user wants to perform compression with Do Nothing mode...
             * 2) "- r" indicates that the user wants to perform compression with Reset mode...
             * 3) "- m" indicates that the user wants to perform compression with Monitor mode...
             *********************************************************************************************************************/
            
             if(args[0].equals("-") && args[1].equals("n") && args.length == 2) //Do Nothing mode
             {
                /******************************************************************************************************************
                * Initialize mode to what the user entered as the second argument. In this case, it is the compression mode,      *
                * which is Do Nothing mode.                                                                                       *
                *                                                                                                                 *
                * Then pass in the mode, so the compress function can perform the correct functionality based on the compression  *
                * mode entered!                                                                                                   *
                *                                                                                                                 *
                * Note: Compressing an already compressed file will degrade the file content.                                     *
                *                                                                                                                 *
                ******************************************************************************************************************/
                mode = args[1];
                compress(mode); 
             }
             else if(args[0].equals("-") && args[1].equals("r") && args.length == 2) //Reset mode
             {
                /******************************************************************************************************************
                * Initialize mode to what the user entered as the second argument. In this case, it is the compression mode,      *
                * which is Reset mode.                                                                                            *  
                *                                                                                                                 *
                * Then pass in the mode, so the compress function can perform the correct functionality based on the compression  *
                * mode entered!                                                                                                   *
                *                                                                                                                 *
                * Note: Compressing an already compressed file will degrade the file content.                                     *
                *                                                                                                                 *
                ******************************************************************************************************************/
                mode = args[1];
                compress(mode);
             }
             else if(args[0].equals("-") && args[1].equals("m") && args.length == 2) //Monitor mode
             {
                /******************************************************************************************************************
                * Initialize mode to what the user entered as the second argument. In this case, it is the compression mode,      *
                * which is Monitor mode.                                                                                          *
                *                                                                                                                 *
                * Then pass in the mode, so the compress function can perform the correct functionality based on the compression  *
                * mode entered!                                                                                                   *
                *                                                                                                                 *
                * Note: Compressing an already compressed file will degrade the file content.                                     *
                *                                                                                                                 *
                ******************************************************************************************************************/
                mode = args[1]; 
                compress(mode);
             }
             else if(args[0].equals("+") && args.length == 1) //Expansion
             {
                /******************************************************************************************************************
                * If the user enters a "+" as the first argument, this indicates that the user wants to expand a Compressed File. *
                *                                                                                                                 *
                * Note: Expansion will not be effective on the original file contents.                                            *
                *                                                                                                                 *
                ******************************************************************************************************************/
                expand();
             }
             else
             {
                /******************************************************************************************************************
                * Otherwise, assume that the user has entered the wrong input, and display two boxes:                             *
                *                                                                                                                 *
                * ErrorBox: Lists the correct way to run the MyLZW program...                                                     *
                *                                                                                                                 *
                * SampleBox: Outputs a sample execution of the MyLZW program...                                                   *
                * - It will also output suggestions of commands that may be helpful to pair along with the MyLZW program.         *
                *                                                                                                                 *
                ******************************************************************************************************************/
                errorBox();
                sampleBox();             
             }
        }
        catch(Exception e)
        {
            /******************************************************************************************************************
            * Otherwise, assume that an Exception has occured in the middle of running one of the compression modes or        * 
            * running expansion on a compressed file...                                                                       *
            *                                                                                                                 *
            * LZW Error Box: Warns the user that an error with LZW has occurred.                                              *
            * ErrorBox: Lists the correct way to run the MyLZW program...                                                     *
            *                                                                                                                 *
            * SampleBox: Outputs a sample execution of the MyLZW program...                                                   *
            * - It will also output suggestions of commands that may be helpful to pair along with the MyLZW program.         *
            *                                                                                                                 *
            ******************************************************************************************************************/
            // errorBox();
            // sampleBox();
            LZWError();
            System.err.println();
            errorBox();
            sampleBox();
        }

    }
    
    // Inherited from LZW.java by Robert Sedgewick and Kevin Wayne
    // Modified & Optimized by Gordon Lu for CS1501: Algorithm Implementation
    public static void compress(String compressionMode) { 
        BinaryStdOut.write(compressionMode, 8); //Write out compressionMode to BinaryStdOut so during expansion, we can determine what the user entered during compression!
        LZW_HybridString input = new LZW_HybridString(BinaryStdIn.readString());    //Read in bytes as a String....
        HybridTST<Integer> st = initializeHybridTSTCodeBook();  //TST to store codewords: It's the codebook!
        //Insert 0-255 chars into the TST...
        int code = ASCII_LENGTH+1;  // R is codeword for EOF

        while (input.length() > 0) {
            LZW_HybridString s = st.longestPrefixOf(input);  // Find max prefix match s.
            uncompressedDataSize += (s.length()) * 8;      //Fetch the uncompressed data size
            BinaryStdOut.write(st.get(s), VW);      // Print s's encoding.
            compressedDataSize += VW;                   //Fetch the compressed data size

            int t = s.length();           
            if (code < L && t < input.length())    // Add s to symbol table, and 2^W is less than or equal to the codeword
            {
                st.put(input.substring(0, t + 1), code++);  //add new codeword to the TST
            }
            else if(VW < MAX_CODEWORD_WIDTH && t < input.length() && code == L)
            {
                VWCodeWords_Resizer();
                st.put(input.substring(0, t + 1), code++);  //add new codeword to the TST
            }
            else if(compressionMode.equals("r") && t < input.length() && code == L)
            {
                //System.err.println(ANSI_RED + "Resetting " + ANSI_RESET + ANSI_BLUE+ "CodeBook" + ANSI_RESET);
               
                VWCodeWords_Reset();
                st = initializeHybridTSTCodeBook();
                code = ASCII_LENGTH  + 1;
                st.put(input.substring(0, t + 1), code++);  //add new codeword to the TST
            }
            else if(compressionMode.equals("m") && t < input.length()) //Check if we need to reset ratios, then handle resetting in the r flag...
            {
                    if(initialRatio == 0 && code == L) {
                            initialRatio = (double)uncompressedDataSize / compressedDataSize;	// Ratio recorded when last filled the codebook
                    }
                    currRatio = (double)uncompressedDataSize / compressedDataSize;
                    
                //System.err.println(ANSI_BLUE + (initialRatio/currRatio) + ANSI_RESET);
                if(initialRatio/currRatio > MAX_THRESHOLD)
                {
                        //System.err.println("Resetting" + ANSI_GREEN + " CodeBook " + ANSI_RESET + "and " + ANSI_RED + "Ratios...");
        
                        VWCodeWords_Reset();
                        st = initializeHybridTSTCodeBook();
                        code = ASCII_LENGTH  + 1;
                        st.put(input.substring(0, t + 1), code++);  //add new codeword to the TST
                        initialRatio = 0;
                }
            }
           
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(ASCII_LENGTH, VW);   //Write r-bit integer to standard output, optimally write if word aligned, otherwise write one at a time.
        BinaryStdOut.close();
    } 

    //Inherited from LZW.java
    static void expand() {
        compressionFlag = BinaryStdIn.readChar(8);       //read in the 8 bits we previous wrote to BinaryStdIn
        
        SeparateChainingHashST<Integer, String> st = initializeHashSTCodeBook();
        int i = ASCII_LENGTH + 1; // next available codeword value

        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(VW);
        if (codeword == ASCII_LENGTH) return;           // expanded message is empty string
        String val = st.get(codeword);
        while (true) {
            uncompressedDataSize += (val.length()) * 8;      //Fetch the uncompressed data size
            compressedDataSize += VW;
    
            if(VW < MAX_CODEWORD_WIDTH && i == L) //Resizing the bits in the codeword, 9 -> 10 bits, i = R + 1....
            {
               VWCodeWords_Resizer();
            }
            else if(VW == MAX_CODEWORD_WIDTH && compressionFlag == 'r' && i == L)
            {
                //System.err.println(ANSI_RED + "Resetting " + ANSI_RESET + ANSI_BLUE+ "CodeBook" + ANSI_RESET);
                VWCodeWords_Reset();
                st = initializeHashSTCodeBook();                
                i = ASCII_LENGTH + 1;       
            }
            else if(VW == MAX_CODEWORD_WIDTH && i == L && compressionFlag == 'm')
            {
                    if(initialRatio == 0) {
                            initialRatio = (double)uncompressedDataSize / compressedDataSize;	// Ratio recorded when last filled the codebook
                    }
                    
                    currRatio = (double)uncompressedDataSize / compressedDataSize;
                    //System.err.println(ANSI_BLUE + (initialRatio/currRatio) + ANSI_RESET);
                    if((initialRatio/currRatio) > MAX_THRESHOLD)
                    {
                        //System.err.println("Resetting" + ANSI_GREEN + " CodeBook " + ANSI_RESET + "and " + ANSI_RED + "Ratios..." + ANSI_RESET);
                        VWCodeWords_Reset();
                        st = initializeHashSTCodeBook();
                        i = ASCII_LENGTH + 1;
                        initialRatio = 0;
                    }
            }
            BinaryStdOut.write(val);        //Write out codeword to standard output
            codeword = BinaryStdIn.readInt(VW);  //Read the written codeword
            if (codeword == ASCII_LENGTH) break;       //If the value is equal to the max value of the ASCII value (256), break...
            //String s = st[codeword];       
            String s = st.get(codeword);
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            
            if (i < L) {              // i corresponds to the next codeword's value???
                st.put(i++, val + s.charAt(0));
                //st[i++] = val + s.charAt(0);
            }
            val = s;
        }
        BinaryStdOut.close();
    }
   
    static HybridTST<Integer> initializeHybridTSTCodeBook()
    {
        HybridTST <Integer> ternaryST = new HybridTST<Integer>();
        for (int i = 0; i < ASCII_LENGTH; i++){
            LZW_HybridString toInsert = new LZW_HybridString("" + (char) i);
            ternaryST.put(toInsert, i);
        }
        return ternaryST;
    }
    static SeparateChainingHashST<Integer, String> initializeHashSTCodeBook()
    {
        int j;
        SeparateChainingHashST<Integer, String> st = new SeparateChainingHashST<Integer, String>(L);
        // initialize symbol table with all 1-character strings
        for (j = 0; j < ASCII_LENGTH; j++){
            st.put(j, "" + (char)j);
        }
        st.put(j++, ""); //(Unused) Lookahead for EOF
        return st;

    }
    static void nullifyCodeWords_StringArray(String[] arrayST, int base)
    {
        VW = MIN_CODEWORD_WIDTH;        
        L = (int)Math.pow(2, VW);
        //initializeArraySTCodeBook(arrayST);
        base = ASCII_LENGTH + 1;
    }
    static void nullifyCodeWords_TST(int codeBase)
    {
        VW = MIN_CODEWORD_WIDTH;
        L = (int)Math.pow(2, VW);
        //initializeTSTCodeBook(st);
        codeBase = ASCII_LENGTH  + 1;
        //i = R + 1;
    }
    static void VWCodeWords_Resizer()
    {
        L = (int)(Math.pow(2,++VW));
    }
    static void VWCodeWords_Reset()
    {
        VW = MIN_CODEWORD_WIDTH;
        L = (int)Math.pow(2, VW);
    }
  
    static void errorBox()
    {
        System.err.println("<~-------------------------------------------------------------------------------------------------------------------------------------------------------~>");
        System.err.println("\tUSAGE: java MyLZW [COMPRESSION (-) / EXPANSION (+)] [MODE] [INPUT REDIRECTION] [INPUT FILE] [OUTPUT REDIRECTION] [OUTPUT FILE]");
        System.err.println();
        System.err.println("\n\tDESCRIPTION: \n\tImplement LZW with variable-width codewords (dynamically increase the size of codewords as dictionary fills up).");
        
        System.err.println();
        System.err.println("\n\tMyLZW:");
        System.err.println("\n\t'MyLZW' is a program that will produce a hybrid of the LZW compression algorithm...");
        
        System.err.println();
        System.err.println("\n\tCOMPRESSION/EXPANSION:");
        System.err.println("\n\t'-' indicates COMPRESSION on the file the user inputs.");
        System.err.println("\n\t'+' indicates EXPANSION on the file the user inputs.");

        System.err.println();
        System.err.println("\n\tMODE:");
        System.err.println("\n\t'n' indicates to use DO NOTHING mode with Compression...");
        System.err.println("\n\t'r' indicates to use RESET mode with Compression...");
        System.err.println("\n\t'r' indicates to use MONITOR mode with Compression...");

        System.err.println();
        System.err.println("\n\tINPUT REDIRECTION:");
        System.err.println("\n\t'<' will process the desired compression mode on the provided file...");

        System.err.println();
        System.err.println("\n\tINPUT FILE:");
        System.err.println("\n\t'filename.type' is the file that the program will compress using hybrid LZW...");

        System.err.println();
        System.err.println("\n\tOUTPUT REDIRECTION:");
        System.err.println("\n\t'>' will process the input file, and send the result into another specified file");

        System.err.println();
        System.err.println("\n\tOUTPUT FILE:");
        System.err.println("\n\t'filename.type' is the file created, and will contained the compressed data from running LZW compression on the input file...");
        System.err.println("\n<~-------------------------------------------------------------------------------------------------------------------------------------------------------~>");

    }
    static void sampleBox()
    {
        System.err.println("\n<~-------------------------------------------------------------------------------------------------------------------------------------------------------~>");

        System.err.print("\tSAMPLE EXECUTION:");
        System.err.println(" The following statement will perform Do Nothing mode Compression on a File named 'foo.txt'\n\t\t\t  and redirect the output of compression to a file called 'foo.lzw'");
        System.err.println("\t\t\t\t\t\tjava MyLZW - n < foo.txt > foo.lzw");
        
        System.err.println();
        System.err.println("\n\tThe following statement will perform expansion on the file, 'foo.lzw' and will expand the compressed file such\n\tthat its size and contents should equal 'foo.txt'. It will then send the expanded file to a new file named 'foo2.txt'");

        System.err.println("\t\t\t\t\t\tjava MyLZW + < foo.lzw > foo2.txt");
        System.err.println("\n\tRunning the command, diff or FC on foo.txt and foo2.txt will ensure the contents are identical!");

       

        System.err.println("\n<~-------------------------------------------------------------------------------------------------------------------------------------------------------~>");
    }

    static void LZWError()
    {
        System.err.println("\n<~-------------------------------------------------------------------------------------------------------------------------------------------------------~>");
        System.err.println("\t\t\t\t\t[A CRITICAL ERROR IN EITHER COMPRESSION OR EXPANSION OCCURRED!]");
        System.err.println("<~-------------------------------------------------------------------------------------------------------------------------------------------------------~>");

    }
}