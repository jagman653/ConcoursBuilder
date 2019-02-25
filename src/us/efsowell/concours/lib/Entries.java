/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;


import static JCNAConcours.AddConcoursEntryDialog.okCancelDialog;
import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Ed Sowell
 */
public class Entries {
    Logger theLogger;
    ArrayList<Entry> concoursEntries ; // All Entries in the concours. Initialized from CSV file EntriesCsv.txt as exported from the Excel Entries worksheet
    private class CustomComparator implements Comparator<Entry> {
    @Override
    public int compare(Entry o1, Entry o2) {
        return o1.GetNode().compareTo(o2.GetNode());
    }
} 

    // Constructor
    public Entries(Logger aLogger){
        concoursEntries =  new ArrayList<>();
        theLogger = aLogger;
    }
    


    public String getEntryClass(Integer aNode){
        
      int intSize = concoursEntries.size();
      Entry anEntry;
      String strClass = "";
      for(int i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
          if(anEntry.Node == aNode){
              strClass = anEntry.JCNAClass;
          }
      }
      if("".equals(strClass)){
          theLogger.info("getEntryClass: Entry with Node number " + aNode + " not found");
          System.exit(1);
      }      
        
        return strClass;
        
    }




       
    public String getEntryID(Integer aNode){
      int intSize = concoursEntries.size();
      Entry anEntry;
      String theID = "";
      for(int i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
          if(anEntry.Node == aNode){
              theID = anEntry.ID;
              break;
          }
      }
      if("".equals(theID)){
           theLogger.info("getEntryID: Entry with Node number " + aNode + " not found");
          System.exit(1);
      }
      return theID;
   }
        
    
    public Entry getEntry(Integer aNode){
      int intSize = concoursEntries.size();
      int i;
      Entry anEntry;
      Entry res = null;
      //String theID = "";
      for( i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
          if(anEntry.Node == aNode){
              res = anEntry;
              break;
          }
      }
      if(res == null){
           theLogger.info("getEntry: Entry with Node number " + aNode + " not found");
          okDialog("getEntry: Entry with Node number " + aNode + " not found");
          System.exit(1);
      }
      return res;
   }

   public String getOwnerLast(Integer aNode){
        
      int intSize = concoursEntries.size();
      Entry anEntry;
      String theOwner = "";
      for(int i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
          if(anEntry.Node == aNode){
              theOwner = anEntry.OwnerLast;
              break;
          }
      }
      if(theOwner == ""){
           theLogger.info("getOwnerLast: Entry with Node number " + aNode + " not found");
          System.exit(1);
      }
      
        
        return theOwner;
        
    }
public ArrayList<Entry> GetConcoursEntries(){
    return concoursEntries;
}
public ArrayList<Entry> GetSortedConcoursEntries(){
    ArrayList<Entry> sortedEntries = new ArrayList<Entry>(concoursEntries);
    Collections.sort(sortedEntries, new CustomComparator());
    return sortedEntries;
}
public List<String> GetConcoursEntryIDs(){
    List<String>  entryIDs = new ArrayList<>();  ;
    for(int i = 1; i< concoursEntries.size();i++) { // skip the header
        entryIDs.add(concoursEntries.get(i).GetID());
    }
   
    return entryIDs;
}
public int ConcourseEntryNode(String aUniqueDescription){
      int intSize = concoursEntries.size();
      int i;
      Entry anEntry;
      int res = 0;
      for( i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
          if(anEntry.UniqueDescription.equals(aUniqueDescription)){
              res = anEntry.GetNode();
              break;
          }
      }
      return res;
    
}

public Entry getEntry(String aUniqueDescription){
      //int intSize = concoursEntries.size();
      //int i;
      boolean found = false;
      Entry theEntry = null;
      for(Entry e : concoursEntries){
          if(e.UniqueDescription.equals(aUniqueDescription)){
              theEntry = e;
              found = true;
              break;
          }
      } 
      if(!found){
          String msg = "Entry with unique descriotion " + aUniqueDescription + " not found";
          okDialog(msg);
          theLogger.info(msg);
      }
    return theEntry; // null if not found
}
public Entries getEntriesObject(){
    return this;
}

/*
 *  Searches current Entries to find the next entry ID for the argument JCNA JCNAClass name. E.g., if 
 *  aJCNAClassName = "C04/150" and the class already has C04/150-1,  C04/150-3, C04/150-4,the returned ID will be "C04/150-5"
*/
public String NextEntryID(String aJCNAClassName){
      int intSize = concoursEntries.size();
      int i;
      Entry anEntry;
      int dashNumber;
      int maxDashNumber = 0;
      String [] parts;
      for( i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
          if(anEntry.ID.contains(aJCNAClassName)){
              if(anEntry.ID.contains("-")){
                 parts = anEntry.ID.split("-");
                 dashNumber = Integer.parseInt(parts[1]);
                 if(dashNumber > maxDashNumber) maxDashNumber = dashNumber;
              }
              else{
                okCancelDialog("Entry ID " + anEntry + " in Concours Entries is improperly formatted. It must have a '-' ");
              }
          }
          
      }
      return aJCNAClassName + "-" + Integer.toString(maxDashNumber + 1);
    
}

/*
    When the JCNA Class of an existing entry is changed the "dash numbers" may no longer be consecutive. E.g.,
    if we have 3 entries in C04/150 the entry IDs are C04/150-1, C04/150-2, C04/150-3. But if the C04/150-2 is
    changed to C18/PN it  the C04/150 entries become C04/150-1, C04/150-3. There is no huge consequence, but it might
    be a little confusing for judges if the printed schedule... "Where is C04/150-2?" So, when such a change is made in 
    ModifyConcoursEntryDialog() (and the initial class still has some entries) this function is called to reset all
    ID for the remaining entries in the class.
    
*/

public void ResetEntryIDs(String aConcoursClass){
      int intSize = concoursEntries.size();
      Entry anEntry;
      int dashNumber = 1;
      for(int i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
         // if(anEntry.ID.equals(aConcoursClass)){
          if(anEntry.ID.contains(aConcoursClass)){
              anEntry.SetEntryID(aConcoursClass + "-" + dashNumber);
              dashNumber++;
          }
      }
}

public int NextEntryNode(){
      int intSize = concoursEntries.size();
      int i;
      Entry anEntry;
      int maxNode = 0;
      int node;
      for( i = 0; i<intSize;i++){
          anEntry = concoursEntries.get(i);
          node = concoursEntries.get(i).GetNode();
          if(node  > maxNode){
              maxNode = node;
          }
      }
      return maxNode + 1;
}

public void AddConcoursEntry(Entry aEntry){
    concoursEntries.add(aEntry);
}

public void RemoveConcoursEntry(Entry aEntry){
    concoursEntries.remove(aEntry);
}
}
