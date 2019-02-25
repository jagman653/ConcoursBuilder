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

/*
***************************************************************************************************************************

  NO LONGER USED. SUPERSEDED BY JudgeAssignDialog SO IT CAN BE MODAL... I.E., USER CAN'T INTERACT WITH ConcoursGUI WHILE IT'S OPEN.

****************************************************************************************************************************
*/
package editJA;

import JCNAConcours.AddConcoursEntryDialog;
import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.ConcoursGUI.theConcours;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.imageio.ImageIO;
import javax.lang.model.util.Elements;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.JudgeAssignment;
import us.efsowell.concours.lib.TimeslotAssignment;

import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MasterJaguar;

class MyCustomFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".db") || file.getAbsolutePath().endsWith(".txt");
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Database files (*.db) or CSV files (*.txt)";
        }
    }

class MyCustomFilterDB extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".db") ;
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Database files (*.db)";
        }
    }


/**
 *
 * @author Ed Sowell
 */
public  class JudgeAssignGUI extends javax.swing.JFrame {

    /**
     * Creates new form JFileChooserJudgeAssign
     */
    public static  Concours theConcours; // making this a Class member allows references to it to be with the Class Name rather than an instance, e.g. JudgeAssignGUI.theConcours
    private static final int BY_JUDGE_HEADER_HEIGHT = 30; // 1 lines
    private static final int BY_CLASS_HEADER_HEIGHT = 100; // 
    private static final int COMPRESSED_HEADER_HEIGHT = 50; // 2 lines
    private static boolean standalone;
    private static String footnote;

    private  LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase;
    private static  Connection theConnection;
  
    
   private javax.swing.JPanel panel1; 
   int byJudgeHeaderHeight = BY_JUDGE_HEADER_HEIGHT;// store in class variable so it can be accessed by table getPreferredSize() as needed

   int byClassHeaderHeight = BY_CLASS_HEADER_HEIGHT;
   int compressedHeaderHeight = COMPRESSED_HEADER_HEIGHT;
    //private static final int HEADER_HEIGHT = 50;
    private static final int HEADER_HEIGHT_MULTIPLIER = 20;  // This is pixles/line-to-line height. Probably can get from font metrics, but guess for now
    private static final int ROW_HEIGHT_MULTIPLIER = 20;     // This is pixles/line-to-line height. Probably can get from font metrics, but guess for now
    private static int COLUMN_WIDTH_MULTIPLIER = 5; // This is pixles/character-to-character width
    
    private static String ScheduleByClassPdfFileName; // file names for exported pdf files.
    private static List<Concours.JATableByClassColHeader> schedulebyclassheaderList;
    private static int ScheduleByClassPagesize;
    
    private static String ScheduleByJudgePdfFileName;
    private static List<Concours.JATableByJudgeColHeader> schedulebyjudgeheaderList;
    private static int ScheduleByJudgePagesize;
    
    private static String ScheduleCompressedPdfFileName;
    private static List<Concours.JATableMergeCompressedColHeader> compressedscheduleheaderList;
    private static int ScheduleCompressedPagesize;
    private static String strParentOfDBFIle;

    Map<Integer, String> timeslotIndexToTimeStringMap;
    Map<String, Integer> timeStringToTimeslotIndexMap;
    
    /*
    public JudgeAssignGUI(boolean aStandalone, Concours aConcours, String aFootnote) {
      //  initComponents();
        standalone = aStandalone;
        theConcours = aConcours;
        footnote = aFootnote;
        ScheduleByClassPagesize = 3; // legal landscape
        ScheduleByJudgePagesize = 3; // legal landscape
        ScheduleCompressedPagesize = 1; // letter landscape
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase(); // for function access only
        myInitComponents_1(theConcours);
        myInitComponents_2(theConcours);
        myInitComponents_3();
        
       timeslotIndexToTimeStringMap = new HashMap<Integer, String>(); 
       timeStringToTimeslotIndexMap = new HashMap<String, Integer>(); 
       //masterJagColorToScheduleColorMap = new HashMap<>();
       //createCarColorMap();
       
       // defer createTimeslotMap() until the 
       //createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch());
        
        theConnection = theConcours.GetConnection();
        File f = new File(theConcours.GetThePath()); // just to get parent
        strParentOfDBFIle = f.getParent();
        theConcours.UpdateTimeslotStats(); 
        theConcours.updateJudgeLoads();
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();

   }
    */
    /*
    private void myInitComponents_1(Concours aConcours){
        fileChooser = new javax.swing.JFileChooser();
        jButton1 = new javax.swing.JButton();
        theTabbedPane = new javax.swing.JTabbedPane();
        spnlText = new javax.swing.JScrollPane();
        textarea = new javax.swing.JTextArea();
        spnlSchedByClass = new javax.swing.JScrollPane();
        tblSchedByClass = new javax.swing.JTable();
        spnlSchedByJudge = new javax.swing.JScrollPane();
        tblSchedByJudge = new javax.swing.JTable();
        spnlSchedCompressed = new javax.swing.JScrollPane();
        tblSchedCompressed = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        File = new javax.swing.JMenu();
        New = new javax.swing.JMenuItem();
        Open = new javax.swing.JMenuItem();
        Save = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();
        Validate = new javax.swing.JMenu();
        CheckDups = new javax.swing.JMenuItem();
        chkSelfJudge = new javax.swing.JMenuItem();
        ChkForRepeatJudges = new javax.swing.JMenuItem();
        ChkForNonuniformJudgesInClass = new javax.swing.JMenuItem();
        CheckForConflicts = new javax.swing.JMenuItem();
        DisplayJudgeLoads = new javax.swing.JMenuItem();
        Edit = new javax.swing.JMenu();
        ChangeEntryTimeslot = new javax.swing.JMenuItem();
        ChangeJudge = new javax.swing.JMenuItem();
        InterchangeTimeslots = new javax.swing.JMenuItem();
        CompressTimeslots = new javax.swing.JMenuItem();
        Export = new javax.swing.JMenu();
        ExportByClass = new javax.swing.JMenuItem();
        ExportByJudge = new javax.swing.JMenuItem();
        ExportCompressedSchedule = new javax.swing.JMenuItem();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Concours Judge Assignment Editing");

        textarea.setColumns(20);
        textarea.setRows(5);
        spnlText.setViewportView(textarea);
        theTabbedPane.setPreferredSize(new Dimension(1200, 1000));
        theTabbedPane.addTab("Text reports", spnlText);
        
    }
    */
    /*
    private void myInitComponents_2(Concours aConcours){
        
        File.setText("File");
        if(standalone){
            //
            // The usage is from main() in this class
            //
            New.setText("New");
            New.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NewActionPerformed(evt);
                }
            });
            File.add(New);
            Open.setText("Open");
            Open.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpenActionPerformed(evt);
                }
            });
            File.add(Open);
            Save.setText("Save");
            Save.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SaveActionPerformed(evt);
                }
            });
            File.add(Save);
            fileChooser.setDialogTitle("Open JCNA Concourse");
            fileChooserDB = new javax.swing.JFileChooser();
            fileChooserDB.setDialogTitle("Choose concours database file");
            fileChooserDB.setFileFilter(new MyCustomFilterDB() );

        } else{ 
            // The usage is from ConcoursGUI
            // Customizing the menus
           // File.remove(New);
           // File.remove(Open);
           // File.remove(Save);
            Validate.setEnabled(true);
            theConcours= aConcours;
        }
        

        Export.setText("Export");

        ExportByClass.setText("Judging Schedule by Class");
        ExportByClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportByClassActionPerformed(evt);
            }
        });
        Export.add(ExportByClass);

        ExportByJudge.setText("Judging Schedule by Judge");
        ExportByJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportByJudgeActionPerformed(evt);
            }
        });
        Export.add(ExportByJudge);

        ExportCompressedSchedule.setText("Compact Judging Schedule");
        ExportCompressedSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportCompressedScheduleActionPerformed(evt);
            }
        });
        Export.add(ExportCompressedSchedule);
        
        File.add(Export);

        
        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        File.add(Exit);

        jMenuBar1.add(File);

        Validate.setText("Validate");
       // Validate.setEnabled(false);

        CheckDups.setText("Check for Duplicate Cars");
        CheckDups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckDupsActionPerformed(evt);
            }
        });
        Validate.add(CheckDups);

        chkSelfJudge.setText("Check for Self-judges entries");
        chkSelfJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSelfJudgeActionPerformed(evt);
            }
        });
        Validate.add(chkSelfJudge);

        ChkForRepeatJudges.setText("Check for Repeated Judges");
        ChkForRepeatJudges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkForRepeatJudgesActionPerformed(evt);
            }
        });
        Validate.add(ChkForRepeatJudges);

        ChkForNonuniformJudgesInClass.setText("Check all Classes for nonuniform Judging teams");
        ChkForNonuniformJudgesInClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkForNonuniformJudgesInClassActionPerformed(evt);
            }
        });
        Validate.add(ChkForNonuniformJudgesInClass);

        CheckForConflicts.setText("Check for Timeslot Conflicts");
        CheckForConflicts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckForConflictsActionPerformed(evt);
            }
        });
        Validate.add(CheckForConflicts);

        DisplayJudgeLoads.setText("DisplayJudgeLoads");
        DisplayJudgeLoads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayJudgeLoadsActionPerformed(evt);
            }
        });
        Validate.add(DisplayJudgeLoads);

        jMenuBar1.add(Validate);

        Edit.setText("Edit");

        ChangeEntryTimeslot.setText("Change Entry Timeslot");
        ChangeEntryTimeslot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeEntryTimeslotActionPerformed(evt);
            }
        });
        Edit.add(ChangeEntryTimeslot);

        ChangeJudge.setText("Change a Judge for a Class");
        ChangeJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeJudgeActionPerformed(evt);
            }
        });
        Edit.add(ChangeJudge);
        
        InterchangeTimeslots.setText("Interchange timeslots");
        InterchangeTimeslots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InterchangeTimeslotsActionPerformed(evt);
            }
        });
        Edit.add(InterchangeTimeslots);
        

        CompressTimeslots.setText("Compress Timeslots");
        CompressTimeslots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CompressTimeslotsActionPerformed(evt);
            }
        });
        //Edit.add(CompressTimeslots);

        jMenuBar1.add(Edit);

        setJMenuBar(jMenuBar1);


    }
    */
    /*
        private void myInitComponents_3(){
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1656, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 181, Short.MAX_VALUE))
        );

        pack();

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

        fileChooser = new javax.swing.JFileChooser();
        jButton1 = new javax.swing.JButton();
        theTabbedPane = new javax.swing.JTabbedPane();
        spnlText = new javax.swing.JScrollPane();
        textarea = new javax.swing.JTextArea();
        spnlSchedByClass = new javax.swing.JScrollPane();
        tblSchedByClass = new javax.swing.JTable();
        spnlSchedByJudge = new javax.swing.JScrollPane();
        tblSchedByJudge = new javax.swing.JTable();
        spnlSchedCompressed = new javax.swing.JScrollPane();
        tblSchedCompressed = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        File = new javax.swing.JMenu();
        New = new javax.swing.JMenuItem();
        Open = new javax.swing.JMenuItem();
        Save = new javax.swing.JMenuItem();
        Export = new javax.swing.JMenu();
        ExportByClass = new javax.swing.JMenuItem();
        ExportByJudge = new javax.swing.JMenuItem();
        ExportCompressedSchedule = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();
        Validate = new javax.swing.JMenu();
        CheckDups = new javax.swing.JMenuItem();
        chkSelfJudge = new javax.swing.JMenuItem();
        ChkForRepeatJudges = new javax.swing.JMenuItem();
        ChkForNonuniformJudgesInClass = new javax.swing.JMenuItem();
        CheckForConflicts = new javax.swing.JMenuItem();
        DisplayJudgeLoads = new javax.swing.JMenuItem();
        Edit = new javax.swing.JMenu();
        ChangeEntryTimeslot = new javax.swing.JMenuItem();
        InterchangeTimeslots = new javax.swing.JMenuItem();
        ChangeJudge = new javax.swing.JMenuItem();
        CompressTimeslots = new javax.swing.JMenuItem();
        LeadJudgesMenuItem = new javax.swing.JMenuItem();

        fileChooser.setDialogTitle("Choose concours file");
        fileChooser.setFileFilter(new MyCustomFilter() );

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Concours Judge Assignment Editing");

        textarea.setColumns(20);
        textarea.setRows(5);
        spnlText.setViewportView(textarea);

        theTabbedPane.addTab("Text reports", spnlText);

        tblSchedByClass.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSchedByClass.setPreferredSize(new java.awt.Dimension(1500, 840));
        spnlSchedByClass.setViewportView(tblSchedByClass);

        theTabbedPane.addTab("Schedule by Class", spnlSchedByClass);

        tblSchedByJudge.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSchedByJudge.setPreferredSize(new java.awt.Dimension(1500, 840));
        spnlSchedByJudge.setViewportView(tblSchedByJudge);

        theTabbedPane.addTab("Schedule by Judge", spnlSchedByJudge);

        tblSchedCompressed.setModel(new javax.swing.table.DefaultTableModel(
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
        spnlSchedCompressed.setViewportView(tblSchedCompressed);

        theTabbedPane.addTab("Compressed Schedule", spnlSchedCompressed);

        File.setText("File");

        New.setText("New");
        New.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewActionPerformed(evt);
            }
        });
        File.add(New);

        Open.setText("Open");
        Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenActionPerformed(evt);
            }
        });
        File.add(Open);

        Save.setText("Save");
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        File.add(Save);

        Export.setText("Export");
        Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportActionPerformed(evt);
            }
        });

        ExportByClass.setText("Judging Schedule by Class");
        ExportByClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportByClassActionPerformed(evt);
            }
        });
        Export.add(ExportByClass);

        ExportByJudge.setText("Judging Schedule by Judge");
        ExportByJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportByJudgeActionPerformed(evt);
            }
        });
        Export.add(ExportByJudge);

        ExportCompressedSchedule.setText("Compressed Judging Schedule");
        ExportCompressedSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportCompressedScheduleActionPerformed(evt);
            }
        });
        Export.add(ExportCompressedSchedule);

        File.add(Export);

        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        File.add(Exit);

        jMenuBar1.add(File);

        Validate.setText("Validate");
        Validate.setEnabled(false);

        CheckDups.setText("Check for Duplicate Cars");
        CheckDups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckDupsActionPerformed(evt);
            }
        });
        Validate.add(CheckDups);

        chkSelfJudge.setText("Check for Self-judges entries");
        chkSelfJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSelfJudgeActionPerformed(evt);
            }
        });
        Validate.add(chkSelfJudge);

        ChkForRepeatJudges.setText("Check for Repeated Judges");
        ChkForRepeatJudges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkForRepeatJudgesActionPerformed(evt);
            }
        });
        Validate.add(ChkForRepeatJudges);

        ChkForNonuniformJudgesInClass.setText("Check all Classes for nonuniform Judging teams");
        ChkForNonuniformJudgesInClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkForNonuniformJudgesInClassActionPerformed(evt);
            }
        });
        Validate.add(ChkForNonuniformJudgesInClass);

        CheckForConflicts.setText("Check for Timeslot Conflicts");
        CheckForConflicts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckForConflictsActionPerformed(evt);
            }
        });
        Validate.add(CheckForConflicts);

        DisplayJudgeLoads.setText("Display Judge Loads");
        DisplayJudgeLoads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayJudgeLoadsActionPerformed(evt);
            }
        });
        Validate.add(DisplayJudgeLoads);

        jMenuBar1.add(Validate);

        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });

        ChangeEntryTimeslot.setText("Change Entry Timeslot");
        ChangeEntryTimeslot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeEntryTimeslotActionPerformed(evt);
            }
        });
        Edit.add(ChangeEntryTimeslot);

        InterchangeTimeslots.setText("Interchange timeslots");
        InterchangeTimeslots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InterchangeTimeslotsActionPerformed(evt);
            }
        });
        Edit.add(InterchangeTimeslots);

        ChangeJudge.setText("Change a Judge for a Class");
        ChangeJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeJudgeActionPerformed(evt);
            }
        });
        Edit.add(ChangeJudge);

        CompressTimeslots.setText("Compress Timeslots");
        CompressTimeslots.setEnabled(false);
        CompressTimeslots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CompressTimeslotsActionPerformed(evt);
            }
        });
        Edit.add(CompressTimeslots);

        LeadJudgesMenuItem.setText("Set Lead Judges");
        LeadJudgesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeadJudgesMenuItemActionPerformed(evt);
            }
        });
        Edit.add(LeadJudgesMenuItem);

        jMenuBar1.add(Edit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1656, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 181, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
public void AppendToTextArea(String aString){
    textarea.append(aString);
}
public void createTimeslotMap(String aStrStartTime, String aStrLunchTime, Integer aTimeslotInterval, Integer aSlotsBeforeLunch, Integer aLunchInterval, boolean aAddNewSlot){
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    Date dateStartTime = null;
    Date dateLunchTime = null;
    Calendar cal = Calendar.getInstance();
    String timeslotTime; 
    try {
        dateStartTime = df.parse(aStrStartTime);
    } catch (ParseException ex) {
        String msg = "Start Time Parse Exception in createTimeslotMap";
        okDialog(msg);
        theConcours.GetLogger().log(Level.SEVERE, msg, ex);
    }
    try {
        dateLunchTime = df.parse(aStrLunchTime);
    } catch (ParseException ex) {
        String msg = "Lunch Time Parse Exception in createTimeslotMap";
        okDialog(msg);
        theConcours.GetLogger().log(Level.SEVERE, msg, ex);
    }
    
    int num = theConcours.getMaxTimeslotIndex() + 1;
    if(aAddNewSlot) num++; // This needed for Change Entry Timeslot... so an entry can be moved to a new, empty timeslot
    // Add one more because we need to have one extra in the combo boxes
    for(int intTimeslot = 0; intTimeslot < num; intTimeslot++){
        if(intTimeslot <= (aSlotsBeforeLunch - 1)){
            cal.setTime(dateStartTime);
            cal.add(Calendar.MINUTE, intTimeslot*aTimeslotInterval);
        } else {
            cal.setTime(dateLunchTime);
            cal.add(Calendar.MINUTE, aLunchInterval + (intTimeslot - aSlotsBeforeLunch)*aTimeslotInterval );
        }
        timeslotTime = df.format(cal.getTime());
        timeslotIndexToTimeStringMap.put(intTimeslot, timeslotTime);
        timeStringToTimeslotIndexMap.put(timeslotTime, intTimeslot);
    }
        
    

    

   
}

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
            // For the changes to stick we must write Judge assignments and schedule to the DB file
        //
        // Closing and reopening to avoid locked database???
        //
        /*
           if(theConnection != null){
            Connection newConn = null;
            System.out.println("Close the database before JudgeAssignmentsMemToDB()");
            try {
                theConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JudgeAssignGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
           String strDB = theConcours.GetThePath() ;

            try {
                Class.forName("org.sqlite.JDBC");
                String strConn = "jdbc:sqlite:" + strDB ;
                newConn = DriverManager.getConnection(strConn);
                System.out.println("Reopened database " + strConn + " successfully");
            } catch ( ClassNotFoundException | SQLException e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
       
           System.out.println("Updating JudgeAssignemnts in memory");
            //loadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB(newConn,  theConcours);
            LoadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB(newConn,  theConcours);
            theConcours.SetConnection(newConn); 
       }
        */
        //
        //  This is necessary here since user might have edited the Judge assignments & schedule
        //
        theConcours.GetLogger().info("Because of possible user-edits, write JudgeAssignemnts from memory to DB before GUI Exit");
        LoadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB(theConnection,  theConcours);

        
    
        if(standalone){
            try {
                System.out.println("Closing the database");
                theConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JudgeAssignGUI.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Exiting");
                System.exit(0); // Closes the application
        } else{
            this.setVisible(false);
        }
        
    }//GEN-LAST:event_ExitActionPerformed

    private void OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenActionPerformed
    /*
        boolean LoadFromCSVFiles = false; // might need to use this for importing ...
    
    int returnVal;
    Logger theLogger = theConcours.GetLogger();
    if(LoadFromCSVFiles){
        //
        // Load from CSV files
        //
        String fullPathToJudgeAssignFile;
        File file;
        returnVal = fileChooserDB.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooserDB.getSelectedFile();
            fullPathToJudgeAssignFile = file.getAbsolutePath();
            Path path = Paths.get(fullPathToJudgeAssignFile);
            theConcours.SetThePath(path.getParent().toString()); 
         
            try {
                theConcours.GetJCNAClasses().LoadJCNAClassesCSV(theConcours.GetThePath(), theLogger);
                theConcours.LoadOwnersCSV(theConcours.GetThePath(), theLogger);
                theConcours.LoadEntriesCSV(theConcours.GetThePath(), theLogger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
                theConcours.LoadJudgesCSV(theConcours.GetThePath(), theLogger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
                theConcours.LoadJudgeAssignmentCSV(fullPathToJudgeAssignFile, theLogger);
                theConcours.LoadConcoursClassesCSV(theConcours.GetThePath(), theLogger);
                theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is initialized here and then again after any change
            
                System.out.println("Concours Judge Assignments, Owners, Entries, Judges, Concours Classes, & JCNA Classes loaded from directory " + theConcours.GetThePath());
                //updateJAByJudgeTable();
               // updateJAByClassTable();
               // updateJACompressedTable();

                textarea.append("\nFiles with Concours Judge Assignments, Entries, Judges, & Classes loaded from directory " + theConcours.GetThePath());
            } catch (IOException ex) {
                okDialog("ERROR: IOException in OpenActionPerformed");
                theConcours.GetLogger().info("ERROR: IOException in OpenActionPerformed");
                theConcours.GetLogger().log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
          
          // Enable Validation Menu
          Validate.setEnabled(true);
        } else {
            theConcours.GetLogger().info("File access cancelled by user.");
            textarea.append("\nFile loading cancelled by user.");
        }
    } else{
        //
        // load from database file
        //

        String strConcoursDBFile;
        File concoursDBFile;
        Connection conn;
         //FileFilter filter = new FileNameExtensionFilter("Sqlite DB","db");
        //fileChooser.addChoosableFileFilter(filter);

        returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            concoursDBFile = fileChooser.getSelectedFile();
            strConcoursDBFile = concoursDBFile.getAbsolutePath();
            
            Path path = Paths.get(strConcoursDBFile);
            theConcours.SetThePath(strConcoursDBFile); 
            conn = null;
            try {
                Class.forName("org.sqlite.JDBC");
                String strConn;
                strConn = "jdbc:sqlite:" + strConcoursDBFile ;
                conn = DriverManager.getConnection(strConn);
                theConcours.SetConnection(conn) ;
                this.theConnection = theConcours.GetConnection();
                
                
            } catch ( ClassNotFoundException | SQLException e ) {
                theConcours.GetLogger().info( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            theConcours.GetLogger().info("Opened database " + strConcoursDBFile + " successfully");
         
           
                theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", theConcours.GetLogger());
                theConcours.LoadMasterPersonnelDB(conn, theConcours.GetLogger());
                theConcours.LoadMasterJaguarDB(conn, theConcours.GetLogger());
                theConcours.LoadEntriesDB(conn, theConcours.GetLogger()); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
                theConcours.LoadConcoursClassesDB(conn, theConcours, theConcours.GetLogger());
                theConcours.LoadJudgesDB(conn, theConcours.GetLogger()); // Judge loads in Judges gets set when TimeslotAssignment is being updated
                theConcours.LoadOwnersDB(conn, theConcours.GetLogger());
                loadSQLiteConcoursDatabase.ReadJudgeAssignmentDBToMem(conn, theConcours);
                theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is initialized here and then again after any change
                
                theConcours.GetLogger().info("Concours Judge Assignments, Owners, Entries, Judges, Concours Classes, & JCNA Classes loaded from directory " + theConcours.GetThePath());
                updateJAByJudgeTable();
                updateJAByClassTable();
                updateJACompressedTable();
                
                textarea.append("\n  Internal data structures loaded from Concours database in directory " + theConcours.GetThePath());
          
          // Enable Validation Menu
          Validate.setEnabled(true);
           
        } else {
            theConcours.GetLogger().info("File access cancelled by user.");
            textarea.append("\nFile loading cancelled by user.");
        }
        
        
    }
   */ 
       
    }//GEN-LAST:event_OpenActionPerformed

    private void CheckDupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckDupsActionPerformed
       //res = editjudgeassignments.checkForDupCars();
        Set<Integer> dups = new HashSet<Integer>();
        //theConcours.DisplayCars ();
       
        if(!dups.isEmpty()) {
            textarea.append("\nDuplicate cars found: [");
            Iterator iter = dups.iterator();
            while (iter.hasNext()) {
                textarea.append(iter.next().toString() + " ");
            }
            textarea.append("]\n");
        }
        else{
            textarea.append("\nDuplicates cars found: [none]");
        }
       
    }//GEN-LAST:event_CheckDupsActionPerformed

    private void CheckForConflictsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckForConflictsActionPerformed

        //res = editjudgeassignments.checkForTimeslotConflicts();
        /*if(!res.isEmpty()) {
            textarea.append("Conflicted timeslots found: [");
            Iterator iter = res.iterator();
            while (iter.hasNext()) {
                //System.out.println(iter.next());
                textarea.append(iter.next().toString() + " ");
            }
            textarea.append("]\n");
            
        }
        
        else{
            textarea.append("Conflicted timeslots found: [none]");
        }
        */
        String conflicts ;
        String res ;
        //
        //clear arrary in case the operation is repeated by the user
        // 
        //if(getTimeslotAssignments().size() > 0){
        ArrayList<TimeslotAssignment> tsa;
        tsa = theConcours.GetTimeslotAssignments();
        if(tsa.size() > 0){
            for(int i = 0;i < tsa.size(); i++)
                tsa.get(i).clearTSLists();
        }
        conflicts = theConcours.CheckForTimeslotConflicts(timeslotIndexToTimeStringMap);
        //
        // Disable this because the only conflicts now are TS conflicts approved by the user.
        //
        /*
        if(!conflicts.contains("no conflicts")){
            textarea.append(conflicts );
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(this, "Select Yes to move offending cars", "Fix timeslot conflicts", dialogButton);
            if(dialogResult == 0){
                //System.out.println("\"User elected  to fix timeslot conflicts\"");
                textarea.append("\nUser elected to fix timeslot conflicts");
                //conflicts = editjudgeassignments.fixTimeslotConflicts();
                res = theConcours.FixTimeslotConflicts();

                textarea.append("\nResults of fixing timeslot conflicts[" + res + "]");
            
            } else{
               // System.out.println("User elected not to fix timeslot conflicts");
                textarea.append("\nUser elected not to fix timeslot conflicts");
            }
        }
        else{
            textarea.append(conflicts);
        }
        */
        textarea.append(conflicts);    
        
    }//GEN-LAST:event_CheckForConflictsActionPerformed

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        // TODO add your handling code here:
     File file;
     String fullPathToJudgeAssignFile;
     JFileChooser fileChooser = new JFileChooser();
     int returnVal = fileChooser.showSaveDialog(this)   ;
     fileChooser.setDialogTitle("Specify file name");    

    if (returnVal == JFileChooser.APPROVE_OPTION) {
         file = fileChooser.getSelectedFile();
         fullPathToJudgeAssignFile =  file.getAbsolutePath();
          //editjudgeassignments.saveConcoursToFile( file.getAbsolutePath());
         theConcours.SaveConcoursToFile(fullPathToJudgeAssignFile);
         theConcours.GetLogger().info("Concourse saved to file " + fullPathToJudgeAssignFile);
         textarea.append("\nConcourse saved to file " + fullPathToJudgeAssignFile);
          // Enable Validation Menu
          Validate.setEnabled(true);
    } else {
        theConcours.GetLogger().info("File access cancelled by user.");
        textarea.append("\nFile loading cancelled by user.");
    }
    
    
       
    }//GEN-LAST:event_SaveActionPerformed

    private void chkSelfJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSelfJudgeActionPerformed
       // List<Integer> probelmEntries;
       //res = editjudgeassignments.checkForSelfJudges();
        //List<String> probelmEntries;
        String results;
        results = theConcours.CheckForSelfJudges();
        int k;
        if(!results.isEmpty()) {
            textarea.append("\nEntries with self-judges: [");
           /* Iterator iter = probelmEntries.iterator();
            k = 0;
            while (iter.hasNext()) {
                if (k > 0) textarea.append(", ");
                textarea.append(iter.next().toString() + " ");
                k++;
            }
            */
            textarea.append(results);
            textarea.append("]\n");
        }
        else{
            textarea.append("\nEntries with self-judges: [none]");
        }
    }//GEN-LAST:event_chkSelfJudgeActionPerformed

    private void ChkForRepeatJudgesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkForRepeatJudgesActionPerformed
        String result = theConcours.CheckForRepeatJudges();
        if(!result.isEmpty()) {
            textarea.append("\nClasses with judge repeats: [" + result + "]");
            textarea.append("\n");
        }
        else{
            textarea.append("\nClasses with judge repeats: [none]");
            textarea.append("\n");
        }
    }//GEN-LAST:event_ChkForRepeatJudgesActionPerformed

    private void ChkForNonuniformJudgesInClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkForNonuniformJudgesInClassActionPerformed
        List<Integer> probelmEntries;
        probelmEntries = theConcours.CheckForClassJudgeUniformity();
        
        if(!probelmEntries.isEmpty()) {
            textarea.append("\nClasses with nonuniform judging teams: [");
            Iterator iter = probelmEntries.iterator();
            while (iter.hasNext()) {
                //System.out.println(iter.next());
                textarea.append(iter.next().toString() + " ");
            }
            textarea.append("]\n");
            
        }
        else{
            textarea.append("\nClasses with nonuniform judging teams: [none]");
        }
    }//GEN-LAST:event_ChkForNonuniformJudgesInClassActionPerformed

    private void DisplayJudgeLoadsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayJudgeLoadsActionPerformed
   //     List<String> res;
   //    res = editjudgeassignments.getJudgeLoads();

        List<String> lstLoadString;
        lstLoadString = theConcours.GetJudgeLoads();
        
        if(!lstLoadString.isEmpty()) {
            textarea.append("\nJudge loads: ");
            Iterator iter = lstLoadString.iterator();
            while (iter.hasNext()) {
                textarea.append("\n" + iter.next().toString() );
            }
            textarea.append("\n");
            
        }
        else{
            textarea.append("\nError while getting judge loads");
        }
    }//GEN-LAST:event_DisplayJudgeLoadsActionPerformed

    private void CompressTimeslotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CompressTimeslotsActionPerformed
        /*
        List<String> res;
        //System.out.println("Attempting to compress timeslots");
        textarea.append("\nCompress timeslots not implemented");
      */ 
    }//GEN-LAST:event_CompressTimeslotsActionPerformed

    
    private void ChangeEntryTimeslotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeEntryTimeslotActionPerformed
        /*
        theConcours.GetLogger().info("Change Entry Timeslot.");
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), true);
        ChangeEntryTSInputDialog changeEntryTSInputDialog = new ChangeEntryTSInputDialog(this, true, theConcours, timeslotIndexToTimeStringMap, timeStringToTimeslotIndexMap);
        changeEntryTSInputDialog.setTitle("Change Entry Timeslot");
        changeEntryTSInputDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        theConcours.GetLogger().info("Returning from ChangeEntryTSInputDialog to Judge Assignment GUI");
                    }
                });        
        changeEntryTSInputDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        changeEntryTSInputDialog.setVisible(rootPaneCheckingEnabled);
        theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is updated here because of the change
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();
        */
    }//GEN-LAST:event_ChangeEntryTimeslotActionPerformed

    
    private void ChangeJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeJudgeActionPerformed
           /*
        theConcours.GetLogger().info("Change Class Judge");
            ChangeClassJudgeInputDialog changeClassJudgeInputDialog = new ChangeClassJudgeInputDialog(this, true, theConcours);
            changeClassJudgeInputDialog.setTitle("Change Class Judge");
            changeClassJudgeInputDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            theConcours.GetLogger().info("Returning from ChangeClassJudgeInputDialog to judge Assignment GUI");
                        }
                    });        
            changeClassJudgeInputDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
            changeClassJudgeInputDialog.setVisible(rootPaneCheckingEnabled);
            theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is updated here because of the change
            theConcours.updateJudgeLoads();
            //System.out.println("updateJAByJudgeTable");
            updateJAByJudgeTable();
            //System.out.println("updateJAByClassTable");
            updateJAByClassTable();
            //System.out.println("updateJACompressedTable");
            updateJACompressedTable();
        */
    }//GEN-LAST:event_ChangeJudgeActionPerformed

    
    private void NewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewActionPerformed

        // TODO add your handling code here:
    }//GEN-LAST:event_NewActionPerformed
 
    private void ExportByJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportByJudgeActionPerformed
        //JTableToPNG(tblSchedByJudge, ScheduleByJudgePdfFileName);
        //
        //   To be sure the column headers etc get set up  
        //
        /*
            theConcours.UpdateTimeslotStats(); 
            theConcours.updateJudgeLoads();
            updateJAByJudgeTable();
            updateJAByClassTable();
            updateJACompressedTable();
        Concours.JATableByJudgeColHeader[] headerArray = new Concours.JATableByJudgeColHeader[schedulebyjudgeheaderList.size()];
        schedulebyjudgeheaderList.toArray(headerArray); // fill the headerArray
        String concoursName = theConcours.GetConcoursName().replaceFirst(".db", "");
        JTableToPdf(tblSchedByJudge, headerArray, ScheduleByJudgePdfFileName, ScheduleByJudgePagesize, concoursName, " by Judge");
        okDialog("Exported " + concoursName + " Schedule by Judge to Folder " + theConcours.GetConcoursBuilderDataPath() + "\\" + concoursName);
        */
    }//GEN-LAST:event_ExportByJudgeActionPerformed

    
    private void ExportByClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportByClassActionPerformed
        //JTableToPNG(tblSchedByClass, ScheduleByClassPdfFileName);
           /* theConcours.UpdateTimeslotStats(); 
            theConcours.updateJudgeLoads();
            updateJAByJudgeTable();
            updateJAByClassTable();
            updateJACompressedTable();
        Concours.JATableByClassColHeader[] headerArray = new Concours.JATableByClassColHeader[schedulebyclassheaderList.size()];
        schedulebyclassheaderList.toArray(headerArray); // fill the headerArray
        String concoursName = theConcours.GetConcoursName().replaceFirst(".db", "");
        
        JTableToPdf(tblSchedByClass, headerArray, ScheduleByClassPdfFileName, ScheduleByClassPagesize, concoursName, "by Class");
        okDialog("Exported " + concoursName + " Schedule by Class to Folder " + theConcours.GetConcoursBuilderDataPath() + "\\" + concoursName);
*/
    }//GEN-LAST:event_ExportByClassActionPerformed

    
    private void ExportCompressedScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportCompressedScheduleActionPerformed
       /* //JTableToPNG(tblSchedCompressed, ScheduleCompressedPdfFileName);
            theConcours.UpdateTimeslotStats(); 
            theConcours.updateJudgeLoads();
            updateJAByJudgeTable();
            updateJAByClassTable();
            updateJACompressedTable();
        Concours.JATableCompressedColHeader[] headerArray = new Concours.JATableCompressedColHeader[compressedscheduleheaderList.size()];
        compressedscheduleheaderList.toArray(headerArray); // fill the headerArray
        String concoursName = theConcours.GetConcoursName().replaceFirst(".db", "");
        JTableToPdf(tblSchedCompressed, headerArray, ScheduleCompressedPdfFileName, ScheduleCompressedPagesize, concoursName, "Compact");
        okDialog("Exported " + concoursName + " Compressed Schedule to Folder " + theConcours.GetConcoursBuilderDataPath() + "\\" + concoursName);
        */
    }//GEN-LAST:event_ExportCompressedScheduleActionPerformed

    
    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditActionPerformed

   
    private void InterchangeTimeslotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InterchangeTimeslotsActionPerformed
        /*
        theConcours.GetLogger().info("Interchange Timeslots");
        // false => do not add extre timeslot
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), false);
        InterchangeTimeslotsDialog interchangeTimeslotsDialog = new InterchangeTimeslotsDialog(this, true, theConcours, timeslotIndexToTimeStringMap, timeStringToTimeslotIndexMap);
        interchangeTimeslotsDialog.setTitle("Interchange Timeslots");
        interchangeTimeslotsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        theConcours.GetLogger().info("Returning from InterchangeTimeslotsDialog to Judge Assignment GUI");
                    }
                });        
        interchangeTimeslotsDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        interchangeTimeslotsDialog.setVisible(rootPaneCheckingEnabled);
        theConcours.UpdateTimeslotStats(); 
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();
        */
    }//GEN-LAST:event_InterchangeTimeslotsActionPerformed

    
    private void ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ExportActionPerformed

    
    private void LeadJudgesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeadJudgesMenuItemActionPerformed
        /*
        theConcours.GetLogger().info("Set Lead Judges for Concours Classes");
        SetLeadJudgeInputDialog setLeadJudgeInputDialog = new SetLeadJudgeInputDialog(this, true, theConcours);
        setLeadJudgeInputDialog.setTitle("Set Lead Judges for Concours Classes");
        setLeadJudgeInputDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        theConcours.GetLogger().info("Returning from LeadJudgesMenuItemActionPerformed to judge Assignment GUI");
                    }
                });        
        setLeadJudgeInputDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLeadJudgeInputDialog.setVisible(rootPaneCheckingEnabled);
       // theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is updated here because of the change
       // theConcours.updateJudgeLoads();
        //System.out.println("updateJAByJudgeTable");
        updateJAByJudgeTable();
        //System.out.println("updateJAByClassTable");
        updateJAByClassTable();
        //System.out.println("updateJACompressedTable");
        updateJACompressedTable();
 */  
    }//GEN-LAST:event_LeadJudgesMenuItemActionPerformed

 
    // getJAByJudgeTable() allows JAByJudgeTable to be loaded, changed, etc. by methods of other Classes
    public  javax.swing.JTable getJAByJudgeTable(){
        return tblSchedByJudge ;
    }
    /*
    // getJAByClassTable() allows JAByClassTable to be loaded, changed, etc. by methods of other Classes
    public  javax.swing.JTable getJAByClassTable(){
        return tblSchedByClass ;
    }
    */
/*
    public final void updateJAByJudgeTable(){
        ScheduleByJudgePdfFileName = strParentOfDBFIle + "\\ScheduleByJudge.pdf";
        schedulebyjudgeheaderList = theConcours.UpdateJAByJudgeTableHeader();
        // Transfer list to array because that's what the table model constructor expect...
        Concours.JATableByJudgeColHeader[] headerArray = new Concours.JATableByJudgeColHeader[schedulebyjudgeheaderList.size()];
        schedulebyjudgeheaderList.toArray(headerArray); // fill the headerArray
        Object [][] rowArray = theConcours.UpdateJAByJudgeTableRowData(schedulebyjudgeheaderList);
        tblSchedByJudge.setAutoResizeMode(AUTO_RESIZE_OFF);
        DefaultTableModel mdlSchedByJudge = new javax.swing.table.DefaultTableModel(
            rowArray,
            headerArray
        );
        tblSchedByJudge.setModel(mdlSchedByJudge);
        TableColumnAdjuster tca = new TableColumnAdjuster(tblSchedByJudge, 6);
        tca.adjustColumns(); 
        int [] SchedByJudgeRowHeights = new int[rowArray.length];
        getRowHeights(tblSchedByJudge, SchedByJudgeRowHeights)  ;      
        // By changing d.height in the table ColumnModel getPreferredSize() we effect dynamic adjustment of header height!
        // See Anubis posting in StackOverflow 7/27/2012
        
        tblSchedByJudge.setTableHeader(new JTableHeader(tblSchedByJudge.getColumnModel()) {
        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = byJudgeHeaderHeight;
            return d;
        }
        });
           
        Arrays.sort(SchedByJudgeRowHeights);
        int rowheight = SchedByJudgeRowHeights[SchedByJudgeRowHeights.length-1] + 10; // + 30;
        tblSchedByJudge.setRowHeight(rowheight);
        spnlSchedByJudge.setViewportView(tblSchedByJudge);
        theTabbedPane.addTab("Schedule by Judge", spnlSchedByJudge);


        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 181, Short.MAX_VALUE))
        );
        
        
    }
*/
/*
public final void updateJAByClassTable(){
    ScheduleByClassPdfFileName = strParentOfDBFIle + "\\ScheduleByClass.pdf";
    //List<Concours.JATableByClassColHeader> headerList;
    schedulebyclassheaderList = theConcours.UpdateJAByClassTableHeader(theConcours);
    // Transfer list to array because that's what the table model constructor expect...
    Concours.JATableByClassColHeader[] headerArray = new Concours.JATableByClassColHeader[schedulebyclassheaderList.size()];
    schedulebyclassheaderList.toArray(headerArray); // fill the headerArray
        
    Object [][] rowArray = theConcours.UpdateJAByClassTableRowData(schedulebyclassheaderList);
    tblSchedByClass.setAutoResizeMode(AUTO_RESIZE_OFF);
    DefaultTableModel mdlSchedByClass = new javax.swing.table.DefaultTableModel(
        rowArray,
        headerArray
    );
    tblSchedByClass.setModel(mdlSchedByClass);
    TableColumnAdjuster tca = new TableColumnAdjuster(tblSchedByClass, 6);
    tca.adjustColumns(); 
    int [] SchedBylassRowHeights = new int[rowArray.length];
    getRowHeights(tblSchedByClass, SchedBylassRowHeights)  ;      
    // By changing d.height in the table ColumnModel getPreferredSize() we effect dynamic adjustment of header height!
    // See Anubis posting in StackOverflow 7/27/2012

    tblSchedByClass.setTableHeader(new JTableHeader(tblSchedByClass.getColumnModel()) {
    @Override public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = byClassHeaderHeight;
        return d;
    }
    });

    Arrays.sort(SchedBylassRowHeights);
    int rowheight = SchedBylassRowHeights[SchedBylassRowHeights.length-1] + 10; //+ 30;
    tblSchedByClass.setRowHeight(rowheight);
    spnlSchedByClass.setViewportView(tblSchedByClass);
    theTabbedPane. addTab("Schedule by Class", spnlSchedByClass);


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1500, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        
       
    
}
*/
    /*
public final void updateJACompressedTable(){
    ScheduleCompressedPdfFileName = strParentOfDBFIle + "\\ScheduleCompact.pdf";
    //List<Concours.JATableCompressedColHeader> headerList;
    compressedscheduleheaderList = theConcours.UpdateJACompressedTableHeader(theConcours);
    // Transfer list to array because that's what the table model constructor expect...
    Concours.JATableCompressedColHeader[] headerArray = new Concours.JATableCompressedColHeader[compressedscheduleheaderList.size()];
    compressedscheduleheaderList.toArray(headerArray); // fill the headerArray
        
    Object [][] rowArray = theConcours.UpdateJACompressedTableRowData(compressedscheduleheaderList);
    tblSchedCompressed.setAutoResizeMode(AUTO_RESIZE_OFF);
    DefaultTableModel mdlSchedCompressed = new javax.swing.table.DefaultTableModel(
        rowArray,
        headerArray
    );
    tblSchedCompressed.setModel(mdlSchedCompressed);
    TableColumnAdjuster tca = new TableColumnAdjuster(tblSchedCompressed, 6);
    tca.adjustColumns(); 
    int [] SchedCompressedRowHeights = new int[rowArray.length];
    getRowHeights(tblSchedCompressed, SchedCompressedRowHeights)  ;      
    // By changing d.height in the table ColumnModel getPreferredSize() we effect dynamic adjustment of header height!
    // See Anubis posting in StackOverflow 7/27/2012

    tblSchedCompressed.setTableHeader(new JTableHeader(tblSchedCompressed.getColumnModel()) {
    @Override public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = compressedHeaderHeight;
        return d;
    }
    });

    Arrays.sort(SchedCompressedRowHeights);
    int rowheight = SchedCompressedRowHeights[SchedCompressedRowHeights.length-1]  + 10; //+ 30;
    tblSchedCompressed.setRowHeight(rowheight);
    spnlSchedCompressed.setViewportView(tblSchedCompressed);
    theTabbedPane.addTab("Compressed Format Schedule", spnlSchedCompressed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(theTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1500, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(theTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        
       
    
}
*/
/*
public void getRowHeights(javax.swing.JTable aTable,  int [] aRowHeights ){
        //
        // Scan aTable to find needed  row heights
        // Note: each <p> </p> is a separate line in the cell
        //
        //int initialRowHeight = aTable.getRowHeight();
        int initialRowHeight = 16;    //problem with aTable.getRowHeight(); is the row height grows on each height!
        String cellContent;
        org.jsoup.nodes.Document parsedCellContents;
        for(int iRow = 0; iRow < aTable.getRowCount(); iRow++){
            aRowHeights[iRow] =    initialRowHeight;         
        }
        for(int jCol = 0; jCol < aTable.getColumnCount(); jCol++){
            for(int iRow = 0; iRow < aTable.getRowCount(); iRow++){
              //  
                cellContent = (String) aTable.getValueAt(iRow, jCol);
                 // Some table cells are null, in which case aRowHeights[iRow] does not change
                if(cellContent != null){
                    parsedCellContents = Jsoup.parse(cellContent); // cellContent is html 
                    org.jsoup.select.Elements cellLines  =  parsedCellContents.select("p"); 
                    //System.out.println(paragraphs);
                    //aRowHeights[iRow] = Math.max(aRowHeights[iRow], cellLines.size()*ROW_HEIGHT_MULTIPLIER);
                    aRowHeights[iRow] = Math.max(aRowHeights[iRow], cellLines.size()*initialRowHeight);
                }
            }
        }
   }
*/
/*
    Not used....
*/    
    /*
public static void JTableToPNG(JTable aJTable, String aFileName) {
        // aJTable = createTable1();
        JScrollPane scroll1 = new JScrollPane(aJTable);

        aJTable.setPreferredScrollableViewportSize(aJTable.getPreferredSize());

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(scroll1, BorderLayout.CENTER);

        // without having been shown, fake a all-ready
        panel1.addNotify();

        // manually size to pref
        panel1.setSize(panel1.getPreferredSize());

        // validate to force recursive doLayout of children
        panel1.validate();

        BufferedImage bi = new BufferedImage(panel1.getWidth(), panel1.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics g = bi.createGraphics();
        panel1.paint(g);
        g.dispose();

        JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bi)));
        boolean OK = false;
        try {
            OK = ImageIO.write(bi, "png", new File(aFileName));
            if (!OK) {
                okDialog("Error: png not recognized by ImagaIO.write()");
            }
        } catch (IOException ex) {
            okDialog("ERROR:IOException in JTableToPNG");
            theConcours.GetLogger().log(Level.SEVERE, "ERROR:IOException in JTableToPNG", ex);
            System.exit(-1);
        }
        
    }
*/
    /*
    public static void JTableToPdf(JTable aJTable, Object [] aHeader, String aFileName, int aPageSize, String aConcoursName, String aColumnwise) {
        String subTitle = "Concours Chair: " + theConcours.GetConcoursChair() +  "     Chief Judge: " + theConcours.GetConcoursChiefJudge() +
                "     Prepared by: " + theConcours.GetConcoursUserName() + " using ConcoursBuilder version " + theConcours.GetCBVersion();
        JTablePdfExporter jtablepdfexporter = new JTablePdfExporter(theConcours, aFileName, aConcoursName + " Judging Schedule " +  aColumnwise,  subTitle, "JCNA Concours", footnote);
        jtablepdfexporter.ExportPdfFile(aJTable, aHeader, aPageSize, aColumnwise); // 0 = LETTER, 1 = LETTER_LANDSCAPE,  2 = LEGAL, 3 = LEGAL_LANDSCAPE
    }
    */

    /**
     * @param args the command line arguments
     */
   /*
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            String strDBName = "SDJC2014.db";
            String strPath = "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours" + "\\" + strDBName;
                Logger logger = Logger.getLogger("ConcoursBuilderLog");  
                FileHandler fh;  
                try {  
                    fh = new FileHandler(strPath);  // The log file will be in the strPath
                    logger.addHandler(fh);
                    SimpleFormatter formatter = new SimpleFormatter();  
                    fh.setFormatter(formatter);  
                    logger.info("ConcoursBuilder started");  
                } catch (SecurityException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                Concours concours = new Concours(logger, 3);
                new JudgeAssignGUI(true, concours,  "E = Entry O = Owner  C = Color  M = Model  J = Judge(first listed is lead)").setVisible(true);
            }
        });
    }
    */

        private javax.swing.JFileChooser fileChooserDB;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ChangeEntryTimeslot;
    private javax.swing.JMenuItem ChangeJudge;
    private javax.swing.JMenuItem CheckDups;
    private javax.swing.JMenuItem CheckForConflicts;
    private javax.swing.JMenuItem ChkForNonuniformJudgesInClass;
    private javax.swing.JMenuItem ChkForRepeatJudges;
    private javax.swing.JMenuItem CompressTimeslots;
    private javax.swing.JMenuItem DisplayJudgeLoads;
    private javax.swing.JMenu Edit;
    private javax.swing.JMenuItem Exit;
    private javax.swing.JMenu Export;
    private javax.swing.JMenuItem ExportByClass;
    private javax.swing.JMenuItem ExportByJudge;
    private javax.swing.JMenuItem ExportCompressedSchedule;
    private javax.swing.JMenu File;
    private javax.swing.JMenuItem InterchangeTimeslots;
    private javax.swing.JMenuItem LeadJudgesMenuItem;
    private javax.swing.JMenuItem New;
    private javax.swing.JMenuItem Open;
    private javax.swing.JMenuItem Save;
    private javax.swing.JMenu Validate;
    private javax.swing.JMenuItem chkSelfJudge;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane spnlSchedByClass;
    private javax.swing.JScrollPane spnlSchedByJudge;
    private javax.swing.JScrollPane spnlSchedCompressed;
    private javax.swing.JScrollPane spnlText;
    private javax.swing.JTable tblSchedByClass;
    private javax.swing.JTable tblSchedByJudge;
    private javax.swing.JTable tblSchedCompressed;
    public static javax.swing.JTextArea textarea;
    private javax.swing.JTabbedPane theTabbedPane;
    // End of variables declaration//GEN-END:variables

}
