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
 * 
 * This is just to return the results of conflicts in a timeslot
 */
public class TSConflicts {
        List<Integer> lstConflicts ;   // a list of all Owners & Judges that have conflicts in the timeslot
        List<Integer> lstConflictCars ;   // a list of all cars with conflicted teams in the timeslot
 public TSConflicts() {    
    lstConflicts = new ArrayList<>();
    lstConflictCars = new ArrayList<>();
}
}