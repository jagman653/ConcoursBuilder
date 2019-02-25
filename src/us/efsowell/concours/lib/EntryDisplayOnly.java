/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package us.efsowell.concours.lib;

import java.util.Comparator;

/**
 *
 * @author Ed Sowell
 */

//
//              Not used.
//
public class EntryDisplayOnly {

    String ID; // e.g., C04/150-1
    String Year;
    String Description;
    String UniqueDescription;
    String OwnerFirst;
    String OwnerLast;
    String OwnerUnique;
    String JCNA;
    String Color;
    String Plate;
    Integer Node;
    
    // Constructor
    public EntryDisplayOnly(String aID,   String aYear,    String aDescription,    String aUniqueDescription, String aOwnerFirst,    String aOwnerLast,  String aOwnerUnique,     String aJCNA,    String aColor,    String aPlate,    Integer aNode){
         ID = aID;
         Year = aYear;
         Description = aDescription;
         UniqueDescription = aUniqueDescription;
         OwnerFirst = aOwnerFirst;
         OwnerLast = aOwnerLast;
         OwnerUnique = aOwnerUnique;
         JCNA = aJCNA;
         Color = aColor;
         Plate = aPlate;
         Node = aNode;
    }

    public String GetID(){
        return ID;
    }
    
    
    public String GetYear(){
        return Year;
    }
    
    public String GetDescription(){
        return Description;
    }

    public String GetUniqueDescription(){
        return UniqueDescription;
    }
    
    
    public String GetOwnerFirst(){
        return OwnerFirst;
    }
    
    public String GetOwnerLast(){
        return OwnerLast;
    }
    public String GetOwnerUnique(){
        return OwnerUnique;
    }
    
    public String GetJCNA(){
        return JCNA;
    }
    
    public String GetColor(){
        return Color;
    }
    public String GetPlateVin(){
        return Plate;
    }
    public Integer GetNode(){
        return Node;
    }
    public EntryDisplayOnly GetEntry(){
        return this;
    }
    
    




    public void SetEntryID(String aEntryName){
       ID =  aEntryName;
    }
    

	/**
	 *	The ID property will double as the toString representation.
	 *
	 *  @return the ID
	 */
	@Override
	public String toString()
	{
		return ID;
	}
	/**
	 *	The Node property will double as the Value representation.
	 *
	 *  @return the Node
	 */
    

        public Integer getValue(){
            return Node;
        }
}
