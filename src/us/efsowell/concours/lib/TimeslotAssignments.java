/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import java.util.ArrayList;

/**
 *
 * @author Ed Sowell
 */
public class TimeslotAssignments {
    ArrayList<TimeslotAssignment> concoursTimeslotAssignments ; 
 
    
// Constructor
public TimeslotAssignments(){
        concoursTimeslotAssignments = new ArrayList<>();
}


public ArrayList<TimeslotAssignment> GetConcoursTimeslotAssignments(){
    return concoursTimeslotAssignments;
}

public TimeslotAssignment getTimeslotAssignment(int aID){
    TimeslotAssignment tsaTarget = null;
    for(TimeslotAssignment tsa : concoursTimeslotAssignments){
        if(tsa.getID() == aID){
            tsaTarget = tsa;
            break;
        };
    }
    return tsaTarget; // returns null if not found
}
        
    
}
