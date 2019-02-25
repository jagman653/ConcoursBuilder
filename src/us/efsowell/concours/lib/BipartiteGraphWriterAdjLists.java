/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

//import static Matching.BipartiteMatchingHungarian.TRACE;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.GraphGenerator;
//import edu.princeton.cs.algs4.GraphGenerator;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;
import static java.lang.reflect.Array.set;
import java.text.SimpleDateFormat;
import java.util.Date;
//import edu.princeton.cs.algs4.StdRandom;

/**
 *
 * @author Ed Sowell
 * 
 * Reads a graph from 
 */
public class BipartiteGraphWriterAdjLists{
    int V;
    int E;
    

    
    
    BipartiteGraphWriterAdjLists(){
        
    }
    //
    //   Writes Graph to file in the format that can be read by BipartiteGraphReader
    //
    public void writeGraph(Graph G, int numLeft, Out outfile) {
       
        this.V = G.V(); // num vertices
        int numRight = this.V - numLeft;
        this.E = G.E(); // num edges
        String header =  numLeft + " " + numRight + " " + this.E;
        outfile.println(header);
        String row = "";
        for(int v = 0; v<this.V; v++){
            row = v + ":"; 
            for(int w : G.adj(v)){
                row = row + " " + w;
            }
            outfile.println(row);
            row = "";
        }
    
    }
    
    public static void main(String[] args) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-h-mm-a");
        String formattedDate = sdf.format(date);
        
        String outfileNameAdj = "myGraphAdj_" + formattedDate + ".txt";
        Out outfile = new Out(outfileNameAdj);
        BipartiteGraphWriterAdjLists gw = new BipartiteGraphWriterAdjLists();
   
        // generate graph
        int numLeft = 2;
        int numRight =2;
        int E  = 4;
        Graph G = GraphGenerator.bipartite(numLeft, numRight, E);
        StdOut.println( "Bipartite graph with " + numLeft + " vertices in Left set, " + numRight + " in the Right set and " + E + " edges has been generated:");
        StdOut.println(G);
        StdOut.println("Writing adjacency list graph to file " + outfileNameAdj);
        gw.writeGraph(G, numLeft, outfile);
   } 
}
