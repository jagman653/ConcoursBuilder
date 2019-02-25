/* 
 * Copyright (C) 2017 Edward F Sowell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package JCNAConcours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonExt;

/**
 *
 * @author Ed Sowell
 */

/*
 The MasterPersonTreeMap class builds a TreeMap repository using data from a parameter list of Masterpersons. It is used in support of AutoSelect comboBoxes
 with persons selected from MasterList.
*/
public class MasterPersonTreeMap {

	/**
	 * Singleton instance.  Don't know if/why this is needed...
	 */
	private static MasterPersonTreeMap instance = new MasterPersonTreeMap();

	private Map<String, MasterPersonExt> personnelMap;
	private Map<String, MasterJaguar> memberJaguarStable;  // one memberJaguarStable for each member in MasterList 
        
        /*
           Constructor for singlton instance
        */
	private MasterPersonTreeMap() {
        }        
            
        
        /*
        // Demonstration
	private MasterPersonTreeMap() {
            // Master Jaguar constructor args
           // Long aMasterjaguar_id, aMasterPerson_id, String aJcnaclass_c, String aJcnaclass_d, String aJoclacategory, Integer aYear, String aDescription, 
           // String aUnique_desc, String aColor, String aPlatevin
            //
            MasterJaguar[] MasterPersonExt ={ new MasterJaguar(99999L, 999999L, "aJcnaclass_c", "aJcnaclass_d", "aJoclacategory", 2020, "aDescription", "aUnique_desc", "aColor", "aPlatevin")};
		personnel = new TreeMap<String, MasterPersonExt>();
		MasterPersonExt user1 = new MasterPersonExt(99999L, 12345, "JOCLA", "Sowell", "Edward", "SowellEdw", "Judge", 2014, 
                            "1207 N Van Buren St", "Placentia", "CA", "USA", "92870", 
                    "", "714-993-2588", "714-403-8818", "ed_c1@efsowell.us", MasterPersonExt);
		MasterPersonExt user2 = new MasterPersonExt(99998L, 12346, "JOCLA", "Joe", "Blow", "BlowJoe", "Judge", 2014, 
                            "102 First St", "Los Angeles", "CA", "USA", "90045", 
                    "", "213-993-2588", "323-403-8818", "jb@yahoo.com", MasterPersonExt);
		MasterPersonExt user3 = new MasterPersonExt(99997L, 34567, "SDJC", "Bill", "Gates", "GatesBil", "", 1999, 
                            "102 First St", "Los Angeles", "CA", "USA", "90045", 
                    "", "213-993-2588", "323-403-8818", "jb@yahoo.com", MasterPersonExt);

		// for the sake of this example, assume that the uniqueName 
		// is unique
		personnel.put(user1.getUniqueName(), user1);
		personnel.put(user2.getUniqueName(), user2);
		personnel.put(user3.getUniqueName(), user3);
                }
   */
        
        /*
            Normal Conctructor
        */
        public MasterPersonTreeMap(List<MasterPersonExt> masterPersonnel) {
       
            MasterPersonExt miMemberExt;

            personnelMap = new TreeMap<String, MasterPersonExt>();
            for (MasterPersonExt mp : masterPersonnel) {
                personnelMap.put(mp.getUniqueName(), mp);
            }
            
                
	}
	

	/**
	 * Gets the user repository.
	 * 
	 * @return The user repository.
	 */
	public static MasterPersonTreeMap getInstance() {
		return instance;
	}

	/**
	 * Gets the information on the user with the specified UniqueName.
	 * 
	 * @param uniqueName
	 *            First name.
	 * @return Matching user info.
	 */
	public MasterPersonExt getMemberInfo(String uniqueName) {
		return personnelMap.get(uniqueName);
	}

	/**
	 * Gets all users.
	 * 
	 * @return All MasterPersons as array.
	 */
	public MasterPersonExt[] getAllMembers() {
		return personnelMap.values().toArray(new MasterPersonExt[] {});
	}
    
    public static void main(String args[]) {
        
            MasterJaguar[] masterJagArray ={ new MasterJaguar(99998L, 999998L, "aJcnaclass1_c", "aJcnaclass1_d", "aJoclacategory1", 2020, "aModel", "aDescription1", "aUnique_desc1", "aColor1", "aPlatevin1"),
                                             new MasterJaguar(99999L, 999999L, "aJcnaclass2_c", "aJcnaclass2_d", "aJoclacategory1", 2020, "aModel", "aDescription2", "aUnique_desc2", "aColor2", "aPlatevin2")};
            //personnel = new TreeMap<String, MasterPersonExt>();
            MasterPersonExt user1 = new MasterPersonExt(99999L, 12345, "JOCLA", "Sowell", "Edward", "", "SowellEdw", "Judge", 2014, 
                        "1207 N Van Buren St", "Placentia", "CA", "USA", "92870", 
                "", "714-993-2588", "714-403-8818", "ed_c1@efsowell.us", masterJagArray);
            MasterPersonExt user2 = new MasterPersonExt(99998L, 12346, "JOCLA", "Joe", "Blow", "", "BlowJoe", "Judge", 2014, 
                        "102 First St", "Los Angeles", "CA", "USA", "90045", 
                "", "213-993-2588", "323-403-8818", "jb@yahoo.com", masterJagArray);
            MasterPersonExt user3 = new MasterPersonExt(99997L, 34567, "SDJC", "Bill", "Gates", "", "GatesBil", "", 1999, 
                        "102 First St", "Los Angeles", "CA", "USA", "90045", 
                "", "213-993-2588", "323-403-8818", "jb@yahoo.com", masterJagArray);


            List<MasterPersonExt> masterPersonnelList = new ArrayList<>();
            masterPersonnelList.add(user1);
            masterPersonnelList.add(user2);
            masterPersonnelList.add(user3);
                
            MasterPersonTreeMap mpTreeMap = new MasterPersonTreeMap(masterPersonnelList);
            
    }


}
