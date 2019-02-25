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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.MasterJaguar;
//import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MyJavaUtils;

/**
 * 
 * @author Kirill Grouchnikov
 */
public class MasterListRepository {
	/**
	 * Singleton instance.
	 */
	private static MasterListRepository instance = new MasterListRepository();

	private Map<String, MasterPersonExt> allMembers;
	private Map<String, MasterJaguar> memberJaguarStable;  // one memberJaguarStable for each member in MasterList 
        
        Concours theConcours;

        /**
         *   Don't think this constructor is needed... it was in example provided by Grouchnikov
         */
        /*
	private MasterListRepository() {
		allMembers = new TreeMap<String, MasterPersonExt>();
                                                                    // masterjaguar_id, masterperson_id 
                MasterJaguar[] member1JaguarStable ={ new MasterJaguar(99999L, 9999999L, "aJcnaclass_c", "aJcnaclass_d", "aJoclacategory", 2020, "aDescription", "aUnique_desc", "aColor", "aPlatevin")};
            
               // Long aMasterpersonnel_id, Integer aJcna, String aClub, String aLastname, String aFirstname, 
              //   String aUnique_name, String aJudgestatus, Integer aCertyear,
              //   String aAddress_street,  String aCity, String aState, 
              //   String aCountry, String aPostalcode, String aPhone_work, String aPhone_home,
              //   String aPhone_cell,  String aEmail, MasterJaguar[] aJagArray                
                
                MasterPersonExt user1 = new MasterPersonExt(99999L, 12345, "JOCLA", "Sowell", "Edward", "", "SowellEdw", "Judge", 2014, 
                            "1207 N Van Buren St", "Placentia", "CA", "USA", "92870", 
                    "", "714-993-2588", "714-403-8818", "ed_c1@efsowell.us", member1JaguarStable);
                MasterJaguar[] member2JaguarStable ={ new MasterJaguar(99999L, 9999999L, "aJcnaclass_c", "aJcnaclass_d", "aJoclacategory", 2020, "aDescription", "aUnique_desc", "aColor", "aPlatevin")};
		MasterPersonExt user2 = new MasterPersonExt(99999L, 12345, "JOCLA", "Joe", "Blow", "", "BlowJoe", "Judge", 2014, 
                            "102 First St", "Los Angeles", "CA", "USA", "90045", 
                    "", "213-993-2588", "323-403-8818", "jb@yahoo.com", member2JaguarStable);
                MasterJaguar[] member3JaguarStable ={ new MasterJaguar(99999L, 9999999L, "aJcnaclass_c", "aJcnaclass_d", "aJoclacategory", 2020, "aDescription", "aUnique_desc", "aColor", "aPlatevin")};
		MasterPersonExt user3 = new MasterPersonExt(99999L, 34567, "SDJC", "Bill", "Gates", "" ,"GatesBil", "", 1999, 
                            "102 First St", "Los Angeles", "CA", "USA", "90045", 
                    "", "213-993-2588", "323-403-8818", "jb@yahoo.com", member3JaguarStable);

		// for the sake of this example, assume that the uniqueName 
		// is unique
		allMembers.put(user1.getUniqueName(), user1);
		allMembers.put(user2.getUniqueName(), user2);
		allMembers.put(user3.getUniqueName(), user3);
                }
        */
        
	

   // Constructor for function access       
    private MasterListRepository() {

    }        
    
    /*    8/27/2015
      Changed to allow loading repository from either  memory or database
        
      6/3/2016 Added a copy constructor and a function to remove an element
    
    */
    
        public MasterListRepository(Concours aConcours, boolean loadFromMemory) {
            MasterPersonExt miMember;
            MasterJaguar miJaguar;
            allMembers = new TreeMap<String, MasterPersonExt>();
            MyJavaUtils utils = new MyJavaUtils();

            if(loadFromMemory){
                // Load from memory
       
               // MasterPersonExt miMemberExt;
                ArrayList<MasterPersonExt> masterPersonnel;
                //ArrayList<MasterJaguar> masterJaguar;
                masterPersonnel = aConcours.GetConcoursMasterPersonnel();
                //masterJaguar  = aConcours.GetConcoursMasterJaguar();
                for (MasterPersonExt mp : masterPersonnel) {
                    allMembers.put(mp.getUniqueName(), mp);
                }
                
            } else{
                // Load from Datbase
                Connection conn = aConcours.GetConnection();
                Statement stat_mp; // Master Personnel
                ResultSet rs_mp;
                Statement stat_mj; // Master Jaguar
                ResultSet rs_mj;

                stat_mp = null;
                rs_mp = null;
                try {
                    stat_mp = conn.createStatement();
                    rs_mp = stat_mp.executeQuery("select * from MasterPersonnel;"); 
                } catch (SQLException ex) {
                    Logger.getLogger(MasterListRepository.class.getName()).log(Level.SEVERE, null, ex);
                }
                Integer intJcna;
                String strClub;
                String strFirstName;
                String strLastName;
                String strUniqueName;
                String strMI;
                String strJudgeStatus;
                Integer intCertYear;
                String strAddress_street;
                String strCity;
                String strState;
                String strCountry;
                String strPostal;
                String strPhone_work;
                String strPhone_home;
                String strPhone_cell;
                String strEmail;   
                Long lngMasterPersonnelID;

                Long lngMasterJaguarID;
                String strJCNAClassC;
                String strJCNAClassD;
                String strJOCLACategory;
                Integer intYear;
                String strModel; // 3/17/2017
                String strDescription;
                String strUniqueDescription;
                String strColor;
                String strPlateVIN;
                try { 
                        // Insert position 0 dummy to force a selection by user
                        memberJaguarStable = new TreeMap<String, MasterJaguar>();
                     /*   miJaguar = new MasterJaguar(99999L, 9999999L, "ChampClass", "DirvenClass", "JOCLACat", 2020, "Description", "SelectJaguar", "Color", "Plate"); 
                        memberJaguarStable.put(miJaguar.getUniqueDesc(), miJaguar);

                     Put in a dummy first member to force user to make a selection
                   miMember = new MasterPersonExt(99999L, 99999, "Club", "Bob", "Aaron", "AaronBob", "", 1955, 
                                "streetAddr", "City", "State", "Conntry", "Postal", 
                        "123-456-7890", "123-456-7890", "123-456-7890", "bill@ms.com", memberJaguarStable.values().toArray(new MasterJaguar[]{}));
                        allMembers.put(miMember.getUniqueName(), miMember);
                        */
                    while (rs_mp.next()) {
                        //System.out.println("\nunique_name:" + rs_mp.getString("unique_name") + " JCNA number: " + rs_mp.getString("jcna"));
                        intJcna = rs_mp.getInt("jcna");
                        strClub = rs_mp.getString("club");
                        strFirstName = rs_mp.getString("firstname");
                        strLastName = rs_mp.getString("lastname");
                        strUniqueName = rs_mp.getString("unique_name");
                        //... no longer necessary to use the utility function since mi in now in the databese 3/18/2018
                        // getMI(String aLastName, String aFirstName, String aUniqueName, int aMaxFistNameExtension){
                        //strMI = utils.getMI(strLastName, strFirstName, strUniqueName, aConcours.GetMaxFirstNameExtension() );
                        strMI = rs_mp.getString("mi");
                        strJudgeStatus = rs_mp.getString("judgestatus");
                        intCertYear = rs_mp.getInt("cert_year");
                        strAddress_street = rs_mp.getString("address_street");
                        strCity = rs_mp.getString("city");
                        strState = rs_mp.getString("state");
                        strCountry = rs_mp.getString("country");
                        strPostal = rs_mp.getString("postalcode");
                        strPhone_work = rs_mp.getString("phone_work");
                        strPhone_home = rs_mp.getString("phone_home");
                        strPhone_cell = rs_mp.getString("phone_cell");
                        strEmail = rs_mp.getString("email");
                        lngMasterPersonnelID = rs_mp.getLong("masterpersonnel_id");


                        // Insert position 0 dummy to force a selection by user
                        memberJaguarStable = new TreeMap<String, MasterJaguar>();
                       // miJaguar = new MasterJaguar(99999L, 9999999L, "ChampClass", "DirvenClass", "JOCLACat", 2020, "Description", "SelectJaguar", "Color", "Plate"); 
                       // memberJaguarStable.put(miJaguar.getUniqueDesc(), miJaguar);
                        // get Jaguars belonging to this owner
                        stat_mj = conn.createStatement();
                        rs_mj = stat_mj.executeQuery("select * from MasterJaguar where masterpersonnel_id == " + lngMasterPersonnelID + ";"); 
                        while (rs_mj.next()) {
                            lngMasterJaguarID = rs_mj.getLong("masterjaguar_id");
                            strJCNAClassC= rs_mj.getString("jcnaclass_c");
                            strJCNAClassD= rs_mj.getString("jcnaclass_d");
                            strJOCLACategory= rs_mj.getString("joclacategory");
                            intYear= rs_mj.getInt("year");;
                            strDescription= rs_mj.getString("description");
                            //strModel = strDescription;  // MUST BE FIXED AFTER EDITING masterjaguar table. 3/17/2018
                            strModel = rs_mj.getString("model");
                            strUniqueDescription= rs_mj.getString("unique_desc");;
                            strColor= rs_mj.getString("color");;
                            strPlateVIN= rs_mj.getString("platevin");
                            /*
                             Long aMasterjaguar_id, String aJcnaclass_c, String aJcnaclass_d, String aJoclacategory, Integer aYear, String aDescription, 
                                    String aUnique_desc, String aColor, String aPlatevin                        
                            */
                            miJaguar = new MasterJaguar(lngMasterJaguarID, lngMasterPersonnelID, strJCNAClassC, strJCNAClassD, strJOCLACategory, intYear, strModel,
                            strDescription, strUniqueDescription, strColor, strPlateVIN);
                            memberJaguarStable.put(miJaguar.getUniqueDesc(), miJaguar);
                        }
                        rs_mj.close();
                        stat_mj.close();

                        miMember = new MasterPersonExt(lngMasterPersonnelID, intJcna, strClub, strLastName, strFirstName,  strMI, strUniqueName, strJudgeStatus, intCertYear, 
                                strAddress_street, strCity, strState, strCountry, strPostal, 
                        strPhone_work, strPhone_home, strPhone_cell, strEmail, memberJaguarStable.values().toArray(new MasterJaguar[]{}));
                        allMembers.put(miMember.getUniqueName(), miMember);


                    }
                    rs_mp.close();
                    stat_mp.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MasterListRepository.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
	}
        //
        // Copy constructor
        //
    public MasterListRepository(MasterListRepository aRepository){
        MasterPersonExt[] allmemberAry = aRepository.getAllMembers();
        allMembers = new TreeMap<String, MasterPersonExt>();
        for(MasterPersonExt mp : allmemberAry){
           allMembers.put(mp.getUniqueName(), mp);
        }
    }	

	/**
	 * Gets the user repository.
	 * 
	 * @return The user repository.
	 */
	public static MasterListRepository getInstance() {
		return instance;
	}

	/**
	 * Gets the information on the user with the specified UniqueName.
	 * 
	 * @param uniqueName
	 *
	 * @return Matching user info.
	 */
	public MasterPersonExt getMemberInfo(String uniqueName) {
		return allMembers.get(uniqueName);
	}

	/**
	 * Gets all users.
	 * 
	 * @return All users.
	 */
	public MasterPersonExt[] getAllMembers() {
		return allMembers.values().toArray(new MasterPersonExt[] {});
	}
        
        // Remove an element
        public void remove(String aUniqueName){
            allMembers.remove(aUniqueName);
        }

}
