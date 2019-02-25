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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.JCNAClasses;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterPersonExt;

/**
 *
 * @author Ed Sowell
 */
//
// Apparently not used
//
public class JCNAClassesRepository {
	private static JCNAClassesRepository instance = new JCNAClassesRepository();
        private Logger aLogger;

	private Map<String, JCNAClass> allJCNAClasses;
        
        
        public JCNAClassesRepository() {
        }
        
        public JCNAClassesRepository(Connection aConn) {
            allJCNAClasses = new TreeMap<String, JCNAClass>();

            LoadJCNAClassesDB(aConn, aLogger);
            
            
            
        }
/////////////////////////////////////////////////////////////////////////////
//     Loads JCNA classes from the JCNAClasses table in the concours database to internal jcnaClasses data structure 
/////////////////////////////////////////////////////////////////////////////
public  void LoadJCNAClassesDB(Connection aConn, Logger aLogger){
    Long lngID;
    String strDivision;
    String strClassName;
    String strNotes;
    String strDescription;
    String strJudgeAssignGroup;
    String strMYLookup;
    Integer intNode;
    Statement stat_c;
    ResultSet rs_c;
    JCNAClass miClass;
    int i ;
    aLogger.info("Started LoadJCNAClassesDB ");
        try {
            stat_c = aConn.createStatement();
            rs_c = stat_c.executeQuery("select * from JCNAClasses;"); 
            i = 1;
            while (rs_c.next()) { 
               // System.out.println("Record: " + i + " Division: " + rs_c.getString("division") + " " + "Class: " + rs_c.getString("class") + " Node: " +  rs_c.getString("node")+ " Description: " +  rs_c.getString("description"));
                //lngID = rs_c.getLong("ID");
                strDivision = rs_c.getString("division");
                strClassName = rs_c.getString("class");
                strDescription= rs_c.getString("description");
                strNotes = rs_c.getString("note");
                strJudgeAssignGroup = rs_c.getString("judgeassigngroup");
                strMYLookup = rs_c.getString("mylookup");
                intNode = rs_c.getInt("node");
        //        jcnaClasses.add(new JCNAClass(lngID, strDivision, strClassName,  strDescription, strNotes, intNode));
                miClass = new JCNAClass(strDivision, strClassName,  strDescription, strNotes, strJudgeAssignGroup, strMYLookup, intNode);
                allJCNAClasses.put(miClass.getName(), miClass);
                i++;
            } 
            rs_c.close();
            stat_c.close();
        } catch (SQLException ex) {
            aLogger.getLogger(JCNAClasses.class.getName()).log(Level.SEVERE, null, ex);
        }
    
     aLogger.info("Finished LoadJCNAClassesDB in JCNAClassesRepository");
   
            
}
	public JCNAClass[] getAllJCNAClasses() {
		return allJCNAClasses.values().toArray(new JCNAClass[] {});
	}
    
}
