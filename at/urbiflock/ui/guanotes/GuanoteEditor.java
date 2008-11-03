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

import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;

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
		
		from_.setEditable(false);
		
		pack();
	}
	
	protected void sendGuanote() {
		String rcvr = this.to_.getText();
		String sdr = this.from_.getText();
		String msg = this.message_.getText();
		try {
			Guanote g = guanotes_.makeGuanote(rcvr, sdr, msg);
			guanotes_.send(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		GuanoteEditor gui = new GuanoteEditor(GuanotesApp._TESTAPP_, new Guanote() {
			public String sender() { return "you"; };
			public String receiver() { return ""; };
			public String message() { return "Your text here..."; };
			public String toString() { return sender() +": "+message(); }
		});
		gui.setVisible(true);
	}

}
