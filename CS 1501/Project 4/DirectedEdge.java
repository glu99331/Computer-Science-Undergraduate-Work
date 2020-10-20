


/******************************************************************************
 *  Compilation:  javac DirectedEdge.java
 *  Execution:    java DirectedEdge
 *  Dependencies: StdOut.java
 *
 *  Immutable weighted directed edge.
 *
 ******************************************************************************/

/**
 *  The {@code DirectedEdge} class represents a weighted edge in an 
 *  {@link EdgeWeightedDigraph}. Each edge consists of two integers
 *  (naming the two vertices) and a real-value weight. The data type
 *  provides methods for accessing the two endpoints of the directed edge and
 *  the weight.
 *  <p>
 *  For additional documentation, see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
import java.util.Random;
@SuppressWarnings({"cast","rawtypes", "unchecked"})
public class DirectedEdge { 
    //private final int v;
    //private final int w;

    private String cableType;
    private int edgeStartPoint;
    private int edgeEndPoint;
    private int bandWidth_mbps;
    private double data_packet_travel_duration;
    private final double weight;
    private Random genRandomWeight = new Random();
    private final double COPPER_SPEED = 230000000;
    private final double FIBER_OPTIC_SPEED = 200000000;
    //private final double weight;

    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with
     * the given {@code weight}.
     * @param v the tail vertex
     * @param w the head vertex
     * @param weight the weight of the directed edge
     * @throws IllegalArgumentException if either {@code v} or {@code w}
     *    is a negative integer
     * @throws IllegalArgumentException if {@code weight} is {@code NaN}
     */
    public DirectedEdge(String[] vertexContents, double weight) {
        
        this.edgeStartPoint = Integer.parseInt(vertexContents[0]);
        this.edgeEndPoint = Integer.parseInt(vertexContents[1]);
        this.cableType = vertexContents[2];
        //this.bandWidth_mbps = Integer.parseInt(vertexContents[3]);
        //this.edgeLength_meters = Integer.parseInt(vertexContents[4]);
        this.weight = weight;
        /* if(this.cableType.equalsIgnoreCase("COPPER"))
        {
            double secondsinMeters = (double)(1/COPPER_SPEED);
            this.weight = Integer.parseInt(vertexContents[4]) * secondsinMeters;
        }
        else if(this.cableType.equalsIgnoreCase("OPTIC"))
        {
            double secondsinMeters = (double)(1/FIBER_OPTIC_SPEED);
            this.weight = Integer.parseInt(vertexContents[4]) * secondsinMeters;
        }
        
         */
        if (edgeStartPoint < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (edgeEndPoint < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        /* this.edgeStartPoint = edgeStartPoint;
        this.edgeEndPoint = edgeEndPoint;
        this.cableType = cableType;
        this.bandWidth_mbps = bandWidth_mbps;
        this.edgeLength_meters = edgeLength_meters; */
    }
    public DirectedEdge(String[] vertexContents) {
        
        this.edgeStartPoint = Integer.parseInt(vertexContents[0]);
        this.edgeEndPoint = Integer.parseInt(vertexContents[1]);
        //this.cableType = vertexContents[2];
        //this.bandWidth_mbps = Integer.parseInt(vertexContents[3]);
        //this.edgeLength_meters = Integer.parseInt(vertexContents[4]);
        this.weight = (double)genRandomWeight.nextInt(100); //generate a random weight, shouldn't matter here, between 0 and 99
        /* if(this.cableType.equalsIgnoreCase("COPPER"))
        {
            double secondsinMeters = (double)(1/COPPER_SPEED);
            this.weight = Integer.parseInt(vertexContents[4]) * secondsinMeters;
        }
        else if(this.cableType.equalsIgnoreCase("OPTIC"))
        {
            double secondsinMeters = (double)(1/FIBER_OPTIC_SPEED);
            this.weight = Integer.parseInt(vertexContents[4]) * secondsinMeters;
        }
        
         */
        if (edgeStartPoint < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (edgeEndPoint < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        /* this.edgeStartPoint = edgeStartPoint;
        this.edgeEndPoint = edgeEndPoint;
        this.cableType = cableType;
        this.bandWidth_mbps = bandWidth_mbps;
        this.edgeLength_meters = edgeLength_meters; */
    }

    /**
     * Returns the tail vertex of the directed edge.
     * @return the tail vertex of the directed edge
     */
    public int retrieveStart()
    {
        return edgeStartPoint;
    }
    public int from()
    {
        return edgeStartPoint;
    }
    public int to()
    {
        return edgeEndPoint;
    }
    /**
     * Returns the head vertex of the directed edge.
     * @return the head vertex of the directed edge
     */
    public int retrieveEnd()
    {
        return edgeEndPoint;
    }
    
    /**
     * Returns the weight of the directed edge.
     * @return the weight of the directed edge
     */
    public double retrieveDensity() 
    {
        return weight;
    }
     /**
     * Returns the associated bandwidth of the directed edge.
     * @return the bandwidth of the directed edge
     */
    
    public int retrieveBandwidth()
    {
        return bandWidth_mbps;
    }

     /**
     * Returns the associated cable type of the directed edge.
     * @return the cable type of the directed edge
     */
    
    public String retrieveCableType()
    {
        return cableType;
    }

    public double retrievePacketLoss()
    {
        return data_packet_travel_duration;
    }
    
    public int other(int vertex) {
        if      (vertex == edgeStartPoint) return edgeEndPoint;
        else if (vertex == edgeEndPoint) return edgeStartPoint;
        else throw new IllegalArgumentException("Illegal endpoint");
    }
    

    /**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    public String toString() 
    {
        return edgeStartPoint + "->" + edgeEndPoint + " ";   
    }

    public String bandWidthToString() 
    {
        return " The bandwidth to traverse from " + edgeStartPoint + " to " + edgeEndPoint + " was: " + weight;
    }
}

/******************************************************************************
 *  Copyright 2002-2018, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/

