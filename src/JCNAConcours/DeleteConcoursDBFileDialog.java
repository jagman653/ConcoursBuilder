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

import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *                  NO LONGER USED. 
 * 
 *  The Delete operation is now incorporated into ConcoursGUI using ConcoursChooser class
 * @author Ed Sowell
 */
public class DeleteConcoursDBFileDialog extends javax.swing.JDialog {
   // JList<String> concoursFolders;
    String strConcoursFolderPath;
    String[] dbFiles; 
    String strSelectedConcours;    

    /**
     *      NOT FINISHED, NOT USED. UNNECESSARY
     * 
     * Creates new form DeleteConcoursDialog to Facilitate deletion of of a Concours database file. 
     * This is needed to restore from a backup file without simply renaming the backup file. The advantage is
     * the backup file is still available after the restore.
     */
    public DeleteConcoursDBFileDialog(java.awt.Frame parent, boolean modal, String aConcoursFolderPath) {
        super(parent, modal);
        initComponents();
        strConcoursFolderPath = aConcoursFolderPath; // this is the folder for all Concourses, e.g., My Concourses
        String [] concoursArray;
        concoursArray  = deleteConcoursFile(strConcoursFolderPath);
        /*
        List<String> lstConcours = new ArrayList<>();
        deleteConcoursFile(strConcoursFolderPath);
        
        File folder = new File(strConcoursFolderPath);
        File[] listOfFiles = folder.listFiles(); // All files in My Concourses
        //
        // Get the names of all  subdirectories of the Concours directory (e.g., My Concourses) with a database file of the same name.
        //
        for (int i = 0; i < listOfFiles.length; i++) {
            File dir = listOfFiles[i];
            dbFiles = null;
            File subFolder;
            if (dir.isDirectory()) {
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

        */
               

    }

    
    // Incomplete, but not sure this will be needed...  
    public static String [] deleteConcoursFile(String aFullPathToConcorsFolder){
        List<String> lstConcours = new ArrayList<>();
        String[] dbFiles = null; 

        File folder = new File(aFullPathToConcorsFolder);
        File[] listOfFiles = folder.listFiles(); // All files in My Concourses 
        //
        // Get the names of all  subdirectories of the Concours directory (e.g., My Concourses) with a database file of the same name.
        //
        for (int i = 0; i < listOfFiles.length; i++) {
            File dir = listOfFiles[i];
            dbFiles = null;
            File subFolder;
            if (dir.isDirectory()) {
                String strSubFolder = aFullPathToConcorsFolder + "\\" + dir.getName();
                subFolder = new File(strSubFolder); 
                    dbFiles = subFolder.list(new FilenameFilter() {
                    public boolean accept(File subFolder, String fileName) {
                        return fileName.endsWith(".db");
                    }
                });
                // collect the list of concours names
                for (String dbFile : dbFiles) {
                    String fileNameOnly = dbFile.substring(0, dbFile.lastIndexOf("."));
                    if(fileNameOnly.equals(dir.getName())){
                        lstConcours.add(fileNameOnly);
                    }
                }            
            } 
            
        } 
        return dbFiles;
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
        btnDelete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Delete concours");

        JListConcourses.setBorder(javax.swing.BorderFactory.createTitledBorder("Existing Concourses"));
        JListConcourses.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(JListConcourses);

        btnDelete.setText("Delete selected concours");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnCancel.setText("cancel");
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
                        .addComponent(btnDelete)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel)))
                .addContainerGap(90, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete)
                    .addComponent(btnCancel))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        strSelectedConcours = (String)JListConcourses.getSelectedValue();
        // Delete the db file, and if that leaves no remaining files in the concours directory, delete the directory too.
        int response = yesNoDialog("do you really want to delete " + strConcoursFolderPath + "\\" + strSelectedConcours + "?");
        if(response == JOptionPane.YES_OPTION) {
           File dir = new File(strConcoursFolderPath + "\\" + strSelectedConcours);
           boolean dirExist = dir.exists() && dir.isDirectory();
           if(dirExist) {
               File db = new File(dir + "\\" + strSelectedConcours + ".db");
               if(db.exists()) db.delete();
               File[] listOfFiles = dir.listFiles();
               if(listOfFiles.length == 0) dir.delete();
           }  
        }
        this.setVisible(false);
        this.dispose();
        return;
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
        return;
    }//GEN-LAST:event_btnCancelActionPerformed

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
            java.util.logging.Logger.getLogger(DeleteConcoursDBFileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DeleteConcoursDBFileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DeleteConcoursDBFileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DeleteConcoursDBFileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DeleteConcoursDBFileDialog dialog = new DeleteConcoursDBFileDialog(new javax.swing.JFrame(), true, "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList JListConcourses;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
