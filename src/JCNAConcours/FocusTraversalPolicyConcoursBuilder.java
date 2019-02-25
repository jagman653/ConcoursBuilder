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
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.Vector;
//import java.util.Vector;

/**
 *
 * @author Ed Sowell
 */
public class FocusTraversalPolicyConcoursBuilder   extends FocusTraversalPolicy{
        Vector<Component> order;
        // Constructor
        public FocusTraversalPolicyConcoursBuilder(Vector<Component> order) {
            this.order = new Vector<Component>(order.size());
            this.order.addAll(order);
        }
        public Component getComponentAfter(Container focusCycleRoot, Component aComponent)  {
           // int idx = (order.indexOf(aComponent) + 1) % order.size();
          //  return order.get(idx);
            int idx = order.indexOf(aComponent); // always returns -1 when the cboUniqueName has focus!!
            // It could be that it's not able to find the cboUniqueName because it's kind of special... AutoCompletion.
            // The following is a Kludge that works...
            if (idx == -1) idx = 0;
            for (int i = 0; i < order.size(); i++)
            {
                idx = (idx + 1) % order.size();
                Component next = order.get(idx);
                if (canBeFocusOwner(next)) return next;
            }
            return null;
        }

        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            /*
            int idx = order.indexOf(aComponent) - 1;
            if (idx < 0) {
                idx = order.size() - 1;
            }
            return order.get(idx);
                    */
            int idx = order.indexOf(aComponent);

            for (int i = 0; i < order.size(); i++)
            {
                idx = (idx - 1);
                if (idx < 0) {
                    idx = order.size() - 1;
                }
                Component previous = order.get(idx);
                if (canBeFocusOwner(previous)) return previous;
            }
               return null;
        }

        public Component getDefaultComponent(Container focusCycleRoot) {
            //return order.get(0);
            return getFirstComponent(focusCycleRoot);
        }

        public Component getLastComponent(Container focusCycleRoot) {
            //return order.lastElement();
            //return order.get(order.size() - 1);
            Component c = order.get(order.size() - 1);
            if (canBeFocusOwner(c))
                return c;
            else
                return getComponentBefore(focusCycleRoot, c);
        }

        public Component getFirstComponent(Container focusCycleRoot) {
            //return order.get(0);
            Component c = order.get(0);
            if (canBeFocusOwner(c))
                return c;
            else
                return getComponentAfter(focusCycleRoot, c);
        }
   
private boolean canBeFocusOwner(Component c){
        if (c.isEnabled() && c.isDisplayable() && c.isVisible() && c.isFocusable()) {
            return true;
        } else{
            return false;
        }
    }
}
