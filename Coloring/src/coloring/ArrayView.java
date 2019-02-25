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
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ed Sowell
 * 
 * Mahantesh Halappanavar's C++ implementation of distance-1 partial coloring uses a matrix view of the graph. This class
 * was to help maintain some similarity in the Java implementation. However, it turns out to be no different from the Graph
 * that it is constructed from.
 * 
 */
public class ArrayView {
   // private HashMap<Point, Integer> map;
    private int numRows = 0;
    private int numColumns = 0;
    
    // for access by row or by column
    public ArrayList<ArrayList<Integer>>    byRow;
    public ArrayList<ArrayList<Integer>>    byCol;

    // Constructor
    public ArrayView(Graph G) {
        // Graph as an ArrayList of column indicies
        numRows = G.V();
        numColumns = G.V();
        byRow = new ArrayList<>();
        byCol = new ArrayList<>();
        for(int i = 0 ; i < G.V(); i++){
            ArrayList<Integer> theCols = new ArrayList<>();
            for(int j : G.adj(i)){
               theCols.add(j);
            }
            byRow.add(theCols);
        }
        // Graph as an ArrayList of row indicies
        byCol = new ArrayList<>();

        // Graph as an ArrayList of row indicies
        for(int i = 0 ; i < G.V(); i++){
            ArrayList<Integer> theRows = new ArrayList<>();
            ArrayList<Integer> theRow = byRow.get(i);
            for(int j = 0; j < G.V(); j++){
                if(theRow.contains(j)) theRows.add(j);
            }
            byCol.add(theRows);
        }
        
        
    }

    public ArrayList<ArrayList<Integer>> getGraphMatrixByRows(){
        return byRow;
    }
    public ArrayList<ArrayList<Integer>> getGraphMatrixByCols(){
        return byCol;
    }
    


}
