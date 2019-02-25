/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editJA;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author jag_m
 */
public class AveryLabels5395 {
    PdfPTable theTable;
    // Constructor
    AveryLabels5395(Document aDoc, List<PdfPCell> aLabelContents, String aOutputFilePathStr) throws DocumentException, FileNotFoundException{
        // careful measurement of actual Avery form 
        int numCols = 2;
        float pageLm = 49.5f; //11/16" *72 = 49.5 pt
        float pageRm = 49.5f;
        float pageTm = 36f; // 1/2" *72 = 36 pt
        float pageBm = 36f;
        float tableWidthInches = (8.5f - 2f*11f/16f);
        float tableWidthPts = tableWidthInches*72f;
        float cellWidthPts = tableWidthPts/(float)numCols;
        aDoc.setMargins(pageRm, pageRm, pageTm, pageBm);
        theTable = new PdfPTable(numCols);
        float[] cellWidthsPts = {cellWidthPts, cellWidthPts}; 
        theTable.setWidths(cellWidthsPts);        
        theTable.setTotalWidth(tableWidthPts); 
        theTable.setLockedWidth(true); // Without this the table width won't become effective!
        PdfWriter.getInstance(aDoc, new FileOutputStream(aOutputFilePathStr));
        aDoc.open();
        float cellHeightPts =  180f; // (11 - 1/2 -1/2)/4 = 2.5" = 180 pt
        float availableTableSpace = 11f*72f - pageTm - pageBm;
        if(4*cellHeightPts  > availableTableSpace){
            String msg = "Space between top & bottom margins (" + availableTableSpace + ") is smaller than 4 * cell height. \n To avoid spillover of last row, decrease margins.";
            okDialog(msg);
        }
        int cellcount = aLabelContents.size();
        int idx = 0;
        for(PdfPCell cell : aLabelContents){
            //
            // The lable cutout (the Badge) fits into the table cell with clearance on all sides. Well, really on just 3 sides
            // because the left badge side is smack up against the left cell boundry and the right badge side is smack up against
            // the right cell boundry. We will call these paddings badgePaddingTop, badgePaddingBottom, badgePaddingLeft, & badgePaddingRight.
            //
            // Also, the badge text  needs to have clearance all around relative to the badge cutout edge. We will call this textPaddingTop,
            // textPaddingBottom, textPaddingLeft, & textPaddingRight. Finally, we we define totalPaddingTop,
            // totalPaddingBottom, totalPaddingLeft, & totalPaddingRight as the sum of badge and text padding relative to the cell edges.
            // 
            float badgeHeightPts = 2.333f*72f; 
            float badgeWidthPts = 3.375f*72f;
            float badgePaddingTop = (cellHeightPts - badgeHeightPts)/2f;
            float badgePaddingBottom = badgePaddingTop;
            
            
            float badgePaddingLeftInLeftCell = 0f;
            float badgePaddingRightInRightCell = 0f;
            float badgePaddingLeftInRightCell = cellWidthPts - badgeWidthPts; // cell width - badge width
            float badgePaddingRightInLeftCell = badgePaddingLeftInRightCell;
            
            float textPaddingTop = 18f; // 1/2"
            float textPaddingBottom = 18f;
            float textPaddingLeft = 18f;
            float textPaddingRight = 18f;

            
            float totalPaddingTop = badgePaddingTop + textPaddingTop;
            float totalPaddingBottom = badgePaddingBottom + textPaddingBottom;
            float totalPaddingLeftInLeftCell = badgePaddingLeftInLeftCell + textPaddingLeft;
            float totalPaddingRightInRightCell = badgePaddingRightInRightCell + textPaddingRight;
            float totalPaddingLeftInRightCell = badgePaddingLeftInRightCell + textPaddingLeft;
            float totalPaddingRightInLeftCell = badgePaddingRightInLeftCell + textPaddingRight;
            
            //cell.setBorderColor(BaseColor.RED);
            cell.setFixedHeight(cellHeightPts);
            //cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingTop(totalPaddingTop); 
            cell.setPaddingBottom(totalPaddingBottom);
            // Position of the Badge cutout in the cell:
            // The Badge cutout (rectangle with rounded corners) is positioned in the LEFT table cells with zero LEFT margin.
            // In the RIGHT table cells, the cutout is positioned with zero RIGHT margin.
            // 
            if(idx%2 == 0){
                // We are in the Left cell
                cell.setPaddingLeft(totalPaddingLeftInLeftCell);
                cell.setPaddingRight(totalPaddingRightInLeftCell);
             } else {
                // We are in Right cell
                cell.setPaddingLeft(totalPaddingLeftInRightCell);
                cell.setPaddingRight(totalPaddingRightInRightCell);
            }
            theTable.addCell(cell);
            idx++;
        }
        int numFill = cellcount%numCols;
        for(int k = 0; k < numFill; k++){
            PdfPCell cell = new PdfPCell(new Phrase("   "));
            theTable.addCell(cell);
        }
        //okDialog(cellcount + " cells added to table");
        aDoc.add(theTable);
        aDoc.close();
    }

    public static void okDialog(String theMessage) {
        JOptionPane.showMessageDialog(null, theMessage);
    }

    public static void main(String[] args) {
        try {
            Document theDoc = new Document(PageSize.LETTER);
            // Avery 5395 Name Badges
            int numLabelSheet = 18;
            List<PdfPCell> labelContents = new ArrayList<>();
            Chunk whitespace = new Chunk("  ");
            int rowLabelSheet = 0;
            int colLabelSheet;
            for(int i = 1; i <= numLabelSheet; i++){
                if((i%2) != 0){
                    rowLabelSheet++;
                    colLabelSheet = 1;
                } else {
                    colLabelSheet = 2;
                }
                Phrase phrase1 = new Phrase();
                phrase1.add("Entrant " + rowLabelSheet + "." + colLabelSheet );
                phrase1.add(whitespace);
                phrase1.add("Entry " + rowLabelSheet + "." + colLabelSheet);
                Paragraph p1 = new Paragraph(phrase1);
                //Paragraph p1 = new Paragraph("Entrant " + rowLabelSheet + "." + colLabelSheet + "               " + "Entry " + rowLabelSheet + "." + colLabelSheet);
                Phrase phrase2 = new Phrase();
                phrase2.add("Model " + rowLabelSheet + "." + colLabelSheet );
                phrase2.add(whitespace);
                phrase2.add("Class " + rowLabelSheet + "." + colLabelSheet);
                Paragraph p2 = new Paragraph(phrase2);
                //Paragraph p2 = new Paragraph("Model " + rowLabelSheet + "." + colLabelSheet + "               " + "Class " + rowLabelSheet + "." + colLabelSheet);
                Phrase phrase3 = new Phrase();
                phrase3.add("Time Slot " + rowLabelSheet + "." + colLabelSheet );
                phrase3.add(whitespace);
                phrase3.add("Team " + rowLabelSheet + "." + colLabelSheet);
                Paragraph p3 = new Paragraph(phrase3);
                p1.setSpacingAfter(18);
                p2.setSpacingAfter(18);
                PdfPCell acell = new PdfPCell();
                acell.addElement(p1);
                acell.addElement(p2);
                acell.addElement(p3);
                labelContents.add(acell);
            }
            AveryLabels5395 theLabels = new AveryLabels5395(theDoc, labelContents, "MyTestAvery5395.pdf");
        } catch (DocumentException ex) {
            Logger.getLogger(AveryLabels5395.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AveryLabels5395.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
