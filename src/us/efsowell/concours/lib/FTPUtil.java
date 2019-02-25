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
package us.efsowell.concours.lib;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;

/**
 * This utility class provides a method that creates a nested directory
 * structure on a FTP server, based on Apache Commons Net library.
 * @author www.codejava.net
 *
 */
public class FTPUtil {
    /**
     * Creates a nested directory structure on a FTP server
     * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param dirPath Path of the directory, i.e /projects/java/ftp/demo
     * @return true if the directory was created successfully, false otherwise
     * @throws IOException if any error occurred during client-server communication
     */
    public static boolean makeDirectories(FTPClient ftpClient, String dirPath, Logger aLogger) throws IOException {
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {
                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) {
                        String msg = "CREATED directory: " + singleDir;
                        System.out.println(msg);
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        String msg = "COULD NOT create directory: " + singleDir;
                        System.out.println(msg);
                        aLogger.info(msg);
                        return false;
                    }
                }
            }
        }
        return true;
    }    
    
    public static void main(String[] args) {
        String server = "nx.dnslinks.net";
        int port = 21;
        String user = "eds653";
        String pass = "#S1h0Hwp5"; 
        FTPClient ftpClient = new FTPClient();
        Logger logger = Logger.getLogger("FTPUtilLog");
 
        try {
            // connect and login to the server
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
 
            // use local passive mode to pass firewall
            ftpClient.enterLocalPassiveMode();
 
            System.out.println("Connected");
 
            String dirPath = "httpdocs/manual-uploads/TestingFTPUtil/SubDir_1/SubDir_2/SubDir_3";
 
            FTPUtil.makeDirectories(ftpClient, dirPath, logger);
 
            // log out and disconnect from the server
            ftpClient.logout();
            ftpClient.disconnect();
 
            System.out.println("Disconnected");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
