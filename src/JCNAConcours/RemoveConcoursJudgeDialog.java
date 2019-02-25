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
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.ConcoursClasses;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.Owner;
//import us.efsowell.concours.lib.Owner;
//import us.efsowell.concours.lib.Owners;

/**
 *
 * @author Ed Sowell
 */
public class RemoveConcoursJudgeDialog extends javax.swing.JDialog {
    	private static class MemberInfoFormat extends Format {
		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,
				FieldPosition pos) {
			if (obj != null)
				toAppendTo.append(((Judge) obj).getUniqueName());
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
        Judges theConcoursJudges;
        ArrayList<ConcoursClass> concoursClasses;
        boolean systemexitwhenclosed;
        Connection theDBConnection;
        Logger theLogger;

        
        
    /**
     * Creates new form RemoveConcoursJudgeDialog
     */
    public RemoveConcoursJudgeDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel,  JudgeRepository aRepository,  boolean aSystemexitwhenclosed) {
        
        super(parent, modal);
        theLogger = aConcours.GetLogger();
        theLogger.info("Constructing RemoveConcoursJudgeDialog");
        systemexitwhenclosed = aSystemexitwhenclosed;
        theDBConnection =  aConnection;
        //classMasterList = aClassMasterList;  // used in UpdateClassCbo()
        initComponents();
        this.setEnabled(true);
        this.setTitle("Remove Concours Judges");
        this.txtJCNA.setEditable(false);
        this.txtJCNA.setEnabled(false);
        this.txtJCNA.setFocusable(false);
        this.txtJudgeFirst.setEditable(false);
        this.txtJudgeFirst.setEnabled(false);
        this.txtJudgeFirst.setFocusable(false);
        this.txtJudgeLast.setEditable(false);
        this.txtJudgeLast.setEnabled(false);
        this.txtJudgeLast.setFocusable(false);

        theConcours = aConcours;
        theConcoursJudges = theConcours.GetConcoursJudgesObject();
        concoursClasses = theConcours.GetConcoursClasses();
        
        theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        
	TextFilterator<Judge> textFilterator = GlazedLists.textFilterator(
		Judge.class, "uniqueName");  // Note the capital N. Why this is necessary is a mystery!
	/*
         * install auto-completion
         */
        theConcoursePersonnel = aConcoursPersonnel;
	Judge[] allConcoursJudges;

        allConcoursJudges = aRepository.getAllJudges();
	AutoCompleteSupport support;
                support = AutoCompleteSupport.install(
                        this.cboJudgeUniqueName, GlazedLists.eventListOf(allConcoursJudges),
                        textFilterator, new RemoveConcoursJudgeDialog.MemberInfoFormat());
	// and set to strict mode
	support.setStrict(true);
        // This was just done as a result of the state change of the cbo
        //UpdateJudgeAttributes();
        
              theLogger.info("Finished Constructing RemoveConcoursJudgeDialog");
  
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtJCNA = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cboJudgeUniqueName = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txtJudgeLast = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtJudgeFirst = new javax.swing.JTextField();
        btnDeleteJudge = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setEnabled(false);

        txtJCNA.setEnabled(false);
        txtJCNA.setFocusable(false);

        jLabel3.setText("First name");

        jLabel4.setText("JCNA number");

        cboJudgeUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJudgeUniqueName.setToolTipText("Select Concours Judge");
        cboJudgeUniqueName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJudgeUniqueNameItemStateChanged(evt);
            }
        });
        cboJudgeUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJudgeUniqueNameActionPerformed(evt);
            }
        });

        jLabel1.setText("Select Judge");

        txtJudgeLast.setEnabled(false);
        txtJudgeLast.setFocusable(false);

        jLabel2.setText("Last name");

        txtJudgeFirst.setEnabled(false);
        txtJudgeFirst.setFocusable(false);

        btnDeleteJudge.setText("Remove");
        btnDeleteJudge.setToolTipText("Click to remove selected Concours Judge & return to main ConcoursBuilder dialog.");
        btnDeleteJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteJudgeActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setToolTipText("Click to return to main ConcoursBuilder dialog without removing selected Judge.");
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboJudgeUniqueName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtJudgeLast, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtJudgeFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(btnDeleteJudge)
                        .addGap(27, 27, 27)
                        .addComponent(btnCancel)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboJudgeUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJudgeLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJudgeFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteJudge)
                    .addComponent(btnCancel))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboJudgeUniqueNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJudgeUniqueNameItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){ // Without this check the action will take place when item is selected and unselected... twice
            UpdateJudgeAttributes();
        }
    }//GEN-LAST:event_cboJudgeUniqueNameItemStateChanged

    private void cboJudgeUniqueNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboJudgeUniqueNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboJudgeUniqueNameActionPerformed

    private void btnDeleteJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteJudgeActionPerformed

        LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only

        Judge selectedJudge = (Judge) RemoveConcoursJudgeDialog.this.cboJudgeUniqueName.getSelectedItem();
        
        /*
          Remove the Judge from the ConcoursClasses Judge Lists
          AND remove the entire team from any Preassigned classes Judge is part of.
        */
        Integer selectedJudgeNode = selectedJudge.GetNode();
        theLogger.info("btnDeleteJudgeActionPerformed  will remove Concours Judge " + selectedJudge.getUniqueName() + " Node: " + selectedJudgeNode);
        for(ConcoursClass cc : concoursClasses){
            // First, remove the entire Preassigned Judge list for the Class if selectedJudge is on the team
            theLogger.info("First, remove the entire Preassigned Judge list for the Class if selectedJudge is on the team");
            
            ArrayList<String> pajList = cc.GetClassPreassignedJudgeNameList();
            if(pajList.contains(selectedJudge.getUniqueName())){
                cc.RemoveAllPreassignedJudges(theDBConnection);
                theLogger.log(Level.INFO, "Removed preassigned judge team for Class {0}", cc.GetClassName());
            }
            // Now, remove selectedJudge from the Class Judges Node & Unique Name lists 
            theLogger.info("Then,remove selectedJudge from the Class Judges Node & Unique Name lists ");
            ArrayList<Judge> cjList = new ArrayList<>(cc.GetClassJudgeObjects());
            if(cjList.contains(selectedJudge)){
                cjList.remove(selectedJudge);
                theLogger.log(Level.INFO, "Removed Judge Unique Name {0} from Class {1}", new Object[]{selectedJudge.getUniqueName(), cc.GetClassName()});
            }
            ArrayList<Integer> cjNodeList = new ArrayList<>(cc.GetClassJudgeIndices());
            if(cjNodeList.contains(selectedJudgeNode)){
                cjNodeList.remove(selectedJudgeNode);
                theLogger.log(Level.INFO, "Removed Judge Node {0} from Class {1}", new Object[]{selectedJudge.GetNode(), cc.GetClassName()});
            }
        }
        /*
          Remove Judge from Concours Judges
        */
        theConcoursJudges.RemoveJudge(selectedJudge);
        theLogger.log(Level.INFO, "Removed Judge {0} from the Concours in memory", selectedJudge.GetNode());
        /*
          Remove from ConcoursJudgeClassRejectTable in DB
        */
        theLogger.info("Now remove from ConcoursJudgeClassRejectTable in DB");
        loadSQLiteConcoursDatabase.UpdateRemoveConcoursJudgeClassRejectByName(theDBConnection, selectedJudge.getUniqueName());
        /*
          Remove from ConcoursJudgeSelfEntryTable in DB
        */
        theLogger.info("Now Remove from ConcoursJudgeSelfEntryTable in DB");
        loadSQLiteConcoursDatabase.UpdateRemoveConcoursJudgeSelfEntryByName(theDBConnection, selectedJudge.getUniqueName());
        /*
          Set status_j = 0 in corresponding ConcoursPerson and in ConcoursPerson DB table
        */
        //Integer selectedJudgeNode = selectedJudge.GetNode(); // Note: same as Person node
        theLogger.info("Set status_j = 0 in corresponding ConcoursPerson and in ConcoursPerson DB table");
        ConcoursPerson selectedConcoursPerson =  theConcoursePersonnel.GetConcoursPerson(selectedJudgeNode);
        selectedConcoursPerson.SetStatus_j(0);
        loadSQLiteConcoursDatabase.UpdateSetstatus_jConcoursPersonnelDBTable(theDBConnection,  selectedJudge.getUniqueName(), 0);
        /*
          Remove from ConcoursJudgesTable in DB
        */
        theLogger.info("Now remove from ConcoursJudgesTable in DB");
        loadSQLiteConcoursDatabase.UpdateRemoveConcoursJudgesTableByName(theDBConnection, selectedJudge.getUniqueName());
        /*
          If NOT an Owner, remove from ConcoursPersonnel
        */
        theLogger.info("If NOT an Owner, remove from ConcoursPersonnel");
        if(selectedConcoursPerson.GetStatus_o() == 0){
            theConcours.GetConcoursPersonnelObject().RemovePerson(selectedConcoursPerson);
            loadSQLiteConcoursDatabase.UpdateRemoveConcoursPersonnel(theDBConnection, selectedJudgeNode);
        }
       // the previouse Judge Assignment is now invalid so manual editing is disabled.
        // Also, might as well clear the JudgeAssignments Table and EntryJudgesTable
        theConcours.SetJudgeAssignmentCurrent(false); 
                try { 
                    loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
                } catch (SQLException ex) {
                   // Logger.getLogger(RemoveConcoursJudgeDialog.class.getName()).log(Level.SEVERE, null, ex);
                    String msg = " SQLException SetSettingsTableJAState in RemoveConcoursJudgeDialog";
                     okDialog(msg);
                   theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                }
         //This fails due to locked table..  probably don't need anyway   
        // Trying again... 7/14/2016   WORKED.  No locked tables.
            // 8/1/2017 Getting locked tables in LoadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB so once again disable this... unnecessary
        //theLogger.info("Calling ClearJudgeAssignmentsTables() in RemoveConcoursEntryDialog");
       //loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);
       
       
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
       theLogger.info("Calling ClearJudgeAssignmentsTables() in RemoveConcoursJudgeDialog");
       loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);
        

        String msg = "Judge " + selectedConcoursPerson.GetUniqueName() + " removed from Concours.";
        okDialog(msg);
        theLogger.info(msg);
        this.setVisible(false);
        this.dispose();
        // This is not wanted when the dialog is invoked by ConcourseGUI, but must be done when run by main()
        // or the process will continu to run!
        if(systemexitwhenclosed){
            System.exit(0);
        }
    }//GEN-LAST:event_btnDeleteJudgeActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
        // This is not wanted when the dialog is invoked by ConcourseGUI, but must be done when run by main()
        // or the process will continu to run!
        if(systemexitwhenclosed){
            System.exit(0);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    /*
     * Loads attributes of the selected Judge into the form
    */ 
    private void UpdateJudgeAttributes(){
                Judge selectedJudge = null;
                Integer judgeNode;
                String first;
                String last;
                Integer jcna;
                ConcoursPerson concoursperson;
                Long masterPerson_id;
                selectedJudge = (Judge) RemoveConcoursJudgeDialog.this.cboJudgeUniqueName.getSelectedItem();
                
		if (selectedJudge != null) {
                    judgeNode = selectedJudge.GetNode();
                    first = theMasterPersonnel.GetMasterPersonnelFirstName(selectedJudge.getUniqueName());
                    last = theMasterPersonnel.GetMasterPersonnelLastName(selectedJudge.getUniqueName());
                    jcna = theMasterPersonnel.GetMasterPersonnelJCNA(selectedJudge.getUniqueName());
                    theConcours.GetLogger().info("UpdateJudgeAttributes selected Judge Node: " + judgeNode + " First: " + first + " Last: " + last );
                    txtJCNA.setText(jcna.toString());
                    txtJudgeFirst.setText(first);
                    txtJudgeLast.setText(last);
                    
 		}
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
            java.util.logging.Logger.getLogger(RemoveConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RemoveConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RemoveConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RemoveConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Connection conn ;
                String strConn;
                String strDBName = "anotherTest.db";
                String strPath = "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours" + "\\" + strDBName;
               //String strPath= "C:\\Users\\jag_m_000\\Documents\\Concours" + "\\" + strDBName;
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
                theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", logger);
                theConcours.LoadMasterPersonnelDB(conn, logger);
                theConcours.LoadConcoursPersonnelDB(conn, logger);
                theConcours.LoadMasterJaguarDB(conn, logger);
                theConcours.LoadEntriesDB(conn, logger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
                theConcours.LoadConcoursClassesDB(conn, theConcours, logger);
                theConcours.LoadJudgesDB(conn, logger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
                theConcours.LoadOwnersDB(conn, logger);

                JudgeRepository concoursJudgeRepository = new JudgeRepository(theConcours);
                ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();

                RemoveConcoursJudgeDialog dialog = new RemoveConcoursJudgeDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, concoursJudgeRepository , true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.out.println("Removed a Judge ");
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
                
               }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteJudge;
    private javax.swing.JComboBox cboJudgeUniqueName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField txtJCNA;
    private javax.swing.JTextField txtJudgeFirst;
    private javax.swing.JTextField txtJudgeLast;
    // End of variables declaration//GEN-END:variables
}
