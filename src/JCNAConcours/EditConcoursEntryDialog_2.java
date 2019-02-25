/* 
 * Copyright (C) 2017 Edward F Sowell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package JCNAConcours;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.Owner;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.JudgeAssignment;
import us.efsowell.concours.lib.JudgeAssignments;
import us.efsowell.concours.lib.MasterPerson;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.Owners;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;


/**
 *
 * @author Ed Sowell
 * 
 * Revised 9/22/2018. Original implementation was wrongheaded. The problem was the ID of the Entry incorporates the Class name, e.g., C12/JS-1.
 * Hence, the Entry Class can't be changed and still refer to the same entity. 
 * 
 * This implementation combines RemoveConcoursEntry and AddConcoursEntry.
 * 
 */
public class EditConcoursEntryDialog_2 extends javax.swing.JDialog {

    	private static class MemberInfoFormat extends Format {
		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,
				FieldPosition pos) {
			if (obj != null)
				toAppendTo.append(((Owner) obj).getUniqueName());
			return toAppendTo;
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return OwnerRepository.getInstance().getMemberInfo(
					source.substring(pos.getIndex()));
		}
	}

        JCNAClass [] classMasterList; 
        Concours theConcours;
        ConcoursPersonnel theConcoursePersonnel;
        MasterPersonnel theMasterPersonnel;
        Owners theConcoursOwners;
        boolean systemexitwhenclosed;
        Connection theDBConnection;
        //List<JudgeAssignment> theJudgeAssignments;
        

    /**
     * Constructor: Creates new form RemoveConcoursEntryDialog
     */
    public EditConcoursEntryDialog_2(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel,  OwnerRepository aRepository, JCNAClass [] aClassMasterList, boolean aSystemexitwhenclosed) {
        super(parent, modal);
        systemexitwhenclosed = aSystemexitwhenclosed;
        theDBConnection =  aConnection;
        classMasterList = aClassMasterList;  // used in UpdateClassCbo()
        initComponents();
        this.setTitle("Change Concours Entries");

        theConcours = aConcours;
        theConcoursOwners = theConcours.GetConcoursOwnersObject();
        
        theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        
        

        Entry theEditedEntry;
        //theJudgeAssignments = theConcours.GetJudgeAssignments();
        
	TextFilterator<Owner> textFilterator = GlazedLists.textFilterator(
		Owner.class, "uniqueName");  // Note the capital N. Why this is necessary is a mystery!
	/*
         * install auto-completion
         */
        theConcoursePersonnel = aConcoursPersonnel;
	Owner[] allConcoursPersons;

        allConcoursPersons = aRepository.getAllOwners();
	AutoCompleteSupport support;
                support = AutoCompleteSupport.install(
                        this.cboOwnerUniqueName, GlazedLists.eventListOf(allConcoursPersons),
                        textFilterator, new MemberInfoFormat());
	// and set to strict mode
	support.setStrict(true);
        theConcours.GetLogger().info("Calling UpdateOwnerAttributes() in constructor EditConcoursEntryDialog_2()");
        UpdateOwnerAttributes();
        theConcours.GetLogger().info("Calling UpdateJaguarAttributes() in constructor EditConcoursEntryDialog_2()");
        UpdateJaguarAttributes();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboOwnerUniqueName = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txtOwnerLast = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtOwnerFirst = new javax.swing.JTextField();
        txtJCNA = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cboJaguar = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        txtYear = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtColor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPlateVin = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtUniqueDesc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnEditEntry = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Delete Concours Entry");
        setFocusable(false);
        setName("deleteEntry"); // NOI18N

        cboOwnerUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboOwnerUniqueName.setToolTipText("Select Entry owner ");
        cboOwnerUniqueName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboOwnerUniqueNameItemStateChanged(evt);
            }
        });
        cboOwnerUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOwnerUniqueNameActionPerformed(evt);
            }
        });

        jLabel1.setText("Select owner");

        txtOwnerLast.setEditable(false);
        txtOwnerLast.setEnabled(false);
        txtOwnerLast.setFocusable(false);

        jLabel2.setText("Last name");

        txtOwnerFirst.setEditable(false);
        txtOwnerFirst.setEnabled(false);
        txtOwnerFirst.setFocusable(false);

        txtJCNA.setEditable(false);
        txtJCNA.setEnabled(false);
        txtJCNA.setFocusable(false);

        jLabel3.setText("First name");

        jLabel4.setText("JCNA number");

        cboJaguar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJaguar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJaguarItemStateChanged(evt);
            }
        });

        jLabel5.setText("Jaguar");

        txtYear.setEditable(false);
        txtYear.setEnabled(false);
        txtYear.setFocusable(false);

        jLabel6.setText("Year");

        txtColor.setEditable(false);
        txtColor.setEnabled(false);
        txtColor.setFocusable(false);

        jLabel7.setText("Color");

        txtPlateVin.setEditable(false);
        txtPlateVin.setEnabled(false);
        txtPlateVin.setFocusable(false);

        jLabel8.setText("Plate/Vin");

        txtUniqueDesc.setEditable(false);
        txtUniqueDesc.setEnabled(false);
        txtUniqueDesc.setFocusable(false);

        jLabel9.setText("Unique description");

        btnEditEntry.setText("Edit");
        btnEditEntry.setToolTipText("Click to remove the selected Jaguar entered by selected owner &  return to main ConcourseBuilder dialog.");
        btnEditEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditEntryActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setToolTipText("Click to return to main ConcoursBuilder dialog without removing Entry");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cboJaguar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboOwnerUniqueName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                    .addComponent(jLabel5))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOwnerLast, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtOwnerFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtJCNA)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addComponent(btnEditEntry)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancel))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(27, 27, 27)
                                        .addComponent(txtPlateVin, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(106, 106, 106)
                                        .addComponent(jLabel8)))))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(txtUniqueDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboOwnerUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOwnerLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOwnerFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboJaguar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPlateVin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUniqueDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditEntry)
                    .addComponent(btnCancel))
                .addGap(54, 54, 54))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboOwnerUniqueNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOwnerUniqueNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboOwnerUniqueNameActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
         // This is not wanted when the dialog is invoked by ConcourseGUI, but must be done when run by main()
         // or the process will continu to run!
        if(systemexitwhenclosed){
            System.exit(0);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnEditEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditEntryActionPerformed
        Entry currentEntry;
        String currentEntryClassName;
        String currentYear;
        String currentModel;
        String currentDescription;
        String currentUniqueDescription;
        String currentOwnerFirst;
        String currentOwnerMI;
        String currentOwnerLast;
        String currentOwnerUnique;
        String currentJCNA;
        String currentColor;
        String currentPlate;
        Integer currentEntryNode;
        Integer currentEntryClassNode;
        Owner currentOwner  ;      
        String currentOwnerUniqueName ;   
        Integer currentOwnerNode;

        LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        
        
        String uniqueDesc = (String) cboJaguar.getSelectedItem();
        Entry selectedEntry = theConcours.GetEntries().getEntry(uniqueDesc);
        currentEntry = selectedEntry;
        currentEntryNode = currentEntry.GetNode();
        currentEntryClassName = currentEntry.GetClassName();
        ConcoursClass currentConcoursEntryClass = theConcours.GetConcoursClassesObject().GetConcoursClassObject(currentEntryClassName);
        //String selectedEntryClassName = concoursClass.GetClassName();
        if(currentConcoursEntryClass == null){
            String msg = "ERROR: Concours Class " + currentEntry.GetClassName() + " not found in Edit Concours Entry dialog";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
        }
        
        // Save this data so it can be used to create the new Entry
        currentYear = currentEntry.GetYear();
        currentModel = currentEntry.GetModel();
        currentDescription = currentEntry.GetDescription();
        currentUniqueDescription = currentEntry.GetUniqueDescription();
        currentOwnerFirst = currentEntry.GetOwnerFirst();
        currentOwnerMI = currentEntry.GetOwnerMI();
        currentOwnerLast = currentEntry.GetOwnerLast();
        currentOwnerUnique = currentEntry.GetOwnerUnique();
        currentJCNA = currentEntry.GetJCNA();
        currentColor = currentEntry.GetColor();
        currentPlate = currentEntry.GetPlateVin();
        currentEntryClassNode = currentConcoursEntryClass.GetClassNode();
        currentConcoursEntryClass.RemoveEntryIndex(currentEntryNode);
        currentConcoursEntryClass.RemoveEntryObject(currentEntry);
        loadSQLiteConcoursDatabase.UpdateRemoveConcoursClassesEntriesDBTable(theDBConnection, currentEntryNode);
        /*
            If ClassEntryIndices &  ClassEntryObjects were left empty by the preceding step, remove the Class from ConcoursClasses
            Also remove the Class from the ConcoursClassesDBTable
        */
        if(currentConcoursEntryClass.GetClassEntryIndices().size() == 0){
            theConcours.GetConcoursClassesObject().RemoveConcoursClass(currentConcoursEntryClass);
            loadSQLiteConcoursDatabase.UpdateRemoveConcoursClassesDBTable(theDBConnection, currentEntryClassName, currentEntryClassNode);
        }
        /*
            Remove the entry from the entrylist of Owner. AND from: OwnersEntries, ConcoursJaguars,  DB tables
        */
        currentOwnerUniqueName = currentEntry.GetOwnerUnique();
        currentOwner = theConcours.GetConcoursOwnersObject().GetOwner(currentOwnerUniqueName);
        currentOwnerNode = currentOwner.GetNode();
        currentOwner.RemoveEntry(currentEntryNode);
        loadSQLiteConcoursDatabase.UpdateRemoveOwnersEntriesDBTable(theDBConnection, currentEntryNode);
        
        //    Remove Entry from ConcoursEntries & DB Table
        theConcours.GetEntries().RemoveConcoursEntry(currentEntry);
        loadSQLiteConcoursDatabase.UpdateRemoveConcoursEntryDBTable(theDBConnection, currentEntryNode);
        loadSQLiteConcoursDatabase.UpdateRemoveConcoursJaguarsDBTable(theDBConnection, currentEntryNode);       
        
        
        /* Note: Since EntryJudgesTable References JudgeAssignmentsDBTable we have to remove the affected rows in EntryJudgesTable first.
         * int JS_size = theConcours.GetJudgeAssignments().size();
         *  if(JS_size > 0)loadSQLiteConcoursDatabase.UpdateRemoveJudgeAssignmentsDBTable(theDBConnection, selectedEntryNode);        
         */
       
       
        /*
            If the preceding steps leave Owner's entrylist empty, remove the Owner from Owners.
            Also, set status_o = 0 in ConcoursPersonnel
            AND if Owner is also a Judge remove the Entry Class from the Judge selfEntry list
            Also remove from ConcoursOwnersDBTable
        */
       
        ConcoursPerson currentConcoursPerson =  theConcours.GetConcoursPersonnelObject().GetConcoursPerson(currentOwnerNode);
        if(currentOwner.GetEntryList().size() == 0){
            theConcours.GetConcoursOwnersObject().RemoveOwner(currentOwnerUniqueName);
            currentConcoursPerson.SetStatus_o(0);
            loadSQLiteConcoursDatabase.UpdateRemoveConcoursOwner(theDBConnection, currentOwnerNode);
            if(currentConcoursPerson.GetStatus_j() == 1){
                // remove class from judge self-entry list
                // NOTE: This assumes Owner/Judge doesn't have more than one entry in any Class...
                Judge judge = theConcours.GetConcoursJudge(currentOwnerUniqueName);
                if(judge == null){
                    JOptionPane.showMessageDialog(null, "Error btnEditEntryActionPerformed: Person " + currentOwnerUniqueName + " is not in Concourse Judges although has status_j = 1 in Concours Personnel.");
                    theConcours.GetLogger().info("Error btnEditEntryActionPerformed: Person " + currentOwnerUniqueName + " is not in Concourse Judges although has status_j = 1 in Concours Personnel.");
                }
                judge.RemoveSelfEntry(currentConcoursEntryClass.GetClassName());  
                loadSQLiteConcoursDatabase.UpdateRemoveConcoursJudgeSelfEntryByClass(theDBConnection, currentConcoursEntryClass.GetClassName());
            }
            else{
                // the selected Entry Owner is now neither an Owner or a Judge so remove from ConcoursPersonnel
                theConcours.GetConcoursPersonnelObject().RemovePerson(currentConcoursPerson);
                loadSQLiteConcoursDatabase.UpdateRemoveConcoursPersonnel(theDBConnection, currentOwnerNode);
            }
                
        }
        
        String msg = "Entry " + uniqueDesc + " removed in preparation for adding the Edited Entry. ";
        okDialog(msg);
        theConcours.GetLogger().info(msg);
       
        theConcours.SetJudgeAssignmentCurrent(false); 
                try { 
                    loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
                } catch (SQLException ex) {
                    msg = " SQLException SetSettingsTableJAState in EditConcoursEntryDialog";
                    okDialog(msg);
                   theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                }
    
       
       // 9/29/2017 
       // I'm putting this back due to the following peoblem
       // a. An entry or judge is removed
       //  b. Exit/save is done WITHOUT DOING A BUILD
       //  In this situation there will be an error whent he concours is reopened. The index of the entry or judge is in the JA table
       //  but is not in the other concourse data structures.
       //  Because of the history of locked database (see above coments) I reorganized ClearJudgeAssignmentsTables to:
       // Drop EntryJudges table
       // Drop JATable
       // Create new JA Table
       // Create new EntryJudges table
       //  Works!
       theConcours.GetLogger().info("Calling ClearJudgeAssignmentsTables() in EditConcoursEntryDialog_2");
       loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);


       //
       //   Now that the Entry has been removed, we will create a new entry with the wanted Class
       //
       

       
       // ----------
       // the previouse Judge Assignment is now invalid so manual editing is disabled.
       // theConcours.SetJudgeAssignmentCurrent(false); 
       //  loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ; 
       //--------------- 
        this.setVisible(false);
        this.dispose();
         // This is not wanted when the dialog is invoked by ConcourseGUI, but must be done when run by main()
         // or the process will continu to run!
        if(systemexitwhenclosed){
            System.exit(0);
        }
    }//GEN-LAST:event_btnEditEntryActionPerformed

    private void cboOwnerUniqueNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboOwnerUniqueNameItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){ // Without this check the action will take place when item is selected and unselected... twice
            UpdateOwnerAttributes();
            UpdateJaguarAttributes();
        }
    }//GEN-LAST:event_cboOwnerUniqueNameItemStateChanged

    private void cboJaguarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJaguarItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){ // Without this check the action will take place when item is selected and unselected... twice
            UpdateJaguarAttributes();
        }
    }//GEN-LAST:event_cboJaguarItemStateChanged

    /*
     * Loads attributes of the selected Owner into the form
    */ 
    private void UpdateOwnerAttributes(){
                Owner selectedOwner = null;
                Integer ownerNode;
                String first;
                String last;
                Integer jcna;
                ConcoursPerson concoursperson;
                Long masterPerson_id;
                //problem here... perhaps get whatever we can out of the cbo and use it to look up the Owner.
                //selectedOwner = (Owner) RemoveConcoursEntryDialog.this.cboOwnerUniqueName.getSelectedItem();
                Object objSelection = EditConcoursEntryDialog_2.this.cboOwnerUniqueName.getSelectedItem();
                selectedOwner = (Owner) objSelection;
                //String strOwnerUniqueName = (String) RemoveConcoursEntryDialog.this.cboOwnerUniqueName.getSelectedItem();
                //selectedOwner = theConcours.GetConcoursOwnersObject().GetOwner(strOwnerUniqueName);
                theConcours.GetLogger().info( objSelection.getClass().toString());
		if (selectedOwner != null) {
                    ownerNode = selectedOwner.GetNode();
                    first = theMasterPersonnel.GetMasterPersonnelFirstName(selectedOwner.getUniqueName());
                    last = theMasterPersonnel.GetMasterPersonnelLastName(selectedOwner.getUniqueName());
                    jcna = theMasterPersonnel.GetMasterPersonnelJCNA(selectedOwner.getUniqueName());
                    //System.out.println("Selected Owner Node: " + ownerNode + " First: " + first + "Last: " + last );
                    txtJCNA.setText(jcna.toString());
                    txtOwnerFirst.setText(first);
                    txtOwnerLast.setText(last);
                    String[] entryUniqueDescAry;                   
                    int i = 0;
                    entryUniqueDescAry = new String[selectedOwner.GetEntryList().size()];
                    List<Entry> entryList = new ArrayList<>();
                    for(Integer EntryNode : selectedOwner.GetEntryList()){
                        Entry e = theConcours.GetEntries().getEntry(EntryNode);
                        entryList.add(e);
                        entryUniqueDescAry[i]  = e.GetUniqueDescription();                     
                        i++;
                    }
                   // cboJaguars.setModel(new javax.swing.DefaultComboBoxModel(entryList.toArray()));  
                    
                    // better display if we have a cbo of Strings... 6/22/2016
                    cboJaguar.setModel(new javax.swing.DefaultComboBoxModel(entryUniqueDescAry));        
		}
    }
    
    private void UpdateJaguarAttributes(){
        String uniqueDesc = (String) cboJaguar.getSelectedItem();
        Entry selectedEntry = theConcours.GetEntries().getEntry(uniqueDesc);

        txtYear.setText(selectedEntry.GetYear());
        txtColor.setText(selectedEntry.GetColor());
        txtPlateVin.setText(selectedEntry.GetPlateVin());
        txtUniqueDesc.setText(selectedEntry.GetUniqueDescription());
        
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditConcoursEntryDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditConcoursEntryDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditConcoursEntryDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditConcoursEntryDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Connection conn ;
                String strConn;
                String strDBName = "SDJC2014.db";
                String strPath = "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours" + "\\" + strDBName;
               // String strPath= "C:\\Users\\jag_m_000\\Documents\\Concours" + "\\" + strDBName;
                conn = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    strConn = "jdbc:sqlite:" + strPath ;
                    conn = DriverManager.getConnection(strConn);
                    System.out.println("Opened database " + strConn + " successfully");
                } catch ( ClassNotFoundException | SQLException e ) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                    System.exit(0);
                }
                System.out.println("Opened database " + strPath + " successfully");
                Logger logger = Logger.getLogger("ConcoursBuilderLog");  
                FileHandler fh;  
                try {  
                    fh = new FileHandler(strPath);  // The log file will be in the strPath
                    logger.addHandler(fh);
                    SimpleFormatter formatter = new SimpleFormatter();  
                    fh.setFormatter(formatter);  
                    logger.info("ConcoursBuilder started");  
                } catch (SecurityException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                Concours theConcours = new Concours(logger, 3);
                theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses",  logger);
                theConcours.LoadMasterPersonnelDB(conn, logger);
                theConcours.LoadConcoursPersonnelDB(conn, logger);
                theConcours.LoadMasterJaguarDB(conn, logger);
                theConcours.LoadEntriesDB(conn, logger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
                theConcours.LoadConcoursClassesDB(conn, theConcours, logger);
                theConcours.LoadJudgesDB(conn, logger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
                theConcours.LoadOwnersDB(conn, logger);

                OwnerRepository concoursPersonnelList = new OwnerRepository(theConcours);
                ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
                ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
                JCNAClass [] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()] );
//                                                     RemoveConcoursEntryDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel,  JCNAClass [] aClassMasterList, boolean aSystemexitwhenclosed) {
                EditConcoursEntryDialog_2 dialog = new EditConcoursEntryDialog_2(new javax.swing.JFrame(), true,  conn, theConcours, theConcoursPersonnel, concoursPersonnelList, classMasterArray, false);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
                try {      
                    System.out.println("Close db Connection");
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(EditConcoursEntryDialog_2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnEditEntry;
    private javax.swing.JComboBox cboJaguar;
    private javax.swing.JComboBox cboOwnerUniqueName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextField txtJCNA;
    private javax.swing.JTextField txtOwnerFirst;
    private javax.swing.JTextField txtOwnerLast;
    private javax.swing.JTextField txtPlateVin;
    private javax.swing.JTextField txtUniqueDesc;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables
}
