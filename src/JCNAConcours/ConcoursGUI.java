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

//import editJA.CreateWindscreenPlacards;
import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import editJA.JudgeAssignDialog;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.table.JTableHeader;
import org.ini4j.Wini;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entries;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.LoadJCNAClassRulesFromCSV;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.LoadJCNAClassesTableFromCSV;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.addNewColumnsToUserSettingsTable;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.checkCSVClassNameForm;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.checkCSVStructure;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.checkColNamesDbUtils;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.checkMasterPersonnelTableStructure;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.checkMasterJaguarTableStructure;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.SchedulingInterfaceJava;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.getLinesFromCSV;
import us.efsowell.concours.lib.MyJavaUtils;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.getJCNAClassFieldFromCSV;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.checkUserSettingsColNamesDbUtils;




/**
 *
 * @author Ed Sowell
 */
public class ConcoursGUI extends javax.swing.JFrame {

    public static  Concours theConcours; // making this a Class member allows references to it to be with the Class Name rather than an instance, e.g. JudgeAssignGUI.theConcours
    private static final int HEADER_HEIGHT = 40;
    private static final int ROW_HEIGHT = 80;
    private static final int COLUMN_WIDTH = 90;

    private static final int TITLE_FONT_SIZE = 12; // used to Set UserSettings table
    private static final int SUBTITLE_FONT_SIZE = 8; // used to Set UserSettings table
    private static final int CELL_FONT_SIZE = 6;   // used to Set UserSettings table
    private static final int HEADER_FONT_SIZE = 6; // used to Set UserSettings table
    private static final int FOOTNOTE_FONT_SIZE = 6;// used to Set UserSettings table
    
    private static final String START_TIME = "10:00";     // used to Set UserSettings table
    private static final String TIMESLOT_INTERVAL = "20";      // used to Set UserSettings table
    private static final String TIMESLOTS_BEFORE_LUNCH = "5";      // used to Set UserSettings table
    private static final String LUNCH_INTERVAL = "60";         // used to Set UserSettings table
    
    private static final String  USER_NAME = "Unknown";
    private static final String  CONCOURS_CHAIR = "Unknown";
    private static final String  CHIEF_JUDGE = "Unknown";
    private static final Integer COMPRESSION = 4;// used to Set UserSettings table. compression is the max number of Classes when table columns are merged
    
    
    
    private String strConcoursBaseDBFile; // absolute path of the Base DB file. User browses to this file
    private String strConcoursDBFile; // absolute path of the concours DB file. 
    private String strConcoursFolder; // absolute path of the concours DB file. 
    private static File flConcoursFolder; //User browses to this folder
    private static File flConcoursDBFile; // This file is derived from the name of flConcoursFolder
    private static File flConcoursBaseDBFile; 
    private static String version;
   
    private String strNewBaseDBFile;
    private File flNewBaseDBFile;
    public static CopySaveDBFile copysaveObject;
    private static boolean boolDBFileOpened;
    private static  Color foregroundColor;
    private javax.swing.JTable jabyjudgetable;
    private javax.swing.JScrollPane jabyjudgescrollpane;
    private Connection baseMasterConnection;
    private String concoursBuilderDataPath; // This is where the concours database & other data for the run is
    //private JCNAClassRules_2 theJCNAClassRules; // Store JCNAClassRules in memory to support an in-memory filtering method
    private String concoursBuilderDocsPath; // This is where Unsers manual, the concours windscreen placards and scoresheets Forms etc. are expected to be
    private static final int NUM_SAVED_LOGS = 3;
    private static FileHandler fhLogger;  
    
    private ProgressMonitor saveBaseProgressMonitor;
    //private Task saveBaseProgressTask;
    
     //OkTextAreaDialog okTextAreaDialog;
    

    //private boolean boolJudgeAssignmentCurrent;
    private  LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase;
    
    /**
     * Creates new form ConcoursGUI
     */
    public ConcoursGUI() {
        //
        // The default folder for  concours database files specified in ConcoursBuilder.ini, stored in {userdocs}, e.g., C:\Users\Ed Sowell\Documents:
        //    Filename: "{userdocs}\ConcoursBuilder.ini"; Section: "InstallSettings"; Key: "UserDataPath"; String: "{userdocs}\My Concourses"
        //    Filename: "{userdocs}\ConcoursBuilder.ini"; Section: "InstallSettings"; Key: "DeveloperDataPath"; String: "{userdocs}\JOCBusiness\Concours"
        //    where UserDataPath is for normal users and DeveloperDataPath is for the developer. Since development is done with NetBeans,
        //    the default folder is set to DeveloperDataPath if the current working directory contains NetBeans. Otherwise, it is set to UserDataPath.
        //    
        //
        int x =0; // to see if this forces Git to Commit.
        initComponents();
        CopyConcoursMenuItem.setEnabled(false); // Set to true when theres is an Open Concours 
        // Delete the BaseDBManagerMenuItem for now
        //BaseDBManagerMenuItem.setEnabled(false);
        String strDisplayText = "";
        String strCurDir = Paths.get(".").toAbsolutePath().normalize().toString(); 
        
        strDisplayText = strDisplayText + "\n" + " Current working directory: " + strCurDir + "\n";
        //String userDocDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString(); // tricky way of getting at user/ Documents directory
        String programFilesDirectory = System.getenv("ProgramFiles");
        String strIniFilePath = null; 
        if(strCurDir.contains("NetBeansProjects")){
            // 2/12/2019
            String username = System.getProperty("user.name");
            //strIniFilePath = strCurDir + "\\CBDocs\\ConcoursBuilder.ini";
            //strIniFilePath = "D:\\DocumentsD\\JOCBusiness\\Concours\\CBDocs\\ConcoursBuilderDev.ini";
            strIniFilePath = "C:\\Users\\" + username + "\\Documents\\ConcoursDev\\CBDocs\\ConcoursBuilderDev.ini";
            //strIniFilePath = "C:\\Users\\Owner\\Documents\\JOCBusiness\\Concours\\CBDocs\\ConcoursBuilderDev.ini";
        } else{
            strIniFilePath = programFilesDirectory + "\\ConcoursBuilder\\CBDocs\\ConcoursBuilderUser.ini";
        }
        Logger preLogger = Logger.getLogger("PreConcoursBuilderLog"); // Can't use the normal logger yet...
        //okDialog("ini file path: " + strIniFilePath);
        //System.out.println("The ini file: " + strIniFileName + "\n");
        //File flIni;
        Wini ini;
        ini = null;
        try {
            ini = new Wini(new File(strIniFilePath));
        } catch (IOException ex) {
            preLogger.log(Level.SEVERE, null, ex);
            
            preLogger.info("ERROR: Could not open ConcoursBuilder ini file:" + strIniFilePath);
            okDialog("ERROR: Could not open ConcoursBuilder ini file: " + strIniFilePath );
            System.exit(-1);
        }
        String installPath = ini.get("InstallSettings", "InstallPath");
        /*
        if(strCurDir.contains("NetBeansProjects")){
            concoursBuilderDataPath = ini.get("InstallSettings", "DeveloperDataPath");
            concoursBuilderDocsPath = ini.get("InstallSettings", "DeveloperFormsPath");
        } else{
            concoursBuilderDataPath = ini.get("InstallSettings", "UserDataPath");
            concoursBuilderDocsPath = ini.get("InstallSettings", "UserFormsPath");
        }
        */
        concoursBuilderDataPath = ini.get("InstallSettings", "ConcoursBuilderDataPath");
        concoursBuilderDocsPath = ini.get("InstallSettings", "ConcoursBuilderDocsPath");
        version = ini.get("InstallSettings", "Version");
        //
        // Set the name of the logger file to ConcoursBuilderLog. 
        // 
        Logger logger = Logger.getLogger("ConcoursBuilderLog"); 
        File logFile = null;
        // Because the stack algorithm requires an initial ConcoursBuilder_0.log, the directory is first checked and an empty one is created if absent.
        //
        // If ConcoursBuilderLog_0 doesn't exist, create it. Typically, this happens only in the first run after INSTALLATION of ConcoursBuilder.
        // However, it must be checked because the user might have deleated it.
        //
        String strFileFullPath = concoursBuilderDataPath + "\\ConcoursBuilderLog_" + 0 + ".log";
        System.out.println("Full path to ConcoursBuilderLog_0 : " + strFileFullPath);
        logFile = new File(strFileFullPath);
        if(!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, "ERROR: ", ex);
                System.exit(-1);
            }
            try {            
                Files.write(Paths.get(logFile.getPath()), "Empty log file".getBytes(), StandardOpenOption.WRITE);
            } catch (IOException ex) {
                Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, "ERROR: ", ex);
                System.exit(-1);
            }
        }
        // Maintain a "stack" of historical ConcoursBuilder log files.
        updateLogHistory(NUM_SAVED_LOGS,  logFile);  
        fhLogger = null;
        try {
            fhLogger = new FileHandler(logFile.toString(), false);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, "ERROR: ", ex);
            System.exit(-1);
        }
        logger.addHandler(fhLogger);
        logger.setLevel(Level.ALL);
        SimpleFormatter formatter = new SimpleFormatter();
        fhLogger.setFormatter(formatter);
        
       /*
        try {
            //fhLogger = new FileHandler(concoursBuilderDataPath + "\\ConcoursBuilderLog_%g.log", Integer.MAX_VALUE, 2, false);
            fhLogger = new FileHandler(concoursBuilderDataPath + "\\ConcoursBuilderLog_%g.log", 100000, 3, false);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.addHandler(fhLogger);
        SimpleFormatter formatter = new SimpleFormatter();  
        fhLogger.setFormatter(formatter);  
        */
        logger.info("ConcoursBuilder started with a new Concours. User: " + System.getProperty("user.name") + " Current working directory: " + strCurDir);

        
        // added customization...
        ConcoursOpenFileChooser.setCurrentDirectory(new File(concoursBuilderDataPath)); 
        ConcoursOpenFileChooser.setDialogTitle("Open JCNA Concourse");
        ConcoursOpenFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ConcoursOpenFileChooser.setAcceptAllFileFilterUsed(false);

        
        theConcours = new Concours(logger, NUM_SAVED_LOGS); // Constructs theConcours with logger
        theConcours.SetInstallationPath(installPath);
        theConcours.SetConcoursBuilderDataPath(concoursBuilderDataPath);
        theConcours.SetConcoursBuilderDocsPath(concoursBuilderDocsPath);
        theConcours.SetCBVersion(version);
        theConcours.SetConcoursName("noConcoursOpen.db"); // allows detection of no Opened concours 
        foregroundColor =   JASchedule.getForeground();
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();
        logger.info("Constructed LoadSQLiteConcoursDatabase()");

        jabyjudgetable = new javax.swing.JTable();
        jabyjudgescrollpane = new javax.swing.JScrollPane();

        
        copysaveObject = new CopySaveDBFile();
        boolDBFileOpened = false;
        CopyConcoursMenuItem.setEnabled(boolDBFileOpened);
        BackupMenuItem.setEnabled(boolDBFileOpened);
        //CloseMenuItem.setEnabled(boolDBFileOpened);
        EditMenu.setEnabled(boolDBFileOpened);
        DisplayMenu.setEnabled(boolDBFileOpened);
        ToolsMenu.setEnabled(boolDBFileOpened);
        OpenMenuItem.setEnabled(!boolDBFileOpened);
        NewMenuItem.setEnabled(!boolDBFileOpened);
        JASchedule.setForeground(foregroundColor);
        
        
                // to adjust header height
        jabyjudgetable.setTableHeader(new JTableHeader(jabyjudgetable.getColumnModel()) {
        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = HEADER_HEIGHT;
            return d;
        }
        });
        //
        // Would like to keep these tabs but have to figure out how to populated them without
        // duplicating code in JudgeAssignGUI
        //
        theTabbedPane.removeTabAt(1); // Removes spnlSchedByClass
        theTabbedPane.removeTabAt(1); // Removes spnlSchedByJudge 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConcoursOpenFileChooser = new javax.swing.JFileChooser();
        SaveAsFileChooser = new javax.swing.JFileChooser();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        SaveBaseDBAsFileChooser = new javax.swing.JFileChooser();
        CSVFileChooser = new javax.swing.JFileChooser();
        ConcoursBaseDBFileChooser = new javax.swing.JFileChooser();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        theTabbedPane = new javax.swing.JTabbedPane();
        spnlTextReports = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();
        spnlSchedByClass = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        spnlSchedByJudge = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        MainMenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        NewMenuItem = new javax.swing.JMenuItem();
        OpenMenuItem = new javax.swing.JMenuItem();
        BackupRestoreMenu = new javax.swing.JMenu();
        BackupMenuItem = new javax.swing.JMenuItem();
        RestoreMenuItem = new javax.swing.JMenuItem();
        CopyConcoursMenuItem = new javax.swing.JMenuItem();
        DeleteConcoursMenuItem = new javax.swing.JMenuItem();
        SaveBaseMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ExportMenu = new javax.swing.JMenu();
        ExportEntriesMenuItem = new javax.swing.JMenuItem();
        EntriesCSVMenuItem = new javax.swing.JMenuItem();
        ExportJudgesMenuItem = new javax.swing.JMenuItem();
        ExportMasterPersonnelAndJaguarsMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        ExitMenuItem = new javax.swing.JMenuItem();
        EditMenu = new javax.swing.JMenu();
        MasterPersonMenu = new javax.swing.JMenu();
        AddMPMenuItem = new javax.swing.JMenuItem();
        EditMPMenuItem = new javax.swing.JMenuItem();
        RemoveMasterPersonMenuItem = new javax.swing.JMenuItem();
        EntryMenu = new javax.swing.JMenu();
        AddEntryMenuItem = new javax.swing.JMenuItem();
        ChangeEntryMenuItem = new javax.swing.JMenuItem();
        RemoveEntryMenuItem = new javax.swing.JMenuItem();
        JudgeMenu = new javax.swing.JMenu();
        AddJudgeMenuItem = new javax.swing.JMenuItem();
        ChangeJudgeMenuItem = new javax.swing.JMenuItem();
        RemoveJudgeMenuItem = new javax.swing.JMenuItem();
        judgingTeamsMenu = new javax.swing.JMenu();
        AddJudgingTeamMenuItem = new javax.swing.JMenuItem();
        ChangeJudgingTeamMenuItem = new javax.swing.JMenuItem();
        RemoveJudgingTeamMenuItem = new javax.swing.JMenuItem();
        JASchedule = new javax.swing.JMenuItem();
        EditJASchedule = new javax.swing.JMenuItem();
        DisplayMenu = new javax.swing.JMenu();
        DisplayStatsMenuItem = new javax.swing.JMenuItem();
        DisplayEntriesMenuItem = new javax.swing.JMenuItem();
        DisplayJudgesMenuItem = new javax.swing.JMenuItem();
        DisplayPersonnelMenuItem = new javax.swing.JMenuItem();
        DisplayCustomJudgingTeamsMenuItem = new javax.swing.JMenuItem();
        DisplayClassJudgeCandidates = new javax.swing.JMenuItem();
        DisplayMasterPersonnelMenuItem = new javax.swing.JMenuItem();
        ToolsMenu = new javax.swing.JMenu();
        UserSettingsMenuItem = new javax.swing.JMenuItem();
        Placards = new javax.swing.JMenuItem();
        ScoreSheets = new javax.swing.JMenuItem();
        JCNAClassFinder = new javax.swing.JMenuItem();
        LoadJCNAClassesAndRulesMenuItem = new javax.swing.JMenuItem();
        HelpMenu = new javax.swing.JMenu();
        UsersManualmenuOption = new javax.swing.JMenuItem();
        AboutMenuItem = new javax.swing.JMenuItem();
        AcknowledgmentsMenuItem = new javax.swing.JMenuItem();

        ConcoursOpenFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConcoursOpenFileChooserActionPerformed(evt);
            }
        });

        SaveAsFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        SaveAsFileChooser.setDialogTitle("Save Concours As");

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JCNA Concours Builder");

        theTabbedPane.setToolTipText("");

        txtArea.setColumns(20);
        txtArea.setRows(5);
        spnlTextReports.setViewportView(txtArea);

        theTabbedPane.addTab("Text displays", spnlTextReports);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        spnlSchedByClass.setViewportView(jTable1);

        theTabbedPane.addTab("Schedule by Class", spnlSchedByClass);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        spnlSchedByJudge.setViewportView(jTable2);

        theTabbedPane.addTab("Schedule by Judge", spnlSchedByJudge);

        FileMenu.setText("File");
        FileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileMenuActionPerformed(evt);
            }
        });

        NewMenuItem.setText("New Concours");
        NewMenuItem.setToolTipText("Create a New Concours");
        NewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(NewMenuItem);

        OpenMenuItem.setText("Open Concours");
        OpenMenuItem.setToolTipText("Open and existing Concours");
        OpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(OpenMenuItem);

        BackupRestoreMenu.setText("Backup or Restore Concours");

        BackupMenuItem.setText("Backup");
        BackupMenuItem.setToolTipText("Backup open Concours");
        BackupMenuItem.setEnabled(false);
        BackupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackupMenuItemActionPerformed(evt);
            }
        });
        BackupRestoreMenu.add(BackupMenuItem);

        RestoreMenuItem.setText("Restore");
        RestoreMenuItem.setToolTipText("Restore a Concours from Backup file");
        RestoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RestoreMenuItemActionPerformed(evt);
            }
        });
        BackupRestoreMenu.add(RestoreMenuItem);

        FileMenu.add(BackupRestoreMenu);

        CopyConcoursMenuItem.setText("Copy Concours");
        CopyConcoursMenuItem.setToolTipText("Copy the Open Concours to a new Concours");
        CopyConcoursMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CopyConcoursMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(CopyConcoursMenuItem);

        DeleteConcoursMenuItem.setText("Delete Concours");
        DeleteConcoursMenuItem.setToolTipText("Delete a concours not currently open");
        DeleteConcoursMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteConcoursMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(DeleteConcoursMenuItem);

        SaveBaseMenuItem.setText("Save Active Base Database");
        SaveBaseMenuItem.setToolTipText("Save the Base database including any modifications made.");
        SaveBaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveBaseMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(SaveBaseMenuItem);
        FileMenu.add(jSeparator1);

        ExportMenu.setText("Export");
        ExportMenu.setToolTipText("Export Concours Entries to text file");
        ExportMenu.setEnabled(false);

        ExportEntriesMenuItem.setText("Entries");
        ExportEntriesMenuItem.setToolTipText("Export Concours Entries as text file");
        ExportEntriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportEntriesMenuItemActionPerformed(evt);
            }
        });
        ExportMenu.add(ExportEntriesMenuItem);

        EntriesCSVMenuItem.setText("Entries (Detail CSV)");
        EntriesCSVMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EntriesCSVMenuItemActionPerformed(evt);
            }
        });
        ExportMenu.add(EntriesCSVMenuItem);

        ExportJudgesMenuItem.setText("Judges");
        ExportJudgesMenuItem.setToolTipText("Export Concours Judges as text file");
        ExportJudgesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportJudgesMenuItemActionPerformed(evt);
            }
        });
        ExportMenu.add(ExportJudgesMenuItem);

        ExportMasterPersonnelAndJaguarsMenuItem.setText("Master Personnel & Jaguars");
        ExportMasterPersonnelAndJaguarsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportMasterPersonnelAndJaguarsMenuItemActionPerformed(evt);
            }
        });
        ExportMenu.add(ExportMasterPersonnelAndJaguarsMenuItem);

        FileMenu.add(ExportMenu);
        FileMenu.add(jSeparator2);

        ExitMenuItem.setText("Exit");
        ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(ExitMenuItem);

        MainMenuBar.add(FileMenu);

        EditMenu.setText("Edit");
        EditMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditMenuActionPerformed(evt);
            }
        });

        MasterPersonMenu.setText("Master Personnel");
        MasterPersonMenu.setToolTipText("Add a new Master Person & Jaguar stable");

        AddMPMenuItem.setText("Add");
        AddMPMenuItem.setToolTipText("Add a new person and Jaguar stable to Master Personnel.");
        AddMPMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddMPMenuItemActionPerformed(evt);
            }
        });
        MasterPersonMenu.add(AddMPMenuItem);

        EditMPMenuItem.setText("Change");
        EditMPMenuItem.setToolTipText("Change a Master Person and Jaguar stable");
        EditMPMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditMPMenuItemActionPerformed(evt);
            }
        });
        MasterPersonMenu.add(EditMPMenuItem);

        RemoveMasterPersonMenuItem.setText("Remove");
        RemoveMasterPersonMenuItem.setToolTipText("Remove a master Person");
        RemoveMasterPersonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveMasterPersonMenuItemActionPerformed(evt);
            }
        });
        MasterPersonMenu.add(RemoveMasterPersonMenuItem);

        EditMenu.add(MasterPersonMenu);

        EntryMenu.setText("Concours Entry");
        EntryMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EntryMenuActionPerformed(evt);
            }
        });

        AddEntryMenuItem.setText("Add");
        AddEntryMenuItem.setToolTipText("Add a Concours Entry from Master Personnel ");
        AddEntryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddEntryMenuItemActionPerformed(evt);
            }
        });
        EntryMenu.add(AddEntryMenuItem);

        ChangeEntryMenuItem.setText("Change");
        ChangeEntryMenuItem.setToolTipText("Make limited changes to a Concours Entry");
        ChangeEntryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeEntryMenuItemActionPerformed(evt);
            }
        });
        EntryMenu.add(ChangeEntryMenuItem);

        RemoveEntryMenuItem.setText("Remove");
        RemoveEntryMenuItem.setToolTipText("Remove an Entry");
        RemoveEntryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveEntryMenuItemActionPerformed(evt);
            }
        });
        EntryMenu.add(RemoveEntryMenuItem);

        EditMenu.add(EntryMenu);

        JudgeMenu.setText("Concours Judge");

        AddJudgeMenuItem.setText("Add");
        AddJudgeMenuItem.setToolTipText("Add a Concours Judge from Master Personnel");
        AddJudgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddJudgeMenuItemActionPerformed(evt);
            }
        });
        JudgeMenu.add(AddJudgeMenuItem);

        ChangeJudgeMenuItem.setText("Change");
        ChangeJudgeMenuItem.setToolTipText("Change Concours Judge Class preferences");
        ChangeJudgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeJudgeMenuItemActionPerformed(evt);
            }
        });
        JudgeMenu.add(ChangeJudgeMenuItem);

        RemoveJudgeMenuItem.setText("Remove");
        RemoveJudgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveJudgeMenuItemActionPerformed(evt);
            }
        });
        JudgeMenu.add(RemoveJudgeMenuItem);

        EditMenu.add(JudgeMenu);

        judgingTeamsMenu.setText("Custom Judging teams");
        judgingTeamsMenu.setToolTipText("Preassign Concours Judges to a Class");
        judgingTeamsMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                judgingTeamsMenuMenuSelected(evt);
            }
        });

        AddJudgingTeamMenuItem.setText("Add");
        AddJudgingTeamMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddJudgingTeamMenuItemActionPerformed(evt);
            }
        });
        judgingTeamsMenu.add(AddJudgingTeamMenuItem);

        ChangeJudgingTeamMenuItem.setText("Change");
        ChangeJudgingTeamMenuItem.setToolTipText("Edit a Custom Judging team for a class");
        ChangeJudgingTeamMenuItem.setEnabled(false);
        ChangeJudgingTeamMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeJudgingTeamMenuItemActionPerformed(evt);
            }
        });
        judgingTeamsMenu.add(ChangeJudgingTeamMenuItem);

        RemoveJudgingTeamMenuItem.setText("Remove");
        RemoveJudgingTeamMenuItem.setToolTipText("Remove a Custom Judging Team");
        RemoveJudgingTeamMenuItem.setEnabled(false);
        RemoveJudgingTeamMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveJudgingTeamMenuItemActionPerformed(evt);
            }
        });
        judgingTeamsMenu.add(RemoveJudgingTeamMenuItem);

        EditMenu.add(judgingTeamsMenu);

        JASchedule.setText("Build Judge assignments & schedule");
        JASchedule.setToolTipText("Assign Judges to all Classes and generate a judging schedule");
        JASchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JAScheduleActionPerformed(evt);
            }
        });
        EditMenu.add(JASchedule);

        EditJASchedule.setText("View/Edit Judge assignment & schedule");
        EditJASchedule.setToolTipText("Open a new window and display the generated assignments & schedule");
        EditJASchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditJAScheduleActionPerformed(evt);
            }
        });
        EditMenu.add(EditJASchedule);

        MainMenuBar.add(EditMenu);

        DisplayMenu.setText("Display");

        DisplayStatsMenuItem.setText("Stats");
        DisplayStatsMenuItem.setToolTipText("Display number of Concours Entries, Judges, etc.");
        DisplayStatsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayStatsMenuItemActionPerformed(evt);
            }
        });
        DisplayMenu.add(DisplayStatsMenuItem);

        DisplayEntriesMenuItem.setText("Entries");
        DisplayEntriesMenuItem.setToolTipText("Dislpay all Concours Entries");
        DisplayEntriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayEntriesMenuItemActionPerformed(evt);
            }
        });
        DisplayMenu.add(DisplayEntriesMenuItem);

        DisplayJudgesMenuItem.setText("Judges");
        DisplayJudgesMenuItem.setToolTipText("Display Concours Judges");
        DisplayJudgesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayJudgesMenuItemActionPerformed(evt);
            }
        });
        DisplayMenu.add(DisplayJudgesMenuItem);

        DisplayPersonnelMenuItem.setText("Personnel");
        DisplayPersonnelMenuItem.setToolTipText("Display Concours Entries & Judges");
        DisplayPersonnelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayPersonnelMenuItemActionPerformed(evt);
            }
        });
        DisplayMenu.add(DisplayPersonnelMenuItem);

        DisplayCustomJudgingTeamsMenuItem.setText("Custom Judging Teams");
        DisplayCustomJudgingTeamsMenuItem.setToolTipText("Display all preassigned Judging teams");
        DisplayCustomJudgingTeamsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayCustomJudgingTeamsMenuItemActionPerformed(evt);
            }
        });
        DisplayMenu.add(DisplayCustomJudgingTeamsMenuItem);

        DisplayClassJudgeCandidates.setText("Class Judge Candidates");
        DisplayClassJudgeCandidates.setToolTipText("Displays candidate Judges for every Concours Class based on Judging preferences or preassigned custom teams.");
        DisplayClassJudgeCandidates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayClassJudgeCandidatesActionPerformed(evt);
            }
        });
        DisplayMenu.add(DisplayClassJudgeCandidates);

        DisplayMasterPersonnelMenuItem.setText("Master Personnel");
        DisplayMasterPersonnelMenuItem.setToolTipText("Display all Master Personnel");
        DisplayMasterPersonnelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayMasterPersonnelMenuItemActionPerformed(evt);
            }
        });
        DisplayMenu.add(DisplayMasterPersonnelMenuItem);

        MainMenuBar.add(DisplayMenu);

        ToolsMenu.setText("Tools");
        ToolsMenu.setToolTipText("");

        UserSettingsMenuItem.setText("User settings");
        UserSettingsMenuItem.setToolTipText("Edit User settings");
        UserSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserSettingsMenuItemActionPerformed(evt);
            }
        });
        ToolsMenu.add(UserSettingsMenuItem);

        Placards.setText("Create Windscreen Placards");
        Placards.setToolTipText("Create windscreen placards for all Entries");
        Placards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PlacardsActionPerformed(evt);
            }
        });
        ToolsMenu.add(Placards);

        ScoreSheets.setText("Create Score Sheets");
        ScoreSheets.setToolTipText("Create scoresheets for all Entries");
        ScoreSheets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ScoreSheetsActionPerformed(evt);
            }
        });
        ToolsMenu.add(ScoreSheets);

        JCNAClassFinder.setText("JCNA Class Finder");
        JCNAClassFinder.setToolTipText("Look up JCNA Class given Year & Model.");
        JCNAClassFinder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JCNAClassFinderActionPerformed(evt);
            }
        });
        ToolsMenu.add(JCNAClassFinder);

        LoadJCNAClassesAndRulesMenuItem.setText("Load JCNA Classes & Rules");
        LoadJCNAClassesAndRulesMenuItem.setToolTipText("Load JCNA Classes & Rules after changes by Rules Committee");
        LoadJCNAClassesAndRulesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadJCNAClassesAndRulesMenuItemActionPerformed(evt);
            }
        });
        ToolsMenu.add(LoadJCNAClassesAndRulesMenuItem);

        MainMenuBar.add(ToolsMenu);

        HelpMenu.setText("Help");

        UsersManualmenuOption.setText("User's Manual");
        UsersManualmenuOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsersManualmenuOptionActionPerformed(evt);
            }
        });
        HelpMenu.add(UsersManualmenuOption);

        AboutMenuItem.setText("About ConcoursBuilder");
        AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutMenuItemActionPerformed(evt);
            }
        });
        HelpMenu.add(AboutMenuItem);

        AcknowledgmentsMenuItem.setText("Acknowledgments");
        AcknowledgmentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcknowledgmentsMenuItemActionPerformed(evt);
            }
        });
        HelpMenu.add(AcknowledgmentsMenuItem);

        MainMenuBar.add(HelpMenu);

        setJMenuBar(MainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(theTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 901, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenMenuItemActionPerformed
        //
        // load from database file
        //
        boolean res = true;
        Connection conn;
        String strConcoursFolderPath = "";
        String strSelectedConcoursName = "";
        ConcoursChooserDialog dialog = new ConcoursChooserDialog(new javax.swing.JFrame(), true,  theConcours, "Open", concoursBuilderDataPath);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
            }
        });
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog.
        //dialog.setVisible(rootPaneCheckingEnabled);
        int resp = dialog.showOpenDialog(); // will be either JFileChooser.APPROVE_OPTION or JFileChooser.CANCEL_OPTION
        
        if (resp == JFileChooser.APPROVE_OPTION) {
            strConcoursFolderPath = dialog.getSelectedFolderFullPath();
            strSelectedConcoursName = dialog.getSelectedFolderName();
            strConcoursDBFile = strConcoursFolderPath + "\\" + strSelectedConcoursName + ".db";
            
              
           File dir = new File(strConcoursFolderPath);
           boolean dirExist = dir.exists() && dir.isDirectory();
           if(dirExist) {
               String strDbFile = dialog.getSelectedFolderName() + ".db";
               //File flConcoursDBFile = new File(dir + "\\" + strDbFile);
               flConcoursDBFile = new File(dir + "\\" + strDbFile);
                        
                //
                // Create a copy in case user decides to not save the revised Concours DB file.
                // 
                theConcours.GetLogger().info("Prepare to copy : " + flConcoursDBFile.toString() + " with User: " + System.getProperty("user.name"));    
                //okDialog("Prepare to copy : " + flConcoursDBFile.toString() + " with User: " + System.getProperty("user.name"));    
                String strBackupFileName = strDbFile.replace(".db", "Backup.db");
                File backup = new File(dir + "\\" + strBackupFileName);
                try {
                    CopySaveDBFile.copyDBFile(flConcoursDBFile, backup, theConcours.GetLogger());
                    //theConcours.GetLogger().info(strBackupFileName + " exists " + dir + " folder.");
                    theConcours.GetLogger().info(strBackupFileName + " created in folder " + dir);
                } catch (IOException ex) {
                    theConcours.GetLogger().info("ERROR: IO exception. Failed copy " + strDbFile + "  to its backup file.");
                    theConcours.GetLogger().log(Level.SEVERE, "ERROR: IO exception. Failed copy " + strDbFile + "  to its backup file.", ex);
                    System.exit(-1);
                }
                // This for backward compatibility
                // Create Folders for theScoresheets & windscreen placards if they don't exist... 
                // First, Placard folders
                File filePlacards = new File(strConcoursFolderPath + "\\Placards");
                if(!(filePlacards.exists() && filePlacards.isDirectory()))     (filePlacards).mkdir();
                File filePlacardsChampionSpecial = new File(strConcoursFolderPath + "\\Placards\\ChampionSpecial");
                if(!(filePlacardsChampionSpecial.exists() && filePlacardsChampionSpecial.isDirectory()))     (filePlacardsChampionSpecial).mkdir();
                File filePlacardsDriven = new File(strConcoursFolderPath +  "\\Placards\\Driven");
                if(!(filePlacardsDriven.exists() && filePlacardsDriven.isDirectory()))    (filePlacardsDriven).mkdir();
                File filePlacardsDisplay = new File(strConcoursFolderPath +  "\\Placards\\Display");
                if(!(filePlacardsDisplay.exists() && filePlacardsDisplay.isDirectory()))    (filePlacardsDisplay).mkdir();
                // Now, Scoresheet folders
                File fileScoresheets = new File(strConcoursFolderPath + "\\Scoresheets");
                if(!(fileScoresheets.exists() && fileScoresheets.isDirectory()))     (fileScoresheets).mkdir();
                File fileScoresheetsChampionSpecial = new File(strConcoursFolderPath + "\\Scoresheets\\ChampionSpecial");
                if(!(fileScoresheetsChampionSpecial.exists() && fileScoresheetsChampionSpecial.isDirectory()))     (fileScoresheetsChampionSpecial).mkdir();
                File fileScoresheetsDriven = new File(strConcoursFolderPath +  "\\Scoresheets\\Driven");
                if(!(fileScoresheetsDriven.exists() && fileScoresheetsDriven.isDirectory()))    (fileScoresheetsDriven).mkdir();

           } else{
               // should not happen since it would not have been available for selection if nonexistant
               okDialog("ERROR: Problem backing up the database file in OpenMenuItemActionPerformed. " + dir + " or is not a Folder");
               theConcours.GetLogger().info("ERROR: Problem backing up the database file in OpenMenuItemActionPerformed. " + dir + " or is not a Folder");
               System.exit(-1);
           }
            
            theConcours.SetThePath(strConcoursDBFile);
            theConcours.GetLogger().info("Opened Concours " + strConcoursDBFile);
            conn = null;
            try {
                Class.forName("org.sqlite.JDBC");
                String strConn;
                strConn = "jdbc:sqlite:" + strConcoursDBFile ;  // PROBLEM HERE IF OPENING.... strConcoursDBFile == NULL
                conn = DriverManager.getConnection(strConn);
                if(conn == null){
                    String msg = "Could not connect to strConcoursDBFile.";
                    okDialog(msg)        ;
                    theConcours.GetLogger().info( msg);
                    System.exit(-1);
                }
                conn.createStatement().execute("PRAGMA foreign_keys = ON");
                theConcours.SetConnection(conn) ;
                
            } catch ( ClassNotFoundException | SQLException e ) {
                theConcours.GetLogger().info( e.getClass().getName() + ": " + e.getMessage() );
                okDialog("SQL exception in while opening " + strConcoursDBFile);
                System.exit(-1);
            }
            // If user has used Windows to create a copy of a Concours the the Windows name will differ from what's in the database.
            // This will change the name in the database to agree with the Windows name.
            // The importance of this is so the name at the top of the JA schedule in the pdf files will be correct.
            String concoursnameDB = loadSQLiteConcoursDatabase.GetSettingsTableConcoursName(conn);
            if(!(concoursnameDB.equals(strSelectedConcoursName + ".db"))){
                String msg = "Concours name from database is " + concoursnameDB + ". Will reset it to the Windows file name " + strSelectedConcoursName + ".db";
                //okDialog(msg);
                theConcours.GetLogger().info(msg);
                try {
                    LoadSQLiteConcoursDatabase.SetSettingsTableConcoursName(conn, strSelectedConcoursName + ".db");
                } catch (SQLException ex) {
                    theConcours.GetLogger().log(Level.SEVERE, "SQLException: SetSettingsTableConcoursName failed", ex);
                    System.exit(-1);
                }
            }
            boolDBFileOpened = true;
            CopyConcoursMenuItem.setEnabled(boolDBFileOpened);
            BackupMenuItem.setEnabled(boolDBFileOpened);
            NewMenuItem.setEnabled(!boolDBFileOpened);
            OpenMenuItem.setEnabled(!boolDBFileOpened);
            RestoreMenuItem.setEnabled(!boolDBFileOpened);
            EditMenu.setEnabled(boolDBFileOpened);
            DisplayMenu.setEnabled(boolDBFileOpened);
            ToolsMenu.setEnabled(boolDBFileOpened);
            JASchedule.setForeground(foregroundColor);
            //ExportEntriesMenuItem.setEnabled(boolDBFileOpened);
            //ExportJudgesMenuItem.setEnabled(boolDBFileOpened);
            ExportMenu.setEnabled(boolDBFileOpened);
            
            theConcours.GetLogger().info("Opened concours database " + strConcoursDBFile + " successfully");
            //System.out.println("Opened database " + strConcoursDBFile + " successfully");
            
            theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", theConcours.GetLogger());
            theConcours.GetLogger().info("Loaded JCNA Classes successfully");
            
            boolean classRulesOK = theConcours.GetJCNAClassRules().LoadJCNAClassRulesDB(conn, "JCNAClassRules", theConcours.GetLogger());
            if(!classRulesOK){
                okDialog("ERROR: LoadJCNAClassRulesDB failed in OpenMenuItemActionPerformed()");
                 theConcours.GetLogger().info("ERROR: LoadJCNAClassRulesDB failed in OpenMenuItemActionPerformed()");
                 System.exit(-1);
            } else{
                theConcours.GetLogger().info("Loaded JCNA Class Rules successfully");
            }
            // 09/19/2018
            // Check the MasterPersonnel database structure
            //
            boolean isOk = checkMasterPersonnelTableStructure(conn, theConcours);
            if(!isOk){
                String msg = "Bad table Masterpersonnel structure in OpenMenuItemActionPerforme. Database is incompatible with this version of ConcoursBuilder.";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
           
            theConcours.LoadMasterPersonnelDB(conn, theConcours.GetLogger());
            theConcours.GetLogger().info("Loaded MasterPersonnel successfully");
            theConcours.LoadConcoursPersonnelDB(conn, theConcours.GetLogger());
            //*********************
            theConcours.GetLogger().info("Loaded ConcoursPersonnel successfully");
            // 09/20/2018
            // Check the MasterJaguar database structure
            //
            
            isOk = checkMasterJaguarTableStructure(conn, theConcours);
            if(!isOk){
                String msg = "Bad table MasterJaguar structure in OpenMenuItemActionPerforme. Database is incompatible with this version of ConcoursBuilder.";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            

            theConcours.LoadMasterJaguarDB(conn, theConcours.GetLogger());
            theConcours.GetLogger().info("Loaded MasterJaguar successfully");
            theConcours.LoadEntriesDB(conn,  theConcours.GetLogger());
            theConcours.GetLogger().info("Loaded Entries successfully");
            theConcours.LoadJudgesDB(conn, theConcours.GetLogger());
            theConcours.GetLogger().info("Loaded Judges successfully");
            // The theJudgeAssignments must be available for loading  ConcoursClasses. Otherwise, the judgelists for the Concourse classes will be empty
            loadSQLiteConcoursDatabase.ReadJudgeAssignmentDBToMem(conn, theConcours);
            theConcours.GetLogger().info("Read JudgeAssignment from database successfully");
            
            res = theConcours.LoadConcoursClassesDB(conn, theConcours,theConcours.GetLogger());
            if(!res){
                String msg = "ERROR: Concours Classes failed to load properly.\nPlease exit ConcoursBuilder and send the 3 ConcourseBuilder Log files in your Concourses folder to CB support. ";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
            }
            theConcours.GetLogger().info("Read ConcoursClasses from database successfully");
            theConcours.SetPreassignedJudgesFlag(theConcours.GetConcoursClassesObject().preassignedJudgeListsExists()); 
            ChangeJudgingTeamMenuItem.setEnabled(theConcours.GetPreassignedJudgesFlag());
            RemoveJudgingTeamMenuItem.setEnabled(theConcours.GetPreassignedJudgesFlag());

            
            theConcours.LoadOwnersDB(conn, theConcours.GetLogger());
            theConcours.GetLogger().info("Read Owners from database successfully");
            // boolJudgeAssignmentCurrent is the state when the last session with this Concours database was closed
            // true means that there is no need to build a Judge Assignment
            boolean boolJudgeAssignmentCurrent = loadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn) ; 
            theConcours.SetJudgeAssignmentCurrent(boolJudgeAssignmentCurrent); 
            String strConcoursName = loadSQLiteConcoursDatabase.GetSettingsTableConcoursName(conn);
            String frameTitle = "JCNA Concours Builder: " + strConcoursName.replace(".db", "");
            this.setTitle(frameTitle); 
            theConcours.SetConcoursName(strConcoursName); 
            // 7/30/2017
            // 
            //  checkAndFixUserSettingsTable()  is needed in order to run Concours DB files from earlier ConcoursBuilder versions
            //  before subtitlefonts, chiefjudge, & concourschair fields were added to usersettings table
            // First, check to see if the theConcours has a usersettings table, and that it has the correct structure.
            // If not, a new one is created.
            //
            theConcours.GetLogger().info("Calling check And Fix User Settings Table");
            checkAndFixUserSettingsTable(conn);                                       
            theConcours.GetLogger().info("Returned from check And Fix User Settings Table");
            //                                                                               
            // UserSettings table now ok so we can load it
            // Get User Settings from DB
            loadSQLiteConcoursDatabase.LoadConcoursUserSettingsDB(conn, theConcours, theConcours.GetLogger());
            // Calculate Lunch time, rounded up to nearest 15 minute clock position
            String startTime = theConcours.GetConcoursStartTime();
            Integer timeslotInterval = theConcours.GetConcoursTimeslotInterval();
            Integer timeslotsBeforeLunch = theConcours.GetConcoursTimeslotsBeforeLunch();
            String lunchTime = MyJavaUtils.calculateLunchtime(theConcours.GetLogger(), startTime, timeslotInterval, timeslotsBeforeLunch);
            theConcours.SetConcoursLunchTime(lunchTime);
            // Manual Editing of Judge Assignment is OK if boolJudgeAssignmentCurrent  == true.
            // If false, no edditing is allowed until a new Judge Assignment is performed. 
            EditJASchedule.setEnabled(boolJudgeAssignmentCurrent); 
            theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is initialized here and then again after any change

            theConcours.createCarColorMap();
            // +++++ 7/14/2016
            if(!theConcours.GetJudgeAssignmentCurrent()){
                JASchedule.setForeground(Color.RED);
            }
            // +++++
            theConcours.GetLogger().info("Finished loading Concours Judge Assignments, Owners, Entries, Judges, Concours Classes, & JCNA Classes  from " + theConcours.GetThePath());
        }
    }//GEN-LAST:event_OpenMenuItemActionPerformed

  private void checkAndFixUserSettingsTable(Connection aConn){
            
            boolean userSettingsTableOk = true;
            boolean boolUserSettingsTableExists = true;
            try {
                int userSettingsTableExists = loadSQLiteConcoursDatabase.CheckUserSettingsTableExistsDbUtils(aConn, theConcours, theConcours.GetLogger());
                if(userSettingsTableExists != 1){
                    String msg = "User Settings Table does not exist. A new one table will be created using default values.";
                    okDialog(msg);
                    theConcours.GetLogger().info(msg);
                    boolUserSettingsTableExists = false;
                    userSettingsTableOk = false;
                }
            } catch (SQLException ex){
                String msg = "SQLException while checking existance of UserSettings table. Will create a new one using default values";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                userSettingsTableOk = false;
                theConcours.GetLogger().log(Level.INFO, msg, ex);
            }
            if(boolUserSettingsTableExists){
                theConcours.GetLogger().info("UserSettins table exists. Will check that it has correct columns");
                // Table exists but may be misisng somefields
                List<String> requredColNames = Arrays.asList("usersettings_id", "starttime", "timeslotinterval", "slotsbeforelunch",
                            "lunchinterval", "titlefontsize",  "subtitlefontsize", "cellfontsize", "headerfontsize", "footnotefontsize",
                            "username", "concourschair", "chiefjudge", "compression" );
                int numMissingCols = 0; 
                List<String> missingColNames = null;
                try {
                    missingColNames = checkUserSettingsColNamesDbUtils(aConn, theConcours, theConcours.GetLogger(), requredColNames);
                    numMissingCols = missingColNames.size();
                } catch (SQLException ex) {
                    String msg = "SQLException in checkUserSettingsColNamesDbUtils() called from ConclursGUI";
                    okDialog(msg);
                    theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                }
                if(numMissingCols != 0){
                    String msg = "UserSettings table has missing columns: ";
                    for(String colname : missingColNames){
                        msg = msg + "\n" + colname;
                    }
                    msg = msg + "\nUserSettings table will be replaced with new one using default values.";
                    okDialog(msg);
                    theConcours.GetLogger().info(msg);
                    userSettingsTableOk = false;
                } else{
                    theConcours.GetLogger().info("UserSettings table has correct columns.");

                    // table exists and all required columns so we check the values in the columns
                    /* skip this to see if it's causing the later locked table problem
                    try {
                        boolean userSettingsOk = loadSQLiteConcoursDatabase.CheckUserSettingsTableIsOK(aConn, theConcours, theConcours.GetLogger() );
                        if(!userSettingsOk){
                            String msg = " Bad UserSettingsTable.   A new table will be created using default values." ;
                            theConcours.GetLogger().info(msg);
                            userSettingsTableOk = false;
                        } else{
                             theConcours.GetLogger().info("UserSettings table OK");
                        }
                    } catch (SQLException ex) {
                        String msg = "SQLException while checking structure of UserSettingsTable. A new  table will be created using default values.";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg);
                        userSettingsTableOk = false;
                    } catch (java.lang.NumberFormatException ex2){
                        String msg = "Structure of the User Settings Table is incorrect for this version of ConcoursBuilder. A new one table will be created using default values.";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg);
                        userSettingsTableOk = false;
                    } catch (Exception e){
                        String msg = "Unknown Exception while checking structure of the UserSettingsTable. A new  table will be created using default values.";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg);
                        userSettingsTableOk = false;
                    }
                    */
                }
            }
            if(!userSettingsTableOk){
                try {
                    theConcours.GetLogger().info("Will fix or replace UserSettings table");
                    if(!boolUserSettingsTableExists){
                        theConcours.GetLogger().info("UserSettings doesn't exist so Will replace it.");
                        loadSQLiteConcoursDatabase.CreateUserSettingsTable(aConn);
                        LoadSQLiteConcoursDatabase.SetUserSettingsTable(aConn, START_TIME, TIMESLOT_INTERVAL, TIMESLOTS_BEFORE_LUNCH, LUNCH_INTERVAL,
                        TITLE_FONT_SIZE, SUBTITLE_FONT_SIZE, CELL_FONT_SIZE, HEADER_FONT_SIZE, FOOTNOTE_FONT_SIZE, USER_NAME, CONCOURS_CHAIR, CHIEF_JUDGE, COMPRESSION);
                    } else{
                        theConcours.GetLogger().info("UserSettings exist so will try to fix it.");
                        addNewColumnsToUserSettingsTable(aConn, theConcours.GetLogger());
                    }
                } catch (SQLException ex) {
                    String msg = "SQLException while trying to fix UserSettings table. Bailing out";
                    okDialog(msg);
                    theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                    System.exit(-1);
                }
            }
  }  
  
private static void prepToCloseCBGUI(){
    theConcours.GetLogger().info("Preparing to close ConcoursGUI");
        if(boolDBFileOpened){
            if(copysaveObject.getSavedRequestedFlag() == 0){ 
                // user has not explicitly requested saving the revised Concours DB file, so we ask to be sure
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult;
                dialogResult = JOptionPane.showConfirmDialog(null,"Click Yes to save the Concours with all changes made in this session. \nClick No to cancel any changes made in this session.)", "Save revised Concours", dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION){
                    theConcours.GetLogger().info("\nUser elected to save the revised Concours. No action required since the active database is up to date.");
                } else{
                    // User doesn't want to keep the results so we rename the Backup Concours database to the original file
                    String absPath = null;
                    if(flConcoursDBFile != null){
                        absPath = flConcoursDBFile.getPath();// .getAbsolutePath();
                    } else{
                        String msg ="File flConcoursDBFile is Null";
                        theConcours.GetLogger().log(Level.SEVERE, msg, ERROR);
                        okDialog(msg);
                    }
                    
                    String strBackupFilePath = absPath.replace(".db", "Backup.db");
                    try {
                        File temp = new File(strBackupFilePath);
                        if(temp.exists()){
                            theConcours.GetLogger().info(strBackupFilePath + " exists ");
                            // Both source (the Opened Concours DB file) & destination (the newly created file named strBackupFileName) are closed before copyDBFile() returns
                            copysaveObject.copyDBFile(new File(strBackupFilePath), flConcoursDBFile, theConcours.GetLogger());
                            theConcours.GetLogger().info("\nUser elected not to save the revised Concours, so saved backup database is restored to its original name.");
                            //okDialog("User elected not to save the possibly revised database");
                        } else {
                            okDialog(strBackupFilePath + " does not exist so changes will be kept.");
                            theConcours.GetLogger().info(strBackupFilePath + " not exist so changes will be kept.");
                        }
                        
                    } catch (IOException ex) {
                        theConcours.GetLogger().info("Io Exception in prepToCloseCBGUI");
                        theConcours.GetLogger().log(Level.SEVERE, null, ex);
                    }
                }
            }
            if(fhLogger != null) fhLogger.close();
        }
    
}   
        

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        prepToCloseCBGUI();
        System.exit(0);
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void RemoveEntryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveEntryMenuItemActionPerformed
      // okDialog("Starting Remove Entry Dialog");
       theConcours.GetLogger().info("Starting Remove Entry Dialog");
       Connection conn = theConcours.GetConnection();
        OwnerRepository concoursPersonnelList = new OwnerRepository(theConcours);
        ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
        ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
        JCNAClass [] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()] );
             
 //     RemoveConcoursEntryDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel,  OwnerRepository aRepository, JCNAClass [] aClassMasterList, boolean aSystemexitwhenclosed)
        //8/25/2018 This Conctructor fails if there are no Concours Entries!
        int numEntries = theConcours.GetEntries().GetConcoursEntries().size();
        if(numEntries <= 0){
            String msg = "No Concours entries to remove.";
            okDialog(msg);
        } else{
            RemoveConcoursEntryDialog removeConcoursEntryDialog = new RemoveConcoursEntryDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, concoursPersonnelList, classMasterArray , false);
            theConcours.GetLogger().info("Finished constructing RemoveConcoursEntryDialog()");
            removeConcoursEntryDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    theConcours.GetLogger().info("Returning from Remove Entry to main CB GUI");
                }
            });        
            removeConcoursEntryDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            removeConcoursEntryDialog.setVisible(rootPaneCheckingEnabled);

            if(!theConcours.GetJudgeAssignmentCurrent()){
                JASchedule.setForeground(Color.RED);
            }
        }
        
        
    }//GEN-LAST:event_RemoveEntryMenuItemActionPerformed

    private void AddEntryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddEntryMenuItemActionPerformed

        //  8/24/2018 
        MasterPersonnel  theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        int MPSize = theMasterPersonnel.GetMasterPersonnelList().size();
        if(MPSize <= 0){
            String msg = "Master Personnel list is empty. You must Add Master Persons before Adding Entries or Judges";
            okDialog(msg);
        } else {
            Connection conn = theConcours.GetConnection();
            //MasterListRepository masterListRepository = new MasterListRepository(conn);
            boolean loadRepositoryFromMemory = true;
            MasterListRepository masterListRepository = new MasterListRepository(theConcours, loadRepositoryFromMemory);

            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
            ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
            JCNAClass [] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()] );
            //AddConcoursEntryDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel, MasterListRepository aRepository, JCNAClass [] aClassMasterList, boolean aSystemexitwhenclosed)    
            AddConcoursEntryDialog addConcoursEntryDialog = new AddConcoursEntryDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, masterListRepository, classMasterArray, false);
            addConcoursEntryDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    theConcours.GetLogger().info("Returning from AddEntry to main CB GUI");
                }
            });   

            addConcoursEntryDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            //addConcoursEntryDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog... allows checking for boolean initialSetting
            addConcoursEntryDialog.traversalOrder.get(0).requestFocusInWindow();  // This is to be sure the focus is set when dialog appears
            addConcoursEntryDialog.setVisible(rootPaneCheckingEnabled);
            if(!theConcours.GetJudgeAssignmentCurrent()){
                JASchedule.setForeground(Color.RED);
            }
        }
    }//GEN-LAST:event_AddEntryMenuItemActionPerformed

    private void DisplayEntriesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayEntriesMenuItemActionPerformed
        Entries theEntries = theConcours.GetEntries();
        txtArea.append("\nConcours Entries(" + theEntries.GetConcoursEntries().size() +"):\n");
        for(Entry e  : theEntries.GetConcoursEntries()){
          // System.out.println("Unique description " + e.GetUniqueDescription() + " Class: " + e.GetClassName() + " timeslot index: " + e.GetTimeslotIndex());
           txtArea.append("  " + e.GetUniqueDescription() + "  " + e.GetClassName() + "\n"); 
        }

    }//GEN-LAST:event_DisplayEntriesMenuItemActionPerformed

 
    private void DisplayPersonnelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayPersonnelMenuItemActionPerformed
        ConcoursPersonnel  thePersonnel = theConcours.GetConcoursPersonnelObject();
        txtArea.append("Concours Personnel(" + thePersonnel.GetConcoursPersonnelList().size() + "):\n");
        for(ConcoursPerson p  : thePersonnel.GetConcoursPersonnelList()){
            //System.out.println("Unique name " + p.GetUniqueName());
            txtArea.append("  Unique name: " + p.GetUniqueName() + " Status: ");
            if(p.GetStatus_o() == 1){
                txtArea.append("Owner ");
            }
            if(p.GetStatus_j() == 1){
                txtArea.append("Judge ");
            }
            txtArea.append("\n");
            
        }
    }//GEN-LAST:event_DisplayPersonnelMenuItemActionPerformed

    
    
    private void JAScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JAScheduleActionPerformed
        if(theConcours.GetConcoursJudges().size() < 3){
            okDialog("There must be at least 3 Judges. Currently there are only " + theConcours.GetConcoursJudges().size());
        } else if(theConcours.GetEntriesList().size() - theConcours.GetNumDisplayOnlyEntries() < 1){
            okDialog("There must be at least 1 Judged Entry. Currently there are  " + (theConcours.GetEntriesList().size() - theConcours.GetNumDisplayOnlyEntries()));
        } else {
            
            try {
                // SchedulingInterfaceCpp schedulingInterface = new SchedulingInterfaceCpp(theConcours.GetConnection(),   strConcoursDBFile,  theConcours,  theConcours.GetThePath(), txtArea, true, false);
                // Try closing the connection BEFORE creating the schedulingInterface.. I.e., let the schedulingInterface open it's own connection and close it when finished.
                String strConn = "jdbc:sqlite:" + theConcours.GetThePath();
                theConcours.GetConnection().close();
                theConcours.GetLogger().info("Closed database in ConcoursGUI " + strConn + " successfully before running SchedulingInterface");
            } catch (SQLException ex) {
                String msg = "Failed to close connection in JAScheduleActionPerformed";
                Logger.getLogger(theConcours.GetLogger().getName()).log(Level.SEVERE, msg, ex);
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            theConcours.GetLogger().info("Set cursor to WAIT");
            SchedulingInterfaceJava schedulingInterface = new SchedulingInterfaceJava(strConcoursDBFile,  theConcours,  theConcours.GetThePath(), txtArea, true, false);
            setCursor(Cursor.getDefaultCursor());
            theConcours.GetLogger().info("Set cursor to default");
           // SchedulingInterfaceJava schedulingInterface = new SchedulingInterfaceJava(strConcoursDBFile,  theConcours,  theConcours.GetThePath(), txtArea, false, false);
            /*Connection conn = null;
            try {
                String strDBPath = theConcours.GetThePath() ;

                Class.forName("org.sqlite.JDBC");
                String strConn = "jdbc:sqlite:" + strDBPath ;
                conn = DriverManager.getConnection(strConn);  
                theConcours.SetConnection(conn);
                System.out.println("Reopened database in ConcoursGUI " + strConn + " successfully");
            } catch ( ClassNotFoundException | SQLException e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            */
            if(schedulingInterface.GetSuccess()){
                theConcours.SetJudgeAssignmentCurrent(true);
                try {
                    // loadSQLiteConcoursDatabase.SetSettingsTableJAState(conn, true);
                    loadSQLiteConcoursDatabase.SetSettingsTableJAState(theConcours.GetConnection(), true);
                } catch (SQLException ex) {
                    String msg = "SQLException in ConcoursGUI call to SetSettingsTableJAState";
                    okDialog(msg);
                    theConcours.GetLogger().info(msg);
                    theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                }
                clearPlacardsScoresheetsFolders();
                String msg = "Placards and Scoresheets Folders have been cleared since the PDF files are no longer valid.\n\nYou will need to rerun Create windscreen placards and scoresheets.";
                //okDialog(msg);
                theConcours.GetLogger().info(msg);
                
                EditJASchedule.setEnabled(true);
                JASchedule.setForeground(foregroundColor);
                msg = "Finished generation of Judge assignment and schedule.\nClick on View/Edit Judge assignment & schedule to see results.";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
            } else{
                theConcours.SetJudgeAssignmentCurrent(false);
                // the following causes a null pointer exception because schedulingInterface failed
                //loadSQLiteConcoursDatabase.SetSettingsTableJAState(theConcours.GetConnection(), false);
                EditJASchedule.setEnabled(false);
                JASchedule.setForeground(Color.RED);
                String msg = "Generation of Judge assignment failed.";
                //okDialog(msg);
                theConcours.GetLogger().info(msg);
            }
        }
    }//GEN-LAST:event_JAScheduleActionPerformed

 
    private void EditJAScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditJAScheduleActionPerformed
         // Create and display the form 
        //JudgeAssignGUI theJAGUI  = new JudgeAssignGUI(false, theConcours, "E = Entry O = Owner C = Color  D = Description J = Judge");
        javax.swing.JFrame theFrame = new javax.swing.JFrame();
        theFrame.setName("JudgeAssignDialog");
        theFrame.setTitle("Judge Assignment View and Edit");
        JudgeAssignDialog theJAGUI  = new JudgeAssignDialog(theFrame, true, false, theConcours, "E = Entry O = Owner C = Color  D = Description J = Judge (First listed is Lead Judge)");
        theJAGUI.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Returning from JudgeAssignDialog to main CB GUI");
            }
        });        
        theJAGUI.setDefaultCloseOperation(HIDE_ON_CLOSE);
        theJAGUI.setVisible(true);
       
    }//GEN-LAST:event_EditJAScheduleActionPerformed

    
    
    private void RemoveJudgeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveJudgeMenuItemActionPerformed
        //okDialog("Not implemented");
        Connection conn = theConcours.GetConnection();
        // 8/25/2018
        int numJudges = theConcours.GetConcoursJudges().size();
        if(numJudges <= 0){ 
            String msg2 = "There are no Concours Judges to remove.";
            okDialog(msg2);
        } else {
            JudgeRepository concoursJudgeRepository = new JudgeRepository(theConcours);
            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();

            RemoveConcoursJudgeDialog theDialog = new RemoveConcoursJudgeDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, concoursJudgeRepository , false);
            theConcours.GetLogger().info("Return from RemoveConcoursJudgeDialog constructor in RemoveJudgeMenuItemActionPerformed.");
            theDialog.setVisible(true);
            if(!theConcours.GetJudgeAssignmentCurrent()){
                JASchedule.setForeground(Color.RED);
                EditJASchedule.setEnabled(false); 
            }
        }
    }//GEN-LAST:event_RemoveJudgeMenuItemActionPerformed

    private void AddJudgeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddJudgeMenuItemActionPerformed
        Connection conn = theConcours.GetConnection();

        //  8/24/2018 
        MasterPersonnel  theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        int MPSize = theMasterPersonnel.GetMasterPersonnelList().size();
        if(MPSize <= 0){
            String msg = "Master Personnel list is empty. You must Add Master Persons before Adding Entries or Judges";
            okDialog(msg);
        } else {
            //MasterListRepository masterListRepository = new MasterListRepository(conn);
            boolean loadRepositoryFromMemory = true;
            MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);
            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
            ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
            JCNAClass[] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()]);
            // last argument true => Add a Concours Judge fales => edit a Concours Judge
            AddConcoursJudgeDialog theDialog = new AddConcoursJudgeDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, masterList, classMasterArray, false, true);
            theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    theConcours.GetLogger().info("Returning from AddJudge to main CB GUI");
                }
            });

            theDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            theDialog.setVisible(rootPaneCheckingEnabled);
            if(!theConcours.GetJudgeAssignmentCurrent()){
                JASchedule.setForeground(Color.RED);
            }
        }
    }//GEN-LAST:event_AddJudgeMenuItemActionPerformed

    private void FileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FileMenuActionPerformed

    private void NewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewMenuItemActionPerformed
        //
        // Rewritten to simplify 1/5/2017. We begin by copying the Base DB file to a new Concourse DB file named as the users selected
        // Afterwards, we create some empty tables etc.
        //
        //  Launch the dialog to get the name of the Base DB and a Concours name,  the latter to be used for the Folder for the new Concours DB file
        //  
       theConcours.GetLogger().info("New concours dialog launched");
        //String strConcoursBaseName = "";
        String strConcoursName = "";
        //Connection conn;
        int returnVal; 

        ConcoursBaseDBFileChooser.setCurrentDirectory(new File(concoursBuilderDataPath)); 
        ConcoursBaseDBFileChooser.setDialogTitle("Select Concours Base DB file");
        ConcoursBaseDBFileChooser.setToolTipText("Select the Base database to be used in creation of new Concours");
        ConcoursBaseDBFileChooser.setFileFilter(new MyCustomFilterBaseDB() );
        ConcoursBaseDBFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        SaveAsFileChooser.setDialogTitle("Save Concourse as");
        SaveAsFileChooser.setCurrentDirectory(new File(concoursBuilderDataPath)); 
        SaveAsFileChooser.setFileFilter(new MyCustomFilterFolder() );
        
        returnVal = ConcoursBaseDBFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.CANCEL_OPTION) {
            return;
        }
        if(returnVal == JFileChooser.APPROVE_OPTION){
            flConcoursBaseDBFile = ConcoursBaseDBFileChooser.getSelectedFile();
            strConcoursBaseDBFile = flConcoursBaseDBFile.getAbsolutePath(); // this is the the absolute path to the Base DB file as a string
            // The following check shouldn't be needed due to MyCustomFilterBaseDB
            if(!strConcoursBaseDBFile.contains("Base")){
                okDialog("Base DB file must contain Base in the name.");     
                returnVal = ConcoursBaseDBFileChooser.showOpenDialog(this);
                if (returnVal == JFileChooser.CANCEL_OPTION) {
                    return;
                }
           }
            // 
            // Create a folder for the new concours. A bit tricky because the user might want to reuse the name of an existing Concoure.
            // It's implemented such that if that's the case the database file, e.g., mnc.db will be deleted BUT NOT THE FOLDER. Thus
            // miscellaneous files that the user may have put there will remain. Only mnc.db will be deleted & recreated as an empty Concours.
            //
            // There is an interesting little quirk: if there is an mnc directory but no mnc.db in it the dialog will NOT show mnc as an
            // existing Concours. Things work out ok, however, since the code checks the existance of mnc.db before deleting it, and no
            // delete is executed, but either way it's still not there so creation of a new one is OK.
            // 
            // BTW, I first implemented this using FileChooser but it was kind of confusing to the user.  Consequently, I redid it with my own special purpose dialog.
            //
            String strNewConcoursFolder = "";
            boolean ok = false;
            // Loop until we get what we want from the user...
            // If user clicks Cancel or X strNewConcoursFolder is set to "cancelled" and the function returns.
            do {
                GetNewConcoursNameDialog getNewConcoursFolderDialog = new GetNewConcoursNameDialog(new javax.swing.JFrame(), true, concoursBuilderDataPath); 
                getNewConcoursFolderDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        theConcours.GetLogger().info("Returning from NewMenuItemActionPerformed to main CB GUI");
                    }
                });   

                getNewConcoursFolderDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog.
                getNewConcoursFolderDialog.setVisible(rootPaneCheckingEnabled);
                List<String> aryExistingConcourses;
                aryExistingConcourses = getNewConcoursFolderDialog.getExistingConcourses(); // file names without extionsion, e.g., "mnc"
                strNewConcoursFolder = getNewConcoursFolderDialog.getNewConcoursName(); // just the concours name, no path, no extension. e.g.,  "mnc"
                if(strNewConcoursFolder == null){
                    // null means the user clicked Cancel or X
                    strNewConcoursFolder = "cancelled";
                    ok = true; // so it doesn't endlessly
                } else if(strNewConcoursFolder.isEmpty()){
                    okDialog("You must enter a name for the new Concours");
                     ok = false;
                } else {
                    if(aryExistingConcourses.contains(strNewConcoursFolder)){
                        int response = yesNoDialog("There is an existing Concours named " + strNewConcoursFolder +
                                                   ". If you choose to overwrite it, all files in the existing concours folder will be deleted.\nDo you wish to overwrite it?");
                        if(response == JOptionPane.YES_OPTION) {
                           //String dbfilename = concoursBuilderDataPath + "\\" + strNewConcoursFolder + "\\" + strNewConcoursFolder + ".db";
                           //File f = new File(folderpath); 
                           //if(f.exists()) f.delete(); // Have to check becausef user might have deleted it. 
                           MyJavaUtils.clearFolder(concoursBuilderDataPath + "\\" + strNewConcoursFolder );
                           clearPlacardsScoresheetsFolders(); // Don't want these in the concours folders
                           ok = true;
                        } 
                    } else {
                        // no conflict with existing concours names
                        ok = true;
                    }
                }
            } while(!ok);
            if(strNewConcoursFolder.equals("cancelled")){
                theConcours.GetLogger().info("User Canceled or X out. Returning from to main CB GUI from NewMenuItemActionPerformed ");
                return;
            }
            
            // If it gets here we have a valid Concours name in strNewConcoursFolder
            theConcours.GetLogger().info("Got valid name for new Concours from user.");
            //
            // Now copy the Base DB to the Concourse DB, making it a new, empty Concours
            // Note that the Concours name will be used for both the DB file and its folder
            //
            strConcoursDBFile = createEmptyConcoursDBFile( concoursBuilderDataPath, strNewConcoursFolder); 
            Path path = Paths.get(strConcoursDBFile); // this is the the absolute path to the DB file as a Path
            String fileName = path.getFileName().toString();            
            File f = new File(fileName);
            strConcoursName = f.getName();
            theConcours.SetConcoursName(strConcoursName);
            String frameTitle = "JCNA Concours Builder: " + strConcoursName.replace(".db", "");
            this.setTitle(frameTitle);
            theConcours.SetThePath(strConcoursDBFile); 
            // Now Connect to it so we can create the new tables
            Connection conn = null;
            try {
                Class.forName("org.sqlite.JDBC");
                String strConn;
                strConn = "jdbc:sqlite:" + strConcoursDBFile ;
                conn = DriverManager.getConnection(strConn);
                theConcours.SetConnection(conn) ;
            } catch ( ClassNotFoundException | SQLException e ) {
                ////System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                theConcours.GetLogger().info(e.getClass().getName() + ": " + e.getMessage());
                okDialog("Failed to connect to " + strConcoursDBFile);                
                System.exit(0);
            }
            //
            // Create all Concours database tables, all empty
            //   1/7/2017 This is NOT NECESSARY since the Base database has all of these tables and all are empty!
            //loadSQLiteConcoursDatabase.CreateConcoursTables(conn);
           try {
               LoadSQLiteConcoursDatabase.SetSettingsTableConcoursName(conn,  strConcoursName);
           } catch (SQLException ex) {
                theConcours.GetLogger().log(Level.SEVERE, "SQLException: SetSettingsTableConcoursName failed", ex);
                System.exit(-1);
           }
            //
            // 6/3/2017
            // This table has Time data for the Schedule: 
            //    1.  Start time, judging interval, judging intervals before  lunch
            //     and lunch interval.
            //      
            //    2.  Font sizes for PDF schedule tables
            //
            //    3.  User name, real name of the user... not sure if it will be used, but put it in the table just in case
            //
            LoadSQLiteConcoursDatabase.CreateUserSettingsTable(conn );
            LoadSQLiteConcoursDatabase.SetUserSettingsTable(conn, START_TIME, TIMESLOT_INTERVAL, TIMESLOTS_BEFORE_LUNCH, LUNCH_INTERVAL,
                                                                  TITLE_FONT_SIZE, SUBTITLE_FONT_SIZE, CELL_FONT_SIZE, HEADER_FONT_SIZE, FOOTNOTE_FONT_SIZE, USER_NAME, CONCOURS_CHAIR, CHIEF_JUDGE, COMPRESSION);
            // Now read them and save into Concours properties
            // 6/5/2017
            loadSQLiteConcoursDatabase.LoadConcoursUserSettingsDB(conn, theConcours, theConcours.GetLogger());
            // Calculate Lunch time, rounded up to nearest 15 minute clock position
            //theConcours.SetConcoursLunchTime(MyJavaUtils.calculateLunchtime(theConcours.GetConcoursStartTime(), theConcours.GetConcoursTimeslotInterval(),theConcours.GetConcoursTimeslotsBeforeLunch()));
            String startTime = theConcours.GetConcoursStartTime();
            int timeslotInterval = theConcours.GetConcoursTimeslotInterval();
            int timeslotsBeforeLunch = theConcours.GetConcoursTimeslotsBeforeLunch();
            String lunchTime = MyJavaUtils.calculateLunchtime(theConcours.GetLogger(), startTime, timeslotInterval, timeslotsBeforeLunch);
            theConcours.SetConcoursLunchTime(lunchTime);

            boolDBFileOpened = true;
            CopyConcoursMenuItem.setEnabled(boolDBFileOpened);
            NewMenuItem.setEnabled(!boolDBFileOpened);
            OpenMenuItem.setEnabled(!boolDBFileOpened);
            EditMenu.setEnabled(boolDBFileOpened);
            DisplayMenu.setEnabled(boolDBFileOpened);
            JASchedule.setForeground(foregroundColor);
            copysaveObject.setSavedRequestedFlag(0);
            copysaveObject.setSavedRequestedFlag(1); // This will ensure that the Concours is saved. Otherwise, user will be prompted and not know what to say.
            ToolsMenu.setEnabled(boolDBFileOpened);
            ExportMenu.setEnabled(boolDBFileOpened);
            
            /*try {
                //
                //      Locked table problem DEBUGGING  This block must be removed after debugging
                //
                //           See if JudgeAssignmentsTable locked after creating councourse tables
                //
                conn.setAutoCommit(false);
                Statement stat_jad = conn.createStatement(); // Entry judge lists
                stat_jad.executeUpdate("drop table if exists JudgeAssignmentsTable;");
                stat_jad.close();
                conn.commit();
                theConcours.GetLogger().info("After CreateConcoursTables DEBUGGING JudgeAssignmentsTable NOT locked after CreateConcoursTables()");

            } catch (SQLException ex) {
                Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
                theConcours.GetLogger().info("DEBUGGING JudgeAssignmentsTable locked after CreateConcoursTables()");
            }
         */  
            
            theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", theConcours.GetLogger());
            boolean classRulesOK = theConcours.GetJCNAClassRules().LoadJCNAClassRulesDB(conn, "JCNAClassRules", theConcours.GetLogger());
            if(!classRulesOK){
                okDialog("ERROR: LoadJCNAClassRulesDB failed in NewMenuItemActionPerformed()");
                 theConcours.GetLogger().info("ERROR: LoadJCNAClassRulesDB failed in NewMenuItemActionPerformed()");
                 System.exit(-1);
            } else{
                theConcours.GetLogger().info("Loaded JCNA Class Rules successfully");
            }
            // 09/19/2018
            // Check the MasterPersonnel database structure
            //
            boolean isOk = checkMasterPersonnelTableStructure(conn, theConcours);
            if(!isOk){
                String msg = "Bad table Masterpersonnel structure in NewMenuItemActionPerformed. Database is incompatible with this version of ConcoursBuilder.";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            theConcours.LoadMasterPersonnelDB(conn, theConcours.GetLogger());
            // 09/20/2018
            // Check the MasterJaguar database structure
            //
            isOk = checkMasterJaguarTableStructure(conn, theConcours);
            if(!isOk){
                String msg = "Bad table MasterJaguar structure in NewMenuItemActionPerformed. Database is incompatible with this version of ConcoursBuilder.";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }

            theConcours.LoadMasterJaguarDB(conn, theConcours.GetLogger());
            // boolJudgeAssignmentCurrent is the state when the last session with this COncours database was closed
            // true means that there is no need to build a Judge Assignment
            boolean boolJudgeAssignmentCurrent = false; //loadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn) ; 
            theConcours.SetJudgeAssignmentCurrent(boolJudgeAssignmentCurrent);
            // Manual Editing of Judge Assignment is OK if boolJudgeAssignmentCurrent  == true.
            // If false, no edditing is allowed until a new Judge Assignment isperformed. 
            EditJASchedule.setEnabled(boolJudgeAssignmentCurrent); 
            
            
                //theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is initialized here and then again after any change
                
                // System.out.println("JCNA Classes, Master Personnel, and Master Jaguar tables loaded from directory " + theConcours.GetThePath());
                /*
                Present user with a FileChooser so the new Concours file can be named and saved
                */
                /*
                try {
                //
                //      Locked table problem DEBUGGING  This block must be removed after debugging
                //
                //           See if EntryJudgesTable locked after above stuff
                //
                conn.setAutoCommit(false);
                Statement stat_ej = conn.createStatement(); // Entry judge lists
                stat_ej.executeUpdate("drop table if exists EntryJudgesTable;");
                stat_ej.close();
                conn.commit();
                theConcours.GetLogger().info("Before createEmptyConcoursDBFile DEBUGGING EntryJudgesTable NOT locked after loading some things into the concours db");
                
                } catch (SQLException ex) {
                Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
                theConcours.GetLogger().info("Before createEmptyConcoursDBFile DEBUGGING EntryJudgesTable locked after loading some things into the concours");
                }
                //
                //   
            try {
                //Need to close connection here. Note that it's file gets closed() in createEmptyConcoursDBFile()
                conn.close();
            } catch (SQLException ex) {
                //Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
                Logger.getLogger(theConcours.GetLogger().getName()).log(Level.SEVERE, null, ex);
            }
            */
            // now reopen conn
           /* conn = null;
            try {
                Class.forName("org.sqlite.JDBC");
                String strConn;
                strConn = "jdbc:sqlite:" + strConcoursDBFile ;
                conn = DriverManager.getConnection(strConn);
                theConcours.SetConnection(conn) ;
            } catch ( ClassNotFoundException | SQLException e ) {
                ////System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                theConcours.GetLogger().info(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            theConcours.GetLogger().info("reopened connection");
            */
            
            
        }
        theConcours.GetLogger().info("Base database " + strConcoursBaseDBFile + " has been used to create the New concours.");
        okDialog("Base database " + strConcoursBaseDBFile + " has been used to create the New concours " + strConcoursName + " \n\n A New concours is automatically saved without user query when exiting ConcoursBuilder.");
        // 8/24/2018 Alert user if the
        MasterPersonnel  theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        int MPSize = theMasterPersonnel.GetMasterPersonnelList().size();
        if(MPSize <=0){
            String msg = "The Master Personnel list from the selected Base database is empty. Master Persons must be Added before Adding Entries or Judges" ; 
            okDialog(msg);
        }
    }//GEN-LAST:event_NewMenuItemActionPerformed

    private void EditMPMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditMPMenuItemActionPerformed
        Connection conn = theConcours.GetConnection();
        //  8/25/2018 
        MasterPersonnel  theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        int MPSize = theMasterPersonnel.GetMasterPersonnelList().size();
        if(MPSize <= 0){
            String msg = "There are no Master Persons to change.";
            okDialog(msg);
        } else {
            boolean loadRepositoryFromMemory = true;
            MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);
            // EditMasterPersonDialog2(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours,  MasterListRepository aRepository, boolean aSystemexitwhenclosed)
            EditMasterPersonDialog2 theDialog = new EditMasterPersonDialog2(new javax.swing.JFrame(), true, conn, theConcours, masterList, false);
            theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    theConcours.GetLogger().info("Returning from EditMPMenuItemActionPerformed to main CB GUI");
                }
            });        
            theDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            theDialog.setVisible(true);            
        }

    }//GEN-LAST:event_EditMPMenuItemActionPerformed

    private void CopyConcoursMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopyConcoursMenuItemActionPerformed
        CopyOpenConcours(theConcours);
    }//GEN-LAST:event_CopyConcoursMenuItemActionPerformed
    /*
        Used by CopyConcoursMenuItemActionPerformed() 
    */
    private void CopyOpenConcours(Concours aConcours){
        //
        // The CopyOpenConcours of currently open Concours is implemented by copying the database into a new file identified 
        // by a new user-provided name. It is put in a new folder of the same name as a subfolder of the Concourses folder.
        //
        copysaveObject = new CopySaveDBFile();
        String strNewConcoursFolder = "";
        String strNewConcoursFullPath = "";
        File newFile = null;
        boolean ok = false;
        // Loop until we get what we want from the user...
        // If user clicks Cancel or X strNewConcoursFolder is set to "cancelled" and the function returns.
        do {
            GetNewConcoursNameDialog getNewConcoursFolderDialog = new GetNewConcoursNameDialog(new javax.swing.JFrame(), true, concoursBuilderDataPath); 
            getNewConcoursFolderDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    theConcours.GetLogger().info("Returning from CopyConcoursNewMenuItemActionPerformed to main CB GUI");
                }
            });   

            getNewConcoursFolderDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog.
            getNewConcoursFolderDialog.setVisible(rootPaneCheckingEnabled);
            List<String> aryExistingConcourses;
            aryExistingConcourses = getNewConcoursFolderDialog.getExistingConcourses(); // file names without extionsion, e.g., "mnc"
            strNewConcoursFolder = getNewConcoursFolderDialog.getNewConcoursName(); // just the concours name, no path, no extension. e.g.,  "mnc"
            if(strNewConcoursFolder == null){
                // null means the user clicked Cancel or X
                strNewConcoursFolder = "cancelled";
                ok = true; // so it doesn't endlessly
            } else if(strNewConcoursFolder.isEmpty()){
                okDialog("You must enter a name for the new Concours");
                 ok = false;
            } else {
                ok = true;
                strNewConcoursFullPath = concoursBuilderDataPath + "\\" + strNewConcoursFolder + "\\" + strNewConcoursFolder + ".db";
                newFile = new File(strNewConcoursFullPath); 
                if(aryExistingConcourses.contains(strNewConcoursFolder)){
                    int response = yesNoDialog("There is an existing Concours named " + strNewConcoursFolder + ". Do you wish to overwrite it?");
                    if(response == JOptionPane.YES_OPTION) {
                       if(newFile.exists()) newFile.delete(); // Have to check because user might have deleted it. 
                    } 
                } 
            }
        } while(!ok);
        if(strNewConcoursFolder.equals("cancelled")){
            theConcours.GetLogger().info("User Canceled or X out. Returning from to main CB GUI from CopyConcoursMenuItemActionPerformed ");
            return;
        }
        String newFolder = createNewConcoursFolder(concoursBuilderDataPath, strNewConcoursFolder);
        try {
            clearPlacardsScoresheetsFolders(); // Don't want these in the concours folders
            copysaveObject.copyDBFile( new File(strConcoursDBFile), newFile, theConcours.GetLogger()); 
        } catch (IOException ex) {
            String msg = "IOException in copyDBFile( ) called from CopyMenuItemActionPerformed.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        String msg = "The Open Concours has been copied to a new Concours named " + strNewConcoursFullPath + 
                ".\n\n To work with the new Concours you must Exit ConcoursBuilder and Open it.";
        okDialog(msg);
        theConcours.GetLogger().info(msg);
    }

    /*
        Used by NewMenuItemActionPerformed()
    */
    private String createEmptyConcoursDBFile(String aPathToFolder, String aFolderName){
        //
        // Create a new Concours DB file by copying the Base DB file & adding enpty Concours tables.
        //
        String strNewFileName = "";
        theConcours.GetLogger().info("In createEmptyConcoursDBFile()");
        String strNewFolder = "";
        // Note: if this is after user elected to overwrite an existing Concours the directory will still exist 
        //
        File f = new File(aPathToFolder + "\\" + aFolderName);
        boolean success = true;
        if(!f.exists())  success = (f).mkdir();
        if(success){
            // Now create Folders for theScoresheets & windscreen placards
           // File fileChampionSpecial = new File(aPathToFolder + "\\" + aFolderName + "\\ChampionSpecial");
           // (fileChampionSpecial).mkdir();
           // File fileDriven = new File(aPathToFolder + "\\" + aFolderName + "\\Driven");
           // (fileDriven).mkdir();
                // Create Folders for theScoresheets & windscreen placards if they don't exist... 
                // First, Placard folders
                String strConcoursFolderPath = aPathToFolder + "\\" +  aFolderName;
                File filePlacards = new File(strConcoursFolderPath + "\\Placards");
                if(!(filePlacards.exists() && filePlacards.isDirectory()))     (filePlacards).mkdir();
                File filePlacardsChampionSpecial = new File(strConcoursFolderPath + "\\Placards\\ChampionSpecial");
                if(!(filePlacardsChampionSpecial.exists() && filePlacardsChampionSpecial.isDirectory()))     (filePlacardsChampionSpecial).mkdir();
                File filePlacardsDriven = new File(strConcoursFolderPath +  "\\Placards\\Driven");
                if(!(filePlacardsDriven.exists() && filePlacardsDriven.isDirectory()))    (filePlacardsDriven).mkdir();
                // Now, Scoresheet folders
                File fileScoresheets = new File(strConcoursFolderPath + "\\Scoresheets");
                if(!(fileScoresheets.exists() && fileScoresheets.isDirectory()))     (fileScoresheets).mkdir();
                File fileScoresheetsChampionSpecial = new File(strConcoursFolderPath + "\\Scoresheets\\ChampionSpecial");
                if(!(fileScoresheetsChampionSpecial.exists() && fileScoresheetsChampionSpecial.isDirectory()))     (fileScoresheetsChampionSpecial).mkdir();
                File fileScoresheetsDriven = new File(strConcoursFolderPath +  "\\Scoresheets\\Driven");
                if(!(fileScoresheetsDriven.exists() && fileScoresheetsDriven.isDirectory()))    (fileScoresheetsDriven).mkdir();
           
            strNewFileName = aPathToFolder + "\\" +  aFolderName + "\\" + EnsureDbExtension(aFolderName);
            try {
                // Note that both source & destination are closed() before copyDBFile returns
                // CopyOpenConcours the file 
                copysaveObject.copyDBFile(flConcoursBaseDBFile, new File(strNewFileName), theConcours.GetLogger()); // the new file itself is created here and the BASE file (flConcoursDBFile) is copied into it
            } catch (IOException ex) {
                okDialog("Failed to copy Base DB into new concours " + strNewFileName);
                theConcours.GetLogger().log(Level.SEVERE, null, ex);
                return "";
            }
            theConcours.GetLogger().info("Created new concours folder " + strNewFolder);
        } else {
            okDialog("failed to create new concours folder " + strNewFolder);
            theConcours.GetLogger().info("failed to create new concours folder " + strNewFolder);
        } 


        theConcours.GetLogger().info("Copied the Base DB and saved it as a new concours as " + strNewFileName);
               
        return strNewFileName;
    }

    private String createNewConcoursFolder(String aPathToFolder, String aFolderName){
        //
        // Create a new Concours folder.
        //
        String strnewFolderName = "";
        theConcours.GetLogger().info("In createNewConcoursFolder()");
        String strNewFolder = "";
        // Note: if this is after user elected to overwrite an existing Concours the directory will still exist 
        //
        File f = new File(aPathToFolder + "\\" + aFolderName);
        boolean success = true;
        if(!f.exists())  success = (f).mkdir();
        if(success){
            theConcours.GetLogger().info("Created new concours folder " + strNewFolder);
        } else {
            okDialog("failed to create new concours folder " + f.toString());
            theConcours.GetLogger().info("failed to create new concours folder " +  f.toString());
        } 
        String strConcoursFolderPath = aPathToFolder + "\\" + aFolderName;
        // Create Placard & Scorsheet folders
        // First, Placard folders
        File filePlacards = new File(strConcoursFolderPath + "\\Placards");
        if(!(filePlacards.exists() && filePlacards.isDirectory()))     (filePlacards).mkdir();
        File filePlacardsChampionSpecial = new File(strConcoursFolderPath + "\\Placards\\ChampionSpecial");
        if(!(filePlacardsChampionSpecial.exists() && filePlacardsChampionSpecial.isDirectory()))     (filePlacardsChampionSpecial).mkdir();
        File filePlacardsDriven = new File(strConcoursFolderPath +  "\\Placards\\Driven");
        if(!(filePlacardsDriven.exists() && filePlacardsDriven.isDirectory()))    (filePlacardsDriven).mkdir();
        // Now, Scoresheet folders
        File fileScoresheets = new File(strConcoursFolderPath + "\\Scoresheets");
        if(!(fileScoresheets.exists() && fileScoresheets.isDirectory()))     (fileScoresheets).mkdir();
        File fileScoresheetsChampionSpecial = new File(strConcoursFolderPath + "\\Scoresheets\\ChampionSpecial");
        if(!(fileScoresheetsChampionSpecial.exists() && fileScoresheetsChampionSpecial.isDirectory()))     (fileScoresheetsChampionSpecial).mkdir();
        File fileScoresheetsDriven = new File(strConcoursFolderPath +  "\\Scoresheets\\Driven");
        if(!(fileScoresheetsDriven.exists() && fileScoresheetsDriven.isDirectory()))    (fileScoresheetsDriven).mkdir();
               
        return  f.toString();
    }
    
    /*
    private String createNewConcoursFolder(){
        //
        // Create a new Concours folder. 
        //
        theConcours.GetLogger().info("In createEmptyConcoursFolder()");
        String strNewFolder = "";
        int res;
        
        boolean concourseNameOk = false;
        {
            SaveAsFileChooser.setSelectedFile(new File("MyNewConcours"));
            res = SaveAsFileChooser.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                // copy and save under selected name
                File flNewFile = SaveAsFileChooser.getSelectedFile(); // not an actual file on HD... just used to get a file name provided by user
                // Create as subdirectory of the normal ConcoursBuilder home directory. 
                strNewFolder =  flNewFile.getAbsolutePath();
                // Do not allow an extension
                int pos = strNewFolder.indexOf(".");
                if(pos !=-1) {
                    okDialog("Concours name cannot include punctuation");
                    concourseNameOk = false;
                    strNewFolder = "";
                } else {
                    if(flNewFile.exists()){
                        okDialog("Concours " + strNewFolder + " already exists. Use a different name or delete " + strNewFolder + " folder");
                        theConcours.GetLogger().info("Concours " + strNewFolder + " already exists. Use a different name or delete " + strNewFolder + " folder");
                        concourseNameOk = false;
                        strNewFolder = "";
                    } else {
                        boolean success = (new File(strNewFolder)).mkdir();
                        if(success){
                            theConcours.GetLogger().info("Created new concours folder " + strNewFolder);
                            concourseNameOk = true;
                        } else {
                            okDialog("failed to create new concours folder " + strNewFolder);
                            theConcours.GetLogger().info("failed to create new concours folder " + strNewFolder);
                            concourseNameOk = false;
                            strNewFolder = "";
                        }
                    }
                }
            }
        } while(!concourseNameOk);
        return strNewFolder;
    }
    */
    
    
    private void DisplayStatsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayStatsMenuItemActionPerformed
        int numMasterPersonnel = loadSQLiteConcoursDatabase.getRowCount(theConcours.GetConnection(), "MasterPersonnel");
        int numMasterJaguars = loadSQLiteConcoursDatabase.getRowCount(theConcours.GetConnection(), "Masterjaguar");
        
        float numJudges = theConcours.GetConcoursJudges().size();  
        float numJudgingSlots;
        int numEntries = theConcours.GetEntries().GetConcoursEntries().size();
        int numDriven = 0;
        int numChamp = 0;
        int numDisplay = 0;
        float avgJudgeLoad;
        for(Entry e  : theConcours.GetEntries().GetConcoursEntries()){
            String strClassName = e.GetClassName();
            if("DISP".equals(strClassName)){
                numDisplay++;
            } else if("D".equals(strClassName.substring(0, 1))){
                numDriven ++;
            } else {
                numChamp++; 
            }
        }
        txtArea.append("\nNumber of Master Personnel: " + numMasterPersonnel + " Number of Master Jaguars: " + numMasterJaguars);
        txtArea.append("\nNumber of Driven Entries: " + numDriven + " Number of Champ Entries: " + numChamp + " Number of Display Only Entries: " + numDisplay + " Total: " + numEntries);

        numJudgingSlots = 2*numDriven + 3*numChamp; 

        if(numJudges>0) {
            avgJudgeLoad = (float) Math.round((numJudgingSlots/numJudges) * 10) / 10;
            txtArea.append("\nNumber of Judges: " + numJudges + " Number of Judging slots: " + numJudgingSlots + " Average Judge load: " + avgJudgeLoad + "\n");
        } else{
            txtArea.append("\nNumber of Judges: " + numJudges  + " Number of Judging slots: " + numJudgingSlots +  " Average Judge load: Undefined\n");
        }

    }//GEN-LAST:event_DisplayStatsMenuItemActionPerformed
    /*
        Silently fixes user ommission of the .db extension while doing  Save as
    */
    public String EnsureDbExtension(String aFileName){
        String extension = "";
        String newFileName;
        int i = aFileName.lastIndexOf('.');
        if (i > 0) {
            extension = aFileName.substring(i+1); // will be empty if aFileName has no extension 
        }
        if("db".equals(extension) ) {
            newFileName = aFileName;
        } else {
            if("".equals(extension)){
                newFileName = aFileName + ".db";
            } else{
                newFileName = aFileName.replaceFirst(extension, "db");
            }
        }
        return newFileName;
    }
    private void ChangeEntryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeEntryMenuItemActionPerformed
        Connection conn = theConcours.GetConnection();
        // 8/25/2018
        int numEntries = theConcours.GetEntries().GetConcoursEntries().size();
        if(numEntries <= 0){
            String msg = "There are no Concours Entries to change.";
            okDialog(msg);
        } else {
            boolean loadRepositoryFromMemory = true;
            MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);

            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
            ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
            JCNAClass [] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()] );
            //AddConcoursEntryDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel, MasterListRepository aRepository, JCNAClass [] aClassMasterList, boolean aSystemexitwhenclosed)    
            EditConcoursEntryDialogRev modifyConcoursEntryDialog = new EditConcoursEntryDialogRev(new javax.swing.JFrame(), true, conn, theConcours,  classMasterArray, false);
            modifyConcoursEntryDialog.setVisible(rootPaneCheckingEnabled);
            if(!theConcours.GetJudgeAssignmentCurrent()){
                JASchedule.setForeground(Color.RED);
            }
        }
        
    }//GEN-LAST:event_ChangeEntryMenuItemActionPerformed

    
    private void SaveBaseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveBaseMenuItemActionPerformed
        // 1. File Chooser lets user specify a folder & name for the new Base database file
        // 2. Since the file doesn't exist SQLite creates it on first attempt to connect
        baseMasterConnection = null;
        int returnVal;
        String strConcoursName = theConcours.GetConcoursName();
        if(strConcoursName.equals("noConcoursOpen.db")){
            okDialog("A concours must be Open when this command is used");
            return;
        }
        saveBaseProgressMonitor = new ProgressMonitor(this, "This might take awhile", "", 0, 100);
        
        String strTempNewBaseDBFile = null;
        SaveBaseDBAsFileChooser.setDialogTitle("Save Concours Master lists & JCNA Classes as new Base database");
        SaveBaseDBAsFileChooser.setToolTipText("Enter name for new Base DB file, e.g., SoCalBase052015");
        SaveBaseDBAsFileChooser.setSelectedFile(new File("MyNewBaseDB"));
        SaveBaseDBAsFileChooser.setFileFilter(new CustomBaseDBFileFilter() );
        SaveBaseDBAsFileChooser.setCurrentDirectory(new File(concoursBuilderDataPath));
        SaveBaseDBAsFileChooser.setApproveButtonToolTipText("Click to save File as a new Base Database");
        returnVal = SaveBaseDBAsFileChooser.showDialog(this, "Save Base DB");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            flNewBaseDBFile = SaveBaseDBAsFileChooser.getSelectedFile();
            strTempNewBaseDBFile = flNewBaseDBFile.getAbsolutePath(); // this is the the absolute path to the new Base DB file as a string
            strNewBaseDBFile = EnsureDbExtension(strTempNewBaseDBFile);
            if(!strNewBaseDBFile.contains("Base")){
                int resp =  yesNoDialog("Base database files should contain Base in the name. Are you sure you waht to use this name?");     
                if(resp != JOptionPane.YES_OPTION) {
                    return;
                }
            } 
        } else {
            return;    
        }
        
        // do the REAL work
        
       // start MyTask here
        try {
            doTheBaseDBSave();
        } catch (SQLException ex) {
           theConcours.GetLogger().log(Level.SEVERE, null, ex);
           okDialog("SQLException while saving Base DB.");
           theConcours.GetLogger().info("SQLException while saving Base DB.");
           //(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_SaveBaseMenuItemActionPerformed

    private void doTheBaseDBSave() throws SQLException{
        theConcours.GetLogger().log(Level.INFO, "Connecting to new Base database: {0}", strNewBaseDBFile);
        try {
          Class.forName("org.sqlite.JDBC");
          String strConn;
          strConn = "jdbc:sqlite:" + strNewBaseDBFile;
          baseMasterConnection = DriverManager.getConnection(strConn);
          
        } catch ( ClassNotFoundException | SQLException e ) {
            okDialog( "Exception in DB connection in doTheBaseDBSave");
            theConcours.GetLogger().log(Level.SEVERE, "Exception in doTheBaseDBSave", e);
        }
        theConcours.GetLogger().info("Connected to new Base database " + strNewBaseDBFile + " in SaveBaseMenuItemActionPerformed()");
        // Create Settings table
        LoadSQLiteConcoursDatabase.CreateSettingsStateTable(baseMasterConnection);
        LoadSQLiteConcoursDatabase.SetSettingsTableJAState(baseMasterConnection, false);
        loadSQLiteConcoursDatabase.CreateConcoursTables(baseMasterConnection);

/////////////////  6/1/2017
    //
    //    And then the actual JCNAClasses and JCNAClassRules DB tables. This requires dropping the existing tables.
    //    IN THE PROPER ORDER
    //
    try {
        baseMasterConnection.setAutoCommit(false);
        Statement stat = baseMasterConnection.createStatement();
        stat.executeUpdate("drop table if exists JCNAClassRules;");
        baseMasterConnection.commit();
        baseMasterConnection.setAutoCommit(true);
        stat.close();
    } catch (SQLException ex){
        theConcours.GetLogger().log(Level.SEVERE, "SQLException dropping JCNAClassRules", ex);
    }
    
    try {
        baseMasterConnection.setAutoCommit(false);
        Statement stat = baseMasterConnection.createStatement();
        stat.executeUpdate("drop table if exists JCNAClasses;");
        baseMasterConnection.commit();
        baseMasterConnection.setAutoCommit(true);
        stat.close();
    } catch (SQLException ex){
        theConcours.GetLogger().log(Level.SEVERE, "SQLException dropping JCNAClasses", ex);
    }
/////////////////  6/1/2017
        // Now write JCNA Classes to new Base DB
        LoadSQLiteConcoursDatabase.WriteJCNAClassesTableFromMemToDB(baseMasterConnection, "JCNAClasses");
        // Write JCNA Class Rules to new Base DB
        LoadSQLiteConcoursDatabase.WriteJCNAClassRulesTableFromMemToDB(baseMasterConnection,  "JCNAClasses", "JCNAClassRules", theConcours);
        
        theConcours.GetLogger().info("Starting the write to new Base DB file");
        long startTime = System.nanoTime();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //getComponent(0).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        LoadSQLiteConcoursDatabase.WriteMasterPersonnelAndJaguarTablesFromMemToDB(baseMasterConnection);
        setCursor(Cursor.getDefaultCursor()); 
//
        // Write MasterPersonnel & masterJaguar to new Base DB in a new thread and show a progress bar
        //
        //  NOTE:  Couldn't get this to work reliably so decided to just use a twirling "Wait" cursor.
        //
       /* BaseDBWriterWithProgressBox baseDBWriterWithProgressBox = new BaseDBWriterWithProgressBox(baseMasterConnection);      
        try {
            baseDBWriterWithProgressBox.runCalc();
        } catch (InterruptedException ex) {
            theConcours.GetLogger().info("InterruptedException in baseDBWriterWithProgressBox.runCalc()");
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        */
        long duration = (System.nanoTime() - startTime);  //divide by 1000000 to get milliseconds.            
        okDialog("Finished creating new Base database. Time: " + duration/1000000 + " milliseconds");
        theConcours.GetLogger().log(Level.INFO, "Finished creating new Base database. Duration =  {0} milliseconds", duration/1000000);
        baseMasterConnection.close();
    }
    
    private void ChangeJudgeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeJudgeMenuItemActionPerformed
        Connection conn = theConcours.GetConnection();
        String msg = "";
        try {
            if(conn.isClosed()){
                msg = "Database is Closed in ChangeJudgeMenuItemActionPerformed() ";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        int numJudges = theConcours.GetConcoursJudges().size();
        if(numJudges <= 0){ 
            String msg2 = "There are no Concours Judges to change.";
            okDialog(msg2);
        } else {
            JudgeRepository concoursJudgeList = new JudgeRepository(theConcours);
            ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
            ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
            JCNAClass[] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()]);
            // last argument true => Add a Concours Judge fales => edit a Concours Judge
            EditConcoursJudgeDialog modifyConcoursJudgeDialog = new EditConcoursJudgeDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, concoursJudgeList, classMasterArray, false);
            modifyConcoursJudgeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    theConcours.GetLogger().info("Returning from ModifyConcoursJudgeDialog to main CB GUI");
                }
            });        
            modifyConcoursJudgeDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            modifyConcoursJudgeDialog.setVisible(true);
            if(!theConcours.GetJudgeAssignmentCurrent()){
                JASchedule.setForeground(Color.RED);
            }
            
        }

    }//GEN-LAST:event_ChangeJudgeMenuItemActionPerformed

    private void AddJudgingTeamMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddJudgingTeamMenuItemActionPerformed
        AddCustomJudgeTeamDialog theDialog = new AddCustomJudgeTeamDialog(new javax.swing.JFrame(), true, theConcours);
        theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                theConcours.GetLogger().info("Returning from AddJudgingTeamMenuItemActionPerformed to main CB GUI");
             //System.exit(0);
            }
        });

       theDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
       theDialog.setVisible(true);
        if(!theConcours.GetJudgeAssignmentCurrent()){
            JASchedule.setForeground(Color.RED);
        }
    }//GEN-LAST:event_AddJudgingTeamMenuItemActionPerformed

    private void ChangeJudgingTeamMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeJudgingTeamMenuItemActionPerformed
        EditCustomJudgeTeamDialog theDialog = new EditCustomJudgeTeamDialog(new javax.swing.JFrame(), true, theConcours);
        theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        theDialog.setVisible(true);
        if(!theConcours.GetJudgeAssignmentCurrent()){
            JASchedule.setForeground(Color.RED);
        }
        
        
    }//GEN-LAST:event_ChangeJudgingTeamMenuItemActionPerformed

    private void RemoveJudgingTeamMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveJudgingTeamMenuItemActionPerformed
        RemoveCustomJudgeTeamDialog theDialog = new RemoveCustomJudgeTeamDialog(new javax.swing.JFrame(), true, theConcours);
        theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

        theDialog.setVisible(true);
        /*if(!theConcours.GetJudgeAssignmentCurrent()){
            JASchedule.setForeground(Color.RED);
        }
        */
    }//GEN-LAST:event_RemoveJudgingTeamMenuItemActionPerformed

    private void judgingTeamsMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_judgingTeamsMenuMenuSelected
       boolean flag = theConcours.GetPreassignedJudgesFlag();
       ChangeJudgingTeamMenuItem.setEnabled(flag);
       RemoveJudgingTeamMenuItem.setEnabled(flag);
    }//GEN-LAST:event_judgingTeamsMenuMenuSelected

    private void DisplayMasterPersonnelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayMasterPersonnelMenuItemActionPerformed
        MasterPersonnel  theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        txtArea.append("Master Personnel(" +theMasterPersonnel.GetMasterPersonnelList().size()+ "):\n");
        for(MasterPersonExt p  : theMasterPersonnel.GetMasterPersonnelList()){
            txtArea.append(" Unique name: " + p.getUniqueName());
            int k = 0;
            for(MasterJaguar mj : p.getJaguarStableList()){
                if(k == 0) txtArea.append(" Stable: ");
                if(k > 0) txtArea.append(", ") ;
                txtArea.append(mj.getUniqueDesc());
                k++;
            }
            
            txtArea.append("\n");
        }    
    }//GEN-LAST:event_DisplayMasterPersonnelMenuItemActionPerformed

    private void AddMPMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMPMenuItemActionPerformed
        Connection conn = theConcours.GetConnection();
        AddMasterPersonDialog theDialog = new AddMasterPersonDialog(new javax.swing.JFrame(), true, conn, theConcours,  false);
        theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Returning from AddMPMenuItemActionPerformed to main CB GUI");
            }
        });        
        theDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        theDialog.setVisible(true);
    }//GEN-LAST:event_AddMPMenuItemActionPerformed

    private void RemoveMasterPersonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveMasterPersonMenuItemActionPerformed
       //okDialog("Starting Remove Master Person Dialog");
       theConcours.GetLogger().info("Starting Remove Master PersonDialog");
        //  8/25/2018 
        MasterPersonnel  theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        int MPSize = theMasterPersonnel.GetMasterPersonnelList().size();
        if(MPSize <= 0){
            String msg = "There are no Master Persons to remove.";
            okDialog(msg);
        } else {
           Connection conn = theConcours.GetConnection();
            //MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);
            RemoveMasterPersonDialog removeMasterPersonDialog = new RemoveMasterPersonDialog(new javax.swing.JFrame(), true, conn, theConcours, false);
            theConcours.GetLogger().info("Finished constructing RemoveMasterPersonDialog()");
            removeMasterPersonDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    theConcours.GetLogger().info("Returning from Remove MasterPerson to main CB GUI");
                }
            });        
            removeMasterPersonDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            removeMasterPersonDialog.setVisible(rootPaneCheckingEnabled);            
        }

    }//GEN-LAST:event_RemoveMasterPersonMenuItemActionPerformed

    
    private void DisplayCustomJudgingTeamsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayCustomJudgingTeamsMenuItemActionPerformed
        ArrayList<ConcoursClass>  theClasses = theConcours.GetConcoursClasses();
        ArrayList<ConcoursClass> classesWithPreassignedTeams = new ArrayList<>();
        for(ConcoursClass cc  : theClasses){
           ArrayList<String> pat = cc.GetClassPreassignedJudgeNameList();
           if(pat.size() > 0) classesWithPreassignedTeams.add(cc);
        }
        txtArea.append("\nCustom Judging Teams(" + classesWithPreassignedTeams.size() + "):\n");
        int j = 0;
        for(ConcoursClass cc  : classesWithPreassignedTeams){
           if (j == 0){
               txtArea.append("Class: " + cc.toString() + "  Team: ");
           } else{
               txtArea.append("\nClass: " + cc.toString() + "  Team: ");
           }
           ArrayList<String> pat = cc.GetClassPreassignedJudgeNameList();
           int k = 0;
           for(String name : pat){
               if(k == 0){
                   txtArea.append(name);
               } else{
                   txtArea.append(", " + name);
               }
               k++;
           }
           j++;
        }

    }//GEN-LAST:event_DisplayCustomJudgingTeamsMenuItemActionPerformed

    private void DisplayJudgesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayJudgesMenuItemActionPerformed
        ArrayList<Judge> theJudges = theConcours.GetConcoursJudges();
        txtArea.append("\nConcours Judges(" + theJudges.size() +"):\n");
        for(Judge j  : theJudges){
           txtArea.append("  " + j.getUniqueName() + "\n"); 
        }
    }//GEN-LAST:event_DisplayJudgesMenuItemActionPerformed

    private void ConcoursOpenFileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConcoursOpenFileChooserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ConcoursOpenFileChooserActionPerformed

    private void DeleteConcoursMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteConcoursMenuItemActionPerformed
            theConcours.GetLogger().info("Entering DeleteConcoursMenuItemActionPerformed");
            String strConcoursFolderPath = "";
            String strSelectedConcoursName = "";
            ConcoursChooserDialog dialog = new ConcoursChooserDialog(new javax.swing.JFrame(), true,  theConcours, "Delete", concoursBuilderDataPath);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    
                }
            });
            dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog.
            //dialog.setVisible(rootPaneCheckingEnabled);
            int resp = dialog.showOpenDialog(); // will be either JFileChooser.APPROVE_OPTION or JFileChooser.CANCEL_OPTION
            if(resp == JFileChooser.APPROVE_OPTION){
                strConcoursFolderPath = dialog.getSelectedFolderFullPath();
                strSelectedConcoursName = dialog.getSelectedFolderName();
                int response = yesNoDialog("Do you really want to delete " + strConcoursFolderPath + "?");
                if(response == JOptionPane.YES_OPTION) {
                    File dir = new File(strConcoursFolderPath);
                    MyJavaUtils.recursivelyeleteFiles(dir);
                    /*
                   File dir = new File(strConcoursFolderPath);
                   boolean dirExist = dir.exists() && dir.isDirectory();
                   if(dirExist) {
                       String strDbFile = dialog.getSelectedFolderName() + ".db";
                       File db = new File(dir + "\\" + strDbFile);
                       if(db.exists()) db.delete();
                       theConcours.GetLogger().log(Level.INFO, "Concours {0} deleted.", strDbFile);
                       File[] listOfFiles = dir.listFiles();
                       if(listOfFiles.length == 0){
                           theConcours.GetLogger().log(Level.INFO, "Try to delete the {0} folder", strSelectedConcoursName);
                           if(dir.delete()){
                               theConcours.GetLogger().log(Level.INFO, "Concours folder {0} deleted.", strSelectedConcoursName);    
                           } else {
                               theConcours.GetLogger().log(Level.INFO, "Concours folder {0} NOT deleted.", strSelectedConcoursName); 
                           }
                       }
                   } 
                   */
                
                }
                
            } else {
                //okDialog("Cancel button clicked");
            }
            
            
            theConcours.GetLogger().info("DeleteConcoursMenuItemActionPerformed() Finished");
    }//GEN-LAST:event_DeleteConcoursMenuItemActionPerformed

    private void AcknowledgmentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcknowledgmentsMenuItemActionPerformed
        AcknowlegmentsCBDialog ackCBd = new AcknowlegmentsCBDialog(new javax.swing.JFrame(), true, theConcours );
        ackCBd.setVisible(true);
    }//GEN-LAST:event_AcknowledgmentsMenuItemActionPerformed

    private void AboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutMenuItemActionPerformed
        //String version = theConcours.GetCBVersion();
        //okDialog("ConcoursBuilder version: " + version + " Build number: " + getRbTok("BUILD"));
        AboutCBDialog aCBd = new AboutCBDialog(new javax.swing.JFrame(), true, theConcours );
        aCBd.setVisible(true);
    }//GEN-LAST:event_AboutMenuItemActionPerformed

    private void UsersManualmenuOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsersManualmenuOptionActionPerformed

        //ConcoursBuilder Users Manual.pdf is in the installation folder e.g. C:\Program Files\ConcoursBuilder

        //String installPath = theConcours.GetInstallationPath();
        //String usersManualPath = installPath  + "\\ConcoursBuilder Users Manual.pdf";
        String usersManualPath = theConcours.GetConcoursBuilderDocsPath()  + "\\ConcoursBuilder Users Manual.pdf";
        String msg_fnf = "Could not find " +  usersManualPath + "\nCheck to see if the file exists.";
        String msg_cno = "Could not open " +  usersManualPath + "\nYour computer does not have default PDF reader. Install Acrobat." ;
        if (Desktop.isDesktopSupported()) {
            try {
                // Path path = Paths.get(usersManualPath); // this is the the absolute path to the DB file as a Path
                //String strFilePath = path.toString();

                File theUMFile = new File(usersManualPath);
                //String msg;
                //if(theUMFile.exists()) msg = "File " + usersManualPath + " exists"; else msg = "File " + usersManualPath + " does not exist";
                //okDialog(msg);

                Desktop.getDesktop().open(theUMFile);
            }
            catch (FileNotFoundException ex){
                okDialog(msg_fnf);
                theConcours.GetLogger().log(Level.SEVERE, null, ex);
                theConcours.GetLogger().info(msg_fnf);
            }
            catch (IllegalArgumentException ex) {
                okDialog(msg_fnf);
                theConcours.GetLogger().log(Level.SEVERE, null, ex);
                theConcours.GetLogger().info(msg_fnf);
            }
            catch (IOException ex) {
                okDialog(msg_cno);
                theConcours.GetLogger().log(Level.SEVERE, null, ex);
                theConcours.GetLogger().info(msg_cno);
            }
        }
    }//GEN-LAST:event_UsersManualmenuOptionActionPerformed

    private void JCNAClassFinderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JCNAClassFinderActionPerformed
        JCNAClassChooserGUI theClassChooser = new JCNAClassChooserGUI(false, theConcours);
        theClassChooser.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Returning from JCNAClassChooserGUI to main CB GUI");
            }
        });
        theClassChooser.setDefaultCloseOperation(HIDE_ON_CLOSE);
        theClassChooser.setVisible(true);
    }//GEN-LAST:event_JCNAClassFinderActionPerformed



    private void ScoreSheetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ScoreSheetsActionPerformed
        Connection conn = theConcours.GetConnection();

        CreateScoreSheetsDialog createScoreSheetsDialog;
        createScoreSheetsDialog = new CreateScoreSheetsDialog(new javax.swing.JFrame(), true, conn, theConcours);
        createScoreSheetsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Returning from createScoreSheetsDialog to main CB GUI");
            }
        });

        //createScoreSheetsDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        createScoreSheetsDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog... allows checking for boolean initialSetting
        createScoreSheetsDialog .setVisible(true);
    }//GEN-LAST:event_ScoreSheetsActionPerformed

    private void PlacardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PlacardsActionPerformed

       //String strCurDir = Paths.get(".").toAbsolutePath().normalize().toString(); 
       okDialog("This operation moved to EditJA");
        //CreateWindscreenPlacards   createWindscreenPlacards = new CreateWindscreenPlacards(theConcours, concoursBuilderDocsPath, concoursBuilderDataPath);
        //createWindscreenPlacards.fillInAllPlacardForms();
    }//GEN-LAST:event_PlacardsActionPerformed

    private void LoadJCNAClassesAndRulesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadJCNAClassesAndRulesMenuItemActionPerformed
        theConcours.GetLogger().info("Started LoadJCNAClassesAndRulesMenuItemActionPerformed--- load from CSV\n");
        int numEntries = theConcours.GetEntriesList().size();
        int numJudges = theConcours.GetConcoursJudges().size();
        String msg = "If JCNA Classes & Rules have been changed (by the JCNA Rules Committee), the JCNAClasses and JCNAClassRules tables "
                    + "must be updated using this command.\nNormally, this should  be done BEFORE adding any Entries or Judges to a new Concours. Eliminating or changing a JCNA Class name "
                    + " while working with a Concours\nwith existing Entries or Judges can cause unexpected results. On the other hand, adding a Class or Rule is OK.\n"
                    + "See User's Manual for more information.\n\n"
                    + "Are you sure you want to continue?";
        if(numEntries > 0 || numJudges > 0){
            int response = yesNoDialog(msg);
            if(response != JOptionPane.YES_OPTION) {
                return;
            }
        }
        // boolean result;
        int numClassesFields = 6;
        int numRulesFields = 10;
        //
        //  User browse to the JCNAClasses.csv file.
        //
        CSVFileChooser.setCurrentDirectory(new File(concoursBuilderDataPath));
        CSVFileChooser.setDialogTitle("Select JCNA Classes CSV file");
        //CSVFileChooser.setFileFilter(new MyCustomFilterCSV() );
        CSVFileChooser.setFileFilter(new CustomCSVFileFilter("Classes") );
        
        String strClassesCSVFileAbsPath = null;
        //String strCSVFileName;
        String classesfileName;
        int returnVal = CSVFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File flCSVFile = CSVFileChooser.getSelectedFile();
            strClassesCSVFileAbsPath = flCSVFile.getAbsolutePath(); // this is the the absolute path to the CSV file as a string
            Path path = Paths.get(strClassesCSVFileAbsPath); // this is the the absolute path to the CSV file as a Path
            classesfileName = path.getFileName().toString();    // name only        
            File f = new File(classesfileName);
            //strCSVFileName = f.getName();
        } else{
            theConcours.GetLogger().info("User cancelled loading JCNAClasses CSV file chooser");
            return;
        }
        //
        // Check the structure of the JCNAClasses.csv file.
        //

        List<String> listBadClassesCSVStructure = checkCSVStructure(strClassesCSVFileAbsPath.toString(), numClassesFields);
        if(!listBadClassesCSVStructure.isEmpty()){
            String msg1 = "These rows in " + strClassesCSVFileAbsPath.toString() + " do not have exactly " + numClassesFields + " fields.\n Probable cause is too few or too many commas. Check Description and Notes.\n";
            for(String line : listBadClassesCSVStructure){
                msg1 = msg1 + line + "\n";
            }
            okDialog( msg1 );
            //okDialog("These rows in " + strClassesCSVFileAbsPath.toString() + " do not have exactly " + numClassesFields + " fields. Probable cause is commas in Description or Notes." +  listclasses );
            theConcours.GetLogger().info(msg1 );
            return;
        } else {
            //okDialog("Structure of rows in " + strClassesCSVFileAbsPath.toString() + " checked OK");
            theConcours.GetLogger().info("Structure of rows in " + strClassesCSVFileAbsPath.toString() + " checked OK");
        }
        //
        //  Check the form of JCNA Class names... must be dnn/s where d = C||D||S, nn = 2 numeric characters, s is a string of uppercase alpha & numeric
        //
        List<String> badClassNames = checkCSVClassNameForm(strClassesCSVFileAbsPath.toString(), numClassesFields);
        if(!badClassNames.isEmpty()){
            String msg1 = "These rows in " + strClassesCSVFileAbsPath.toString() + " have problems with Division or JCNAClass names.\n"
                    + "Note that:\n" 
                    + "    Division must be Championship, Driven, or Special.\n"
                    + "    Class name must start with the first character of the Division. E.g. Championship, C01/PRE\n"
                    + "    Class number must be 2 digits, e.g., D02.\n"
                    + "    Class number must be followed by / and a mnemonic, e.g. C01/PRE.\n";
            for(String line : badClassNames){
                msg1 = msg1 + line + "\n";
            }
            okDialog( msg1 );
            theConcours.GetLogger().info(msg1);
            return;
        } else {
            //okDialog("Class names in " + strClassesCSVFileAbsPath.toString() + " checked OK");
            theConcours.GetLogger().info("Class names in " + strClassesCSVFileAbsPath.toString() + " checked OK");
        }
        //
        //  Get a Lists of JCNA Class names from JCNAClasses.csv to be checked against JCNAClassRules.csv
        //

       
        CSVFileChooser.setDialogTitle("Select JCNA Class Rules CSV file");
        CSVFileChooser.setFileFilter(new CustomCSVFileFilter("Rules") );
        
        String strRulesCSVFileAbsPath = null;
        //String strRulesCSVFileName;
        //String fileName;
        returnVal = CSVFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File flCSVFile = CSVFileChooser.getSelectedFile();
            strRulesCSVFileAbsPath = flCSVFile.getAbsolutePath(); // this is the the absolute path to the CSV file as a string
            //Path path = Paths.get(strRulesCSVFileAbsPath); // this is the the absolute path to the CSV file as a Path
            //fileName = path.getFileName().toString();    // name only        
            //File f = new File(fileName);
            //strRulesCSVFileName = f.getName();
        } else{
            theConcours.GetLogger().info("User cancelled Class Rules CSV file chooser");
            return;
        }
        //
        // Check the structure of the JCNAClassRules.csv file.
        //
        // Check structure of file
        List<String> listrules = checkCSVStructure(strRulesCSVFileAbsPath, numRulesFields);
        if(!listrules.isEmpty()){
            String msg1 = "These rows in " + strClassesCSVFileAbsPath.toString() + " do not have exactly " + numClassesFields + " fields.\n Probable cause is commas in Description or Notes.\n";
            for(String line : listrules){
                msg1 = msg1 + line + "\n"; // the "/n" is unnecessary because "line" seems to end with one...
            }
            okDialog( msg1 );
            return;
        } else {
            //okDialog("Structure of " + strRulesCSVFileAbsPath + " checked OK");
            theConcours.GetLogger().info("Structure of " + strRulesCSVFileAbsPath + " checked OK");
        }
        
        //
        // Now check to see that the Classes in JCNAClasses.cvs are the same as in JCNAClassRules.cvs 
        // 7/24/2017 Using the NEW Model Year Lookup field in JCNAClasses.cvs. This will
        // avoid embedding current JCNA Class names in the code as was done previously
        //
        List<String> lookupJCNAClassNamesFromClassesCSV = new ArrayList<>(); // only the JCNAClasses that can be looked up based on Model Year
        List<String[]> lstLinesFromJCNAClassesCSV = getLinesFromCSV(strClassesCSVFileAbsPath);
        for(String[] line : lstLinesFromJCNAClassesCSV){
            String cls = line[1];
            String mylookup = line[5].trim();
            if(mylookup.equals("yes")) lookupJCNAClassNamesFromClassesCSV.add(cls);
        }
        List<String> lookupJCNAClassNamesFromClassesCSVCopy = new ArrayList<>(lookupJCNAClassNamesFromClassesCSV); // since the removal will change it
        // Now get the JCNAClass names from the Rules file
        List<String> classesJCNAClassNamesFromRulesCSV = new ArrayList<>();
        List<String[]> lstLinesFromJCNAClassRuleCSV = getLinesFromCSV(strRulesCSVFileAbsPath.toString());
        for(String[] line : lstLinesFromJCNAClassRuleCSV){
            String cls = line[2];
            classesJCNAClassNamesFromRulesCSV.add(cls);
        }
        boolean r = lookupJCNAClassNamesFromClassesCSVCopy.removeAll(classesJCNAClassNamesFromRulesCSV)  ;
        if(!lookupJCNAClassNamesFromClassesCSVCopy.isEmpty()){
            String msg1 = "These JCNA Class names in " + strClassesCSVFileAbsPath + " are not present in " + strRulesCSVFileAbsPath + "\n\n";
            for(String line : lookupJCNAClassNamesFromClassesCSVCopy){
                msg1 = msg1 + line + "\n";
            }
            okDialog( msg1 );
            theConcours.GetLogger().info(msg1);
            return;
        } else {
            String msg1 = "Good news! All JCNA Class names in " + strClassesCSVFileAbsPath + " are present in " + strRulesCSVFileAbsPath + "\n";
            //okDialog( msg1 );
            theConcours.GetLogger().info(msg1);
        }

        
        // Now check to see that the Classes in JCNAClasses.cvs are the same as in JCNAClassRules.cvs 
        List<String> classesJCNAClassNamesFromClassRulesCSVCopy = new ArrayList<>(classesJCNAClassNamesFromRulesCSV); // since the removal will change it
        r = classesJCNAClassNamesFromClassRulesCSVCopy.removeAll(lookupJCNAClassNamesFromClassesCSV);
        if(!classesJCNAClassNamesFromClassRulesCSVCopy.isEmpty()){
            String msg1 = "These JCNA Class names in " + strRulesCSVFileAbsPath + " are not present in " + strClassesCSVFileAbsPath + "\n\n";
            for(String line : classesJCNAClassNamesFromClassRulesCSVCopy){
                msg1 = msg1 + line + "\n";
            }
            okDialog( msg1 );
            theConcours.GetLogger().info(msg1);
            return;
        } else {
            String msg1 = "Good news! All JCNA Class names in " + strRulesCSVFileAbsPath  + " are present in " + strClassesCSVFileAbsPath + "\n";
            okDialog( msg1 );
            theConcours.GetLogger().info(msg1);
        }
        
        // Note: Due to Foreign keys, the Rules table has to be dropped before the Classes table
        // Drop JCNAClassRulesTemp table, NOT the actual JCNAClassRules table!!!
        Statement stat1 = null;
        Statement stat2 = null;
        try {
            theConcours.GetConnection().setAutoCommit(false);
            stat1 = theConcours.GetConnection().createStatement();
            //
            // trying to see if the table exists.  Doesn't work!
            ///String qTest = "SELECT ID FROM sqlite_master WHERE type='table' AND name='JCNAClassRulesTemp';";
            //result = stat1.execute(qTest);
            //
            stat1.executeUpdate("drop table if exists JCNAClassRulesTemp;");
            theConcours.GetConnection().commit();
            stat1.close();
            // If the JCNAClassesTemp table was successfully dropped. Now, drop the JCNAClassesTEMP table
            stat2 = theConcours.GetConnection().createStatement();
            stat2.executeUpdate("drop table if exists JCNAClassesTEMP;");
            theConcours.GetConnection().commit();
            stat2.close();
            theConcours.GetConnection().commit();
        } catch (SQLException ex){
            theConcours.GetLogger().log(Level.SEVERE, "SQLException in LoadJCNAClassesMenuItemActionPerformed while attempting drop JCNAClasses table. JCNA Classes & Rules not loaded.", ex);
            okDialog("SQLException in LoadJCNAClassesMenuItemActionPerformed while attempting drop JCNAClasses table. JCNA Classes & Rules not loaded.");
            return;
        } 
        //
        // Now load both TEMP tables from CSV files, starting with the JCNAClassesTemp table.
        //
        String strPath = Paths.get(strClassesCSVFileAbsPath).toString();
        theConcours.GetLogger().info("Start LoadJCNAClassesTableFromCSV to load JCNAClassesTemp table from " + strPath);
        if(LoadJCNAClassesTableFromCSV(theConcours.GetConnection(), "JCNAClassesTemp", strPath, theConcours)){
            okDialog("Good news! Loading JCNA Classes from CSV completed successfully.");
            theConcours.GetLogger().info("JCNAClassesTemp loaded successfully in LoadJCNAClassesTableFromCSV");
        } else{
            okDialog("JCNAClassesTemp load failed in LoadJCNAClassesTableFromCSV.");
            theConcours.GetLogger().info("JCNAClassesTemp load failed in LoadJCNAClassesTableFromCSV.");
            return;
        }
        // Now load the Rules
        strPath = Paths.get(strRulesCSVFileAbsPath).toString();
        theConcours.GetLogger().info("Start LoadJCNAClassRulesFromCSV to load JCNAClassRulesTemp table from " + strPath );
        if(LoadJCNAClassRulesFromCSV(theConcours.GetConnection(), "JCNAClassesTemp" , "JCNAClassRulesTemp",  strPath, theConcours)){
            msg = "Loading JCNA Class Rules from CSV into temp file completed successfully.";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
        } else{
            msg = "Load JCNA Class Rules From CSV into temp files failed.";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
            return;
        }
        
    //        
    // Getting to here means the new CSV files were successfully read and saved in the Temp tables.
    // Now have to update the in-memory structures...
    //
    theConcours.GetJCNAClasses().getJCNAClasses().clear();
    theConcours.GetJCNAClasses().LoadJCNAClassesDB(theConcours.GetConnection(), "JCNAClassesTemp", theConcours.GetLogger());
    theConcours.GetJCNAClassRules().getJCNAClassRules().clear();
    theConcours.GetJCNAClassRules().LoadJCNAClassRulesDB(theConcours.GetConnection(), "JCNAClassRulesTemp", theConcours.GetLogger());
    //
    //    And then the actual JCNAClasses and JCNAClassRules DB tables. This requires dropping the existing tables.
    //    IN THE PROPER ORDER
    //
    try {
        theConcours.GetConnection().setAutoCommit(false);
        Statement stat = theConcours.GetConnection().createStatement();
        stat.executeUpdate("drop table if exists JCNAClassRules;");
        theConcours.GetConnection().commit();
        theConcours.GetConnection().setAutoCommit(true);
        stat.close();
        theConcours.GetLogger().info("Successfully dropped the actual JCNAClassRules");
    } catch (SQLException ex){
        msg = "SQLException dropping JCNAClassRules";
        okDialog(msg);
        theConcours.GetLogger().log(Level.SEVERE, msg, ex);
    }
    
    try {
        theConcours.GetConnection().setAutoCommit(false);
        Statement stat = theConcours.GetConnection().createStatement();
        stat.executeUpdate("drop table if exists JCNAClasses;");
        theConcours.GetConnection().commit();
        theConcours.GetConnection().setAutoCommit(true);
        stat.close();
        theConcours.GetLogger().info("Successfully dropped the actual JCNAClasses");
    } catch (SQLException ex){
        msg = "SQLException dropping JCNAClasses";
        okDialog(msg);
       theConcours.GetLogger().log(Level.SEVERE, msg, ex);
    }
    // Now write out to DB
    LoadSQLiteConcoursDatabase.WriteJCNAClassesTableFromMemToDB(theConcours.GetConnection(), "JCNAClasses");
    LoadSQLiteConcoursDatabase.WriteJCNAClassRulesTableFromMemToDB(theConcours.GetConnection(), "JCNAClasses",  "JCNAClassRules", theConcours);
    
    try {
        theConcours.GetConnection().setAutoCommit(true);
    } catch (SQLException ex) {
        msg = "SQLException while resetting autocommit(true)";
        //Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
        okDialog(msg);
        theConcours.GetLogger().log(Level.SEVERE, msg, ex);
    }
    
    }//GEN-LAST:event_LoadJCNAClassesAndRulesMenuItemActionPerformed

    private void UserSettingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserSettingsMenuItemActionPerformed
                UserSettingsDialog dialog = new UserSettingsDialog(new javax.swing.JFrame(), true, theConcours);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        return;
                    }
                });
                dialog.setVisible(true);
    }//GEN-LAST:event_UserSettingsMenuItemActionPerformed

    private void EditMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditMenuActionPerformed

    private void ExportEntriesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportEntriesMenuItemActionPerformed
        PrintWriter out = null;
        String strFullPath = "";
        try {
            strFullPath = theConcours.GetConcoursBuilderDataPath() + "\\" + theConcours.GetConcoursName().replaceFirst(".db", "") + "\\"  + "ConcoursEntries.txt";
            out = new PrintWriter(strFullPath);
        } catch (FileNotFoundException ex) {
            String msg = "Could not open print writer for Concours Entries";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        Entries theEntries = theConcours.GetEntries();
        String HeaderLine = "\nConcours Entries(" + theEntries.GetConcoursEntries().size() +") for "  + theConcours.GetConcoursName();
        out.println(HeaderLine);
        out.println();
        for(Entry e  : theEntries.GetConcoursEntries()){
           String mi = e.GetOwnerMI();
           if(mi == null) mi = "";
           out.println("  " + e.GetUniqueDescription() + "  " + e.GetClassName() + " Owner: " + e.GetOwnerLast() + "," + e.GetOwnerFirst() + " " + mi); 
        }
        out.close();
        String msg = "Exported Entries to " + strFullPath;
        okDialog(msg);
        theConcours.GetLogger();
    }//GEN-LAST:event_ExportEntriesMenuItemActionPerformed

    private void ExportJudgesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportJudgesMenuItemActionPerformed
        PrintWriter out = null;
         String strFullPath = "";
        try {
            strFullPath = theConcours.GetConcoursBuilderDataPath() + "\\" + theConcours.GetConcoursName().replaceFirst(".db", "") + "\\" + "ConcoursJudges.txt";
            out = new PrintWriter(strFullPath);
        } catch (FileNotFoundException ex) {
            String msg = "Could not open print writer for Concours Judges";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        Judges theJudges = theConcours.GetConcoursJudgesObject();
        String HeaderLine = "\nConcours Judges(" + theJudges.GetConcoursJudges().size() +") for "  + theConcours.GetConcoursName();
        out.println(HeaderLine);
        out.println();
        for(Judge j  : theJudges.GetConcoursJudges()){
           out.println(j.GetLastName() + "," + j.GetFirstName()); 
        }
        out.close();
        String msg = "Exported Judges to " + strFullPath;
        okDialog(msg);
        theConcours.GetLogger();
    }//GEN-LAST:event_ExportJudgesMenuItemActionPerformed

    
    private void BackupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackupMenuItemActionPerformed
        //
        // The Backup of currently open Concours is implemented by copying the database into a new file identified 
        // by the full path with "Backup" and timestamp appended. It is saved in the same folder as the current Concours.
        //
        copysaveObject = new CopySaveDBFile();
        Date date = new Date();
        String strTimestampTemp = new Timestamp(date.getTime()).toString();
        int pos = strTimestampTemp.indexOf('.');
        String fraction = strTimestampTemp.substring(pos, strTimestampTemp.length());
        String strTimestamp = strTimestampTemp.replaceFirst(fraction, "");
        strTimestamp = strTimestamp.replace(" ", "-");
        strTimestamp = strTimestamp.replace(":", ".");
        
        String strBackupFileName = strConcoursDBFile.replaceFirst(".db", "Backup-" + strTimestamp + ".db");
       //String strNewFileName = EnsureDbExtension(strTempNewFileName);
        try {
            copysaveObject.copyDBFile( flConcoursDBFile, new File(strBackupFileName), theConcours.GetLogger()); 
        } catch (IOException ex) {
            String msg = "IOException in copyDBFile( ) called from BackupMenuItemActionPerformed.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        String msg = "Concours backed up as " + strBackupFileName;
        okDialog(msg);
        theConcours.GetLogger().info(msg);
    }//GEN-LAST:event_BackupMenuItemActionPerformed
    //
    //      Restores a Concours from a Beackup
    //              7/6/2017
    private void RestoreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestoreMenuItemActionPerformed
        boolean res = true;
        Connection conn;
        String strConcoursFolderPath = "";
        String strSelectedConcoursName = "";
        //
        //  Get the target Concours folder
        //
        ConcoursChooserDialog concourschooserdialog = new ConcoursChooserDialog(new javax.swing.JFrame(), true,  theConcours, "Restore", concoursBuilderDataPath);
        concourschooserdialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
            }
        });
        concourschooserdialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog.
        int resp = concourschooserdialog.showOpenDialog(); 
        if (resp != JFileChooser.APPROVE_OPTION) {
            String msg = "User cancelled Concours Chooser Dialog";
            theConcours.GetLogger().info(msg);
            return;
        } else {
            strConcoursFolderPath = concourschooserdialog.getSelectedFolderFullPath();
            strSelectedConcoursName = concourschooserdialog.getSelectedFolderName();
            strConcoursDBFile = strConcoursFolderPath + "\\" + strSelectedConcoursName + ".db";
            File dir = new File(strConcoursFolderPath);
            boolean dirExist = dir.exists() && dir.isDirectory();
            if(!dirExist) {
                   // should not happen since it would not have been available for selection if nonexistant
                   String msg = "ERROR: Problem backing up the database file in RestoreMenuItemActionPerformed. " + dir + " or is not a Folder";
                   okDialog(msg);
                   theConcours.GetLogger().info(msg);
                   return;
            } else {               
               //
               //  Now that we have the folder for the target Concours, present User with list of Backup files
               //  to choose from.
               //
                ConcoursRestoreChooserDialog restoredialog = new ConcoursRestoreChooserDialog(new javax.swing.JFrame(), true,  theConcours,  strConcoursFolderPath);
                restoredialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                    }
                });
                restoredialog.button = "Restore selected backup file";
                restoredialog.setTitle("Select Backup file to be used for the restore");
                restoredialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // to force it to reconstruct the dialog.
                resp = restoredialog.showOpenDialog();
                if (resp != JFileChooser.APPROVE_OPTION) {
                    String msg = "User cancelled Restore file Dialog";
                    theConcours.GetLogger().info(msg);
                    return;
                } else {
                    // Full steam ahead!
                    theConcours.GetLogger().info("Get Selected Backup file");
                    String selectedBackupFileFullPath = restoredialog.getSelectedBackupFullPath();
                    //
                    // Delete the Concours db file that's to be replaced by the backup
                    //
                    
                    File flTarget = new File(strConcoursDBFile);
                    if(flTarget.exists()) flTarget.delete();
                    String msg ="Existing Concours file " + strConcoursDBFile + " deleted. Will now copy the selected backup file into NEW target";
                    theConcours.GetLogger().info(msg);
                    File flSelectedBackup = new File(selectedBackupFileFullPath);
                    try {
                        //
                        //  CopyOpenConcours the selected Backup file into the target Concours
                        //
                        theConcours.GetLogger().info("Try to copy Selected Backup file to " + flTarget.toPath());
                        CopySaveDBFile.copyDBFile(flSelectedBackup, flTarget, theConcours.GetLogger());
                        flConcoursDBFile = flTarget;
                    } catch (IOException ex) {
                        msg = "IO exception while copying backup file into target.";
                        theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                        okDialog(msg);
                        return;
                    }
                    //
                    //  Now copy the same file into the Backup file used if User decides not to save changes.
                    //  This will leave us in exactly the same stat as when a file is normally opened... 2 identical db files.
                    //
                    String strBackupFileName = strConcoursDBFile.replace(".db", "Backup.db");
                    File flBackup = new File(strBackupFileName);
                    try {
                        theConcours.GetLogger().info("Try to copy Selected Backup file to " + flTarget.toPath() + " Backup file");
                        CopySaveDBFile.copyDBFile(flSelectedBackup, flBackup, theConcours.GetLogger());
                    } catch (IOException ex) {
                       msg = "IO exception while copying the restored concours file into concours Backup.db.";
                       theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                       okDialog(msg);
                       return;
                    }
                    //
                    // load from database file to memory
                    //
                    theConcours.SetThePath(strConcoursDBFile);
                    if(strConcoursDBFile == null){
                        msg = "Null strConcoursDBFile";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg);
                        System.exit(-1);
                    }
                    theConcours.GetLogger().info("Opened Concours " + strConcoursDBFile);
                    conn = null;
                    try {
                        theConcours.GetLogger().info("Connect to Opened Concours " + strConcoursDBFile);
                        Class.forName("org.sqlite.JDBC");
                        String strConn;
                        strConn = "jdbc:sqlite:" + strConcoursDBFile ;  // PROBLEM HERE IF OPENING.... strConcoursDBFile == NULL
                        conn = DriverManager.getConnection(strConn);
                        conn.createStatement().execute("PRAGMA foreign_keys = ON");
                        theConcours.SetConnection(conn) ;

                    } catch (ClassNotFoundException | SQLException e ) {
                        theConcours.GetLogger().info(e.getClass().getName() + ": " + e.getMessage());
                        okDialog("SQL exception in while connection to " + strConcoursDBFile);
                        return;
                    }
                    // If user has used Windows to create a copy of a Concours the the Windows name will differ from what's in the database.
                    // This will change the name in the database to agree with the Windows name.
                    // The importance of this is so the name at the top of the JA schedule in the pdf files will be correct.
                    String concoursnameDB = LoadSQLiteConcoursDatabase.GetSettingsTableConcoursName(conn);
                    if(!(concoursnameDB.equals(strSelectedConcoursName + ".db"))){
                         msg = "Concours name from database is " + concoursnameDB + ". Will reset it to the Windows file name " + strSelectedConcoursName + ".db";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg);
                        try {
                            LoadSQLiteConcoursDatabase.SetSettingsTableConcoursName(conn, strSelectedConcoursName + ".db");
                        } catch (SQLException ex) {
                            theConcours.GetLogger().log(Level.SEVERE, "SQLException: SetSettingsTableConcoursName failed. Bailing out", ex);
                            System.exit(-1);
                        }
                    }
                    //
                //  this for backward compatibility
                // Now create Folders for theScoresheets & windscreen placards if they don't exist... 
                // First, Placard folders
                File filePlacards = new File(strConcoursFolderPath + "\\Placards");
                if(!(filePlacards.exists() && filePlacards.isDirectory()))     (filePlacards).mkdir();
                File filePlacardsChampionSpecial = new File(strConcoursFolderPath + "\\Placards\\ChampionSpecial");
                if(!(filePlacardsChampionSpecial.exists() && filePlacardsChampionSpecial.isDirectory()))     (filePlacardsChampionSpecial).mkdir();
                File filePlacardsDriven = new File(strConcoursFolderPath +  "\\Placards\\Driven");
                if(!(filePlacardsDriven.exists() && filePlacardsDriven.isDirectory()))    (filePlacardsDriven).mkdir();
                // Now, Scoresheet folders
                File fileScoresheets = new File(strConcoursFolderPath + "\\Scoresheets");
                if(!(fileScoresheets.exists() && fileScoresheets.isDirectory()))     (fileScoresheets).mkdir();
                File fileScoresheetsChampionSpecial = new File(strConcoursFolderPath + "\\Scoresheets\\ChampionSpecial");
                if(!(fileScoresheetsChampionSpecial.exists() && fileScoresheetsChampionSpecial.isDirectory()))     (fileScoresheetsChampionSpecial).mkdir();
                File fileScoresheetsDriven = new File(strConcoursFolderPath +  "\\Scoresheets\\Driven");
                if(!(fileScoresheetsDriven.exists() && fileScoresheetsDriven.isDirectory()))    (fileScoresheetsDriven).mkdir();

                    
                    boolDBFileOpened = true;
                    CopyConcoursMenuItem.setEnabled(boolDBFileOpened);
                    BackupMenuItem.setEnabled(boolDBFileOpened);
                    //CloseMenuItem.setEnabled(boolDBFileOpened);
                    NewMenuItem.setEnabled(!boolDBFileOpened);
                    OpenMenuItem.setEnabled(!boolDBFileOpened);
                    RestoreMenuItem.setEnabled(!boolDBFileOpened);
                    EditMenu.setEnabled(boolDBFileOpened);
                    DisplayMenu.setEnabled(boolDBFileOpened);
                    ToolsMenu.setEnabled(boolDBFileOpened);
                    JASchedule.setForeground(foregroundColor);
                    //ExportEntriesMenuItem.setEnabled(boolDBFileOpened);
                    //ExportJudgesMenuItem.setEnabled(boolDBFileOpened);
                    ExportMenu.setEnabled(boolDBFileOpened);

                    theConcours.GetLogger().info("Opened concours database " + strConcoursDBFile + " successfully");
                    //System.out.println("Opened database " + strConcoursDBFile + " successfully");

                    theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", theConcours.GetLogger());
                    theConcours.GetLogger().info("Loaded JCNA Classes successfully");

                    boolean classRulesOK = theConcours.GetJCNAClassRules().LoadJCNAClassRulesDB(conn, "JCNAClassRules", theConcours.GetLogger());
                    if(!classRulesOK){
                        okDialog("ERROR: LoadJCNAClassRulesDB failed in RestoreMenuItemActionPerformed()");
                         theConcours.GetLogger().info("ERROR: LoadJCNAClassRulesDB failed in RestoreMenuItemActionPerformed()");
                         return;
                    } else{
                        theConcours.GetLogger().info("Loaded JCNA Class Rules successfully");
                    }

                    // 09/19/2018
                    // Check the MasterPersonnel database structure
                    //
                    boolean isOk = checkMasterPersonnelTableStructure(conn, theConcours);
                    if(!isOk){
                        String msg2 = "Bad table Masterpersonnel structure in RestoreMenuItemActionPerformed. Database is incompatible with this version of ConcoursBuilder.";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg2);
                        System.exit(-1);
                    }
                    theConcours.LoadMasterPersonnelDB(conn, theConcours.GetLogger());
                    theConcours.GetLogger().info("Loaded MasterPersonnel successfully");
                    theConcours.LoadConcoursPersonnelDB(conn, theConcours.GetLogger());
                    theConcours.GetLogger().info("Loaded ConcoursPersonnel successfully");
                    // 09/20/2018
                    // Check the MasterJaguar database structure
                    //
                    isOk = checkMasterJaguarTableStructure(conn, theConcours);
                    if(!isOk){
                        String msg2 = "Bad table MasterJaguar structure in RestoreMenuItemActionPerformed. Database is incompatible with this version of ConcoursBuilder.";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg2);
                        System.exit(-1);
                    }

                    theConcours.LoadMasterJaguarDB(conn, theConcours.GetLogger());
                    theConcours.GetLogger().info("Loaded MasterJaguar successfully");
                    theConcours.LoadEntriesDB(conn,  theConcours.GetLogger());
                    theConcours.GetLogger().info("Loaded Entries successfully");
                    theConcours.LoadJudgesDB(conn, theConcours.GetLogger());
                    theConcours.GetLogger().info("Loaded Judges successfully");
                    // The theJudgeAssignments must be available for loading  ConcoursClasses. Otherwise, the judgelists for the Concourse classes will be empty
                    loadSQLiteConcoursDatabase.ReadJudgeAssignmentDBToMem(conn, theConcours);
                    theConcours.GetLogger().info("Read JudgeAssignment from database successfully");

                    res = theConcours.LoadConcoursClassesDB(conn, theConcours,theConcours.GetLogger());
                    if(!res){
                        msg = "ERROR: Concours Classes failed to load properly.\nPlease exit ConcoursBuilder and send the 3 ConcourseBuilder Log files in your Concourses folder to CB support. ";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg);
                        return;
                    }
                    theConcours.GetLogger().info("Read ConcoursClasses from database successfully");
                    theConcours.SetPreassignedJudgesFlag(theConcours.GetConcoursClassesObject().preassignedJudgeListsExists()); 
                    ChangeJudgingTeamMenuItem.setEnabled(theConcours.GetPreassignedJudgesFlag());
                    RemoveJudgingTeamMenuItem.setEnabled(theConcours.GetPreassignedJudgesFlag());


                    theConcours.LoadOwnersDB(conn, theConcours.GetLogger());
                    theConcours.GetLogger().info("Read Owners from database successfully");
                    // boolJudgeAssignmentCurrent is the state when the last session with this Concours database was closed
                    // true means that there is no need to build a Judge Assignment
                    boolean boolJudgeAssignmentCurrent = loadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn) ; 
                    theConcours.SetJudgeAssignmentCurrent(boolJudgeAssignmentCurrent); 
                    String strConcoursName = LoadSQLiteConcoursDatabase.GetSettingsTableConcoursName(conn);
                    theConcours.SetConcoursName(strConcoursName);
                    String frameTitle = "JCNA Concours Builder: " + strConcoursName.replace(".db", "");
                    this.setTitle(frameTitle);
                    // 7/30/2017
                    // 
                    //  checkAndFixUserSettingsTable()  is needed in order to run Concours DB files from earlier ConcoursBuilder versions
                    //  before subtitlefonts, chiefjudge, & concourschari fields were added to usersettings table
                    // First, check to see if the theConcours has a usersettings table, and that it has the correct structure.
                    // If not, a new one is created.
                    //
                    checkAndFixUserSettingsTable(conn);
                    //
                    // UserSettings table now ok so we can load it
                    loadSQLiteConcoursDatabase.LoadConcoursUserSettingsDB(conn, theConcours, theConcours.GetLogger());
                    // Calculate Lunch time, rounded up to nearest 15 minute clock position
                    String startTime = theConcours.GetConcoursStartTime();
                    Integer timeslotInterval = theConcours.GetConcoursTimeslotInterval();
                    Integer timeslotsBeforeLunch = theConcours.GetConcoursTimeslotsBeforeLunch();
                    String lunchTime = MyJavaUtils.calculateLunchtime(theConcours.GetLogger(), startTime, timeslotInterval, timeslotsBeforeLunch);
                    theConcours.SetConcoursLunchTime(lunchTime);
                    // Manual Editing of Judge Assignment is OK if boolJudgeAssignmentCurrent  == true.
                    // If false, no edditing is allowed until a new Judge Assignment is performed. 
                    EditJASchedule.setEnabled(boolJudgeAssignmentCurrent); 
                    theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is initialized here and then again after any change

                    theConcours.createCarColorMap();

                    // +++++ 7/14/2016
                    if(!theConcours.GetJudgeAssignmentCurrent()){
                        JASchedule.setForeground(Color.RED);
                    }
                    theConcours.GetLogger().info("Finished loading Concours Judge Assignments, Owners, Entries, Judges, Concours Classes, & JCNA Classes  from " + theConcours.GetThePath());
                }
            }
        } 
    }//GEN-LAST:event_RestoreMenuItemActionPerformed

    private void DisplayClassJudgeCandidatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayClassJudgeCandidatesActionPerformed
            ArrayList<String> result = new ArrayList<>();
            //int jClass = 0;
            this.txtArea.append("\n\nClass Juddging Candidates:");
            for(ConcoursClass cc : theConcours.GetConcoursClasses()){
                ArrayList<String> classJudgeCandidates = new ArrayList<>();
                int judgeCandidateCount = 0;
                String className = cc.GetClassName();
                this.txtArea.append(" \n" + className + ":");
                ArrayList<Integer> judgeIndicies = cc.GetClassPreassignedJudgeIndiciesList();
                judgeCandidateCount = judgeIndicies.size();
                if(judgeCandidateCount == 0){
                    // No preassigned judging team so look to class reject lists to count number of candidates
                    ArrayList<Judge> judgeList = theConcours.GetConcoursJudges();
                    for(Judge judge : judgeList){
                        ArrayList<String> rejectList = judge.GetRejectClasses();
                        ArrayList<String> selfjudgeList = judge.GetSelfEntryClasses(); // added 9/28/2017
                        if(!rejectList.contains(className) && !selfjudgeList.contains(className)) {
                            judgeCandidateCount++;
                            classJudgeCandidates.add(judge.getUniqueName());
                        }
                    }
                } else {
                    // Get the Judges from the preassigned list for the class
                    for(Integer judgeIndex : judgeIndicies){
                        Judge judge = theConcours.GetConcoursJudge(judgeIndex);
                        classJudgeCandidates.add(judge.getUniqueName());
                    }
                }
                for(String judgeName : classJudgeCandidates){
                    this.txtArea.append("\n\t" + judgeName);
                }
            }
    }//GEN-LAST:event_DisplayClassJudgeCandidatesActionPerformed

    private void ExportMasterPersonnelAndJaguarsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportMasterPersonnelAndJaguarsMenuItemActionPerformed
        PrintWriter out = null;
         String strFullPath = "";
        try {
            strFullPath = theConcours.GetConcoursBuilderDataPath() + "\\" + theConcours.GetConcoursName().replaceFirst(".db", "") + "\\" + "MasterPersonnel.txt";
            out = new PrintWriter(strFullPath);
        } catch (FileNotFoundException ex) {
            String msg = "Could not open PrintWriter for Master Personnel & Jaguars";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        MasterPersonnel theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        String HeaderLine = "\nMasterPersonnel(" + theMasterPersonnel.GetMasterMersonnelSize() +") for "  + theConcours.GetConcoursName();
        out.println(HeaderLine);
        out.println();
        for(MasterPersonExt mp  : theMasterPersonnel.GetMasterPersonnelList()){
           out.println(mp.getLastName() + "," + mp.getFirstName() + ", " + mp.getMI()) ; 
        }
        out.close();
        String msg = "Exported Master Personnel to " + strFullPath;
        okDialog(msg);
        theConcours.GetLogger();
    }//GEN-LAST:event_ExportMasterPersonnelAndJaguarsMenuItemActionPerformed

    private void EntriesCSVMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EntriesCSVMenuItemActionPerformed
        try {
            String strFullPath;
            strFullPath = theConcours.GetConcoursBuilderDataPath() + "\\" + theConcours.GetConcoursName().replaceFirst(".db", "") + "\\"  + "ConcoursEntries.csv";
            List<String[]> allElements = new ArrayList<>();
            Entries theEntries = theConcours.GetEntries();
            //                     0       1       2       3            4            5         6           7             8          8             10             11                  12               13         
            String[] headerRow = {"ID", "Class", "Year", "Model", "Description",  "Color", "Plate/VIN", "OwnerFirst", "OwnerMI", "Ownerlast", "OwnerUnique", "Owner JCNANum", "Street Address", "City State Zip"};
            allElements.add(headerRow);
            MasterPersonnel mpersonnel = theConcours.GetMasterPersonnelObject();
            for(Entry e  : theEntries.GetConcoursEntries()){
                String   ownerUnique = e.GetOwnerUnique();
                //    String aID, String aClass,  String aYear,    String aDescription,    String aUniqueDescription, String aOwnerFirst,    String aOwnerLast,  String aOwnerUnique,     String aJCNA,    String aColor,    String aPlate,    Integer aNode
                String[] row = new String[14]; // a row in a table of Entry
                row[0] = e.GetID();
                row[1] = e.GetClassName();
                row[2] = e.GetYear();
                row[3] = e.GetModel();
                row[4] = e.GetDescription();
                row[5] = e.GetColor();
                row[6] = e.GetPlateVin();
                row[7] = e.GetOwnerFirst();
                row[8] = mpersonnel.GetMasterPersonnelMI(ownerUnique);
                row[9] = e.GetOwnerLast();
                row[10] = ownerUnique;
                row[11] = e.GetJCNA();
                row[12] = mpersonnel.GetMasterPersonnelStreetAddress(ownerUnique);
                row[13] = mpersonnel.GetMasterPersonnelCityStatePostalCode(ownerUnique);
                allElements.add(row);
            }
            CSVWriter writer = new CSVWriter(new FileWriter(strFullPath, false));
            writer.writeAll(allElements);
            writer.close();
            //System.out.println("\n\nGenerated CSV File:\n\n");
            String msg = "Exported Entries to " + strFullPath;
            okDialog(msg);
            theConcours.GetLogger();
        } catch (IOException ex) {
            String msg = "Could not open print writer for Concours Entries";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            //Logger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_EntriesCSVMenuItemActionPerformed

    private void EntryMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EntryMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EntryMenuActionPerformed

    

private static  void updateLogHistory(int size, File logFile0){
        // 
        // updateLogHistory() maintains historical log files in the MyConcourses directory. It is called in the ConcoursGUI() constructor.
        //
        // The intuition is a "stack" of log files with the current log, i.e., ConcoursBuilder_0.log,  on top. To make room for a new log file
        // for the ConcoursBuilder session, any existing log files have to be "pushed down." However, the stack is allowed to hold only a prescribed
        //number, size, elements so if there are already size files in the stack the bottom one gets deleted, or rather, overwritten. When the function
        // finishes the top TWO will be identical, so the top one can be overwritten in the caller.
        //
        // It's important to note that "stack" is a metophore here; there isn't a stack in the sense of a programed data structure. There can be only 
        // size log files in the MyConcourses folder. What actually happens in a so-called push is the CONTENTS of ConcoursBuilder_i.log 
        // gets copied into ConcoursBuilder_i+1.log, leaving  ConcoursBuilder_i.log unchanged.
        // 
        
        // The algorithm below requires an initial ConcoursBuilder_0.log. Consequently, prior to calling updateLogHistory() 
        // the directory is first checked and an empty one is created if absent. Typically, this happens only in the first 
        // run after INSTALLATION of ConcoursBuilder. However, it must be checked because the user might accidently deleted it.
        //
        // Work from the bottom up to push all existing log files down, leaving the contents of the current ConcoursBuilderLog_0 in
        // both ConcoursBuilderLog_0 and 1. the subsequent log file will overwrite both ConcoursBuilderLog_0.
        //
        String strTemp = logFile0.toString(); 
        String strPrefix = strTemp.substring(0, strTemp.indexOf("_")+1);
        for(int i =  size-1; i >=0; i--){
            String strFileFullPath = strPrefix + i + ".log";
            File logFile_i  = new File(strFileFullPath);
            if(logFile_i.exists() && !logFile_i.isDirectory()){
                Path path= Paths.get(strFileFullPath); // this is the the absolute path to the i file as a Path                
                if( i != size-1){
                    // not at the end so there is a valid file name i+1.
                    // Note that the file selected by iNext has already been pushed into iNext+1, or is the last. Either way,
                    // it can properly be overwritten.
                    int iPlus1 = i + 1;
                    String strFileFullPathIPlus1 = strPrefix + iPlus1 + ".log";
                    Path pathIPlus1 = Paths.get(strFileFullPathIPlus1); //  absolute path to the file as a Path                
                    try {
                        Files.copy(path, pathIPlus1,   REPLACE_EXISTING);
                    } catch (IOException ex) {
                        okDialog("IOException while updating log history in updateLogHistory()");
                        Logger aLogger = Logger.getLogger("LoggerSetuplogger");
                        aLogger.info("IOException while updating log history in updateLogHistory()");
                        aLogger.getLogger(ConcoursGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    continue; // (unnecessary) branch back to for() 
                } else{
                    // Getting here means stack is full. IOW, the reference file the is last log file to be kept so we no longer need it's contents.
                    // No action required since it will simply be overwritten in the next pass.
                    // ;
                }
            } 
        }
        
    }     
    
    final static ResourceBundle rb =  ResourceBundle.getBundle("version"); 

    
    public static final String getRbTok(String propToken) { 
         String msg = ""; 
         try { 
         msg = rb.getString(propToken); 
         } catch (MissingResourceException e) { 
             okDialog("Token " + propToken + " not in Propertyfile!");
             theConcours.GetLogger().info("Token " + propToken + " not in Propertyfile!"); 
         } 
         return msg; 
     }     
    
public  void clearPlacardsScoresheetsFolders(){
    String name = theConcours.GetConcoursName().replace(".db", "");
    String strDir;
    File f;
    strDir = theConcours.GetConcoursBuilderDataPath() + "\\" + name +  "\\Placards\\ChampionSpecial";
    f = new File(strDir);
    if(f.exists()) MyJavaUtils.clearFolder(strDir);
    strDir = theConcours.GetConcoursBuilderDataPath() + "\\" + name + "\\Placards\\Driven";
    f = new File(strDir);
    if(f.exists()) MyJavaUtils.clearFolder(strDir);
    strDir = theConcours.GetConcoursBuilderDataPath() + "\\" + name + "\\Scoresheets\\ChampionSpecial";
    f = new File(strDir);
    if(f.exists()) MyJavaUtils.clearFolder(strDir);
    strDir = theConcours.GetConcoursBuilderDataPath() + "\\" + name + "\\Scoresheets\\Driven";
    f = new File(strDir);
    if(f.exists()) MyJavaUtils.clearFolder(strDir);
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConcoursGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConcoursGUI theCBGUI = new ConcoursGUI();
                theConcours.GetLogger().info("Using ConcoursBuilder version " + version + " build number: " + getRbTok("BUILD")); 
                theCBGUI.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                theCBGUI.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        if (JOptionPane.showConfirmDialog(theCBGUI, 
                            "Are you sure you want to close ConcoursBuilder?", "Really Closing?", 
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                                theConcours.GetLogger().info("User closed the ConcoursGUI with X");
                                prepToCloseCBGUI();
                                System.exit(0);
                            } else {
                                theConcours.GetLogger().info("User chose not to close the ConcoursGUI after hitting X");
                            }
                    }
                });
                theCBGUI.setLocationRelativeTo(null);
                theCBGUI.setVisible(true);
            }
        });
        

 
    class MyTask extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            //doTheBaseDBSave();

            return null;
        }
            
 
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            //startButton.setEnabled(true);
            //saveBaseProgressMonitor.setProgress(0);
        }
        
        }
        
    }
       
    
    /*    Not used... ?
    class ShowWaitActionWriteMasterPersonnelAndJaguarTablesFromMemToDB extends AbstractAction {
    theConcours.GetLogger().info("Launch Show Wait");
    protected static final long SLEEP_TIME = 3 * 1000;

    public ShowWaitActionWriteMasterPersonnelAndJaguarTablesFromMemToDB(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {

                
                //Thread.sleep(SLEEP_TIME);
                LoadSQLiteConcoursDatabase.WriteMasterPersonnelAndJaguarTablesFromMemToDB(baseMasterConnection);
                return null;
            }
        };

        Window win = SwingUtilities.getWindowAncestor((AbstractButton) evt.getSource());
        final JDialog dialog = new JDialog(win, "Dialog", Dialog.ModalityType.APPLICATION_MODAL);

        mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("state")) {
                    if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                        dialog.dispose();
                    }
                }
            }
        });
        mySwingWorker.execute();

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(new JLabel("This can take a while......."), BorderLayout.PAGE_START);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(win);
        dialog.setVisible(true);
    }
}
*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenuItem AcknowledgmentsMenuItem;
    private javax.swing.JMenuItem AddEntryMenuItem;
    private javax.swing.JMenuItem AddJudgeMenuItem;
    private javax.swing.JMenuItem AddJudgingTeamMenuItem;
    private javax.swing.JMenuItem AddMPMenuItem;
    private javax.swing.JMenuItem BackupMenuItem;
    private javax.swing.JMenu BackupRestoreMenu;
    private javax.swing.JFileChooser CSVFileChooser;
    private javax.swing.JMenuItem ChangeEntryMenuItem;
    private javax.swing.JMenuItem ChangeJudgeMenuItem;
    private javax.swing.JMenuItem ChangeJudgingTeamMenuItem;
    private javax.swing.JFileChooser ConcoursBaseDBFileChooser;
    private javax.swing.JFileChooser ConcoursOpenFileChooser;
    private javax.swing.JMenuItem CopyConcoursMenuItem;
    private javax.swing.JMenuItem DeleteConcoursMenuItem;
    private javax.swing.JMenuItem DisplayClassJudgeCandidates;
    private javax.swing.JMenuItem DisplayCustomJudgingTeamsMenuItem;
    private javax.swing.JMenuItem DisplayEntriesMenuItem;
    private javax.swing.JMenuItem DisplayJudgesMenuItem;
    private javax.swing.JMenuItem DisplayMasterPersonnelMenuItem;
    private javax.swing.JMenu DisplayMenu;
    private javax.swing.JMenuItem DisplayPersonnelMenuItem;
    private javax.swing.JMenuItem DisplayStatsMenuItem;
    private javax.swing.JMenuItem EditJASchedule;
    private javax.swing.JMenuItem EditMPMenuItem;
    private javax.swing.JMenu EditMenu;
    private javax.swing.JMenuItem EntriesCSVMenuItem;
    private javax.swing.JMenu EntryMenu;
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenuItem ExportEntriesMenuItem;
    private javax.swing.JMenuItem ExportJudgesMenuItem;
    private javax.swing.JMenuItem ExportMasterPersonnelAndJaguarsMenuItem;
    private javax.swing.JMenu ExportMenu;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JMenuItem JASchedule;
    private javax.swing.JMenuItem JCNAClassFinder;
    private javax.swing.JMenu JudgeMenu;
    private javax.swing.JMenuItem LoadJCNAClassesAndRulesMenuItem;
    private javax.swing.JMenuBar MainMenuBar;
    private javax.swing.JMenu MasterPersonMenu;
    private javax.swing.JMenuItem NewMenuItem;
    private javax.swing.JMenuItem OpenMenuItem;
    private javax.swing.JMenuItem Placards;
    private javax.swing.JMenuItem RemoveEntryMenuItem;
    private javax.swing.JMenuItem RemoveJudgeMenuItem;
    private javax.swing.JMenuItem RemoveJudgingTeamMenuItem;
    private javax.swing.JMenuItem RemoveMasterPersonMenuItem;
    private javax.swing.JMenuItem RestoreMenuItem;
    private javax.swing.JFileChooser SaveAsFileChooser;
    private javax.swing.JFileChooser SaveBaseDBAsFileChooser;
    private javax.swing.JMenuItem SaveBaseMenuItem;
    private javax.swing.JMenuItem ScoreSheets;
    private javax.swing.JMenu ToolsMenu;
    private javax.swing.JMenuItem UserSettingsMenuItem;
    private javax.swing.JMenuItem UsersManualmenuOption;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JMenu judgingTeamsMenu;
    private javax.swing.JScrollPane spnlSchedByClass;
    private javax.swing.JScrollPane spnlSchedByJudge;
    private javax.swing.JScrollPane spnlTextReports;
    private javax.swing.JTabbedPane theTabbedPane;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration//GEN-END:variables
}
