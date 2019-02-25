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
package JCNAConcours;

//import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
//import us.efsowell.concours.lib.SchedulingInterfaceCpp;

/**
 *
 * @author Ed Sowell
 */
public class CopySaveDBFile {
    int saveRequested;
    //Conctructor
    public CopySaveDBFile(){
        saveRequested = 0;
    }
    public void setSavedRequestedFlag(int aFlag){
       saveRequested = aFlag; 
    }
    public int getSavedRequestedFlag(){
       return saveRequested; 
    }

    public static void copyDBFile(File aSourceFilePath, File aDestinationFilePath, Logger aLogger)  throws IOException{
        if(!aDestinationFilePath.exists()) {
            aLogger.log(Level.INFO, "Creating New File {0} in copyDBFile", aDestinationFilePath.toString());
            aDestinationFilePath.createNewFile();
        }
        
        
        // TESTING
        boolean wasReset = false;
        if(aSourceFilePath.canRead()){
            //okDialog(aSourceFilePath.toString() + " is readable");
            aLogger.log(Level.INFO, "{0} is readable", aSourceFilePath.toString());
        } else {
            //okDialog(aSourceFilePath.toString() + " is not readable. Will set it readable");
            aLogger.log(Level.INFO, "{0} is not readable. Will set it readable", aSourceFilePath.toString());
            aSourceFilePath.setReadable(true);
            wasReset = true;
        }
        
        if (wasReset){
            if(aSourceFilePath.canRead()){
                //okDialog("After resetting " + aSourceFilePath.toString() + " is now readable");
                aLogger.log(Level.INFO, "After resetting {0} is now readable", aSourceFilePath.toString());
            } else{
                //okDialog("After resetting " + aSourceFilePath.toString() + " is is still  not readable");
                aLogger.log(Level.INFO, "After resetting {0} is is still  not readable", aSourceFilePath.toString());
            }
        }
        
        // END TESTING 
        
        aLogger.info("Low-level copy of " + aSourceFilePath.toString() + " to " +  aDestinationFilePath.toString() + " in copyDBFile");
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(aSourceFilePath).getChannel(); // Access denied here
            destination = new FileOutputStream(aDestinationFilePath).getChannel();
            long count = 0;
            long size = source.size();              
            while((count += destination.transferFrom(source, count, size-count))<size);
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
}        
        

 
/*
     renameDBFileProcess  explores use of system commands run in a separate process.
     It's probably better to use the Java java.nio commands
    */
    public void renameDBFileProcess() {
        //File pathToExecutable = new File( "rename.exe" ); // this is relative to NetBeans Projects\EditJudgeAssignments
       /* ProcessBuilder builder = new ProcessBuilder( "rename",  "aauw.html", "aauw3.html");
        builder.directory( new File("D:\\temp\\test")); // this is where you set the root folder for the executable to run with
    
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException ex) {
            Logger.getLogger(SchedulingInterfaceCpp.class.getName()).log(Level.SEVERE, null, ex);
        }
               */
        String[] command = {"CMD", "/C", "rename", "aauw.html", "aauw123.html"};
        ProcessBuilder probuilder = new ProcessBuilder( command );
        //You can set up your work directory
        probuilder.directory(new File("d:\\temp\\test"));
        
        Process process = null;
        try {
            process = probuilder.start();
        } catch (IOException ex) {
            Logger.getLogger(CopySaveDBFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        Scanner s = new Scanner(process.getInputStream());
        StringBuilder text = new StringBuilder();
        while (s.hasNextLine()) {
          text.append(s.nextLine());
          text.append("\n");
        }
        s.close();

        int result = 0;
        try {
            //try {
            result = process.waitFor();
            //} catch (InterruptedException ex) {
            //    Logger.getLogger(SchedulingInterfaceCpp.class.getName()).log(Level.SEVERE, null, ex);
            // }
        } catch (InterruptedException ex) {
            Logger.getLogger(CopySaveDBFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.printf( "Renaming process exited with result %d and output\n %s%n", result, text );
        
    }
    
    
}
