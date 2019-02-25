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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.Owner;
import us.efsowell.concours.lib.Owners;

/**
 *
 * @author Ed Sowell
 */
public class OwnerRepository {
	/**
	 * Singleton instance.
	 */
	private static OwnerRepository instance = new OwnerRepository();

	private Map<String, Owner> allOwners;
	private MasterJaguar[] jaguarEntriesArray;  // one jaguarEntriesArray for each member in  ConcoursPersonnel
        

        /**
         *   Don't think this constructor is needed... it was in example provided by Grouchnikov
         */
	private OwnerRepository() {
        }
        
	
        public OwnerRepository(Concours aConcours) {
            MasterPersonnel masterPersonnel = aConcours.GetMasterPersonnelObject();
            Owners concoursOwners = aConcours.GetConcoursOwnersObject();
            allOwners = new TreeMap<String, Owner>();
            //List<MasterJaguar> ownerEntryJaguars = new ArrayList<MasterJaguar>();
          
            for(Owner o : concoursOwners.GetConcoursOwners()){
               /* String unique_name = o.getUniqueName();
                Integer node = o.GetNode();
                List<Integer> entrylist = o.GetEntryList(); // Entry Nodes
                String lastName = masterPersonnel.GetMasterPersonnelLast(unique_name);
                String firstName = masterPersonnel.GetMasterPersonnelFirst(unique_name);
                Integer jcna = masterPersonnel.GetMasterPersonnelJCNA(unique_name);
                */
                allOwners.put(o.getUniqueName(), o);
            }
	}
	

	/**
	 * Gets the user repository.
	 * 
	 * @return The user repository.
	 */
	public static OwnerRepository getInstance() {
		return instance;
	}

	public Owner getMemberInfo(String uniqueName) {
		return allOwners.get(uniqueName);
	}
        
	/**
	 * Gets the information on the user with the specified UniqueName.
	 * 
	 * @param firstName
	 *            First name.
	 * @return Matching user info.
	 */
	public Owner getOwner(String uniqueName) {
		return allOwners.get(uniqueName);
	}

	/**
	 * Gets all users.
	 * 
	 * @return All users.
	 */
	public Owner[] getAllOwners() {
		return allOwners.values().toArray(new Owner[] {});
	}
    
}
