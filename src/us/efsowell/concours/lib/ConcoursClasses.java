/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import au.com.bytecode.opencsv.CSVReader;
import editJA.JudgeAssignGUI;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Sowell
 */
public class ConcoursClasses {
    ArrayList<ConcoursClass> concoursClasses ; // All Classes in the concours. Initialized from CSV file ClassesCsv.txt as exported from the Excel Judges worksheet

    
// Constructor
    public ConcoursClasses(){
        concoursClasses = new ArrayList<>();
    }

public ArrayList<ConcoursClass> GetConcoursClasses(){
    return concoursClasses;
}
public ConcoursClasses GetConcoursClassesObject(){
    return this;
}

    public String getClassName(Integer aNode){
        
      int intSize = concoursClasses.size();
      ConcoursClass aConcoursClass;
      String theClassName = "";
      for(int i = 1; i<intSize;i++){
          aConcoursClass = concoursClasses.get(i);
          if(aConcoursClass.Node == aNode){
              theClassName = aConcoursClass.ClassName;
              break;
          }
      }
        return theClassName;
    }

    public ConcoursClass GetConcoursClass(Integer aNode){
        
      int intSize = concoursClasses.size();
      ConcoursClass aConcoursClass;
      ConcoursClass theTargetConcoursClass = null;
      for(int i = 1; i<intSize;i++){
          aConcoursClass = concoursClasses.get(i);
          if(aConcoursClass.Node == aNode){
              theTargetConcoursClass = aConcoursClass;
              break;
          }
      }
        return theTargetConcoursClass;
    }
    public Integer GetConcoursClassNode(String aClassName){
        
      int intSize = concoursClasses.size();
      ConcoursClass concoursClass;
      Integer theTargetConcoursClassNode = 0;
      for(int i = 0; i<intSize;i++){
          concoursClass = concoursClasses.get(i);
          if(concoursClass.ClassName.equals(aClassName)){
              theTargetConcoursClassNode = concoursClass.GetClassNode();
              break;
          }
      }
        return theTargetConcoursClassNode;
    }
    
    public boolean isAConcoursClassNode(String aClassName){
      boolean res = false;  
      for(ConcoursClass cc : concoursClasses){
          if(cc.ClassName.equals(aClassName)){
              res = true;
              break;
          }
      }
        return res;
    }
    
    public ConcoursClass GetConcoursClassObject(String aClassName){
        int intSize = concoursClasses.size();
        boolean found = false;
        ConcoursClass concoursClass = null;
        for(int i = 0; i<intSize;i++){
            concoursClass = concoursClasses.get(i);
            if(concoursClass.ClassName.equals(aClassName)){
                  found = true;
                  break;
            }
          }
        if(found){
            return concoursClass;
        } else{
            return null;
        }
    }

    public void AddConcoursClass(ConcoursClass aConcoursClass){
       concoursClasses.add(aConcoursClass);
    }
    public void RemoveConcoursClass(ConcoursClass aConcoursClass){
        int intSize = concoursClasses.size();
        ConcoursClass concoursClass = null;
        for(int i = 0; i<intSize;i++){
            concoursClass = concoursClasses.get(i);
            if(concoursClass.GetClassName().equals(aConcoursClass.GetClassName() )){
               concoursClasses.remove(i);
               break;
            }
          }
    }
  

    
    public Integer GetNextClassNode(){
        
      Integer theClassNode = 0;
      for(ConcoursClass cc : concoursClasses){
          if(cc.Node >= theClassNode){
              theClassNode = cc.Node;
          }
      }
      return theClassNode + 1;
    }

    
    public Integer getCount(Integer aNode){
        
      int intSize = concoursClasses.size();
      ConcoursClass aConcoursClass;
      Integer theCount = 0;
      for(int i = 1; i<intSize;i++){
          aConcoursClass = concoursClasses.get(i);
          if(aConcoursClass.Node == aNode){
              theCount = aConcoursClass.Count;
          }
      }
      
        
        return theCount;
        
    }
    

    
    public ArrayList<Integer> getClassEntryIndicies(Integer aNode){
        
      int intSize = concoursClasses.size();
      ConcoursClass aConcoursClass;
      ArrayList<Integer> theList = new ArrayList<>();
      for(int i = 1; i<intSize;i++){
          aConcoursClass = concoursClasses.get(i);
          if(aConcoursClass.Node == aNode){
              theList = aConcoursClass.GetClassEntryIndices(); 
              break;
          }
      }
        return theList;
        
    }

    public ArrayList<Entry> getClassEntryObjects(Integer aNode){
        
      int intSize = concoursClasses.size();
      ConcoursClass aConcoursClass;
      ArrayList<Entry> theList = new ArrayList<>();
      for(int i = 1; i<intSize;i++){
          aConcoursClass = concoursClasses.get(i);
          if(aConcoursClass.Node == aNode){
              theList = aConcoursClass.GetClassEntryObjects(); 
          }
      }
        return theList;
        
    }

    public boolean preassignedJudgeListsExists(){
        boolean result = false;
        for(ConcoursClass cc : concoursClasses){
            if(cc.preassignedJudgeIndicies.size() >0){
                result = true;
            }
        }
        return result;
    }
    
    /*
    public int getNumConcoursClassJudgingSlots(){
        int result = 0;
        for(ConcoursClass cc : concoursClasses){
            result += cc.numJudgingSlots;
        }
        return result;
    }
    */
}
