/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

import edu.princeton.cs.algs4.Graph;
//import edu.princeton.cs.algs4.GraphGenerator;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;
//import edu.princeton.cs.algs4.Stack;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import static java.util.regex.Pattern.quote;
//import edu.princeton.cs.algs4.StdRandom;

/**
 *
 * @author Ed Sowell
 * 
 * Reads a graph with predefined left-right verticies from a file
 */
public class BipartiteLeftRightGraphReaderAdjLists{
    int VL; // number of left verticies
    int VR; // number of right verticies
    int E;  // number of edges
    Integer[] weights;
            
    private static final class Edge implements Comparable<Edge> {
        private int v;
        private int w;

        private Edge(int v, int w) {
                this.v = v;
                this.w = w;
        }

        public int compareTo(Edge that) {
            if (this.v < that.v) return -1;
            if (this.v > that.v) return +1;
            if (this.w < that.w) return -1;
            if (this.w > that.w) return +1;
            return 0;
        }
    }
    
    //Constructor
    BipartiteLeftRightGraphReaderAdjLists(){
        
    }
    // Returns the fromVertex.  Also returns the weight of the vertex as weight[0], the adjacent verticies a a Stack<Integers>
    protected Integer processAdjLine(String aLine, Integer[] weight, Stack<Integer> stack){
        
        aLine = aLine.replaceFirst(":", " ");
        Scanner scanner = new Scanner(aLine);
        //scanner.useDelimiter(" ");
        Integer fromVertex= Integer.parseInt(scanner.next()); 
        weight[0] = Integer.parseInt(scanner.next());
        while (scanner.hasNext()){
            stack.push(Integer.parseInt(scanner.next()));
        }
        return fromVertex;
  }

    public int getSizeLeft(){
        return this.VL;
    }
    public int getSizeRight(){
        return this.VR;
    }
    
    public Integer[] getLeftWeights(){
        return weights;
    }

    //
    //   Instantiates a new Graph and loads it from an input file of adjacency lists to make bipartite graph.
    //   Each line is assumed start with a left vertex followed by ":", i.e., 0:. followed by a list of right verticies.
    //   Notes: (1) Only edges from Left to Right are assumed to be in the file. 
    //          (2) Verticies w to the the right of the : must be in the range VL < w <= VL+VR
    //          (3) left[k] == false means vertex k is in the Left set
    //
    Graph createAndReadTheGraph(In infile ) {
        String headerLine;
        String vertexLine;
        Stack<Integer> adjVerts = new Stack<>();
        int v; // Left vertex
        int w; // Right vertex

        headerLine = infile.readLine();
        Scanner headerScanner = new Scanner(headerLine);
        this.VL = headerScanner.nextInt();
        this.VR = headerScanner.nextInt();
        this.E  = headerScanner.nextInt();
       
        Graph G = new Graph(this.VL+this.VR);
        weights = new Integer[this.VL];
        Integer[] wt = new Integer[1];
                


        // Repeated edges are not allowed
        SET<BipartiteLeftRightGraphReaderAdjLists.Edge> set = new SET<>();
        for (int k = 0; k < this.VL; k++) {
            vertexLine = infile.readLine();
            v = processAdjLine(vertexLine, wt, adjVerts);
            weights[v] = wt[0];
            if (v < 0 || v > (this.VL-1)) throw new IllegalArgumentException("Left vertex " + v + " out of range");

            while(!adjVerts.empty()){
                w = adjVerts.pop();
                if (w < this.VL || v > (this.VL+this.VR-1)) throw new IllegalArgumentException("Right vertex " + w + " out of range");
                BipartiteLeftRightGraphReaderAdjLists.Edge e = new BipartiteLeftRightGraphReaderAdjLists.Edge(v, w);
                if (!set.contains(e)) {
                    set.add(e);
                    G.addEdge(v, w);
                } else{
                    throw new IllegalArgumentException("Repeated edge(" + v + ", " + w + ")");
                }
            }
                
                    
        }

        infile.close();
        return G;
    }
    
    public static void main(String[] args) {
        String infilename = "twoClassss4JudgeAdj_2.txt";
        BipartiteLeftRightGraphReaderAdjLists   gr = new BipartiteLeftRightGraphReaderAdjLists();
        
        In infile = null;
        infile= new In(infilename);
        
        
        Graph G = gr.createAndReadTheGraph(infile);
        boolean[] color = new boolean[G.V()]; // color[k] == true means k is a left vertex
        int VL = gr.getSizeLeft();
        for(int k = 0; k< VL; k++){
            color[k] = true;
        }
        
        StdOut.println("Graph:");                
        StdOut.print(G);
            //
            //  Display the partitions
            //
            StdOut.println("Partition:");
            for (int v = 0; v < G.V(); v++) {
                String vertexSetName; 
                if(color[v]){
                    vertexSetName = "Left";
                } else {
                    vertexSetName = "Right";
                }
                        
                StdOut.println(v + ": " + vertexSetName);
            }
    }
    
    
}
