/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.BipartiteMatching;
import edu.princeton.cs.algs4.BipartiteX;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.GraphGenerator;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Ed Sowell
 * Based on pseudocode for algorithm ASM2 in Harvey, N. J. A et al "Semi-Matching for Bipartite Graphs and Load Balancing, Journal 
  Journal of Algorithms  archive  Volume 59 Issue 1, April 2006  Pages 53-78  
  * 
  * This is an "optimal matching" which they define based on number of matched edges for the task doers... load balancing
  * 
 */



public class BipartiteMatchingHLLT {
    static final int UNMATCHED = -1;
    private static int matchingCardinalityLeft;
    private static int matchingCardinalityRight;
    private static SemiMatching semimatching;
    private static SET<Integer> allmatched;
    private static SET<Integer> allunmatched;

        
    static final boolean TRACE = false; 
    
    
    
   //Constructor
    public BipartiteMatchingHLLT(Graph G){
        matchingCardinalityLeft  = 0;
        matchingCardinalityRight = 0;
        semimatching = new SemiMatching(G.V());
        allmatched = new SET<>();        
        allunmatched = new SET<>();        
    }
    private static final class Edge implements Comparable<Edge> {
        private int v;
        private int w;

        private Edge(int v, int w) {
            // This makes sure that {2, 3} == {3, 2}
            if (v < w) {
                this.v = v;
                this.w = w;
            }
            else {
                this.v = w;
                this.w = v;
            }
        }

        public int compareTo(Edge that) {
            if (this.v < that.v) return -1;
            if (this.v > that.v) return +1;
            if (this.w < that.w) return -1;
            if (this.w > that.w) return +1;
            return 0;
        }
    }

    //                                  NOT USED
    //
    //   Returns the best vertex  for right vertex u and the current bestV 
    //   Note: bestV must not be null.... handle this case separately
    //   
    private Integer getBestV(Graph G, boolean[] leftVertex, ArrayList<ArrayList<Edge>> matching, Integer[] weight, Integer u, Integer bestV){
        if (!leftVertex[u]) throw new IllegalArgumentException("Bad u argument in getBestV. u must be a Left vertex");

        int totalWtU = 0;
        int totalWtBestV = 0;
        Integer result;
        ArrayList<Edge> singleList;
        for(int k : G.adj(u)){
            if(!leftVertex[k]){
                singleList = matching.get(k);
                if(singleList.contains(new Edge(k, u)))  totalWtU = totalWtU + weight[k];  
            }
        }
        for(int k : G.adj(bestV)){
            if(!leftVertex[k]){
                singleList = matching.get(k);
                if(singleList.contains(new Edge(k, bestV)))  totalWtBestV = totalWtBestV + weight[k];
            }
        }
        if(totalWtU < totalWtBestV){
            result = totalWtU;
        } else{
            result = totalWtBestV;
        }
        return result;
    }
    
    
    private static final class SemiMatching{
        private final ArrayList<ArrayList<Integer>> thesemimatching;
        private int size; // Graph size
        // constructor
        private SemiMatching(Integer aSize){
            thesemimatching = new ArrayList<>(aSize);
            for(int i = 0; i<aSize; i++){
                thesemimatching.add(i, new ArrayList<>());
            }
        }
        
        public void addMatching(Integer u, Integer v){
            thesemimatching.get(u).add(v);
            thesemimatching.get(v).add(u);
        }
        public void removeMatching(Integer u, Integer v){
            thesemimatching.get(u).remove(v);
            thesemimatching.get(v).remove(u);
        }
        public boolean inMatching(Integer u, Integer v){
            boolean result = false;
            if ((u<0) || (u>size-1) || (v<0) || (v>size-1)) throw new IllegalArgumentException("Vertex out of range");
            if(thesemimatching.isEmpty()){
                result = false;
            } else {
                ArrayList<Integer> vertexListAtU = thesemimatching.get(u);
                ArrayList<Integer> vertexListAtV = thesemimatching.get(v);
                if(vertexListAtU.contains(v) && vertexListAtV.contains(u)) result = true;
            }
            return result;
        }
        
         public SET<Integer> getMatchedNeighbors(Integer w){
             SET<Integer> result = new SET<>();
             for(Integer k : thesemimatching.get(w)) result.add(k);
             return result;
         }

         public SET<Integer> getUnmatchedNeighbors(Graph G, Integer w){
             SET<Integer> result = new SET<>();
             
             for(Integer k : G.adj(w))
                 if(!thesemimatching.get(k).contains(w)) result.add(k);
             return result;
         }
         
         public ArrayList<ArrayList<Integer>> getTheSemimatching(){
             return thesemimatching;
         }
        
         public int degM(Integer w){
            return getMatchedNeighbors(w).size();
         }

         //
         // The sum of weights of all vertices that are matched with vertex w.
         // As used in BipartiteMatchingHLLT, w is a Right vertex and the weights being summed are 
         // those of Left vertices. 
         // Note that Left vertex weights are assigned apriori. If all weights are 1 the loads are
         // numerically equally to the matched degree of w.
         //
        public int loadM(Integer[] weights, Integer w){
            int result = 0;
            for(int k : getMatchedNeighbors(w)){
                result += weights[k];
            }
            return result;
         }

        private  void display(int aSize){
            for(int k = 0; k < aSize; k++){
                StdOut.print(k + ": " + thesemimatching.get(k) + "\n");
            }
        
        }
    }
    
    public BipartiteMatchingHLLT(){

    }
    // algs4 Queue doesn't support clear()
    private void clearQ(Queue<Integer> Q){
        while(!Q.isEmpty()){
            Q.dequeue();
        }
    }
    
    //
    //   Returns the total weight  of veritices with which a Right vertex is matched
    //   
    private int weightRightM(Graph G, boolean[] leftVertex, ArrayList<ArrayList<Integer>> matching, Integer[] weight, int v){
        int totalWt = 0;
        ArrayList<Integer> singleList;
        for(int k : G.adj(v)){
            if(!leftVertex[k]){
                singleList = matching.get(k);
                if(singleList.contains(v))  totalWt = totalWt + weight[k];
            }
        }
        return totalWt;
    }

        public void displayJudgeLoads(int aSize, boolean[] leftVertex,  Integer[] weights){
            ArrayList<Integer> singleList;
            for(int v = 0; v < aSize; v++){
                // Display only the Loads for the Right vertices, i.e., Judges
                int totalWt = 0;
                if(!leftVertex[v]){
                    singleList = semimatching.getTheSemimatching().get(v); // this is a list of the Left vertexes, i.e., Judging slots, matched to v
                    for(int lv : singleList){
                        totalWt = totalWt + weights[lv];
                    }
                    System.out.println("Bipartite graph judge vertex: " + v + " Load =" + totalWt);
                }
            }
                
            
        }
    
    

    private boolean contains(Integer[] predecessor, int v){
        boolean result = false;
        for(int k = 0; k < predecessor.length; k++ ){
           if(predecessor[k] == v) result = true;
           break;
        }
        return result;
    }
    
 

    
    
    
    
    //
    //  Finds optimal semi-matching starting from empty matching
    //
    public int findOptimalSemiMatching(Graph G, boolean[] leftVertex, Integer[] weights){
        int result = 1; // 1 ==> good result, 0 ==> failed to find a semi-matching with all left vertices matched
        Integer[] predecessor = new Integer[G.V()]; // Called Parent in pseudocode
        Queue<Integer> Q = new Queue<>(); 
        SET<Integer> adjSet;                // Called N in pseudocode
        for( int root = 0;root < G.V(); root++){
            Integer w = null;
            SET<Integer> visited = new SET<>(); // Called S in pseudocode
            if( leftVertex[root]){
                if(TRACE) System.out.println("\n++++++++++++++++++++In vertex loop in findOptimalSemimatching. Root vertex " + root);
                clearQ(Q);
                Q.enqueue(root);
                visited.add(root);
                Integer bestV = null; // Right vertex with least load found so far
                if(TRACE)System.out.println("Q before while loop: " + Q);
                while(!Q.isEmpty()){
                    w = Q.dequeue();
                    if(leftVertex[w]){ 
                        // w is in left set. Get all  neighbors of w ( which are in Right set) that are unmatched
                       adjSet = semimatching.getUnmatchedNeighbors(G, w);
                       if(TRACE)System.out.println("Unmatched neighbors of Left vertex " + w + " are :" + adjSet);
                    } else {
                        //
                        //  Extend the breadth first search
                        //
                        // w is a Right vertex. Get all  neighbors (which are of course Left verticies) of w that are matched
                        //
                        adjSet = semimatching.getMatchedNeighbors(w);
                        if(TRACE)System.out.println("Matched neighbors of Right vertex " + w + " are :" + adjSet);
                        //if((bestV == null) || (semimatching.degM(w) < semimatching.degM(bestV))) bestV = w;
                        if((bestV == null) || (semimatching.loadM(weights, w) < semimatching.loadM(weights, bestV))) bestV = w;
                        if(TRACE)System.out.println("bestV = " + bestV);
                    }
                    for( int n : adjSet){
                        // This check is not it pseudocode but it needs to be here to avoid endless loop
                        // Also, visited (S in the pseudocode) is not used otherwise.
                        if(!visited.contains(n)){ 
                            predecessor[n] = w;
                            Q.enqueue(n);
                        }
                    }
                    visited = visited.union(adjSet);
                    
                    if(TRACE)System.out.println("Q at end of while loop: " + Q);

                }
                if(TRACE)System.out.println("visited: " + visited);
                //
                // Switch edges along the path from bestV to root
                //
                    if(bestV == null){
                        //System.out.println("bestV is null");
                        System.out.println("Matching failed.");
                        result = 0;
                    } else {
                        int v = bestV;
                        int u = predecessor[v];                               
                        allmatched.add(u);
                        allmatched.add(v);
                        allunmatched.delete(u);
                        allunmatched.delete(v);
                        semimatching.addMatching(u, v);
                        while(u != root){
                            v = predecessor[u];
                            semimatching.removeMatching(u, v);
                            u = predecessor[v];
                            semimatching.addMatching(u, v);                    }
                        if(TRACE){
                            System.out.println("Updated matching:");
                            semimatching.display(G.V());
                        }
                    }
            } // if(leftVertex[root])
        } // for all verticies in G
        
        return result;
    } // findOptimalSemiMatching
    
        public int getLeftCardinality(Graph G, boolean[] leftVertex){
            for(int k = 0; k < G.V(); k++){
                if(leftVertex[k] && (G.degree(k) > 0)) matchingCardinalityLeft++;
            }
            return matchingCardinalityLeft;
        }

        public int getRightCardinality(Graph G, boolean[] leftVertex){
            for(int k = 0; k < G.V(); k++){
                if(!leftVertex[k] && (G.degree(k) > 0)) matchingCardinalityRight++;
            }
            return matchingCardinalityRight;
        }
        
        public ArrayList<ArrayList<Integer>> getTheSemimatching(){
            return semimatching.getTheSemimatching();
        }
    
    //
    //   This main() tests findOptimalSemiMatching() in the BipartiteMatchingHungarian class.
    //
    //   If READ_GRAPH is true the presumed birpartite graph will be read from an adjacency list file. The first line has the number of verticies 
    //   followed by number of edges. Subsequent lines give the vertex followed by ":" and a list of adjacent verticies. For example:
    //      8 12
    //      0: 3 6 7 5
    //      1: 6 7 3
    //      2: 3 5 7
    //      3: 4 0 2 1
    //      4: 3 7
    //      5: 2 0
    //      6: 1 0
    //      7: 1 2 0 4
    //
    //    If READ_GRAPH is false a random bipartite graph will be generated, saved to an adjacency list file, and READ BACK IN to be processed.
    //    The save & re-read is done to make sure  all subsequent runs on the same graph will generate identical results. I.e., the vertex orderings
    //    will be identical. This was important during debugging.
    //
    //    The code contains traceing output for debugging purposes. It is turned on by setting TRACE to true.
    //
    //    After the matching is done with the same graph is processed with BipartiteMatching from algs4 library. For all test cases tried
    //    matchings of the same cardinality were found for both methods. Naturally, the matchings are not identical.
    
    public static void main(String[] args) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-h-mm-a");
        String formattedDate = sdf.format(date);
         if(TRACE) System.out.println(formattedDate);         
        //String outfileName = "myGraph_" + formattedDate + ".txt";
        //String outfileNameAdj = "myGraphAdj_" + formattedDate + ".txt";
        boolean READ_GRAPH = true;
        //Graph tempG;
        Graph G = null;
        boolean[] leftVertex = null;
        Integer[] weights = null;
        if(READ_GRAPH){
            //
            //  Read the graph & be sure it's bipartite
            //
            //String infilename = "twoClassss4JudgeAdj_2.txt";
            String infilename = "tinyAdj_1.txt";
            BipartiteLeftRightGraphReaderAdjLists grAdj = new BipartiteLeftRightGraphReaderAdjLists();
            In infile = new In(infilename);
            G = grAdj.createAndReadTheGraph(infile);             // also creats weights[]                                     
            StdOut.println( "Bipartite graph read from file " + infilename + ":");
            StdOut.println(G);
            leftVertex = new boolean[G.V()]; // leftVertex[k] == true means k is a left vertex
            int VL = grAdj.getSizeLeft();
            for(int k = 0; k<VL; k++){
                leftVertex[k] = true;
            }
            // Set weights[]
            weights = new Integer[VL];
            weights = grAdj.getLeftWeights();
        } else {
        }
        // Do the run
           /* BipartiteX bipartition;      
            bipartition = new BipartiteX(G);
            if (!bipartition.isBipartite()) {
                throw new IllegalArgumentException("Graph is not bipartite");
            }
            */
        
            //
            //  Display the partitions
            //
            StdOut.println("Partition:");
            for (int v = 0; v < G.V(); v++) {
                String vertexSetName; 
                if(leftVertex[v]){
                    vertexSetName = "Left";
                } else {
                    vertexSetName = "Right";
                }
                StdOut.println(v + ": " + vertexSetName);
            }

        
        
        BipartiteMatchingHLLT maximumMatching = new BipartiteMatchingHLLT(G);
        long startTime =  System.nanoTime();
        // Initialize match
       // Integer[] mate = new Integer[G.V()]; // mate[u] =  v means an edge from u --> v is in the current matching
      //  for( int k = 0; k < G.V(); k++){
      //      mate[k] = UNMATCHED;
      //  }
        

        maximumMatching.findOptimalSemiMatching(G, leftVertex, weights);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.        maximumMatching.findOptimalSemiMatching(G, bipartition, mate);

        System.out.println("Semi-matching from HLLT method: " );
        semimatching.display(G.V());
        System.out.println();
       // System.out.println("\nCardinality = " + getCardinality());
        System.out.println("Hungarian running time (ms):" + duration);
        
        /*
            Now check it against BipartiteMatching from algs4 library
        */
         System.out.println("\n\n+++++++++++++++++++++Now check it against BipartiteMatching from algs4 library++++++++++++\n\n");
         startTime =  System.nanoTime();
       
         BipartiteMatching matching = new BipartiteMatching(G);
         endTime = System.nanoTime();
         duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.        maximumMatching.findOptimalSemiMatching(G, bipartition, mate);
         System.out.println("algs4 running time (ms):" + duration);
        
        // print maximum matching
        StdOut.printf("Number of edges in max matching        = %d\n", matching.size());
        StdOut.printf("Number of vertices in min vertex cover = %d\n", matching.size());
        StdOut.printf("Graph has a perfect matching           = %b\n", matching.isPerfect());
        StdOut.println();

        if (G.V() >= 1000) return;

        StdOut.print("Max matching: ");
        for (int v = 0; v < G.V(); v++) {
            int w = matching.mate(v);
            if (matching.isMatched(v) && v < w)  // print each edge only once
                StdOut.print(v + "-" + w + " ");
        }
        StdOut.println();

        // print minimum vertex cover
        StdOut.println("Min vertex cover: ");
        for (int v = 0; v < G.V(); v++)
            if (matching.inMinVertexCover(v))
                StdOut.print(v + " ");
        StdOut.println();
        
    }
    
    
    //
    // Test flipMate()
    //
    
    /*public static void main(String[] args) {
        int SIZE = 6;
        Integer[] mate = new Integer[SIZE];
        Integer[] predecessor = new Integer[SIZE];
        boolean[]  free = new boolean[SIZE];
        int startVertex = 0;
        int endVertex = 2;
        for(int i = 0; i< SIZE; i++){
            mate[i] = -1;
            predecessor[i] = -1;
            free[i] = true;
        }
        mate[1] = 2;
        mate[2] = 1;
        free[1] = false;
        free[2] = false;
        predecessor[1] = 0;
        predecessor[2] = 1;
        predecessor[3] = 2;
        startVertex = 0;
        endVertex = 3;
        System.out.println("initial mate[]: " );
        for(int k = 0; k< SIZE; k++){
            System.out.print(k + "-" + mate[k] +  ", ");
        }
        System.out.println();
    

        flipMate(mate, predecessor,free, startVertex, endVertex);   
        System.out.println("final mate[]: " );
        for(int k = 0; k< SIZE; k++){
            System.out.print(k + "-" + mate[k] +  ", ");
        }
        System.out.println();
        
    }
    */
    
    
}
