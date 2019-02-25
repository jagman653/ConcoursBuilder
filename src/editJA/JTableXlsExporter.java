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
import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
//import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import javax.swing.table.TableModel;
//import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi. xssf.usermodel.XSSFWorkbook;
//import org.apache.commons.collections4.ListUtils;


import us.efsowell.concours.lib.Concours;

/**
 *
 * @author Ed Sowell
 */
public class JTableXlsExporter {

    private Concours theConcours;
    private static String strFileName;
    private static String strTableName;

 //  Constructor
public JTableXlsExporter(Concours aConcours, String aFileName, String aTableName){    
    strFileName = aFileName;
    theConcours = aConcours;
    strTableName = aTableName;
}
// Constructor
public JTableXlsExporter(Concours aConcours){    
    theConcours = aConcours;
}

public static String getFileName(){
    return strFileName;
}

public static void setFileName(String aFileName){
    strFileName = aFileName;
}

public static void setTableName(String aTableName){
     strTableName = aTableName;
}
        
    private static String [] MakeExampleHeaderArray()  {
        String [] theHeaderArray = {"<html><p>First</p><p>name</p></html>", "<html><p>Last</p><p>name</p></html>", "<html><p>Sporting</p><p>activity</p></html>", "<html><p>Years</p><p>in</p><p>sport</p></html>", "<html><p>Performed well</p></html>"};
        return theHeaderArray;
   }
    
    
    /*
       Returns an example JTable 
    */
    private static JTable MakeExampleJTable(String[] aHeaderArray)  {
        Object [][] theRowData  = new Object [5][4];  // 
        // Row 0
        Object [] oa0 = {"<html><p>Mary</p><p>Alice</p></html>", "<html><p>Smith</p></html>", "<html><p>Snowboarding</p></html>", "<html><p>5</p></html>", "<html><p>false</p></html>"};
        theRowData[0] = oa0;
        // Row 1
        Object [] oa1 = {"<html><p>John</p><p>Paul</p></html>", "<html><p>Doe</p></html>", "<html><p>Rowing</p></html>", "<html><p>10</p></html>", "<html><p>true</p></html>"};
        theRowData[1] = oa1;
        // Row 2
        Object [] oa2 = {"<html><p>Sue</p><p>Marie</p></html>", "<html><p>Black</p></html>", "<html><p>Knitting</p></html>", "<html><p>15</p></html>", "<html><p>false</p></html>"};
        theRowData[2] = oa2 ;
        // Row 3
        Object [] oa3 = {"<html><p>Jane</p></html>", "<html><p>White</p></html>", "<html><p>Speed</p><p>Reading</p></html>", "<html><p>20</p></html>", "<html><p>false</p></html>"};
        theRowData[3] = oa3 ;
        // Row 4
        Object [] oa4 = {"<html><p>Joseph</p><p>Henry</p><p>Francis</p></html>", "<html><p>Ericson</p></html>", "<html><p>Football</p><p>Quarterback</p></html>", "<html><p>5</p></html>", "<html><p>true</p></html>"};
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
 

     /*
    Note: aNumRows is the number of rows in the aRowData... doesn't include the Header row.
          aNumCols INCLUDES the left-column with timeslot intervals
    */      

public static void writeNewWorksheetToExcel(Logger aLogger, Workbook wb, JTable aJTable, Object [] aHeaderArray, Path path) throws FileNotFoundException, IOException {
    Sheet sheet = wb.createSheet(); //WorkSheet
    //sheet.getSheetName()
//  The CellStyle changes doesn't do anything!!    
//    CellStyle cellStyle = wb.createCellStyle();
//    cellStyle.setWrapText(true);
//    for(int j = 0; j < aNumCols; j++){ //For each column
//        sheet.setDefaultColumnStyle(j, cellStyle);
//    }
    TableModel theModel = aJTable.getModel();
    int numRows = theModel.getRowCount();
    int numCols = theModel.getColumnCount();

    Object [][] theRowData =  new Object [numRows][numCols]; 
    for(int i = 0; i < numRows; i++){
        for(int j = 0; j < numCols; j++){
            Object value = theModel.getValueAt(i, j);
            if(value != null)
                theRowData[i][j] = value;
            else
                theRowData[i][j] = "";
        }
    }
    Row headerRow = sheet.createRow(0); //Create reader row at line 0
    aLogger.info("Translate header row");
    for(int j = 0; j < numCols; j++){ //For each column
            String translatedCellContents;            
            Object tableCellValue = aHeaderArray[j];//theModel.getValueAt(i, j);
            if( tableCellValue != null){
                translatedCellContents = (String)translateCellContents(aLogger, tableCellValue);
            } else{
                String msg = "writeToExcel: aHeaderArray[" + j + "] is null. Setting it to empty string";
                aLogger.info(msg);
                translatedCellContents = "" ;
            }
        headerRow.createCell(j).setCellValue(translatedCellContents);                              //   WORKING HERE
    }

    Row row = sheet.createRow(1); //First RowData Row created at line 1
    aLogger.info("Translate table rows");
    for(int i = 0; i < numRows; i++){ //For each table row
        for(int j = 0; j < numCols; j++){ //For each table column
            //String cellValue;
            String translatedCellContents;            
            Object tableCellValue = theRowData[i][j];//theModel.getValueAt(i, j);
            if( tableCellValue != null){
                translatedCellContents = (String)translateCellContents(aLogger, tableCellValue);
            } else{
                String msg = "writeToExcel: aRowData[" + i + "][" + j + "] is null. Setting it to empty string";
                aLogger.info(msg);
                translatedCellContents = "" ;
            }
            
            row.createCell(j).setCellValue(translatedCellContents); 
        }
        //Set the row to the next one in the sequence 
        row = sheet.createRow((i + 2)); // the 2 accounts for the i = 0 row goes in the the Excel row 2
    }
}    
    
    //public static void writeToExcel(Logger aLogger, JTable aJTable, Object [] aHeaderArray, Path path) throws FileNotFoundException, IOException {
    public static void writeToExcel(Logger aLogger, JTable aByJudgeTable, Object [] aByJudgeHeaderArray, JTable aByClassTable, Object [] aByClassHeaderArray, JTable aCompressedTable, Object [] aCompressedHeaderArray, Path path) throws FileNotFoundException, IOException {
    String msg2 = "Starting writeToExcel. Will be written to: " + path;
    aLogger.info(msg2);
    //okDialog(msg2);
    new WorkbookFactory();
    Workbook wb = new XSSFWorkbook(); //Excel workbook
    
    // by Judge
    writeNewWorksheetToExcel(aLogger, wb, aByJudgeTable,  aByJudgeHeaderArray,  path); 
    wb.setSheetName(0, "By Judge");
    msg2 = "Finished adding By Judge table";
    aLogger.info(msg2);
    
    // By Class
    writeNewWorksheetToExcel(aLogger, wb, aByClassTable,  aByClassHeaderArray,  path);
    wb.setSheetName(1, "By Class");
    
    msg2 = "Finished adding By Class table";
    aLogger.info(msg2);

    // Compressed
    writeNewWorksheetToExcel(aLogger, wb, aCompressedTable,  aCompressedHeaderArray,  path); 
    wb.setSheetName(2, "Compressed");

    msg2 = "Finished adding Compressed table";
    aLogger.info(msg2);
    
    String strPath = path.toString();
    FileOutputStream out = new FileOutputStream(strPath);
    
    wb.write(out);//Save the file 
    out.close();
    aLogger.info("Finished writeToExcel");
}      
public static String translateCellContents(Logger aLogger, Object aContents){
    String multiLine = "";
    org.jsoup.nodes.Document parsedCellContents;
    aLogger.info("Cell contents: " + aContents.toString());
    parsedCellContents = org.jsoup.Jsoup.parse(aContents.toString()); // cellContent is html 
    org.jsoup.select.Elements cellLines  =  parsedCellContents.select("p"); 
    multiLine = "";
    int k = 0;
    String eText = "";
    for(org.jsoup.nodes.Element  e : cellLines){
        eText = e.text();
        if(k>0){
             multiLine =  multiLine + "\n" + eText;
        } else {
            multiLine = eText;
        }
        k++;
    }
    return multiLine;
}
 /*
public static String[] translateHeaderRow(int aNumCols, Object [] headerRow)    {
    String[] result; 
    result = new String[aNumCols];
    Object objCellContent;
    org.jsoup.nodes.Document parsedCellContents;
    String multiLine;
    for(int j = 0; j< aNumCols; j++){
        objCellContent = headerRow[j];
        parsedCellContents = org.jsoup.Jsoup.parse(objCellContent.toString()); // cellContent is html 
        org.jsoup.select.Elements cellLines  =  parsedCellContents.select("p"); 
        multiLine = "";
        int k = 0;
        String eText = "";
        for(org.jsoup.nodes.Element  e : cellLines){
            eText = e.text();
            if(k>0){
                 multiLine =  multiLine + "\n" + eText;
            } else {
                multiLine = eText;
            }
            k++;
        }
        result[j] = multiLine;
    }
    return result;
}*/

/*
public static Object[][] translateRowData(int aNumRows, int aNumCols, JTable aTable)    {
    Object[][] result; 
    result = new Object[aNumRows][aNumCols]; 
    Object objCellContent;
    org.jsoup.nodes.Document parsedCellContents;
    String multiLine;
        for(int i = 0; i < aNumRows; i++){
            for(int j = 0; j < aNumCols; j++){
                // Translate Row data cells, inserting newlines instead of HTML <p>.. <\p>
                String strCellContent;
                objCellContent = aTable.getModel().getValueAt(i, j);
                if(objCellContent != null){
                    strCellContent = objCellContent.toString();
                    
                } else {
                    strCellContent = "";
                }
                System.out.println( "i = " + i + " j = " + j + " cell content: " +  strCellContent);
                parsedCellContents = org.jsoup.Jsoup.parse(strCellContent); // cellContent is html 
                org.jsoup.select.Elements cellLines  =  parsedCellContents.select("p"); 
                multiLine = "";
                int k = 0;
                for(org.jsoup.nodes.Element  e : cellLines){
                    if(k==0){
                        multiLine = e.text();
                    } else {
                        multiLine =  multiLine + "\n" + e.text();
                    }
                    k++;
                }
                result[i][j] = multiLine;
            }
        }
    return result;
}
*/

public static void main(String[] args)  {
    String [] headerRow = MakeExampleHeaderArray();    
    JTable  jtable = MakeExampleJTable(headerRow)     ;
    String strPath = "C:\\Users\\jag_m\\Documents\\NetBeansProjects\\EditJudgeAssignments\\JavaGenerated.xlsx";
       File f = new File(strPath);
       boolean itExists = false;
       if(f.exists()){
           itExists = true;
           int response = yesNoDialog("There is an existing Excel file " + strPath + ". Do you wish to overwrite it?");
           if(response == JOptionPane.NO_OPTION){
               return;
           } 
       }
       // Either it doesn't exist, or it does exist but the user wishes to overwrite it
       if(itExists){
           f.delete();
       }     
       try {
           // for convenience, write the same table to each worksheet
            writeToExcel(Logger.getLogger("aLoggerLog"), jtable, headerRow, jtable, headerRow, jtable, headerRow,  Paths.get(strPath));
       } catch (IOException ex) {
           String msg = "Could not write to " + strPath + " Either it doesn't exist or is in use by another application, e.g., Excel. Close it and retry";
           okDialog(msg);
           Logger.getLogger(JTableXlsExporter.class.getName()).log(Level.INFO, msg, ex);
       }

       
}    


}
 