/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

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
public class MasterJaguars {
    ArrayList<MasterJaguar> masterJaguarList ; // All Jaguars owned by those in MasterPersonnel
    

    // Constructor
    public MasterJaguars(){
       masterJaguarList =  new ArrayList<>();
    }
    // Loads the MasterJaguars list from the MasterJaguars database table
    public  void LoadMasterJagaursDB(Connection aConn){
        
        Long masterjaguar_id;
        Long masterpersonnel_id;
        String jcnaclass_c;
        String jcnaclass_d;
        String joclacategory;
        Integer year;
        String model; // 3/17/2017
        String description;
        String unique_desc;
        String color;
        String platevin;
        
        MasterJaguar theMasterJaguar;

        Statement stat_mj;
        ResultSet rs_mj;
        int i ;
        try{
            stat_mj = aConn.createStatement();
            rs_mj = stat_mj.executeQuery("select * from MasterJaguar;"); 
            i = 1;
            while (rs_mj.next()) { 
                System.out.println("Record: " + i + " Unique description: " + rs_mj.getString("unique_desc") + " " + "MasterPersonnel ID: " + rs_mj.getInt("masterpersonnel_id"));
                masterjaguar_id = rs_mj.getLong("masterjaguar_id");
                masterpersonnel_id = rs_mj.getLong("masterpersonnel_id");
                jcnaclass_c =  rs_mj.getString("jcnaclass_c");
                jcnaclass_d =  rs_mj.getString("jcnaclass_d");
                joclacategory =  rs_mj.getString("joclacategory");
                year = rs_mj.getInt("year");
                description =  rs_mj.getString("description");
                //model = description;  // MUST BE FIXED AFTER EDITING masterjagau table 3/17/2017
                model = rs_mj.getString("model");
                unique_desc =  rs_mj.getString("unique_desc");
                color =  rs_mj.getString("color");
                platevin =  rs_mj.getString("platevin");
                theMasterJaguar = new MasterJaguar(masterjaguar_id, masterpersonnel_id, jcnaclass_c, jcnaclass_d, joclacategory, year, model, description, unique_desc, color, platevin);
                masterJaguarList.add(theMasterJaguar);
                i++;
            }
        
        
            rs_mj.close();
    
        } catch (SQLException ex) {
            Logger.getLogger(JCNAClasses.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public void AddJaguar(MasterJaguar aJaguar){
        masterJaguarList.add(aJaguar);
    }
    public void RemoveJaguar(MasterJaguar aJaguar){
        masterJaguarList.remove(aJaguar);
    }

    public ArrayList<MasterJaguar> GetMasterJaguarList(){
        return masterJaguarList;
    }
    public MasterJaguar GetMasterJaguar(String aUnigueDesc){
        MasterJaguar result = null;
        String ud;
        for(MasterJaguar mj : masterJaguarList){
            ud = mj.getUniqueDesc();
            //System.out.println("ud["+ ud + "] " + " aUnigueDesc[" + aUnigueDesc + "]");
            if(ud.equals(aUnigueDesc)){
                result = mj;
                break;
            }
        }
        return result;
    }
}

    
