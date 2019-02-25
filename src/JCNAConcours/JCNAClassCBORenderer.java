/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JCNAConcours;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author jag_m
 */
public class JCNAClassCBORenderer extends JLabel implements ListCellRenderer {
    public JCNAClassCBORenderer() {
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }
     
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        int len = value.toString().length();
        int posColon = value.toString().indexOf(':');
        setText(value.toString().substring(0, posColon));
        setToolTipText(value.toString().substring(posColon+1, len));
        return this;
    }
    
}
