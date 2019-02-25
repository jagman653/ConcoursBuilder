/*
 * Copyright (C) 2017 Owner
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

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.ConcoursGUI.theConcours;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MyJavaUtils;

/**
 *
 * @author Owner
 */
public class UserSettingsDialog extends javax.swing.JDialog {
    String[] titleFontSizeAry;
    String curSelectedTitleFontSize;
    
    String[] subtitleFontSizeAry;
    String curSelectedSubtitleFontSize;
    
    String[] cellFontSizeAry;
    String curSelectedCellFontSize;
    
    String[] headerFontSizeAry;
    String curSelectedHeaderFontSize;
    
    String[] footnoteFontSizeAry;
    String curSelectedFootnoteFontSize;
    
    String curStartTime;
    String curTSInterval;
    String curTSBeforLunch;
    String curLunchInterval;
    String curUserName;
    String curConcoursChair;
    String curChiefJudge;
    
    String[] startTimeAry;
    String curSelectedStartTime;
    
   // MyIntegerInputVerifier integerFieldVerifier;
    
    Connection conn;
    Concours theConcours;
    LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();
    /**
     * Creates new form UserSettingsDialog
     */
    public UserSettingsDialog(java.awt.Frame parent, boolean modal, Concours aConcours) {
        super(parent, modal);
        //
        // titleFontSizeAry is used to set the default model for the title font size combobox IN CUSTOM CODE in the NetBeans Designer.
        // Previously, I've reset the DefaultComboBoxModel AFTER the Designer had set it to "Item 1", Item 2" etc. This seems to be a little better.
        //
        theConcours = aConcours;
        conn = theConcours.GetConnection();
        
        // Set up font size arrays for the comboboxes
        ArrayList<String> lstTitleFontSize = new ArrayList<>();
        for(Integer i = 8; i<=14; i++){
            lstTitleFontSize.add(i.toString());
        }
        titleFontSizeAry = new String[lstTitleFontSize.size()];
        titleFontSizeAry = lstTitleFontSize.toArray(titleFontSizeAry);
        curSelectedTitleFontSize = theConcours.GetConcoursTitleFontSize().toString();

        ArrayList<String> lstSubtitleFontSize = new ArrayList<>();
        for(Integer i = 6; i<=10; i++){
            lstSubtitleFontSize.add(i.toString());
        }
        subtitleFontSizeAry = new String[lstSubtitleFontSize.size()];
        subtitleFontSizeAry = lstSubtitleFontSize.toArray(subtitleFontSizeAry);
        curSelectedSubtitleFontSize = theConcours.GetConcoursSubtitleFontSize().toString();
        
        
        ArrayList<String> lstCellFontSize = new ArrayList<>();
        for(Integer i = 6; i<=8; i++){
            lstCellFontSize.add(i.toString());
        }
        cellFontSizeAry = new String[lstCellFontSize.size()];
        cellFontSizeAry = lstCellFontSize.toArray(cellFontSizeAry);
        curSelectedCellFontSize = theConcours.GetConcoursCellFontSize().toString();
        
        ArrayList<String> lstHeaderFontSize = new ArrayList<>();
        for(Integer i = 6; i<=8; i++){
            lstHeaderFontSize.add(i.toString());
        }
        headerFontSizeAry = new String[lstHeaderFontSize.size()];
        headerFontSizeAry = lstHeaderFontSize.toArray(headerFontSizeAry);
        curSelectedHeaderFontSize = theConcours.GetConcoursHeaderFontSize().toString();
        
        ArrayList<String> lstFootnoteFontSize = new ArrayList<>();
        for(Integer i = 6; i<=8; i++){
            lstFootnoteFontSize.add(i.toString());
        }
        footnoteFontSizeAry = new String[lstFootnoteFontSize.size()];
        footnoteFontSizeAry = lstFootnoteFontSize.toArray(footnoteFontSizeAry);
        curSelectedFootnoteFontSize = theConcours.GetConcoursFootnoteFontSize().toString();
    
        // Set up Start time list for a combobox
        String strEarliestStartTime = "08:00";
        int clockInterval = 15; // qtr hour
        int numIntervalsl = 32; // clock intervals in 8 hours
        
        ArrayList<String> lstStartTimes = createStartTimeList(theConcours, strEarliestStartTime, clockInterval, numIntervalsl);
        startTimeAry = new String[lstStartTimes.size()];
        startTimeAry = lstStartTimes.toArray(startTimeAry);
        curSelectedStartTime = theConcours.GetConcoursStartTime();
        
        curStartTime = theConcours.getStartTime();
        curTSInterval = theConcours.GetConcoursTimeslotInterval().toString();
        curTSBeforLunch = theConcours.GetConcoursTimeslotsBeforeLunch().toString();
        curLunchInterval = theConcours.GetConcoursLunchInterval().toString();

        curUserName = theConcours.GetConcoursUserName() ;
        curConcoursChair = theConcours.GetConcoursChair();
        curChiefJudge = theConcours.GetConcoursChiefJudge();
        
        //integerFieldVerifier = new MyIntegerInputVerifier();
            
        
        initComponents();
        
        
    }
    private static ArrayList<String> createStartTimeList(Concours aConcours, String aFirstStartTime, int aIncrement, int aNumItems){
        // Create an array of Strings of the form "8:00", "8:15",... "16:00"
        ArrayList<String> theList = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");  // HH:mm for 24-hour time reporting
        Date d = null;
        String strStartTime3 = "8:00";
        try {
            d = df.parse(strStartTime3);
        } catch (ParseException ex) {
            //Logger.getLogger(TimeArithmetic.class.getName()).log(Level.SEVERE, null, ex);
            String msg= " Parse Exception in createStartTimeList";
            okDialog(msg);
            aConcours.GetLogger().log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String s = df.format(cal.getTime());
        theList.add(s);
        for(int i = 0;i<32;i++){
            cal.add(Calendar.MINUTE, 15);
            s = df.format(cal.getTime());
            try {
                cal.setTime(df.parse(s));
            } catch (ParseException ex) {
                //Logger.getLogger(TimeArithmetic.class.getName()).log(Level.SEVERE, null, ex);
                String msg= " Parse Exception in createStartTimeList";
                okDialog(msg);
                aConcours.GetLogger().log(Level.SEVERE, msg, ex);
                System.exit(-1);
            }
            String newTime = df.format(cal.getTime());
            theList.add(newTime);
        }
        return theList;        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtTSInterval = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTSBeforLunch = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtLunchInterval = new javax.swing.JTextField();
        cboTitleFontSize = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        cboCellFontSize = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cboHeaderFontSize = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        cboFootnoteFontSize = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtConcoursChair = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtChiefJudge = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        cboStartTimes = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        cboSubtitleFontSize = new javax.swing.JComboBox<>();
        cboCompression = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("User Settings");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        getContentPane().add(btnOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 310, -1, -1));

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 310, -1, -1));

        jLabel1.setText("Start time");
        jLabel1.setToolTipText("Select concours starting time");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, -1, -1));

        jLabel2.setText("Timeslot interval(minutes)");
        jLabel2.setToolTipText("Select judging time interval");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, -1, -1));

        txtTSInterval.setText(curTSInterval);
        txtTSInterval.setToolTipText("You must enter an integer value in order to leave this field.");
        txtTSInterval.setInputVerifier(new MyIntegerInputVerifier());
        getContentPane().add(txtTSInterval, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 120, 78, -1));

        jLabel3.setText("Timeslots before lunch");
        jLabel3.setToolTipText("Select number of judging periods before lunch break");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, -1));

        txtTSBeforLunch.setText(curTSBeforLunch);
        txtTSBeforLunch.setToolTipText("You must enter an integer value in order to leave this field.");
        txtTSBeforLunch.setInputVerifier(new MyIntegerInputVerifier());
        getContentPane().add(txtTSBeforLunch, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 160, -1, -1));

        jLabel4.setText("Lunch interval(minutes)");
        jLabel4.setToolTipText("Select lunch break interval");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, -1, -1));

        txtLunchInterval.setText(curLunchInterval);
        txtLunchInterval.setToolTipText("You must enter an integer value in order to leave this field.");
        txtLunchInterval.setInputVerifier(new MyIntegerInputVerifier());
        getContentPane().add(txtLunchInterval, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 200, 78, -1));

        cboTitleFontSize.setModel(new javax.swing.DefaultComboBoxModel<>(titleFontSizeAry));
        cboTitleFontSize.setSelectedItem(curSelectedTitleFontSize);
        cboTitleFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTitleFontSizeActionPerformed(evt);
            }
        });
        getContentPane().add(cboTitleFontSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 70, 62, -1));

        jLabel5.setText("Page title font");
        jLabel5.setToolTipText("Font size for page title");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(362, 76, -1, -1));

        jLabel6.setText("Schedule timing");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 34, -1, -1));

        jLabel7.setText("Schedule PDF");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, -1, -1));

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 52, 270, 10));

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 50, 190, 10));

        cboCellFontSize.setModel(new javax.swing.DefaultComboBoxModel<>(cellFontSizeAry));
        cboCellFontSize.setSelectedItem(curSelectedCellFontSize);
        cboCellFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCellFontSizeActionPerformed(evt);
            }
        });
        getContentPane().add(cboCellFontSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 150, 62, -1));

        jLabel8.setText("Table cell font");
        jLabel8.setToolTipText("Font size for table cell text");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(362, 152, 80, -1));

        cboHeaderFontSize.setModel(new javax.swing.DefaultComboBoxModel<>(headerFontSizeAry));
        cboHeaderFontSize.setSelectedItem(curSelectedHeaderFontSize);
        cboHeaderFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHeaderFontSizeActionPerformed(evt);
            }
        });
        getContentPane().add(cboHeaderFontSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, 62, -1));

        jLabel9.setText("Column header font");
        jLabel9.setToolTipText("Font size for column header text");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(362, 190, -1, -1));

        cboFootnoteFontSize.setModel(new javax.swing.DefaultComboBoxModel<>(footnoteFontSizeAry));
        cboFootnoteFontSize.setSelectedItem(curSelectedFootnoteFontSize);
        cboFootnoteFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFootnoteFontSizeActionPerformed(evt);
            }
        });
        getContentPane().add(cboFootnoteFontSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 220, 62, -1));

        jLabel10.setText("Page footnote font");
        jLabel10.setToolTipText("Font size for page footnote");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 220, -1, 20));

        jLabel11.setText("User name");
        jLabel11.setToolTipText("ConcoursBuilder user-- you!");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 70, -1, -1));

        txtUserName.setText(curUserName);
        getContentPane().add(txtUserName, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 70, 90, -1));

        jLabel12.setText("Concours Chair");
        jLabel12.setToolTipText("Recipient of Entry forms, etc.");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 110, -1, -1));

        txtConcoursChair.setText(curConcoursChair);
        getContentPane().add(txtConcoursChair, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 110, 80, -1));

        jLabel13.setText("Chief Judge");
        jLabel13.setToolTipText("Person responsible for judge assignments, schedule, etc");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 150, -1, -1));

        txtChiefJudge.setText(curChiefJudge);
        getContentPane().add(txtChiefJudge, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 150, 80, -1));

        jSeparator3.setForeground(new java.awt.Color(0, 0, 0));
        getContentPane().add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, 190, 10));

        jLabel14.setText("Personnel");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 30, -1, -1));

        cboStartTimes.setModel(new javax.swing.DefaultComboBoxModel<>(startTimeAry));
        cboStartTimes.setSelectedItem(curSelectedStartTime);
        getContentPane().add(cboStartTimes, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 70, 78, -1));

        jLabel15.setText("Page subtitle font");
        jLabel15.setToolTipText("Font size for page subtitle");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(362, 114, -1, -1));

        cboSubtitleFontSize.setModel(new javax.swing.DefaultComboBoxModel<>(subtitleFontSizeAry));
        cboTitleFontSize.setSelectedItem(curSelectedSubtitleFontSize);
        cboSubtitleFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSubtitleFontSizeActionPerformed(evt);
            }
        });
        getContentPane().add(cboSubtitleFontSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 110, 62, -1));

        cboCompression.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2", "3", "4", "5", "6" }));
        cboCompression.setSelectedIndex(2);
        getContentPane().add(cboCompression, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 260, 40, -1));

        jLabel16.setText("Compression");
        jLabel16.setToolTipText("Max Classes per column for compact view");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(362, 266, -1, -1));
        getContentPane().add(filler1, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 40, 340));
        getContentPane().add(filler2, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 10, 40, 340));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
public class MyIntegerInputVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        try {
            Integer value = new Integer(text);
            return value > 0; 
        } catch (NumberFormatException e) {
            return false;
        }
    }
}    
    
    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        String strStartTime = cboStartTimes.getSelectedItem().toString();
        String strTimeslotInterval = txtTSInterval.getText();
        String strTimeslotsBeforeLunch = txtTSBeforLunch.getText();
        String strLunchInterval = this.txtLunchInterval.getText();
        Integer strTitleFontSize = Integer.parseInt((String)cboTitleFontSize.getSelectedItem());
        Integer strSubtitleFontSize = Integer.parseInt((String)cboSubtitleFontSize.getSelectedItem());
        Integer strCellFontSize = Integer.parseInt((String)cboCellFontSize.getSelectedItem());
        Integer strHeaderFontSize = Integer.parseInt((String)cboHeaderFontSize.getSelectedItem());
        Integer strFootnoteFontSize = Integer.parseInt((String)cboFootnoteFontSize.getSelectedItem());
        String strUserName = txtUserName.getText();
        String strConcoursChair = txtConcoursChair.getText();
        String strChiefJudge = txtChiefJudge.getText();
        Integer intCompression = Integer.parseInt((String)cboCompression.getSelectedItem()); 
        LoadSQLiteConcoursDatabase.CreateUserSettingsTable(conn ); // throw away the current one
        LoadSQLiteConcoursDatabase.SetUserSettingsTable(conn, strStartTime, strTimeslotInterval, strTimeslotsBeforeLunch, strLunchInterval,
                                                        strTitleFontSize, strSubtitleFontSize, strCellFontSize, strHeaderFontSize, strFootnoteFontSize, strUserName,
                                                        strConcoursChair, strChiefJudge, intCompression);
        // Now read them and save into Concours properties
        // 
        loadSQLiteConcoursDatabase.LoadConcoursUserSettingsDB(conn, theConcours, theConcours.GetLogger());
        // Calculate Lunch time, rounded up to nearest 15 minute clock position
        //theConcours.SetConcoursLunchTime(MyJavaUtils.calculateLunchtime(theConcours.GetConcoursStartTime(), theConcours.GetConcoursTimeslotInterval(),theConcours.GetConcoursTimeslotsBeforeLunch()));
        String startTime = theConcours.GetConcoursStartTime();
        int timeslotInterval = theConcours.GetConcoursTimeslotInterval();
        int timeslotsBeforeLunch = theConcours.GetConcoursTimeslotsBeforeLunch();
        String lunchTime = MyJavaUtils.calculateLunchtime(theConcours.GetLogger(), startTime, timeslotInterval, timeslotsBeforeLunch);
        theConcours.SetConcoursLunchTime(lunchTime);
        this.setVisible(false);
        //this.dispose();
        return;
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        //this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void cboTitleFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTitleFontSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTitleFontSizeActionPerformed

    private void cboCellFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCellFontSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboCellFontSizeActionPerformed

    private void cboHeaderFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHeaderFontSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboHeaderFontSizeActionPerformed

    private void cboFootnoteFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFootnoteFontSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboFootnoteFontSizeActionPerformed

    private void cboSubtitleFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSubtitleFontSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSubtitleFontSizeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserSettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserSettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserSettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserSettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Logger theLogger = Logger.getLogger("PreConcoursBuilderLog"); // Can't use the normal logger yet...

                Concours theConcours = new Concours(theLogger, 3);
                UserSettingsDialog dialog = new UserSettingsDialog(new javax.swing.JFrame(), true, theConcours);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox<String> cboCellFontSize;
    private javax.swing.JComboBox<String> cboCompression;
    private javax.swing.JComboBox<String> cboFootnoteFontSize;
    private javax.swing.JComboBox<String> cboHeaderFontSize;
    private javax.swing.JComboBox<String> cboStartTimes;
    private javax.swing.JComboBox<String> cboSubtitleFontSize;
    private javax.swing.JComboBox<String> cboTitleFontSize;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField txtChiefJudge;
    private javax.swing.JTextField txtConcoursChair;
    private javax.swing.JTextField txtLunchInterval;
    private javax.swing.JTextField txtTSBeforLunch;
    private javax.swing.JTextField txtTSInterval;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
