/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Ed Sowell
 */
//
// Adds an array of MasterJaguar
//  Needed to support AddConcoursEntry
//
//public class MasterPersonExt extends MasterPerson{
public class MasterPersonExt implements Comparable<MasterPersonExt>{
    Long masterpersonnel_id;
    Integer jcna;
    String club;
    String lastname;
    String firstname;
    String middleInitial;
    String uniquename;
    String judgestatus;
    Integer certyear;
    String addressstreet;
    String city;
    String state;
    String country;
    String postalcode;
    String phonework;
    String phonehome;
    String phonecell;
    String email;
    MasterJaguar[] jaguarstable;
    Concours theConcours;
    
     // Constructor --- to construct instance for function access
    public  MasterPersonExt(){
        
    }

    
    // Constructor
    
    public  MasterPersonExt(Long aMasterpersonnel_id, Integer aJcna, String aClub, String aLastname, String aFirstname, String aMI,
                 String aUnique_name, String aJudgestatus, Integer aCertyear,
                 String aAddress_street,  String aCity, String aState, 
                 String aCountry, String aPostalcode, String aPhone_work, String aPhone_home,
                 String aPhone_cell,  String aEmail, MasterJaguar[] aJagArray){
     masterpersonnel_id = aMasterpersonnel_id;
     jcna = aJcna;
     club = aClub;
     lastname = aLastname;
     firstname = aFirstname;
     uniquename = aUnique_name;
     judgestatus = aJudgestatus;
     certyear = aCertyear;
     addressstreet = aAddress_street;
     city = aCity;
     state = aState;
     country = aCountry;
     postalcode = aPostalcode;
     phonework = aPhone_work;
     phonehome = aPhone_home;
     phonecell = aPhone_cell;
     email = aEmail;
     jaguarstable = aJagArray;
     middleInitial = aMI;
        
    }
    /*
      Constructor w/o jaguarstable initialization
    */
    public  MasterPersonExt(Long aMasterpersonnel_id, Integer aJcna, String aClub, String aLastname, String aFirstname, String aMI,
                 String aUnique_name, String aJudgestatus, Integer aCertyear,
                 String aAddress_street,  String aCity, String aState, 
                 String aCountry, String aPostalcode, String aPhone_work, String aPhone_home,
                 String aPhone_cell,  String aEmail){
     masterpersonnel_id = aMasterpersonnel_id;
     jcna = aJcna;
     club = aClub;
     lastname = aLastname;
     firstname = aFirstname;
     uniquename = aUnique_name;
     middleInitial = aMI;
     judgestatus = aJudgestatus;
     certyear = aCertyear;
     addressstreet = aAddress_street;
     city = aCity;
     state = aState;
     country = aCountry;
     postalcode = aPostalcode;
     phonework = aPhone_work;
     phonehome = aPhone_home;
     phonecell = aPhone_cell;
     email = aEmail;
       
    }
    
	public Integer getJcna() {
		return jcna;
	}
	public String getClub() {
		return club;
	}

	public String getLastName() {
		return lastname;
	}
	public String getMI() {
		return middleInitial;
	}
	public String getFirstName() {
		return firstname;
	}
	public String getUniqueName() {
		return uniquename;
	}
	public String getJudgeStatus() {
		return judgestatus;
	}
	public Integer getCertYear() {
		return certyear;
	}
       public String getAddressSreet() {
		return addressstreet;
	}
        public String getCity() {
		return city;
	}
        public String getState() {
		return state;
	}
        public String getCountry() {
		return country;
	}
        public String getPostalCode() {
		return postalcode;
	}
        public String getPhoneWork() {
		return phonework;
	}
        public String getPhoneHome() {
		return phonehome;
	}
        public String getPhoneCell() {
		return phonecell;
	}
        public String getEmail() {
		return email;
	}
        
        public Long getMasterPersonID() {
            return masterpersonnel_id;
	}
        public void setMasterPersonID(Long aMpid) {
            masterpersonnel_id = aMpid;
	}
        /*
        MasterPersonExt tempMasterPerson = new MasterPersonExt(0L, intJCNA, strClub, strLN, fn,
            mi, un, strJudgeStatus, intCertYear, strStreet,
            strCity, strState, strCountry, strPostalCode,
            strPhoneWork, strPhoneHome, strPhoneCell, strEmail, newJagArray);
        
        */
        public void setMasterPersonPersonalData(Integer aJCNA, String aClub, String aLN, String aFN,
            String aMi, String aUN, String aJudgeStatus, Integer aCertYear, String aStreet,
            String aCity, String aState, String  aCountry, String  aPostalCode,
            String aPhoneWork, String  aPhoneHome, String  aPhoneCell, String aEmail){
            jcna = aJCNA;
            club = aClub;
            lastname = aLN;
            firstname = aFN;
            middleInitial = aMi;
            uniquename = aUN;
            judgestatus = aJudgeStatus;
            certyear = aCertYear;
            addressstreet =  aStreet;
            city = aCity;
            state = aState;
            country = aCountry;
            postalcode = aPostalCode;
            phonework = aPhoneWork;
            phonehome = aPhoneHome;
            phonecell = aPhoneCell;
            email = aEmail;
        }    
    public MasterJaguar[] getJaguarStable(){
        return jaguarstable;
    }
    public ArrayList<MasterJaguar> getJaguarStableList(){
        ArrayList<MasterJaguar> jaguarstablelist = new ArrayList<>() ;
        jaguarstablelist.addAll(Arrays.asList(jaguarstable));
        return jaguarstablelist;
    }
    
    public void addJaguarToStable(MasterJaguar aMasterJaguar){
        
        // Must convert MasterJaguar[] jaguarstable to a list, add to it, then convert back!!!
        ArrayList<MasterJaguar> jaguarstablelist = new ArrayList<>() ;
        jaguarstablelist.addAll(Arrays.asList(jaguarstable));
        jaguarstablelist.add(aMasterJaguar);
        MasterJaguar[] temparray = new MasterJaguar[jaguarstablelist.size()];
        jaguarstablelist.toArray(temparray);         
        jaguarstable = temparray;
    }
    public void removeJaguarFromStable(MasterJaguar aMasterJaguar){
        // A test for a legitimate removal must be done before removeJaguarFromStable is called
        // cannot be removed if it is used in the Concours
        // Cannot be removed if it would leave the Master Person without any Jaguars
        //
        // Must convert MasterJaguar[] jaguarstable to a list, add to it, then convert back!!!
        //working here.... debugging edit MasterPerson remove Jag from stable 6/20/2016
        ArrayList<MasterJaguar> jaguarstablelist = new ArrayList<>() ;
        jaguarstablelist.addAll(Arrays.asList(jaguarstable)); // existing stable
        jaguarstablelist.remove(aMasterJaguar); // after removing
        MasterJaguar[] temparray = new MasterJaguar[jaguarstablelist.size()];
        jaguarstablelist.toArray(temparray);         
        jaguarstable = temparray;
    }

    @Override
        public String toString() {
            return uniquename;
        }   

    @Override
    public int compareTo(MasterPersonExt t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
public static class Comparators {

        public static Comparator<MasterPersonExt> LASTNAME = new Comparator<MasterPersonExt>() {
            @Override
            public int compare(MasterPersonExt o1, MasterPersonExt o2) {
                return o1.lastname.compareToIgnoreCase(o2.lastname);
            }
        };
        public static Comparator<MasterPersonExt> FIRSTNAME = new Comparator<MasterPersonExt>() {
            @Override
            public int compare(MasterPersonExt o1, MasterPersonExt o2) {
                return o1.firstname.compareToIgnoreCase(o2.firstname);
            }
        };
        public static Comparator<MasterPersonExt> UNIQUENAME = new Comparator<MasterPersonExt>() {
            @Override
            public int compare(MasterPersonExt o1, MasterPersonExt o2) {
                return o1.uniquename.compareToIgnoreCase(o2.uniquename);
            }
        };
    }

}
