/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.util.List;

/**
 *
 * @author Ed Sowell
 */
public class MarketMatrixRow {
   List<Integer> row; 
   // constructor
   public  MarketMatrixRow(List<Integer> aRow){
      row = aRow; 
   }
   public List<Integer> getRow(){
       return row;
   }
   
   public String toString(){
        String result  = ""; // Never used... to keep compiler happy
        int i = 0;
        for(Integer intRowElement : row){
            if( i == 0){
                result = intRowElement.toString();
            } else {
                result = result + " " + intRowElement.toString();
            }
            i++;
        }  
        return result;
   }
           
}    

