/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.efsowell.concours.lib;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jag_m
 */
//
// See https://stackoverflow.com/questions/19758002/how-to-insert-delete-column-to-jtable-java
//
public class JACompressedMergeTableModel extends DefaultTableModel {
    public JACompressedMergeTableModel(Object rowData[][], Object columnNames[]) {
     super(rowData, columnNames);
    }

    public void removeColumn(int column) {
        // for each row, remove the column
        Vector rows = dataVector;
        for (Object row : rows) {
            ((Vector) row).remove(column);
        }

        // remove the header
        columnIdentifiers.remove(column);

        // notify
        fireTableStructureChanged();
    }
}