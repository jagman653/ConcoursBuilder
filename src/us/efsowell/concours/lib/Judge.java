/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;

/**
 *
 * @author Ed Sowell
 */
public class Judge {

    String Name;
    String JudgeFirst;
    String JudgeUniqueName;
    Integer JCNA;
    Integer Year;
    String Club;
    String ID;
    ArrayList<String> selfEntriesClasses;
    ArrayList<String> rejectClasses;
    Integer Node;
    String Status;
    Integer Load;
   // Long cpid; // should not be here since when the Judge is added to the data structures it is not yet in the database table

    // Constructor
    // NOTE: ArrayList<String> aSelfEntryClasses has to be set when
    //      (a) an Judge is added that is an Owner, i.e., the Judge is in the personnel list but not the Judge list. In this case
    //          there may be more than one Class to be added to the aSelfEntryClasses.
    //       
    //      (b) a new Entry is added and the Owner is in the Judge List. 
    //      So the best way to handle this is to leave it out of the constructor and add 
    public Judge(String aName, String aJudgeFirst, String aJudgeUniqueName, Integer aJCNA, Integer aYear, String aClub, String aID, ArrayList<String> aRejectClasses, String aStatus, Integer aNode, int aLoad) {
        //System.out.println("Constructing judge: ");
        Name = aName;
        JudgeFirst = aJudgeFirst;
        JudgeUniqueName = aJudgeUniqueName;
        JCNA = aJCNA;
        Year = aYear;
        Club = aClub;
        ID = aID;
        rejectClasses = aRejectClasses;
        Node = aNode;
        Status = aStatus;
        Load = aLoad;

        selfEntriesClasses = new ArrayList<>();
    }
    @Override
    public String toString() {
        return Name + "/" + Integer.toString(Node);
    }

    public void AddSelfEntry(String aSelfEntryClassName) {
        selfEntriesClasses.add(aSelfEntryClassName);
    }

    public void RemoveSelfEntry(String aSelfEntryClassName) {
        for (int i = 0; i < selfEntriesClasses.size(); i++) {
            if (selfEntriesClasses.get(i).equals(aSelfEntryClassName)) {
                selfEntriesClasses.remove(i);
            }
        }
    }


    
    public int GetLoad() {
        return Load.intValue();

    }

    public String GetLastName() {
        return Name;

    }
    public String GetFirstName() {
        return JudgeFirst;

    }
    public String getUniqueName() {
        return JudgeUniqueName;
    }

    public Integer GetNode() {
        return Node;

    }

    public String GetID() {
        return ID;
    }

    public ArrayList<String> GetSelfEntryClasses() {
        return selfEntriesClasses;
    }

    public void SetRejectClasses(ArrayList<String> aRejectList) {
        rejectClasses = aRejectList;
    }
    public void SetRejectClass(String aRejectedClass) {
        rejectClasses.add(aRejectedClass);
    }
    
    public ArrayList<String> GetRejectClasses(){
        return rejectClasses;
    }

    public void SetLoad(int aLoad) {
        Load = Integer.valueOf(aLoad);
    }

    public void IncLoad() {
        Load = Load + 1;
    }


    /**
     * The Node property will double as the Value representation.
     *
     * @return the Node
     */

    public Integer getValue() {
        return Node;
    }

   
    //
    //  JudgeAction is used to set up a check-box list in CustomJudgeTeamDialog
    //
    public static class JudgeAction extends AbstractAction {
    
        private Judge judge;
            // The following is the conctructor for JudgeAction
        // First, it calls the AbstractAction(String s) constructor

        public JudgeAction(Judge judge) {
                // The following super() calls the AbstractAction(String s) constructor
            // with the String argument s = the name of JCNAClass instance jcnaClass.
            // The net effect is to allow the JCNA class name to appear in the list...
            // without this super() call the checkbox appears without any text!
            super(judge.getUniqueName());
            this.judge = judge;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
        // this does nothing.  Not needed
            //System.out.println("ActionPerformed on Judge checkbox");
            // if(ae.)
        }

    }
}
