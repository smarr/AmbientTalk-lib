/**
 * AmbientTalk/2 Project
 * GuanoteView.java created on 3 nov 2008 at 16:08:53
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;

/**
 * A superclass used to factor out the behavior common
 * to a Guanote Editor and a Guanote Reader.
 * 
 * @author tvcutsem
 */
public abstract class GuanoteView extends Frame {
	
	protected final TextField from_ = new TextField();
	protected final Label fromLbl = new Label("From:");

	protected final TextField to_ = new TextField();
	protected final TextArea message_ = new TextArea();
	protected final Panel contentPanel_ = new Panel();
	protected final Panel headers_ = new Panel();

	public GuanoteView(Guanote g, String title) {
		super(title);
		try {
			from_.setText(g.getSenderName());
			String[] receivers = g.getReceiverList();
			StringBuffer allReceivers = new StringBuffer("");
			if (receivers.length >= 1) {
				allReceivers = allReceivers.append(receivers[0]);
				for (int i = 1; i < receivers.length; i++) {
					allReceivers = allReceivers.append(","+receivers[i]);
				}
			}
			to_.setText(allReceivers.toString());
			message_.setText(g.message());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//from_.setBackground(Color.YELLOW.darker());
		//to_.setBackground(Color.YELLOW.darker());
		
		//setBackground(Color.YELLOW.brighter().brighter());
		
		
		message_.setBackground( new Color(205,198,97) );
		message_.setFont(Font.decode("Marker Felt-18"));
		message_.setMinimumSize(new Dimension(1,1));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		contentPanel_.setLayout(new BoxLayout(contentPanel_, BoxLayout.Y_AXIS));
		
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        headers_.setLayout(gridbag);
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.BOTH;
        
        // FROM LABEL
        c.weightx = 0;
        gridbag.setConstraints(fromLbl, c);
        headers_.add(fromLbl);
        fromLbl.setAlignment((int) fromLbl.RIGHT_ALIGNMENT);
		
        
        // FROM TEXTFIELD
        c.weightx = 1;
        gridbag.setConstraints(from_, c);
        headers_.add(from_);
                
        // new row
        c.gridy = 1;
        
        // TO LABEL
        c.weightx = 0;
		Label toLbl = new Label("To: ");
        gridbag.setConstraints(toLbl, c);
        headers_.add(toLbl);
        toLbl.setAlignment((int) fromLbl.RIGHT_ALIGNMENT);
        
        // TO TEXTFIELD
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(to_, c);
        headers_.add(to_);
		
		contentPanel_.add(headers_);
		contentPanel_.add(message_);
		add(contentPanel_);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
			}
		});
				
	}
	
}
