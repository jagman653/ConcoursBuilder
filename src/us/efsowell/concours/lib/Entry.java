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
public class Entry {

    String ID; // e.g., C04/150-1
    String JCNAClass;
    String Year;
    String Model;
    String Description;
    String UniqueDescription;
    String OwnerFirst;
    String OwnerMI;
    String OwnerLast;
    String OwnerUnique;
    String JCNA;
    String Color;
    String Plate;
    Integer Node;
    Integer TimeslotIndex; // This gets set while TimeslotAssignment is being updated
    
    // Constructor
    public Entry(String aID, String aClass,  String aYear,    String aModel, String aDescription,    String aUniqueDescription, String aOwnerFirst,  String aOwnerMI,  String aOwnerLast,  String aOwnerUnique,     String aJCNA,    String aColor,    String aPlate,    Integer aNode){
         ID = aID;
         JCNAClass = aClass;
         Year = aYear;
         Model = aModel;
         Description = aDescription;
         UniqueDescription = aUniqueDescription;
         OwnerFirst = aOwnerFirst;
         OwnerMI = aOwnerMI;
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
    
    public String GetClassName(){
        return JCNAClass;
    }
    
    public String GetYear(){
        return Year;
    }
    public String GetModel(){
        return Model;
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
    public String GetOwnerMI(){
        return OwnerMI;
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
    public void SetColor(String aColor){
        Color = aColor;
    }
    public String GetPlateVin(){
        return Plate;
    }
    public void SetPlateVin(String aPlateVin){
        Plate = aPlateVin;
    }
    public Integer GetNode(){
        return Node;
    }
    public Entry GetEntry(){
        return this;
    }
    
    
    public Integer GetTimeslotIndex(){
        return TimeslotIndex;
    }

    public void SetEntryJCNAClass(String aJCNAClassName){
       JCNAClass =  aJCNAClassName;
    }

    public void SetEntryID(String aEntryName){
       ID =  aEntryName;
    }
    
    public void SetTimeslotIndex(Integer aTimeslotIndex){
        TimeslotIndex = aTimeslotIndex;
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
