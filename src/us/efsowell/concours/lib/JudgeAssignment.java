/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;
//import java.lang.*;
import editJA.JudgeAssignGUI;
import java.util.List;

import java.util.ArrayList;



/**
 *
 * @author Ed Sowell
 */
public class JudgeAssignment {
    int theCarIndex;
    int theOwnerIndex;
    int theTimeslot;
    String strReport= "";
    ArrayList<Integer> lstTheJudgeIndices ;

    public JudgeAssignment(){
    // sometimes a null JudgeAssignment is needed to keep the compiler happy! See ChangeEntryTimeslot() in Concourse.jave
        lstTheJudgeIndices = new ArrayList<>();
        int p1;
        int p2;
        int p3;
        int p4;
        int  intLen;
        p1 = 0;
        p2 = 0;
        p3 = 0;
        p4 = 0;
        intLen = 0;
    }
    
          
    // Constructor
    // L is a line from the JudgeAssign.txt file. It is a String of the form 
    //  "carIndex { (ownerIndex) judgeIndex judgeIndex judgeIndex } Timeslot"
    //   where all elements are integers. This Constructor extracts the Car Class data elements from
    //  L.
    // 
    public JudgeAssignment(String L){
        lstTheJudgeIndices = new ArrayList<>();
        int p1;
        int p2;
        int p3;
        int p4;
        int  intLen;
        String strTheCarIndex;
        String strOwnerIndex;
        String[] theJudgeIndex ;
        String strTimeslot;
        String aJudgeIndex;
        int intCount ;
        Entry entry;
 
        intLen = L.length();
        p1 = L.indexOf("{");
        p2 = L.indexOf("}");
        if( (p1 == -1) || (p2 == -1) ){
                strReport = strReport + " Bad format in JudgeAssign.txt file line: " + L;
            }
            else{
                strTheCarIndex = L.substring(0, p1);
                theCarIndex = Integer.parseInt(strTheCarIndex.trim()); //does not include the char at p1
                strTimeslot = L.substring(p2+1);
		theTimeslot = Integer.parseInt(strTimeslot.trim());
                entry = JudgeAssignGUI.theConcours.theEntries.getEntry(theCarIndex);
                entry.SetTimeslotIndex(theTimeslot);
                // Now  get OwnerIndex & 2 or 3 judges
                p3 = L.indexOf("(");
                p4 = L.indexOf(")");
                if( (p3 == -1) || (p4 == -1) ){
                    strReport = strReport + " Bad format in JudgeAssign.txt file line: " + L;
                }
                else{
                 strOwnerIndex = L.substring(p3+1,p4);
                 theOwnerIndex = Integer.parseInt(strOwnerIndex.trim());
                 String strJudgeIndex = L.substring(p4+1, p2).trim();
                 theJudgeIndex = strJudgeIndex.split("\\s+");
                 intCount = theJudgeIndex.length;
                 
                 for (int i = 0; i<intCount;i++){
                    aJudgeIndex = theJudgeIndex[i].trim();
                    lstTheJudgeIndices.add(Integer.parseInt(aJudgeIndex));
                 }
                }
                
            }
        
    }
    
    public JudgeAssignment(int aCarIndex, int aOwnerIndex,  ArrayList<Integer> aJudgeIndicesList, int aTimeslot){
        theCarIndex = aCarIndex;
        theOwnerIndex = aOwnerIndex;
        theTimeslot = aTimeslot;
        strReport= "";
        lstTheJudgeIndices = aJudgeIndicesList;
    }    

public int GetCarIndex(){
        return theCarIndex;
    }
public void SetTheCarIndex(int aCar){
        theCarIndex = aCar;
  
}
public int GetOwnerIndex(){
        return theOwnerIndex;
}
public void SetTheOwnerIndex(int anOwnerIndex){
        theOwnerIndex = anOwnerIndex;
}
public int GetTimeslot(){
        return theTimeslot;
}
public void SetTheTimeslot(int aTimeslot){
        theTimeslot = aTimeslot;
        
}
public ArrayList<Integer> GetJudgeIndicies(){
        return lstTheJudgeIndices;
}
public void SetTheJudges(int[] aJudgesArray){
        lstTheJudgeIndices.clear();
        int len = aJudgesArray.length;
       for(int i = 0; i< len; i++){
           String strJ = Integer.toString(aJudgesArray[i]);
           lstTheJudgeIndices.add(Integer.parseInt(strJ)) ;
       }
}

public void SetTheJudges(List<Integer> aJudgesIndexLst){
        lstTheJudgeIndices = new ArrayList<>(aJudgesIndexLst);
}

    public int GetJudgeCount(){
        return lstTheJudgeIndices.size();
    }

}

