/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
        See D:\DocumentsD\CBSchedulingInterfaceCpp for C++ version of this. Removed from the NetBeans project 2/12/2019
*/
package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.SET;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.ini4j.Wini;

/**
 * Routines to  Matching and Scheduling program using Java
 * replacing C++ implementation by Mahantesh Halappanavar
 *
 * @author Ed Sowell
 */

        /* The SchedulingInterfaceJava Java Class performs the actual task of building a Concours Judging Schedule.
         * The first step in is matching judges to Concours entries, followed by assigning each judging event to a timeslot.
         *
         * For the matching step, each entry is characterized by a Class, with each Class being in either the Driven Division or the Championship Division.
         * The follow JCNA rules must be followed:
         *  (a) there must 2 judges for Driven Classes and 3 for Championship Classes.
         *  (b) all entries in each Class mmust have the same judging team. 
         *
         * In addition to these rules there are some obvious realities:
         *  (d) a judge cannot be repeated in a team
         *  (e) Judges are not necessarily candidates for every Class, due either to Judge preferences or lack of expertise as determined by the Chief Judge.
         *  (f) judging loads should be shared fairly by the available judges
         * 
         * Rules (a) through (e) can be enforced by straightforward logic using user input data. Briefly, a comprehensive list of "judging slots" 
         * is constructed with 2 slots for each Driven Class and 3 for each Championship Class. Then construct a list of all Concours Judges, each 
         * with a list of Classes not disallowed under rule (e). Then, for each Class assign every allowed
         * Judge (i.e., not not excluded under rule (e)) to ONLY ONE of the judging slots for that Class. Note that the "ONLY ONE" part
         * enforces rule (d). Once these steps have been carried out it's easy to construct a biparttite graph with the comprehensive list of judging slots
         * forming the Left verticies, the Concours Judges forming the Right side, and judge to slot assignments become the edges. 
         *
         * After constructing the bipartite graph,  a "semi-matching" is done. In the semi-matching every judging slot vertex is matched to exactly one Judge
         * vertex, but Judging vertices can be matched to more than one judging slot vertex.  Note that while rules (a)-(e) allow deterministic enforcement
         * the semi-matching process is not. That is, many different semi-matching are possible and the one found will depend largely on the order in which the 
         * verticies are processed. However, the algorithm used here (based on pseudocode for algorithm ASM2 in Harvey, N. J. A et al "Semi-Matching for Bipartite Graphs and Load Balancing, Journal 
         * Journal of Algorithms  archive  Volume 59 Issue 1, April 2006  Pages 53-78) addresses rule (f). That is, it finds a semi-matching that
         * balances the load among the judges.
        */

public class SchedulingInterfaceJava {
        // Returns a list of Concours Classes that do not have the requisite number of classes
        private ArrayList<String> precheckJudgesForClasses(Concours aConcours){
            ArrayList<String> result = new ArrayList<>();
            for(ConcoursClass cc : aConcours.GetConcoursClasses()){
                int judgeCandidateCount = 0;
                String className = cc.GetClassName();
                ArrayList<Integer> judgeIndicies = cc.GetClassPreassignedJudgeIndiciesList();
                judgeCandidateCount = judgeIndicies.size();
                if(judgeCandidateCount == 0){
                    // No preassigned judging team so look to class reject lists to count number of candidates
                    ArrayList<Judge> judgeList = aConcours.GetConcoursJudges();
                    for(Judge j : judgeList){
                        ArrayList<String> rejectList = j.GetRejectClasses();
                        ArrayList<String> selfjudgelist = j.GetSelfEntryClasses(); // added 9/28/2017
                        if(!rejectList.contains(className) && !selfjudgelist.contains(className)) {
                            judgeCandidateCount++;
                        }
                    }
                }
                int requiredNumberJudgingSlots = ("D".equals(cc.GetClassName().substring(0, 1))) ? 2:3 ; // 2 slots for driven, 3 if Championship
                if(judgeCandidateCount < requiredNumberJudgingSlots){
                    String msg = "Class " + className + " has only " + judgeCandidateCount 
                            + " judging candidates.\n Must be " + requiredNumberJudgingSlots
                            + " or more.\n\n Either change Judge Class preferences or assign Custom Judging Team." ;
                    okDialog(msg);
                    aConcours.GetLogger().info(msg);
                    result.add(className);
                }
            }
            return result;
        }

        protected static final class ConcoursJudgingSlots {
            String result = "";

            ListOfSlotLists listOfSlotLists;
            Integer[] weights;
            private static final class JudgingSlot {
                ArrayList<Integer> judgecandidates;
                int load; // each slot ia associated with a Concours Class. The load is the number of Concours Entries in that Class.
                int slotindex;
                int concoursClass;
                // constructor
                public JudgingSlot(int aLoad, int aSlotindex, int aConcoursClass){
                   judgecandidates = new ArrayList<Integer>();
                   load = aLoad;
                   slotindex = aSlotindex;
                   concoursClass = aConcoursClass;
                }

                protected void addJudgecandidate(int candidate){
                    judgecandidates.add(candidate);
                }
                
               /* protected void setLoad(int aLoad){
                    load = aLoad;
                }
                */

                protected int getJudgeCandidate(int candidate){
                    return judgecandidates.get(candidate);
                }

                protected  ArrayList<Integer> getJudgeCandidates(){
                    return judgecandidates;
                }

                protected  int getLoad(){
                    return load;
                }
                protected  int getSlotindex(){
                    return slotindex;
                }

                protected  int getConcoursClassNode(){
                    return concoursClass;
                }
                
                protected  void setSlotindex(int aSlotindex){
                    slotindex = aSlotindex;
                }
                
                protected void displaySlot(){
                    System.out.println(judgecandidates);
                }

            }
            
            public static final class SlotList{
                // slots are created for one Class
                ArrayList<JudgingSlot> theSlotList;
                int load; // this becomes the load on every JudgingSlot in this slotlist
                int concoursClass;
                // constructor
                protected SlotList(int aLoad, int aConcoursClass){
                   theSlotList = new ArrayList<>();
                   load = aLoad;
                   concoursClass = aConcoursClass;
                }
                protected void addJudgingSlot(int aSlotindex){
                    theSlotList.add(new JudgingSlot(load, aSlotindex, concoursClass));
                }
                protected JudgingSlot getJudgingSlot(int k){
                    return theSlotList.get(k);
                }
                
                protected int getConcoursClassNode(){
                    return concoursClass;
                }
                
                protected void displaySlotList(){
                    int i = 0;
                    for(JudgingSlot js : theSlotList){
                        System.out.print("Slot " + i + " load = " + load + " Judge indices: ");
                        js.displaySlot();
                        i++;
                    }
                }
                
                
            }
            
            private static final class ListOfSlotLists{
                ArrayList<SlotList> thelists;
                ArrayList<Integer> numEntries;
                // constructor
                public ListOfSlotLists(){
                   thelists = new ArrayList<>();
                }
                protected void addSlotList(SlotList sl){
                   thelists.add(sl);
                }
                private void displayListOfSlotLists(){
                    int i = 0;
                    for(SlotList sl : thelists){
                        System.out.println("Judging slots for Concours Class: " + i );
                        sl.displaySlotList();
                        i++;
                    }
                }
               public String checkListOfSlotLists(){ 
                   // Implemented 7/13 to 7/14/2017 to be sure all required judging solots for each class are connected to a Judge.
                   // This is to address reported crashes (by Pete Rieth) with heavy usage of Judge Preferences and subsequent Custom Judging teams.
                   //
                   String res = "";
                   int classIndex = 0;
                   // Each SlotList is a Concours Class
                   for(SlotList sl : thelists){
                      int i = 0;
                      for(JudgingSlot js : sl.theSlotList){
                        if(js.getJudgeCandidates().isEmpty()){
                            String msg ="ERROR: Slot: " + i + " of Class: " + classIndex + " is empty";
                            //okDialog(msg);
                            res = msg;
                            break;
                        }
                        i++;
                       }
                       if(!res.equals("")){
                          break;
                       }
                            classIndex++;
                   }
                   return res;
               }
               
                public ArrayList<SlotList> getListOfJudgingLists(){
                    return thelists;
                }
                
            }
            // adds the candidate & increments position so as to allow cycling through the judging slots for the Class
            public int addJudgingCandidate(SlotList slotList, int numJudgingSlots, int aJudgingSlotPosition, Integer judgecandidate){
                int pos  = aJudgingSlotPosition;
                slotList.getJudgingSlot(pos).addJudgecandidate(judgecandidate) ; 
                pos++;
                pos = pos % numJudgingSlots; 
                return pos;
            } 

            protected static final class Edge implements Comparable<Edge> {
                private int v;
                private int w;

                private Edge(int v, int w) {
                        this.v = v;
                        this.w = w;
                }

                public int compareTo(Edge that) {
                    if (this.v < that.v) return -1;
                    if (this.v > that.v) return +1;
                    if (this.w < that.w) return -1;
                    if (this.w > that.w) return +1;
                    return 0;
                }
                public String toString(){
                    return "(" + v + ", " + w + ")";
                }
            }
            protected String getResultString(){
                return result;
            }
            
            
            // Constructor
            public ConcoursJudgingSlots(Concours aConcours){
                Logger theLogger = aConcours.GetLogger();
                theLogger.info("Starting ConcoursJudgingSlots constructor");
                listOfSlotLists = new ListOfSlotLists();
                int slotindex = 0;
                int theJudgeNode;
                for(ConcoursClass cc : aConcours.GetConcoursClasses()){
                    int numClassEntries = cc.GetClassEntryIndices().size();
                    int theClassNode =cc.GetClassNode();
                    SlotList slotList = new SlotList(numClassEntries, theClassNode); // numClassEntries becomes load on all JudgingSlots in this SlotList
                    int numJudgingSlots = ("D".equals(cc.GetClassName().substring(0, 1))) ? 2:3 ; // 2 slots for driven, 3 if Championship
                    for(int i = 0; i < numJudgingSlots; i++){
                        slotList.addJudgingSlot(slotindex);
                        slotindex++;
                     }
                    int judgingSlotPosition = 0;
                    ArrayList<String> preassignedJudgeList= cc.GetClassPreassignedJudgeNameList();
                    if(!preassignedJudgeList.isEmpty()){
                        theLogger.info("Class " + cc.GetClassName() + " has a preassigned JudgeList so Judging preferences will be ignored.");
                        // If ConcoursClass cc has preassigned judges they will be the only candidates.
                        for(String uniquename : preassignedJudgeList){
                            Judge cj = aConcours.GetConcoursJudge(uniquename);
                            if(cj == null){
                                result = "ERROR: No Judge with uniquename " + uniquename + " in ConcoursJudgingSlots()";
                                //okDialog(msg); will be reported in the caller
                                //theLogger.info(msg);
                                return;
                            }
                            theJudgeNode = cj.GetNode();
                            judgingSlotPosition = addJudgingCandidate(slotList, numJudgingSlots,  judgingSlotPosition, theJudgeNode);                            
                        }
                    } else {
                        // All Concours Judges are candidates unless cc is in their reject list or selfentry list
                        for(Judge cj : aConcours.GetConcoursJudges()){
                            List<String> theRejectedClasses = cj.GetRejectClasses();
                            theJudgeNode = cj.GetNode();
                            List<String> theSelfEntryClasses = cj.GetSelfEntryClasses();
                            // Add theJudgeNode as candidates for ConcoursClass cc if theJudgeNode is on the preassigned list.
                                if(!theRejectedClasses.contains(cc.GetClassName()) && !theSelfEntryClasses.contains(cc.GetClassName()))  {
                                    // add cj as a candidate Judge for cc and increment the position for the next added judge candidate
                                    judgingSlotPosition = addJudgingCandidate(slotList, numJudgingSlots,  judgingSlotPosition, theJudgeNode);
                                }
                        }
                    
                    }        
                    listOfSlotLists.addSlotList(slotList);
                }
                intNumConcoursClassJudgingSlots = slotindex;
            }
            
            public ListOfSlotLists getListOfJudgingSlots(){
                return listOfSlotLists;
            }
            public void displayConcoursJudgingSlots(){
                listOfSlotLists.displayListOfSlotLists();
                
            }
            public int getNumberOfConcoursJudgingSlots(){
                return intNumConcoursClassJudgingSlots;
            }
            
        }        
   
        
    String strNewLine = "\r\n";
    int intNumDivisions = 2; // Driven = 1 Championship = 2
    private static int intNumConcoursClassJudgingSlots = 0;
    Concours theConcours;
   // String strConcorusPath = "C:\\Users\\Ed Sowell\\Documents\\JOCBusiness\\Concours";
    LoadSQLiteConcoursDatabase loadSQLiteconcoursdatabase;
    /*
    Node mapping is necessary because the Matching code expects consecutive node numbering, while the indicies in Concours can have gaps due to removals.  
    */
       // Map ConcoursClassToMatchingJudgingSlots; // This is a 1-to-2 or 1-to-3 mapping. E.g., a single (Concourse-based) Driven ConcoursClass index maps 
                                                 // to a list of 2  (matching-based) JudgingSlot
      //  Map MatchingJudgingSlotsToConcoursClass;// This is a 2-to-1 or 3-to-1 mapping. E.g., for a Driven ConcoursClass there are 2 (matching-based) JudgingSlot
                                                 // verticies that map to a single (Concourse-based) ConcoursClass index.
        

      //  Map ConcoursEntryToMatching; 
        Map MatchingToConcoursEntry; // MatchingToConcoursEntry.get(k) gets kth Concours Entry node; k=0 is the first in  Entries SORTED by Entry node
        
        Map ConcoursJudgeToMatching; // ConcoursJudgeToMatching.get(cj_node) gets the judge vertex in the matching graph corrosponding to Judge/ConcoursPersonnel node.
        Map MatchingToConcoursJudge; // MatchingToConcoursJudge.get(judgeVertex) gets the Judge/ConcoursPersonnel node corrosponding to the judgeVertex in the matching graph 

        Map ConcoursPersonnelToColoring; 
        Map ColoringToConcoursPersonnel; 
        Map ColoringToConcoursEntry;
        
        Map<String, ArrayList<Integer>> concoursClassToJudgingSlot;
        Map<String, ArrayList<Integer>> concoursClassToJudges;        

        
    private void InitMappings(Concours aConcours){
    //    ConcoursClassToMatchingJudgingSlots = new HashMap(); 
    //    MatchingJudgingSlotsToConcoursClass = new HashMap(); 

    //   ConcoursEntryToMatching = new HashMap(); 
        MatchingToConcoursEntry = new HashMap(); 
        
 
        ConcoursJudgeToMatching = new HashMap(); 
        MatchingToConcoursJudge = new HashMap(); 
 
        ConcoursPersonnelToColoring = new HashMap(); 
        ColoringToConcoursPersonnel = new HashMap(); 
        ColoringToConcoursEntry = new HashMap(); // Used to the COncours Entry node for an Entry vertex in entryPersonnel;
        
        concoursClassToJudgingSlot = new HashMap<>(); // Used to get judging slots given the Entry. 
        concoursClassToJudges = new HashMap<>();

        
        
        Integer k;
       /*
        k = 0; // Graph verticies are 0-based
        Integer cc_i;
        for(ConcoursClass cc : aConcours.GetConcoursClasses()){
            cc_i = cc.GetClassNode();
            ConcoursClassToMatchingJudgingSlots.put(cc_i, k);
            MatchingJudgingSlotsToConcoursClass.put(k, cc_i);
            k++;
        }
*/
        
        Integer ce_i;
        k = 0; // Graph verticies are 0-based
        for(Entry ce : aConcours.GetEntries().GetSortedConcoursEntries()){
            if(!ce.GetClassName().equals("DISP")){
                ce_i = ce.GetNode();
               // ConcoursEntryToMatching.put(ce_i, k);
                MatchingToConcoursEntry.put(k, ce_i);
                k++;
            }
        }
        
        
       

        
        /*System.out.println("ConcoursClassToMatching: ");
        System.out.print(ConcoursClassToMatching + "\n");
        System.out.println("ConcoursEntryToMatching: ");
        System.out.print(ConcoursEntryToMatching + "\n");
        System.out.println("ConcoursJudgeToMatching: ");
        System.out.print(ConcoursJudgeToMatching + "\n");
        */
    }
        //
        //  Note: These mappings are for the matching step. The judge vertex numbering is different than
        //  that used in the coloring step.
        private void setJudgeMapMatching(Concours aConcours, int aFirstJudgeVertex){
           Integer cj_node;
            int k = aFirstJudgeVertex; // Graph verticies are 0-based
        //    for(ConcoursPerson cp : aConcours.GetConcoursPersonnelObject().GetConcoursPersonnelList()){
            for(Judge cj : aConcours.GetConcoursJudges()){
                cj_node = cj.GetNode();
                ConcoursJudgeToMatching.put(cj_node, k);
                MatchingToConcoursJudge.put(k, cj_node);
                k++;
            }
        }

        //
        //  Note: These mappings are for the coloring step. The personnel vertex numbering is different than
        //  that used in the matching step.
        private void setConcoursPersonnelMapColoring(Concours aConcours, int aFirstPersonalVertex){
           Integer cp_node;
            int k = aFirstPersonalVertex; // Graph verticies are 0-based
            for(ConcoursPerson cp : aConcours.GetConcoursPersonnelObject().GetConcoursPersonnelList()){
                cp_node = cp.GetConcoursPersonnel_node();
                ConcoursPersonnelToColoring.put(cp_node, k);
                ColoringToConcoursPersonnel.put(k, cp_node);
                k++;
            }
        }

        private boolean success;
        
    // Conctructor
    public  SchedulingInterfaceJava(String strDBPath, Concours aConcours, String aConcoursPath,  JTextArea aTextArea, boolean aCloseReconnectDB, boolean aStandAlone) {
        success = false;
        Connection conn;
        theConcours = aConcours;
        Integer[] weights;        // the number of entries in each class
        InitMappings(aConcours);
        //String strClassName;
        //String strDivision;
       // ConcoursClass theConcoursClass ;
        //int intNumDivision = 2;
        //int intDivision = 0;
        //Integer intClassNode;
        //Integer intMatchingClassNode;
        Coloring coloring;
        //ArrayList<ArrayList<Integer>> sm;
        Graph entriesPersonnel;
        

        File flTemp = new File(strDBPath);
        String strDBDir = flTemp.getAbsoluteFile().getParentFile().getAbsolutePath();
        conn = null;
        String strConn = "";
        
        // EntryJudgesTable locked here
        
        loadSQLiteconcoursdatabase = new LoadSQLiteConcoursDatabase() ; // function access
        
        
        //int intNumClasses = theConcours.GetConcoursClasses().size();
       // int intNumConcoursClassJudgingSlots = theConcours.GetConcoursClassesObject().getNumConcoursClassJudgingSlots(); 
        int intNumPersonnel = theConcours.GetConcoursPersonnelObject().GetConcoursPersonnelList().size();
        int intNumJudgedEntries = theConcours.GetEntriesList().size() - theConcours.GetNumDisplayOnlyEntries();
        int intNumJudges = theConcours.GetConcoursJudges().size();
        int intNumConcoursClasses = theConcours.GetConcoursClasses().size();
        String res;
        //
        //  Pre-check to be sure every Concours Class has at least the required number of Judges
        //  Note that if a Class has a pre-assigned Custom Judging team the requirement is met because the Custom Judging Team dialog
        //  requires it. But is the Class does NOT have a pre-assigned Custom Judging team we have to look at the 
        // Judging Presserence data.
        //
        ArrayList<String>  understaffedClasses = precheckJudgesForClasses(aConcours);
        if(!understaffedClasses.isEmpty()){
            String msg = "Abandoning Build of Judge Assignments & Scheguling due to understaffed Concours Classes";
            okDialog(msg);
            aConcours.GetLogger().info(msg);
            //
            //          8/30/2017 Have to reconnect to the DB. Otherwise, when we try to do anything else it fails due to closed connection
            //
            if(aCloseReconnectDB){
               try {
                    Class.forName("org.sqlite.JDBC");
                    strConn = "jdbc:sqlite:" + strDBPath ;
                    conn = DriverManager.getConnection(strConn);
                    theConcours.SetConnection(conn);
                    aConcours.GetLogger().info("Re-Opened database " + strConn + " in SchedulingInterfaceJava successfully");
                } catch ( ClassNotFoundException | SQLException e ) {
                    okDialog("Exception while reconnectiong to database after finding understaffed classes");
                    aConcours.GetLogger().info( e.getClass().getName() + ": " + e.getMessage() );
                    //System.exit(0);
                    success = false;
                    return;
                }
            }
            
            return;
        } else{
            String msg = "No understaffed Concours Classes. Will continue with building the assignments & schedule";
            //okDialog(msg);
            aConcours.GetLogger().info(msg);
            
        }
        aConcours.GetLogger().info("Starting SchedulingInterfaceJava with aCloseReconnectDB=" + aCloseReconnectDB 
           + " intNumPersonnel = " + intNumPersonnel 
           + " intNumJudgedEntries = " + intNumJudgedEntries 
           + " intNumJudges = " + intNumJudges 
           + " intNumConcoursClasses = " + intNumConcoursClasses);
        ConcoursJudgingSlots theJudgingSlots = new ConcoursJudgingSlots(theConcours);
        res = theJudgingSlots.getResultString();
        if(!res.equals("")){
            res = theJudgingSlots.getResultString();
            okDialog(res);
            theConcours.GetLogger().info(res);
            success = false;
            return;
        } else{
            theConcours.GetLogger().info("ConcoursJudgingSlots() completed successfully");
        }
        aConcours.GetLogger().info("Returned from creating Concours Judging Slots");
        ConcoursJudgingSlots.ListOfSlotLists listofjudgingslots;
        listofjudgingslots = theJudgingSlots.getListOfJudgingSlots();
        //   Check to see if all Concours classes have the appropriate number of Judging slots 
        res = listofjudgingslots.checkListOfSlotLists();
        System.out.println("Concourse list of Judging Slots:");
        theJudgingSlots.displayConcoursJudgingSlots();

        if(!res.equals("")){
            okDialog(res);
            theConcours.GetLogger().info(res);
            success = false;
            return;
        } else{
            theConcours.GetLogger().info("checkListOfSlotLists() completed successfully");
        }
        System.out.println("Concourse list of Judging Slots:");
        theJudgingSlots.displayConcoursJudgingSlots();
        
        
        
        int intNumJudgingSlots = theJudgingSlots.getNumberOfConcoursJudgingSlots();
        aConcours.GetLogger().info("Number of Concours Judging Slots " + intNumJudgingSlots);
        
        Graph judgingSlotJudgeBipartiteGraph = new Graph(intNumJudgingSlots + intNumJudges);
        aConcours.GetLogger().info("Finished constructing judgingSlotJudgeBipartiteGraph");
        weights = new Integer[intNumJudgingSlots];

        // Set up Maps ConcoursJudgeToMatching and MatchingToConcoursJudge
        aConcours.GetLogger().info("Start setJudgeMapMatching ");
        setJudgeMapMatching(theConcours, intNumJudgingSlots); 
        
        // Build judgingSlotJudgeBipartiteGraph. Has Judging slots on the left side and candidate Judges on the other.
        // This Graph is the input to BipartiteMatchingHLLT()
        // Also set up Map concoursClassToJudgingSlot 
        aConcours.GetLogger().info("Build judging Slot Judge Bipartite Graph");

        int intNumEdges = 0;
        // No need to add the same edge twice here
        SET<ConcoursJudgingSlots.Edge> set = new SET<>();
        for(int k = 0; k < intNumConcoursClasses; k++){
            Integer cc_node = theConcours.GetConcoursClasses().get(k).GetClassNode();
            String cc_name = theConcours.GetConcoursClasses().get(k).GetClassName();
            ConcoursJudgingSlots.SlotList sl = listofjudgingslots.getListOfJudgingLists().get(k);
            ArrayList<Integer> slotIndexList = new ArrayList<>();
            for(ConcoursJudgingSlots.JudgingSlot js : sl.theSlotList){
                ArrayList<Integer> jCandidates = js.getJudgeCandidates();
                Integer slotindex = js.getSlotindex();
                slotIndexList.add(slotindex);
                for(int jc : jCandidates){
                    Integer intMatchingJudgeNode = (Integer) ConcoursJudgeToMatching.get(jc);
                    ConcoursJudgingSlots.Edge e = new ConcoursJudgingSlots.Edge(slotindex, jc);
                    if (!set.contains(e)) {
                        set.add(e);
                        judgingSlotJudgeBipartiteGraph.addEdge(slotindex, intMatchingJudgeNode);
                        //System.out.println("Added edge #: " + intNumEdges + e);
                        intNumEdges++;
                    } else{
                        aConcours.GetLogger().info("Duplicate edge skipped: " + e);
                    }
                    if(slotindex > intNumJudgingSlots-1){
                        String msg = "ERROR: Index out of bounds while building bipartite graph: slotindex = " + slotindex + " intNumJudgingSlots = " + intNumJudgingSlots;
                        okDialog(msg);
                        aConcours.GetLogger().info(msg);
                        success = false;
                        return;
                    }
                    weights[slotindex] = js.getLoad();
                }
            }
            concoursClassToJudgingSlot.put(cc_name, slotIndexList); 

        }
        aConcours.GetLogger().info("Finished building judging Slot/Judge Bipartite Graph");
        boolean[] matchingLeftVertex = null;
        
        BipartiteMatchingHLLT maximumMatching = new BipartiteMatchingHLLT(judgingSlotJudgeBipartiteGraph);
        /*System.out.println("The bipartite Graph:");
        System.out.print(judgingSlotJudgeBipartiteGraph);
        */
        aConcours.GetLogger().info("Finished constructing BipartiteMatchingHLLT");

        
        matchingLeftVertex = new boolean[judgingSlotJudgeBipartiteGraph.V()]; // matchingLeftVertex[k] == true means k is a left vertex
        for(int k = 0; k < intNumJudgingSlots; k++){
            matchingLeftVertex[k] = true;
        }
        
        aConcours.GetLogger().info("Start findOptimalSemiMatching()");
        int result = maximumMatching.findOptimalSemiMatching(judgingSlotJudgeBipartiteGraph, matchingLeftVertex, weights);
        aConcours.GetLogger().info("Finished findOptimalSemiMatching() with result = " + result);
        
        if (result == 0) {
           // System.out.println("semi-matching failed. Probably don't have enough Judges to staff some Concours Class." );
           String msg = "Matching Judges with Classes failed. Probably too few Judges to staff some Concours Class.";
            JOptionPane.showMessageDialog(null, msg);
            aConcours.GetLogger().info(msg);
           
            //
            //          1/9/2017 Have to reconnect to the DB. Otherwise, when we try to Add more Judges it fails due to closed connection
            //
            if(aCloseReconnectDB){
               try {
                    Class.forName("org.sqlite.JDBC");
                    strConn = "jdbc:sqlite:" + strDBPath ;
                    conn = DriverManager.getConnection(strConn);
                    theConcours.SetConnection(conn);
                    aConcours.GetLogger().info("Re-Opened database " + strConn + " in SchedulingInterfaceJava successfully");
                } catch ( ClassNotFoundException | SQLException e ) {
                    okDialog("Exception while reconnectiong to database after bipartite matching");
                    aConcours.GetLogger().info( e.getClass().getName() + ": " + e.getMessage() );
                    //System.exit(0);
                    success = false;
                    return;
                }
            }
            success = false;
            return;    
        } else {
            aConcours.GetLogger().info("Found a semi-matching:");
            aConcours.GetLogger().info("Semi-matching Left cardinality: " + maximumMatching.getLeftCardinality(judgingSlotJudgeBipartiteGraph, matchingLeftVertex));
            //System.out.println("semi-matching Left cardinality: " + maximumMatching.getLeftCardinality(judgingSlotJudgeBipartiteGraph, matchingLeftVertex));
            aConcours.GetLogger().info("Semi-matching Right cardinality: " + maximumMatching.getRightCardinality(judgingSlotJudgeBipartiteGraph, matchingLeftVertex));
            //System.out.println("semi-matching Right cardinality: " + maximumMatching.getRightCardinality(judgingSlotJudgeBipartiteGraph, matchingLeftVertex));
            //System.out.println("Judge loads:");
            //maximumMatching.displayJudgeLoads(judgingSlotJudgeBipartiteGraph.V(), matchingLeftVertex, weights);

            //
            // Now we need to do the "coloring" to put Entries into time slots. But first, we need a Map from ConcousrClass Name to 
            // Judge (Personnel) vertex. Then we build a Graph in which with Entries are represented by Left vertices and
            // Concours Personnel (Judges & Owners) are Right verticies. 
            //
            aConcours.GetLogger().info("Build graph for Coloring");
            ArrayList<ArrayList<Integer>> sm = maximumMatching.getTheSemimatching(); // sm verticies are relative to the BipartiteMatchingHLLT  Graph. 
                                                                                     // 
            for(ConcoursClass cc : theConcours.GetConcoursClasses()){
                String cc_name = cc.GetClassName();
                ArrayList<Integer> slotIndexListForThisCConcoursClass = (ArrayList<Integer>) concoursClassToJudgingSlot.get(cc_name);
                //System.out.println("cc_name:" + cc_name + " matchedSlotIndex:" + slotIndexListForThisCConcoursClass);
                concoursClassToJudges.put(cc_name, slotIndexListForThisCConcoursClass);
            }
            // Build the Graph with Entries  represented by Left vertices
            // and Concours Personnel (Judges & Owners) are Right verticies. 
            // Each Entry has either 2 (Driven) or 3(Championship) Judges and 1 owner.
            entriesPersonnel = new Graph(intNumJudgedEntries + intNumPersonnel);
            setConcoursPersonnelMapColoring(theConcours,  intNumJudgedEntries);

            int k = 0; // entry index
            for(Entry ce : aConcours.GetEntries().GetSortedConcoursEntries()){
                String eClassName = ce.GetClassName();
                if(!eClassName.equals("DISP")){
                    Integer entryNode = ce.GetNode();
                    ColoringToConcoursEntry.put(k, entryNode); 
                    ArrayList<Integer> slotIndexListForThisCConcoursClass = concoursClassToJudges.get(eClassName);
                    for(Integer sli : slotIndexListForThisCConcoursClass){
                        Integer judgeMatchingVertex = sm.get(sli).get(0); // only one Judge vertex is matched with JudgingSlot verticies
                        // Note that the vertex numbering for the Matching step are not the same as for the coloring step!!
                        Integer judgePersonnelNode = (Integer)MatchingToConcoursJudge.get(judgeMatchingVertex);
                        Integer judgeColoringVertex = (Integer) ConcoursPersonnelToColoring.get(judgePersonnelNode);
                        //System.out.println(" Slotlist index: " + sli + " Adding edge (" + k + ", " + judgeColoringVertex + ") to coloring graph" );
                        entriesPersonnel.addEdge(k, judgeColoringVertex);
                    }
                    // Add edge for Owner
                    String ownerUniqueName = ce.GetOwnerUnique();
                    Integer ownerPersonnelNode = theConcours.GetConcoursPersonnelObject().GetConcoursPerson(ownerUniqueName).GetConcoursPersonnel_node();
                    Integer ownerColoringVertex = (Integer) ConcoursPersonnelToColoring.get(ownerPersonnelNode);
                    entriesPersonnel.addEdge(k, ownerColoringVertex); // no mapping used so this is the index in the SORTED Concours Entries list  it is NOT an entry Node number
                    k++;
                }
            }


            aConcours.GetLogger().info("Finished building coloring Graph:");
            //System.out.print(entriesPersonnel);
            //     TESTING
            // Wtite to file for use while testing  Coloring standalone 
            //
            /*BipartiteGraphWriterAdjLists graphWriterAdjLists = new BipartiteGraphWriterAdjLists();
            String strOutFilePath = strDBPath.replaceFirst("db", "txt");
            System.out.println("Writing entriesPersonnel to: " + strOutFilePath);
            Out outfile = new Out(strOutFilePath);
            graphWriterAdjLists.writeGraph(entriesPersonnel, intNumEntries, outfile);
            */
            //  end TESTING
            coloring = new Coloring(entriesPersonnel, intNumJudgedEntries, intNumPersonnel);
            
            aConcours.GetLogger().info("Finished coloring.");
            // Write matching/coloring into JudgeAssignments database
            // NOTE: A db file locked error occurs if the db isn't closed & reopened... don't know why
            //      It gets closed before SchedulingInterfaceJava() is created
            //
            if(aCloseReconnectDB){
               try {
                    Class.forName("org.sqlite.JDBC");
                    strConn = "jdbc:sqlite:" + strDBPath ;
                    conn = DriverManager.getConnection(strConn);
                    theConcours.SetConnection(conn);
                    aConcours.GetLogger().info("Re-Opened database " + strConn + " in SchedulingInterfaceJava successfully");
                } catch ( ClassNotFoundException | SQLException e ) {
                    aConcours.GetLogger().info( e.getClass().getName() + ": " + e.getMessage() );
                    aConcours.GetLogger().info("Failed to reopen database " + strConn + " after coloring");
                    //System.exit(0);
                    success = false;
                    return;
                }
                try {
                    String res1 = JudgeAssignmentsMatchingColoringToDB(conn, entriesPersonnel, coloring, intNumJudgedEntries);
                    if(!res1.equals("")){
                        aConcours.GetLogger().info(res1);
                        success = false;
                        return;
                    } else{
                        aConcours.GetLogger().info("Finished writing JudgeAssignments to database");
                    }
                } catch (SQLException ex) {
                  String msg = "SQLException while writing JudgeAssignments to database";
                  okDialog(msg);
                    aConcours.GetLogger().log(Level.SEVERE, msg, ex);
                    success = false;
                    return;
              }
            } else{
                try {
                    conn = theConcours.GetConnection();
                    String res1 = JudgeAssignmentsMatchingColoringToDB(conn, entriesPersonnel, coloring, intNumJudgedEntries);
                    if(!res1.equals("")){
                        aConcours.GetLogger().info("Finished writing JudgeAssignments to database");
                    } else {
                      okDialog(res1);
                      aConcours.GetLogger().info(res1);
                      success = false;
                      return;
                    }
                } catch (SQLException ex) {
                    String msg = "SQLException while writing JudgeAssignments to database";
                    okDialog(msg);
                    aConcours.GetLogger().log(Level.SEVERE, msg, ex);
                    success = false;
                    return;
                }
                
            }

        
            //
            //  Now after clearing theJudgeAssignemnts in memory (NOT the DB), load the results of the new matching into memory
            //
            theConcours.GetLogger().info("Read Judge Assigments from DB and write to memory data structures.");
            theConcours.theJudgeAssignments.ClearJudgeAssignments();
            loadSQLiteconcoursdatabase.ReadJudgeAssignmentDBToMem(conn, theConcours);
            // ... and  update judge assignements in Concours Classes
            theConcours.GetLogger().info("Update Concourse Classes JudgeLists in memory");

            theConcours.UpdateConcourseClassesJudgeLists(); 
            aConcours.GetLogger().info("Finished updating JudgeAssignments and Class judge lists to memory");
             success = true;
            /*
            try {
                conn.close();
                System.out.println("Closed database " + strConn + " in SchedulingInterface successfully");
            } catch (SQLException ex) {
                Logger.getLogger(SchedulingInterfaceJava.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
        }
        }
    
    
    
    public String  JudgeAssignmentsMatchingColoringToDB(Connection aConn,  Graph entriesPersonnel, Coloring coloring, int numJudgedEntries) throws SQLException{
        String q;
        theConcours.GetLogger().info("Write JudgeAssignmentsto DB");
        aConn.setAutoCommit(false);
        Statement stat_ej = aConn.createStatement(); // Entry judge lists
        stat_ej.executeUpdate("drop table if exists EntryJudgesTable;");
        aConn.commit();
        q = "create table EntryJudgesTable ('entryjudges_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'judgeassignment_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL,  FOREIGN KEY (judgeassignment_id) REFERENCES JudgeAssignmentsTable (judgeassignment_id));";
        stat_ej.executeUpdate(q);
        aConn.commit();
        stat_ej.close();

        Statement stat_ja = aConn.createStatement(); 
        stat_ja.executeUpdate("drop table if exists JudgeAssignmentsTable;"); 
        q = "create table JudgeAssignmentsTable ('judgeassignment_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'car_node' INTEGER NOT NULL, 'owner_node' INTEGER NOT NULL, 'timeslot' INTEGER);";
        stat_ja.executeUpdate(q);
        stat_ja.close();
        
        
        Statement stat_lrid  = aConn.createStatement()    ;
        ResultSet rs_lrid;
        Integer intCarIndexConcours;
        Integer intOwnerIndexConcours;
        Integer intJudgeIndexConcours;
        Long lngJudgeassignment_id;

        PreparedStatement prep_ja = aConn.prepareStatement( "insert into JudgeAssignmentsTable ('car_node', 'owner_node', 'timeslot') values (?, ?,  ?);"); 
        PreparedStatement prep_ej = aConn.prepareStatement( "insert into EntryJudgesTable ('judgeassignment_id', judge_node) values (?, ?);"); 

        Integer[] theColors  = coloring.theDistanceTwoVertexColoring.vertexColors; 

        for(int k = 0; k < entriesPersonnel.V(); k++){
            if(k < numJudgedEntries){
                int intTimeslot = theColors[k];
                intCarIndexConcours = (Integer) ColoringToConcoursEntry.get(k); 
                int adjSize = 0;
                for(int person : entriesPersonnel.adj(k)) adjSize++; // Graph adj has no easier way to get size??
                int[] team = new int[adjSize]; // team[0] == Owner. Rest are Judges for the entry
                int i = 0;
                for(int person : entriesPersonnel.adj(k)){
                    team[i] = (Integer) ColoringToConcoursPersonnel.get(person);
                    i++;
                }
                // Insert into database table  in terms of the CONCOURS indicies
                // first integer in adj() is Owner, the rest are Judges
                intOwnerIndexConcours = team[0];
                prep_ja.setInt(1, intCarIndexConcours);
                prep_ja.setInt(2, intOwnerIndexConcours);
                prep_ja.setInt(3, intTimeslot);
                prep_ja.executeUpdate();
                aConn.commit();
                // write Judges to EntryJudgesTable
                ////                  NOTE: SELECT last_insert_rowid() works here... some places it doesn't                                     
                stat_lrid = aConn.createStatement();
                rs_lrid = stat_lrid.executeQuery("SELECT last_insert_rowid()");
                lngJudgeassignment_id = 0L; // won't be used
                if (rs_lrid.next()) {
                    lngJudgeassignment_id = rs_lrid.getLong(1);
                }
                else{
                    String msg = "ERROR: Failed to find last row id of inserts into JudgeAssignmentsTable";
                    okDialog(msg);
                    theConcours.GetLogger().info(msg);
                    //System.exit(-1);
                    return msg;
                }
                rs_lrid.close();
                stat_lrid.close();
                //System.out.println("JudgeAssignmentsTable: insert into EntryJudgesTable ('judgeassignment_id', judge_node) values: lngJudgeassignment_id= " + lngJudgeassignment_id + " intJudgeIndexConcours= " + intJudgeIndexConcours);
                //for(int person : entriesPersonnel.adj(k)){
                for(Integer kp = 1; kp<adjSize; kp++){
                   // intJudgeIndexConcours = (Integer) ColoringToConcoursPersonnel.get(team[kp]);
                    intJudgeIndexConcours = team[kp];
                    prep_ej.setLong(1, lngJudgeassignment_id); 
                    prep_ej.setInt(2, intJudgeIndexConcours); 
                    prep_ej.executeUpdate();
                    aConn.commit();
                }
            }
        }
        aConn.setAutoCommit(true);
        return "";
    }
        
  //
  // Reads from the JudgeAssign.txt file and writes into the Concours database JudgeAssignmentsTable.
  // Used in SchedulingInterfaceCpp immediately after JudgeAssign.txt is created by Matching process
  // Also used in JudgeAssignmentGUI after manual changes
  //
  //        NOT USED IN SchedulingInterfaceJava
    //
  public  void JudgeAssignmentsTxtToDB(Connection aConn,  String strPath) throws SQLException{
    //LoadSQLiteConcoursDatabase.SchedulingInterfaceCpp.InitMappings(Concours aConcours);      
    Statement stat_ja = aConn.createStatement(); 
    stat_ja.executeUpdate("drop table if exists JudgeAssignmentsTable;"); 
    String q = "create table JudgeAssignmentsTable ('judgeassignment_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'car_node' INTEGER NOT NULL, 'owner_node' INTEGER NOT NULL, 'timeslot' INTEGER);";
    stat_ja.executeUpdate(q);
    stat_ja.close();

    Statement stat_ej = aConn.createStatement(); // Entry judge lists
    stat_ej.executeUpdate("drop table if exists EntryJudgesTable;"); 
    q = "create table EntryJudgesTable ('entryjudges_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'judgeassignment_id' INTEGER NOT NULL, 'judge_node' INTEGER NOT NULL,  FOREIGN KEY (judgeassignment_id) REFERENCES JudgeAssignmentsTable (judgeassignment_id));";
    stat_ej.executeUpdate(q);
    stat_ej.close();
    Statement stat_lrid  = aConn.createStatement()    ;
    ResultSet rs_lrid;
    
    int iLine;
    String sCurrentLine;
    
    //int theOwnerIndex;
    String strTheCarIndex;
    Integer intCarIndex;
    Integer intCarIndexConcours;
    Integer intOwnerIndexConcours;
    int intTimeslot;
   // ArrayList<Integer> lstTheJudgeIndices;
    String strOwnerIndex;
    int intOwnerIndex;
    String[] theJudgeIndexAry ;
    String strTimeslot;
    String strJudgeIndex;
    Integer intJudgeIndex;
    Integer intJudgeIndexConcours;
    
    Long lngJudgeassignment_id;
    int intCount ;
    //Entry entry;
    
    
    
    String JudgeAssignTxt = strPath ;
    String strReport = "";

    PreparedStatement prep_ja = aConn.prepareStatement( "insert into JudgeAssignmentsTable ('car_node', 'owner_node', 'timeslot') values (?, ?,  ?);"); 
    PreparedStatement prep_ej = aConn.prepareStatement( "insert into EntryJudgesTable ('judgeassignment_id', judge_node) values (?, ?);"); 
    
    BufferedReader br = null;
      try {
          br = new BufferedReader(new FileReader(JudgeAssignTxt));
      } catch (FileNotFoundException ex) {
          Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
      }

    iLine = 0;
      try {
          aConn.setAutoCommit(false);
          while ((sCurrentLine = br.readLine()) != null) {
              //System.out.println(sCurrentLine);
              if (iLine > 0){ // skip header line in JudgeAssign.txt
                  //lstTheJudgeIndices = new ArrayList<>();
                  int p1;
                  int p2;
                  int p3;
                  int p4;
                  //int  intLen;
                  
                  //intLen = sCurrentLine.length();
                  p1 = sCurrentLine.indexOf("{");
                  p2 = sCurrentLine.indexOf("}");
                  if( (p1 == -1) || (p2 == -1) ){
                      strReport = strReport + " Bad format in JudgeAssign.txt file line: " + sCurrentLine;
                  }
                  else{
                      strTheCarIndex = sCurrentLine.substring(0, p1);
                      intCarIndex = Integer.parseInt(strTheCarIndex.trim()); //does not include the char at p1
                      intCarIndexConcours = (Integer) MatchingToConcoursEntry.get(intCarIndex);
                      strTimeslot = sCurrentLine.substring(p2+1);
                      intTimeslot = Integer.parseInt(strTimeslot.trim());
                      //entry = JudgeAssignGUI.theConcours.theEntries.getEntry(theCarIndex);
                      //entry.SetTimeslotIndex(theTimeslot);
                      // Now  get OwnerIndex & 2 or 3 judges
                      p3 = sCurrentLine.indexOf("(");
                      p4 = sCurrentLine.indexOf(")");
                      if( (p3 == -1) || (p4 == -1) ){
                          strReport = strReport + " Bad format in JudgeAssign.txt file line: " + sCurrentLine;
                      }
                      else{
                          strOwnerIndex = sCurrentLine.substring(p3+1,p4);
                          intOwnerIndex = Integer.parseInt(strOwnerIndex.trim());
                          intOwnerIndexConcours = (Integer) MatchingToConcoursJudge.get(intOwnerIndex);
                          strJudgeIndex = sCurrentLine.substring(p4+1, p2).trim();
                          theJudgeIndexAry = strJudgeIndex.split("\\s+");
                          intCount = theJudgeIndexAry.length;

                          // Insert into database table  in terms of the CONCOURS indicies
                          prep_ja.setInt(1, intCarIndexConcours); 
                          prep_ja.setInt(2, intOwnerIndexConcours); 
                          prep_ja.setInt(3, intTimeslot); 
                          prep_ja.executeUpdate();
                          ////                  NOTE: SELECT last_insert_rowid() works here... some places it doesn't                                     
                          stat_lrid = aConn.createStatement();
                          rs_lrid = stat_lrid.executeQuery("SELECT last_insert_rowid()");
                          lngJudgeassignment_id = 0L; // won't be used
                          if (rs_lrid.next()) {
                            lngJudgeassignment_id = rs_lrid.getLong(1);
                          }
                          else{
                            String msg = "ERROR: Failed to find last row id of inserts into JudgeAssignmentsTable";
                            okDialog(msg);
                            theConcours.GetLogger().info(msg); 
                            System.exit(-1);
                          }
                          rs_lrid.close();
                          stat_lrid.close();
                          for (int i = 0; i<intCount;i++){
                              intJudgeIndex = Integer.parseInt(theJudgeIndexAry[i].trim());
                              intJudgeIndexConcours = (Integer) MatchingToConcoursJudge.get(intJudgeIndex);
                              System.out.println("JudgeAssignmentsTable: insert into EntryJudgesTable ('judgeassignment_id', judge_node) values: lngJudgeassignment_id= " + lngJudgeassignment_id + " intJudgeIndexConcours= " + intJudgeIndexConcours);
                              prep_ej.setLong(1, lngJudgeassignment_id); 
                              prep_ej.setInt(2, intJudgeIndexConcours);
                              prep_ej.executeUpdate();
                          }
                          aConn.commit();
                      }
                      
                  }
                  
              }
              iLine++;
          }
          prep_ej.close();
          prep_ja.close();
      } catch (IOException ex) {
          Logger.getLogger(LoadSQLiteConcoursDatabase.class.getName()).log(Level.SEVERE, null, ex);
      }
    
    
    
    
  }

 /*   public Map GetMatchingJudgingSlotsToConcoursClass(){
        return MatchingJudgingSlotsToConcoursClass;
    }
  */
    
    public Map GetMatchingToConcoursEntry(){
        return MatchingToConcoursEntry;
    }

    public Map GetMatchingToConcoursPersonnel(){
        return MatchingToConcoursJudge;
    }
    
    public boolean GetSuccess(){
        return success;
    }
   // public SchedulingInterfaceJava
    
    public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            Connection conn ;
            String strConn;
            String strDBName = "SDJC2015V3Copy.db"; 
            //String strDBName = "6Cars2Classes3Judges.db"; 
            String strConcoursPath = "C:\\Users\\" + System.getProperty("user.name") +  "\\Documents\\JOCBusiness\\Concours";
         //   String strConcoursPath= "C:\\Users\\jag_m_000\\Documents\\Concours" ;
            String strDBPath = strConcoursPath + "\\" + strDBName;
            conn = null;
            try {
                Class.forName("org.sqlite.JDBC");
                strConn = "jdbc:sqlite:" + strDBPath ;
                conn = DriverManager.getConnection(strConn);
                System.out.println("Opened database " + strConn + " successfully");
            } catch ( ClassNotFoundException | SQLException e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
                Logger logger = Logger.getLogger("ConcoursBuilderLog");  
                FileHandler fh;  
                try {  
                    fh = new FileHandler(strConcoursPath);  // The log file will be in the strPath
                    logger.addHandler(fh);
                    SimpleFormatter formatter = new SimpleFormatter();  
                    fh.setFormatter(formatter);  
                    logger.info("ConcoursBuilder started");  
                } catch (SecurityException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            Concours theConcours = new Concours(logger, 3);
            theConcours.SetThePath(strConcoursPath);

            theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", logger);
            theConcours.LoadMasterPersonnelDB(conn, logger);
            theConcours.LoadConcoursPersonnelDB(conn, logger);
            theConcours.LoadMasterJaguarDB(conn, logger);
            theConcours.LoadEntriesDB(conn, logger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
           // theConcours.LoadConcoursClassesDB(conn);
            theConcours.LoadJudgesDB(conn, logger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
            theConcours.LoadConcoursClassesDB(conn, theConcours, logger);
            theConcours.LoadOwnersDB(conn, logger);
            //
            //  the Connection has to be closed here and reopened in theSchedulingInterface.
            //  Otherwise, the table is locked when accessed later
            //  Don't know why....
            //
            // The answer is most likely:  http://beets.radbox.org/blog/sqlite-nightmare.html
            //
            //
           
           /* try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SchedulingInterfaceCpp.class.getName()).log(Level.SEVERE, null, ex);
            }
                   */
                   
            SchedulingInterfaceJava theSchedulingInterfaceJava;
            //theSchedulingInterface = new SchedulingInterfaceCpp(conn, strDBPath, theConcours, strConcoursPath, null, true, true); // textArea not used id stand alone
            theSchedulingInterfaceJava = new SchedulingInterfaceJava(strDBPath, theConcours, strConcoursPath, null, true, true); // textArea not used id stand alone
            
            }
        });
    }
    
} // end of Class
