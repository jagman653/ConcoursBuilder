/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import JCNAConcours.EditConcoursEntryDialog;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Sowell
 */
public class MyJavaUtils {
    public MyJavaUtils(){
        
    }
    private static MyJavaUtils instance = new MyJavaUtils();

    
    /*
    Calculates age in years
    */    
public int CalcAge(String year, String month, String day) throws ParseException{
    Date dateOfBirth;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
    String dateInString = year + "-" + month + "-" + day;//"1976-1-01";
    Calendar calDOB = Calendar.getInstance();
    dateOfBirth = sdf.parse(dateInString);

    calDOB.setTime( dateOfBirth );
    Calendar today = Calendar.getInstance();
    int age = today.get(Calendar.YEAR) - calDOB.get(Calendar.YEAR);
    if (today.get(Calendar.DAY_OF_YEAR) <= calDOB.get(Calendar.DAY_OF_YEAR))
        age--;
    return age;  
}

public String CalculatePreservationClass(String aYearModel){
        String strPreversationClass;
        MyJavaUtils utils = new MyJavaUtils();
        int jaguarAge = 0;
        try {
            jaguarAge = utils.CalcAge(aYearModel, "1", "01");
        } catch (ParseException ex) {
            Logger.getLogger(EditConcoursEntryDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(jaguarAge < 20 ){
            strPreversationClass = "";
        } else{
            if(jaguarAge <= 35){
                strPreversationClass = "C18/PN";   // 20 to 35 years old 
            } else{
                strPreversationClass = "C17/PN";   // more than 35 years old
            }
        }
        return strPreversationClass;
}

/*
    Since middle initial(s) is not kept in the Master Person databasefrom table we extract it from UniqueName
    Not clear it's needed though
    As 3/18/2018 this function is no longer needed since mi ins in the database.
*/
public String getMI(String aLastName, String aFirstName, String aUniqueName, int aMaxFistNameExtension){
    // FirstNameExtension is the portion of FirstName used in forming UiqueName. If FirstName is less than aMaxFistNameExtension
    // the entire FirstANme is used. Otherwise, it is the first aMaxFistNameExtension characters.
     String mi = aUniqueName;
     mi = mi.replace(aLastName, "");
     int lenFirst = aFirstName.length();
     int lenFirstExtension  = Math.min(aMaxFistNameExtension, lenFirst);
     mi = mi.replace(aFirstName.substring(0, lenFirstExtension), "");
     return mi;     
}

public static void deleteFileOrFolder(final Path path) throws IOException {
  Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
    @Override public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
      throws IOException {
      Files.delete(file);
      return CONTINUE;
    }

    @Override public FileVisitResult visitFileFailed(final Path file, final IOException e) {
      return handleException(e);
    }

    private FileVisitResult handleException(final IOException e) {
      e.printStackTrace(); // replace with more robust error handling
      return TERMINATE;
    }

    @Override public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
      throws IOException {
      if(e!=null)return handleException(e);
      Files.delete(dir);
      return CONTINUE;
    }
  });
};

public static boolean checkJCNAClassDivisionAndName(String aDiv, String aName) {
    //boolean result = true;
    if(!(aDiv.equals("Championship") || aDiv.equals("Driven") || aDiv.equals("Special"))){
        return false;
    }
    if(!aDiv.substring(0, 1).equals(aName.subSequence(0, 1))){
        //e.g., for Champinoship, "C" = "C"
        return false;
    }
    // See StackOverflow http://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
    // Class number s.b. 2 digits
    if(!isInteger(aName.substring(1, 3))){
        return false;
    }
    // e.g., C01/PRE
    if(!aName.substring(3, 4).equals("/")){
        return false;
    }
    // ending with the / means there is no mnumonic tag, e.g., C01/ instead of C01/PRE
    if(aName.endsWith("/")){
        return false;
    }
    return true;
}

public static boolean isInteger(String s) {
    return isInteger(s,10);
}

public static boolean isInteger(String s, int radix) {
    if(s.isEmpty()) return false;
    for(int i = 0; i < s.length(); i++) {
        if(i == 0 && s.charAt(i) == '-') {
            if(s.length() == 1) return false;
            else continue;
        }
        if(Character.digit(s.charAt(i),radix) < 0) return false;
    }
    return true;
}

public static int countOccurrences(String haystack, char needle)
{
    int count = 0;
    for (int i=0; i < haystack.length(); i++){
        if (haystack.charAt(i) == needle)  count++;
    }   
    return count;
}
//
//     Rounds up to next quarter-hour clock position past the last pre-lunch judging interval
//
public static String calculateLunchtime(Logger aLogger, String aStartTime, int aTimeslotInterval, int aTSIntervalsBeforeLunch)
{
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");  // HH:mm for 24-hour time reporting
    Date d = null;
        try {
            d = df.parse(aStartTime);
        } catch (ParseException ex) {
            String msg = "Exception while parsing Date in calculateLunchtime()";
            okDialog(msg);
            aLogger.log(Level.SEVERE, msg, ex);
            System.exit(-1);
        }
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    cal.add(Calendar.MINUTE, aTimeslotInterval*aTSIntervalsBeforeLunch);
    int unroundedMinutes = cal.get(Calendar.MINUTE);
    int minutesToAdjust;
    // this will set lunch to start at the end of the just completed Judging interval if the latter is 5 minutes or less beyond a Quarter hour mark.
    // If more than 5 minutes beyond a Quarter hour mark, it sets lunch to the next quarter hour mark.
    int mod = unroundedMinutes % 15;
    if(mod > 5){
        minutesToAdjust = 15 - mod;
    } else {
        minutesToAdjust = 0;
    }
    cal.add(Calendar.MINUTE, minutesToAdjust);
    String lunchTime = df.format(cal.getTime());
    System.out.println("\nStart time: " + aStartTime + " Interval(minutes): " + aTimeslotInterval + " Num intervals  before lunch:" + aTSIntervalsBeforeLunch  + " Lunch time time: " + lunchTime);
    
    return lunchTime;
}

public static void clearFolder(String aFolder){
    File f = new File(aFolder);
    if(!(f.exists() && f.isDirectory())){
        String msg = aFolder + " doesn't exist or is not a folder.";
       System.out.println(msg);
    }
    for(File file: f.listFiles()) {
        if (!file.isDirectory())  file.delete();   
    }
}
        
public static void clearConcoursFolder(String aConcoursFolder){
    // Clear the .db, .pdf, .txt files. That is, everything EXCEPT the Placards & Scoresheet folders
    File f = new File(aConcoursFolder);
    clearFolder(aConcoursFolder);
    for(File file: f.listFiles()) {
        if (!file.isDirectory())  file.delete();   
    }
    
    
    // Now Recursively remove the Placards folder along with its subfolders.
    File filePlacards = new File(aConcoursFolder + "\\Placards");
    recursivelyeleteFiles(filePlacards);
    
    //  ... and then recreate Placards folder & subfolders
    (filePlacards).mkdir();
    File filePlacardsChampionSpecial = new File(aConcoursFolder + "\\Placards\\ChampionSpecial");
    (filePlacardsChampionSpecial).mkdir();    
    File filePlacardsDriven = new File(aConcoursFolder + "\\Placards\\Driven");
    (filePlacardsDriven).mkdir();    
    File filePlacardsDisplay = new File(aConcoursFolder + "\\Placards\\Dispaly");
    (filePlacardsDisplay).mkdir();    
    // Recursively remove the Scoresheets folder along with its subfolders.
    File fileScoresheets = new File(aConcoursFolder + "\\Scoresheets");
    recursivelyeleteFiles(fileScoresheets);
    
    //  ... and then recreate Scoresheets folder & subfolders
    (fileScoresheets).mkdir();
    File fileScoresheetsChampionSpecial = new File(aConcoursFolder + "\\Scoresheets\\ChampionSpecial");
    (fileScoresheetsChampionSpecial).mkdir();    
    File fileScoresheetsDriven = new File(aConcoursFolder + "\\Scoresheets\\Driven");
    (fileScoresheetsDriven).mkdir();    
    File fileScoresheetsDisplay = new File(aConcoursFolder + "\\Scoresheets\\Dispaly");
    (fileScoresheetsDisplay).mkdir();    
   
}
    /*private static void recursivelyeleteFiles(File file) {
    if (file.isDirectory())
        for (File f : file.listFiles())
            recursivelyeleteFiles(f);
    else
        file.delete();
}
*/
    
    public static void recursivelyeleteFiles(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    recursivelyeleteFiles(f);
                }
            }
        }
        file.delete();
    }    
    
    public static void main(String[] args) {
        MyJavaUtils u = new MyJavaUtils();
        /*
        for(int i = 1997; i>= 1976; i--){
            int age = 0;
            try {
                age = u.CalcAge(Integer.toString(i), "1", "01");
            } catch (ParseException ex) {
                Logger.getLogger(MyJavaUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Preservation class for " + i + " + car: " + u.CalculatePreservationClass(Integer.toString(i)) + "Age= " + age);
        }
*/
         Logger aLogger = Logger.getLogger("MyLoggerLog"); 
        
        String lunchtime = calculateLunchtime(aLogger, "11:00", 25, 2);
    }

       
}
