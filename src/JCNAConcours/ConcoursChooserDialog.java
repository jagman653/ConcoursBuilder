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
 *  This is used in place of FileChooser. For Opening or deleting a Concours there's no real need
 *  to browse because all concourses are in the My Concourses folder. The user is given a list of My Concourses subfolders
 * that have a concours.db file with the same name as the subfolder. Like FileChooser, there is a 
 *  
 * @author Ed Sowell
 */
public class ConcoursChooserDialog extends javax.swing.JDialog {
   // JList<String> concoursFolders;
    String strConcoursFolderPath;
    String[] dbFiles; 
    String strSelectedConcours; 
    String strSelectedConcoursFolderFullPath;
    //File folderConcours;
    int buttonClicked;
    String button;
    String strSelectedConcoursName;
    String operation;


    /**
     * Creates new form ConcoursChooser
     */
    public ConcoursChooserDialog(java.awt.Frame parent, boolean modal, Concours aConcours, String aOperation, String aConcoursFolderPath) {
        // aOperation is "Open" or "Delete"
        super(parent, modal);
        initComponents();
        setTitle(aOperation + " Concours");
        this.btnOperation.setText(aOperation + " selected Concours");
        jScrollPane1.setToolTipText("Select a concours from the list and click " + aOperation + " selected concours");  // doesn't work

        strConcoursFolderPath = aConcoursFolderPath; // this is the folder for all Concourses, e.g., My Concourses
        String [] concoursArray;
        List<String> lstConcours = new ArrayList<>();
        operation = aOperation;
       
        //strSelectedConcours = (String)JListConcourses.getSelectedValue();
        if(operation == "Open"){
            strSelectedConcoursName = "";
            btnOperation.setToolTipText("Click to Open the selected concours");
        } else if(operation == "Restore"){
           strSelectedConcoursName = "";
           btnOperation.setToolTipText("Click to Restore the selected concours");
        } else{
            // Delete operation
            btnOperation.setToolTipText("Click to Delete the selected concours");
             // Avoid presenting an Open concours as a target for Delete
            if(aConcours == null){
                    //Should not happen because a Concours is constructed in the ConcoursGUI construction process.
                    // Name will be "noConcoursOpen.db" until a Concours is Opened or created with New 
                    okDialog("Null Concours in ConcoursChooserDialog");
                    return;
            } else {
                 strSelectedConcoursName = aConcours.GetConcoursName();
            }
            int pos = strSelectedConcoursName.indexOf(".db");
            strSelectedConcoursName = strSelectedConcoursName.substring(0, pos);
            
       }
        File folder = new File(strConcoursFolderPath);
        File[] listOfFiles = folder.listFiles(); // All files in My Concourses
        //
        // Get the names of all  subdirectories of the Concours directory (e.g., My Concourses) with a database file of the same name.
        //
        
        for (int i = 0; i < listOfFiles.length; i++) {
            File dir = listOfFiles[i];
            dbFiles = null;
            File subFolder;
            // Note: when Operation is Open strSelectedConcoursName == "" so strSelectedConcoursName.equals(dir.getName() == false
            //       Therefore none of the .db files will get skipped
            if (dir.isDirectory() && !strSelectedConcoursName.equals(dir.getName())) {
                String strSubFolder = strConcoursFolderPath + "\\" + dir.getName();
                subFolder = new File(strSubFolder); 
                dbFiles = subFolder.list(new FilenameFilter() {
                    public boolean accept(File subFolder, String fileName) {
                        return fileName.endsWith(".db");
                    }
                });
                    // collect the list of concours names
                for (int k = 0; k < dbFiles.length; k++) {
                    String fileNameOnly = dbFiles[k].substring(0, dbFiles[k].lastIndexOf("."));
                    if(fileNameOnly.equals(dir.getName())){
                        lstConcours.add(fileNameOnly);
                    }
                }            
            } 
            
        }   
        concoursArray = lstConcours.toArray(new String[lstConcours.size()] );
        JListConcourses.setModel(new javax.swing.AbstractListModel() {
            String[] strings = concoursArray;
            @Override
            public int getSize() { return strings.length; }
            @Override
            public Object getElementAt(int i) { return strings[i]; }
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

        btnOperation.setText("Open selected concours");
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
                .addContainerGap(96, Short.MAX_VALUE))
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
        
        strSelectedConcours = (String)JListConcourses.getSelectedValue();
        strSelectedConcoursFolderFullPath = strConcoursFolderPath + "\\" + strSelectedConcours;
        boolean validSelection  = false;
        boolean isNull = strSelectedConcours == null;
        if(!isNull) validSelection = !strSelectedConcours.isEmpty();
        if(!validSelection){
            okDialog("You must select a Concours to " + operation);
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
    
    public String getSelectedFolderName(){
        return strSelectedConcours;
    }

    public String getSelectedFolderFullPath(){
        return strSelectedConcoursFolderFullPath;
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
            java.util.logging.Logger.getLogger(ConcoursChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConcoursChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConcoursChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConcoursChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConcoursChooserDialog dialog = new ConcoursChooserDialog(new javax.swing.JFrame(), true ,null, "Open", "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                int resp = dialog.showOpenDialog(); // will be either JFileChooser.APPROVE_OPTION or JFileChooser.CANCEL_OPTION
                if(resp == JFileChooser.APPROVE_OPTION){
                    okDialog("Open or Delete selected Concours button clicked. Selected file is: "  + dialog.getSelectedFolderFullPath());
                } else {
                    okDialog("Cancel button clicked. Selected file is: "  + dialog.getSelectedFolderFullPath());
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
