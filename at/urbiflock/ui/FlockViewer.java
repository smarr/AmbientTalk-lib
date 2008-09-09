/**
 * AmbientTalk/2 Project
 * FlockViewer.java created on 5 sep 2008 at 16:39:51
 * (c) Programming Technology Lab, 2006 - 2007
 * Authors: Tom Van Cutsem & Stijn Mostinckx
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package at.urbiflock.ui;

import edu.vub.at.objects.natives.NATBoolean;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.grammar.AGSymbol;

import java.awt.Frame;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A viewer for flocks of flockrs. Clicking on a flockr opens a profile viewer on the flockr.
 */
public class FlockViewer extends Frame {

	public FlockViewer(final Flock f) {
		
		final String[] usernames = f.listUsernames();
		
		final List unameList = new List();
		unameList.setMultipleMode(false);
		for (int i = 0; i < usernames.length; i++) {
			unameList.add(usernames[i]);
		}
		unameList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selected = unameList.getSelectedIndex();
				if (selected != -1) {
					new ProfileViewer(f.getProfile(usernames[selected]), false);
				}
			}
		});
		
		add(unameList);
		
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		final HashMap propertyMap = new HashMap();
		propertyMap.put("username", "foobar");
		propertyMap.put("firstname", "foo");
		propertyMap.put("lastname", "bar");
		FlockViewer f = new FlockViewer(new Flock() {
			public Profile getProfile(String username) {
				return new Profile() {
					public HashMap propertyHashMap() { return propertyMap; };
					public NATBoolean isMandatoryField(AGSymbol symbol) { return NATBoolean._FALSE_; };
					public void addField(AGSymbol name, NATText value) {  };
					public void removeField(AGSymbol fieldName) {  };
				};
			}
			public Profile[] listProfiles() {
				return null;
			}
			public String[] listUsernames() {
				return new String[] { "foo" };
			}
		});
	}
	
}
