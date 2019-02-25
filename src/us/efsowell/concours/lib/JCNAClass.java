/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Ed Sowell
 */
public class JCNAClass implements Comparable<JCNAClass> {

    Long ID; // Database ID
    String division;
    String name;
    String description;
    String notes;
    String judgeassigngroup;
    String modelyearlookup;
    Integer node;
    boolean visible;

    ///////////////
    // 
    /*
    public JCNAClass(Long aID, String aDivision, String aName, String aDescription, String aNotes, String aJudgeAssignGroup, Integer aNode) {
        //ID = aID;
        division = aDivision;
        name = aName;
        description = aDescription;
        notes = aNotes;
        judgeassigngroup = aJudgeAssignGroup;
        node = aNode;
    }
    */
    
    // Alternative Constructor for adding 
    public JCNAClass(String aDivision, String aName, String aDescription, String aNotes, String aJudgeAssignGroup, String aModelYearLookup, Integer aNode) {
        division = aDivision;
        name = aName;
        description = aDescription;
        notes = aNotes;
        judgeassigngroup = aJudgeAssignGroup;
        modelyearlookup =  aModelYearLookup;
        node = aNode;
    }

    @Override
    public String toString() {
        return name  + ": " + description;
    }

    public JCNAClass getObject() {
        return this;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String aDivision) {
        division = aDivision;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String aNotes) {
        notes = aNotes;
    }

    public String getJudgeAssignGroup() {
        return judgeassigngroup;
    }

    public void setJudgeAssignGroup(String aJudgeassigngroup) {
        judgeassigngroup = aJudgeassigngroup;
    }

    public String getModelYearLookup() {
        return modelyearlookup;
    }

    public void setModelYearLookup(String aModelYearLookup) {
        modelyearlookup = aModelYearLookup;
    }
    
    
    public Integer getNode() {
        return node;
    }

    public void setNode(Integer aNode) {
        node = aNode;
    }

    public String getNameDescription() {
        return name + ": " + description;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println(getName() + ".setVisible:" + visible);
    }

    public void doSomething() {
        System.out.println(getName() + ".setVisible:" + visible);
    }

    @Override
    public int compareTo(JCNAClass t) {
        //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this.getName().compareTo(t.getName());
    }

    public static class JCNAClassAction extends AbstractAction {

        private JCNAClass jcnaClass;
            // The following is the conctructor for JCNAClassAction
        // First, it calls the AbstractAction(String s) constructor

        public JCNAClassAction(JCNAClass jcnaClass) {
                // The following super() calls the AbstractAction(String s) constructor
            // with the String argument s = the name of JCNAClass instance jcnaClass.
            // The net effect is to allow the JCNA class name to appear in the list...
            // without this super() call the checkbox appears without any text!
            super(jcnaClass.getNameDescription());
            this.jcnaClass = jcnaClass;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            System.out.println("ActionPerformed on JCNAClass checkbox");
            // if(ae.)
        }

    }

}
