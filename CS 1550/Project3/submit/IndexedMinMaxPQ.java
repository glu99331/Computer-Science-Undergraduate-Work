import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*********************************************
 * Combines both IndexMinPQ and IndexMaxPQ   *
 * ========================================= *
 * Author: Gordon Lu                         *
 * Created for: CS 1501 Advanced PQ Project  *
 * ========================================= *
 * [UPDATE]: Modified for CS 1550 Project 3. *
 * ========================================= *
 * 1) Implements PTEs, by frequency of       *
 * accesses for each page.                   *
 * 2) Upon each insertion, swaps minimum PTE *
 * with the index at the end.                *
 * 3) Implements resizing of each array:     *
 * pq, qp, and the pageEntries array are all *
 * resized when the maximum capacity is      *
 * reached.                                  *
 *********************************************/
 
//Inherited from IndexMinPQ and IndexMaxPQ
public class IndexedMinMaxPQ <Key extends Comparable <Key>> implements Iterable<Integer>
{   
    private int maxN = 100;        // maximum number of elements on PQ
    private int n;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private PTE[] pageEntries;      // pageEntries[i] = priority of i
    private String sentinelKey;     //Indicates if working with minPQ or maxPQ
    
    public IndexedMinMaxPQ(String sentinelKey, int maxN)
    {
        this.sentinelKey = sentinelKey;
        switch(sentinelKey.toUpperCase())
        {
            case "MAX":
            //Code for IndexMaxPQ Constructor
                if (maxN < 0) throw new IllegalArgumentException();
                this.maxN = maxN;
                n = 0;
                pageEntries = new PTE[maxN + 1];    // make this of length maxN??
                pq   = new int[maxN + 1];                   //pq corresponds to indices in the heap??
                qp   = new int[maxN + 1];                   // qp corresponds to the position of "i" in the pq
                for (int i = 0; i <= maxN; i++)
                    qp[i] = -1;
            break;

            case "MIN":
            //Code for IndexMinPQ Constructor
                if (maxN < 0) throw new IllegalArgumentException();
                this.maxN = maxN;
                n = 0;
                pageEntries = new PTE[maxN + 1];    // make this of length maxN??
                pq   = new int[maxN + 1];
                qp   = new int[maxN + 1];                   // make this of length maxN??
                for (int i = 0; i <= maxN; i++)
                    qp[i] = -1;
            break;

            default:
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' to indicate initialization to IndexMaxPQ or IndexMinPQ!");
            break;
        }
    }

    public boolean isEmpty()
    {
        return n == 0;
    }
    
    public boolean contains(int i)
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            return qp[i] != -1;  
        }
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            return qp[i] != -1;
        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
            return false;
        }
          
    }

    public int size()
    {
        return n;
    }
    
    public String getSentinelKey()
    {
        return sentinelKey;
    }

    public void insert(int i, PTE pte)
    {
        
        switch(sentinelKey.toUpperCase())
        {
            case "MAX":
            //Slightly modified code from IndexMaxPQ
            if (i < 0 || i >= maxN) //Instead of throwing an index out of bounds exception, resize all arrays:
            {
                qp = Arrays.copyOf(qp, 2 * qp.length);
                pq = Arrays.copyOf(pq, 2 * pq.length);
                pageEntries = Arrays.copyOf(pageEntries, 2 * pageEntries.length);
            }
            //throw new IndexOutOfBoundsException();
            if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
            n++;
            qp[i] = n;
            pq[n] = i;
            pageEntries[i] = pte;
            percolateUp(n);

            //Once we've finished percolating up the heap, we should swap the min index with i.
            //In our implementation of LRU, i should also indicate the current capacity of our heap:
            //Therefore, we should just be looking for the minimum and swapping i with that minimum index:
            int minPTE = pageEntries[0].get_memory_references();
            int minIndex = 0;
            int endIndex = 0;
            for(int currPTE = 0; currPTE < pageEntries.length; currPTE++)
            {
                //Increment the end index until we reach a page that isn't in the page table... 
                if(pageEntries[currPTE].get_valid_bit() == 1)
                {
                    endIndex++;
                }
                //If the minPTE is less than the number of accesses of the current page, and the page is in the table...
                if(minPTE < pageEntries[currPTE].get_memory_references() && pageEntries[currPTE].get_valid_bit() == 1)
                {
                    minPTE = pageEntries[currPTE].get_memory_references();
                    minIndex = currPTE;
                }
            }
            //Now swap with i:  
            PTE temp = pageEntries[endIndex];
            pageEntries[endIndex] = pageEntries[minIndex];
            pageEntries[minIndex] = temp;
            
            //Swap the indices in the qp and the pq arrays too:
            //Call the exchange function: Swap the minimum and end indices
            exch(minIndex, endIndex);
            //Should we do the same with the PQ and the QP arrays?
            //Possibly not, since we only really care about the key array. Even though the pq and qp arrays will not necessarily maintain the
            //same order as the key array, we still have the maximum at the top. That's all that really matters.
            
            break;

            case "MIN":
            //Slightly modified code from IndexMinPQ
            if (i < 0 || i >= maxN) //Instead of throwing an index out of bounds exception, resize all arrays:
            {
                qp = Arrays.copyOf(qp, 2 * qp.length);
                pq = Arrays.copyOf(pq, 2 * pq.length);
                pageEntries = Arrays.copyOf(pageEntries, 2 * pageEntries.length);
            }
            if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
            n++;
            qp[i] = n;
            pq[n] = i;
            pageEntries[i] = pte;
            percolateUp(n);
            
            break;

            default:
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
            break;
        }
    }

    public int maxIndex()
    {
        if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
                return pq[1];
        }
        else
        {
            System.out.println("Error. Cannot invoke maxIndex.\nMust invoke maxIndex on an Indexable Max PQ....");
            return -1;
        }
        
    }

    public int minIndex() 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
                return pq[1];
        }
        else
        {
            System.out.println("Error. Cannot invoke minIndex.\nMust invoke minIndex on an Indexable Min PQ....");
            return -1;
        }   
        
    }

    public PTE maxPageAccesses() 
    {
        if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
                //System.out.println(apartmentComplex[pq[1]]); Checks it fine
                return pageEntries[pq[1]];
        }
        else
        {
            System.out.println("Error. Cannot invoke maxPageAccesses.\nMust invoke maxPageAccesses on an Indexable Max PQ....");
            return null;
        }
        
    }

    public PTE minPageAccesses() 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
                return pageEntries[pq[1]];
        }
        else
        {
            System.out.println("Error. Cannot invoke minPageAccesses.\nMust invoke minPageAccesses on an Indexable Min PQ....");
            return null;
        }
    }
    public void printPTEs()
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            for(int i = 1; i <= n; i++)
            {
                System.out.println(pageEntries[pq[i]].get_memory_references());
            }
        }
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            for(int i = 1; i <= n; i++)
            {
                System.out.println(pageEntries[pq[i]].get_memory_references());
            }
        }
        
    }
    

   
    public int delMin() 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
            int min = pq[1];
            exch(1, n--);
            heapifyDown(1);
            assert min == pq[n+1];
            qp[min] = -1;        // delete
            pageEntries[min] = null;    // to help with garbage collection
            pq[n+1] = -1;        // not needed
            return min;
        }
        else
        {
            System.out.println("Error. Cannot invoke delMin.\nCan only be invoked Indexable Min PQs....");
            return -1;
        }
        
    }

     public int delMax() {
        if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
            int min = pq[1];
            exch(1, n--);
            heapifyDown(1);

            assert pq[n+1] == min;
            qp[min] = -1;        // delete
            pageEntries[min] = null;    // to help with garbage collection
            pq[n+1] = -1;        // not needed
            return min;
        }
        else
        {
            System.out.println("Error. Cannot invoke delMax.\nCan only be invoked Indexable Max PQs....");
            return -1;
        }
    }

    public void delete(int i) 
    {
        if(sentinelKey.equals("MAX"))
        {
            //if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            int index = qp[i]; 
            exch(index, n--); 
            percolateUp(index);
            heapifyDown(index);
            pageEntries[i] = null;
            qp[i] = -1;

        }
        else if(sentinelKey.equals("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            //if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            int index = qp[i];
            exch(index, n--);
            percolateUp(index);
            heapifyDown(index);
            pageEntries[i] = null;
            qp[i] = -1; 
        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
        }
        
    }



    public PTE PTEof(int i)
    {
        if(sentinelKey.equals("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
                else return pageEntries[i];
        }
        else if(sentinelKey.equals("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            else return pageEntries[i];
        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
            return null;
        }
    }

    public void changeKey(int i, PTE pte) 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            pageEntries[i] = pte;
            percolateUp(qp[i]);
            heapifyDown(qp[i]);
        }
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            pageEntries[i] = pte;
            percolateUp(qp[i]);
            heapifyDown(qp[i]);
        }
        
    }

    public void decreaseKey(int i, PTE pte) 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (pageEntries[i].get_memory_references() <= pte.get_memory_references())
                throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
                pageEntries[i] = pte;
            percolateUp(qp[i]);
        }
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (pageEntries[i].get_memory_references() <= pte.get_memory_references())
                throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
                pageEntries[i] = pte;
            heapifyDown(qp[i]);
        }
    }

    public void increaseKey(int i, PTE pte) 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (pageEntries[i].get_memory_references() >= pte.get_memory_references())
                throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
            pageEntries[i] = pte;
            heapifyDown(qp[i]);
        }   
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (pageEntries[i].get_memory_references() >= pte.get_memory_references())
                throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
            pageEntries[i] = pte;
            percolateUp(qp[i]);
        }  
    }

    public void mutateAccesses(int i, int new_references) 
    {
        if(sentinelKey.equals("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            pageEntries[i].set_num_references(new_references);;
            percolateUp(qp[i]);
            heapifyDown(qp[i]);
        }
        else if(sentinelKey.equals("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            pageEntries[i].set_num_references(new_references);
            percolateUp(qp[i]);
            heapifyDown(qp[i]);
        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
        }
    }
 
    /***************************************************************************
    * General helper functions.
    ***************************************************************************/
    //THIS IS THE PROBLEM
    private boolean less(int i, int j) 
    {
        if(sentinelKey.equals("MAX"))
        {
            return pageEntries[pq[i]].get_memory_references() < (pageEntries[pq[j]]).get_memory_references();
        }
        else if(sentinelKey.equals("MIN"))
        {
            return pageEntries[pq[i]].get_memory_references() > (pageEntries[pq[j]]).get_memory_references();
        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
            return false;
        }
        //
    }

    private void exch(int i, int j) 
    {
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp[pq[i]] = i;
        qp[pq[j]] = j;

    }

   /***************************************************************************
    * Heap helper functions.
    ***************************************************************************/
    private void percolateUp(int k) 
    {

        while (k > 1 && less(k/2, k)) 
        {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void heapifyDown(int k) 
    {
        while (2*k <= n) 
        {
            int j = 2*k;
            if (j < n && less(j, j+1)) j++;
            if (!less(k, j)) break;
            exch(k, j);
            k = j;
        }


    }


    //Class must override the iterator method in order to implement the Iterator class.

    /**
     * Returns an iterator that iterates over the apts on the
     * priority queue in descending order.
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the apts in descending order
     */
    public Iterator<Integer> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Integer> {
        // create a new pq
        private IndexedMinMaxPQ copy;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no apts move
        public HeapIterator() {
            if(sentinelKey.equals("MAX"))
            {
                copy = new IndexedMinMaxPQ<PTE>("MAX", pq.length - 1);
                for (int i = 1; i <= n; i++)
                    copy.insert(pq[i],pageEntries[pq[i]]);
            }
            else if(sentinelKey.equals("MIN"))
            {
                copy = new IndexedMinMaxPQ<PTE>("MIN", pq.length - 1);
                for (int i = 1; i <= n; i++)
                    copy.insert(pq[i], pageEntries[pq[i]]);
            }
        }

        public boolean hasNext()
        { 
            return !copy.isEmpty();                     
        }
        public void remove() 
        {
            throw new UnsupportedOperationException();  
        }

        public Integer next()
        {
            Integer returnValue = 0;
            if(sentinelKey.equals("MAX"))
            {
                if (!hasNext()) throw new NoSuchElementException();
                returnValue =  copy.delMax();
            }
            else if(sentinelKey.equals("MIN"))
            {
                if (!hasNext()) throw new NoSuchElementException();
                returnValue = copy.delMin();
            }
            return returnValue;
        }
    }
}