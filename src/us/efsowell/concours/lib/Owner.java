/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ed Sowell
 */
public class Owner {

    String uniquename;
    Integer node;
    int count; // number of entries in concours
    List<Integer> entrylist; // Entry Nodes
   // ArrayList<String> entryClassList;

    // Constructor
    public Owner(String aUnique_name, Integer aNode, int aCount, List<Integer> aEntrylist) {
        uniquename = aUnique_name;
        node = aNode;
        count = aCount;
        entrylist = aEntrylist;
    }

    // Alternative Constructor
    public Owner(String aUnique_name, Integer aNode) {
        uniquename = aUnique_name;
        node = aNode;
        count = 0;
        entrylist = new ArrayList<>();
    }

    public String getUniqueName() {
        return uniquename;

    }

    public Integer GetNode() {
        return node;

    }

    public int GetCount() {
        return count;
    }

    public List<Integer> GetEntryList() {
        return entrylist;

    }

    public void SetName(String aUnique_name) {
        uniquename = aUnique_name;
    }
/*
    public void AddEntryClass(String aClassName) {
        entryClassList.add(aClassName);
    }
  */
    public void AddEntry(Integer aEntryNode) {
        entrylist.add(aEntryNode);
        count = entrylist.size();
    }

    public void RemoveEntry(Integer aEntryNode) {
        for (int i = 0; i < entrylist.size(); i++) {
            if (entrylist.get(i) == aEntryNode) {
                entrylist.remove(i);
            }
        }
        count = entrylist.size();
    }
    
    public String getLastName(MasterPersonnel theMasterPersonnel, String aUniquename){
       return theMasterPersonnel.GetMasterPersonnelLastName(aUniquename);
    }
    
    @Override
    public String toString() {
        return this.getUniqueName();
    }
    

}
