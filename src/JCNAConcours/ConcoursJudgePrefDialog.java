/*
 * Copyright (C) 2017 Owner
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.Judge;

/**
 *
 * @author Owner
 */
public class ConcoursJudgePrefDialog extends javax.swing.JDialog {
    Concours theConcours;
    JCNAClassesGroups theJCNAClassGroups;
    ArrayList<String> rejectList;

    /**
     * Creates new form JudgePrefDialog
     */
    public ConcoursJudgePrefDialog(java.awt.Frame parent, boolean modal, Concours aConcours, JCNAClassesGroups aJCNAClassGroups, JCNAClass[] aClassMasterListArray) {
        super(parent, modal);
        initComponents();
        PopulatePrefPanelConcoursJudgePrefDialog(aConcours, aJCNAClassGroups, spJCNAClasses, aClassMasterListArray);
        theConcours = aConcours;
        theJCNAClassGroups = aJCNAClassGroups;
        
    }
    //
    // PopulatePrefPanelConcoursJudgePrefDialog constructs the Judge preference panel.  
    //
private void PopulatePrefPanelConcoursJudgePrefDialog(Concours aConcours, JCNAClassesGroups aJCNAClassGroups, JScrollPane aClassesScrollPane, JCNAClass[] aClassMasterListArray)
    {
        ArrayList<JCNAClass> jcnalasses = new ArrayList<>();
        String judgeassigngroupname;
        String classname;
        JCNAClassesGroup cg;
        /* 
         * iterate the list of JCNA Classes to set up the checkbox lists for Class judge assignment Groups and the 
         * for all classes. 
         *  
         * All of this is set into 2 panels in the dialog
         */
        
         
        jcnalasses = aConcours.GetJCNAClasses().GetJCNAClasses();
        for (JCNAClass c : jcnalasses) {
            classname = c.getName();
            judgeassigngroupname = c.getJudgeAssignGroup();
            cg = aJCNAClassGroups.GetJCNAClassesGroup(judgeassigngroupname);
            if (cg == null) {
                cg = new JCNAClassesGroup(judgeassigngroupname);
                aJCNAClassGroups.AddClassesGroup(cg);
            }
            cg.AddClassName(classname);
        }
        // now populate the Class group scroll panel
        JCheckBox cb;
        List<JCheckBox> cbList = new ArrayList<>();
       // pnlJCNAClassGroups.removeAll();  // clearing the checkboxes inserted with the GUI designer
        for (JCNAClassesGroup jcnaclassgroup : aJCNAClassGroups.GetClassesGroupList()) {
            cb = new JCheckBox(new JCNAClassesGroup.JCNAClassesGroupAction(jcnaclassgroup));
            cb.setName(jcnaclassgroup.GetGroupName());
            cbList.add(cb);
            cb.addItemListener(new ItemListener() {
                /*
                 *  The following itemStateChanged listener transfers the Group selections to the list of all classes.
                 */
                @Override
                public void itemStateChanged(ItemEvent e) {
                    //String cbText;
                    String classname;
                    String cbName;
                    String[] groupclassnames;
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        JCheckBox cbe = (JCheckBox) e.getItem();
                        String groupname = cbe.getName();
                        groupclassnames = aJCNAClassGroups.GetJCNAClassesGroup(groupname).GetClassNames();
                        for (String groupclassname : groupclassnames) {
                            classname = groupclassname;
                            JPanel p = pnlJCNAClasses;
                            int count = p.getComponentCount();
                            JCheckBox cb;
                            // iterate over the JCNA Classes check boxes in pnlJCNAClasses
                            // and select those  that have been selected in group i
                           for (int j = 0; j < count; j++) {
                                //p.getClass();
                                cb = (JCheckBox) p.getComponent(j);
                                cbName = cb.getName();
                                if (cbName.equals(classname)) {
                                    //cbText = cb.getText();
                                    cb.setSelected(true);
                                }
                            }
                            
                        }
                    } else {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            JCheckBox cbe = (JCheckBox) e.getItem();
                            String groupname = cbe.getName();
                            groupclassnames = aJCNAClassGroups.GetJCNAClassesGroup(groupname).GetClassNames();
                            for (String groupclassname : groupclassnames) {
                                classname = groupclassname;
                                JPanel p = pnlJCNAClasses;
                                int count = p.getComponentCount();
                                JCheckBox cb;
                            // iterate over the JCNA Classes check boxes in pnlJCNAClasses
                                // and select those  that have been selected in group i
                                for (int j = 0; j < count; j++) {
                                   cb = (JCheckBox) p.getComponent(j);
                                   if (cb.getName().equals(classname)) {
                                        //cbText = cb.getText();
                                        cb.setSelected(false);
                                    }
                                }
                            }
                        }
                    }
                }
            });
            pnlJCNAClassGroups.add(cb);
        }
        
        // Set up Classes panel

        //pnlJCNAClasses = new javax.swing.JPanel();
        //pnlJCNAClasses.setBorder(javax.swing.BorderFactory.createTitledBorder("Select JCNA classes individually"));
        //pnlJCNAClasses.setMinimumSize(new Dimension(1000, 300));
        //
        //  Set up scrolling for the Class panel
        //
       // aClassesScrollPane = new JScrollPane(pnlJCNAClasses, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       // aClassesScrollPane.setMinimumSize(new Dimension(400, 300));
        //scrollPaneClasses.setPreferredSize(new Dimension(800, 400));        
       
       // javax.swing.BoxLayout pnlJCNAClassesLayout;
       // pnlJCNAClassesLayout = new javax.swing.BoxLayout(pnlJCNAClasses, BoxLayout.PAGE_AXIS);
       // pnlJCNAClasses.setLayout(pnlJCNAClassesLayout);
       // pnlJCNAClasses.revalidate();
        
        
       // pnlJCNAClassGroups.setLayout(new BoxLayout(pnlJCNAClassGroups, BoxLayout.PAGE_AXIS)); 
        //pnlJCNAClassGroups.setVisible(true);
        
//       pnlPreferenceContent.add(pnlJCNAClassGroups, java.awt.BorderLayout.WEST);

      //  pnlJCNAClasses.removeAll(); // clearing the checkboxes inserted with the GUI designer
        for (JCNAClass c : aClassMasterListArray) {
            classname = c.getName();
            cb = new JCheckBox(new JCNAClass.JCNAClassAction(c));
            cb.setName(classname);
            spJCNAClasses.add(cb);
        }
       // pnlJCNAClasses.createToolTip().setTipText("");
        spJCNAClasses.revalidate();
        spJCNAClasses.repaint();


       //pnlPreferenceContent.setPreferredSize(new Dimension(800, 800));
//        pnlPreferenceContent.setLayout(new java.awt.BorderLayout());
        //javax.swing.BoxLayout pnlPreferenceContentLayout;
        //pnlPreferenceContentLayout = new javax.swing.BoxLayout(pnlPreferenceContent, BoxLayout.LINE_AXIS);
        //pnlPreferenceContent.setLayout(pnlPreferenceContentLayout);
        //pnlPreferenceContent.add(pnlJCNAClassGroups);
        //pnlPreferenceContent.add(scrollPaneClasses);
        //pnlPreferenceContent.setVisible(true);

    
    
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnUnselectAll = new javax.swing.JButton();
        btnSelectAll = new javax.swing.JButton();
        pnlJCNAClasses = new javax.swing.JPanel();
        spJCNAClasses = new javax.swing.JScrollPane();
        pnlJCNAClassGroups = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select preferred Judging Classes");

        btnUnselectAll.setText("Unselect  All");
        btnUnselectAll.setToolTipText("Unselect  all classes. Makes a \"reserved\" Judge");

        btnSelectAll.setText("Select All");
        btnSelectAll.setToolTipText("Select all classes");

        pnlJCNAClasses.setBorder(javax.swing.BorderFactory.createTitledBorder("Classes"));

        javax.swing.GroupLayout pnlJCNAClassesLayout = new javax.swing.GroupLayout(pnlJCNAClasses);
        pnlJCNAClasses.setLayout(pnlJCNAClassesLayout);
        pnlJCNAClassesLayout.setHorizontalGroup(
            pnlJCNAClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 709, Short.MAX_VALUE)
            .addGroup(pnlJCNAClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlJCNAClassesLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(spJCNAClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlJCNAClassesLayout.setVerticalGroup(
            pnlJCNAClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 402, Short.MAX_VALUE)
            .addGroup(pnlJCNAClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spJCNAClasses, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
        );

        pnlJCNAClassGroups.setBorder(javax.swing.BorderFactory.createTitledBorder("Class Groups"));

        javax.swing.GroupLayout pnlJCNAClassGroupsLayout = new javax.swing.GroupLayout(pnlJCNAClassGroups);
        pnlJCNAClassGroups.setLayout(pnlJCNAClassGroupsLayout);
        pnlJCNAClassGroupsLayout.setHorizontalGroup(
            pnlJCNAClassGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
        );
        pnlJCNAClassGroupsLayout.setVerticalGroup(
            pnlJCNAClassGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 402, Short.MAX_VALUE)
        );

        btnOK.setText("OK");
        btnOK.setToolTipText("Accept selection and return to Add Judge dialog");
        btnOK.setAutoscrolls(true);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(pnlJCNAClassGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlJCNAClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(btnSelectAll)
                        .addGap(18, 18, 18)
                        .addComponent(btnUnselectAll))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(509, 509, 509)
                        .addComponent(btnOK)
                        .addGap(41, 41, 41)
                        .addComponent(btnCancel)))
                .addContainerGap(672, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUnselectAll)
                    .addComponent(btnSelectAll))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlJCNAClassGroups, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlJCNAClasses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        // Write inverse of the class selections to the class reject list of the added judge
        rejectList = CreateRejectedClassesList();
        return;
    }//GEN-LAST:event_btnOKActionPerformed

    public ArrayList<String> getRejectList(){
        return rejectList;
    }
    
private ArrayList<String> CreateRejectedClassesList(){
    ArrayList<String> list = new ArrayList<>();
    Component [] components = pnlJCNAClasses.getComponents();
   for( Component c : components){
       JCheckBox cb = (JCheckBox) c;
       if(!cb.isSelected() ){
           list.add(cb.getName() );
       }
        
   }
   return list;
}
    
    
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String args[]) {
        // Set the Nimbus look and feel
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        // If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        // For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         //
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConcoursJudgePrefDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConcoursJudgePrefDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConcoursJudgePrefDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConcoursJudgePrefDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConcoursJudgePrefDialog judgePrefDialog = new ConcoursJudgePrefDialog(new javax.swing.JFrame(), true, theConcours, theJCNAClassGroups );
                judgePrefDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                judgePrefDialog.setVisible(true);
            }
        });
    }
   */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JButton btnUnselectAll;
    private javax.swing.JPanel pnlJCNAClassGroups;
    private javax.swing.JPanel pnlJCNAClasses;
    private javax.swing.JScrollPane spJCNAClasses;
    // End of variables declaration//GEN-END:variables
}
