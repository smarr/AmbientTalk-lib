/**
 * AmbientTalk/2 Project
 * ProfileViewer.java created on 5 sep 2008 at 16:04:09
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

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;

/**
 * The UI of a Urbiflock Profile (by default viewer, editor for own profile)
 */
public class ProfileViewer extends Frame {

	private boolean editable_ = false;
	
	public ProfileViewer(Profile p, boolean editable) {
		editable_ = editable;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	
		
		Iterator keysIterator = p.propertyHashMap().keySet().iterator();
		while (keysIterator.hasNext()) {
			String key = (String)keysIterator.next();
			String value = (String)(p.propertyHashMap().get(key));
			addFieldPanel(key, value);
		}
		
		pack();
		setVisible(true);
	}
	
	public void addFieldPanel(String fieldName, String fieldValue) {
		Panel thePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		thePanel.add(new Label(fieldName));
		TextField textField = new TextField(fieldValue);
		textField.setEditable(editable_);
		thePanel.add(textField);
		add(thePanel);
	}
	
	public static void main(String[] args) {
		final HashMap propertyMap = new HashMap();
		propertyMap.put("username", "foobar");
		propertyMap.put("firstname", "foo");
		propertyMap.put("lastname", "bar");
		ProfileViewer v = new ProfileViewer(new Profile() {
			 public HashMap propertyHashMap() { return propertyMap; }; 
		}, true);
	}
	
}
