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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Ed Sowell From http://www.devx.com/tips/Tip/5342
 */
public class CheckBoxList extends JList {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public CheckBoxList() {
        setCellRenderer(new CellRenderer());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1) {
                    JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                    checkbox.setSelected(
                            !checkbox.isSelected());
                    repaint();
                }
            }
        }
        );

        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    protected class CellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkbox = (JCheckBox) value;
            checkbox.setBackground(isSelected
                    ? getSelectionBackground() : getBackground());
            checkbox.setForeground(isSelected
                    ? getSelectionForeground() : getForeground());
            checkbox.setEnabled(isEnabled());
            checkbox.setFont(getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);
            checkbox.setBorder(isSelected
                    ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
            return checkbox;
        }
    }




        private static void createAndShowGUI() {
            //Create and set up the window.
            JFrame frame = new JFrame("CheckBoxList");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            CheckBoxList cbList = new CheckBoxList();
            JCheckBox check1 = new JCheckBox("One");
            JCheckBox check2 = new JCheckBox("two");
            JCheckBox[] myList = {check1, check2};
            cbList.setListData(myList);
            frame.getContentPane().add(cbList);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
        }


    public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
            //creating and showing this application's GUI.
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
        }
  

}
