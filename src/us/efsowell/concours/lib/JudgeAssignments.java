/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Ed Sowell
 */
public class JudgeAssignments {
    ArrayList<JudgeAssignment> concoursJudgeAssignments ; 
 
    
// Constructor
    public JudgeAssignments(){
        concoursJudgeAssignments = new ArrayList<>();
    }


public ArrayList<JudgeAssignment> GetConcoursJudgeAssignments(){
    return concoursJudgeAssignments;
}

public void  SetConcoursJudgeAssignments(ArrayList<JudgeAssignment> aCJA){
    concoursJudgeAssignments = aCJA;
}

public ArrayList<Integer> getJudgeIndicies(Integer aEntryIndex){
        
      int intSize = concoursJudgeAssignments.size();
      JudgeAssignment aJudgeAssignment;
      ArrayList<Integer> theJudgeList = new ArrayList<>();
      for(int i = 0; i<intSize;i++){
          aJudgeAssignment = concoursJudgeAssignments.get(i);
          if(aJudgeAssignment.GetCarIndex() == aEntryIndex){
              theJudgeList = aJudgeAssignment.GetJudgeIndicies(); 
              break;
          }
      }
      return theJudgeList;
}
public void addJudgeAssignment(JudgeAssignment aJA){
    concoursJudgeAssignments.add(aJA);
}

public List<Integer> GetTeam(Integer aTargetCarIndex)   {
    JudgeAssignment aJA;
    Integer theCarIndex;
    List<Integer> result = new ArrayList<>();
    for(int i = 0; i< concoursJudgeAssignments.size(); i++){
        aJA = concoursJudgeAssignments.get(i);
        theCarIndex =aJA.GetCarIndex();
        if(theCarIndex == aTargetCarIndex){
           result.add(aJA.GetOwnerIndex()); // Add the Owner to the Team
           result.addAll(aJA.GetJudgeIndicies()); // Add the Judges to the Team
        }
    }    
    return result;
} 
public List<Integer> GetJudges(Integer aTargetCarIndex)   {
    JudgeAssignment aJA;
    Integer theCarIndex;
    List<Integer> result = new ArrayList<>();
    for(int i = 0; i< concoursJudgeAssignments.size(); i++){
        aJA = concoursJudgeAssignments.get(i);
        theCarIndex =aJA.GetCarIndex();
        if(theCarIndex == aTargetCarIndex){
           result.addAll(aJA.GetJudgeIndicies()); // Add the Judges 
        }
    }    
    return result;
} 

public List<Integer> GetOwners(Integer aTargetCarIndex)   {
    JudgeAssignment aJA;
    Integer theCarIndex;
    List<Integer> result = new ArrayList<>();
    for(int i = 0; i< concoursJudgeAssignments.size(); i++){
        aJA = concoursJudgeAssignments.get(i);
        theCarIndex =aJA.GetCarIndex();
        if(theCarIndex == aTargetCarIndex){
           result.add(aJA.GetOwnerIndex()); // Add the Owners 
        }
    }    
    return result;
} 

public JudgeAssignments GetJudgeAssignmentObj(){
    return this;
}


/*
 Clears all lists in preparation for loading new Judge Assignments after running the matching process
*/
public void ClearJudgeAssignments(){
    JudgeAssignment aJA;
    for(int i = 0; i< concoursJudgeAssignments.size(); i++){
       aJA = concoursJudgeAssignments.get(i);
       aJA.GetJudgeIndicies().clear();
    }
    concoursJudgeAssignments.clear();
}

public int getMaxTimeslotIndex(){
    int idxMax = 0;
    for (JudgeAssignment aJA : concoursJudgeAssignments) {
        if(aJA.GetTimeslot() > idxMax) idxMax =   aJA.GetTimeslot();          
    }
    return idxMax;
}


public int getNumEntriesInTimeSlot(int aTimeslotIndex){
    int num = 0;
    for (JudgeAssignment aJA : concoursJudgeAssignments) {
        if(aJA.GetTimeslot() == aTimeslotIndex) num++;          
    }
    return num;
}

/*
    If a move of an Entry to another timeslot leaves its original TS empty
    we have to decrement all succeding TS indicies to close the gap.
*/
public void closeGap(int aTimeslotIndex){
    for (JudgeAssignment aJA : concoursJudgeAssignments) {
        int idx = aJA.GetTimeslot();
        if(idx  > aTimeslotIndex){
            aJA.SetTheTimeslot(idx - 1);
        }          
    }
}


/*
private void updateJudgeLoads(Judges aJudges){
    
    
   
    //
    // Iterates through Judge Assignments and calculates the number of Entries assigned to each Judge
    //
    JudgeAssignment aJA;
    Integer theJudgeNode;
   // List<Integer> aJudgeList = new ArrayList<>();
    //System.out.println("Updating Judge Loads");
    
    aJudges.clearJudgeLoads(); 
    for(int i = 0; i< concoursJudgeAssignments.size(); i++){
        aJA = concoursJudgeAssignments.get(i);
        Iterator iterJudges = aJA.GetJudgeIndicies().iterator(); // This is the Judge list for the ith entry in JudgeAssignments
        while (iterJudges.hasNext()) {
            theJudgeNode = Integer.parseInt(iterJudges.next().toString());
            //System.out.println("Incrementing load for Judge Node: " + theJudgeNode.toString());
            aJudges.IncLoadForNode(theJudgeNode);
        }

    }    

}
*/


}
