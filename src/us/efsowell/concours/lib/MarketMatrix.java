/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import static JCNAConcours.AddConcoursEntryDialog.okDialog;


/**
/**
 * Market Matrix is an all-integer representation of graphs used for interface data for the Matching and Scheduling program
 * from Mahantesh Halappanavar
 *
 *
 * @author Ed Sowell
 */
public class MarketMatrix {
    String strFirstLine;
    List<String>  strDataColumnHeaders;
    List<String>  strCountRowElementNames;
    List<Integer> intCountsRow;
    List<MarketMatrixRow> rows;
// Constructor    
public  MarketMatrix(String aFirstLine, List<String> aDataColumnHeaders, List<String> aCountRowElementNames, List<Integer> aCountsRow){
    strFirstLine = aFirstLine; 
    strDataColumnHeaders = aDataColumnHeaders;
    strCountRowElementNames = aCountRowElementNames;
    intCountsRow = aCountsRow;
    rows = new ArrayList();
    
}
public void addRow(MarketMatrixRow aRow){
    rows.add(aRow);
}
public List<MarketMatrixRow> getMMRows() {
        return rows;
}

public void SetCountsRow(List<Integer> aCountsRow){
    intCountsRow = aCountsRow;
}

public void writeMarketMatrixFile(String aFolderPath, String aFilename, boolean includeHeaders){

        Path MMFile = Paths.get(aFolderPath, aFilename);
        Charset charset = Charset.forName("UTF-8");
        ArrayList<String> lines = new ArrayList<>();
        int i;
        lines.add(strFirstLine);
        
        if(includeHeaders){
            // Optional header line to show what the count row elements mean
            String countRowElementNames = ""; // Never used... to keep compiler happy;
            i = 0;
            for(String strCountRowElementName : strCountRowElementNames){
                if(i == 0){
                    countRowElementNames = "%% " + strCountRowElementName; // comment
                } else{
                    countRowElementNames = countRowElementNames + " " + strCountRowElementName;
                }
                i++;
            }   
           lines.add(countRowElementNames); 
           
            // Optional header line to show what the columns mean
            String headerLine = ""; // Never used... to keep compiler happy;
            i = 0;
            for(String strHeaderElement : strDataColumnHeaders){
                if(i == 0){
                    headerLine = "%% " + strHeaderElement; // comment
                } else{
                    headerLine = headerLine + " " + strHeaderElement;
                }
                i++;
            }   
           lines.add(headerLine); 
        
        }
        // Counts line
        String strCountsLine = ""; // Never used... to keep compiler happy
        i = 0;
        for(Integer intCountsElement : intCountsRow){
            if(i == 0){
                strCountsLine = intCountsElement.toString();
            } else{
                strCountsLine = strCountsLine + " " + intCountsElement.toString();
            }
            i++;
        }   
        lines.add(strCountsLine); 
        // All the data rows
        int k = 0;
        for(MarketMatrixRow aRow : rows){
            k++;
            //System.out.println("k = " + k + " Row: " + aRow);
            lines.add(aRow.toString());
        }

        try {
            if(Files.exists(MMFile)){
                Files.delete(MMFile);
            }
            // Write everything in onw fell swoop
            Files.write(MMFile, lines, charset, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            System.err.println(e);
        }
    
    
}
    
}
