/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import java.util.ArrayList;

/**
 *   NOT USED
 * @author jag_m_000
 */
public class ConcoursColors {
    ArrayList<ConcoursColor> concourscolorArray;
    
    public ConcoursColors(){
     concourscolorArray = new ArrayList<>() ;  
    }
    
    public void addColor(ConcoursColor aColor){
        concourscolorArray.add(aColor);
    }
    
    public ConcoursColor getConcoursColor(int aIndex){
        ConcoursColor cc_i = null;
        for(ConcoursColor cc : concourscolorArray){
            if(cc.index == aIndex){
              cc_i = cc;
              break;
            }
        }
        if(cc_i == null){
            System.out.println("No ConcoursColor with index " + aIndex );
            System.exit(-1);
        }
        return cc_i;
    }
    public Integer GetNextColorIndex(){
      Integer i = 1; // in keeping with other Concourse collections, index is 1-based
        for(ConcoursColor cc : concourscolorArray){
          if(cc.index > i){
              i = cc.index;
          }
      }
      return i + 1;
    }
    
}
