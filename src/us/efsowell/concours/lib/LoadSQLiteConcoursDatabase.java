/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okCancelDialog;
import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.ConcoursGUI.theConcours;
import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Calendar.LONG;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import static us.efsowell.concours.lib.MyJavaUtils.checkJCNAClassDivisionAndName;

/**
 *
 * @author Ed Sowell
 */
public class LoadSQLiteConcoursDatabase {
    

    public LoadSQLiteConcoursDatabase(){
    }

    /*
     *  Check structure of CSV file
    */
    public static List<String> checkCSVStructure(String strCSVFileFullPath, int numFields){
        String [] nextLine;
        List<String> res = new ArrayList<>();
        
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(strCSVFileFullPath));
        } catch (FileNotFoundException ex) {
            theConcours.GetLogger().info("FileNotFoundException in checkCSVStructure");
            okDialog("FileNotFoundException in checkCSVStructure");
            try {
                if(reader != null) reader.close();
            } catch (IOException ex1) {
                //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex1);
                theConcours.GetLogger().info("IOException while closing reader in checkCSVStructure");
            }
            return res;
        }
        
        char delimiter = ',';
        int iLine;
        iLine = 0;
        try {
            while ((nextLine = reader.readNext()) != null) {
                String theLine = "";
                int n = nextLine.length;
                if( n != numFields){
                    //okDialog("Number of delimiters in line " + iLine + " is " + n + " Should be " + numFields);
                    for(int i = 0;  i<n; i++){
                        if(i==0){
                            theLine = theLine + nextLine[i];
                        } else {
                            theLine = theLine + ", " + nextLine[i];
                        }
                    }
                    int numCommas = MyJavaUtils.countOccurrences(theLine, delimiter);
                    theLine = "\nLine " + iLine + " has " + numCommas + " commas. Should be " + (numFields-1) + " :" + theLine;
                    res.add(theLine);
                }
                iLine++;
            }
        } catch (IOException ex) {
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            String msg = "IOException while reading file in checkCSVStructure";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
            return res;
        }
        return res;
    }
   
    public static List<String> checkCSVClassNameForm(String strCSVFileFullPath, int numFields){
        String [] nextLine;
        List<String> res = new ArrayList<>();
        
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(strCSVFileFullPath));
        } catch (FileNotFoundException ex) {
            theConcours.GetLogger().info("FileNotFoundException in checkCSVClassNameForm()");
            okDialog("FileNotFoundException in checkCSVStructure in checkCSVClassNameForm()");
            try {
                if(reader != null) reader.close();
            } catch (IOException ex1) {
                //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex1);
                okDialog("FileNotFoundException in checkCSVStructure in checkCSVClassNameForm()");
                theConcours.GetLogger().info("IOException while closing reader in checkCSVClassNameForm()");
            }
            return res;
        }
        boolean ok;
       // char delimiter = ',';
        int iLine;
        iLine = 0;
        try {
            while ((nextLine = reader.readNext()) != null) {
                if(iLine > 0){
                    String theLine = "";
                    ok = checkJCNAClassDivisionAndName(nextLine[0], nextLine[1]);
                    if(!ok){
                        int n = nextLine.length;
                        //if( n != numFields){
                           /*for(int i = 0;  i<n; i++){
                                if(i==0){
                                    theLine = theLine + nextLine[i];
                                } else {
                                    theLine = theLine + ", " + nextLine[i];
                                }
                            }
                        */
                            theLine = "Row " + iLine + ": " + nextLine[0] + ", " + nextLine[1];
                            res.add(theLine);
                        //}
                    }
                }
                iLine++;
            }
        } catch (IOException ex) {
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            String msg = "IOException while reading file in checkCSVClassNameForm";
            okDialog(msg);
            theConcours.GetLogger().info("IOException while reading file in checkCSVClassNameForm");
        }
        return res;
    }

    //
    //  getLinesFromCSV gets entire CSV file as a list of lines for easy access
    //
    public static List<String[]> getLinesFromCSV(String strCSVFileFullPath){
        List<String[]> res = new ArrayList<>();
        try {
            String [] nextLine;
            CSVReader reader = null;
            try {
                reader = new CSVReader(new FileReader(strCSVFileFullPath));
            } catch (FileNotFoundException ex) {
                String msg = "FileNotFoundException in getLinesFromCSV";
                theConcours.GetLogger().info(msg);
                okDialog(msg);
                try {
                    if(reader != null) reader.close();
                } catch (IOException ex1) {
                    //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex1
                    msg = "IOException while closing reader in getLinesFromCSV";
                    okDialog(msg);
                    theConcours.GetLogger().log(Level.SEVERE, msg, ex1);
                    return res;
                }
            }
            int iLine;
            iLine = 0;
            while ((nextLine = reader.readNext()) != null) {
                if(iLine > 0){
                    res.add(nextLine);
                }
                iLine++;
            }
            return res;
        } catch (IOException ex) {
            String msg = "IOException while closing reader in getLinesFromCSV";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
       }
        return res;
    }
    
    public static List<String> getJCNAClassFieldFromCSV(String strCSVFileFullPath, int fieldIndex){
        String [] nextLine;
        List<String> res = new ArrayList<>();
        
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(strCSVFileFullPath));
        } catch (FileNotFoundException ex) {
            theConcours.GetLogger().info("FileNotFoundException in getJCNAClassNamesFromCSV");
            okDialog("FileNotFoundException in getJCNAClassNamesFromCSV");
            try {
                if(reader != null) reader.close();
            } catch (IOException ex1) {
                //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex1);
                theConcours.GetLogger().info("IOException while closing reader in getJCNAClassNamesFromCSV");
            }
            return res;
        }
        
        int iLine;
        iLine = 0;
        try {
            while ((nextLine = reader.readNext()) != null) {
                if(iLine > 0){
                    res.add(nextLine[fieldIndex]);
                }
                iLine++;
            }
        } catch (IOException ex) {
            theConcours.GetLogger().info("IOException while reading file in getJCNAClassNamesFromCSV");
        }
        return res;
    }
   
    
    
  /*   Rewrite to coordinate JCNA Classes & JCNA Class Rules tables.
   *   Note that since the Rules table references the Classes table, the Rules table has to be deleted FIRST.
   *   But, then the Classes table has to be loaded from CSV, followed by loading the Rules from CSV.
   * 
   *   Modified 5/2/2017 to allow loading to an argument table name.
   */
  
  public static boolean LoadJCNAClassesTableFromCSV(Connection aConn, String aTableName, String strCSVFileFullPath, Concours aConcours) {
    String [] nextLine;
    String strDivision;
    String strClass;
    String strNotes;
    String strDescription;
    String strJudgeAssignGroup;
    String strModelYearLookup; // "yes"  if JCNAClass can be looked up from Model & Year using Rules (normal), 
                               //"no" if JCNAClass must be selected when car is added as an Entry (Preservation and Special)
    
    //ArrayList<JCNAClass>  theClassList = new ArrayList<>(); // This is where the Classes read from CSV are placed. If all goes well
                                                             // they will be used to replace the JCNAClasses in memory
        try {
            //    
//    NOTE: The "drop table if exists" must be done before this is called. It is now done in the calling ActionPerformed()
        aConn.setAutoCommit(false);
        String qCreatetable = "create table " + aTableName + " ('ID'	INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL, 'class' TEXT NOT NULL UNIQUE, 'description' TEXT NOT NULL, 'note'	TEXT NOT NULL, 'judgeassigngroup' TEXT NOT NULL, 'mylookup' TEXT NUT NULL, 'node' INTEGER NOT NULL);";
        Statement statCreatetable = null;
            statCreatetable = aConn.createStatement();
            statCreatetable.execute(qCreatetable);  
            aConn.commit();
            statCreatetable.close();
        } catch (SQLException ex){
            String msg = "SQLException in LoadJCNAClassesTableFromCSV while closing statCreatetable";
            okDialog(msg);
            aConcours.GetLogger().log(Level.INFO, "SQLException in LoadJCNAClassesTableFromCSV while closing statCreatetable"); 
            return false;
        }
    //
    // Now we can do the reads...
    //
     CSVReader reader = null;
    // String JCNAClassesCSV = strPath + "\\" + strCSVFileName ; 
    int iLine;
    String qInserttable;
    try {
        reader = new CSVReader(new FileReader(strCSVFileFullPath ));
        qInserttable = "insert into " + aTableName + " ('division', 'class', 'description', 'note', 'judgeassigngroup', 'mylookup','node') values ( ?,  ?, ?, ?,  ?,  ?, ?);";
        PreparedStatement pstatInserttable = aConn.prepareStatement(qInserttable);
        iLine = 0;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            // System.out.println(nextLine[0] + nextLine[1] );
            if (iLine > 0){
                strDivision = nextLine[0].trim();
                strClass = nextLine[1].trim();
                strDescription= nextLine[2].trim();
                strNotes= nextLine[3];
                strJudgeAssignGroup = nextLine[4];
                strModelYearLookup = nextLine[5]; // added 7/23/2017
                pstatInserttable.setString(1, strDivision); // note that the comma seems not to be a problem
                pstatInserttable.setString(2,strClass); 
                pstatInserttable.setString(3,strDescription); 
                pstatInserttable.setString(4,strNotes); 
                pstatInserttable.setString(5,strJudgeAssignGroup); 
                pstatInserttable.setString(6,strModelYearLookup);  // added 7/23/2017
                pstatInserttable.setInt(7,iLine); 
                pstatInserttable.addBatch(); 
            }
            iLine++;
        }
        pstatInserttable.executeBatch();
        aConn.setAutoCommit(true); 
        pstatInserttable.close();
        reader.close();
    }    catch (FileNotFoundException ex) {
            okDialog("FileNotFoundException in LoadJCNAClassesTableFromCSV");
            theConcours.GetLogger().info("FileNotFoundException in LoadJCNAClassesTableFromCSV");
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
            return false;
    }   catch (IOException ex) {
            okDialog("IOException in LoadJCNAClassesTableFromCSV");
            theConcours.GetLogger().info("IOException in LoadJCNAClassesTableFromCSV");
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
            return false;
    } catch (SQLException ex) {
            okDialog("SQLException in LoadJCNAClassesTableFromCSV");
            theConcours.GetLogger().info("SQLException in LoadJCNAClassesTableFromCSV");
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
    }
    try {
        if(reader != null) reader.close();
    } catch (IOException ex) {
        okDialog("IOException upon CVSReader close()in LoadJCNAClassesTableFromCSV");
        theConcours.GetLogger().info("IOException in LoadJCNAClassesTableFromCSV");
        theConcours.GetLogger().log(Level.SEVERE, null, ex);
        //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        return false;
    }
    return true;
  }
  
  
 /*                 Trying to fix a locked table issue.
                    Gave up on this approach... too much I don't understand about DbUtils.
                    Fixed the locked table by closing statements ets in...
               
  */
  public static boolean LoadJCNAClassesTableFromCSVDbUtils(Connection aConn, String aTableName, int numDBCols, String strCSVFileFullPath, Concours aConcours){
    String msg;
    String [] nextLine;
    String strDivision;
    String strClass;
    String strNotes;
    String strDescription;
    String strJudgeAssignGroup;
    String strModelYearLookup;  // "yes"  if JCNAClass can be looked up from Model & Year using Rules (normal),
    //"no" if JCNAClass must be selected when car is added as an Entry (Preservation and Special)
    // Read the data into a 2-d array...
    //
    CSVReader reader = null;
    // String JCNAClassesCSV = strPath + "\\" + strCSVFileName ;
    int row;
        

    Object[][] transposedTable = new Object[numDBCols][];
    try {
        row = 0;
        reader = new CSVReader(new FileReader(strCSVFileFullPath ));
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            // System.out.println(nextLine[0] + nextLine[1] );
            if (row > 0){
                strDivision = nextLine[0].trim();
                strClass = nextLine[1].trim();
                strDescription= nextLine[2].trim();
                strNotes= nextLine[3];
                strJudgeAssignGroup = nextLine[4];
                strModelYearLookup = nextLine[5]; // added 7/23/2017
                transposedTable[0][row] = strDivision;
                transposedTable[1][row] = strClass;
                transposedTable[2][row] = strDescription;
                transposedTable[3][row] = strNotes;
                transposedTable[4][row] = strJudgeAssignGroup;
                transposedTable[5][row] = strModelYearLookup;
                transposedTable[6][row] = (Integer)row;
            }
            row++;
        }
        // transpose to get what is expected by DbUtils
        int numRows = row;
        Object[][] table = new Object[row][numDBCols];
        for(int r = 0; r<numRows; r++ ){
            for(int c = 0; c<numDBCols; c++){
                table[r][c] = transposedTable[c][r];
            }
        }
        
    } catch (FileNotFoundException ex) {
            msg = "FileNotFoundException in LoadJCNAClassesTableFromCSVDbUtils";
            okDialog(msg);
            aConcours.GetLogger().log(Level.SEVERE, msg, ex);
    }   catch (IOException ex) {
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            msg = "IOException in LoadJCNAClassesTableFromCSVDbUtils";
            okDialog(msg);
            aConcours.GetLogger().log(Level.SEVERE, msg, ex);
    }
        
    String qInserttable;
    qInserttable = "insert into " + aTableName + " (division, class, description, note, judgeassigngroup, mylookup, node) values ( ?,  ?, ?, ?,  ?,  ?, ?)";
    // PreparedStatement pstatInserttable = aConn.prepareStatement(qInserttable);
    
    QueryRunner run = new QueryRunner();

    return true;
    }

  public static boolean LoadJCNAClassRulesFromCSV(Connection aConn,  String aClassesTableName, String aRulesTableName, String strAbsPathCSV, Concours aConcours) {
    String [] nextLine = null;
    String strDivision = null;
    String strClass = null; 
    String strModel = null;
    String strDesc1 = null;
    String strDesc2 = null;
    String strDesc3 = null;
    Integer fy = null;
    Integer ly = null;
    Long ID = null;
    int numFields = 10;

//    
//    NOTE: The "drop table if exists" is now done in ActionPerformed()
//    
    
   // ArrayList<JCNAClassRule>  theRulesList = new ArrayList<>(); // This is where the rules read from CSV are placed. If all goes well
                                                                // they will be used to replace aConcours.GetJCNAClassRules().getJCNAClassRules();
    try {
        aConn.setAutoCommit(false);
    } catch (SQLException ex) {
        okDialog("SQLException in LoadJCNAClassRulesFromCSV trying to setAutoCommit to false");
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        theConcours.GetLogger().info(LoadSQLiteConcoursDatabase.class.getName());
        theConcours.GetLogger().log(Level.SEVERE, null, ex);
        return false;
    }
    
    // Note: Instead of indicies given in the CSV file we will use the ID field names associated with DB table rows
    //RuleIndex	Division	ClassName	ClassIndex	ModelName	Descriptor_1	Descriptor_2	Descriptor_3	FirstYear	LastYear
    // 0         1                  2               3              4                5              6                 7             8               9
    
    //String q = "create table JCNAClassRules ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL,  'classname' TEXT NOT NULL, 'class_id' INTEGER NOT NULL, 'modelname' TEXT NOT NULL, 'descriptor_1' TEXT, 'descriptor_2' TEXT, 'descriptor_3' TEXT, 'firstyear' INTEGER NOT NULL, 'lastyear' INTEGER NOT NULL, FOREIGN KEY (class_id) REFERENCES JCNAClasses (ID));";
    //try {
   //     stat = aConn.createStatement();
   //     stat.executeUpdate(q);
   //     aConn.commit();
   // } catch (SQLException ex) {
        //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
   //     theConcours.GetLogger().info("SQLException in LoadJCNAClassRulesFromCSV while trying create JCNAClassRules table");
   //     okDialog("SQLException in LoadJCNAClassRulesFromCSV while trying create JCNAClassRules table JCNAClassRules");
   //     return false;
  //  }
    
    // Note: Instead of indicies given in the CSV file we will use the ID field names associated with DB table rows
    //RuleIndex	Division	ClassName	ClassIndex	ModelName	Descriptor_1	Descriptor_2	Descriptor_3	FirstYear	LastYear
    // 0         1                  2               3              4                5              6                 7             8               9
    //String qCreatetable = "create ? JCNAClassRules ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL,  'classname' TEXT NOT NULL, 'class_id' INTEGER NOT NULL, 'modelname' TEXT NOT NULL, 'descriptor_1' TEXT, 'descriptor_2' TEXT, 'descriptor_3' TEXT, 'firstyear' INTEGER NOT NULL, 'lastyear' INTEGER NOT NULL, FOREIGN KEY (class_id) REFERENCES JCNAClasses (ID));";
    String qCreatetable = "create table " + aRulesTableName + " ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL,  'classname' TEXT NOT NULL, 'class_id' INTEGER NOT NULL, 'modelname' TEXT NOT NULL, 'descriptor_1' TEXT, 'descriptor_2' TEXT, 'descriptor_3' TEXT, 'firstyear' INTEGER NOT NULL, 'lastyear' INTEGER NOT NULL, FOREIGN KEY (class_id) REFERENCES " + aClassesTableName + " (ID));";
    Statement statCreatetable = null;
        try {
            statCreatetable = aConn.createStatement();
            //statCreatetable.setString(1, aTableName);
            statCreatetable.execute(qCreatetable);
            aConn.commit();
        } catch (SQLException ex) {
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            aConcours.GetLogger().log(Level.INFO, "SQLException in LoadJCNAClassRulesFromCSV while creating table ", aRulesTableName);
            aConcours.GetLogger().log(Level.SEVERE, null, ex);
            okDialog("SQLException in LoadJCNAClassRulesFromCSV while creating table " + aRulesTableName);
            return false;
        }
        try {
            statCreatetable.close();
        } catch (SQLException ex) {
           // Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            aConcours.GetLogger().log(Level.INFO, "SQLException in LoadJCNAClassRulesFromCSV while closing statCreatetable");
            aConcours.GetLogger().log(Level.SEVERE, null, ex);
            okDialog("SQLException in LoadJCNAClassRulesFromCSV while closing statCreatetable");
            return false;
        }
         
    CSVReader reader = null;
    int iLine;
    //
    // Check structure of file
    //
    List<String> list;
        list = checkCSVStructure(strAbsPathCSV, numFields);
    if(!list.isEmpty()){
        okDialog("These lines in " + strAbsPathCSV + " have wrong number of fields. Cause is too many or too few commas. " +  list );
        aConcours.GetLogger().info("These lines in " + strAbsPathCSV + " have wrong number of fields. Cause is too many or too few commas. " +  list);
        return false;
    }

   /* List<String> list2;
        list2 = checkCSVStructure(strAbsPathCSV, numFields);
    if(!list.isEmpty()){
        okDialog("These lines in " + strAbsPathCSV + " have wrong number of fields. Cause is too many or too few commas. " +  list );
        aConcours.GetLogger().info("These lines in " + strAbsPathCSV + " have wrong number of fields. Cause is too many or too few commas. " +  list);
        return false;
    }
    */
    
    // Now do the inserts
    Statement statGetID = null;
    PreparedStatement pstatInserttable = null;
    String qInserttable;
    String qGetID;
    try {
        reader = new CSVReader(new FileReader(strAbsPathCSV));
        qInserttable = "insert into " + aRulesTableName + " ('division', 'classname', 'class_id', 'modelname', 'descriptor_1', 'descriptor_2',  'descriptor_3', 'firstyear', 'lastyear') values ( ?,  ?,  ?,  ?, ?, ?, ?, ?, ?);";
        pstatInserttable = aConn.prepareStatement(qInserttable); 
        statGetID = aConn.createStatement(); 
        iLine = 0;
        aConn.setAutoCommit(false); 
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            ResultSet rs;
            rs = null;
            if (iLine > 0){
                // nextLine[0] not needed since the ID is autoincremented
                //IDFromFile = nextLine[0].trim();
                strDivision = nextLine[1].trim();
                strClass = nextLine[2].trim();
                // nextLine[3] not used since we get it from JCNAClasses
                strModel = nextLine[4].trim();
                strDesc1 = nextLine[5].trim();
                strDesc2 = nextLine[6].trim();
                strDesc3 = nextLine[7].trim();
                try{
                    fy = Integer.parseInt(nextLine[8].trim());
                    ly = Integer.parseInt(nextLine[9].trim());
                } catch (NumberFormatException ex){
                    String msg = "First year or Last year  in " + strAbsPathCSV + " is not an integer";
                    theConcours.GetLogger().log(Level.SEVERE, msg, ex);
                    okDialog(msg);
                    return false;
                }
                // get ID from JCNAClasses table
                qGetID =  "select ID from " + aClassesTableName + " where  class like '" +  strClass + "';";
                ID = null;
                rs =  statGetID.executeQuery(qGetID);
                 if(rs.next()) {
                    ID  = rs.getLong(1);
                 }
                 else{
                    theConcours.GetLogger().info("Failed to find JCNA Class " + strClass + " in " + aClassesTableName + " table");
                    okDialog("Failed to find JCNA Class " + strClass + " in " + aClassesTableName + " table");
                    return false;
                 }
                 if(rs.next()){
                    theConcours.GetLogger().info("JCNA Class " + strClass + " occurs more than once in " +  aClassesTableName + " table");
                    okDialog("JCNA Class " + strClass + " occurs more than once in " +  aClassesTableName + " table");
                    return false;
                }
                //pstatInserttable.setString(1, aTableName);
                pstatInserttable.setString(1, strDivision); 
                pstatInserttable.setString(2,strClass); 
                pstatInserttable.setLong(3,ID); 
                pstatInserttable.setString(4,strModel); 
                pstatInserttable.setString(5,strDesc1); 
                pstatInserttable.setString(6,strDesc2); 
                pstatInserttable.setString(7,strDesc3); 
                pstatInserttable.setInt(8,fy); 
                pstatInserttable.setInt(9,ly); 
                pstatInserttable.addBatch(); 
                //theRulesList.add(new JCNAClassRule(strDivision, strClass, ID, strModel, strDesc1, strDesc2, strDesc3, fy, ly));
                }
                if(rs !=null) rs.close();
                iLine++;
        }
        pstatInserttable.executeBatch();
        aConn.setAutoCommit(true);                                             //Should this be done here????????????????????????????
        statGetID.close();
        pstatInserttable.close();
        if(reader != null) reader.close();
    }  catch (FileNotFoundException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
            okDialog("File Not Found Exception. New rules not loaded");
            try {
                aConn.setAutoCommit(true);
            } catch (SQLException ex1) {
                theConcours.GetLogger().log(Level.SEVERE, null, ex);
                okDialog("SQLException aConn.setAutoCommit(true)");
                //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex1);
            }
            try {
                reader.close();
            } catch (IOException ex1) {
                theConcours.GetLogger().log(Level.SEVERE, null, ex);
                okDialog("SQLException Closing reader");
                //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return false;
        }   catch (IOException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
            okDialog("IO Exception. New rules not loaded");
            return false;
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
            okDialog("SQL exception. New rules not loaded");
            theConcours.GetLogger().log(Level.SEVERE, "SQL exception. New rules not loaded", ex);
            return false;
        }
        // Don't want to do this now. Memory update will be done AFTER we get a good read of the new rules
        //aConcours.GetJCNAClassRules().getJCNAClassRules().clear();
        //for(JCNAClassRule jcr : theRulesList){
        //     aConcours.GetJCNAClassRules().getJCNAClassRules().add(jcr);
        //}
        //
    return true;
  }


  
  /*
   *   This copy will be used to filter the rule set with the selection criteria, year, model,etc.
   *   This is no longer used. The JCNA Class Rules are now stored in memory and used for filtering needed for AddMasterJaguar_2 
   */
  public static void CopyJCNAClassRules(Connection aConn, String aJCNAClassesTableName, String aJCNAClassRulesTableName) throws SQLException{
    String q;
    Statement stat1 = aConn.createStatement();
    stat1.executeUpdate("drop table if exists " + aJCNAClassRulesTableName + ";");
    // Note: Instead of indicies given in the CVS file we will use the ID field names associated with DB table rows
    //RuleIndex	Division	ClassName	ClassIndex	ModelName	Descriptor_1	Descriptor_2	Descriptor_3	FirstYear	LastYear
    // 0         1                  2               3              4                5              6                 7             8               9
    q = "create table " + aJCNAClassRulesTableName  + " ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL,  'classname' TEXT NOT NULL, 'class_id' INTEGER NOT NULL, 'modelname' TEXT NOT NULL, 'descriptor_1' TEXT, 'descriptor_2' TEXT, 'descriptor_3' TEXT, 'firstyear' INTEGER NOT NULL, 'lastyear' INTEGER NOT NULL, FOREIGN KEY (class_id) REFERENCES " + aJCNAClassesTableName + " (ID));";
    stat1.executeUpdate(q);
    stat1.close();
    
    Statement stat2;
    stat2 = aConn.createStatement();
    PreparedStatement prep = aConn.prepareStatement( "insert into " + aJCNAClassRulesTableName + " ('division', 'classname', 'class_id', 'modelname', 'descriptor_1', 'descriptor_2',  'descriptor_3', 'firstyear', 'lastyear') values ( ?,  ?,  ?,  ?, ?, ?, ?, ?, ?);"); 
    q = "select * from JCNAClassRules;";
    ResultSet rs = stat2.executeQuery(q);
    aConn.setAutoCommit(false);
    while(rs.next()){
        String strDivision = rs.getString("division");
        String strClass = rs.getString("classname");
        Long ID = rs.getLong("class_id");
        String strModel = rs.getString("modelname");
        String strDesc1 = rs.getString("descriptor_1");
        String strDesc2 = rs.getString("descriptor_2");
        String strDesc3 = rs.getString("descriptor_3");
        Integer fy = rs.getInt("firstyear");
        Integer ly = rs.getInt("lastyear");
        prep.setString(1, strDivision); 
        prep.setString(2,strClass); 
        prep.setLong(3,ID); 
        prep.setString(4,strModel); 
        prep.setString(5,strDesc1); 
        prep.setString(6,strDesc2); 
        prep.setString(7,strDesc3); 
        prep.setInt(8,fy); 
        prep.setInt(9,ly); 
        prep.addBatch(); 
    }
    prep.executeBatch();
    aConn.commit();
    aConn.setAutoCommit(true);
    rs.close();
    prep.close();
    stat2.close();
  } 
  
  
  public static void WriteJCNAClassesTableFromMemToDB(Connection aConn, String aTableName){
        try {
            String strDivision;
            String strClass;
            String strNotes;
            String strDescription;
            String strJudgeAssignGroup;
            String strModelYearLookup;
            Statement stat = aConn.createStatement();
            // Do the latter outside this function so the JCNAClassRules table can be dropped before the JCNAClasses to avoid Foreign key exception
            //stat.executeUpdate("drop table if exists JCNAClasses;");
            String q = "create table " + aTableName + "('ID'	INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL, 'class' TEXT NOT NULL UNIQUE, 'description' TEXT NOT NULL, 'note'	TEXT NOT NULL, 'judgeassigngroup' TEXT NOT NULL, 'mylookup' TEXT NOT NULL, 'node' INTEGER NOT NULL);";
            stat.executeUpdate(q);
            stat.close();
            PreparedStatement prep = aConn.prepareStatement( "insert into " + aTableName + " ('division', 'class', 'description', 'note', 'judgeassigngroup', 'mylookup', 'node') values ( ?, ?,  ?,  ?,  ?,  ?, ?);");
            aConn.setAutoCommit(false);
            for(JCNAClass c : theConcours.GetJCNAClasses().GetJCNAClasses()){
                strDivision = c.getDivision();
                strClass = c.getName();
                strDescription = c.getDescription();
                strNotes = c.getNotes();
                strModelYearLookup = c.getModelYearLookup();
                int node = c.getNode();
                strJudgeAssignGroup = c.getJudgeAssignGroup();
                prep.setString(1, strDivision); // note that the comma seems not to be a problem
                prep.setString(2,strClass);
                prep.setString(3,strDescription);
                prep.setString(4,strNotes);
                prep.setString(5,strJudgeAssignGroup);
                prep.setString(6,strModelYearLookup);
                prep.setInt(7,node);
                prep.addBatch();
            }
            
            prep.executeBatch();
            aConn.setAutoCommit(true);
            prep.close();
        } catch (SQLException ex) {
            okDialog("SQLException in WriteJCNAClassesTableFromMemToDB");
            theConcours.GetLogger().info("SQLException in WriteJCNAClassesTableFromMemToDB");
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

  }
  
  public static void WriteJCNAClassRulesTableFromMemToDB(Connection aConn, String aClassesTableName, String aRulesTableName, Concours aConcours){
        try {
            String division;
            String classname;
            Long class_id;
            String modelname;
            String descriptor_1;
            String descriptor_2;
            String descriptor_3;
            Integer firstyear;
            Integer lastyear;
            Statement stat = aConn.createStatement();
            // Do the latter outside this function so the JCNAClassRules table can be dropped before the JCNAClasses to avoid Foreign key exception
            
           // stat.executeUpdate("drop table if exists JCNAClassRules;");
            //RuleIndex	Division	ClassName	ClassIndex	ModelName	Descriptor_1	Descriptor_2	Descriptor_3	FirstYear	LastYear
            // 0         1                  2               3              4                5              6                 7             8               9
            String q = "create table " + aRulesTableName + " ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL,  'classname' TEXT NOT NULL, 'class_id' INTEGER NOT NULL, 'modelname' TEXT NOT NULL, 'descriptor_1' TEXT, 'descriptor_2' TEXT, 'descriptor_3' TEXT, 'firstyear' INTEGER NOT NULL, 'lastyear' INTEGER NOT NULL, FOREIGN KEY (class_id) REFERENCES " + aClassesTableName + "(ID));";
            stat.executeUpdate(q);
            stat.close();
            PreparedStatement prep = aConn.prepareStatement( "insert into " + aRulesTableName + " ('division', 'classname', 'class_id', 'modelname', 'descriptor_1', 'descriptor_2', 'descriptor_3', 'firstyear', 'lastyear') values ( ?,  ?,  ?,  ?,  ?, ?, ?, ?, ?);");
            aConn.setAutoCommit(false);
            for(JCNAClassRule cr : theConcours.GetJCNAClassRules().jcnaclassrules){
                division = cr.getDivision();
                classname = cr.getClassName();
                class_id = cr.getClass_id();
                modelname = cr.getModelName();
                descriptor_1 = cr.getDescriptor_1();
                descriptor_2 = cr.getDescriptor_2();
                descriptor_3 = cr.getDescriptor_3();
                firstyear = cr.getFirstyear();
                lastyear = cr.getLastyear();
                prep.setString(1, division); 
                prep.setString(2, classname);
                prep.setLong(3, class_id);
                prep.setString(4, modelname);
                prep.setString(5, descriptor_1);
                prep.setString(6, descriptor_2);
                prep.setString(7, descriptor_3);
                prep.setInt(8, firstyear);
                prep.setInt(9, lastyear);
                prep.addBatch();
            }
            
            prep.executeBatch();
            aConn.setAutoCommit(true);
            prep.close();
            stat.close();
        } catch (SQLException ex) {
            okDialog("SQL exception in WriteJCNAClassRulesTableFromMemToDB");
            aConcours.GetLogger().info("SQL exception in WriteJCNAClassRulesTableFromMemToDB");
            aConcours.GetLogger().log(Level.SEVERE, null, ex);
        }

  }
  
  /*
  //   THIS DOESN'T WORK WITH THE VERSION OF SQLITE DRIVER I HAVE...   CAN'T FIND 3.7.11 FOR NETBEANS
  //
  public static void LoadJCNAClassesTableAlt(Connection aConn, String strPath) throws SQLException{
    String [] nextLine;
    String strDivision;
    String strClass;
    String strNotes;
    String strDescription;
      
    Statement stat = aConn.createStatement(); 
    stat.executeUpdate("drop table if exists JCNAClassesAlt;"); 
    String q = "create table JCNAClassesAlt ('ID'	INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL, 'class' TEXT NOT NULL UNIQUE, 'description' TEXT NOT NULL, 'note'	TEXT NOT NULL, 'node' INTEGER NOT NULL);";
    stat.executeUpdate(q);
    CSVReader reader;
    int iLine;
    
    String JCNAClassesCSV = strPath + "\\JCNAClassesCsv.txt" ;
    PreparedStatement prep;
    try {
        reader = new CSVReader(new FileReader(JCNAClassesCSV ));
        
        q ="insert into JCNAClassesAlt ('division', 'class', 'description', 'note', 'node') values ";
        //PreparedStatement prep = aConn.prepareStatement( "insert into JCNAClasses ('division', 'class', 'description', 'note', 'node') values ( ?,  ?,  ?,  ?,  ?);"); 
        
        iLine = 0;
        //aConn.setAutoCommit(false); 
        
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
        // System.out.println(nextLine[0] + nextLine[1] );
            if (iLine > 0){
                if(iLine > 1)  q = q + ",";
                strDivision =  "'" +  nextLine[0]+ "'";
                strClass = "'" +  nextLine[1]+ "'";
                strDescription= "'" +  nextLine[2] + "'";
                strNotes= "'" +  nextLine[3]  + "'";
                q = q + "(" + strDivision + "," + strClass + "," + strDescription + "," + strNotes + "," + iLine + ")";               
            }
            iLine++;
        }
        q = q + ";";
        //q = "insert into JCNAClassesAlt ('division', 'class', 'description', 'note', 'node') values ('Championship','C01A','Description','note',1);";
          System.out.println("\n" + q + "\n\n");
        //prep = aConn.prepareStatement(q);
       // prep.executeQuery(q);
        stat.executeUpdate(q);
        System.out.println("did the insert");
        
    }    catch (FileNotFoundException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }    
        catch (IOException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        } 
       //catch (SQLException ex) {
       //     Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
       // }

  }

*/
  public static void LoadConcoursClassesTable(Connection aConn, String strPath) throws SQLException{
    int iLine;
    String [] nextLine;
    String strClassName ;
    int intClassNode ; 
    int intEntryCount; 
    int iColEntryCount;
    int intEntryNode;
    Long lngConcoursclasses_id;
    
    Statement stat_cc = aConn.createStatement(); 
    stat_cc.executeUpdate("drop table if exists ConcoursClassesTable;"); 
    String q = "create table ConcoursClassesTable ('concoursclasses_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'class_name' TEXT NOT NULL, 'class_node' INTEGER NOT NULL);";
    stat_cc.executeUpdate(q);

    Statement stat_cce = aConn.createStatement(); 
    stat_cce.executeUpdate("drop table if exists ConcoursClassesEntries;"); 
    q = "create table ConcoursClassesEntries ('concoursclassesentries_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursclasses_id' INTEGER NOT NULL, 'entry_node' INTEGER NOT NULL, FOREIGN KEY (concoursclasses_id) REFERENCES ConcoursClassesTable (concoursclasses_id));";
    stat_cce.executeUpdate(q);
    
    Statement stat_lrid ;
    ResultSet rs_lrid;

    PreparedStatement prep_cc = aConn.prepareStatement( "insert into ConcoursClassesTable ('class_name', 'class_node') values (?, ?);"); 
    PreparedStatement prep_cce = aConn.prepareStatement( "insert into ConcoursClassesEntries ('concoursclasses_id', 'entry_node') values (?, ?);"); 
    
    String ConcoursClassesCSV = strPath ;
    CSVReader reader = null;
    try{
        reader = new CSVReader(new FileReader(ConcoursClassesCSV ));
    }    catch (FileNotFoundException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
    }    
        
        
      iLine = 0;
      try {
          while ((nextLine = reader.readNext()) != null) {
              // nextLine[] is an array of values from the line
              //
              if(nextLine[0].isEmpty()){
                  System.out.println("Empty line in LoadConcoursClassesTable");
                  System.exit(-1);
              }
              if (iLine > 0 && !nextLine[0].isEmpty()){
                  strClassName = nextLine[0].trim();
                  intClassNode = Integer.parseInt(nextLine[1].trim());
                  intEntryCount = Integer.parseInt(nextLine[2].trim());
                  prep_cc.setString(1, strClassName);
                  prep_cc.setInt(2, intClassNode);
                  prep_cc.executeUpdate();
                  
                  stat_lrid = aConn.createStatement();
                  rs_lrid = stat_lrid.executeQuery("SELECT last_insert_rowid()");
                  lngConcoursclasses_id = 0L; // won't be used
                  if (rs_lrid.next()) {
                      lngConcoursclasses_id = rs_lrid.getLong(1);
                  }
                  else{
                      okDialog("ERROR: Failed to find last row id of inserts into LoadConcoursClassesTable");
                      theConcours.GetLogger().info("ERROR: Failed to find last row id of inserts into LoadConcoursClassesTable");
                      System.exit(-1);
                  }
                  rs_lrid.close();
                  stat_lrid.close();
                  iColEntryCount = 2;
                  for(int iCol = 1+iColEntryCount; iCol<= (intEntryCount + iColEntryCount); iCol++){
                      intEntryNode = Integer.parseInt(nextLine[iCol].trim());
                      prep_cce.setLong(1, lngConcoursclasses_id );
                      prep_cce.setInt(2, intEntryNode);
                      prep_cce.executeUpdate();
                      
                  }
                 // aConn.commit();

              }
              iLine++;
          }
          prep_cce.close();
          prep_cce.close();
      } catch (IOException ex) {
          Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
      }
  }  
  //
  //  Writes JudgeAssignments from memory to JudgeAssignmentsTable and EntryJudgesTable in the Concours database
  //  Used upon exit from the JudgeAssignGUI.
  //
public static void JudgeAssignmentsMemToDB(Connection aConn, Concours aConcours){
        try {
            aConn.setAutoCommit(false);
            Statement stat_ej = aConn.createStatement(); // Entry judge lists
            stat_ej.executeUpdate("drop table if exists EntryJudgesTable;");
            stat_ej.close();
            aConn.commit();
            
            Statement stat_ja = aConn.createStatement();
            stat_ja.executeUpdate("drop table if exists JudgeAssignmentsTable;");
            stat_ja.close();
            aConn.commit();
            
            Statement stat_jac = aConn.createStatement();
            String q = "create table JudgeAssignmentsTable ('judgeassignment_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'car_node' INTEGER NOT NULL, 'owner_node' INTEGER NOT NULL, 'timeslot' INTEGER);";
            stat_jac.executeUpdate(q);
            stat_jac.close();
            aConn.commit();
            
            Statement stat_ejc = aConn.createStatement();
            q = "create table EntryJudgesTable ('entryjudges_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'judgeassignment_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL,  FOREIGN KEY (judgeassignment_id) REFERENCES JudgeAssignmentsTable (judgeassignment_id));";
            stat_ejc.executeUpdate(q);
            stat_ejc.close();
            aConn.commit();
            
            Statement stat_lrid  = null ;
            //ResultSet rs_lrid = null; // 9/25/2018 --- trying to fix locked table while Removing a Judge
            
            int intCarIndex;
            int intTimeslot;
            ArrayList<Integer> lstTheJudgeIndices;
            int intOwnerIndex;
            Long lngJudgeassignment_id;
            
            PreparedStatement prep_ja = aConn.prepareStatement( "insert into JudgeAssignmentsTable ('car_node', 'owner_node', 'timeslot') values (?, ?,  ?);");
            PreparedStatement prep_ej = aConn.prepareStatement( "insert into EntryJudgesTable ('judgeassignment_id', judge_node) values (?, ?);");
            //aConn.setAutoCommit(false);
            
            // Write JudgeAssignmentsTable
            for(JudgeAssignment ja : aConcours.GetJudgeAssignments()){
                ResultSet rs_lrid; // 9/25/2018 --- trying to fix locked table while Removing a Judge
                intCarIndex = ja.GetCarIndex();
                intOwnerIndex = ja.GetOwnerIndex();
                lstTheJudgeIndices = ja.GetJudgeIndicies();
                intTimeslot = ja.GetTimeslot();
                
                // Insert into database table
                prep_ja.setInt(1, intCarIndex);
                prep_ja.setInt(2, intOwnerIndex);
                prep_ja.setInt(3, intTimeslot);
                prep_ja.executeUpdate();
                aConn.commit();
                ////  NOTE: SELECT last_insert_rowid() seems to no longer work here... don't know why. Could be because of working with the affected tables
                ///         in a separate process....  JudgeAssignGUI
                //
                //     
                //
                stat_lrid = aConn.createStatement();
                rs_lrid = stat_lrid.executeQuery("SELECT judgeassignment_id  FROM JudgeAssignmentsTable where car_node == " + intCarIndex);  
                lngJudgeassignment_id = 0L;
                if (rs_lrid.next()) {
                    lngJudgeassignment_id = rs_lrid.getLong("judgeassignment_id")  ;
                    rs_lrid.close();
                    stat_lrid.close();
                }
                else{
                    okDialog("ERROR: Could not find intCarIndex " + intCarIndex + " in JudgeAssignmentsTable");
                    aConcours.GetLogger().info("ERROR: Could not find intCarIndex " + intCarIndex + " in JudgeAssignmentsTable");
                    rs_lrid.close();
                    stat_lrid.close();
                    System.exit(-1);
                }
                
                // Write EntryJudgesTable
                for ( Integer ji : lstTheJudgeIndices){
                    prep_ej.setLong(1, lngJudgeassignment_id);
                    prep_ej.setInt(2, ji);
                    prep_ej.executeUpdate();
                }
                aConn.commit();
            }
            aConn.setAutoCommit(true);
            prep_ja.close();
            prep_ej.close();
            theConcours.GetLogger().info("Finished writing Judge Assignments from memory to DB after possible user edits.");
        } catch (SQLException ex) {
            String msg = "ERROR: Failed writing Judge Assignments from memory to DB"; 
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
      }
    
////////////////////////////////////////////////////////////////////////////////////////    
// ReadJudgeAssignmentDBToMem   
//         Reads the Judge Assignments from concours database into memory
//
public void ReadJudgeAssignmentDBToMem(Connection aConn, Concours aConcours) {
        try {
            int intCarIndex;
            int intOwnerIndex;
            ArrayList<Integer> lstJudges;
            int intTimeslot;
            int intJudgeIndex;
            Statement stat_ja;
            ResultSet rs_ja;
            Statement stat_ej = null;
            ResultSet rs_ej = null;
            
            Long lngJudgeAssignment_id;
            
            JudgeAssignment aJudgeAssignment;
            aConn.setAutoCommit(false);
            stat_ja = aConn.createStatement();
            rs_ja = stat_ja.executeQuery("select * from JudgeAssignmentsTable;");
            
            aConn.commit();
            theConcours.GetLogger().info("Load Judge assignments from DB to memory:");
            int count = 0;
            while (rs_ja.next()) {
                lngJudgeAssignment_id = rs_ja.getLong("judgeassignment_id");
                intCarIndex = rs_ja.getInt("car_node");  // Does the need to be mapped back? No! The JudgeAssign.txt indices s.b. mapped back to Concours indices when written to the database.
                intOwnerIndex = rs_ja.getInt("owner_node");
                lstJudges = new ArrayList<>();
                intTimeslot = rs_ja.getInt("timeslot");
                //theConcours.GetLogger().info("JA ID= " + lngJudgeAssignment_id + " Car index= " + intCarIndex + " OwnerIndex= " + intOwnerIndex + " Timeslot= " + intTimeslot);                
                // get judge list
                stat_ej = aConn.createStatement();
                rs_ej = stat_ej.executeQuery("select * from EntryJudgesTable where judgeassignment_id == " + lngJudgeAssignment_id + ";"); 
                while (rs_ej.next()) {
                    intJudgeIndex = rs_ej.getInt("judge_node");
                    lstJudges.add(intJudgeIndex);
                }
                aConn.commit();
                //theConcours.GetLogger().info("Judge list: " + lstJudges);
                rs_ej.close();
                stat_ej.close();
                aJudgeAssignment =  new JudgeAssignment(intCarIndex, intOwnerIndex, lstJudges, intTimeslot);
                aConcours.theJudgeAssignments.addJudgeAssignment(aJudgeAssignment);
                //
                // Now we have to set the Timeslot index to the entry (i.e., car) with node car_node
                //
                aConcours.theEntries.getEntry(intCarIndex).SetTimeslotIndex(intTimeslot);
                count++;
            }
             aConn.setAutoCommit(true);
            theConcours.GetLogger().info("finished Loading " + count + " Judge Assignment table rows from database to Memory");
            
            if(rs_ej != null) rs_ej.close();
            if(stat_ej != null) stat_ej.close();
            rs_ja.close();
            stat_ja.close();
        } catch (SQLException ex) {
            String msg = "Exception while loading JA table to memory";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        

}
  
  public static void LoadMasterPersonnelAndJaguarTablesFromCSV(Connection aConn, String strPath, String strMasterPersonnelCSVFileName) throws SQLException{
    String [] nextLine;
    Integer intJCNA;
    String strJOC;
    String strClub;
    String strLastName ;
    String strFirstName;
    String strJudgeStatus;
    Integer intCertYear;
    String strAddressStreet;
    String strCity ;
    String strState ;
    String strCountry;
    String strPostalCode; // ZIP
    String strPhoneWork ;
    String strPhoneHome ;
    String strPhoneCell;
    String strEmail;
    String strUniqueName; // last name + 1st 3 chars of first name

    String strJCNA_C;
    String strJCNA_D;
    String strCategory;
    Integer intYear;
    String strModel;
    String strDescription;
    String strColor;
    String strPlateVIN;
    String strUniqueDescription;
    //ResultSet rs_mp;
    Long lngMasterPersonnelID;
    
    
    
    Integer intJagCountIndex = 35; // index in nextLine
    Integer intJagCount;
    
    Integer intNumemberInHousehold;
     
    Statement stat = aConn.createStatement(); 
    stat.executeUpdate("drop table if exists MasterPersonnel;"); 
    String q = "create table MasterPersonnel ('masterpersonnel_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'jcna' INTEGER NOT NULL, 'club' TEXT, 'lastname' TEXT NOT NULL , 'firstname' TEXT NOT NULL, 'mi' TEXT,'unique_name' TEXT NOT NULL, 'judgestatus' TEXT, 'cert_year' INTEGER, 'address_street' TEXT, 'city' TEXT, 'state' TEXT, 'country' TEXT, postalcode TEXT, 'phone_work' TEXT, 'phone_home' TEXT, 'phone_cell' TEXT, 'email' TEXT) ;";
    stat.executeUpdate(q);

    stat.executeUpdate("drop table if exists MasterJaguar;"); 
    q = "create table MasterJaguar ('masterjaguar_id' INTEGER PRIMARY KEY AUTOINCREMENT,  'masterpersonnel_id' INTEGER,   'jcnaclass_c' TEXT NOT NULL, 'jcnaclass_d' TEXT NOT NULL, 'joclacategory' TEXT NOT NULL, 'year' INTEGER NOT NULL, 'model' TEXT NOT NULL, 'description' TEXT NOT NULL, 'unique_desc' TEXT NOT NULL, 'color' TEXT NOT NULL, 'platevin' TEXT NOT NULL,  FOREIGN KEY (masterpersonnel_id) REFERENCES MasterPersonnel (masterpersonnel_id)) ;";
    stat.executeUpdate(q);
    
    CSVReader reader;
    int iLine;
    
    String MasterPersonnelCSV = strPath + "\\" + strMasterPersonnelCSVFileName; // e.g. MasterPersonnelListCsv.txt
    
    try {
        reader = new CSVReader(new FileReader(MasterPersonnelCSV ));
        PreparedStatement prep_p = aConn.prepareStatement( "insert into MasterPersonnel ('jcna', 'club', 'lastname', 'firstname', 'unique_name', 'judgestatus', 'cert_year', 'address_street', 'city', 'state', 'country', postalcode, 'phone_work', 'phone_home', 'phone_cell', 'email') values ( ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ?, ?, ?);"); 
        PreparedStatement prep_j = aConn.prepareStatement( "insert into MasterJaguar ('masterpersonnel_id',  'jcnaclass_c', 'jcnaclass_d', 'joclacategory', 'year', 'model', 'description', 'unique_desc', 'color', 'platevin') values ( ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?, ?);"); 
        
        iLine = 0;
        aConn.setAutoCommit(false); 
        intCertYear = 2015; // This will eventually be input by user, but for now all we will jaut assume all Judges were trained in 2014
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            if (iLine > 0){
                //
                //  Load Personnel data into MasterPersonnel
                //
                    intJCNA = Integer.parseInt(nextLine[7]);
                    strClub = nextLine[0];
                    strJudgeStatus = nextLine[28].trim();
                    strAddressStreet = nextLine[14];
                    strCity  = nextLine[15].trim();
                    strState  = nextLine[16].trim();
                    strCountry = nextLine[19].trim();
                    if("USA".equals(strCountry) || strCountry.isEmpty()){
                        strPostalCode = nextLine[17].trim(); // ZIP
                    }
                    else{
                        strPostalCode = nextLine[18].trim();
                    }
                    strPhoneWork = nextLine[20];
                    strPhoneHome= nextLine[21];
                    strPhoneCell =   nextLine[22];
                    strEmail  = nextLine[23].trim();
                    intJagCount = Integer.parseInt(nextLine[intJagCountIndex]);
                    
                
                intNumemberInHousehold = Integer.parseInt(nextLine[1]);
                for(int i = 0; i< intNumemberInHousehold; i++){
                    if(i == 0) {
                        strLastName = nextLine[8].trim();
                        strFirstName = nextLine[9].trim();
                         
                        if("Pri mbr".equals(strJudgeStatus)){
                           strJudgeStatus = "J"; 
                        }
                        else{
                           strJudgeStatus = "" ;
                        }
                    }
                    else{
                        strLastName = nextLine[11].trim();
                        strFirstName= nextLine[12].trim();
                        if("Sec mbr".equals(strJudgeStatus)){
                           strJudgeStatus = "J"; 
                        }
                        else{
                           strJudgeStatus = "" ;
                        }
                    }
                    strUniqueName = UniqueName(strLastName, strFirstName);
                    prep_p.setInt(1, intJCNA); 
                    prep_p.setString(2,strClub); 
                    prep_p.setString(3,strLastName); 
                    prep_p.setString(4,strFirstName); 
                    prep_p.setString(5,strUniqueName); 
                    prep_p.setString(6,strJudgeStatus);
                    prep_p.setInt(7, intCertYear);
                    prep_p.setString(8,strAddressStreet); 
                    prep_p.setString(9,strCity); 
                    prep_p.setString(10,strState); 
                    prep_p.setString(11,strCountry); 
                    prep_p.setString(12,strPostalCode); 
                    prep_p.setString(13,strPhoneWork); 
                    prep_p.setString(14,strPhoneHome); 
                    prep_p.setString(15,strPhoneCell); 
                    prep_p.setString(16,strEmail);
                    prep_p.addBatch(); 
                    prep_p.executeBatch(); 
                    aConn.commit();
                //
                //  Load the Jaguars for this person into MasterJaguar table
                //  Note: If the record in the MasterPersonnelListCsv file is a 2-person household the Jaguars will
                //        be listed twice, once for each person. This will have to be dealt with when the Concours is built because
                //        an entry cannot have two separate owners... or can it??  More thought necessary.  Also have to
                //        deal with the "other person with interest in the Jaguar" issue.  Perhaps yet another table of "associated persons"
                //
 //Fixed to allow names with apostrophy etc.              q =  "select * from MasterPersonnel where  unique_name like '" +  strUniqueName + "';";
               String q_mp =  "select * from MasterPersonnel where  unique_name like ?"; 
               PreparedStatement prepStmt_mp1 = aConn.prepareStatement(q_mp);
               prepStmt_mp1.setString(1, strUniqueName);
               ResultSet rs_mp1 = prepStmt_mp1.executeQuery();
               lngMasterPersonnelID = 0L; // will never be used
               if (rs_mp1.next()) {
                    lngMasterPersonnelID = rs_mp1.getLong("masterpersonnel_id") ; 
                    
                    Integer iLoc = 1+intJagCountIndex;
                    for(int k = 0; k<intJagCount;k++){   
                        strJCNA_C  = nextLine[iLoc++].trim();
                        strJCNA_D = nextLine[iLoc++].trim();
                        strCategory = nextLine[iLoc++].trim();
                        if(nextLine[iLoc].isEmpty()){
                            System.out.println("Model year invalid  for " + strUniqueName);
                        }
                        intYear = Integer.parseInt(nextLine[iLoc++].trim());
                        strDescription = nextLine[iLoc++].trim();
                        strModel = nextLine[iLoc++].trim();
                        strUniqueDescription = UniqueDescription(strDescription, strUniqueName);
                        strColor = nextLine[iLoc++].trim();
                        strPlateVIN = nextLine[iLoc++].trim();
                        prep_j.setLong(1, lngMasterPersonnelID);
                        prep_j.setString(2,strJCNA_C); 
                        prep_j.setString(3,strJCNA_D); 
                        prep_j.setString(4,strCategory);
                        prep_j.setInt(5, intYear);
                        prep_j.setString(6, strModel);
                        prep_j.setString(7,strDescription); 
                        prep_j.setString(8,strUniqueDescription); 
                        prep_j.setString(9,strColor); 
                        prep_j.setString(10,strPlateVIN); 
                        prep_j.addBatch(); 
                        prep_j.executeBatch(); 
                    }
                }
                else{
                    okDialog("Could not find UniqueName " + strUniqueName + " in MasterPersonnel");
                    theConcours.GetLogger().info("Could not find UniqueName " + strUniqueName + " in MasterPersonnel");
                    System.exit(-1);
                }
               
               rs_mp1.close();
               prepStmt_mp1.close();
              }
                    aConn.commit();
            }       
            iLine++;
        }
        //prep.executeBatch();
        aConn.setAutoCommit(true); 
       prep_p.close();
       prep_j.close();
        

    }    catch (FileNotFoundException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }    
        catch (IOException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    
  }

  public static void WriteMasterPersonnelAndJaguarTablesFromMemToDB(Connection aConn) throws SQLException{
    Integer intJCNA;
    String strJOC;
    String strClub;
    String strLastName ;
    String strFirstName;
    String strJudgeStatus;
    Integer intCertYear;
    String strAddressStreet;
    String strCity ;
    String strState ;
    String strCountry;
    String strPostalCode; // ZIP
    String strPhoneWork ;
    String strPhoneHome ;
    String strPhoneCell;
    String strEmail;
    String strUniqueName; // last name + 1st 3 chars of first name

    String strJCNA_C;
    String strJCNA_D;
    String strCategory;
    Integer intYear;
    String strModel;
    String strDescription;
    String strColor;
    String strPlateVIN;
    String strUniqueDescription;
    //ResultSet rs_mp;
    Long lngMasterPersonnelID;
    
    
    
    String q; 
    aConn.setAutoCommit(false);
    Statement stat ;
            stat = aConn.createStatement();
            stat.executeUpdate("drop table if exists MasterPersonnel;"); 
            q = "create table MasterPersonnel ('masterpersonnel_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'jcna' INTEGER NOT NULL, 'club' TEXT, 'lastname' TEXT NOT NULL , 'firstname' TEXT NOT NULL, 'mi' TEXT,'unique_name' TEXT NOT NULL, 'judgestatus' TEXT, 'cert_year' INTEGER, 'address_street' TEXT, 'city' TEXT, 'state' TEXT, 'country' TEXT, postalcode TEXT, 'phone_work' TEXT, 'phone_home' TEXT, 'phone_cell' TEXT, 'email' TEXT) ;";
            stat.executeUpdate(q);

            stat.executeUpdate("drop table if exists MasterJaguar;"); 
            q = "create table MasterJaguar ('masterjaguar_id' INTEGER PRIMARY KEY AUTOINCREMENT,  'masterpersonnel_id' INTEGER,   'jcnaclass_c' TEXT NOT NULL, 'jcnaclass_d' TEXT NOT NULL, 'joclacategory' TEXT NOT NULL, 'year' INTEGER NOT NULL, 'model' TEXT NOT NULL, 'description' TEXT NOT NULL, 'unique_desc' TEXT NOT NULL, 'color' TEXT NOT NULL, 'platevin' TEXT NOT NULL,  FOREIGN KEY (masterpersonnel_id) REFERENCES MasterPersonnel (masterpersonnel_id)) ;";
            stat.executeUpdate(q);

            PreparedStatement prep_p = aConn.prepareStatement( "insert into MasterPersonnel ('jcna', 'club', 'lastname', 'firstname', 'unique_name', 'judgestatus', 'cert_year', 'address_street', 'city', 'state', 'country', postalcode, 'phone_work', 'phone_home', 'phone_cell', 'email') values ( ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ?, ?, ?);");
            PreparedStatement prep_j ;
            prep_j = aConn.prepareStatement( "insert into MasterJaguar ('masterpersonnel_id',  'jcnaclass_c', 'jcnaclass_d', 'joclacategory', 'year',  'model', 'description', 'unique_desc', 'color', 'platevin') values ( ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?, ?);");
            int count = 0;
            for(MasterPersonExt mp : theConcours.GetConcoursMasterPersonnel()){
                strUniqueName = mp.getUniqueName();
               //if(lstUniqueNames.LoadJCNAClassesTableFromCSV(strUniqueName)){
               //     System.out.println("UniqueName " + strUniqueName + " is repeated in MasterPersonnel. Skipping all but the first.");
               // } else{
               //    lstUniqueNames.add(strUniqueName);
                //
                //  Load Personnel data into MasterPersonnel database
                //
                    // DEBUGGING
                    //if(count % 20 == 0){
                    //    System.out.println("count = " + count + " Inserted " + strUniqueName);
                    //}
                    count ++;
                    intJCNA = mp.getJcna();
                    strLastName = mp.getLastName();
                    strFirstName = mp.getFirstName();
                    strClub = mp.getClub();
                    strJudgeStatus = mp.getJudgeStatus();
                    intCertYear = mp.getCertYear();
                    strAddressStreet = mp.getAddressSreet();
                    strCity  = mp.getCity();
                    strState  = mp.getState();
                    strCountry = mp.getCountry();
                    strPostalCode =mp.getPostalCode(); // ZIP
                    strPhoneWork = mp.getPhoneWork();
                    strPhoneHome= mp.getPhoneHome();
                    strPhoneCell =   mp.getPhoneCell();
                    strEmail  = mp.getEmail();
                    prep_p.setInt(1, intJCNA); 
                    prep_p.setString(2,strClub); 
                    prep_p.setString(3,strLastName); 
                    prep_p.setString(4,strFirstName); 
                    prep_p.setString(5,strUniqueName); 
                    prep_p.setString(6,strJudgeStatus);
                    prep_p.setInt(7, intCertYear);
                    prep_p.setString(8,strAddressStreet); 
                    prep_p.setString(9,strCity); 
                    prep_p.setString(10,strState); 
                    prep_p.setString(11,strCountry); 
                    prep_p.setString(12,strPostalCode); 
                    prep_p.setString(13,strPhoneWork); 
                    prep_p.setString(14,strPhoneHome); 
                    prep_p.setString(15,strPhoneCell); 
                    prep_p.setString(16,strEmail);
                    prep_p.addBatch(); 
                    prep_p.executeBatch(); 
                    aConn.commit();
                    
                    //
                    //  Load the Jaguars for this person into MasterJaguar table
                    //
                    // Get the row_id of the most recent insert into MasterPersonnel
                    //
                    Statement stat_lrid = aConn.createStatement();
                    ResultSet rs_lrid = stat_lrid.executeQuery("SELECT last_insert_rowid()");
                    lngMasterPersonnelID = 0L; // won't be used
                    if (rs_lrid.next()) {
                        lngMasterPersonnelID = rs_lrid.getLong(1);
                    }
                    else{
                        okDialog("ERROR: Failed to get last row id of inserts into MasterPersonnel in WriteMasterPersonnelAndJaguarTablesFromMemToDB");
                        theConcours.GetLogger().info("ERROR: Failed to get last row id of inserts into MasterPersonnel in WriteMasterPersonnelAndJaguarTablesFromMemToDB");
                        System.exit(-1);
                    }
                    stat_lrid.close();
                    rs_lrid.close();
                    // write the stable for the master person
                    
                    for(MasterJaguar mj : mp.getJaguarStable()){
                        strJCNA_C  = mj.getJcnaclass_c();
                        strJCNA_D = mj.getJcnaclass_d();
                        strCategory = mj.getJoclacategory();
                        intYear = mj.getYear();
                        strModel = mj.getDescription();
                        strDescription = mj.getDescription();
                        strUniqueDescription = mj.getUniqueDesc();
                        strColor = mj.getColor();
                        strPlateVIN = mj.getPlateVIN();
                        prep_j.setLong(1, lngMasterPersonnelID);
                        prep_j.setString(2,strJCNA_C); 
                        prep_j.setString(3,strJCNA_D); 
                        prep_j.setString(4,strCategory);
                        prep_j.setInt(5, intYear);
                        prep_j.setString(6,strModel); 
                        prep_j.setString(7,strDescription); 
                        prep_j.setString(8,strUniqueDescription); 
                        prep_j.setString(9,strColor); 
                        prep_j.setString(10,strPlateVIN); 
                        prep_j.addBatch(); 
                        prep_j.executeBatch(); 
                    } // end of mj loop
                  aConn.commit();
            } // end of for(mp)      
        
       aConn.setAutoCommit(true); 
       prep_p.close();
       prep_j.close();
    
  }
  
  
  
public static void DisplayJCNAClassesTable(Connection aConn, String strPath) throws SQLException{
    Statement stat_c;
    ResultSet rs_c;
    System.out.println("\n\n Loop over and display all JCNA classes");
    stat_c = aConn.createStatement();
    
    rs_c = stat_c.executeQuery("select * from JCNAClasses;"); 
    while (rs_c.next()) { 
        System.out.println("Division: " + rs_c.getString("division") + " " + "Class: " + rs_c.getString("class") + " Node: " +  rs_c.getString("node")+ " Description: " +  rs_c.getString("description"));
    } 

    rs_c.close();
        
}

  
    public static void LoadConcoursEntriesTable(Connection aConn,  String strPath) throws SQLException{ 
    String [] nextLine;
    String strEntryName;
    String strClass;
    Integer intYear;
    String strDescription;
    String strUniqueDescription;
    String strOwnerFirst;
    String strOwnerLast;
    Integer intJCNA;
    String strColor;
    String strPlate;
    Integer intEntryNode;
    Long lngPersonnel_id;
    Long  lngJaguar_id;
    String strStatus;
    int intPersonnelNode;
    long lngMasterPersonnelID;
    long lngConcoursPersonnelID;
    long lngConcoursOwnerID;
    long lngMasterJaguarID;
    String strUniqueName; // last name + 1st 3 chars of first name
    Integer intEntryCount;
    Integer iColEntryCount = 4;
    String strEntry_n;

    CSVReader reader;
    String q;
    int iLine;
    PreparedStatement prep_p;
    PreparedStatement prep_j;
    PreparedStatement prep_e;
    PreparedStatement prep_o;
    PreparedStatement prep_oe;
    ResultSet rs_mp1;
    ResultSet rs_cp1;
    ResultSet rs_j;
    ResultSet rs_mj; 
    ResultSet rs_o; 
    Statement stat = aConn.createStatement();
      //
      // Concourse Personnel Table:  Owners, Judges, & Interested parties
      // Linked to MasterPersonnel
      //
      int executeUpdate = stat.executeUpdate("drop table if exists ConcoursPersonnel"); 
      theConcours.GetLogger().info("drop table if exists ConcoursPersonnel result=" + executeUpdate);
    
   // q = "create table ConcoursPersonnel ('concourspersonnel_id' INTEGER PRIMARY KEY AUTOINCREMENT ,  'masterpersonnel_id' INTEGER, 'ownerfirst' TEXT , 'ownerlast' TEXT NOT NULL, 'jcna' INTEGER  , 'status' TEXT , 'concourspersonnel_node' INTEGER NOT NULL UNIQUE,  UNIQUE (masterpersonnel_id), FOREIGN KEY (masterpersonnel_id) REFERENCES MasterPersonnel (masterpersonnel_id))";
    q = "create table ConcoursPersonnel ('concourspersonnel_id' INTEGER PRIMARY KEY AUTOINCREMENT ,  'masterpersonnel_id' INTEGER UNIQUE, 'unique_name' TEXT NOT NULL, 'status_o' INTEGER DEFAULT 0,  'status_j' INTEGER DEFAULT 0 , 'concourspersonnel_node' INTEGER NOT NULL UNIQUE,  UNIQUE (masterpersonnel_id), FOREIGN KEY (masterpersonnel_id) REFERENCES MasterPersonnel (masterpersonnel_id))";
    stat.executeUpdate(q);

    //
    // Concours Jaguar  Table
    //
    stat.executeUpdate("drop table if exists ConcoursJaguars"); 
    //q  = "create table ConcoursJaguars ('jaguar_id' INTEGER PRIMARY KEY AUTOINCREMENT , 'class' TEXT NOT NULL, 'year' TEXT NOT NULL, 'description' TEXT NOT NULL, 'Color' TEXT, 'plate' TEXT, 'node' INTEGER NOT NULL UNIQUE)";
    q  =   "create table ConcoursJaguars ('jaguar_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'masterjaguar_id' INTEGER, 'class' TEXT NOT NULL,  'node' INTEGER NOT NULL UNIQUE, UNIQUE (masterjaguar_id), FOREIGN KEY (masterjaguar_id) REFERENCES MasterJaguar (masterjaguar_id))";
    stat.executeUpdate(q);

    //
    // Concours Owners  Table
    //
    stat.executeUpdate("drop table if exists ConcoursOwners"); 
    q  =   "create table ConcoursOwners ('concoursowner_id' INTEGER PRIMARY KEY AUTOINCREMENT,   'concourspersonnel_id' , 'unique_name' TEXT NOT NULL UNIQUE, 'concourspersonnel_node' INTEGER NOT NULL UNIQUE, UNIQUE (concourspersonnel_id), FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id))";
    stat.executeUpdate(q);

    //
    // Concours Owners  Entries Table.. an Owner can have several entries in the Concours
    //
    stat.executeUpdate("drop table if exists ConcoursOwnersEntries"); 
    q  =   "create table ConcoursOwnersEntries ('ownerentry_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursowner_id' LONG,  'entrynode' INTEGER NOT NULL UNIQUE,  FOREIGN KEY (concoursowner_id) REFERENCES ConcoursOwners (concoursowner_id))";
    stat.executeUpdate(q);
    
    //
    // Entry  Table ( a Relationship or "link" between Personnel & Jaguars
    //
    stat.executeUpdate("drop table if exists ConcoursEntries"); 
    q  = "create table ConcoursEntries (entry_name TEXT NOT NULL, concourspersonnel_id INTEGER NOT NULL, jaguar_id INTEGER NOT NULL, UNIQUE (concourspersonnel_id, jaguar_id), FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id), FOREIGN KEY (jaguar_id) REFERENCES ConcoursJaguars (jaguar_id));";
    stat.executeUpdate(q);

    //
    //  Load Owners from the OwnersCsv.txt file into Personnel table.
    //  Note: this could be gleaned directly from the EntriesCsv.txt file but here we have to use the OwnersCsv.txt file (generated by Excel) to be sure
    //          The Personnel node numbers are consistant.
    //
    String OwnersCSV = strPath + "\\OwnersCsv.txt" ;
    //String strOwnerLast;
    try {
        reader = new CSVReader(new FileReader(OwnersCSV ));
        iLine = 0;
        aConn.setAutoCommit(false); 
        prep_p = aConn.prepareStatement( "insert into ConcoursPersonnel ('masterpersonnel_id', 'unique_name', 'status_o',  'concourspersonnel_node' ) values ( ?,  ?, ?, ?);"); 
        prep_o = aConn.prepareStatement( "insert into ConcoursOwners ('concourspersonnel_id', 'unique_name',  'concourspersonnel_node' ) values (?, ?, ?);"); 
        prep_oe = aConn.prepareStatement( "insert into ConcoursOwnersEntries ('concoursowner_id',  'entrynode' ) values (?, ?);"); 
        rs_mp1 = null;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            if (iLine > 0){
                strUniqueName = nextLine[2].trim();

                intPersonnelNode= Integer.parseInt(nextLine[3]);
                intEntryCount = Integer.parseInt(nextLine[iColEntryCount]);
               
//Fixed to allow names with apostrophy etc.               q =  "select * from MasterPersonnel where  unique_name like '" +  strUniqueName + "';";
//               rs_mp = stat.executeQuery(q);
               String q_mp =  "select * from MasterPersonnel where  unique_name like ?"; 
               PreparedStatement prepStmt_mp1 = aConn.prepareStatement(q_mp);
               prepStmt_mp1.setString(1, strUniqueName);
               rs_mp1 = prepStmt_mp1.executeQuery();
               lngMasterPersonnelID = 0L; // will never be used
                
                
                if (rs_mp1.next()) {
                    // load into ConcoursPersonnel 
                    lngMasterPersonnelID = rs_mp1.getLong("masterpersonnel_id") ; 
                    prep_p.setLong(1,lngMasterPersonnelID); 
                    prep_p.setString(2, strUniqueName ); 
                    prep_p.setInt(3, 1); // status_o = 1 means this person is an owner
                    prep_p.setInt(4,intPersonnelNode);
                    prep_p.addBatch(); 
                    prep_p.executeBatch(); 
                    aConn.commit();
                    // Load into ConcoursOwners
//Fixed to allow names with apostrophy etc.                    q =  "select * from ConcoursPersonnel where  unique_name like '" +  strUniqueName + "';";
                   String q_cp =  "select * from ConcoursPersonnel where  unique_name like ?"; 
                   PreparedStatement prepStmt_cp1 = aConn.prepareStatement(q_cp);
                   prepStmt_cp1.setString(1, strUniqueName);
                   rs_cp1 = prepStmt_cp1.executeQuery();
                    lngConcoursPersonnelID = 0L; // will never be used
                    if(rs_cp1.next()){
                        lngConcoursPersonnelID = rs_cp1.getLong("concourspersonnel_id");
                        //lngConcoursOwnerID = rs_cp.getLong("concoursowner_id");
                        prep_o.setLong(1, lngConcoursPersonnelID);
                        prep_o.setString(2, strUniqueName);
                        prep_o.setInt(3, intPersonnelNode);
                        prep_o.addBatch(); 
                        prep_o.executeBatch(); 
                        aConn.commit();
//Fixed to allow names with apostrophy etc.                        q =  "select * from ConcoursOwners where  unique_name like '" +  strUniqueName + "';";
                       String q_o =  "select * from ConcoursOwners where  unique_name like ?"; 
                       PreparedStatement prepStmt_o1 = aConn.prepareStatement(q_o);
                       prepStmt_o1.setString(1, strUniqueName);
                       rs_o = prepStmt_o1.executeQuery();
                        lngConcoursOwnerID = 0L; // will never be used
                        if(rs_o.next()){
                            lngConcoursOwnerID = rs_o.getLong("concoursowner_id");
                            for(int iColEntry = 1+iColEntryCount; iColEntry <= (intEntryCount + iColEntryCount); iColEntry++){
                                intEntryNode = Integer.parseInt(nextLine[iColEntry].trim());
                                prep_oe.setLong(1, lngConcoursOwnerID);
                                prep_oe.setInt(2,  intEntryNode);
                                prep_oe.addBatch();
                                prep_oe.executeBatch();
                                aConn.commit();
                            }
                            rs_o.close();
                            prepStmt_o1.close();
                        }
                        else{
                            rs_o.close();
                            prepStmt_o1.close();
                            okDialog("ERROR: Could not find owner_unique = " + strUniqueName + " in ConcoursOwners in LoadConcoursEntriesTable");
                            theConcours.GetLogger().info("ERROR: Could not find owner_unique = " + strUniqueName + " in LoadConcoursEntriesTable");
                            System.exit(-1);
                        }
                      
                        rs_cp1.close();    
                        prepStmt_cp1.close();
                    }
                    else{
                        rs_cp1.close();
                        prepStmt_cp1.close();
                        okDialog("ERROR: Could not find owner_unique = " + strUniqueName + " in ConcoursPersonnel in LoadConcoursEntriesTable");
                        theConcours.GetLogger().info("ERROR: Could not find owner_unique = " + strUniqueName + " in ConcoursPersonnel");
                        System.exit(-1);
                    }
                    rs_mp1.close();
                    prepStmt_mp1.close();
                }
                else{
                    okDialog("ERROR: Could not find owner_unique = " + strUniqueName + " in MasterPersonnel in LoadConcoursEntriesTable");
                    theConcours.GetLogger().info("ERROR: Could not find owner_unique = " + strUniqueName + " in ConcoursPersonnel");
                    System.exit(-1);
                }
               // prep_p.addBatch(); 
               // prep_p.executeBatch(); 
               // aConn.commit();
                
            }
            iLine++;
        }
        prep_p.close();
        prep_oe.close();
        prep_o.close();
      
    }    catch (FileNotFoundException ex1) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex1);
        }    
        catch (IOException ex1) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex1);
        }
    
// Testing        

//       String strTest = "CohenRob";
//        q = "select * from ConcoursPersonnel where ownerlast like '" + strTest + "';";
//       rs = stat.executeQuery(q); 
//        while (rs.next()) { 
//            System.out.println("ownerlast: " + rs.getString("ownerlast") + " Node: " + rs.getInt("node") + " "); 
//        } 
// end Testing
//        rs.close();
    
    
    //
    //  Now process the EntriesCsv.txt file to get Jaguars 
    //
    String EntriesCSV = strPath + "\\EntriesCsv.txt" ;
    //Integer intPersonnelID;
    stat = aConn.createStatement();
    //strStatus = "O"; // not in the CSV data so set to Owner
    try {
        reader = new CSVReader(new FileReader(EntriesCSV ));
        aConn.setAutoCommit(false); 
        iLine = 0;
        prep_j = aConn.prepareStatement( "insert into ConcoursJaguars ('class', 'masterjaguar_id', 'node' ) values ( ?,  ?,  ?);"); 
        prep_e = aConn.prepareStatement( "insert into ConcoursEntries (entry_name, concourspersonnel_id, jaguar_id) values ( ?,  ?, ?);"); 
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            // System.out.println(nextLine[0] + nextLine[1] );
            if (iLine > 0){
                strEntryName = nextLine[0].trim(); // e.g., C02-2
                strClass = nextLine[1].trim();   // e.g., C02
                intYear= Integer.parseInt(nextLine[2]);
                strDescription= nextLine[3].trim();
                strOwnerFirst= nextLine[4].trim();
                strOwnerLast= nextLine[5].trim();
                intJCNA = Integer.parseInt(nextLine[6]) ;
                strColor= nextLine[7].trim();
                strPlate= nextLine[8].trim();
                intEntryNode= Integer.parseInt(nextLine[9]); // Since Jaguars are 1-to-1 with Entries this is used as Jaguar Node numbers as well as Entry numbers.
                                                        // However, it can't be used for Personnel Node since Owners can have more than one entry. 
              strUniqueName = UniqueName(strOwnerLast, strOwnerFirst);
//Fixed to allow names with apostrophy etc.              q =  "select * from ConcoursPersonnel where  unique_name like '" +  strUniqueName + "';";
               String q_cp =  "select * from ConcoursPersonnel where  unique_name like ?"; 
               PreparedStatement prepStmt_cp1 = aConn.prepareStatement(q_cp);
               prepStmt_cp1.setString(1, strUniqueName);
               rs_cp1 = prepStmt_cp1.executeQuery();
               intPersonnelNode = 0; // will never be used
                if (rs_cp1.next()) {
                    intPersonnelNode = rs_cp1.getInt("concourspersonnel_node") ; 
                }
                else{
                    rs_cp1.close();
                    prepStmt_cp1.closeOnCompletion();
                    okDialog("ERROR: Could not find owner_unique = " + strUniqueName + " in MasterPersonnel in LoadConcoursEntriesTable");
                    theConcours.GetLogger().info("ERROR: Could not find owner_unique = " + strUniqueName + " in ConcoursPersonnel");
                    System.exit(-1);
                }
                rs_cp1.close();
                prepStmt_cp1.closeOnCompletion();
                strUniqueDescription = UniqueDescription(strDescription, strUniqueName);
//Fixed to allow names with apostrophy etc.              q =  "select * from MasterJaguar where  unique_desc like '" +  strUniqueDescription + "';";
               String q_mj = "select * from MasterJaguar where  unique_desc like ?";
               PreparedStatement prepStmt_mj = aConn.prepareStatement(q_mj);
               prepStmt_mj.setString(1, strUniqueDescription);
               rs_mj = prepStmt_mj.executeQuery();
               lngMasterJaguarID = 0; // will never be used
               if (rs_mj.next()) {
                    lngMasterJaguarID = rs_mj.getLong("masterjaguar_id") ; 
                }
                else{
                    rs_mj.close();
                    prepStmt_mj.closeOnCompletion();
                    okDialog("ERROR: Could not find owner_desc = " + strUniqueDescription + " in MasterJaguar in LoadConcoursEntriesTable");
                    theConcours.GetLogger().info("ERROR: Could not find owner_desc = " + strUniqueDescription + " in MasterJaguar in LoadConcoursEntriesTable");
                    System.exit(-1);
                }
                rs_mj.close();
                prepStmt_mj.closeOnCompletion();                
                aConn.commit();
                //
                // Load Entry Jaguars into the ConcoursJaguars table
                //
                prep_j.setString(1,strClass); 
                prep_j.setLong(2,lngMasterJaguarID);  //                                        
                prep_j.setInt(3,intEntryNode);  //                                        
                prep_j.addBatch(); 
                prepStmt_mj.executeBatch(); 
                aConn.commit();
                q =  "select jaguar_id from ConcoursJaguars where node = " + intEntryNode + ";";
                rs_j = stat.executeQuery(q);
                lngJaguar_id = null; // Will not be used
                if(rs_j.next()){
                    lngJaguar_id = rs_j.getLong("jaguar_id");
                } else{
                    rs_j.close();
                    okDialog("Could not find Entry node " + intEntryNode + " in ConcoursJaguars in LoadConcoursEntriesTable");
                    theConcours.GetLogger().info("Could not find  Entry node " + intEntryNode + " in ConcoursJaguars in LoadConcoursEntriesTable");
                }
                rs_j.close();

                q =  "select concourspersonnel_id from ConcoursPersonnel where concourspersonnel_node = " + intPersonnelNode + ";";
                ResultSet rs_cp = stat.executeQuery(q);
                lngPersonnel_id = null; // will not be useed
                if(rs_cp.next()){
                    lngPersonnel_id = rs_cp.getLong("concourspersonnel_id");
                } else{
                    rs_cp.close();
                    okDialog("ERROR: Could not find concourspersonnel_node = " + intPersonnelNode + " in ConcoursPersonnel in LoadConcoursEntriesTable");
                    theConcours.GetLogger().info("ERROR: Could not find concourspersonnel_node = " + intPersonnelNode + " in ConcoursPersonnel in LoadConcoursEntriesTable");
                    System.exit(-1);
                }
                rs_cp.close();
                prep_e.setString(1,strEntryName); 
                prep_e.setLong(2,lngPersonnel_id); 
                prep_e.setLong(3,lngJaguar_id);
                prep_e.addBatch(); 
                prep_e.executeBatch(); 
                aConn.commit();
           }
            iLine++;
        }
       // aConn.setAutoCommit(true);

        prep_j.close();
        prep_e.close();
        
    }    catch (FileNotFoundException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }    
        catch (IOException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
public static void DisplayConcoursEntriesTable(Connection aConn, String strPath) throws SQLException{
    int  person_id;
    int  jag_id;
    Statement stat_e;
    ResultSet rs_e;
    Statement stat_p;
    ResultSet rs_p;
    Statement stat_j;
    ResultSet rs_j;
    Statement stat_m;
    ResultSet rs_m;
    Long lngMasterPersonnel_ID;
            
    System.out.println("\n\n Loop over Concours Entries and list the Entry links all Owner and Jaguars");

    stat_e = aConn.createStatement();
    rs_e = stat_e.executeQuery("select * from ConcoursEntries;"); 
    while (rs_e.next()) { 
        System.out.println("\nEntry_name:" + rs_e.getString("entry_name") + " concourspersonnel_id: " + rs_e.getString("concourspersonnel_id") + " jaguar_id:" + rs_e.getString("jaguar_id" ));
        person_id = rs_e.getInt("concourspersonnel_id");
        jag_id = rs_e.getInt("jaguar_id" );
        stat_p = aConn.createStatement();
        rs_p = stat_p.executeQuery("select * from ConcoursPersonnel where concourspersonnel_id == " + person_id + ";"); 
        while (rs_p.next()) {
            lngMasterPersonnel_ID = rs_p.getLong("masterpersonnel_id");
            stat_m = aConn.createStatement();
            rs_m = stat_m.executeQuery("select * from MasterPersonnel where masterpersonnel_id == " + lngMasterPersonnel_ID + ";"); 
            System.out.println("Owner last: " + rs_m.getString("lastname") + "Owner status: " +  rs_m.getInt("status_o") + "Judge status: " +  rs_m.getInt("status_j") + " JCNA: " + rs_m.getInt("jcna") + " Personnel Node: " + rs_p.getInt("concourspersonnel_node" ));
        }
        rs_p.close();
        
        stat_j = aConn.createStatement();
        rs_j = stat_j.executeQuery("select * from ConcoursJaguars where jaguar_id == " + jag_id + ";"); 
        while (rs_j.next()) {
            System.out.println("Class: " + rs_j.getString("class") + " Year: " + rs_j.getInt("year") + " Description: " + rs_j.getString("description") +" Jaguar Node: " + rs_j.getInt("node" ));
        }
        
        rs_j.close();
    } 
    rs_e.close();
}

public static void DisplayConcoursJudgesTable(Connection aConn, String strPath) throws SQLException{
    //int  jag_id;
    Statement stat_j;
    ResultSet rs_j;
    //Statement stat_p;
    //ResultSet rs_p;
    Statement stat_mp;
    ResultSet rs_cp;
    Statement stat_cp;
    ResultSet rs_mp;
    Long lngConcoursPersonnelID;
    Long lngMasterPersonnelID;
    String q;
            
    System.out.println("\n\n Loop over Concours Judges Table display judge data");

    stat_j = aConn.createStatement();
    rs_j = stat_j.executeQuery("select * from ConcoursJudgesTable;"); 
    stat_cp = aConn.createStatement();
    stat_mp = aConn.createStatement();
    while (rs_j.next()) {
        lngConcoursPersonnelID = rs_j.getLong("concourspersonnel_id");
        q = "select * from ConcoursPersonnel where concourspersonnel_id == " + lngConcoursPersonnelID +  ";";
        rs_cp = stat_cp.executeQuery(q); 
        lngMasterPersonnelID = rs_cp.getLong("masterpersonnel_id");
        q = "select * from MasterPersonnel where masterpersonnel_id == " + lngMasterPersonnelID +  ";";
        rs_mp = stat_mp.executeQuery(q); 

        System.out.println("\njudge_id:" + rs_j.getInt("judge_id") + " concourspersonnel_id: " + lngConcoursPersonnelID + " masterpersonnel_id: " + lngMasterPersonnelID);
        
        //stat_p = aConn.createStatement();
        //rs_p = stat_p.executeQuery("select * from ConcoursPersonnel where concourspersonnel_id == " + person_id + ";"); 
        //while (rs_p.next()) {
        System.out.println("Judge unique name: " + rs_cp.getString("unique_name") +  rs_cp.getInt("status_o") + "Judge status: " +  rs_cp.getInt("status_j") +  " cert_year:" + rs_mp.getInt("cert_year" ) + " JCNA: " + rs_mp.getInt("jcna") + " Concours Personnel Node: " + rs_cp.getInt("concourspersonnel_node" ));
       // }
        rs_cp.close();
        rs_mp.close(); 
        stat_cp.close();
        stat_cp.close();
        
    } 
    rs_j.close();
    stat_j.close();
    
}


public static void DisplayJaguarsByOwner(Connection aConn, String strPath) throws SQLException{
    int  person_id;
    int  jag_id;
    Statement stat_e;
    ResultSet rs_e;
    Statement stat_p;
    ResultSet rs_p;
    Statement stat_j;
    ResultSet rs_j;
    Statement stat_m;
    Statement stat_mj;
    ResultSet rs_m;
    ResultSet rs_mj;
    Long lngMasterPersonnel_ID;
    Long lngMasterJaguar_ID;
    String q;
    String strUniqueName;
    
            
    
    System.out.println("\n\n Loop over Concours Personnel and list all Jaguars for each person");
    stat_p = aConn.createStatement();
    rs_p = stat_p.executeQuery("select * from ConcoursPersonnel;"); 
    while (rs_p.next()) {
        lngMasterPersonnel_ID = rs_p.getLong("masterpersonnel_id");
        stat_m = aConn.createStatement();
        rs_m = stat_m.executeQuery("select * from MasterPersonnel where masterpersonnel_id == " + lngMasterPersonnel_ID + ";"); 
        //System.out.println("Owner last: " + rs_m.getString("lastname") + " JCNA: " + rs_m.getInt("jcna") + " Personnel Node: " + rs_p.getInt("concourspersonnel_node" ));
        //System.out.println("\nOwner last name:" + rs_m.getString("lastname") + " concourspersonnel_id: " + rs_p.getString("concourspersonnel_id") );
        strUniqueName = rs_m.getString("unique_name");
        person_id = rs_p.getInt("concourspersonnel_id");
        stat_e = aConn.createStatement();
        rs_e = stat_e.executeQuery("select * from ConcoursEntries where concourspersonnel_id == " + person_id + "\n;"); 
        while(rs_e.next()){
            jag_id = rs_e.getInt("jaguar_id");
            stat_j = aConn.createStatement();
            rs_j = stat_j.executeQuery("select * from ConcoursJaguars where jaguar_id == " + jag_id + "\n;"); 
        
            while (rs_j.next()) {
                lngMasterJaguar_ID =  rs_j.getLong("masterjaguar_id");
                q =  "select * from MasterJaguar where masterjaguar_id = " + lngMasterJaguar_ID + ";";
                stat_mj =  aConn.createStatement();
                rs_mj = stat_mj.executeQuery(q);
                
                System.out.println("Owner unique name " +strUniqueName+ " Year: " + rs_mj.getInt("year") + " Descrip: " + rs_mj.getString("description") + " Color: " + rs_mj.getString("color") );
                rs_mj.close();                
            }
            rs_j.close();


        }
        rs_e.close();
        rs_m.close();
        System.out.println( );
    } 
    rs_p.close();
    
}
public static void DisplayOwners(Connection aConn, String strPath) throws SQLException{
    // Read Owners from concours database and store in Owners ArrayList
    Statement stat_rowcount;
    ResultSet rs_rowcount;

    Statement stat_co;
    ResultSet rs_co;
    Statement stat_oe;
    ResultSet rs_oe;
    int i;
    int rowCount;
    Long lngConcoursOwner_id;
    Long lngConcoursPersonnel_id;
    String strOwnerUniqueName;
    Integer intConcoursPersonnelNode;
    ArrayList<Integer> aEntrylist;
    
    
    List<Integer> lstEntryList;
    

    try {    
            stat_co = aConn.createStatement();
            rs_co = stat_co.executeQuery("select * from ConcoursOwners;"); 
            
            i = 1;
            while (rs_co.next()) { 
                lngConcoursOwner_id = rs_co.getLong("concoursowner_id");
                lngConcoursPersonnel_id = rs_co.getLong("concourspersonnel_id");
                strOwnerUniqueName = rs_co.getString("unique_name");
                intConcoursPersonnelNode = rs_co.getInt("concourspersonnel_node");
                aEntrylist = new ArrayList<>();
                stat_oe = aConn.createStatement();
                rs_oe = stat_oe.executeQuery("select * from ConcoursOwnersEntries where concoursowner_id == " + lngConcoursOwner_id + ";"); 
                while (rs_oe.next()){
                    Integer theEntryNode = rs_oe.getInt("entrynode");
                    aEntrylist.add(theEntryNode);
                }
                rs_oe.close();
                rs_oe.close();
               System.out.println("Record: " + i + " Owner ID: " + lngConcoursOwner_id + " " + "Personnel ID: " + lngConcoursPersonnel_id + " Unique name:" + strOwnerUniqueName + " Personnel Node: " +  intConcoursPersonnelNode);                
               // theOwners.GetConcoursOwners().add(new Owner(strOwnerUniqueName,  intConcoursPersonnelNode, aEntrylist.size(), aEntrylist));
                i++;
            } 
            rs_co.close();
            stat_co.close();
    
        } catch (SQLException ex) {
            Logger.getLogger(JCNAClasses.class.getName()).log(Level.SEVERE, null, ex);
        }
}
        
public static  void LoadConcoursJudgesTable(Connection aConn, String strPath) throws SQLException{ // e.g., "SDJC2014Judges"
    Integer  iColSelfentryCount = 9;
    Integer  iColRejectCount = 13;
    String [] nextLine;
    String strJudgeLastName; //0
    String strJudgeFirstName; //1
    String strJudgeUniqueName;//2
    Integer intJCNA; //3
    Integer intYear; //4 Cetrification thru year
    String strClub; //5
    String strJudgeID;  //6 not used
    Integer intPersonnelNode; //7

    String strJudgeStatus; //8
    Integer intSelfentryCount; //9 the number of rejected classes to follow
    String strSelfEntry1; //10
    String strSelfEntry2; //11
    String strSelfEntry3;  //12
    Integer intRejectCount; //13 the number of rejected classes to follow
   // Integer iCol;
    String strRejectClass_n;
    String strSelfentryClass_n;
    Long lngMasterPersonnel_id;          
    
    String strCurrentStatus;
    String strNewStatus;
    CSVReader reader;
    String q;
    int iLine;
    int intRecord;
    PreparedStatement prep_p;
    PreparedStatement prep_j;
    PreparedStatement prep_reject;
    PreparedStatement prep_self;
    ResultSet rs;
    //ResultSet rs_mp;
    
    Long lngPersonnel_id;

    Statement stat = aConn.createStatement();
    
    stat.executeUpdate("drop table if exists ConcoursJudgesTable" ); 
    stat.executeUpdate("create table ConcoursJudgesTable (judge_id INTEGER PRIMARY KEY AUTOINCREMENT, concourspersonnel_id INTEGER, UNIQUE (concourspersonnel_id), FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id) );"); // Lists of excluded JCNA Classes are in ConcoursJudgeClassRejectTable table 
    stat.executeUpdate("drop table if exists ConcoursJudgeClassSelfEntryTable" ); 
    stat.executeUpdate("create table ConcoursJudgeClassSelfEntryTable (classselfentry_id INTEGER PRIMARY KEY AUTOINCREMENT, concourspersonnel_id INTEGER, class TEXT,  FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id) );"); // Lists of selfentry JCNA Classes  
    stat.executeUpdate("drop table if exists ConcoursJudgeClassRejectTable" ); 
    stat.executeUpdate("create table ConcoursJudgeClassRejectTable (classreject_id INTEGER PRIMARY KEY AUTOINCREMENT, concourspersonnel_id INTEGER, class TEXT,  FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id) );"); // Lists of excluded JCNA Classes  
    String JudgesCSV = strPath + "\\JudgesCsv.txt" ;
    try {
        reader = new CSVReader(new FileReader(JudgesCSV ));
        iLine = 0;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            // System.out.println(nextLine[0] + nextLine[1] );
            // 
            if(nextLine[0].isEmpty()){
                System.out.println("Empty line in LoadConcoursJudgesTable");
                
            }
            strJudgeUniqueName = "";
            if (iLine > 0 && !nextLine[0].isEmpty()){
                strJudgeLastName = nextLine[0].trim();
                strJudgeFirstName = nextLine[1].trim();
                strJudgeUniqueName = nextLine[2].trim();
                intJCNA = Integer.parseInt(nextLine[3]) ;
                intYear= Integer.parseInt(nextLine[4]); // Cetrification thru year
                strClub= nextLine[5].trim(); // not used here
                strJudgeID = nextLine[6].trim(); // not used here
                intPersonnelNode = Integer.parseInt(nextLine[7]);
                strJudgeStatus = nextLine[8].trim();
                strSelfEntry1 = nextLine[9].trim();
                strSelfEntry2 = nextLine[10].trim();
                strSelfEntry3 = nextLine[11].trim();
                intRejectCount = Integer.parseInt(nextLine[iColRejectCount]);
                intSelfentryCount = Integer.parseInt(nextLine[iColSelfentryCount]);

                Statement stmt = aConn.createStatement();
                // 
                // Get concourspersonnel_id from ConcoursPersonnel, keying off of Judge intPersonnelNode
                //   Also set status_j to true
                //
                rs = stmt.executeQuery("SELECT *  FROM ConcoursPersonnel where concourspersonnel_node == " + intPersonnelNode);  
                lngPersonnel_id = 0L;
                if (rs.next()) {
                    lngPersonnel_id = rs.getLong("concourspersonnel_id")  ;
                    q = "UPDATE ConcoursPersonnel SET status_j = 1 where concourspersonnel_id == " + lngPersonnel_id  + ";";
                    stat.executeUpdate(q);
                    aConn.commit();
                }
                else{
                    String msg = "Judge node " + intPersonnelNode + " name " + strJudgeUniqueName + " not found in ConcoursPersonnel. Will add  to ConcoursPersonnel";
                    okDialog(msg);
                    theConcours.GetLogger().log(Level.INFO, msg);
//Fixed to allow names with apostrophy etc.                 rs_mp = stmt.executeQuery("SELECT masterpersonnel_id  FROM MasterPersonnel where unique_name like '" +  strJudgeUniqueName + "';");
                   String q_mp =  "select masterpersonnel_id from MasterPersonnel where  unique_name like ?"; 
                   PreparedStatement prepStmt_mp1 = aConn.prepareStatement(q_mp);
                   prepStmt_mp1.setString(1, strJudgeUniqueName);
                   ResultSet rs_mp = prepStmt_mp1.executeQuery();

                   lngMasterPersonnel_id = 0L; // Will not be used
                    if (rs_mp.next()) {
                        lngMasterPersonnel_id = rs_mp.getLong("masterpersonnel_id")  ;
                    }
                    else{
                        msg = "ERROR: Judge name " + strJudgeUniqueName + " not found in MasterPersonnel";
                        okDialog(msg);
                        theConcours.GetLogger().info(msg);
                        rs_mp.close();
                        prepStmt_mp1.close();
                        System.exit(-1);
                    }
                    rs_mp.close();
                    prepStmt_mp1.close();
                    // Now that we have lngMasterPersonnel_id we can complete the insert into ConcoursPersonnel table
                    prep_p = aConn.prepareStatement( "insert into ConcoursPersonnel ('masterpersonnel_id', 'unique_name', 'status_j', 'concourspersonnel_node' ) values ( ?,  ?, ?, ?);"); 
                    prep_p.setLong(1,lngMasterPersonnel_id); 
                    prep_p.setString(2, strJudgeUniqueName);
                    prep_p.setInt(3, 1);
                    prep_p.setInt(4, intPersonnelNode); // this was set in the Excel VBA code
                    prep_p.addBatch(); 
                   // aConn.setAutoCommit(false); // starts transaction
                    prep_p.executeBatch(); 
                     aConn.commit();
                   //aConn.setAutoCommit(true);  // ends transaction 
                    
                }
                                   

                rs.close();

                
                prep_j = aConn.prepareStatement( "insert into ConcoursJudgesTable ('concourspersonnel_id' ) values ( ?);"); 
                prep_j.setInt(1,intPersonnelNode);  // Judge node and Personnel node are the same... this was enforced when the CSV files were created
                prep_j.addBatch(); 
               // aConn.setAutoCommit(false); // starts transaction
                prep_j.executeBatch(); 
                aConn.commit();
                //
                //  Build table of Self-entry JCNA Classes for each judge                                    
                //
                rs = stmt.executeQuery("SELECT concourspersonnel_id  FROM ConcoursPersonnel where concourspersonnel_node == " + intPersonnelNode);  
                lngPersonnel_id = 0L;
                if (rs.next()) {
                    lngPersonnel_id = rs.getLong("concourspersonnel_id")  ;
                }
                else{
                    String msg = "ERROR: Could not find intPersonnelNode " + intPersonnelNode + " in ConcoursPersonnel";
                    okDialog(msg);
                    theConcours.GetLogger().info(msg);
                    System.exit(-1);
                }
                
                
               prep_self = aConn.prepareStatement( "insert into ConcoursJudgeClassSelfEntryTable ('concourspersonnel_id', 'class' ) values ( ?,  ?);"); 
               for(int iColSC = 1+iColSelfentryCount; iColSC <= (intSelfentryCount + iColSelfentryCount); iColSC++){
                   strSelfentryClass_n = nextLine[iColSC].trim();
                   prep_self.setLong(1, lngPersonnel_id);
                   prep_self.setString(2, strSelfentryClass_n );
                   prep_self.addBatch();
                   prep_self.executeBatch();
               }

                //
                //  Build table of Excluded JCNA Classes for each judge                                    
                //
                rs = stmt.executeQuery("SELECT concourspersonnel_id  FROM ConcoursPersonnel where concourspersonnel_node == " + intPersonnelNode);  
                lngPersonnel_id = 0L;
                if (rs.next()) {
                    lngPersonnel_id = rs.getLong("concourspersonnel_id")  ;
                }
                else{
                    String msg = "ERROR: Could not find intPersonnelNode " + intPersonnelNode + " in ConcoursPersonnel";
                    okDialog(msg);
                    theConcours.GetLogger().info(msg);
                    System.exit(-1);
                }
                
                // There's something I don't understand about "SELECT last_insert_rowid()" ... sometimes works, sometimes doesn't
                /*stat = aConn.createStatement();
                rs = stat.executeQuery("SELECT last_insert_rowid()");
                lngPersonnel_id = 0L; // won't be used
                if (rs.next()) {
                    lngPersonnel_id = rs.getLong(1);
                }
                else{
                    System.out.println("couldn't get last row id into ConcoursJudgesTable");
                    System.exit(-1);
                }
                */
                
               prep_reject = aConn.prepareStatement( "insert into ConcoursJudgeClassRejectTable ('concourspersonnel_id', 'class' ) values ( ?,  ?);"); 
               for(int iColRC = 1+iColRejectCount; iColRC <= (intRejectCount + iColRejectCount); iColRC++){
                   strRejectClass_n = nextLine[iColRC].trim();
                   prep_reject.setLong(1, lngPersonnel_id);
                   prep_reject.setString(2, strRejectClass_n );
                   prep_reject.addBatch();
                   prep_reject.executeBatch();
               }

               
               
            }
            intRecord = iLine + 1;
            //System.out.println("CSV record: " + intRecord + "Judge unique name: " + strJudgeUniqueName);

            iLine++;
        }
    }    catch (FileNotFoundException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }    
        catch (IOException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }



}

//  
//  Unique names in the database are composed of ONLY last name + first 3 (max) characters of first name, whereas
//  the UniqueNames in memory might also have a MI. Consequently, wnen searching the database we have to construct
//  unique names with this function.
//  
public static String UniqueName(String aLastName, String aFirstName) {
    String strLastName = aLastName.trim();
    String strFirstName = aFirstName.trim();
    
                    if(strFirstName.length() >= 3){
                        return strLastName + strFirstName.substring(0,3);
                    }
                    else{
                        return strLastName + strFirstName.substring(0,strFirstName.length());
                    }
}

public static String UniqueDescription(String aDescription, String aUniqueName) {
    String strDescription = aDescription.trim();
    String strUniqueName  = aUniqueName.trim();
                    if(strDescription.length() >= 5){
                        return strDescription.substring(0,5).trim() + "_" + strUniqueName;
                    }
                    else{
                        return strDescription.substring(0,strDescription.length()).trim() + "_" + strUniqueName;
                    }
}

public void UpdateAddConcoursPersonnelDBTable(Connection aConn, Logger aLogger, ConcoursPerson aConcoursPerson){
    String q;
    //q = "insert into ConcoursPersonnel ('masterpersonnel_id', 'unique_name', 'status_o', 'status_j', 'concourspersonnel_node' ) values ( ?, ?, ?, ?, ?);";
        PreparedStatement prep_p = null;
        try {
          prep_p = aConn.prepareStatement( "insert into ConcoursPersonnel ('masterpersonnel_id', 'unique_name', 'status_o', 'status_j', 'concourspersonnel_node' ) values ( ?, ?,  ?, ?, ?);"); 
        } catch (SQLException ex) {
            okDialog("ERROR: SQLException in UpdateAddConcoursPersonnelDBTable");
            aLogger.log(Level.SEVERE, "ERROR: In UpdateAddConcoursPersonnelDBTable", ex);
            System.exit(-1);
        }
        
    try {
        aConn.setAutoCommit(false);

          // ConcoursPersonnel : masterpersonnel_id, unique_name, status_o, status_j, concourspersonnel_node
          prep_p.setLong(1, aConcoursPerson.GetMasterPersonnel_id());
          prep_p.setString(2, aConcoursPerson.GetUniqueName());
          prep_p.setInt(3, aConcoursPerson.GetStatus_o());
          prep_p.setInt(4, aConcoursPerson.GetStatus_j());
          prep_p.setInt(5, aConcoursPerson.GetConcoursPersonnel_node()); 
          prep_p.addBatch();
          prep_p.executeBatch();
          aConn.commit();
          
          prep_p.close();
      } catch (SQLException ex) {
          String msg = "ERROR: SQLException in UpdateAddConcoursPersonnelDBTable";
          okDialog(msg);
          aLogger.log(Level.SEVERE, msg, ex);
          System.exit(-1);
      }
}

public void UpdateSetstatus_oConcoursPersonnelDBTable(Connection aConn,  Long aMasterPerson_id, int aSetValue){
        try {
            Statement stmt;
            stmt= aConn.createStatement();
            String q = "UPDATE ConcoursPersonnel SET status_o = " + aSetValue +  " where masterpersonnel_id == " + aMasterPerson_id  + ";";
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            String msg = "ERROR: SQLException in UpdateSetstatus_oConcoursPersonnelDBTable";
            theConcours.GetLogger().info(msg);
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
}
/*
 *   Sets Judge status in ConcoursPersonnel DB table using masterperson_id
*/    
public void UpdateSetstatus_jConcoursPersonnelDBTable(Connection aConn,  Long aMasterPerson_id, int aSetValue){
        try {
            Statement stmt;
            stmt= aConn.createStatement();
            String q = "UPDATE ConcoursPersonnel SET status_j = " + aSetValue +  " where masterpersonnel_id == " + aMasterPerson_id  + ";";
            stmt.executeUpdate(q);
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
}

/*
 *   Sets Judge status in ConcoursPersonnel DB table using unique name
*/    
public void UpdateSetstatus_jConcoursPersonnelDBTable(Connection aConn,  String aUniqueName, int aSetValue){
/*
String updateTableSQL = "UPDATE DBUSER SET USERNAME = ? WHERE USER_ID = ?";
PreparedStatement preparedStatement = dbConnection.prepareStatement(updateTableSQL);
preparedStatement.setString(1, "mkyong_new_value");
preparedStatement.setInt(2, 1001);
// execute insert SQL stetement
preparedStatement .executeUpdate();    
    */
    
    try {
            Statement stmt;
            stmt= aConn.createStatement();
//Fixed to allow names with apostrophy etc.            String q = "UPDATE ConcoursPersonnel SET status_j = " + aSetValue +  " where unique_name like '" + aUniqueName  + "';";
//            stmt.executeUpdate(q);
            String q = "update ConcoursPersonnel set status_j = ? where unique_name like ?";
            PreparedStatement prepStmt = aConn.prepareStatement(q);
            prepStmt.setInt(1, aSetValue);
            prepStmt.setString(2, aUniqueName);
            int result = prepStmt.executeUpdate();
            if (result != 1) {
                okDialog("ERROR: There are " + result + " records in ConcoursPersonnel were updated. Should be 1");
                theConcours.GetLogger().info("ERROR: There are " + result + " records in ConcoursPersonnel were updated. Should be 1");
                prepStmt.close();
                System.exit(-1);
            } 
            prepStmt.close();
            
        } catch (SQLException ex) {
            
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
}

/*
  * UpdateConcoursOwnersDBTable() assumes that ConcoursPersonnel table has already been updated to include the Concours Person becoming an Owner
*/
public void UpdateAddConcoursOwnersDBTable(Connection aConn, Owner aOwner){
            String strUniqueName;
            Integer intPersonnelNode;
            Long lngConcoursPersonnelID;
            Statement stmt;
            ResultSet rs;
            
        try {
            PreparedStatement prep_o;
            prep_o = aConn.prepareStatement( "insert into ConcoursOwners ('concourspersonnel_id', 'unique_name',  'concourspersonnel_node' ) values (?, ?, ?);");
            strUniqueName = aOwner.getUniqueName();
            intPersonnelNode = aOwner.GetNode();
            lngConcoursPersonnelID = 0L;
            stmt= aConn.createStatement();
            rs = stmt.executeQuery("SELECT concourspersonnel_id  FROM ConcoursPersonnel where concourspersonnel_node == " + intPersonnelNode);
            if (rs.next()) {
                lngConcoursPersonnelID = rs.getLong("concourspersonnel_id")  ;
            }
            else{
                okDialog("Could not find Personnel Node " + intPersonnelNode + " in ConcoursPersonnel");
                theConcours.GetLogger().info("Could not find Personnel Node " + intPersonnelNode + " in ConcoursPersonnel");
                System.exit(-1);
            }
            prep_o.setLong(1, lngConcoursPersonnelID);
            prep_o.setString(2, strUniqueName);
            prep_o.setInt(3, intPersonnelNode);
            prep_o.addBatch();
            prep_o.executeBatch();
            //aConn.commit();
            prep_o.close();
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

    
}

/*
  * 
*/
public void UpdateAddConcoursEntriesAndJaguarsDBTables(Connection aConn, Concours aConcours, Entry aNewEntry, MasterJaguar aSelectedMasterJaguar, ConcoursClass aConcoursClass){
                
    String strEntryName;
    Long lngConcoursPersonnel_id;
   // PreparedStatement prep_e;
 //   PreparedStatement prep_oe;
   // Statement stmt;
    String strOwnerUnique;
    Long lngConcoursOwner_id;
   // Long lngOwnerEntry_id;
    //Long lngConcoursClasses_id;
    strEntryName = aNewEntry.GetID();
    strOwnerUnique = aNewEntry.GetOwnerUnique();
    //Long mpid = aConcours.theMasterPersonnel.GetMasterPersonnelID(strOwnerUnique);
    String strClass = aConcoursClass.GetClassName();
    Long lngMasterJaguarID = aSelectedMasterJaguar.getMasterJaguarID(); //  PROBLEM HERE If this was just added through adding a new master person the result will be null
    Long lngJaguar_id;
    Integer intEntryNode = aNewEntry.GetNode(); // same for both Entry & Jaguar
    //
    // DEBUGGING
    //
    /*
    System.out.println(" DisplayJaguarDB before insert into ConcoursOwnersEntries");
    DisplayJaguarDB(aConn);
    */
    
    try {
        aConn.setAutoCommit(false);
        lngConcoursPersonnel_id = GetConcoursPersonnel_idDB(aConn, strOwnerUnique);
        String q = "insert into ConcoursJaguars ('class', 'masterjaguar_id', 'node' ) values ( \"" + strClass + "\", " + lngMasterJaguarID + ", " + intEntryNode + ");";
        // Insert into ConcoursJaguars DB
        Statement stmt_j = aConn.createStatement();
        stmt_j.executeUpdate(q);
        //aConn.commit();
        // Insert into ConcoursEntries DB
        
        ResultSet rs_j = stmt_j.getGeneratedKeys();
        lngJaguar_id = rs_j.getLong("last_insert_rowid()"); // Entry node and Jaguar node s.b. same
        
        q = "insert into ConcoursEntries ('entry_name', 'concourspersonnel_id', 'jaguar_id' ) values (\"" + strEntryName + "\", " + lngConcoursPersonnel_id + ", " + lngJaguar_id + ");";
        Statement stmt_ce = aConn.createStatement();
        stmt_ce.executeUpdate(q);
        
//        System.out.println(" DisplayJaguarDB after insert into ConcoursEntries");
        //Debugging
 //       DisplayJaguarDB(aConn);

            // aConn.commit();
        // Insert into ConcoursOwnersEntries DB
        lngConcoursOwner_id = GetConcoursOwner_idDB(aConn, strOwnerUnique);
        q = "insert into ConcoursOwnersEntries ( 'concoursowner_id', 'entrynode' ) values ( " + lngConcoursOwner_id  + ", " + intEntryNode + ");"; 
        Statement stmt_oe = aConn.createStatement();
        stmt_oe.executeUpdate(q);
        // Insert into ConcoursClassesEntries DB

        // The new entry is added to the ConcourseClassesEntries table elsewhere.
        /*
        lngConcoursClasses_id = GetConcoursClasses_idDB(aConn, strClass);
        q = "insert into ConcoursClassesEntries ( 'concoursclasses_id', 'entry_node' ) values ( " + lngConcoursClasses_id  + ", " + intEntryNode + ");"; 
        Statement stmt_cce = aConn.createStatement();
        stmt_cce.executeUpdate(q);
        */
        
        
        // close stuff
        //prep_e.close();
        stmt_ce.close(  );
        stmt_j.close();
        stmt_oe.close();
        //stmt_cce.close();
        rs_j.close();
        aConn.commit();  // This is important... without it the actions taken won't be commited tot he DB till the next time around!
        aConn.setAutoCommit(true);
    } catch (SQLException ex) {
        String msg = "ERROR: SQLException UpdateAddConcoursEntriesAndJaguarsDBTables";
        okDialog(msg);
        theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        System.exit(-1);
    }
    
} // EndUpdateAddConcoursEntriesAndJaguarsDBTables


/*
  UpdateRemoveConcoursEntriesAndJaguarsDBTables does the following:

  Remove the  Entry Jaguar from ConcoursJaguars DB table
  Remove the selected Entry from the ConcoursEntries DB table
  
  Remove the selected Entry from the ConcoursClassesEntries DB table
  If this was the last Entry in it's ConcoursClass, remove the class from ConcoursClasses

  Remove the selected Entry from the ConcoursOwnersEntries DB table
  If this was the last Entry for theOwner, remove from ConcoursOwners table 
    AND if the Owner isn't a Judge remove from ConcoursPersonnel table


*/

public void UpdateRemoveConcoursEntryDBTable(Connection aConn, Integer aEntryNode) {
    Integer intEntryNode;// same for both Entry & Jaguar
    Long lngJaguar_id = null;
    Long lngLoncoursowner_id = null;
    String q;
            // Get jaguar_id
        try {
            ResultSet rs_1;
            Statement stmt_1;
            stmt_1 = aConn.createStatement();
            q = "select * from ConcoursJaguars where node = " + aEntryNode +";";
            rs_1 = stmt_1.executeQuery(q);
            if(rs_1.next()) { // sets the cursor. Note there can be only one result...
                lngJaguar_id = rs_1.getLong("jaguar_id");
            } else{
                okDialog("Error in UpdateRemoveConcoursEntryDBTabl. Entry node " + aEntryNode + " not found in ConcoursJaguars DB table ");
                //System.exit(-1);
                theConcours.GetLogger().info("Error in UpdateRemoveConcoursEntryDBTabl. Entry node " + aEntryNode + " not found in ConcoursJaguars DB table ");
                return;
            }  
            stmt_1.close();
            rs_1.close( );
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
            // now remove the ConcoursEntries row
        try{
            q = "delete from ConcoursEntries where jaguar_id =  " + lngJaguar_id  + ";";      
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
            
      

}

    public void UpdateRemoveOwnersEntriesDBTable(Connection aConn, Integer aEntryNode){
        try {
            String q = "delete from ConcoursOwnersEntries where entrynode = " + aEntryNode  + ";";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
        public void UpdateRemoveConcoursJaguarsDBTable(Connection aConn, Integer aEntryNode){
        try {
            String q = "delete from ConcoursJaguars where node = " + aEntryNode  + ";";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
        
        public void UpdateRemoveJudgeAssignmentsDBTable(Connection aConn, Integer aEntryNode){
            String q;
            Long lngJA_id = 0L;
            Statement stmt_1;
            ResultSet rs_1;
        try {
            // This is tricky--- have to find the entry's row in JudgeAssignmentsTable and
            // the delete corresponding rows in EntryJudgesTable. Only then can we delete the row in JudgeAssignmentsTable
            q = "select * from JudgeAssignmentsTable where car_node = " + aEntryNode  + ";";
            stmt_1 = aConn.createStatement();
            rs_1 = stmt_1.executeQuery(q);
            if(rs_1.next()) { // sets the cursor. Note there can be only one result...
                lngJA_id = rs_1.getLong("judgeassignment_id");
            } else{
                okDialog("Error in UpdateRemoveJudgeAssignmentsDBTable. Entry node " + aEntryNode + " not found in JudgeAssignmentsTable DB table ");
                theConcours.GetLogger().info("Error in UpdateRemoveJudgeAssignmentsDBTable. Entry node " + aEntryNode + " not found in JudgeAssignmentsTable DB table ");
                //System.exit(-1);
                stmt_1.close();
                rs_1.close();
                return;
            }  
            stmt_1.close();
            rs_1.close();
            q = "delete from EntryJudgesTable where judgeassignment_id = " + lngJA_id  + ";";
            Statement stmt_2 = aConn.createStatement();
            stmt_2.executeUpdate(q);
            // Now we can delete the row in JudgeAssignmentsTable
            q = "delete from JudgeAssignmentsTable where judgeassignment_id = " + lngJA_id  + ";";
            Statement stmt_3 = aConn.createStatement();
            stmt_3.executeUpdate(q);
            stmt_2.close();
            stmt_3.close();
            
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        
        
    }

        
    

    
    /*
      Note that Owner node is same as Personnel node
    */
    public void UpdateRemoveConcoursOwner(Connection aConn, Integer aOwnerNode){
        try {
            String q = "delete from ConcoursOwners where concourspersonnel_node = " + aOwnerNode  + ";";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

public void UpdateRemoveConcoursJudgeSelfEntryByClass(Connection aConn, String aClassName){
        try {
            String q = "delete from ConcoursJudgeClassSelfEntryTable where class like '" + aClassName  + "';";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

}

public void UpdateRemoveConcoursJudgeClassRejectByName(Connection aConn, String aUniquePersonName){
        try {
            //Statement stmt_1 = null;
            //stmt_1 = aConn.createStatement();
            //Fixed to allow names with apostrophy etc.           String q = "select concourspersonnel_id from ConcoursPersonnel where unique_name like  '" + aUniquePersonName + "';";            
            String q_mp =  "select * from ConcoursPersonnel where  unique_name like ?";
            PreparedStatement prepStmt_mp1;
            prepStmt_mp1 = aConn.prepareStatement(q_mp);
            prepStmt_mp1.setString(1, aUniquePersonName);
            ResultSet rs_mp1 = prepStmt_mp1.executeQuery();
            //ResultSet rs_1 = null;
            //rs_1 = stmt_1.executeQuery(q);
            Long aconcoursePersonnel_id = 0L;
            if(rs_mp1.next()){
                aconcoursePersonnel_id = rs_mp1.getLong("concourspersonnel_id");
            } else {
                rs_mp1.close();
                prepStmt_mp1.close();
                String msg = "ERROR: Could not find unique_name " + aUniquePersonName + " in MasterPersonnel";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            rs_mp1.close();
            prepStmt_mp1.close();
            
            String q = "delete from ConcoursJudgeClassRejectTable where concourspersonnel_id = " + aconcoursePersonnel_id  + ";";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
           theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }

}


public void UpdateRemoveConcoursJudgeSelfEntryByName(Connection aConn, String aUniquePersonName){
        try {
// Fixed to allow names with apostrophy etc.           String q = "select concourspersonnel_id from ConcoursPersonnel where unique_name like  '" + aUniquePersonName + "';";            
//            ResultSet rs_1 =stmt_1.executeQuery(q);
            String q_mp =  "select concourspersonnel_id from ConcoursPersonnel where  unique_name like ?";
            PreparedStatement prepStmt_mp1;
            prepStmt_mp1 = aConn.prepareStatement(q_mp);
            prepStmt_mp1.setString(1, aUniquePersonName);
            ResultSet rs_mp1 = prepStmt_mp1.executeQuery();
            Long aconcoursePersonnel_id = null;
            if(rs_mp1.next()){
                aconcoursePersonnel_id = rs_mp1.getLong("concourspersonnel_id");
            } else {
                rs_mp1.close();
                prepStmt_mp1.close();
                String msg = "ERROR: Could not find unique_name " + aUniquePersonName + " in ConcoursPersonnel";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            String q = "delete from ConcoursJudgeClassSelfEntryTable where concourspersonnel_id = " + aconcoursePersonnel_id  + ";";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            prepStmt_mp1.close();
            rs_mp1.close();
            stmt.close();
        } catch (SQLException ex) {
            String msg = "ERROR: SQLException in UpdateRemoveConcoursJudgeSelfEntryByName";
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, msg, ex);
        }

}
/*
   Removes Judge from ConcoursJudgesTable
*/
public void UpdateRemoveConcoursJudgesTableByName(Connection aConn, String aUniquePersonName){
        try {
//            Statement stmt_1 = aConn.createStatement();
//Fixed to allow names with apostrophy etc.            String q = "select concourspersonnel_id from ConcoursPersonnel where unique_name like  '" + aUniquePersonName + "';";            
//            ResultSet rs_1 =stmt_1.executeQuery(q);
            String q_mp =  "select concourspersonnel_id from ConcoursPersonnel where  unique_name like ?";
            PreparedStatement prepStmt_mp1;
            prepStmt_mp1 = aConn.prepareStatement(q_mp);
            prepStmt_mp1.setString(1, aUniquePersonName);
            ResultSet rs_mp1 = prepStmt_mp1.executeQuery();
            //Long aconcoursePersonnel_id = rs_1.getLong("concourspersonnel_id");
            Long aconcoursePersonnel_id = null;
            if(rs_mp1.next()){
                aconcoursePersonnel_id = rs_mp1.getLong("concourspersonnel_id");
            } else {
                rs_mp1.close();
                prepStmt_mp1.close();
                String msg = "ERROR: Could not find unique_name " + aUniquePersonName + " in ConcoursPersonnel";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            rs_mp1.close();
            prepStmt_mp1.close();
            String q = "delete from ConcoursJudgesTable where concourspersonnel_id = " + aconcoursePersonnel_id  + ";";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

}
/*
   This is done after removing a Judge or Entry because node numbers are no linger valid
   Has to be recreated anyway.... by running the assignment/matching 

   Revised 3/29/2017 to avoid Foreign Key errors. Previously, the JudgeAssignmentsTable was removed first,
   leaving EntryJudgesTable refering to nonexistant JudgeAssignmentsTable entries.
*/
public void ClearJudgeAssignmentsTables(Connection aConn){
        try {
            Statement stat;
            String q;
            aConn.setAutoCommit(false);
            // drop & recreate EntryJudgesTable
            Statement stat2 = aConn.createStatement();
            stat2.executeUpdate("drop table if exists EntryJudgesTable;");
            aConn.commit();
            stat2.close();

            stat = aConn.createStatement();
            stat.executeUpdate("drop table if exists JudgeAssignmentsTable;");
            aConn.commit();
            stat.close();
            
            Statement stat_ja = aConn.createStatement();
            q = "create table JudgeAssignmentsTable ('judgeassignment_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'car_node' INTEGER NOT NULL, 'owner_node' INTEGER NOT NULL, 'timeslot' INTEGER);";
            stat_ja.executeUpdate(q);
            aConn.commit();
            stat_ja.close();
            
            Statement stat_ej = aConn.createStatement();
            q = "create table EntryJudgesTable ('entryjudges_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'judgeassignment_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL,  FOREIGN KEY (judgeassignment_id) REFERENCES JudgeAssignmentsTable (judgeassignment_id));";
            stat_ej.executeUpdate(q);
            aConn.commit();
            stat_ej.close();

        } catch (SQLException ex) {
            String msg = "SQLException in ClearJudgeAssignmentsTables";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
    
}
public void UpdateRemoveConcoursPersonnel(Connection aConn, Integer aConcoursPersonNode){
        try {
            String q = "delete from ConcoursPersonnel where concourspersonnel_node = " + aConcoursPersonNode  + ";";
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            theConcours.GetLogger().log(Level.SEVERE, "SQL exception in UpdateRemoveConcoursPersonnel", ex);
        }
    
}
public static void CreateSettingsStateTable(Connection aConn){
        try {
            Statement stat_cc = aConn.createStatement();
            stat_cc.executeUpdate("drop table if exists SettingsState;"); 
            String q = "create table SettingsState ('settingsstate_id' INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ", 'judgeassignmentstate' INTEGER NOT NULL DEFAULT 0"
                    + ", 'concoursname' DEFAULT NULL"
                    + ");";
            stat_cc.executeUpdate(q);
            stat_cc.close();
            Statement stat_init = aConn.createStatement();
            q = "insert into SettingsState ( 'judgeassignmentstate') values ( " + 0  +  ");"; // leaves concoursname Null

            stat_init.executeUpdate(q);
            stat_init.close();
        } catch (SQLException ex) {
            String msg = "SQL exception imsgn CreateSettingsStateTable";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
    
}

public static void addNewColumnsToUserSettingsTable(Connection aConn, Logger aLogger) throws SQLException{
        aConn.setAutoCommit(false);
        aLogger.info("Starting addNewColumnsToUserSettingsTable");
        // drop tempOldUserSettingsTable if exists
        String q_delete = "drop table if exists tempOldUserSettings";
        Statement statDropIfExists1 = aConn.createStatement();
        statDropIfExists1.executeUpdate(q_delete);
        statDropIfExists1.close();
        
        // Rename the current UserSettings table to tempUserSettings
        Statement statRenameTable1 = aConn.createStatement();
        String q_RenameTable = "ALTER TABLE UserSettings RENAME TO tempOldUserSettings";
        statRenameTable1.executeUpdate(q_RenameTable);
        statRenameTable1.close();
        // 8/12/2018 added compression field... max number of Classes per column while merging columns for compact schedule
        // Create a new table, reusing the name of the original table, but with wanted new column
        Statement statCreateNewTable1 = aConn.createStatement();
        String q_CreateNewTable = "create table UserSettings ('usersettings_id' INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ", 'starttime' TEXT NOT NULL DEFAULT 10"
                    + ", 'timeslotinterval' TEXT NOT NULL DEFAULT 20"  
                    + ", 'slotsbeforelunch' TEXT NOT NULL DEFAULT 5"  
                    + ", 'lunchinterval' TEXT NOT NULL DEFAULT 60"  
                    + ", 'titlefontsize' INTEGER NOT NULL DEFAULT 12"  
                    + ", 'subtitlefontsize' INTEGER NOT NULL DEFAULT 8"  
                    + ", 'cellfontsize' INTEGER NOT NULL DEFAULT 6"  
                    + ", 'headerfontsize' INTEGER NOT NULL DEFAULT 6"  
                    + ", 'footnotefontsize' INTEGER NOT NULL DEFAULT 6"  
                    + ", 'username' TEXT  DEFAULT Unknown"  
                    + ", 'concourschair' TEXT NOT NULL DEFAULT Unknown"  
                    + ", 'chiefjudge' TEXT NOT NULL DEFAULT Unknown"  
                    + ", 'compression' TEXT NOT NULL DEFAULT 4"  
                   + ");";
        statCreateNewTable1.executeUpdate(q_CreateNewTable);
        statCreateNewTable1.close();
        
        // Insert data from tempOldUserSettings into UserSettings
        //
        // IF THE FILE WAS CREATED BY EARLIER CB VERSION footnotfontsize, instead of  footnotefontsizeis is existing table,
        // but if it was created with current version, it's footnotefontsize
        // Code should work bot ways!!!!
        //
        Statement statInsertData1 = aConn.createStatement(); 
        String q_InsertData = null;
               //                                               usersettings_id, starttime, timeslotinterval, slotsbeforelunch, lunchinterval, titlefontsize, cellfontsize, headerfontsize, footnotfontsize, username FROM tempOldUserSettings
        if(userSettingsColNamesInclude(aConn, theConcours, aLogger, "footnotfontsize")){
            q_InsertData = "INSERT INTO UserSettings (usersettings_id, starttime, timeslotinterval, slotsbeforelunch, lunchinterval, titlefontsize, cellfontsize, headerfontsize, footnotefontsize, username) SELECT usersettings_id, starttime, timeslotinterval, slotsbeforelunch, lunchinterval, titlefontsize, cellfontsize, headerfontsize, footnotfontsize, username FROM tempOldUserSettings";        
        } else {
            q_InsertData = "INSERT INTO UserSettings (usersettings_id, starttime, timeslotinterval, slotsbeforelunch, lunchinterval, titlefontsize, cellfontsize, headerfontsize, footnotefontsize, username) SELECT usersettings_id, starttime, timeslotinterval, slotsbeforelunch, lunchinterval, titlefontsize, cellfontsize, headerfontsize, footnotefontsize, username FROM tempOldUserSettings";        
        }
        statInsertData1.executeUpdate(q_InsertData);
        aLogger.info("Inserted data into UserSettings into table with new columns in addNewColumnsToUserSettingsTable");
        statInsertData1.close();
         
       /* This encounters a locked table... not necessary anyway ?????
        q_delete = "drop table if exists tempOldUserSettings";
        Statement statDropIfExists2 = aConn.createStatement();
        statDropIfExists2.executeUpdate(q_delete);
        statDropIfExists2.close();
        */
        
        aLogger.info("Dropped the temporary file in addNewColumnsToUserSettingsTable");
        //
        //  Now, set the new fields
        //
        aLogger.info("Write data into new fields in UserSettings table");
        String q_Update = "UPDATE UserSettings SET subtitlefontsize = ? WHERE usersettings_id = ?";
        PreparedStatement psUpdate1 = aConn.prepareStatement(q_Update);
        psUpdate1.setString(1, "8");
        psUpdate1.setString(2, "1");
        psUpdate1.executeUpdate();
        aConn.commit();
        psUpdate1.close();

        q_Update = "UPDATE UserSettings SET concourschair = ? WHERE usersettings_id = ?";
        PreparedStatement psUpdate2 = aConn.prepareStatement(q_Update);
        psUpdate2.setString(1, "Unknown");
        psUpdate2.setString(2, "1");
        psUpdate2.executeUpdate();
        aConn.commit();
        psUpdate2.close();
        
        q_Update = "UPDATE UserSettings SET concourschair = ? WHERE usersettings_id = ?";
        PreparedStatement psUpdate3 = aConn.prepareStatement(q_Update);
        psUpdate3.setString(1, "Unknown");
        psUpdate3.setString(2, "1");
        psUpdate3.executeUpdate();
        aConn.commit();
        psUpdate3.close();
        
        aConn.setAutoCommit(true);
  }


public static void CreateUserSettingsTable(Connection aConn){
        // Not sure the DEFAULT values will be used since actual values will be set by SetUserSettingsTable
        try {
            aConn.setAutoCommit(false);
            Statement stat = aConn.createStatement();
            stat.executeUpdate("drop table if exists UserSettings;");
            String q = "create table UserSettings ('usersettings_id' INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ", 'starttime' TEXT NOT NULL DEFAULT 10"
                    + ", 'timeslotinterval' TEXT NOT NULL DEFAULT 20"  
                    + ", 'slotsbeforelunch' TEXT NOT NULL DEFAULT 5"  
                    + ", 'lunchinterval' TEXT NOT NULL DEFAULT 60"  
                    + ", 'titlefontsize' INTEGER NOT NULL DEFAULT 12"  
                    + ", 'subtitlefontsize' INTEGER NOT NULL DEFAULT 8"  
                    + ", 'cellfontsize' INTEGER NOT NULL DEFAULT 6"  
                    + ", 'headerfontsize' INTEGER NOT NULL DEFAULT 6"  
                    + ", 'footnotefontsize' INTEGER NOT NULL DEFAULT 6"  
                    + ", 'username' TEXT  DEFAULT Unknown"  
                    + ", 'concourschair' TEXT NOT NULL DEFAULT Unknown"  
                    + ", 'chiefjudge' TEXT NOT NULL DEFAULT Unknown"  
                    + ", 'compression' INTEGER NOT NULL DEFAULT 4"  
                   + ");";
            stat.executeUpdate(q);
            aConn.commit();
            stat.close();
            aConn.setAutoCommit(true);
        } catch (SQLException ex) {
            String msg = "SQL exception in CreateUserSettingsTable";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
    
}

public static void SetUserSettingsTable(Connection aConn, String aStartTime, String aSlotIntervalMinutes, String aSlotsBeforeLunch, String aLunchIntervalMinutes,
                                                          Integer aTitleFontSize, Integer aSubtitleFontSize, Integer aCellFontSize, Integer aHeaderFontSize, Integer aFootnoteFontSize,
                                                          String aUserName,  String aConcoursChair,  String aChiefJudge, Integer aCompression){
        try {
            String q = "insert into UserSettings ('starttime', 'timeslotinterval', 'slotsbeforelunch', 'lunchinterval'"
                    + ", 'titlefontsize', 'subtitlefontsize', 'cellfontsize', 'headerfontsize', 'footnotefontsize','username', 'concourschair', 'chiefjudge', 'compression') values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            aConn.setAutoCommit(false);
            PreparedStatement ps;
            ps = aConn.prepareStatement(q);
            ps.setString(1, aStartTime);
            ps.setString(2, aSlotIntervalMinutes);
            ps.setString(3, aSlotsBeforeLunch);
            ps.setString(4, aLunchIntervalMinutes);
            ps.setInt(5, aTitleFontSize);
            ps.setInt(6, aSubtitleFontSize);
            ps.setInt(7, aCellFontSize);
            ps.setInt(8, aHeaderFontSize);
            ps.setInt(9, aFootnoteFontSize);
            ps.setString(10, aUserName);
            ps.setString(11, aConcoursChair);
            ps.setString(12, aChiefJudge);
            ps.setInt(13, aCompression);
            ps.addBatch();
            ps.executeBatch();
            aConn.commit();
            ps.close();
            aConn.setAutoCommit(true);
        } catch (SQLException ex) {
            String msg = "SQLException in SetUserSettingsTable" + ex;
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
    
}
/*
   Loads User Settings directly into correcponding Concours member variables
*/
 public void LoadConcoursUserSettingsDB(Connection aConn, Concours aConcours, Logger aLogger){
        try {
            aLogger.info("Started LoadConcoursUserSettingsDB");
            Statement stat;
            ResultSet rs;
            aConn.setAutoCommit(false);
            stat = aConn.createStatement();
            rs = stat.executeQuery("select * from UserSettings;"); 
           
            while (rs.next()) {
                aConcours.SetConcoursStartTime(rs.getString("starttime"));
                //strStartTime  = rs.getString("starttime");
                aConcours.SetConcoursTimeslotInterval(Integer.parseInt(rs.getString("timeslotinterval")));
                //timeslotInterval  = Integer.parseInt(rs.getString("timeslotinterval"));
                aConcours.SetConcoursTimeslotsBeforeLunch(Integer.parseInt(rs.getString("slotsbeforelunch")));
                //slotsBeforeLunch  = Integer.parseInt(rs.getString("slotsbeforelunch"));
                aConcours.SetConcoursLunchInterval(Integer.parseInt(rs.getString("lunchinterval")));
                //lunchInterval  = Integer.parseInt(rs.getString("lunchinterval"));
                aConcours.SetConcoursTitleFontSize(Integer.parseInt(rs.getString("titlefontsize")));
                //titleFontSize  = Integer.parseInt(rs.getString("titlefontsize"));
                aConcours.SetConcoursSubtitleFontSize(Integer.parseInt(rs.getString("subtitlefontsize")));
                aConcours.SetConcoursHeaderFontSize(Integer.parseInt(rs.getString("headerfontsize")));
                //headerFontSize  = Integer.parseInt(rs.getString("headerfontsize"));
                aConcours.SetConcoursCellFontSize(Integer.parseInt(rs.getString("cellfontsize")));
                //cellFontSize  = Integer.parseInt(rs.getString("cellfontsize"));
                aConcours.SetConcoursFootnoteFontSize(Integer.parseInt(rs.getString("footnotefontsize")));
                //footnoteFontSize  = Integer.parseInt(rs.getString("footnotefontsize"));
                aConcours.SetConcoursUserName(rs.getString("username"));
                //userName  = rs.getString("username");
                aConcours.SetConcoursChair(rs.getString("concourschair"));
                //concoursChair  = rs.getString("concourschair");
                aConcours.SetConcoursChiefJudge(rs.getString("chiefjudge"));
                aConcours.SetConcoursCompression(Integer.parseInt(rs.getString("compression")));
                //concoursChiefJudge  = rs.getString("chiefjudge");
                aConn.commit();
            }
            aConn.setAutoCommit(true);
            stat.close();
            rs.close();
            
        } catch (SQLException ex) {
            String msg = "SQLException in LoadConcoursUserSettingsDB";
            okDialog(msg);
            aConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
    }    
   
 
//
//   Checks UserSettings table to see if exist
 //
 //  Switched to Apache Commons DbUtils version CheckUserSettingsTableExistsDbUtils() with the hope
 //  of better handling of ResultSet & Statement closing
//
 /*
 public int CheckUserSettingsTableExists(Connection aConn, Concours aConcours, Logger aLogger) throws SQLException{
    int count = 0;
    aConn.setAutoCommit(false);
    ResultSet rs = null;
        try (Statement stat = aConn.createStatement()) {
            String q = "SELECT name FROM sqlite_master WHERE type='table' AND name='UserSettings'";
            rs = stat.executeQuery(q);
            while(rs.next()){
                count++;
            } 
            aConn.commit();
            aConn.setAutoCommit(true);
        } catch (SQLException ex){
            String msg = "SQLException CheckUserSettingsTableExists";
            okDialog(msg);
            if(rs != null) rs.close();
            aConn.setAutoCommit(true);
           
            aLogger.log(Level.SEVERE, msg, ex);
            return count;
        }
    rs.close();
    aConn.setAutoCommit(true);
    return count; // should be 1. Will be 0 if UserSettings doesn't exist
 }
 */
 
 public int CheckUserSettingsTableExistsDbUtils(Connection aConn, Concours aConcours, Logger aLogger) throws SQLException{
        ResultSetHandler<Integer> h;
        h = new ResultSetHandler<Integer>() {
            public Integer handle(ResultSet rs) throws SQLException {
                int count = 0;
                while(rs.next()){
                    count++;
                }
                return count;
            }
        };

        QueryRunner run = new QueryRunner();
        Integer result;
        try{
            result = run.query(aConn, "SELECT name FROM sqlite_master WHERE type=? AND name=?", h, "table", "UserSettings");
            return result;
        } finally {
            // Use this helper method so we don't have to check for null
        }
 }    
    
//
//   Checks to see if the EXISTING UserSettings table has all required fields with valid data
//   Returns number of fields
//
public int CheckUserSettingsColumnCount(Connection aConn, Concours aConcours, Logger aLogger) throws SQLException{
    aConn.setAutoCommit(false);
    Statement stat = aConn.createStatement();
    ResultSet rs = stat.executeQuery("select * from UserSettings");
    int count = 0;
    if(rs.next()) { // sets the cursor. Note there can be only one result...    
        ResultSetMetaData rsMetaData = rs.getMetaData();
        count = rsMetaData.getColumnCount();
    }
    stat.close();
    rs.close();
    return count;
}


    //
    // checkUserSettingsColNames checks names of all columns. If return value is not 0
    // UserSettings an old version and needs to be replaced
    // 
 //  Switched to Apache Commons DbUtils version CheckUserSettingsTableExistsDbUtils() with the hope
 //  of better handling of ResultSet & Statement closing
/*
public static int checkUserSettingsColNames(Connection aConn, Concours aConcours, Logger aLogger, List<String> aRequiredColumnNames){
        List<String> missingNames = new ArrayList<>(aRequiredColumnNames);
        try{
            List<String> names = new ArrayList<>();
            aConn.setAutoCommit(false);
            Statement stat = aConn.createStatement();
            ResultSet rs = stat.executeQuery("select * from UserSettings");
            int count = 0;
            if(rs.next()) { // sets the cursor. Note there can be only one result...
                ResultSetMetaData rsMetaData = rs.getMetaData();
                count = rsMetaData.getColumnCount();
                for(int i = 1; i<=count; i++){
                    names.add(rsMetaData.getColumnName(i));
                }
            }
            missingNames.removeAll(names);
            aConn.commit();
            aConn.setAutoCommit(true);
            stat.close();
            rs.close();
        } catch(SQLException ex) {
            String msg = "SQLException in checkUserSettingsColumnNames";
            okDialog(msg);
            aLogger.log(Level.SEVERE, msg, ex);
        }
    return missingNames.size();
}
*/


public static List<String> checkUserSettingsColNamesDbUtils(Connection aConn, Concours aConcours, Logger aLogger, List<String> aRequiredColumnNames) throws SQLException{
        List<String> missingNames = new ArrayList<>(aRequiredColumnNames);
        ResultSetHandler<List<String> > h;
        h = new ResultSetHandler<List<String> >() {
            public List<String>  handle(ResultSet rs) throws SQLException {
                List<String> names = new ArrayList<>();
                if(rs.next()) { // sets the cursor. Note there can be only one result...
                    ResultSetMetaData meta = rs.getMetaData();
                    int cols = meta.getColumnCount();
                    for (int i = 0; i < cols; i++) {
                        String colName = meta.getColumnName(i + 1);
                        names.add(colName) ;
                    }
                    missingNames.removeAll(names);
                    return missingNames; // normal return should be empty
                }
                return missingNames; // Hard to decide what to return, but this should do
            }
        };
        QueryRunner run = new QueryRunner();
        List<String>  result;
        try{
            result = run.query(aConn, "select * from UserSettings", h);
            return result;
        } finally {
            // Use this helper method so we don't have to check for null
        }
        
}

// 9/19/2018
public static boolean checkMasterPersonnelTableStructure(Connection aConn, Concours aConcours){
            
        // Assumes MasterPersonnel Table exists 
        List<String> requredColNames = Arrays.asList("masterpersonnel_id", "jcna", "club", "lastname",
                    "firstname", "mi",  "unique_name", "judgestatus", "cert_year", "address_street",
                    "city", "state", "country", "postalcode", "phone_work", "phone_home", "phone_cell", "email");
        boolean colNamesOk = true;
        try {
            colNamesOk = checkColNamesDbUtils(aConn, aConcours, aConcours.GetLogger(), "MasterPersonnel", requredColNames);
            
        } catch (SQLException ex) {
            String msg = "SQLException while checking MasterPersonnel table in checkMasterPersonnelTableStructure()";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        return colNamesOk;
}

// 9/20/2018
public static boolean checkMasterJaguarTableStructure(Connection aConn, Concours aConcours){
            
        // Assumes MasterJaguar Table exists 
        List<String> requredColNames = Arrays.asList("masterjaguar_id", "masterpersonnel_id", "jcnaclass_c", "jcnaclass_d",
                    "joclacategory", "year",  "model", "description", "unique_desc", "color",
                    "platevin");
        boolean colNamesOk = true;
        try {
            colNamesOk = checkColNamesDbUtils(aConn, aConcours, aConcours.GetLogger(), "MasterJaguar", requredColNames);
            
        } catch (SQLException ex) {
            String msg = "SQLException while checking MasterJaguar table in checkMasterJaguarTableStructure()";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        return colNamesOk;
}


public static boolean checkColNamesDbUtils(Connection aConn, Concours aConcours, Logger aLogger, String aTableName, List<String> aRequiredColumnNames) throws SQLException{
    boolean result = true;
    Statement stmt = aConn.createStatement();
    String q = "SELECT *";
    int k = 0;
    q = q + " FROM " + aTableName;
    ResultSet rs;
    rs = stmt.executeQuery(q);
    //List<String> columnNames = new ArrayList<>();
    if (rs != null) {
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCols = rsmd.getColumnCount();
        int i = 0;
        if(aRequiredColumnNames.size() != numCols){
            String msg = "Bad column count in checkColNamesDbUtils";
            aLogger.log(Level.SEVERE, msg);
            okDialog(msg);
            result = false;
        } else {
            // Count is correct, so check them one-by-one
            while (i < numCols) {
              String cnRequired = aRequiredColumnNames.get(i);
              i++;
              String cnMetaData = rsmd.getColumnName(i);
              System.out.print(cnMetaData + "\t");
              //columnNames.add(cnMetaData);
              if(!cnMetaData.equals(cnRequired)){
                String msg = "Bad column name in checkColNamesDbUtils";
                aLogger.log(Level.SEVERE, msg);
                okDialog(msg);
                result = false;
                break;
              }
            }
        }
        rs.close();
    } else {
      String msg = " Null Result Set in checkColNamesDbUtils()";
      aLogger.log(Level.SEVERE, msg);
    }
    return result;
}


public static boolean userSettingsColNamesInclude(Connection aConn, Concours aConcours, Logger aLogger, String aTargetColName){
        boolean result = false;
        try{
            ResultSet rs;
            try (Statement stat = aConn.createStatement()) {
                rs = stat.executeQuery("select * from UserSettings");
                int count;
                ResultSetMetaData rsMetaData = rs.getMetaData();
                count = rsMetaData.getColumnCount();
                for(int i = 1; i<=count; i++){
                    if(rsMetaData.getColumnName(i).equals(aTargetColName)){
                        result = true;
                        break;
                    }
                }   
                // the caller has us in autoCommit false
                aConn.commit(); 
            }
            rs.close();
        } catch(SQLException ex) {
            String msg = "SQLException in userSettingsColNamesInclude";
            okDialog(msg);
            aLogger.log(Level.SEVERE, msg, ex);
        }
        return result;
}


 //public boolean CheckUserSettingsTableIsOK(Connection aConn, Concours aConcours, Logger aLogger) throws SQLException, java.lang.NumberFormatException, Exception {
public boolean CheckUserSettingsTableIsOK(Connection aConn, Concours aConcours, Logger aLogger) throws SQLException{
        Statement stat = null;
        try {
            stat = aConn.createStatement();
        } catch (SQLException ex) {
           aLogger.log(Level.SEVERE, null, ex);
        }
            ResultSet rs = null;
        try {
            rs = stat.executeQuery("select * from UserSettings");
        } catch (SQLException ex) {
           aLogger.log(Level.SEVERE, null, ex);
        }
        try {
            //boolean result = true;
            aConn.setAutoCommit(false);
            
            String s = null;
            Integer I;
            //int fieldcount = 0; // a count of number of fields in the 1-row table
            while (rs.next()) {
                s = rs.getString("starttime");
                aLogger.info("Check timeslotinterval: " + s);
                //fieldcount++;
                try{
                    aLogger.info("Check timeslotinterval");
                    I = Integer.parseInt(rs.getString("timeslotinterval"));
                } catch (java.lang.NumberFormatException ex1){
                    String msg = "timeslotinterval in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    return false;
                }
                //fieldcount++;
                try {
                    aLogger.info("Check slotsbeforelunch");
                    I = Integer.parseInt(rs.getString("slotsbeforelunch"));
                } catch (java.lang.NumberFormatException ex2){
                    String msg = "slotsbeforelunch in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    return false;
                }
                //fieldcount++;
                try{
                    aLogger.info("Check lunchinterval");
                    I = Integer.parseInt(rs.getString("lunchinterval"));
                } catch (java.lang.NumberFormatException ex2){
                    String msg = "lunchinterval in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    return false;
                }
                //fieldcount++;
                try{
                    aLogger.info("Check titlefontsize");
                    I = Integer.parseInt(rs.getString("titlefontsize"));
                } catch (java.lang.NumberFormatException ex2){
                    String msg = "titlefontsize in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    return false;
                }
                // fieldcount++;
                try{
                    aLogger.info("Check subtitlefontsize");
                    I = Integer.parseInt(rs.getString("subtitlefontsize"));
                } catch (java.lang.NumberFormatException ex2){
                    String msg = "subtitlefontsize in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    //throw ex2;
                    return false;
                }
                //fieldcount++;
                try{
                    aLogger.info("Check headerfontsize");
                    I = Integer.parseInt(rs.getString("headerfontsize"));
                } catch (java.lang.NumberFormatException ex2){
                    String msg = "headerfontsize in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    return false;
                }
                //fieldcount++;
                try{
                    aLogger.info("Check cellfontsize");
                    I = Integer.parseInt(rs.getString("cellfontsize"));
                } catch (java.lang.NumberFormatException ex2){
                    String msg = "cellfontsize in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    return false;
                }
                //fieldcount++;
                try{
                    aLogger.info("Check footnotefontsize");
                    I = Integer.parseInt(rs.getString("footnotefontsize"));
                } catch (java.lang.NumberFormatException ex2){
                    String msg = "footnotefontsize in User Settings table is not a proper number.";
                    okDialog(msg);
                    aLogger.info(msg);
                    stat.close();
                    rs.close();
                    return false;
                }
            }
            aConn.commit();
            aConn.setAutoCommit(true);
            stat.close();
            rs.close();
            return true;
        } catch (SQLException ex) {
            String msg = "SQLException in CheckUserSettingsTableIsOK";
            okDialog(msg);
            aLogger.log(Level.SEVERE, msg, ex);
            aConn.commit();
            aConn.setAutoCommit(true);
            stat.close();
            rs.close();
            return false;
        }
    }    
 
        


public static void SetSettingsTableJAState(Connection aConn, boolean boolJudgeAssignmentState) throws SQLException{
        int intJudgeAssignmentState = boolJudgeAssignmentState?1:0;
        Long lngSettingsstate_id = 1L;
        Statement stat = null;
        String q = null;
        try {
            stat = aConn.createStatement();
            q = "update SettingsState set judgeassignmentstate = " + intJudgeAssignmentState +  " where settingsstate_id == " + lngSettingsstate_id  + ";";
            stat.executeUpdate(q);
            stat.close();
        } catch (SQLException ex) {
            String msg = "SQLException in SetSettingsTableJAState";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            stat.executeUpdate(q);
            stat.close();
            //System.exit(-1);
        }
    
}

public static void SetSettingsTableConcoursName(Connection aConn, String aConcoursName) throws SQLException{
    aConn.setAutoCommit(false);
    PreparedStatement ps;
    String q =  "UPDATE  SettingsState SET concoursname = ? WHERE settingsstate_id = ?";
    ps = aConn.prepareStatement(q);
    ps.setString(1,  aConcoursName);
    ps.setInt(2, 1);
    ps.executeUpdate();
    ps.close();
    aConn.commit();
    aConn.setAutoCommit(true);
}

public static boolean GetSettingsStateTableJAState(Connection aConn){
        boolean boolResult;
        int intResult = 0;
        Long lngSettingsstate_id = 1L;
        try {
            aConn.setAutoCommit(false);
            ResultSet rs;
            String q = "select judgeassignmentstate from SettingsState where settingsstate_id == " + lngSettingsstate_id +  ";" ;
            Statement stmt = aConn.createStatement();
            rs = stmt.executeQuery(q);
            if(rs.next()) { // sets the cursor. Note there can be only one result...
                intResult = rs.getInt("judgeassignmentstate")  ;
            }
            aConn.commit();
            aConn.setAutoCommit(true);
            stmt.close();
            rs.close();
        } catch (SQLException ex) {
            String msg = "SQLException in GetSettingsStateTableJAState";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolResult =  (intResult == 1) ?true:false;  
        return boolResult;
}

public static String GetSettingsTableConcoursName(Connection aConn){
        String strResult = null;
        Long lngSettingsstate_id = 1L;
        try {
            ResultSet rs;
            String q = "select concoursname from SettingsState where settingsstate_id = " + lngSettingsstate_id  ;
            try (Statement stmt = aConn.createStatement()) {
                rs = stmt.executeQuery(q);
                if(rs.next()) { // sets the cursor. Note there can be only one result...
                    strResult = rs.getString("concoursname")  ;
                }
            }
            rs.close();
        } catch (SQLException ex) {
            String msg = "SQLException in GetSettingsTableConcoursName().\n\nTry Restoring from an earlier backup.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
    return strResult;
}

/*
   When an Entry gets removed and the Owner is also a Judge it is necessary to  remove the Class of the Entry from the the Judge's self-entrys stored in
   ConcoursJudgeClassSelfEntryTable.
*/
public void UpdateJudgeStuff(Connection aConn,  Concours aConcours,  Entry aSelectedEntry){
        try {
            // If no remaining entres for this Owner in ConcoursOwnersEntries fix up Judge stuff and THEN remove Owner from ConcoursOwners
            String strSelectedEntryClass;
            Long lngConcoursPersonnel_id;
            String strOwnerUnique = aSelectedEntry.GetOwnerUnique();
            Long lngConcoursOwner_id = GetConcoursOwner_idDB(aConn, strOwnerUnique);
            String q = "select ownerentry_id from  ConcoursOwnersEntries where concoursowner_id = " + lngConcoursOwner_id + ";";
            Statement stmt_oe_empty = aConn.createStatement();
            ResultSet rs_oe_empty = stmt_oe_empty.executeQuery(q);
            if(!rs_oe_empty.next()){
                lngConcoursPersonnel_id = GetConcoursPersonnel_idDB(aConn, strOwnerUnique);
                // Now, if the Owner is a Judge, remove the Class from his/her selfEntry list
                //This assumes the ConcoursPerson hasn't been removed from ConcoursPersonnel
                if(aConcours.GetConcoursPersonnelObject().GetConcoursPerson(strOwnerUnique).GetStatus_j() == 1) {
                    Statement stmt_se = aConn.createStatement();
                    strSelectedEntryClass = aSelectedEntry.GetClassName();
                    q = "delete from ConcoursJudgeClassSelfEntryTable concourspersonnel_id = " + lngConcoursPersonnel_id + " and class = " + strSelectedEntryClass + ";";
                    ResultSet rs_se = stmt_se.executeQuery(q);
                    rs_se.close();
                    stmt_se.close();
                }
                q = "delete from  ConcoursOwners where concoursowner_id = " + lngConcoursOwner_id + ";";
                Statement stmt_co = aConn.createStatement();
                ResultSet rs_co = stmt_co.executeQuery(q);
            }
            rs_oe_empty.close();
            stmt_oe_empty.close();
        } catch (SQLException ex) {
            String msg = "SQLException in UpdateJudgeStuff()";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }

}

        
/*
 *  This adds a NEW Concours Class to the ConcoursClassesTable. Since s new class is needed ONLY when a new Entry has been entered, there
 *  will be ONLY 1 Entry node to insert into the ConcoursClassesEntries.
*/
public void  UpdateAddConcoursClassesDBTable(Connection aConn, ConcoursClass aConcoursClass){
        try {
            
            String className = aConcoursClass.GetClassName();
            Integer classNode = aConcoursClass.GetClassNode();
            String q = "insert into ConcoursClassesTable ('class_name', 'class_node' ) values (" + "\"" + className + "\"" + ","  + classNode  + ");";
            
            Statement stmt_c = aConn.createStatement();
            stmt_c.executeUpdate(q);
            stmt_c.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
}

/*
   This is called from the action button in RemoveConcourseEntryDialog
*/
//public void  UpdateRemoveConcoursClassesDBTable(Connection aConn, Integer aConcoursClassNode){
public void  UpdateRemoveConcoursClassesDBTable(Connection aConn, String aConcoursClassName, Integer aConcoursClassNode){
    String q;
    // First we must remove any entries in ConcoursClassPreassignedJudgesTable that reference the ConcoursClass being removed
    //String classname = theConcours.GetConcoursClassesObject().getClassName(aConcoursClassNode);
    Long lngConcoursClasses_id;
    lngConcoursClasses_id = GetConcoursClasses_idDB(aConn, aConcoursClassName);
    q = "delete from ConcoursClassPreassignedJudgesTable where concoursclasses_id == " + lngConcoursClasses_id + ";";
    Statement stmt_pj;
    try {
        stmt_pj = aConn.createStatement();
        stmt_pj.executeUpdate(q);
        stmt_pj.close();

    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
    // remove the class from ConcoursClassesTable
    try {
        q = "delete from ConcoursClassesTable where class_node = "  + aConcoursClassNode  + ";";
        Statement stmt_c = aConn.createStatement();
        stmt_c.executeUpdate(q);
        stmt_c.close();

    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
}
//public void  UpdateAddConcoursClassesEntriesDBTable(Connection aConn, ConcoursClass aConcoursClass, Entry aEntry){
public void  UpdateAddConcoursClassesEntriesDBTable(Connection aConn, String aConcoursClassName, Entry aEntry){
            // insert Entry indicies for aConcoursClass into ConcoursClassesEntries table
            Integer intEntryNode;
            Long lngConcoursClasses_id;
            String q;
            intEntryNode = aEntry.GetNode();
            //String className = aConcoursClass.GetClassName();
            
            lngConcoursClasses_id = GetConcoursClasses_idDB(aConn, aConcoursClassName);
        try {
            q = "insert into ConcoursClassesEntries ('concoursclasses_id', 'entry_node' ) values (" + lngConcoursClasses_id + ", " + intEntryNode  + ");";      
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}        
/*
   When an Entry is removed it's necessary to remove it from the DB table of entry nodes for the classs
   Note that the Class is not needed since the entry node can only appear once in the ConcoursClassesEntries table
*/
public void  UpdateRemoveConcoursClassesEntriesDBTable(Connection aConn, Integer aEntryNode){
            String q;
        try {
            q = "delete from ConcoursClassesEntries where entry_node =  " + aEntryNode  + ";";      
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}        


public static void UpdateAddConcoursJudgeSelfEntryTable(Connection aConn, Judge aJudge, String aJCNAClassName){
        Long lngConcourspersonnel_id = GetConcoursPersonnel_idDB( aConn, aJudge.getUniqueName());
        String q = "insert into ConcoursJudgeClassSelfEntryTable ('concourspersonnel_id', 'class' ) values (" + lngConcourspersonnel_id + ", '" + aJCNAClassName  + "');";
        try {
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            String msg ="ERROR: SQLException in UpdateAddConcoursJudgeSelfEntryTable";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
}   


public static void UpdateAddConcoursClassPreassignedJudgesTable(Connection aConn, String aJCNAClassName, int judgeNode, String aJudgeUniqueNAme){
    
        Long lngConcourspersonnel_id = GetConcoursPersonnel_idDB( aConn, aJudgeUniqueNAme);
        Long lngConcoursclass_id = GetConcoursClasses_idDB( aConn, aJCNAClassName);
//        stat_cpj.executeUpdate("create table ConcoursClassPreassignedJudgesTable ('preassignedjudge_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursclasses_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL, judge TEXT, FOREIGN KEY (concoursclasses_id) REFERENCES ConcoursClassesTable (concoursclasses_id));");
        String q = "insert into ConcoursClassPreassignedJudgesTable ('concoursclasses_id',  'judge_node', 'judge') values (" + lngConcoursclass_id + ", " + judgeNode + ", \"" + aJudgeUniqueNAme  + "\");";
        try {
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
}   

public static void UpdateRemoveConcoursClassPreassignedJudgesTable(Connection aConn, String aJCNAClassName){
    
        Long lngConcoursclass_id = GetConcoursClasses_idDB( aConn, aJCNAClassName);
        String q = "delete from ConcoursClassPreassignedJudgesTable where concoursclasses_id = " + lngConcoursclass_id  + ";";
        try {
            Statement stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
}   

/*
  * UpdateAddConcoursJudgeesDBTable() assumes that ConcoursPersonnel table has already been updated to include the Concours Person becoming an Judge
*/
public void UpdateAddConcoursJudgesDBTable(Connection aConn, Judge aJudge){
        String strUniqueName;
        Long lngConcoursPersonnel_id;
        Statement stmt;

        try {
            // Insert into ConcoursJudgesTable
            aConn.setAutoCommit(false);
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        strUniqueName = aJudge.getUniqueName();
        lngConcoursPersonnel_id = GetConcoursPersonnel_idDB(aConn, strUniqueName);
        String q = "insert into ConcoursJudgesTable ('concourspersonnel_id' ) values ( " + lngConcoursPersonnel_id + ");";
        try {
            stmt = aConn.createStatement();
            stmt.executeUpdate(q);
            stmt.close();
            aConn.commit();
            aConn.setAutoCommit(true);            
        } catch (SQLException ex) {
            theConcours.GetLogger().info("Failed to add concours judge: " + strUniqueName);
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
}
public void UpdateJudgeClassRejectDBTable(Connection aConn, Judge aJudge){
        theConcours.GetLogger().info("Starting UpdateJudgeClassRejectDBTable() for Judge " + aJudge.getUniqueName());
        ArrayList<String> rejectedClassNames = aJudge.GetRejectClasses();
        String q;
        Long lngConcoursPersonnel_id;
        lngConcoursPersonnel_id = GetConcoursPersonnel_idDB(aConn, aJudge.getUniqueName());
        // Clear existing preferences
        try {
            aConn.setAutoCommit(false);
            q = "delete from ConcoursJudgeClassRejectTable where concourspersonnel_id =  " + lngConcoursPersonnel_id  + ";";      
            Statement stmt_0 = aConn.createStatement();
            stmt_0.executeUpdate(q);
            aConn.commit();
            stmt_0.close();          
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        try {
            Statement stmt = aConn.createStatement();
            for(String cName :  rejectedClassNames){
                q = "insert into ConcoursJudgeClassRejectTable ('concourspersonnel_id', 'class' ) values ( " + lngConcoursPersonnel_id + ", \"" + cName + "\" );";
                stmt.executeUpdate(q);
                aConn.commit();
            } 
            stmt.close();
            aConn.setAutoCommit(true);
            theConcours.GetLogger().info("Completed UpdateJudgeClassRejectDBTable() for Judge " + aJudge.getUniqueName());
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
}
/*
    Used when a Entry is modified, i.e., when the JCNA Class of an Entry is changed in ModifyConcoursEntryDialog().
    
    Note that aModifiedEntry is the subject Entry with all changes already made.
    
*/
public void  UpdateModifyConcoursEntry(Connection aConn,  Entry aModifiedEntry, String aOriginalEntryName, String aOriginalJCNAClassName, ConcoursPerson aSelectedConcoursPerson){
    /*
      Necessary changes:
    
    0. Remove row from ConcoursesClassesEntries table where concourseclasses_id from ConcoursClassesTable  where  class_name == aOriginalJCNAClassName

    1a. Change entry_name in ConcoursEntries table to aModifiedEntry.GetID(). The row to change is where entry_name == aOriginalEntryName

    1b. Change class in ConcoursJaguars table to aModifiedEntry.GetClassName(). The row to change is where class == aOriginalJCNAClassName

    2. Remove row from ConcoursClassesTable where class_name == aOriginalJCNAClassName

    3. Add row to ConcoursClassesTable with class_name = aModifiedEntry.GetClassName() and class_node = aModifiedEntry.GetNode()
    3a. Add the Entry to the ConcoursClassesEntries DB table

    4. Fix up the Judge SelfEntry Lists.  If the owner is a Concours Judge then the current class SHOULD be in his SelfyEntry list, so remove it.

    5.  Is it necessary to check all Judges who are also owners to see if any has a car in the theNewConcoursClass? No!
        
         A judge is an owner or he/she is not. If not, nothing gets done. If so,
         the next question is does he/she have an intery in theNewConcoursClass. If not, nothing needs to be done.
          If he/she DOES have an entry in the theNewConcoursClass that class should ALREADY be in his/her SelfEntryList,
          so again nothing needs to be done.
    */
    String q;

    //0. Remove row from ConcoursesClassesEntries table where concourseclasses_id from ConcoursClassesTable  where  class_name == aOriginalJCNAClassName 

    try {
        Long lngConcourseclasses_id = GetConcoursClasses_idDB(aConn, aOriginalJCNAClassName);
        q = "delete from ConcoursClassesEntries where concoursclassesentries_id =  " + lngConcourseclasses_id  + ";";      
        Statement stmt_0 = aConn.createStatement();
        stmt_0.executeUpdate(q);
        stmt_0.close();
    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
    
     // 1a. Change entry_name in ConcoursEntries table to aModifiedEntry.GetID(). The row to change is where entry_name == aOriginalEntryName
     // Note: aModifiedEntry.GetID() has already been assigned a proper dash-number
    
    try {
            Statement stmt_1 = aConn.createStatement();
            //  "select concourspersonnel_id from ConcoursPersonnel where unique_name like  '" + aUniquePersonName + "';";            

            q = "update ConcoursEntries set entry_name = '" + aModifiedEntry.GetID() +  "' where entry_name like '" + aOriginalEntryName  + "';";
            stmt_1.executeUpdate(q);
            stmt_1.close();
    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
    
     //1b. Change class in ConcoursJaguars table to aModifiedEntry.GetClassName(). The row to change is where class == aOriginalJCNAClassName

    try {
            Statement stmt_1b = aConn.createStatement();
            q = "update ConcoursJaguars set class = '" + aModifiedEntry.GetClassName() +  "' where class like '" + aOriginalJCNAClassName  + "';";
            stmt_1b.executeUpdate(q);
            stmt_1b.close();
    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
    
     // 2. Remove row from ConcoursClassesTable where class_name == aOriginalJCNAClassName
    try {
        q = "delete from ConcoursClassesTable where class_name like  '" + aOriginalJCNAClassName  + "';";      
        Statement stmt_2 = aConn.createStatement();
        stmt_2.executeUpdate(q);
        stmt_2.close();
    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    // 3. Add row to ConcoursClassesTable with class_name = aModifiedEntry.GetClassName() and class_node = aModifiedEntry.GetNode()

    try {
        q = "insert into ConcoursClassesTable ('class_name', 'class_node' ) values ('" + aModifiedEntry.GetClassName() + "', " + aModifiedEntry.GetNode()  + ");";      
        Statement stmt_3 = aConn.createStatement();
        stmt_3.executeUpdate(q);
        stmt_3.close();
    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    //3a. Add the Entry to the ConcoursClassesEntries DB table
    UpdateAddConcoursClassesEntriesDBTable(aConn, aModifiedEntry.GetClassName(), aModifiedEntry);    
    
    //4. Fix up the Judge SelfEntry Lists.  If the owner is a Concours Judge then the current class will be in his/her SelfyEntry list, so remove it.
    
    if(aSelectedConcoursPerson.GetStatus_j() == 1){
        // first, get the concours personnel id for the owner/judge
        String strOwnerUnique = aSelectedConcoursPerson.GetUniqueName();
        Long cpid = 0L;
        try {
/*
            String q_mp =  "select concourspersonnel_id from ConcoursPersonnel where  unique_name like ?";
            PreparedStatement prepStmt_mp1;
            prepStmt_mp1 = aConn.prepareStatement(q_mp);
            prepStmt_mp1.setString(1, aUniquePersonName);
            ResultSet rs_mp1 = prepStmt_mp1.executeQuery();
            //Long aconcoursePersonnel_id = rs_1.getLong("concourspersonnel_id");
            Long aconcoursePersonnel_id = null;
            if(rs_mp1.next()){
                aconcoursePersonnel_id = rs_mp1.getLong("concourspersonnel_id");
            } else {
                rs_mp1.close();
                prepStmt_mp1.close();
                String msg = "Could not find unique_name " + aUniquePersonName + " in ConcoursPersonnel";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            
*/            
//Fixed to allow names with apostrophy etc.        q = "select *  from ConcoursPersonnel where unique_name like  '" + strOwnerUnique + "'";
            String q_4a =  "select concourspersonnel_id from ConcoursPersonnel where  unique_name like ?";
            PreparedStatement prepStmt_4a;
            prepStmt_4a = aConn.prepareStatement(q_4a);
            prepStmt_4a.setString(1, strOwnerUnique);
            ResultSet rs_4a = prepStmt_4a.executeQuery();
            //ResultSet rs_cpid;
            if (rs_4a.next()) {
                cpid = rs_4a.getLong("concourspersonnel_id")  ;
            }
            else{
                String msg = "ERROR: Could not find Owner/Judge " + strOwnerUnique + " in Concours ConcoursPersonnel DB table";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            rs_4a.close();
            prepStmt_4a.close();
            
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        // Now remove from ConcoursJudgeClassSelfEntryTable 
        try{
            Statement stmt_4b = aConn.createStatement();
            q = "delete  from ConcoursJudgeClassSelfEntryTable where class like '" + aOriginalJCNAClassName + "' AND concourspersonnel_id == " + cpid + ";";
            stmt_4b.executeUpdate(q);
            stmt_4b.close();
        } catch (SQLException ex){
             theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        

    }
    
    //5. Is it necessary to check all Judges who are also owners to see if any has a car in the theNewConcoursClass? No!
        //
        //  A judge is an owner or he/she is not. If not, nothing gets done. If so,
        //  the next question is does he/she have an intery in theNewConcoursClass. If not, nothing needs to be done.
        //  If he/she DOES have an entry in the theNewConcoursClass that class should ALREADY be in his/her SelfEntryList,
        //  so again nothing needs to be done.

    

}



public static Long GetConcoursPersonnel_idDB(Connection aConn, String aUniqueName){
            Long cpid = 0L;
            //ResultSet rs_cp1;
            //Fixed to allow names with apostrophy etc. 
            //String q = "select concourspersonnel_id  FROM ConcoursPersonnel where unique_name like \"" + CPUniqueName + "\"";
        try {
            if(aConn.isClosed() ){
                String msg = " database is closed in GetConcoursPersonnel_idDB()";
                okDialog(msg);
                theConcours.GetLogger().log(Level.SEVERE, msg, aConn);
                System.exit(-1);
            }

            String q_cp = "select concourspersonnel_id  from ConcoursPersonnel where unique_name like ?";
            PreparedStatement prepStmt_cp1;
            prepStmt_cp1 = aConn.prepareStatement(q_cp);
            prepStmt_cp1.setString(1, aUniqueName);
            ResultSet rs_cp1 = prepStmt_cp1.executeQuery();
            if (rs_cp1.next()) {
                cpid = rs_cp1.getLong("concourspersonnel_id")  ;
                rs_cp1.close();
                prepStmt_cp1.close();
            }
            else{
                rs_cp1.close();
                prepStmt_cp1.close(); 
                String msg = "ERROR: Could not find Personnel unique name " + aUniqueName + " in GetConcoursPersonnel_idDB()";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
        } catch (SQLException ex) {
            String msg = "SQLException while looking up " + aUniqueName + " in GetConcoursPersonnel_idDB(). \nDatabase might be left in a corrupt state. Revert to a backup and send log file to CB support.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
        } catch (NullPointerException ex){
            String msg = "NullPointerException while looking up " + aUniqueName + " in GetConcoursPersonnel_idDB(). \nDatabase might be left in a corrupt state. Revert to a backup and send log file to CB support.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
            
        }
        return cpid;
}

public Long GetConcoursOwner_idDB(Connection aConn, String aUniqueName){
            Long coid = 0L;
            ResultSet rs_o;
//Fixed to allow names with apostrophy etc.            String q = "select concoursowner_id  FROM ConcoursOwners where unique_name like \"" + aUniqueName + "\"";
        try {
           String q_o =  "select concoursowner_id from ConcoursOwners where  unique_name like ?"; 
           PreparedStatement prepStmt_o1 = aConn.prepareStatement(q_o);
           prepStmt_o1.setString(1, aUniqueName);
           rs_o = prepStmt_o1.executeQuery();
            //Statement stmt = aConn.createStatement();
            //rs = stmt.executeQuery(q);
            if (rs_o.next()) {
                coid = rs_o.getLong("concoursowner_id")  ;
                rs_o.close();
                prepStmt_o1.close();
            }
            else{
                rs_o.close();
                prepStmt_o1.close();
                String msg = "ERROR: Could not find Owner unique name " + aUniqueName + " in ConcoursOwners DB";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            
        } catch (SQLException ex) {
            String msg = "ERROR: SQLException in GetConcoursOwner_idDB";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
        return coid;
}

public Long GetJaguar_idDB(Connection aConn, Integer aNode){
            Long cjid = 0L;
            ResultSet rs;
            String q = "select jaguar_id  FROM ConcoursJaguars where node == " + aNode + ";" ;
        try {
            Statement stmt = aConn.createStatement();
            rs = stmt.executeQuery(q);
            if (rs.next()) {
                cjid = rs.getLong("jaguar_id")  ;
                rs.close();
            }
            else{
                rs.close();
                String msg = "ERROR: Could not find Jaguar node " + aNode + " in ConcoursJaguars DB";
                okDialog(msg);
                theConcours.GetLogger().info(msg);
                System.exit(-1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cjid;
        
}

public void DisplayJaguarDB(Connection aConn){
            Long cjid = 0L;
            Long masterjaguar_id = 0L;
            String jaguarclass;
            Integer node;
            ResultSet rs;
            String q = "select *  from ConcoursJaguars;" ;
        try {
            Statement stmt = aConn.createStatement();
            rs = stmt.executeQuery(q);
            while (rs.next()) {
                cjid = rs.getLong("jaguar_id")  ;
                masterjaguar_id = rs.getLong("masterjaguar_id");
                jaguarclass = rs.getString("class");
                node = rs.getInt("node");
                System.out.println("jaguar_id: " + cjid + " masterjaguar_id: " + masterjaguar_id + " class: " + jaguarclass + " node: " + node );
               // rs.close();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
}


public static Long GetConcoursClasses_idDB(Connection aConn, String aClassName){
            Long ccid = 0L;
            ResultSet rs;
            if(aClassName == ""){
                theConcours.GetLogger().info("GetConcoursClasses_idDB(): Empty Class name in Concours Classes DB.");
            }
            String q = "select concoursclasses_id  FROM ConcoursClassesTable where class_name like  \"" + aClassName + "\"";
        try {
            Statement stmt = aConn.createStatement();
            rs = stmt.executeQuery(q);
            if (rs.next()) {
                ccid = rs.getLong("concoursclasses_id")  ;
                rs.close();
            }
            else{
                rs.close();
                okDialog("ERROR: GetConcoursClasses_idDB(): Could not find Class name " + aClassName + " in Concours Classes DB.");
                theConcours.GetLogger().info("ERROR: GetConcoursClasses_idDB(): Could not find Class name " + aClassName + " in Concours Classes DB.");
                System.exit(-1);
            }
            stmt.close();
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        return ccid;
        
}
 
public Long UpdateAddMasterPerson(Connection aConn, MasterPersonExt aMasterPersonExt){
        Long lngMasterPersonnelID = 0L; // will never be used
    try {
         aConn.setAutoCommit(false);
        PreparedStatement prep_mp;
        // Added MI 3/18/2018
        prep_mp = aConn.prepareStatement( "insert into MasterPersonnel ('jcna', 'club', 'lastname', 'firstname', 'mi', 'unique_name', 'judgestatus', 'cert_year', 'address_street', 'city', 'state', 'country', postalcode, 'phone_work', 'phone_home', 'phone_cell', 'email') values ( ?, ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ?, ?, ?);");

        prep_mp.setInt(1, aMasterPersonExt.getJcna());
        prep_mp.setString(2,aMasterPersonExt.getClub());
        prep_mp.setString(3,aMasterPersonExt.getLastName());
        prep_mp.setString(4,aMasterPersonExt.getFirstName());
        prep_mp.setString(5,aMasterPersonExt.getMI());
        prep_mp.setString(6,aMasterPersonExt.getUniqueName());
        prep_mp.setString(7, aMasterPersonExt.getJudgeStatus());
        prep_mp.setInt(8, aMasterPersonExt.getCertYear());
        prep_mp.setString(9,aMasterPersonExt.getAddressSreet());
        prep_mp.setString(10,aMasterPersonExt.getCity());
        prep_mp.setString(11,aMasterPersonExt.getState());
        prep_mp.setString(12, aMasterPersonExt.getCountry());
        prep_mp.setString(13, aMasterPersonExt.getPostalCode());
        prep_mp.setString(14, aMasterPersonExt.getPhoneWork());
        prep_mp.setString(15, aMasterPersonExt.getPhoneHome());
        prep_mp.setString(16, aMasterPersonExt.getPhoneCell());
        prep_mp.setString(17, aMasterPersonExt.getEmail());
        prep_mp.addBatch();
        prep_mp.executeBatch();
        //aConn.commit();
        
        //
        // Now put the MasterPerson's Jaguars into MasterJaguar table
        //
        // Note: Switched to PreparedStatement because names like O'Toole are a problem with string concatenation
        // String q =  "select * from MasterPersonnel where  unique_name like '" +  aMasterPersonExt.getUniqueName() + "';";
        String q_mp1 =  "select * from MasterPersonnel where unique_name like ?";
        PreparedStatement prepStmt_mp1 = aConn.prepareStatement(q_mp1);
        prepStmt_mp1.setString(1, aMasterPersonExt.getUniqueName());
        ResultSet rs_mp1 = prepStmt_mp1.executeQuery();
        if (rs_mp1.next()) {
            PreparedStatement prep_j = aConn.prepareStatement( "insert into MasterJaguar ('masterpersonnel_id',  'jcnaclass_c', 'jcnaclass_d', 'joclacategory', 'year',  'model', 'description', 'unique_desc', 'color', 'platevin') values ( ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?);");
            lngMasterPersonnelID = rs_mp1.getLong("masterpersonnel_id") ;
            for(MasterJaguar mj : aMasterPersonExt.getJaguarStableList()){
                prep_j.setLong(1, lngMasterPersonnelID);   
                String classname_c = mj.getJcnaclass_c();
                prep_j.setString(2,classname_c);
                String classname_d = mj.getJcnaclass_d();
                prep_j.setString(3,classname_d);
                prep_j.setString(4, mj.getJoclacategory());
                prep_j.setInt(5, mj.getYear());
                prep_j.setString(6, mj.getModel());
                prep_j.setString(7,mj.getDescription());
                prep_j.setString(8,mj.getValue());
                prep_j.setString(9,mj.getColor());
                prep_j.setString(10,mj.getPlateVIN());
                prep_j.addBatch();
                prep_j.executeBatch();
            }
            prep_j.close();
          //  aConn.commit();
        }
        else{
            rs_mp1.close();
            prepStmt_mp1.close();
            String msg = "Could not find UniqueName " + aMasterPersonExt.getUniqueName() + " in MasterPersonnel";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
        }
        rs_mp1.close();
        prepStmt_mp1.close();
        aConn.commit();
        aConn.setAutoCommit(true); 
    } catch (SQLException ex) {
        theConcours.GetLogger().log(Level.SEVERE, null, ex);
    }
                    
                    
    return lngMasterPersonnelID;
}
/*
      This updates an EXISTING MasterPerson using data in the argument aEditedMasterPersonExt. IOW, the argument MasterPerson
      is only a way to get the putative edited data into this function, so it can be used to update the corresponding
      MasterPerson in the database. Also removes masterJaguars in aMasterJagsToBeRemoved

      Returns the masterperson_id
*/
public void UpdateEditMasterJaguar(Connection aConn,  MasterJaguar aMasterJagsToBeUpdated, String aColor, String aPlateVin ){
        try {
            aConn.setAutoCommit(false);
            PreparedStatement prep_mj;
            String q =  "UPDATE  MasterJaguar SET color = ?,  platevin = ?  WHERE unique_desc like ?";
            prep_mj = aConn.prepareStatement(q);
            prep_mj.setString(1, aColor);
            prep_mj.setString(2, aPlateVin);
            prep_mj.setString(3, aMasterJagsToBeUpdated.getUniqueDesc());
            prep_mj.executeUpdate();
            prep_mj.close();
            aConn.commit();
            aConn.setAutoCommit(true);
            prep_mj.close();
        } catch (SQLException ex) {
            String msg = "SQLException in UpdateEditMasterJaguar";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
}


public Long UpdateUpdateMasterPerson(Connection aConn,  MasterPersonExt aEditedMasterPersonExt, List<MasterJaguar> aMasterJagsToBeRemoved){
     Long lngMasterPersonnelID = 0L; // 0L will never be used
    try {
        aConn.setAutoCommit(false); 
        PreparedStatement prep_mp;
        String q =  "UPDATE  MasterPersonnel SET jcna = ?,  club = ?,  lastname = ?,  firstname = ?,  unique_name = ?,  judgestatus = ?,  cert_year = ?,  address_street = ?,  city = ?,  state = ?,  country = ?,  postalcode = ?,  phone_work = ?,  phone_home = ?,  phone_cell = ?,  email = ? WHERE unique_name like ?";

        prep_mp = aConn.prepareStatement(q);
        prep_mp.setInt(1, aEditedMasterPersonExt.getJcna());
        prep_mp.setString(2,aEditedMasterPersonExt.getClub());
        prep_mp.setString(3,aEditedMasterPersonExt.getLastName());
        prep_mp.setString(4,aEditedMasterPersonExt.getFirstName());
        prep_mp.setString(5,aEditedMasterPersonExt.getUniqueName());
        prep_mp.setString(6, aEditedMasterPersonExt.getJudgeStatus());
        prep_mp.setInt(7, aEditedMasterPersonExt.getCertYear());
        prep_mp.setString(8,aEditedMasterPersonExt.getAddressSreet());
        prep_mp.setString(9,aEditedMasterPersonExt.getCity());
        prep_mp.setString(10,aEditedMasterPersonExt.getState());
        prep_mp.setString(11, aEditedMasterPersonExt.getCountry());
        prep_mp.setString(12, aEditedMasterPersonExt.getPostalCode());
        prep_mp.setString(13, aEditedMasterPersonExt.getPhoneWork());
        prep_mp.setString(14, aEditedMasterPersonExt.getPhoneHome());
        prep_mp.setString(15, aEditedMasterPersonExt.getPhoneCell());
        prep_mp.setString(16, aEditedMasterPersonExt.getEmail());
        prep_mp.setString(17,  aEditedMasterPersonExt.getUniqueName()); // this will be the same as aCurMasterPersonExt because unique name can't be changed
        prep_mp.executeUpdate();
        prep_mp.close();
        aConn.commit();
        //
        // Now put the ADDED Jaguars into the MasterJaguar table AND remove the Jags in aMasterJagsToBeRemoved
        //
//Fixed to allow names with apostrophy etc.        q =  "select * from MasterPersonnel where  unique_name like '" +  aEditedMasterPersonExt.getUniqueName() + "';"; // this will be the same as aCurMasterPersonExt because unique name can't be changed
        //Statement stat_mp = aConn.createStatement();
        //ResultSet rs_mp = stat_mp.executeQuery(q);
        String un = aEditedMasterPersonExt.getUniqueName();
        String q_mp =  "select * from MasterPersonnel where  unique_name like ?";
        PreparedStatement prepStmt_mp1 = aConn.prepareStatement(q_mp);
        prepStmt_mp1.setString(1, un);
        ResultSet rs_mp1 = prepStmt_mp1.executeQuery();
        if (rs_mp1.next()) {
            lngMasterPersonnelID = rs_mp1.getLong("masterpersonnel_id") ;
            if(lngMasterPersonnelID == null || lngMasterPersonnelID <= 0){
                okDialog("ERROR: MasterPerson ID null or <=0 in UpdateUpdateMasterPerson");
                theConcours.GetLogger().info("ERROR: MasterPerson ID null or <=0 in UpdateUpdateMasterPerson");
                System.exit(-1);
            }
            //
            // Add the new Jags
            //
            PreparedStatement prep_jAdd = aConn.prepareStatement( "insert into MasterJaguar ('masterpersonnel_id',  'jcnaclass_c', 'jcnaclass_d', 'joclacategory', 'year',  'model', 'description', 'unique_desc', 'color', 'platevin') values ( ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?, ?);");
            for(MasterJaguar mj : aEditedMasterPersonExt.getJaguarStable()){ // Note that the argument MasterPerson has only the ADDED Jags
                prep_jAdd.setLong(1, lngMasterPersonnelID);
                String classname_c = mj.getJcnaclass_c();
                prep_jAdd.setString(2, classname_c);
                String classname_d = mj.getJcnaclass_d();
                prep_jAdd.setString(3,classname_d);
                prep_jAdd.setString(4,mj.getJoclacategory());
                prep_jAdd.setInt(5, mj.getYear());
                prep_jAdd.setString(6,mj.getModel());
                prep_jAdd.setString(7,mj.getDescription());
                prep_jAdd.setString(8,mj.getValue());
                prep_jAdd.setString(9,mj.getColor());
                prep_jAdd.setString(10,mj.getPlateVIN());
                prep_jAdd.executeUpdate();
            }
            prep_jAdd.close();
            aConn.commit();
            //
            //  Now remove the Jags to be removed, if any
            //
            PreparedStatement prep_jRemove = aConn.prepareStatement("delete from MasterJaguar where unique_desc like ?"); 
            for(MasterJaguar mj : aMasterJagsToBeRemoved){
                prep_jRemove.setString(1, mj.getUniqueDesc());    
                prep_jRemove.executeUpdate();
            }
            aConn.commit();
            prep_jRemove.close();
        } else{
             String msg = "Could not find UniqueName " + aEditedMasterPersonExt.getUniqueName() + " in MasterPersonnel";
            okDialog(msg);
            theConcours.GetLogger().info(msg);
            rs_mp1.close();
            prepStmt_mp1.close();
            aConn.setAutoCommit(true); 
            System.exit(-1);
        }
        aConn.setAutoCommit(true); 
        rs_mp1.close();
        prepStmt_mp1.close();
    } catch (SQLException ex) {
        String msg = "ERROR: SQLException in UpdateRemoveMasterPersonList";
        okDialog(msg);
        theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        System.exit(-1);
    }
                    
    return lngMasterPersonnelID;
}

public int UpdateRemoveMasterPersonList(Connection aConn, List<MasterPersonExt> aMasterPersonToBeRemovedList){
    Long lngMasterPersonnelID = null;
    String q_mp;
    PreparedStatement prepStmt_mp1 = null;
    PreparedStatement prepStmt_mp2 = null;
    ResultSet rs_mp1 = null;
    PreparedStatement prepStmt_j = null;
    int numRemoved = 0;
    for(MasterPersonExt mp : aMasterPersonToBeRemovedList){
        try {
            // Find the MasterPerson ID
            String un = mp.getUniqueName();
            q_mp =  "select * from MasterPersonnel where  unique_name like ?";
            prepStmt_mp1 = aConn.prepareStatement(q_mp);
            prepStmt_mp1.setString(1, un);
            rs_mp1 = prepStmt_mp1.executeQuery();
            if (rs_mp1.next()) {
                lngMasterPersonnelID = rs_mp1.getLong("masterpersonnel_id") ;
                if(lngMasterPersonnelID <= 0){
                    okDialog("MasterPerson ID <=0 in UpdateRemoveMasterPersonList()");
                    theConcours.GetLogger().info("MasterPerson ID <=0 in UpdateRemoveMasterPersonList");
                    prepStmt_mp1.close();
                    rs_mp1.close();
                }
            }
            prepStmt_mp1.close();
            rs_mp1.close();
            // First, remove all Jaguars belonging to mp
            String q_j = "delete from MasterJaguar where masterpersonnel_id == ?";
            prepStmt_j = aConn.prepareStatement(q_j);
            prepStmt_j.setLong(1, lngMasterPersonnelID);
            int c1 = prepStmt_j.executeUpdate();
            //okDialog("Removed " + c1 + " Jaguars before removing MasterPerson " + un);
            
            prepStmt_j.close();
            
            
            // Now remove mp
            q_mp = "delete from MasterPersonnel where masterpersonnel_id = ?";
            prepStmt_mp2 = aConn.prepareStatement(q_mp);
            prepStmt_mp2.setLong(1, lngMasterPersonnelID);
            int c2 = prepStmt_mp2.executeUpdate();
            numRemoved = numRemoved + c2;
            //okDialog("Removed " + c2 + " MasterPersons with ID " + lngMasterPersonnelID);
            
            prepStmt_mp2.close();
            
        } catch (SQLException ex) {
            okDialog("SQL Exception in UpdateRemoveMasterPersonList()");
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
    }
    if(numRemoved != aMasterPersonToBeRemovedList.size()){
        okDialog("The number of Master Persons removed " + numRemoved + " is different from the number requested " + aMasterPersonToBeRemovedList.size() + " This is unexpected. Please report to ConcoursBuilder developer.");
    }
    return numRemoved;
}


/*
      THIS NOT USED, BUT KEEP IT FOR NOW 
*/

public static void UpdateJCNAClassesFromDB(Connection aConn, String aPath){
  /*  Map currClassNameToNew = new HashMap(); 
        currClassNameToNew.put("C01A", "C01/PRE");
        currClassNameToNew.put("C01B", "C01/PRE");
        currClassNameToNew.put("C02", "C02/120");
        currClassNameToNew.put("C02", "C02/120");
        currClassNameToNew.put("C04", "C02/150");
        currClassNameToNew.put("C05", "C05/E1");
        currClassNameToNew.put("C06", "C06/E2");
        currClassNameToNew.put("C07", "C07/E3");
        currClassNameToNew.put("C08", "C08/SLS");
        currClassNameToNew.put("C09", "C08/SLS");
        currClassNameToNew.put("C10", "C09/XJ");
        currClassNameToNew.put("C11", "C09/XJ");
        currClassNameToNew.put("C12", "C09/XJ");
        currClassNameToNew.put("C13", "C10/XJ");
        currClassNameToNew.put("C14", "C11/J8");
        currClassNameToNew.put("C15A", "C12/J8");
        currClassNameToNew.put("C15B", "C13/JS");
        currClassNameToNew.put("C16A", "C14/K8");
        currClassNameToNew.put("C17", "C16/SX");
        currClassNameToNew.put("C18", "C16/SX");
        currClassNameToNew.put("C19A", "C17/PN");
        currClassNameToNew.put("C19B", "C18/PN");
        currClassNameToNew.put("C20", "C19/FJ");
        currClassNameToNew.put("C21", "C19/FJ");
        currClassNameToNew.put("C22", "C20/F");
        //************* DRIVEN**********************
        currClassNameToNew.put("D01", "D01/PRE");
        currClassNameToNew.put("D02", "D02/E1");
        currClassNameToNew.put("D03", "D03/E2"); 
        currClassNameToNew.put("D04", "D04/E3");
        currClassNameToNew.put("D05", "D05/SLS");
        currClassNameToNew.put("D06", "D06/XJ");
        currClassNameToNew.put("D07", "D07/XJ");
        currClassNameToNew.put("D08A", "D08/XJS");
        currClassNameToNew.put("D08B", "D09/XJS");
        currClassNameToNew.put("D09A", "D10/K8");
        currClassNameToNew.put("D09B", "D11/XK");
        currClassNameToNew.put("D10", "D12/J8");
        currClassNameToNew.put("D11", "D13/SX");
        currClassNameToNew.put("D12", "D14/FJ");
        currClassNameToNew.put("D13", "D14/FJ");
        currClassNameToNew.put("D14", "D15/F");
        //************* SPECIAL**********************
        currClassNameToNew.put("S01", "S01/PD");
        currClassNameToNew.put("S02", "S01/PD");
        currClassNameToNew.put("S03", "S02/MOD");
        currClassNameToNew.put("SO4", "S03/REP");
        
        
        
    try {
        Statement stat_c;
        ResultSet rs_c;
        //System.out.println("\n\n Loop over and display all JCNA classes");
        stat_c = aConn.createStatement();

        rs_c = stat_c.executeQuery("select * from JCNAClasses;");
        while (rs_c.next()) {
           // System.out.println("Division: " + rs_c.getString("division") + " " + "Class: " + rs_c.getString("class") + " Node: " +  rs_c.getString("node")+ " Description: " +  rs_c.getString("description"));
        }

        rs_c.close();
    } catch (SQLException ex) {
        Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }

*/

}
/*
       This is used only when JCNA changes Class definititions, such as they did at the 2015 AGM.
       The database tables for JCNA Classes, MasterPersonnel, and MasterJaguar must be updated to reflect the changes.

        The new JCNA classes and master data are read from  input CSV files, typically created with Excel and exported CSV. 
        See JCNANewClassesCsv.txt and MasterPersonnelListNewJCNAClassesCsv.txt
*/

public static void CreateNewConcoursFromCSV(Connection aConn, String aPath,  String aJCNAClassesCSVFileName, String aMasterPersonnelCSVFileName){

      // 
      // not compatible with changes 5/1/2017
    //LoadSQLiteConcoursDatabase.LoadJCNAClassesTableFromCSV(aConn, aPath + "\\"  + aJCNAClassesCSVFileName); // don't need an instance of LoadSQLiteConcoursDatabase to use its methods
        
    
    
    try {
            LoadSQLiteConcoursDatabase.LoadMasterPersonnelAndJaguarTablesFromCSV(aConn, aPath, aMasterPersonnelCSVFileName); // don't need an instance of LoadSQLiteConcoursDatabase to use its methods
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

}

public int getRowCount(Connection aConn, String aTable){
        int count = 0;
        try {
            Statement stmt= aConn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM "+ aTable);
            while (res.next()){
                count = res.getInt(1);
            }
            
            return count;
        } catch (SQLException ex) {
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        return count;
}
/*
    Used for creating a fresh Concours. Creates the various Concourse DB tables, empty
*/
public void CreateConcoursTables(Connection aConn){
    String q;
    try {
        aConn.setAutoCommit(false);
        // ConcoursPersonnel 
        Statement stat_cpd = aConn.createStatement();
        stat_cpd.executeUpdate("drop table if exists ConcoursPersonnel");
        stat_cpd.close();
        aConn.commit();

        Statement stat_cpc = aConn.createStatement();
        q = "create table ConcoursPersonnel ('concourspersonnel_id' INTEGER PRIMARY KEY AUTOINCREMENT ,  'masterpersonnel_id' INTEGER, 'unique_name' TEXT NOT NULL, 'status_o' INTEGER DEFAULT 0,  'status_j' INTEGER DEFAULT 0 , 'concourspersonnel_node' INTEGER NOT NULL UNIQUE,  FOREIGN KEY (masterpersonnel_id) REFERENCES MasterPersonnel (masterpersonnel_id))";
        stat_cpc.executeUpdate(q);
        stat_cpc.close();
        aConn.commit();

        // Concours Jaguar  Table
        Statement stat_jd = aConn.createStatement();
        stat_jd.executeUpdate("drop table if exists ConcoursJaguars"); 
        stat_jd.close();
        aConn.commit();
        Statement stat_jc = aConn.createStatement();
        q  =   "create table ConcoursJaguars ('jaguar_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'masterjaguar_id' INTEGER, 'class' TEXT NOT NULL,  'node' INTEGER NOT NULL UNIQUE, UNIQUE (masterjaguar_id), FOREIGN KEY (masterjaguar_id) REFERENCES MasterJaguar (masterjaguar_id))";
        stat_jc.executeUpdate(q);
        stat_jc.close();
        aConn.commit();

        // Concours Owners  Table
        Statement stat_od = aConn.createStatement();
        stat_od.executeUpdate("drop table if exists ConcoursOwners"); 
        stat_od.close();
        aConn.commit();
        Statement stat_oc = aConn.createStatement();
       // q  =   "create table ConcoursOwners ('concoursowner_id' INTEGER PRIMARY KEY AUTOINCREMENT,   'concourspersonnel_id' , 'unique_name' TEXT NOT NULL UNIQUE, 'concourspersonnel_node' INTEGER NOT NULL UNIQUE, UNIQUE (concourspersonnel_id), FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id))";
        q  =   "create table ConcoursOwners ('concoursowner_id' INTEGER PRIMARY KEY AUTOINCREMENT,   'concourspersonnel_id' INTEGER, 'unique_name' TEXT NOT NULL UNIQUE, 'concourspersonnel_node' INTEGER NOT NULL UNIQUE,  FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id))";
        stat_oc.executeUpdate(q);
        stat_oc.close();
        aConn.commit();

        // Concours Owners  Entries Table.. an Owner can have several entries in the Concours
        Statement stat_oed = aConn.createStatement();
        stat_oed.executeUpdate("drop table if exists ConcoursOwnersEntries"); 
        stat_oed.close();
        aConn.commit();
        
        Statement stat_oec = aConn.createStatement();
        q  =   "create table ConcoursOwnersEntries ('ownerentry_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursowner_id' LONG,  'entrynode' INTEGER NOT NULL UNIQUE,  FOREIGN KEY (concoursowner_id) REFERENCES ConcoursOwners (concoursowner_id))";
        stat_oec.executeUpdate(q);
        stat_oec.close();
        aConn.commit();
        
    // ConcoursClassesEntries
        Statement stat_cced = aConn.createStatement();
        stat_cced.executeUpdate("drop table if exists ConcoursClassesEntries;");
        stat_cced.close();
        
        aConn.commit();
        Statement stat_ccec = aConn.createStatement();
        q = "create table ConcoursClassesEntries ('concoursclassesentries_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursclasses_id' INTEGER NOT NULL, 'entry_node' INTEGER NOT NULL, FOREIGN KEY (concoursclasses_id) REFERENCES ConcoursClassesTable (concoursclasses_id));";
        stat_ccec.executeUpdate(q);
        stat_ccec.close();
        aConn.commit();
        
// ConcoursClassesTable
        Statement stat_ccd = aConn.createStatement();
        stat_ccd.executeUpdate("drop table if exists ConcoursClassesTable;");
        stat_ccd.close();
        aConn.commit();
        
        Statement stat_ccc = aConn.createStatement();
        q = "create table ConcoursClassesTable ('concoursclasses_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'class_name' TEXT NOT NULL, 'class_node' INTEGER NOT NULL);";
        stat_ccc.executeUpdate(q);
        stat_ccc.close();
        aConn.commit();

//     ConcoursEntries        
        Statement stat_ced = aConn.createStatement();
        stat_ced.executeUpdate("drop table if exists ConcoursEntries");
        stat_ced.close();
        aConn.commit();

        Statement stat_cec = aConn.createStatement();
        q  = "create table ConcoursEntries (entry_name TEXT NOT NULL, concourspersonnel_id INTEGER NOT NULL, jaguar_id INTEGER NOT NULL, UNIQUE (concourspersonnel_id, jaguar_id), FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id), FOREIGN KEY (jaguar_id) REFERENCES ConcoursJaguars (jaguar_id));";
        stat_cec.executeUpdate(q);
        stat_cec.close();
        aConn.commit();

        
        
//     ConcoursJudgesTable        
        Statement stat_jgd = aConn.createStatement();
        stat_jgd.executeUpdate("drop table if exists ConcoursJudgesTable" );
        stat_jgd.close();
        aConn.commit();

        Statement stat_jgc = aConn.createStatement();
        stat_jgc.executeUpdate("create table ConcoursJudgesTable (judge_id INTEGER PRIMARY KEY AUTOINCREMENT, concourspersonnel_id INTEGER, UNIQUE (concourspersonnel_id), FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id) );"); // Lists of excluded JCNA Classes are in ConcoursJudgeClassRejectTable table 
        stat_jgc.close();
        aConn.commit();

// ConcoursJudgeClassSelfEntryTable
        Statement stat_jse = aConn.createStatement();
        stat_jse.executeUpdate("drop table if exists ConcoursJudgeClassSelfEntryTable" ); 

        aConn.commit();
        stat_jse.executeUpdate("create table ConcoursJudgeClassSelfEntryTable (classselfentry_id INTEGER PRIMARY KEY AUTOINCREMENT, concourspersonnel_id INTEGER, class TEXT,  FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id) );"); // Lists of selfentry JCNA Classes  
        aConn.commit();
        stat_jse.close();

//  ConcoursJudgeClassRejectTable       
        Statement stat_jcrd = aConn.createStatement();
        stat_jcrd.executeUpdate("drop table if exists ConcoursJudgeClassRejectTable" ); 
        stat_jcrd.close();
        aConn.commit();
        
        Statement stat_jcrc = aConn.createStatement();
        stat_jcrc.executeUpdate("create table ConcoursJudgeClassRejectTable (classreject_id INTEGER PRIMARY KEY AUTOINCREMENT, concourspersonnel_id INTEGER, class TEXT,  FOREIGN KEY (concourspersonnel_id) REFERENCES ConcoursPersonnel (concourspersonnel_id) );"); // Lists of excluded JCNA Classes  
        stat_jcrc.close();
        aConn.commit();

//  ConcoursClassPreassignedJudgesTable       
        Statement stat_cpjd = aConn.createStatement();
        stat_cpjd.executeUpdate("drop table if exists ConcoursClassPreassignedJudgesTable;");
        stat_cpjd.close();
        aConn.commit();

        //q = "create table ConcoursClassPreassignedJudgesTable ('preassignedjudge_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursclasses_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL, judge TEXT, FOREIGN KEY (concoursclasses_id) REFERENCES ConcoursClassesTable (concoursclasses_id));";
        Statement stat_cpjc = aConn.createStatement();
        stat_cpjc.executeUpdate("create table ConcoursClassPreassignedJudgesTable ('preassignedjudge_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'concoursclasses_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL, judge TEXT, FOREIGN KEY (concoursclasses_id) REFERENCES ConcoursClassesTable (concoursclasses_id));");
        stat_cpjc.close();
        aConn.commit();

// EntryJudgesTable
        Statement stat_ejd = aConn.createStatement(); // Entry judge lists
        stat_ejd.executeUpdate("drop table if exists EntryJudgesTable;");
        stat_ejd.close();
        aConn.commit();
        q = "create table EntryJudgesTable ('entryjudges_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'judgeassignment_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL,  FOREIGN KEY (judgeassignment_id) REFERENCES JudgeAssignmentsTable (judgeassignment_id));";
        Statement stat_ejc = aConn.createStatement();
        stat_ejc.executeUpdate(q);
        stat_ejc.close();
        aConn.commit();
        
// JudgeAssignmentsTable
        Statement stat_jad = aConn.createStatement();
        stat_jad.executeUpdate("drop table if exists JudgeAssignmentsTable;");
        aConn.commit();
        stat_jad.close();
        q = "create table JudgeAssignmentsTable ('judgeassignment_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'car_node' INTEGER NOT NULL, 'owner_node' INTEGER NOT NULL, 'timeslot' INTEGER);";
        Statement stat_jac = aConn.createStatement();
        stat_jac.executeUpdate(q);
        aConn.commit();
        stat_jac.close();
        aConn.setAutoCommit(true);
        
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
}
public void clearMasterPersonnelAndMasterJaguarTables(Connection aConn, Logger aLogger){
        String q;
        try {
            Statement stat_1 = aConn.createStatement();
            stat_1.executeUpdate("drop table if exists MasterJaguar;");
            stat_1.close();
            
            Statement stat_2 = aConn.createStatement();
            q = "create table MasterJaguar ('masterjaguar_id' INTEGER PRIMARY KEY AUTOINCREMENT,  'masterpersonnel_id' INTEGER,   'jcnaclass_c' TEXT NOT NULL, 'jcnaclass_d' TEXT NOT NULL, 'joclacategory' TEXT NOT NULL, 'year' INTEGER NOT NULL, 'model' TEXT NOT NULL,  'description' TEXT NOT NULL, 'unique_desc' TEXT NOT NULL, 'color' TEXT NOT NULL, 'platevin' TEXT NOT NULL,  FOREIGN KEY (masterpersonnel_id) REFERENCES MasterPersonnel (masterpersonnel_id)) ;";
            stat_2.executeUpdate(q);
            stat_2.close();

            Statement stat_3 = aConn.createStatement();
            stat_3.executeUpdate("drop table if exists MasterPersonnel;");
            stat_3.close();
            
            Statement stat_4 = aConn.createStatement();
            q = "create table MasterPersonnel ('masterpersonnel_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'jcna' INTEGER NOT NULL, 'club' TEXT, 'lastname' TEXT NOT NULL , 'firstname' TEXT NOT NULL, 'mi' TEXT, 'unique_name' TEXT NOT NULL, 'judgestatus' TEXT, 'cert_year' INTEGER, 'address_street' TEXT, 'city' TEXT, 'state' TEXT, 'country' TEXT, postalcode TEXT, 'phone_work' TEXT, 'phone_home' TEXT, 'phone_cell' TEXT, 'email' TEXT) ;";
            stat_4.executeUpdate(q);
            stat_4.close();
            
        } catch (SQLException ex) {
            aLogger.log(Level.SEVERE, null, ex);
        }

    
}

public boolean tableExists(Connection aConn, Logger aLoggger, String aTableName){
    
    //String q = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name= " + "'table_name'";
    String q = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + aTableName + "';";
    boolean result = false;
     int val = -1;
        try { 
            Statement stmt;
            stmt = aConn.createStatement();
            ResultSet res = stmt.executeQuery(q);
            while(res.next()){
                val = res.getInt(1);
            }
            if(val == -1){ result = true;} else {result = false;}
            stmt.close();
            res.close();
        } catch (SQLException ex) {
            aLoggger.log(Level.SEVERE, null, ex);
        }
        return result;
}

public Long getMasterJaguarID(Connection aConn, String aUniqueDesc){
            Long mjid = 0L;
            ResultSet rs;
            PreparedStatement prepStmt;
            String q_mp1 = "select masterjaguar_id from MasterJaguar where unique_desc like ?" ;
        try {
            //Statement stmt = aConn.createStatement();
            //rs = stmt.executeQuery(q);
            prepStmt = aConn.prepareStatement(q_mp1);
            prepStmt.setString(1, aUniqueDesc);
            rs = prepStmt.executeQuery();
            
            if (rs.next()) {
                mjid = rs.getLong("masterjaguar_id")  ;
                rs.close();
            }
            else{
                rs.close();
                theConcours.GetLogger().info("ERROR: Could not find Jaguar named " + aUniqueDesc + " in MasterJaguar DB table");
                okDialog("ERROR: Could not find Jaguar named " + aUniqueDesc + " in MasterJaguar DB table");
                System.exit(-1);
            }
            
        } catch (SQLException ex) {
            String msg = "ERROR: SQLException in getMasterJaguarID";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
            //Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return mjid;
}

 public static void main(String[] args) throws SQLException{
    
//
//      This main() is used as a utility. The following flags determine what is actually done
//     
    boolean createSettingsStateTable = false; // this only needs to be done if theSettingsStateTable structure is chabged
    boolean LoadAllTablesFromCsvFiles = false;
    boolean LoadJCNAClassesTableFromCsvFile = true;
    boolean displayJCNAClasses = false;
    boolean displayJudges = false;
    boolean displayJaguars = false;
    boolean displayEntries = false;
    boolean displayOwners = false;
    boolean boolCreatNewConcours = true;

    //
    //  Establish connection to the database
    //
//    String strDBName = "SDJC2014_01_20_2015";
      String strPath = "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours";
//    String strPath = "C:\\Users\\jag_m_000\\Documents\\Concours";
      String strDBName = "SoCalBase"; //A Concours database with only the MasterPersonnel, MasterJaguar, & JCNAClasses tables
//    String strPath = "C:\\Users\\Ed Sowell\\Documents\\JOCBusiness\\Concours";
//    String strJCNAClassesCSVFileName = "JCNAClassesCsv.txt";
    String strJCNAClassesCSVFileName = "JCNANewClassesCsv.txt"; // AGM 2015
//    String strMasterPersonnelCSVFileName = "MasterPersonnelListCsv.txt"; 
    String strMasterPersonnelCSVFileName = "MasterPersonnelListNewJCNAClassesCsv.txt";  // updated for AGM 2015 JCNA Class changes
    Connection conn = null;
    try {
      Class.forName("org.sqlite.JDBC");
      String strConn;
      strConn = "jdbc:sqlite:" + strPath + "\\" + strDBName + ".db";
      conn = DriverManager.getConnection(strConn);
    } catch ( ClassNotFoundException | SQLException e ) {
      theConcours.GetLogger().info( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    theConcours.GetLogger().info("Opened database " + strDBName + ".db successfully");
    
    if(createSettingsStateTable){
        LoadSQLiteConcoursDatabase.CreateSettingsStateTable(conn);
        boolean currentState = LoadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn);
        theConcours.GetLogger().info("Initial state is: " + currentState);
        LoadSQLiteConcoursDatabase.SetSettingsTableJAState(conn, true);
        currentState = LoadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn);
        theConcours.GetLogger().info("After setting to 'true' State is: " + currentState);
        LoadSQLiteConcoursDatabase.SetSettingsTableJAState(conn, false);
        currentState = LoadSQLiteConcoursDatabase.GetSettingsStateTableJAState(conn);
        theConcours.GetLogger().info("After setting to 'false' State is: " + currentState);
        
    }
    if(LoadAllTablesFromCsvFiles){
        //
        // Create JCNAClassesTable
        //
       
        //  not compatible with changes 5/1/2017
        //LoadSQLiteConcoursDatabase.LoadJCNAClassesTableFromCSV(conn, strPath + "\\" + strJCNAClassesCSVFileName); // don't need an instance of LoadSQLiteConcoursDatabase to use its methods
        
        //
        // Create the Master Personnel Table based on JOCLA Membership data (augmented by SDJC 2014 Concours entrants
        //
        try {
            LoadSQLiteConcoursDatabase.LoadMasterPersonnelAndJaguarTablesFromCSV(conn, strPath, strMasterPersonnelCSVFileName); // don't need an instance of LoadSQLiteConcoursDatabase to use its methods
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        //
        // Create ConcoursEntriesTable
        //
        try {
            LoadSQLiteConcoursDatabase.LoadConcoursEntriesTable(conn, strPath); // don't need an instance of CreateSQLiteConcoursDatabase to use its methods
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        //
        // Create ConcoursJudgesTable
        //
        try {
            LoadSQLiteConcoursDatabase.LoadConcoursJudgesTable(conn, strPath); // don't need an instance of LoadSQLiteConcoursDatabase to use its methods
        } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    try{
        LoadConcoursClassesTable(conn, strPath+ "\\ClassesCSV.txt");
    } catch (SQLException ex) {
            Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
    if(LoadJCNAClassesTableFromCsvFile) {        //
        // Create JCNAClassesTable
        //
        // not compatible with changes 5/1/2017
        //LoadSQLiteConcoursDatabase.LoadJCNAClassesTableFromCSV(conn, strPath + "\\" +strJCNAClassesCSVFileName); // don't need an instance of LoadSQLiteConcoursDatabase to use its methods
        
    }
    if(displayJCNAClasses) DisplayJCNAClassesTable(conn, strPath);

    if(displayEntries) DisplayConcoursEntriesTable(conn, strPath);
    
    if(displayJudges)DisplayConcoursJudgesTable(conn, strPath);
    
    if(displayJaguars)DisplayJaguarsByOwner(conn, strPath);
    if(displayOwners)DisplayOwners(conn, strPath);
    
    if(boolCreatNewConcours){
        okCancelDialog("This takes far too long, but be patient!");
        CreateNewConcoursFromCSV(conn, strPath, strJCNAClassesCSVFileName, strMasterPersonnelCSVFileName);
    }
    
    conn.close(); 
        
   } // main
}  // class

