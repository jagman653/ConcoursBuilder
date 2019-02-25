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

/**
 *
 * @author  Michael McCaskill
 */
//This is free for anyone's use/modification. I'd like to receive credit if it gets incorporated into the official itext though. 
//Copyright 2005. Michael McCaskill ([hidden email]) 
import com.itextpdf.text.BadElementException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that writes a <code>PdfPTable</code>, and spans it across multiple
 * pages if the columns won't fit on one page
 */


public class PdfPTableWriter {

    //Instance variables 
    private static PdfPTable table;
    private static PdfWriter writer;
    private static Document document;

    //List of how many columns per horizontal page 
    private List numberOfColumnsPerPage;

    //List of how many rows per vertical page 
    private List numberOfRowsPerPage;

    //Offsets if given 
    private float widthOffset = 0;
    private float heightOffset = 0;

    /**
     * Class Constructor
     */
    public PdfPTableWriter(Document document, PdfWriter writer, PdfPTable table) {
        this.document = document;
        this.writer = writer;
        this.table = table;
        calculateColumns();
        calculateRows();
    }

    /**
     * Writes the table to the document
     */
    public void writeTable() throws DocumentException {
        //Begin at row 1 (row after the header) 
        int rowBegin = 1;
        int rowEnd = 0;
        //Note the size of numberOfRowsPerPage is how many vertical 
        //pages there are. 
        Iterator rowsIter = numberOfRowsPerPage.iterator();
        while (rowsIter.hasNext()) {
            rowEnd = ((Integer) rowsIter.next()).intValue();
            writeSelectedRows(rowBegin, rowEnd);
            rowBegin = rowEnd;
        }
    }

    /**
     * Prints the Reports columns (splitting horizontally if necessary) and
     * subsequent rows
     *
     * @param rowBegin
     * @param rowEnd
     * @throws DocumentException
     */
    private void writeSelectedRows(int rowBegin, int rowEnd) throws DocumentException {
        int colBegin = 0;
        int colEnd = 0;
        float pageHeight = document.getPageSize().getHeight() - heightOffset;
        PdfContentByte contentByte = writer.getDirectContent();
        Iterator columnsIter = numberOfColumnsPerPage.iterator();
        while (columnsIter.hasNext()) {
            colEnd = colBegin + ((Integer) columnsIter.next()).intValue();
            //Writer table header 
            writeSelectedRows(colBegin, colEnd, 0, 1, widthOffset, pageHeight);
            //Writes selected rows to the document 
            writeSelectedRows(colBegin, colEnd, rowBegin, rowEnd, widthOffset, pageHeight - table.getHeaderHeight());
            //Add a new page 
            document.newPage();
            colBegin = colEnd;
        }
    }

    public int getTotalPages() {
        return numberOfColumnsPerPage.size() * numberOfRowsPerPage.size();
    }

    public void setHeightOffset(float heightOffset) {
        this.heightOffset = heightOffset;
    }

    public void setWidthOffset(float widthOffset) {
        this.widthOffset = widthOffset;
    }

    private void writeSelectedRows(int colBegin, int colEnd, int rowBegin,
            int rowEnd, float x, float y) {
        PdfContentByte cb = writer.getDirectContent();
        table.writeSelectedRows(colBegin, colEnd, rowBegin, rowEnd, x, y, cb);
    }

    private void calculateColumns() {
        numberOfColumnsPerPage = new ArrayList();
        float pageWidth = document.getPageSize().getWidth() - widthOffset;
        float[] widths = table.getAbsoluteWidths();
        float tot_width = table.getTotalWidth();
        if (table.getTotalWidth() > pageWidth) {
            //tmp variable for amount of total width thus far 
            float tmp = 0f;
            //How many columns for this page 
            int columnCount = 0;
            //Current page we're on 
            int currentPage = 0;
            //Iterate through the column widths 
            for (int i = 0; i < widths.length; i++) {
                //Add to the temporary total 
                tmp += widths[i];
                //If this column will not fit on the page 
                if (tmp > pageWidth) {
                    //Add the current column count to this page 
                    numberOfColumnsPerPage.add(new Integer(columnCount));
                    //Since this column won't fit, the tmp variable should start off the next iteration 
                    //as this column's width 
                    tmp = widths[i];
                    //Set column count to 1, since we have moved this column to the next page 
                    columnCount = 1;
                } //If this is will fit on the page 
                else {
                    //Increase the column count 
                    columnCount++;
                }
            }
            //Save the remaining columns 
            numberOfColumnsPerPage.add(new Integer(columnCount));
        } //All the columns will fit on one horizontal page 
        //Note: -1 means all the columns 
        else {
            numberOfColumnsPerPage.add(new Integer(-1));
        }
    }

    private void calculateRows() {
        numberOfRowsPerPage = new ArrayList();
        float pageHeight = document.getPageSize().getHeight() - heightOffset - table.getHeaderHeight();
        //If the table won't fit on the first page 
        if (table.getTotalHeight() > pageHeight
                - table.getHeaderHeight()) {
            //Temp variables 
            float tmp = 0f;
            //Determine the start and end rows for each page 
            for (int i = 1; i < table.size(); i++) {
                //Add this row's height to the tmp total         
                tmp += table.getRowHeight(i);
                if (tmp > pageHeight) {
                    //This row won't fit so end at previous row 
                    numberOfRowsPerPage.add(new Integer(i - 1));
            //Since this row won't fit, the tmp variable should start off the next iteration 
                    //as this row's height 
                    tmp = table.getRowHeight(i);
                }
            }
            //Last page always ends on totalRows 
            numberOfRowsPerPage.add(new Integer(table.size()));
        } //All the rows will fit on one vertical page 
        //Note: -1 means all the rows 
        else {
            numberOfRowsPerPage.add(new Integer(-1));
        }
    }
    
    /*
                    THIS IS ONLY FOR THE EXAMPLE USAGE IN MAIN()
    */
 private static PdfPTable createTable(int numRows, int numCols  ) throws BadElementException {
        PdfPTable table = new PdfPTable(numCols);

        PdfPCell cell;
        /*for(int j = 0; j < numCols; j++){
            cell = new PdfPCell(new Phrase("Column " + j));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        table.setHeaderRows(1);
*/
        table.setHeaderRows(0);
        for(int i=0; i< numRows; i++){
            for(int j = 0; j < numCols; j++){
               table.addCell("Cell contents for row " +  i + " col " + j); 
            }
        }

        return table;
    }
    
    public  static void main(String[] args)   {
        try {
            OutputStream theFile = null;
            PdfWriter myWriter = null;
            int numRows = 10; 
            int numCols = 2; //30;
            PdfPTable MyTable = createTable(numRows, numCols);
            MyTable.setTotalWidth(numCols*72f);
            Document myDocument  = new Document();
 
            try {
                theFile = new FileOutputStream(new File("MyTable.pdf"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PdfPTableWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                myWriter = PdfWriter.getInstance(myDocument, theFile);
                myDocument.open();
            } catch (DocumentException ex) {
                Logger.getLogger(PdfPTableWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
            PdfPTableWriter theMultipagePdfPTableWriter =  new  PdfPTableWriter(myDocument,  myWriter, MyTable);
            try {
                theMultipagePdfPTableWriter.writeTable();
            } catch (DocumentException ex) {
                Logger.getLogger(PdfPTableWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
            myDocument.close();
        } catch (BadElementException ex) {
            Logger.getLogger(PdfPTableWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
