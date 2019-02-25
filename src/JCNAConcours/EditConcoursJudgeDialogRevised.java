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
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.JCNAClasses;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.Owner;


/**
 *
 * @author Ed Sowell
 * 
 * 
 *                       NOT USED
 */
public class EditConcoursJudgeDialogRevised extends javax.swing.JDialog {

    JPanel pnlJCNAClassGroups;
    JPanel pnlJCNAClasses;
    JScrollPane scrollPaneClasses;
    
    JCNAClass[] classMasterListArray;
    Concours theConcours;
    ConcoursPersonnel theConcoursePersonnel;
    Judges theConcoursJudges;
    List<JCNAClass> theJCNAclassList;
    JCNAClassesGroups theJCNAClassGroups;
    boolean systemexitwhenclosed;
    Connection theDBConnection;
    Logger theLogger;

    MasterPersonnel theMasterPersonnel;
    

    private static class MemberInfoFormat extends Format {

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo,
                        FieldPosition pos) {
                if (obj != null)
                        toAppendTo.append(((Judge) obj).getUniqueName());
                return toAppendTo;
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
                return OwnerRepository.getInstance().getMemberInfo(
                                source.substring(pos.getIndex()));
        }
        
    }

    /**
     * Creates new form ModifyoncoursJudgeDialog
     * 
     *               NOT USED
     */
    public EditConcoursJudgeDialogRevised(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel,  JudgeRepository aRepository, JCNAClass[] aClassMasterList, boolean aSystemexitwhenclosed) {
        super(parent, modal);
        theLogger = aConcours.GetLogger();
        theLogger.info("Starting EditConcoursJudgeDialog");
        this.setTitle("Modify Concours Judges Class Preferences");
        systemexitwhenclosed = aSystemexitwhenclosed;
        theDBConnection = aConnection;
        theJCNAClassGroups = new JCNAClassesGroups();
        classMasterListArray = aClassMasterList;
        //myInitComponents();
        initComponents();
        theConcours = aConcours;
        theMasterPersonnel = theConcours.GetMasterPersonnelObject();

        PopulatePrefPanel(theConcours);
        theLogger.info("Finished PopulatePrefPanel in EditConcoursJudgeDialog");
        this.setSize(1400,780); /// 5/19/2017
        // custom filterator
        TextFilterator<Judge> textFilterator = GlazedLists.textFilterator(
                Judge.class, "uniqueName");

        theConcoursJudges = theConcours.GetConcoursJudgesObject();
        /*
         * install auto-completion
         */
        theConcoursePersonnel = aConcoursPersonnel;
        Judge[] concoursJudges;
        concoursJudges = aRepository.getAllJudges();
        AutoCompleteSupport support = AutoCompleteSupport.install(this.cboJudgeUniqueName, GlazedLists.eventListOf(concoursJudges),
                textFilterator, new MemberInfoFormat());
        // and set to strict mode
        support.setStrict(true);

        /*
         * Based on the selected MasterPerson in  cboUniqueName, fill in the member attribute fields
         * in the dialog and get the jaguar stable for this member so it can be used to populate cboJaguars
         */
        UpdateJudgePeronalAttributes();

    }
private void PopulatePrefPanel(Concours aConcours)   {
        ArrayList<JCNAClass> jcnalasses = new ArrayList<>();
        String judgeassigngroupname;
        String classname;
        JCNAClassesGroup cg;
        
        //JCNAClassesSubset allClasses;
        /* 
         * iterate the list of JCNA Classes to set up the checkbox lists for Class judge assignment Groups and the 
         * for all classes. 
         *  
         * All of this is set into 2 panels in the dialog
         */
        
        // Set up Class Groups panel
        pnlJCNAClassGroups = new javax.swing.JPanel();
        pnlJCNAClassGroups.setBorder(javax.swing.BorderFactory.createTitledBorder("Select JCNA classes by groups"));
        javax.swing.BoxLayout pnlJCNAClassGroupsLayout;
        pnlJCNAClassGroupsLayout = new javax.swing.BoxLayout(pnlJCNAClassGroups, BoxLayout.PAGE_AXIS);
        pnlJCNAClassGroups.setLayout(pnlJCNAClassGroupsLayout);
        
        jcnalasses = aConcours.GetJCNAClasses().GetJCNAClasses();
        for (JCNAClass c : jcnalasses) {
            classname = c.getName();
            judgeassigngroupname = c.getJudgeAssignGroup();
            cg = theJCNAClassGroups.GetJCNAClassesGroup(judgeassigngroupname);
            if (cg == null) {
                cg = new JCNAClassesGroup(judgeassigngroupname);
                theJCNAClassGroups.AddClassesGroup(cg);
            }
            cg.AddClassName(classname);
        }
        // now populate the Class group scroll panel
        JCheckBox cb;
        //List<JCheckBox> cbList = new ArrayList<>();
       // pnlJCNAClassGroups.removeAll();  // clearing the checkboxes inserted with the GUI designer
        for (JCNAClassesGroup jcnaclassgroup : theJCNAClassGroups.GetClassesGroupList()) {
            cb = new JCheckBox(new JCNAClassesGroup.JCNAClassesGroupAction(jcnaclassgroup));
            cb.setName(jcnaclassgroup.GetGroupName());
            //cbList.add(cb);
            cb.addItemListener(new ItemListener() {
                /*
                 *  The following itemStateChanged listener transfers the Group selections to the list of all classes.
                 */
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String cbText;
                    String classname;
                    String cbName;
                    String[] groupclassnames;
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        JCheckBox cbe = (JCheckBox) e.getItem();
                        String groupname = cbe.getName();
                        groupclassnames = theJCNAClassGroups.GetJCNAClassesGroup(groupname).GetClassNames();
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
                                    cbText = cb.getText();
                                    cb.setSelected(true);
                                }
                            }
                            
                        }
                    } else {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            JCheckBox cbe = (JCheckBox) e.getItem();
                            String groupname = cbe.getName();
                            groupclassnames = theJCNAClassGroups.GetJCNAClassesGroup(groupname).GetClassNames();
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
                                        cbText = cb.getText();
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
        pnlJCNAClassGroups.revalidate();
        pnlJCNAClassGroups.repaint();        
        
        // Set up Classes panel

        pnlJCNAClasses = new javax.swing.JPanel();
        pnlJCNAClasses.setBorder(javax.swing.BorderFactory.createTitledBorder("Select JCNA classes individually"));
        pnlJCNAClasses.setPreferredSize(new Dimension(800, 400));
        //
        //  Set up scrolling for the Class panel
        //
        scrollPaneClasses = new JScrollPane(pnlJCNAClasses, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneClasses.setMinimumSize(new Dimension(400, 300));
        scrollPaneClasses.setMaximumSize(new Dimension(800, 400));
        scrollPaneClasses.setPreferredSize(new Dimension(800, 400));        
       
        javax.swing.BoxLayout pnlJCNAClassesLayout;
        pnlJCNAClassesLayout = new javax.swing.BoxLayout(pnlJCNAClasses, BoxLayout.PAGE_AXIS);
        pnlJCNAClasses.setLayout(pnlJCNAClassesLayout);
        pnlJCNAClasses.revalidate();
        pnlJCNAClasses.repaint();
        
        
        pnlJCNAClassGroups.setLayout(new BoxLayout(pnlJCNAClassGroups, BoxLayout.PAGE_AXIS)); 
        //pnlJCNAClassGroups.setVisible(true);
        
//       pnlPreferenceContent.add(pnlJCNAClassGroups, java.awt.BorderLayout.WEST);

      //  pnlJCNAClasses.removeAll(); // clearing the checkboxes inserted with the GUI designer
        for (JCNAClass c : classMasterListArray) {
            classname = c.getName();
            cb = new JCheckBox(new JCNAClass.JCNAClassAction(c));
            cb.setName(classname);
            pnlJCNAClasses.add(cb);
        }
        pnlJCNAClasses.revalidate();
        pnlJCNAClasses.repaint();


       pnlClassGroups.setPreferredSize(new Dimension(1900, 320)); //  NOTE: this will be too big once I get the Classes panel set right!!!!!!!!!!!!!!!!!!!
//        pnlPreferenceContent.setLayout(new java.awt.BorderLayout());
        javax.swing.BoxLayout pnlPreferenceContentLayout;
        pnlPreferenceContentLayout = new javax.swing.BoxLayout(pnlClassGroups, BoxLayout.LINE_AXIS);
        pnlClassGroups.setLayout(pnlPreferenceContentLayout);
        pnlClassGroups.add(pnlJCNAClassGroups);
        pnlClassGroups.add(scrollPaneClasses);
        pnlClassGroups.revalidate();
        pnlClassGroups.repaint();
        pnlClassGroups.setVisible(true);

}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSaveJudge = new javax.swing.JButton();
        pnlClassGroups = new javax.swing.JPanel();
        cbAll = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        pnlPersonalInfoContent = new javax.swing.JPanel();
        txtLast = new javax.swing.JTextField();
        txtJCNA = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtFirst = new javax.swing.JTextField();
        cboJudgeUniqueName = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        cbPlaceHolder = new javax.swing.JCheckBox();
        btnFinished = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSaveJudge.setText("Save");
        btnSaveJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveJudgeActionPerformed(evt);
            }
        });
        getContentPane().add(btnSaveJudge, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 480, -1, -1));

        pnlClassGroups.setBorder(javax.swing.BorderFactory.createTitledBorder("Select by Class group"));
        pnlClassGroups.setToolTipText("Select/deselect items in left or right panel");
        pnlClassGroups.setPreferredSize(new java.awt.Dimension(1500, 350));

        cbAll.setText("Select  all");

        jCheckBox1.setText("Unselect all");

        javax.swing.GroupLayout pnlClassGroupsLayout = new javax.swing.GroupLayout(pnlClassGroups);
        pnlClassGroups.setLayout(pnlClassGroupsLayout);
        pnlClassGroupsLayout.setHorizontalGroup(
            pnlClassGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClassGroupsLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlClassGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(cbAll))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlClassGroupsLayout.setVerticalGroup(
            pnlClassGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClassGroupsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(cbAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnlClassGroups, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 210, 500, 240));
        pnlClassGroups.getAccessibleContext().setAccessibleName("");

        pnlPersonalInfoContent.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Judge"));

        txtLast.setEditable(false);
        txtLast.setText("last");
        txtLast.setEnabled(false);
        txtLast.setFocusable(false);

        txtJCNA.setEditable(false);
        txtJCNA.setText("unknown");
        txtJCNA.setEnabled(false);
        txtJCNA.setFocusable(false);

        jLabel2.setText("First");

        jLabel4.setText("Last");

        jLabel1.setText("Judge unique name");

        jLabel10.setText("JCNA #");

        txtFirst.setEditable(false);
        txtFirst.setText("first");
        txtFirst.setEnabled(false);
        txtFirst.setFocusable(false);

        cboJudgeUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJudgeUniqueName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJudgeUniqueNameItemStateChanged(evt);
            }
        });
        cboJudgeUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJudgeUniqueNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPersonalInfoContentLayout = new javax.swing.GroupLayout(pnlPersonalInfoContent);
        pnlPersonalInfoContent.setLayout(pnlPersonalInfoContentLayout);
        pnlPersonalInfoContentLayout.setHorizontalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(cboJudgeUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70))
                    .addComponent(jLabel10))
                .addGap(194, 194, 194))
        );
        pnlPersonalInfoContentLayout.setVerticalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboJudgeUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnlPersonalInfoContent, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, -1));

        cbPlaceHolder.setText("cbPlaceHolder");
        cbPlaceHolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPlaceHolderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(cbPlaceHolder)
                .addContainerGap(572, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(cbPlaceHolder)
                .addContainerGap(490, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 220, 580, 230));

        btnFinished.setText("Finished");
        getContentPane().add(btnFinished, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 480, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void UpdateJudgePeronalAttributes() {
        // System.out.println("cboUniqueName itemStateChange");
        Judge selectedJudge = null;
        Integer judgeNode;
        String first;
        String last;
        Integer jcna;
        ConcoursPerson concoursperson;
        Long masterPerson_id;
       // System.out.println("cboUniqueName ItemEvent.SELECTED ");
        selectedJudge = (Judge) EditConcoursJudgeDialogRevised.this.cboJudgeUniqueName.getSelectedItem();

        if (selectedJudge != null) {

            judgeNode = selectedJudge.GetNode();
            first = theMasterPersonnel.GetMasterPersonnelFirstName(selectedJudge.getUniqueName());
            last = theMasterPersonnel.GetMasterPersonnelLastName(selectedJudge.getUniqueName());
            jcna = theMasterPersonnel.GetMasterPersonnelJCNA(selectedJudge.getUniqueName());
            theLogger.info("Selected Judge Node: " + judgeNode + " First: " + first + "Last: " + last + " in UpdateJudgePeronalAttributes");
            txtJCNA.setText(jcna.toString());
            txtFirst.setText(first);
            txtLast.setText(last);
                    
            //
            //  Set Class preferences to current values for the selected Judge
            //  Don't bother to set the Class Groups
            //
            JCheckBox cb;
            JPanel p = pnlJCNAClasses;
            List<JCNAClass> acceptedClass = new ArrayList<>();
            
            // First select all JCNA Classes
            int count = p.getComponentCount();
            for (int j = 0; j < count; j++) {
               cb = (JCheckBox) p.getComponent(j);
               cb.setSelected(true);
            }
            // Now uncheck those on the reject list for this Judge
            for (String rejectedClassName : selectedJudge.GetRejectClasses()) {
                for (int j = 0; j < count; j++) {
                   cb = (JCheckBox) p.getComponent(j);
                   if (cb.getName().equals(rejectedClassName)) {
                       // System.out.println("Class " +  rejectedClassName + " unchecked");
                        cb.setSelected(false);
                   }
                }
            }
               
                    
        } else{
            okDialog("Selected Judge is null");
            theLogger.info("Selected Judge is null in UpdateJudgePeronalAttributes");
        }

    }
    
    /*private void myInitComponents(){
        btnSaveJudge = new javax.swing.JButton();
        btnFinished = new javax.swing.JButton();
        pnlClassGroups = new javax.swing.JPanel();
        pnlPersonalInfoContent = new javax.swing.JPanel();
        txtLast = new javax.swing.JTextField();
        txtLast.setEnabled(false);
        txtLast.setFocusable(false);
        txtJCNA = new javax.swing.JTextField();
        txtJCNA.setEnabled(false);
        txtJCNA.setFocusable(false);
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtFirst = new javax.swing.JTextField();
        txtFirst.setEnabled(false);
        txtFirst.setFocusable(false);
        cboJudgeUniqueName = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        setMaximumSize(new java.awt.Dimension(1200, 800)); // the dialog
        setPreferredSize(new java.awt.Dimension(1200, 800)); // the dialog

        btnSaveJudge.setText("Save");
        btnSaveJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveJudgeActionPerformed(evt);
            }
        });

        btnFinished.setText("Finished");
        btnFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishedActionPerformed(evt);
            }
        });

        pnlClassGroups.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Judge Class Preferences"));
        pnlClassGroups.setToolTipText("Select/deselect boxes in left or right panel.");
        // Group layout might not be the best for pnlPreferenceContent

       / javax.swing.GroupLayout pnlPreferenceContentLayout = new javax.swing.GroupLayout(pnlPreferenceContent);
        //pnlPreferenceContent.setLayout(pnlPreferenceContentLayout);
       // pnlPreferenceContentLayout.setHorizontalGroup(
       //     pnlPreferenceContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      //      .addGap(0, 1069, Short.MAX_VALUE)
      //  );
     //   pnlPreferenceContentLayout.setVerticalGroup(
     //       pnlPreferenceContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
     //       .addGap(0, 333, Short.MAX_VALUE)
     //   );
        //
        java.awt.FlowLayout pnlPreferenceContentLayout = new java.awt.FlowLayout();
        pnlClassGroups.setLayout(pnlPreferenceContentLayout);
        pnlClassGroups.setSize(new Dimension(1000, 400));
        
        pnlPersonalInfoContent.setBorder(javax.swing.BorderFactory.createTitledBorder("Judge selection"));

        txtLast.setEditable(false);
        txtLast.setText("last");



        txtJCNA.setEditable(false);
        txtJCNA.setText("unknown");
        // is this needed?

        jLabel2.setText("First");


        jLabel4.setText("Last");



        jLabel1.setText("Judge unique name");

        jLabel10.setText("JCNA #");


        txtFirst.setEditable(false);
        txtFirst.setText("first");




        //  Maybe this isn't necessary.... should put the real one in here?
        cboJudgeUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Name 1", "Name 2", "Name 3", "Name 4" }));
        cboJudgeUniqueName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJudgeUniqueNameItemStateChanged(evt);
            }
        });
        cboJudgeUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJudgeUniqueNameActionPerformed(evt);
            }
        });



        javax.swing.GroupLayout pnlPersonalInfoContentLayout = new javax.swing.GroupLayout(pnlPersonalInfoContent);
        pnlPersonalInfoContent.setLayout(pnlPersonalInfoContentLayout);
        pnlPersonalInfoContentLayout.setHorizontalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(cboJudgeUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70))
                    .addComponent(jLabel10))
                .addGap(194, 194, 194))
        );
        pnlPersonalInfoContentLayout.setVerticalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboJudgeUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(pnlClassGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(449, 449, 449)
                        .addComponent(btnSaveJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFinished))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(pnlPersonalInfoContent, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(pnlPersonalInfoContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(180, 180, 180)
                .addComponent(pnlClassGroups, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveJudge)
                    .addComponent(btnFinished))
                .addGap(69, 69, 69))
        );

 


        getContentPane().setLayout(layout);

        pack();
        
    }
*/
    private void ClearSelectedClassList(ArrayList<JCNAClass> aSelectedClasses) {
        aSelectedClasses.clear();
    }

    private void btnSaveJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveJudgeActionPerformed

        String person_unique_name;
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        ArrayList<String> rejectedClasses;

        Judge selectedJudge = (Judge) EditConcoursJudgeDialogRevised.this.cboJudgeUniqueName.getSelectedItem();

        person_unique_name = selectedJudge.getUniqueName();
       
        rejectedClasses = CreateRejectedClassesList(); // All the classes that were NOT selected
        selectedJudge.SetRejectClasses(rejectedClasses);

           // Now update the affected database tables:
       loadSQLiteConcoursDatabase.UpdateJudgeClassRejectDBTable(theDBConnection, selectedJudge);
       // ++++++++++++
       // Changed 7/14/2016 to remove the no longer valid Judge Assignment/schedule tables from the database
       // the previouse Judge Assignment is now invalid so manual editing is disabled.
        // Also, might as well clear the JudgeAssignments Table and EntryJudgesTable
        theConcours.SetJudgeAssignmentCurrent(false); 
        try { 
            loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
        } catch (SQLException ex) {
                String msg = "SQLException in EditConcoursJudgeDialogRevised call to SetSettingsTableJAState";
                okDialog(msg);
                theConcours.GetLogger( ).log(Level.SEVERE, msg, ex);
        }
         //This fails due to locked table..  probably don't need anyway   
        // Trying again... 7/14/2016   WORKED.  No locked tables.
        theConcours.GetLogger().info("Calling ClearJudgeAssignmentsTables() after UpdateJudgeClassRejectDBTable)()");
        loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);
       // ++++++++
       // --------
       // the previouse Judge Assignment is now invalid so manual editing is disabled pending rematching/scheduling.
       //  theConcours.SetJudgeAssignmentCurrent(false); 
       /// loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ; 
       // --------
        okDialog("Judging Class preferences for Judge " + person_unique_name + " have been updated");
        theLogger.info("Judging Class preferences for Judge " + person_unique_name + " have been updated in btnSaveJudgeActionPerformed.");
    }//GEN-LAST:event_btnSaveJudgeActionPerformed

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


    private void cboJudgeUniqueNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboJudgeUniqueNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboJudgeUniqueNameActionPerformed

    private void cboJudgeUniqueNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJudgeUniqueNameItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) { // Without this check the action will take place when item is selected and unselected... twice
            UpdateJudgePeronalAttributes();
        }
    }//GEN-LAST:event_cboJudgeUniqueNameItemStateChanged

    private void cbPlaceHolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPlaceHolderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbPlaceHolderActionPerformed

    /**
     * @param args the command line arguments
     */
    
            ////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////  !!!!!!                          SOMETHING'S WRONG WITH THIS MAIN. IT CRASHES, LEAVING THE TARGET DB FILE CORRUPTED!!!!!!!!!!!!!!!!!!
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) {

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
            java.util.logging.Logger.getLogger(EditConcoursJudgeDialogRevised.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditConcoursJudgeDialogRevised.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditConcoursJudgeDialogRevised.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditConcoursJudgeDialogRevised.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Connection conn;
                String strConn;
                //C:\Users\Owner\Documents\My Concourses\TutorialE20J11
                String strDBName = "TutorialE5J5";
                String strPath = "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\My Concourses" + "\\" + strDBName+ "\\" + strDBName + ".db";
                //String strPath= "C:\\Users\\jag_m_000\\Documents\\Concours" + "\\" + strDBName;
                conn = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    strConn = "jdbc:sqlite:" + strPath;
                    conn = DriverManager.getConnection(strConn);
                    System.out.println("Opened database " + strConn + " successfully");
                } catch (ClassNotFoundException | SQLException e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
                System.out.println("Opened database " + strPath + " successfully");
                Logger logger = Logger.getLogger("ConcoursBuilderLog");  
                FileHandler fh = null;  
                try {
                    fh = new FileHandler(strPath);  // The log file will be in the strPath
                } catch (IOException ex) {
                    Logger.getLogger(EditConcoursJudgeDialogRevised.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(EditConcoursJudgeDialogRevised.class.getName()).log(Level.SEVERE, null, ex);
                }
                logger.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();  
                fh.setFormatter(formatter);  
                logger.info("ConcoursBuilder started");  
                Concours theConcours = new Concours(logger, 3);
                theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses",  logger);
                theConcours.LoadMasterPersonnelDB(conn, logger);
                theConcours.LoadConcoursPersonnelDB(conn, logger);
                theConcours.LoadMasterJaguarDB(conn, logger);
                theConcours.LoadEntriesDB(conn, logger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
                theConcours.LoadJudgesDB(conn, logger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
                theConcours.LoadOwnersDB(conn, logger);

                JudgeRepository judgeList = new JudgeRepository(theConcours);
                ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
                ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
                JCNAClass[] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()]);
                EditConcoursJudgeDialogRevised theDialog = new EditConcoursJudgeDialogRevised(new javax.swing.JFrame(), true,  conn, theConcours, theConcoursPersonnel, judgeList, classMasterArray, true);

                theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });

                theDialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinished;
    private javax.swing.JButton btnSaveJudge;
    private javax.swing.JCheckBox cbAll;
    private javax.swing.JCheckBox cbPlaceHolder;
    private javax.swing.JComboBox cboJudgeUniqueName;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlClassGroups;
    private javax.swing.JPanel pnlPersonalInfoContent;
    private javax.swing.JTextField txtFirst;
    private javax.swing.JTextField txtJCNA;
    private javax.swing.JTextField txtLast;
    // End of variables declaration//GEN-END:variables
}
