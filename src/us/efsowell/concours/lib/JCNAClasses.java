/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Sowell
 */
public class JCNAClasses {
    ArrayList<JCNAClass> jcnaClasses ; // All Classes defined by JCNA Initialized from CSV file JCNAClasses
    Logger logger;

    // Constructor
    public JCNAClasses(Logger aLogger){
        jcnaClasses =  new ArrayList<>();
        logger = aLogger;
    }

public ArrayList<JCNAClass> getJCNAClasses(){
    return jcnaClasses;
}    
    

    
public void LoadJCNAClassesCSV(String strPath, Logger aLogger){
    String [] nextLine;
    String strDivision;
    String strClassName;
    String strNotes;
    String strDescription;
    String strJudgeAssignGroup;
    String strModelYearLookup;
    //Integer intNode;
    Long lngID;
    
    CSVReader reader;
    int iLine;
    String JCNAClassesCSV = strPath + "\\JCNAClassesCsv.txt" ;
    aLogger.info("Starting LoadJCNAClassesCSV");
    try {
        reader = new CSVReader(new FileReader(JCNAClassesCSV ));
        iLine = 0;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
        // System.out.println(nextLine[0] + nextLine[1] );
            if (iLine > 0){
                lngID = Long.parseLong(String.valueOf(iLine));
                strDivision = nextLine[0];
                strClassName = nextLine[1];
                strNotes= nextLine[2];
                strDescription= nextLine[3];
                strJudgeAssignGroup = nextLine[4];
                strModelYearLookup = nextLine[5];
                
                    
               // jcnaClasses.add(new JCNAClass(lngID, strDivision, strClassName, strNotes, strDescription,  strJudgeAssignGroup, iLine));
                jcnaClasses.add(new JCNAClass(strDivision, strClassName, strNotes, strDescription,  strJudgeAssignGroup, strModelYearLookup, iLine));
            }
            iLine++;
        }
    }    catch (FileNotFoundException ex) {
            okDialog("ERROR: FileNotFoundException in LoadJCNAClassesCSV");
            logger.info("ERROR: FileNotFoundException in LoadJCNAClassesCSV");
            logger.log(Level.SEVERE, null, ex);
            System.exit(-1);
        }    
        catch (IOException ex) {
            okDialog("ERROR: IOException in LoadJCNAClassesCSV");
            logger.info("ERROR: IOException in LoadJCNAClassesCSV");
            logger.log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    
    aLogger.info("Finished LoadJCNAClassesCSV");
            
}

private void addMyLookupColumnToJCNAClassesTable(Connection aConn, Logger aLogger) throws SQLException{
        aConn.setAutoCommit(false);
        aLogger.info("Starting addMyLookupColumnToJCNAClassesTable");
        // drop tempOldJCNAClassRulesTable if exists                  ORDER IS IMPORTANT DUE TO FOREIGN KEY
        Statement statDropIfExists1 = aConn.createStatement();
        String q_delete = "drop table if exists tempOldJCNAClassRules";
        statDropIfExists1.executeUpdate(q_delete);
        statDropIfExists1.close();

        // drop tempOldJCNAClassesTable if exists
        q_delete = "drop table if exists tempOldJCNAClasses";
        Statement statDropIfExists2 = aConn.createStatement();
        statDropIfExists2.executeUpdate(q_delete);
        statDropIfExists2.close();
        // Rename the current JCNAClasses table to tempOldJCNAClasses
        Statement statRenameTable1 = aConn.createStatement();
        String q_RenameTable = "ALTER TABLE JCNAClasses RENAME TO tempOldJCNAClasses";
        statRenameTable1.executeUpdate(q_RenameTable);
        statRenameTable1.close();

        // Rename the current JCNAClassRules  to tempOldJCNAClassRules
        q_RenameTable = "ALTER TABLE JCNAClassRules RENAME TO tempOldJCNAClassRules";
        Statement statRenameTable2 = aConn.createStatement();
        statRenameTable2.executeUpdate(q_RenameTable);
        statRenameTable2.close();

        aLogger.info("Renamed JCNAClasses &   JCNAClassRules in addMyLookupColumnToJCNAClassesTable");
        
        // Create a new table, reusing the name of the original table, but with wanted new column
        Statement statCreateNewTable1 = aConn.createStatement();
        
       // String qCreatetable = "create table " + aTableName + " (ID	INTEGER PRIMARY KEY AUTOINCREMENT, division TEXT NOT NULL, class TEXT NOT NULL UNIQUE, description TEXT NOT NULL, note	TEXT NOT NULL, judgeassigngroup TEXT NOT NULL, mylookup TEXT NUT NULL, node INTEGER NOT NULL);";
        
        String q_CreateNewTable = "CREATE TABLE JCNAClasses (ID INTEGER PRIMARY KEY AUTOINCREMENT, division TEXT NOT NULL, class TEXT NOT NULL UNIQUE, description TEXT NOT NULL, note TEXT NOT NULL, judgeassigngroup TEXT NOT NULL, mylookup TEXT NOT NULL DEFAULT yes, node INTEGER NOT NULL)";
        statCreateNewTable1.executeUpdate(q_CreateNewTable);
        statCreateNewTable1.close();
        
        // Insert data from tempOldJCNAClasses into JCNAClasses
        Statement statInsertData1 = aConn.createStatement();
        String q_InsertData = "INSERT INTO JCNAClasses (ID, division, class, description, note, judgeassigngroup, node) SELECT ID, division, class, description, note, judgeassigngroup, node FROM tempOldJCNAClasses";        
        statInsertData1.executeUpdate(q_InsertData);
        aLogger.info("Inserted data into JCNAClasses into table with new mylookup column in addMyLookupColumnToJCNAClassesTable");
        statInsertData1.close();
        q_CreateNewTable = "CREATE TABLE JCNAClassRules (ID INTEGER PRIMARY KEY AUTOINCREMENT, division TEXT NOT NULL,  classname TEXT NOT NULL, class_id INTEGER NOT NULL, modelname TEXT NOT NULL, descriptor_1 TEXT, descriptor_2 TEXT, descriptor_3 TEXT, firstyear INTEGER NOT NULL, lastyear INTEGER NOT NULL, FOREIGN KEY (class_id) REFERENCES JCNAClasses (ID) )";
        Statement statCreateNewTable2 = aConn.createStatement();
        statCreateNewTable2.executeUpdate(q_CreateNewTable);
        statCreateNewTable2.close();
        
        
        // Insert data from tempOldJCNAClassRules into JCNAClassRules
//                                                  'ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'division' TEXT NOT NULL,  'classname' TEXT NOT NULL, 'class_id' INTEGER NOT NULL, 'modelname' TEXT NOT NULL, 'descriptor_1' TEXT, 'descriptor_2' TEXT, 'descriptor_3' TEXT, 'firstyear' INTEGER NOT NULL, 'lastyear' INTEGER NOT NULL, FOREIGN KEY (class_id) REFERENCES " + aClassesTableName + " (ID)        
        q_InsertData = "INSERT INTO JCNAClassRules (ID, division,  classname, class_id, modelname, descriptor_1, descriptor_2, descriptor_3, firstyear, lastyear) SELECT ID, division,  classname, class_id, modelname, descriptor_1, descriptor_2, descriptor_3, firstyear, lastyear FROM tempOldJCNAClassRules"; 
        Statement statInsertData2 = aConn.createStatement();
        statInsertData2.executeUpdate(q_InsertData);
        statInsertData2.close();
        aLogger.info("Inserted data into JCNAClassRules in addMyLookupColumnToJCNAClassesTable");

        q_delete = "drop table if exists tempOldJCNAClassRules";
        Statement statDropIfExists3 = aConn.createStatement();
        statDropIfExists3.executeUpdate(q_delete);
        statDropIfExists3.close();
        
        q_delete = "drop table if exists tempOldJCNAClasses";
        Statement statDropIfExists4 = aConn.createStatement();
        statDropIfExists4.executeUpdate(q_delete);
        statDropIfExists4.close();
        
        aLogger.info("Dropped the temporary files in addMyLookupColumnToJCNAClassesTable");
        //
        //  Now, set the mylookup fields for Special and Preservations Classes to "no"
        //
        aLogger.info("Write 'no' into mylookup for Special and Preservation Classesin addMyLookupColumnToJCNAClassesTable");
        String q_Update = "UPDATE JCNAClasses SET mylookup = ? WHERE class LIKE ?";
        PreparedStatement psUpdate1 = aConn.prepareStatement(q_Update);
        psUpdate1.setString(1, "no");
        psUpdate1.setString(2, "C17/PN");
        psUpdate1.executeUpdate();
        aConn.commit();
        psUpdate1.close();
        
        PreparedStatement psUpdate2 = aConn.prepareStatement(q_Update);
        psUpdate2.setString(1, "no");
        psUpdate2.setString(2, "C18/PN");
        psUpdate2.executeUpdate();
        aConn.commit();
        psUpdate2.close();

        PreparedStatement psUpdate3 = aConn.prepareStatement(q_Update);
        psUpdate3.setString(1, "no");
        psUpdate3.setString(2, "S01/PD");
        psUpdate3.executeUpdate();
        aConn.commit();
        psUpdate3.close();
        
        PreparedStatement psUpdate4 = aConn.prepareStatement(q_Update);
        psUpdate4.setString(1, "no");
        psUpdate4.setString(2, "S02/MOD");
        psUpdate4.executeUpdate();
        aConn.commit();
        psUpdate4.close();
        
        PreparedStatement psUpdate5 = aConn.prepareStatement(q_Update);
        psUpdate5.setString(1, "no");
        psUpdate5.setString(2, "S03/REP");
        psUpdate5.executeUpdate();
        aConn.commit();
        psUpdate5.close();
        
        aConn.setAutoCommit(true);
        String msg = "Added mylookup column To JCNAClassesTable";
        aLogger.info(msg);
  }
    

/////////////////////////////////////////////////////////////////////////////
//     Loads JCNA classes from the JCNAClasses table in the concours database to internal jcnaClasses data structure 
/////////////////////////////////////////////////////////////////////////////
public  void LoadJCNAClassesDB(Connection aConn, String aTableName, Logger aLogger){
    //Long lngID;
    String strDivision;
    String strClassName;
    String strNotes;
    String strDescription;
    Integer intNode;
    String strJudgeAssignGroup;
    String strModelYearLookup;
    Statement stat_c;
    ResultSet rs_c;
    System.out.println("Started LoadJCNAClassesDB to load table " + aTableName + " from DB to concours in-memory structure");
    aLogger.info("Started LoadJCNAClassesDB to load table " + aTableName + " from DB to concours in-memory structure");

            Statement stat_test = null;
            ResultSet rs_test = null;
        try {
            stat_test = aConn.createStatement();
            String q;
            q = "select mylookup from " +  aTableName + ";";
            rs_test = stat_test.executeQuery(q);
            rs_test.close();
            stat_test.close();
        } catch (SQLException ex) {
            String msg = "The structure of JCNAClasses table in Concours is not compatible with this version of ConcoursBuilder. It will be updated"
                          + "\nBe sure to Save the Concours at the end of the session.";
            aLogger.info(msg);
            okDialog(msg);
            try {            
                if(rs_test != null) rs_test.close();
                if(stat_test != null)stat_test.close();
                addMyLookupColumnToJCNAClassesTable(aConn, aLogger);
            } catch (SQLException ex1) {
                msg = "Failed to fix table. Bailing out";
                aLogger.info(msg);
                okDialog(msg);
                aLogger.log(Level.SEVERE, msg, ex1);
                System.exit(-1);
            }
        }
        try {
            stat_c = aConn.createStatement();
            String q = "select * from " +  aTableName + ";";
            rs_c = stat_c.executeQuery(q); 
            //i = 1;
            aConn.setAutoCommit(false);
            while (rs_c.next()) { 
             //  System.out.println("Record: " + i + " Division: " + rs_c.getString("division") + " " + "Class: " + rs_c.getString("class") + " Node: " +  rs_c.getString("node")+ " Description: " +  rs_c.getString("description"));
               // lngID = rs_c.getLong("ID");
                strDivision = rs_c.getString("division");
                strClassName = rs_c.getString("class");
                strDescription= rs_c.getString("description");
                strNotes = rs_c.getString("note");
                intNode = rs_c.getInt("node");
                strJudgeAssignGroup = rs_c.getString("judgeassigngroup");
                strModelYearLookup = rs_c.getString("mylookup");
                //jcnaClasses.add(new JCNAClass(lngID, strDivision, strClassName,  strDescription, strNotes, strJudgeAssignGroup, intNode));
                //public JCNAClass           (aDivision, aName,           aDescription,   aNotes,  aJudgeAssignGroup, String aModelYearLookup, Integer aNode) {
                jcnaClasses.add(new JCNAClass(strDivision, strClassName,  strDescription, strNotes, strJudgeAssignGroup, strModelYearLookup, intNode));
                //i++;
            } 
            aConn.commit();
            aConn.setAutoCommit(true);
            rs_c.close();
            stat_c.close();
        } catch (SQLException ex) {
            String msg = "ERROR: SQLException in LoadJCNAClassesDB";
            okDialog(msg);
            logger.log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
    
      aLogger.info("Finished LoadJCNAClassesDB in JCNAClasses");
}



public ArrayList<JCNAClass> GetJCNAClasses(){
    return jcnaClasses;
}

    public String getJCNAClassName(Integer aNode){
        
      int intSize = jcnaClasses.size();
      JCNAClass jcnac;
      String theClassName = "";
      for(int i = 1; i<intSize;i++){
          jcnac = jcnaClasses.get(i);
          if(jcnac.node == aNode){
              theClassName = jcnac.getName();
              break;
          }
      }
      return theClassName;
        
    }
   public JCNAClass getJCNAClass(String aClassName){
        
      int intSize = jcnaClasses.size();
      JCNAClass jcnac = null;
      boolean found = false;
      //for(int i = 0; i<intSize;i++){
      for(JCNAClass c : jcnaClasses){
         jcnac = c;
          if(jcnac.name.equals(aClassName)){
              found = true;
              break;
          }
      }
      if(!found) jcnac = null;
              
      return jcnac;
    }

 public static void main(String[] args) throws SQLException{
    //JCNAClasses theJCNAClassLoader = new JCNAClasses(logger);
    String strDBName = "TutorialE5J5\\TutorialE5J5";
    String strPath = "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours";
    //        String strPath= "C:\\Users\\jag_m_000\\Documents\\Concours" ;
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
    System.out.println("Opened database " + strDBName + ".db successfully");
    //theJCNAClassLoader.LoadJCNAClassesDB(conn);
    conn.close(); 
}    
}


