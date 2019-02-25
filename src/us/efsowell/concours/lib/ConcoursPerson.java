/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

/**
 *
 * @author Ed Sowell
 * Mirrors row in database ConcoursPersonnel table
 * Not sure this is useful since everything in it can be gleaned from Entries & Judges. OTOH, it might be more efficient to accumulate all this info
 * in one place...
*/


public class ConcoursPerson {
  // long concourspersonnel_id;  Not used anywhere, and not a good idea to be reaching back into the database.
    Long masterpersonnel_id;
    String unique_name;
    int status_o;
    int status_j;
    int concourspersonnel_node;
    
    public  ConcoursPerson( Long aMasterpersonnel_id,  String aUnique_name,  int aStatus_o,  int aStatus_j, int aConcourspersonnel_node){
        // concourspersonnel_id is assigned when the wne the corresponding database table is created... doesn't exist when the
        // ConcoursPersonnel object is created from the user interface.
        masterpersonnel_id = aMasterpersonnel_id;
        unique_name = aUnique_name;
        status_o = aStatus_o;
        status_j = aStatus_j;
        concourspersonnel_node = aConcourspersonnel_node;
    }

  /*  public  ConcoursPerson( Long aConcourspersonnel_id, Long aMasterpersonnel_id,  String aUnique_name,
                int aStatus_o,  int aStatus_j, int aConcourspersonnel_node){
        concourspersonnel_id = aConcourspersonnel_id;
        masterpersonnel_id = aMasterpersonnel_id;
        unique_name = aUnique_name;
        status_o = aStatus_o;
        status_j = aStatus_j;
        concourspersonnel_node = aConcourspersonnel_node;
    }
    
*/   
    /*
    public Long GetConcoursPersonnel_id(){
        return concourspersonnel_id ;
    }
*/
    
 /*
    public void SetConcoursPersonnel_id(long aConcourspersonnel_id){
        concourspersonnel_id = aConcourspersonnel_id;
    }
*/
    public Long GetMasterPersonnel_id(){
        return masterpersonnel_id ;
    }

    
    public void SetMasterPersonnel_id(long aMasterpersonnel_id){
        masterpersonnel_id = aMasterpersonnel_id;
    }
    
    public void SetStatus_o(int aStatus_o){
        status_o = aStatus_o;
    }
    public void SetStatus_j(int aStatus_j){
        status_j = aStatus_j;
    }
    public int GetConcoursPersonnel_node(){
            return concourspersonnel_node;
    }

    public void SetConcoursPersonnel_node(int aConcourspersonnel_node){
        concourspersonnel_node = aConcourspersonnel_node;
    }
    
    public int GetStatus_o(){
            return status_o;
    }
    public int GetStatus_j(){
            return status_j;
    }
    public String GetUniqueName(){
            return unique_name;
    }
    
}
