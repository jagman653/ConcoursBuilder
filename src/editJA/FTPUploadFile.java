/*
 * Copyright (C) 2019 jag_m
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author jag_m
 */

//working here   1/24/2019
public class FTPUploadFile {
    
    String server;
    int port;
    String user;
    String password;
    FTPClient ftpClient;
    int replyCode;
    

    /**
     * @param args the command line arguments
     */
// Constructor
    public FTPUploadFile(String aServer, int aPort, String aUser, String aPassword){
        server = aServer;
        port = aPort;
        user = aUser;
        password = aPassword;
        ftpClient = new FTPClient(); 
    } 
    
    public boolean connect(){
        boolean result =false;
        try {
            
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            result = true;
        } catch (IOException ex) {
            String msg = "IO Exception in FTPUploadFile connect()";
            okDialog(msg);
            Logger.getLogger(FTPUploadFile.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
        return result;
    }
    
    public int getReplyCode(){
        return replyCode;
    }
    
    public  boolean dirExists( String strRemoteDir){
        boolean result = false;
        try {
            switch (ftpClient.cwd(strRemoteDir)) {
                case 550:
                    System.out.println("Directory Doesn't Exists");
                    break;
                case 250:
                    System.out.println("Directory " + strRemoteDir +" Exists");
                    result = true; 
                    break;
                default:
                    System.out.println("Unknown Status");
                    break;
            }
        } catch (IOException ex) {
            String msg = "IOException in FTPUploadFile";
            okDialog(msg);
            Logger.getLogger(FTPUploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
// working here... // somtimes fails with reply code 227 java.net.ConnectException: Connection timed out: connect
    public void uploadInstream(String strFileName, String strLocalFileDir,  String strRemoteDir){
        InputStream inputStream = null;
        try {
            File firstLocalFile = new File(strLocalFileDir + "\\" + strFileName);
            inputStream = new FileInputStream(firstLocalFile);
        } catch (FileNotFoundException ex) {
            String msg = "Local file " + strLocalFileDir + "\\" + strFileName + " not found.";
            okDialog(msg);
            Logger.getLogger(FTPUploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Start uploading  file");
        boolean done = false;
        try {
            done = ftpClient.storeFile(strRemoteDir + "/" + strFileName, inputStream);
        } catch (IOException ex) {
            String msg = "Upload to " + strRemoteDir + "/" + strFileName + " failed.";
            okDialog(msg);
            Logger.getLogger(FTPUploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        replyCode = ftpClient.getReplyCode() ;
        try {
            inputStream.close();
        } catch (IOException ex) {
            String msg = "inputStream close() failed";
            okDialog(msg);
            Logger.getLogger(FTPUploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
       if (done) {
            System.out.println("The file was uploaded finished with done == true, so it must have worked.\nReply code = " + replyCode);
        } else {
            System.out.println("The file was uploaded finished with done == false, so it must have failed.\nReply code = " + replyCode);
        }
        
       
               
    }
    
    public static void okDialog(String theMessage) {
        JOptionPane.showMessageDialog(null, theMessage);
    }

    public static void main(String[] args) {
        String server = "nx.dnslinks.net";
        int port = 21;
        String user = "eds653";
        String password = "#S1h0Hwp5";
        // NOTE:  we must create an instance of FTPUploadFile to avoid "non-static variable cannot be referenced from a static " error
        FTPUploadFile ftpUpload = new FTPUploadFile(server, port, user, password);
        ftpUpload.connect();
        
        if(ftpUpload.dirExists("/httpdocs/manual-uploads/SW04")){
            System.out.println("/httpdocs/manual-uploads/SW04 exists");
        }
        
        ftpUpload.uploadInstream("JudgeAssignmentsSheets.pdf", "D:\\DocumentsD\\JOCBusiness\\Concours\\TutorialE5J5\\JudgeAssignmentsSheets",  "/httpdocs/manual-uploads/SW04");
        int replyCode = ftpUpload.getReplyCode();

    }
 
}    
     
