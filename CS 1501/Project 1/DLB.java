import java.util.ArrayList;
/******************************************************************
* @author Gordon Lu                                              *
* @Email GOL6@pitt.edu                                           *
* @PSID 4191042                                                  
******************************************************************/
public class DLB 
{
    /*****************************************************
     * An implementation of a De La Briandais Trie (DLB) *
     * as described in lecture:                          *
     ****************************************************/   
    
    /**************************************************
     * <~-------------Global Variables:------------~> *
     * =============================================  *
     * rootNode - Needed to provide base point to     *
     * insert and search for elements (need a place)  *
     * to start.                                      *
     *                                                *
     * TERMINATOR_CHAR - Indicates the end of a word  *
     * - Essential to indicate if a word is found or  *
     * when to stop insertion.                        *
     *                                                *
     * prefixBuilder - Builds up string as a given    *
     * path is traversed, compared to val, in order   *
     * to determine whether prefix/word was found in  *
     * DLB.                                           *
     *                                                *
     * MAX_PREDICTIONS - Indicates the maximum        *
     * number of predictions to be displayed when     *
     * the user enters a character.                   *
     * ============================================== *
     *************************************************/

    private Node rootNode; //Root node of DLB
    private static final char TERMINATOR_CHAR = '$'; //Indicates the end of a word
    private StringBuilder prefixBuilder; //To search for a given prefix
    private static final int MAX_PREDICTIONS = 5; //Maximum number of predictions allowed.

     
    /*******************************************
     * Inner node class:
     * <ul>
     * <li>Contains reference to child</li> 
     * <li>Contains reference to sibling</li>
     * <li>Char with each node</li>
     * </ul>
     *******************************************/
    private static class Node
    {
        private char val;                           // character
        private Node childNode, siblingNode;        // child and sibling references
        /**************************************
         * Constructor to simply add a new 
         * node with a particular value.
         * 
         * @param c represents value node will 
         * be initialized with.
         *************************************/
        public Node(char c)
        {
            val = c;
        }
    }

    /***********************************
     * Initializes an empty DLB
     * 
     **********************************/
    public DLB()
    {
        //Set rootNode to null
        rootNode = null;
    }
    
    /*******************************
     * Does the DLB store anything?
     *
     * @return Whether or not the 
     * DLB is empty
     ******************************/
    public boolean isEmpty()
    {
        return rootNode == null ? true : false;
    }

    /**********************************************************
     * There should be 2 primary functions in a Symbol Table: *
     * ====================================================== *
     * 1) put                                                 *
     * 2) contains (i.e., search for a given prefix)          *              
     *********************************************************/


     /**********************************************************
     * Inserts a given word into the DLB via a helper
     * method that will iteratively insert words into the 
     * DLB.
     *
     * @param val represents the string to be inserted
     * @return Whether or not the word was successfully
     * inserted into the DLB
     *********************************************************/
     public boolean put(String val)
     {
        //Placeholder until function is actually complete
        Node currentNode = putPrimaryHelper(val);

        if(currentNode.val == TERMINATOR_CHAR)
        {
            return true;
        }
        return false;
     }

     /**********************************************************
     * Inserts a given word into the DLB using an 
     * iterative scheme to insert words into the 
     * DLB.
     *
     * @param val represents the string to be inserted
     * @return The node that insertion terminates at.
     *********************************************************/
     public Node putPrimaryHelper(String val)
     {
        val += TERMINATOR_CHAR; //Append the terminating character to the string to be inserted to indicate the end of a word.
        //Have a pointer to the rootNode to navigate through the DLB
        Node currNode = rootNode;
        //Loop through all the characters in the String...
        for(int i = 0; i < val.length(); i++)
        {
            //Case I: If the root node is null, just create the first node...
            if(putSecondaryHelper("CHECK_ROOT", currNode, val.charAt(i)))
            {
                //Create rootNode:
                rootNode = new Node(val.charAt(0));
                currNode = rootNode;
            }
            //Case II: If we're not at the terminator, and the current node does not have a child...
            //I. This means we can safely insert a node into the DLB...
            else if(putSecondaryHelper("ADD_CHILD", currNode, val.charAt(i)))
            {
                currNode = addChild(currNode, val.charAt(i));
            }
            //Case III: If the child is not null, traverse until we get to a node whose child is null...
            else if(putSecondaryHelper("TRAVERSE_CHILD", currNode, val.charAt(i)))
            {
                currNode = traverseChild(currNode);
            }
            //Case IV: While the node we're currently looking is not the terminator, and there's no sibling node, then create a new sibling node!
            else if(putSecondaryHelper("ADD_SIBLING", currNode, val.charAt(i)))
            {
                currNode = addSibling(currNode, val.charAt(i));
            }
            //Case V: We need to traverse through the siblings properly, if the sibling node is not null, and tehe value is not equal to what we are currently at...
            else if(putSecondaryHelper("TRAVERSE_SIBLING", currNode, val.charAt(i)))
            {
                //The tricky part about traversing a sibling has to do with 
                //indexing, since if we increment i, we will be next character
                //and risk appending the wrong character. 
                currNode = traverseSibling(currNode, i--); //Readjust 'index' to ensure that you're are on the same level (avoids diving deeper than necessary...)
            }

        }
        //Return wherever currNode ends up at:
        return currNode;
     }

     /*********************************************************** 
     * Searches for a given word into the DLB via a helper
     * method that will recursively search for a prefix in the 
     * DLB.
     * 
     * @param prefix represents prefix to search for in DLB
     * @return Whether or not the DLB contains a given prefix.
     ***********************************************************/
     public boolean searchPrefix(String prefix)
     {
        prefixBuilder = new StringBuilder(); //To compare word searched in DLB to the word passed in
        Node currentNode = searchPrefix(rootNode, prefixBuilder, prefix, 0); //At this point, we're assuming that the DLB contains something in it!
        if(prefixBuilder.toString().equals(prefix)) //compare built up string from traversing DLB to passed in string
        {
            return true;
        }
        return false;
     }

     /**********************************************************************************
     * This method will recursively search for a prefix within the 
     * DLB.
     * 
     * <p> There are several cases to consider for in searching for a prefix:</p>
     * <p><b>Base Case</b>: String passed in matched String built up.</p>
     * <p><b>Recursive Case I</b>: Traversing child node.</p>
     * <p><b>Recursive Case II</b>: Traversing a sibling node.</p>
     * <p><b>Recursive Case III</b>: Traversing a sibling node via Terminator Char</p>
     *
     * @param current represents the current node
     * @param sb represents the current string built up so far
     * @param val represents the string we want to search for
     * @param char_pos represents current position in the string
     * @return The node we end up at as a result of a single search
     **********************************************************************************/
     public Node searchPrefix(Node current, StringBuilder sb, String val, int char_pos)
     {
        //Return wherever currNode ends up at:
        //Base Case: Prefix matches passed in string
        if(sb.toString().equals(val))
        {
            return current;
        }
        else
        {
            //Current character of the string:
            char letter = val.charAt(char_pos);
            //Recursive Case I: Hit! Append character to StringBuilder, and traverse down child
            if(current.val == letter && current.childNode != null)
            {
                sb.append(current.val);
                current = searchPrefix(current.childNode, sb, val, char_pos + 1);
            }
            //Recursive Case II: Mismatch! Traverse siblings!
            else if(current.val != letter && current.siblingNode != null)
            {
                current = searchPrefix(current.siblingNode, sb, val, char_pos);
            }
            //Recursive Case III: Encounter the terminator, and sibling is non-null, continue traversing siblings!
            else if(current.val == TERMINATOR_CHAR && current.siblingNode != null)
            {
                current = searchPrefix(current.siblingNode, sb, val, char_pos);
            }
        }
        //Return wherever currNode ends up at:
        return current;
    }
    
    /*********************************************************** 
     * Searches for a given word into the DLB via a helper
     * method that will recursively search for a full word in 
     * the DLB.
     * 
     * Essentially, we want to look for a terminator following 
     * the word, if not, it's not a word in the DLB.
     * 
     * @param word represents word to search for in DLB
     * @return Whether or not the DLB contains a given word.
     ***********************************************************/
    public boolean search(String word)
    {
        prefixBuilder = new StringBuilder(); //To compare word searched in DLB to the word passed in
        Node currentNode = searchHelper(rootNode, prefixBuilder, word, 0); //At this point, we're assuming that the DLB contains something in it!
        if(prefixBuilder.toString().equals(word) && currentNode.val == TERMINATOR_CHAR && currentNode.siblingNode == null)
        {
            return true;
        }
        return false;
    }
    /**********************************************************************************
     * This method will recursively search for a word within the 
     * DLB.
     * 
     * <p> There are several cases to consider for searching for a word:</p>
     * <p><b>Base Case</b>: Current node has the terminator, and String passed in 
     * matches the String built up, and not a prefix (sibling is null).</p>
     * <p><b>Recursive Case I</b>: Traversing child node.</p>
     * <p><b>Recursive Case II</b>: Traversing a sibling node.</p>
     * <p><b>Recursive Case III</b>: Traversing a sibling node via Terminator Char</p>
     *
     * @param current represents the current node
     * @param sb represents the current string built up so far
     * @param val represents the string we want to search for
     * @param char_pos represents current position in the string
     * @return The node we end up at as a result of a single search
     **********************************************************************************/
    public Node searchHelper(Node current, StringBuilder sb, String val, int char_pos)
    {
        //Base Case: Current node has value of Terminator Character, String passed matches
        //String built up, and not a prefix!
        //Base Case: Prefix matches passed in string
        if(sb.toString().equals(val) && current.val == TERMINATOR_CHAR && current.siblingNode == null)
        {
            return current;
        }
        //Rest of the recursive cases should be identitical to search prefix cases:
        else
        {
            //Current character of the string:
            char letter = val.charAt(char_pos);
            //Recursive Case I: Hit! Append character to StringBuilder, and traverse down child
            if(current.val == letter && current.childNode != null)
            {
                sb.append(current.val);
                current = searchPrefix(current.childNode, sb, val, char_pos + 1);
            }
            //Recursive Case II: Mismatch! Traverse siblings!
            else if(current.val != letter && current.siblingNode != null)
            {
                current = searchPrefix(current.siblingNode, sb, val, char_pos);
            }
            //Recursive Case III: Encounter the terminator, and sibling is non-null, continue traversing siblings!
            else if(current.val == TERMINATOR_CHAR && current.siblingNode != null)
            {
                current = searchPrefix(current.siblingNode, sb, val, char_pos);
            }
        }
        //Return wherever currNode ends up at:
        return current;
    }

    //  /**
    //  * Returns the string in the symbol table that is the longest prefix of <tt>query</tt>,
    //  * or <tt>null</tt>, if no such string.
    //  * @param query the query string
    //  * @throws NullPointerException if <tt>query</tt> is <tt>null</tt>
    //  * @return the string in the symbol table that is the longest prefix of <tt>query</tt>,
    //  *     or <tt>null</tt> if no such string
    //  */
    // public LZW_HybridString longestPrefixOf(String query)
    // {

    // }

    /****************************************************************
     * Recursively navigates a node to the end of a given string.
     * <p>Note, this does not have to be a full word, a prefix is
     * perfectly fine.</p>
     * 
     * <p>There are several cases to consider in navigating a node
     * to the correct location given a certain String.</p>
     * <p><b>Base Case</b>: We reached the end of the word.</p>
     * <p><b>Recursive Case I</b>: Traversing child node.</p>
     * <p><b>Recursive Case II</b>: Traversing sibling node.</p>
     * 
     * @param val represents the word the user wants to generate
     * predictions with.
     * @param char_pos represents the position in the word to 
     * look at.
     * @return The node indicating the end of a given word.
     **************************************************************/
    public Node navigateNode(Node current, String val, int char_pos)
    {
        //Base Case: Reached end of word
        if(char_pos >= val.length())
        {
            return current;
        }
        else
        {
            //Current character of the string:
            char letter = val.charAt(char_pos);
            //Recursive Case I: Hit! Traverse children!
            if(putSecondaryHelper("TRAVERSE_CHILD", current, letter))
            {
                current = navigateNode(current.childNode, val, char_pos + 1);
            }
            //Recursive Case II: Mismatch! Traverse siblings!
            else if(putSecondaryHelper("TRAVERSE_SIBLING", current, letter))
            {
                current = navigateNode(current.siblingNode, val, char_pos);            
            }
        }
        //Return wherever currNode ends up at:
        return current; 
    }


    /**********************************************************************
     * Fills up an ArrayList with a list of all words in the DLB that
     * begin with a given prefix.
     * 
     * <p>Utilizes a helper function that will recursively search for a 
     * prefix in the DLB.</p>
     * 
     * @param prefix represents the String that is built up, as the user
     * enters characters into the auto-completion engine.
     * @param predictionsList represents the ArrayList to be filled up
     * by words in the DLB with the same prefix as the String passed in.
     * @return An ArrayList filled with the words starting with the same
     * prefix as the String that was passed in.
     *********************************************************************/
    public ArrayList<String> generatePredictions(String prefix, ArrayList<String> predictionsList)
    {
        Node currentNode = rootNode; //We're assuming at this point that the DLB contains something in it.
        prefixBuilder = new StringBuilder(); //Necessary to build up strings to add to the ArrayList.
        ArrayList<String> bigList = new ArrayList<String>(); //This ArrayList will end up containing all words that begin with a given prefix.
        prefixBuilder.append(prefix); //Append the passed in prefix into the String Builder...
        //We only fill the Array List if there is actually a word in the dictionary that starts with the given prefix.
        //Otherwise, we just return null... (I.e., the ArrayList will just be empty.)
        if(searchPrefix(prefix))
        {
            currentNode = navigateNode(currentNode, prefix, 0);
            //FOUND THE BUG: If we don't navigate the node to the right position, defaults to searching the ENTIRE DLB.
            //So only fill up the array list if the DLB DOES CONTAIN THE PREFIX!
            //Fill the ArrayList with all strings that start with the 'prefix'
            bigList = predictionsGenerator(currentNode, prefixBuilder, bigList);
            //Only fill in 5 slots...
            for(int i = 0; i < 5 && i < bigList.size(); i++)
            {
                predictionsList.add(bigList.get(i));
            }
        }
        //Return the ArrayList with at most 5 elements.
        return predictionsList;
    }

    /****************************************************************
     * Recursively builds up an ArrayList of Strings based on a 
     * given prefix.
     * 
     * <p>There are several cases to consider in determining how to
     * fill up the ArrayList.</p>
     * <ul>
     * <li>We will take a similar approach to determining prefixes
     * as in the searchPrefix and search functions.</li>
     * <li>First, we need to use a StringBuilder to build up a 
     * String up to the end of the prefix, and determine if we
     * need to traverse children and siblings.</li>
     * </ul>
     * <p><b>Base Case I</b>: Encounter a Terminating Character, 
     * but we have a sibling</p>
     * <p><b>Base Case II</b>: Encounter a Terminating Character, 
     * and we have no siblings
     * <p><b>Recursive Case I</b>: Encounter a non-null Child Node, 
     * but we have no siblings</p>
     * <p><b>Recursive Case II</b>: Encounter a no null Child 
     * and Sibling Node</p>
     * 
     * @param currentNode represents the current node we are at.
     * This should theoretically be at the end of a given prefix.
     * @param base represents the current condition of the String 
     * that we want to add to our ArrayList.
     * @param prefixList represents the ArrayList that will contain
     * all words that begin with a given prefix.
     * @return The filled up ArrayList which should be filled up 
     * with all words in the DLB beginning with a given prefix.
     **************************************************************/
    public ArrayList<String> predictionsGenerator(Node currentNode, StringBuilder base, ArrayList<String> prefixList)
    {
        //We need to recursively traverse through the DLB, and populate the ArrayList with all of the words that begin with the given prefix...
        StringBuilder anchorPoint = new StringBuilder();
        //Base Case I: Encounter a Terminating Character, but we have a sibling <-----> We have a word, and it's a prefix to another word!!
        if(traversalHelper("ENCOUNTER_PREFIX", currentNode))
        {
            //We have to recurse through all of the siblings of the starting node...
            return appendPrefix(anchorPoint, base, currentNode, prefixList);
        }
        //Base Case II: Encounter a Terminating Character, and we have no siblings <-----> We have a full word. Simply add the word, and return.
        else if(traversalHelper("ENCOUNTER_FULL_WORD", currentNode))
        {
            //We're done, we can just add the String that was built up by the String Builder to the ArrayList, and just return the ArrayList...
            return appendWord(prefixList, base);
        }
        //Recursive Case I: Encounter a non-null Child Node, but we have no siblings
        else if(traversalHelper("TRAVERSE_CHILD", currentNode))
        {
            //We have to recurse through all of the children of the starting node...
            return recursiveTraverseChild(base, currentNode, prefixList);
        }
        //Recursive Case II: Encounter a no null Child and Sibling Node
        else if(traversalHelper("TRAVERSE_BOTH", currentNode))
        {
            return recursiveTraverseBoth(anchorPoint, base, currentNode, prefixList);
        }
        //Return filled up array list.
        return prefixList;
    }
    /*************************************************************
     *             Helper Methods for Put Function:              *
     * ========================================================= *
     * 1) A function to actually add nodes                       *
     * 2) A function to determine when it is appropriate to      *
     * add/traverse a node.                                      *
     * 3) Add Child Node                                         *
     * 4) Traverse Child Node                                    *
     * 5) Add Sibling Node                                       *
     * 6) Traverse Sibling Node                                  *
     *************************************************************/

    /**************************************************************
     * Adds a child node to the current node with a given 
     * character.
     * 
     * @param current represents current node in DLB. We want 
     * to append a child node to this node using this function.
     * @param letter represents the value to be stored with
     * this node.
     * @return The newly created child node.
     *************************************************************/
    private Node addChild(Node current, char letter)
    {
        current.childNode = new Node(letter);
        return current.childNode;
    }

    /************************************************************
     * Traverses the current node to the current node's child
     * node. Necessary when there is a hit, but the child node
     * is non-null. 
     * 
     * @param current represents the current node in DLB. We 
     * want to traverse to the current node's child node 
     * using this function.
     * @return The current node's child node.
     ***********************************************************/
    public Node traverseChild(Node current)
    {
        return current.childNode;
    }

    /**************************************************************
     * Adds a sibling node to the current node with a given 
     * character.
     * 
     * @param current represents current node in DLB. We want 
     * to append a sibling node to this node using this function.
     * @param letter represents the value to be stored with
     * this node.
     * @return The newly created sibling node.
     *************************************************************/
    public Node addSibling(Node current, char letter)
    {
        current.siblingNode = new Node(letter);
        return current.siblingNode;
    }

    /************************************************************
     * Traverses the current node to the current node's sibling
     * node. Necessary when there is a miss, but the sibling node
     * is non-null. 
     * 
     * @param current represents the current node in DLB. We 
     * want to traverse to the current node's sibling node 
     * using this function.
     * @return The current node's sibling node.
     ***********************************************************/
    public Node traverseSibling(Node current, int index)
    {
        return current.siblingNode;
    }
    
    /*******************************************************************
     * This function helps determine whether an append/traversal is 
     * required from a given node/character.
     * 
     * @param sentinelKey represents what we should consider with the 
     * passed in node.
     * @param currNode represents the current node in the DLB.
     * @param letter represents the value we want to compare with the 
     * node's value, to determine if we need to traverse/add a child
     * or sibling node.
     * @return Whether to append/traverse a child/sibling node.
     ******************************************************************/
    public boolean putSecondaryHelper(String sentinelKey, Node currNode, char letter)
    {
        switch(sentinelKey)
        {
            //Does the root exist?
            case "CHECK_ROOT":
                return (rootNode == null) ? true : false;
            //When can I add a childNode? 
            //(When we're not at the TERMINATOR (since the terminator can't have child nodes), we have a hit, and the child doesn't exist!)
            case "ADD_CHILD":
                return (currNode.val != TERMINATOR_CHAR && currNode.childNode == null) ? true : false;
            //When do I need to traverse to a childNode?
            //(When I get a hit with the current node's value and the current char of the string!)
            case "TRAVERSE_CHILD":
                return(currNode.val == letter && currNode.childNode != null) ? true : false;
            //When can I add a siblingNode?
            //(When the sibling node is null, and there's a mismatch, and we haven't reached the terminator!)
            case "ADD_SIBLING":
                return(currNode.val != letter && letter != TERMINATOR_CHAR && currNode.siblingNode == null) ? true : false;
            //When can I traverse to a siblingNode?
            //(When the sibling is not null, and there's a mismatch!)
            case "TRAVERSE_SIBLING":
                return(currNode.val != letter && currNode.siblingNode != null) ? true : false;
            //Should never hit this case, but just to make the compiler stop complaining!
            default: 
                return false;
        }
    }

    /*******************************************************************
     * This function helps determine what to traverse to from a 
     * given node/character.
     * 
     * @param sentinelKey represents what we should consider with the 
     * passed in node.
     * @param currNode represents the current node in the DLB.
     * @return Whether to traverse a child/sibling or both.
     ******************************************************************/
    public boolean traversalHelper(String sentinelKey, Node startingNode)
    {
        switch(sentinelKey)
        {
            //Need to traverse sibling and at terminator, indicates that we have a prefix to another word.
            case "ENCOUNTER_PREFIX":
                return (startingNode.val == TERMINATOR_CHAR && startingNode.siblingNode != null) ? true : false;
            //Don't need to traverse sibling, and at terminator, indicates a full word.
            case "ENCOUNTER_FULL_WORD":
                return (startingNode.val == TERMINATOR_CHAR && startingNode.siblingNode == null) ? true : false;
            //Need to traverse child node only.
            case "TRAVERSE_CHILD":
                return(startingNode.childNode != null && startingNode.siblingNode == null) ? true : false;
            //Need to traverse child and sibling nodes. 
            case "TRAVERSE_BOTH":
                return(startingNode.childNode != null && startingNode.siblingNode != null) ? true : false;
            //Should never hit this case, but just to make the compiler stop complaining!
            default: 
                return false;
        }
    }

    /***********************************************************************
     * Encounter a prefix. Add prefix to anchorPoint, and then recursively 
     * go through the sibling, and see if there are any more words to add
     * to the ArrayList.
     * 
     * @param anchorPoint represents a anchor point for when recursing 
     * through both the child and the sibling nodes. 
     * @param base represents String being built up to add to ArrayList.
     * @param currentNode represents the currentNode, and required to 
     * determine which node to traverse to, either the sibling or the 
     * child node.
     * @param prefixList represents ArrayList to be filled up by words 
     * beginning with same prefix.
     * @return the corresponding ArrayList filled up with words with the 
     * same prefix.
     **********************************************************************/
    public ArrayList<String> appendPrefix(StringBuilder anchorPoint, StringBuilder base, Node currentNode, ArrayList<String> prefixList)
    {
        anchorPoint.append(base);
        prefixList.add(anchorPoint.toString());
        return predictionsGenerator(currentNode.siblingNode, base, prefixList);
    }

    /***********************************************************************
     * Encountered a full word. Simply add the word, and return, thus 
     * terminating recursion.
     * 
     * @param prefixList represents ArrayList to be filled up by words 
     * beginning with same prefix.
     * @param base represents String being built up to add to ArrayList.
     * @return the corresponding ArrayList filled up with words with the 
     * same prefix.
     **********************************************************************/
    public ArrayList<String> appendWord(ArrayList<String> prefixList, StringBuilder base)
    {
        prefixList.add(base.toString());
        return prefixList;
    }

    /**********************************************************************
     * Recursively go through all of the nodes and adding necessary words
     * from the child node.
     * 
     * @param base represents String being built up to add to ArrayList.
     * @param currentNode represents the currentNode, and required to 
     * determine which node to traverse to, either the sibling or the 
     * child node.
     * @param prefixList represents ArrayList to be filled up by words 
     * beginning with same prefix.
     * @return the corresponding ArrayList filled up with words with the 
     * same prefix.
     *********************************************************************/
    public ArrayList<String> recursiveTraverseChild(StringBuilder base, Node currentNode, ArrayList<String> prefixList)
    {
        base.append(currentNode.val);
        return predictionsGenerator(currentNode.childNode, base, prefixList);
    }

    /*************************************************************************
     * Recursively go through all of the nodes and adding necessary words
     * from the child and sibling nodes.
     * 
     * <p><ul><li>It is important to note that the anchor point is important 
     * to traverse through both child and sibling nodes.</li></ul></p>
     * 
     * <p>Once we have traversed through and looked through all nodes from
     * the child node, and added all necessary words into the arrayList, 
     * if we needed to go through the sibling, we can't go from where we
     * end up from traversing through the child nodes. We need to go 
     * from where we started recursion from.</p>
     * 
     * <p>In other words, we need a ANCHOR POINT, thus the need for that 
     * anchor point, so we need to use that anchor point when we recurse
     * using sibling nodes.</p>
     * 
     * @param anchorPoint represents a anchor point for when recursing 
     * through both the child and the sibling nodes. 
     * @param base represents String being built up to add to ArrayList.
     * @param currentNode represents the currentNode, and required to 
     * determine which node to traverse to, either the sibling or the 
     * child node.
     * @param prefixList represents ArrayList to be filled up by words 
     * beginning with same prefix.
     * @return the corresponding ArrayList filled up with words with the 
     * same prefix.
     ***********************************************************************/
    public ArrayList<String> recursiveTraverseBoth(StringBuilder anchorPoint, StringBuilder base, Node currentNode, ArrayList<String> prefixList)
    {
        //Append string to the anchor point, so we know exactly where to start from after recursing through child...
        anchorPoint.append(base);
        //Append current node's val!
        base.append(currentNode.val);
        //Recurse through both the child and the sibling (ORDER MATTERS: Child first, then the sibling...)
        predictionsGenerator(currentNode.childNode, base, prefixList);
        //Important, we need to have the same anchor point, thus the need for the temporary String Builder to serve as the reference point...
        predictionsGenerator(currentNode.siblingNode, anchorPoint, prefixList);
        
        return prefixList;
    }


}