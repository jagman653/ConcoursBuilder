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
package editJA;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.TimeslotAssignment;

/**
 *
 * @author Ed Sowell
 */
public class InterchangeTimeslotsDialog_2 extends javax.swing.JDialog {
    
    Concours theConcours;
    Map<Integer,String> timeslotIndexToTimeStringMap; 
    Map<String, Integer> timeStringToTimeslotIndexMap; 

    /**
     * Creates new form InterchangeTimeslotsDialog
     */
    public InterchangeTimeslotsDialog_2(java.awt.Dialog parent, boolean modal, Concours aConcours, Map<Integer, String> aTimeslotIndexToTimeStringMap,  Map<String, Integer> aTimeStringToTimeslotIndexMap) {
        super(parent, modal);
        initComponents();
        theConcours = aConcours;
        timeslotIndexToTimeStringMap = aTimeslotIndexToTimeStringMap;
        timeStringToTimeslotIndexMap = aTimeStringToTimeslotIndexMap;
        int numSlotsInUse = theConcours.getMaxTimeslotIndex() + 1;
        
        String[] tsTextAry = new String[numSlotsInUse]; 
        for(int i=0; i<numSlotsInUse; i++ ){
            tsTextAry[i] = aTimeslotIndexToTimeStringMap.get(i);
        }
        
        
        //cboTimeslot.setModel(new javax.swing.DefaultComboBoxModel(getTimeslots().toArray()));
       cboTs1.setModel(new javax.swing.DefaultComboBoxModel(tsTextAry));
       cboTs2.setModel(new javax.swing.DefaultComboBoxModel(tsTextAry));
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboTs1 = new javax.swing.JComboBox();
        cboTs2 = new javax.swing.JComboBox();
        btnInterchange = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Interchange timeslots");

        cboTs1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboTs2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnInterchange.setText("Interchange");
        btnInterchange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInterchangeActionPerformed(evt);
            }
        });

        jLabel1.setText("Timeslot 1");

        jLabel2.setText("Timeslot 2");

        btnCancel.setText("Cancel");
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
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(cboTs1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(cboTs2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnInterchange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel)
                .addGap(78, 78, 78))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTs1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTs2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(69, 69, 69)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInterchange)
                    .addComponent(btnCancel))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInterchangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInterchangeActionPerformed
        String theCurrentTimeslotString = (String)this.cboTs1.getSelectedItem();
        int theCurrentTimeslotIndex = timeStringToTimeslotIndexMap.get(theCurrentTimeslotString);

        String theNewTimeslotString = (String)this.cboTs2.getSelectedItem();
        int theNewTimeslotIndex = timeStringToTimeslotIndexMap.get(theNewTimeslotString);
        
        theConcours.switchTimeslots(JudgeAssignDialog.theConcours.GetJudgeAssignments(), theCurrentTimeslotIndex, theNewTimeslotIndex);
        String msg = "TimeslotAssignment " + theCurrentTimeslotString + " interchanged with " + theNewTimeslotString;
        okDialog(msg);
        theConcours.GetLogger().info(msg);
        theConcours.GetLogger().info("Returned from Timeslots interchange");
        JudgeAssignDialog.textarea.append(msg);
        JudgeAssignDialog.theConcours.GetLogger().info(msg);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnInterchangeActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed
    private static List<TimeslotAssignment> getTimeslots(){
       return (ArrayList<TimeslotAssignment>) JudgeAssignDialog.theConcours.GetTimeslotAssignments();
        
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
            java.util.logging.Logger.getLogger(InterchangeTimeslotsDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterchangeTimeslotsDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterchangeTimeslotsDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterchangeTimeslotsDialog_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
       /*
        Logger theLogger;
        theLogger = Logger.getLogger("InterchangeTimeslotsLogger");
        Concours theConcours = new Concours(theLogger);
        // Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InterchangeTimeslotsDialog dialog = new InterchangeTimeslotsDialog(new javax.swing.JFrame(), true, theConcours);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
        //</editor-fold>
       /*
        Logger theLogger;
        theLogger = Logger.getLogger("InterchangeTimeslotsLogger");
        Concours theConcours = new Concours(theLogger);
        // Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InterchangeTimeslotsDialog dialog = new InterchangeTimeslotsDialog(new javax.swing.JFrame(), true, theConcours);
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnInterchange;
    private javax.swing.JComboBox cboTs1;
    private javax.swing.JComboBox cboTs2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
