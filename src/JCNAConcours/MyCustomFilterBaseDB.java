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
public class MyCustomFilterBaseDB  extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
            return file.getAbsolutePath().contains("Base") && file.getAbsolutePath().endsWith(".db") ;
    }

    @Override
    public String getDescription() {
            return "Database files (*.db)";
    }
    
}

