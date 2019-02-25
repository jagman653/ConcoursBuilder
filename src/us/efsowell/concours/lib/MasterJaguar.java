/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.util.Objects;

/**
 *
 * @author Ed Sowell
 */
public class MasterJaguar {
    private Long masterjaguar_id;
    private Long masterpersonnel_id;
    private String jcnaclass_c;
    private String jcnaclass_d;
    private String joclacategory;
    private Integer year;
    private String description;
    private String model;
    private String unique_desc;
    private String color;
    private String platevin;
    
   public MasterJaguar(Long aMasterjaguar_id, Long aMasterpersonnel_id, String aJcnaclass_c, String aJcnaclass_d, String aJoclacategory, Integer aYear, String aModel, String aDescription, 
            String aUnique_desc, String aColor, String aPlatevin) {
        masterjaguar_id = aMasterjaguar_id;
        masterpersonnel_id = aMasterpersonnel_id;
        jcnaclass_c = aJcnaclass_c;
        jcnaclass_d = aJcnaclass_d;
        joclacategory = aJoclacategory;
        year = aYear;
        model = aModel;
        description = aDescription;
        unique_desc = aUnique_desc;
        color = aColor;
        platevin = aPlatevin;
   }
   
   // Alternative Constructor
   public MasterJaguar(String aJcnaclass_c, String aJcnaclass_d, String aJoclacategory, Integer aYear, String aModel, String aDescription, 
                       String aUnique_desc, String aColor, String aPlatevin) {
        jcnaclass_c = aJcnaclass_c;
        jcnaclass_d = aJcnaclass_d;
        joclacategory = aJoclacategory;
        year = aYear;
        model = aModel;
        description = aDescription;
        unique_desc = aUnique_desc;
        color = aColor;
        platevin = aPlatevin;
   }
   public void setMj_id(Long aMasterjaguar_id){
       masterjaguar_id = aMasterjaguar_id;
   }
   
   public void setMp_id(Long aMasterpersonnel_id){
       masterpersonnel_id = aMasterpersonnel_id;
   }
   
   
   public Long getMasterJaguarID(){
           return masterjaguar_id;
   }
   public String getJcnaclass_c(){
           return jcnaclass_c;
   }
   public String getJcnaclass_d(){
           return jcnaclass_d;
   }
   public String getJoclacategory(){
           return joclacategory;
   }
   public Integer getYear(){
           return year;
   }
   
   public String getModel(){
           return model;
   }   
   public String getDescription(){
           return description;
   }
   public String getUniqueDesc(){
           return unique_desc;
   }
   public String getColor(){
           return color;
   }
   public String getPlateVIN(){
           return platevin;
   }
   public void setColor(String aColor){
           color = aColor;
   }
   public void setPlateVIN(String aPlateVin){
           platevin = aPlateVin;
   }
   
   public String getValue() {
        return unique_desc;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

   
    public boolean equal(Object other){
        //return (this.unique_desc.equals(other.unique_desc));
        //return Objects.equals(this.unique_desc, new String(other.unique_desc));
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof MasterJaguar)) return false;
        MasterJaguar o = (MasterJaguar) other;
        return o.unique_desc.equals(this.unique_desc);
    }
   
}
