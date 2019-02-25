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
import edu.princeton.cs.algs4.SET;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.ini4j.Wini;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.JCNAClassRule;
import us.efsowell.concours.lib.JCNAClassRules_2;
import us.efsowell.concours.lib.JCNAClasses;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
//import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.CopyJCNAClassRules;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.LoadJCNAClassRulesFromCSV;

/**
 *
 * @author Ed Sowell
 */
public class JCNAClassChooserGUI extends javax.swing.JFrame {

    
    
    public Logger logger;
   
    public Concours theConcours;
    public CopySaveDBFile copysaveObject;
    String thePath; // where the DB & CSV files are
    Connection theConn;
    private File flConcoursDBFile;
    boolean boolDBFileOpened;
    private boolean boolStandalone;
    int firstJagYear;
    int maxJagYear;
    private String strCurDir;
    JCNAClassRules_2 theJCNAClassRules; // Store JCNAClassRules in memory to support an in-memory filtering method
    JCNAClasses theJCNAClasses; // Store JCNAClassRules in memory to support an in-memory filtering method
    private LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase;
    
    // Constructor for function access
//     public JCNAClassChooserGUI(){
         
//     }

    /**
     * Constructor
     *                    Creates new form JCNAClassChooserGUI
     */
    public JCNAClassChooserGUI(boolean aStandalone, Concours aConcours) {
        boolStandalone = aStandalone;
        boolDBFileOpened = false;
        String q;
        initComponents();
        cboYear.setEnabled(boolDBFileOpened);
        cboModel.setEnabled(boolDBFileOpened);
        cboDivision.setEnabled(boolDBFileOpened);
        cboDescriptor.setEnabled(boolDBFileOpened);
        btnLookup.setEnabled(boolDBFileOpened);
        LoadMenuItem.setEnabled(boolDBFileOpened);
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();
        setTitle("Look up JCNA Classes");
        if(aStandalone){
            theConcours = null;
            copysaveObject = new CopySaveDBFile();
            logger = Logger.getLogger("filechooserLog");
            theJCNAClassRules = new JCNAClassRules_2(logger);
            theJCNAClasses = new JCNAClasses(logger);
            String strDisplayText = "";
            strCurDir = Paths.get(".").toAbsolutePath().normalize().toString(); // Just to see the current working directory...

            strDisplayText = strDisplayText + "\n" + " Current working directory: " + strCurDir + "\n";
            String userDocDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString(); // tricky way of getting at user/ Documents directory
            String strIniFilePath= userDocDirectory + "\\" +"ConcoursBuilder.ini";

            //System.out.println("The ini file: " + strIniFileName + "\n");
            File flIni = new File(strIniFilePath);
            Wini ini = null;
            try {
                ini = new Wini(flIni);
            } catch (IOException ex) {
                okDialog("IO exception in JCNAClassChooserGUI");
                logger.log(Level.SEVERE, null, ex);
                logger.info("ERROR: Could not open " + strIniFilePath + " in JCNAClassChooserGUI");
                System.exit(-1);
            }
            //String installPath = ini.get("InstallSettings", "InstallPath");
            String concoursBuilderDataPath; // This is where the concours database & other data for the run is
            if(strCurDir.contains("NetBeansProjects")){
                concoursBuilderDataPath = ini.get("InstallSettings", "DeveloperDataPath");
            } else{
                concoursBuilderDataPath = ini.get("InstallSettings", "UserDataPath");
            }
            FileHandler fh;  
            try {
                fh = new FileHandler(concoursBuilderDataPath + "\\JCNAClassChooser.log");  // The log file will be in the concoursBuilderDataPath
                logger.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();  
                fh.setFormatter(formatter);  
                logger.info("JCNA Class Chooser started");  
            } catch (SecurityException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            } 
            logger.info(" Current working directory: " + strCurDir)    ;        

            
            ConcoursDBFileChooser.setCurrentDirectory(new File(concoursBuilderDataPath)); 
            ConcoursDBFileChooser.setDialogTitle("Open database the Rules are to be added to");
            //ConcoursDBFileChooser.setFileFilter(new CustomBaseDBFileFilter() );
            ConcoursDBFileChooser.setFileFilter(new MyCustomFilterDB() ); // This is so the Rules can be added to any Concours databas file, including but not limited to Base db files

            RulesCSVFileChooser.setCurrentDirectory(new File(concoursBuilderDataPath));
            RulesCSVFileChooser.setDialogTitle("Load JCNA Class Rules into Base database");
            RulesCSVFileChooser.setFileFilter(new MyCustomFilterCSV() );
            
            // Note: Can't populate the dropdown boxes  until the OpenMenueItem action is performed...
           
        } else {
            theConcours = aConcours;
            FileMenu.remove(OpenMenuItem);
            FileMenu.remove(LoadMenuItem);
            logger = theConcours.GetLogger();
            theConn = theConcours.GetConnection();
            boolDBFileOpened = true;
            
            cboYear.setEnabled(boolDBFileOpened);
            cboModel.setEnabled(boolDBFileOpened);
            cboDivision.setEnabled(boolDBFileOpened);
            cboDescriptor.setEnabled(boolDBFileOpened);
            btnLookup.setEnabled(boolDBFileOpened);
            //boolean rulesMissing = loadSQLiteConcoursDatabase.tableExists(theConn,  logger, "JCNAClassRules");
            boolean rulesMissing = false;
            if(rulesMissing){
                String strConcoursDBFile = theConcours.GetThePath();
               okDialog("JCNA Class Rules table missing from " + strConcoursDBFile + "\nYou must load from CSV to get current rules.");
               PopulateCboListsJCNAClassChooserGUI(true); // Load from internal data
               this.btnLookup.setEnabled(false);
            } else {
                theJCNAClassRules = theConcours.GetJCNAClassRules();
                theJCNAClasses = theConcours.GetJCNAClasses();
                PopulateCboListsJCNAClassChooserGUI(false); // Load from DB
            }
        }
        logger.info("Finished setting up combo boxes in JCNAClassFinder");

    }

/*public ArrayList<String> GetSortedJCNAModels(ArrayList<String> aModelsList){
    ArrayList<String> sortedModels = new ArrayList<>(aModelsList);
    //Collections.sort(sortedEntries, new CustomComparator());
    Collections.sort(sortedModels);
    return sortedModels;
} 
    */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConcoursDBFileChooser = new javax.swing.JFileChooser();
        RulesCSVFileChooser = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();
        btnLookup = new javax.swing.JButton();
        cboYear = new javax.swing.JComboBox();
        cboModel = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboDivision = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cboDescriptor = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        btnFinished = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        OpenMenuItem = new javax.swing.JMenuItem();
        LoadMenuItem = new javax.swing.JMenuItem();
        ExitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JCNA Judging Class Chooser");

        txtArea.setColumns(20);
        txtArea.setRows(5);
        txtArea.setToolTipText("JCNA judging classes matching the selected attributes.");
        jScrollPane1.setViewportView(txtArea);

        btnLookup.setText("Lookup");
        btnLookup.setToolTipText("Click to look up the JCNA judging classes that match the current attribute selections.");
        btnLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupActionPerformed(evt);
            }
        });

        cboYear.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboYear.setToolTipText("Select the model year. Optional.");

        cboModel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboModel.setToolTipText("Select the Jaguar model. Optional.");

        jLabel1.setText("Year model");

        jLabel2.setText("Jag model");

        jLabel3.setText("Division");

        cboDivision.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboDivision.setToolTipText("Select the JCNA  Division. Optional.");

        jLabel4.setText("Descriptor");

        cboDescriptor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboDescriptor.setToolTipText("Select a auxiliary descriptor. Optional.");

        jLabel5.setText("Selected Classes & rules");

        btnFinished.setText("Finished");
        btnFinished.setToolTipText("Click when finished");
        btnFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishedActionPerformed(evt);
            }
        });

        FileMenu.setText("File");
        FileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileMenuActionPerformed(evt);
            }
        });

        OpenMenuItem.setText("Open");
        OpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(OpenMenuItem);

        LoadMenuItem.setText("Load fromCSV");
        LoadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(LoadMenuItem);

        ExitMenuItem.setText("Exit");
        ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(ExitMenuItem);

        jMenuBar1.add(FileMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboDescriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboDivision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboYear, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(86, 86, 86)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnLookup)
                        .addGap(18, 18, 18)
                        .addComponent(btnFinished)
                        .addGap(257, 257, 257))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(293, 293, 293))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboDivision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboDescriptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLookup)
                    .addComponent(btnFinished))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenMenuItemActionPerformed
        //
        // Load JCNA Rules from database file
        // This is used only for standalone usage of the JCNAClassChooserGUI.
        //  When launched from the ConcoursGUI, all this work has already been done
        // 
        int returnVal;

        returnVal = ConcoursDBFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            flConcoursDBFile = ConcoursDBFileChooser.getSelectedFile();
            String strConcoursDBFile = flConcoursDBFile.getAbsolutePath(); // this is the the absolute path to the DB file as a string
            /* the restriction below is no longer enforced because it is sometimes necessary to update the Class Rules of a Concours file.
            
            if(!strConcoursDBFile.contains("Base")){
                okDialog("The selected DB file must be a Concours Base fail. Such files contain Base in the name, e.g., SoCalBase.db.");     
                return;
            }
            */
                /*
                Create a copy in case user decides to not save the revised Concours DB file.
                */ 
                String strBackupFileName = strConcoursDBFile.replace(".db", "Backup.db");
            try {
                // Both source (the Opened Concours DB file) & destination (the newly created file named strBackupFileName) are closed before copyDBFile() returns
                copysaveObject.copyDBFile(flConcoursDBFile, new File(strBackupFileName), logger);
            } catch (IOException ex) {
                okDialog("ERROR: IO exception in OpenMenuItemActionPerformed");
                logger.info("ERROR: IO exception in OpenMenuItemActionPerformed");
                logger.log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
            
            thePath = strConcoursDBFile;
            theConn = null;
            try {
                Class.forName("org.sqlite.JDBC");
                String strConn;
                strConn = "jdbc:sqlite:" + strConcoursDBFile ;
                theConn = DriverManager.getConnection(strConn);
                theConn.createStatement().execute("PRAGMA foreign_keys = ON");
            } catch ( ClassNotFoundException | SQLException e ) {
                logger.info( e.getClass().getName() + "ERROR: " + e.getMessage() );
                System.exit(0);
            }
            boolDBFileOpened = true;
            logger.info("Opened JCNA Base database " + strConcoursDBFile + " successfully");
            cboYear.setEnabled(boolDBFileOpened);
            cboModel.setEnabled(boolDBFileOpened);
            cboDivision.setEnabled(boolDBFileOpened);
            cboDescriptor.setEnabled(boolDBFileOpened);
            btnLookup.setEnabled(boolDBFileOpened);
            boolean rulesMissing = loadSQLiteConcoursDatabase.tableExists(theConn,  logger, "JCNAClassRules");
            
            if(rulesMissing){
               okDialog("JCNA Class Rules table missing from " + strConcoursDBFile + "\nYou must load from CSV to get current rules.");
               PopulateCboListsJCNAClassChooserGUI(true); // Load from internal data
               this.btnLookup.setEnabled(false);
            } else {
                // Load 
                theJCNAClasses.LoadJCNAClassesDB(theConn, "JCNAClasses", logger);
                logger.info("Loaded JCNA Classes successfully");
                //theJCNAClassRules.LoadJCNAClassRulesDB(theConn, logger);
                boolean classRulesOK = theJCNAClassRules.LoadJCNAClassRulesDB(theConn, "JCNAClassRules", logger);
                if(!classRulesOK){
                    okDialog("ERROR: LoadJCNAClassRulesDB failed in OpenMenuItemActionPerformed() in JCNAClassChooser");
                     theConcours.GetLogger().info("ERROR: LoadJCNAClassRulesDB failed in OpenMenuItemActionPerformed() in JCNAClassChooser");
                     System.exit(-1);
                } else{
                    logger.info("Loaded JCNA Class Rules successfully OpenMenuItemActionPerformed() in JCNAClassChooser");
                }

                PopulateCboListsJCNAClassChooserGUI(false); // Load from DB
            }
           LoadMenuItem.setEnabled(true);
        } else {
            logger.info("User cancelled opening DB file");
        }
    }//GEN-LAST:event_OpenMenuItemActionPerformed

    
            //
            //  Populate models comboBox
            //
    private void PopulateCboListsJCNAClassChooserGUI(boolean useHardcodedlModelsAndDescriptors){
        //
        //  set up comboBox lists
        //
        //   Years
        cboYear.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"All"}));
        firstJagYear = 1927;
        maxJagYear = 2030;
        //Integer [] years = new Integer[2030-1927+1];
        for(int i = 0; i< (maxJagYear-firstJagYear+1); i++){
            cboYear.addItem(firstJagYear + i);
        }
        //
        //  Divisions
        //
        cboDivision.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"All", "Championship", "Driven", "Special"}));
        //
        //  Models
        //
        cboModel.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"All"}));
        cboDescriptor.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"All"}));
        if(!useHardcodedlModelsAndDescriptors){
            //
            //                               REVISED TO WORK FROM IN-MEMORY JCNA CLASSES & RULES
            //
            /*
            // Extract Model Names & Descriptors from the database JCNARules table
            // These are used to populate the dropdown lists in the dialog
            Statement stmt_models;
            try{
                stmt_models = theConn.createStatement();
                String q = "select modelname from JCNAClassRules";
                ResultSet rs = stmt_models.executeQuery(q);
                List<String> models = new ArrayList<>();
                while(rs.next()){
                    String theModel = rs.getString("modelname");
                    if(!models.contains(theModel)){
                        models.add(theModel);
                    }
                }
                stmt_models.close();
                rs.close();
                ArrayList<String> sortedModels = new ArrayList<String>(models);
                Collections.sort(sortedModels);
                String [] sortedModelsAry;
                sortedModelsAry = sortedModels.toArray(new String[sortedModels.size()] );
                for(String s: sortedModelsAry){
                    cboModel.addItem(s);
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            */
            cboModel.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"All"}));
            List<String> models = new ArrayList<>();
            ArrayList<JCNAClassRule>  theJCNAClassRulesAry = theJCNAClassRules.getJCNAClassRules();
            for(JCNAClassRule jcr : theJCNAClassRulesAry){
                String theModel = jcr.getModelName();
                if(!models.contains(theModel)){
                    models.add(theModel);
                }
            }
            ArrayList<String> sortedModels = new ArrayList<String>(models);
            Collections.sort(sortedModels);
            String [] sortedModelsAry;
            sortedModelsAry = sortedModels.toArray(new String[sortedModels.size()] );
            for(String s: sortedModelsAry){
                cboModel.addItem(s);
            }
            
            //  Populate Descriptors comboBox
            //
            /*
            //                      No longer necessary to work directly on the database...
            Statement stmt_desc;
            try {
                stmt_desc = theConn.createStatement();
                String q = "select descriptor_1, descriptor_2, descriptor_3 from JCNAClassRules";
                ResultSet rs_desc= stmt_desc.executeQuery(q);
                List<String> descriptors = new ArrayList<>();
                while(rs_desc.next()){
                    String d1 = rs_desc.getString("descriptor_1");
                    if( d1 != null && !d1.isEmpty() && !descriptors.contains(d1.trim())) descriptors.add(d1.trim());
                    String d2 = rs_desc.getString("descriptor_2");
                    if(d2 != null  && !d2.isEmpty() && !descriptors.contains(d2.trim())) descriptors.add(d2.trim());
                    String d3 = rs_desc.getString("descriptor_3");
                    if(d3 != null  && !d3.isEmpty() && !descriptors.contains(d3.trim())) descriptors.add(d3.trim());
                }
                stmt_desc.close();
                rs_desc.close();
                ArrayList<String> sortedDescriptors = new ArrayList<>(descriptors);
                Collections.sort(sortedDescriptors);
                String [] sortedDescriptorssAry;
                sortedDescriptorssAry = sortedDescriptors.toArray(new String[sortedDescriptors.size()] );
                for(String s: sortedDescriptorssAry){
                    cboDescriptor.addItem(s);
                }
                
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            */
            cboDescriptor.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"All"}));
            List<String> descriptors = new ArrayList<>();
            for(JCNAClassRule jcr : theJCNAClassRulesAry){
                String d1 = jcr.getDescriptor_1();
                if( d1 != null && !d1.isEmpty() && !descriptors.contains(d1.trim())) descriptors.add(d1.trim());
                String d2 = jcr.getDescriptor_2();
                if(d2 != null  && !d2.isEmpty() && !descriptors.contains(d2.trim())) descriptors.add(d2.trim());
                String d3 = jcr.getDescriptor_3();
                if(d3 != null  && !d3.isEmpty() && !descriptors.contains(d3.trim())) descriptors.add(d3.trim());
            }
            ArrayList<String> sortedDescriptors = new ArrayList<>(descriptors);
            Collections.sort(sortedDescriptors);
            String [] sortedDescriptorssAry;
            sortedDescriptorssAry = sortedDescriptors.toArray(new String[sortedDescriptors.size()] );
            for(String s: sortedDescriptorssAry){
                cboDescriptor.addItem(s);
            }
        } else{
            // Hard-coded  model names & descriptors.... This option was used during development. Probably will never be used again
            String[] modelNames = {
            "C01/PRE", "C02/120", "C03/140", "C04/150", "C05/E1", "C06/E2", "C07/E3", "C08/SLS", "C09/XJ", "C10/XJ", "C11/J8", "C12/JS", "C13/JS", "C14/K8",
            "C15/XK", "C16/SX", "C17/PN", "C18/PN", "C19/FJ", "C20/F",
            "D01/PRE", "D02/E1", "D03/E2", "D04/E3", "D05/SLS", "D06/XJ", "D07/XJ", "D08/XJS", "D09/XJS", "D10/K8", "D11/XK", "D12/J8", "D13/SX", "D14/FJ",
            "D15/F"};
            List<String> models = new ArrayList<>();
            for(String mn : modelNames){
                if(!models.contains(mn)){
                    models.add(mn);
                }
            }
            
            ArrayList<String> sortedModels = new ArrayList<String>(models);
            Collections.sort(sortedModels);
            String [] sortedModelsAry;
            sortedModelsAry = sortedModels.toArray(new String[sortedModels.size()] );
            for(String s: sortedModelsAry){
                cboModel.addItem(s);
            }
            String[] descriptorText = {
            "Classics", "Pre-XK Engine", "XK", "Sport Cars", "Series 1", "Series 2", "Series 1.5", "Series 3", "XKE", "Early", "Large", "Small", "Saloons", "Coupes",
            "Daimler", "VDP", "Sovereign", "Sedans",  "H&E",
            "Series III", "XJ40", "X300", "X308", "X350", "Cabriolet", "Convertible", "XJ-RS", "Pre-Facelift", "Facelift", "XKR", "Estate Wagon",
            "D15/F"};
            List<String> descriptors = new ArrayList<>();
            for(String d : descriptorText){
                if(!descriptors.contains(d)){
                    descriptors.add(d);
                }
            }
            
            ArrayList<String> sortedDescriptors = new ArrayList<String>(descriptors);
            Collections.sort(sortedDescriptors);
            String [] sortedDescriptorArray;
            sortedDescriptorArray = sortedDescriptors.toArray(new String[sortedDescriptors.size()] );
            for(String s: sortedDescriptorArray){
                cboDescriptor.addItem(s);
            }
        }
        
    }
    
    private void LoadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadMenuItemActionPerformed
        String strCSVFileAbsPath = null;
        String strCSVFileName;
        String fileName;
        RulesCSVFileChooser.setDialogTitle("Load JCNA Class rules from CSV");
        int returnVal = RulesCSVFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File flCSVFile = RulesCSVFileChooser.getSelectedFile();
            strCSVFileAbsPath = flCSVFile.getAbsolutePath(); // this is the the absolute path to the DB file as a string
            Path path = Paths.get(strCSVFileAbsPath); // this is the the absolute path to the DB file as a Path
            fileName = path.getFileName().toString();    // name only        
            File f = new File(fileName);
            strCSVFileName = f.getName();
        } else{
            logger.info("User cancelled Class Rules CSV file chooser");
            return;
        }
        LoadJCNAClassRulesFromCSV(theConn, "JCNAClasses", "JCNAClassRules", strCSVFileAbsPath, theConcours);
        this.btnLookup.setEnabled(true);
    
    }//GEN-LAST:event_LoadMenuItemActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        if(boolStandalone && boolDBFileOpened){
            if(copysaveObject.getSavedRequestedFlag() == 0){ 
                // user has not explicitly requested saving the revised Concours DB file, so we ask to be sure
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(this, "Do you wish to save the revised database file?", "Save revised Base database",dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION){
                    // Nothing to do since the changes have been synched with the originally selected Concours DB file.
                    logger.info("\nUser elected to save the revised database");
                    //okDialog("User elected to save the new or revised database");
                } else{
                    // User doesn't want to keep the results so we rename the Backup Concours database to the original file
                    logger.info("\nUser elected not to save the revised database");
                    //okDialog("User elected not to save the new or revised database");
                    String strBackupFilePath = flConcoursDBFile.getAbsolutePath().replace(".db", "Backup.db");
                    try {
                        copysaveObject.copyDBFile(new File(strBackupFilePath), flConcoursDBFile, logger);
                    } catch (IOException ex) {
                        okDialog("ERROR: IOException in copysaveObject.copyDBFile called from ExitMenuItemActionPerformed ");
                        logger.log(Level.SEVERE, null, ex);
                        logger.info("ERROR: IOException in copysaveObject.copyDBFile called from ExitMenuItemActionPerformed ");
                        System.exit(-1);
                    }
               }
            }
        }
        if(boolStandalone){
            logger.info("Exiting ");
            System.exit(0);            
        } else{
            this.setVisible(false);
            return;
        }

    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void btnLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupActionPerformed
            String strYear = cboYear.getSelectedItem().toString();
            String strModel = cboModel.getSelectedItem().toString();
            String strDivision = cboDivision.getSelectedItem().toString();
            String strDescriptor = cboDescriptor.getSelectedItem().toString();
            List<String> filteredList = filterInMemory(theConn, strYear, strModel, strDivision, strDescriptor);
            txtArea.setText("");
            if(filteredList.isEmpty()){
                txtArea.setText("[No Classes match user choices]");
            } else{
                for(String s : filteredList){
                    txtArea.append(s);
                }
            }
    }//GEN-LAST:event_btnLookupActionPerformed

    private void btnFinishedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishedActionPerformed
       setVisible(false);
       return;
    }//GEN-LAST:event_btnFinishedActionPerformed

    private void FileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileMenuActionPerformed
       
    }//GEN-LAST:event_FileMenuActionPerformed

    public List<String> filterInMemory(Connection aConn, String aYear, String aModel, String aDivision, String aDescriptor){
        List<JCNAClassRule> filteredRules_1 = new ArrayList<>(); // All rules that match aYear
        List<JCNAClassRule> filteredRules_2 = new ArrayList<>(); // All rules that match aYear AND aModel
        List<JCNAClassRule> filteredRules_3 = new ArrayList<>(); // All rules that match aYear AND aModel AND aDivision
        List<JCNAClassRule> filteredRules_final = new ArrayList<>(); // All rules that match aYear AND aModel AND aDivision AND aDescriptor
        List<String> filteredList = new ArrayList<>();

        if("All".equals(aYear)){
            for(JCNAClassRule jcr: theJCNAClassRules.getJCNAClassRules()){
                filteredRules_1.add(jcr);
            }
        } else {
            for(JCNAClassRule jcr: theJCNAClassRules.getJCNAClassRules()){
                int fy = jcr.getFirstyear();
                int ly = jcr.getLastyear();
                int yr = Integer.parseInt(aYear);
                if(yr >= fy && yr <= ly) filteredRules_1.add(jcr);
            }
        }

        if("All".equals(aModel)){
            for(JCNAClassRule jcr: filteredRules_1){
                filteredRules_2.add(jcr);
            }
        } else {
            for(JCNAClassRule jcr: filteredRules_1){
                String mn = jcr.getModelName();
                if(mn.equals(aModel)) filteredRules_2.add(jcr);
            }
        }
        
        if("All".equals(aDivision)){
            for(JCNAClassRule jcr: filteredRules_2){
                filteredRules_3.add(jcr);
            }
        } else {
            for(JCNAClassRule jcr: filteredRules_2){
                String div = jcr.getDivision();
                if(div.equals(aDivision)) filteredRules_3.add(jcr);
            }
        }

        if("All".equals(aDescriptor)){
            for(JCNAClassRule jcr: filteredRules_3){
                filteredRules_final.add(jcr);
            }
        } else {
            for(JCNAClassRule jcr: filteredRules_3){
                String desc1 = jcr.getDescriptor_1();
                String desc2 = jcr.getDescriptor_2();
                String desc3 = jcr.getDescriptor_3();
                if(desc1.equals(aDescriptor) || desc2.equals(aDescriptor) || desc3.equals(aDescriptor)) filteredRules_final.add(jcr);
            }
        }
        //
        // Now format construct 
        //
        //String displayText = "[none]";
        String displayText;
        for(JCNAClassRule jcr: filteredRules_final){
            //if(displayText == "[none]") displayText = "";
            displayText = "";
            String classname = jcr.getClassName();
            String modelname = jcr.getModelName();
            String desc =jcr.getDescriptor_1();
            String descriptor_1;
            descriptor_1 = (desc != null) ? desc : "";
            desc = jcr.getDescriptor_2();
            String descriptor_2;
            descriptor_2 = (desc != null) ? desc : "";
            desc = jcr.getDescriptor_3();
            String descriptor_3;
            descriptor_3 = (desc != null) ? desc : "";
            int firstyear = jcr.getFirstyear();
            int lastyear = jcr.getLastyear();
            displayText = displayText + "Class: " + classname + " Model: " + modelname + " ";
            String descriptors = "";
            if(descriptor_1 != "" || descriptor_2 != ""  || descriptor_3 != "" )  descriptors =  "Descriptors: ";
            if(descriptor_1 != "") descriptors = descriptors + descriptor_1;
            if(descriptor_2 != "") descriptors = descriptors + " " + descriptor_2;
            if(descriptor_3 != "") descriptors = descriptors + " " + descriptor_3;
            displayText = displayText + " " + descriptors + " Years: " + firstyear + "-" + lastyear + "\n";
            if(!displayText.isEmpty()) filteredList.add(displayText);
        }
        //if(displayText.isEmpty()) {txtArea.setText("[no matches for user choices]");} else {txtArea.setText(displayText);}
        //if(displayText.isEmpty() || "[none]".equals(displayText)) {txtArea.setText("[no matches for user choices]");} else {txtArea.setText(displayText);}
        return filteredList;
    }
    private void filter(Connection aConn, String aYear, String aModel, String aDivision, String aDescriptor, String aRulesCopy){
        String q;
        try {
            if(aYear != "All"){
                int y = Integer.parseInt(aYear);
                q = "delete from " + aRulesCopy  + " where firstyear > " + y + " or lastyear < " + y + ";";
                Statement stmt_y = aConn.createStatement();
                stmt_y.executeUpdate(q);
                stmt_y.close();
            }
            if(aModel != "All"){
                q = "delete from " + aRulesCopy  + " where modelname not like '" + aModel + "';";
                Statement stmt_m = aConn.createStatement();
                stmt_m.executeUpdate(q);
                stmt_m.close();
            }
            if(aDivision != "All"){
                q = "delete from " + aRulesCopy  + " where division not like '" + aDivision + "';";
                Statement stmt_d = aConn.createStatement();
                stmt_d.executeUpdate(q);
                stmt_d.close();
            }
            if(aDescriptor != "All"){
                q = "delete from " + aRulesCopy  + " where descriptor_1 not like '" + aDescriptor + "' and descriptor_2 not like '" + aDescriptor + "' and descriptor_3 not like '" + aDescriptor + "';";
                Statement stmt_des = aConn.createStatement();
                stmt_des.executeUpdate(q);
                stmt_des.close();
            }
           
            aConn.setAutoCommit(true);
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        String displayText = "[None]";
        String d;
        q = "select * from " + aRulesCopy + " ;";
        try{
            Statement stmt_filtered = aConn.createStatement();
            ResultSet rs_filtered = stmt_filtered.executeQuery(q); 
            while (rs_filtered.next()) {
                if(displayText == "[None]") displayText = "";
                String classname = rs_filtered.getString("classname");
                String modelname = rs_filtered.getString("modelname");
                d = rs_filtered.getString("descriptor_1");
                String descriptor_1;
                if(d != null){
                    descriptor_1 = d;
                } else {
                    descriptor_1 = "";
                }
                d = rs_filtered.getString("descriptor_2");
                String descriptor_2;
                if(d != null){
                    descriptor_2 = d;
                } else {
                    descriptor_2 = "";
                }
                d = rs_filtered.getString("descriptor_3");
                String descriptor_3;
                if(d != null){
                    descriptor_3 = d;
                } else {
                    descriptor_3 = "";
                }

                int firstyear = rs_filtered.getInt("firstyear");
                int lastyear = rs_filtered.getInt("lastyear");
                displayText = displayText + "Class: " + classname + " Model: " + modelname + " ";
                String descriptors = "";
                //if((descriptor_1 != "") or (descriptor_2 != "") or (descriptor_3 != "")) { descriptors =  "Descriptors: ";} 
                if(descriptor_1 != "" || descriptor_2 != ""  || descriptor_3 != "" )  descriptors =  "Descriptors: ";
                if(descriptor_1 != "") descriptors = descriptors + descriptor_1;
                if(descriptor_2 != "") descriptors = descriptors + " " + descriptor_2;
                if(descriptor_3 != "") descriptors = descriptors + " " + descriptor_3;
                displayText = displayText + " " + descriptors + " Years: " + firstyear + "-" + lastyear + "\n";
                if(displayText.isEmpty()) {txtArea.setText("[no matches for user choices]");} else {txtArea.setText(displayText);}
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        txtArea.setText(displayText);
    }
    
    public JCNAClassRules_2 GetJCNAClassRules(){
        return theJCNAClassRules;
    } 
    
    /**
     * @param args the command line arguments
     */
   
    public static void main(String args[]) {
        // Set the Nimbus look and feel 
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
            java.util.logging.Logger.getLogger(JCNAClassChooserGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JCNAClassChooserGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JCNAClassChooserGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JCNAClassChooserGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new JCNAClassChooserGUI(true, null).setVisible(true);
            }
        });
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser ConcoursDBFileChooser;
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuItem LoadMenuItem;
    private javax.swing.JMenuItem OpenMenuItem;
    private javax.swing.JFileChooser RulesCSVFileChooser;
    private javax.swing.JButton btnFinished;
    private javax.swing.JButton btnLookup;
    private javax.swing.JComboBox cboDescriptor;
    private javax.swing.JComboBox cboDivision;
    private javax.swing.JComboBox cboModel;
    private javax.swing.JComboBox cboYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration//GEN-END:variables
}
