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

import java.awt.Frame;
import java.awt.List;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.grammar.AGSymbol;

/**
 * A viewer for flocks of flockrs. Clicking on a flockr opens a profile viewer on the flockr.
 */
public class FlockViewer extends Frame {
	
	List unameList_ = new List();
	final Flockr owner_;
	final String[] usernames_;
	final Flock flock_;
	
	public FlockViewer(final Flock f, Flockr owner) {
		super("Flock Viewer");

		flock_ = f;
		owner_ = owner;
		usernames_ = f.listUsernames();
		
		unameList_ = new List();
		unameList_.setMultipleMode(false);
		for (int i = 0; i < usernames_.length; i++) {
			unameList_.add(usernames_[i]);
		}
		unameList_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selected = unameList_.getSelectedIndex();
				if (selected != -1) {
					new ProfileViewer(f.getProfile(usernames_[selected]), false);
				}
			}
		});
		
		unameList_.addMouseListener(new PopupListener());
		
		add(unameList_);
		
		pack();
		setVisible(true);
	}
	
	class PopupListener extends MouseAdapter {
		MenuItem addBuddyItem = new MenuItem("Add to buddylist");
		MenuItem removeBuddyItem = new MenuItem("Remove from buddylist");
		PopupMenu buddyMenu = new PopupMenu();

		public PopupListener() {
			unameList_.add(buddyMenu);
			buddyMenu.add(addBuddyItem);
			buddyMenu.add(removeBuddyItem);
			addBuddyItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int selected = unameList_.getSelectedIndex();
					if (selected != -1) {
						owner_.addBuddy(flock_.getProfile(usernames_[selected]));
					}
				}
			});
			removeBuddyItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int selected = unameList_.getSelectedIndex();
					if (selected != -1) {
						owner_.removeBuddy(flock_.getProfile(usernames_[selected]));
					}
				}
			});
		}
		
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
				int selected = unameList_.getSelectedIndex();
				if (selected != -1) {
					boolean isFriend = owner_.isBuddy(usernames_[selected]);
					addBuddyItem.setEnabled(!isFriend);
					removeBuddyItem.setEnabled(isFriend);
		            buddyMenu.show(e.getComponent(),
		                       e.getX(), e.getY());	
				}
	        }
	    }
	}
	
	public static void main(String[] args) {
		final HashMap propertyMap = new HashMap();
		propertyMap.put(AGSymbol.jAlloc("username"), NATText.atValue("foobar"));
		propertyMap.put(AGSymbol.jAlloc("firstname"), NATText.atValue("foo"));
		propertyMap.put(AGSymbol.jAlloc("lastname"), NATText.atValue("bar"));
		FlockViewer f = new FlockViewer(new Flock() {
			public Profile getProfile(String username) {
				return new Profile() {
					public HashMap propertyHashMap() { return propertyMap; };
					public boolean isMandatoryField(AGSymbol symbol) { return false; };
					public void addField(AGSymbol name, NATText value) {  };
					public void removeField(AGSymbol fieldName) {  };
					public void setField(AGSymbol fieldName, NATText value) {  };
				};
			}
			public Profile[] listProfiles() {
				return null;
			}
			public String[] listUsernames() {
				return new String[] { "foo" };
			}
			public String getName() { return "testflock"; }
		}, new Flockr() {

			public void addBuddy(Profile profile) {
				System.out.println("Buddy added: " + profile);
			}

			public Profile getBuddy(String uid) {
				return null;
			}

			public boolean isBuddy(String uid) {
				return false;
			}

			public void removeBuddy(Profile profile) {				
			}

			public Flock[] getFlocks() {
				return null;
			}

			public void removeFlock(Flock f) {}
			
		});
	}
	
}
