/**
 * AmbientTalk/2 Project
 * GuanoteList.java created on 3 nov 2008 at 16:13:01
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
package at.urbiflock.ui.guanotes;


import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.List;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BoxLayout;

/**
 * A GUI to read the list of received guanotes, and to
 * create new and delete received guanotes.
 * 
 * @author tvcutsem
 */
public class GuanoteList extends Frame implements GuanoteListener {
	
	private Button newGuanote_ = new Button("New");
	private Button deleteGuanote_ = new Button("Delete");
	
	private List guanoteList_ = new List();
	private Vector guanoteListModel_ = new Vector();
	
	private final GuanotesApp guanotes_;
	
	public GuanoteList(final GuanotesApp app) {
		super("Guanotes Inbox");
		try {
			this.setTitle(app.owner().getProfile().username() + ":: Guanotes Inbox");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		guanotes_ = app;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		ScrollPane listPane = new ScrollPane();
		listPane.add(guanoteList_);
		listPane.setMinimumSize(new Dimension(1,1));
		add(listPane);
		
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(newGuanote_);
		buttonPanel.add(deleteGuanote_);
		
		add(buttonPanel);
		
		guanoteList_.setFont(Font.decode("Marker Felt-18"));

		guanoteList_.setBackground(new Color(205,198,97));
		
		guanoteList_.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() > 1) {
					int idx = guanoteList_.getSelectedIndex();
					if (idx != -1) {
						Guanote g = (Guanote) guanoteListModel_.get(idx);
						GuanoteReader reader = new GuanoteReader(app,g);
						reader.setVisible(true);
					}
				}
			}
		});
		
		newGuanote_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				newGuanote();
			}
		});
		deleteGuanote_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int[] toDelete = guanoteList_.getSelectedIndexes();
				if (toDelete.length > 0) {
					deleteGuanotes(toDelete);					
				}
			}
		});
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
				    guanotes_.stop();		
				} catch(Exception exc) {
					exc.printStackTrace();
				}
				
				setVisible(false);
				dispose();
			}
		});
		
		try {
		    guanotes_.listenForGuanotesToOwner(this);		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		pack();
		this.setSize(400, 200);
		this.setVisible(true);
		
	}
	
	public void guanoteReceived(Guanote g) throws Exception {
		guanoteList_.add(shorten(g.getSender() + ": " + g.getMessage()));
		guanoteListModel_.add(g);
	}
	
	protected void newGuanote() {
		GuanoteEditor editor = new GuanoteEditor(guanotes_, Guanote._EMPTY_);
		editor.setVisible(true);
	}
	
	protected void deleteGuanotes(int[] toDeleteIndices) {
		for (int i = 0; i < toDeleteIndices.length; i++) {
			guanoteList_.remove(toDeleteIndices[i]);
			guanoteListModel_.remove(toDeleteIndices[i]);
		}
	}
	
	private String shorten(String s) {
		int max = guanoteList_.getWidth();
		if (s.length() > max) {
			return s.substring(0, max) + "...";
		} else {
			return s;
		}
	}
	
	public static void main(String[] args) {
		GuanoteList gui = new GuanoteList(GuanotesApp._TESTAPP_);
		
		try {
			gui.guanoteReceived(new Guanote() {
				public String getSender() { return "sender"; };
				public String[] getReceivers() { return new String[] { "rcvr" }; };
				public String getMessage() { return "test body test test test"; };
				public String toString() { return getSender() +": "+getMessage(); }
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
