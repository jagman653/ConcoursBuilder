/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

/**
 *
 * @author jag_m_000
 */
public class ConcoursColor {
    String name;
    int index; // in keeping with other Concourse collections, index is 1-based
    int r;
    int g;
    int b;
    public ConcoursColor(String aName, int aIndex, int aR, int aG, int aB){
        name = aName;
        index = aIndex;
        r = aR;
        g = aG;
        b = aB;
    }
    public String getName(){
            return name;
    }
    public int getR(){
            return r;
    }
    public int getG(){
            return g;
    }
    public int getB(){
            return b;
    }
    
    public void setName(String aName){
        
    }
    
    public void setR(int aR){
        r = aR;
    }
    public void setG(int aG){
        g = aG;
    }
    public void setB(int aB){
        b = aB;
    }
    
}
