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
package editJA;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/*
 *  Custom model to make sure the items are stored in a sorted order.
 *  The default is to sort in the natural order of the item, but a
 *  Comparator can be used to customize the sort order.
 */
//class SortedComboBoxModel extends DefaultComboBoxModel
class  SortedComboBoxModel<E> extends DefaultComboBoxModel<E>
{
	private Comparator comparator;

	/*
	 *  Create an empty model that will use the natural sort order of the item
	 */
	public SortedComboBoxModel()
	{
		super();
	}

	/*
	 *  Create an empty model that will use the specified Comparator
	 */
	public SortedComboBoxModel(Comparator comparator)
	{
		super();
		this.comparator = comparator;
	}

	/*
	 *	Create a model with data and use the nature sort order of the items
	 */
	public SortedComboBoxModel(E items[])
	{
		this( items, null );
	}

	/*
	 *  Create a model with data and use the specified Comparator
	 */
	public SortedComboBoxModel(E items[], Comparator comparator)
	{
		this.comparator = comparator;

		for (E item : items)
		{
            addElement( item );
        }
	}

	/*
	 *	Create a model with data and use the nature sort order of the items
	 */
	public SortedComboBoxModel(Vector<E> items)
	{
		this( items, null );
	}

	/*
	 *  Create a model with data and use the specified Comparator
	 */

	public SortedComboBoxModel(Vector<E> items, Comparator comparator)
	{
		this.comparator = comparator;

		for (E item : items)
		{
            addElement( item );
        }
	}

	@Override
	public void addElement(E element)
	{
		insertElementAt(element, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insertElementAt(E element, int index)
	{
		int size = getSize();

		//  Determine where to insert element to keep model in sorted order

		for (index = 0; index < size; index++)
		{
			if (comparator != null)
			{
				E o = getElementAt( index );

				if (comparator.compare(o, element) > 0)
					break;
			}
			else
			{
				Comparable c = (Comparable)getElementAt( index );

				if (c.compareTo(element) > 0)
					break;
			}
		}

		super.insertElementAt(element, index);

		//  Select an element when it is added to the beginning of the model

		if (index == 0 && element != null)
		{
			setSelectedItem( element );
		}
	}
}
