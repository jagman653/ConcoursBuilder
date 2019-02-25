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
import java.util.Map;
import java.util.TreeMap;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonExt;

/**
 *
 * @author Ed Sowell
 */

/*

    NOT USED                                NOT USED                                          NOT USED
 
 The MasterListRepository2 class builds the same MasterList repository as MasterListRepository but gets the master members & Jaguars
 from the already-loaded collections MasterPersonnel and MasterJaguar

  8/27/2015 This functionality has been incorporated into MasterListRepository, controlled by a parameter

*/
public class MasterListRepository2 {

	/**
	 * Singleton instance.  Don't know if/why this is needed...
	 */
	private static MasterListRepository2 instance = new MasterListRepository2();

	private Map<String, MasterPersonExt> allPersonnel;
	private Map<String, MasterJaguar> memberJaguarStable;  // one memberJaguarStable for each member in MasterList 

	private MasterListRepository2() {
            /* Master Jaguar constructor args
            Long aMasterjaguar_id, aMasterPerson_id, String aJcnaclass_c, String aJcnaclass_d, String aJoclacategory, Integer aYear, String aDescription, 
            String aUnique_desc, String aColor, String aPlatevin
            */
            MasterJaguar[] MasterPersonExt ={ new MasterJaguar(99999L, 999999L, "aJcnaclass_c", "aJcnaclass_d", "aJoclacategory", 2020, "aModel", "aDescription", "aUnique_desc", "aColor", "aPlatevin")};
		allPersonnel = new TreeMap<String, MasterPersonExt>();
		MasterPersonExt user1 = new MasterPersonExt(99999L, 12345, "JOCLA", "Sowell", "Edward", "", "SowellEdw", "Judge", 2014, 
                            "1207 N Van Buren St", "Placentia", "CA", "USA", "92870", 
                    "", "714-993-2588", "714-403-8818", "ed_c1@efsowell.us", MasterPersonExt);
		MasterPersonExt user2 = new MasterPersonExt(99998L, 12346, "JOCLA", "Joe", "Blow", "", "BlowJoe", "Judge", 2014, 
                            "102 First St", "Los Angeles", "CA", "USA", "90045", 
                    "", "213-993-2588", "323-403-8818", "jb@yahoo.com", MasterPersonExt);
		MasterPersonExt user3 = new MasterPersonExt(99997L, 34567, "SDJC", "Bill", "Gates", "", "GatesBil", "", 1999, 
                            "102 First St", "Los Angeles", "CA", "USA", "90045", 
                    "", "213-993-2588", "323-403-8818", "jb@yahoo.com", MasterPersonExt);

		// for the sake of this example, assume that the uniqueName 
		// is unique
		allPersonnel.put(user1.getUniqueName(), user1);
		allPersonnel.put(user2.getUniqueName(), user2);
		allPersonnel.put(user3.getUniqueName(), user3);
                }
        
	// 
        // Note: it is assumed that  MasterPersonnel has already been loaded from the database
        //
        
        public MasterListRepository2(Concours aConcours) {
       
            MasterPersonExt miMemberExt;

            allPersonnel = new TreeMap<String, MasterPersonExt>();
            ArrayList<MasterPersonExt> masterPersonnel;
            ArrayList<MasterJaguar> masterJaguar;
            masterPersonnel = aConcours.GetConcoursMasterPersonnel();
            masterJaguar  = aConcours.GetConcoursMasterJaguar();
            for (MasterPersonExt masterPersonnel1 : masterPersonnel) {
                miMemberExt = masterPersonnel1;
                allPersonnel.put(miMemberExt.getUniqueName(), miMemberExt);
            }
            
                
	}
	

	/**
	 * Gets the user repository.
	 * 
	 * @return The user repository.
	 */
	public static MasterListRepository2 getInstance() {
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
		return allPersonnel.get(uniqueName);
	}

	/**
	 * Gets all users.
	 * 
	 * @return All users.
	 */
	public MasterPersonExt[] getAllMembers() {
		return allPersonnel.values().toArray(new MasterPersonExt[] {});
	}
    

}
