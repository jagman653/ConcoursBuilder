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


import static JCNAConcours.AddConcoursEntryDialog.okCancelDialog;
import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.ConcoursGUI.theConcours;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Component;
import java.awt.event.ItemEvent;
import static java.awt.event.ItemEvent.SELECTED;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entries;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterJaguars;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.MyJavaUtils;
import us.efsowell.concours.lib.Owner;
import us.efsowell.concours.lib.Owners;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ed Sowell
 */
public class EditConcoursEntryDialogRev extends javax.swing.JDialog {    

    private void OkDialog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    	private static class MemberInfoFormat extends Format {
		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,
				FieldPosition pos) {
			if (obj != null)
				toAppendTo.append(((MasterPersonExt) obj).getUniqueName());
			return toAppendTo;
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return MasterListRepository.getInstance().getMemberInfo(
					source.substring(pos.getIndex()));
		}
	}

        JCNAClass [] classMasterList; 
        Concours theConcours;
        ConcoursPersonnel theConcoursePersonnel;
        Owners theConcoursOwners;
        boolean systemexitwhenclosed;
        Connection theDBConnection;
    /**
     * Creates new form ModifyConcoursEntryGui
     */
        

    /**
     * Creates new form ModifyConcoursEntryDialog
     * @param parent
     * @param modal
     * @param aConcours
     * @param aClassMasterList
     */
    public  EditConcoursEntryDialogRev(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, JCNAClass [] aClassMasterList, boolean aSystemexitwhenclosed) {
        super(parent, modal);

        systemexitwhenclosed = aSystemexitwhenclosed;
        theDBConnection =  aConnection;
        classMasterList = aClassMasterList;  // used in UpdateClassCbo()
        initComponents();
        this.setTitle("Modify Concours Entry");
        theConcours = aConcours;
        theConcoursePersonnel = theConcours.GetConcoursPersonnelObject();
        theConcoursOwners =  theConcours.GetConcoursOwnersObject();
        //ModifyConcoursEntryDialog.this.cboConcoursEntry.setModel(new javax.swing.DefaultComboBoxModel(theConcours.GetEntriesList().toArray()));
        
        List<Entry> theEntries = theConcours.GetEntriesList(); 
        //cboConcoursEntry.setModel(new javax.swing.DefaultComboBoxModel(theConcours.GetEntriesList().toArray()));
        String[] entryUniqueDescAry;                   // <--declared statement
        entryUniqueDescAry = new String[theEntries.size()];
        int i = 0;
        for(Entry e : theEntries){
            entryUniqueDescAry[i] = e.GetUniqueDescription();
            i++;
        }
        cboConcoursEntry.setModel(new javax.swing.DefaultComboBoxModel(entryUniqueDescAry));
        /*
         * Based on the selected MasterPerson in  cboUniqueName, fill in the member attribute fields
         * in the dialog and get the jaguar stable for this member so it can be used to populate cboJaguars
         */
       UpdateEntryAttributes();
        
        /*
         * Based on the selected Jaguar in cboJaguars fill in the Jaguar attribute fields
         * in the dialog and get the possible JCNA class list for this Jaguar so it can be used to populate cboJCNAClass
         */
       //UpdateJaguarAttributes() ;   
        /*
         * Based on the selected JCNAClass fill in the Class description  fields
         * in the dialog
         */
       UpdateJCNAClassAttributes();
    }
    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel12 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        txtFirst = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtLast = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtJCNA = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btnSaveEntry = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtYear = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtColor = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtPlateVIN = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        cboJCNAClass = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        txtClassDescription = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtDivision = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        cboConcoursEntry = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txtEntryOwnerUniqueName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCurClass = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCurDivision = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        jLabel12.setText("jLabel12");

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Change Judging Class for a Concours Entry ");

        txtFirst.setEditable(false);
        txtFirst.setEnabled(false);
        txtFirst.setFocusable(false);

        jLabel1.setText("Owner first");

        txtLast.setEditable(false);
        txtLast.setEnabled(false);
        txtLast.setFocusable(false);

        jLabel2.setText("Owner last");

        txtJCNA.setEditable(false);
        txtJCNA.setEnabled(false);
        txtJCNA.setFocusable(false);

        jLabel10.setText("JCNA #");

        btnSaveEntry.setText("Save");
        btnSaveEntry.setToolTipText("Click after the JCNA Class has been selected.");
        btnSaveEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveEntryActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setToolTipText("Click if you do not want to make any change");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        txtYear.setEditable(false);
        txtYear.setEnabled(false);
        txtYear.setFocusable(false);

        jLabel21.setText("Year");

        txtDescription.setEditable(false);
        txtDescription.setEnabled(false);
        txtDescription.setFocusable(false);

        jLabel22.setText("Jaguar Description");

        txtColor.setEditable(false);
        txtColor.setEnabled(false);
        txtColor.setFocusable(false);

        jLabel23.setText("Color");

        txtPlateVIN.setEditable(false);
        txtPlateVIN.setEnabled(false);
        txtPlateVIN.setFocusable(false);

        jLabel24.setText("Plate or VIN");

        cboJCNAClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJCNAClass.setToolTipText("Select the wanted judging class");
        cboJCNAClass.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJCNAClassItemStateChanged(evt);
            }
        });

        jLabel25.setText("Select JCNA class");

        txtClassDescription.setEditable(false);
        txtClassDescription.setEnabled(false);
        txtClassDescription.setFocusable(false);

        jLabel26.setText("Class description");

        txtDivision.setEditable(false);
        txtDivision.setEnabled(false);
        txtDivision.setFocusable(false);

        jLabel27.setText("Division");

        cboConcoursEntry.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboConcoursEntry.setToolTipText("Select the Entry to be changed. NOTE: Only the JCNA Judging class can be changed. ");
        cboConcoursEntry.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboConcoursEntryItemStateChanged(evt);
            }
        });

        jLabel3.setText("Entry unique name");

        txtEntryOwnerUniqueName.setEditable(false);
        txtEntryOwnerUniqueName.setEnabled(false);
        txtEntryOwnerUniqueName.setFocusable(false);

        jLabel5.setText("Entry Owner unique name");

        txtCurClass.setEditable(false);
        txtCurClass.setEnabled(false);
        txtCurClass.setFocusable(false);

        jLabel4.setText("Current JCNA Class");

        txtCurDivision.setEditable(false);
        txtCurDivision.setEnabled(false);
        txtCurDivision.setFocusable(false);

        jLabel6.setText("Current Division");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(cboConcoursEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtCurClass)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(txtEntryOwnerUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(57, 57, 57)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCurDivision, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPlateVIN, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22))
                                .addGap(58, 58, 58))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboJCNAClass, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(txtDivision, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel27)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(txtClassDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 837, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
            .addGroup(layout.createSequentialGroup()
                .addGap(396, 396, 396)
                .addComponent(btnSaveEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboConcoursEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel24)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtPlateVIN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel23)
                                .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtYear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCurClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCurDivision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEntryOwnerUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboJCNAClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDivision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClassDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveEntry)
                    .addComponent(btnCancel))
                .addGap(282, 282, 282))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
     * Loads attributes of the selected Entry into the form
    */ 
    private void UpdateEntryAttributes(){
        Entry selectedEntry;
        String uniqueDesc; 
        MasterJaguar   theEntryMasterJaguar;
        String theEntryUniqueDescription;
        String strChampionshipClass;
        String strDrivenClass;
        String strPreversationClass;
        MyJavaUtils myJavaUtils = new MyJavaUtils();
        JCNAClassesSubset possibleClasses = new JCNAClassesSubset();
        uniqueDesc = (String) cboConcoursEntry.getSelectedItem();
        selectedEntry = theConcours.GetEntries().getEntry(uniqueDesc);
        //selectedEntry = (Entry)cboConcoursEntry.getSelectedItem();
        txtEntryOwnerUniqueName.setText( selectedEntry.GetOwnerUnique());
        txtJCNA.setText(selectedEntry.GetJCNA().toString());
        txtFirst.setText(selectedEntry.GetOwnerFirst());
        txtLast.setText(selectedEntry.GetOwnerLast());
        txtYear.setText(selectedEntry.GetYear());
        txtColor.setText(selectedEntry.GetColor());
        txtPlateVIN.setText(selectedEntry.GetPlateVin());
        txtDescription.setText(selectedEntry.GetDescription());
        txtCurClass.setText(selectedEntry.GetClassName());
        JCNAClass jcnaClass = theConcours.GetJCNAClasses().getJCNAClass(selectedEntry.GetClassName());
        if(jcnaClass == null){
            String msg = "ERROR: JCNAClass named " + selectedEntry.GetClassName() + " not found in UpdateEntryAttributes()";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
            System.exit(-1);
        }
        txtCurDivision.setText(jcnaClass.getDivision());
        theEntryUniqueDescription = selectedEntry.GetUniqueDescription();
        // Now have to load cboPossibleClass
        cboJCNAClass.removeAllItems();
        MasterJaguars masterJaguars = theConcours.GetConcoursMasterJaguarsObject();
        theEntryMasterJaguar = masterJaguars.GetMasterJaguar(theEntryUniqueDescription);
        if(theEntryMasterJaguar == null){
           okDialog("Could not find MasterJaguar with unique description " + theEntryUniqueDescription + " in UpdateEntryAttributes");
           return;
        }
        strChampionshipClass = theEntryMasterJaguar.getJcnaclass_c();
        strDrivenClass = theEntryMasterJaguar.getJcnaclass_d();
        // Calculate preservation class
        strPreversationClass = myJavaUtils.CalculatePreservationClass(selectedEntry.GetYear()); // empty string if less than 20 years old. Otherewise, C17/PN or C18/PN  
        boolean foundit = false;
        for( int i = 0; i<classMasterList.length ; i++){
                       if(classMasterList[i].getName().equals(strChampionshipClass)){
                           possibleClasses.AddClass(classMasterList[i]);
                           foundit = true;
                            break;
                       }
        }
        if(!foundit){
            okCancelDialog("Could not find class " + strChampionshipClass + " in JCNA Master Class list");
        }   
        
        foundit = false;
        for (JCNAClass classMasterList1 : classMasterList) {
                    if (classMasterList1.getName().equals(strDrivenClass)) {
                        possibleClasses.AddClass(classMasterList1);
                        foundit = true;
                        break;
                    }
        }
        if(!foundit){
            okCancelDialog("Could not find class " + strDrivenClass + " in JCNA Master Class list");
        }
        
        if(!"".equals(strPreversationClass)){
            foundit = false;
            for (JCNAClass classMasterList1 : classMasterList) {
                        if (classMasterList1.getName().equals(strPreversationClass)) {
                            possibleClasses.AddClass(classMasterList1);
                            foundit = true;
                            break;
                        }
            }
            if(!foundit){
                okCancelDialog("Could not find class " + strPreversationClass + " in JCNA Master Class list");
            }
        }
        // Add Special classes
        foundit = false;
        for (JCNAClass classMasterList1 : classMasterList) {
            if (classMasterList1.getDivision().equals("Special")) {
                possibleClasses.AddClass(classMasterList1);
                foundit = true;
            }
        }
        if(!foundit){
            String msg = "Could not find any Special Classes in JCNA Master Class list";
            okCancelDialog(msg);
            theConcours.GetLogger().info(msg);
        }
        //  Add Display only class
        JCNAClass display =  new JCNAClass("Display", "DISP",  "Display only", "", "", "", 99);
        possibleClasses.AddClass(display);    
        
        cboJCNAClass.setModel(new javax.swing.DefaultComboBoxModel(possibleClasses.GetPossibleClasses()));
        cboJCNAClass.setRenderer(new JCNAClassCBORenderer());

    
    }
        
   
  
    private void UpdateJCNAClassAttributes(){
        JCNAClass theSelectedJCNAClass;
        theSelectedJCNAClass = (JCNAClass)cboJCNAClass.getSelectedItem();
        txtClassDescription.setText(theSelectedJCNAClass.getDescription());
        txtDivision.setText(theSelectedJCNAClass.getDivision());
    }    
    
    
    /*
     *  Update attributes of the selected possible class
    */
    private void cboJCNAClassItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJCNAClassItemStateChanged
      // JCNAClass  theSelectedJCNAClass;
           // List<JCNAClass> JCNAClassObjects; // for the selected Entry
           System.out.println("In cboPossibleClassItemStateChanged");

            if (evt.getStateChange() == SELECTED) {
                    //theSelectedJCNAClass = (JCNAClass)cboJCNAClass.getSelectedItem();
                   // System.out.println("Selected JCNA Class:" + theSelectedJCNAClass.getName());
                    UpdateJCNAClassAttributes();
            }
    }//GEN-LAST:event_cboJCNAClassItemStateChanged

    private void btnSaveEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveEntryActionPerformed
        Integer theNewClassNode;
        ConcoursClass theNewConcoursClass = null;
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        //
        // First, save necessary data. Then remove the Entry entierly.
        //
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
        Integer currentOwnerNode;
        MasterPersonExt currentMasterPerson;
        Long currentMPid;
        Integer currentConcoursPersonnelNode;        
        String currentEntryID;      // A name of the form C12/JS-n where n ranges from 1 to number of Entries in that class   
        
        String uniqueDesc = (String) cboConcoursEntry.getSelectedItem();
        currentEntry = theConcours.GetEntries().getEntry(uniqueDesc);
        currentEntryNode = currentEntry.GetNode();
        currentEntryID  = currentEntry.GetID();
        currentEntryClassName  = currentEntry.GetClassName();
        //currentEntryNode = currentEntry.GetNode();
        currentEntryClassName = currentEntry.GetClassName();
        ConcoursClass currentConcoursEntryClass = theConcours.GetConcoursClassesObject().GetConcoursClassObject(currentEntryClassName);
        if(currentConcoursEntryClass == null){
            String msg = "ERROR: Concours Class " + currentEntry.GetClassName() + " not found in Edit Concours Entry dialog";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
            System.exit(-1);
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
        currentMasterPerson = theConcours.GetMasterPersonnelObject().GetMasterPerson(currentOwnerUnique);
        currentMPid = currentMasterPerson.getMasterPersonID();
        // Now do the removal
        currentConcoursEntryClass.RemoveEntryIndex(currentEntry.GetNode());
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
        currentOwner = theConcours.GetConcoursOwnersObject().GetOwner(currentOwnerUnique);
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
            theConcours.GetConcoursOwnersObject().RemoveOwner(currentOwnerUnique);
            currentConcoursPerson.SetStatus_o(0);
            loadSQLiteConcoursDatabase.UpdateRemoveConcoursOwner(theDBConnection, currentOwnerNode);
            if(currentConcoursPerson.GetStatus_j() == 1){
                // remove class from judge self-entry list
                // NOTE: This assumes Owner/Judge doesn't have more than one entry in any Class...
                Judge judge = theConcours.GetConcoursJudge(currentOwnerUnique);
                if(judge == null){
                    JOptionPane.showMessageDialog(null, "ERROR: btnEditEntryActionPerformed: Person " + currentOwnerUnique + " is not in Concourse Judges although has status_j = 1 in Concours Personnel.");
                    theConcours.GetLogger().info("ERROR: btnEditEntryActionPerformed: Person " + currentOwnerUnique + " is not in Concourse Judges although has status_j = 1 in Concours Personnel.");
                    System.exit(-1);
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
        //okDialog(msg);
        theConcours.GetLogger().info(msg);
       
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
       boolean addedEntry = true;       
        JCNAClass JCNAClassObject;
       
        addedEntry = true;
        JCNAClassObject = (JCNAClass) cboJCNAClass.getSelectedItem();
        String strJCNAClassName = JCNAClassObject.getName();
        AddConcoursEntryDialog anAddConcoursEntryDialog = new AddConcoursEntryDialog();  //  Function access only
        //addEntryHelper_2(Concours aConcours, String aOwnerUniqueName, String aJaguarUniqueDescription, String aStrJCNAClass, boolean aEditEntry)
        anAddConcoursEntryDialog.addEntryHelper_2(theConcours, currentOwnerUnique, currentUniqueDescription, strJCNAClassName, false); // false indicate this is being called from Edit Entry
        msg = "Changed Entry Class";
        //okDialog(msg);
        theConcours.GetLogger( ).info(msg);


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
        
        
    }//GEN-LAST:event_btnSaveEntryActionPerformed

    private void cboConcoursEntryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboConcoursEntryItemStateChanged
           System.out.println("In cboConcoursEntryItemStateChanged");
            if (evt.getStateChange() == SELECTED) {
                    //theSelectedJCNAClass = (JCNAClass)cboJCNAClass.getSelectedItem();
                   // System.out.println("Selected JCNA Class:" + theSelectedJCNAClass.getName());
                    UpdateEntryAttributes();
            }
    }//GEN-LAST:event_cboConcoursEntryItemStateChanged

   public static int okCancelDialog(String theMessage) {
    int result = JOptionPane.showConfirmDialog((Component) null, theMessage,
        "alert", JOptionPane.OK_CANCEL_OPTION);
    return result;
  }

   public static int yesNoDialog(String theMessage) {
    int result = JOptionPane.showConfirmDialog((Component) null, theMessage,
        "alert", JOptionPane.YES_NO_OPTION);
    return result;
  }
   
   
   public static void okDialog(String theMessage) {
/*    int result = JOptionPane.showConfirmDialog((Component) null, theMessage,
        "alert", JOptionPane.OK_OPTION);
*/   
       JOptionPane.showMessageDialog(null, theMessage);
       //return result;
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditConcoursEntryDialogRev.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
                //</editor-fold>
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
          //  String strPath= "C:\\Users\\jag_m_000\\Documents\\Concours" + "\\" + strDBName;
            
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
            theConcours.LoadEntriesDB(conn,logger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
            theConcours.LoadConcoursClassesDB(conn, theConcours, logger);
            theConcours.LoadJudgesDB(conn, logger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
            theConcours.LoadOwnersDB(conn, logger);
                
            //MasterListRepository masterList = new MasterListRepository(conn);
            boolean loadRepositoryFromMemory = true;
            MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);
            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
            ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
            JCNAClass [] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()] );
            EditConcoursEntryDialogRev theDialog = new EditConcoursEntryDialogRev(new javax.swing.JFrame(), true, conn, theConcours,  classMasterArray, true);
                theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                theDialog.setVisible(true);
            try {
                conn.close(); 
            } catch (SQLException ex) {
                Logger.getLogger(EditConcoursEntryDialogRev.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSaveEntry;
    private javax.swing.JComboBox cboConcoursEntry;
    private javax.swing.JComboBox cboJCNAClass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField txtClassDescription;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextField txtCurClass;
    private javax.swing.JTextField txtCurDivision;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtDivision;
    private javax.swing.JTextField txtEntryOwnerUniqueName;
    private javax.swing.JTextField txtFirst;
    private javax.swing.JTextField txtJCNA;
    private javax.swing.JTextField txtLast;
    private javax.swing.JTextField txtPlateVIN;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables


}
