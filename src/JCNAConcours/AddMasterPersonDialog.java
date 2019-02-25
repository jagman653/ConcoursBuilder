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
import java.awt.Component;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonExt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ed Sowell
 */
public class AddMasterPersonDialog extends javax.swing.JDialog {    
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
        MasterJaguar newMj;
        ArrayList<MasterJaguar> newJagList ;
        Vector<Component> traversalOrder;
        FocusTraversalPolicyConcoursBuilder ftp;
        
        String curLastName;
        String curFirstName; 
        String curMI; 
        String curUniqueName;


        // Constructor
    public  AddMasterPersonDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours,  boolean aSystemexitwhenclosed) {
        super(parent, modal);
        systemexitwhenclosed = aSystemexitwhenclosed;
        theDBConnection =  aConnection;
        initComponents();
        curLastName = "";
        curFirstName = "";
        curMI = "";
        curUniqueName = "";
        //this.setTitle("Add Concours Entries"); // this is already set in the Designer
        this.txtLastName.setEnabled(true);
        this.txtLastName.setToolTipText("Required");
        this.txtLastName.setText(curLastName);

        this.txtFirstName.setEnabled(true);
        this.txtFirstName.setToolTipText("Required");
        this.txtFirstName.setText(curFirstName);

        this.txtMI.setEnabled(true);
        this.txtMI.setToolTipText("Optional");
        this.txtMI.setText(curMI);

        this.txtUniqueName.setEnabled(true);
        this.txtUniqueName.setToolTipText("Required");
        this.txtUniqueName.setText(curUniqueName);

        this.txtCertYear.setEnabled(false);
        this.txtCertYear.setText("1955");
        this.txtCity.setEnabled(false);
        this.txtClub.setEnabled(false);
        this.txtEmail.setEnabled(false);
        this.txtJCNA.setEnabled(false);
        this.txtJCNA.setText("99999");

        this.txtPhoneCell.setEnabled(false);
        this.txtPhoneHome.setEnabled(false);
        this.txtPhoneWork.setEnabled(false);
        this.txtPostalCode.setEnabled(false);
        this.txtStreetAddress.setEnabled(false);
        this.btnAddNewJaguar.setEnabled(false);
        this.txtCountry.setEnabled(false);
        this.cboJaguarList.setEnabled(false);
        this.txtState.setEnabled(false);
        this.txtJudgeStatus.setEnabled(false);
        this.btnAddMasterPerson.setEnabled(false); 
        this.btnFinished.setToolTipText("Click Finished after Add Master Person");
        this.btnAddMasterPerson.setToolTipText("Click to add the new Master Person to MasterList");
        traversalOrder = new Vector<>();
        traversalOrder.add(txtLastName);
        traversalOrder.add(txtFirstName);
        traversalOrder.add(txtMI);
        traversalOrder.add(btnGetUniqueName);
        traversalOrder.add(txtUniqueName);
        traversalOrder.add(txtJCNA);
        traversalOrder.add(txtClub);
        traversalOrder.add(txtCertYear);
        traversalOrder.add(txtStreetAddress);
        traversalOrder.add(txtCity);
        traversalOrder.add(txtState);
        traversalOrder.add(txtPostalCode);
        traversalOrder.add(txtCountry);
        traversalOrder.add(txtPhoneHome);
        traversalOrder.add(txtPhoneWork);
        traversalOrder.add(txtPhoneCell);
        traversalOrder.add(txtEmail);
        traversalOrder.add(cboJaguarList);
        traversalOrder.add(btnAddNewJaguar);
        traversalOrder.add(btnAddMasterPerson);
        traversalOrder.add(btnFinished);
        traversalOrder.add(btnCancel);

        ftp = new FocusTraversalPolicyConcoursBuilder(traversalOrder);
        this.setFocusTraversalPolicy(ftp);
        theConcours = aConcours;

        newJagList = new ArrayList<>();
        this.cboJaguarList.setModel(new javax.swing.DefaultComboBoxModel(new MasterJaguar[] {  })); // empty
        
        boolAddedNewJag = false; // This is intended to alert user to add the Master Person if finished is clicked before Add new person is clicked.
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
        btnGrpAddEdit = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtJCNA = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtStreetAddress = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtPostalCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtClub = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
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
        btnAddMasterPerson = new javax.swing.JButton();
        btnFinished = new javax.swing.JButton();
        cboJaguarList = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        btnAddNewJaguar = new javax.swing.JButton();
        txtLastName = new javax.swing.JTextField();
        txtJudgeStatus = new javax.swing.JTextField();
        txtState = new javax.swing.JTextField();
        txtCountry = new javax.swing.JTextField();
        btnRemoveJaguar = new javax.swing.JButton();
        txtFirstName = new javax.swing.JTextField();
        txtUniqueName = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        txtMI = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btnGetUniqueName = new javax.swing.JButton();

        jLabel12.setText("jLabel12");

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Master Person");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jLabel1.setText("First");

        jLabel2.setText("Last");

        txtJCNA.setText("unknown");
        txtJCNA.setToolTipText("JCNA number (Currently optional)");
        txtJCNA.setNextFocusableComponent(txtClub);
        txtJCNA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJCNAActionPerformed(evt);
            }
        });

        jLabel4.setText("Unique name");

        txtStreetAddress.setText("unknown");
        txtStreetAddress.setToolTipText("Currently optional");
        txtStreetAddress.setNextFocusableComponent(txtCity);

        jLabel5.setText("Street address");

        txtCity.setText("unknown");
        txtCity.setToolTipText("Currently optional");

        jLabel6.setText("City");

        jLabel7.setText("State");

        txtPostalCode.setText("unknown");
        txtPostalCode.setToolTipText("Currently optional");

        jLabel8.setText("Postal code");

        jLabel9.setText("Country");

        jLabel10.setText("JCNA #");

        txtClub.setText("unknown");
        txtClub.setToolTipText("Members Club (optional)");

        jLabel11.setText("Club");

        jLabel3.setText("Judge status");

        txtCertYear.setText("unknown");
        txtCertYear.setToolTipText("Currently optional");
        txtCertYear.setNextFocusableComponent(txtStreetAddress);

        jLabel14.setText("Year certified");

        txtPhoneHome.setText("unknown");
        txtPhoneHome.setToolTipText("Currently optional");
        txtPhoneHome.setNextFocusableComponent(txtPhoneWork);
        txtPhoneHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneHomeActionPerformed(evt);
            }
        });

        jLabel15.setText("Home phone");

        txtPhoneCell.setText("unknown");
        txtPhoneCell.setToolTipText("Optional");
        txtPhoneCell.setNextFocusableComponent(txtEmail);
        txtPhoneCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneCellActionPerformed(evt);
            }
        });

        txtPhoneWork.setText("unknown");
        txtPhoneWork.setToolTipText("Optional");
        txtPhoneWork.setNextFocusableComponent(txtPhoneCell);
        txtPhoneWork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneWorkActionPerformed(evt);
            }
        });

        jLabel16.setText("Work phone");

        jLabel17.setText("Cell phone");

        txtEmail.setText("unknown");
        txtEmail.setToolTipText("Currently optional");
        txtEmail.setNextFocusableComponent(cboJaguarList);

        jLabel18.setText("Email");

        btnAddMasterPerson.setText("Add Master Person");
        btnAddMasterPerson.setToolTipText("Click after completing required fields. Adds person to the Master Person list for the current concurs. Can be used repeatedly.");
        btnAddMasterPerson.setNextFocusableComponent(btnFinished);
        btnAddMasterPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMasterPersonActionPerformed(evt);
            }
        });

        btnFinished.setText("Finished Adding  Master Persons");
        btnFinished.setToolTipText("Click after Adding one or more Master Persons. Note: To make these additions available for future concourses you must use the Save Active Base Database command on the File menu");
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

        cboJaguarList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJaguarList.setNextFocusableComponent(btnAddNewJaguar);

        jLabel13.setText("Jaguar stable");

        btnAddNewJaguar.setText("Add new Jaguar");
        btnAddNewJaguar.setToolTipText("Click to get dialog for adding a Jaguar to the persons stable");
        btnAddNewJaguar.setNextFocusableComponent(btnAddMasterPerson);
        btnAddNewJaguar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewJaguarActionPerformed(evt);
            }
        });

        txtLastName.setToolTipText("Enter JCNA members' last name");
        txtLastName.setFocusCycleRoot(true);
        txtLastName.setNextFocusableComponent(txtFirstName);
        txtLastName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLastNameFocusGained(evt);
            }
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

        txtJudgeStatus.setText("unknown");
        txtJudgeStatus.setToolTipText("Currently optional");

        txtState.setText("unknown");
        txtState.setToolTipText("Currently optional");

        txtCountry.setText("unknown");
        txtCountry.setToolTipText("Currently optional");

        btnRemoveJaguar.setText("Remove Jaguar");
        btnRemoveJaguar.setToolTipText("Click to remove a Jaguar from persons stable.");
        btnRemoveJaguar.setEnabled(false);
        btnRemoveJaguar.setNextFocusableComponent(btnAddMasterPerson);

        txtFirstName.setToolTipText("Enter JCNA members' first name");
        txtFirstName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFirstNameFocusGained(evt);
            }
        });
        txtFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFirstNameActionPerformed(evt);
            }
        });

        txtUniqueName.setToolTipText("Generated automatically from first, last, and middle initial");
        txtUniqueName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUniqueNameFocusGained(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setToolTipText("Click this to leave the dialog without adding any Master Persons.");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        txtMI.setToolTipText("Optional middle initial. Use for very common last & names");
        txtMI.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMIFocusGained(evt);
            }
        });
        txtMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMIActionPerformed(evt);
            }
        });

        jLabel19.setText("MI");

        btnGetUniqueName.setText("Get unique name");
        btnGetUniqueName.setToolTipText("Click after entering first, last, and middle initial. ");
        btnGetUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetUniqueNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel2)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3)
                            .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel16)
                                    .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(70, 70, 70)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel13)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(cboJaguarList, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btnAddNewJaguar))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(34, 34, 34)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel6))))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(18, 18, 18)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                .addComponent(jLabel7)
                                                .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(57, 57, 57)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtPostalCode))
                                            .addGap(39, 39, 39)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel9)
                                                .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(3, 3, 3)
                                            .addComponent(btnRemoveJaguar))))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnAddMasterPerson)
                                    .addGap(55, 55, 55)
                                    .addComponent(btnFinished)
                                    .addGap(56, 56, 56)
                                    .addComponent(btnCancel))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtFirstName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addGap(39, 39, 39)))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(txtMI, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel19))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnGetUniqueName)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(txtUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4))
                                    .addGap(92, 92, 92))))
                        .addGap(248, 277, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel19)
                    .addComponent(jLabel4)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel3)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGetUniqueName)
                        .addComponent(txtUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtMI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboJaguarList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddNewJaguar)
                    .addComponent(btnRemoveJaguar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddMasterPerson)
                    .addComponent(btnFinished)
                    .addComponent(btnCancel))
                .addGap(33, 33, 33))
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
        if(boolAddedNewJag){
            okDialog("You have not added the new Master Person since a new Jaguar was added to the stable");
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
    private String GenerateUniqueName(String aLastName, String aFirstName, String aMI){
       this.cboJaguarList.removeAllItems();
       String un;
       int mfne = theConcours.GetMaxFirstNameExtension();
       int lenFn = aFirstName.length();
       if(lenFn < mfne){
           un = aLastName + aFirstName.substring(0, lenFn);
       } else {
           un = aLastName + aFirstName.substring(0, mfne);
       }
       un = un + aMI;
       return un;
    }
    
    
private void clearAllFields(){
    txtLastName.setText("");
    txtFirstName.setText("");
    txtMI.setText("");
    txtUniqueName.setText("");
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
    
    
    private void btnAddMasterPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMasterPersonActionPerformed
        Long mpid; 
        
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        theConcours.GetLogger().info("Adding MasterPerson");
        String strFirstName = txtFirstName.getText();
        String strUniqueName = txtUniqueName.getText();
        String strMI = txtMI.getText();
        // Check for required field values
        ComboBoxModel model = cboJaguarList.getModel();
        int size = model.getSize();
        if(size <= 0) {
            okDialog("A Master Person must have at least one Jaguar.");
            return;
        }
      /* if(strFirstName.equals(null) || strFirstName.equals("lastname") || strFirstName.equals("")){
            okDialog("Provide First Name.");
            ftp.getDefaultComponent(rootPane) ;
            return;
       } 
       if(strLastName.equals(null) || strLastName.equals("lastname") || strLastName.equals("")){
            okDialog("Provide Last Name.");
            ftp.getDefaultComponent(rootPane) ;
            return;
       } 
        
        */
       //
       // Load cboJaguarList into jagArray[]
        MasterJaguar [] jagArray = new MasterJaguar[size];
        for(int i=0;i<model.getSize();i++) {
           jagArray[i] = (MasterJaguar) model.getElementAt(i);
        }
       
            // We have no master person ID at this point since it hasn't been put into the database!
            // Will have to  set it later...
            MasterPersonExt newMasterPerson = new MasterPersonExt(0L, Integer.parseInt(txtJCNA.getText()), txtClub.getText(), txtLastName.getText(), strFirstName, strMI,
                    strUniqueName, txtJudgeStatus.getText(), Integer.parseInt(txtCertYear.getText()), txtStreetAddress.getText(),
                    txtCity.getText(), txtState.getText(), txtCountry.getText(), txtPostalCode.getText(),
                    txtPhoneWork.getText(), txtPhoneHome.getText(), txtPhoneCell.getText(), txtEmail.getText(), jagArray);
                   // Add Master Person to MasterPersonnel in memory
            theConcours.GetMasterPersonnelObject().AddMasterPerson(newMasterPerson); // sans mpid in memory... probably not needed!
                                                                                     // But also the new master jaguar is without a mpid in database, which is needed
            // Add Master Person to MasterPersonnel and MsterJAguar DB tables.  mpid is returned so we can patch the new master person in memory
            mpid = loadSQLiteConcoursDatabase.UpdateAddMasterPerson(theDBConnection, newMasterPerson); 
            
            if(mpid <= 0){
                theConcours.GetLogger().info("Bad MasterPerson ID in AddMasterPersonDialog btnSaveActionPerformed");
                //newMasterPerson.setMasterPersonID(mpid);
                okDialog("New Master Person " + strUniqueName + " not added due to bad Master Person ID in btnAddMasterPersonActionPerformed()");
            } else{
                newMasterPerson.setMasterPersonID(mpid);
                // Also need to set mpid in the Jags in MasterJaguar
                ArrayList<MasterJaguar> mjl = newMasterPerson.getJaguarStableList();
                for(MasterJaguar mj : mjl){
                    mj.setMp_id(mpid);
                    Long mjid = loadSQLiteConcoursDatabase.getMasterJaguarID(theDBConnection, mj.getUniqueDesc());
                    mj.setMj_id(mjid); 
                }
                theConcours.GetConcoursMasterJaguar().add(newMj);  // added 10/4/2018
                okDialog("New Master Person " + strUniqueName + " added");
                clearAllFields();
                this.cboJaguarList.removeAllItems();
                
                boolAddedNewJag = false; // so it will be correct for adding another MasterPerson
            }
       
    }//GEN-LAST:event_btnAddMasterPersonActionPerformed

    private void txtLastNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtLastNamePropertyChange
    }//GEN-LAST:event_txtLastNamePropertyChange

    private void txtLastNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLastNameFocusLost
        /*
        String lastname = txtLastName.getText();
        ArrayList<String> firstnames = new ArrayList<String>();
        if("lastname".equals(lastname)){
            JOptionPane.showMessageDialog(null, "You must enter Last name first. Ignor if Cancelling the dialog");
        } else{
            firstnames = theConcours.GetMasterPersonnelObject().GetMasterPersonnelFirstNames(lastname);
            PopulateCboFirstName(firstnames);
            txtUniqueName.setEnabled(true);
        }
        */
    }//GEN-LAST:event_txtLastNameFocusLost

    private void btnAddNewJaguarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewJaguarActionPerformed
        Connection conn = theConcours.GetConnection();
        ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
        String strOwnerUniqueName = txtUniqueName.getText();
        if(strOwnerUniqueName == null || strOwnerUniqueName.isEmpty()){
            okDialog("Unique name empty.\nYou must provide Last name, First name so a proper Unique name can be generated");
            theConcours.GetLogger().info("Unique name empty.\nYou must provide Last name, First name so a proper Unique name can be generated");
            return;
        }
        theConcours.GetLogger().info("Adding Master Jaguar for " + strOwnerUniqueName );
        if(boolNewMasterPerson ){
            if(newJagList.isEmpty()){
                // a new master person will have 4 place-holder Items in the cboJaguarList
                // These need to be removed for the first added jag
                this.cboJaguarList.removeAllItems();
            }
        } 
        AddMasterJaguar_2 addMasterJaguarDialog = new AddMasterJaguar_2(new javax.swing.JFrame(), true, conn, theConcours,  JCNAClassesList, strOwnerUniqueName);
        //AddMasterJaguar addMasterJaguarDialog = new AddMasterJaguar(new javax.swing.JFrame(), true, conn, theConcours,  JCNAClassesList, strOwnerUniqueName);
        addMasterJaguarDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Exiting AddMasterJaguar_2 back to AddMasterPersonDialog");
            }
        });        
        addMasterJaguarDialog .setDefaultCloseOperation(HIDE_ON_CLOSE);
        addMasterJaguarDialog .setVisible(true);
        //addMasterJaguarDialog.setVisible(rootPaneCheckingEnabled);
        newMj = addMasterJaguarDialog.getNewMasterJaguar(); // retrives  result from AddMasterJaguar_2() NOTE: masterperson_id & masterjaguar_id null at this point
        if(newMj == null){
            theConcours.GetLogger().info("User clicked Finished without Adding a Master Jaguar for " + strOwnerUniqueName);
            okDialog("User clicked Finished without Adding a Master Jaguar for " + strOwnerUniqueName);
            return;
        }
        String newMjUniqueDescription = newMj.getUniqueDesc();
        // Check to be sure it hasn't already been added to stable
        if(isInStable(newMjUniqueDescription)){
            //boolAddedNewJag = true;
            okDialog("Jaguar with Unique Description " + newMjUniqueDescription + " is already in stable of " + strOwnerUniqueName + ". Will not be added.");
            theConcours.GetLogger().info("Jaguar " + newMjUniqueDescription + " is already in stable. Will not be added for MasterPerson " + strOwnerUniqueName + ". boolAddedNewJag is: " + boolAddedNewJag);
        } else{
            cboJaguarList.addItem(newMj);
            newJagList.add(newMj);
            boolAddedNewJag = true;
            okDialog("Jaguar " + newMj.getUniqueDesc() + " will be added for MasterPerson " + strOwnerUniqueName);
            theConcours.GetLogger().info("Jaguar " + newMj.getUniqueDesc() + " added for MasterPerson " + strOwnerUniqueName + ". boolAddedNewJag is: " + boolAddedNewJag);
        }
    }//GEN-LAST:event_btnAddNewJaguarActionPerformed

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
    
    
    private void txtLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLastNameActionPerformed
    }//GEN-LAST:event_txtLastNameActionPerformed

    private void btnFinishedMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFinishedMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFinishedMouseDragged

    private void txtUniqueNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUniqueNameFocusGained
        //
        // Could not get this to work... strange behavior
        //
        /* String fn = txtFirstName.getText().trim();
        String ln = txtLastName.getText().trim();
        String mi  = txtMI.getText().trim();
        String un;
        if(fn.equals(null) || fn.equals("") ){
            txtFirstName.requestFocusInWindow();
            okDialog("Provide First Name");
            return;
        }
        if(ln.equals(null) || ln.equals("") ){
            txtLastName.requestFocusInWindow();
            okDialog("Provide last Name");
            //txtLastName.setText("lastname");
            return;
        }
        // We have Last & First, maybe MI
        un = GenerateUniqueName(ln, fn, mi);
       //
       // Check for conflict with existing MasterPerson UniqueName
       //
        ArrayList<String> uniquenames = new ArrayList<String>();
        uniquenames = theConcours.GetMasterPersonnelObject().GetMasterPersonnelUniqueNames(un);
        if(uniquenames.size() > 0){
            String msg =  "UniqueName " + un + " is already in Concours Master List.";
            this.txtMI.requestFocus();
            msg = msg + "\nClick OK and add a middle initial";
            okDialog(msg);
            un = "";
        }        
        txtUniqueName.setText(un);
        this.txtCertYear.setEnabled(true);
        this.txtCertYear.setText("1955");
        this.txtCity.setEnabled(true);
        this.txtClub.setEnabled(true);
        this.txtEmail.setEnabled(true);
        this.txtJCNA.setEnabled(true);
        this.txtJCNA.setText("99999");

        this.txtPhoneCell.setEnabled(true);
        this.txtPhoneHome.setEnabled(true);
        this.txtPhoneWork.setEnabled(true);
        this.txtPostalCode.setEnabled(true);
        this.txtStreetAddress.setEnabled(true);
        this.btnAddNewJaguar.setEnabled(true);
        this.txtCountry.setEnabled(true);
        this.cboJaguarList.setEnabled(true);
        this.txtState.setEnabled(true);
        this.txtJudgeStatus.setEnabled(true);
        this.btnAddMasterPerson.setEnabled(true);
        
  */
    }//GEN-LAST:event_txtUniqueNameFocusGained

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFirstNameActionPerformed

    private void txtMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMIActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMIActionPerformed

    private void txtLastNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLastNameFocusGained
        this.txtLastName.selectAll();
    }//GEN-LAST:event_txtLastNameFocusGained

    private void txtFirstNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFirstNameFocusGained
        this.txtFirstName.selectAll();
    }//GEN-LAST:event_txtFirstNameFocusGained

    private void txtMIFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMIFocusGained
        this.txtCertYear.setEnabled(true);
        this.txtCertYear.setText("1955");
        this.txtCity.setEnabled(true);
        this.txtClub.setEnabled(true);
        this.txtEmail.setEnabled(true);
        this.txtJCNA.setEnabled(true);
        this.txtJCNA.setText("99999");

        this.txtPhoneCell.setEnabled(true);
        this.txtPhoneHome.setEnabled(true);
        this.txtPhoneWork.setEnabled(true);
        this.txtPostalCode.setEnabled(true);
        this.txtStreetAddress.setEnabled(true);
        this.btnAddNewJaguar.setEnabled(true);
        this.txtCountry.setEnabled(true);
        this.cboJaguarList.setEnabled(true);
        this.txtState.setEnabled(true);
        this.txtJudgeStatus.setEnabled(true);
    }//GEN-LAST:event_txtMIFocusGained

    private void btnGetUniqueNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetUniqueNameActionPerformed
        String fn = txtFirstName.getText().trim();
        String ln = txtLastName.getText().trim();
        String mi  = txtMI.getText().trim();
        String un;
        if(fn.equals(null) || fn.equals("") ){
            txtFirstName.requestFocus();
            okDialog("Provide First Name");
            return;
        }
        if(ln.equals(null) || ln.equals("") ){
            txtLastName.requestFocus();
            okDialog("Provide last Name");
            //txtLastName.setText("lastname");
            return;
        }
        // We have Last & First, maybe MI
        un = GenerateUniqueName(ln, fn, mi);
       //
       // Check for conflict with existing MasterPerson UniqueName
       //
        ArrayList<String> uniquenames = new ArrayList<String>();
        uniquenames = theConcours.GetMasterPersonnelObject().GetMasterPersonnelUniqueNames(un);
        if(uniquenames.size() > 0){
            String msg =  "UniqueName " + un + " is already in Concours Master List.";
            this.txtMI.requestFocus();
            msg = msg + "\nClick OK and add a middle initial";
            okDialog(msg);
            un = "";
        }        
        txtUniqueName.setText(un);
        this.txtCertYear.setEnabled(true);
        this.txtCertYear.setText("1955");
        this.txtCity.setEnabled(true);
        this.txtClub.setEnabled(true);
        this.txtEmail.setEnabled(true);
        this.txtJCNA.setEnabled(true);
        this.txtJCNA.setText("99999");

        this.txtPhoneCell.setEnabled(true);
        this.txtPhoneHome.setEnabled(true);
        this.txtPhoneWork.setEnabled(true);
        this.txtPostalCode.setEnabled(true);
        this.txtStreetAddress.setEnabled(true);
        this.btnAddNewJaguar.setEnabled(true);
        this.txtCountry.setEnabled(true);
        this.cboJaguarList.setEnabled(true);
        this.txtState.setEnabled(true);
        this.txtJudgeStatus.setEnabled(true);
        this.btnAddMasterPerson.setEnabled(true);
        
    }//GEN-LAST:event_btnGetUniqueNameActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusGained

    private void txtJCNAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJCNAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJCNAActionPerformed

   public static int okCancelDialog(String theMessage) {
    int result = JOptionPane.showConfirmDialog((Component) null, theMessage,
        "alert", JOptionPane.OK_CANCEL_OPTION);
    return result;
  }

  public static int yesNoCancelDialog(String theMessage) {
    int result = JOptionPane.showConfirmDialog((Component) null, theMessage, "alert", JOptionPane.YES_NO_CANCEL_OPTION);
    return result;
  }

   
  /* public static int okDialog(String theMessage) {
    int result = JOptionPane.showConfirmDialog((Component) null, theMessage,
        "alert", JOptionPane.OK_OPTION);
    return result;
  }
   */
    
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
            java.util.logging.Logger.getLogger(AddMasterPersonDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", logger);
            theConcours.LoadMasterPersonnelDB(conn, logger);
            theConcours.LoadConcoursPersonnelDB(conn, logger);
            theConcours.LoadMasterJaguarDB(conn, logger);
            theConcours.LoadEntriesDB(conn, logger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
            theConcours.LoadConcoursClassesDB(conn, theConcours, logger);
            theConcours.LoadJudgesDB(conn, logger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
            theConcours.LoadOwnersDB(conn, logger);
                
           // MasterListRepository masterList = new MasterListRepository(conn);
            boolean loadRepositoryFromMemory = true;
            MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);
            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
            ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
            JCNAClass [] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()] );
            AddMasterPersonDialog theDialog = new AddMasterPersonDialog(new javax.swing.JFrame(), true, conn, theConcours,  true);
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
                Logger.getLogger(AddMasterPersonDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMasterPerson;
    private javax.swing.JButton btnAddNewJaguar;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnFinished;
    private javax.swing.JButton btnGetUniqueName;
    private javax.swing.ButtonGroup btnGrpAddEdit;
    private javax.swing.JButton btnRemoveJaguar;
    private javax.swing.JComboBox cboJaguarList;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
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
    private javax.swing.JTextField txtUniqueName;
    // End of variables declaration//GEN-END:variables


}
