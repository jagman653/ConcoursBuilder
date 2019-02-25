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

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.io.FileOutputStream;
import javax.swing.JTable;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Graphics;
//import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.Rectangle;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.ConcoursClasses;
import us.efsowell.concours.lib.Judge;

/**
 *
 * @author Ed Sowell
 */
public class JTablePdfExporter {
    private static final int TITLE_FONT_SIZE = 12;
    private static final int CELL_FONT_SIZE = 6;
    private static final int HEADER_FONT_SIZE = 6;
    private static final int FOOTNOTE_FONT_SIZE = 6;
    private static String strFileName;
    private static String documentTitle;
    private static String documentSubtitle;
    private static String documentSubject;
    private static String footnote;
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 8,
            Font.NORMAL);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);
    /*
    private static Font TITLE_FONT =      new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static Font ENTRY_CELL_FONT  = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
    private static Font HEADER_CELL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8,Font.BOLD);
    private static Font FOOTNOTE_FONT   = new Font(Font.FontFamily.TIMES_ROMAN, 8,  Font.NORMAL);
    */
//5/29/
     /*   Changed 6/5/2017
    private static final Font TITLE_FONT =      new Font(Font.FontFamily.TIMES_ROMAN, TITLE_FONT_SIZE, Font.BOLD);
    private static final Font ENTRY_CELL_FONT  = new Font(Font.FontFamily.TIMES_ROMAN, CELL_FONT_SIZE, Font.NORMAL);
    private static final Font HEADER_CELL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, HEADER_FONT_SIZE, Font.BOLD);
    private static final Font FOOTNOTE_FONT   = new Font(Font.FontFamily.TIMES_ROMAN, FOOTNOTE_FONT_SIZE,  Font.NORMAL);
    
*/
    private  static Font titleFont;
    private  static Font subtitleFont;
    private static Font cellFont;
    private static Font cellFontBold;
    private static Font headerFont;
    private  static Font footnoteFont;
    private  Integer titleFontSize;
    private  Integer subtitleFontSize;
    private Integer cellFontSize;
    private Integer headerFontSize;
    private  Integer footnoteFontSize;
    private Concours theConcours;

/*
   Constructor
*/
public JTablePdfExporter(Concours aConcours, String aFileName, String aDocumentTitle, String aDocumentSubtitle, String aSubject, String aFootnote){    
    strFileName = aFileName;
    documentTitle = aDocumentTitle;
    documentSubtitle = aDocumentSubtitle;
    documentSubject = aSubject;
    footnote = aFootnote;
    titleFontSize = aConcours.GetConcoursTitleFontSize();
    subtitleFontSize = aConcours.GetConcoursSubtitleFontSize();
    cellFontSize = aConcours.GetConcoursCellFontSize();
    headerFontSize = aConcours.GetConcoursHeaderFontSize();
    footnoteFontSize = aConcours.GetConcoursFootnoteFontSize();
    
    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, titleFontSize, Font.BOLD);
    subtitleFont = new Font(Font.FontFamily.TIMES_ROMAN, subtitleFontSize, Font.BOLD);
    cellFont = new Font(Font.FontFamily.TIMES_ROMAN, cellFontSize, Font.NORMAL);
    cellFontBold = new Font(Font.FontFamily.TIMES_ROMAN, cellFontSize, Font.BOLD);
    headerFont = new Font(Font.FontFamily.TIMES_ROMAN, headerFontSize, Font.BOLD);
    footnoteFont = new Font(Font.FontFamily.TIMES_ROMAN, footnoteFontSize, Font.NORMAL);
    theConcours = aConcours;
}

public static String getFileName(){
    return strFileName;
}
public static String getDocumentTitle(){
    return documentTitle;
}
public static String getDocumentSubtitle(){
    return documentSubtitle;
}
public static String getDocumentSubject(){
    return documentSubject;
}
public static String getFootnote(){
    return footnote;
}
        

  // iText allows addition of metadata to the PDF which can be viewed in Adobe reader
    private static void addMetaData(Document document, String aTitle, String aSubject) {
        document.addTitle(aTitle);
        document.addSubject(aSubject);
        document.addKeywords("Java, PDF, iText, JTable, PDFTable");
        document.addAuthor("Ed Sowell");
        document.addCreator("Ed Sowell");
    }

    /*
       Returns an example JTable header array
    */
    
    private static String [] MakeExampleHeaderArray()  {
        String [] theHeaderArray = {"<html><p>First</p><p>name</p></html>", "<html><p>last</p><p>name</p></html>", "<html><p>Sporting</p><p>activity</p></html>", "<html><p>Years</p><p>in</p><p>performed</p></html>", "<html><p>Performed well</p></html>"};
        return theHeaderArray;
   }
    
    
    /*
       Returns an example JTable 
    */
    private static JTable MakeExampleJTable(String[] aHeaderArray)  {
        Object [][] theRowData  = new Object [5][4];        
        Object [] oa0 = {"<html><p>Mary</p><p>Alice</p></html>", "<html><p>Smith</p></html>", "<html><p>Snowboarding</p></html>", "<html><p>5</p></html>", "<html><p>false</p></html>"};
        theRowData[0] = oa0;
        Object [] oa1 = {"<html><p>John</p><p>Paul</p></html>", "<html><p>Doe</p></html>", "<html><p>Rowing</p></html>", "<html><p>10</p></html>", "<html><p>true</p></html>"};
        theRowData[1] = oa1;
        Object [] oa2 = {"<html><p>Sue</p></html>", "<html><p>Black</p></html>", "<html><p>Knitting</p></html>", "<html><p>15</p></html>", "<html><p>false</p></html>"};
        theRowData[2] = oa2 ;
        Object [] oa3 = {"<html><p>Jane</p></html>", "<html><p>White</p></html>", "<html><p>Speed reading</p></html>", "<html><p>20</p></html>", "<html><p>false</p></html>"};
        theRowData[3] = oa3 ;
        Object [] oa4 = {"<html><p>Joe</p><p>Paul</p><p>Good</p></html>", "<html><p>Doe</p><p>yyy</p></html>", "<html><p>Pool</p></html>", "<html><p>5</p></html>", "<html><p>true</p></html>"};
        theRowData[4] = oa4 ;
        
        JTable theJTable = new JTable();
        theJTable.setAutoResizeMode(AUTO_RESIZE_OFF);
        DefaultTableModel theTableModel = new javax.swing.table.DefaultTableModel(
            theRowData,
            aHeaderArray
        );
        theJTable.setModel(theTableModel);
        //DispalyJTable(theJTable);
        return theJTable;
   }

    private  void addContent(Document document, JTable aJTable, Object [] aHeaderArray, String aColumnwise) throws DocumentException {
        Paragraph titlePara = new Paragraph(getDocumentTitle(), titleFont);
        Paragraph subtitlePara = new Paragraph(getDocumentSubtitle(), subtitleFont); 
        subtitlePara.setAlignment(Element.ALIGN_CENTER);
        Paragraph footnotePara =  new Paragraph(getFootnote(), footnoteFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        subtitlePara.setAlignment(Element.ALIGN_CENTER);
        footnotePara.setAlignment(Element.ALIGN_CENTER);

        Paragraph subPara = new Paragraph("", subFont);
        document.add(titlePara);
        document.add(subtitlePara);
        // We have to extract rowData & header from the JTable 
        int numRows = aJTable.getRowCount();
        int numCols = aJTable.getColumnCount();
        Object [][] theRowData =  new Object [numRows][numCols]; 
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numCols; j++){
                theRowData[i][j] = aJTable.getModel().getValueAt(i, j);
            }
        }
        createPDFTable(theRowData, aHeaderArray,  subPara, aColumnwise);
        document.add(subPara);
        document.add(footnotePara);
   }

    
    /*
        The argument header and rowData cells are in HTML because that allows multiline cells in JTable.
        Since PdfPTable has different syntax for multiple lines we have to parse the JTable cell data
        and use Paragraph & Chunk.NEWLINE to build the PdfPTable
    */
    private  void createPDFTable(Object[][] theRowData, Object [] theHeader, Paragraph subCatPart, String aColumwise) throws BadElementException {

        System.out.println("In createPDFTable().  aColumwise = " + aColumwise);
        
        PdfPTable table = new PdfPTable(theHeader.length);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        PdfPCell c1;
        int numRows = theRowData.length;
        int numCols = theHeader.length;
        //String cellContent;
        Object objCellContent;
        Paragraph paragraph;
        Phrase phrase;
        Map<Integer,String> mapIdxToJudgeName = null;
        if(aColumwise.contains("by Judge")){
            mapIdxToJudgeName = new HashMap<>();
        }
        
        //
        // Translate header cells
        //
        org.jsoup.nodes.Document parsedCellContents;
        for(int j = 0; j< numCols; j++){
            objCellContent = theHeader[j];
            parsedCellContents = org.jsoup.Jsoup.parse(objCellContent.toString()); // cellContent is html 
            org.jsoup.select.Elements cellLines  =  parsedCellContents.select("p"); 
            phrase = new Phrase();
            phrase.setFont(headerFont);

            for(org.jsoup.nodes.Element  e : cellLines){
               paragraph = new Paragraph();
               if(aColumwise.contains("by Judge")){
                   mapIdxToJudgeName.put(j, e.text());
               }
               paragraph.add(e.text());
               phrase.add(paragraph) ;
               phrase.add(Chunk.NEWLINE);
            }
            c1 = new PdfPCell(phrase);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
        }
        table.setHeaderRows(1);
        //
        // Translate rowData cells
        //
         System.out.println("numRows: " + numRows + " numCols: " + numCols);
        for(int i = 0; i < numRows; i++){
           for(int j = 0; j<numCols; j++) {
            objCellContent = theRowData[i][j];
            System.out.println("Row: " + i + " Col: " + j + " Cell content: " + objCellContent);
            phrase = new Phrase();
            phrase.setFont(cellFont); 
            
            if(objCellContent != null){
                parsedCellContents = org.jsoup.Jsoup.parse(objCellContent.toString()); // cellContent is html 
                org.jsoup.select.Elements cellLines  =  parsedCellContents.select("p"); 
                for(org.jsoup.nodes.Element  e : cellLines){
                   paragraph = new Paragraph(); // if(paragraph.contains("E:"))... Entry string; WORKING ON MAKING ENTRY BOLD IF THE JUDGE IN COLUMN IS LEAD FOR THE ENTRY JCNA CLASS
                   paragraph.add(e.text());
                   if(aColumwise.contains("by Judge")){
                        String strClass = null;
                        ConcoursClass cc = null;
                        Judge judge = null;
                        if(paragraph.toString().contains("E:")){
                            int posColon = paragraph.toString().indexOf(":");
                            int posDash = paragraph.toString().indexOf("-");
                            strClass = paragraph.toString().substring(posColon+1, posDash);
                            cc = theConcours.GetConcoursClassesObject().GetConcoursClassObject(strClass);
                            if(cc == null){
                                System.out.println(" Class " + strClass + " is null");
                            }
                            judge = cc.GetClassLeadJudge();
                            if(judge.getUniqueName().equals(mapIdxToJudgeName.get(j))){
                                phrase.setFont(cellFontBold);
                            }
                        }
                   }
                   phrase.add(paragraph) ;
                   phrase.add(Chunk.NEWLINE);
                }
            } else{
                phrase.add("");
            }
            c1 = new PdfPCell(phrase);
             table.addCell(c1);
        } // end j loop
       } // end i loop
        
       table.setWidthPercentage(95);
        subCatPart.add(table);

    }
    
    public static ArrayList<String> getHeaderList(JTable aJTable){
         ArrayList<String> headerlist = new ArrayList<>();
        int numCols = aJTable.getColumnModel().getColumnCount();
        for(int j = 0; j < numCols; j++){
            headerlist.add((String)aJTable.getColumnModel().getColumn(j).getHeaderValue());
        }
         return headerlist; 
    }
    
    
    
    public static void DispalyJTable(JTable aJTable) {
        JScrollPane scroll1 = new JScrollPane(aJTable);

        aJTable.setPreferredScrollableViewportSize(aJTable.getPreferredSize());

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(scroll1, BorderLayout.CENTER);

        // without having been shown, fake an all-ready
        panel1.addNotify();

        // manually size to pref
        panel1.setSize(panel1.getPreferredSize());

        // validate to force recursive doLayout of children
        panel1.validate();

        BufferedImage bi = new BufferedImage(panel1.getWidth(), panel1.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics g = bi.createGraphics();
        panel1.paint(g);
        g.dispose();

        JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bi)));
    }
    
    /*
       This is the executive, dispatchinbg the tasks. addContent does the major work
    */
    public void ExportPdfFile(JTable aJTable, Object [] aHeaderArray, int  aPageSize, String aColumwise ){
        

            com.itextpdf.text.Document document;
            switch(aPageSize){
                case 0: // letter
                   // document = new Document(com.itextpdf.text.PageSize.LETTER);
                    
                    document = new Document(new com.itextpdf.text.Rectangle(0f,0f, 612f, 792f));
                    break;
                    
                case 1: //letter landscape
                    document = new Document(new com.itextpdf.text.Rectangle(0f,0f, 792f, 612f));
                    break;
                    
                case 2:  // legal
                     document = new Document(new com.itextpdf.text.Rectangle(0f,0f, 612f, 1008f));
                    break;
                    
                case 3: // legal landscape
                    document = new Document(new com.itextpdf.text.Rectangle(0f,0f, 1008f, 612f));
                    break;
                    
                default:
                    //document = new Document(com.itextpdf.text.PageSize.LETTER);
                    document = new Document(new com.itextpdf.text.Rectangle(0f,0f, 792f, 612f));
                    break;
                    
            }
            OutputStream theFile = null;
        try {
            theFile = new FileOutputStream(new File(strFileName));
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(JTablePdfExporter.class.getName()).log(Level.SEVERE, null, ex);
            String msg = "Could not create " + strFileName + " in ExportPdfFile, possibly because it's open in another program.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        } catch (NullPointerException ex) {
            // Added 9/19/2018
            String msg = "Null Pointer. Could not create " + strFileName + " in ExportPdfFile, possibly because it's open in another program.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        try {
            PdfWriter.getInstance(document, theFile);
        } catch (DocumentException ex) {
            //Logger.getLogger(JTablePdfExporter.class.getName()).log(Level.SEVERE, null, ex);
            String msg = "Could not create " + strFileName + " ExportPdfFile, possibly because it's  open in another program.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        } catch (NullPointerException ex) {
            // Added 9/19/2018
            String msg = "Null Pointer. Could not create " + strFileName + " in ExportPdfFile, possibly because it's open in another program.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }

        document.open();
        addMetaData(document, documentTitle, documentSubject);
        try {
            addContent(document, aJTable, aHeaderArray, aColumwise);
        } catch (DocumentException ex) {
            //Logger.getLogger(JTablePdfExporter.class.getName()).log(Level.SEVERE, null, ex);
            String msg = "Could not create " + strFileName + " ExportPdfFile, possibly because it's  open in another program.\n\nClose PDF viewer.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        } catch (NullPointerException ex) {
            // Added 9/19/2018
            String msg = "Null Pointer. Could not create " + strFileName + " in ExportPdfFile, possibly because it's open in another program.";
            okDialog(msg);
            theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }

        document.close();
    }
            
        
    

   /* public static void main(String[] args) {
        try {
            JTable theJTable = new JTable();
            String[] theHeaderArray = MakeExampleHeaderArray();
            theJTable = MakeExampleJTable(theHeaderArray);
            JTablePdfExporter  jtablepdfexporter= new JTablePdfExporter(".\\JTablePdfExporterExamplePdf.pdf", "Example", "JTablePdfExporter", "This is a footnote");
            jtablepdfexporter.ExportPdfFile(theJTable, theHeaderArray, 3);// 0 = LETTER, 1 = LETTER_LANDSCAPE,  2 = LEGAL, 3 = LEGAL_LANDSCAPE
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
}
 