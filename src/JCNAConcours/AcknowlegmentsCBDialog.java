/*
 * Copyright (C) 2017 Ed Sowell
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
import static JCNAConcours.ConcoursGUI.getRbTok;
//import static JCNAConcours.ConcoursGUI.theConcours;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.ini4j.Wini;
import us.efsowell.concours.lib.Concours;

/**
 *
 * @author Ed Sowell
 */
public class AcknowlegmentsCBDialog extends javax.swing.JDialog {

    /**
     * Creates new form AboutCBDialog
     */
    public AcknowlegmentsCBDialog(java.awt.Frame parent, boolean modal, Concours aConcours) {
        super(parent, modal);
        initComponents();
        /*String strCurDir = Paths.get(".").toAbsolutePath().normalize().toString(); // Just to see the current working directory...
        String userDocDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString(); // tricky way of getting at user/ Documents directory
        String strIniFilePath= userDocDirectory + "\\" + "ConcoursBuilder.ini";
        System.out.println("AcknowledgmentsCBDialog ini file path: " +  strIniFilePath);
        //System.out.println("The ini file: " + strIniFileName + "\n");
        File flIni = new File(strIniFilePath);
        Wini ini = null;
           
        try {
            ini = new Wini(flIni);
        } catch (IOException ex) {
            aConcours.GetLogger().info("Faild to get ini file in AcknowlegmentsCBDialog");
            System.exit(-1);
        }
        aConcours.GetLogger().info("AcknowledgmentsCBDialog opened ini");
 
        String concoursBuilderDataPath = null; // This is where the concours database & other data for the run is
        //String AcknowledgementsDataPath = null; 
        if(strCurDir.contains("NetBeansProjects")){
            concoursBuilderDataPath = ini.get("InstallSettings", "DeveloperDataPath");
        } else{
            concoursBuilderDataPath = ini.get("InstallSettings", "UserDataPath");
        }
        aConcours.GetLogger().info("AcknowledgmentsCBDialog concoursBuilderDataPath " + concoursBuilderDataPath);
*/
        //String version = theConcours.GetCBVersion();
        String concoursBuilderDocsPath = aConcours.GetConcoursBuilderDocsPath();
        //this.txtVersion.setText(version);
        //this.txtBuild.setText(getRbTok("BUILD"));
        //String strCurDir = Paths.get(".").toAbsolutePath().normalize().toString(); // Just to see the current working directory...
        //String userDocDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString(); // tricky way of getting at user/ Documents directory
        //String strIniFilePath= concoursBuilderDocsPath + "\\" + "ConcoursBuilder.ini";

        //String strAboutText = "file not found";
        
        String strAknowledgments = "File not found";
        try {                                                          
            strAknowledgments = readFile(concoursBuilderDocsPath +  "\\AcknowledgmentsCB.txt");
            aConcours.GetLogger().info("AcknowledgmentsCBDialog strAknowledgments read from " + concoursBuilderDocsPath +  "\\AcknowledgmentsCB.txt");
            this.txtArea.setText(strAknowledgments);
        } catch (IOException ex) {
            okDialog("Failed to read " +strAknowledgments  +" file in AboutCBDialog");
            aConcours.GetLogger().log(Level.INFO, "Failed to read Aknowledgments file {0} in AcknowlegmentsCBDialog", strAknowledgments);
        }
    }
    
    String readFile(String file) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader (file));
    String         line = null;
    StringBuilder  stringBuilder = new StringBuilder();
    String         ls = System.getProperty("line.separator");

    try {
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    } finally {
        reader.close();
    }
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
        txtArea = new javax.swing.JTextArea();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Acknowledgments"));

        txtArea.setEditable(false);
        txtArea.setColumns(20);
        txtArea.setRows(5);
        jScrollPane1.setViewportView(txtArea);

        btnOk.setText("OK");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 714, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(401, 401, 401)
                        .addComponent(btnOk)))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        this.setVisible(false);
        this.dispose();
        return;
    }//GEN-LAST:event_btnOkActionPerformed

    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOk;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration//GEN-END:variables
}
