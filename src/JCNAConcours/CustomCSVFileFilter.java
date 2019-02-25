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

import java.io.File;

/**
 *
 * @author Ed Sowell
 */
public class CustomCSVFileFilter  extends javax.swing.filechooser.FileFilter{
    String contains;
            @Override
        public boolean accept(File file) {
            // Allow only directories, or CSV files with 'contains' embedded
            return file.isDirectory() || (file.getAbsolutePath().contains(contains) && (file.getAbsolutePath().endsWith(".csv")|| file.getAbsolutePath().endsWith(".CSV")));
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return contains + " CSV files (*" + contains + "*.csv)";
        }
        public CustomCSVFileFilter(String strContains){
            contains = strContains;
        }

    
}
