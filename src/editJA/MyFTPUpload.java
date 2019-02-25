/*
 * Copyright (C) 2018 jag_m
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author jag_m
 */
public class MyFTPUpload {
    String server;
    int port;
    String user;
    String password;
    FTPClient ftpClient;
    int replyCode;
    
// Constructor
    public MyFTPUpload(String aServer, int aPort, String aUser, String aPassword){
        server = aServer;
        port = aPort;
        user = aUser;
        password = aPassword;
        ftpClient = new FTPClient(); 
    } 

    public boolean connectLogin(){
        boolean result =false;
        try {
            
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            result = true;
        } catch (IOException ex) {
            String msg = "IO Exception in MyFTPUpload connect()";
            okDialog(msg);
            Logger.getLogger(MyFTPUpload.class.getName()).log(Level.SEVERE, null, ex);
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
            
            if(ftpClient.cwd(strRemoteDir)==550){
                System.out.println("Directory Doesn't Exists");
            }else if(ftpClient.cwd(strRemoteDir)==250){
                System.out.println("Directory Exists");
                result = true;
            }else{
                System.out.println("Unknown Status");
            }
        } catch (IOException ex) {
            String msg = "IO Exception in MyFTPUpload connect()";
            okDialog(msg);
            Logger.getLogger(MyFTPUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

        public void uploadInstream(String strFileName, String strLocalFileDir,  String strRemoteDir){
       // boolean result = true;
        //String remoteDirectory = "/httpdocs/manual-uploads/SW04";
        //boolean directoryExists;
       // directoryExists = ftpClient.changeWorkingDirectory(remoteDirectory);
        //String firstFileName = "JudgeAssignmentsSheets.pdf";
        //String firstRemotePath = strRemoteDir + "/" + strFilaName;
        InputStream inputStream = null;
        try {
            File firstLocalFile = new File(strLocalFileDir + "\\" + strFileName);
            inputStream = new FileInputStream(firstLocalFile);
        } catch (FileNotFoundException ex) {
            String msg = "Local file " + strLocalFileDir + "\\" + strFileName + " not found.";
            okDialog(msg);
            Logger.getLogger(MyFTPUpload.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Start uploading  file");
        boolean done = false;
        try {
            done = ftpClient.storeFile(strRemoteDir + "/" + strFileName, inputStream);
        } catch (IOException ex) {
            String msg = "Upload to " + strRemoteDir + "/" + strFileName + " failed.";
            okDialog(msg);
            Logger.getLogger(MyFTPUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        replyCode = ftpClient.getReplyCode() ;
        try {
            inputStream.close();
        } catch (IOException ex) {
            String msg = "inputStream close() failed";
            okDialog(msg);
            Logger.getLogger(MyFTPUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
       if (done) {
            System.out.println("The file was uploaded finished with done == true, so it must have worked.\nReply code = " + replyCode);
        } else {
            System.out.println("The file was uploaded finished with done == false, so it must have failed.\nReply code = " + replyCode);
        }
               
    }

    
}
