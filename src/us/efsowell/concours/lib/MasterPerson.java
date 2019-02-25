/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

/**
 *
 * @author Ed Sowell
 */
public class MasterPerson {
 
    Long masterpersonnel_id;
    Integer jcna;
    String club;
    String lastname;
    String firstname;
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
    
    // Constructor with aMasterpersonnel_id
    public MasterPerson(Long aMasterpersonnel_id, Integer aJcna, String aClub,
            String aLastname, String aFirstname,  String aUnique_name, String aJudgestatus, 
            String aAddress_street,  String aCity, String aState,   String aCountry, String aPostalcode, 
            String aPhone_work, String aPhone_home, String aPhone_cell,  String aEmail){
     masterpersonnel_id = aMasterpersonnel_id;
     jcna = aJcna;
     club = aClub;
     lastname = aLastname;
     firstname = aFirstname;
     uniquename = aUnique_name;
     judgestatus = aJudgestatus;
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

    // Constructor w/o aMasterpersonnel_id argument
    public MasterPerson(Integer aJcna, String aClub, String aLastname, String aFirstname, 
                        String aUnique_name, String aJudgestatus, Integer aCertyear,
                        String aAddress_street,  String aCity, String aState, 
                        String aCountry, String aPostalcode, String aPhone_work, String aPhone_home, String aPhone_cell,  String aEmail){
     jcna = aJcna;
     club = aClub;
     lastname = aLastname;
     firstname = aFirstname;
     uniquename = aUnique_name;
     judgestatus = aJudgestatus;
     certyear  = aCertyear;
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

    
}
