/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

/**
 *
 * @author Ed Sowell
 */

/*
 *                    THIS ISN'T USED... 
 *
*/
public class ConcoursOwner {
    Long masterpersonnel_id;
    String unique_name;
    int status_o;
    int status_j;
    int concourspersonnel_node;
    MasterJaguar[] jaguarstable;
    
    
    public  ConcoursOwner( Long aMasterpersonnel_id,  String aUnique_name,  int aStatus_o,  int aStatus_j, int aConcourspersonnel_node, MasterJaguar[] aJaguarstable){
        // concourspersonnel_id is assigned when the wne the corresponding database table is created... doesn't exist when the
        // ConcoursPersonExtnel object is created from the user interface.
        masterpersonnel_id = aMasterpersonnel_id;
        unique_name = aUnique_name;
        status_o = aStatus_o;
        status_j = aStatus_j;
        concourspersonnel_node = aConcourspersonnel_node;
        jaguarstable = aJaguarstable;
    }

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
    public int GetConcoursPersonExtnel_node(){
            return concourspersonnel_node;
    }

    public void SetConcoursPersonExtnel_node(int aConcourspersonnel_node){
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
