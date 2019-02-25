/*
 * Copyright Edward F Sowell 2014
 */

// See http://stackoverflow.com/questions/4637215/
//     and my implementation of DBProgressDemo in NetBeans project SQLiteProject
//

package us.efsowell.concours.lib;

import static JCNAConcours.ConcoursGUI.theConcours;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;


/**
 *
 * @author Ed Sowell
 */
public class BaseDBWriterWithProgressBox extends JFrame{
    //private static final String s = "0.000";
    private JProgressBar progressBar = new JProgressBar(0, 100);
    //private JLabel label = new JLabel(s, JLabel.CENTER);
    //private JButton btnOK = new JButton("OK");
    
    Connection theConn;
    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {  
        this.setVisible(false);
        //this.dispose();
    }                                    

    public BaseDBWriterWithProgressBox(Connection aConncetion) {
        theConn = aConncetion;
        this.setLayout(new GridLayout(0, 1));
        this.setTitle("Base DB write progress");
        TitledBorder border = BorderFactory.createTitledBorder("Writing...");
        progressBar.setBorder(border);        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Container content = this.getContentPane();
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.CENTER);        
        //this.add(label);
        //
        /*this.add(btnOK, BorderLayout.SOUTH);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        */
        
        this.setSize(300, 100);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void runCalc() throws InterruptedException {
        progressBar.setIndeterminate(true);
        DBWorker theDBTask = new DBWorker();
        theDBTask.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue((Integer) evt.getNewValue());
                }
            }
        });
        theDBTask.execute();
        /*try {
            long duration = theDBTask.get();
        } catch (ExecutionException ex) {
            theConcours.GetLogger().info("ExecutionException in theDBTask.get(()");
            theConcours.GetLogger().log(Level.SEVERE, null, ex);
        }
        */
        
    }

    private class DBWorker extends SwingWorker<Long, Double> {

        long duration = 0;
        //private final DecimalFormat df = new DecimalFormat(s);
        long startTime;
        @Override
        protected Long doInBackground() throws Exception {
            Integer intJCNA;
            String strClub;
            String strLastName ;
            String strFirstName;
            String strJudgeStatus;
            Integer intCertYear;
            String strAddressStreet;
            String strCity ;
            String strState ;
            String strCountry;
            String strPostalCode; // ZIP
            String strPhoneWork ;
            String strPhoneHome ;
            String strPhoneCell;
            String strEmail;
            String strUniqueName; // last name + 1st 3 chars of first name

            String strJCNA_C;
            String strJCNA_D;
            String strCategory;
            Integer intYear;
            String strModel;
            String strDescription;
            String strColor;
            String strPlateVIN;
            String strUniqueDescription;
            //ResultSet rs_mp;
            Long lngMasterPersonnelID;
            String q; 
            theConn.setAutoCommit(false);
            Statement stat ;
            stat = theConn.createStatement();
            stat.executeUpdate("drop table if exists MasterPersonnel;"); 
            q = "create table MasterPersonnel ('masterpersonnel_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'jcna' INTEGER NOT NULL, 'club' TEXT, 'lastname' TEXT NOT NULL , 'firstname' TEXT NOT NULL, 'mi' TEXT, 'unique_name' TEXT NOT NULL, 'judgestatus' TEXT, 'cert_year' INTEGER, 'address_street' TEXT, 'city' TEXT, 'state' TEXT, 'country' TEXT, postalcode TEXT, 'phone_work' TEXT, 'phone_home' TEXT, 'phone_cell' TEXT, 'email' TEXT) ;";
            stat.executeUpdate(q);

            stat.executeUpdate("drop table if exists MasterJaguar;"); 
            q = "create table MasterJaguar ('masterjaguar_id' INTEGER PRIMARY KEY AUTOINCREMENT,  'masterpersonnel_id' INTEGER,   'jcnaclass_c' TEXT NOT NULL, 'jcnaclass_d' TEXT NOT NULL, 'joclacategory' TEXT NOT NULL, 'year' INTEGER NOT NULL, 'model' TEXT NOT NULL, 'description' TEXT NOT NULL, 'unique_desc' TEXT NOT NULL, 'color' TEXT NOT NULL, 'platevin' TEXT NOT NULL,  FOREIGN KEY (masterpersonnel_id) REFERENCES MasterPersonnel (masterpersonnel_id)) ;";
            stat.executeUpdate(q);

            PreparedStatement prep_p = theConn.prepareStatement( "insert into MasterPersonnel ('jcna', 'club', 'lastname', 'firstname', 'unique_name', 'judgestatus', 'cert_year', 'address_street', 'city', 'state', 'country', postalcode, 'phone_work', 'phone_home', 'phone_cell', 'email') values ( ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ?, ?, ?);");
            PreparedStatement prep_j ;
            prep_j = theConn.prepareStatement( "insert into MasterJaguar ('masterpersonnel_id',  'jcnaclass_c', 'jcnaclass_d', 'joclacategory', 'year', 'model', 'description', 'unique_desc', 'color', 'platevin') values ( ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?,  ?);");
            int count = 0;
            int numMp = theConcours.GetConcoursMasterPersonnel().size();
            startTime = System.nanoTime();
            for(MasterPersonExt mp : theConcours.GetConcoursMasterPersonnel()){
                strUniqueName = mp.getUniqueName();
                count ++;
                intJCNA = mp.getJcna();
                strLastName = mp.getLastName();
                strFirstName = mp.getFirstName();
                strClub = mp.getClub();
                strJudgeStatus = mp.getJudgeStatus();
                intCertYear = mp.getCertYear();
                strAddressStreet = mp.getAddressSreet();
                strCity  = mp.getCity();
                strState  = mp.getState();
                strCountry = mp.getCountry();
                strPostalCode =mp.getPostalCode(); // ZIP
                strPhoneWork = mp.getPhoneWork();
                strPhoneHome= mp.getPhoneHome();
                strPhoneCell =   mp.getPhoneCell();
                strEmail  = mp.getEmail();
                prep_p.setInt(1, intJCNA); 
                prep_p.setString(2,strClub); 
                prep_p.setString(3,strLastName); 
                prep_p.setString(4,strFirstName); 
                prep_p.setString(5,strUniqueName); 
                prep_p.setString(6,strJudgeStatus);
                prep_p.setInt(7, intCertYear);
                prep_p.setString(8,strAddressStreet); 
                prep_p.setString(9,strCity); 
                prep_p.setString(10,strState); 
                prep_p.setString(11,strCountry); 
                prep_p.setString(12,strPostalCode); 
                prep_p.setString(13,strPhoneWork); 
                prep_p.setString(14,strPhoneHome); 
                prep_p.setString(15,strPhoneCell); 
                prep_p.setString(16,strEmail);
                prep_p.addBatch(); 
                prep_p.executeBatch(); 
                theConn.commit();

                //
                //  Load the Jaguars for this person into MasterJaguar table
                //
                // Get the row_id of the most recent insert into MasterPersonnel
                //
                Statement stat_lrid = theConn.createStatement();
                ResultSet rs_lrid = stat_lrid.executeQuery("SELECT last_insert_rowid()");
                lngMasterPersonnelID = 0L; // won't be used
                if (rs_lrid.next()) {
                    lngMasterPersonnelID = rs_lrid.getLong(1);
                }
                else{
                    theConcours.GetLogger().info("ERROR: Failed to get last row id of inserts into MasterPersonnel in WriteMasterPersonnelAndJaguarTablesFromMemToDB");
                    System.exit(-1);
                }
                stat_lrid.close();
                rs_lrid.close();
                //
                // write the stable for the master person
                //
                for(MasterJaguar mj : mp.getJaguarStable()){
                    strJCNA_C  = mj.getJcnaclass_c();
                    strJCNA_D = mj.getJcnaclass_d();
                    strCategory = mj.getJoclacategory();
                    intYear = mj.getYear();
                    strModel = mj.getModel();
                    strDescription = mj.getDescription();
                    strUniqueDescription = mj.getUniqueDesc();
                    strColor = mj.getColor();
                    strPlateVIN = mj.getPlateVIN();
                    prep_j.setLong(1, lngMasterPersonnelID);
                    prep_j.setString(2,strJCNA_C); 
                    prep_j.setString(3,strJCNA_D); 
                    prep_j.setString(4,strCategory);
                    prep_j.setInt(5, intYear);
                    prep_j.setString(6, strModel);
                    prep_j.setString(7,strDescription); 
                    prep_j.setString(8,strUniqueDescription); 
                    prep_j.setString(9,strColor); 
                    prep_j.setString(10,strPlateVIN); 
                    prep_j.addBatch(); 
                    prep_j.executeBatch(); 
                } // end of mj loop
                
                theConn.commit();
                //long elapsedTime = System.nanoTime() - startTime;
                //double t = elapsedTime*1.E-9;
                //publish(Double.valueOf(t));
                setProgress(count*(100/numMp));
            } // end of for(mp)      

       theConn.setAutoCommit(true); 
       prep_p.close();
       prep_j.close();
       duration = System.nanoTime() - startTime;
       return duration;
    }

        /*@Override
        protected void process(List<Double> chunks) {
            for (Double d : chunks) {
                //label.setText(Long.toString(duration));
                label.setText("Time: " + df.format(d));
                
            }
        }
        */
        
       
        
        //@Override
        //private void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //}
    }

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DBWriterWithProgressBox task = new DBWriterWithProgressBox();
                task.runCalc();
            }
        });
      }
    */
    
    
}
