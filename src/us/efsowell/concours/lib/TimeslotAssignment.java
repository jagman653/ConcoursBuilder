 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import editJA.JudgeAssignGUI;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Ed Sowell
 */
import java.util.Comparator;
 

   public class TimeslotAssignment implements Comparable<TimeslotAssignment> {
        private int intTSId;
        List<Integer> lstTeamMembers ; // a list of all Owners & Judges active in the timeslot
        List<Integer> lstOwners;  // a list of all Owners active in the timeslot
        List<Integer> lstJudges;  // a list of all Judges active in the timeslot
        List<Integer> lstCars ; // a list of all cars in the timeslot
        List<Integer> lstConflicts ;   // a list of all Owners & Judges that have conflicts in the timeslot
        List<Integer> lstConflictCars ;   // a list of all cars with conflicted teams in the timeslot
        List<Integer>  lstAvailableJudgeIndices; // concours judges not assigned to and Entry in the timeslot
        List<Judge>  lstAvailableJudgeObjects; // concours judges not assigned to and Entry in the timeslot

        public static class OrderByEntriesSize implements Comparator<TimeslotAssignment> {

            @Override
            public int compare(TimeslotAssignment t1, TimeslotAssignment t2) {
                return t1.GetCarListSize() > t2.GetCarListSize() ? 1 : (t1.GetCarListSize() < t2.GetCarListSize() ? -1 : 0);
            }
        }

       public static class ReverseOrderByEntriesSize implements Comparator<TimeslotAssignment> {

            @Override
            public int compare(TimeslotAssignment t1, TimeslotAssignment t2) {
                return t2.GetCarListSize() > t1.GetCarListSize() ? 1 : (t2.GetCarListSize() < t1.GetCarListSize() ? -1 : 0);
            }
        }

// Constructor
    public TimeslotAssignment(int ID, Concours aConcours){
        intTSId = ID;
        lstTeamMembers = new ArrayList<>();
        lstOwners = new ArrayList<>();
        lstJudges = new ArrayList<>();
        lstCars = new ArrayList<>();
        lstConflicts = new ArrayList<>();
        lstConflictCars = new ArrayList<>();
        lstAvailableJudgeIndices = new ArrayList<>(); // as judge Nodes
        lstAvailableJudgeObjects = new ArrayList<>(); // as judge Nodes
        Integer judgeNode;
        //for(Judge j : JudgeAssignGUI.theConcours.GetConcoursJudges()){
        for(Judge j : aConcours.GetConcoursJudges()){
            judgeNode = j.GetNode();
            lstAvailableJudgeIndices.add(judgeNode);
            lstAvailableJudgeObjects.add(j);
        }
    }

// Alternative Constructor for an empty TimeslotAssignment
    public TimeslotAssignment(int ID){
        intTSId = ID;
        lstTeamMembers = new ArrayList<>();
        lstOwners = new ArrayList<>();
        lstJudges = new ArrayList<>();
        lstCars = new ArrayList<>();
        lstConflicts = new ArrayList<>();
        lstConflictCars = new ArrayList<>();
        lstAvailableJudgeIndices = new ArrayList<>(); // as judge Nodes
        lstAvailableJudgeObjects = new ArrayList<>(); // as judge Nodes
    }
    
    // Copy Constructor
    //used in SwitchTimeslots
    //  
    public TimeslotAssignment(TimeslotAssignment aTimeslaotAssignment){
        intTSId = aTimeslaotAssignment.getID(); // 
        lstTeamMembers = aTimeslaotAssignment.lstTeamMembers;
        lstOwners =   aTimeslaotAssignment.lstOwners;
        lstJudges = aTimeslaotAssignment.lstJudges;
        lstCars = aTimeslaotAssignment.lstCars;
        lstConflicts = aTimeslaotAssignment.lstConflicts;
        lstConflictCars = aTimeslaotAssignment.lstConflictCars;
        lstAvailableJudgeIndices = aTimeslaotAssignment.lstAvailableJudgeIndices; // as judge Nodes
        lstAvailableJudgeObjects = aTimeslaotAssignment.GetAvailableJudgeObjects(); // as judge Nodes
    }
   
    //  
    // This populates the TimeslotAssignment identified by aID with the data of aTimeslaotAssignment
    //  Used in SwitchTimeslots
    
    // Note that ID is NOT changed.
    public void switchTimeslotAssignment(TimeslotAssignment aTimeslaotAssignment){
        this.lstTeamMembers = aTimeslaotAssignment.lstTeamMembers;
        this.lstOwners =   aTimeslaotAssignment.lstOwners;
        this.lstJudges =   aTimeslaotAssignment.lstJudges;
        this.lstCars = aTimeslaotAssignment.lstCars;
        this.lstConflicts = aTimeslaotAssignment.lstConflicts;
        this.lstConflictCars = aTimeslaotAssignment.lstConflictCars;
        this.lstAvailableJudgeIndices = aTimeslaotAssignment.lstAvailableJudgeIndices; // as judge Nodes
        this.lstAvailableJudgeObjects = aTimeslaotAssignment.GetAvailableJudgeObjects(); // as judge Objects
    }
    
    public void clearTSLists(){
       lstTeamMembers.clear();
       lstOwners.clear();
       lstJudges.clear();
       lstCars.clear();
       lstConflicts.clear();
       lstConflictCars.clear();
    }
        
    public List<Integer> GetTeamMembers(){
        return lstTeamMembers;
    }
    public List<Integer> GetOwners(){
        return lstOwners;
    }
    public List<Integer> GetJudges(){
        return lstJudges;
    }
    public List<Integer> GetAvailableJudgeIndices(){
        return lstAvailableJudgeIndices;
    }
    
        public List<Judge> GetAvailableJudgeObjects(){
        return lstAvailableJudgeObjects;
    }

    public  boolean addOwner(Integer theOwner){
        boolean aConflict = false;
    
        if(lstTeamMembers.contains(theOwner)){
            lstConflicts.add(theOwner);
            aConflict = true;
        }
        else{
            lstTeamMembers.add(theOwner);
            lstOwners.add(theOwner);
        }
        
        
        return aConflict;
    }

public  boolean addJudge(Integer theJudgeNode){
    Judge theJudgeObject;
    boolean aConflict = false;// added OK so not a dup
    if(lstTeamMembers.contains(theJudgeNode)){
        lstConflicts.add(theJudgeNode);
        aConflict = true;
    }
    else{
        lstTeamMembers.add(theJudgeNode);
        lstJudges.add(theJudgeNode);
    }
    
    // remove from list of available Judge indicies
    if(lstAvailableJudgeIndices.contains(theJudgeNode)){
        lstAvailableJudgeIndices.remove(theJudgeNode);
    }
    
    //LOOK at this LOGIC!!!
    //remove from list of available Judge objects
    int size = lstAvailableJudgeObjects.size();
    int toBeRemovedIndex = -1;
    for(int i = 0; i < size; i++){
      theJudgeObject = lstAvailableJudgeObjects.get(i);
      if(theJudgeObject.Node == theJudgeNode){
            toBeRemovedIndex = i;
            break;
      }
    }
    if(toBeRemovedIndex == -1){
        System.out.println("Judge node: " + theJudgeNode + " not found in list of available judges. Probably means same judge assigned to entry twice.");
        //okDialog("Judge node: " + theJudgeNode + " not found in list of available judges. Probably means same judge assigned to entry twice.");
        //System.exit(-1);
    } else {
        lstAvailableJudgeObjects.remove(toBeRemovedIndex);
    }
           
        
    
    return aConflict;
 }

public  void addCar(Integer theCar){
    if(lstConflicts.size() > 0){
        lstConflictCars.add(theCar);
    }
    else{
        lstCars.add(theCar);
    }
    
 }

public  int GetCarListSize(){
    return lstCars.size();
    
}
public List<Integer> lstGetConflicts(){
        return lstConflicts;
}
public List<Integer> lstConflictCars(){
        return lstConflictCars;
}
public int getConflictedCar(int i){
    return lstConflictCars.get(i);
    
}

public int getID(){
    return intTSId;
}

public void setID(int aID){
    intTSId =  aID;
}
public int getValue(){
    return intTSId;
}

//
// Sorting on intTSId is natural sorting for Order.
//
@Override
    public int compareTo(TimeslotAssignment t) {
        return this.intTSId > t.intTSId ? 1 : (this.intTSId < t.intTSId ? -1 : 0);

    
    }
    //
    // implementing toString method to print intTSId of Timeslot
    //
    @Override
    public String toString(){
        return String.valueOf(intTSId);
    }


}
