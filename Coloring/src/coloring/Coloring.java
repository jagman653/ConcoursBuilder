/* 
 * Copyright (C) 2017 Edward F Sowell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package coloring;

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.SET;
import java.util.ArrayList;

/**
 *
 * @author Ed Sowell
 * Based on Mahantesh Halappanavar C++ implementation 
 */
public class Coloring {
    
    protected static final class DistanceTwoVertexColoring{
        
        // constructor
        public DistanceTwoVertexColoring(ArrayView aArrayView){
            
        }
        
    }
    ArrayView theArrayView;
    DistanceTwoVertexColoring theDistanceTwoVertexColoring;
   //Constructor
    public Coloring(Graph bipartiteGraph){
        //
        // Create the graph of Entries & Personnel
        // Note: not sure bipartiteGraph couldn't be used directly instead of creating an "array view."
        theArrayView = new ArrayView(bipartiteGraph);
        //
        //  Parallels Monhantesh's algoDistanceTwoVertexColoring( matrix_CSC *X, matrix_CSR *Y, int *vtxColor, int *numColors, int MaxDegree )
        //
        Integer[] vtxColor = new Integer[100];  // temporary...................................................  must fix
        
        theDistanceTwoVertexColoring = new DistanceTwoVertexColoring(theArrayView);
        
       
    }
    public ArrayView getArrayView(){
        return theArrayView;
    }
    
    
     
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Graph G = new Graph(11);
        G.addEdge(0, 6);
        G.addEdge(0, 7);
        G.addEdge(0, 8);
        G.addEdge(1, 6 );
        G.addEdge(1, 7);
        G.addEdge(2, 6);
        G.addEdge(2, 7);
        G.addEdge(2, 8);
        G.addEdge(3, 6);
        G.addEdge(3, 7);
        G.addEdge(3, 8);
        G.addEdge(4, 9);
        G.addEdge(4, 10);
        G.addEdge(5, 9);
        G.addEdge(5, 10);
        Coloring coloring = new Coloring(G);
        // Note: byRow & byCol are the same (except for order) because the graph is symmetric... i.e., an edge from u to v is also an edge from v to u
        System.out.print("By Row:");
        ArrayList<ArrayList<Integer>> rows = coloring.getArrayView().getGraphMatrixByRows();
        int k = 0;
        for(ArrayList<Integer> r : rows){
            System.out.print("\nrow " + k  +": ");
            for(Integer c : r)
                System.out.print(c + " ");
            k++;
        }
        System.out.print("\n\nBy Column:");
        ArrayList<ArrayList<Integer>> cols = coloring.getArrayView().getGraphMatrixByCols();
        k = 0;
        for(ArrayList<Integer> c : cols){
            System.out.print("\nCol " + k  +": ");
            for(Integer r : c)
                System.out.print(r + " ");
            k++;
        }
        
  
    }
    
}
