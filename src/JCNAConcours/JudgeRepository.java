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

import java.util.Map;
import java.util.TreeMap;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;

/**
 *
 * @author Ed Sowell
 */
public class JudgeRepository {
	/**
	 * Singleton instance.
	 */
	private static JudgeRepository instance = new JudgeRepository();

	private Map<String, Judge> allJudges;
	private MasterJaguar[] jaguarEntriesArray;  // one jaguarEntriesArray for each member in  ConcoursPersonnel
        

        /**
         *   Don't think this constructor is needed... it was in example provided by Grouchnikov
         */
	private JudgeRepository() {
        }
        
	
        public JudgeRepository(Concours aConcours) {
            MasterPersonnel masterPersonnel = aConcours.GetMasterPersonnelObject();
            Judges concoursJudges = aConcours.GetConcoursJudgesObject();
            allJudges = new TreeMap<String, Judge>();
            //List<MasterJaguar> ownerEntryJaguars = new ArrayList<MasterJaguar>();
          
            for(Judge j : concoursJudges.GetConcoursJudges()){
               /* String unique_name = j.getUniqueName();
                Integer node = j.GetNode();
                List<Integer> entrylist = j.GetEntryList(); // Entry Nodes
                String lastName = masterPersonnel.GetMasterPersonnelLast(unique_name);
                String firstName = masterPersonnel.GetMasterPersonnelFirst(unique_name);
                Integer jcna = masterPersonnel.GetMasterPersonnelJCNA(unique_name);
                */
                allJudges.put(j.getUniqueName(), j);
            }
	}
	

	/**
	 * Gets the user repository.
	 * 
	 * @return The user repository.
	 */
	public static JudgeRepository getInstance() {
		return instance;
	}

	public Judge getMemberInfo(String uniqueName) {
		return allJudges.get(uniqueName);
	}
        
	/**
	 * Gets the information on the user with the specified UniqueName.
	 * 
	 * @param uniqueName
	 *            
	 * @return Matching user info.
	 */
	public Judge getJudge(String uniqueName) {
		return allJudges.get(uniqueName);
	}

	/**
	 * Gets all users.
	 * 
	 * @return All users.
	 */
	public Judge[] getAllJudges() {
		return allJudges.values().toArray(new Judge[] {});
	}
    
    
}
