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
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.awt.Component;
//import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
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
public class CreateEntryPacketLabels {
    private static String strPacketInputFile_judged;
    private static String strPacketInputFile_disp;
    private String strAbsolutePathToNewDir;
    private static String concoursBuilderDataPath;
    private static String concoursBuilderDocsPath;
    private String strPacketOutputFileBase;
    String strConcoursPath;
    Concours theConcours;
   // Constructor
   public CreateEntryPacketLabels( Concours aConcours, String aDocsPath, String aDataPath){
        theConcours = aConcours;
        concoursBuilderDataPath = aDataPath;
        concoursBuilderDocsPath = aDocsPath;
        int pos;
        strConcoursPath= theConcours. GetThePath(); // includes the db file
        pos = strConcoursPath.lastIndexOf("\\");
        strAbsolutePathToNewDir = strConcoursPath.substring(0, pos);
   }
   
public void createTheLabelsPdf(Map<Integer,String> aTSIndexToTimeMap) throws FileNotFoundException, DocumentException{
    Document theDoc = new Document(PageSize.LETTER);
    // Avery template 5395 Name Badges Laser/Inkjet 42395
    List<PdfPCell> labelContents = new ArrayList<>();
    Chunk whitespace = new Chunk("  ");
    
    int count = 0;    
    JCNAClasses jcnaClasses = theConcours.GetJCNAClasses();
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    for( Entry e : theConcours.GetEntriesList()){
        count++;
        PdfPCell acell = new PdfPCell();
        String strJCNAClass  = e.GetClassName();
        Phrase phraseOwn = new Phrase();
        Chunk chunkOwnLabel = new Chunk("Owner: ", labelFont);
        Chunk owner = new Chunk(e.GetOwnerFirst() + " " + e.GetOwnerLast());
        phraseOwn.add(chunkOwnLabel);
        phraseOwn.add(owner);
        Paragraph pOwn = new Paragraph(phraseOwn);
        acell.addElement(pOwn);
        
        Phrase phraseEnt = new Phrase();
        Chunk chunkEntLabel = new Chunk("Entry ID: ", labelFont);
        Chunk chunkEntID = new Chunk(e.GetID());
        phraseEnt.add(chunkEntLabel);
        phraseEnt.add(chunkEntID);
        Paragraph pEnt = new Paragraph(phraseEnt);
        acell.addElement(pEnt);
        
        Phrase phraseModClass = new Phrase();
        Chunk chunkModLabel = new Chunk("Model: ", labelFont);
        Chunk chunkModel= new Chunk(e.GetModel());
        Chunk chunkClassLabel = new Chunk("Class: ", labelFont);
        Chunk chunkClass= new Chunk(e.GetClassName());
       
        phraseModClass.add(chunkModLabel);
        phraseModClass.add(chunkModel);
        phraseModClass.add(whitespace);
        phraseModClass.add(chunkClassLabel);
        phraseModClass.add(chunkClass);
        Paragraph pModClass = new Paragraph(phraseModClass);
        acell.addElement(pModClass);
        
        if(!strJCNAClass.equals("DISP")){
            float fntSize, leading;
            fntSize = 20f;
            leading = 1.2f*fntSize;
            Integer timeSlotIndex = e.GetTimeslotIndex();  
            String judgingTime = aTSIndexToTimeMap.get(timeSlotIndex);
            Phrase phraseTime = new Phrase(leading, "Judged at: "  + judgingTime, FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize));
            Paragraph pTime = new Paragraph(phraseTime);
            pTime.setSpacingBefore(18);
            acell.addElement(pTime);
            
            Chunk chunkTeamLabel = new Chunk("By: ", labelFont);
            
            Phrase phraseTeam = new Phrase();
            ConcoursClass cc = theConcours.GetConcoursClassesObject().GetConcoursClassObject(e.GetClassName());
            ArrayList<Judge> judges = cc.GetClassJudgeObjects();
            String judgingTeam = "";
            int k = 0;
            for(Judge judge : judges){
                if(k == 0){
                    judgingTeam = judgingTeam + judge.getUniqueName() ;
                } else {
                    judgingTeam = judgingTeam + ", " + judge.getUniqueName();
                }
                k++;
            }
            Chunk chunkTeam = new Chunk(judgingTeam);
            phraseTeam.add( chunkTeamLabel);
            phraseTeam.add( chunkTeam);
            Paragraph pJudged = new Paragraph(phraseTeam);
            acell.addElement(pJudged);
        } else{
            float fntSize, leading;
            fntSize = 20f;
            leading = 1.2f*fntSize;
            
            //Phrase phrase3 = new Phrase();
            Phrase phraseDisp = new Phrase(leading, "Display Only",  FontFactory.getFont(FontFactory.HELVETICA_BOLD, fntSize));
            //phrase3.add("Display only");
            Paragraph pDisp = new Paragraph(phraseDisp);
            acell.addElement(pDisp);
        }
        //p1.setSpacingAfter(18);
        //p2.setSpacingAfter(18);
        labelContents.add(acell);
    }
    //okDialog("Number of Entries: " + count);
    
    //Create a new dir named "Labels" if not already present
   
    File outfiledir = new File(strAbsolutePathToNewDir +"\\Labels");
    if (!outfiledir.exists()) {
        outfiledir.mkdir();
    }    
    String outFilePath = strAbsolutePathToNewDir + "\\Labels\\EntryPacketLabelsAvery5395.pdf";    
    AveryLabels5395 theLabels = new AveryLabels5395(theDoc, labelContents, outFilePath);

}
   
}
