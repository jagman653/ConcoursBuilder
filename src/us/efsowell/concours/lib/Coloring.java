/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

import edu.princeton.cs.algs4.Graph;
import static edu.princeton.cs.algs4.GraphGenerator.bipartite;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
//import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 *
 * @author Ed Sowell
 * Based on Mahantesh Halappanavar C++ implementation 
 * 
 *   Each column is Entry (each row is a Person)
 *   We want to color the cars
 *
 */

public class Coloring {
    //ColoringMatrix theColoringMatrix;
    ColoringBipartiteDistanceTwo theDistanceTwoVertexColoring;

    
    protected static final class ColoringBipartiteDistanceTwo{
        Integer[] vertexColors;
        int nRows;
        int nCols;
        Integer[] theRandomNumbers;
        // constructor
        /*
        public ColoringBipartiteDistanceTwo(ColoringMatrix theColoringMatrix ){
            nRows       = theColoringMatrix.getMatrixEntryPerson().size();  //Number of Entries. X->nRows; 
            nCols       = theColoringMatrix.getMatrixPersonEntry().size();  //Number of Persons. X->nCols;
            theRandomNumbers = prand(nRows);
            vertexColors = new Integer[nRows];  // Only Entry verticies get colored
             
            // Initialize colors to unassigned status
            for(int i = 0; i < nRows; i ++){
                vertexColors[i] = -1;
            }
            
            int maxDegree = 256;
            int numColors = 0;
            algo(theColoringMatrix, maxDegree,  vertexColors, numColors);
        }
        */

        // Constructor
        public ColoringBipartiteDistanceTwo(Graph G, int numEntries, int numPersons ){
            //nRows       = theColoringMatrix.getMatrixEntryPerson().size();  //Number of Entries. X->nRows; 
            //nCols       = theColoringMatrix.getMatrixPersonEntry().size();  //Number of Persons. X->nCols;
            nRows = numEntries;
            nCols = numEntries;
            theRandomNumbers = prand(nRows);
            vertexColors = new Integer[nRows];  // Only Entry verticies get colored
             
            // Initialize colors to unassigned status
            for(int i = 0; i < nRows; i ++){
                vertexColors[i] = -1;
            }
            
            int maxDegree = 256;
            int numColors = 0;
            algo(G, maxDegree,  vertexColors, numColors);
        }
        
        
        //
        //  algo transliterated from Monhantesh's algoDistanceTwoVertexColoring( matrix_CSC *X, matrix_CSR *Y, int *vtxColor, int *numColors, int MaxDegree )
        //
        //public void algo(ColoringMatrix theColoringMatrix, int maxDegree, Integer vertexColors[], int numColors){
        public void algo(Graph G, int maxDegree, Integer vertexColors[], int numColors){
             //Create & initilize Queues for the storing the vertices in conflict
            Queue<Integer> qConflict1 = new Queue<>();
            Queue<Integer> qConflict2 = new Queue<>();
            Queue<Integer> qConflictSwap = new Queue<>();
            // Initialize to natural order
            for(int i=0; i<nCols; i++){
                qConflict1.enqueue(i);
                //qConflict2.enqueue(-1);
            }
            
            int nLoops = 0;     //Number of rounds of conflict resolution
            int nTotalConflicts = 0; //Total number of conflicts
            int[] mark = new int[maxDegree*nCols];
            for (int i=0; i<(maxDegree*nCols); i++) {
              mark[i]= -1; 
            }
            
            do{
                ///////////////////////////////////////// PART 1 ////////////////////////////////////////
                //Color the vertices in parallel - do not worry about conflicts
                //System.out.printf("Phase 1: Color vertices, ignore conflicts.\n");    
                // System.out.printf("** Iteration : %d (|Q|=%d)\n", nLoops, QTail);
                int v;
                // Iterate through qConflict1 loop
                for(int qi = 0; qi<qConflict1.size(); qi++){
                    v = qConflict1.peek();
                    int StartIndex = v*maxDegree; //Location in Mark      
                    if ( (StartIndex < 0) || (StartIndex > (nCols-1)*maxDegree)) {
                        System.out.printf("StartIndex out of range. v= %d   StartIndex = %d\n",v, StartIndex);
                        System.exit(1);
                    }      
                    for (int i=0; i<maxDegree; i++)
                        mark[StartIndex+i]= -1;
                    
                    int maxColor = -1;
                    int adjColor = -1;                    //Browse the adjacency set of vertex v
                    //SparseVectorEFS adjV = theColoringMatrix.getMatrixEntryPerson().get(v);
                     
                    for(Integer k  : G.adj(v)){
                        for(Integer kk : G.adj(k)){
                            if ( kk == v ) //Self-loop
                                continue;
                            adjColor =  vertexColors[kk];
                            if ( adjColor >= 0 ) {
                                mark[StartIndex+adjColor] = v;
                                //Find the largest color in the neighborhood
                                if ( adjColor > maxColor )
                                  maxColor = adjColor;
                            }
                        }
                    }
                    int myColor;
                    for (myColor=0; myColor<=maxColor; myColor++) {
                    if ( mark[StartIndex+myColor] != v )
                              break;
                    }
                    if (myColor == maxColor)
                        myColor++; /* no available color with # less than cmax */
                    vertexColors[v] = myColor; //Color the vertex
                } //End of loop iterating over qConflict1.  Note that qConflict1 has NOT been changed
                qConflict1.dequeue();
                
                ////////////////////////////   PART 2  /////////////////////////////////////////////
                ////////////////////////   NOT NEEDED BECAUSE IN A SINGLE PROCESSOR SINGLE THREAD ENVIRONMENT NO CONFLICTS ARE FOUND IN PART 1  
                //Detect Conflicts:
                /*
                for (int qi=0; qi<qConflict1.size(); qi++) {
                    v = qConflict1.peek();
                    //Traverse the adjacency set of vertex v, and it's adjacencies
                    for(Integer k  : G.adj(v)){
                        for(Integer w : G.adj(k)){
                          //int w = kk;
                          if ( (w == v) || (vertexColors[v] == -1) ) //Link back/preprocess
                            continue;
                          if ( vertexColors[v] == vertexColors[w] ) {
                            //Q.push_back(v or w) 
                            if ( (theRandomNumbers[v] < theRandomNumbers[w]) ||  ((theRandomNumbers[v] == theRandomNumbers[w])&&(v < w)) ) {
                              //int whereInQ = __sync_fetch_and_add(&QtmpTail, 1);
                              //int whereInQ = QtmpTail++;
                              //Qtmp[whereInQ] = v;//Add to the queue
                              qConflict2.enqueue(v);
                              vertexColors[v] = -1;  //Will prevent v from being in conflict in another pairing
                              break;
                            } //If rand values			
                        } //End of if( vtxColor[v] == vtxColor[verInd[k]] )
                      } // end inner adj traversal
                    } // end outer adj traversal
               } //End of outer for loop on Qi
                nLoops++;
                // Clear qConflict1
                while(!qConflict1.isEmpty()){
                    qConflict1.dequeue();
                }
                //Copy qConflict2 to qConflict1
                while(!qConflict2.isEmpty()){
                    qConflict1.enqueue(qConflict2.dequeue());
                } 
                */
            } while(!qConflict1.isEmpty());
           
            /*System.out.print("vertexColors:\n");
            for(int l=0; l<vertexColors.length; l++){
                 System.out.println("vertex " + l + " color= " + vertexColors[l] );
            }
            */
        }
        
    }

        
        private static int findRandomIntInRange(int aStart, int aEnd, Random aRandom){
            if (aStart > aEnd) {
              throw new IllegalArgumentException("Start cannot exceed End in random number generation.");
            }
            //get the range, casting to long to avoid overflow problems
            long range = (long)aEnd - (long)aStart + 1;
             // compute a fraction of the range, 0 <= frac < range
            long fraction = (long)(range * aRandom.nextDouble());
            int randomNumber =  (int)(fraction + aStart);    
            return randomNumber;
        }

        private static Integer[] prand(int numCols){
            Integer result[] = new Integer[numCols]; 
            Random random = new Random();
            for (int i = 0; i < numCols; ++i){
                result[i] = findRandomIntInRange(0, numCols-1, random);
            }
            return result;
        }

   //Constructor
    public Coloring(Graph GColoring, int numEntries, int numPersons){
        //
        // Create a matrix view of the GColoring. Each row represents a Concours Person (owner or Judge or Owner/Judge).
        //      There is an entry in the row for every Entry that the Concours Person is involved with, either as the Owner or one of the Judges
        // 
        // 
        /*
        theColoringMatrix = new ColoringMatrix(GColoring, numEntries, numPersons);
        theDistanceTwoVertexColoring = new ColoringBipartiteDistanceTwo(theColoringMatrix);
        */
        
        // Try using GColoring directly instead of converting to SparseVectors
        //                                 ColoringBipartiteDistanceTwo(Graph G, int numEntries, int numPersons )
        theDistanceTwoVertexColoring = new ColoringBipartiteDistanceTwo(GColoring,  numEntries,  numPersons);
        
    }
    /*public ColoringMatrix getColoringMatrix(){
        return theColoringMatrix;
    }
    */
    
    
     
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       // Graph GColoring = new Graph(16);
        // Note: indicies are those for GColoring
        //
        // edge(entry, person)
        // Entry C1-1
/*        GColoring.addEdge(0, 5); // owner person vertex 11
        GColoring.addEdge(0, 0);  // Judge person vertex 6
        GColoring.addEdge(0, 1);  // Judge person vertex 7
        GColoring.addEdge(0, 2);  // Judge person vertex 8
        // Entry C1-2        
        GColoring.addEdge(1, 6 );// owner person vertex 12
        GColoring.addEdge(1, 0 ); // Judge person vertex 6
        GColoring.addEdge(1, 1);  // Judge person vertex 7
        GColoring.addEdge(1, 2);  // Judge person vertex 8
        // Entry C1-3
        GColoring.addEdge(2, 7); // owner person vertex 13
        GColoring.addEdge(2, 0);  // Judge person vertex 6
        GColoring.addEdge(2, 1);  // Judge person vertex 7
        GColoring.addEdge(2, 2);  // Judge person vertex 8
        // Entry C1-4
        GColoring.addEdge(3, 8);  // owner person vertex 14
        GColoring.addEdge(3, 0);  // Judge person vertex 6
        GColoring.addEdge(3, 1);  // Judge person vertex 7
        GColoring.addEdge(3, 2);  // Judge person vertex 8
        // Entry D1-1
        GColoring.addEdge(4, 9);  // owner person vertex 15
        GColoring.addEdge(4, 3);  // Judge person vertex 9
        GColoring.addEdge(4, 4);  // Judge person vertex 10
        // Entry D1-2
        GColoring.addEdge(5, 1); // owner person vertex 7
        GColoring.addEdge(5, 3); // Judge person vertex 9
        GColoring.addEdge(5, 4); // Judge person vertex 10
        */
        /*
        GColoring.addEdge(0, 11); // owner
        GColoring.addEdge(0, 6);  // Judge
        GColoring.addEdge(0, 7);  // Judge
        GColoring.addEdge(0, 8);  // Judge
        // Entry C1-2        
        GColoring.addEdge(1, 12 );// owner
        GColoring.addEdge(1, 6 ); // Judge
        GColoring.addEdge(1, 7);  // Judge
        GColoring.addEdge(1, 8);  // Judge
        // Entry C1-3
        GColoring.addEdge(2, 13); // owner
        GColoring.addEdge(2, 6);  // Judge
        GColoring.addEdge(2, 7);  // Judge
        GColoring.addEdge(2, 8);  // Judge
        // Entry C1-4
        GColoring.addEdge(3, 14);  // owner
        GColoring.addEdge(3, 6);  // Judge
        GColoring.addEdge(3, 7);  // Judge
        GColoring.addEdge(3, 8);  // Judge
        // Entry D1-1
        GColoring.addEdge(4, 15); // owner
        GColoring.addEdge(4, 9);  // Judge
        GColoring.addEdge(4, 10); // Judge
        // Entry D1-2
        GColoring.addEdge(5, 7); // owner
        GColoring.addEdge(5, 9); // Judge
        GColoring.addEdge(5, 10); // Judge
        
        
        int numEntries = 6;
        int numPersons = 10;
        */
        //
        //  Read the graph & be sure it's bipartite
        //
        String infilename = "SDJC2015V3.txt";
        //String infilename = "tinyAdj_1.txt";
        BipartiteLeftRightGraphReaderAdjLists grAdj = new BipartiteLeftRightGraphReaderAdjLists();
        In infile = new In(infilename);
        Graph GColoring = null;
        boolean[] leftVertex = null;
        Integer[] weights = null;
        GColoring = grAdj.createAndReadTheGraph(infile);             // also creates weights[]                                     
        StdOut.println( "Bipartite graph read from file " + infilename + ":");
        StdOut.println(GColoring);
        leftVertex = new boolean[GColoring.V()]; // leftVertex[k] == true means k is a left vertex
        int numLeftVerticies = grAdj.getSizeLeft();
        int numRightVerticies = GColoring.V() - numLeftVerticies;
        for(int k = 0; k<numLeftVerticies; k++){
            leftVertex[k] = true;
        }
        // Set weights[]
        weights = new Integer[numLeftVerticies];
        weights = grAdj.getLeftWeights();
        Coloring theColoring = new Coloring(GColoring, numLeftVerticies, numRightVerticies);

       /* System.out.print("MatrixEntryPerson:");
        List<SparseVectorEFS> rowsMEP = theColoring.getColoringMatrix().getMatrixEntryPerson();
        int k = 0;
        for(SparseVectorEFS r : rowsMEP){
            System.out.print("\nrow " + k  +": ");
            System.out.print(r);
            k++;
        }
       System.out.print("\n\nMatrixPersonEntry:");
        List<SparseVectorEFS> rowsMPE = theColoring.getColoringMatrix().getMatrixPersonEntry();
        k = 0;
        for(SparseVectorEFS r : rowsMPE){
            System.out.print("\nRow " + k  +": ");
             System.out.print(r);
            k++;
        }
        System.out.println();
        
        System.out.println("Array (-1 means no entry in the column)");
        Integer[][] x = theColoring.getColoringMatrix().getMatrixPersonEntryAsArray();
        for(int l =0; l<x.length; l++){
            System.out.println("Entry " + l + ": " +Arrays.toString(x[l]));
        }
               */
        
        
    }
    
}
