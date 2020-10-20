/***********************************************************************************
 * <ul>
 * <li> Compilation:  javac SeparateChainingHashST.java </li>
 * <li> Execution:    java SeparateChainingHashST < input.txt </li>
 * <li> Dependencies: RedBlackBST.java </li>
 * </ul>
 * 
 *  A symbol table implemented with a separate-chaining hash table.
 * 
 * <ul>
 * <li>Inherited from SeparateChainingHashST.java by Robert Sedgewick and Kevin Wayne
 * </li>
 * <li>Modified by Gordon Lu for CS 1501: Algorithm Implementation
 * </li>
 * </ul>
 * 
 **********************************************************************************/
@SuppressWarnings({"unchecked"})
public class SeparateChainingHashST<Key extends Comparable<Key>, Value> {
    private static final int INIT_CAPACITY = 100;
    private int numElements; //number of key-value pairs
    private int tableSize; //hash table size
    //Potentially making a linked list of symbol tables could risk a runtime akin to that of a sequential search:
    //Use Red-Black Trees to guarantee logarithmic runtime
    private RedBlackBST<Key, Value>[] st; //array of red-black tree symbol tables
    // private SequentialSearchST<Key, Value>[] st;  // array of linked-list symbol tables


    /**
     * Initializes an empty symbol table.
     */
    public SeparateChainingHashST() {
        this(INIT_CAPACITY);
    } 

    /**
     * Initializes an empty symbol table with {@code m} chains.
     * @param m the initial number of chains
     */
    public SeparateChainingHashST(int m) {
        this.tableSize = performSieve(m);         //Generate closest prime to size passed in
        //this.m = m;
        //this.tableSize = m;
        this.numElements = 0;
        st = (RedBlackBST<Key, Value>[]) new RedBlackBST[tableSize];
        for(int i = 0; i < this.tableSize; i++)
        {
            //Initialize each red black tree
            st[i] = new RedBlackBST<Key, Value>();
        }
        // st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[m];
        // for (int i = 0; i < m; i++)
        //     st[i] = new SequentialSearchST<Key, Value>();
    } 

    /***************************************************************************
    *  Separate Chaining Hash ST resize function.
    ***************************************************************************/
    
    // resize the hash table to have the given number of chains,
    // rehashing all of the keys
    private void resize(int chains) {
        SeparateChainingHashST<Key, Value> temp = new SeparateChainingHashST<Key, Value>(chains);
        for(int i = 0; i < this.tableSize; i++)
        {
            for(Key key : st[i].keys())
            {
                temp.put(key, st[i].get(key));
            }
        }
        //this.tableSize = performSieve(temp.tableSize);
        this.tableSize = temp.tableSize;
        this.numElements = temp.numElements;
        this.st = temp.st;
        // SeparateChainingHashST<Key, Value> temp = new SeparateChainingHashST<Key, Value>(chains);
        // for (int i = 0; i < m; i++) {
        //     for (Key key : st[i].keys()) {
        //         temp.put(key, st[i].get(key));
        //     }
        // }
        // this.m  = temp.m;
        // this.n  = temp.n;
        // this.st = temp.st;
    }

    /***************************************************************************
    *  Separate Chaining Hash ST helper functions.
    ***************************************************************************/
    
    //hash value between 0 and m-1
    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % tableSize;
    } 

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return numElements;
    } 

    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /***************************************************************************
    *  Separate Chaining Hash ST search.
    ***************************************************************************/

    /**
     * Returns true if this symbol table contains the specified key.
     *
     * @param  key the key
     * @return {@code true} if this symbol table contains {@code key};
     *         {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    } 

    /**
     * Returns the value associated with the specified key in this symbol table.
     *
     * @param  key the key
     * @return the value associated with {@code key} in the symbol table;
     *         {@code null} if no such value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        int i = hash(key);
        return st[i].get(key);
    } 

    /***************************************************************************
    *  Separate Chaining Hash ST insertion.
    ***************************************************************************/

    /**
     * Inserts the specified key-value pair into the symbol table, overwriting the old 
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is {@code null}.
     *
     * @param  key the key
     * @param  val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) {
            delete(key);
            return;
        }

        // double table size if average length of list >= 10
        if (numElements >= 10*tableSize) resize(2*tableSize);

        int i = hash(key);
        if (!st[i].contains(key)) numElements++;
        st[i].put(key, val);
    } 

    /***************************************************************************
    *  Separate Chaining Hash ST deletion.
    ***************************************************************************/

    /**
     * Removes the specified key and its associated value from this symbol table     
     * (if the key is in this symbol table).    
     *
     * @param  key the key
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");

        int i = hash(key);
        if (st[i].contains(key)) numElements--;
        st[i].delete(key);

        // halve table size if average length of list <= 2
        if (tableSize > INIT_CAPACITY && numElements <= 2*tableSize) resize(tableSize/2);
    } 

    /***************************************************************************************************************
     * The Sieve of Eratosthenes is an ancient greek algorithm used to find all prime numbers up to a given limit.
     * To find all the prime numbers less than or equal to a given integer n by Eratosthenes' method:
     *
     * 1) Create a list of consecutive integers from 2 through n: (2, 3, 4, ..., n).
     * 2) Initially, let p equal 2, the smallest prime number.
     * 3) Enumerate the multiples of p by counting in increments of p from 2p to n, and mark them in the list 
     * (these will be 2p, 3p, 4p, ...; the p itself should not be marked).
     * 4) Find the first number greater than p in the list that is not marked. If there was no such number, stop. 
     * Otherwise, let p now equal this new number (which is the next prime), and repeat from step 3.
     * 5) When the algorithm terminates, the numbers remaining not marked in the list are all the primes below n.
     *
     *The main idea here is that every value given to p will be prime, because if it were composite it would be 
     * marked as a multiple of some other, smaller prime. Note that some of the numbers may be marked more than once 
     * (e.g., 15 will be marked both for 3 and 5).
    ****************************************************************************************************************/
    public static int performSieve(int n)
    {
        //n represent the limit
        boolean[] primes = new boolean[n+1]; //iterate from 0 to n;
        //Initialize boolean array to all true;
        for(int p = 0; p < n; p++)
        {
            primes[p] = true;
        }
        //Now perform the algorithm:
        for(int i = 2; i <= Math.sqrt(n); i++) //i = 2, 3, 4 <= sqrt(n)
        {
            //If prime unchanged, then it's a prime number:
            if(primes[i])
            {
                //Update all multiples of p to false:
                for(int j = i*i; j <= n; j += i) //j = i^2, (i^2 + i), (i^2 + 2i) + (i^2 + 3i) + ...
                {
                    primes[j] = false;
                }
            }
        }
        //Find max prime <= n
        int maxPrime = 2;
        for(int k = 2; k <= n; k++)
        {
            if(maxPrime < k && primes[k])
            {
                maxPrime = k;
            }
        }
        return maxPrime;
    }

}