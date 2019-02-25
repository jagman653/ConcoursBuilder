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
package editJA;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import com.itextpdf.text.DocumentException;
import static editJA.JTableXlsExporter.writeToExcel;
import static editJA.JudgeAssignGUI.theConcours;
//import static editJA.JudgeAssignGUI.theConcours;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import java.util.Collections;
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
//import static javax.print.attribute.standard.ReferenceUriSchemesSupported.FTP;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.apache.commons.net.ftp.FTPClient;
import org.jsoup.Jsoup;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.Concours.JATableByJudgeColHeader;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.JudgeAssignment;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.RowAndHeader;
import us.efsowell.concours.lib.TimeslotAssignment;
import org.apache.commons.net.ftp.FTP;




/**
 *
 * @author Owner
 */
public class JudgeAssignDialog extends javax.swing.JDialog {

   
    /**
     * Creates new form JudgeAssignDialog
     */
    public static  Concours theConcours; // making this a Class member allows references to it to be with the Class Name rather than an instance, e.g. JudgeAssignDialog.theConcours
    private static final int BY_JUDGE_HEADER_HEIGHT = 30; // 1 lines
    private static final int BY_CLASS_HEADER_HEIGHT = 80; // 3 lines
    private static final int COMPRESSED_HEADER_HEIGHT = 250; // 
    private static boolean standalone;
    private static String footnote;

    /*private static void writeToExcel(Logger GetLogger, int numRows, int numCols, String[] transHeaderRow, Object[][] theRowData, Path get) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */

    private  LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase;
    private static  Connection theConnection;
  
    
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
    DefaultTableModel mdlSchedCompressed;
    Map<Integer, String> timeslotIndexToTimeStringMap;
    Map<String, Integer> timeStringToTimeslotIndexMap;
    
    private static JTableXlsExporter jTableXlsExporter;
    
    
    public JudgeAssignDialog(java.awt.Frame parent, boolean modal, boolean aStandalone, Concours aConcours, String aFootnote) {
        super(parent, modal);
        standalone = aStandalone;
        theConcours = aConcours;
        footnote = aFootnote;
        ScheduleByClassPagesize = 3; // legal landscape
        ScheduleByJudgePagesize = 3; // legal landscape
        ScheduleCompressedPagesize = 1; // letter landscape
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase(); // for function access only
        initComponents();
       
        timeslotIndexToTimeStringMap = new HashMap<Integer, String>(); 
        timeStringToTimeslotIndexMap = new HashMap<String, Integer>(); 
        CompressTimeslots.setVisible(false);
        
        theConnection = theConcours.GetConnection();
        File f = new File(theConcours.GetThePath()); // just to get parent
        strParentOfDBFIle = f.getParent();
        theConcours.UpdateTimeslotStats(); 
        theConcours.updateJudgeLoads();
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();

        jTableXlsExporter = new JTableXlsExporter(theConcours); // files name & table name parameters set later when known
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        ExportPDF = new javax.swing.JMenu();
        ExportByClass = new javax.swing.JMenuItem();
        ExportByJudge = new javax.swing.JMenuItem();
        ExportCompressedSchedule = new javax.swing.JMenuItem();
        WindscreenPlacards = new javax.swing.JMenuItem();
        EntryPacketLabels = new javax.swing.JMenuItem();
        JudgePacketLabels = new javax.swing.JMenuItem();
        JudgeAssignmentsSheetsAllInOneFile = new javax.swing.JMenuItem();
        JudgeAssignmentsSheetsSeparateFiles = new javax.swing.JMenuItem();
        ExportWorkbookMenuItem = new javax.swing.JMenuItem();
        uploadFilesMenuItem = new javax.swing.JMenuItem();
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
        LeadJudgeMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Judge Assignment & Schedule View and Edit");
        setPreferredSize(new java.awt.Dimension(1666, 800));
        getContentPane().setLayout(null);

        theTabbedPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

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
        spnlSchedByClass.setViewportView(tblSchedByClass);

        theTabbedPane.addTab("Schedule By Class", spnlSchedByClass);

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
        spnlSchedByJudge.setViewportView(tblSchedByJudge);

        theTabbedPane.addTab("Schedule By Judge", spnlSchedByJudge);

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

        getContentPane().add(theTabbedPane);
        theTabbedPane.setBounds(27, 11, 1617, 851);

        File.setText("File");

        ExportPDF.setText("Export PDF");
        ExportPDF.setToolTipText("Export schedule, Entry packets, etc. to local files.");
        ExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportPDFActionPerformed(evt);
            }
        });

        ExportByClass.setText("Judging Schedule by Class");
        ExportByClass.setToolTipText("");
        ExportByClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportByClassActionPerformed(evt);
            }
        });
        ExportPDF.add(ExportByClass);

        ExportByJudge.setText("Judging Schedule by Judge");
        ExportByJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportByJudgeActionPerformed(evt);
            }
        });
        ExportPDF.add(ExportByJudge);

        ExportCompressedSchedule.setText("Compressed Judging Schedule");
        ExportCompressedSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportCompressedScheduleActionPerformed(evt);
            }
        });
        ExportPDF.add(ExportCompressedSchedule);

        WindscreenPlacards.setText("Windscreen Placards");
        WindscreenPlacards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WindscreenPlacardsActionPerformed(evt);
            }
        });
        ExportPDF.add(WindscreenPlacards);

        EntryPacketLabels.setText("Entry Packet Labels (Avery 5395/42395)");
        EntryPacketLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EntryPacketLabelsActionPerformed(evt);
            }
        });
        ExportPDF.add(EntryPacketLabels);

        JudgePacketLabels.setText("Judge Packet Labels (Avery 5395/42395)");
        JudgePacketLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JudgePacketLabelsActionPerformed(evt);
            }
        });
        ExportPDF.add(JudgePacketLabels);

        JudgeAssignmentsSheetsAllInOneFile.setText("Judge Assignments Sheets (All in one PDF file)");
        JudgeAssignmentsSheetsAllInOneFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JudgeAssignmentsSheetsAllInOneFileActionPerformed(evt);
            }
        });
        ExportPDF.add(JudgeAssignmentsSheetsAllInOneFile);

        JudgeAssignmentsSheetsSeparateFiles.setText("Judge Assignments Sheets ( Separate PDF files)");
        JudgeAssignmentsSheetsSeparateFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JudgeAssignmentsSheetsSeparateFilesActionPerformed(evt);
            }
        });
        ExportPDF.add(JudgeAssignmentsSheetsSeparateFiles);

        File.add(ExportPDF);

        ExportWorkbookMenuItem.setText("Export Excel Workbook");
        ExportWorkbookMenuItem.setToolTipText("Export schedule as an Excel Workbook file");
        ExportWorkbookMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportWorkbookMenuItemActionPerformed(evt);
            }
        });
        File.add(ExportWorkbookMenuItem);

        uploadFilesMenuItem.setText("Upload Exports");
        uploadFilesMenuItem.setToolTipText("Upload schedule, Entry packets, etc. for online access. Exports must exist.");
        uploadFilesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadFilesMenuItemActionPerformed(evt);
            }
        });
        File.add(uploadFilesMenuItem);

        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        File.add(Exit);

        jMenuBar1.add(File);

        Validate.setText("Validate");
        Validate.setToolTipText("Various checks on schedule");

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

        LeadJudgeMenuItem.setText("Set Lead Judge");
        LeadJudgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeadJudgeMenuItemActionPerformed(evt);
            }
        });
        Edit.add(LeadJudgeMenuItem);

        jMenuBar1.add(Edit);

        setJMenuBar(jMenuBar1);

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
    
    
    private void ChangeEntryTimeslotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeEntryTimeslotActionPerformed
        theConcours.GetLogger().info("Change Entry Timeslot.");
        
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), true);
        ChangeEntryTSInputDialog_2 changeEntryTSInputDialog = new ChangeEntryTSInputDialog_2(this, true, theConcours, timeslotIndexToTimeStringMap, timeStringToTimeslotIndexMap);
        changeEntryTSInputDialog.setTitle("Change Entry Timeslot");
        changeEntryTSInputDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Returning from ChangeEntryTSInputDialog_2 to Judge Assignment GUI");
            }
        });
        changeEntryTSInputDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        changeEntryTSInputDialog.setVisible(rootPaneCheckingEnabled);
        theConcours.UpdateTimeslotStats(); // UpdateTimeslotStats is updated here because of the change
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();
    }//GEN-LAST:event_ChangeEntryTimeslotActionPerformed

    private void InterchangeTimeslotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InterchangeTimeslotsActionPerformed
        theConcours.GetLogger().info("Interchange Timeslots");
        // false => do not add extre timeslot
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), false);
        InterchangeTimeslotsDialog_2 interchangeTimeslotsDialog = new InterchangeTimeslotsDialog_2(this, true, theConcours, timeslotIndexToTimeStringMap, timeStringToTimeslotIndexMap);
        interchangeTimeslotsDialog.setTitle("Interchange Timeslots");
        interchangeTimeslotsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                theConcours.GetLogger().info("Returning from InterchangeTimeslotsDialog_2 to Judge Assignment GUI");
            }
        });
        interchangeTimeslotsDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        interchangeTimeslotsDialog.setVisible(rootPaneCheckingEnabled);
        theConcours.UpdateTimeslotStats();
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();
    }//GEN-LAST:event_InterchangeTimeslotsActionPerformed
    // getJAByJudgeTable() allows JAByJudgeTable to be loaded, changed, etc. by methods of other Classes
    public  javax.swing.JTable getJAByJudgeTable(){
        return tblSchedByJudge ;
    }
    // getJAByClassTable() allows JAByClassTable to be loaded, changed, etc. by methods of other Classes
    public  javax.swing.JTable getJAByClassTable(){
        return tblSchedByClass ;
    }

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


public final void updateJAByClassTable(){
    ScheduleByClassPdfFileName = strParentOfDBFIle + "\\ScheduleByClass.pdf";
    //List<Concours.JATableByClassColHeader> headerList;
    schedulebyclassheaderList = theConcours.UpdateJAByClassTableHeader(theConcours);
    // Transfer list to array because that's what the table model constructor expect...
    Concours.JATableByClassColHeader[] headerArray = new Concours.JATableByClassColHeader[schedulebyclassheaderList.size()];
    schedulebyclassheaderList.toArray(headerArray); // fill the headerArray
    // schedulebyclassheaderList is List<JATableByClassColHeader>     
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

public final void updateJACompressedTable(){
    ScheduleCompressedPdfFileName = strParentOfDBFIle + "\\ScheduleCompact.pdf";
    //List<Concours.JATableCompressedColHeader> headerList;
    /* following changed for the compress merge method 7/20/2018
    compressedscheduleheaderList = theConcours.UpdateJACompressedTableHeader(theConcours);
    // Transfer list to array because that's what the table model constructor expect...
    Concours.JATableCompressedColHeader[] headerArray = new Concours.JATableCompressedColHeader[compressedscheduleheaderList.size()];
    compressedscheduleheaderList.toArray(headerArray); // fill the headerArray
    
        
    Object [][] rowArray = theConcours.UpdateJACompressedTableRowData(compressedscheduleheaderList);
    */
    // ++++++++++++++++++++++++++++++++++++++
    schedulebyclassheaderList = theConcours.UpdateJAByClassTableHeader(theConcours);
    // Transfer list to array because that's what the table model constructor expect...
    //Concours.JATableByClassColHeader[] headerArray = new Concours.JATableByClassColHeader[schedulebyclassheaderList.size()];
    //schedulebyclassheaderList.toArray(headerArray); // fill the headerArray
    // ----------------------------------------------------------------
    //DefaultTableModel mdlSchedCompressed;
//working here
    RowHeader rowheader;
    rowheader = theConcours.UpdateJACompressedTableRowDataAndHeader(theConcours, schedulebyclassheaderList); // returns a new RowHeader
   // mdlSchedCompressed = new javax.swing.table.DefaultTableModel 
   // Create the compressed table model
    DefaultTableModel mdlSchedCompressed = new javax.swing.table.DefaultTableModel(
        rowheader.getRowArray(),
        rowheader.getHeaderArray()
    );
    // 
    compressedscheduleheaderList = new ArrayList<Concours.JATableMergeCompressedColHeader>();
    for(int jCol = 0; jCol <  rowheader.getHeaderArray().length; jCol++){
        compressedscheduleheaderList.add(rowheader.getHeaderArray()[jCol]);
    }
    //compressedscheduleheaderList = theConcours.UpdateJACompressedTableHeader(theConcours); // 7/30/2018
    tblSchedCompressed.setAutoResizeMode(AUTO_RESIZE_OFF);
    
    int numRows = mdlSchedCompressed.getRowCount();
    
    tblSchedCompressed.setModel(mdlSchedCompressed);
    TableColumnAdjuster tca = new TableColumnAdjuster(tblSchedCompressed, 6);
    tca.adjustColumns(); 
    //int [] SchedCompressedRowHeights = new int[rowArray.length];
    int [] SchedCompressedRowHeights = new int[numRows];
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
}


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


    private void ChangeJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeJudgeActionPerformed
        theConcours.GetLogger().info("Change Class Judge");
        ChangeClassJudgeInputDialog_2 changeClassJudgeInputDialog = new ChangeClassJudgeInputDialog_2(this, true, theConcours);
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
    }//GEN-LAST:event_ChangeJudgeActionPerformed

    private void CompressTimeslotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CompressTimeslotsActionPerformed
        List<String> res;
        //System.out.println("Attempting to compress timeslots");
        textarea.append("\nCompress timeslots not implemented");
        /*
        res = theConcours.CompressTimeslots(theConcours.GetTimeslotAssignments());
        if(!res.isEmpty()) {
            textarea.append("\nCompressions done:\n");
            Iterator iter = res.iterator();
            while (iter.hasNext()) {
                //System.out.println(iter.next());
                textarea.append(iter.next().toString() + "\n");
            }
            textarea.append("\n");

        }

        else{
            textarea.append("\nNo compressions done");
        }
        */
    }//GEN-LAST:event_CompressTimeslotsActionPerformed

    private void ExportByClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportByClassActionPerformed
        //JTableToPNG(tblSchedByClass, ScheduleByClassPdfFileName);
        theConcours.GetLogger().info("INFO: ExportByClassActionPerformed");
        theConcours.UpdateTimeslotStats();
        theConcours.updateJudgeLoads();
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();
        Concours.JATableByClassColHeader[] headerArray = new Concours.JATableByClassColHeader[schedulebyclassheaderList.size()];
        schedulebyclassheaderList.toArray(headerArray); // fill the headerArray
        String concoursName = theConcours.GetConcoursName().replaceFirst(".db", "");

        jTableToPdf(tblSchedByClass, headerArray, ScheduleByClassPdfFileName, ScheduleByClassPagesize, concoursName, "by Class");
        okDialog("Exported " + concoursName + " Schedule by Class to Folder " + theConcours.GetConcoursBuilderDataPath() + "\\" + concoursName);
    }//GEN-LAST:event_ExportByClassActionPerformed

    private void ExportByJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportByJudgeActionPerformed
        //JTableToPNG(tblSchedByJudge, ScheduleByJudgePdfFileName);
        //
        //   To be sure the column headers etc get set up
        //
        theConcours.GetLogger().info("INFO: ExportByJudgeActionPerformed" );
        theConcours.UpdateTimeslotStats();
        theConcours.updateJudgeLoads();
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();
        Concours.JATableByJudgeColHeader[] headerArray = new Concours.JATableByJudgeColHeader[schedulebyjudgeheaderList.size()];
        schedulebyjudgeheaderList.toArray(headerArray); // fill the headerArray
        String concoursName = theConcours.GetConcoursName().replaceFirst(".db", "");
        jTableToPdf(tblSchedByJudge, headerArray, ScheduleByJudgePdfFileName, ScheduleByJudgePagesize, concoursName, " by Judge");
        okDialog("Exported " + concoursName + " Schedule by Judge to Folder " + theConcours.GetConcoursBuilderDataPath() + "\\" + concoursName);
    }//GEN-LAST:event_ExportByJudgeActionPerformed

    private void ExportCompressedScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportCompressedScheduleActionPerformed
        //JTableToPNG(tblSchedCompressed, ScheduleCompressedPdfFileName);
        theConcours.GetLogger().info("INFO: ExportCompressedScheduleActionPerformed" );

        theConcours.UpdateTimeslotStats();
        theConcours.updateJudgeLoads();
        updateJAByJudgeTable();
        updateJAByClassTable();
        updateJACompressedTable();
        Concours.JATableMergeCompressedColHeader[] headerArray = new Concours.JATableMergeCompressedColHeader[compressedscheduleheaderList.size()];
        compressedscheduleheaderList.toArray(headerArray); // fill the headerArray
        String concoursName = theConcours.GetConcoursName().replaceFirst(".db", "");
        jTableToPdf(tblSchedCompressed, headerArray, ScheduleCompressedPdfFileName, ScheduleCompressedPagesize, concoursName, "Compact");
        okDialog("Exported " + concoursName + " Compressed Schedule to Folder " + theConcours.GetConcoursBuilderDataPath() + "\\" + concoursName);
    }//GEN-LAST:event_ExportCompressedScheduleActionPerformed

    private void ExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportPDFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ExportPDFActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
        // For the changes to stick we must write Judge assignments and schedule to the DB file
        //
        // Closing and reopening to avoid locked database???
        //
        
        if(theConnection != null){
            Connection newConn = null;
            try {
                theConnection.close();
            } catch (SQLException ex) {
                String msg = "SQLException while closing the database before JudgeAssignmentsMemToDB()";
                okDialog(msg);
                theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            }
            String strDB = theConcours.GetThePath() ;
            try {
                Class.forName("org.sqlite.JDBC");
                String strConn = "jdbc:sqlite:" + strDB ;
                newConn = DriverManager.getConnection(strConn);
                theConcours.GetLogger().info("Reopened database " + strConn + " successfully");
            } catch ( ClassNotFoundException | SQLException e ) {
                String msg = "ClassNotFoundException or SQLException while repoening the database before JudgeAssignmentsMemToDB()";
                okDialog(msg);
                theConcours.GetLogger().log(Level.SEVERE, msg, e);
            }

            //theConcours.GetLogger().info("Updating JudgeAssignemnts in memory");
            //loadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB(newConn,  theConcours);
            //LoadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB(newConn,  theConcours);
            theConcours.SetConnection(newConn);
            theConnection = newConn;
        }
       
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
                Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Exiting");
            System.exit(0); // Closes the application
        } else{
            this.setVisible(false);
        }

    }//GEN-LAST:event_ExitActionPerformed

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

    private void ExportWorkbookMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportWorkbookMenuItemActionPerformed
    //
    theConcours.GetLogger().info("INFO: ExportWorkbookMenuItemActionPerformed");
    theConcours.UpdateTimeslotStats();
    theConcours.updateJudgeLoads();
    updateJAByJudgeTable();
    updateJAByClassTable();
    updateJACompressedTable();
    Concours.JATableByJudgeColHeader[] ByJudgeHeaderArray = new Concours.JATableByJudgeColHeader[schedulebyjudgeheaderList.size()];
    schedulebyjudgeheaderList.toArray(ByJudgeHeaderArray); // fill the ByJudgeHeaderArray

    Concours.JATableByClassColHeader[] ByClassHeaderArray = new Concours.JATableByClassColHeader[schedulebyclassheaderList.size()];
    schedulebyclassheaderList.toArray(ByClassHeaderArray); // fill the ByClassHeaderArray

    Concours.JATableMergeCompressedColHeader[] CompressedHeaderArray = new Concours.JATableMergeCompressedColHeader[compressedscheduleheaderList.size()];
    compressedscheduleheaderList.toArray(CompressedHeaderArray); // fill the CompressedHeaderArray

    String concoursName = theConcours.GetConcoursName().replaceFirst(".db", "");
    //String strPath = "C:\\Users\\Owner\\Documents\\NetBeansProjects\\EditJudgeAssignments\\JavaGenerated.xlsx";
    //String strPath = strParentOfDBFIle +  "\\JavaGenerated.xlsx";
    String strPath = strParentOfDBFIle +  "\\" + concoursName + ".xlsx";
    okDialog("Will write Excel file to " + strPath);
       theConcours.GetLogger().info("Will write Excel file to " + strPath);
       File f = new File(strPath);
       boolean itExists = false;
       if(f.exists()){
           itExists = true;
           int response = yesNoDialog("There is an existing Excel file " + strPath + ". Do you wish to overwrite it?");
           if(response == JOptionPane.NO_OPTION){
               return;
           } 
       }
       // Either it doesn't exist, or it does exist but the user wishes to overwrite it
       if(itExists){
           f.delete();
       }     
       try {
            writeToExcel(theConcours.GetLogger(), tblSchedByJudge, ByJudgeHeaderArray, tblSchedByClass, ByClassHeaderArray, tblSchedCompressed, CompressedHeaderArray,  Paths.get(strPath));
       } catch (IOException ex) {
           String msg = "Could not write to " + strPath + " Either it doesn't exist or is in use by another application, e.g., Excel. Close it and retry";
           okDialog(msg);
           Logger.getLogger(JTableXlsExporter.class.getName()).log(Level.INFO, msg, ex);
       }
    }//GEN-LAST:event_ExportWorkbookMenuItemActionPerformed

    private void LeadJudgeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeadJudgeMenuItemActionPerformed
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

    }//GEN-LAST:event_LeadJudgeMenuItemActionPerformed

    private void WindscreenPlacardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WindscreenPlacardsActionPerformed
        String concoursBuilderDocsPath = theConcours.GetConcoursBuilderDocsPath();
        String concoursBuilderDataPath = theConcours.GetConcoursBuilderDataPath();
        // Create timeslot Maps
        //createTimeslotMap(String aStrStartTime, String aStrLunchTime, Integer aTimeslotInterval, Integer aSlotsBeforeLunch, Integer aLunchInterval, boolean aAddNewSlot){        
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), true);
        CreateWindscreenPlacards   createWindscreenPlacards = new CreateWindscreenPlacards(theConcours, concoursBuilderDocsPath, concoursBuilderDataPath);
        createWindscreenPlacards.fillInAllPlacardForms(timeslotIndexToTimeStringMap);
    }//GEN-LAST:event_WindscreenPlacardsActionPerformed

    private void EntryPacketLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EntryPacketLabelsActionPerformed
        //
        // Scan Concours Entries and generate a lable for each. These should be placed on the envelopes
        // handed to eache entrant upon arrival.
        //
        String concoursBuilderDocsPath = theConcours.GetConcoursBuilderDocsPath();
        String concoursBuilderDataPath = theConcours.GetConcoursBuilderDataPath();
        // Create timeslot Maps
        //createTimeslotMap(String aStrStartTime, String aStrLunchTime, Integer aTimeslotInterval, Integer aSlotsBeforeLunch, Integer aLunchInterval, boolean aAddNewSlot){        
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), true);
        CreateEntryPacketLabels   createEntryPacketLabels = new CreateEntryPacketLabels(theConcours, concoursBuilderDocsPath, concoursBuilderDataPath);
        try {
            createEntryPacketLabels.createTheLabelsPdf(timeslotIndexToTimeStringMap);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, "File not found in EntryPacketLabelsActionPerformed", ex);
        } catch (DocumentException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_EntryPacketLabelsActionPerformed

    private void JudgePacketLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JudgePacketLabelsActionPerformed
        //
        // Scan Concours Judges and generate a lable for each. These should be placed on envelopes, or perhaps 
        // the schedules handed out att he Judges Meeting.
        //
        String concoursBuilderDocsPath = theConcours.GetConcoursBuilderDocsPath();
        String concoursBuilderDataPath = theConcours.GetConcoursBuilderDataPath();
        // Create timeslot Maps
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), true);
        CreateJudgePacketLabels   createJudgePacketLabels = new CreateJudgePacketLabels(theConcours, concoursBuilderDocsPath, concoursBuilderDataPath);
        
        try {
            createJudgePacketLabels.createTheJudgeLabelsPdf(timeslotIndexToTimeStringMap);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }//GEN-LAST:event_JudgePacketLabelsActionPerformed

    private void JudgeAssignmentsSheetsAllInOneFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JudgeAssignmentsSheetsAllInOneFileActionPerformed
        //
        // Scan Concours Judges and generate a 8.5x11 sheet for each. These  handed out at the Judges Meeting.
        // Produces a single PDF with a page for each judge.
        //
        String concoursBuilderDocsPath = theConcours.GetConcoursBuilderDocsPath();
        String concoursBuilderDataPath = theConcours.GetConcoursBuilderDataPath();
        // Create timeslot Maps
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), true);
        CreateJudgeAssignmentsSheetsSingleFile   createJudgeAssignmentsSheets = new CreateJudgeAssignmentsSheetsSingleFile(theConcours, concoursBuilderDocsPath, concoursBuilderDataPath);
        
        
        try {
            createJudgeAssignmentsSheets.createTheJudgeAssignmentsSheetsPdf(timeslotIndexToTimeStringMap);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }//GEN-LAST:event_JudgeAssignmentsSheetsAllInOneFileActionPerformed

    private void JudgeAssignmentsSheetsSeparateFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JudgeAssignmentsSheetsSeparateFilesActionPerformed
        //
        // Scan Concours Judges and generate a 8.5x11 sheet for each. These  handed out at the Judges Meeting.
        // Produces a separate PDF with for each judge.
        //
        String concoursBuilderDocsPath = theConcours.GetConcoursBuilderDocsPath();
        String concoursBuilderDataPath = theConcours.GetConcoursBuilderDataPath();
        // Create timeslot Maps
        createTimeslotMap(theConcours.getStartTime(), theConcours.getLunchTime(), theConcours.getTimeslotInterval(), theConcours.getSlotsBeforeLunch(), theConcours.GetConcoursLunchInterval(), true);
        CreateJudgeAssignmentsSheetsIndividual   createJudgeAssignmentsSheetsIndividual = new CreateJudgeAssignmentsSheetsIndividual(theConcours, concoursBuilderDocsPath, concoursBuilderDataPath);
        
        
        try {
            createJudgeAssignmentsSheetsIndividual.createTheJudgeAssignmentsSheetsIndivualPdf(timeslotIndexToTimeStringMap);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(JudgeAssignDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }//GEN-LAST:event_JudgeAssignmentsSheetsSeparateFilesActionPerformed

    
    
            
    private void uploadFilesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadFilesMenuItemActionPerformed
        String server = "nx.dnslinks.net";
        int port = 21;
        String user = "eds653";
        String pass = "#S1h0Hwp5";
        String strConcoursName = theConcours.GetConcoursName().replace(".db", "");
        
        String concoursBuilderDataPath = theConcours.GetConcoursBuilderDataPath() + "\\" + strConcoursName + "\\JudgeAssignmentsSheets";
        String strFileName = "JudgeAssignmentsSheets.pdf";
       
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
 
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            //Uploads the file using an InputStream
            String strFilename = "JudgeAssignmentsSheets.pdf";
            File localFile = new File(concoursBuilderDataPath + "\\" + strFilename);
            if(!localFile.exists()){
                System.out.println("The local file " + concoursBuilderDataPath + "\\JudgeAssignmentsSheets.pdf" + " not found.");
            }
            String remoteDirectory = "/httpdocs/manual-uploads/Testing";
            int replyCode = -1;
            if(ftpClient.cwd(remoteDirectory) == 550){
                System.out.println("remoteDirectory doesn't exist so create it");
                boolean result = ftpClient.makeDirectory(remoteDirectory);
                replyCode = ftpClient.getReplyCode();
                System.out.println("ReplyCode = " + replyCode);
            }
            System.out.println("ReplyCode = " + replyCode);
            //String fileName = "JudgeAssignmentsSheets.pdf";
            String remotePath = remoteDirectory + "/" + strFilename;
            InputStream inputStream = new FileInputStream(localFile);
 
            System.out.println("Start uploading  file");
            boolean done = ftpClient.storeFile(remotePath, inputStream);
            int replycode = ftpClient.getReplyCode() ;
            inputStream.close();
            if (done) {
                System.out.println("The file upload finished with done == true, so it must have worked.\nReply code = " + replycode);
            } else {
                System.out.println("The file upload finished with done == false, so it must have failed.\nReply code = " + replycode);
            }
     /*
            // APPROACH #2: uploads second file using an OutputStream
            File secondLocalFile = new File("D:\\DocumentsD\\temp\\dist\\README.TXT");
            if(!secondLocalFile.exists()){
                System.out.println("The second local file not found.");
            }
            String secondRemoteFile = "README.TXT";
            inputStream = new FileInputStream(secondLocalFile);
 
            System.out.println("Start uploading second file");
            OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
            byte[] bytesIn = new byte[4096];
            int read = 0;
 
            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();
 
            boolean completed = ftpClient.completePendingCommand();
            if (completed) {
                System.out.println("The second file is uploaded successfully.");
            }
        */
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }//GEN-LAST:event_uploadFilesMenuItemActionPerformed

                                                    



    public static void jTableToPdf(JTable aJTable, Object [] aHeader, String aFileName, int aPageSize, String aConcoursName, String aColumnwise) {
        String subTitle = "Concours Chair: " + theConcours.GetConcoursChair() +  "     Chief Judge: " + theConcours.GetConcoursChiefJudge() +
                "     Prepared by: " + theConcours.GetConcoursUserName() + " using ConcoursBuilder version " + theConcours.GetCBVersion();
        String fn;
        if(aColumnwise.contains("by Judge")){
            int pos = footnote.indexOf('J');
            fn = footnote.substring(0, pos); // Omit " J: (First listed is Lead Judge)"
            fn = fn + "Bold indicates column Judge leads Entry judging";
        } else {
            fn = footnote;  
        }
        JTablePdfExporter jtablepdfexporter = new JTablePdfExporter(theConcours, aFileName, aConcoursName + " Judging Schedule " +  aColumnwise,  subTitle, "JCNA Concours", fn);
        jtablepdfexporter.ExportPdfFile(aJTable, aHeader, aPageSize, aColumnwise); // 0 = LETTER, 1 = LETTER_LANDSCAPE,  2 = LEGAL, 3 = LEGAL_LANDSCAPE
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
            java.util.logging.Logger.getLogger(JudgeAssignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JudgeAssignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JudgeAssignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JudgeAssignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                /*
                JudgeAssignDialog dialog = new JudgeAssignDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
                */
            String strDBName = "TestUserSettings";
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
    //       public JudgeAssignDialog(java.awt.Frame parent, boolean modal, boolean aStandalone, Concours aConcours, String aFootnote) {
                new JudgeAssignDialog(new javax.swing.JFrame(), true, true, concours,  "E = Entry O = Owner  C = Color  M = Model  J = Judge(first listed is lead)").setVisible(true);
            }
                
            
        });
    }
    private javax.swing.JFileChooser fileChooserDB;
    private javax.swing.JFileChooser fileChooser;

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
    private javax.swing.JMenuItem EntryPacketLabels;
    private javax.swing.JMenuItem Exit;
    private javax.swing.JMenuItem ExportByClass;
    private javax.swing.JMenuItem ExportByJudge;
    private javax.swing.JMenuItem ExportCompressedSchedule;
    private javax.swing.JMenu ExportPDF;
    private javax.swing.JMenuItem ExportWorkbookMenuItem;
    private javax.swing.JMenu File;
    private javax.swing.JMenuItem InterchangeTimeslots;
    private javax.swing.JMenuItem JudgeAssignmentsSheetsAllInOneFile;
    private javax.swing.JMenuItem JudgeAssignmentsSheetsSeparateFiles;
    private javax.swing.JMenuItem JudgePacketLabels;
    private javax.swing.JMenuItem LeadJudgeMenuItem;
    private javax.swing.JMenu Validate;
    private javax.swing.JMenuItem WindscreenPlacards;
    private javax.swing.JMenuItem chkSelfJudge;
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
    private javax.swing.JMenuItem uploadFilesMenuItem;
    // End of variables declaration//GEN-END:variables
}
