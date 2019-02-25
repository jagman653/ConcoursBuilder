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
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import us.efsowell.concours.lib.Concours;

/**
 *  The user is allowed to choose from a list of Backup files in a given Concours folder.
 * @author Ed Sowell
 */
public class ConcoursRestoreChooserDialog extends javax.swing.JDialog {
   // JList<String> concoursFolders;
    String strConcoursFolderPath;
    String[] dbFiles; 
    String strSelectedBackup; 
    String strSelectedBackupFolderFullPath;
    String strSelectedBackupFullPath;
    //File folderConcours;
    int buttonClicked;
    String button;
    String strSelectedBckupName;


    /**
     * Constructor
     */
    public ConcoursRestoreChooserDialog(java.awt.Frame parent, boolean modal, Concours aConcours, String aConcoursFolderPath) {
        super(parent, modal);
        initComponents();
        setTitle("Restore Concours");
        btnOperation.setText("Restore selected Concours");
        jScrollPane1.setToolTipText("Select a Backup file from the list and click Restore"); 

        strConcoursFolderPath = aConcoursFolderPath; // this is the path for the folder for the Concourse to be replaced by the selected backup file
        String [] backupArray;
        List<String> lstBackups = new ArrayList<>();
       
        //strSelectedBackupName = "";
        File folder = new File(strConcoursFolderPath);
        File[] listOfFiles = folder.listFiles(); // All files in strConcoursFolderPath
        //
        // Get the names of all  subdirectories of the Concours directory (e.g., My Concourses) with a database file of the same name.
        //
        for (int i = 0; i < listOfFiles.length; i++) {
            File f = listOfFiles[i];
            String fn = f.getName();
            if (f.isFile() && fn.endsWith(".db") && fn.contains("Backup")) {
                lstBackups.add(fn);
            } 
        }        

        backupArray = lstBackups.toArray(new String[lstBackups.size()] );
        JListConcourses.setModel(new javax.swing.AbstractListModel() {
            String[] strings = backupArray;
            @Override
            public int getSize() { return strings.length; }
            @Override
            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        JListConcourses = new javax.swing.JList();
        btnOperation = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Delete concours");
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });

        jScrollPane1.setToolTipText("Select a concours from the list and click Open or Delete selected concour");
        jScrollPane1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseMoved(evt);
            }
        });

        JListConcourses.setBorder(javax.swing.BorderFactory.createTitledBorder("Existing Concourses"));
        JListConcourses.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(JListConcourses);

        btnOperation.setText("Restore selected concours");
        btnOperation.setToolTipText("Click to open an existing concours");
        btnOperation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOperationActionPerformed(evt);
            }
        });

        btnCancel.setText("cancel");
        btnCancel.setToolTipText("Click to exit dialog without opening a concours");
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
                        .addGap(91, 91, 91)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(btnOperation)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel)))
                .addContainerGap(84, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOperation)
                    .addComponent(btnCancel))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOperationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOperationActionPerformed
        //String strOpenConcoursName;
        
        strSelectedBackup = (String)JListConcourses.getSelectedValue();
        strSelectedBackupFullPath = strConcoursFolderPath + "\\" + strSelectedBackup;
        boolean validSelection  = false;
        boolean isNull = strSelectedBackup == null;
        if(!isNull) validSelection = !strSelectedBackup.isEmpty();
        if(!validSelection){
            okDialog("You must select a Backup to restore");
            return;
        }
        button = "Operation";
        this.setVisible(false);
        return;
    }//GEN-LAST:event_btnOperationActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        buttonClicked = JFileChooser.CANCEL_OPTION;
        button = "Cancel";
        this.setVisible(false);
        return;
    }//GEN-LAST:event_btnCancelActionPerformed

    private void jScrollPane1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseMoved
     
    }//GEN-LAST:event_jScrollPane1MouseMoved

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseMoved
    
    public String getSelectedBackupFullPath(){
        return strSelectedBackupFullPath;
    }
            
    
    
    public int showOpenDialog(){
        this.setVisible(true);
        if(button.equalsIgnoreCase("Operation")){
            buttonClicked =  JFileChooser.APPROVE_OPTION; //user clicked the other button, which can be Open, Delete, or ??
        } else {
            buttonClicked = JFileChooser.CANCEL_OPTION; //user clicked the Cancel button
        }
        return buttonClicked;
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
            java.util.logging.Logger.getLogger(ConcoursRestoreChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConcoursRestoreChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConcoursRestoreChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConcoursRestoreChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConcoursRestoreChooserDialog dialog = new ConcoursRestoreChooserDialog(new javax.swing.JFrame(), true ,null,  "D:\\DocumentsD\\JOCBusiness\\Concours\\TutorialE5J5");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                int resp = dialog.showOpenDialog(); // will be either JFileChooser.APPROVE_OPTION or JFileChooser.CANCEL_OPTION
                if(resp == JFileChooser.APPROVE_OPTION){
                    okDialog("Restore selected Backup button clicked. Selected file is: "  + dialog.getSelectedBackupFullPath());
                } else {
                    okDialog("Cancel button clicked. Selected file is: "  + dialog.getSelectedBackupFullPath());
                }
            }
        });
    }
 
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList JListConcourses;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOperation;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}