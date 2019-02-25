/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Sowell
 */
public class Judges {
    ArrayList<Judge> concoursJudges; // All Judges in the concours. Initialized from CSV file JudgesCsv.txt as exported from the Excel Judges worksheet
 
    
// Constructor
    
    public Judges(){
        concoursJudges = new ArrayList<>();
    }

    public void AddJudge(Judge aJudge){
        concoursJudges.add(aJudge);
    }
    public void RemoveJudge(Judge aJudge){
        concoursJudges.remove(aJudge);
    }
    public void clearJudgeLoads(){
      int intSize = concoursJudges.size();
      Judge aJudge;
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          aJudge.SetLoad(0);
      }
    }
    public String GetJudgeID(Integer aNode){
        
      int intSize = concoursJudges.size();
      Judge aJudge;
      String theID = "";
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          if(aJudge.Node == aNode){
              theID = aJudge.ID;
          }
      }
      
        
        return theID;
        
    }
    
    public String getJudgeLastName(Integer aNode){
        
      int intSize = concoursJudges.size();
      Judge aJudge;
      String theName = "";
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          if(aJudge.Node == aNode){
              theName = aJudge.Name;
          }
      }
      
        
        return theName;
        
    }
    public int GetLoadForNode(Integer aNode){
        
      int intSize = concoursJudges.size();
      Judge aJudge;
      int theLoad = 0;
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          if(aJudge.Node == aNode){
              theLoad = aJudge.GetLoad();
          }
      }
        
        return theLoad;
    }

   public void SetLoadForNode(Integer aNode, int aLoad){
      
      int intSize = concoursJudges.size();
      Judge aJudge;
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          if(aJudge.Node == aNode){
              aJudge.SetLoad(aLoad);
          }
      }
   }
        
   public void IncLoadForNode(Integer aNode){
      
      int intSize = concoursJudges.size();
      Judge aJudge;
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          if(aJudge.Node == aNode){
              aJudge.IncLoad();
          }
      }
       
    }
   
   public ArrayList<Judge> GetConcoursJudges(){
    return concoursJudges;
}
   public Judges GetConcoursJudgesObject(){
    return this;
}

  public Judge GetConcoursJudge(Integer aJudgeNode){
      int intSize = concoursJudges.size();
      Judge aJudge;
      Judge theJudge = null;
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          if(aJudge.Node == aJudgeNode){
              theJudge = aJudge;
              break;
          }
      }
      if(theJudge == null){
          System.out.println("Judge not found with Node " + aJudgeNode  );
      }
    return theJudge; // this has to be checked by the caller
}

    public Judge GetConcoursJudge(String aUnigueName){
      int intSize = concoursJudges.size();
      Judge aJudge;
      Judge theJudge = null;
      for(int i = 0; i<intSize;i++){
          aJudge = concoursJudges.get(i);
          if(aJudge.JudgeUniqueName.equals(aUnigueName)){
              theJudge = aJudge;
              break;
          }
      }
      if(theJudge == null){
          System.out.println("Judge not found with UniqueName  " + aUnigueName  );
      }
    return theJudge; // this has to be checked by the caller
}

 public int PersonInJudgeList(String aUnigueName){
      int intSize = concoursJudges.size();
      Judge theJudge;
      int theJudgeNode = 0;
      for(int i = 0; i<intSize;i++){
          theJudge = concoursJudges.get(i);
          if(theJudge.JudgeUniqueName.equals(aUnigueName)){
              theJudgeNode = theJudge.GetNode();
              break;
          }
      }
       return theJudgeNode;
}

public String GetNextJudgeID(){
    // Judge ID is of the form "Jn" where n is an integer
    String strID;
    String strIDnum;
    Integer intIDnum;
    Integer numMax = 0;
    Integer nextNum;
    String result;
    int intSize = concoursJudges.size();
    Judge theJudge;
    for(int i = 0; i<intSize;i++){
        theJudge = concoursJudges.get(i);
        strID = theJudge.GetID();
        strIDnum = strID.substring(1); // s.b. just the numerical digits
        intIDnum= Integer.parseInt(strIDnum);
        if(intIDnum > numMax){
            numMax = intIDnum;
        }
    }

    result = "J" + (numMax + 1);
    return result;
}

}
