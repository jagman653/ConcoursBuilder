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
import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterJaguars;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.MyJavaUtils;

/**
 *
 * @author Ed Sowell
 */
public class EditMasterPersonDialog2 extends javax.swing.JDialog {
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

        Concours theConcours;
        boolean systemexitwhenclosed;
        Connection theDBConnection;
        boolean boolNewMasterPerson;
        boolean boolAddedNewJag;
        int stablesize;

        //int numJagsAdded = 0;
        MasterJaguar newMj;
        MasterJaguar removedMJ;
        MasterJaguar selectedMasterJaguar;
        List<MasterJaguar> newJagList;
        List<MasterJaguar> masterJagsToBeRemovedJagList;
        MyJavaUtils utils = new MyJavaUtils();

        
        MasterPersonnel theMasterPersonnel;
        int MAX_FIRST_NAME_EXTENSION = 3; // used in construction og unique name NOW IN CONCOURS
  
    /**
     * Constructor
     * Creates new form EditMasterPersonDialog2
     */
    public EditMasterPersonDialog2(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, MasterListRepository aRepository, boolean aSystemexitwhenclosed) {
        super(parent, modal);
        this.masterJagsToBeRemovedJagList = new ArrayList<>();
        this.newJagList = new ArrayList<>();
        initComponents();
        this.setTitle("Edit Master Person");
        
        theConcours = aConcours;
        theDBConnection = theConcours.GetConnection();
        theConcours.GetLogger().info("Starting Edit Master Person");
        //theConcoursOwners = theConcours.GetConcoursOwnersObject();
        theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        
	// custom filterator
	TextFilterator<MasterPersonExt> textFilterator = GlazedLists.textFilterator(
		MasterPersonExt.class, "uniqueName"); // Why is N necessary? I.e., why doesn't "uniquename" work even when that is the property name?

	/*
         * install auto-completion
         */
	MasterPersonExt[] allMasterPersonnel;
        allMasterPersonnel = aRepository.getAllMembers();
	AutoCompleteSupport support = AutoCompleteSupport.install(
		this.cboUniqueName, GlazedLists.eventListOf(allMasterPersonnel),
		textFilterator, new EditMasterPersonDialog2.MemberInfoFormat());
	// and set to strict mode
	support.setStrict(true);
        
        UpdateMasterPersonAttributes();
    }
    
        /*
     * Loads attributes of the selected Master Person into the form
     * and returns the list of Master Jaguars belonging to the selected Master Person 
    */ 
    private void UpdateMasterPersonAttributes(){
/*
        String strSelectedMasterPersonUniqueName;
        MasterPersonExt selectedMasterPerson;
       // System.out.println("cboUniqueName ItemEvent.SELECTED "); 
        strSelectedMasterPersonUniqueName = cboUniqueName.getSelectedItem().toString();

        //selectedMasterPerson = (MasterPersonExt) AddOrEditMasterPersonDialog.this.cboUniqueName.getSelectedItem();
        MasterPersonnel theMasterPersonnel = theConcours.GetMasterPersonnelObject(); 
        selectedMasterPerson = theMasterPersonnel.GetMasterPerson(strSelectedMasterPersonUniqueName); // returns null if not currently in MasterPersonnel
        
        */
        
        Integer intCertYear;
        MasterPersonExt selectedMasterPerson;
       // System.out.println("cboUniqueName ItemEvent.SELECTED ");                       
        selectedMasterPerson = (MasterPersonExt) EditMasterPersonDialog2.this.cboUniqueName.getSelectedItem();

        if (selectedMasterPerson != null) {
           // System.out.println("Selected '" + selectedMasterPerson.getFirstName() + " " + selectedMasterPerson.getLastName() + "'");
            // strTemp = selectedMasterPerson.getJcna().toString();
            txtJCNA.setText(selectedMasterPerson.getJcna().toString());
            txtClub.setText(selectedMasterPerson.getClub());

            txtFirstName.setText(selectedMasterPerson.getFirstName());
            txtLastName.setText(selectedMasterPerson.getLastName());
            String un = selectedMasterPerson.getUniqueName();
            // The following no longer needed since mi is no in the database and internal data structures. 3/18/2018
            //String strMI = utils.getMI(txtLastName.getText(), txtFirstName.getText(), un,  theConcours.GetMaxFirstNameExtension());  3/18/2018
            txtMI.setText(selectedMasterPerson.getMI());

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
            this.cboJaguarList.setModel(new javax.swing.DefaultComboBoxModel(selectedMasterPerson.getJaguarStable()));        

        } else {
            okDialog("UniqueName " + selectedMasterPerson.getUniqueName().toString() + " not found in EditMasterPersonDialog2");
            theConcours.GetLogger().info("UniqueName " + selectedMasterPerson.getUniqueName().toString() + " not found in EditMasterPersonDialog2");
        }

    }
    
    
    //private void cboUniqueNameItemStateChanged(java.awt.event.ItemEvent evt) {                                               
//        }

   // }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        cboUniqueName = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        txtFirstName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtMI = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtJCNA = new javax.swing.JTextField();
        txtClub = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtJudgeStatus = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtCertYear = new javax.swing.JTextField();
        txtStreetAddress = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtState = new javax.swing.JTextField();
        txtPostalCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCountry = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtPhoneHome = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtPhoneWork = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtPhoneCell = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cboJaguarList = new javax.swing.JComboBox();
        btnAddNewJaguar = new javax.swing.JButton();
        btnRemoveJaguar = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnFinished = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnEditJaguar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel4.setText("Unique name");

        cboUniqueName.setEditable(true);
        cboUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboUniqueName.setToolTipText("Select Master Person to be edited. Note: The dropdown list omits Master Persons active in the current Concours.");
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

        jLabel2.setText("Last");

        txtLastName.setEditable(false);
        txtLastName.setToolTipText("Cannot be changed. If incorrect spellling delete Master Person and Add new.");
        txtLastName.setEnabled(false);
        txtLastName.setFocusable(false);
        txtLastName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtLastNameFocusLost(evt);
            }
        });
        txtLastName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLastNameActionPerformed(evt);
            }
        });
        txtLastName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtLastNamePropertyChange(evt);
            }
        });

        txtFirstName.setEditable(false);
        txtFirstName.setToolTipText("Cannot be changed. If incorrect, deleter MAster Person and Add new.");
        txtFirstName.setEnabled(false);
        txtFirstName.setFocusable(false);

        jLabel1.setText("First");

        jLabel19.setText("MI");

        txtMI.setEditable(false);
        txtMI.setToolTipText("Cannot be changed. If incorrect, deoete MAster Person and Add new.");
        txtMI.setEnabled(false);
        txtMI.setFocusable(false);

        jLabel10.setText("JCNA #");

        txtJCNA.setText("unknown");
        txtJCNA.setToolTipText("Currently optional");
        txtJCNA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJCNAActionPerformed(evt);
            }
        });

        txtClub.setText("unknown");
        txtClub.setToolTipText("Currently optional");

        jLabel11.setText("Club");

        jLabel3.setText("Judge status");

        txtJudgeStatus.setText("unknown");
        txtJudgeStatus.setToolTipText("Currently optional");
        txtJudgeStatus.setMinimumSize(new java.awt.Dimension(15, 20));
        txtJudgeStatus.setName(""); // NOI18N

        jLabel14.setText("Year certified");

        txtCertYear.setText("unknown");
        txtCertYear.setToolTipText("Currently optional");

        txtStreetAddress.setText("unknown");
        txtStreetAddress.setToolTipText("Currently optional");

        jLabel5.setText("Street address");

        jLabel6.setText("City");

        txtCity.setText("unknown");
        txtCity.setToolTipText("Currently optional");

        jLabel7.setText("State");

        txtState.setText("unknown");
        txtState.setToolTipText("Currently optional");

        txtPostalCode.setText("unknown");
        txtPostalCode.setToolTipText("Currently optional");

        jLabel8.setText("Postal code");

        jLabel9.setText("Country");

        txtCountry.setText("unknown");
        txtCountry.setToolTipText("Currently optional");

        jLabel15.setText("Home phone");

        txtPhoneHome.setText("unknown");
        txtPhoneHome.setToolTipText("Currently optional");
        txtPhoneHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneHomeActionPerformed(evt);
            }
        });

        jLabel16.setText("Work phone");

        txtPhoneWork.setText("unknown");
        txtPhoneWork.setToolTipText("Currently optional");
        txtPhoneWork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneWorkActionPerformed(evt);
            }
        });

        jLabel17.setText("Cell phone");

        txtPhoneCell.setText("unknown");
        txtPhoneCell.setToolTipText("Currently optional");
        txtPhoneCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneCellActionPerformed(evt);
            }
        });

        txtEmail.setText("unknown");
        txtEmail.setToolTipText("Currently optional");

        jLabel18.setText("Email");

        jLabel13.setText("Jaguar stable");

        cboJaguarList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJaguarList.setToolTipText("If you want to remove a Jaguar from the stable, select it here.");

        btnAddNewJaguar.setText("Add new Jaguar");
        btnAddNewJaguar.setToolTipText("Click to bring up a dialog for adding a Jaguar to the stable");
        btnAddNewJaguar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewJaguarActionPerformed(evt);
            }
        });

        btnRemoveJaguar.setText("Remove Jaguar");
        btnRemoveJaguar.setToolTipText("Click to remove the selected Jaguar");
        btnRemoveJaguar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveJaguarActionPerformed(evt);
            }
        });

        btnSave.setText("Save Edited Master Person");
        btnSave.setToolTipText("Save the selected Master Person with any changes made. Can be used repeatedly.");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnFinished.setText("Finished Editing  Master Persons");
        btnFinished.setToolTipText("Click after editing one or more Master Persons. Returns to the main ConcoursBuilder dialog.");
        btnFinished.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                btnFinishedMouseDragged(evt);
            }
        });
        btnFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishedActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setToolTipText("Click if you do not want to save the changes made for the selected Master Person.");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnEditJaguar.setText("Edit Jaguar");
        btnEditJaguar.setToolTipText("Click to remove the selected Jaguar");
        btnEditJaguar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditJaguarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16)
                                            .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(189, 189, 189))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel9))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtPostalCode, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboJaguarList, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(btnAddNewJaguar)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnEditJaguar)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnRemoveJaguar)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(37, 37, 37)
                                        .addComponent(jLabel4)
                                        .addGap(103, 103, 103)
                                        .addComponent(jLabel2)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel1)
                                    .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(txtMI, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSave)
                                .addGap(55, 55, 55)
                                .addComponent(btnFinished)
                                .addGap(37, 37, 37)
                                .addComponent(btnCancel)
                                .addGap(4, 4, 4)))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(142, 142, 142))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel14)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPostalCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(3, 3, 3)
                        .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(3, 3, 3)
                        .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(3, 3, 3)
                        .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(3, 3, 3)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32)
                .addComponent(jLabel13)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboJaguarList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddNewJaguar)
                    .addComponent(btnRemoveJaguar)
                    .addComponent(btnEditJaguar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnSave)
                    .addComponent(btnFinished)
                    .addComponent(btnCancel))
                .addGap(49, 49, 49))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboUniqueNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUniqueNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboUniqueNameActionPerformed

    private void txtLastNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLastNameFocusLost
        /*
        String lastname = txtLastName.getText();
        ArrayList<String> firstnames = new ArrayList<String>();
        if("lastname".equals(lastname)){
            JOptionPane.showMessageDialog(null, "You must enter Last name first. Ignor if Cancelling the dialog");
        } else{
            firstnames = theConcours.GetMasterPersonnelObject().GetMasterPersonnelFirstNames(lastname);
            PopulateCboFirstName(firstnames);
            cboUniqueName.setEnabled(true);
        }
        */
    }//GEN-LAST:event_txtLastNameFocusLost

    private void txtLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLastNameActionPerformed

    }//GEN-LAST:event_txtLastNameActionPerformed

    private void txtLastNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtLastNamePropertyChange

    }//GEN-LAST:event_txtLastNamePropertyChange

    private void txtJCNAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJCNAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJCNAActionPerformed

    private void txtPhoneHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneHomeActionPerformed

    private void txtPhoneWorkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneWorkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneWorkActionPerformed

    private void txtPhoneCellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneCellActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneCellActionPerformed

    private void btnAddNewJaguarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewJaguarActionPerformed
        Connection conn = theConcours.GetConnection();
        ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
        //String strOwnerUniqueName = cboUniqueName.getSelectedItem().toString();
        MasterPersonExt mp = (MasterPersonExt)cboUniqueName.getSelectedItem();
        String strOwnerUniqueName = mp.getUniqueName();
        if(strOwnerUniqueName == null || strOwnerUniqueName.isEmpty()){
            String msg = "Null or empty strOwnerUniqueName in addMasterJaguarDialog.getNewMasterJaguar()";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
            return;
        }
        if(boolNewMasterPerson ){
            if(newJagList.isEmpty()){
                // a new master person will have 4 place-holder Items in the cboJaguarList
                // These need to be removed for the first added jag
                this.cboJaguarList.removeAllItems();
            }
        }
        AddMasterJaguar_2 addMasterJaguarDialog = new AddMasterJaguar_2(new javax.swing.JFrame(), true, conn, theConcours,  JCNAClassesList, strOwnerUniqueName);
        addMasterJaguarDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Exiting AddMasterJaguar back to AddEditMasterPersonDialog");
            }
        });
        addMasterJaguarDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        addMasterJaguarDialog .setVisible(true);
        newMj = addMasterJaguarDialog.getNewMasterJaguar(); // retrives  result from AddMasterJaguar_2() NOTE: masterperson_id & masterjaguar_id null at this point
        if(newMj == null){
            theConcours.GetLogger().info("No new Master Jaguar added");
            okDialog("No new Master Jaguar added");
            return;
        }
        
        String newMjUniqueDescription = newMj.getUniqueDesc();
        // Check to be sure it hasn't already been added to stable
        if(isInStable(newMjUniqueDescription)){
            boolAddedNewJag = true;
            okDialog("Jaguar with Unique Description " + newMjUniqueDescription + " is already in stable. Will not be added.");
            theConcours.GetLogger().info("Jaguar " + newMjUniqueDescription + " is already in stable. Will not be added for MasterPerson " + strOwnerUniqueName + ". boolAddedNewJag is: " + boolAddedNewJag);
        } else{
            if (masterJagListContains(masterJagsToBeRemovedJagList, newMj)){
                int response = yesNoDialog("This Add will effectively reverse the recent Remove of " + newMj + " Is that what you want?");
                if(response == JOptionPane.NO_OPTION) {
                    // No ressponse means the user wants the Add to be carried out. Accordingly, we do nothing, leaving newMj on the masterJagsToBeRemovedJagList
                } else {
                    // User response was Yes, so the Add must be carried out.
                    // But since it hasn't really been Removed removing it from the masterJagsToBeRemovedJagList effectivly "Adds it back"
                    //boolean remove = masterJagsToBeRemovedJagList.remove(newMj);
                    boolean remove = masterJagListRemove(masterJagsToBeRemovedJagList, newMj);
                    cboJaguarList.addItem(newMj); // Put it back in cboJaguarList
                    stablesize = stablesize + 1;
                    okDialog( "The previously removed Master Jaguar " + newMj.getUniqueDesc() + " will be added back. ");
                }
            } else{
                cboJaguarList.addItem(newMj);
                stablesize = stablesize + 1;
                newJagList.add(newMj);
                boolAddedNewJag = true;
                okDialog("Jaguar " + newMj.getUniqueDesc() + " added for MasterPerson " + strOwnerUniqueName);
                theConcours.GetLogger().info("Jaguar " + newMj.getUniqueDesc() + " added for MasterPerson " + strOwnerUniqueName );
            }
        }
    }//GEN-LAST:event_btnAddNewJaguarActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        Long mpid;

        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        MasterJaguar [] newJagArray = newJagList.toArray(new MasterJaguar[newJagList.size()]);
        String fn = txtFirstName.getText();
        MasterPersonExt mpExisting = (MasterPersonExt)this.cboUniqueName.getSelectedItem();
        String un = mpExisting.getUniqueName();
        String mi = txtMI.getText();
        // Note that this temporary MasterPerson is only used to pass the edited data into UpdateUpdateMasterPerson so the database can be updated.
        // Note that newJagArray is only the ADDED Jaguars. UpdateUpdateMasterPerson adds then to the existing stable of the Masterperson being edited.
        // 
        Integer intJCNA = Integer.parseInt(txtJCNA.getText());
        String strClub = txtClub.getText();
        String strLN = txtLastName.getText();
        String strJudgeStatus = txtJudgeStatus.getText();
        Integer intCertYear = Integer.parseInt(txtCertYear.getText());
        String strStreet = txtStreetAddress.getText();
        String strCity =  txtCity.getText();
        String strState =  txtState.getText();
        String strCountry = txtCountry.getText();
        String strPostalCode = txtPostalCode.getText();
        String strPhoneWork = txtPhoneWork.getText();
        String strPhoneHome =txtPhoneHome.getText();
        String strPhoneCell = txtPhoneCell.getText();
        String strEmail = txtEmail.getText();
        MasterPersonExt tempMasterPerson = new MasterPersonExt(0L, intJCNA, strClub, strLN, fn,
            mi, un, strJudgeStatus, intCertYear, strStreet,
            strCity, strState, strCountry, strPostalCode,
            strPhoneWork, strPhoneHome, strPhoneCell, strEmail, newJagArray);
        //
        // Update Master Person to MasterPersonnel and MasterJaguar DB tables.
        // Any new Jags addded are passed into UpdateUpdateMasterPerson() as the "stable" of tempMasterPerson
        // Any Jags to be removed are passed a a separate list, masterJagsToBeRemovedJagList
        //  mpid isn't used since this is an existing Master Person, presumably already with an mpid...
        //
        mpid = loadSQLiteConcoursDatabase.UpdateUpdateMasterPerson(theDBConnection, tempMasterPerson, masterJagsToBeRemovedJagList);

        //
        // Must add the added Jags to the corresponding MasterPerson in memory, as well as deleting masterJagsToBeRemovedJagList 
        //
        //       NOTE: this might look like extra work being done needlessly, but adding the new Jag to mpSelected  
        //             Results in it not showing up in  Display Master Personnel 
        MasterPersonnel objMasterPersonnel = theConcours.GetMasterPersonnelObject();
        MasterPersonExt mpSelected = (MasterPersonExt)cboUniqueName.getSelectedItem();
        String strSelectedMasterPersonUniqueName = mpSelected.getUniqueName();
        MasterPersonExt selectedMasterPerson = objMasterPersonnel.GetMasterPerson(strSelectedMasterPersonUniqueName); // this mp is from the cbo and therefore still has the original stable!!
        
        // update personal data in memory
        // 10/16/2018 Also up MasterJaguar list
        MasterJaguars theMasterJaguars = theConcours.GetMasterJaguarsObject(); // 10/16/2018
        selectedMasterPerson.setMasterPersonPersonalData(intJCNA, strClub, strLN, fn,
            mi, un, strJudgeStatus, intCertYear, strStreet,
            strCity, strState, strCountry, strPostalCode,
            strPhoneWork, strPhoneHome, strPhoneCell,strEmail);
        for(MasterJaguar mj : newJagArray){
            Long mjid = loadSQLiteConcoursDatabase.getMasterJaguarID(theDBConnection, mj.getUniqueDesc());
            mj.setMj_id(mjid);
            mj.setMp_id(mpid);
            selectedMasterPerson.addJaguarToStable(mj);
            theMasterJaguars.AddJaguar(mj); // 10/16/2018
        }
        //
        //  Remove masterJagsToBeRemovedJagList
        //
        for(MasterJaguar mj : masterJagsToBeRemovedJagList){
            selectedMasterPerson.removeJaguarFromStable(mj);
        }
        boolAddedNewJag = false;
        okDialog("Master Person " + selectedMasterPerson.getUniqueName() + " edits saved.");
        //clearAllFields();                                                                    removed   1/27/2017
        //this.cboJaguarList.removeAllItems();                                                 removed   1/27/2017


        //
        //  Add or Edit/Save until Finished is clicked
        //
        //this.setVisible(false);
        //this.dispose();

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnFinishedMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFinishedMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFinishedMouseDragged

    private void btnFinishedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishedActionPerformed
        if(boolAddedNewJag){
            okDialog("You have not saved Master Person since a new Jaguar was added to the stable");
        } else {
            this.setVisible(false);
            this.dispose();
        }
        // System.exit() is not wanted when the dialog is invoked by ConcourseGUI, but must be done when run by main()
        // or the process will continu to run!
        if(systemexitwhenclosed){
            System.exit(0);
        }
    }//GEN-LAST:event_btnFinishedActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void cboUniqueNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboUniqueNameItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){ // Without this check the action will take place when item is selected and unselected... twice
            UpdateMasterPersonAttributes();
            MasterPersonExt mpSelected = (MasterPersonExt)cboUniqueName.getSelectedItem();
            List<MasterJaguar> stableLst =  mpSelected.getJaguarStableList(); 
            stablesize = stableLst.size(); // needed to prevent over-removal when Remove is done
            this.btnSave.setEnabled(true);
        }
    }//GEN-LAST:event_cboUniqueNameItemStateChanged

    private void btnRemoveJaguarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveJaguarActionPerformed
        //okDialog("Not implemented");
        MasterPersonExt mpSelected = (MasterPersonExt)cboUniqueName.getSelectedItem();
        //String strSelectedMasterPersonUniqueName = mpSelected.getUniqueName();
        //List<MasterJaguar> stableLst = mpSelected.getJaguarStableList(); // This doesn't reflect any Jags added in this editing session.
        int adjustedStableSize = stablesize + newJagList.size(); 
        if(adjustedStableSize == 1){
            String msg = "Jaguar stable of " + mpSelected.getUniqueName() +  " has only one Jaguar.";
                    msg = msg + "\nSince all MasterPersons must have at least one Jaguar, it cannot be removed." ;
                    msg = msg + "\nIf your intention is to keep the Master Person and replace the selected Jaguar, first add the wanted one and then remove the unwanted one.";
                    msg = msg + "\nIf you don't want to keep the Master Person, use the Remove Master Person command";
            okDialog(msg);
            return;
        }
        MasterJaguar mjSelected = (MasterJaguar)this.cboJaguarList.getSelectedItem();
        selectedMasterJaguar = mjSelected;
        Entry entry = theConcours.getMasterJaguarFromEntryList(mjSelected.getUniqueDesc());
        if(entry != null){
            String msg = "Master Jaguar " + mjSelected.getUniqueDesc() +  " is an Entry in the Concours so cannot be removed.";
                   msg = msg + "\nIf you wish to remove this Master Jaguar you must remove the Entry and then remove the Master Jaguar";
            okDialog(msg);
            return;
        }
        if(newJagList.contains(mjSelected) ){
            int response = yesNoDialog("This Remove will reverse the recent Add of " + mjSelected.getUniqueDesc() + " Is that what you want?");
            if(response == JOptionPane.NO_OPTION) {
                // No means the user did not intend to do the Remove, so we do nothing at all
                return;
            } else{
                // Yes response. Means the user wants to reverse the recent Add.
                // But since it hasn't really been added yet all we have to do is remove it from the newJagList
                newJagList.remove(mjSelected);
                cboJaguarList.removeItem(mjSelected); // and from cboJaguarList
                okDialog( "The selected Master Jaguar " + mjSelected.getUniqueDesc() + " will be removed. ");
            }
        } else{
            removedMJ = mjSelected;
            masterJagsToBeRemovedJagList.add(mjSelected);
            cboJaguarList.removeItem(mjSelected);
            stablesize = stablesize - 1;
            okDialog( "The selected Master Jaguar " + mjSelected.getUniqueDesc() + " was  put on the to-be-Removed list. ");
        }
        
    }//GEN-LAST:event_btnRemoveJaguarActionPerformed

    private void btnEditJaguarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditJaguarActionPerformed
        Connection conn = theConcours.GetConnection();
        //EditMasterJaguar(java.awt.Frame parent, boolean modal, Connection aConn, Concours aConcours, MasterJaguar aMasterJaguar,  String aOwnerUniqueName)
        MasterJaguar selectedMj = (MasterJaguar)this.cboJaguarList.getSelectedItem();
        MasterPersonExt selectedMp = (MasterPersonExt) this.cboUniqueName.getSelectedItem();
        String strSelectedUniqueName = selectedMp.getUniqueName();
        EditMasterJaguar editMasterJaguarDialog = new EditMasterJaguar(new javax.swing.JFrame(), true, conn, theConcours, selectedMj, strSelectedUniqueName);
        editMasterJaguarDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Exiting AddMasterJaguar back to AdEditMasterPersonDialog");
            }
        });
        editMasterJaguarDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        editMasterJaguarDialog .setVisible(true);
    }//GEN-LAST:event_btnEditJaguarActionPerformed


    private boolean isInStable(String aUniqueDescription){
        boolean result = false;
        ComboBoxModel model = cboJaguarList.getModel();
        int size = model.getSize();
        if(size <= 0) {
            theConcours.GetLogger().info("nothing in stable.");
            result = false;
        } else{
            for(int i=0;i<model.getSize();i++) {
                MasterJaguar mj = (MasterJaguar) model.getElementAt(i);
                if(mj.getUniqueDesc().equals(aUniqueDescription)) result = true;
            }
        }
        
        return result;
    }
    
    private boolean masterJagListContains(List<MasterJaguar> aList, MasterJaguar aMasterJaguar){
        boolean result = false;
        for(MasterJaguar mj : aList){
            if(mj.getUniqueDesc().equals(aMasterJaguar.getUniqueDesc())){
                result = true;
                break;
            }
        }
        return result;
    }
    
    /*
 
    */
   
    private boolean masterJagListRemove(List<MasterJaguar> aList, MasterJaguar aMasterJaguar) {
        boolean result = false;
        Iterator<MasterJaguar> mjIt = aList.iterator();
        while (mjIt.hasNext()) {
            MasterJaguar mj = mjIt.next();
            if (mj.getUniqueDesc().equals(aMasterJaguar.getUniqueDesc())) {
                mjIt.remove();
                result = true;
             }
        }   
        return result;
    }   
    private void clearAllFields(){
    txtLastName.setText("");
    txtFirstName.setText("");
    txtMI.setText("");
    txtJCNA.setText("99999");
    txtClub.setText("unknown");
    txtJudgeStatus.setText("unknown");
    txtCertYear.setText("1955");
    txtStreetAddress.setText("unknown");
    txtCity.setText("unknown");
    txtState.setText("unknown");
    txtCountry .setText("unknown");
    txtPostalCode.setText("unknown");
    txtPhoneWork.setText("unknown");
    txtPhoneHome.setText("unknown");
    txtPhoneCell.setText("unknown");
    txtEmail.setText("unknown");
    this.cboJaguarList.setModel(new javax.swing.DefaultComboBoxModel(new MasterJaguar[] {  })); // empty
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
            java.util.logging.Logger.getLogger(EditMasterPersonDialog2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditMasterPersonDialog2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditMasterPersonDialog2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditMasterPersonDialog2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditMasterPersonDialog2 dialog = new EditMasterPersonDialog2(new javax.swing.JFrame(), true, aConnection, aConcours,  aSystemexitwhenclosed);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
                */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNewJaguar;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnEditJaguar;
    private javax.swing.JButton btnFinished;
    private javax.swing.JButton btnRemoveJaguar;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cboJaguarList;
    private javax.swing.JComboBox cboUniqueName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtCertYear;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtClub;
    private javax.swing.JTextField txtCountry;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtJCNA;
    private javax.swing.JTextField txtJudgeStatus;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtMI;
    private javax.swing.JTextField txtPhoneCell;
    private javax.swing.JTextField txtPhoneHome;
    private javax.swing.JTextField txtPhoneWork;
    private javax.swing.JTextField txtPostalCode;
    private javax.swing.JTextField txtState;
    private javax.swing.JTextField txtStreetAddress;
    // End of variables declaration//GEN-END:variables
}
