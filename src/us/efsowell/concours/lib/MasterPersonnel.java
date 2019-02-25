/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import java.sql.Connection;
import java.sql.DriverManager;
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
public class MasterPersonnel {
    
    ArrayList<MasterPersonExt> masterPersonnelList ; // All Personnel of record. Must be JCNA members. Both Entrants & Judges from current & past Concours events are included
 

    // Constructor
    public MasterPersonnel(){
       masterPersonnelList =  new ArrayList<>();
    }


    
    public void AddMasterPerson(MasterPersonExt aPerson){
        masterPersonnelList.add(aPerson);
    }
    
    public ArrayList<MasterPersonExt> GetMasterPersonnelList(){
        return masterPersonnelList;
    }
    public MasterPersonnel GetMasterPersonnelObject(){
        return this;
    }

    public Long GetMasterPersonnelID(String aUniqueName){
        Long mpid;
        mpid = 0L;
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                mpid = masterPersonnelList1.masterpersonnel_id;
                break;
            }
        }
        return mpid;
    }
    
    public MasterPersonExt GetMasterPerson(String aUniqueName){
        MasterPersonExt theMasterPerson = null;
        for (MasterPersonExt mp : masterPersonnelList) {
            if (mp.getUniqueName().equals(aUniqueName)) {
                theMasterPerson = mp;
                break;
            }
        }
        return theMasterPerson;
    }
    
    
    public String GetMasterPersonnelLastName(String aUniqueName){
        String lastName = "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                lastName = masterPersonnelList1.getLastName();
                break;
            }
        }
        return lastName;
    }
    public String GetMasterPersonnelFirstName(String aUniqueName){
        String firstName = "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                firstName = masterPersonnelList1.getFirstName();
                break;
            }
        }
        return firstName;
    }
    public String GetMasterPersonnelMI(String aUniqueName){
        String mi = "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                mi = masterPersonnelList1.getMI();
                break;
            }
        }
        return mi;
    }

    public String GetMasterPersonnelStreetAddress(String aUniqueName){
        String sa = "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                sa = masterPersonnelList1.getAddressSreet();
                break;
            }
        }
        return sa;
    }
    

    public String GetMasterPersonnelCity(String aUniqueName){
        String c = "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                c = masterPersonnelList1.getCity();
                break;
            }
        }
        return c;
    }
    
    public String GetMasterPersonnelState(String aUniqueName){
        String s = "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                s = masterPersonnelList1.getState();
                break;
            }
        }
        return s;
    }
    
    public String GetMasterPersonnelPostalCode(String aUniqueName){
        String pc =  "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                pc = masterPersonnelList1.getPostalCode();
                break;
            }
        }
        return pc;
    }

    public String GetMasterPersonnelClub(String aUniqueName){
        String c =  "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                c = masterPersonnelList1.getClub();
                break;
            }
        }
        return c;
    }
    

    public String GetMasterPersonnelCityStatePostalCode(String aUniqueName){
        String cspc =  "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                cspc = masterPersonnelList1.getCity() + ", " +  masterPersonnelList1.getState()+ " " +  masterPersonnelList1.getPostalCode();
                break;
            }
        }
        return cspc;
    }
    
    
    public Integer GetMasterPersonnelJCNA(String aUniqueName){
        Integer jcna = 0;
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.getUniqueName().equals(aUniqueName)) {
                jcna = masterPersonnelList1.getJcna();
                break;
            }
        }
        return jcna;
    }

    public String GetMasterPersonnelUnique(String aLastName){
        String uniqueName = "";
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.lastname.equals(aLastName)) {
                uniqueName = masterPersonnelList1.getUniqueName();
                break;
            }
        }
        return uniqueName;
    }
    /*
     Returns a list of first names of  Master list persons with a given last name
    */
    public ArrayList<String> GetMasterPersonnelFirstNames(String aLastName){
         ArrayList<String> firstnames = new  ArrayList<String>();
        for (MasterPersonExt masterPersonnelList1 : masterPersonnelList) {
            if (masterPersonnelList1.lastname.equals(aLastName)) {
                firstnames.add(masterPersonnelList1.getFirstName());
            }
        }
        return firstnames;
    }

    /*
     Returns a list of unique names of  Master list persons with a given last and first name
    */
    public ArrayList<String> GetMasterPersonnelUniqueNames(String aLastName, String aFirstName){
         ArrayList<String> uniquenames = new  ArrayList<String>();
         String fn;
         String ln;
        for (MasterPersonExt masterperson : masterPersonnelList) {
            ln = masterperson.lastname;
            fn = masterperson.firstname;
            if (ln.equals(aLastName)){
                if (fn.equals(aFirstName)){
                    uniquenames.add(masterperson.getUniqueName());   
                }
            }
        }
        return uniquenames;
    }
    /*
     Returns a list of unique names   Master list persons with the argument aUniqueName.
     Should be 1 at most!
    */
    public ArrayList<String> GetMasterPersonnelUniqueNames(String aUniqueName){
        ArrayList<String> uniquenames = new  ArrayList<String>();
        String un;
        for (MasterPersonExt masterperson : masterPersonnelList) {
            //ln = masterperson.lastname;
            //fn = masterperson.firstname;
            un = masterperson.getUniqueName();
            if (un.equals(aUniqueName)){
                    uniquenames.add(un);   
            }
        }
        return uniquenames;
    }
    
    public void RemoveFromMasterPersonnel(List<MasterPersonExt> aMasterPersonList){
        masterPersonnelList.removeAll(aMasterPersonList);
    }
    
    public int GetMasterMersonnelSize(){
        return masterPersonnelList.size();
    }
    
/* public static void main(String[] args) throws SQLException{
    MasterPersonnel theMasterPersonnel = new MasterPersonnel();
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
    System.out.println("Opened database " + strDBName + ".db successfully");
    theMasterPersonnel.LoadMasterPersonnelDB(conn);
    conn.close(); 
}    
    */


    
}
