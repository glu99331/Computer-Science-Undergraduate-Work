import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.util.Map.*;

/*****************************************************
 * CS 1501: Algorithm Implementation Project I       *
 *                                                   *
 * Description: AutoComplete Engine                  *
 * - This program utilizes De La Briandais Tries to  *
 * allow for quick predictions!                      *
 *                                                   *
 * - If a warning is outputted, the user can still   *
 * proceed with entering characters.                 *
 *                                                   *
 * - The program will cease producing suggestions    *
 * if the user enters either the '$' or the '!'      *
 * characters.                                       *
 *                                                   *
 * Author: Gordon Lu                                 *
 *                                                   *
 * Term: Fall 2019, Dr. William Garrison             *
 *                                                   *
 * Dependencies: DLB.java                            *
 *                                                   *
 * Execution: java ac_test [filename]                *
 *****************************************************/

public class ac_test
{
    public static void main(String[] args) throws Exception
    {
        //Create DLBs: One for the Dictionary, and one for User History
        DLB dictionaryTrie = new DLB();
        DLB userHistoryTrie = new DLB();

        //Hash Map to Map Strings to a Frequency Count: Higher Frequency words have priority over others!
        //Recursively build up the Hash Map? 
        //If the user adds a word to the DLB that already exists, increment the frequency of that word... 
        TreeMap<String, Integer> userHistoryFrequencies = new TreeMap<String, Integer>();   //Guarantees logarithmic runtime (self-balancing BST in background)
        TreeMap<String, Integer> tempUserHistoryFrequencies = new TreeMap<String, Integer>();
        //To sort by descending frequency... -> LinkedHashMap maintains insertion order
        Map<String, Integer> descendingFrequencyMap = new LinkedHashMap<String, Integer>();
        
        //To filter duplicates...
        HashSet<String> filteredUserHistory = new HashSet<String>();
        //To represent userHistory entries...
        ArrayList<String> userHistory = new ArrayList<String>();
        //To represent merged predictions of userHistory and dictionary...
        ArrayList<String> mergedPredictions = new ArrayList<String>();
        String dictionary_string = "dictionary.txt";
        //Check if user_history file exists:
        String user_history_string = "user_history.txt";
        File user_history = new File(user_history_string);
        PrintWriter pw = null;          
        
        
        /* To generate the list of predictions, your program should not only consult the dictionary trie, 
        but also keep track of what words the user has entered in the past. 
        
        If the user has previously entered the same sequence of characters as a prefix to a word, 
        you should prioritize the words that most frequently resulted from this sequence previously. 
        
        If the user has never entered the current sequence before, or has entered fewer than 5 words with 
        the current sequence as a prefix (i.e., not enough words to complete the list of 5 predictions), 
        your program should suggest words from dictionary.txt that have the current sequence as a prefix. */
        
        //Maximum Predictions generated at a time is 5...
        final int MAX_PREDICTIONS = 5;

        //For timing:
        double startTime = 0.0, finishTime = 0.0, currentTime = 0.0, totalTime = 0.0, averageTime = 0.0;

        //Boolean flag to indicate whether the user has entered '!'
        boolean exitProgram = false;

        //Boolean flag to indicate that the user chose one of the words (1 - 5)
        boolean choseWord = false;

        //Boolean flag to indicate whether we're starting a new word!
        boolean beginNextWord = false;

        //Record size of the predictions array list
        int predictionsSize = 0;

        //Boolean flag to indicate if the user has entered a number, after choosing an invalid option.
        boolean resetPredictions = false;

        //Track the number of iterations, and the number of words that the user has completed!
        int numIterations = 0, numWordsCompleted = 0;
        //Load in dictionary into the DLB
        dictionaryTrie = populateDLB(dictionary_string, dictionaryTrie);
        //If the user_history file already exists:
        //To write to the user_history file...
        //We want the user history to appear first, and then the dictionary entries:
        //So the way in which we insert words matters!
        if(user_history.exists() && !user_history.isDirectory())
        {
            pw = new PrintWriter(new FileOutputStream(new File(user_history_string), true));
            //Now we need to read through user_history:
            //Need to insert into DLB and the Map (to track frequencies):
            BufferedReader user_history_reader = new BufferedReader(new FileReader(user_history_string));
            String line = "";
            while(user_history_reader.ready())
            {
                line = user_history_reader.readLine();
                String[] splitter = line.split(" ");
                //Only insert into the DLB if it is not already in the DLB:
                if(!dictionaryTrie.isEmpty() && !dictionaryTrie.search(line))
                {
                    dictionaryTrie.put(splitter[0]);
                }
                else if(dictionaryTrie.isEmpty())
                {
                    //Account for first word:
                    dictionaryTrie.put(splitter[0]);
                }
                //Add word into map with corresponding frequency!
                userHistoryFrequencies.put(splitter[0], Integer.parseInt(splitter[1]));
            }
            user_history_reader.close();
        }
        else
        {
            pw = new PrintWriter(user_history_string);
        }
        /* BufferedReader br = new BufferedReader(new FileReader("dictionary.txt"));
        //Read words into the DLB
        while(br.ready())
        {
            String line = br.readLine();
            dictionaryTrie.put(line);
        }
        br.close(); //Close the BufferedReader */

        //Create Predictions ArrayList
        ArrayList<String> predictions = new ArrayList<String>();
        //Create a Temporary ArrayList to store the values before deletion...
        ArrayList<String> tempPredictionsList = new ArrayList<String>();
        //Create a Scanner to store the User's Input
        Scanner strScanner = new Scanner(System.in);
        //String Builder to build up the string to produce accurate predictions...
        //E.g.: User enters 't', then 'a', the DLB needs to search for words that begin with "ta", not 'a'.
        StringBuilder predictionPrefix = new StringBuilder();
        //Only prompt the user to enter if the user has not entered '!'
        while(!exitProgram)
        {
            //Prompt the user to enter a single char
            promptUser(numIterations, numWordsCompleted, beginNextWord);
            beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!
            //System.out.print("Enter a character: ");
            //Append the user's input into the String Builder...
            String input = strScanner.nextLine();
            if(input.equals("!") && numIterations > 0)
            {
                exitProgram = true;
                averageTime = totalTime/numIterations;
                //Copy LinkedHashSet into a temporary array list...
                // ArrayList<String> tempHistory = new ArrayList<String>(userHistoryFrequencies.keySet());
                for(Map.Entry<String, Integer> entry : userHistoryFrequencies.entrySet())
                {
                    //Write out each word and its corresponding frequency:
                    pw.write(entry.getKey() + " " + entry.getValue() + "\n");
                }
                // for(int i = 0; i < tempHistory.size(); i++)
                // {
                //         //System.out.println(tempHistory.get(i));
                //         pw.write(tempHistory.get(i) + "\n");
                // }
                System.out.printf("\n\nAverage time:  (%.6f s)\nBye!", averageTime);

                continue;
            }
            else if(input.equals("!") && numIterations == 0)
            {
                System.out.println("\nWarning: No predictions have been generated so far. Cannot display average time as of now.");
                System.out.println("[Message]: Now prompting to begin another word...");

                //Reset choseWord
                choseWord = false;
                //Reset resetPredictions:
                resetPredictions = false;
                //Reset String Builder (Don't, still continue to prompt the user...)
                //predictionPrefix.setLength(0);
                
                //Reset ArrayList
                tempPredictionsList.clear();
                //Reset mergedPredictions
                mergedPredictions.clear();
                //Reset for next word!
                beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!

                continue;
            }
            else if(input.length() > 1)
            {
                System.out.println("\nWarning: Please enter a single character.");

                //Reset choseWord
                choseWord = false;
                //Reset String Builder (Don't append, just skip this iteration of the loop)
                //predictionPrefix.setLength(0);
            
                //Reset ArrayList
                tempPredictionsList.clear();
                //Reset mergedPredictions
                mergedPredictions.clear();
                //Reset for next word!
                beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!
                
                continue;
            }
            if(input.equals("") || input.equals(" ")) //Case 1: If the user just presses ENTER or presses SPACE + ENTER...
            {
                System.out.println("\nWarning: Please enter a single character, empty input will not accepted.");
                
                //Reset choseWord
                choseWord = false;
                //Reset String Builder (Don't, still continue to prompt the user...)
                //predictionPrefix.setLength(0);
                
                //Reset ArrayList
                tempPredictionsList.clear();
                //Reset mergedPredictions
                mergedPredictions.clear();
                //Reset for next word!
                beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!

                continue;
            }
            else if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5"))
            {
                if(numIterations == 0 || resetPredictions)
                {
                    System.out.println("\nWarning: No predictions have been generated so far. A non-numeric ASCII character is expected...");
                    System.out.println("[Message]: Now prompting to begin another word...");

                    //Reset choseWord
                    choseWord = false;
                    //Reset resetPredictions:
                    resetPredictions = false;
                    //Reset String Builder (Don't, still continue to prompt the user...)
                    //predictionPrefix.setLength(0);
                    
                    //Reset ArrayList
                    tempPredictionsList.clear();
                    //Reset mergedPredictions
                    mergedPredictions.clear();
                    //Reset for next word!
                    beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!
 
                    continue;
                }
                else
                {
                    choseWord = true;
                }
                
            }
            else if(input.equals("$"))
            {
                //dictionaryTrie.put(predictionPrefix.toString());

                //Add string to the userHistory DLB
                userHistoryTrie.put(predictionPrefix.toString());
                //Also add to HashMap...
                //If it does not exist in the hashMap... add it!
                if(!userHistoryFrequencies.containsKey(predictionPrefix.toString()))
                {
                    //Start with Base Frequency 1...
                    userHistoryFrequencies.put(predictionPrefix.toString(), 1);
                }
                //Otherwise, increment the frequency...
                else
                {
                    userHistoryFrequencies.put(predictionPrefix.toString(), userHistoryFrequencies.get(predictionPrefix.toString()) + 1);
                }
                System.out.printf("\nSuccessfully added '%s' to User History!\n", predictionPrefix.toString());

                //Reset choseWord
                choseWord = false;
                //Reset String Builder
                predictionPrefix.setLength(0);
                //Reset ArrayList
                tempPredictionsList.clear();
                //Reset mergedPredictions
                mergedPredictions.clear();
                //Increment the number of words completed!
                numWordsCompleted++;
                //Indicate that we need to begin the next word!
                beginNextWord = nextWord(beginNextWord);
            }
            /* else if(!dictionaryTrie.search(predictionPrefix.toString()))
            {
                System.out.println("No predictions were found for the sequence: " + predictionPrefix.toString());
                continue;
            } */
            /* If the current sequence of characters has not been entered by the user before and does not appear in dictionary.txt, 
            you should display a message to the user stating that no predicions were found,and allow the user to continue entering 
            characters one at a time. */
            else
            {
                predictions.clear();
                predictionPrefix.append(input);    

                //BUG FOUND: PREDICTIONS EDGE CASE DETECTED (IF NOT FOUND IN DLB, DEFAULTS TO SEARCHING ENTIRE DLB), AND FIXED.

                //If not in either dictionaryTrie or userHistory:
                //TEST ITERATE TREEMAP:
                /* System.out.println("PRINTING TREEMAP CONTENTS:");
                for(String key : userHistoryFrequencies.keySet())
                {
                    System.out.println(key);
                }
                System.out.println("IT'S IN THE MAP:" + keySet_startsWith(userHistoryFrequencies, predictionPrefix.toString()));
                System.out.println("In the trie?? " + dictionaryTrie.searchPrefix(predictionPrefix.toString()));
                System.out.println("NOT IN BOTH?? " + (!dictionaryTrie.searchPrefix(predictionPrefix.toString()) && !keySet_startsWith(userHistoryFrequencies, predictionPrefix.toString()))); */
                //If not in either dictionaryTrie or userHistory:
                if(!dictionaryTrie.searchPrefix(predictionPrefix.toString()) && !keySet_startsWith(userHistoryFrequencies, predictionPrefix.toString()))
                {
                    int c = (int)input.charAt(0);
                    if((c >= 54 && c <= 57 || c == 48) && numIterations == 0)    //Comparing ASCII values (6 - 9) or 0
                    {
                        System.out.println("\nWarning I: No predictions have been generated so far.");
                        System.out.println("\nWarning II:. Please select one of the options: (Between 1 and 5).");
                        System.out.println("[Message]: Now prompting to begin another word...");

                        //Reset choseWord
                        choseWord = false;
                        //Reset String Builder (Don't, still continue to prompt the user...)
                        predictionPrefix.setLength(0);

                        //Reset ArrayList
                        tempPredictionsList.clear();
                        //Reset mergedPredictions
                        mergedPredictions.clear();
                        //Reset for next word!
                        beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!
                    }
                    else if((c >= 54 && c <= 57 || c == 48) && numIterations != 0)   //Comparing ASCII values (6 - 9) or 0
                    {
                        System.out.println("\nWarning: Please enter one of the options: (Between 1 and 5)."); 
                        System.out.println("[Message]: Now prompting to begin another word...");

                        //Restart building string..
                        //Reset choseWord
                        choseWord = false;
                        //Reset String Builder (Don't, still continue to prompt the user...)
                        predictionPrefix.setLength(0);
                        
                        //Reset ArrayList
                        tempPredictionsList.clear();
                        //Reset mergedPredictions
                        mergedPredictions.clear();
                        //Reset for next word!
                        beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!
                    }
                    else
                    {
                        System.out.printf("\nNo predictions were found for the sequence: %s \n", predictionPrefix.toString());
                        System.out.println("[Suggestion]: Enter '$' to add this word to the dictionary!\n");

                        //Reset for next word!
                        beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!
                        //Don't need to reset in this case, the user might want to add this word!
                    }
                    continue;
                }  
                //Get the startTime 
                startTime = System.nanoTime();
                //Populate the Predictions Array List with the prefix
                //System.out.println(predictionPrefix.toString());
                predictions = dictionaryTrie.generatePredictions(predictionPrefix.toString(), predictions);     
                 //Check the array list:
                // System.out.println("AFTER PREDICTIONS CALL");
                // for(String i : predictions)
                // {
                //     System.out.println(i);
                // }             
                //Get the finishTime
                finishTime = System.nanoTime();
                //Calculate the Current Time to determine the Predictions:
                currentTime = calcTime(startTime, finishTime);
                //Add the Current Time to the Total Time:
                totalTime += currentTime;
                //Print out the Time it took to generate Predictions
                System.out.printf("\n(%.6f s)\n", currentTime);
                System.out.println("Predictions: ");

                // //Record size of the Predictions Array List <----> should this be the mergedPredictions size???
                // predictionsSize = mergedPredictions.size();

                tempPredictionsList.clear();
                //tempUserHistoryFrequencies.clear();
                //descendingFrequencyMap.clear();
                tempPredictionsList.addAll(predictions);

                //Copy User History Frequencies into the Temporary Tree Map
                tempUserHistoryFrequencies.putAll(userHistoryFrequencies);

                //Load in UserHistory...
                //Filter HashMap...
                //System.out.println("Original User History Contains: " + userHistoryFrequencies);
                //These are the first entries that should appear in predictions...
                //System.out.println("Filtered User History Contains: " + descendingFrequencyMap);
                //Turns out filtering the actual Tree Map modifies the original data. We want the data to stay, but somehow filtered, like a SubMap, so use a Temporary!
                tempUserHistoryFrequencies.keySet().removeIf(key -> !key.startsWith(predictionPrefix.toString()));
                //Sort by descending order...
                //descendingFrequencyMap = tempUserHistoryFrequencies.entrySet().stream().sorted((Map.Entry.<String, Integer>comparingByValue().reversed())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                //Temporary map to sort elements based on the frequency of each of the words in user history!
                Map<String, Integer> tempHistories = frequencyComparator(tempUserHistoryFrequencies);
                //Have frequency map take on all Key, Value Pairs from this map!
                descendingFrequencyMap = new LinkedHashMap<String, Integer>(tempHistories);
                /* for (Map.Entry<String,Integer> entry : descendingFrequencyMap.entrySet())  
                {
                    System.out.println(entry.getKey()); //Print out keys rather than values...
                } */
                //If the Frequency Map contains the Key, and it's also in the userHistoryTrie...
                mergedPredictions.clear();
                //System.out.println(descendingFrequencyMap.containsKey(predictionPrefix.toString()) && !descendingFrequencyMap.isEmpty() && userHistoryTrie.startsWith(predictionPrefix.toString()));
                
                //if(descendingFrequencyMap.containsKey(predictionPrefix.toString()) && !descendingFrequencyMap.isEmpty())
                boolean containsPrefix = false;
                for (Map.Entry<String,Integer> entry : descendingFrequencyMap.entrySet())  
                {
                    if(entry.getKey().startsWith(predictionPrefix.toString()))
                    {
                        //System.out.print(entry.getKey() + " ");
                        containsPrefix = true;
                    }   
                    else
                    {
                        containsPrefix = false;
                        break;
                    }
                }
                //If the entry is given search key is in user history, we have to add it in the merged predictions
                if(containsPrefix)
                {
                    //Retrieve all the words from the map:
                    userHistory = new ArrayList<String>(descendingFrequencyMap.keySet());
                    filteredUserHistory = new LinkedHashSet<String>(userHistory);
                    //Prevent duplicates from occurring:
                    filteredUserHistory.addAll(predictions);
                    //Build up merged predictions list:
                    mergedPredictions = new ArrayList<String>(filteredUserHistory);
                }
                else
                {
                    //Otherwise, if it's not in user history, we can just fetch from predictions:
                    mergedPredictions = new ArrayList<String>(predictions);
                }
                //Displaying predictions:
                for(int i = 0; i < mergedPredictions.size() && i < MAX_PREDICTIONS; i++)
                {
                    //BUG FOUND: FETCH FROM MERGED PREDICTIONS NOT PREDICTIONS!!
                    if(mergedPredictions.get(i) != null)  
                    {
                        System.out.printf("(%d) %s%-4s", (i + 1), mergedPredictions.get(i),"");
                    }
                }
                System.out.println();
                //Sorted by frequencies in descending order...
                //Print out the Predictions
                /* for(int i = 0; i < predictions.size() && i < MAX_PREDICTIONS; i++)
                {
                    if(predictions.get(i) != null)  
                    {
                        System.out.printf("(%d) %s%-4s", (i + 1), predictions.get(i),"");
                    }
                } */
                numIterations++;
                //Record size of the Predictions Array List <----> should this be the mergedPredictions size???
                predictionsSize = mergedPredictions.size();
                //Always turn off resetPredictions flag unless dictated otherwise:
                resetPredictions = false;
            }
            // System.out.println("CHOSE WORD??? " + choseWord);
            // System.out.println("PREDICTIONS SIZE: " + predictionsSize);
            // System.out.println("Is predictions empty??" + mergedPredictions.isEmpty());
            if(choseWord)
            {
                
                int parsedInput = Integer.parseInt(input);
                //Do any of the elements in the MergedPredictions ArrayList begin with the given prefix?
                //if(parsedInput > predictionsSize || mergedPredictions.isEmpty() || !mergedPredictions.contains(predictionPrefix.toString()))
                // System.out.println("AYYY WHAT'S YOUR SIZE BOY???" + predictionsSize);
                // System.out.println("PRINTING MERGED PREDICTIONS: ");
                // for(String i : mergedPredictions)
                // {
                //     System.out.println(i);
                // }
                if(parsedInput > predictionsSize || mergedPredictions.isEmpty() || !arrayList_startsWith(mergedPredictions, predictionPrefix.toString()))
                {
                    // System.out.println("CONTAINS PREDICTIONS PREFIX??? " + (!(mergedPredictions.contains(predictionPrefix.toString()))));
                    // System.out.println("PRINTING MERGED PREDICTIONS: ");
                    // for(String i : mergedPredictions)
                    // {
                    //     System.out.println(i);
                    // }
                    //Ask if the user enters an invalid choice, if to keep building up string???
                    System.out.println("\nWarning: Not a valid choice among the choices displayed...");
                    System.out.println("[Message]: Now prompting to begin another word...");
                    //Reset choseWord
                    choseWord = false;
                    //Set resetPredictions
                    resetPredictions = true;
                    //Reset String Builder
                    predictionPrefix.setLength(0);
                    //Reset ArrayList
                    tempPredictionsList.clear();
                    //Reset mergedPredictions
                    mergedPredictions.clear();
                    //Reset for next word!
                    // beginNextWord = !nextWord(beginNextWord);    //now that we've prompted, we're not at the beginning of the next word!
                    beginNextWord = nextWord(beginNextWord);
                    continue;
                }
                // else if(!mergedPredictions.contains(predictionPrefix.toString()))
                // {
                //     //mergedPredictions does not contain prefix:
                //     System.out.println("OOPS. " + predictionPrefix.toString());

                // }
                else
                { 
                    switch(parsedInput)
                    {
                        case 1:
                            //System.out.println("HERE");
                            System.out.println("\nWORD COMPLETED: " + mergedPredictions.get(0));
                            //Everytime the user completes a word, add it to the userHistory, and increment the count to the HashMap...
                            userHistoryTrie.put(mergedPredictions.get(0));
                            if(!userHistoryFrequencies.containsKey(mergedPredictions.get(0)))
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(0), 1);
                            }
                            else
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(0), userHistoryFrequencies.get(mergedPredictions.get(0)) + 1);
                            }
                            //System.out.println(userHistoryFrequencies.get(predictionPrefix.toString()));
                            //Reset choseWord
                            choseWord = false;
                            //Reset String Builder
                            predictionPrefix.setLength(0);
                            //Reset ArrayList
                            tempPredictionsList.clear();
                            //Reset mergedPredictions
                            mergedPredictions.clear();
                            //Increment the number of words completed!
                            numWordsCompleted++;
                            //Indicate that we need to begin the next word!
                            // beginNextWord = nextWord(beginNextWord);
                            beginNextWord = nextWord(beginNextWord);
                            
                            break;
                        case 2:
                            System.out.println("\nWORD COMPLETED: " + mergedPredictions.get(1));
                            //Everytime the user completes a word, add it to the userHistory, and increment the count to the HashMap...
                            userHistoryTrie.put(mergedPredictions.get(1));
                            if(!userHistoryFrequencies.containsKey(mergedPredictions.get(1)))
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(1), 1);
                            }
                            else
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(1), userHistoryFrequencies.get(mergedPredictions.get(1)) + 1);
                            }
                            //Reset choseWord
                            choseWord = false;
                            //Reset String Builder
                            predictionPrefix.setLength(0);
                            //Reset ArrayList
                            tempPredictionsList.clear();
                            //Reset mergedPredictions
                            mergedPredictions.clear();
                            //Increment the number of words completed!
                            numWordsCompleted++;
                            //Indicate that we need to begin the next word!
                            beginNextWord = nextWord(beginNextWord);

                            break;
                        case 3:
                            System.out.println("\nWORD COMPLETED: " + mergedPredictions.get(2));
                            //Everytime the user completes a word, add it to the userHistory, and increment the count to the HashMap...
                            userHistoryTrie.put(mergedPredictions.get(2));
                            if(!userHistoryFrequencies.containsKey(mergedPredictions.get(2)))
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(2), 1);
                            }
                            else
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(2), userHistoryFrequencies.get(mergedPredictions.get(2)) + 1);
                            }
                            //Reset choseWord
                            choseWord = false;
                            //Reset String Builder
                            predictionPrefix.setLength(0);
                            //Reset ArrayList
                            tempPredictionsList.clear();
                            //Reset mergedPredictions
                            mergedPredictions.clear();
                            //Increment the number of words completed!
                            numWordsCompleted++;
                            //Indicate that we need to begin the next word!
                            beginNextWord = nextWord(beginNextWord);

                            break;
                        case 4:
                            System.out.println("\nWORD COMPLETED: " + mergedPredictions.get(3));
                            //Everytime the user completes a word, add it to the userHistory, and increment the count to the HashMap...
                            userHistoryTrie.put(mergedPredictions.get(3));
                            if(!userHistoryFrequencies.containsKey(mergedPredictions.get(3)))
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(3), 1);
                            }
                            else
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(3), userHistoryFrequencies.get(mergedPredictions.get(3)) + 1);
                            }
                            //Reset choseWord
                            choseWord = false;
                            //Reset String Builder
                            predictionPrefix.setLength(0);
                            //Reset ArrayList
                            tempPredictionsList.clear();
                            //Reset mergedPredictions
                            mergedPredictions.clear();
                            //Increment the number of words completed!
                            numWordsCompleted++;
                            //Indicate that we need to begin the next word!
                            beginNextWord = nextWord(beginNextWord);
                            
                            break;
                        case 5:
                            System.out.println("\nWORD COMPLETED: " + mergedPredictions.get(4));
                            //Everytime the user completes a word, add it to the userHistory, and increment the count to the HashMap...
                            userHistoryTrie.put(mergedPredictions.get(4));
                            if(!userHistoryFrequencies.containsKey(mergedPredictions.get(4)))
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(4), 1);
                            }
                            else
                            {
                                userHistoryFrequencies.put(mergedPredictions.get(4), userHistoryFrequencies.get(mergedPredictions.get(4)) + 1);
                            }
                            
                            //Reset choseWord
                            choseWord = false;
                            //Reset String Builder
                            predictionPrefix.setLength(0);
                            //Reset ArrayList
                            tempPredictionsList.clear();
                            //Reset mergedPredictions
                            mergedPredictions.clear();
                            //Increment the number of words completed!
                            numWordsCompleted++;
                            //Indicate that we need to begin the next word!
                            beginNextWord = nextWord(beginNextWord);
                             
                            break;
                    }
                }
                
            }
            //Clear the predictions Array List on every iteration... (The predictions from previous iterations are not overwritten...)
            //For aesthetic purposes.
            System.out.println();
        }
       pw.close();
    }
    //Simple conversion from nanoseconds to seconds to determine the time to generate predictions.
    public static double calcTime(double start, double finish)
    {
        return (finish-start)/1000000000.0;
    }

    public static DLB populateDLB(String dictionary_string, DLB dictionaryTrie) throws Exception
    {
         //Well, if we didn't enter any arguments, this program won't work!
         //if (args.length < 1 )
         if(!dictionary_string.equals("dictionary.txt"))
         {
             System.out.println("\nusage: C:\\> java ac_test\n\n"); // i.e. C:\> java ac_test
             System.out.println("Please provide the dictionary.txt file...");
             System.exit(0);
         }
         try
         {
            //BufferedReader file = new BufferedReader(new FileReader(args[0]));
            BufferedReader file = new BufferedReader(new FileReader(dictionary_string));
            while(file.ready())
            {
                 String word = file.readLine();
                 try
                 {
                     dictionaryTrie.put(word);
                 }
                 catch(NullPointerException n)
                 {
                    //Print the stack trace of why it didn't work
                    n.printStackTrace();
                    System.out.println("ERROR: " + n.getClass() + " ROOT CAUSE OF ERROR: " + n.getCause());
                 }
            }
            file.close();
         }
         catch(FileNotFoundException e)
         {
            //Print the stack trace of why it didn't work
            e.printStackTrace();
            //Print out a friendly message explaining it wasn't found!
            System.out.println("Error. File + " + dictionary_string + " was not found!");
         }
         
         //otherwise insert the word into the DLB!
         return dictionaryTrie;
    }

    public static boolean nextWord(boolean currentState)
    {
        return true;
    }

    public static void promptUser(int totalCharCount, int wordCount, boolean beginNextWord)
    {
        if(totalCharCount == 0 && wordCount != 1)
        {
            System.out.print("\nEnter your first character: ");
        }
        else if(beginNextWord == true)
        {
            System.out.print("\nEnter the first character of the next word: ");
        }
        else if(totalCharCount > 0 || wordCount > 1)
        {
            System.out.print("\nEnter the next character: ");
        }
        else
        {
            System.out.print("\nEnter the first character of your word: ");
        }
        
    }

    //Another method to handle frequency by descending order: Comparators!
    private static Map<String, Integer> frequencyComparator(Map<String, Integer> unsortMap)
    {
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
        {
            //Compare reversed -> o2 vs o1 rather than the usual o1 vs o2
            return o2.getValue().compareTo(o1.getValue());
        }
        });

    // Maintaining insertion order with the help of LinkedHashMap
    Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    for (Entry<String, Integer> entry : list)
    {
        sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
    }

    //Look if any keys in the Map begin with a given prefix:
    private static boolean keySet_startsWith(TreeMap<String, Integer> userHistoryFrequencies, String prefix)
    {
        ArrayList<String> keySet = new ArrayList<String>(userHistoryFrequencies.keySet());
        for(String key : keySet)
        {
            if(key.startsWith(prefix))
            {
                return true;
            }
        }
        return false;
    }

    //Determine if any elements in the ArrayList begin with the passed in prefix!
    private static boolean arrayList_startsWith(ArrayList<String> mergedPredictions, String prefix)
    {
        for(String elem : mergedPredictions)
        {
            if(elem.startsWith(prefix))
            {
                return true;
            }
        }
        return false;
    }
}
