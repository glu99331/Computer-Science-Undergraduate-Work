import java.util.*;
import java.io.*;
import java.util.Map.*;
/************************************************************************************
* ================ CS 1550: Project III, Virtual Memory Simulator. ================= *
* ---------------------------------------------------------------------------------- *
* Author: Gordon Lu																				                           *
* Term: Spring 2020, Dr.Mosse's Operating Systems class		                           *
* ---------------------------------------------------------------------------------- *
*************************************************************************************/
@SuppressWarnings("unchecked")
public class vmsim
{
  static int page_faults_opt = 0;
  static int page_faults_lru = 0;
  static int page_faults_second = 0;
  public static void main(String[] args) throws Exception
  {
    //Implement a Page Table for a 32-bit address space, all pages will be 4KB in size.
    //Number of frames will be a parameter to the execution of your program.
    
    // You will write a program called vmsim that takes the following command line arguments:
    // java vmsim –n <numframes> -a <opt|lru|second> <tracefile>
    // The program will then run through the memory references of the file and decide the action taken for
    // each address (hit, page fault – no eviction, page fault – evict clean, page fault – evict dirty).
    int numframes = 0;
    String page_replacement_algorithm = "";
    String tracefile = "";
    if(args.length == 5 && args[0].equals("-n") && args[2].equals("-a"))
    {
      numframes = Integer.parseInt(args[1]);
      page_replacement_algorithm = args[3];
      tracefile = args[4]; 
    }
    else
    {
      System.out.println("Error! Please enter the correct command line arguments!");
    }
    simulate_page_replacement_algorithm(page_replacement_algorithm, tracefile, numframes, true);
    //The below line will cause Gradescope to time out, that's because it simulates Second Chance on every file
    //with 2,...,100 frames, so it's no surprise that it should take a while!
    //Comment out the below line to help test for Belady's Anomaly for Second Chance
    //test_beladys_anomaly();
    //The below line will cause Gradescope to time out, that's because it simulates LRU, Second and OPT on every file
    //With 8, 16, 32, and 64 frames, of course it would take a while!
    //Comment out the below line to generate files to help generate graphs using python
    //generate_vmsim_results();
  }
  
  static void simulate_page_replacement_algorithm(String algorithm, String tracefile, int numFrames, boolean printResults) throws Exception
  {
    //Evicting a page happens by identifying the page to evict and writing the page to the disk (if
    //dirty), or abandoning the page (if clean)
    switch(algorithm)
    {
      case "opt":
      //Simulate optimal page replacement algorithm
      simulate_opt(tracefile, numFrames, printResults);
      break;
      
      case "second":
      //Simulate second change page replacement algorithm
      simulate_second_chance(tracefile, numFrames, printResults);
      break;
      
      case "lru":
      //Simulate least recently used page replacement algorithm
      simulate_lru(tracefile, numFrames, printResults);
    } //End of Switch-Case
  }
  /*************************Page Replacement Algorithms:**********************/
  /****************************************************************************
  *==================I. Optimal Page Replacement Algorithm:===================*
  *****************************************************************************
  * Algorithm: Replace the page that will be used furthest in the future      *
  * - Only works if we know the whole sequence!                               *
  * - Can be approximated by running the program twice                        *
  * - Once to generate the reference trace                                    *
  * - Once (or more) to apply the optimal algorithm                           *
  ****************************************************************************/
  static void simulate_opt(String tracefile, int numFrames, boolean printResults) throws Exception
  {
    //Track the total number of page faults encountered:
    int num_page_faults = 0; 
    //Track the total number of memory accesses: check the reference bit of each PTE we want to evict...
    int num_memory_accesses = 0;
    //Track the total number of writes to disk: check the dirty bit of each PTE we want to evict..
    int num_writes_to_disk = 0;
    //Calculate the page table size: first 5 hex digits contain the address, the rest is just the offset
    int page_table_size = (int)Math.pow(2, 20);
    //Create a corresponding array to represent the total RAM that the Page Table has:
    int[] RAM = new int[numFrames];
    //Create a corresponding TreeMap to represent  the Page Table has:
    //Key-Value pair will be the pageNumbers and the corresponding entry!
    TreeMap<Integer, PTE> pageTable = new TreeMap<Integer, PTE>();
    //TreeMap will store the  page that will be used in the future
    TreeMap<Integer, LinkedList<Integer>> futurePageTable = new TreeMap<Integer, LinkedList<Integer>>();
    //Create a TreeMap to represent parsed file content:
    TreeMap<Integer, int[]> pageReferences = new TreeMap<Integer, int[]>();
    //To read from the trace files:
    BufferedReader br = new BufferedReader(new FileReader(tracefile));
    //Fill up the RAM with empty frames!
    Arrays.fill(RAM, -1);
    //Fill up both the page table and the future page table!
    for(int page = 0; page < page_table_size; page++)
    {
      //Create a new page:
      PTE pageEntry = new PTE();
      //Insert the page entry into the page table:
      pageTable.put(page, pageEntry);
      //Insert the page entry into the future page table:
      futurePageTable.put(page, new LinkedList<Integer>());
    }
    //Keep a count of the current page frame that we're looking at:
    int numPagesRead = 0;
    //Read from trace file:
    while(br.ready())
    {
      //Extract the current line:
      String line = br.readLine();
      //Split the lines, delimited by spaces:
      String[] parts = line.split(" ");
      //Create a new int[] to store each of the parsed pieces:
      int[] parsedMemory = new int[2];
      //Now assign each of the parts of the memory reference:
      //Access mode will be the char in ASCII:
      parsedMemory[0] = (int)parts[0].charAt(0);
      //The first 5 hex digits represent the actual page number, the other 3 hex digits are just the offset
      parsedMemory[1] = Integer.parseInt(parts[1].substring(2, 7), 16);
      //Add the updated array along with the current page number we're at:
      pageReferences.put(numPagesRead, parsedMemory);
      //Add this page number into the future page table:
      futurePageTable.get(parsedMemory[1]).add(numPagesRead);
      //Increment the number of pages we've seen so far:
      numPagesRead++;
    }
    //Simply traverse through the Tree Map, no need to scan through the file again:
    //Keep a track of the frames that have not been evicted:
    int num_frames_not_evicted = 0;
    for(int page = 0; page < numPagesRead; page++)
    {
      //Increment the number of memory accesses:
      num_memory_accesses++;
      //Read file contents from the pageReferences map:
      int[] pageReference_array = pageReferences.get(page);
      //Fetch the page number!
      int pageNumber = pageReference_array[1];
      //Cast from ASCII back into a char!
      char accessType = (char)pageReference_array[0];
      //Fetch the corresponding page table entry from the page number:
      PTE currentPTE = pageTable.get(pageNumber);
      //Set the PTE's reference bit to true, since we just accessed the page!
      currentPTE.set_referenced_bit(1); //1 means the page has been accessed!
      //Check if our future page table already contains the key, if so, remove it, so we can put it in...
      if(futurePageTable.containsKey(pageNumber))
      {
        futurePageTable.get(pageNumber).remove(0);
      }
      
      //Check the mode of the bit, if it's a store, then change the dirty bit!
      if(accessType == 's')
      {
        currentPTE.set_dirty_bit(1); //1 means the page has been modified!
      }
      //Now we need to determine if we need to evict a page!
      //We can tell if we need to evict a page by looking at its valid bit!
      if(currentPTE.get_valid_bit() == 1)
      {
        //System.out.println("Page " + pageNumber + ": Hit: No page fault.");
      }
      //Otherwise, if the valid bit is 0, we have a page fault, and we have to check if
      //we need to evict someone!
      else if(currentPTE.get_valid_bit() == 0)
      {
        //We have a page fault, so increment the number of page faults encountered:
        num_page_faults++;
        //If the number of non-evicted frames has not reached the maximum number of frames,
        //we don't need to evict:
        if(num_frames_not_evicted < numFrames)
        {
          //System.out.println("Page " + pageNumber + ": Page fault - No eviction.");
          //Add the frame into the RAM:
          RAM[num_frames_not_evicted] = pageNumber;
          //Update the PTE's frame number:
          currentPTE.set_page_frame_number(num_frames_not_evicted);
          //Since the frame is now in our table, update the PTE's valid bit:
          currentPTE.set_valid_bit(1);
          //Increment the number of non-evicted frames encountered:
          num_frames_not_evicted++;
        }
        //Otherwise, since we've reached our maximum capacity, we need to evict a page!
        else if(num_frames_not_evicted >= numFrames)
        {
          //find the page that will be used furthest in the future
          int pageNumber_toEvict = furthestFuturePage(RAM, futurePageTable);
          //Fetch the corresponding frame:
          PTE page_to_evict = pageTable.get(pageNumber_toEvict);
          
          //Check if the page is dirty:
          if(page_to_evict.get_dirty_bit() == 1)
          {
            //If the page has been modified (is dirty), increment the number of writes to disk!
            num_writes_to_disk++;
            //System.out.println("Page " + pageNumber_toEvict + ": Page fault - Eviction, dirty.");
          }
          else if(page_to_evict.get_dirty_bit() == 0)
          {
            //If the page has not been modified (is clean), just print out a message!
            //System.out.println("Page " + pageNumber_toEvict + ": Page fault - Eviction, clean.");
          }
          //Swap out the evicted frame with the frame we want to add!
          RAM[page_to_evict.get_page_frame_number()] = pageNumber;
          //Swap out our page to evict with our current page
          currentPTE.set_page_frame_number(page_to_evict.get_page_frame_number());
          //Set the current PTE's valid bit as a valid entry in the page table!
          currentPTE.set_valid_bit(1);
          //Evict our page, by resetting all of its instance fields
          page_to_evict.set_referenced_bit(0);
          page_to_evict.set_dirty_bit(0);
          page_to_evict.set_valid_bit(0);
          page_to_evict.set_page_frame_number(-1);
          //Reset the page table mapping between the corresponding page number
          //and the reset eviction page
          pageTable.put(pageNumber_toEvict, page_to_evict);
        }
      }
      //Add the page entry into the page table
      pageTable.put(pageNumber, currentPTE);
    }
    page_faults_opt = num_page_faults;

    if(printResults)
    {
      printSummaryStatistics("OPT", numFrames, num_memory_accesses, num_page_faults, num_writes_to_disk);  
    }
  }


  /****************************************************************************
  *============III. Least Recently Used Page Replacement Algorithm:===========*
  *****************************************************************************
  * Algorithm: Evict the least recently used page.                            *
  * - Throw out page that has been unused for longest time                    *
  * - Must keep a linked list of pages                                        *
  *   a) Most recently used at front, least at rear                           *
  *   b) Update this list every memory reference!                             *
  *     - This can be somewhat slow: hardware has to update a linked list     *
  *     on every reference!                                                   *
  * - Alternatively, keep a counter in each Page Table Entry:                 *
  *   a) Global counter increments with each CPU cycle                        *
  *   b) Copy global counter to PTE counter on a reference to the page        *
  *   c) For page replacement, evict the page with the lowest counter value   *
  ****************************************************************************/
  static void simulate_lru(String tracefile, int numFrames, boolean printResults) throws IOException
  {  
    //Track the total number of page faults encountered:
    int num_page_faults = 0; 
    //Track the total number of memory accesses: check the reference bit of each PTE we want to evict...
    int num_memory_accesses = 0;
    //Track the total number of writes to disk: check the dirty bit of each PTE we want to evict..
    int num_writes_to_disk = 0;
    //Calculate the page table size: first 5 hex digits contain the address, the rest is just the offset
    int page_table_size = (int)Math.pow(2, 20);
    //Create a corresponding TreeMap to represent  the Page Table has:
    //Key-Value pair will be the pageNumbers and the corresponding entry!
    LinkedHashMap<String, PTE> pageTable = new LinkedHashMap<String, PTE>();
    //To read from the trace files:
    BufferedReader br = new BufferedReader(new FileReader(tracefile));
    //Record where the minimum PTE based on memory references is located!
    int global_references = 0; //Global references, each page will receive this updated global reference upon each memory reference!
    //Read from trace file:
    while(br.ready())
    {
      //Increment the number of memory accesses:
      num_memory_accesses++;
      //Extract the current line:
      String line = br.readLine();
      //Split the lines, delimited by spaces:
      String[] pageReference_array = line.split(" ");
      //Now assign each of the parts of the memory reference:
      //Access mode will be a char:
      char accessType = pageReference_array[0].charAt(0);
      String pageNum = pageReference_array[1].substring(0, 7);
      //Fetch the corresponding page table entry from the page number:
      if(pageTable.containsKey(pageNum))
      {
        PTE current = pageTable.get(pageNum);
        //Update reference bit
        current.set_referenced_bit(1);
        global_references++;
        //Set dirty bit depending on the instruction:
        if(accessType == 's') current.set_dirty_bit(1);
        
        //If it's already in our page table:
        //Increment the number of references:
        current.set_num_references(global_references);
        //Put it back into the table:
        pageTable.put(pageNum, current);
      
      }
      else
      {
        //increment page faults
        num_page_faults++;
        //If we don't need to evict a frame:
        if(pageTable.size() < numFrames)
        {
          PTE newEntry = new PTE();
          if(accessType == 's') newEntry.set_dirty_bit(1);
          //Update reference bit
          //newEntry.set_referenced_bit(1);
          newEntry.set_valid_bit(1); //We're about to put a page into our pageTable, so change the valid bit!
          //Increment the number of references:
          global_references++;
          newEntry.set_num_references(global_references);
          //Put it back into the table:
          pageTable.put(pageNum, newEntry);
        }
        else
        {
          //Get the PTE we want to insert:
          PTE current = new PTE();
          //Increment the number of references:
          global_references++;
          current.set_num_references(global_references);    
          if(accessType == 's') current.set_dirty_bit(1);
          //We need to evict someone:          
          //Temporary map to sort elements based on the global references with each page!
          Map<String, PTE> lruMap = lruComparator(pageTable);
          //Have pageTable take on all Key, Value Pairs from this map!
          pageTable = new LinkedHashMap<String, PTE>(lruMap);
          
          //Evict the page at the end of the map: this will be our LRU page!
          Map.Entry<String, PTE> eviction_page = (Map.Entry<String, PTE>)pageTable.entrySet().toArray()[pageTable.size() - 1];
          //Get the address associated with this page:
          String page_number_to_evict = eviction_page.getKey();
          PTE page_to_evict = pageTable.get(page_number_to_evict);
          
          //Check if the evicted page has been modified:
          if(pageTable.get(page_number_to_evict).get_dirty_bit() == 1)
          {
            num_writes_to_disk++;
          }
          //Remove the evicted page:
          pageTable.remove(page_number_to_evict);
          //Add the new entry into the linked hash map:
          pageTable.put(pageNum, current);
        
        }
      }
        
      }
    page_faults_lru = num_page_faults;
    if(printResults)
    {
      printSummaryStatistics("LRU", numFrames, num_memory_accesses, num_page_faults, num_writes_to_disk);

    }    
  }
  /****************************************************************************
  *===============II. Second Chance Page Replacement Algorithm:===============*
  *****************************************************************************
  * Algorithm: Modify FIFO to avoid throwing out heavily used pages!          *
  * - If the reference bit is 0, throw the page out                           *
  * - If the reference bit is 1:                                              *
  *   a) Move the page to the tail of the list                                *
  *   b) Continue to search for a free page                                   * 
  ****************************************************************************/
  static void simulate_second_chance(String tracefile, int numFrames, boolean printResults) throws IOException
  {
    //Track the total number of page faults encountered:
    int num_page_faults = 0; 
    //Track the total number of memory accesses: check the reference bit of each PTE we want to evict...
    int num_memory_accesses = 0;
    //Track the total number of writes to disk: check the dirty bit of each PTE we want to evict..
    int num_writes_to_disk = 0;
    //Key-Value pair will be the pageNumbers and the corresponding entry!
    LinkedHashMap<String, PTE> pageTable = new LinkedHashMap<String, PTE>();
    ArrayList<PTE> clock_page_map = new ArrayList<PTE>();
    ArrayList<String> clock_address_map = new ArrayList<String>();
    //To read from the trace files:
    BufferedReader br = new BufferedReader(new FileReader(tracefile));
    //Second chance: so we know where we left off
    int clock_pointer = 0;
    //Read from trace file:
    while(br.ready())
    {
      //Increment the number of memory accesses:
      num_memory_accesses++;
      //Extract the current line:
      String line = br.readLine();
      //Split the lines, delimited by spaces:
      String[] pageReference_array = line.split(" ");
      //Now assign each of the parts of the memory reference:
      //Access mode will be a char:
      char accessType = pageReference_array[0].charAt(0);
      String pageNum = pageReference_array[1].substring(0,7);
      //Fetch the corresponding page table entry from the page number:
      //If the map contains the page number already, just modify the referenced bit:
      if(pageTable.containsKey(pageNum))
      {
        int clock_index = clock_address_map.indexOf(pageNum);
        PTE current = pageTable.get(pageNum);
        PTE curr_clock = clock_page_map.get(clock_index);
        
        //Update reference bit
        //current.set_referenced_bit(1);
        //Set dirty bit depending on the instruction:
        curr_clock.set_referenced_bit(1);
        current.set_referenced_bit(1);
        if(accessType == 's') 
        {
          current.set_dirty_bit(1);
          curr_clock.set_dirty_bit(1);
        }
        
      
      }
      else
      {
        //increment page faults
        num_page_faults++;
        //If we don't need to evict a frame:
        if(pageTable.size() < numFrames && clock_page_map.size() < numFrames && clock_address_map.size() < numFrames)
        {
          PTE newEntry = new PTE();
          if(accessType == 's') 
          {
            newEntry.set_dirty_bit(1);
            newEntry.set_referenced_bit(1);
          }
              
          //Update reference bit
          //newEntry.set_referenced_bit(1);
          newEntry.set_valid_bit(1); //We're about to put a page into our pageTable, so change the valid bit!
          pageTable.put(pageNum, newEntry);
          clock_page_map.add(clock_pointer, newEntry);
          clock_address_map.add(clock_pointer, pageNum);
          clock_pointer++;

        }
        else if (clock_page_map.size() == numFrames && clock_address_map.size() == numFrames && pageTable.size() == numFrames)
        {
          //Get the PTE we want to insert:
          PTE current = new PTE();
          
          //Update reference bit
          //current.set_referenced_bit(1);
          //We need to evict someone:
          String page_number_to_evict = null;
          
          boolean found_page = false;
          // for(Map.Entry<String, PTE> entry : pageTable.entrySet()) 
          // {
          // 
          //   System.out.println("Current page is: " + entry);
          // }
          while(found_page == false)
          {
            //for(int i = curr_entry; i < pageTable.size(); i++)
            //for(Map.Entry<String, PTE> entry : pageTable.entrySet()) 
            for(int i = 0; i < pageTable.size() && i < clock_page_map.size() && i < clock_address_map.size(); i++)
            {
              if (clock_pointer == clock_page_map.size() && clock_pointer == pageTable.size())
                  clock_pointer = 0;
              if (clock_page_map.get(clock_pointer).get_referenced_bit() == 1){
                  
                  clock_page_map.get(clock_pointer).set_referenced_bit(0);
                  
                  pageTable.get(clock_address_map.get(clock_pointer)).set_referenced_bit(0);
                  clock_pointer++;
                  if (clock_pointer == clock_page_map.size() && clock_pointer == pageTable.size())
                      clock_pointer = 0;
              } else {
                  found_page = true;
                  page_number_to_evict = clock_address_map.get(clock_pointer);
                  break;                                          //Dont continue the search 
              }
              
            }
          }

        
          //Remove the evicted page:
          if(pageTable.get(page_number_to_evict).get_dirty_bit() == 1 && clock_page_map.get(clock_address_map.indexOf(page_number_to_evict)).get_dirty_bit() == 1)
          {
            num_writes_to_disk++;
          }
          pageTable.remove(page_number_to_evict);
          clock_page_map.remove(clock_pointer);
          clock_address_map.remove(clock_pointer);
        
          //Add the new entry into the linked hash map:
          if(accessType == 's') 
          {
            current.set_dirty_bit(1);
            current.set_referenced_bit(0);
          }          
          pageTable.put(pageNum, current);
          clock_page_map.add(clock_pointer, current);
          clock_address_map.add(clock_pointer, pageNum);
          clock_pointer++;
      
        }
      }
      }
    page_faults_second = num_page_faults;
    if(printResults)
    {
      printSummaryStatistics("SECOND", numFrames, num_memory_accesses, num_page_faults, num_writes_to_disk);

    }
  }
  //Display Summary Statistics:
  static void printSummaryStatistics(String algorithm, int numFrames, int num_memory_accesses, int num_page_faults, int num_writes_to_disk)
  {
    System.out.println("Algorithm: " + algorithm);
    System.out.println("Number of frames: " + numFrames);
    System.out.println("Total memory accesses: " + num_memory_accesses);
    System.out.println("Total page faults: " + num_page_faults);
    System.out.println("Total writes to disk: " + num_writes_to_disk);
    //Format the Strings:
    // String formattedAlgorithm = String.format("||\tAlgorithm: %-24s%s\n", algorithm, "||");
    // String formattedFrames = String.format("||\tNumber of frames:\t%-11d%s\n", numFrames, "||");
    // String formattedMemoryAccesses = String.format("||\tTotal memory accesses:\t%-11d%s\n", num_memory_accesses, "||");
    // String formattedPageFaults = String.format("||\tTotal page faults:\t%-11d%s\n", num_page_faults, "||");
    // String formattedWrites = String.format("||\tTotal writes to disk:\t%-11d%s\n", num_writes_to_disk, "||");
    // //Now print them out, aesthetically!
    // System.out.println("<===========Summary====Statistics===========>");
    // System.out.print(formattedAlgorithm);
    // System.out.print(formattedFrames);
    // System.out.print(formattedMemoryAccesses);
    // System.out.print(formattedPageFaults);
    // System.out.print(formattedWrites);
    // System.out.println("<===========================================>");
  }

  //Search for the furthest future page from our current page:
  static int furthestFuturePage(int[] RAM, TreeMap<Integer, LinkedList<Integer>> futurePageTable)
  {
    int page_to_evict = 0;
    // int current_page_number = 0;
    int furthest_page = 0;
    //Loop through all of the page numbers within RAM, and find the one with the furthest distance
    for(int page = 0; page < RAM.length; page++)
    {
      //compare our current page to our future page
      int current_page_number = RAM[page];
      //If there is a single entry, then simply return that corresponding future entry, since we want to evict that page
      if(futurePageTable.get(current_page_number).size() == 0)
      {
        //page to evict will be our current page number!
        page_to_evict = current_page_number;
        break;
      }
      else
			{
        //Otherwise, continually traverse the future page table's linked list, until we find the future page
        //with the same page number, with the furthest distance from our current page
				if(futurePageTable.get(current_page_number).get(0) > furthest_page)
				{
          //If there's a tie, get the first element, or least recently used
					furthest_page = futurePageTable.get(current_page_number).get(0);
					page_to_evict = current_page_number;
				}
			}
    }
    return page_to_evict;
  }

private static void generate_vmsim_results() throws Exception
{
  //Simulate algorithms for report: performance gets slower by like 10 seconds 
  generate_vmsim_results("gcc.trace");
  generate_vmsim_results("gzip.trace");
  generate_vmsim_results("swim.trace");

}

private static void generate_vmsim_results(String traceFile) throws Exception
{ 
  StringBuilder opt_results = new StringBuilder();
  simulate_page_replacement_algorithm("opt", traceFile, 8, false);
  opt_results.append("OPT:" + page_faults_opt + " ");
  simulate_page_replacement_algorithm("opt", traceFile, 16, false);
  opt_results.append(page_faults_opt + " ");
  simulate_page_replacement_algorithm("opt", traceFile, 32,false);
  opt_results.append(page_faults_opt + " ");
  simulate_page_replacement_algorithm("opt", traceFile, 64, false);
  opt_results.append(page_faults_opt + "\n");

  String new_traceFile = traceFile.replace(".trace","");
  PrintWriter pw = new PrintWriter(new_traceFile + "_results.txt");
  pw.print(opt_results.toString());
  

  StringBuilder lru_results = new StringBuilder();
  simulate_page_replacement_algorithm("lru", traceFile, 8, false);
  lru_results.append("LRU:" + page_faults_lru + " ");
  simulate_page_replacement_algorithm("lru", traceFile, 16, false);
  lru_results.append(page_faults_lru + " ");
  simulate_page_replacement_algorithm("lru", traceFile, 32,false);
  lru_results.append(page_faults_lru + " ");
  simulate_page_replacement_algorithm("lru", traceFile, 64, false);
  lru_results.append(page_faults_lru + "\n");

  pw.print(lru_results.toString());

  StringBuilder second_results = new StringBuilder();
  simulate_page_replacement_algorithm("second", traceFile, 8, false);
  second_results.append("Second:" + page_faults_second + " ");
  simulate_page_replacement_algorithm("second", traceFile, 16, false);
  second_results.append(page_faults_second + " ");
  simulate_page_replacement_algorithm("second", traceFile, 32,false);
  second_results.append(page_faults_second + " ");
  simulate_page_replacement_algorithm("second", traceFile, 64, false);
  second_results.append(page_faults_second + "\n");

  pw.print(second_results.toString());
  pw.close();


}

static void test_beladys_anomaly() throws Exception
{
  PrintWriter pw = new PrintWriter("beladys.txt");
  test_beladys_anomaly("gcc.trace", pw);
  test_beladys_anomaly("gzip.trace", pw);
  test_beladys_anomaly("swim.trace", pw);
  pw.close();

}
static void test_beladys_anomaly(String traceFile, PrintWriter pw) throws Exception
{
  String new_traceFile = traceFile.replace(".trace","");


  StringBuilder second_results = new StringBuilder();
  second_results.append(new_traceFile + ":");

  for(int i = 2;i <= 100;)
  {
    
    simulate_page_replacement_algorithm("second", traceFile, i, false);    
    second_results.append(page_faults_second + " ");
    i +=2;

  }
  second_results.append("\n");
  pw.print(second_results.toString());
}
//Another method to handle LRU: Comparators!
private static Map<String, PTE> lruComparator(Map<String, PTE> unsortMap)
{

    List<Entry<String, PTE>> list = new LinkedList<Entry<String, PTE>>(unsortMap.entrySet());

    // Sorting the list based on values
    Collections.sort(list, new Comparator<Entry<String, PTE>>()
    {
      public int compare(Entry<String, PTE> o1, Entry<String, PTE> o2)
      {
        return o2.getValue().compareTo(o1.getValue());
      }
    });

  // Maintaining insertion order with the help of LinkedList
  Map<String, PTE> sortedMap = new LinkedHashMap<String, PTE>();
  for (Entry<String, PTE> entry : list)
  {
    sortedMap.put(entry.getKey(), entry.getValue());
  }
  return sortedMap;
}
}