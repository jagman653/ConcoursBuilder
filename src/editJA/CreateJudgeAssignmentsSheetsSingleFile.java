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
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.EntryComparable;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.JCNAClasses;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterJaguars;
import us.efsowell.concours.lib.MasterPersonnel;

/**
 * Creates a single pdf file with assignment sheets for ALL Judges. This allows easy printing for distribution at the Judges meeting.
 * @author Ed Sowell
 */
public class CreateJudgeAssignmentsSheetsSingleFile {
    private static String strPacketInputFile_judged;
    private static String strPacketInputFile_disp;
    private String strAbsolutePathToNewDir;
    private static String concoursBuilderDataPath;
    private static String concoursBuilderDocsPath;
    private String strPacketOutputFileBase;
    String strConcoursPath;
    Concours theConcours;
    //
    // Nested Class. There is an instance of AssignedJudge for every Judge in the concours.
    //
    private class AssignedJudge {
        Integer judgeNode;
        List<EntryComparable> concoursentries;
        // Constructors
        private  AssignedJudge(Integer aJudgeNode, List<EntryComparable> aEntries){
            judgeNode = aJudgeNode;
            concoursentries = aEntries;
        }

        private  AssignedJudge(Integer aJudgeNode){
            judgeNode = aJudgeNode;
            concoursentries = new ArrayList<EntryComparable>();
        }
        
        private void addEntry(EntryComparable aEntry){
            concoursentries.add(aEntry);
        }
        
        List<EntryComparable> getEntries() {
            return concoursentries;
        }
        
        Integer getJudgeNode(){
            return judgeNode;
        }
        
    }
   /* 
    private class JudgeEntry  {
        String judgingTime;
        int judgingTimeSlot;
        String color;
        String year;
        String model;
        String className;
        
        public JudgeEntry(String aJudgingTime, int aJudgingTimeSlot, String aColor, String aYear, String aModel, String aClassName) {
                this.judgingTime = aJudgingTime;
                this.judgingTimeSlot = aJudgingTimeSlot; 
                this.color = aColor;
                this.year = aYear;
                this.model = aModel;
                this.className = aClassName;
        }
        public Integer getJudgingTimeSlot() {
            return judgingTimeSlot;
        }       

    }
*/
    
    
   // Constructor
   public CreateJudgeAssignmentsSheetsSingleFile( Concours aConcours, String aDocsPath, String aDataPath){
        theConcours = aConcours;
        concoursBuilderDataPath = aDataPath;
        concoursBuilderDocsPath = aDocsPath;
        int pos;
        strConcoursPath= theConcours. GetThePath(); // includes the db file
        pos = strConcoursPath.lastIndexOf("\\");
        strAbsolutePathToNewDir = strConcoursPath.substring(0, pos);
   }
//
//  createTheJudgeAssignmentsSheetsPdf() is called in JusgeAssignDialog
//   
public void createTheJudgeAssignmentsSheetsPdf(Map<Integer,String> aTSIndexToTimeMap) throws FileNotFoundException, DocumentException{
    Document theDoc = new Document(PageSize.LETTER);
    File outfiledir = new File(strAbsolutePathToNewDir +"\\JudgeAssignmentsSheets");
    if (!outfiledir.exists()) {
        outfiledir.mkdir();
    }    
    String outFilePath = strAbsolutePathToNewDir + "\\JudgeAssignmentsSheets\\JudgeAssignmentsSheets.pdf";    
    try{
        PdfWriter.getInstance(theDoc, new FileOutputStream(outFilePath));
    } catch (FileNotFoundException ex){
        String msg = "The output file " + outFilePath + " is in use by another application. Close it and try again.";
        okDialog(msg);
        return;
    }
    theDoc.open();
    Chunk whitespace = new Chunk(" ");
    
    Font JudgeLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    Font EntriesLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

    //
    //  An AssignedJudge has a judgenode and a list of concours entries assigned to that Judge
    //
    List<AssignedJudge> assignedJudges = new ArrayList<>();
    List<EntryComparable> concoursEntriesComparable  = new ArrayList<>();
    for(Entry concoursEntry : theConcours.GetEntriesList()){
        concoursEntriesComparable.add(new EntryComparable(concoursEntry)) ;
    }
    Collections.sort(concoursEntriesComparable); // Puts the entries in order of ascending Judging times.
    
    for(Judge concoursjudge : theConcours.GetConcoursJudges()){
        AssignedJudge assignedJudge = new AssignedJudge(concoursjudge.GetNode());
        // Find the Entries assigned to concoursjudge
        System.out.println("Judge " + concoursjudge.getUniqueName());
        for(EntryComparable concoursEntryComparable : concoursEntriesComparable){
            String className = concoursEntryComparable.GetClassName();
            ConcoursClass cc = theConcours.GetConcoursClassesObject().GetConcoursClassObject(className);
            ArrayList<Judge> classJudges = cc.GetClassJudgeObjects();
            
            for(Judge classjudge : classJudges){
                if(classjudge.getUniqueName() == concoursjudge.getUniqueName()){
                    assignedJudge.addEntry(concoursEntryComparable);
                    break;
                }
            }
            
        }
        System.out.println("assignedJudge Entries: " + assignedJudge.getEntries());
        assignedJudges.add(assignedJudge);
    }
    int count = 0;
    for(AssignedJudge aj : assignedJudges){
        count++;
        //PdfPCell acell = new PdfPCell();
        List<Judge> judges = theConcours.GetConcoursJudges();
        Integer jnode = aj.getJudgeNode();
        Judge theJudge = null;
        for(Judge jj : judges){
            if(jj.GetNode() == jnode){
                theJudge = jj;
                break;
            }
        }
        //System.out.println("Assigned Judge loop adding Cell number " + count + " aj node=" + aj.getJudgeNode() + " the Judge: " + theJudge.getUniqueName());
        
        String judgeFirstName = theJudge.GetFirstName();
        String judgeLastName = theJudge.GetLastName();
        Phrase phraseJudge = new Phrase();
        Chunk chunkJudgeLabel = new Chunk("Judge: ", JudgeLabelFont);
        Chunk judgeChunk = new Chunk(judgeFirstName + " " + judgeLastName);
        phraseJudge.add(chunkJudgeLabel);
        phraseJudge.add(judgeChunk);
        Paragraph pJudge = new Paragraph(phraseJudge);
        theDoc.add(pJudge);
       
         
        Phrase phraseEntriesHeader = new Phrase();
        Chunk chunkEntriesLabel = new Chunk("Entries to be judged: ", EntriesLabelFont);
        phraseEntriesHeader.add(chunkEntriesLabel);
        Paragraph pEntriesHeader =  new Paragraph(phraseEntriesHeader);
        theDoc.add(pEntriesHeader);
        for(EntryComparable e : aj.getEntries()){
            Integer timeslotindex = e.GetTimeslotIndex();
            String judgingTime = aTSIndexToTimeMap.get(timeslotindex);
            String className = e.GetClassName();
            ConcoursClass cc = theConcours.GetConcoursClassesObject().GetConcoursClassObject(className);
            String leadJudge = cc.GetClassLeadJudge().GetFirstName() + " " + cc.GetClassLeadJudge().GetLastName();
            String strOtherTeamMembers = "";
            
            boolean first = true;
            for(Judge j : cc.GetClassJudgeObjects()){
                if( !Objects.equals(j.GetNode(), aj.getJudgeNode())){
                    if(first){
                        strOtherTeamMembers =  strOtherTeamMembers + j.GetFirstName() + " " +  j.GetLastName();
                        first = false;
                    }
                    else{
                        strOtherTeamMembers =  strOtherTeamMembers + " and " +  j.GetFirstName() + " " +  j.GetLastName();
                    }
                }
            }
            
            // Construct the formatted content
            Chunk chunkTimelabel = new Chunk("Time: ", EntriesLabelFont);
            Chunk chunkTime = new Chunk(judgingTime);
            Phrase phraseTime = new Phrase();
            phraseTime.add(chunkTimelabel);
            phraseTime.add(chunkTime);
            Paragraph pTime = new Paragraph(phraseTime);
            theDoc.add(pTime);
            
            Phrase phraseDescription = new Phrase();
            Chunk chunkColor = new Chunk(e.GetColor());
            Chunk chunkYear = new Chunk(e.GetYear());
            Chunk chunkModel = new Chunk(e.GetModel());
            Chunk chunkDescriptionLabel = new Chunk("Description: ", EntriesLabelFont);
            phraseDescription.add(chunkDescriptionLabel);
            phraseDescription.add(whitespace);
            phraseDescription.add(chunkColor);
            phraseDescription.add(whitespace);
            phraseDescription.add(chunkYear);
            phraseDescription.add(whitespace);
            phraseDescription.add(chunkModel);
            Paragraph pDesc = new Paragraph(phraseDescription);
            pDesc.setIndentationLeft(20);
            theDoc.add(pDesc);

            Phrase phraseClass = new Phrase();  
            Chunk chunkClassLabel = new Chunk("Class: ", EntriesLabelFont);
            Chunk chunkClass = new Chunk(e.GetClassName());
            phraseClass.add(chunkClassLabel);
            phraseClass.add(chunkClass);
            Paragraph pClass = new Paragraph(phraseClass);
            pClass.setIndentationLeft(20);
            theDoc.add(pClass);

            Phrase phraseOwner = new Phrase();  
            Chunk chunkOwnerLabel = new Chunk("Owner: ", EntriesLabelFont);
            Chunk chunkOwner  = new Chunk(e.GetOwnerFirst() + " " + e.GetOwnerLast());
            phraseOwner.add(chunkOwnerLabel);
            phraseOwner.add(chunkOwner);
            Paragraph pOwner = new Paragraph(phraseOwner);
            pOwner.setIndentationLeft(20);
            theDoc.add(pOwner);
            
            Phrase phraseOtherTeamMembers = new Phrase();
            Chunk chunkOtherTeamMembersLabel = new Chunk("Other Team Members: ", EntriesLabelFont);
            Chunk chunkOtherTeamMembers = new Chunk(strOtherTeamMembers);
            phraseOtherTeamMembers.add(chunkOtherTeamMembersLabel);
            phraseOtherTeamMembers.add(chunkOtherTeamMembers);
            Paragraph pOtherTeamMembers = new Paragraph(phraseOtherTeamMembers);
            pOtherTeamMembers.setIndentationLeft(20);
            theDoc.add(pOtherTeamMembers);
            
            Phrase phraseLeadJudge = new Phrase();
            Chunk chunkLeadJudgeLabel = new Chunk("Lead Judge: ", EntriesLabelFont);
            Chunk chunkLeadJudge = new Chunk(leadJudge);
            phraseLeadJudge.add(chunkLeadJudgeLabel);
            phraseLeadJudge.add(chunkLeadJudge);
            Paragraph pLeadJudge = new Paragraph(phraseLeadJudge);
            pLeadJudge.setIndentationLeft(20);
            theDoc.add(pLeadJudge);
            
            System.out.println("       Time: " + judgingTime + " Entry: " + e.GetColor() + " " + e.GetYear() + " " +  e.GetModel() + " Class: " + className);
            System.out.println("            Other team members: " + strOtherTeamMembers);
        }
        theDoc.newPage();
    }
    theDoc.close();
}
}
