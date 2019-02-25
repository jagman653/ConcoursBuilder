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

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;

/**
 *
 * @author Ed Sowell
 */
public class JCNAClassesGroup {

    List<String> classeNames;
    String groupName;
    boolean visible;

    public JCNAClassesGroup(String aGroupName) {
        groupName = aGroupName;
        classeNames = new ArrayList<>();
    }

    public String[] GetClassNames() {

        return (String[]) classeNames.toArray(new String[classeNames.size()]);
    }

    public void AddClassName(String aClassName) {
        classeNames.add(aClassName);
    }

    public void SetName(String aGroupName) {
        groupName = aGroupName;
    }

    public String GetGroupName() {
        return groupName;
    }
    public String GetGroupNameDescription() {
        String result = groupName + ": ";
        int k = 0;
        for (String classeName : classeNames) {
            if(k == 0){
             result = result  +  classeName;
            }
            else{
             result = result  + ", " + classeName;
            }
            k++;
        }
        return result;
    }
    
    public JCNAClassesGroup getObject() {
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println(GetGroupName() + ".setVisible:" + visible);
    }

    public void doSomething() {
        System.out.println(GetGroupName() + ".setVisible:" + visible);
    }

    static class JCNAClassesGroupAction extends AbstractAction {

        private JCNAClassesGroup jcnaClassesGroup;
            // The following is the conctructor for JCNAClassAction
        // First, it calls the AbstractAction(String s) constructor

        public JCNAClassesGroupAction(JCNAClassesGroup aJcnaClassesGroup) {
                // The following super() calls the AbstractAction(String s) constructor
            // with the String argument s = the name of JCNAClassesGroup instance jcnaClassesGroup.
            // The net effect is to allow the JCNA class group name to appear in the list...
            // without this super() call the checkbox appears without any text!
            super(aJcnaClassesGroup.GetGroupNameDescription());
            this.jcnaClassesGroup = aJcnaClassesGroup;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
          //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         //  Object o = evt.getSource();
           
        }
       
        public void itemStateChanged(ItemEvent e) {
                System.out.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "SELECTED" : "DESELECTED");
        }

        
    }
}
