/**
 * AmbientTalk/2 Project
 * FlockListViewer.java created on 12 sep 2008 at 14:10:55
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

import java.awt.Button;
import java.awt.Frame;
import java.awt.List;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;

/**
 * A viewer for the list of flocks defined for a given flockr.
 * Clicking on a flock opens a flock viewer on the flock.
 */
public class FlockListViewer extends Frame implements ActionListener {
	
	final List listOfFlocks_;
	final Button newFlockButton_;
	final Flockr owner_;
	Flock[] flocks_;
	
	public FlockListViewer(final Flockr owner) {
		super("Flock List Viewer");
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		owner_ = owner;

		listOfFlocks_ = new List();
		listOfFlocks_.setMultipleMode(false);
		
		flocks_ = owner_.getFlocks();
		for (int i = 0; i < flocks_.length; i++) {
			listOfFlocks_.add(flocks_[i].getName());
		}

		listOfFlocks_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selected = listOfFlocks_.getSelectedIndex();
				if (selected != -1) {
					new FlockViewer(flocks_[selected], owner);
				}
			}
		});

		listOfFlocks_.addMouseListener(new PopupListener());
		
		add(listOfFlocks_);
		
		newFlockButton_ = new Button("New");
		newFlockButton_.setActionCommand("New");
		newFlockButton_.addActionListener(this);
		add(newFlockButton_);
		
		pack();
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
		        dispose();
		    }
		});
		owner_.registerFlocksListener(this);
	}
	
	public void dispose() {
		owner_.removeFlocksListener(this);
		super.dispose();
	}
	
	class PopupListener extends MouseAdapter {
		MenuItem removeItem = new MenuItem("Remove flock");
		PopupMenu menu = new PopupMenu();

		public PopupListener() {
			listOfFlocks_.add(menu);
			menu.add(removeItem);
			removeItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int selected = listOfFlocks_.getSelectedIndex();
					if ((selected != -1) & !(flocks_[selected].isDefaultFlock())) {
						owner_.removeFlock(flocks_[selected]);
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
				int selected = listOfFlocks_.getSelectedIndex();
				if (selected != -1) {
		            menu.show(e.getComponent(),
		                       e.getX(), e.getY());	
				}
	        }
	    }
	}

	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command == "New") {
			owner_.openFlockEditorOnNewFlock();
			return;
		}
	}
	
	public void notifyFlockAdded(Flock flock) {
		flocks_ = owner_.getFlocks();
		listOfFlocks_.add(flock.getName());
	}
	
	public void notifyFlockRemoved(Flock flock) {
		flocks_ = owner_.getFlocks();
		listOfFlocks_.remove(flock.getName());
	}
	
	/*public static void main(String[] args) {
		new FlockListViewer(new Flockr() {

			public void addBuddy(Profile profile) {}
			public Profile getBuddy(String uid) { return null; }

			public Flock[] getFlocks() {
				return new Flock[] {
					new Flock() {
						public String getName() { return "testflock"; }

						public Profile getProfile(String username) {
							return null;
						}

						public Profile[] listProfiles() {
							return null;
						}

						public String[] getSnapshot() {
							return null;
						}
					}
				};
			}

			public boolean isBuddy(String uid) { return false; }

			public void removeBuddy(Profile profile) {}

			public void removeFlock(Flock f) {
				System.out.println("Removed flock "+f);
			}
			
			public void registerProfileChangedListener(Object l) {}
			public void removeProfileChangedListener(Object l) {}
			public void registerBuddyListListener(Object l) {}
			public void removeBuddyListListener(Object l) {}
		});
	}*/
	
}

