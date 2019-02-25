/*
 * Copyright (C) 2018 jag_m
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
package editJA;

import us.efsowell.concours.lib.Concours;

/**
 *
 * Allows return of JTable rowArray & headerArray in the compressed schedule table
 * @author jag_m
 */
public class RowHeader {
    Object [] [] rowArray;
    Concours.JATableMergeCompressedColHeader [] headerArray;
    public RowHeader(Object [] [] aRowArray, Concours.JATableMergeCompressedColHeader [] aHeaderArray){
        rowArray = aRowArray;
        headerArray = aHeaderArray;
    }
    Object [] [] getRowArray(){
        return rowArray;
    }
    Concours.JATableMergeCompressedColHeader  [] getHeaderArray(){
        return headerArray;
    }
}
    
