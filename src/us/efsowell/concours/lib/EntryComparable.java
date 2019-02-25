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
public class EntryComparable implements Comparable<EntryComparable>{
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
    public EntryComparable(Entry aEntry){
         ID = aEntry.GetID();
         JCNAClass = aEntry.GetClassName();
         Year = aEntry.GetYear();
         Model = aEntry.GetModel();
         Description = aEntry.GetDescription();
         UniqueDescription = aEntry.GetUniqueDescription();
         OwnerFirst = aEntry.GetOwnerFirst();
         OwnerMI = aEntry.GetOwnerMI();
         OwnerLast = aEntry.GetOwnerLast();
         OwnerUnique = aEntry.GetOwnerUnique();
         JCNA = aEntry.GetJCNA();
         Color = aEntry.GetColor();
         Plate = aEntry.GetPlateVin();
         Node = aEntry.GetNode();
         TimeslotIndex = aEntry.GetTimeslotIndex();
         // Display Entries will have null TimeslotIndex.
         // These get set to a arbitrarily large value, just to keep the needed Collections sort from generating
         // Null Exception. Since Display Entries will not be in any Judge's Entries list, this TimeslotIndex will never be used
         // while generating Judge Packet labels.
         if(TimeslotIndex == null){
             TimeslotIndex = 999;
         }
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
    public EntryComparable GetEntry(){
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


    @Override
    public int compareTo(EntryComparable e) {
        return this.TimeslotIndex > e.TimeslotIndex ? 1 : this.TimeslotIndex < e.TimeslotIndex ? -1 : 0;        
    }
}
