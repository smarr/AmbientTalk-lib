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
package frameworks.urbiflock.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

/**
 * A viewer for flocks of flockrs. Clicking on a flockr opens a profile viewer on the flockr.
 */
public class FlockViewer extends Frame implements FlockListener {
	
	List unameList_ = new List();
	final Flockr owner_;
	private Vector usernames_;
	final Flock flock_;
	Subscription flockSubscription_;
	
	public FlockViewer(final Flock f, Flockr owner) {
		super("Viewing flock " + f.getName());

		setLayout(new BorderLayout());
		add(new Label(f.getName()), BorderLayout.NORTH);
		
		flock_ = f;
		owner_ = owner;
		
		unameList_ = new List();
		updateFlockrList(flock_.getSnapshot());
		unameList_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selected = unameList_.getSelectedIndex();
				if (selected != -1) {
					new ProfileViewer(owner_, f.getProfile((String)usernames_.get(selected)), false);
				}
			}
		});
		
		unameList_.addMouseListener(new PopupListener());
		
		add(unameList_, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
		        dispose();
		    }
		});
		
		flockSubscription_ = flock_.addListener(this);
	}
	
	public void dispose() {
		flockSubscription_.cancel();
		super.dispose();
	}
	
	public void updateFlockrList(String[] usernames) {
		usernames_ = new Vector(Arrays.asList(usernames));
		Iterator it = usernames_.iterator();
		while (it.hasNext()) {
			unameList_.add((String)it.next());
		}
	}
	
	public void addFlockrWithProfile(Profile p) {
		if (usernames_.contains(p.username())) {
			return;
		}
		usernames_.add(p.username());
		unameList_.add(p.username());
	}
	
	public void removeFlockrWithProfile(Profile p) {
		if (usernames_.contains(p.username())) {
			usernames_.remove(p.username());
			unameList_.remove(p.username());
		}
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
						owner_.addBuddy(flock_.getProfile((String)usernames_.get(selected)));
					}
				}
			});
			removeBuddyItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int selected = unameList_.getSelectedIndex();
					if (selected != -1) {
						owner_.removeBuddy(flock_.getProfile((String)usernames_.get(selected)));
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
					boolean isFriend = owner_.isBuddy((String)usernames_.get(selected));
					addBuddyItem.setEnabled(!isFriend);
					removeBuddyItem.setEnabled(isFriend);
		            buddyMenu.show(e.getComponent(),
		                       e.getX(), e.getY());	
				}
	        }
	    }
	}
	
	
	public void notifyFlockrAdded(Profile p) {	
		addFlockrWithProfile(p);
	}

	public void notifyFlockrRemoved(Profile p) {
		removeFlockrWithProfile(p);
	}
	
	
	/*public static void main(String[] args) {
		final HashMap propertyMap = new HashMap();
		propertyMap.put(AGSymbol.jAlloc("username"), NATText.atValue("foobar"));
		propertyMap.put(AGSymbol.jAlloc("firstname"), NATText.atValue("foo"));
		propertyMap.put(AGSymbol.jAlloc("lastname"), NATText.atValue("bar"));
		FlockViewer f = new FlockViewer(new Flock() {
			public Profile getProfile(String username) {
				return new Profile() {
					public HashMap propertyHashMap() { return propertyMap; };
					public boolean isMandatoryField(AGSymbol symbol) { return false; };
					public void addField(AGSymbol name, Object value) {  };
					public void removeField(AGSymbol fieldName) {  };
					public void setField(AGSymbol fieldName, Object value) {  };
					public String username() { return "foobar"; };
					public AbstractFieldType[] possibleTypes() { return null; };
					public AbstractFieldType getFieldType(AGSymbol fieldName) { return null; };
				};
			}
			public Profile[] listProfiles() {
				return null;
			}
			public String[] getSnapshot() {
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
			
			public void registerProfileChangedListener(Object l) {}
			public void removeProfileChangedListener(Object l) {}
			public void registerBuddyListListener(Object l) {}
			public void removeBuddyListListener(Object l) {}
			
		});
	}*/
	
}
