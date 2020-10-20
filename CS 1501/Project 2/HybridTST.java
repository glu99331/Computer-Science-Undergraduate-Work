/*************************************************************************
 *  Compilation:  javac HybridTST.java
 *  Execution:    java HybridTST < words.txt
 *  Dependencies: StdIn.java, LZW_HybridString, HybridQueue
 *
 *  Symbol table with LZW_HybridString keys, implemented using a ternary search
 *  trie (TST).
 *
 *
 *  % java HybridTST < shellsST.txt
 *  keys(""):
 *  by 4
 *  sea 6
 *  sells 1
 *  she 0
 *  shells 3
 *  shore 7
 *  the 5
 *
 *  longestPrefixOf("shellsort"):
 *  shells
 *
 *  keysWithPrefix("shor"):
 *  shore
 *
 *  keysThatMatch(".he.l."):
 *  shells
 *
 *  % java TST
 *  theory the now is the time for all good men

 *  Remarks
 *  --------
 *    - can't use a key that is the empty LZW_HybridString ""
 *
 *************************************************************************/

/**
 *  The <tt>TST</tt> class represents an symbol table of key-value
 *  pairs, with LZW_HybridString keys and generic values.
 *  It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 *  <em>delete</em>, <em>size</em>, and <em>is-empty</em> methods.
 *  It also provides character-based methods for finding the LZW_HybridString
 *  in the symbol table that is the <em>longest prefix</em> of a given prefix,
 *  finding all LZW_HybridStrings in the symbol table that <em>start with</em> a given prefix,
 *  and finding all LZW_HybridStrings in the symbol table that <em>match</em> a given pattern.
 *  A symbol table implements the <em>associative array</em> abstraction:
 *  when associating a value with a key that is already in the symbol table,
 *  the convention is to replace the old value with the new value.
 *  Unlike {@link java.util.Map}, this class uses the convention that
 *  values cannot be <tt>null</tt>&mdash;setting the
 *  value associated with a key to <tt>null</tt> is equivalent to deleting the key
 *  from the symbol table.
 *  <p>
 *  This implementation uses a ternary search trie.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/52trie">Section 5.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne, modified to be compatible with 
 *  Java6 Strings by Gordon Lu.
 */
public class HybridTST<Value> {
    private int N;              // size
    private Node<Value> root;   // root of TST

    private static class Node<Value> {
        private char c;                        // character
        private Node<Value> left, mid, right;  // left, middle, and right subtries        
        private Value val;                     // value associated with LZW_HybridString
    }

    /**
     * Initializes an empty LZW_HybridString symbol table.
     */
    public HybridTST() {
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return N;
    }

    /**
     * Does this symbol table contain the given key?
     * @param key the key
     * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
     *     <tt>false</tt> otherwise
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public boolean contains(LZW_HybridString key) {
        return get(key) != null;
    }

    /**
     * Returns the value associated with the given key.
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol table
     *     and <tt>null</tt> if the key is not in the symbol table
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public Value get(LZW_HybridString key) {
        if (key == null) throw new NullPointerException();
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node<Value> x = get(root, key, 0);
        if (x == null) return null;
        return x.val;
    }

    // return subtrie corresponding to given key
    private Node<Value> get(Node<Value> x, LZW_HybridString key, int d) {
        if (key == null) throw new NullPointerException();
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        if (x == null) return null;
        char c = key.charAt(d);
        if      (c < x.c)              return get(x.left,  key, d);
        else if (c > x.c)              return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid,   key, d+1);
        else                           return x;
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
     * @param key the key
     * @param val the value
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void put(LZW_HybridString s, Value val) {
        if (!contains(s)) N++;
        root = put(root, s, val, 0);
    }

    private Node<Value> put(Node<Value> x, LZW_HybridString s, Value val, int d) {
        char c = s.charAt(d);
        if (x == null) {
            x = new Node<Value>();
            x.c = c;
        }
        if      (c < x.c)             x.left  = put(x.left,  s, val, d);
        else if (c > x.c)             x.right = put(x.right, s, val, d);
        else if (d < s.length() - 1)  x.mid   = put(x.mid,   s, val, d+1);
        else                          x.val   = val;
        return x;
    }

    /**
     * Returns the LZW_HybridString in the symbol table that is the longest prefix of <tt>query</tt>,
     * or <tt>null</tt>, if no such LZW_HybridString.
     * @param query the query LZW_HybridString
     * @throws NullPointerException if <tt>query</tt> is <tt>null</tt>
     * @return the LZW_HybridString in the symbol table that is the longest prefix of <tt>query</tt>,
     *     or <tt>null</tt> if no such LZW_HybridString
     */
    public LZW_HybridString longestPrefixOf(LZW_HybridString s) {
        if (s == null || s.length() == 0) return null;
        int length = 0;
        Node<Value> x = root;
        int i = 0;
        while (x != null && i < s.length()) {
            char c = s.charAt(i);
            if      (c < x.c) x = x.left;
            else if (c > x.c) x = x.right;
            else {
                i++;
                if (x.val != null) length = i;
                x = x.mid;
            }
        }
        return s.substring(0, length);
    }

    /**
     * Returns all keys in the symbol table as an <tt>Iterable</tt>.
     * To iterate over all of the keys in the symbol table named <tt>st</tt>,
     * use the foreach notation: <tt>for (Key key : st.keys())</tt>.
     * @return all keys in the sybol table as an <tt>Iterable</tt>
     */
    public Iterable<LZW_HybridString> keys() {
        HybridQueue<LZW_HybridString> queue = new HybridQueue<LZW_HybridString>();
        collect(root, new StringBuilder(), queue);
        return queue;
    }

    /**
     * Returns all of the keys in the set that start with <tt>prefix</tt>.
     * @param prefix the prefix
     * @return all of the keys in the set that start with <tt>prefix</tt>,
     *     as an iterable
     */
    public Iterable<LZW_HybridString> keysWithPrefix(LZW_HybridString prefix) {
        HybridQueue<LZW_HybridString> queue = new HybridQueue<LZW_HybridString>();
        Node<Value> x = get(root, prefix, 0);
        if (x == null) return queue;
        if (x.val != null) queue.enqueue(prefix);
        collect(x.mid, new StringBuilder(prefix), queue);
        return queue;
    }

    // all keys in subtrie rooted at x with given prefix
    private void collect(Node<Value> x, StringBuilder prefix, HybridQueue<LZW_HybridString> queue) {
        if (x == null) return;
        collect(x.left,  prefix, queue);
        if (x.val != null){ 
            LZW_HybridString prefixToHybrid = new LZW_HybridString(prefix.toString() + x.c);
            queue.enqueue(prefixToHybrid);
        
        }
        collect(x.mid,   prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(x.right, prefix, queue);
    }


    /**
     * Returns all of the keys in the symbol table that match <tt>pattern</tt>,
     * where . symbol is treated as a wildcard character.
     * @param pattern the pattern
     * @return all of the keys in the symbol table that match <tt>pattern</tt>,
     *     as an iterable, where . is treated as a wildcard character.
     */
    public Iterable<LZW_HybridString> keysThatMatch(LZW_HybridString pattern) {
        HybridQueue<LZW_HybridString> queue = new HybridQueue<LZW_HybridString>();
        collect(root, new StringBuilder(), 0, pattern, queue);
        return queue;
    }
 
    private void collect(Node<Value> x, StringBuilder prefix, int i, LZW_HybridString pattern, HybridQueue<LZW_HybridString> queue) {
        if (x == null) return;
        char c = pattern.charAt(i);
        if (c == '.' || c < x.c) collect(x.left, prefix, i, pattern, queue);
        if (c == '.' || c == x.c) {
            LZW_HybridString prefixToHybrid = new LZW_HybridString(prefix.toString() + x.c);
            if (i == pattern.length() - 1 && x.val != null) queue.enqueue(prefixToHybrid);
            if (i < pattern.length() - 1) {
                collect(x.mid, prefix.append(x.c), i+1, pattern, queue);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        if (c == '.' || c > x.c) collect(x.right, prefix, i, pattern, queue);
    }


    /**
     * Unit tests the <tt>TST</tt> data type.
     */
    public static void main(String[] args) {

        // build symbol table from standard input
        HybridTST<Integer> st = new HybridTST<Integer>();
        for (int i = 0; !StdIn.isEmpty(); i++) {
            LZW_HybridString key = new LZW_HybridString(StdIn.readString());
            st.put(key, i);
        }

        // print results
        if (st.size() < 100) {
            StdOut.println("keys(\"\"):");
            for (LZW_HybridString key : st.keys()) {
                StdOut.println(key + " " + st.get(key));
            }
            StdOut.println();
        }

        LZW_HybridString test = new LZW_HybridString("shellsort");
        StdOut.println("longestPrefixOf(\"shellsort\"):");
        StdOut.println(st.longestPrefixOf(test));
        StdOut.println();

        LZW_HybridString test2 = new LZW_HybridString("shor");
        StdOut.println("keysWithPrefix(\"shor\"):");
        for (LZW_HybridString s : st.keysWithPrefix(test2))
            StdOut.println(s);
        StdOut.println();

        StdOut.println("keysThatMatch(\".he.l.\"):");
        LZW_HybridString test3 = new LZW_HybridString(".he.l.");
        for (LZW_HybridString s : st.keysThatMatch(test3))
            StdOut.println(s);
    }
}
