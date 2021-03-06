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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.JCNAClassRule;
//import us.efsowell.concours.lib.JCNAClassRules_2;
import us.efsowell.concours.lib.JCNAClasses;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
//import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.CopyJCNAClassRules;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonExt;

/**
 *
 * @author Ed Sowell
 */
public class EditMasterJaguar extends javax.swing.JDialog {
    MasterJaguar theMasterJaguar;
    Concours theConcours;
    Logger theLogger;
    Connection theConn;
    JCNAClasses theJCNAClasses;  
    MasterPersonExt  theSelectedMasterPerson;
    JCNAClassChooserGUI theJCNAClassChooser;
    
    /**
     * Constructor
     */
    public EditMasterJaguar(java.awt.Frame parent, boolean modal, Connection aConn, Concours aConcours, MasterJaguar aMasterJaguar,  String aOwnerUniqueName) {
        super(parent, modal);
        initComponents();
        theConcours = aConcours;
        theLogger = theConcours.GetLogger();
        theConn = aConn;
        theMasterJaguar = aMasterJaguar;
        txtOwner.setText(aOwnerUniqueName);
        txtYear.setText(aMasterJaguar.getYear().toString());
        txtUniqueDescription.setText(aMasterJaguar.getUniqueDesc());
        txtColor.setText(aMasterJaguar.getColor());
        txtChampClass.setText(aMasterJaguar.getJcnaclass_c());
        txtDrivenClass.setText(aMasterJaguar.getJcnaclass_d());
        txtUniqueDescription.setText(aMasterJaguar.getUniqueDesc());
        txtPlateVIN.setText(aMasterJaguar.getPlateVIN());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel21 = new javax.swing.JLabel();
        txtColor = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtPlateVIN = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnOk = new javax.swing.JButton();
        txtOwner = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtUniqueDescription = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        txtYear = new javax.swing.JTextField();
        txtChampClass = new javax.swing.JTextField();
        txtDrivenClass = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Master Jaguar");

        jLabel21.setText("Year");

        jLabel23.setText("Color");

        jLabel24.setText("Plate or VIN");

        btnOk.setText("OK");
        btnOk.setToolTipText("Click after choosing Championship AND Driven classes. Adds to the Owner's stable.");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        txtOwner.setEditable(false);

        jLabel1.setText("Owner unique name");

        txtUniqueDescription.setEditable(false);
        txtUniqueDescription.setEnabled(false);
        txtUniqueDescription.setFocusable(false);
        txtUniqueDescription.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUniqueDescriptionFocusGained(evt);
            }
        });

        jLabel6.setText("Driven Class");

        jLabel7.setText("Championship Class");

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        txtYear.setEditable(false);
        txtYear.setEnabled(false);
        txtYear.setFocusable(false);

        txtChampClass.setEditable(false);
        txtChampClass.setText("jTextField1");
        txtChampClass.setEnabled(false);
        txtChampClass.setFocusable(false);

        txtDrivenClass.setEditable(false);
        txtDrivenClass.setText("jTextField1");
        txtDrivenClass.setEnabled(false);
        txtDrivenClass.setFocusable(false);

        jLabel5.setText("Jaguar Unique Description");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(txtChampClass)
                            .addComponent(txtDrivenClass, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 32, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtOwner, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtUniqueDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPlateVIN, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(280, 280, 280)
                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancel)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel21)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOwner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUniqueDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtChampClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDrivenClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPlateVIN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancel))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    
    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        String color;
        String platevin;
        color = txtColor.getText();
        platevin = txtPlateVIN.getText();
        // Change in theMasterJaguar
        theMasterJaguar.setColor(color);
        theMasterJaguar.setPlateVIN(platevin);
        Entry entry = theConcours.getMasterJaguarFromEntryList(theMasterJaguar.getUniqueDesc());
        if(entry != null){
            String msg = "Master Jaguar " + theMasterJaguar.getUniqueDesc() +  " is an Entry in the Concours so it will also be changed.";
            //okDialog(msg);
            theLogger.info(msg);
            int maxLen = (color.length() > 10) ? 10 : color.length();
            String mapTo = color.substring(0, maxLen);
            // Change in theConcourse entry
            entry.SetColor(mapTo);
            entry.SetPlateVin(platevin);
        }
        // Change in the database Master Jaguar table
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        loadSQLiteConcoursDatabase.UpdateEditMasterJaguar(theConn,   theMasterJaguar,  color,  platevin );
        
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnOkActionPerformed

    
 
    private void txtUniqueDescriptionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUniqueDescriptionFocusGained
    }//GEN-LAST:event_txtUniqueDescriptionFocusGained

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    public MasterJaguar getMasterJaguar(){
        return theMasterJaguar;
    }
    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        //Set the Nimbus look and feel 
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
            java.util.logging.Logger.getLogger(EditMasterJaguar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditMasterJaguar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditMasterJaguar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditMasterJaguar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField txtChampClass;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextField txtDrivenClass;
    private javax.swing.JTextField txtOwner;
    private javax.swing.JTextField txtPlateVIN;
    private javax.swing.JTextField txtUniqueDescription;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables
}
