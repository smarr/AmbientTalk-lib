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
package frameworks.urbiflock.ui.ir8u;


import java.awt.Button;
import java.awt.Color;
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
import javax.swing.JOptionPane;

import frameworks.urbiflock.ui.Flockr;
import frameworks.urbiflock.ui.Profile;

/**
 * A GUI to read the list of received guanotes, and to
 * create new and delete received guanotes.
 * 
 * @author tvcutsem
 */
public class IR8U extends Frame {
	
	private Button newRating_ = new Button("Rate Me!");
	private Button deleteRating_ = new Button("Delete");
	
	private List ratingList_ = new List();
	private Vector ratingListModel_ = new Vector();
	
	private final IR8UApp ir8u_;
	
	public IR8U(final IR8UApp app) {
		super("IR8U");
		
		try {
			this.setTitle(app.owner().getProfile().username() + ":: IR8U Log");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ir8u_ = app;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		ScrollPane listPane = new ScrollPane();
		listPane.add(ratingList_);
		
		add(listPane);
		
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(newRating_);
		buttonPanel.add(deleteRating_);
		
		add(buttonPanel);
		
		ratingList_.setFont(Font.decode("Marker Felt-18"));

		ratingList_.setBackground(new Color(50,240,88));
		
		ratingList_.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() > 1) {
					int idx = ratingList_.getSelectedIndex();
					if (idx != -1) {
						Object[] tuple = (Object[]) ratingListModel_.get(idx);
						if (tuple == null) return;
						Flockr toRate = (Flockr) tuple[0];
						String subject = (String) tuple[1];
						String rating = JOptionPane.showInputDialog("Rate " + subject);
						try {
							ir8u_.rateFlockr(toRate, subject, Integer.parseInt(rating));
						} catch ( Exception e ) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		
		newRating_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String subject = JOptionPane.showInputDialog("On what subject would you like to be rated?");
				if (subject != null) {
					try {
						ir8u_.askRatingFor(subject);
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
			}
		});
		deleteRating_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int[] toDelete = ratingList_.getSelectedIndexes();
				if (toDelete.length > 0) {
					deleteGuanotes(toDelete);					
				}
			}
		});
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
				    ir8u_.stop();		
				} catch(Exception exc) {
					exc.printStackTrace();
				}
				
				setVisible(false);
				dispose();
			}
		});
		
		try {
		    // guanotes_.listenForGuanotesToOwner(this);		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		pack();
		this.setSize(400, 200);

		this.setVisible(true);
	}
	
	public void askToRate(Flockr toRate, Profile p, String subject) throws Exception {
		ratingList_.add(p.username() + " wants you to rate him/her on " + subject);
		ratingListModel_.add(new Object[] { toRate, subject });
	}
	
	public void updateRating(String ratingFlockrName, String subject, int rating) {
		ratingList_.add(ratingFlockrName + " gave you a " + rating + " on " + subject);
		ratingListModel_.add(null);
	}
	
	protected void newGuanote() {
		//GuanoteEditor editor = new GuanoteEditor(guanotes_, Guanote._EMPTY_);
		//editor.setVisible(true);
	}
	
	protected void deleteGuanotes(int[] toDeleteIndices) {
		for (int i = 0; i < toDeleteIndices.length; i++) {
			ratingList_.remove(toDeleteIndices[i]);
			ratingListModel_.remove(toDeleteIndices[i]);
		}
	}
	
	private String shorten(String s) {
		int max = ratingList_.getWidth();
		if (s.length() > max) {
			return s.substring(0, max) + "...";
		} else {
			return s;
		}
	}
	
	public static void main(String[] args) {
		// IR8U gui = new IR8U(GuanotesApp._TESTAPP_);
	}

}
