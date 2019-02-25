/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import editJA.JudgeAssignGUI;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ed Sowell
 */
public class ConcoursClass {
    
String ClassName;
Integer Node;
Integer Count;
ArrayList<Integer> ClassEntryIndices;
ArrayList<Entry> ClassEntryObjects;
ArrayList<Integer> ClassJudgeIndicies;
ArrayList<Judge> ClassJudgeObjects;
ArrayList<Integer> preassignedJudgeIndicies;
ArrayList<String> preassignedJudgeUniqueNames;

// 8/31/2017
ArrayList<Integer> iClassJudgeOrder; // List of Integers with designated Lead Judge in 0th position.
                                    // Used to order the Judge names in the Schedule tables



// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
// The following code is ill considered. Problem is, to make it consistant with other parts of the code it would also
// have to be written to the Database which is a lot of work and extra maintenance. All of this should be done in the
// SchedulingInterfaceJava.
//
/*
ArrayList<JudgingSlot> judgingSlots ; // 2 slots for Driven class, 3 for Championship
int numJudgingSlots;
int judgingSlotPosition; // judgingSlot where next judge candidate will be added. Computed mod(2) od mod(3) depending on driven or Championship

    
        private static final class JudgingSlot {
            ArrayList<Integer> judgecandidates;
            // constructor
            public JudgingSlot(){
                judgecandidates = new ArrayList<Integer>();
            }
            
            public void addJudgecandidate(int candidate){
                judgecandidates.add(candidate);
            }
            
            public int getJudgecandidate(int candidate){
                return judgecandidates.get(candidate);
            }
            
            public  ArrayList<Integer> getJudgeCandidates(){
                return judgecandidates;
            }
            
        }
        
        private void addJudgingSlot(){
            JudgingSlot js = new JudgingSlot();
            judgingSlots.add(js);
        }
    // judgingSlotPosition is the judgingSlot into which judgecandidate will be inserted.
    // judgingSlotPosition is incremented mod(2) or mod(3) depending on number of judgingSlots for the Class.
    // This distributes the judges available to judge this class evenly among the 2 or 3 slots for the class.
    public void addJudgingCandidate(Integer judgecandidate){
        judgingSlots.get(judgingSlotPosition).addJudgecandidate(judgecandidate) ; 
        judgingSlotPosition = judgingSlotPosition++ % numJudgingSlots; 
    }

// ---------------------------------------------------------------------------        
 */

// Constructor

//   This is indeed being used!
    public ConcoursClass(Concours aConcours, String aClassName, Integer aNode, Integer aCount, ArrayList<Integer> aEntryIndexList,  ArrayList<Entry> aEntryObjectList, ArrayList<Integer> aJudgeIndexList, ArrayList<Integer> aPreassignedJudgeIndexList,  ArrayList<String> aPreassignedJudgeUniqueNameList){
        //String msg = "ConcoursClass(Full argument list) is indeed being used!" ;
        //okDialog(msg);
        //aConcours.GetLogger().info(msg);
        ClassName = aClassName;
         Node = aNode;
         // 
         /*
         judgingSlots = new ArrayList<>();
         numJudgingSlots = ("D".equals(aClassName.substring(0, 1))) ? 2:3 ; // 2 slots for driven, 3 if Championship
         for(int i = 0; i < numJudgingSlots; i++){
            addJudgingSlot(); 
         }
         judgingSlotPosition = 0; // position in Class judging slots.  0 to numJudgingSlots-1 
         */
         Count = aCount;  // should not be used. Use dynamic calculation ClassEntryIndices.size()
         ClassEntryIndices = aEntryIndexList;
         ClassEntryObjects = aEntryObjectList;
         ClassJudgeIndicies = aJudgeIndexList;
         ClassJudgeObjects = new ArrayList<>();
         preassignedJudgeIndicies = aPreassignedJudgeIndexList; // new list was created by the constructor caller and passed in as an argument
         preassignedJudgeUniqueNames = aPreassignedJudgeUniqueNameList; // new list was created by the constructor caller and passed in as an argument
         iClassJudgeOrder = new ArrayList<>();

         for(Integer ji : aJudgeIndexList){
             Judge judge = aConcours.GetConcoursJudge(ji);
             if(judge == null){
                 String msg = "No Judge with node " + ji + " is  in Concours Judges";
                 aConcours.GetLogger().info(msg);
                JOptionPane.showMessageDialog(null, msg);
                System.exit(-1);
             } else{
                 ClassJudgeObjects.add(judge);
                 iClassJudgeOrder.add(ji); // default order   This must be done AFTER the Judge Assignments & Schedule are built !!!!!!!!!!!!!
             }
         }
    }    
// Alternative Constructor for Adding Entries from the GUI
    public ConcoursClass(Concours aConcours, String aClassName, Integer aNode){
          
         ClassName = aClassName;
         Node = aNode;
         Count = 0;
         ClassEntryIndices = new ArrayList<>();
         ClassEntryObjects =  new ArrayList<>();
         ClassJudgeIndicies =  new ArrayList<>();
         ClassJudgeObjects = new ArrayList<>();
         preassignedJudgeUniqueNames = new ArrayList<>();
         preassignedJudgeIndicies = new ArrayList<>();
         iClassJudgeOrder = new ArrayList<>();
         /*
         for(Integer ji : aJudgeIndexList){
             Judge judge = aConcours.GetConcoursJudge(ji);
             if(judge == null){
                JOptionPane.showMessageDialog(null, "No Judge with node " + ji + " is  in Concours Judges");
             } else{
                 ClassJudgeObjects.add(judge);
                 iClassJudgeOrder.add(ji); // default order   This must be done AFTER the Judge Assignments & Schedule are built !!!!!!!!!!!!!
             }
         }
         */
    }    

    public void AddEntryIndex(Integer aEntryIndex){
        ClassEntryIndices.add(aEntryIndex);
    }
    public void AddEntryObject(Entry aEntry){
        ClassEntryObjects.add(aEntry);
    }

    public void RemoveEntryIndex(Integer aEntryIndex){
        for(int i = 0; i < ClassEntryIndices.size(); i++){
            if(ClassEntryIndices.get(i) == aEntryIndex){
               ClassEntryIndices.remove(i);
               break;
            }
        }
    }
    
    
    public void RemoveEntryObject(Entry aEntry){
        for(int i = 0; i < ClassEntryObjects.size(); i++){
            if(ClassEntryObjects.get(i).GetNode() == aEntry.GetNode()){
               ClassEntryObjects.remove(i);
               break;
            }
        }
    }
 
    
    
    public void AddJudgeIndex(Integer aJudgeIndex){
        ClassJudgeIndicies.add(aJudgeIndex);
    }
    public void AddJudgeObject(Judge aJudge){
        ClassJudgeObjects.add(aJudge);
    }
 
    public void RemoveJudgeIndex(Integer aJudgeIndex){
        if (ClassJudgeIndicies.contains(aJudgeIndex)) {
            ClassJudgeIndicies.remove(aJudgeIndex);
        }
    }
    public void RemoveJudgeObject(Judge aJudge){
        if (ClassJudgeObjects.contains(aJudge)) {
            ClassJudgeObjects.remove(aJudge);
        }
    }

    public void RemoveAllJudges(){
        ClassJudgeIndicies.clear();
        ClassJudgeObjects.clear();
    }
    
    
    public String GetClassName(){
        return ClassName;
    }
    public Integer GetClassNode(){
        return Node;
    }
    
    public int GetClassDivision(){
        // returns 1 if Driven, 2 if Championship or Special
        int division = -1;  // won't be used
            String strDivision = ClassName.substring(0, 1);
            if ("D".equals(strDivision)) {
                division = 1;
            } else {
                if ("C".equals(strDivision) || "S".equals(strDivision)) {
                    division = 2;
                } else {
                    okDialog("ERROR: First character in Class " + ClassName + " is not C, D, or S.");
                    JOptionPane.showMessageDialog(null, "ERROR: First character in Class " + ClassName + " is not C, D, or S.");
                    System.exit(-1);
                }
            }
        return division;
    }
    public Integer GetClassCount(){
        //return Count;
        return ClassEntryIndices.size();
    }
    public ArrayList<Integer>  GetClassJudgeIndices(){
        return ClassJudgeIndicies;
    }
    public ArrayList<String>  GetClassJudgeUniqueNames(){
        ArrayList<String> result = new ArrayList<>();
        for(Judge j : ClassJudgeObjects){
            result.add(j.JudgeUniqueName);
        }
        return result;
    }
    
    public Judge GetClassLeadJudge(){
         // User sets Lead Judge in SetLeadJudgeInputDialog in 
         // Edit menu of JudgeAssignDialog, which moves the 
         //selected Judge to position 0        
        return ClassJudgeObjects.get(0); 
    }

    public ArrayList<Judge>  GetClassJudgeObjects(){
        return ClassJudgeObjects;
    }

    
    public ArrayList<Integer>  GetClassEntryIndices(){
        return ClassEntryIndices;
    }
    public ArrayList<Entry>  GetClassEntryObjects(){
        return ClassEntryObjects;
    }
    
    public void AddPreassignedJudge(Connection aConn, Judge aJudge) {
        String jun = aJudge.getUniqueName();
        int jnode = aJudge.GetNode();
        if(!preassignedJudgeUniqueNames.contains(jun)){
            preassignedJudgeUniqueNames.add(jun);
            preassignedJudgeIndicies.add(jnode);
            LoadSQLiteConcoursDatabase.UpdateAddConcoursClassPreassignedJudgesTable( aConn, ClassName, jnode, jun);
        }
    }

    public void RemoveAllPreassignedJudges(Connection aConn) {
        // For THIS class, obviously
        preassignedJudgeUniqueNames.clear();
        preassignedJudgeIndicies.clear();
        // remove them also from database
        LoadSQLiteConcoursDatabase.UpdateRemoveConcoursClassPreassignedJudgesTable(aConn, ClassName);
    }
    
    public ArrayList<String>  GetClassPreassignedJudgeNameList(){
        return preassignedJudgeUniqueNames;
    }
    public ArrayList<Integer>  GetClassPreassignedJudgeIndiciesList(){
        return preassignedJudgeIndicies;
    }
    
    
	/**
	 *	The ClassName property will double as the toString representation.
	 *
	 *  @return the ID
	 */
	@Override
	public String toString()
	{
		return ClassName;
	}
	/**
	 *	The Node property will double as the Value representation.
	 *
	 *  @return the Node
	 */
    

        public Integer getValue(){
            return Node;
        }
    
        //
        // Put Lead Judge as first in ClassJudgeIndicies and ClassJudgeObjects
        //
        public boolean reorderJudges(Concours aConcours, Judge aLeadJudgeObject){
            
            int pos; // if aLeadJudgeNode is actually in the list for the class, this will be it's index in the list
            boolean found = false;
            pos = 0;
            for(Judge judge : ClassJudgeObjects){
                if( judge.Node == aLeadJudgeObject.Node){
                    found = true;
                    break;
                }
                pos++;
            }
            String msg;
                                    
            if(!found){
                msg = "No Judge " + aLeadJudgeObject.JudgeUniqueName + " for this Class";
                okDialog(msg);
                aConcours.GetLogger().info(msg);
                return false;
            } else{
                // Put the Lead Judge at the front of the lists
                int res = ClassJudgeIndicies.remove(pos);
                ClassJudgeIndicies.add(0, aLeadJudgeObject.Node);
                ClassJudgeObjects.remove(pos);
                ClassJudgeObjects.add(0, aLeadJudgeObject);
            }
            return true;
        }
}
