/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Ed Sowell
 */
public class Owners {
    ArrayList<Owner> concoursOwnerList; 

// Constructor
    public Owners(){
        concoursOwnerList = new ArrayList<>();
    }


    
    public String getOwnerName(Integer aNode){
        
      int intSize = concoursOwnerList.size();
      //int k; 
      Owner aOwner;
      String theName = "";
      
      
      for(int i = 0; i<intSize;i++){
          aOwner = concoursOwnerList.get(i);
          if(aOwner.node == aNode){
              theName = aOwner.getUniqueName();
          }
      }
      if( "".equals(theName)){
        JOptionPane.showMessageDialog(null, "Owner with Node " + aNode + " not found in concours Owners list");
           //System.out.println("Owner with Node " + aNode + " not found in concours Owners list");
          // System.exit(-1);
        }
      return theName;
    }

public Integer GetOwnerNode(String aUnique_name)  {
    Integer theNode;
    theNode = 0;
    for(Owner o : concoursOwnerList){
        if(o.getUniqueName().equals(aUnique_name)){
            theNode = o.GetNode();
            break;
        }
    }
    /*if(theNode == 0){
        JOptionPane.showMessageDialog(null, "ERROR: Person " + aUnique_name + " is not in Concourse Owners list.");
    }
     */
    return theNode;
}

public Owner GetOwner(String aUnique_name)  {
    Integer theNode;
    Owner theOwner = null;
    for(Owner o : concoursOwnerList){
        if(o.getUniqueName().equals(aUnique_name)){
            theOwner  = o;
            break;
        }
    }
    return theOwner;
}  


public void AddOwner(Owner aOwner){
    concoursOwnerList.add(aOwner);
}

public void RemoveOwner(String aOwnerUnique){
    for(int i = 0; i< concoursOwnerList.size(); i++){
        if(concoursOwnerList.get(i).getUniqueName().equals(aOwnerUnique)){
            concoursOwnerList.remove(i);
            break;
        }
    }
}


   public ArrayList<Owner> GetConcoursOwners(){
    return concoursOwnerList;
}
   public Owners GetConcoursOwnersObject(){
    return this;
}
    
}
