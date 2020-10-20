import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
public class AptTracker
{
    /***********************************************************************************
     *                      Project 3: Advanced Priority Queues                        *
     * <~--------------------------------------------------------------------------~>  *
     *                                  Goal:                                          *
     * To explore an advanced application of Priority Queues to gain a deeper          *
     * understanding of PQs..                                                          *
     * =============================================================================== *
     *                               Background:                                       *
     * You will be writing a basic application to help a user select an apartment to   *
     * rent.                                                                           *
     * =============================================================================== *
     * You will write a menu-based user interface driver program (No GUIs).            *
     * - Most of the logic will be in implementing the PQ-based data structure         *
     * =============================================================================== *
     * You should write a PQ-based data structure that store objects according to      *
     * the relative priorities of two of their attributes                              *
     * - Makes it efficient to retrieve objects with the minimum or maximum value of   *
     * either attribute                                                                *              
     * 1) Min attribute -> PRICE                                                       *
     * 2) Max attribute -> SIZE                                                        *
     * =============================================================================== *
     * Data structure should be indexable to allow for efficient updates of entered    *
     * items.                                                                          *
     * =============================================================================== *
     * You will want users to be able to enter details about apartments that they are  *
     * considering renting...                                                          *
     * =============================================================================== *
     * The user should then be able to efficiently retrieve the apartment with the     *
     * highest square footage OR the lowest rent...                                    *
     * =============================================================================== *
     * Assume that the user is looking for apartments in multiple different cities.    *
     * - Retrievals should be possible on the set ofr all entered apartments or on the *
     * subset of all apartments within a specific city...                              *
     * "Lowest Rent in Pittsburgh", "Highest Square Footage in San Francisco"          *
     * =============================================================================== *
     **********************************************************************************/

    static Scanner UI_Input = new Scanner(System.in);
    static ApartmentPQ aptPQ = new ApartmentPQ();
    static String toFile;
    public static void main(String[] args) throws Exception
    {
        /***************************************************************************
         * Main Method: Create a Terminal Menu-Based Driver Program                *
         * ======================================================================= *
         * Must present the User with the following options:                       *
         * 1) Add an Apartment                                                     *
         * - Prompts User for each of the following:                               *
         * ~> Street Address (E.g. 4200 Forbes Ave.)                               *
         * ~> Apartment Number (E.g. 3601)                                         *
         * ~> City the Apartment is in (E.g. Pittsburgh)                           *
         * ~> Apartment's Zip Code (E.g. 15213)                                    *
         * ~> Monthly Cost to Rent (in US Dollars)                                 *
         * ~> Square Footage of the Apartment                                      *
         * ======================================================================= *                                                
         * 2) Update an Apartment                                                  *
         * - Prompts user for...                                                   *
         * ~> Street Address                                                       *
         * ~> Apartment Number                                                     *
         * ~> ZipCode                                                              *
         * ~> Additionally prompt user: Ask if they want to update the rent        *
         * ======================================================================= *
         * 3) Remove a Specific Apartment from Consideration                       *
         * - Prompt the User for...                                                *
         * ~> Street Address                                                       *
         * ~> Apartment Number                                                     *
         * ~> ZipCode                                                              *
         * ======================================================================= *
         * 4) Retrieve Lowest Rent Apartment                                       *
         * ======================================================================= *
         * 5) Retrieve Highest Square Footage Apartment                            *
         * ======================================================================= *
         * 6) Retrieve Lowest Rent Apartment by City                              *
         * - Prompt User to...                                                     *
         * ~> Enter City                                                           *
         * ~> Returns apartment with Lowest Rent within that City                  *
         * ======================================================================= *
         * 7) Retrieve Highest Square Footage Apartment by City...                 *
         * - Prompt User to...                                                     *
         * ~> Enter City                                                           *
         * ~> Returns apartment with Biggest Size within that City                 *
         * ======================================================================= *
         ***************************************************************************/


        ///We need to read in the apartments.txt file when the Program is run... 

        try
        {
            /*******************************************************************************************************
             * =================================================================================================== *
             * Error Box I: Determines whether the user has entered the appropriate arguments...                   *  
             * - If the user enters more than 1 argument, the program will display a Message Box that displays the *
             * expected input!                                                                                     *     
             * - The program will then exit...                                                                     * 
             * =================================================================================================== * 
             ******************************************************************************************************/
            /* if(args.length > 1)
            {
                System.out.println("\n<===================ERROR===INVALID==ARGUMENTS==PROVIDED====================>");
                System.out.println("|| ======================================================================= ||");
                System.out.println("||      Expected Usage is: C:\\> java AptTracker <input filename>           ||");
                System.out.println("||              Re-execute with the correct arguments!                     ||");
                System.out.println("|| ======================================================================= ||");
                System.out.println("<===========================================================================>\n");
                System.exit(0);
            } */
            /*******************************************************************************************************
             * =================================================================================================== *
             * Reading from User-Inputted File: The program will perform the following...                          *
             * 1) Read from the file...                                                                            *
             * 2) Create an Apartment based on the File Contents...                                                *
             * 3) Send the Apartment into the Priority Queue...                                                    * 
             * =================================================================================================== * 
             ******************************************************************************************************/
            //Create a File Handle!
            BufferedReader fhand = new BufferedReader(new FileReader("apartments.txt")); 
            //Create a progress bar!!
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader("apartments.txt"));
            lineNumberReader.skip(Long.MAX_VALUE);
            int lines = lineNumberReader.getLineNumber() + 1;

            System.out.println("\n<~-------------------Loading--in--File:'apartments.txt'--------------------~>");
            System.out.println("|| ======================================================================= ||");

            String fLine;   //Represents the current line in the file!
            int count = 0;
            while(fhand.ready())
            {
                //Print the progress of loading the apartments.txt file in...
                //We already know how long the file is going to be, beforehand...
                
                fLine = fhand.readLine();

                if(fLine.charAt(0) == '#') //If the first character is the Waffle... SKIP IT!
                {
                    // lines = lines - 1;
                    continue;
                }
                //Output the progress of the Lines parsed from the file... (via LineNumberReader...)
                // double truncatedRatio =  Math.floor((((double)++count/lines)*100) * 100) / 100;
                // String temporaryConvert = Double.toString(truncatedRatio);
                //In order to determine appropriate spacing, determine how many digits are in the Whole Number portion of the Number..
                //String[] splitConvert = temporaryConvert.split("\\.");
               
                // if(splitConvert[0].length() == 1)
                // {
                //     System.out.printf("||                   ~~~~~~~~IN-PROGRESS:0%.2f%%~~~~~~~                     ||\n", truncatedRatio);

                // }
                // else if(splitConvert[0].length() == 3)
                // {
                //      System.out.printf("||                   ~~~~~~~~IN-PROGRESS:%.2f%%~~~~~~                     ||\n", truncatedRatio);
                // }
                // else
                // {
                //     System.out.printf("||                   ~~~~~~~~IN-PROGRESS:%.2f%%~~~~~~~                     ||\n", truncatedRatio);

                // }
                String[] contents = fLine.split(":"); //Each respective field is delimited by the ':'
                Apartment apartment = new Apartment(contents[0], contents[1], contents[2], contents[3], contents[4], contents[5]);
                aptPQ.insertApartment(apartment);
            }
            fhand.close();
            lineNumberReader.close();
            System.out.println("|| ======================================================================= ||");
            System.out.println("<~-----------------Completed---Loading---'apartments.txt'------------------~>\n");

            // aptPQ.viewApts();
            
           
        }
        /*******************************************************************************************************
        * ===================================================================================================  *
        * Error Box II: Determines whether the user has entered a valid appropriate file...                    *  
        * - If the user enters more an invalid file , the program will display a Message Box that displays the *
        * expected input!                                                                                      *     
        * - The program will then exit...                                                                      * 
        * ===================================================================================================  * 
        *******************************************************************************************************/
        catch(FileNotFoundException f)
        {
            System.out.println("\n<=====================ERROR===COULD==NOT==LOAD==FILE========================>");
            System.out.println("|| ======================================================================= ||");
            System.out.println("||      There was an unexpected error when loading 'apartments.txt'        ||");
            System.out.println("||                       Re-execute with a valid file!                     ||");
            System.out.println("|| ======================================================================= ||");
            System.out.println("<===========================================================================>\n");
            System.exit(0);
        }
        /*******************************************************************************************************
        * ===================================================================================================  *
        * Error Box III: Determines whether there is an error when processing the file...                      *  
        * - If an error occurs during the processing of the file, the program will display a Message Box that  *
        * indicates there was an error processing the given file!                                              *     
        * - The program will then exit...                                                                      * 
        * ===================================================================================================  * 
        *******************************************************************************************************/
        catch(IOException i)
        {
            System.out.println("\n<======================ERROR===PROCESSING===FILE=============================>");
            System.out.println("|| ======================================================================= ||");
            System.out.println("||      There was an unexpected error when loading 'apartments.txt'        ||");
            System.out.println("||                       Re-execute with a valid file!                     ||");
            System.out.println("|| ======================================================================= ||");
            System.out.println("<===========================================================================>\n");
            System.exit(0);
        }
        /*******************************************************************************************************
        * =================================================================================================== *
        * Error Box IV: Determines whether the user has entered the appropriate arguments...                  *  
        * - If the user enters no arguments, the program will display a Message Box that displays the         *
        * expected input!                                                                                     *     
        * - The program will then exit...                                                                     * 
        * =================================================================================================== * 
        ******************************************************************************************************/
        /* catch(ArrayIndexOutOfBoundsException a)
        {
            System.out.println("\n<=================ERROR===INSUFFICIENT==ARGUMENTS==PROVIDED==================>");
            System.out.println("|| ======================================================================= ||");
            System.out.println("||      Expected Usage is: C:\\> java AptTracker <input filename>           ||");
            System.out.println("||              Re-execute with the correct arguments!                     ||");
            System.out.println("|| ======================================================================= ||");
            System.out.println("<===========================================================================>\n");
            System.exit(0);
        } */

        /********************************************************************************************************
         * ==================================================================================================== *
         * Print Menu Options: If all goes well with Processing & Loading the File, display the menu of options *
         *                     for the to pick from!                                                            *
         * ==================================================================================================== *
         ******************************************************************************************************/ 
        aptSim();
    
       
    }
    public static void displayOptions()
    {
        System.out.println("<~---------------Welcome----to--the---Apartment----Simulator---------------~>");
        System.out.println("|| Please select one of the following options:                             ||");
        System.out.println("|| ======================================================================= ||");
        System.out.println("|| (1): Add an Apartment                                                   ||");
        System.out.println("|| (2): Update a pre-existing Apartment                                    ||");
        System.out.println("|| (3): Remove an Apartment                                                ||");
        System.out.println("|| (4): Display the lowest rent (in US Dollars) Apartment                  ||");
        System.out.println("|| (5): Display the largest size (in sqft) Apartment                       ||");
        System.out.println("|| (6): Display the lowest rent (in US Dollars) Apartment by City          ||");
        System.out.println("|| (7): Display the largest size (in sqft) Apartment by City               ||");
        System.out.println("|| ======================================================================= ||");        
        System.out.println("|| NOTE: Please be wary of erroneous input! Try to enter viable choices!   ||");
        System.out.println("<~-------------------------------------------------------------------------~>");

        System.out.println("\n<~=========================================================================~>");
        System.out.print("||                          ||Enter your option||: ");

    }

    public static void enqueueApartment()
    {
        try
        {
            System.out.println("\n<~--------Enter---the---Contents---of---the---Apartments---to---Add--------~>");
            System.out.println("|| ======================================================================= ||");

            System.out.print("|| Enter an Apartment Address: ");
            String address = UI_Input.nextLine();
            System.out.print("|| Enter the Apartment Number: ");
            String aptNum = UI_Input.nextLine();
            System.out.print("|| Enter the City Name: ");
            String cityName = UI_Input.nextLine();
            System.out.print("|| Enter the Zip Code: ");
            String zipCode = UI_Input.nextLine();
            System.out.print("|| Enter the Monthly Rent for the Apartment: ");
            String price = UI_Input.nextLine();
            System.out.print("|| Enter the Size of the Apartment in Square Feet: ");
            String sqFt = UI_Input.nextLine();
            System.out.println("|| ======================================================================= ||");

            System.out.println("<~-------------------------------------------------------------------------~>");

            Apartment toAdd = new Apartment(address, aptNum, cityName, zipCode, price, sqFt);
            aptPQ.insertApartment(toAdd);
            System.out.println("\n<~-----------------Enqueued---Apartment----to---the---Queue----------------~>");
            System.out.println("|| Its contents were:                                                      ||\n" + toAdd.enqueuedToString());
            
            /* toFile = address + ":" + aptNum + ":" + cityName + ":" + zipCode + ":" + price + ":" + sqFt;

            System.out.println("We want to add the following:\n" + toFile);
            pw.println(toFile); */
        }
        catch(Exception e)
        {

            System.out.println("<~-------------------------------------------------------------------------~>\n");

            System.out.println("\n<~----------------------Error-Inserting-Apartment--------------------------~>");
            System.out.println("||                 Error. All fields must be filled in...                  ||");
            System.out.println("<~-------------------------------------------------------------------------~>\n");
           
            // System.out.println("Unexpected input...\nNow terminating AptTracker.java...");
            // System.exit(0);
        }
    }


    public static void updateApt()
    {
        System.out.println("\n<~------Enter---Search----Criteria---of---the---Apartment---to--Update-----~>");
        System.out.println("|| ======================================================================= ||");

        System.out.print("|| Enter Address of the Apartment to Update: ");
        String address = UI_Input.nextLine();
        System.out.print("|| Enter Apartment Number of the Apartment to Update: ");
        String aptNum = UI_Input.nextLine();
        System.out.print("|| Enter Zip Code of the Apartment to Update: ");
        String zipCode = UI_Input.nextLine();
        System.out.println("|| ======================================================================= ||");
        System.out.println("<~-------------------------------------------------------------------------~>");


        try
        {
            Apartment toUpdate = aptPQ.fetchApartment(address, aptNum, zipCode);
            System.out.println(toUpdate);
            
            String rentDecision = "";
            boolean continuePromptRent = false;
            boolean jumpToPrompt = false;
            while(continuePromptRent == false)
            {
                if(jumpToPrompt == false)
                {
                    System.out.println("\n<~------Enter---Search----Criteria---of---the---Apartment---to--Update-----~>");
                    System.out.println("|| ======================================================================= ||");
                    System.out.print("|| Would you like to update the rent of the Apartment? ");
                    rentDecision = UI_Input.nextLine();
                }

                if(rentDecision.equalsIgnoreCase("Y") || rentDecision.equalsIgnoreCase("YES"))
                {
                    continuePromptRent = true;
                    System.out.print("|| Enter the new rent: ");
                    String rent = UI_Input.nextLine();
                    int mutatedRent = Integer.parseInt(rent);
                    aptPQ.updateApartment(address, aptNum, zipCode, mutatedRent);
                    toUpdate = aptPQ.fetchApartment(address, aptNum, zipCode);
                    System.out.println("<~--------------------------Updating--Apartment----------------------------~>");
                    System.out.println("\n" + toUpdate);
                }

                else if(rentDecision.equalsIgnoreCase("N") || rentDecision.equalsIgnoreCase("NO"))
                {
                    continuePromptRent = true;
                    System.out.println("||                   Ceasing Attempt to Update by Rent....                 ||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
                else
                {
                    while(continuePromptRent == false)
                    {

                        System.out.println("\n<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||                 Would you like to try again? ");
                        rentDecision = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(rentDecision.equalsIgnoreCase("N") || rentDecision.equalsIgnoreCase("NO"))
                        {
                            continuePromptRent = true;
                            System.out.println("||                   Ceasing Attempt to Update by Rent....                 ||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");
                            
                            break;
                        }
                        else if(rentDecision.equalsIgnoreCase("Y") || rentDecision.equalsIgnoreCase("YES"))
                        {
                            System.out.println("\n<~----------------------Rent-------Update-------Prompt---------------------~>");
                            System.out.println("|| ======================================================================= ||");
                            System.out.print("|| Would you like to update the rent of the Apartment? ");
                            rentDecision = UI_Input.nextLine();
                            // System.out.println();
                            continuePromptRent = true;
                            System.out.print("|| Enter the new rent: ");
                            String rent = UI_Input.nextLine();
                            int mutatedRent = Integer.parseInt(rent);
                            aptPQ.updateApartment(address, aptNum, zipCode, mutatedRent);
                            toUpdate = aptPQ.fetchApartment(address, aptNum, zipCode);
                            System.out.println("<~--------------------------Updating--Apartment----------------------------~>");
                            System.out.println("||                  Apartment was succesfully updated!                     ||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");
                            // System.out.println("----------------------Updating-Apartment--------------------------");
                            System.out.println(toUpdate);
                    
                            break;
                        }
                        else
                        {
                            continuePromptRent = false;
                            continue;
                        }
                    }
                   
                }
            } 
        }
        catch(Exception e)
        {
            System.out.println("\n<===================ERROR===APARTMENT===DOES==NOT==EXIST====================>");
            System.out.println("|| ======================================================================= ||");
            System.out.println("||  Enter search contents that corresponds to an apartment in the Queue!!  ||");
            System.out.println("||                       Enter valid search content!                       ||");
            System.out.println("|| ======================================================================= ||");
            System.out.println("<===========================================================================>\n");
        }

    }

    public static void removeApt()
    {
        System.out.println("\n<~-----------------------Enter--Search--Criteria---------------------------~>");
        System.out.print("|| Enter Apartment Address to Delete: ");
        String address = UI_Input.nextLine();
        System.out.print("|| Enter Apartment Number to Delete: ");
        String aptNum = UI_Input.nextLine();
        System.out.print("|| Enter Zip Code to Delete: ");
        String zipCode = UI_Input.nextLine();

        Apartment toDelete = aptPQ.fetchApartment(address, aptNum, zipCode);
        if(toDelete == null)
        {
            System.out.println("<~-------------------------------------------------------------------------~>\n");

            System.out.println("\n<~----------------------Error-Fetching-Apartment---------------------------~>");
            System.out.println("||                   Error. Apartment does not exist...                    ||");
            System.out.println("<~-------------------------------------------------------------------------~>\n");
        }
        else
        {
            System.out.println(toDelete);
            aptPQ.removeApartment(address, aptNum, zipCode);

            System.out.println("<~------------------------Successful---Deletion----------------------------~>");
            System.out.println("||                  Apartment was succesfully deleted!                     ||");
            System.out.println("<~-------------------------------------------------------------------------~>\n");
        }
    }
    public static void displayCheapest_byCity()
    {

        System.out.println("\n<~---------------------Enter---City---to---Search---By---------------------~>");
        System.out.println("||=========================================================================||");
        System.out.print("|| Enter City to Search (by Minimum Rent): ");
        String city = UI_Input.nextLine();
        System.out.println("||=========================================================================||");
        String formattedOutput = String.format("|| The minimum rent apartment in %s contains...", city);
        System.out.println(formattedOutput);
        System.out.println(aptPQ.cheapCity(city));
        System.out.println("||=========================================================================||");
        System.out.println("<~-------------------------------------------------------------------------~>\n");

    }
    public static void displayLargest_byCity()
    {
        System.out.println("\n<~---------------------Enter---City---to---Search---By---------------------~>");
        System.out.println("||=========================================================================||");
        System.out.print("|| Enter City to Search (by Maximum Square Footage): ");
        String city = UI_Input.nextLine();
        System.out.println("||=========================================================================||");
        String formattedOutput = String.format("|| The largest square footage apartment in %s contains...", city);
        System.out.println(formattedOutput);
        System.out.println(aptPQ.sizeCity(city));
        System.out.println("||=========================================================================||");
        System.out.println("<~-------------------------------------------------------------------------~>\n");
        
    }

    public static void displayCheapest()
    {
        System.out.println("\n<~---------------Displaying-----Lowest-----Rent-----Apartment--------------~>");
        System.out.println("||=========================================================================||");
        System.out.println("||The minimum rent apartment's contents are...                             ||");
        System.out.println(aptPQ.minPrice());
        System.out.println("||=========================================================================||");
        System.out.println("<~-------------------------------------------------------------------------~>\n");

    }
    public static void displayLargest()
    {

        System.out.println("\n<~--------------Displaying-----Largest-----Sqft-----Apartment-------------~>");
        System.out.println("||=========================================================================||");
        System.out.println("||The largest square footage apartment's contents are...                   ||");
        System.out.println(aptPQ.maxSize());
        System.out.println("||=========================================================================||");
        System.out.println("<~-------------------------------------------------------------------------~>\n");
       

    }
    public static void aptSim()
    {
        
        int menuChoice; //What the user decides on...
        String restart = ""; //Determines whether or not the User wants to Choose another option..
        displayOptions();
        
        try
        {
            menuChoice = Integer.parseInt(UI_Input.nextLine());    //if the user does not enter valid input....
        
            // System.out.println("||                                                                         ||");
            System.out.println("||=========================================================================||");

            System.out.println("<~=========================================================================~>");
        }   
        catch(Exception e)
        {
            menuChoice = -1; //give'm the default value to tell them they messed up!
        }
        boolean ceasePrompt = false;
        boolean jumpToYes = false;
        //While the user still wants to do some operation with the apartments...
        while(ceasePrompt  == false)
        {
            switch(menuChoice)
            {
                case 1:
                //call to add the new apartment to the PQ...
                if(jumpToYes == false)
                {
                    enqueueApartment();
                    System.out.println("<~---------------Prompting----User----For---Additional----Input------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||             Would you like to enter another option (Enter Y/N)? ");
                    restart = UI_Input.nextLine().strip();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;

                    System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                        Terminating AptTracker.java...                   ||");
                    System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                    System.out.println("||                               Goodbye!                                  ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");     

                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(UI_Input.nextLine());;
                        System.out.println("|| ======================================================================= ||");
                        System.out.println("<~-------------------------------------------------------------------------~>");

                    }
                    catch(Exception e)
                    {
                        menuChoice = -1;
                    }
                }
                else
                {
                    while(ceasePrompt == false)
                    {

                        /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                          Terminating AptTracker.java...                 ||");
                        System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                        System.out.println("||                                  Goodbye!                               ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                        System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||                 Would you like to try again (Enter Y/N)? ");
                        restart = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;

                            System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                            System.out.println("||=========================================================================||");
                            System.out.println("||                        Terminating AptTracker.java...                   ||");
                            System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                            System.out.println("||                               Goodbye!                                  ||");
                            System.out.println("||=========================================================================||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                            
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(UI_Input.nextLine());
                                System.out.println("|| ======================================================================= ||");
                                System.out.println("<~-------------------------------------------------------------------------~>");
        
                            }
                            catch(Exception e)
                            {
                                menuChoice = -1;
                            }              
                            break;              
                        }
                        else
                        {
                            continue;   
                        }
                    }   
                }
                break;

                case 2:
                if(jumpToYes == false)
                {
                    updateApt();
                    System.out.println("<~---------------Prompting----User----For---Additional----Input------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||             Would you like to enter another option (Enter Y/N)? ");
                    restart = UI_Input.nextLine().strip();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;

                    System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                        Terminating AptTracker.java...                   ||");
                    System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                    System.out.println("||                               Goodbye!                                  ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");     

                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(UI_Input.nextLine());
                        System.out.println("|| ======================================================================= ||");
                        System.out.println("<~-------------------------------------------------------------------------~>");

                    }
                    catch(Exception e)
                    {
                        menuChoice = -1;
                    }
                }
                else
                {
                    while(ceasePrompt == false)
                    {

                        /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                          Terminating AptTracker.java...                 ||");
                        System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                        System.out.println("||                                  Goodbye!                               ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                        System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||             Would you like to try again (Enter Y/N)? ");
                        restart = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;

                            System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                            System.out.println("||=========================================================================||");
                            System.out.println("||                        Terminating AptTracker.java...                   ||");
                            System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                            System.out.println("||                               Goodbye!                                  ||");
                            System.out.println("||=========================================================================||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                            
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(UI_Input.nextLine());
                                System.out.println("|| ======================================================================= ||");
                                System.out.println("<~-------------------------------------------------------------------------~>");
        
                            }
                            catch(Exception e)
                            {
                                menuChoice = -1;
                            }              
                            break;              
                        }
                        else
                        {
                            continue;   
                        }
                    }   
                }           
                break;

                case 3:
                if(jumpToYes == false)
                {
                    removeApt();
                    System.out.println("<~---------------Prompting----User----For---Additional----Input------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||             Would you like to enter another option (Enter Y/N)? ");
                    restart = UI_Input.nextLine().strip();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;

                    System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                        Terminating AptTracker.java...                   ||");
                    System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                    System.out.println("||                               Goodbye!                                  ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");     

                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(UI_Input.nextLine());

                        System.out.println("|| ======================================================================= ||");
                        System.out.println("<~-------------------------------------------------------------------------~>");

                    }
                    catch(Exception e)
                    {
                        menuChoice = -1;
                    }
                }
                else
                {
                    while(ceasePrompt == false)
                    {

                        /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                          Terminating AptTracker.java...                 ||");
                        System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                        System.out.println("||                                  Goodbye!                               ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                        System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||             Would you like to try again (Enter Y/N)? ");
                        restart = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;

                            System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                            System.out.println("||=========================================================================||");
                            System.out.println("||                        Terminating AptTracker.java...                   ||");
                            System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                            System.out.println("||                               Goodbye!                                  ||");
                            System.out.println("||=========================================================================||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                            
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(UI_Input.nextLine());
                                System.out.println("|| ======================================================================= ||");
                                System.out.println("<~-------------------------------------------------------------------------~>");
        
                            }
                            catch(Exception e)
                            {
                                menuChoice = -1;
                            }              
                            break;              
                        }
                        else
                        {
                            continue;   
                        }
                    }   
                }              
                break;

                case 4:
                if(jumpToYes == false)
                {
                    displayCheapest();
                    System.out.println("<~---------------Prompting----User----For---Additional----Input------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||             Would you like to enter another option (Enter Y/N)? ");
                    restart = UI_Input.nextLine().strip();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;

                    System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                        Terminating AptTracker.java...                   ||");
                    System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                    System.out.println("||                               Goodbye!                                  ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");     

                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(UI_Input.nextLine());
                        System.out.println("|| ======================================================================= ||");
                        System.out.println("<~-------------------------------------------------------------------------~>");

                    }
                    catch(Exception e)
                    {
                        menuChoice = -1;
                    }
                }
                else
                {
                    while(ceasePrompt == false)
                    {

                        /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                          Terminating AptTracker.java...                 ||");
                        System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                        System.out.println("||                                  Goodbye!                               ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                        System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||                 Would you like to try again (Enter Y/N)? ");
                        restart = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;

                            System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                            System.out.println("||=========================================================================||");
                            System.out.println("||                        Terminating AptTracker.java...                   ||");
                            System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                            System.out.println("||                               Goodbye!                                  ||");
                            System.out.println("||=========================================================================||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                            
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(UI_Input.nextLine());
                                System.out.println("|| ======================================================================= ||");
                                System.out.println("<~-------------------------------------------------------------------------~>");
        
                            }
                            catch(Exception e)
                            {
                                menuChoice = -1;
                            }              
                            break;              
                        }
                        else
                        {
                            continue;   
                        }
                    }   
                }   
                break;

                case 5:
                if(jumpToYes == false)
                {
                    displayLargest();
                    System.out.println("<~---------------Prompting----User----For---Additional----Input------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||             Would you like to enter another option (Enter Y/N)? ");
                    restart = UI_Input.nextLine().strip();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;

                    System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                        Terminating AptTracker.java...                   ||");
                    System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                    System.out.println("||                               Goodbye!                                  ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");     

                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(UI_Input.nextLine());
                        System.out.println("|| ======================================================================= ||");
                        System.out.println("<~-------------------------------------------------------------------------~>");

                    }
                    catch(Exception e)
                    {
                        menuChoice = -1;
                    }
                }
                else
                {
                    while(ceasePrompt == false)
                    {

                        /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                          Terminating AptTracker.java...                 ||");
                        System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                        System.out.println("||                                  Goodbye!                               ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                        System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||                 Would you like to try again (Enter Y/N)? ");
                        restart = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;

                            System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                            System.out.println("||=========================================================================||");
                            System.out.println("||                        Terminating AptTracker.java...                   ||");
                            System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                            System.out.println("||                               Goodbye!                                  ||");
                            System.out.println("||=========================================================================||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                            
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(UI_Input.nextLine());

                                System.out.println("|| ======================================================================= ||");
                                System.out.println("<~-------------------------------------------------------------------------~>");
        
                            }
                            catch(Exception e)
                            {
                                menuChoice = -1;
                            }              
                            break;              
                        }
                        else
                        {
                            continue;   
                        }
                    }   
                }   
                break;

                case 6:
                if(jumpToYes == false)
                {
                    displayCheapest_byCity();
                    System.out.println("<~---------------Prompting----User----For---Additional----Input------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||             Would you like to enter another option (Enter Y/N)? ");
                    restart = UI_Input.nextLine().strip();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;

                    System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                        Terminating AptTracker.java...                   ||");
                    System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                    System.out.println("||                               Goodbye!                                  ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");     

                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(UI_Input.nextLine());

                        System.out.println("|| ======================================================================= ||");
                        System.out.println("<~-------------------------------------------------------------------------~>");

                    }
                    catch(Exception e)
                    {
                        menuChoice = -1;
                    }
                }
                else
                {
                    while(ceasePrompt == false)
                    {

                        /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                          Terminating AptTracker.java...                 ||");
                        System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                        System.out.println("||                                  Goodbye!                               ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                        System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||                 Would you like to try again (Enter Y/N)? ");
                        restart = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;

                            System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                            System.out.println("||=========================================================================||");
                            System.out.println("||                        Terminating AptTracker.java...                   ||");
                            System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                            System.out.println("||                               Goodbye!                                  ||");
                            System.out.println("||=========================================================================||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                            
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(UI_Input.nextLine());
                                System.out.println("|| ======================================================================= ||");
                                System.out.println("<~-------------------------------------------------------------------------~>");
        
                            }
                            catch(Exception e)
                            {
                                menuChoice = -1;
                            }              
                            break;              
                        }
                        else
                        {
                            continue;   
                        }
                    }   
                }   
                break;

                case 7:
                if(jumpToYes == false)
                {
                    displayLargest_byCity();
                    System.out.println("<~---------------Prompting----User----For---Additional----Input------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||                 Would you like to enter another option (Enter Y/N)? ");
                    restart = UI_Input.nextLine().strip();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");

                }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;

                    System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                        Terminating AptTracker.java...                   ||");
                    System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                    System.out.println("||                               Goodbye!                                  ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");     

                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(UI_Input.nextLine());

                        System.out.println("|| ======================================================================= ||");
                        System.out.println("<~-------------------------------------------------------------------------~>");

                    }
                    catch(Exception e)
                    {
                        menuChoice = -1;
                    }
                }
                else
                {
                    while(ceasePrompt == false)
                    {

                        /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                          Terminating AptTracker.java...                 ||");
                        System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                        System.out.println("||                                  Goodbye!                               ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                        System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                            Invalid user input.                          ||");
                        System.out.println("||                  Input is not 'Y' or 'N' or 'YES' or 'NO'.              ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                        System.out.print("||                 Would you like to try again (Enter Y/N)? ");
                        restart = UI_Input.nextLine();
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;

                            System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                            System.out.println("||=========================================================================||");
                            System.out.println("||                        Terminating AptTracker.java...                   ||");
                            System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                            System.out.println("||                               Goodbye!                                  ||");
                            System.out.println("||=========================================================================||");
                            System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                            
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(UI_Input.nextLine());

                                System.out.println("|| ======================================================================= ||");
                                System.out.println("<~-------------------------------------------------------------------------~>");
        
                            }
                            catch(Exception e)
                            {
                                menuChoice = -1;
                            }              
                            break;              
                        }
                        else
                        {
                            continue;   
                        }
                    }   
                }
                break;

                default:
                while(ceasePrompt == false)
                {

                    /* System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                          Terminating AptTracker.java...                 ||");
                    System.out.println("||                  Thank you for using the Apartment Simulator!           ||");
                    System.out.println("||                                  Goodbye!                               ||");
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n"); */

                    System.out.println("<~-------------------Input-------Mismatch--------Warning-------------------~>");
                    System.out.println("||=========================================================================||");
                    System.out.println("||                            Invalid user input.                          ||");
                    System.out.println("||           Input does not match one of the provided options...           ||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!    ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                 ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                   ||");
                    System.out.print("||             Would you like to try again (Enter Y/N)? ");
                    restart = UI_Input.nextLine();
                    System.out.println("||=========================================================================||");
                    System.out.println("<~-------------------------------------------------------------------------~>\n");
                    if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                    {
                        ceasePrompt = true;

                        System.out.println("\n<~----------------Exiting--------Apartment-------Simulator-----------------~>");
                        System.out.println("||=========================================================================||");
                        System.out.println("||                        Terminating AptTracker.java...                   ||");
                        System.out.println("||               Thank you for using the Apartment Simulator!              ||");
                        System.out.println("||                               Goodbye!                                  ||");
                        System.out.println("||=========================================================================||");
                        System.out.println("<~-------------------------------------------------------------------------~>\n");                           
                        
                        System.exit(0);
                    }
                    else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                    {
                        displayOptions();
                        try
                        {
                            menuChoice = Integer.parseInt(UI_Input.nextLine());
                        
                            System.out.println("|| ======================================================================= ||");
                            System.out.println("<~-------------------------------------------------------------------------~>");
    
                        }
                        catch(Exception e)
                        {
                            menuChoice = -1;
                        }              
                        break;              
                    }
                    else
                    {
                        continue;   
                    }
                }   
            
            }
        }

    }

}

