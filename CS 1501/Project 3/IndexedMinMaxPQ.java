import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Arrays;

/*********************************************
 * Combines both IndexMinPQ and IndexMaxPQ   *
 * ========================================= *
 * Author: Gordon Lu                         *
 * Created for: CS 1501 Advanced PQ Project  * 
 * ========================================= *
 *                                           *
 *********************************************/
//Inherited from IndexMinPQ and IndexMaxPQ
public class IndexedMinMaxPQ <Key extends Comparable <Key>> implements Iterable<Integer>
{   
    private int maxN = 100;        // maximum number of elements on PQ
    private int n;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Apartment[] apartmentComplex;      // apts[i] = priority of i
    private String sentinelKey;     //Indicates if working with minPQ or maxPQ
    private String query_by_city;   //Indicates if querying by city or not!
    
    public IndexedMinMaxPQ(String sentinelKey, String query_by_city, int maxN)
    {
        this.sentinelKey = sentinelKey;
        this.query_by_city = query_by_city;
        switch(sentinelKey.toUpperCase())
        {
            case "MAX":
            //Code for IndexMaxPQ Constructor
                if (maxN < 0) throw new IllegalArgumentException();
                this.maxN = maxN;
                n = 0;
                apartmentComplex = new Apartment[maxN + 1];    // make this of length maxN??
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
                apartmentComplex = new Apartment[maxN + 1];    // make this of length maxN??
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

    public void insert(int i, Apartment apt)
    {
        
        switch(sentinelKey.toUpperCase())
        {
            case "MAX":
            //Slightly modified code from IndexMaxPQ
            // if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
            // if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
            if (i < 0 || i >= maxN) //Instead of throwing an index out of bounds exception, resize all arrays:
            {
                maxN = (maxN * 2) - 1;
                qp = Arrays.copyOf(qp, maxN + 1);
                pq = Arrays.copyOf(pq, maxN + 1);
                apartmentComplex = Arrays.copyOf(apartmentComplex, maxN + 1);
            }
            n++;
            qp[i] = n;
            pq[n] = i;
            apartmentComplex[i] = apt;
            percolateUp(n);
            break;

            case "MIN":
            //Slightly modified code from IndexMinPQ
            // if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
            if (i < 0 || i >= maxN) //Instead of throwing an index out of bounds exception, resize all arrays:
            {
                maxN = (maxN * 2) - 1;
                qp = Arrays.copyOf(qp, maxN + 1);
                pq = Arrays.copyOf(pq, maxN + 1);
                apartmentComplex = Arrays.copyOf(apartmentComplex, maxN + 1);
            }
            n++;
            qp[i] = n;
            pq[n] = i;
            apartmentComplex[i] = apt;
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

    public Apartment maxAptSize() 
    {
        if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
                //System.out.println(apartmentComplex[pq[1]]); Checks it fine
                return apartmentComplex[pq[1]];
        }
        else
        {
            System.out.println("Error. Cannot invoke maxAptSize.\nMust invoke maxAptSize on an Indexable Max PQ....");
            return null;
        }
        
    }

    public Apartment minAptPrice() 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
                return apartmentComplex[pq[1]];
        }
        else
        {
            System.out.println("Error. Cannot invoke minAptPrice.\nMust invoke minAptPrice on an Indexable Min PQ....");
            return null;
        }
    }
    public void printApartments()
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            for(int i = 1; i <= n; i++)
            {
                System.out.println(apartmentComplex[pq[i]].getRent());
            }
        }
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            for(int i = 1; i <= n; i++)
            {
                System.out.println(apartmentComplex[pq[i]].getSize());
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
            apartmentComplex[min] = null;    // to help with garbage collection
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
            apartmentComplex[min] = null;    // to help with garbage collection
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
            //System.out.println("asahduiashApartment is: " + apartmentComplex[1].getAptCity());
            apartmentComplex[i] = null;
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
            apartmentComplex[i] = null;
            qp[i] = -1; 
        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
        }
        
    }



    public Apartment PQApartmentIndexOf(int i)
    {
        if(sentinelKey.equals("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
                else return apartmentComplex[i];
        }
        else if(sentinelKey.equals("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            else return apartmentComplex[i];
        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
            return null;
        }
    }

    public void changeKey(int i, Apartment apt) 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            apartmentComplex[i] = apt;
            percolateUp(qp[i]);
            heapifyDown(qp[i]);
        }
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            apartmentComplex[i] = apt;
            percolateUp(qp[i]);
            heapifyDown(qp[i]);
        }
        
    }

    public void decreaseKey(int i, Apartment apt) 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (apartmentComplex[i].getRent() <= apt.getRent())
                throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
            apartmentComplex[i] = apt;
            percolateUp(qp[i]);
        }
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (apartmentComplex[i].getRent() <= apt.getRent())
                throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
            apartmentComplex[i] = apt;
            heapifyDown(qp[i]);
        }
    }

    public void increaseKey(int i, Apartment apt) 
    {
        if(sentinelKey.equalsIgnoreCase("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IllegalArgumentException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (apartmentComplex[i].getRent() >= apt.getRent())
                throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
            apartmentComplex[i] = apt;
            heapifyDown(qp[i]);
        }   
        else if(sentinelKey.equalsIgnoreCase("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (apartmentComplex[i].getRent() >= apt.getRent())
                throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
            apartmentComplex[i] = apt;
            percolateUp(qp[i]);
        }  
    }

    public void mutateAptRent_maxPQ(int i, int newRent) 
    {
        if(sentinelKey.equals("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            apartmentComplex[i].setRent(newRent);
            percolateUp(qp[i]);
            heapifyDown(qp[i]);

            System.out.println(qp[i]);
        }
        else if(sentinelKey.equals("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            apartmentComplex[i].setRent(newRent);
            System.out.println("In MAX: " + qp[i]);

            percolateUp(qp[i]);
            heapifyDown(qp[i]);
            System.out.println("After MAX: " + qp[i]);

        }
        else
        {
            System.out.println("Invalid Sentinel Key provided...\nMust be either 'MAX' or 'MIN' (Case Insensitive) to indicate initialization to IndexMaxPQ or IndexMinPQ!");
        }
    }
    public void mutateAptSqFt_maxPQ(int i, int newSize) 
    {
        if(sentinelKey.equals("MAX"))
        {
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            apartmentComplex[i].setRent(newSize);
            percolateUp(qp[i]);
            heapifyDown(qp[i]);
        }
        else if(sentinelKey.equals("MIN"))
        {
            if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            apartmentComplex[i].setRent(newSize);
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
            return apartmentComplex[pq[i]].getSize() < (apartmentComplex[pq[j]]).getSize();
        }
        else if(sentinelKey.equals("MIN"))
        {
            return apartmentComplex[pq[i]].getRent() > (apartmentComplex[pq[j]]).getRent();
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
                copy = new IndexedMinMaxPQ<Apartment>("MAX", "REG", pq.length - 1);
                for (int i = 1; i <= n; i++)
                    copy.insert(pq[i],apartmentComplex[pq[i]]);
            }
            else if(sentinelKey.equals("MIN"))
            {
                copy = new IndexedMinMaxPQ<Apartment>("MIN", "REG", pq.length - 1);
                for (int i = 1; i <= n; i++)
                    copy.insert(pq[i], apartmentComplex[pq[i]]);
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