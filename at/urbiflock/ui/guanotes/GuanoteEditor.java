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
package at.urbiflock.ui.guanotes;

import edu.vub.at.objects.natives.NATText;
import edu.vub.at.parser.NATParser;

import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import at.urbiflock.ui.Flock;
import at.urbiflock.ui.Flockr;

/**
 * The UI of a Guanote Editor. This GUI allows the user to
 * create and send a new Guanote.
 * 
 * @author tvcutsem
 */
public class GuanoteEditor extends GuanoteView {

	private final Button sendButton_ = new Button("Send");
	private final Button discardButton_ = new Button("Discard");
	private final GuanotesApp guanotes_;
	public GuanoteEditor(GuanotesApp app, Guanote g) {
		super(g, "Guanote Editor");
		guanotes_ = app;
		
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
		    String[] names = rcvr.split(",");
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
			public String getSenderName() { return "you"; };
			public String[] getReceiverList() { return new String[] { "" }; };
			public String message() { return "Your text here..."; };
			public String toString() { return getSenderName() +": "+message(); }
		});
		gui.setVisible(true);
	}

}
