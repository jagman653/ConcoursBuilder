/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Sowell
 */
public class JCNAClassRules_2 {
   ArrayList<JCNAClassRule> jcnaclassrules;
   Logger theLogger;
   
   // Constructor
   public JCNAClassRules_2(Logger aLogger){
       jcnaclassrules = new ArrayList<JCNAClassRule>();
       theLogger = aLogger;
   }
   
   public ArrayList<JCNAClassRule> getJCNAClassRules(){
       return jcnaclassrules;
   }
   
   public  boolean LoadJCNAClassRulesDB(Connection aConn, String aTableName, Logger aLogger)  {
   // Long lngID;
    String division;
    String classname;
    Long class_id;
    String modelname;
    String descriptor_1;
    String descriptor_2;
    String descriptor_3;
    Integer firstyear;
    Integer lastyear;  
    
    Statement stat = null;
    ResultSet rs = null;
    //int i ;
   try {
       aConn.setAutoCommit(false);
   } catch (SQLException ex) {
       //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
        okDialog("SQL exception while aConn.setAutoCommit(false)in LoadJCNAClassRulesDB, " + JCNAClassRules_2.class.getName());
        aLogger.info("SQL exception while aConn.setAutoCommit(false) in LoadJCNAClassRulesDB, "  + JCNAClassRules_2.class.getName());
        aLogger.log(Level.SEVERE, "SQL exception while aConn.setAutoCommit(false) in LoadJCNAClassRulesDB", ex);
        return false;
   }
   try {
       stat = aConn.createStatement();
   } catch (SQLException ex) {
       //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
        okDialog("SQL exception while creating SQL Statement in LoadJCNAClassRulesDB, " + JCNAClassRules_2.class.getName());
        aLogger.info("SQL exception while creating SQL Statement in LoadJCNAClassRulesDB, "  + JCNAClassRules_2.class.getName());
        aLogger.log(Level.SEVERE, null, ex);
        return false;
   }
   try { 
       //rs = stat.executeQuery("select * from JCNAClassRules;");
       rs = stat.executeQuery("select * from " + aTableName + ";");
   } catch (SQLException ex) {
       //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
        okDialog("SQL exception while doing \"select * from JCNAClassRules\" in LoadJCNAClassRulesDB. Probably missing table. " + JCNAClassRules_2.class.getName());
        aLogger.info("SQL exception while doing \"select * from JCNAClassRules\" in LoadJCNAClassRulesDB. Probably missing table. "  + JCNAClassRules_2.class.getName());
        aLogger.log(Level.SEVERE, null, ex);
        return false;
   }
    //i = 1;
       try {
           while (rs.next()) {
               division = rs.getString("division");
               classname = rs.getString("classname");
               class_id = rs.getLong("class_id");
               modelname = rs.getString("modelname");
               descriptor_1 = rs.getString("descriptor_1");
               descriptor_2 = rs.getString("descriptor_2");
               descriptor_3 = rs.getString("descriptor_3");
               firstyear = rs.getInt("firstyear");
               lastyear = rs.getInt("lastyear");
               jcnaclassrules.add(new JCNAClassRule(division, classname,  class_id, modelname, descriptor_1, descriptor_2, descriptor_3, firstyear, lastyear));
               //i++; 
           }  
       } catch (SQLException ex) {
               //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
            okDialog("SQL exception while processing Class Rules in LoadJCNAClassRulesDB. " + JCNAClassRules_2.class.getName());
            aLogger.info("SQL exception while processing Class Rules in LoadJCNAClassRulesDB. "  + JCNAClassRules_2.class.getName());
            aLogger.log(Level.SEVERE, null, ex);
            return false;
       }
       try {
            aConn.commit();
            aConn.setAutoCommit(true);
            rs.close();
            stat.close();
       } catch (SQLException ex) {
           //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
            okDialog("SQL exception while finishing up in LoadJCNAClassRulesDB. " + JCNAClassRules_2.class.getName());
            aLogger.info("SQL exception while finishing up in LoadJCNAClassRulesDB. "  + JCNAClassRules_2.class.getName());
            aLogger.log(Level.SEVERE, null, ex);
            return false;
       }
        aLogger.info("LoadJCNAClassRulesDB completed successfully. "  + JCNAClassRules_2.class.getName());
        
       try {
           if(rs != null) rs.close();
       } catch (SQLException ex) {
           String msg = "Failed to close results set in LoadJCNAClassRulesDB";
           aLogger.log(Level.SEVERE, msg, ex);
       }
       return true;
   }   
   
   public Integer[] getMinFirstMaxLastYears(Connection aConn, String aTableName, Logger aLogger){
    int firstyear;
    int lastyear;  
    Statement stat = null;
    ResultSet rs = null;
    Integer [] results = new Integer[2]; 

    results[0] = 3000; // minFirstYear
    results[1] = 0;    // maxLastYear
   try {
       aConn.setAutoCommit(false);
   } catch (SQLException ex) {
       //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
       String msg = "SQL exception while aConn.setAutoCommit(false)in LoadJCNAClassRulesDB, " + JCNAClassRules_2.class.getName();
        okDialog(msg);
        aLogger.info(msg);
        aLogger.log(Level.SEVERE, "SQL exception while aConn.setAutoCommit(false) in LoadJCNAClassRulesDB", ex);
        return results;
   }
   try {
       stat = aConn.createStatement();
   } catch (SQLException ex) {
       //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
       String msg = "SQL exception while creating SQL Statement in LoadJCNAClassRulesDB, " + JCNAClassRules_2.class.getName();
        okDialog(msg);
        aLogger.info(msg);
        aLogger.log(Level.SEVERE, null, ex);
        return results;
   }
   try { 
       //rs = stat.executeQuery("select * from JCNAClassRules;");
       rs = stat.executeQuery("select * from " + aTableName + ";");
   } catch (SQLException ex) {
       String msg = "SQL exception while doing \"select * from JCNAClassRules\" in LoadJCNAClassRulesDB. Probably missing table. " + JCNAClassRules_2.class.getName();
        okDialog(msg);
        aLogger.info(msg);
        aLogger.log(Level.SEVERE, null, ex);
        return results;
   }
       try {
           while (rs.next()) {
               firstyear = rs.getInt("firstyear");
               lastyear = rs.getInt("lastyear");
               if(firstyear < results[0]) results[0] = firstyear;
               if(lastyear  > results[1]) results[1] = lastyear;
           }  
       } catch (SQLException ex) {
            okDialog("SQL exception while processing Class Rules in LoadJCNAClassRulesDB. " + JCNAClassRules_2.class.getName());
            aLogger.info("SQL exception while processing Class Rules in LoadJCNAClassRulesDB. "  + JCNAClassRules_2.class.getName());
            aLogger.log(Level.SEVERE, null, ex);
            return results;
       }
       try {
            aConn.commit();
            aConn.setAutoCommit(true);
            rs.close();
            stat.close();
       } catch (SQLException ex) {
           //Logger.getLogger(JCNAClassRules_2.class.getName()).log(Level.SEVERE, null, ex);
            okDialog("SQL exception while finishing up in LoadJCNAClassRulesDB. " + JCNAClassRules_2.class.getName());
            aLogger.info("SQL exception while finishing up in LoadJCNAClassRulesDB. "  + JCNAClassRules_2.class.getName());
            aLogger.log(Level.SEVERE, null, ex);
            return results;
       }
        aLogger.info("LoadJCNAClassRulesDB completed successfully. "  + JCNAClassRules_2.class.getName());
        
       try {
           if(rs != null) rs.close();
       } catch (SQLException ex) {
           String msg = "Failed to close results set in LoadJCNAClassRulesDB";
           aLogger.log(Level.SEVERE, msg, ex);
       }
       return results;
   }
}
