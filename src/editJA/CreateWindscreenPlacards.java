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
package editJA;
import static JCNAConcours.EditConcoursEntryDialog.okDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.JCNAClasses;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterJaguars;
import us.efsowell.concours.lib.MasterPersonnel;

/**
 *
 * @author Ed Sowell
 */
public class CreateWindscreenPlacards {
   private static String strPlacardsInputFile;
   private static String strPlacardsInputFile_c;
   private static String strPlacardsInputFile_d;
   private static String strPlacardsInputFile_s;
   private static String strPlacardsInputFile_disp;
   private static String strPlacardsOutputFileBase;
   private String strAbsolutePathToNewDir;
   private static String concoursBuilderDataPath;
   private static String concoursBuilderDocsPath;
   String strConcoursPath;
   // strWindscreenPlacardsInputFile  = "C:\\Users\\Ed Sowell\\Documents\\NetBeansProjects\\SecondPDF\\Score_Sheets v07022016Form.pdf";
   // strWindscreenPlacardsOutputFileBase = "C:\\Users\\Ed Sowell\\Documents\\NetBeansProjects\\SecondPDF\\Score_SheetsFilledIn.pdf";
   //private static String theConcoursFolder;
   Concours theConcours;
   //PdfReader placardReader;
   PdfReader placardReaderOriginal_c;
   PdfReader placardReaderCopy_c;

   PdfReader placardReaderOriginal_d;
   PdfReader placardReaderCopy_d;

   PdfReader placardReaderOriginal_s;
   PdfReader placardReaderCopy_s;

   PdfReader placardReaderOriginal_disp;
   PdfReader placardReaderCopy_disp;
   
   
   // Constructor
   public CreateWindscreenPlacards( Concours aConcours, String aDocsPath, String aDataPath){
        theConcours = aConcours;
        concoursBuilderDataPath = aDataPath;
        concoursBuilderDocsPath = aDocsPath;
        //Connection conn = theConcours.GetConnection();
        int pos;
        strConcoursPath= theConcours. GetThePath(); // includes the db file
        pos = strConcoursPath.lastIndexOf("\\");
        strAbsolutePathToNewDir = strConcoursPath.substring(0, pos);
        //String msg = "Will expect the blank Forms to be in " + concoursBuilderDocsPath;
        //okDialog(msg);
        
        strPlacardsInputFile_c = concoursBuilderDocsPath +    "\\2005_windscreen_c_Form.pdf";
        strPlacardsInputFile_d = concoursBuilderDocsPath +    "\\2005_windscreen_d_Form.pdf";
        strPlacardsInputFile_s = concoursBuilderDocsPath +    "\\2005_windscreen_s_Form.pdf";
        strPlacardsInputFile_disp = concoursBuilderDocsPath + "\\2005_windscreen_disp_Form.pdf";
        strPlacardsOutputFileBase = strAbsolutePathToNewDir + "\\PlacardFilledIn.pdf";
        
       try {
           this.placardReaderOriginal_c = new PdfReader(strPlacardsInputFile_c);
           this.placardReaderOriginal_d = new PdfReader(strPlacardsInputFile_d);
           this.placardReaderOriginal_s = new PdfReader(strPlacardsInputFile_s);
           this.placardReaderOriginal_disp = new PdfReader(strPlacardsInputFile_disp);
       } catch (IOException ex) {
           okDialog("IOException  in CreateWindscreenPlacards");
           theConcours.GetLogger().log(Level.SEVERE, null, ex);
           theConcours.GetLogger().info("IOException in " + CreateWindscreenPlacards.class.getName());
           return;
       }
   }
   
public void fillInAllPlacardForms(Map<Integer,String> aTSIndexToTimeMap){
    //String strPlacardOutputFileBase = strPlacardsOutputFileBase;
    MasterPersonnel mpObject = theConcours.GetMasterPersonnelObject();
    //MasterJaguars masterJaguars = theConcours.GetMasterJaguarsObject();
    int count = 0;    
    JCNAClass jcnaclass;
    String div;

    JCNAClasses jcnaClasses = theConcours.GetJCNAClasses();
    for( Entry e : theConcours.GetEntriesList()){
        String strJCNAClass  = e.GetClassName();
        if(!strJCNAClass.equals("DISP")){
            jcnaclass = jcnaClasses.getJCNAClass(strJCNAClass);
            div = jcnaclass.getDivision();
        } else {
            div = "Display";
        }
        PdfReader placardReaderCopy;
        if(div.equals("Championship")){
            placardReaderCopy = new PdfReader(placardReaderOriginal_c);
        } else if(div.equals("Driven")){
            placardReaderCopy = new PdfReader(placardReaderOriginal_d);
        } else if(div.equals("Special")){
            placardReaderCopy = new PdfReader(placardReaderOriginal_s);
        } else if(div.equals("Display")){
            placardReaderCopy = new PdfReader(placardReaderOriginal_disp);
        } else{
            okDialog("Bad Division in fillInAllPlacardForms: " + div);
            theConcours.GetLogger().info("Bad Division in fillInAllPlacardForms: " + div);
            return;
        }

        //int dotIndex = strPlacardsOutputFileBase.indexOf('.', 0);
       // String strP = strPlacardsOutputFileBase.substring(0, dotIndex) + "-" + e.GetUniqueDescription() + ".pdf";
        String ownerUniqueName = e.GetOwnerUnique();
        String entryYear = e.GetYear();
        String entryClassName = e.GetClassName();
        String jaguarDescription = e.GetUniqueDescription();
        //MasterJaguar mj = masterJaguars.GetMasterJaguar(jaguarDescription);  
       // String jaguarColor = mj.getColor();
        String entryID = e.GetID();
       // String ownerJCNA = mpObject.GetMasterPersonnelJCNA(ownerUniqueName).toString();
        String ownerClub = mpObject.GetMasterPerson(ownerUniqueName).getClub();
        //String streetAddress = mpObject.GetMasterPerson(ownerUniqueName).getAddressSreet();
        String state = mpObject.GetMasterPerson(ownerUniqueName).getState() ;
        String city =  mpObject.GetMasterPerson(ownerUniqueName).getCity();
        String strP;
        Integer timeSlotIndex = e.GetTimeslotIndex();  
       
        String judgingTime = aTSIndexToTimeMap.get(timeSlotIndex);
        ConcoursClass cc = theConcours.GetConcoursClassesObject().GetConcoursClassObject(entryClassName);
        ArrayList<Judge> judges = cc.GetClassJudgeObjects();
        String judgingTeam = "";
        int k = 0;
        for(Judge judge : judges){
            if(k == 0){
                judgingTeam = judgingTeam + judge.GetFirstName() + " " +  judge.GetLastName();
            } else {
                judgingTeam = judgingTeam + ", " + judge.GetFirstName() + " " +  judge.GetLastName();
            }
            k++;
        }
        if("DISP".equals(strJCNAClass)){
            strP = strAbsolutePathToNewDir + "\\Placards\\Display\\"+ e.GetOwnerLast()+"_" +  e.GetUniqueDescription() + "-Placard.pdf";
        } else if( "D".equals(e.GetClassName().substring(0,1))){
            strP = strAbsolutePathToNewDir + "\\Placards\\Driven\\"+ e.GetOwnerLast()+"_" +  e.GetUniqueDescription() + "-Placard.pdf";
        } else {
            strP = strAbsolutePathToNewDir + "\\Placards\\ChampionSpecial\\"+ e.GetOwnerLast()+"_" +  e.GetUniqueDescription() + "-Placard.pdf";
        }

        
        try {
            fillInPlacardForm(placardReaderCopy, div, entryYear,  e.GetModel(), entryClassName, e.GetOwnerFirst() + " " + e.GetOwnerLast(), entryID, city, state, ownerClub, judgingTime, judgingTeam, strP);
        } catch (DocumentException ex) {
            //Logger.getLogger(CreateWindscreenPlacards.class.getName()).log(Level.SEVERE, null, ex);
            String msg = "";
            theConcours.GetLogger().log(Level.SEVERE, "Exception in fillInAllPlacardForms", e);
            okDialog("DocumentException in fillInAllPlacardForms");
            return;
        }
        count++;    
    }
    String msg = "Windscreen placards for " + count + " Entries have been saved in Placards folder in " + strAbsolutePathToNewDir;
    okDialog(msg);
    theConcours.GetLogger().info(msg);
}  
private void fillInPlacardForm(PdfReader reader, String div, String yr, String model, String cls, String entrant, String entryId, String city, String state, String club, String judgingTime, String judgeTeam, String strOutputFile) throws DocumentException {
            PdfStamper stamper;
        try {
            stamper = new PdfStamper(reader, new FileOutputStream(strOutputFile));
            AcroFields form = stamper.getAcroFields();
            //form.setField("DIVISION", div);
            form.setField("YEAR", yr);
            form.setField("MODEL", model);
            form.setField("CLASS", cls);
            form.setField("ENTRANT", entrant);
            form.setField("ENTRYNUMBER", entryId);
            form.setField("CITY", city);
            form.setField("STATEPROVINCE", state);
            form.setField("CLUB", club);
            form.setField("TIME", judgingTime);
            form.setField("TEAM", judgeTeam);
            stamper.close();
        } catch (IOException ex) {
           //theConcours.GetLogger().log(Level.SEVERE, null, ex);
           okDialog("File "  + strOutputFile + " could not be opened for writing.  If it is open, close it and try again.");
           theConcours.GetLogger().info("IOException in " + CreateWindscreenPlacards.class.getName() + " File "  + strOutputFile + " could not be opened for writing.  If it is open, close it and try again.");
           return;
        }
        
   }
   

    
}
