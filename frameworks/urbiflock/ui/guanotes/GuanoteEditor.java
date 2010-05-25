/**
 * AmbientTalk/2 Project
 * GuanoteEditor.java created on 3 nov 2008 at 16:09:50
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
package frameworks.urbiflock.ui.guanotes;

import java.awt.Button;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import frameworks.urbiflock.ui.Flock;
import frameworks.urbiflock.ui.Flockr;

/**
 * The UI of a Guanote Editor. This GUI allows the user to
 * create and send a new Guanote.
 * 
 * @author tvcutsem
 */
public class GuanoteEditor extends GuanoteView {
	
	class PopupListener extends MouseAdapter {
		MenuItem expandItem = new MenuItem("Expand flock");
		PopupMenu menu = new PopupMenu();

		public PopupListener() {
			to_.add(menu);
			menu.add(expandItem);
			
			expandItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					String fieldText = to_.getText();
					String flockName = to_.getSelectedText();
					int start = to_.getSelectionStart();
					int end = to_.getSelectionEnd();
					
					try {
						guanotes_.owner().getFlocks();
						Flock[] flocks = guanotes_.owner().getFlocks();
						String[] names = new String[flocks.length];
						
						for (int i = 0; i < names.length; i++) {
							if ( flocks[i].getName().equals(flockName) ) {
								String[] flockrs = flocks[i].getFlockrList();
								String flockrsNames = "";
								if (flockrs.length > 0) {
									flockrsNames = flockrs[0];
									for (int j = 1; j < flockrs.length; j++) {
										flockrsNames +=  ","+flockrs[j];
									}
								}
								to_.setText(fieldText.substring(0, start) + flockrsNames + fieldText.substring(end, fieldText.length()));
								
								return;
							}
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
			if (e.getClickCount() == 2) {
				menu.show(e.getComponent(),e.getX(), e.getY());
		    }
		}	
	}
	

	private final Button sendButton_ = new Button("Send");
	private final Button discardButton_ = new Button("Discard");
	private final GuanotesApp guanotes_;
	public GuanoteEditor(GuanotesApp app, Guanote g) {
		super(g, "New Guanote");
		try {
			this.setTitle(app.owner().getProfile().username() + ":: New Guanote");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		guanotes_ = app;
		
		from_.setVisible(false);
		fromLbl.setVisible(false);
		
		to_.addMouseListener(new PopupListener());

		
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(sendButton_);
		buttonPanel.add(discardButton_);
		add(buttonPanel);
		
		sendButton_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				sendGuanote();
			}
		});
		
		discardButton_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
				dispose();
			}
		});
		
		try {
			from_.setText(app.owner().getProfile().username());
		} catch (Exception e) {
			e.printStackTrace();
		}
		from_.setEditable(false);
		
		// add a combobox containing the names of the user's flocks
		final JComboBox flockList = new JComboBox();
		String[] flockNames = getFlockNames(app);
		for (int i = 0; i < flockNames.length; i++) {
			flockList.addItem(flockNames[i]);
		}
        
        headers_.add(flockList);
		
		flockList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				to_.setText(to_.getText()
				  + (to_.getText().equals("") ? "" : ", ")
				  + flockList.getSelectedItem());
			}
		});
		
		pack();
	}
	
	private String[] getFlockNames(GuanotesApp app) {
		Flockr owner;
		try {
			owner = app.owner();
			Flock[] flocks = owner.getFlocks();
			String[] names = new String[flocks.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = flocks[i].getName();
			}
			return names;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void sendGuanote() {
		String rcvr = this.to_.getText();
		String sdr = this.from_.getText();
		String msg = this.message_.getText();
		try {
		    String[] names = (rcvr.replaceAll(" ","")).split(",");
			guanotes_.makeGuanoteFromNames(names, sdr, msg);
			//guanotes_.sendGuanote(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// close window after sending
		this.setVisible(false);
		this.dispose();
	}
	
	public static void main(String[] args) {
		GuanoteEditor gui = new GuanoteEditor(GuanotesApp._TESTAPP_, new Guanote() {
			public String getSender() { return "you"; };
			public String[] getReceivers() { return new String[] { "" }; };
			public String getMessage() { return "Your text here..."; };
			public String toString() { return getSender() +": "+getMessage(); }
		});
		gui.setVisible(true);
	}

}



