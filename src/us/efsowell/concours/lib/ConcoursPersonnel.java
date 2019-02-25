/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Ed Sowell
 * 
 * Not sure this is useful since everything in it can be gleaned from Entries & Judges. OTOH, it might be more efficient to accumulate all this info
 * in one place...
 */
public class ConcoursPersonnel {

    ArrayList<ConcoursPerson> concoursPersonnelList ; // All Personnel in the Concours. Both Entrants & Judges  are included
 

    // Constructor
    public ConcoursPersonnel(){
       concoursPersonnelList =  new ArrayList<>();
    }


    
    public void AddPerson(ConcoursPerson aPerson){
         concoursPersonnelList.add(aPerson);
    }

    public void RemovePerson(ConcoursPerson aPerson){
         concoursPersonnelList.remove(aPerson);
    }

    
    public void SetPersonOwnerStatus(String aUniqueName, int aOwnerStatus){
        for (ConcoursPerson cp : concoursPersonnelList) {
            if (cp.unique_name.equals(aUniqueName)) {
                cp.SetStatus_o(aOwnerStatus);
                break;
            } 
        }
    }
    public void SetPersonJudgeStatus(String aUniqueName, int aJudgeStatus){
        for (ConcoursPerson cp : concoursPersonnelList) {
            if (cp.unique_name.equals(aUniqueName)) {
                cp.SetStatus_j(aJudgeStatus);
                break;
            } 
        }
    }
    
    public ArrayList<ConcoursPerson> GetConcoursPersonnelList(){
        return concoursPersonnelList;
    }

    // returns Concours Personnel node if aUniqueName is already there, 0 if not there
    public int PersonInPersonnelList(String aUniqueName){
        int result = 0;
        for (ConcoursPerson cp : concoursPersonnelList) {
            if (cp.unique_name.equals(aUniqueName)) {
                result = cp.GetConcoursPersonnel_node();
                break;
            } else {
            }
        }
        return result;
    }
    // returns Concours Person or null
    public ConcoursPerson GetConcoursPerson(Integer aNode){
        ConcoursPerson result = null;
        for (ConcoursPerson cp : concoursPersonnelList) {
            if (cp.concourspersonnel_node == aNode) {
                result = cp;
                break;
            } 
        }
        return result;
    }

    public ConcoursPerson GetConcoursPerson(String strUniqueName){
        ConcoursPerson result = null;
        for (ConcoursPerson cp : concoursPersonnelList) {
            if (cp.GetUniqueName().equals(strUniqueName)) {
                result = cp;
                break;
            } 
        }
        return result;
    }

    public ConcoursPersonnel GetConcoursPersonnelObject(){
        return this;
    }
            
    // Return the next available Person node number for matching graph ... current highest + 1
    // Note: Owners & Judges share this node numbering.  IOW, if a Person in both an Owner & a Judge they have the same Node number
    //       in both roles.
    //
    /*
    public int GetNextPersonnelNode(){
        int n = 0;
        for(int i = 0; i <concoursPersonnelList.size(); i++ ){
            if (concoursPersonnelList.get(i).concourspersonnel_node > n){
                n = concoursPersonnelList.get(i).concourspersonnel_node;
            } else {
            }
        }
        return ++n;
    }
 */ 
    /*  
    public Long GetConcoursPersonnelID(Long mpid){
        Long cpid;
        cpid = 0L;
        for(int i = 0; i<concoursPersonnelList.size(); i++ ){
            if (concoursPersonnelList.get(i).masterpersonnel_id == mpid) {
                cpid = concoursPersonnelList.get(i).concourspersonnel_id;
                break;
            }
        }
        return cpid;
    }
    */
public int NextNode(){
        int n = 0;
        for(int i = 0; i <concoursPersonnelList.size(); i++ ){
            if (concoursPersonnelList.get(i).concourspersonnel_node > n){
                n = concoursPersonnelList.get(i).concourspersonnel_node;
            } else {
            }
        }
        return ++n;
}    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
