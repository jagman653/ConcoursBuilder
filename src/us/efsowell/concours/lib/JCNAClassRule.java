/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

/**
 *
 * @author Ed Sowell
 */
public class JCNAClassRule {

    String division;
    String classname;
    Long class_id;
    String modelname;
    String descriptor_1;
    String descriptor_2;
    String descriptor_3;
    Integer firstyear;
    Integer lastyear;  
    
    // Constructor
    public JCNAClassRule(String aDivision,    String aClassname,  Long aClass_id,  String aModelname, 
                         String aDescriptor_1, String aDescriptor_2,  String aDescriptor_3,  Integer aFirstyear,  Integer aLastyear){
        
         division  = aDivision;
         classname = aClassname;
         class_id = aClass_id;
         modelname = aModelname;
         descriptor_1 = aDescriptor_1;
         descriptor_2 = aDescriptor_2;
         descriptor_3 = aDescriptor_3;
         firstyear = aFirstyear;
         lastyear = aLastyear;  
    }
    
    public String getDivision(){
        return division;
    }
    public String getClassName(){
        return classname;
    }
    public String getModelName(){
        return modelname;
    }
    public String getDescriptor_1(){
        return descriptor_1;
    }
    public String getDescriptor_2(){
        return descriptor_2;
    }
    public String getDescriptor_3(){
        return descriptor_3;
    }
    
    public Long getClass_id(){
        return class_id;
    }
    public Integer getFirstyear(){
        return firstyear;
    }
    public Integer getLastyear(){
        return lastyear;
    }
}
