/************************************************************************
 * Name: Gordon Lu
 * Project: Network Flow
 * 
 * Designed for Dr.Garrison's CS 1501 Algorithm Implementation Class
 * 
 ***********************************************************************/

 /******************************************************************************
 *  Compilation:  javac NetworkAnalysis.java
 *  Execution:    java NetworkAnalysis [filename]
 *  Dependencies: EdgeWeightedGraph.java, Edge.java, EdgeWeightedDigraph.java,
 *                DirectedEdge.java, Graph.java, UnweightedEdge.java, 
 *                DijkstraSP.java, DijkstraAllPairsSP.java, Queue.java,
 *                IndexMinPQ.java, MinPQ.java, Stack.java, Bag.java, 
 *                KruskalMST.java, UF.java, DepthFirstSearch.java
 *
 *  Performing rudimentary operations on a Computer Network Graph.
 *
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;
import java.io.FileNotFoundException;
public class NetworkAnalysis
{
    /*****************************************************************************************************
     * Project 4: Graph Application
     * 
     * 
     * Your program will analyze a given graph representing a computer network according to several specified metrics. 
     * The vertices of these graphs will represent switches in the network, while the edges represent either fiber optic or copper cables 
     * run between the switches. Your program should operate entirely via a console interface menu (no GUI).
     * 
     * Example: 0 5 optical 10000 25 describes an edge between vertex 0 and vertex 5 that represents a 25 meter optical cable w/bandwidth 10 gb/s
     * 
     * 
     * 
     * 
     ****************************************************************************************************/
    static Scanner userInput = new Scanner(System.in);
    static int numVertices;
    static int menuChoice;

    //static ArrayList<Graph> subgraphSets = new ArrayList<Graph>();

    //Initialize a digraph :)
    static EdgeWeightedDigraph networkFlowGraph;
    static DijkstraAllPairsSP lowestLatencyPath;
    static EdgeWeightedDigraph networkTimeGraph;
    static EdgeWeightedGraph undirectedNetworkFlowGraph;
    static EdgeWeightedDigraph networkCopperFlowGraph;

    static EdgeWeightedGraph undirectedCopperFlowGraph;
    static Graph unweightedNetworkGraph;

    static KruskalMST minSpanningTree;
    /* static BreadthFirstPaths bfs;
    static BreadthFirstPaths bfs_biconnectivity;
    */
    //static Biconnected articulationPointCheck;

    public static void main(String[] args) throws Exception
    {
        //Determine whether the user has entered sufficient arguments.
        try
        {
            /*******************************************************************************************************
             * =================================================================================================== *
             * Error Box I: Determines whether the user has entered the appropriate arguments...                   *  
             * - If the user does not enter a single argument, the program will display a Message Box that         *
             * displays the expected input!                                                                        *     
             * - The program will then exit...                                                                     * 
             * =================================================================================================== * 
             ******************************************************************************************************/
            if(args.length != 1)
            {
                System.out.println("\n<===================ERROR===INVALID==ARGUMENTS==PROVIDED====================>");
                System.out.println("|| ======================================================================= ||");
                System.out.println("||      Expected Usage is: C:\\> java NetworkAnalysis <input filename>      ||");
                System.out.println("||              Re-execute with the correct arguments!                     ||");
                System.out.println("|| ======================================================================= ||");
                System.out.println("<===========================================================================>\n");
                System.exit(0);
            }
            /*******************************************************************************************************
             * =================================================================================================== *
             * Reading from User-Inputted File: The program will perform the following...                          *
             * 1) Read from the file...                                                                            *
             * 2) Create both a Directed and Undirected Graph based on the first line from the file!               *
             * 3) Create both a DirectedEdge (for DirectedGraph), and an Edge (for UndirectedGraph)!               * 
             * =================================================================================================== * 
             ******************************************************************************************************/
            //Create a File Handle to read in the File Contents!!
            BufferedReader fHand = new BufferedReader(new FileReader(args[0]));
            //The number of vertices in the graph is indicated by the first line in the file!
            String numVertices = fHand.readLine();
            //Generate a EdgeWeightDirectedGraph (to be used with Dijkstra's) based on number of vertices from the first line of the file!
            networkFlowGraph = new EdgeWeightedDigraph(Integer.parseInt(numVertices));
            //Generate an UndirectedGraph (to be used with Kruskal's) based on number of vertices from the first line of the file!
            undirectedNetworkFlowGraph = new EdgeWeightedGraph(Integer.parseInt(numVertices));
            //Generate a EdgeWeightDirectedGraph (to be used to determine Copper Only) based on number of vertices from the first line of the file!
            networkCopperFlowGraph = new EdgeWeightedDigraph(Integer.parseInt(numVertices));
            //Generate a EdgeWeightDirectedGraph (to be used to determine Latency) based on number of vertices from the first line of the file!
            networkTimeGraph = new EdgeWeightedDigraph(Integer.parseInt(numVertices));
            //Generate a regular ol' graph, to be used to determine connectivity!
            unweightedNetworkGraph = new Graph(Integer.parseInt(numVertices));
            undirectedCopperFlowGraph = new EdgeWeightedGraph(Integer.parseInt(numVertices));
            //Represents the current line in the file!
            String fLine;
            //Iterate through the file only when there are lines left in the file!
            while(fHand.ready())
            {
                //Read in the current line of the file!
                fLine = fHand.readLine();
                //Split the contents of the File based on spaces, delimit based on " "
                String[] vertexContents = fLine.split(" ");
                if(vertexContents[2].equalsIgnoreCase("COPPER"))
                {
                    networkCopperFlowGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[3])));
                    undirectedCopperFlowGraph.addEdge(new Edge(vertexContents, Double.parseDouble(vertexContents[3])));
                    String temporaryStartVertex = vertexContents[0];
                    vertexContents[0] = vertexContents[1];
                    vertexContents[1] = temporaryStartVertex;
                    networkCopperFlowGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[3])));
                }
                //Create the corresponding directed edge, then add it into the directed network flow graph!
                networkFlowGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[3])));
                //Create the "undirected" edge, then add it into the undirected network flow graph!
                //undirectedNetworkFlowGraph.addEdge(new Edge(vertexContents));

                //Create a time graph...
                if(vertexContents[2].equalsIgnoreCase("COPPER"))
                {
                    networkTimeGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[4])/230000000));
                    undirectedNetworkFlowGraph.addEdge(new Edge(vertexContents, Double.parseDouble(vertexContents[4])/230000000));

                }
                else if(vertexContents[2].equalsIgnoreCase("OPTICAL"))
                {
                    networkTimeGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[4])/200000000));
                    undirectedNetworkFlowGraph.addEdge(new Edge(vertexContents, Double.parseDouble(vertexContents[4])/200000000));
                }

                //Since a directed graph can have self-edges, we have to add the pair (b,a), given that the pair (a,b) was just added! 
                //Utilize a temporary to do this!
                String temporaryStartVertex = vertexContents[0];
                vertexContents[0] = vertexContents[1];
                vertexContents[1] = temporaryStartVertex;
                //Once it's all said and done, we create the directed edge to the directed network flow graph!
                networkFlowGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[3])));
                
                if(vertexContents[2].equalsIgnoreCase("COPPER"))
                {
                    networkTimeGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[4])/230000000));
                }
                else if(vertexContents[2].equalsIgnoreCase("OPTICAL"))
                {
                    networkTimeGraph.addEdge(new DirectedEdge(vertexContents, Double.parseDouble(vertexContents[4])/200000000));
                }
                unweightedNetworkGraph.addEdge(new UnweightedEdge(vertexContents));
            }
            //Close the File Handle once the File is fully processed!
            fHand.close();
            System.out.println("Copper Only Graph: \n" + networkCopperFlowGraph);
            System.out.println("Regular Flow Graph: \n" + networkTimeGraph);

        } //End of Try Block...
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
            System.out.println("||      There was an unexpected error when loading the provided file!      ||");
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
            System.out.println("||      There was an unexpected error when loading the provided file!      ||");
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
        catch(ArrayIndexOutOfBoundsException a)
        {
            System.out.println("\n<===================ERROR===INVALID==ARGUMENTS==PROVIDED====================>");
            System.out.println("|| ======================================================================= ||");
            System.out.println("||      Expected Usage is: C:\\> java NetworkAnalysis <input filename>      ||");
            System.out.println("||              Re-execute with the correct arguments!                     ||");
            System.out.println("|| ======================================================================= ||");
            System.out.println("<===========================================================================>\n");
            System.exit(0);
        } // End of Try-Catch block!
        /********************************************************************************************************
         * ==================================================================================================== *
         * Print Menu Options: If all goes well with Processing & Loading the File, display the menu of options *
         *                     for the to pick from!                                                            *
         * ==================================================================================================== *
         ******************************************************************************************************/ 

        displayOptions();
        /********************************************************************************************************      
         * ==================================================================================================== *
         * Parsing the User's Choice.....                                                                       *
         * 1) If the user enters a String, the program will send the user to the default case, and prompt the   *
         * user to enter a valid option!                                                                        *
         *                                                                                                      *
         * 2) Otherwise, process the result, and send them into the corresponding cases!                        * 
         *                                                                                                      *
         * 3) If the user enters an option that is not on the menu, also send them into the default case!       *
         * ==================================================================================================== *
         ********************************************************************************************************/ 
        try
        {
            menuChoice = Integer.parseInt(userInput.nextLine());
            System.out.println("||==================================================================================||");
            System.out.println("<~----------------------------------------------------------------------------------~>\n");
        }
        catch(Exception e)
        {
            menuChoice = -1;
        }
        /********************************************************************************************************      
         * ==================================================================================================== *
         * Determining when to cease the simulation...                                                          *
         * 1) Utilize boolean variables to indicate whether or not to prompt the user to enter one of the menu  *
         * option!                                                                                              *
         *                                                                                                      *
         * 2) ceasePrompt -> indicates whether or not the user wants to go through another option!              *
         * - If it is true, it means stop prompting and exit!                                                   *
         *                                                                                                      *
         * 3) jumpToYes -> indicates whether the user has decided to choose another menu option!                *
         * - On the first iteration, it should be false, and overwritten based on if the user decides to choose *
         * yes!                                                                                                 *
         *                                                                                                      *
         * 4)                                                                                                   *
         * ==================================================================================================== *        
        ********************************************************************************************************/ 
        boolean ceasePrompt = false;
        boolean jumpToYes = false;
        String restart = "";

        /********************************************************************************************************      
         * ==================================================================================================== *
         * Constantly asking the user to enter menu options: The UI part! :)                                    *
         * 1) We should only be prompting the user if the user has entered the Yes option, or has entered       *
         * invalid input, then has requested to display ioptions to decide on another option!                   * 
         *                                                                                                      *
         * 2) If the user enters no, we just exit the program!                                                  *
         * ==================================================================================================== *        
        ********************************************************************************************************/ 
        //While the user still wants to do some operation with the Network...
        while(ceasePrompt  == false)
        {
            /********************************************************************************************************      
             * ==================================================================================================== *
             * Switch-Case Block: Determine which operation to perform based on the users input!                    *
             * (1): Determine the Lowest Latency Path between Vertices in the Network                               *
             * (2): Determine whether the network is Copper-Only Connected                                          *
             * (3): Determine the Minimum Average Latency Spanning Tree for the Network                             *   
             * (4): Performing a Connectivity Test on all Vertex Pairs                                              *
             * (5): Exit the Simulation                                                                             *
             * (ELSE): Tell the user that their input was not one of the above options, and prompt the user again!  *
             * ==================================================================================================== *
            ********************************************************************************************************/
            switch(menuChoice)
            {
                /******************************************************************************************************** 
                 * ==================================================================================================== *
                 * Determine the Lowest Latency Path in the Computer Network:                                           *
                 * ==================================================================================================== *
                 * Prompt the user to enter two vertices in the graph!                                                  *
                 * - If any vertices don't exist, EdgeWeightedDigraph class will display an IllegalArgumentException    *
                 * indicating that the user has entered a vertex that is not in the graph, or that path does not exist! *
                 *                                                                                                      *
                 * Once the correct vertices have been passed in, perform Djikstra's on EdgeWeightedDigraph, and then   *
                 * determine if there is a path between the two vertices.                                               *
                 * - If so, determine the SP, then output the given path along with the bandwidths to traverse to each  *
                 * path!                                                                                                *
                 * - Then, output the minimum bandwidth!                                                                *
                 * - NOTE: If the same vertex is entered as the start and end, the program will simply display the      *
                 * bandwidth as the lowest possible integer value (since it takes little to no time!)                   *
                 * ==================================================================================================== *
                 *******************************************************************************************************/
                case 1: 
                    //Call to view the lowest latency path in the network...
                    if(jumpToYes == false)
                    {
                        System.out.println("<~-----Enter---the--Vertices--to---Determine--the---Lowest--Latency--Path---for-----~>");
                        System.out.println("||==================================================================================||");
                        System.out.print("|| Enter the First Vertex: ");
                        int startVertex = Integer.parseInt(userInput.nextLine());
                        System.out.print("|| Enter the Second Vertex: ");
                        int endVertex = Integer.parseInt(userInput.nextLine());
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>");

                        //Utilize Dijkstra's SP algorithm to determine the shortest between vertices!
                        try
                        {
                            lowestLatencyPath = new DijkstraAllPairsSP(networkTimeGraph);
                            if(lowestLatencyPath.hasPath(startVertex, endVertex))
                            {
                                int min_bandwidth = Integer.MAX_VALUE;
    
                                String minLatencyPath = lowestLatencyPath.path(startVertex, endVertex).toString();
                                String[] pathsArray = minLatencyPath.split(" ");
                                System.out.println("<~-----------Displaying---the---Minimum---Latency---Path---between---Vertices-------~>");
                                System.out.println("||==================================================================================||");
                                System.out.println("|| Logistics:                                                                       ||");
                                System.out.println("||==================================================================================||");
                                System.out.println("|| The Lowest Latency Path between vertices [ " + startVertex + " ] and [ " + endVertex +  " ] is: \t\t    ||" );
                                for(int i = 0; i < pathsArray.length; i++)
                                {
                                    //Beautify it ;)
                                    if(i == 0)
                                    {
                                        System.out.print("|| ");
                                    }
                                    if(startVertex == endVertex)
                                    {
                                        min_bandwidth = 0;
                                        System.out.println("==========================WARNING:=SELF-EDGE=DETECTED=========================== ||");
                                        System.out.print("|| ( " + startVertex + " )" + " -> " + "( " + endVertex + " ) \t\t\t\t\t\t\t\t    ||");
                                    }
                                    if(i == pathsArray.length - 1 && startVertex != endVertex)
                                    {
                                        System.out.print("( " + pathsArray[i] + " )" + " ");
                                    }
                                     else if(!pathsArray[i].equals("") && startVertex != endVertex)
                                    {
                                        System.out.print("( " + pathsArray[i] + " )" + " -> ");
                                    }
                                    
                                    
                                }
                                System.out.println("\n||==================================================================================||");
    
                                for(DirectedEdge edge : lowestLatencyPath.path(startVertex, endVertex))
                                {
                                    for(DirectedEdge bandwidthEdge : networkFlowGraph.edges())
                                    {
                                        if(edge.retrieveStart() == bandwidthEdge.retrieveStart() && (edge.retrieveEnd() == bandwidthEdge.retrieveEnd()))
                                        {
                                            min_bandwidth = Math.min(min_bandwidth, (int)bandwidthEdge.retrieveDensity());
                                            System.out.println("||" + bandwidthEdge.bandWidthToString());
    
                                        }
                                    }
                                }
                                System.out.println("||==================================================================================||");
                                System.out.println("|| The minimum bandwidth along this path was: " + min_bandwidth);
                                System.out.println("||==================================================================================||");
                                System.out.println("<~----------------------------------------------------------------------------------~>\n");
                            
                           
                        
                            }
                        }
                        catch(Exception e)
                        {
                            System.out.println("||==================================================================================||");
                            System.out.println("|| ======================WARNING:=INVALID-VERTEX=DETECTED========================== ||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n"); 
                        }   
                        System.out.println("\n<~------------------Prompting----User----For---Additional----Input------------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                        System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                        restart = userInput.nextLine().strip();
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");


                }
                
                    if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                    {
                        ceasePrompt = true;
                        System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                        System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                        System.out.println("||                            Goodbye, have a nice day!                             ||");
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                        System.exit(0);
                    }
                    else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                    {
                        displayOptions();
                        try
                        {
                            menuChoice = Integer.parseInt(userInput.nextLine());
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");

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
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");

                            System.out.println("<~----------------------Input---------Mismatch---------Warning----------------------~>");
                            System.out.println("||==================================================================================||");
                            System.out.println("||                            Invalid user input!                                   ||");
                            System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                            System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                            System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                            System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                            restart = userInput.nextLine().strip();
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");
                        
                            if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                            {
                                System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                                System.out.println("||==================================================================================||");
                                System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                                System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                                System.out.println("||                            Goodbye, have a nice day!                             ||");
                                System.out.println("||==================================================================================||");
                                System.out.println("<~----------------------------------------------------------------------------------~>\n");
                                System.exit(0);
                            }
                            else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                            {
                                displayOptions();
                                try
                                {
                                    menuChoice = Integer.parseInt(userInput.nextLine());
                                    System.out.println("||==================================================================================||");
                                    System.out.println("<~----------------------------------------------------------------------------------~>\n");
            
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
                        System.out.println("<~------------Testing---if---Network----is----Copper----Only----Connected-----------~>");
                        System.out.println("||==================================================================================||");

                        //System.out.println(networkCopperFlowGraph);
                        //System.out.println(networkTimeGraph);
                        //boolean copperOnly = networkCopperFlowGraph.connectedVertex(networkCopperFlowGraph);
                        //System.out.println(copperOnly);
                        //int numCopperConnectedSwitches = bfs.traverseCopper(networkFlowGraph, 0);
                        //int numCopperConnectedSwitches = bfs(0);
                        //If all of the vertices are connected by copper wires???
                        boolean copperOnly = undirectedCopperFlowGraph.connectedVertex(undirectedCopperFlowGraph);
                        
                        if(copperOnly)
                        {
                            System.out.println("||          The computer network is connected only using copper cables...           ||");
                        }
                        /* else if(numCopperConnectedSwitches == 0)
                        {
                            System.out.println("||       The computer network is connected only using fiber optic cables...         ||");
                        } */
                        else
                        {
                            System.out.println("||     The computer network is not only connected using copper cables...            ||");
                        }
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");

                        System.out.println("\n<~------------------Prompting----User----For---Additional----Input------------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                        System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                        restart = userInput.nextLine().strip();
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                    }
            
                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                {
                    ceasePrompt = true;
                    System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                    System.out.println("||==================================================================================||");
                    System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                    System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                    System.out.println("||                            Goodbye, have a nice day!                             ||");
                    System.out.println("||==================================================================================||");
                    System.out.println("<~----------------------------------------------------------------------------------~>\n");
                    System.exit(0);
                }
                else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                {
                    displayOptions();
                    try
                    {
                        menuChoice = Integer.parseInt(userInput.nextLine());
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");

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
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");

                        System.out.println("<~----------------------Input---------Mismatch---------Warning----------------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("||                            Invalid user input!                                   ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                        System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                        restart = userInput.nextLine().strip();
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;
                            System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                            System.out.println("||==================================================================================||");
                            System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                            System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                            System.out.println("||                            Goodbye, have a nice day!                             ||");
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(userInput.nextLine());
                                System.out.println("||==================================================================================||");
                                System.out.println("<~----------------------------------------------------------------------------------~>\n");
        
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
                    // Determine the Minimum Average Latency Spanning Tree...
                    // Use either Kruskal's or Prim's??? (it doesn't matter!)
                    // Read in as undirected graph though!!

                    System.out.println("<~---------Displaying---the---Minimum---Average---Latency---Spanning---Tree---------~>");
                    System.out.println("||==================================================================================||");
                    //KruskalMST minimumAvgLatencySpanningTree = new KruskalMST(undirectedNetworkFlowGraph);
                    KruskalMST minimumAvgLatencySpanningTree = new KruskalMST(undirectedNetworkFlowGraph);
                    minimumAvgLatencySpanningTree.displayMinimumSpanningTree();                    
                    System.out.println("||==================================================================================||");
                    System.out.println("<~----------------------------------------------------------------------------------~>\n");
                    

                    System.out.println("\n<~------------------Prompting----User----For---Additional----Input------------------~>");
                    System.out.println("||==================================================================================||");
                    System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                    System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                    System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                    System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                    restart = userInput.nextLine().strip();
                    System.out.println("||==================================================================================||");
                    System.out.println("<~----------------------------------------------------------------------------------~>\n");
                }

                if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                    {
                        ceasePrompt = true;
                        System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                        System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                        System.out.println("||                            Goodbye, have a nice day!                             ||");
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                        System.exit(0);
                    }
                    else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                    {
                        displayOptions();
                        try
                        {
                            menuChoice = Integer.parseInt(userInput.nextLine());
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");    
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
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");

                            System.out.println("<~----------------------Input---------Mismatch---------Warning----------------------~>");
                            System.out.println("||==================================================================================||");
                            System.out.println("||                            Invalid user input!                                   ||");
                            System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                            System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                            System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                            System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                            restart = userInput.nextLine().strip();
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");
                
                            if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                            {
                                ceasePrompt = true;
                                System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                                System.out.println("||==================================================================================||");
                                System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                                System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                                System.out.println("||                            Goodbye, have a nice day!                             ||");
                                System.out.println("||==================================================================================||");
                                System.out.println("<~----------------------------------------------------------------------------------~>\n");
                                System.exit(0);
                            }
                            else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                            {
                                displayOptions();
                                try
                                {
                                    menuChoice = Integer.parseInt(userInput.nextLine());
                                    System.out.println("||==================================================================================||");
                                    System.out.println("<~----------------------------------------------------------------------------------~>\n");            
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
                    //Need to check if the graph will maintain biconnectivity if vertices are removed??
                    //Perform a bfs to determine if there are any pairs of vertices whose removal could potentially ruin connectivity
                    if(jumpToYes == false)
                    {
                        System.out.println("<~------------Performing---Connectivity---Test---on---all---Vertex---Pairs----------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>");

                        //articulationPointCheck = new Biconnected(unweightedNetworkGraph); //Find all the articulation points in the Graph
                        //Graph tempGraph = new Graph(unweightedNetworkGraph); //tempGraph is a deep copy of the unweighted network graph!
                        //EdgeWeightedDigraph subDigraph = new EdgeWeightedDigraph(networkTimeGraph.V() - 1);
                        //TreeSet<DirectedEdge> propUnweightedEdges = new TreeSet<DirectedEdge>(); //TreeSet is a self-balancing BST used to store objects
                        //Set<DirectedEdge> synchronizedSet = Collections.synchronizedSet(propUnweightedEdges); //Thread-Efficient Set
                        ArrayList<Integer> vertexList = new ArrayList<Integer>(networkTimeGraph.V() - 1);
                        ArrayList<Integer> newGraphSize = new ArrayList<Integer>(networkTimeGraph.V() - 1);
                        HashMap<Integer, Integer> subGraphReindexer = new HashMap<Integer, Integer>(networkTimeGraph.V() - 1);
                        //int initial_APs_annihilated_graph = 0;
                        int numAPs_subquery_obliterated_graph = 0;
                        EdgeWeightedDigraph subDigraph = new EdgeWeightedDigraph(networkTimeGraph.V() - 1);
                        for(int v = 0; v < networkTimeGraph.V(); v++)
                        {
                            subDigraph = new EdgeWeightedDigraph(networkTimeGraph.V() - 1);   //The subgraph will ignore another vertex...
                            subGraphReindexer = new HashMap<Integer, Integer>(networkTimeGraph.V() - 1);    //Ignore another vertex mwahahah
                            
                            for(int i = 0; i < networkTimeGraph.V(); i++)
                            {   
                                if(i != v)  //If i == v -> this is the node we want to remove, we just want to reindex the vertices that have not been ignored!
                                {
                                    subGraphReindexer.put(i, subGraphReindexer.size());
                                }
                            }
                            //for(int w = 0; w < unweightedNetworkGraph.V(); w++ )
                            //{
                               // for(UnweightedEdge uEdge : unweightedNetworkGraph.adj(v))
                                //{
                                for(DirectedEdge dEdge : networkTimeGraph.edges())
                                {
                                  if(v != dEdge.retrieveStart() && v != dEdge.retrieveEnd())
                                  {
                                    //propUnweightedEdges.add(dEdge);
                                    String toInsert = "" + subGraphReindexer.get(dEdge.retrieveStart()) + " " + subGraphReindexer.get(dEdge.retrieveEnd()) + " ";
                                    subDigraph.addEdge(new DirectedEdge(toInsert.split(" ")));
                                    //String toInsert = "" + subGraphIndexer.get(uEdge.retrieveStart()) + " " + subGraphIndexer.get(uEdge.retrieveEnd()) + " ";
                                    //subGraph.addEdge(new UnweightedEdge(toInsert.split(" ")));
                                  }
                                }
                                /* for(UnweightedEdge uEdge : unweightedNetworkGraph.edges())
                                {
                                    if(get_TreeSet(propUnweightedEdges, uEdge) == null)
                                    {
                                        continue;
                                    }
                                    if(subGraphReindexer.containsKey(get_TreeSet(propUnweightedEdges, uEdge).retrieveStart()) && subGraphReindexer.containsKey(get_TreeSet(propUnweightedEdges, uEdge).retrieveEnd()))
                                    {
                                        if(v != uEdge.retrieveStart() && v != uEdge.retrieveEnd())
                                        {
                                            String toInsert = "" + subGraphReindexer.get(uEdge.retrieveStart()) + " " + subGraphReindexer.get(uEdge.retrieveEnd()) + " ";
                                            subGraph.addEdge(new UnweightedEdge(toInsert.split(" ")));
                                        }
                                    }
                                } */
                                //}
                            //}
                            subDigraph.performAPSearch(subDigraph); //Determine if there are any remaining articulation points in the newly generated subgraph!
                            if(subDigraph.hasArticulationPoint())
                            {
                                numAPs_subquery_obliterated_graph++; 
                            }
                        }
                        if(numAPs_subquery_obliterated_graph >= 1)
                        {
                            System.out.println("||   The network cannot maintain connectivity upon removal of any two vertices...   ||");
                        }
                        else
                        {
                            System.out.println("||\t The network is powerful enough to survive removal of any two vertices\t    ||");
                        }
                        
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                    
                        System.out.println("\n<~------------------Prompting----User----For---Additional----Input------------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                        System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                        restart = userInput.nextLine().strip();
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                    }

                    if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                    {
                        ceasePrompt = true;
                        System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                        System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                        System.out.println("||                            Goodbye, have a nice day!                             ||");
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                        System.exit(0);
                    }
                    else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES") )
                    {
                        displayOptions();
                        try
                        {
                            menuChoice = Integer.parseInt(userInput.nextLine());;
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");    
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
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");
                            
                            System.out.println("<~----------------------Input---------Mismatch---------Warning----------------------~>");
                            System.out.println("||==================================================================================||");
                            System.out.println("||                            Invalid user input!                                   ||");
                            System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                            System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                            System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                            System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                            restart = userInput.nextLine().strip();
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");
                    
                            if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                            {
                                ceasePrompt = true;
                                System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                                System.out.println("||==================================================================================||");
                                System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                                System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                                System.out.println("||                            Goodbye, have a nice day!                             ||");
                                System.out.println("||==================================================================================||");
                                System.out.println("<~----------------------------------------------------------------------------------~>\n");
                                System.exit(0);
                            }
                            else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                            {
                                displayOptions();
                                try
                                {
                                    menuChoice = Integer.parseInt(userInput.nextLine());
                                    System.out.println("||==================================================================================||");
                                    System.out.println("<~----------------------------------------------------------------------------------~>\n");                                                
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
                    ceasePrompt = true;
                    System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                    System.out.println("||==================================================================================||");
                    System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                    System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                    System.out.println("||                            Goodbye, have a nice day!                             ||");
                    System.out.println("||==================================================================================||");
                    System.out.println("<~----------------------------------------------------------------------------------~>\n");
                    System.exit(0);
                break;

                default:
                    while(ceasePrompt == false)
                    {
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");

                        System.out.println("<~----------------------Input---------Mismatch---------Warning----------------------~>");
                        System.out.println("||==================================================================================||");
                        System.out.println("||                            Invalid user input!                                   ||");
                        System.out.println("|| NOTE: Whitespace has been accounted for & Input is Case-Insensitive!             ||");
                        System.out.println("|| (1): 'Yes' <==> ' Yes '                                                          ||");
                        System.out.println("|| (2): 'YES' <==> 'yes'                                                            ||");
                        System.out.print("||              Would you like to enter another option (Enter Y/N)? ");
                        restart = userInput.nextLine().strip();
                        System.out.println("||==================================================================================||");
                        System.out.println("<~----------------------------------------------------------------------------------~>\n");
                
                        if(restart.equalsIgnoreCase("N") || restart.equalsIgnoreCase("NO"))
                        {
                            ceasePrompt = true;
                            System.out.println("<~-----------------Exiting------Computer------Network------Simulator----------------~>");
                            System.out.println("||==================================================================================||");
                            System.out.println("||                      Terminating Computer Network Simulator...                   ||");
                            System.out.println("||                 Thank you using the Computer Network Simulator!!                 ||");
                            System.out.println("||                            Goodbye, have a nice day!                             ||");
                            System.out.println("||==================================================================================||");
                            System.out.println("<~----------------------------------------------------------------------------------~>\n");
                            System.exit(0);
                        }
                        else if(restart.equalsIgnoreCase("Y") || restart.equalsIgnoreCase("YES"))
                        {
                            displayOptions();
                            try
                            {
                                menuChoice = Integer.parseInt(userInput.nextLine());
                                System.out.println("||==================================================================================||");
                                System.out.println("<~----------------------------------------------------------------------------------~>\n");
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
                    }   //End of Default...
            }   //End of Switch-Case... 
        }   //End of While Loop...
    }   //End of Main...
    public static void displayOptions()
    {
        System.out.println("<~---------------Welcome----to--the---Computer---Network----Simulator---------------~>");
        System.out.println("||==================================================================================||");
        System.out.println("||                  Please enter one of the following options:                      ||");
        System.out.println("||==================================================================================||");
        System.out.println("|| (1): Determine the Lowest Latency Path between Switches in the Network           ||");
        System.out.println("|| (2): Determine whether the Network is Copper-Only Connected                      ||");
        System.out.println("|| (3): Find the Minimum Average Latency Spanning Tree for the Network              ||");
        System.out.println("|| (4): Determine whether or not the graph maintains Connectivity                   ||");
        System.out.println("|| (5): Exit the Simulation                                                         ||");
        System.out.println("||==================================================================================||");
        System.out.println("|| NOTE: Please be wary of erroneous input! Try to enter viable choices!            ||");
        System.out.println("<~----------------------------------------------------------------------------------~>");

        System.out.println("\n<~==================================================================================~>");
        System.out.print("||                                Select your option: ");

    }   
   /*  public static UnweightedEdge get_TreeSet(TreeSet<UnweightedEdge> set, UnweightedEdge uEdge) {
        if (set.contains(uEdge)) {
            return set.floor(uEdge);
        }
        return null;
    } */
}