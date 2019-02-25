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


import static JCNAConcours.ConcoursGUI.theConcours;
import static JCNAConcours.EditConcoursEntryDialog.okCancelDialog;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import static java.awt.event.ItemEvent.SELECTED;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
import us.efsowell.concours.lib.SchedulingInterfaceJava;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ed Sowell
 */
public class AddConcoursEntryDialog extends javax.swing.JDialog {    
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
        Vector<Component> traversalOrder;
        FocusTraversalPolicyConcoursBuilder ftp;
        boolean addedEntry; // Used to alert user if Finished adding Entires button is clicked without Clicking Add Entry first.
        boolean changedSelections;
        

    public  AddConcoursEntryDialog(){
        
    }        
    /**
     * Creates new form AddConcoursEntryGui
     * @param parent
     * @param modal
     * @param aConcours
     * @param aConcoursPersonnel
     * @param aRepository
     * @param aClassMasterList
     */
    public  AddConcoursEntryDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel, MasterListRepository aRepository, JCNAClass [] aClassMasterList, boolean aSystemexitwhenclosed) {
        super(parent, modal);
        systemexitwhenclosed = aSystemexitwhenclosed;
        theDBConnection =  aConnection;
        classMasterList = aClassMasterList;  // used in UpdateClassCbo()
        initComponents();
        this.setTitle("Add Concours Entries");
        traversalOrder = new Vector<>();
        traversalOrder.add(this.cboUniqueName);
        traversalOrder.add(this.cboJaguars);
        traversalOrder.add(this.cboJCNAClass);
        traversalOrder.add(this.btnAddEntry);
        traversalOrder.add(this.btnFinished);
        ftp = new FocusTraversalPolicyConcoursBuilder(traversalOrder);
        this.setFocusTraversalPolicy(ftp);
        
        theConcours = aConcours;
        theConcours.GetLogger().info("Starting AddConcoursEntry");
        theConcoursOwners = theConcours.GetConcoursOwnersObject();
        addedEntry = false; // Used to alert user if Finished adding Entires button is clicked without Clicking Add Entry first.
        changedSelections = true;
	// custom filterator
	TextFilterator<MasterPersonExt> textFilterator = GlazedLists.textFilterator(
		MasterPersonExt.class, "uniqueName"); // Why is N necessary? I.e., why doesn't "uniquename" work even when that is the property name?

	/*
         * install auto-completion
         */
        theConcoursePersonnel = aConcoursPersonnel;
	MasterPersonExt[] allMembers;
        allMembers = aRepository.getAllMembers();
	AutoCompleteSupport support = AutoCompleteSupport.install(
		this.cboUniqueName, GlazedLists.eventListOf(allMembers),
		textFilterator, new MemberInfoFormat());
	// and set to strict mode
	support.setStrict(true);
        /*
         * Based on the selected MasterPerson in  cboUniqueName, fill in the member attribute fields
         * in the dialog and get the jaguar stable for this member so it can be used to populate cboJaguars
         */
       UpdateMasterPersonAttributes();
        
        /*
         * Based on the selected Jaguar in cboJaguars fill in the Jaguar attribute fields
         * in the dialog and get the possible JCNA class list for this Jaguar so it can be used to populate cboJCNAClass
         */
       UpdateJaguarAttributes() ;   
        /*
         * Based on the selected JCNAClass fill in the Class description  fields
         * in the dialog
         */
       UpdateJCNAClassAttributes();
       
       theConcours.GetLogger().info("AddConcoursEntryDialog created.");

    }
    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboUniqueName = new javax.swing.JComboBox();
        cboJaguars = new javax.swing.JComboBox();
        txtFirst = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtLast = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtJCNA = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtStreetAddress = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtState = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPostalCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCountry = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtClub = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtJudgeStatus = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCertYear = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtPhoneHome = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtPhoneCell = new javax.swing.JTextField();
        txtPhoneWork = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        btnAddEntry = new javax.swing.JButton();
        btnFinished = new javax.swing.JButton();
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
        jLabel28 = new javax.swing.JLabel();
        txtMI = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtModel = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Entry to Concours");

        cboUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboUniqueName.setToolTipText("Begin typing Last name for autocompletion.");
        cboUniqueName.setFocusTraversalPolicyProvider(true);
        cboUniqueName.setNextFocusableComponent(cboJaguars);
        cboUniqueName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboUniqueNameItemStateChanged(evt);
            }
        });
        cboUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUniqueNameActionPerformed(evt);
            }
        });

        cboJaguars.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJaguars.setToolTipText("Select a Jaguar from the Owner's stable");
        cboJaguars.setFocusTraversalPolicyProvider(true);
        cboJaguars.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJaguarsItemStateChanged(evt);
            }
        });
        cboJaguars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJaguarsActionPerformed(evt);
            }
        });

        txtFirst.setEditable(false);
        txtFirst.setText("first");
        txtFirst.setEnabled(false);
        txtFirst.setFocusable(false);

        jLabel1.setText("First");

        txtLast.setEditable(false);
        txtLast.setText("last");
        txtLast.setEnabled(false);
        txtLast.setFocusable(false);

        jLabel2.setText("Last");

        txtJCNA.setEditable(false);
        txtJCNA.setText("unknown");
        txtJCNA.setEnabled(false);

        jLabel4.setText("Owner Unique name");

        txtStreetAddress.setEditable(false);
        txtStreetAddress.setText("unknown");
        txtStreetAddress.setEnabled(false);
        txtStreetAddress.setFocusable(false);

        jLabel5.setText("Street address");

        txtCity.setEditable(false);
        txtCity.setText("unknown");
        txtCity.setEnabled(false);
        txtCity.setFocusable(false);

        jLabel6.setText("City");

        txtState.setEditable(false);
        txtState.setText("unknown");
        txtState.setEnabled(false);
        txtState.setFocusable(false);

        jLabel7.setText("State");

        txtPostalCode.setEditable(false);
        txtPostalCode.setText("unknown");
        txtPostalCode.setEnabled(false);
        txtPostalCode.setFocusable(false);

        jLabel8.setText("Postal code");

        txtCountry.setEditable(false);
        txtCountry.setText("unknown");
        txtCountry.setEnabled(false);
        txtCountry.setFocusable(false);

        jLabel9.setText("Country");

        jLabel10.setText("JCNA #");

        txtClub.setEditable(false);
        txtClub.setText("unknown");
        txtClub.setEnabled(false);
        txtClub.setFocusable(false);

        jLabel11.setText("Club");

        jLabel13.setText("Select Jaguar");

        txtJudgeStatus.setEditable(false);
        txtJudgeStatus.setText("unknown");
        txtJudgeStatus.setEnabled(false);
        txtJudgeStatus.setFocusable(false);

        jLabel3.setText("Judge status");

        txtCertYear.setEditable(false);
        txtCertYear.setText("unknown");
        txtCertYear.setEnabled(false);
        txtCertYear.setFocusable(false);

        jLabel14.setText("Year certified");

        txtPhoneHome.setEditable(false);
        txtPhoneHome.setText("unknown");
        txtPhoneHome.setEnabled(false);
        txtPhoneHome.setFocusable(false);
        txtPhoneHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneHomeActionPerformed(evt);
            }
        });

        jLabel15.setText("Home phone");

        txtPhoneCell.setEditable(false);
        txtPhoneCell.setText("unknown");
        txtPhoneCell.setEnabled(false);
        txtPhoneCell.setFocusable(false);
        txtPhoneCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneCellActionPerformed(evt);
            }
        });

        txtPhoneWork.setEditable(false);
        txtPhoneWork.setText("unknown");
        txtPhoneWork.setEnabled(false);
        txtPhoneWork.setFocusable(false);
        txtPhoneWork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneWorkActionPerformed(evt);
            }
        });

        jLabel16.setText("Work phone");

        jLabel17.setText("Cell phone");

        txtEmail.setEditable(false);
        txtEmail.setText("unknown");
        txtEmail.setEnabled(false);
        txtEmail.setFocusable(false);

        jLabel18.setText("Email");

        jSeparator1.setForeground(new java.awt.Color(51, 51, 255));

        jSeparator2.setForeground(new java.awt.Color(51, 51, 255));

        jLabel20.setText("Entry Owner");

        btnAddEntry.setText("Add");
        btnAddEntry.setToolTipText("Click to add the seleted Entry.");
        btnAddEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEntryActionPerformed(evt);
            }
        });

        btnFinished.setText("Finished");
        btnFinished.setToolTipText("Click after adding one or more Entries. You can add more later.");
        btnFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishedActionPerformed(evt);
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
        cboJCNAClass.setToolTipText("Select the Class the Owner wants his/her Jaguar to be judges in.");
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
        txtDivision.setText("jTextField3");
        txtDivision.setEnabled(false);
        txtDivision.setFocusable(false);

        jLabel27.setText("Division");

        jLabel28.setText("Jaguars");

        txtMI.setEditable(false);
        txtMI.setText("   ");
        txtMI.setEnabled(false);

        jLabel12.setText("MI");

        txtModel.setEditable(false);
        txtModel.setEnabled(false);

        jLabel19.setText("Model");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinished)
                .addGap(313, 313, 313))
            .addGroup(layout.createSequentialGroup()
                .addGap(145, 145, 145)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboJCNAClass, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtDivision, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel27)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(txtClassDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(70, 70, 70)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPostalCode)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(39, 39, 39)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)))
                            .addComponent(jLabel4)
                            .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(103, 103, 103)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel1)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel10)
                                                .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(27, 27, 27)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel12)
                                            .addComponent(txtMI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboJaguars, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtModel)
                                .addGap(17, 17, 17)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPlateVIN, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPostalCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel19)))
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboJaguars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtPlateVIN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(16, 16, 16)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddEntry)
                    .addComponent(btnFinished)))
            .addGroup(layout.createSequentialGroup()
                .addGap(376, 376, 376)
                .addComponent(jLabel28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPhoneCellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneCellActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneCellActionPerformed

    private void txtPhoneWorkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneWorkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneWorkActionPerformed

    private void txtPhoneHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneHomeActionPerformed

    private void btnFinishedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishedActionPerformed
       int response = 0;
       if(this.changedSelections && !this.addedEntry) {
            response = yesNoDialog("You have not clicked Add for the current selections. Are you sure you want to leave the Add Entry dialog?");
            if(response != JOptionPane.YES_OPTION) {
                    return;
            }
        }
        theConcours.GetLogger().info("Finished adding Entries");
        this.setVisible(false);
        this.dispose();
         // This is not wanted when the dialog is invoked by ConcourseGUI, but must be done when run by main()
         // or the process will continu to run!
        if(systemexitwhenclosed){
            System.exit(0);
        }
    }//GEN-LAST:event_btnFinishedActionPerformed

    private void cboJaguarsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboJaguarsActionPerformed
    }//GEN-LAST:event_cboJaguarsActionPerformed

    private void cboUniqueNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUniqueNameActionPerformed
        //okDialog("cboUniqueNameActionPerformed");
    }//GEN-LAST:event_cboUniqueNameActionPerformed
    /*
     * Loads attributes of the selected Master Person into the form
     * and returns the list of Master Jaguars belonging to the selected Master Person 
    */ 
    private void UpdateMasterPersonAttributes(){
                Integer intCertYear;
                MasterPersonExt selectedMasterPerson;
                                      
		selectedMasterPerson = (MasterPersonExt) AddConcoursEntryDialog.this.cboUniqueName.getSelectedItem();

		if (selectedMasterPerson != null) {
                   // System.out.println("Selected '" + selectedMasterPerson.getFirstName() + " " + selectedMasterPerson.getLastName() + "'");
                    // strTemp = selectedMasterPerson.getJcna().toString();
                    txtJCNA.setText(selectedMasterPerson.getJcna().toString());
                    txtClub.setText(selectedMasterPerson.getClub());
                            
                    txtFirst.setText(selectedMasterPerson.getFirstName());
                    txtMI.setText(selectedMasterPerson.getMI()); // 3/18/2017
                    txtLast.setText(selectedMasterPerson.getLastName());
                            
                    txtJudgeStatus.setText(selectedMasterPerson.getJudgeStatus());
                    intCertYear = selectedMasterPerson.getCertYear();
                            
                    txtCertYear.setText(intCertYear.toString());
                            
                    txtStreetAddress.setText(selectedMasterPerson.getAddressSreet());
                    txtCity.setText(selectedMasterPerson.getCity() );
                    txtState.setText(selectedMasterPerson.getState() );
                    txtCountry .setText(selectedMasterPerson.getCountry() );
                    txtPostalCode.setText(selectedMasterPerson.getPostalCode() );

                    txtPhoneWork.setText(selectedMasterPerson.getPhoneWork());
                    txtPhoneHome.setText(selectedMasterPerson.getPhoneHome());
                    txtPhoneCell.setText(selectedMasterPerson.getPhoneCell());
                    txtEmail.setText(selectedMasterPerson.getEmail());
                    cboJaguars.setModel(new javax.swing.DefaultComboBoxModel(selectedMasterPerson.getJaguarStable()));        
                            
		}
               theConcours.GetLogger().info("UpdateMasterPersonAttributes completed in AddConcoursEntryDialog.");

    }
    
    
    private void cboUniqueNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboUniqueNameItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){ // Without this check the action will take place when item is selected and unselected... twice
            UpdateMasterPersonAttributes();
            UpdateJaguarAttributes();
            UpdateJCNAClassAttributes();
            this.changedSelections = true;
            this.addedEntry = false;
            this.btnAddEntry.setEnabled(true);
        }
   
        
    }//GEN-LAST:event_cboUniqueNameItemStateChanged
    
    private void UpdateJaguarAttributes(){
        MasterJaguar   theSelectedMasterJaguar;
        String strChampionshipClassName;
        String strDrivenClassName;
        String strPreversationClass;
        Integer intYear;
        String strYear;
        String strModel;
        JCNAClassesSubset possibleClasses = new JCNAClassesSubset();
        MyJavaUtils myJavaUtils = new MyJavaUtils();

        cboJCNAClass.removeAllItems();
        theSelectedMasterJaguar = (MasterJaguar)cboJaguars.getSelectedItem();
        intYear = theSelectedMasterJaguar.getYear();
        strYear = intYear.toString();
        txtYear.setText(strYear);
        strModel = theSelectedMasterJaguar.getModel();
        txtModel.setText(strModel);
        txtPlateVIN.setText(theSelectedMasterJaguar.getPlateVIN());
        txtDescription.setText(theSelectedMasterJaguar.getDescription());
        txtColor.setText(theSelectedMasterJaguar.getColor());
        // Note: getJcnaclass_c() returns jcna class name only
        strChampionshipClassName = theSelectedMasterJaguar.getJcnaclass_c();
        strDrivenClassName = theSelectedMasterJaguar.getJcnaclass_d();
        //  get the list of 2 JCNA classes associated with the selected Jaguar in master Jaguar list
        //  Must be a more efficient way!
        strPreversationClass = myJavaUtils.CalculatePreservationClass(strYear); 
                    
        boolean foundit = false;
        for( int i = 0; i<classMasterList.length ; i++){
                       if(classMasterList[i].getName().equals(strChampionshipClassName)){
                           possibleClasses.AddClass(classMasterList[i]);
                           foundit = true;
                            break;
                       }
        }
        if(!foundit){
            String msg = "Could not find class " + strChampionshipClassName + " in JCNA Master Class list";
            okCancelDialog(msg);
            theConcours.GetLogger().info(msg);
        }   
        
        foundit = false;
        for (JCNAClass classMasterList1 : classMasterList) {
                    if (classMasterList1.getName().equals(strDrivenClassName)) {
                        possibleClasses.AddClass(classMasterList1);
                        foundit = true;
                        break;
                    }
        }
        if(!foundit){
            String msg = "Could not find class " + strDrivenClassName + " in JCNA Master Class list";
            okCancelDialog(msg);
            theConcours.GetLogger().info(msg);
        }
        // Add Preservation classes
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
                String msg = "Could not find class " + strPreversationClass + " in JCNA Master Class list";
                okCancelDialog(msg);
                theConcours.GetLogger().info(msg);
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
     *  Populates selected Jaguar attribute fields, including cboJCNAClasses.
    */
    private void cboJaguarsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJaguarsItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
           this.changedSelections = true;
           this.addedEntry = false;
           UpdateJaguarAttributes(); 
           UpdateJCNAClassAttributes();
        }
        
    }//GEN-LAST:event_cboJaguarsItemStateChanged

    /*
     *  Update attributes of the selected Jaguar
    */
    private void cboJCNAClassItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJCNAClassItemStateChanged
       JCNAClass  theSelectedJCNAClass;
        List<JCNAClass> JCNAClassObjects; // for the selected Jaguar
        if (evt.getStateChange() == SELECTED) {
            this.changedSelections = true;
            this.addedEntry = false;
            UpdateJCNAClassAttributes();
        }
            
    }//GEN-LAST:event_cboJCNAClassItemStateChanged

    private void btnAddEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEntryActionPerformed
       
        // Modified 9/24/2018 to use addEntryHelper(), which is also used in Edit Concours Entry
        //Long mpid;
        //int concoursPersonnelNode;
        //JCNAClass JCNAClassObject;
       
        addedEntry = true; // Used to alert user if Finished adding Entires button is clicked without Clicking Add Entry first.
        String msgdebuging = "Starting btnAddEntryActionPerformed";
        theConcours.GetLogger().info(msgdebuging);
                
        JCNAClass JCNAClassObject = (JCNAClass) cboJCNAClass.getSelectedItem();
        String JCNAClassName = JCNAClassObject.getName();
        //addEntryHelper_2(Concours aConcours, String aOwnerUniqueName, String aJaguarUniqueDescription, String aStrJCNAClass, boolean aEditEntry){      
         // True means addEntryHelper_2 is being used for Add Entry, as opposed to Editing and Entry
        addEntryHelper_2(theConcours, cboUniqueName.getSelectedItem().toString(), cboJaguars.getSelectedItem().toString(), JCNAClassName, true);
        
        //////////////////////////////////////
        /*ConcoursClass theConcoursClass = null;
        Entry theNewEntry = null;
        ConcoursPerson theConcoursPerson = null;
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        Owner newOwner = null;
        
        MasterPersonExt selectedMasterPerson = (MasterPersonExt) AddConcoursEntryDialog.this.cboUniqueName.getSelectedItem();
        MasterJaguar  theSelectedMasterJaguar = (MasterJaguar)cboJaguars.getSelectedItem();
        uniqueDescription = theSelectedMasterJaguar.getUniqueDesc();
        mpid = selectedMasterPerson.getMasterPersonID();
        owner_unique_name = selectedMasterPerson.getUniqueName();
        concoursPersonnelNode = theConcoursePersonnel.PersonInPersonnelList(owner_unique_name); // returns Concours Personnel node or 0
        ownerNode = theConcours.GetConcoursOwnersObject().GetOwnerNode(owner_unique_name);
        entryNode = theConcours.GetEntries().ConcourseEntryNode(uniqueDescription);

        if(entryNode == 0){
            //  Need to add the entry
                //Entry(String aID, String aClass,  String aYear,    String aDescription,    String aUniqueDescription, String aOwnerFirst,    String aOwnerLast,  String aOwnerUnique,     String aJCNA,    String aColor,    String aPlate,    Integer aNode)           
           //JCNAClass = theSelectedMasterJaguar.getJcnaclass_c();
           //JCNAClass = theSelectedMasterJaguar.toString();
           JCNAClassObject = (JCNAClass) cboJCNAClass.getSelectedItem();
           strJCNAClass = JCNAClassObject.getName();
           Year = Integer.toString(theSelectedMasterJaguar.getYear());
           Description = theSelectedMasterJaguar.getDescription();
           
           Model = theSelectedMasterJaguar.getModel();
           uniqueDescription = theSelectedMasterJaguar.getUniqueDesc();
           OwnerFirst = selectedMasterPerson.getFirstName();
           OwnerMI = selectedMasterPerson.getMI();
           OwnerLast = selectedMasterPerson.getLastName();
           OwnerUnique = selectedMasterPerson.getUniqueName();
           JCNA = Integer.toString(selectedMasterPerson.getJcna());
           Color = theSelectedMasterJaguar.getColor();
           Plate = theSelectedMasterJaguar.getPlateVIN();
           entryID = theConcours.GetEntries().NextEntryID(strJCNAClass);
           entryNode = theConcours.GetEntries().NextEntryNode();
           theNewEntry = new Entry(entryID, strJCNAClass, Year, Model, Description, uniqueDescription, OwnerFirst, OwnerMI, OwnerLast, OwnerUnique, JCNA, Color, Plate, entryNode);
           theConcours.GetEntries().AddConcoursEntry(theNewEntry);
           // Changed 5/13/2015 
           boolean alreadyAConcourseClass = theConcours.GetConcoursClassesObject().isAConcoursClassNode(strJCNAClass);
           if(!alreadyAConcourseClass){ // First on this Class in the Concours so need to add it to ConcoursClasses
               theNewClassNode = theConcours.GetConcoursClassesObject().GetNextClassNode();
               theConcoursClass = new ConcoursClass(theConcours, strJCNAClass, theNewClassNode);
               // Also need to register the new Entry with the Class.
               theConcoursClass.AddEntryIndex(entryNode);
               theConcoursClass.AddEntryObject(theNewEntry);
               theConcours.GetConcoursClassesObject().AddConcoursClass(theConcoursClass);
               // Add theConcoursClass to the DB 
               loadSQLiteConcoursDatabase.UpdateAddConcoursClassesDBTable(theDBConnection, theConcoursClass);
           }
           else{
               // Class is already in ConcoursClasses, but we need register the new Entry with the Class.
               theConcoursClass = theConcours.GetConcoursClassesObject().GetConcoursClassObject(strJCNAClass);
               theConcoursClass.AddEntryIndex(entryNode);
               theConcoursClass.AddEntryObject(theNewEntry);
           }
           // Add the new Entry to the ConcoursClassesEntries table
           loadSQLiteConcoursDatabase.UpdateAddConcoursClassesEntriesDBTable(theDBConnection, theConcoursClass.GetClassName(), theNewEntry);
           //entryName = theConcours.GetEntries().getEntryID(entryNode);
           
           if(concoursPersonnelNode == 0) { // The entry owner isn't in ConcoursPersonnel
               // Add the person  to ConcoursPersonnel 
               concoursPersonnelNode = theConcoursePersonnel.NextNode();
               //Long aMasterpersonnel_id,  String aUnique_name, int aStatus_o,  int aStatus_j, int aConcourspersonnel_node
               theConcoursPerson = new ConcoursPerson(mpid, owner_unique_name, 1, 0, concoursPersonnelNode);
               theConcoursePersonnel.AddPerson(theConcoursPerson); 
               // Now update the affected database tables:
               loadSQLiteConcoursDatabase.UpdateAddConcoursPersonnelDBTable(theDBConnection, theConcours.GetLogger(), theConcoursPerson);
            } else{
               theConcoursPerson = theConcoursePersonnel.GetConcoursPerson(concoursPersonnelNode);
            }
           
           if(ownerNode == 0){ 
               // The owner isn't in  Owners... either a new Concours Person or an existing Judge
               // Add to Owners in either case
               newOwner = new Owner(owner_unique_name, concoursPersonnelNode); // Owner Node corresponds to ConcoursPersonnel Node
               newOwner.AddEntry(entryNode); // Owners have a List<Integers> of Entry nodes
               theConcoursOwners.AddOwner(newOwner);
               // Set the status_o flag in the ConcoursPerson
               theConcoursePersonnel.SetPersonOwnerStatus(owner_unique_name, 1);
               // Update status_o for theConcoursPerson
               loadSQLiteConcoursDatabase.UpdateSetstatus_oConcoursPersonnelDBTable(theDBConnection, mpid, 1);
               // If theConcoursPerson is a Judge we have to add the new Entry to his SelfEntry list
               if(theConcoursPerson.GetStatus_j() == 1){ 
                   Judge theJudge = theConcours.GetConcoursJudge(OwnerUnique);
                   if(theJudge == null){
                        JOptionPane.showMessageDialog(null, "Error in btnAddEntryActionPerformed: Person " + OwnerUnique + " is not in Concourse Judges although has status_j = 1 in Concours Personnel.");
                        //return;
                   }
                   theJudge.AddSelfEntry(strJCNAClass);
                   // Update the ConcoursJudgeClassSelfEntryTable
                   loadSQLiteConcoursDatabase.UpdateAddConcoursJudgeSelfEntryTable(theDBConnection,  theJudge,  strJCNAClass);
               }
               // Update the ConcoursOwners DB table
                loadSQLiteConcoursDatabase.UpdateAddConcoursOwnersDBTable(theDBConnection, newOwner);
           }
           ////////////////////////////////////////////           
           loadSQLiteConcoursDatabase.UpdateAddConcoursEntriesAndJaguarsDBTables(theDBConnection, theConcours, theNewEntry, theSelectedMasterJaguar, theConcoursClass);
           // ++++++++++++
           // Changed 7/14/2016 to remove the no longer valid Judge Assignment/schedule tables from the database
           // the previouse Judge Assignment is now invalid so manual editing is disabled.
            // Also, might as well clear the JudgeAssignments Table and EntryJudgesTable
            theConcours.SetJudgeAssignmentCurrent(false); 
            try {
                loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
            } catch (SQLException ex) {
                String msg = "SQLException in AddConcoursEntry call to SetSettingsTableJAState";
                okDialog(msg);
                theConcours.GetLogger( ).log(Level.SEVERE, msg, ex);
            }

            // Trying again... 7/14/2016   WORKED.  No locked tables. 
            // Problem returned January 2017 after making changes to put per-concourse in a subdirectory of same name.
            // In an effort to fix it, completely re-wrote the New concours action function to simplify. Still trying to fix it!
            //
            // 1/16/2017 Foreigh key issue while adding new Entry Skip the ClearJudgeAssignmentsTables() here... not necessary
            // 3/29/2017 Put this back in after fixing ClearJudgeAssignmentsTables()
            // 8/1/2017 Getting locked tables in LoadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB so once again disable this... unnecessary
            //loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);
           // ++++++++
           //--------
            // the previouse Judge Assignment is now invalid so manual editing is disabled.
            //boolean boolJudgeAssignmentCurrent = loadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn) ; 
            //theConcours.SetJudgeAssignmentCurrent(false); 
            //loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
            // ----------
            okDialog("Entry " + uniqueDescription + " added to Concours for " +  OwnerUnique);
            theConcours.GetLogger( ).info("Entry " + uniqueDescription + " added to Concours for " +  OwnerUnique);
        } else{
            JOptionPane.showMessageDialog(null, "The Jaguar " + cboJaguars.getSelectedItem() + "  is already entered. Entry will not be added.");
        }
        */
        //clearAllFields(); // this is not a good idea since all those fields are auto-filled when uniquename is changed
       // this.btnAddEntry.setEnabled(false); // gets enabled when uniquename is next selected
       MasterJaguar  theSelectedMasterJaguar = (MasterJaguar)cboJaguars.getSelectedItem();
       theConcours.GetLogger().info( "btnAddEntryActionPerformed completed for " +  theSelectedMasterJaguar.getUniqueDesc());
    }//GEN-LAST:event_btnAddEntryActionPerformed
    
    
    // Revised 9/28/2018
    // Rewriten to improve clarity and thereby correctness!
    
    public void addEntryHelper_2(Concours aConcours, String aOwnerUniqueName, String aJaguarUniqueDescription, String aStrJCNAClass, boolean aEditEntry){
        Entry theNewEntry = null;
        ConcoursPerson theConcoursPerson = null;
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        Owner newOwner = null;
        Long mpid;
        int concoursPersonnelNode;
        int ownerNode;
        int entryNode;
        //JCNAClass JCNAClassObject;
        String entryID;
        Integer theNewClassNode;
        ConcoursClass theConcoursClass;
        String Year;
        String Model;
        String Description;
        //String uniqueDescription;
        String OwnerFirst;
        String OwnerMI;
        String OwnerLast;
        
        String JCNA;
        String Color;
        String Plate;        
        
        //MasterPersonExt selectedMasterPerson = (MasterPersonExt) AddConcoursEntryDialog.this.cboUniqueName.getSelectedItem();
        MasterPersonnel mp = aConcours.GetMasterPersonnelObject();
        MasterPersonExt selectedMasterPerson = mp.GetMasterPerson(aOwnerUniqueName);
        //MasterJaguar  theSelectedMasterJaguar = (MasterJaguar)cboJaguars.getSelectedItem();
        MasterJaguars mjags = aConcours.GetMasterJaguarsObject();
        MasterJaguar  theSelectedMasterJaguar = mjags.GetMasterJaguar(aJaguarUniqueDescription);
        //uniqueDescription = theSelectedMasterJaguar.getUniqueDesc();
        mpid = selectedMasterPerson.getMasterPersonID();
        //owner_unique_name = selectedMasterPerson.getUniqueName();
        ConcoursPersonnel cp = aConcours.GetConcoursPersonnelObject();
        concoursPersonnelNode = cp.PersonInPersonnelList(aOwnerUniqueName); // returns Concours Personnel node or 0
        ownerNode = aConcours.GetConcoursOwnersObject().GetOwnerNode(aOwnerUniqueName);
        entryNode = aConcours.GetEntries().ConcourseEntryNode(aJaguarUniqueDescription);
        Owners  concoursOwners = aConcours.GetConcoursOwnersObject();
        if(entryNode == 0){
            //  Need to add the entry
           //JCNAClassObject = aConcours.GetJCNAClasses().getJCNAClass(aStrJCNAClass); 
           Year = Integer.toString(theSelectedMasterJaguar.getYear());
           Description = theSelectedMasterJaguar.getDescription();
           
           Model = theSelectedMasterJaguar.getModel();
           //uniqueDescription = theSelectedMasterJaguar.getUniqueDesc();
           OwnerFirst = selectedMasterPerson.getFirstName();
           OwnerMI = selectedMasterPerson.getMI();
           OwnerLast = selectedMasterPerson.getLastName();
           //owner_unique_name = selectedMasterPerson.getUniqueName();
           JCNA = Integer.toString(selectedMasterPerson.getJcna());
           Color = theSelectedMasterJaguar.getColor();
           Plate = theSelectedMasterJaguar.getPlateVIN();
           entryID = aConcours.GetEntries().NextEntryID(aStrJCNAClass);
           entryNode = aConcours.GetEntries().NextEntryNode();
           theNewEntry = new Entry(entryID, aStrJCNAClass, Year, Model, Description, aJaguarUniqueDescription, OwnerFirst, OwnerMI, OwnerLast, aOwnerUniqueName, JCNA, Color, Plate, entryNode);
           aConcours.GetEntries().AddConcoursEntry(theNewEntry);
           
           boolean alreadyAConcourseClass = aConcours.GetConcoursClassesObject().isAConcoursClassNode(aStrJCNAClass);
           if(!alreadyAConcourseClass){ // This will be first of this Class in the Concours so need to add it to ConcoursClasses
               theNewClassNode = aConcours.GetConcoursClassesObject().GetNextClassNode();
               theConcoursClass = new ConcoursClass(aConcours, aStrJCNAClass, theNewClassNode);
               // Also need to register the new Entry with the Class.
               theConcoursClass.AddEntryIndex(entryNode);
               theConcoursClass.AddEntryObject(theNewEntry);
               aConcours.GetConcoursClassesObject().AddConcoursClass(theConcoursClass);
               // Add theConcoursClass to the DB 
               loadSQLiteConcoursDatabase.UpdateAddConcoursClassesDBTable(aConcours.GetConnection(), theConcoursClass);
           }
           else{
               // Class is already in ConcoursClasses, but we need register the new Entry with the Class.
               theConcoursClass = aConcours.GetConcoursClassesObject().GetConcoursClassObject(aStrJCNAClass);
               theConcoursClass.AddEntryIndex(entryNode);
               theConcoursClass.AddEntryObject(theNewEntry);
           }
           // Add the new Entry to the ConcoursClassesEntries table
           loadSQLiteConcoursDatabase.UpdateAddConcoursClassesEntriesDBTable(aConcours.GetConnection(), theConcoursClass.GetClassName(), theNewEntry);
           if(concoursPersonnelNode == 0) { // The entry owner isn't in ConcoursPersonnel
               // Add the person  to ConcoursPersonnel 
               concoursPersonnelNode = cp.NextNode();
               //Long aMasterpersonnel_id,  String aUnique_name, int aStatus_o,  int aStatus_j, int aConcourspersonnel_node
               theConcoursPerson = new ConcoursPerson(mpid, aOwnerUniqueName, 1, 0, concoursPersonnelNode);
               cp.AddPerson(theConcoursPerson); 
               // Now update the affected database tables:
               loadSQLiteConcoursDatabase.UpdateAddConcoursPersonnelDBTable(aConcours.GetConnection(), aConcours.GetLogger(), theConcoursPerson);
            } else{
               theConcoursPerson = cp.GetConcoursPerson(concoursPersonnelNode);
            }
           
           if(ownerNode == 0){ 
               // The owner isn't in  Owners... either a new Concours Person or an existing Judge
               // Add to Owners in either case
               newOwner = new Owner(aOwnerUniqueName, concoursPersonnelNode); // Owner Node corresponds to ConcoursPersonnel Node
               newOwner.AddEntry(entryNode); // Owners have a List<Integers> of Entry nodes
               concoursOwners.AddOwner(newOwner);
               // Set the status_o flag in the ConcoursPerson
               cp.SetPersonOwnerStatus(aOwnerUniqueName, 1);
               // Update status_o for theConcoursPerson
               loadSQLiteConcoursDatabase.UpdateSetstatus_oConcoursPersonnelDBTable(aConcours.GetConnection(), mpid, 1);
               // If theConcoursPerson is a Judge we have to add the new Entry to his SelfEntry list
               if(theConcoursPerson.GetStatus_j() == 1){ 
                   Judge theJudge = aConcours.GetConcoursJudge(aOwnerUniqueName);
                   if(theJudge == null){
                        JOptionPane.showMessageDialog(null, "Error in btnAddEntryActionPerformed addEntryHelper_2(): Person " + aOwnerUniqueName + " is not in Concourse Judges although has status_j = 1 in Concours Personnel.");
                        //return;
                   }
                   theJudge.AddSelfEntry(aStrJCNAClass);
                   // Update the ConcoursJudgeClassSelfEntryTable
                   loadSQLiteConcoursDatabase.UpdateAddConcoursJudgeSelfEntryTable(aConcours.GetConnection(),  theJudge,  aStrJCNAClass);
               }
               // Update the ConcoursOwners DB table
                loadSQLiteConcoursDatabase.UpdateAddConcoursOwnersDBTable(aConcours.GetConnection(), newOwner);
           }
           ////////////////////////////////////////////           
           loadSQLiteConcoursDatabase.UpdateAddConcoursEntriesAndJaguarsDBTables(aConcours.GetConnection(), aConcours, theNewEntry, theSelectedMasterJaguar, theConcoursClass);
           // ++++++++++++
           // Changed 7/14/2016 to remove the no longer valid Judge Assignment/schedule tables from the database
           // the previouse Judge Assignment is now invalid so manual editing is disabled.
            // Also, might as well clear the JudgeAssignments Table and EntryJudgesTable
            aConcours.SetJudgeAssignmentCurrent(false); 
            try {
                loadSQLiteConcoursDatabase.SetSettingsTableJAState(aConcours.GetConnection(), false) ;
            } catch (SQLException ex) {
                String msg = "SQLException in AddConcoursEntry call to SetSettingsTableJAState";
                okDialog(msg);
                aConcours.GetLogger( ).log(Level.SEVERE, msg, ex);
            }

            // Trying again... 7/14/2016   WORKED.  No locked tables. 
            // Problem returned January 2017 after making changes to put per-concourse in a subdirectory of same name.
            // In an effort to fix it, completely re-wrote the New concours action function to simplify. Still trying to fix it!
            //
            // 1/16/2017 Foreigh key issue while adding new Entry Skip the ClearJudgeAssignmentsTables() here... not necessary
            // 3/29/2017 Put this back in after fixing ClearJudgeAssignmentsTables()
            // 8/1/2017 Getting locked tables in LoadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB so once again disable this... unnecessary
            //loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);
           // ++++++++
           //--------
            // the previouse Judge Assignment is now invalid so manual editing is disabled.
            //boolean boolJudgeAssignmentCurrent = loadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn) ; 
            //theConcours.SetJudgeAssignmentCurrent(false); 
            //loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
            // ----------
            String msg = "Entry " + aJaguarUniqueDescription + " added or changed for " +  aOwnerUniqueName;
            okDialog(msg);
            aConcours.GetLogger( ).info(msg);
        } else{
            JOptionPane.showMessageDialog(null, "The Jaguar " + aJaguarUniqueDescription + "  is already entered in Concours. Entry will not be added.");
        }
        
    }
    // Added 9/24/2018. Used in btnAddEntryActionPerformed(), AND in EditConcoursEntryDialogRev
    //     See addEntryHelper_2
    /* 
    public void addEntryHelper(JCNAClass JCNAClassObject, boolean aEditEntry){
        String msgdebuging = "Starting addEntryHelper";
        theConcours.GetLogger().info(msgdebuging);
        
        String entryID;      
        int concoursPersonnelNode;
        Long mpid;
        Integer ownerNode;
        Integer theNewClassNode;
        Integer entryNode;
        String owner_unique_name;
        ConcoursClass theConcoursClass = null;
        Entry theNewEntry = null;
        ConcoursPerson theConcoursPerson = null;
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        Owner newOwner = null;
        String strJCNAClass;
        String Year;
        String Model;
        String Description;
        String uniqueDescription;
        String OwnerFirst;
        String OwnerMI;
        String OwnerLast;
        String OwnerUnique;
        String JCNA;
        String Color;
        String Plate;        
        
        MasterPersonExt selectedMasterPerson = (MasterPersonExt) AddConcoursEntryDialog.this.cboUniqueName.getSelectedItem();
        MasterJaguar  theSelectedMasterJaguar = (MasterJaguar)cboJaguars.getSelectedItem();
        uniqueDescription = theSelectedMasterJaguar.getUniqueDesc();
        mpid = selectedMasterPerson.getMasterPersonID();
        owner_unique_name = selectedMasterPerson.getUniqueName();
        concoursPersonnelNode = theConcoursePersonnel.PersonInPersonnelList(owner_unique_name); // returns Concours Personnel node or 0
        ownerNode = theConcours.GetConcoursOwnersObject().GetOwnerNode(owner_unique_name);
        entryNode = theConcours.GetEntries().ConcourseEntryNode(uniqueDescription);
        // Note: When aEditEntry == true entryNode will be 0 because it has just been removed. Consequently, it will be re-added with user edits.
        if(entryNode == 0){
            //  Need to add the entry
           strJCNAClass = JCNAClassObject.getName();
           Year = Integer.toString(theSelectedMasterJaguar.getYear());
           Description = theSelectedMasterJaguar.getDescription();
           
           Model = theSelectedMasterJaguar.getModel();
           uniqueDescription = theSelectedMasterJaguar.getUniqueDesc();
           OwnerFirst = selectedMasterPerson.getFirstName();
           OwnerMI = selectedMasterPerson.getMI();
           OwnerLast = selectedMasterPerson.getLastName();
           OwnerUnique = selectedMasterPerson.getUniqueName();
           JCNA = Integer.toString(selectedMasterPerson.getJcna());
           Color = theSelectedMasterJaguar.getColor();
           Plate = theSelectedMasterJaguar.getPlateVIN();
           entryID = theConcours.GetEntries().NextEntryID(strJCNAClass);
           entryNode = theConcours.GetEntries().NextEntryNode();
           theNewEntry = new Entry(entryID, strJCNAClass, Year, Model, Description, uniqueDescription, OwnerFirst, OwnerMI, OwnerLast, OwnerUnique, JCNA, Color, Plate, entryNode);
           theConcours.GetEntries().AddConcoursEntry(theNewEntry);
           // Changed 5/13/2015 
           boolean alreadyAConcourseClass = theConcours.GetConcoursClassesObject().isAConcoursClassNode(strJCNAClass);
           if(!alreadyAConcourseClass){ // First on this Class in the Concours so need to add it to ConcoursClasses
               theNewClassNode = theConcours.GetConcoursClassesObject().GetNextClassNode();
               theConcoursClass = new ConcoursClass(theConcours, strJCNAClass, theNewClassNode);
               // Also need to register the new Entry with the Class.
               theConcoursClass.AddEntryIndex(entryNode);
               theConcoursClass.AddEntryObject(theNewEntry);
               theConcours.GetConcoursClassesObject().AddConcoursClass(theConcoursClass);
               // Add theConcoursClass to the DB 
               loadSQLiteConcoursDatabase.UpdateAddConcoursClassesDBTable(theDBConnection, theConcoursClass);
           }
           else{
               // Class is already in ConcoursClasses, but we need register the new Entry with the Class.
               theConcoursClass = theConcours.GetConcoursClassesObject().GetConcoursClassObject(strJCNAClass);
               theConcoursClass.AddEntryIndex(entryNode);
               theConcoursClass.AddEntryObject(theNewEntry);
           }
           // Add the new Entry to the ConcoursClassesEntries table
           loadSQLiteConcoursDatabase.UpdateAddConcoursClassesEntriesDBTable(theDBConnection, theConcoursClass.GetClassName(), theNewEntry);
           //entryName = theConcours.GetEntries().getEntryID(entryNode);
           
           if(concoursPersonnelNode == 0) { // The entry owner isn't in ConcoursPersonnel
               // Add the person  to ConcoursPersonnel 
               concoursPersonnelNode = theConcoursePersonnel.NextNode();
               //Long aMasterpersonnel_id,  String aUnique_name, int aStatus_o,  int aStatus_j, int aConcourspersonnel_node
               theConcoursPerson = new ConcoursPerson(mpid, owner_unique_name, 1, 0, concoursPersonnelNode);
               theConcoursePersonnel.AddPerson(theConcoursPerson); 
               // Now update the affected database tables:
               loadSQLiteConcoursDatabase.UpdateAddConcoursPersonnelDBTable(theDBConnection, theConcours.GetLogger(), theConcoursPerson);
            } else{
               theConcoursPerson = theConcoursePersonnel.GetConcoursPerson(concoursPersonnelNode);
            }
           
           if(ownerNode == 0){ 
               // The owner isn't in  Owners... either a new Concours Person or an existing Judge
               // Add to Owners in either case
               newOwner = new Owner(owner_unique_name, concoursPersonnelNode); // Owner Node corresponds to ConcoursPersonnel Node
               newOwner.AddEntry(entryNode); // Owners have a List<Integers> of Entry nodes
               theConcoursOwners.AddOwner(newOwner);
               // Set the status_o flag in the ConcoursPerson
               theConcoursePersonnel.SetPersonOwnerStatus(owner_unique_name, 1);
               // Update status_o for theConcoursPerson
               loadSQLiteConcoursDatabase.UpdateSetstatus_oConcoursPersonnelDBTable(theDBConnection, mpid, 1);
               // If theConcoursPerson is a Judge we have to add the new Entry to his SelfEntry list
               if(theConcoursPerson.GetStatus_j() == 1){ 
                   Judge theJudge = theConcours.GetConcoursJudge(OwnerUnique);
                   if(theJudge == null){
                        JOptionPane.showMessageDialog(null, "Error in btnAddEntryActionPerformed: Person " + OwnerUnique + " is not in Concourse Judges although has status_j = 1 in Concours Personnel.");
                        //return;
                   }
                   theJudge.AddSelfEntry(strJCNAClass);
                   // Update the ConcoursJudgeClassSelfEntryTable
                   loadSQLiteConcoursDatabase.UpdateAddConcoursJudgeSelfEntryTable(theDBConnection,  theJudge,  strJCNAClass);
               }
               // Update the ConcoursOwners DB table
                loadSQLiteConcoursDatabase.UpdateAddConcoursOwnersDBTable(theDBConnection, newOwner);
           }
           if(aEditEntry){
                JOptionPane.showMessageDialog(null, "The Jaguar Entry" + cboJaguars.getSelectedItem() + " has been added to Concours Entries.");               
           } else{
                JOptionPane.showMessageDialog(null, "Changes to the Entry" + cboJaguars.getSelectedItem() + " have been saved.");               
           }
        
        } else{
            JOptionPane.showMessageDialog(null, "The Jaguar Entry " + cboJaguars.getSelectedItem() + "  is already in the Concours so will not be added.");
        }
        
        theConcours.GetLogger().info("Finished addEntryHelper");
    }
    */
    private void clearAllFields(){
        this.txtCertYear.setText("unknown");
        this.txtCity.setText("unknown");
        this.txtClassDescription.setText("unknown");
        this.txtClub.setText("unknown");
        this.txtColor.setText("unknown");
        this.txtCountry.setText("unknown");
        this.txtDescription.setText("unknown");
        this.txtDivision.setText("unknown");
        this.txtEmail.setText("unknown");
        this.txtFirst.setText("unknown");
        this.txtJCNA.setText("999999");
        this.txtJudgeStatus.setText("unknown");
        this.txtLast.setText("unknown");
        this.txtPhoneCell.setText("unknown");
        this.txtPhoneHome.setText("unknown");
        this.txtPhoneWork.setText("unknown");
        this.txtPlateVIN.setText("unknown");
        this.txtPostalCode.setText("unknown");
        this.txtState.setText("unknown");
        this.txtStreetAddress.setText("unknown");
        this.txtYear.setText("unknown");
}
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
       JOptionPane.showMessageDialog(null, theMessage);
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
            java.util.logging.Logger.getLogger(AddConcoursEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                fh = new FileHandler(strPath + "ConcoursBuilder.log");  // The log file will be in the strPath
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
            theConcours.LoadConcoursClassesDB(conn, theConcours, theConcours.GetLogger());
            theConcours.LoadJudgesDB(conn, theConcours.GetLogger()); // Judge loads in Judges gets set when TimeslotAssignment is being updated
            theConcours.LoadOwnersDB(conn, theConcours.GetLogger());
                
//            MasterListRepository masterList = new MasterListRepository(conn);
            boolean loadRepositoryFromMemory = true;
            MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);
            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
            ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
            JCNAClass [] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()] );
            AddConcoursEntryDialog theDialog = new AddConcoursEntryDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, masterList, classMasterArray, true);
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
                Logger.getLogger(AddConcoursEntryDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEntry;
    private javax.swing.JButton btnFinished;
    private javax.swing.JComboBox cboJCNAClass;
    private javax.swing.JComboBox cboJaguars;
    private javax.swing.JComboBox cboUniqueName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField txtCertYear;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtClassDescription;
    private javax.swing.JTextField txtClub;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextField txtCountry;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtDivision;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFirst;
    private javax.swing.JTextField txtJCNA;
    private javax.swing.JTextField txtJudgeStatus;
    private javax.swing.JTextField txtLast;
    private javax.swing.JTextField txtMI;
    private javax.swing.JTextField txtModel;
    private javax.swing.JTextField txtPhoneCell;
    private javax.swing.JTextField txtPhoneHome;
    private javax.swing.JTextField txtPhoneWork;
    private javax.swing.JTextField txtPlateVIN;
    private javax.swing.JTextField txtPostalCode;
    private javax.swing.JTextField txtState;
    private javax.swing.JTextField txtStreetAddress;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables


}
