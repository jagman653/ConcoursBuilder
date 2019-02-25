/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import au.com.bytecode.opencsv.CSVReader;
import editJA.RowHeader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
//import java.lang.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.UniqueDescription;
import static us.efsowell.concours.lib.LoadSQLiteConcoursDatabase.UniqueName;


/**
 *
 * @author Ed Sowell
 */
        
public class Concours {
    
    Logger theLogger;
    int numSavedLogs;
    String theInstallationPath;
    String theVersion; // ConcourseBuilder Version
    JudgeAssignments theJudgeAssignments;
    JCNAClasses theJCNAClasses;
    JCNAClassRules_2 theJCNAClassRules;
    Entries theEntries ; // the data in Entries worksheet
    Judges theJudges; // the data in Judges worksheet
    Owners theOwners; // the data in Judges worksheet
    MasterPersonnel theMasterPersonnel;
    MasterJaguars theMasterJaguars;
    ConcoursPersonnel theConcoursPersonnel;
    ConcoursClasses theConcoursClasses; // the data in Classes worksheet
    TimeslotAssignments theTimeslotAssignments;
    boolean boolJudgeAssignmentCurrent;
    String thePath; // to where all the input files are
    String concoursBuilderDataPath; //This is the parent of the Concours Folders.  
                                    //E.g.,  ConcoursBuilderDataPath\TutorialE5J5\TutorialE5J5.db.
                                    //Also, the log files are placed here, e.g.,   ConcoursBuilderLog_0.log .
    String concoursBuilderDocsPath; // This is where various documents used by the ConcoursBuilder program are kept,
                                    //including ConcoursBuilder Users Manual.pdf, AcknowledgmentsCB.txt, licenseCB.txt,
                                    // and the score sheets and placard forms.
    Connection conn; // Sqlite Connection to the selected concours database file, e.g., SDJC2014.db
    String strStartTime; // From DB
    String strLunchTime;  // Calculated
    int timeslotInterval;// From DB
    int slotsBeforeLunch;// From DB
    int lunchInterval;// From DB
    
    int titleFontSize;
    int subtitleFontSize;
    int headerFontSize;
    int cellFontSize;
    int footnoteFontSize;
    
    String userName; 
    String concoursChair; 
    String concoursChiefJudge; 
    Integer concoursCompression; // 8/12/2018 max number of Classes in column after compression for Compact schedule view.
    int MAX_MODEL_YEAR; // used to initialize CBO in AddMasterJaguar_2
    int MAX_FIRST_NAME_EXTENSION; // used in construction of unique name 
    //private static final int MAX_CLASSES_PER_COMPRESSED_COL = 4; // 

    Map<String, String> masterJagColorToScheduleColorMap;
    // 11/8/2018 moved from ChangeEntryTSInputDialog
    //Map<Integer,String> timeslotIndexToTimeStringMap; 
    //Map<String, Integer> timeStringToTimeslotIndexMap; 

   // private Map<Integer, String> timeslotIndexToTimeStringMap;

    String strConcoursName;
    boolean boolPreassignedJudges;  // true if any ConcoursClass has preassigned Judges

    // 
    // Build the list of judge assignments
    //
    //private JudgeAssignment aJudgeAssignment;
    
 // Constructor
    public Concours(Logger aLogger, int aNumSavedLogs){ 
     
        theLogger = aLogger;
        numSavedLogs = aNumSavedLogs; // current plus aNumSavedLogs-1 previous
        theJudgeAssignments = new JudgeAssignments();
        theJCNAClasses = new JCNAClasses(theLogger);
        theJCNAClassRules = new JCNAClassRules_2(theLogger);
        //JudgeAssignments = new ArrayList<>();
        theEntries = new Entries(theLogger);
        theOwners = new Owners();
        theMasterPersonnel = new MasterPersonnel();
        theConcoursPersonnel = new ConcoursPersonnel();
        theMasterJaguars = new MasterJaguars() ;
        theTimeslotAssignments = new TimeslotAssignments();

        theJudges = new Judges();
        theConcoursClasses = new ConcoursClasses();
        /*strStartTime = "10:00";
        timeslotInterval = 23;     // minutes
        lunchInterval = 60; // minutes
        strLunchTime = "12:00"; 
        slotsBeforeLunch = 5; 
        */
        MAX_FIRST_NAME_EXTENSION = 3;
        //MAX_MODEL_YEAR = 2040;
        
        masterJagColorToScheduleColorMap = new HashMap<>();
        
       // timeslotIndexToTimeStringMap = new HashMap<Integer, String>(); 
        //createTimeslotMap(strStartTime, strLunchTime, timeslotInterval, slotsBeforeLunch); Can't do this here because we don't know how many timeslots yet
       // theTimeslotAssignments = aTimeslotAssignments;  // a referece to timeslotAssignments in JudgeAssignGUI
       theLogger.info("Constructed theConcours");
    }
////////////////////////////////////////////////////////////////////////////////////////    
// LoadJudgeAssignmentCSV    
//         Loads the Judge Assignments    
public void LoadJudgeAssignmentCSV(String JAFile, Logger aLogger) throws FileNotFoundException, IOException
{

    //Path path = Paths.get(JAFile);
    //    thePath = path.getParent().toString();
    aLogger.info("Starting LoadJudgeAssignmentCSV");
        JudgeAssignment aJudgeAssignment;
	//try (BufferedReader br = new BufferedReader(new FileReader(JAFile)))
	//{
     	BufferedReader br = new BufferedReader(new FileReader(JAFile));

		String sCurrentLine;
                int iLine = 0;
		while ((sCurrentLine = br.readLine()) != null) {
                    //theLogger.info(sCurrentLine); 
                    if (iLine > 0){ // skip header line in JudgeAssign.txt
                          aJudgeAssignment =  new JudgeAssignment(sCurrentLine);
                          theJudgeAssignments.addJudgeAssignment(aJudgeAssignment);  
                         //theLogger.info ("theCar = " + aJudgeAssignment.GetCarIndex());
                         //theLogger.info ("theOwner = " + aJudgeAssignment.GetOwnerIndex()  );
                         //theLogger.info ("theTimeslot = " + aJudgeAssignment.GetTimeslot());
                         //theLogger.info ("Number of Judges = " + aJudgeAssignment.GetJudgeCount());
                         //theLogger.info ("Judges = " + aJudgeAssignment.GetJudgeIndicies() );
                    }
                    iLine++;
		}

    aLogger.info("Finished LoadJudgeAssignmentCSV");
	
//} catch (IOException e) {
	//	e.printStackTrace();
	//}
}





public void LoadEntriesCSV(String strPath, Logger aLogger){
    // Modified to add Owner MI... not tested!!!!
         // Read Entries CSV file and store in Entries ArrayList
    String EntriesCSV = strPath + "\\EntriesCsv.txt" ;
    //theLogger.info("EntriesCSV to be loaded: " + EntriesCSV);
    String [] nextLine;
    String strID;
    String strClass;
    String strYear;
    String strModel;
    String strDescription;
    String strUniqueDescription;
    String strOwnerFirst;
    String strOwnerMI;
    String strOwnerLast;
    String strOwnerUnique;
    String strJCNA; 
    String strColor;
    String strPlate;
    Integer intNode;
    CSVReader reader;
    int iLine;
    aLogger.info("Starting LoadEntriesCSV" );
    try {
            reader = new CSVReader(new FileReader(EntriesCSV ));
        iLine = 0;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
        // theLogger.info(nextLine[0] + nextLine[1] );
            if (iLine > 0){
                strID = nextLine[0];
                strClass = nextLine[1];
                strYear= nextLine[2];
                strDescription= nextLine[3];
                strOwnerFirst= nextLine[4];
                strOwnerMI= nextLine[5];
                strOwnerLast= nextLine[6];
                strOwnerUnique = UniqueName(strOwnerLast, strOwnerFirst);
                strUniqueDescription = UniqueDescription(strDescription, strOwnerUnique);
                strJCNA= nextLine[6]; 
                strColor= nextLine[7];
                strPlate= nextLine[8];
                //theLogger.info("Node:{" + nextLine[9] + "}");
                try{
                    intNode= (Integer)Integer.parseInt(nextLine[9]);
                } catch (NumberFormatException   ex) {
                    theLogger.info(" Number format exception while reading Entry node");
                    intNode = 999;
                }
                strModel =  "ModelNotInCSVFile"; //   Model was not original included. Working on fix 3/17/2018
                theEntries.GetConcoursEntries().add(new Entry(strID, strClass, strModel, strYear, strDescription, strUniqueDescription, strOwnerFirst, strOwnerMI,  strOwnerLast, strOwnerUnique, strJCNA, strColor, strPlate, intNode));
            }
            iLine++;
        }
    }    catch (FileNotFoundException ex) {
        
            aLogger.getLogger(Entries.class.getName()).log(Level.SEVERE, null, ex);
        }    
        catch (IOException ex) {
            okDialog("ERROR: IOException in LoadEntriesCSV");
            aLogger.getLogger(Entries.class.getName()).log(Level.SEVERE, "ERROR: in LoadEntriesCSV", ex);
            System.exit(-1);
        }
    
    aLogger.info("Finished LoadEntriesCSV" );
                
    
}

public void LoadEntriesDB(Connection aConn, Logger aLogger){
    
    aLogger.info("Starting LoadEntriesDB");
         // Read Entries from DB and store in Entries ArrayList
    String strID; //  from entry_name  in ConcoursEntries database table 
    String strClass; //  from  class  in ConcoursJaguars database table, key off of jaguar_id in ConcoursEntries table
    String strYear;  //  from ConcoursEntries database table
    String strModel;  // Introduced 3/17/2018. Will set it to strDescription until MasterJaguar DB table can be modified
    String strDescription;
    String strUniqueDescription;
    String strOwnerFirst;
    String strOwnerMI;
    String strOwnerLast;
    String strOwnerUnique;
    String strJCNA; 
    String strColor;
    String strPlate;
    Integer intEntryNode;
    int i;
    
    Long lngConcoursPersonnel_id;
    Long lngMasterPersonnel_id;
    Long lngJaguar_id;
    Long lngMasterJaguar_id; // get from masterjaguar_id in ConcoursJaguars
    //Integer intConcoursPersonnelNode;
    
    Statement stat_e;
    ResultSet rs_e;
    Statement stat_cj;
    ResultSet rs_cj;
    Statement stat_mj;
    ResultSet rs_mj;
    Statement stat_mp;
    ResultSet rs_mp;
    Statement stat_cp;
    ResultSet rs_cp;
    
    int iLine;
        try {    
            aConn.setAutoCommit(false);
            stat_e = aConn.createStatement();
            rs_e = stat_e.executeQuery("select * from ConcoursEntries;"); 
            stat_cj = aConn.createStatement();
            stat_mj = aConn.createStatement();
            stat_cp = aConn.createStatement();
            stat_mp = aConn.createStatement();
            i = 1;
            while (rs_e.next()) { 
                // get stuff from ConcoursEntries
                strID = rs_e.getString("entry_name");
                lngConcoursPersonnel_id = rs_e.getLong("concourspersonnel_id");
                lngJaguar_id = rs_e.getLong("jaguar_id");
                // get stuff from ConcoursJaguars
                rs_cj = stat_cj.executeQuery("select * from ConcoursJaguars where jaguar_id == " + lngJaguar_id + ";"); 
                strClass = rs_cj.getString("class");
                lngMasterJaguar_id = rs_cj.getLong("masterjaguar_id");
                intEntryNode = rs_cj.getInt("node");
                // get stuff from MasterJaguars
                rs_mj = stat_mj.executeQuery("select * from MasterJaguar where masterjaguar_id == " + lngMasterJaguar_id + ";"); 
                strYear = rs_mj.getString("year");
                strDescription = rs_mj.getString("description");
                //strModel = strDescription;                                  // MUST BE FIXED. REQUIRES TRAPPING AND FIXING 3/17/2017
                strModel = rs_mj.getString("model");
                strUniqueDescription = rs_mj.getString("unique_desc");
                strColor= rs_mj.getString("color");
                strPlate= rs_mj.getString("platevin");
                
                // get stuff from ConcoursPersonnel
                rs_cp = stat_cp.executeQuery("select * from ConcoursPersonnel where concourspersonnel_id == " + lngConcoursPersonnel_id + ";"); 
                lngMasterPersonnel_id = rs_cp.getLong("masterpersonnel_id");
               // intConcoursPersonnelNode = rs_cp.getInt("concourspersonnel_node");
                // get stuff from MasterPersonnel
                rs_mp = stat_mp.executeQuery("select * from MasterPersonnel where masterpersonnel_id == " + lngMasterPersonnel_id + ";"); 
                strOwnerFirst = rs_mp.getString("firstname");
                strOwnerMI = rs_mp.getString("mi");
                strOwnerLast = rs_mp.getString("lastname");
                strOwnerUnique= rs_mp.getString("unique_name");
                strJCNA= rs_mp.getString("jcna");
                aConn.commit();
                
                rs_cj.close();
                rs_mj.close();
                rs_cp.close();
                rs_mp.close();
                theEntries.GetConcoursEntries().add(new Entry(strID, strClass, strYear, strModel, strDescription, strUniqueDescription, strOwnerFirst, strOwnerMI, strOwnerLast, strOwnerUnique, strJCNA, strColor, strPlate, intEntryNode));
                i++;
            }
            aConn.commit();
            aConn.setAutoCommit(true);

            rs_e.close();
            stat_e.close();
            stat_cj.close();
            stat_mj.close();
            stat_cp.close();
            stat_mp.close();

        } catch (SQLException ex) {
            aLogger.getLogger(Concours.class.getName()).log(Level.SEVERE, null, ex);
        }
    aLogger.info("Finished LoadEntriesDB");
    
} // End LoadEntriesDB


    public void LoadOwnersCSV(String strPath, Logger aLogger){
    // Read Owners CSV file and store in Owners ArrayList
    //theLogger.info("OwnersCSV to be loaded: " + OwnersCSV);
    String [] nextLine;
    String strName;
    String strOwnerFirst; // not used
    String strOwnerUnique; 
    Integer intNode;
    int intCount;
    
    List<Integer> lstEntryList;
    aLogger.info("Starting LoadOwnersCSV");
    CSVReader reader;
    int intLine = 0;
    String OwnersCSV = strPath + "\\OwnersCsv.txt" ;
    try {
            reader = new CSVReader(new FileReader(OwnersCSV ));
        while ((nextLine = reader.readNext()) != null) {
            // First line is a Header and the line after the last Judge is empty
            if (intLine > 0 && !nextLine[0].isEmpty()) { 
                // nextLine[] is an array of values from the line
                // theLogger.info(nextLine[0] + nextLine[1] );
                strName = nextLine[0];
                strOwnerUnique = nextLine[2];
                if (!nextLine[1].isEmpty())
                    intNode= Integer.parseInt(nextLine[3]);
                else{
                    theLogger.info( "Error in reading Owner Node in " + OwnersCSV) ;
                    intNode = 99; // never used since exiting, but compiler insists that intNode be initialized
                    System.exit(1);
                }
                if (!nextLine[4].isEmpty())
                    intCount= Integer.parseInt(nextLine[4]);
                else{
                    theLogger.info( "Error in reading Owner self-judge count in " + OwnersCSV) ;
                    intCount = 99; // never used since exiting, but compiler insists that intNode be initialized
                    System.exit(1);
                }
                lstEntryList = new ArrayList<>();
                int k=3;
                for(int i = 0; i < intCount; i++){
                    if (!nextLine[k].isEmpty()) {
                        lstEntryList.add(Integer.parseInt(nextLine[k]));
                        k++;
                    }
                    else{
                        theLogger.info( "Error in reading Owner self-judge Node in " + OwnersCSV) ;
                        intCount = 99; // never used since exiting, but compiler insists that intNode be initialized
                        System.exit(1);
                    }
                }
            
                theOwners.GetConcoursOwners().add(new Owner(strOwnerUnique, intNode, intCount, lstEntryList));
            }
            intLine ++;
        }
    }    catch (FileNotFoundException ex) {
            okDialog("ERROR: FileNotFoundException in LoadOwnersCSV");
            aLogger.getLogger(Owners.class.getName()).log(Level.SEVERE, "ERROR: FileNotFoundException in LoadOwnersCSV", ex);
            System.exit(-1);
    }    catch (IOException ex) {
            okDialog("ERROR: IOException in LoadOwnersCSV");
            aLogger.getLogger(Owners.class.getName()).log(Level.SEVERE, "ERROR: IOException in LoadOwnersCSV", ex);
            System.exit(-1);
        }
      aLogger.info("Finished LoadOwnersCSV");
  
                
}

    
    public void LoadOwnersDB(Connection aConn, Logger aLogger){
        aLogger.info("Starting LoadOwnersDB");
        try {
            // Read Owners from concours database and store in Owners ArrayList
            Statement stat_rowcount;
            ResultSet rs_rowcount;
            
            Statement stat_co;
            ResultSet rs_co;
            Statement stat_oe;
            ResultSet rs_oe;
            int i;
            Long lngConcoursOwner_id;
            //Long lngConcoursPersonnel_id;
            String strOwnerUniqueName;
            Integer intConcoursPersonnelNode;
            ArrayList<Integer> aEntrylist;
            
            stat_co = aConn.createStatement();
            rs_co = stat_co.executeQuery("select * from ConcoursOwners;"); 
            i = 1;
            while (rs_co.next()) {
                // theLogger.info("Record: " + i + " Division: " + rs_c.getString("division") + " " + "Class: " + rs_c.getString("class") + " Node: " +  rs_c.getString("node")+ " Description: " +  rs_c.getString("description"));
                lngConcoursOwner_id = rs_co.getLong("concoursowner_id");
                //lngConcoursPersonnel_id = rs_co.getLong("concourspersonnel_id");
                strOwnerUniqueName = rs_co.getString("unique_name");
                intConcoursPersonnelNode = rs_co.getInt("concourspersonnel_node");
                aEntrylist = new ArrayList<>();
                stat_oe = aConn.createStatement();
                rs_oe = stat_oe.executeQuery("select * from ConcoursOwnersEntries where concoursowner_id == " + lngConcoursOwner_id + ";"); 
                while (rs_oe.next()){
                    Integer theEntryNode = rs_oe.getInt("entrynode");
                    aEntrylist.add(theEntryNode);
                }
                stat_oe.close();    
                rs_oe.close();
                theOwners.GetConcoursOwners().add(new Owner(strOwnerUniqueName,  intConcoursPersonnelNode, aEntrylist.size(), aEntrylist));
                i++;
            } 
            rs_co.close();
            stat_co.close();
        } catch (SQLException ex) {
            aLogger.getLogger(Concours.class.getName()).log(Level.SEVERE, null, ex);
        }
        aLogger.info("Finished LoadOwnersDB");
    

        }

    // Loads the MasterJaguars list from the MasterJaguars database table
    public  void LoadMasterJaguarDB(Connection aConn, Logger aLogger){
        aLogger.info(" Started LoadMasterJaguarDB ");
        Long masterjaguar_id;
        Long masterpersonnel_id;
        String jcnaclass_c;
        String jcnaclass_d;
        String joclacategory;
        Integer year;
        String model; // Introduced 3/17/2018
        String description;
        String unique_desc;
        String color;
        String platevin;
        
        MasterJaguar theMasterJaguar;

        Statement stat_mj;
        ResultSet rs_mj;
        int i ;
        try{
            aConn.setAutoCommit(false);
            stat_mj = aConn.createStatement();
            rs_mj = stat_mj.executeQuery("select * from MasterJaguar;"); 
            i = 1;
            while (rs_mj.next()) { 
            //    theLogger.info("Record: " + i + " Unique description: " + rs_mj.getString("unique_desc") + " " + "MasterPersonnel ID: " + rs_mj.getInt("masterpersonnel_id"));
                masterjaguar_id = rs_mj.getLong("masterjaguar_id");
                masterpersonnel_id = rs_mj.getLong("masterpersonnel_id");
                jcnaclass_c =  rs_mj.getString("jcnaclass_c");
                jcnaclass_d =  rs_mj.getString("jcnaclass_d");
                joclacategory =  rs_mj.getString("joclacategory");
                year = rs_mj.getInt("year");
                description =  rs_mj.getString("description");
               // model = description; //     MUST BE FIXED 3/17/2018 
                model = rs_mj.getString("model");
                unique_desc =  rs_mj.getString("unique_desc");
                color =  rs_mj.getString("color");
                platevin =  rs_mj.getString("platevin");
                theMasterJaguar = new MasterJaguar(masterjaguar_id, masterpersonnel_id, jcnaclass_c, jcnaclass_d, joclacategory, year, model, description, unique_desc, color, platevin);
                theMasterJaguars.AddJaguar(theMasterJaguar);
                i++;
            }
            aConn.commit();
            aConn.setAutoCommit(true);

            rs_mj.close();
            stat_mj.close();
        } catch (SQLException ex) {
            aLogger.getLogger(JCNAClasses.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        aLogger.info(" Finished LoadMasterJaguarDB ");
        
    }
    
                
public  void LoadMasterPersonnelDB(Connection aConn, Logger aLogger){
    
    aLogger.info("Starting LoadMasterPersonnelDB");
    Long masterpersonnel_id;
    Integer jcna;
    String club;
    String lastname;
    String firstname;
    String mi;  // 3/17/2018
    
    String unique_name;
    String judgestatus;
    Integer cert_year;
    String address_street;
    String city;
    String state;
    String country;
    String postalcode;
    String phone_work;
    String phone_home;
    String phone_cell;
    String email;
    Long lngMasterJaguarID;
    String strJCNAClassC;
    String strJCNAClassD;
    String strJOCLACategory;
    Integer intYear;
    String strModel; // Introduced 3/17/2018
    String strDescription;
    String strUniqueDescription;
    String strColor;
    String strPlateVIN;
    Statement stat_c;
    ResultSet rs_c;
    Statement stat_mj;
    MasterJaguar miJaguar;
    ArrayList<MasterJaguar> memberJaguarStable ;// = new ArrayList<>();
    int i ;
        try {
            //aConn.setAutoCommit(false);
            stat_c = aConn.createStatement();
            rs_c = stat_c.executeQuery("select * from MasterPersonnel;"); 
            i = 1;
            while (rs_c.next()) { 
               // theLogger.info("Record: " + i + " Unique name: " + rs_c.getString("unique_name") + " " + "JCNA: " + rs_c.getInt("jcna"));
                masterpersonnel_id = rs_c.getLong("masterpersonnel_id");
                jcna = rs_c.getInt("jcna");
                club = rs_c.getString("club");
                lastname = rs_c.getString("lastname");
                firstname= rs_c.getString("firstname");
                mi = rs_c.getString("mi");
                unique_name = rs_c.getString("unique_name");
                judgestatus = rs_c.getString("judgestatus");
                cert_year = rs_c.getInt("cert_year");
                address_street = rs_c.getString("address_street");
                city = rs_c.getString("city");
                state = rs_c.getString("state");
                country = rs_c.getString("country");
                postalcode = rs_c.getString("postalcode");
                phone_work = rs_c.getString("phone_work");
                phone_home = rs_c.getString("phone_home");
                phone_cell = rs_c.getString("phone_cell");
                email = rs_c.getString("email");  
                    stat_mj = aConn.createStatement();
                    ResultSet rs_mj;
                    memberJaguarStable = new ArrayList<>();
                    rs_mj = stat_mj.executeQuery("select * from MasterJaguar where masterpersonnel_id == " + masterpersonnel_id + ";"); 
                    while (rs_mj.next()) {
                        lngMasterJaguarID = rs_mj.getLong("masterjaguar_id");
                        strJCNAClassC= rs_mj.getString("jcnaclass_c");
                        strJCNAClassD= rs_mj.getString("jcnaclass_d");
                        strJOCLACategory= rs_mj.getString("joclacategory");
                        intYear= rs_mj.getInt("year");
                        strDescription= rs_mj.getString("description");
                        //strModel = strDescription; //  MUST BE FIXED AFTER EDITING masterjaguar table   3/17/2018
                        strModel= rs_mj.getString("model");
                        strUniqueDescription= rs_mj.getString("unique_desc");
                        strColor= rs_mj.getString("color");
                        strPlateVIN= rs_mj.getString("platevin");
                        /*
                         Long aMasterjaguar_id, String aJcnaclass_c, String aJcnaclass_d, String aJoclacategory, Integer aYear, String aDescription, 
                                String aUnique_desc, String aColor, String aPlatevin                        
                        */
                        miJaguar = new MasterJaguar(lngMasterJaguarID, masterpersonnel_id, strJCNAClassC, strJCNAClassD, strJOCLACategory, intYear, strModel,
                        strDescription, strUniqueDescription, strColor, strPlateVIN);
                        memberJaguarStable.add(miJaguar);
                    }
                    //aConn.commit();
                    //aConn.setAutoCommit(true);
                    rs_mj.close();
                    stat_mj.close();
                
/*
Long aMasterpersonnel_id, Integer aJcna, String aClub, String aLastname, String aFirstname, 
                 String aUnique_name, String aJudgestatus, Integer aCertyear,
                 String aAddress_street,  String aCity, String aState, 
                 String aCountry, String aPostalcode, String aPhone_work, String aPhone_home,
                 String aPhone_cell,  String aEmail, MasterJaguar[] aJagArray                    
                    */                
 
                    /*
                    toArray(new MasterJaguar[memberJaguarStable.size()]);
                    */
                    
                MasterPersonExt thePerson = new MasterPersonExt(masterpersonnel_id, jcna, club,
                                lastname, firstname, mi, unique_name, judgestatus,cert_year, 
                                address_street, city, state, country, postalcode,
                                phone_work, phone_home, phone_cell, email, memberJaguarStable.toArray(new MasterJaguar[memberJaguarStable.size()]));
                        theMasterPersonnel.AddMasterPerson(thePerson);
                i++;
            } 
            rs_c.close();
            stat_c.close();
    
        } catch (SQLException ex) {
            aLogger.getLogger(JCNAClasses.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(GetMasterPersonnelListSize() <= 0){
            String msg = "Empty Master Personnel list after LoadMasterPersonnelDB(). \nNormal if starting with EmptyBase.db.\nOtherwise, probably because the database file is for an earlier ConcoursBuilder version.\n Contact CB Support.";
            //okDialog(msg);
            aLogger.info(msg);
        } 
        
        aLogger.info("Finished LoadMasterPersonnelDB");
        

}

public  void LoadConcoursPersonnelDB(Connection aConn, Logger aLogger){
        aLogger.info("Started LoadConcoursPersonnelDB");


    Long concourspersonnel_id;
    Long masterpersonnel_id;
    int status_o;
    int status_j;
    String unique_name;
    int concourspersonnel_node;
    
    
    
    Statement stat_c = null;
    ResultSet rs_c = null;
    Statement stat_mj;
    MasterJaguar miJaguar;
    ArrayList<MasterJaguar> memberJaguarStable ;// = new ArrayList<>();
        try {
            aConn.setAutoCommit(false);
        } catch (SQLException ex) {
            aLogger.getLogger(Concours.class.getName()).log(Level.SEVERE, null, ex);
        }
    int i ;
        try {
            stat_c = aConn.createStatement();
        } catch (SQLException ex) {
            aLogger.getLogger(Concours.class.getName()).log(Level.SEVERE, null, ex);
        }
        try { 
            rs_c = stat_c.executeQuery("select * from ConcoursPersonnel;");
        } catch (SQLException ex) {
            aLogger.getLogger(Concours.class.getName()).log(Level.SEVERE, null, ex);
        }
            i = 1;
        try { 
            while (rs_c.next()) {
                concourspersonnel_id = rs_c.getLong("concourspersonnel_id");
                masterpersonnel_id = rs_c.getLong("masterpersonnel_id");
                unique_name = rs_c.getString("unique_name");
                status_o = rs_c.getInt("status_o");
                status_j = rs_c.getInt("status_j");
                concourspersonnel_node = rs_c.getInt("concourspersonnel_node");
                ConcoursPerson thePerson = new ConcoursPerson( masterpersonnel_id, unique_name, status_o, status_j, concourspersonnel_node);
                theConcoursPersonnel.AddPerson(thePerson);
                i++;
            }
            aConn.commit();
            aConn.setAutoCommit(true);
            rs_c.close();
            stat_c.close();
            
        } catch (SQLException ex) {
            String msg = "SQL Exception in LoadConcoursPersonnelDB()";
            okDialog(msg);
            aLogger.getLogger(Concours.class.getName()).log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
    
        aLogger.info("Finished LoadConcoursPersonnelDB");
}    

    
    
    
public ArrayList<Owner> GetConcoursOwners(){
    return theOwners.GetConcoursOwners();
}

public Owners GetConcoursOwnersObject(){
    return theOwners.GetConcoursOwnersObject();
}

public int GetMasterPersonnelListSize(){
    return theMasterPersonnel.GetMasterPersonnelList().size();
    
}
public ArrayList<MasterPersonExt> GetConcoursMasterPersonnel(){
    return theMasterPersonnel.GetMasterPersonnelList();
}

public MasterPersonnel GetMasterPersonnelObject(){
    return theMasterPersonnel;
}

public MasterJaguars GetMasterJaguarsObject(){
    return theMasterJaguars;
}

public MasterJaguar GetMasterJaguar(String aUniqueDescription){
    MasterJaguar result = null;
    for(MasterJaguar mj : theMasterJaguars.GetMasterJaguarList()){
        if(mj.getUniqueDesc().equals(aUniqueDescription)) {
            result = mj;
            break;
        }
    }
    return result;
} 

public ConcoursPersonnel GetConcoursPersonnelObject(){
    return theConcoursPersonnel;
}

public ConcoursClasses GetConcoursClassesObject(){
    return theConcoursClasses;
}

public ArrayList<MasterJaguar> GetConcoursMasterJaguar(){
    return theMasterJaguars.GetMasterJaguarList();
}

public MasterJaguars GetConcoursMasterJaguarsObject(){
    return theMasterJaguars;
}

public Entry getMasterJaguarFromEntryList(String aMJUniquename){
    Entry theEntry = null;
    for(Entry e : theEntries.GetConcoursEntries()){
        String ud = e.GetUniqueDescription();
        if(ud.equals(aMJUniquename)){
            theEntry = e;
            break;
        }
    }
    return theEntry;
}

    public void LoadJudgesCSV(String strPath, Logger aLogger){
         // Read Judges CSV file and store in Judges ArrayList
    //theLogger.info("JudgesCSV to be loaded: " + JudgesCSV);
    String [] nextLine;
    String strName;
    String strJudgeFirst;
    String strJudgeUnique;
    String strJCNA;
    Integer intJCNA;
    String strYear;
    Integer intYear;
    String strClub;
    String strID;
    String strSelfEntry1;
    String strSelfEntry2;
    String strSelfEntry3;
    String strRejectClass_n;
    Integer intStatus_o;
    Integer intStatus_j;
    
    Integer intNode;
    String strStatus;
    
    Integer iColSECount = 9;
    Integer intSelfEntryCount ;
    Integer iColRejectCount = 13;
    Integer intRejectCount;
    
    CSVReader reader;
    int intLine = 0;
    aLogger.info("Started LoadJudgesCSV");

    String JudgesCSV = strPath + "\\JudgesCsv.txt" ;
    try {
            reader = new CSVReader(new FileReader(JudgesCSV ));
        while ((nextLine = reader.readNext()) != null) {
            // First line is a Header and the line after the last Judge is empty
            if (intLine > 0 && !nextLine[0].isEmpty()) { 
                // nextLine[] is an array of values from the line
                // theLogger.info(nextLine[0] + nextLine[1] );
                strName = nextLine[0].trim();
                strJudgeFirst = nextLine[1].trim();
                strJudgeUnique =  nextLine[2].trim();
                strJCNA= nextLine[3].trim();
                intJCNA = Integer.getInteger(strJCNA);
                strYear= nextLine[4].trim();
                intYear = Integer.getInteger(strYear);
                strClub= nextLine[5].trim();
                strID= nextLine[6].trim();
                if (!nextLine[7].isEmpty())
                    intNode= Integer.parseInt(nextLine[7].trim());
                else{
                    theLogger.info( "Error in  " + JudgesCSV) ;
                    intNode = 99; // never used since exiting, but compiler insists that intNode be initialized
                    System.exit(1);
                }
                strStatus= nextLine[8].trim(); 
                
                intSelfEntryCount = Integer.parseInt(nextLine[iColSECount].trim());
               // strSelfEntry1= nextLine[10]; 
               // strSelfEntry2= nextLine[11];
               // strSelfEntry3= nextLine[12];
                //ArrayList<String> selfEntryClassList = new ArrayList<>();
                
               /* for(int iColSE = 1+iColSECount; iColSE <= (intSelfEntryCount + iColSECount); iColSE++){
                   selfEntryClassList.add(nextLine[iColSE].trim());
                }
                */
                
                intRejectCount = Integer.parseInt(nextLine[iColRejectCount].trim()); 
                ArrayList<String> rejectClassList = new ArrayList<>();
               for(int iColRC = 1+iColRejectCount; iColRC <= (intRejectCount + iColRejectCount); iColRC++){
                   rejectClassList.add(nextLine[iColRC].trim());
               }
                   //public Judge(String aName, String aJudgeFirst, String aJudgeUniqueName,  String aJCNA, String aYear, String aClub, String aID, ArrayList<String> aSelfEntryClasses, ArrayList<String> aRejectClasses, String aStatus, Integer aNode, long aCpid, int aLoad){        //theLogger.info("Constructing judge: ");
                Judge aNewJudge = new Judge(strName, strJudgeFirst, strJudgeUnique,  intJCNA, intYear, strClub, strID,  rejectClassList, strStatus, intNode, 0);
                
                for(int iColSE = 1+iColSECount; iColSE <= (intSelfEntryCount + iColSECount); iColSE++){
                   //selfEntryClassList.add(nextLine[iColSE].trim());
                    aNewJudge.AddSelfEntry(nextLine[iColSE].trim());
                }
                theJudges.GetConcoursJudges().add(aNewJudge);
            }
            intLine ++;
        }
    }    catch (FileNotFoundException ex) {
            okDialog("ERROR: FileNotFoundException in LoadJudgesCSV");
            aLogger.getLogger(Judges.class.getName()).log(Level.SEVERE, "ERROR: FileNotFoundException in LoadJudgesCSV", ex);
            System.exit(-1);
    }    catch (IOException ex) {
            okDialog("ERROR: IOException in LoadJudgesCSV");
           aLogger.getLogger(Judges.class.getName()).log(Level.SEVERE, "ERROR: IOException in LoadJudgesCSV", ex);
        }
    aLogger.info("Finished LoadJudgesCSV");
}

    public void LoadJudgesDB(Connection aConn, Logger aLogger){
            aLogger.info("Started LoadJudgesDB");

        try {
            String strJudgeLast = null;
            String strJudgeFirst = null;
            String strJudgeUnique = null;
            String strJCNA = null;
            Integer intJCNA = null;
            
           // Integer intStatus_o;
           // Integer intStatus_j;
            String strYear = null;
            Integer intYear = null;
            
            String strClub = null;
            String strID;
            
            Integer intJudgeNode = null;
            String strStatus = null;
           // long cpid; // concours personnel id
            
            
            Judge aNewJudge;
            
            ArrayList<String> rejectClassList;
            // ArrayList<String> selfEntryClassList;
            
           // Long lngJudge_id;
            Long lngConcoursPersonnel_id;
            Long lngMasterPersonnel_id;
            
            Statement stat_cj;
            ResultSet rs_cj;
            Statement stat_cjr;
            ResultSet rs_cjr;
            Statement stat_cjs;
            ResultSet rs_cjs;
           // Statement stat_cp;
           // ResultSet rs_cp;
            //int i;
            
            aConn.setAutoCommit(true);
            stat_cj = aConn.createStatement();
            rs_cj = stat_cj.executeQuery("select * from ConcoursJudgestable;");
            //i = 1;
            while (rs_cj.next()) {
                //lngJudge_id = rs_cj.getLong("judge_id");
                lngConcoursPersonnel_id = rs_cj.getLong("concourspersonnel_id");
                
                // get stuff from ConcoursPersonnel
                Statement stat_cp = aConn.createStatement();
                ResultSet rs_cp = stat_cp.executeQuery("select * from ConcoursPersonnel where concourspersonnel_id == " + lngConcoursPersonnel_id + ";");
                // should be only one but...
                lngMasterPersonnel_id = null; // won't be used
                while(rs_cp.next()){
                    lngMasterPersonnel_id = rs_cp.getLong("masterpersonnel_id");
                    intJudgeNode  = rs_cp.getInt("concourspersonnel_node");
                }
                stat_cp.close();
                rs_cp.close();
                //strID = "J" + intJudgeNode;
                strID = theJudges.GetNextJudgeID();
                
                // get stuff from MasterPersonnel
                Statement stat_mp = aConn.createStatement();
                ResultSet rs_mp = stat_mp.executeQuery("select * from MasterPersonnel where masterpersonnel_id == " + lngMasterPersonnel_id + ";");
                // should be only one but...
                while(rs_mp.next()){
                    strJudgeFirst = rs_mp.getString("firstname");
                    strJudgeLast = rs_mp.getString("lastname");
                    strJudgeUnique= rs_mp.getString("unique_name");
                    strJCNA= rs_mp.getString("jcna");
                    intJCNA = Integer.getInteger(strJCNA);
                    strStatus = rs_mp.getString("judgestatus"); // eventually this will be used to distinguish  Certified, Apprintice, and Chief judges
                    strYear = rs_mp.getString("cert_year");
                    intYear = Integer.getInteger(strYear);
                    strClub = rs_mp.getString("club");
                }
                stat_mp.close();
                rs_mp.close();
                //aConn.commit();
                // get self entry and reject lists for the Judge
                //selfEntryClassList = new ArrayList<>();
                /*
                while(rs_cjs.next()){
                selfEntryClassList.add(rs_cjs.getString("class"));
                }
                */
                stat_cjr = aConn.createStatement();
                rs_cjr = stat_cjr.executeQuery("select * from ConcoursJudgeClassRejectTable where concourspersonnel_id ==" + lngConcoursPersonnel_id + ";");
                rejectClassList = new ArrayList<>();
                while(rs_cjr.next()){
                    rejectClassList.add(rs_cjr.getString("class"));
                }
                //aConn.commit();
                rs_cjr.close();
                stat_cjr.close();
                aNewJudge = new Judge(strJudgeLast, strJudgeFirst, strJudgeUnique,  intJCNA, intYear, strClub, strID,  rejectClassList, strStatus, intJudgeNode,  0);
                
                stat_cjs = aConn.createStatement();
                rs_cjs = stat_cjs.executeQuery("select * from ConcoursJudgeClassSelfEntryTable where concourspersonnel_id ==" + lngConcoursPersonnel_id + ";");
                while(rs_cjs.next()){
                    // selfEntryClassList.add(rs_cjs.getString("class"));
                    aNewJudge.AddSelfEntry(rs_cjs.getString("class"));
                }
                //aConn.commit();
                
                rs_cjs.close();
                stat_cjs.close();
                
                theJudges.GetConcoursJudges().add(aNewJudge);
                //i++;
            }
            //aConn.setAutoCommit(true);
            
            rs_cj.close();
            stat_cj.close();
        } catch (SQLException ex) {
            aLogger.getLogger(Concours.class.getName()).log(Level.SEVERE, null, ex);
        }
    
            aLogger.info("Finished LoadJudgesDB");
                
}


    
    
    /**
 * 
 *    NOTE: the JudgeAssignments must be loaded before LoadConcoursClassesCSV can be called since JudgeAssignments is needed to initialize the judge lists for each class
 * 
 *    NOTE:  This is no longer used. Moreover, the CSV files Excel had no Preassigned judges so it would be a waste of time to update in
*/
    public void LoadConcoursClassesCSV(String strPath, Logger aLogger){
        
        aLogger.info("Started LoadConcoursClassesCSV");
         // Read Judges CSV file and store in Judges ArrayList
    String ConcoursClassesCSV = strPath  + "\\ClassesCsv.txt";
    //theLogger.info("ConcoursClassesCSV to be loaded: " + ConcoursClassesCSV);
    String [] nextLine;
    String ClassName;
    Integer Node;
    int EntryCount;

    CSVReader reader;
    int intline = 0;
    ArrayList<Integer> EntryIndexList;    
    ArrayList<Integer> JudgeList;
    ArrayList<Entry> EntryObjectList;
    Entry entryObject;
    try {
        reader = new CSVReader(new FileReader(ConcoursClassesCSV ));
        while ((nextLine = reader.readNext()) != null) {
            if(intline > 0){
            // nextLine[] is an array of values from the line
                EntryIndexList = new ArrayList<>();
                EntryObjectList = new ArrayList<>();
                JudgeList = null;
                ClassName = nextLine[0];
                Node  = Integer.parseInt(nextLine[1]) ;
                //theLogger.info("Node as string [" + nextLine[1] + "]");
                EntryCount  = Integer.parseInt(nextLine[2]) ;
                //theLogger.info("Loading Entry list for Class " + ClassName + " Node " + Node + " EntryCount = " + EntryCount); 
                Integer entryIndex ;
                //Integer  clsIndex;
                for( int i = 3; i < (EntryCount+3); i++){
                   entryIndex = Integer.parseInt(nextLine[i]);
                   entryObject = theEntries.getEntry(entryIndex);
                   EntryIndexList.add(entryIndex);
                   EntryObjectList.add(entryObject);
                   JudgeList = GetJudgeListForEntryIndex(entryIndex); // The JudgeAssign file is the only place the judge list for an entry exists at this point.
                   
                }
                //     no longer valid call since Constructor now has a preassigned judge list argument
                //theConcoursClasses.GetConcoursClasses().add(new ConcoursClass(this, ClassName, Node, EntryCount, EntryIndexList, EntryObjectList, JudgeList));                
            }
            intline++;
        }
       
    }    catch (FileNotFoundException ex) {
            okDialog("ERROR: FileNotFoundException in LoadConcoursClassesCSV");
            aLogger.getLogger(Judges.class.getName()).log(Level.SEVERE, "ERROR: FileNotFoundException in LoadConcoursClassesCSV", ex);
    }    catch (IOException ex) {
        okDialog("ERROR: IOException in LoadConcoursClassesCSV");
            aLogger.info("ERROR: IOException in LoadConcoursClassesCSV");
            System.exit(-1);
        }
        aLogger.info("Finished LoadConcoursClassesCSV");
    
                
}

    public boolean LoadConcoursClassesDB(Connection aConn, Concours aConcours, Logger aLogger){
        aLogger.info("Started LoadConcoursClassesDB");
        // Read ConcoursClasses from database and store in ConcoursClasses ArrayList
        //
        // Note that the ConcoursClass Judges lists are not stored separately in the database and therefore not loaded when the ConcoursClasses are loaded
        // Instead ConcoursClass Judges lists are loaded when JudgeAssignments are loaded.
        //
    boolean result = true;
    String strClassName;
    int intClassNode;
    int intEntryCount;

    ArrayList<Integer> EntryIndexList;    
    ArrayList<Integer> JudgeList;
    ArrayList<Entry> EntryObjectList;
    Entry entryObject;
    // The purpose of these instantiations is so PreassignedJudgeNodeList and PreassignedJudgeUniqueNameList aren't null for classes that have no preassigned Judges
    ArrayList<Integer> PreassignedJudgeNodeList = new ArrayList<>();
    ArrayList<String> PreassignedJudgeUniqueNameList = new ArrayList<>();

    
    
    
   // int intline = 0;

    Statement stat_cc;
    ResultSet rs_cc;
    Statement stat_ce;
    ResultSet rs_ce;
    Statement stat_pj;
    ResultSet rs_pj;
    Statement stat_table;
    int i;
    Long lngConcoursClass_id;
    //Long lngConcoursPersonnel_id;

    try {   
            stat_cc = aConn.createStatement();
            rs_cc = stat_cc.executeQuery("select * from ConcoursClassesTable;"); 
            stat_pj = aConn.createStatement();
            
            stat_table = aConn.createStatement();
            stat_table.executeUpdate("create table if not exists ConcoursClassPreassignedJudgesTable ('preassignedjudge_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursclasses_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL, judge TEXT, FOREIGN KEY (concoursclasses_id) REFERENCES ConcoursClassesTable (concoursclasses_id));");
            rs_pj = stat_pj.executeQuery("select * from ConcoursClassPreassignedJudgesTable;"); 
            i = 1;
            while (rs_cc.next()) { 
                lngConcoursClass_id = rs_cc.getLong("concoursclasses_id");
                strClassName = rs_cc.getString("class_name");
                intClassNode = rs_cc.getInt("class_node");
                
                EntryIndexList = new ArrayList<>();
                EntryObjectList = new ArrayList<>();
                
                stat_ce = aConn.createStatement();
                rs_ce = stat_ce.executeQuery("select * from ConcoursClassesEntries where concoursclasses_id == " + lngConcoursClass_id + ";");
                JudgeList = null;
                while (rs_ce.next()){
                    Integer theEntryNode = rs_ce.getInt("entry_node");
                    EntryIndexList.add(theEntryNode);
                    entryObject = theEntries.getEntry(theEntryNode);
                    EntryObjectList.add(entryObject);
                    // Notes regarding JudgeList: First, the JudgeAssign file is the only place the judge list for an entry exists at this point.
                    //    So, GetJudgeListForEntryIndex() searches Judgelist for the entry node in JudgeAssignments. There might more than one entry
                    //    in a class, so we put the judge in JudgeList only once.
                    if(JudgeList == null) JudgeList = GetJudgeListForEntryIndex(theEntryNode);
                    // If JudgeList still null there's something wrong... an Entry should have 2 or 3 Judges
                    if(JudgeList == null){
                        String msg = "ERROR: In LoadConcoursClassesDB. Class " + strClassName + " Entry Node " + theEntryNode + " has no Judges.";
                        okDialog(msg);
                        aLogger.info(msg);
                        result = false;
                    }
                }
                intEntryCount = EntryIndexList.size();
                rs_ce.close();
                stat_ce.close();
                //
                // Now use ConcoursClassPreassignedJudgesTable to set up Preassigned Judge list for strClassName
                //
                PreassignedJudgeNodeList = new ArrayList<>();
                PreassignedJudgeUniqueNameList = new ArrayList<>();
                rs_pj = stat_pj.executeQuery("select * from ConcoursClassPreassignedJudgesTable where concoursclasses_id == " + lngConcoursClass_id + ";");
                while (rs_pj.next()){
                    Integer theJudgeNode = rs_pj.getInt("judge_node");
                    String theJudgeUniqueName = rs_pj.getString("judge");
                    PreassignedJudgeNodeList.add(theJudgeNode) ;
                    PreassignedJudgeUniqueNameList.add(theJudgeUniqueName) ;
                }
                intEntryCount = EntryIndexList.size();
                rs_ce.close();
                stat_ce.close();
                rs_pj.close();
                stat_pj.close();
                if(JudgeList == null){
                    String msg = "ERROR: In LoadConcoursClassesDB. Class " + strClassName +  " has no Judges.";
                    okDialog(msg);
                    aLogger.info(msg);
                     result =  false;
                     break;
                }
                
                theConcoursClasses.GetConcoursClasses().add(new ConcoursClass(aConcours, strClassName, intClassNode, intEntryCount, EntryIndexList, EntryObjectList, JudgeList, PreassignedJudgeNodeList, PreassignedJudgeUniqueNameList));                
                i++;
            }
            rs_cc.close();
            stat_cc.close();
            stat_table.close();
    
        } catch (SQLException ex) {
            aLogger.getLogger(JCNAClasses.class.getName()).log(Level.SEVERE, null, ex);
        }
        aLogger.info("Finished LoadConcoursClassesDB");
        return result;
}
    
   
    
public ArrayList<Judge> GetConcoursJudges(){
    return theJudges.GetConcoursJudges();
}




public void AddConcoursJudge(Judge aJudge){
    theJudges.AddJudge(aJudge);
}

public ArrayList<ConcoursClass> GetConcoursClasses(){
    return theConcoursClasses.GetConcoursClasses();
}
        
    
public Logger GetLogger(){
    return theLogger;
} 

public int GetNumSavedLogs(){
    return numSavedLogs;
}

public String GetInstallationPath(){
    return theInstallationPath;
}
public void SetInstallationPath(String aInstallationPath){
    theInstallationPath = aInstallationPath;
}

public String GetConcoursBuilderDataPath(){
    return concoursBuilderDataPath;
}
public void SetConcoursBuilderDataPath(String aDataPath){
    concoursBuilderDataPath = aDataPath;
}

public String GetConcoursBuilderDocsPath(){
    return concoursBuilderDocsPath;
}
public void SetConcoursBuilderDocsPath(String aDocPath){
    concoursBuilderDocsPath = aDocPath;
}

public String GetCBVersion(){
    return theVersion;
}
public void SetCBVersion(String aVersion){
    theVersion = aVersion;
}
public void SetNumSavedLogs(int aNumSavedLogs){
    numSavedLogs = aNumSavedLogs;
}


public List<JudgeAssignment> GetJudgeAssignments(){
       return theJudgeAssignments.GetConcoursJudgeAssignments();
               
}
public List<String> GetEntriesIDs(){
    return theEntries.GetConcoursEntryIDs();
}
public List<Entry> GetEntriesList(){
    return theEntries.concoursEntries;
}

public Entries GetEntries(){
    return theEntries;
}
public int GetNumDisplayOnlyEntries(){
    int count = 0;
    for(Entry e : theEntries.concoursEntries){
        if(e.GetClassName().equals("DISP")) count++;
    }
    return count;
}
public Map<String, String> getMasterColorMap(){
    return masterJagColorToScheduleColorMap;
}
public ArrayList<TimeslotAssignment> GetTimeslotAssignments(){
     return theTimeslotAssignments.GetConcoursTimeslotAssignments() ;
}

/*public void setTimeslotIndexToTimeStringMap(Map<Integer, String> aMap){
    timeslotIndexToTimeStringMap = aMap;
}
public Map<Integer, String> getTimeslotIndexToTimeStringMap (){
    return timeslotIndexToTimeStringMap;
}


public void setTimeslotTimeToIndexMap(Map<String, Integer> aMap){
    timeStringToTimeslotIndexMap = aMap;
}

public Map<String,Integer> getTimeslotTimeToIndexMap (){
    return timeStringToTimeslotIndexMap;
}
*/
public TimeslotAssignment GetTimeslotObject(int tsID){
    TimeslotAssignment res = null;
    for(TimeslotAssignment ta :  theTimeslotAssignments.GetConcoursTimeslotAssignments()){
        if(ta.getID() == tsID){
            res = ta;
            break;
        }
    }
    if(res == null){
        String msg = "ERROR: In GetTimeslotObject(tsID) called from ChangeClassJudgeInputDialog. ID " + tsID + " not found in theTimeslotAssignments." ;
        okDialog(msg);
        theLogger.info(msg);
        System.exit(-1);
    }
    
    return res;
}


public int GetMaxFirstNameExtension(){
    return MAX_FIRST_NAME_EXTENSION;
}
public boolean GetJudgeAssignmentCurrent(){
    return boolJudgeAssignmentCurrent;
}


public void SetJudgeAssignmentCurrent(boolean aJudgeAssignmentCurrent){
    boolJudgeAssignmentCurrent = aJudgeAssignmentCurrent;
}

public boolean GetPreassignedJudgesFlag(){
    return boolPreassignedJudges;
}
public void SetPreassignedJudgesFlag(boolean flag){
     boolPreassignedJudges = flag;
}

public String GetConcoursName(){
    return strConcoursName;
}

public void SetConcoursName(String aConcoursName){
    strConcoursName = aConcoursName;
}
/*
String strStartTime;
    String strLunchTime;
    Integer timeslotInterval;
    Integer slotsBeforeLunch;
*/
public String GetConcoursStartTime(){
    return strStartTime;
}

public void SetConcoursStartTime(String aStartTime){
    strStartTime = aStartTime;
}

public Integer GetConcoursTimeslotInterval(){
    return timeslotInterval;
}

public void SetConcoursTimeslotInterval(Integer aInterval){
    timeslotInterval = aInterval;
}

public Integer GetConcoursTimeslotsBeforeLunch(){
    return slotsBeforeLunch;
}

public void SetConcoursTimeslotsBeforeLunch(Integer aInterval){
    slotsBeforeLunch = aInterval;
}

public Integer GetConcoursLunchInterval(){
    return lunchInterval;
}

public void SetConcoursLunchInterval(Integer aInterval){
    lunchInterval = aInterval;
}
public String GetConcoursLunchTime(){
    return strLunchTime;
}

public void SetConcoursLunchTime(String aLunchTime){
    strLunchTime = aLunchTime;
}

public Integer GetConcoursTitleFontSize(){
    return titleFontSize;
}

public void SetConcoursTitleFontSize(int aFontSize){
    titleFontSize = aFontSize;
}

public Integer GetConcoursSubtitleFontSize(){
    return subtitleFontSize;
}

public void SetConcoursSubtitleFontSize(int aFontSize){
    subtitleFontSize = aFontSize;
}

public Integer GetConcoursHeaderFontSize(){
    return headerFontSize;
}

public void SetConcoursHeaderFontSize(int aFontSize){
    headerFontSize = aFontSize;
}

public Integer GetConcoursCellFontSize(){
    return cellFontSize;
}

public void SetConcoursCellFontSize(int aFontSize){
    cellFontSize = aFontSize;
}

public Integer GetConcoursFootnoteFontSize(){
    return footnoteFontSize;
}

//public int GetMaxModelYear(){
//    return MAX_MODEL_YEAR;
//}

public void SetConcoursFootnoteFontSize(int aFontSize){
    footnoteFontSize = aFontSize;
}


public String GetConcoursUserName(){
    return userName;
}

public void SetConcoursUserName(String aName){
    userName = aName;
}

public String GetConcoursChair(){
    return concoursChair;
}

public void SetConcoursChair(String aName){
    concoursChair = aName;
}

public String GetConcoursChiefJudge(){
    return concoursChiefJudge;
}

public void SetConcoursChiefJudge(String aName){
    concoursChiefJudge = aName;
}

public void SetConcoursCompression(Integer aCompression){
    concoursCompression = aCompression;
}

public Integer GetConcoursCompression(){
    return concoursCompression;
}

public String GetThePath(){
    return thePath;
}

public void SetThePath(String strPath){
    thePath = strPath;
}

public void SetConnection(Connection aConn){
    conn = aConn;
}
public Connection GetConnection(){
    return conn;
}

public void DisplayCars(){
    JudgeAssignment aJA;    
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
         aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theLogger.info ("theCar = " + aJA.GetCarIndex());
        
    }
}
public Set<Integer>  CheckForDupCars(){
    final Set<Integer> set1 = new HashSet();
     Set<Integer> setDuplicates; 
     setDuplicates = new HashSet();
   
    int theCar;
    JudgeAssignment aJA;    
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theCar =aJA.GetCarIndex();
        if (!set1.add(theCar)){
             setDuplicates.add(theCar);
        }

        
    }
    return setDuplicates;
}
   // Finds any Entries in Judge Assignments for which a the  Owner is also in a Judge
   // If found, an attempt is made to fix by finding a Judge who is available it ALL timeslots
   // in which the Class entrants are being judged.
  //
    //
    // Right now this just reports conflicts... Fix needs to be implemented
     //

public String  CheckForSelfJudges(){
    // List<String> lstProbelmEntries = new  ArrayList<>(); 
    String result;
    int theCar;
    String strCar;
    int theOwner;
    String strOwner;
    int  intJudge;
    String strJudge;
    boolean aSelfJudge;
    ArrayList<Integer> lstJudges ;
    JudgeAssignment aJA;    
    aSelfJudge = false;
    result = "";
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theCar = aJA.GetCarIndex();
        strCar = GetEntries().getEntryID(theCar);
        theOwner =aJA.GetOwnerIndex();
        strOwner = GetConcoursOwnersObject().getOwnerName(theOwner);
        lstJudges = aJA.GetJudgeIndicies();
        int conflictCount = 0;
        for(int j = 0;j<lstJudges.size(); j++ ){
           intJudge = lstJudges.get(j);
           /*if(intJudge == theOwner){
                aSelfJudge = true;
            }
           */
           if(intJudge == theOwner){
               conflictCount++;
               if(conflictCount > 0) result = result + "\n\t";
                   result = result + "Entry: " + strCar + " Owner: " + strOwner;
           }
        }
        /*
        if(aSelfJudge) {
            //lstProbelmEntries.add(strCar);
            result = result + "Entry: " + theCar + "Owner: " + strOwner;
            aSelfJudge = false;
        }
        */

    }
    return result;
}
   // Finds any Entries in Judge Assignments for which a Judge appears mor ethan once.
   // If found, an attempt is made to fix by finding a Judge who is available it ALL timeslots
   // in which the Class entrants are being judged.
   //
    //
    // Right now this just reports conflicts... Fix needs to be implemented
     //


public String  CheckForRepeatJudges(){
    List<Integer> lstProbelmEntries = new  ArrayList<>(); 
   
    int theCar;
    String strCar;
    String strClass;
    String strJudge;
    String result = "";
    int  intJudge_j;
    int  intJudge_k;
    boolean aRepeatJudge;
    List<Integer> lstJudges ;
    List<String> lstClasses = new  ArrayList<>(); // list of Class names encountered so far
    JudgeAssignment aJA;    
    aRepeatJudge = false;
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theCar =aJA.GetCarIndex();
        strCar = GetEntries().getEntryID(theCar);
        strClass = GetEntries().getEntryClass(theCar);
        strJudge = "";
        //int conflicts = 0;
        if(lstClasses.contains(strClass)){
            continue;
        } else{
            lstClasses.add(strClass);
            lstJudges = aJA.GetJudgeIndicies();
            JudgeOuterLoop: for(int j = 0;j<lstJudges.size(); j++ ){
               intJudge_j = lstJudges.get(j);
               // Could this be done with lstJudges.contains()???
               for(int k=j+1; k < lstJudges.size(); k++){
                    intJudge_k = lstJudges.get(k);
                    if(intJudge_k == intJudge_j){
                        aRepeatJudge = true;
                        Judge theJudge = this.GetConcoursJudge(intJudge_j);
                         if(theJudge == null){
                             JOptionPane.showMessageDialog(null, "No Judge with node " + intJudge_j + " is  in Concourse Judges in CheckForRepeatJudges");
                             strJudge = "none";
                        } else{
                            strJudge = theJudge.getUniqueName();
                         }
                        break JudgeOuterLoop;
                    }
               }
            }
            if(aRepeatJudge) {
              //  conflicts++;
                result = result + "\n\tClass: " + strClass + " Judge: ";
                lstProbelmEntries.add(theCar);
                result = result + " " + strJudge;
                aRepeatJudge = false;
            }
            
        }
         //result = result + " ";
    }
    return result;
}

    //
    //      NOTE--- have to implement a fix strategy... if possible
    //

public List<Integer>  CheckForClassJudgeUniformity(){
     List<Integer> lstProbelmEntries = new  ArrayList<>(); 
   
    //int theClass;
    int intClassEntry_j ;
    int intClassEntry_k ;
    boolean aJudgeTeamUniform;
    ArrayList<Integer> lstClassEntries ;
    List<Integer> lstClassJudges_j;
    List<Integer> lstClassJudges_k;
    ArrayList<ConcoursClass> aCCList;   
    JudgeAssignment aJA;
    aCCList = theConcoursClasses.concoursClasses;
    String strClassName;
   
    Integer intClassNode;
    theLogger.info("Scanning for nonuniform judge list among entries in same Class");
    theLogger.info("Concourse classes size = " + aCCList.size());
    for(int i = 0; i< aCCList.size(); i++){
        strClassName = aCCList.get(i).GetClassName();
        intClassNode = aCCList.get(i).Node;
        //if(intClassNode == 26){
        //  theLogger.info("Class " + strClassName) ;     
        //}
        lstClassEntries = aCCList.get(i).GetClassEntryIndices();
        //theLogger.info("Number of entries in Class Name " + strClassName + " Node " + intClassNode + " = " + lstClassEntries.size());
        //theLogger.info("Class entries:" + lstClassEntries);
        aJudgeTeamUniform = true;
        for(int j = 0;(lstClassEntries.size()-1)>=j; j++ ){ //Outer loops can skip the last entry ????
           intClassEntry_j = lstClassEntries.get(j);
           lstClassJudges_j = theJudgeAssignments.getJudgeIndicies(intClassEntry_j);
           //theLogger.info("Entry " + intClassEntry_j + " Judge list: " + j + ": " +lstClassJudges_j);
           for(int k=j+1; k < lstClassEntries.size(); k++){
                intClassEntry_k = lstClassEntries.get(k);
                lstClassJudges_k = theJudgeAssignments.getJudgeIndicies(intClassEntry_k);
                //theLogger.info("Entry " + intClassEntry_k + " Judge list k " +lstClassJudges_k);
                // Lists of Judges must be of equal length and have the same elemenst, but in any order
                if(lstClassJudges_j.size() == lstClassJudges_k.size()){
                    if(lstClassJudges_j.containsAll(lstClassJudges_k)){
                        aJudgeTeamUniform = true;
                    }
                    else
                        aJudgeTeamUniform = false;
                }else
                    aJudgeTeamUniform = false;
           }
                   
        }

        if(!aJudgeTeamUniform) {
            theLogger.info("Non-uniform judgeing team for class: " + strClassName + "(" + intClassNode + ")" );
            lstProbelmEntries.add(intClassNode);
            int intClassEntry;
            List<Integer> lstClassJudges;
            for(int j = 0; j< lstClassEntries.size(); j++ ){
                intClassEntry = lstClassEntries.get(j);
                lstClassJudges = theJudgeAssignments.getJudgeIndicies(intClassEntry);
                theLogger.info("Entry " + intClassEntry + " Judge list k " +lstClassJudges);
            }
        }

    }
    return lstProbelmEntries;
}

public List<String> GetJudgeLoads(){
   List<String> LoadsList = new ArrayList<>();
   updateJudgeLoads();
   Judge aJudge;
   String res;
   int aJudgeLoad;
   String aJudgeName;
   Integer aJudgeNode;
   int intCount = theJudges.concoursJudges.size();
   for (int i=0; i < intCount;i++){
       aJudge = theJudges.concoursJudges.get(i);
       aJudgeName = aJudge.Name;
       aJudgeNode = aJudge.Node;
       aJudgeLoad = aJudge.Load;
      // theLogger.info("Name = " + aJudgeName + " Node= " + aJudgeNode + " Load=" + aJudgeLoad);
       //res = aJudgeName + " " + aJudgeNode + " " + aJudgeLoad;
       res = aJudgeName + " "  + aJudgeLoad;
       LoadsList.add(res);
   }

       
    return LoadsList;
}


        
public String CheckForTimeslotConflicts(Map<Integer, String> aTSIndexToStringMap ){
//public String CheckForTimeslotConflicts(TimeslotAssignment<Timeslot> aTimeslots){

    
    TimeslotAssignment objTheTimeslot;
    
   UpdateTimeslotStats();    //Must be done here so conflict lists are up to date
    //
    // Summarize the results & report
    //
    int intSizeConflicts;
    int ts;
    String results;
    List<Integer> lstConflicts; 
    List<Integer> lstConflictCars; 
    //TSConflicts tsconflicts = new TSConflicts();
    results = "\nTimeslot conflict analysis results: ";
    boolean hasConflicts = false;
    for(int j = 0; j<theTimeslotAssignments.GetConcoursTimeslotAssignments().size(); j++){
        objTheTimeslot = theTimeslotAssignments.GetConcoursTimeslotAssignments().get(j); // 
        intSizeConflicts = objTheTimeslot.lstConflicts.size();
        if( intSizeConflicts > 0){
            hasConflicts = true;
            ts = j ; // report base-0 timeslots
           // theLogger.info("Conflicts in timeslot " + ts + " :" + objTheTimeslot.lstConflicts);
            //setConflicts.add(ts);
            results = results + "\nConflicts in timeslot " + aTSIndexToStringMap.get(ts) + " :" + "\n";
            results = results + "\tPersonnel: ";
            lstConflicts = objTheTimeslot.lstConflicts;
            int k = 0;
            for(Integer personnelNode : lstConflicts){
                ConcoursPerson cp = theConcoursPersonnel.GetConcoursPerson(personnelNode);
                if (k > 0) results = results + ", ";
                results = results + cp.GetUniqueName();
                k++;
            }

            results = results + "\n\tCars: ";
            lstConflictCars = objTheTimeslot.lstConflictCars;
            k = 0;
            for(Integer entryNode : lstConflictCars){
                String entryID = theEntries.getEntryID(entryNode);
                if(k > 0) results = results + ", ";
                results = results + entryID;
                k++;
            }

        }
    }
    if(!hasConflicts) results = results + " no conflicts";
    return results;
}        

public String FixTimeslotConflicts( ){
//public String FixTimeslotConflicts( ){
    String results;
    results = "";
    //ArrayList<TimeslotAssignment> aTimeslotAssignments = JudgeAssignGUI.getTimeslotAssignments();
    //UpdateTimeslotStats(aTimeslotAssignments);    
    ArrayList<Integer> lstCompositConflictCars = new ArrayList<>();
    TimeslotAssignment objTheTimeslot;
    //
    //  First, build a composit list of conflicted cars
    //
   // theLogger.info("No. of timeslots used=" + theTimeslotAssignments.GetConcoursTimeslotAssignments().size() );
    int ts;
 
    for(int j = 0; j<theTimeslotAssignments.GetConcoursTimeslotAssignments().size(); j++){
        ts = j + 1;  // report base-1 timeslots
        objTheTimeslot = theTimeslotAssignments.GetConcoursTimeslotAssignments().get(j); // 
            //theLogger.info("timeslot: " + ts );
            //results = results + "\n\tCars:";
            //lstCompositConflictCars = objTheTimeslot.lstConflictCars;
            Iterator iter = objTheTimeslot.lstConflictCars.iterator();
            while (iter.hasNext()) {
                Integer intCar;
                String strCar;
                strCar = iter.next().toString();
                intCar = Integer.valueOf(strCar);  
                lstCompositConflictCars.add(intCar);
                //theLogger.info("\tthe conflicted Car : " + strCar);
                //results = results + " " + strCar;
            }

      }
      //theLogger.info("List of all conflicted cars in concours: " + lstCompositConflictCars);
    
    //
    // Now loop through each item in the composite list of conflicted cars. For each such car, search the list of timeslots
    //  to find a list of  timeslots to which they could be safely moved.
    //
    Iterator itrConflictedCars =lstCompositConflictCars.iterator();
    
    while (itrConflictedCars.hasNext()) {
        String strTargetCar;
        List<Integer> lstAvailableTimeslots = new ArrayList<>();
        List<Integer> theTeam = new ArrayList<>();
        strTargetCar = itrConflictedCars.next().toString();
        Integer intTargetCar = Integer.valueOf(strTargetCar); 
        theLogger.info("\nConflicted car index: " + strTargetCar);
        // Get the Team for intTargetCar
        theTeam = theJudgeAssignments.GetTeam(intTargetCar);
        theLogger.info("\nTeam for " + strTargetCar + " is: " + theTeam);
        Set<Integer> setTeam = new HashSet<>(theTeam);
        Set<Integer> intersection;
        Set<Integer> setTimeslotPersonnel;
        int maxTsID = 0; // maxTsTD +1 will be used for a new timeslot ID;
        for(int j = 0; j<theTimeslotAssignments.GetConcoursTimeslotAssignments().size(); j++){
           // ts = j + 1;  // report base-1 timeslots
            //ts = j;
            ts = theTimeslotAssignments.GetConcoursTimeslotAssignments().get(j).getValue();
            if(ts > maxTsID) maxTsID = ts;  // used below as
            intersection = new HashSet<>(setTeam);
            setTimeslotPersonnel = new HashSet<>(theTimeslotAssignments.GetConcoursTimeslotAssignments().get(j).lstTeamMembers);
               // For this to work ALL personnel need to be considered, including those conflicted
            setTimeslotPersonnel.addAll(theTimeslotAssignments.GetConcoursTimeslotAssignments().get(j).lstConflicts); 
            theLogger.info("\nPersonnel active in timeslot " + ts + " is: " + setTimeslotPersonnel);
            intersection.retainAll(setTimeslotPersonnel);
            theLogger.info("\nIntersection of active personnel and team for target car in timeslot : " + ts + " is: " + intersection);
            
            if(intersection.isEmpty()){
                theLogger.info("\nConflicted car index: " + strTargetCar + " can be moved to timeslot " + ts);
                lstAvailableTimeslots.add(j);
            }
            else{
                theLogger.info("\nConflicted car index: " + strTargetCar + " cannot be moved to timeslot " + ts);
            }
        }
        // Adding a Timeslot 
        if(lstAvailableTimeslots.isEmpty()){
            theLogger.info("\nConflicted car index: " + strTargetCar + " cannot be moved to any existing timeslot. Will add new timeslot." );
            // replace the timeslot index of the target car with the the current maximun timeslot ID + 1
            Integer intTSIndexNext =  maxTsID + 1;
            for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
                JudgeAssignment aJA;
                Integer theCar;
                String strCar;
                int intTheTimeslot;
                int tsOld;
                aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
                theCar =aJA.GetCarIndex();
                strCar = GetEntries().getEntryID(theCar);
                intTheTimeslot = aJA.GetTimeslot();
                tsOld = intTheTimeslot;
                if(theCar == intTargetCar){
                    aJA.SetTheTimeslot(intTSIndexNext);
                    theLogger.info("Changed timeslot for car  " + strCar + " from " + tsOld + " to " + intTSIndexNext);
                    results = results + "\nChanged timeslot for car  " + strCar + " from " + tsOld + " to " + intTSIndexNext;
                }
            }
            //
            // Update the timeslot stats 
            //
            UpdateTimeslotStats(); 
            Collections.sort(theTimeslotAssignments.GetConcoursTimeslotAssignments(), new TimeslotAssignment.ReverseOrderByEntriesSize());
        }

    }
        
        return results;
        
    }
public void switchTimeslots(List<JudgeAssignment> aJudgeAssignments, int aTSIndex1, int aTSIndex2 ){
    
    
    //  Note: There's a temptation to combine Steps 3 & 3 with Step 1 but changing something in aJudgeAssignments while it is being iterated is hazardous. 
    //
    // Step 1: Build list of JudgeAssignment objects for each of the subject Timeslots
    List<JudgeAssignment> jaObjectList_1  = new ArrayList<> ();
    List<JudgeAssignment> jaObjectList_2  = new ArrayList<> ();
    for(JudgeAssignment ja : aJudgeAssignments){
        int tsID = ja.GetTimeslot();
        if( tsID == aTSIndex1){
            jaObjectList_1.add(ja);
        } else if(tsID == aTSIndex2){
            jaObjectList_2.add(ja);
        }
    }
    
    //Step 2: change the timeslot index for each of the jaObjectList_1 to aTSIndex2
    for(JudgeAssignment ja : jaObjectList_1){
        ja.SetTheTimeslot(aTSIndex2);
    }
    
    //Step 3: change the timeslot index for each of the jaObjectList_2 to aTSIndex1
    for(JudgeAssignment ja : jaObjectList_2){
        ja.SetTheTimeslot(aTSIndex1);
    }
}
    public MasterPersonnel GetConcoursMasterPersonnelObject() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return theMasterPersonnel;
    }
//
// This class supports CompressTimeslots(). Each Entry in the concours has a Set of TimeSlotAssignmnets it has previously occupied.
//
private class EntryPreviousTimeslot{
    Set<Integer> previousTS;
    Integer entryNode;
    EntryPreviousTimeslot(){
        previousTS = new HashSet<>();
    }
    public Integer GetEntryNode(){
        return entryNode;
    }
    public void SetEntryNode(Integer aEntryNode){
        entryNode = aEntryNode;
    }
     
    public void SetAddTS(Integer aTS){
        previousTS.add(aTS);
    }
    
    
}    
//public List<String> CompressTimeslots( ArrayList<TimeslotAssignment>  TimeslotAssignments){
/*
public List<String> CompressTimeslots(ArrayList<TimeslotAssignment> timeslotAssignments, Map<Integer, String> aTSIndexToStringMap ){
    //
    // The basic strategy is to find a timeslot (TimeslotA) with the smallest number of cars being judged, then for each car 
    // and in TimeslotA attempt to find another timeslot (TimeslotB) to put it in. The requirement is that
    // all Team members for the car in TimeslotA ( Owner & Judges) must not already be in the list of active members of the
    // TimeslotB. Once all cars in TimeslotA than can be are so relocated,repeat the process for the timeslot with the second smallest 
    // number of cars.
    //
    // Returns a list Entries moved from Timeslot_A to TImeSlot_B. Each item in the list
    // is a String of the form "C01-2 moved from Timeslot 8 to Timeslot 3"
    
    // Populating the TimeslotAssignments array is done in UpdateTimeslotStats
    
    //
    // Set up a list of the previous timeslots occupied by each Entry. The lists are initialized to the initial timslots, then updated
    // each time an Entry is moved to a different timeslot. Before allowing a move of an Entry, the list is checked to see if the putative new timeslot
    // is there. If so, the move is rejected. With this check the outermost loop would run forever, moving entries back and forth.
    //
    List<EntryPreviousTimeslot> entryPrevTimeslots = new ArrayList<>();
    ListIterator iEntryPrevTS;
    EntryPreviousTimeslot aEntryPrevList;
    EntryPreviousTimeslot theEntryPrevList;
    Integer TSAssignmentInnerValue = 0;
    Integer TSAssignmentOuterValue = 0;
    JudgeAssignment aJA;
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        Integer entryNode = aJA.theCarIndex;
        Integer ts = aJA.theTimeslot;
        entryPrevTimeslots.add(new EntryPreviousTimeslot());
        entryPrevTimeslots.get(i).SetEntryNode(entryNode);
        entryPrevTimeslots.get(i).SetAddTS(ts);
    }
    
    
    List<String> results;
    results = new ArrayList<>();
        Collections.sort(timeslotAssignments, new TimeslotAssignment.ReverseOrderByEntriesSize());
        //theLogger.info("Timeslots sorted by number of cars, largest first : " + timeslotAssignments);

        int FromCarIndex = 0;
        Set<Integer> setFromCarTeam ;
        Set<Integer> setConflictingPersonnel;
        Set<Integer> setToTimeslotPersonnel;

        List<Integer> lstFromCars; 
        TimeslotAssignment theTimeslotAssignmentOuter;
        TimeslotAssignment theTimeslotAssignmentInner;
        ListIterator liOuter;
        ListIterator liInner;
        //
        // The outermost do {} while() loop keeps trying until no timeslot moves are possible
        //
        int numOfMoves;    
        int numberOfTries = 0;
        do {
            numberOfTries++;
            theLogger.info("\n Sweep " + numberOfTries);
            //timeslotAssignments = JudgeAssignGUI.getTimeslotAssignments();
        //
        // For each try, there are 2 timeslot loops so as to try all pairs. The outer loop starts at the last entry because it has the fewest cars, and decrements.
        // The inner loop works from the top down, looking for a timeslot in which all judges & owner for the car needing to be moved are available
        //
        boolean breakOuter = false;
        numOfMoves = 0;

        liOuter = timeslotAssignments.listIterator(timeslotAssignments.size());
        search: while(liOuter.hasPrevious()) {
            theTimeslotAssignmentOuter = (TimeslotAssignment)liOuter.previous();
            TSAssignmentOuterValue = theTimeslotAssignmentOuter.getValue();
            //theLogger.info("From timeslot = " + timeslotAssignments.get(intFrom).getValue());
           // theLogger.info("From timeslot = " + theTimeslotAssignmentOuter.getValue());
            lstFromCars = new ArrayList<>(theTimeslotAssignmentOuter.lstCars);

            liInner = timeslotAssignments.listIterator(0);
            while(liInner.hasNext()) {
                theTimeslotAssignmentInner = (TimeslotAssignment)liInner.next(); 
                TSAssignmentInnerValue = theTimeslotAssignmentInner.getValue();
                setToTimeslotPersonnel = new HashSet<>(theTimeslotAssignmentInner.GetTeamMembers());
                theLogger.info(" Try to move cars from timeslot  " + TSAssignmentOuterValue + " to timeslot " + TSAssignmentInnerValue + "\n personnel:" + setToTimeslotPersonnel);
                // Try each car in lstFrom to see if it can be moved to the intTo timeslot
                for(int k = 0; k < lstFromCars.size(); k++){
                    FromCarIndex = lstFromCars.get(k);
                    setFromCarTeam = new HashSet<>(theJudgeAssignments.GetTeam(FromCarIndex));
                    theLogger.info("\tFrom car " + FromCarIndex + " Team " + setFromCarTeam);
                    setConflictingPersonnel  =  new HashSet<>(setFromCarTeam);
                    setConflictingPersonnel.retainAll(setToTimeslotPersonnel);
                    
                   if(setConflictingPersonnel.isEmpty()){
                        //theLogger.info("No Conflicting personnel while trying to move car " + FromCarIndex + " from timeslot " + theTimeslotAssignmentOuter.getValue() + " to timeslot " + theTimeslotAssignmentInner.getValue() );
                        // Check to see if the Entry has previously occupied the ToTimeslot
                        iEntryPrevTS = entryPrevTimeslots.listIterator(0);
                         while(iEntryPrevTS.hasNext()) {
                             aEntryPrevList = (EntryPreviousTimeslot)iEntryPrevTS.next();
                             if(aEntryPrevList.GetEntryNode() == FromCarIndex){
                                 if(!aEntryPrevList.previousTS.contains(theTimeslotAssignmentInner.getValue())) {
                                    theLogger.info("\tSUCCESS. Moving Entry " + FromCarIndex + " to timeslot " + TSAssignmentInnerValue);
                                    aEntryPrevList.SetAddTS(theTimeslotAssignmentInner.getValue());  // Add it to the previous list so it won't be moved here again
                                    //
                                    // break out of iterators to do actual move. Otherwise, ConcurrentModificationException might be thrown.
                                    //
                                     break search;
                                 }
                             }
                         }
                   }
                   else{
                       //aTSIndexToStringMap.get(currentTimeslot)  + " to " + aTSIndexToStringMap.get(aNewTimeslot)
                        theLogger.info("\tConflicting personnel while trying to move car " + FromCarIndex + " from timeslot " + aTSIndexToStringMap.get(TSAssignmentOuterValue) + " to timeslot " +  aTSIndexToStringMap.get(TSAssignmentInnerValue) + " :" + setConflictingPersonnel);
                    }
                }
            }
            
        }
        
        // do the move and update Timeslots 
        results.add(changeEntryTimeslot(timeslotAssignments, FromCarIndex, TSAssignmentInnerValue)); 
        numOfMoves++;
        results.add("Entry " +  FromCarIndex + " moved from timeslot " + TSAssignmentOuterValue + " to timeslot " + TSAssignmentInnerValue + "\n" );
        UpdateTimeslotStats();  
        
        } while(numOfMoves > 0 && numberOfTries < 5 );// repeat the Outermost loop if there were any changes, up to 5 tries just to prevent endless looping.
    return results;

}
*/



// Gets Judges for a target entry car

public List<Judge> GetJudgeObjectsForEntry(Integer aTargetCarIndex)   {
    JudgeAssignment aJA;
    Integer theCarIndex;
    Integer theJudgeNode;
    List<Judge> lstJudgeObjects = new ArrayList<>();
    
    
    List<Judge> result = new ArrayList<>();
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theCarIndex =aJA.GetCarIndex();
        
        if(theCarIndex == aTargetCarIndex){
            for(Integer j : aJA.GetJudgeIndicies()){
                Judge judge = theJudges.GetConcoursJudge(j);
                if(judge == null){
                   JOptionPane.showMessageDialog(null, "No Judge with node " + j + " is  in Concourse Judges in GetJudgeObjectsForEntry");
                } else{
                    lstJudgeObjects.add(judge);
                }
            }
         break;   
        }
    }    
    return lstJudgeObjects;
} 





public void SaveConcoursToFile(String fullPathToJudgeAssignFile){
        JudgeAssignment aJA;
        Integer theCarIndex;
        Integer theOwnerIndex;
        Integer theTimeslot;
        int intJudgeCount;
        Integer aJudge;
        List<Integer> lstJudges;
        String strOut;
     
        Writer writer = null;   

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(fullPathToJudgeAssignFile)));
            theLogger.info("Judge Assignment size: " + theJudgeAssignments.GetConcoursJudgeAssignments().size());
            strOut = "Car {(owner) Judges} Timeslot\n";
            //writer.write("Car {(owner) Judges} Timeslot\n" );
            for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
                aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
                theCarIndex =aJA.GetCarIndex();
                strOut = strOut + theCarIndex.toString();
                theOwnerIndex =aJA.GetOwnerIndex();
                strOut = strOut + " {(" + theOwnerIndex.toString() + ") ";
                theTimeslot =aJA.GetTimeslot();
                intJudgeCount = aJA.GetJudgeCount();
                lstJudges = new ArrayList<>(aJA.GetJudgeIndicies()) ;
 
                for(int j = 0;j<intJudgeCount; j++){
                    if(j > 0) strOut = strOut + " ";
                    strOut = strOut + lstJudges.get(j);
               }
                strOut = strOut + "}";
                strOut = strOut + " " + theTimeslot.toString() + "\n";
                //theLogger.info(theCarIndex);

            }    
            writer.write(strOut );
        } catch (IOException ex) {
            okDialog("ERROR: IOException in SaveConcoursToFile");
            theLogger.info("ERROR: IOException in SaveConcoursToFile");
            System.exit(-1);
        } finally {
                try {writer.close();} catch (Exception ex) {
                    okDialog("ERROR: Unknown exception in SaveConcoursToFile");
                    theLogger.info("ERROR: Unknown exception in SaveConcoursToFile");
                    System.exit(-1);
                }
        }        
        
        for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
            aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
            theCarIndex =aJA.GetCarIndex();
        }    

}

public void updateJudgeLoads(){
    //
    // Iterates through Judge Assignments and calculates the number of Entries assigned to each Judge
    //
    JudgeAssignment aJA;
    Integer theJudgeNode;
    theJudges.clearJudgeLoads();
    theLogger.info("Updating Judge Loads");
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        Iterator iterJudges = aJA.GetJudgeIndicies().iterator(); // This is the Judge list for the ith entry in JudgeAssignments
        while (iterJudges.hasNext()) {
            theJudgeNode = Integer.parseInt(iterJudges.next().toString());
            //theLogger.info("Incrementing load for Judge Node: " + theJudgeNode.toString());
            theJudges.IncLoadForNode(theJudgeNode);
        }

    }    

}


/*
  Returns the MAXIMUM number of Entries judged in ANY timeslot.
*/
public int UpdateTimeslotStats(){
    int maxTSEntries = 0;
    
    // lists of entries (aka Cars), active personnel, conflicted personal, etc.
     Set<Integer> setUsedTimeSlotIndicies = new HashSet();
     ArrayList<Integer> lstUsedTimeSlotIndicies = new ArrayList<>(); 
    int intTheTimeslot;
    int theOwner;
    int theCar;
    List<Integer> lstTheJudges;
    TimeslotAssignment objTheTimeslot;
    
    boolean ret = false; 
    
    JudgeAssignment aJA;
    TimeslotAssignment aTA;
    //
    // Create an list of used timeslot indicies, and as each new one is added also instantiate a TimeslotAssignment object
    //
    theTimeslotAssignments.GetConcoursTimeslotAssignments().clear(); // In case the command is executed again
    //theLogger.info("JudgeAssignments size:" + theJudgeAssignments.GetConcoursJudgeAssignments().size());
    int maxTs = 0;
    for(int iJA = 0; iJA< theJudgeAssignments.GetConcoursJudgeAssignments().size(); iJA++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(iJA);
        intTheTimeslot =aJA.GetTimeslot();
        if(intTheTimeslot > maxTs) maxTs = intTheTimeslot;
        if (setUsedTimeSlotIndicies.add(intTheTimeslot)) { // need set to ensure uniqueness
           ret = lstUsedTimeSlotIndicies.add(intTheTimeslot); // need array for convenient access
           theTimeslotAssignments.GetConcoursTimeslotAssignments().add(new TimeslotAssignment(intTheTimeslot, this)); // intTheTimeslot becomes intTSId
        }
    }
    int [] numEntriesInSlots = new int[lstUsedTimeSlotIndicies.size()];
   //
   //   Now sweep through the Judge Assignments and collect all Judges & Owners active in each timeslot into list lstTeamMembers, lstOwners, & lstJudges
   //   for respective timeslots. Conflicts are collected into lstConflicts for each timeslots.
    
   //   Also construct lists of all Cars in each timeslot, and lists of all Cars with conflicted teams in each timeslot 
   // 
    boolean OwnerConflict;
    boolean JudgeConflict;
    for(int iJA = 0; iJA< theJudgeAssignments.GetConcoursJudgeAssignments().size(); iJA++){
        OwnerConflict = false;
        JudgeConflict = false;
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(iJA);
        theOwner =aJA.GetOwnerIndex();
        theCar =aJA.GetCarIndex();
        lstTheJudges =aJA.GetJudgeIndicies();
        intTheTimeslot = aJA.GetTimeslot();
        
        //objTheTimeslot = TimeslotAssignments.get(intTheTimeslot); // timeslot indices are now 0-based
        objTheTimeslot = getTimeslotForTargetTSId(theTimeslotAssignments.GetConcoursTimeslotAssignments(), intTheTimeslot);
        //if(intTheTimeslot == 4){
        //    theLogger.info("Adding owner " +theOwner + " to timeslot " + intTheTimeslot);
        //}
        OwnerConflict = objTheTimeslot.addOwner(theOwner); // will add to lstTeamMember and lstOwners if not already there. Otherwise it's added to
                                                // lstConflicts.
       for(int kJ = 0; kJ<lstTheJudges.size(); kJ++){
        //if(intTheTimeslot == 4){
        //    theLogger.info("Adding judge " +lstTheJudges.get(kJ) + " to timeslot " + intTheTimeslot);
        //}
           JudgeConflict = JudgeConflict || objTheTimeslot.addJudge(lstTheJudges.get(kJ)); // will add to lstTeamMember if not already there. Otherwise, it's added to
                                                              // lstConflicts
        }
       
       
    }
    //
    // Building the list of conflicted cars requires iterating JudgeAssignments again
    //
     for(int iJA = 0; iJA< theJudgeAssignments.GetConcoursJudgeAssignments().size(); iJA++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(iJA);
        theOwner =aJA.GetOwnerIndex();
        theCar =aJA.GetCarIndex();
        lstTheJudges =aJA.GetJudgeIndicies();
        intTheTimeslot = aJA.GetTimeslot();
        objTheTimeslot = getTimeslotForTargetTSId(theTimeslotAssignments.GetConcoursTimeslotAssignments(), intTheTimeslot);
        for(Integer JudgeIndex : objTheTimeslot.lstConflicts){
            if(lstTheJudges.contains(JudgeIndex)) objTheTimeslot.addCar(theCar);
        }
        if(objTheTimeslot.lstConflicts.contains(theOwner))objTheTimeslot.addCar(theCar);
    }
     
    for(int ts : lstUsedTimeSlotIndicies){
        for (Iterator<JudgeAssignment> it = theJudgeAssignments.GetConcoursJudgeAssignments().iterator(); it.hasNext();) {
            JudgeAssignment ja = it.next();
            intTheTimeslot = ja.GetTimeslot();
            if(intTheTimeslot > (numEntriesInSlots.length -1)){
                String msg = "ERROR: In UpdateTimeslotStats() time slot index = " + intTheTimeslot + " Should be <= " + (numEntriesInSlots.length -1);
                okDialog(msg);
                theLogger.info(msg);
                System.exit(-1);
            }
            if(intTheTimeslot == ts){
                numEntriesInSlots[intTheTimeslot]++;
            }
        }
    }
    if(numEntriesInSlots.length > 0){
        Arrays.sort(numEntriesInSlots);
        maxTSEntries = numEntriesInSlots[numEntriesInSlots.length-1];
    } else{
        maxTSEntries = 0;
    }
    
    return maxTSEntries;
     
}
// helper function to get the TimeslotAssignment in TimeslotAssignments  for targetTSId.
    public  TimeslotAssignment getTimeslotForTargetTSId(ArrayList<TimeslotAssignment> timeslots, Integer targetTSId) {
    for(TimeslotAssignment ts : timeslots) {
        if(ts != null && ts.getValue() == targetTSId) {
            return ts;
        }
    }
    theLogger.info("ERROR: ISId " + targetTSId + " not in TimeslotAssignments array.");
    return null;
    }

    // helper function to get the index of TimeslotAssignment in TimeslotAssignments for targetTSId.
    public  Integer getTimeslotIndexForTargetTSId(ArrayList<TimeslotAssignment> timeslots, Integer targetTSId) {
    for(int i = 0; i < timeslots.size(); i++) {
        if(timeslots.get(i).getValue() == targetTSId) {
            return i;
        }
    }
    theLogger.info("Error: ISId " + targetTSId + " not in TimeslotAssignments array.");
    return null;
    }
    
    

public String changeEntryTimeslot(ArrayList<TimeslotAssignment>  aTimeslotAssignments, Integer aEntry, Integer aNewTimeslot, Map<Integer, String> aTSIndexToStringMap){
    //
    // Moves aEntry to newTimeslot in JudgeAssignments
    //
    final int  NO_CONFLICT = 0; 
    final int JUDGE_CONFLICT = 1; // a judge would have two or more judging assignments in the new timeslot if the change were to be done. NOT ALLOWED
    final int OWNER_CONFLICT = 2; // an owner would have two or more entries in the new timeslot if the change were to be done. NOT ALLOWED
    final int OWNER_JUDGE_CONFLICT = 3; // an owner would have one or more judging assignments the new timeslot if the change were to be done. USER CAN ELECT TO ALLOW THE CONFLICT
    final int MULTIPLE_CONFLICT_TYPES = 4; //  JUDGE_CONFLICT & OWNER_CONFLICT
    String res;
    boolean found;
    
    boolean teamConflicts;
    boolean ownerConflicts = false;
    boolean judgeConflicts = false;
    
    //boolean noConflict; 
    int conflictType; // NO_CONFLICT, JUDGE_CONFLICT, OWNER_CONFLICT, OWNER_JUDGE_CONFLICT
    String[]  strConflictType = {"No conflict", "Judge conflict", "Owner conflict", " Owner-Judge conflict"};
    boolean invalidNewTimeslot;
    TimeslotAssignment objTheNewTimeslot;

    JudgeAssignment aJA;
    Integer currentTimeslot;
    String strEntry = theEntries.getEntryID(aEntry);
    Set<Integer> setEntryTeam ;
    Set<Integer> setEntryOwners ;
    Set<Integer> setConflictingOwners ;
    Set<Integer> setNewTimeslotOwners = null;
    
    Set<Integer> setEntryJudges ;
    Set<Integer> setConflictingJudges ;
    Set<Integer> setNewTimeslotJudges = null;

    Set<Integer> setConflictingPersonnel;
    Set<Integer> setNewTimeslotPersonnel = null;
    res = "";
    theLogger.info("\tStarting changeEntryTimeslot() for Entry " + strEntry +  " to timeslot " + aTSIndexToStringMap.get(aNewTimeslot));
    found = false;
    conflictType = NO_CONFLICT;
    invalidNewTimeslot = false;
    currentTimeslot = 0;
    objTheNewTimeslot = getTimeslotForTargetTSId(aTimeslotAssignments, aNewTimeslot);
    aJA = null; //new JudgeAssignment(); 5/22/2017 should never be used
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        if (aJA.GetCarIndex() == aEntry){
            found = true;
            currentTimeslot = aJA.GetTimeslot();
            if(currentTimeslot == aNewTimeslot){
                invalidNewTimeslot = true;
                //theLogger.info("New & current timeslots are the same. No action will be taken");
            } else {
                theLogger.info("\nChecking feasibility of moving of Entry " + strEntry + " from timesolt " +  aTSIndexToStringMap.get(currentTimeslot)  + " to " + aTSIndexToStringMap.get(aNewTimeslot));
                setEntryTeam = new HashSet<>(theJudgeAssignments.GetTeam(aEntry)); // entry Owner & judges for the Entry to be moved
                // null objTheNewTimeslot means a new, empty timeslot so no conflicts are possible
                if(objTheNewTimeslot == null){
                    // it's going to be a new timeslot at end of table
                    setConflictingPersonnel  =  new HashSet<>();
                } else {
                    setConflictingPersonnel  =  new HashSet<>(setEntryTeam);
                    setNewTimeslotPersonnel = new HashSet<>(objTheNewTimeslot.GetTeamMembers()); // all Owner & judges in the new timeslot
                    setConflictingPersonnel.retainAll(setNewTimeslotPersonnel);    // Set intersection
                }
                
                
                if(setConflictingPersonnel.isEmpty()){
                    teamConflicts = false;
                    conflictType = NO_CONFLICT;
                } else{
                    // There are conflicts so we have to determine the type
                    //
                    // First, look for owner conflicts
                    //
                    teamConflicts  = true;
                    setEntryOwners = new HashSet<>(theJudgeAssignments.GetOwners(aEntry)); // entry Owner for the Entry to be moved
                    if(objTheNewTimeslot == null){
                        // it's going to be a new timeslot at end of table
                        setConflictingOwners = new HashSet<>();
                    } else {
                        setConflictingOwners  =  new HashSet<>(setEntryOwners);
                        setNewTimeslotOwners = new HashSet<>(objTheNewTimeslot.GetOwners()); // all Owner in the new timeslot
                        setConflictingOwners.retainAll(setNewTimeslotOwners);    // Set intersection
                    }
                    if(!setConflictingOwners.isEmpty()){
                        ownerConflicts = true;
                    }

                    //
                    // Now, look for Judge conflicts
                    //
                    setEntryJudges = new HashSet<>(theJudgeAssignments.GetJudges(aEntry)); // entry Judge for the Entry to be moved
                    if(objTheNewTimeslot == null){
                        // it's going to be a new timeslot at end of table
                        setConflictingJudges =  new HashSet<>();
                    } else{
                        setConflictingJudges  =  new HashSet<>(setEntryJudges);
                        setNewTimeslotJudges = new HashSet<>(objTheNewTimeslot.GetJudges()); // all Judges in the new timeslot
                        setConflictingJudges.retainAll(setNewTimeslotJudges);    // Set intersection
                    }
                    if(!setConflictingJudges.isEmpty()){
                        judgeConflicts = true;
                    }
                    //
                    // Now set the conflict types
                    //
                    if(ownerConflicts && !judgeConflicts) {
                        // Owner would have 2 or more cars judged in the new timeslot
                        conflictType = OWNER_CONFLICT;
                    } else if(judgeConflicts && !ownerConflicts) {
                        // Judge woould be assigned to 2 or more cars in the new timeslot
                        conflictType = JUDGE_CONFLICT;
                    } else if(judgeConflicts && ownerConflicts){
                         // Owner & Judge conflicts
                         conflictType = MULTIPLE_CONFLICT_TYPES;
                    } else {
                        // By eliminiation, the conflict must be Owner is being judged while being assigned to judge int the new timeslot
                        conflictType = OWNER_JUDGE_CONFLICT;
                    } 
                    
                }
            }
            break;
        } 
//        theLogger.info("\nFinished checking for conflicts in changeEntryTimeslot. Conflict type = " + strConflictType[conflictType]);
    }

      theLogger.info("\nFinished checking for conflicts in changeEntryTimeslot. Conflict type = " + strConflictType[conflictType]);
    
   // Reworked logic here to optionally allow Judge/Owner conflicts during JA edits 5/27/16
    if(!found){
        res = "\nERROR: Invalid Entry node index in ChangeEntryTimeslot. This should never happen";
        okDialog(res);
        theLogger.info(res);
        System.exit(-1);
    } else{
        if(invalidNewTimeslot){
            okDialog("New timeslot same as current timeslot. No action taken.");
            theLogger.info(res);
            return res;
        }
        int numEntriesInCurrentTimeslot = theJudgeAssignments.getNumEntriesInTimeSlot(currentTimeslot);  // If == 0 the From TS is now empty so indicies of all subsequent TS have to be decremented.
        switch (conflictType){
            case NO_CONFLICT:
                aJA.SetTheTimeslot(aNewTimeslot);
                if(numEntriesInCurrentTimeslot == 1){ 
                    // the original TS for the Entry being move has become empty because the single entry has been moved. So we must
                    // decrement all timeslot indices in JudgeAssignemtns greating than the index of a newly emptied timeslot
                    theJudgeAssignments.closeGap(currentTimeslot);
                }
                res = "\n Entry " + strEntry + " has been moved from timesolt " +  aTSIndexToStringMap.get(currentTimeslot)  + " to " + aTSIndexToStringMap.get(aNewTimeslot) ;
                //okDialog(res);
                theLogger.info(res);
                break;// Goes to UpdateTimeslotStats()
            case JUDGE_CONFLICT:
                res = "\n Move of Entry " + strEntry + " from timesolt " +  aTSIndexToStringMap.get(currentTimeslot) + " to " + aTSIndexToStringMap.get(aNewTimeslot) + " prevented by Judge conflict. No action will be taken";
                okDialog(res);
                theLogger.info(res);
                return res;
            case OWNER_CONFLICT:
                res = "\n Move of Entry " + strEntry + " from timesolt " +  aTSIndexToStringMap.get(currentTimeslot) + " to " + aTSIndexToStringMap.get(aNewTimeslot) + " prevented by Owner conflict. No action will be taken";
                okDialog(res);
                theLogger.info(res);
                 return res;
            case OWNER_JUDGE_CONFLICT:
                  //ask user for permission to do the move
                int response = yesNoDialog("Entry Owner has a judging assignment in this timeslot. Do you wish to allow this conflict?");
                if(response == JOptionPane.YES_OPTION) {
                    aJA.SetTheTimeslot(aNewTimeslot);
                    if(numEntriesInCurrentTimeslot == 1){ 
                        // the original TS for the Entry being move will become empty so we must
                        // decrement all timeslot indices in JudgeAssignemtns greating than the index of a newly emptied timeslot
                        theJudgeAssignments.closeGap(currentTimeslot);
                    }
                    res = "\n Entry " + strEntry + " has been moved from timesolt " +  aTSIndexToStringMap.get(currentTimeslot) + " to " + aTSIndexToStringMap.get(aNewTimeslot) ;
                    //okDialog(res);
                    theLogger.info(res);
                    break;// Goes to UpdateTimeslotStats()
               } else{
                    res = "\n User declined move entry to the conflicting timeslot. No action taken" ;
                    //okDialog(res);
                    theLogger.info(res);
                    return res;
                }
        }
        
    }
    
    UpdateTimeslotStats(); 
    
    
    return res;
}


////////////////////////////////////////////////////////////////////
//  5/23/2017
//  Redesigned this function entirely since the original was complicated and wrong!
//   Simply iterate through theJudgeAssignments & patch the new team into
//   the ones for which the car index maps /////////////////////////
///////////////////////////////////////////////////////////////////
public String ChangeClassJudge(ConcoursClass aClass, Judge aCurrentJudge,  Judge aNewJudge){
    String res = "";
    Integer aCurrentJudgeIndex =  aCurrentJudge.GetNode();
    ArrayList<Integer> lstJi = null; // to keep compiler happy
    boolean foundEntry = false;
    int numJAsForClass = 0;
    for(JudgeAssignment ja :  theJudgeAssignments.GetConcoursJudgeAssignments()){
        int jaCarIndex = ja.GetCarIndex();
        String entryClassName = theEntries.getEntryClass(jaCarIndex);
        if( entryClassName.equals(aClass.GetClassName())){
            numJAsForClass++;
            lstJi =  new ArrayList<>(ja.GetJudgeIndicies()); // list of Judge indicies
            int  i = lstJi.indexOf(aCurrentJudgeIndex); // i is the index in lstJi
            lstJi.remove(i);
            lstJi.add(i, aNewJudge.GetNode());
            ja.SetTheJudges(lstJi); // clears the existing list and replaces it
        }
     }
     if(numJAsForClass == 0){
         String msg = "ERROR: Failed to find an Judge Assignments for Concours Class " + aClass.GetClassName() + " in ChangeClassJudge";
         okDialog(msg);
         theLogger.info(msg);
         return msg;
     } 
   
    // Change ConcoursClass in memory
    aClass.RemoveJudgeIndex(aCurrentJudge.GetNode());
    aClass.RemoveJudgeObject(aCurrentJudge);
    aClass.AddJudgeIndex(aNewJudge.GetNode());
    aClass.AddJudgeObject(aNewJudge);

    // And finally, update Judge loads
    updateJudgeLoads();
    res = "\nChanged judge " + aCurrentJudge.GetLastName() + " to " + aNewJudge.GetLastName() + " in Class " + aClass.GetClassName();    
    return res;
}


public class JATableByJudgeColHeader{  
    String judgename;
    Integer judgenode;
    String judgeHeaderText;
    JATableByJudgeColHeader(String ajudgename, Integer ajudgenode){
        judgename= ajudgename;
        judgenode = ajudgenode;
        // REMOVE node number
        //judgeHeaderText = "<html><p>" + judgename  + "</p><p>(" + judgenode +")</p></html>";
        judgeHeaderText = "<html><p>" + judgename  + "</p></html>";
    }
    @Override
    public String toString(){
        return judgeHeaderText;
    }
    public String getName(){
        return judgename;
    }
    public Integer getValue(){
        return judgenode;
    }

}

    /*
     * Update the Judge lists in all Concours Classes to reflect the current JudgeAssignments created by the maching/sheduling process
    */
public void UpdateConcourseClassesJudgeLists(){

    for(JudgeAssignment JA : theJudgeAssignments.GetConcoursJudgeAssignments()){
        int entryIndex = JA.GetCarIndex();
        String entryClassName = theEntries.getEntryClass(entryIndex);
        ConcoursClass cco = theConcoursClasses.GetConcoursClassObject(entryClassName);
        cco.RemoveAllJudges();
        for( Integer ji : JA.GetJudgeIndicies()){
            Judge j = theJudges.GetConcoursJudge(ji);
            if(j == null){
               JOptionPane.showMessageDialog(null, "No Judge with node " + ji + " is  in Concourse Judges in UpdateConcourseClassesJudgeLists");
            } else{
                cco.AddJudgeIndex(ji);
                cco.AddJudgeObject(j);
            }
            
        }
    }
}


public class JATableByClassColHeader{  
    String classname;
    Integer classnode;
    ArrayList<String> classjudgenames;
    String classheadertext;

    /*
     * Update the Judge lists in all Concours Classes to reflect the current JudgeAssignments created by the matching/scheduling process
    */
    
    
    JATableByClassColHeader(Concours aConcours, String aclassname, Integer aclassenode){
        classname= aclassname;
        classnode = aclassenode;
        ConcoursClass cc = aConcours.GetConcoursClassesObject().GetConcoursClassObject(aclassname);
        classheadertext = "<html><p>Class: " + aclassname + "</p>";
        if(cc != null){
            classjudgenames = cc.GetClassJudgeUniqueNames() ;
            for(String name : classjudgenames){
                classheadertext = classheadertext + "<p>J: " + name + "</p>";
            }
        }
        classheadertext = classheadertext + "</html>";

    }
    @Override
    public String toString(){
        return classheadertext;
    }
    public Integer getValue(){
        return classnode;
    }
    public String getName(){
        return classname;
    }

}


public class JATableMergeCompressedColHeader{  
    ArrayList<String> classjudgenames;
    String mergedclassheadertext;
    ArrayList<String> classnames = new ArrayList<>();

    // Constructor 1 ... 
    JATableMergeCompressedColHeader(Concours aConcours, String aMergedClassName){
        //for(String classname : aMergedClassNamesList){
            classnames.add(aMergedClassName);
            //String classheadertext;
            if(!aMergedClassName.contains("Time")){
                mergedclassheadertext = "<html><p>---Class: " + aMergedClassName + "</p>";
            } else {
                mergedclassheadertext = "<html><p>" + aMergedClassName + "</p>";
            }
            ConcoursClass cc = aConcours.GetConcoursClassesObject().GetConcoursClassObject(aMergedClassName);
            if(cc != null){
                classjudgenames = cc.GetClassJudgeUniqueNames() ;
                for(String name : classjudgenames){
                    mergedclassheadertext = mergedclassheadertext + "<p>J: " + name + "</p>";
                }
            }
            mergedclassheadertext = mergedclassheadertext + "</html>";
       //}
        
    }
   
    // Constructor 2
    // Converts a JATableByClassColHeader to a JATableMergeCompressedColHeader.
    // 
    JATableMergeCompressedColHeader(Concours aConcours, List<JATableByClassColHeader> aJATableByClassColHeaderList){
        for(JATableByClassColHeader byclassheader : aJATableByClassColHeaderList){
           classjudgenames = byclassheader.classjudgenames;
           classnames.add(byclassheader.getName());
           mergedclassheadertext = "<html><p>" + byclassheader.getName() + "</p>";
            ConcoursClass cc = aConcours.GetConcoursClassesObject().GetConcoursClassObject(byclassheader.getName());
            if(cc != null){
                classjudgenames = cc.GetClassJudgeUniqueNames() ;
                for(String name : classjudgenames){
                    mergedclassheadertext = mergedclassheadertext + "<p>J: " + name + "</p>";
                }
            }
            mergedclassheadertext = mergedclassheadertext + "</html>";
        }
    }
    // This is used to merge 2 columns. aClassNamesToMerge is merged into "this"
    
    public void mergeCompressedColHeaders(Concours aConcours, List<String> aClassNamesToMerge){
        for(String  classname : aClassNamesToMerge){
            if(!classnames.contains(classname)){ 
                classnames.add(classname);
                //if(!classname.contains("Time")){
                    mergedclassheadertext = mergedclassheadertext.replaceFirst("</html>", "") + "<p>---Class: " + classname + "</p>";
                //} else {
                //    mergedclassheadertext = mergedclassheadertext.replaceFirst("</html>", "") + "<p>" + classname + "</p>";
                //}
                ConcoursClass cc = aConcours.GetConcoursClassesObject().GetConcoursClassObject(classname);
                if(cc != null){
                    classjudgenames = cc.GetClassJudgeUniqueNames() ;
                    for(String name : classjudgenames){
                        mergedclassheadertext = mergedclassheadertext + "<p>J: " + name + "</p>";
                    }
                }
                mergedclassheadertext = mergedclassheadertext + "</html>";
            }
        }
        
    }
        @Override
    public String toString(){
        return mergedclassheadertext;
    }
    
    public void setMergedClassHeaderText(String aText){
        mergedclassheadertext = aText;
    }
    
    public void makeEmptyMergedClassHeaderText(){
       mergedclassheadertext = "";
       classnames.clear();
       classjudgenames.clear();
    }

}
public class JATableCompressedColHeader{  
    String columnheadertext;
    int columnindex;
    // Constructor
    JATableCompressedColHeader(Concours aConcours, String aHeader, int aColIndex){
        columnheadertext = "<html><p>" + aHeader + "</p></html>";
        columnindex = aColIndex;
    }
    @Override
    public String toString(){
        return columnheadertext;
    }
    public int getValue(){
        return columnindex;
    }
}


public List<JATableByJudgeColHeader> UpdateJAByJudgeTableHeader(){
    Integer theJudgeNode;
    String theJudgeLastName;
    Judge theJudge;
    JATableByJudgeColHeader  colHeader;
    List<JATableByJudgeColHeader> headerList = new ArrayList<>();
    
    colHeader = new JATableByJudgeColHeader("Time slot", 99); // This is a column for timeslots, obviously not a judge!
    headerList.add(0, colHeader);
    Iterator iterJudges = theJudges.concoursJudges.iterator(); // This is the Judge list for the ith entry in JudgeAssignments
    while (iterJudges.hasNext()) {
        theJudge = (Judge) iterJudges.next();
        theJudgeNode = theJudge.GetNode();
        //theJudgeLastName = theJudge.GetLastName();
        theJudgeLastName = theJudge.getUniqueName();
       // theLogger.info("Judge Node: " + theJudgeNode + " Last Name: " + theJudgeLastName);
        colHeader = new JATableByJudgeColHeader(theJudgeLastName, theJudgeNode);
        headerList.add(colHeader);
    }
       return headerList;
    }

public List<JATableByClassColHeader> UpdateJAByClassTableHeader(Concours aConcours){
    Integer theClassNode;
    String theClassName;
    JATableByClassColHeader  colHeader;
    List<JATableByClassColHeader> headerList = new ArrayList<>();
    
    colHeader = new JATableByClassColHeader(aConcours, "Time slot", 99); // This is a column for timeslots, obviously not a judge!
    headerList.add(0, colHeader);
    for(ConcoursClass cc : theConcoursClasses.GetConcoursClasses()){
        theClassName = cc.GetClassName();
        if(!theClassName.equals("DISP")){
            theClassNode = cc.GetClassNode();
            colHeader = new JATableByClassColHeader(aConcours, theClassName, theClassNode);
            headerList.add(colHeader);
        }
    }
       return headerList;
    }
// UpdateJACompressedTableHeader() no longer used 7/26/2018
//  
// Now see that UpdateJACompressedTableHeader() is needed, but not this one!!! 7/30/2018 
/*
public List<JATableCompressedColHeader> UpdateJACompressedTableHeader(Concours aConcours){
    //Integer theClassNode;
   // String theClassName;
    JATableCompressedColHeader  colHeader;
    List<JATableCompressedColHeader> headerList = new ArrayList<>();
    
    colHeader = new JATableCompressedColHeader(aConcours, "Time slot", 99); // This is a column for timeslots, obviously not a column!
    headerList.add(0, colHeader);
    int numEntryCols = getMaxTimeslotEntries();
    for(int i = 1; i <= numEntryCols; i++){
        colHeader = new JATableCompressedColHeader(aConcours, "Column " + i,  i );
        headerList.add(colHeader);
    }
       return headerList;
    }
*/


public Object [][] UpdateJAByJudgeTableRowData(List<JATableByJudgeColHeader> headerList){
    JudgeAssignment aJA;
    Integer theCarIndex;
    String theCarID;
    String theCarDescription;
    String theCarColor;
    Integer theOwnerIndex;
    String  theOwnerLastName;
    String aJudgeLastName;
    Integer theTimeslot;
    String cellText;
    String strCarClass;
    Judge theClassLeadJudge;
    int intJudgeCount;
    List<Integer> lstJudges ;
    List<Integer> uniqueTimeslots = new ArrayList<>();
    String temp;
    int numRows;
    int numCols;
    
    Integer aJudgeIndex;
    //
    // Construct a list of lists of values with table row data the table rows.
    // Begin by getting a count of the different timeslots used in JudgeAssignments
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theTimeslot =aJA.GetTimeslot();
        if(!uniqueTimeslots.contains(theTimeslot))   uniqueTimeslots.add(theTimeslot);
    }
    numRows = uniqueTimeslots.size();
    numCols = headerList.size();
    Object rowArray [][] = new Object [numRows][numCols] ;
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theCarIndex = aJA.GetCarIndex();
        theCarID = theEntries.getEntryID(theCarIndex); 
        strCarClass = theEntries.getEntryClass(theCarIndex);
        theClassLeadJudge = theConcoursClasses.GetConcoursClassObject(strCarClass).GetClassLeadJudge();
        
        theCarDescription = theEntries.getEntry(theCarIndex).GetDescription();
        String color = theEntries.getEntry(theCarIndex).GetColor();
        theCarColor = masterJagColorToScheduleColorMap.get(color);
        if(theCarColor == null){
            String msg = "Color: " + color + " not in masterJagColorToScheduleColorMap. Will use first 10 characters.";
            //okDialog(msg);
            theLogger.info(msg);
            int maxLen = (color.length() > 10) ? 10 : color.length();
            String mapTo = color.substring(0, maxLen);
            theCarColor = mapTo;
        }
        theOwnerIndex =aJA.GetOwnerIndex();
       // theOwnerLastName = theEntries.getOwnerLast(theCarIndex); // this works too, provided the owner node numbers in Entries correspond to those in Owners... should always be so.
        theOwnerLastName = theOwners.getOwnerName(theOwnerIndex);
        
        theTimeslot =aJA.GetTimeslot();
        //Fill in the Timeslot column 
        int column = -1;
        int headerListSize = headerList.size();
        for(int k =0; k < headerListSize; k++){
            if(headerList.get(k).judgenode == 99) { // 99 is the "judgenode" for the Timeslot column in headerList
                column = k;
                break;
            }
        }
        String nextStartTime = calcNextTimeslotStart(theTimeslot);
        String htmltimeslotTime =  "<html><p>" + nextStartTime + "</p></html>";
        rowArray [theTimeslot] [column] = htmltimeslotTime; 
       // The follwing loop will fill a cell in the current timeslot for each judge assigned to the Class
       intJudgeCount = aJA.GetJudgeCount();
       lstJudges = new ArrayList<>(aJA.GetJudgeIndicies()) ;
       int judgeNode;
       for(int j = 0;j<intJudgeCount; j++){
            aJudgeIndex = lstJudges.get(j);
            aJudgeLastName = theJudges.getJudgeLastName(aJudgeIndex);
            // find the index in headerList, which will be the column index in rowArray
            column = -1;
            for(int k =0; k < headerList.size(); k++){
                judgeNode = headerList.get(k).judgenode;
                if(judgeNode ==  aJudgeIndex) {
                    column = k;
                    break ;
                }
            }
            
            boolean ok = true; 
            if ((theTimeslot < 0)  || (theTimeslot >= numRows)){
                theLogger.info("Addressing error in UpdateJAByJudgeTableRowData. timeslot = " +  theTimeslot + " but should be  greater than 0 and less than " + numRows);
                ok = false;
            }
            if((column < 0) || (column >= numCols)){
                theLogger.info("Addressing error in UpdateJAByJudgeTableRowData. column = " +  column + " but should be greater than 0 and less than " + numCols);
                ok = false;
            }
            if(ok){
                //  WANT TO SHOW WHEN THERE IS A CONFLICT...
                //   This could be improved, e.g., color the background, and perhaps show the all entries in the timeslot.
                //
                //  Also want to set cell contents BOLD if the Column Judge is the Lead Judge for the Entry's Class
                //
                boolean leadjudge = aJudgeLastName.equals(theClassLeadJudge.GetLastName());
                String sb;
                String eb;
                if(leadjudge){
                    sb = "<b>";
                    eb = "</b>";
                } else {
                    sb = "";
                    eb = "";
                }
                String curCellText = (String)rowArray [theTimeslot] [column]; 
                if(curCellText != null){
                   // cellText = "<html><p>E:" + theCarID + "(" + theCarIndex + ")</p><p>O:" + theOwnerLastName + "(" + theOwnerIndex + ") JUDGE OVERLOAD</p></html>";
                    cellText = "<html>" + sb + "<p>E:" + theCarID + "</p><p>O:" + theOwnerLastName  + "</p><p>C:" + theCarColor + "</p><p>D:" + theCarDescription  + " JUDGE OVERLOAD</p>" + eb + "</html>";
                    theLogger.log(Level.INFO, "Judge overloaded in timeslot {0}Current cell contents: {1}", new Object[]{theTimeslot, curCellText});
                } else{
                   // cellText = "<html><p>E:" + theCarID + "(" + theCarIndex + ")</p><p>O:" + theOwnerLastName + "(" + theOwnerIndex + ")</p></html>";
                    cellText = "<html>" + sb + "<p>E:" + theCarID + "</p><p>O:" + theOwnerLastName +  "</p><p>C:" + theCarColor + "</p><p>D:" + theCarDescription +"</p>" + eb + "</html>";
                }
                rowArray [theTimeslot] [column] = cellText;
            }
            else {
                okDialog("ERROR: Problem in UpdateJAByJudgeTableRowData ");
                theLogger.info("ERROR: Problem in UpdateJAByJudgeTableRowData ");
                System.exit(-1);
            }
       }
    }
    return  rowArray;

}
public String calcNextTimeslotStart(int aTimeslot){
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    Date dateStartTime = null;
    Date dateLunchTime = null;

    Calendar cal = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    df = new SimpleDateFormat("HH:mm");
    String nextJudgingStartTime; 
 
    try {
        dateStartTime = df.parse(strStartTime);
    } catch (ParseException ex) {
        String msg = "Exception parsing Start time in calcNextTimeslotStart().";
        okDialog(msg);
        theLogger.log(Level.SEVERE, msg, ex);
        System.exit(-1);
    }
    try {
        dateLunchTime = df.parse(strLunchTime); // precalculated 
    } catch (ParseException ex) {
        String msg = "Exception parsing Lunch time in calcNextTimeslotStart().";
        okDialog(msg);
        theLogger.log(Level.SEVERE, msg, ex);
        System.exit(-1);
    }
        if(aTimeslot <= (slotsBeforeLunch - 1)){
            cal2.setTime(dateStartTime);
            cal.setTime(dateStartTime);
            cal.add(Calendar.MINUTE, aTimeslot*timeslotInterval);
            cal2.add(Calendar.MINUTE, (aTimeslot+1)*timeslotInterval); // end of interval
            if(aTimeslot == (slotsBeforeLunch - 1)){
                nextJudgingStartTime = df.format(cal.getTime()) + "-Lunch"; // Gives a clue as to when lunch is
            } else {
                nextJudgingStartTime = df.format(cal.getTime()) + "-" + df.format(cal2.getTime());
            }
        } else {
            // after lunch
            cal2.setTime(dateLunchTime);
            // first time here aTimeslot == slotsBeforeLunch 
            cal.setTime(dateLunchTime);
            cal.add(Calendar.MINUTE, lunchInterval + (aTimeslot - slotsBeforeLunch)*timeslotInterval );// Beginning of judging timeslot
            int minutesAfterLunchTime = (aTimeslot - slotsBeforeLunch + 1)*timeslotInterval; 
            cal2.add(Calendar.MINUTE, minutesAfterLunchTime +  lunchInterval); // End of judging timeslot
            nextJudgingStartTime = df.format(cal.getTime()) + "-" + df.format(cal2.getTime());
        }
    return nextJudgingStartTime;
}
public Object [][] UpdateJAByClassTableRowData(List<JATableByClassColHeader> aheaderList){
    JudgeAssignment aJA;
    Integer theCarIndex;
    String theCarID;
    String theCarColor;
    String theCarDescription;
    Integer theOwnerIndex;
    String  theOwnerLastName;
    String aJudgeLastName;
    Integer theTimeslot;
    String cellText;
    int intJudgeCount;
    List<Integer> lstJudges ;
    List<Integer> uniqueTimeslots = new ArrayList<>();
    String temp;
    int numRows;
    int numCols;
    
    Integer aJudgeIndex;
    //
    // Construct a list of lists of values with table row data the table rows.
    // Begin by getting a count of the different timeslots used in JudgeAssignments
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theTimeslot =aJA.GetTimeslot();
        if(!uniqueTimeslots.contains(theTimeslot))   uniqueTimeslots.add(theTimeslot);
    }
    numRows = uniqueTimeslots.size();
    numCols = aheaderList.size(); // this is the number of Concours Classes + 1
    Object rowArray [][] = new Object [numRows][numCols] ;
    String strCarClass;
    String strCarUniqueDescription;
    
    /*SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    Date dateStartTime = null;
    Date dateLunchTime = null;

    Calendar cal = Calendar.getInstance();
    df = new SimpleDateFormat("HH:mm");
    String timeslotTime; 
 
    try {
        dateStartTime = df.parse(strStartTime);
    } catch (ParseException ex) {
        theLogger.log(Level.SEVERE, null, ex);
    }
    try {
        dateLunchTime = df.parse(strLunchTime);
    } catch (ParseException ex) {
        theLogger.log(Level.SEVERE, null, ex);
    }
    */
   ConcoursClass theEntryClassObject;
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theCarIndex = aJA.GetCarIndex();
        theCarID = theEntries.getEntryID(theCarIndex);
        theCarDescription = theEntries.getEntry(theCarIndex).GetDescription();
        String color = theEntries.getEntry(theCarIndex).GetColor();
        theCarColor = masterJagColorToScheduleColorMap.get(color);
        // Check for no mapping
        if(theCarColor == null){
            String msg = "Color: " + color + " not in masterJagColorToScheduleColorMap. Will use first 10 characters.";
            //okDialog(msg);
            theLogger.info(msg);
            int maxLen = (color.length() > 10) ? 10 : color.length();
            String mapTo = color.substring(0, maxLen);
            theCarColor = mapTo;
        }
        
        strCarClass = theEntries.getEntryClass(theCarIndex);
        strCarUniqueDescription = theEntries.getEntry(theCarIndex).GetUniqueDescription();
        theEntryClassObject = theConcoursClasses.GetConcoursClassObject(strCarClass);
        if(theEntryClassObject == null){
            okDialog("ERROR: Concours Class " + strCarClass + " not found in UpdateJAByClassTableRowData");
            theLogger.info("ERROR: Concours Class " + strCarClass + " not found in UpdateJAByClassTableRowData");
            System.exit(-1);
        }
        
        theOwnerIndex =aJA.GetOwnerIndex();
       // theOwnerLastName = theEntries.getOwnerLast(theCarIndex); // this works too, provided the owner node numbers in Entries correspond to those in Owners... should always be so.
        theOwnerLastName = theOwners.getOwnerName(theOwnerIndex);
        
        theTimeslot =aJA.GetTimeslot();
        //Fill in the Class column 
        int column = -1;
        int headerListSize = aheaderList.size();
        for(int k =0; k < headerListSize; k++){
            if(aheaderList.get(k).classnode == 99) { // 99 is the "classnode" for the leftmost (Timeslot) column in aheaderList
                column = k;
                break;
            }
        }
        
        String nextStartTime = calcNextTimeslotStart(theTimeslot);
        String htmltimeslotTime =  "<html><p>" + nextStartTime  + "</p></html>";
        rowArray [theTimeslot] [column] = htmltimeslotTime; 
        
        intJudgeCount = aJA.GetJudgeCount();
        lstJudges = new ArrayList<>(aJA.GetJudgeIndicies()) ;
        // find the index in aheaderList, which will be the column index in rowArray
        column = -1;
        for(int k =0; k < aheaderList.size(); k++){
            if(aheaderList.get(k).classnode ==  theEntryClassObject.GetClassNode()) {
                column = k;
                break ;
            }
        }
        if(column == -1){
            okDialog("ERROR: Entry Class not found in UpdateJAByClassTableRowData()");
            theLogger.info("ERROR: Entry Class not found in UpdateJAByClassTableRowData()") ;
            System.exit(-1);
        }
 
            // find the index in aheaderList, which will be the column index in rowArray
            
            boolean ok = true; 
            if ((theTimeslot < 0)  || (theTimeslot >= numRows)){
                //Problem  HERE ??
                theLogger.info("Addressing error in UpdateJAByClassTableRowData. timeslot = " +  theTimeslot + " but should be  greater than 0 and less than " + numRows);
                ok = false;
            }
            if((column < 0) || (column >= numCols)){
                theLogger.info("Addressing error in UpdateJAByClassTableRowData. column = " +  column + " but should be greater than 0 and less than " + numCols);
                ok = false;
            }
            if(!ok){
                okDialog("ERROR: Problems in UpdateJAByClassTableRowData at timeslot " + theTimeslot );
                theLogger.info("ERROR: Problems in UpdateJAByClassTableRowData at timeslot " + theTimeslot );
                System.exit(-1);
                
            } else {
            // Note:
            // A cell should have only one entry, but errors in matching/coloring code can cause overloading
            // In that event ALL entries will be put in the cell and a warning written to stdErr
            //
            if(rowArray [theTimeslot] [column] == null){
                cellText = "";
            } else{
                cellText = (String)rowArray [theTimeslot] [column];
                theLogger.info("WARNING: Judging events overloaded in timeslot " + theTimeslot);
            }
            cellText = cellText
                    +  "<html>"
                    + "<p>E:" + theCarID + "</p>"
                    + "<p>O:" + theOwnerLastName + "</p>"
                    + "<p>C:" + theCarColor + "</p>"
                    + "<p>D:" + theCarDescription + "</p></html>";
                
                rowArray [theTimeslot] [column] = cellText;
                
            }
       }
    return  rowArray;

}


/* No longer used 7/30/2018
public Object [][] UpdateJACompressedTableRowData(List<JATableCompressedColHeader> aheaderList){
    JudgeAssignment aJA;
    Integer theCarIndex;
    String theCarID;
    String theCarDescription;
    String theCarColor;
    Integer theOwnerIndex;
    String  theOwnerLastName;
    //String aJudgeLastName;
    Integer theTimeslot;
    String cellText;
    //int intJudgeCount;
    //List<Integer> lstJudges ;
    List<Integer> uniqueTimeslots = new ArrayList<>();
    //String temp;
    int numRows;
    int numCols;
    int rowIndex;
    int colIndex;
    //Integer aJudgeIndex;
    // Begin by getting a count of the different timeslots used in JudgeAssignments
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theTimeslot =aJA.GetTimeslot();
        if(!uniqueTimeslots.contains(theTimeslot))   uniqueTimeslots.add(theTimeslot);
    }
    numRows = uniqueTimeslots.size();
    numCols = aheaderList.size(); // this is the number of Concours Classes + 1 for the timeslot time
    
    int [] nextAvailableTableCol = new int[numRows]; // gets incremented when an Entry is placed into the row
    for(int i = 0; i < numRows; i++){
        nextAvailableTableCol[i] = 1;
    }
    
    Object rowArray [][] = new Object [numRows][numCols] ;
    String strCarClass;
   String strCarUniqueDescription;
    ConcoursClass theEntryClassObject;
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        theCarIndex = aJA.GetCarIndex();
        theCarID = theEntries.getEntryID(theCarIndex);
        theCarDescription = theEntries.getEntry(theCarIndex).GetDescription();
        String color = theEntries.getEntry(theCarIndex).GetColor();
        theCarColor = masterJagColorToScheduleColorMap.get(color);
        // Check for no mapping
        if(theCarColor == null){
            String msg = "Color: " + color + " not in masterJagColorToScheduleColorMap. Will use first 10 characters.";
            //okDialog(msg);
            theLogger.info(msg);
            int maxLen = (color.length() > 10) ? 10 : color.length();
            String mapTo = color.substring(0, maxLen);
            theCarColor = mapTo;
        }
        
        strCarClass = theEntries.getEntryClass(theCarIndex);
        strCarUniqueDescription = theEntries.getEntry(theCarIndex).GetUniqueDescription();
        theEntryClassObject = theConcoursClasses.GetConcoursClassObject(strCarClass);
        if(theEntryClassObject == null){
            okDialog("ERROR: Concours Class " + strCarClass + " not found in UpdateJACompressedTableRowData");
            theLogger.info("ERROR: Concours Class " + strCarClass + " not found in UpdateJACompressedTableRowData");
            System.exit(-1);
        }
        
        theOwnerIndex =aJA.GetOwnerIndex();
       // theOwnerLastName = theEntries.getOwnerLast(theCarIndex); // this works too, provided the owner node numbers in Entries correspond to those in Owners... should always be so.
        theOwnerLastName = theOwners.getOwnerName(theOwnerIndex);
        //
        // Put timeslotTime into the first column of the rowIndex row
        //
        theTimeslot =aJA.GetTimeslot();
        rowIndex = theTimeslot;
        //
        //  It's only necessary to set the Timeslot time the first time the row is visited
        //  Note: nextAvailableTableCol[] was initialized to 1
        //
        if(nextAvailableTableCol[rowIndex] == 1){
            String nextStartTime = calcNextTimeslotStart(theTimeslot);
            String htmltimeslotTime =  "<html><p>" + nextStartTime + "</p></html>";

            rowArray [rowIndex] [0] = htmltimeslotTime; 
        }
        //
        // Now put the Entry description, , Owner, and Judges into the next available cell at rowIndex
        //
        //intJudgeCount = aJA.GetJudgeCount();
        //lstJudges = new ArrayList<>(aJA.GetJudgeIndicies()) ;
        boolean ok = true; 
        if ((theTimeslot < 0)  || (theTimeslot >= numRows)){

            theLogger.info("Addressing error in UpdateJACompressedTableRowData. timeslot = " +  theTimeslot + " but should be  greater than 0 and less than " + numRows);
            ok = false;
        }
        colIndex = nextAvailableTableCol[rowIndex]; // was initialized to 1
        if((colIndex < 0) || (colIndex >= numCols)){
            theLogger.info("Addressing error in UpdateJACompressedTableRowData. colIndex = " +  colIndex + " but should be greater than 0 and less than " + numCols);
            ok = false;
        }
        if(!ok){
            okDialog("ERROR: Problems in UpdateJACompressedRowData at timeslot " + theTimeslot);
            theLogger.info("ERROR: Problems in UpdateJACompressedRowData at timeslot " + theTimeslot );
            System.exit(-1);
        }
        else {
            // Note:
            // A cell should have only one entry, but errors in matching/coloring code can cause overloading
            // In that event ALL entries will be put in the cell and a warning written to stdErr
            //
            if(rowArray [rowIndex] [colIndex] == null){
                cellText = "";
            } else{
                cellText = (String)rowArray [rowIndex] [colIndex];
                theLogger.info("WARNING: Judging events overloaded in timeslot " + theTimeslot);
            }
            cellText = cellText
                    +  "<html>"
                    + "<p>E:" + theCarID + "</p>"
                    + "<p>O:" + theOwnerLastName + "</p>"
                    + "<p>C:" + theCarColor + "</p><p>D:" + theCarDescription + "</p>"; // </html> will be added later
            ConcoursClass cc = GetConcoursClassesObject().GetConcoursClassObject(strCarClass);
            //classheadertext = "<html><p>Class: " + strCarClass + "</p>";
            String strJudges = "";
            if(cc == null){
                theLogger.info("WARNING: No judges in for Entry in timeslot " + theTimeslot);
            } else {
                ArrayList<String> classjudgenames = cc.GetClassJudgeUniqueNames() ;
                for(String name : classjudgenames){
                    strJudges = strJudges + "<p>J: " + name + "</p>";
                }
            }
            cellText = cellText + strJudges + "</html>";
            rowArray [rowIndex] [colIndex] = cellText;
            nextAvailableTableCol[rowIndex]++; // bump up the index of next available cell at rowIndex 
        }
       }
    return  rowArray;

}
*/

/*
 *       Revised Compressed view to a merging of columns rather than simply closing row gaps.
 *
 *      In this new view each column will have all Entries for one or more specific JCNA Classes, as opposed to a hodgepodge of JCNA Classes
 *   Each class column is compared with all other columns, looking for opportunities to merge two columns. For example,
 *   the schedule:
 *    timeslot  C01     C02     C03     C04
 *    10:00     C01-1           C03-1   
 *    10:20             C02-1           C04-1
 *    10:40     C01-2           C03-2
 * 
 *    can be compressed by merging to:
 *  timeslot  C01, C02     C03,C04 
 *    10:00     C01-1      C03-1   
 *    10:20     C02-1      C04-1
 *    10:40     C01-2      C03-2
 *
 *  The returned object, RowHeader, has both the rowArray and the headerArray
*/
public RowHeader UpdateJACompressedTableRowDataAndHeader(Concours aConcours, List<JATableByClassColHeader> byClassheaderList){
    int numRows;
    int numCols;
    
    //  byClassheaderList is a List of Java Class JATableByClassColHeader
    Object [] [] rowArrayInitial  = UpdateJAByClassTableRowData(byClassheaderList);
    numRows = rowArrayInitial.length;
    numCols = byClassheaderList.size();
    for(int iRow = 0; iRow < numRows; iRow++){
        for(int iCol = 0; iCol < numCols; iCol++){
            if(rowArrayInitial [iRow] [iCol] == null) rowArrayInitial [iRow] [iCol] = "";
        }
    }
    
//        Now, compress it by merging columns. This will make each column include one or more JCNA classes.
//
//  First, initialize the header array to the byClass headers CONVERTED TO JATableMergeCompressedColHeader
    JATableMergeCompressedColHeader[] headerArrayInitial = new JATableMergeCompressedColHeader[byClassheaderList.size()];
    //Problem here. The following doesn't work
   // byClassheaderList.toArray(headerArrayInitial); // fill the headerArrayInitial
    // but this does...
    int ii = 0;
    for(JATableByClassColHeader classcolheader : byClassheaderList){
        //System.out.println(s);
        JATableMergeCompressedColHeader compressedcolheader = new JATableMergeCompressedColHeader(aConcours, classcolheader.getName());
        headerArrayInitial[ii] = compressedcolheader;
        ii++;
    }
    //
    // Now, compress it by merging columns. This will make each column include one or more JCNA classes.
    //
    //CustomTableModel ctm = new CustomTableModel(); // provides a function for column removal
    ArrayList<Integer> allCols = new ArrayList<>();
    ArrayList<Integer> emptyCols = new ArrayList<>();
    ArrayList<Integer> mergedCols = new ArrayList<>();
    ArrayList<Integer> unmergedCols = new ArrayList<>();
    ArrayList<Integer> unmergedNonemptyCols = new ArrayList<>();
    int lastMergedCol = -1;
    int jColOuter = 1;
    int jColInner;
    numCols = byClassheaderList.size();
    int[] classcount = new int[numCols];
    for(int i=0; i< numCols; i++) classcount[i] = 1;
    numRows = rowArrayInitial.length;
    while(jColOuter <= (numCols - 1)){
        allCols.add(jColOuter);
        if(!emptyCols.contains(jColOuter) ){
            jColInner = jColOuter + 1;
            while(jColInner <= numCols - 1){
                System.out.println("Compare cols " + jColOuter + " & " + jColInner);
                //if(jColInner == 6){
                //    System.out.println(jColOuter + " & " + jColInner);
                //}
                // Skip empty columns
                if(!emptyCols.contains(jColInner) ){
                    boolean merge = true;
                    for(int iRow = 0; iRow < numRows; iRow++){
                        Object cellText_o = rowArrayInitial [iRow] [jColOuter];
                        if(cellText_o == null) cellText_o = "";
                        Object cellText_i = rowArrayInitial [iRow] [jColInner];
                        if(cellText_i == null) cellText_i = "";
                        if((cellText_o != "" && cellText_i != "") || (classcount[jColInner]+classcount[jColOuter])> concoursCompression){
                            merge = false;
                            break;
                        }
                     }
                    
                    // merge == true means that in every row a merge can be done.
                    // However, the merge operation depends on which cells are occupied.
                    // In the following table, 0 means cell is blank, 1 means it has text in it
                    //   colOuter  colInner  merge  operation
                    //     0          0        1      No need to move the colInter to colOuter. No need to blank colInner. No need to change the header
                    //     0          1        1      Move colInter to colOuter, blank the colInner, ADD the collInner header to the colOuterHeader
                    //     1          0        1      No need to move the colInter to colOuter, no need to blank colInner, no need to add the collInner header to the colOuterHeader
                    //     1          1        0      The entire if(merge){} will be skipped since no merge is possible
                    //
                    if (merge){
                        System.out.println("\nMerge cols --------------------------------------" + jColOuter + " & " + jColInner);
                        for(int iRow = 0; iRow < numRows; iRow++){
                            // The merge requires resetting jColOuter value only if jColInner value is not blank
                            if(rowArrayInitial [iRow] [jColOuter] == ""){
                                if(rowArrayInitial [iRow] [jColInner] != "" ) {
                                    System.out.println("iRow: " + iRow + "Merging jColOuter: " + jColOuter + " and jColInner: " + jColInner);    
                                    // this is the 0 1 1 condition
                                    // Move the entry to the Outer column & blank the Inner column, add the colInner header to colOuter header, blank the colInner header
                                    rowArrayInitial [iRow] [jColOuter] = rowArrayInitial [iRow] [jColInner];
                                    rowArrayInitial [iRow] [jColInner] = "";
                                    lastMergedCol = jColInner;
                                    if(headerArrayInitial[jColOuter].toString().equals("")){
                                        System.out.println("\nHeader is blank at: " + jColOuter + " & " + jColInner);    
                                    }
                                    headerArrayInitial[jColOuter].mergeCompressedColHeaders(aConcours, headerArrayInitial[jColInner].classnames);
                                    classcount[jColOuter] = classcount[jColOuter] + classcount[jColInner];
                                    classcount[jColInner] = 0;
                                } else {
                                    // this is the 0 0 1 condition. No action required
                                }
                            } else {
                                if(rowArrayInitial [iRow] [jColInner] != ""){
                                    // this is the 1 1 0 condition, meaning merge = false and this point can't be reached
                                } else {
                                    // this is the 1 0 1 condition, meaning no action required
                                }
                            }
                        }
                        emptyCols.add(jColInner);
                        if(!mergedCols.contains(jColInner)) mergedCols.add(jColInner);
                        if(!mergedCols.contains(jColOuter)) mergedCols.add(jColOuter);
                     }
                 } else {
                    System.out.println("Inner col " + jColInner + " skipped because it's empty");
                }
                jColInner++;
            }
        } else{
            System.out.println("Outer col " + jColOuter + " skipped because it's empty");
        }
        jColOuter++;
    }
    
   
    unmergedCols.addAll(allCols);
    unmergedCols.removeAll(mergedCols);
    unmergedNonemptyCols.addAll(unmergedCols);
    unmergedNonemptyCols.removeAll(emptyCols);
    System.out.println("lastMergedCol: " + lastMergedCol);
    //int maxMergedCol = Collections.max(mergedCols);
    //int minEmptyCol = Collections.min(emptyCols);
    //int minUnmergedCol = Collections.min(unmergedCols);
    
// Now shove all empty columns left, so all emptys are at the right
    System.out.println("Remove " + emptyCols.size() + " empty columns");
    Collections.sort(emptyCols,  Collections.reverseOrder()); // this is important!!
    int lastCol = numCols-1;
    while(!emptyCols.isEmpty()){
        int col = emptyCols.get(0); 
        boolean atLastCol = col == lastCol;
        if(!atLastCol){
            // empty column is in the middle of the table... i.e., there's a non-empty column to its right
            for(int iRow = 0; iRow < numRows; iRow++){
                rowArrayInitial [iRow] [col] = rowArrayInitial [iRow] [col+1]; // move non-empty into empty
                rowArrayInitial [iRow] [col+1] = "";
            }
            headerArrayInitial[col] = headerArrayInitial[col+1];
            emptyCols.remove(0);
            emptyCols.add(col+1);
            Collections.sort(emptyCols,  Collections.reverseOrder()); // this is important too!!
        } else {
            // the empty column is the end column so just shrink the table and remove the col from emptyCols
            emptyCols.remove(0);
            numCols--;
            lastCol = numCols - 1;
        }
    }
    //
    // Copy merged cells into new rowArray & headerArray
    //
    System.out.println("Create new headerArray & rowArray with " + rowArrayInitial.length + " rows & " + numCols + " columns" );
    Object [] [] rowArray = new Object [numRows] [numCols];
    JATableMergeCompressedColHeader [] headerArray = new JATableMergeCompressedColHeader [numCols];
    for(int iRow = 0; iRow < numRows; iRow++){
        System.arraycopy(rowArrayInitial [iRow], 0, rowArray [iRow], 0, numCols);
    }
    System.arraycopy(headerArrayInitial, 0, headerArray, 0, numCols);
    
// Create  the table model
    //JACompressedMergeTableModel mdlSchedByClassMergeCompressed = new JACompressedMergeTableModel(
    //DefaultTableModel mdlSchedByClassMergeCompressed = new DefaultTableModel(
    RowHeader rh = new RowHeader(
        rowArray,
        headerArray
    );
    
   // return mdlSchedByClassMergeCompressed;
   return rh;
}

/*
  createCarColorMap() is used to convert to the somewhat lengthy and uncommon colors in the MasterJaguars to short, common colors
  needed in the printed Schedules
*/
public void createCarColorMap(){
    
    //ArrayList<MasterJaguar> lstMj = theMasterJaguars.masterJaguarList;
    for(MasterJaguar mj : theMasterJaguars.masterJaguarList){
        String dbColor = mj.getColor();
        if(dbColor.equalsIgnoreCase("brg") 
                || dbColor.equalsIgnoreCase("british racing green")
                || dbColor.equalsIgnoreCase("green british racing")
                || dbColor.equalsIgnoreCase("racing green")){
                    masterJagColorToScheduleColorMap.put(dbColor, "BRG");
        } else if(dbColor.equalsIgnoreCase("silver/black") 
                || dbColor.equalsIgnoreCase("black/silver")){
            masterJagColorToScheduleColorMap.put(dbColor, "Slvr/Blk");
        } else if(dbColor.equalsIgnoreCase("black/grey") 
                || dbColor.equalsIgnoreCase("grey/black")
                || dbColor.equalsIgnoreCase("gray/black")
                || dbColor.equalsIgnoreCase("black/gray")){
            masterJagColorToScheduleColorMap.put(dbColor, "Blk/Gry");
        } else if(dbColor.equalsIgnoreCase("black/maroon") 
                || dbColor.equalsIgnoreCase("maroon/black")){
            masterJagColorToScheduleColorMap.put(dbColor, "Blk/Mrn");
        } else if(dbColor.equalsIgnoreCase("black/tan") 
                || dbColor.equalsIgnoreCase("tan/black")){
            masterJagColorToScheduleColorMap.put(dbColor, "Blk/Tan");
        } else if(dbColor.toLowerCase().contains("red") 
                || dbColor.equalsIgnoreCase("cranberry") 
                || dbColor.equalsIgnoreCase("crimson")){
            masterJagColorToScheduleColorMap.put(dbColor, "Red");
        }  else if(dbColor.toLowerCase().contains("blue") 
                || dbColor.equalsIgnoreCase("sapphire") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Blue");
        } else if(dbColor.toLowerCase().contains("green") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Green");
        } else if(dbColor.toLowerCase().contains("white") 
                || dbColor.toLowerCase().contains("oyster")  ){
            masterJagColorToScheduleColorMap.put(dbColor, "White");
        } else if(dbColor.toLowerCase().contains("black") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Black");
        }  else if(dbColor.toLowerCase().contains("yellow") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Yellow");
        }  else if(dbColor.toLowerCase().contains("gold")
                || dbColor.toLowerCase().contains("golden") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Gold");
        }   else if(dbColor.toLowerCase().contains("cream") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Cream");
        }   else if(dbColor.toLowerCase().contains("yellow") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Yellow");
        }   else if(dbColor.toLowerCase().contains("gray")
                || dbColor.toLowerCase().contains("grey")
                || dbColor.toLowerCase().contains("gunmetal")
                || dbColor.toLowerCase().contains("slate")
                || dbColor.toLowerCase().contains("titanium")
                || dbColor.toLowerCase().contains("zircon")){
            masterJagColorToScheduleColorMap.put(dbColor, "Grey");
        }  else if(dbColor.toLowerCase().contains("burg") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Burg");
        }  else if(dbColor.toLowerCase().contains("primrose") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Prim");
        }  else if(dbColor.toLowerCase().contains("quartz") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Quartz");
        }  else if(dbColor.toLowerCase().contains("sable") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Sable");
        }   else if(dbColor.toLowerCase().contains("silver") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Silver");
        }  else if(dbColor.toLowerCase().contains("topaz") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Topaz");
        }  else if(dbColor.toLowerCase().contains("unknown") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Unk");
        }  else if(dbColor.toLowerCase().contains("wine") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Wine");
        }  else if(dbColor.toLowerCase().contains("antelope") 
                || dbColor.toLowerCase().contains("tan")
                || dbColor.toLowerCase().contains("fawn")
                || dbColor.toLowerCase().contains("sand")
                || dbColor.toLowerCase().contains("champagne")
                || dbColor.toLowerCase().contains("champaign")){
            masterJagColorToScheduleColorMap.put(dbColor, "Tan");
        }  else if(dbColor.toLowerCase().contains("beige") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Beige");
        }  else if(dbColor.toLowerCase().contains("maroon") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Maroon");
        }  else if(dbColor.toLowerCase().contains("platinum") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Plat");
        }  else if(dbColor.toLowerCase().contains("rose") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Rose");
        }  else if(dbColor.toLowerCase().contains("sea foam") ){
            masterJagColorToScheduleColorMap.put(dbColor, "SeaFoam");
        }  else if(dbColor.toLowerCase().contains("sea frost") ){
            masterJagColorToScheduleColorMap.put(dbColor, "SeaFrost");
        }  else if(dbColor.toLowerCase().contains("brown") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Brown");
        }  else if(dbColor.toLowerCase().contains("primer") ){
            masterJagColorToScheduleColorMap.put(dbColor, "Primer");
        }  else {
            int maxLen = (dbColor.length() > 10) ? 10 : dbColor.length();
            String mapTo = dbColor.substring(0, maxLen);
            System.out.println("[" + dbColor + "] maps to [" + mapTo + "]");
            masterJagColorToScheduleColorMap.put(dbColor, mapTo); // use up to 10 char of DB color string
        }
    }
    
}


public String getStartTime(){
    return strStartTime;
}

public String getLunchTime(){
    return strLunchTime;
}

public Integer getTimeslotInterval(){
    return timeslotInterval;
}
public Integer getSlotsBeforeLunch(){
    return slotsBeforeLunch;
}

/*
public void createTimeslotMap(String aStrStartTime, String aStrLunchTime, Integer aTimeslotInterval, Integer aSlotsBeforeLunch){
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    Date dateStartTime = null;
    Date dateLunchTime = null;
    Calendar cal = Calendar.getInstance();
    String timeslotTime; 
 
    try {
        dateStartTime = df.parse(aStrStartTime);
    } catch (ParseException ex) {
        theLogger.log(Level.SEVERE, null, ex);
    }
    try {
        dateLunchTime = df.parse(aStrLunchTime);
    } catch (ParseException ex) {
        theLogger.log(Level.SEVERE, null, ex);
    }
    
    for(int i = 0; i< theJudgeAssignments.GetConcoursJudgeAssignments().size(); i++){
        JudgeAssignment aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        int intTimeslot =aJA.GetTimeslot();
        if(intTimeslot <= (aSlotsBeforeLunch - 1)){
            cal.setTime(dateStartTime);
            cal.add(Calendar.MINUTE, intTimeslot*aTimeslotInterval);
        } else {
            cal.setTime(dateLunchTime);
            cal.add(Calendar.MINUTE, lunchInterval + (intTimeslot - aSlotsBeforeLunch)*aTimeslotInterval );
        }
        timeslotTime = df.format(cal.getTime());
        String strTimeslotTime =  timeslotTime;
        timeslotIndexToTimeStringMap.put(intTimeslot, timeslotTime);
    }

   
}

*/
public int getMaxTimeslotIndex(){
    return theJudgeAssignments.getMaxTimeslotIndex();
}

/*
  Returns the maximum number of Entries in a Timeslot
  Used to set the number of columns in tblSchedCompressed
*/

public int getMaxTimeslotEntries(){
    
    //This approach doesn't work because the TimeslotAssignments don't have member data set... 
 /*
    int max = 0;
    for(TimeslotAssignment tsa : theTimeslotAssignments.GetConcoursTimeslotAssignments()){
        int size =tsa.GetCarListSize();
        if( size> max){
            max = size; 
        }
    }
    */
    //
    //   So we do it the hard way... directly from theJudgeAssignments
    //   
    //  Create an list of used timeslot indicies
    //
    int maxTs = 0;
    JudgeAssignment aJA;
    int intTheTimeslot;
    boolean ret = false; 

    Set<Integer> setUsedTimeSlotIndicies = new HashSet();
    ArrayList<Integer> lstUsedTimeSlotIndicies = new ArrayList<>(); 
    
    for(int iJA = 0; iJA< theJudgeAssignments.GetConcoursJudgeAssignments().size(); iJA++){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(iJA);
        intTheTimeslot =aJA.GetTimeslot();
        if(intTheTimeslot > maxTs) maxTs = intTheTimeslot;
        if (setUsedTimeSlotIndicies.add(intTheTimeslot)) { // need set to ensure uniqueness
           ret = lstUsedTimeSlotIndicies.add(intTheTimeslot); // need array for convenient access
        }
    }
    int [] numEntriesInSlots = new int[lstUsedTimeSlotIndicies.size()];
    
    // Find the maximum number of Entries in any timeslot  
     // Java initializes int arrays to 0
     //for(int i = 0; i < lstUsedTimeSlotIndicies.size(); i++ ){
     //        numEntriesInSlots[i] = 0;
     //}
    for(int ts : lstUsedTimeSlotIndicies){
        for (Iterator<JudgeAssignment> it = theJudgeAssignments.GetConcoursJudgeAssignments().iterator(); it.hasNext();) {
            JudgeAssignment ja = it.next();
            intTheTimeslot = ja.GetTimeslot();
            if(intTheTimeslot == ts){
                numEntriesInSlots[intTheTimeslot]++;
            }
        }
    }
    Arrays.sort(numEntriesInSlots);
    int maxTSEntries = numEntriesInSlots[numEntriesInSlots.length-1];
    
    return maxTSEntries;
}



   // not used 9/14/2014
    public void AddJudgeToClass(Integer aJudgeIndex, ConcoursClass cc){
        cc.ClassJudgeIndicies.add(aJudgeIndex);
    }

public ArrayList<Integer> GetJudgeListForEntryIndex(Integer aEntryIndex){
    ArrayList<Integer> lstEntryJudges = new ArrayList<>();
    for(JudgeAssignment ja : theJudgeAssignments.GetConcoursJudgeAssignments()){
        if(ja.theCarIndex == aEntryIndex){
            lstEntryJudges = ja.GetJudgeIndicies();
            break;
        }
    }
    return lstEntryJudges;
}    
public Judge GetConcoursJudge(Integer aJudgeNode){
    return theJudges.GetConcoursJudge(aJudgeNode);
}  

public Judge GetConcoursJudge(String aUniqueName){
    return theJudges.GetConcoursJudge(aUniqueName);
}

public Judges GetConcoursJudgesObject(){
        return theJudges.GetConcoursJudgesObject();
}
public JCNAClasses GetJCNAClasses(){
    return theJCNAClasses;
} 
public JCNAClassRules_2 GetJCNAClassRules(){
    return theJCNAClassRules;
} 

public void RemoveJudgeAssignment(Integer aCarNode){
   // for(JudgeAssignment ja : theJudgeAssignments.GetConcoursJudgeAssignments()){
       // if(ja.GetCarIndex() == aCarNode) theJudgeAssignments.GetConcoursJudgeAssignments().remove(ja);
       // break;
   // }
    
    //This isn't wrking
    JudgeAssignment aJA;
    boolean found = false;
    Integer ci = 0;
    for(int i = 0; i<theJudgeAssignments.GetConcoursJudgeAssignments().size();i++ ){
        aJA = theJudgeAssignments.GetConcoursJudgeAssignments().get(i);
        ci = aJA.GetCarIndex();
        if(Objects.equals(ci, aCarNode)) {
            theJudgeAssignments.GetConcoursJudgeAssignments().remove(i);
            found = true;
            break;
        }
    }
    if( !found) {
        okDialog("Car node " + aCarNode + " not found in RemoveJudgeAssignment");
    } else {
    }

 
    
}

//////////////////////////////////////////////////////////////////////////////
//             Test driver
///////////////////////////////////////////////////////////////////////////////
/*
public static void main(String[] args) throws SQLException{
    Entries theConcourseEntries = new Entries();
    String strDBName = "SDJC2014";
    String strPath = "C:\\Users\\Ed Sowell\\Documents\\JOCBusiness\\Concours";
    Connection conn = null;
    try {
      Class.forName("org.sqlite.JDBC");
      String strConn;
      strConn = "jdbc:sqlite:" + strPath + "\\" + strDBName + ".db";
      conn = DriverManager.getConnection(strConn);
    } catch ( ClassNotFoundException | SQLException e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    theLogger.info("Opened database " + strDBName + ".db successfully");
    LoadEntriesDB(conn);
    conn.close(); 
}    
*/


}
    

 









