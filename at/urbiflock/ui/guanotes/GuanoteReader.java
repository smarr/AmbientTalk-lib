/**
 * AmbientTalk/2 Project
 * GuanoteReader.java created on 3 nov 2008 at 16:10:55
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

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;

/**
 * The UI of a Guanote Reader. This GUI allows the user to
 * read a previously received Guanote.
 * 
 * @author tvcutsem
 */
public class GuanoteReader extends GuanoteView {
	
	public GuanoteReader(Guanote g) {
		super(g, "Guanote Reader");
		
		from_.setEditable(false);
		to_.setEditable(false);
		message_.setEditable(false);
		
		pack();
	}
	
	public static void main(String[] args) {
		GuanoteReader gui = new GuanoteReader(new Guanote() {
			public String sender() { return "sender"; };
			public String receiver() { return "rcvr"; };
			public String message() { return "test body test test test"; };
			public String toString() { return sender() +": "+message(); }
		});
		gui.setVisible(true);
	}
	
}
