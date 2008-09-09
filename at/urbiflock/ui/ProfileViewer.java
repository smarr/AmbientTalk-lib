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

import java.awt.Button;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;

import edu.vub.at.objects.natives.NATBoolean;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.grammar.AGSymbol;

/**
 * The UI of a Urbiflock Profile (by default viewer, editor for own profile)
 */
public class ProfileViewer extends Frame implements ActionListener {

	private boolean editable_ = false;
	private Profile profile_;
	private Panel fieldsPanel_ = new Panel();
	
	public ProfileViewer(Profile p, boolean editable) {
		fieldsPanel_.setLayout(new BoxLayout(fieldsPanel_, BoxLayout.Y_AXIS));
		editable_ = editable;
		profile_ = p;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(fieldsPanel_);
		
		Iterator keysIterator = p.propertyHashMap().keySet().iterator();
		while (keysIterator.hasNext()) {
			String key = (String)keysIterator.next();
			String value = (String)(p.propertyHashMap().get(key));
			addFieldPanel(key, value);
		}
		
		Button addFieldButton = new Button("Add Field");
		addFieldButton.setActionCommand("addField");
		addFieldButton.addActionListener(this);
		add(addFieldButton);
		
		pack();
		setVisible(true);
	}
	
	private void addFieldPanel(String fieldName, String fieldValue) {
		Panel thePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		Label label = new Label(fieldName);
		thePanel.add(label);
		TextField textField = new TextField(fieldValue);
		textField.setEditable(editable_);
		thePanel.add(textField);
		Button removeFieldButton = new Button("remove");
		removeFieldButton.setActionCommand("removeField_" + fieldName);
		removeFieldButton.addActionListener(this);
		thePanel.add(removeFieldButton);
		thePanel.setName(fieldName);
		fieldsPanel_.add(thePanel);
		this.pack();
	}
	
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command == "addField") {
			new AddFieldDialog(this, profile_);
			return;
		}
		if ((command.substring(0, 12)).equals("removeField_")) {
			String fieldNameString = command.substring(12);
			AGSymbol fieldName = AGSymbol.jAlloc(fieldNameString);
			if (profile_.isMandatoryField(fieldName).javaValue) {
				new MandatoryFieldRemovalDialog();
			} else {
				Component[] components = fieldsPanel_.getComponents();
				for (int i = 0; i < components.length; i++) {
					if (components[i].getName().equals(fieldNameString)) {
						fieldsPanel_.remove(components[i]);
						this.pack();
					}
				}
			}
			return;
		}
	}
	
	private class MandatoryFieldRemovalDialog extends Frame implements ActionListener {
		public MandatoryFieldRemovalDialog() {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new Label("Cannot remove mandatory field!"));
			Button okButton = new Button("Ok");
			okButton.addActionListener(this);
			add(okButton);
			pack();
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			this.dispose();
		}
	}
	
	private class AddFieldDialog extends Frame implements ActionListener {
		
		TextField fieldNameTextField_ = new TextField();
		TextField fieldValueTextField_ = new TextField();
		Profile profile_;
		ProfileViewer profileViewer_;
		
		public AddFieldDialog(ProfileViewer pv, Profile p) {
			profileViewer_ = pv;
			profile_ = p;
			
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			Panel fieldNamePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			fieldNamePanel.add(new Label("Name"));
			fieldNamePanel.add(fieldNameTextField_);
			add(fieldNamePanel);
			Panel fieldValuePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			fieldNamePanel.add(new Label("Value"));
			fieldNamePanel.add(fieldValueTextField_);
			fieldNamePanel.add(fieldValuePanel);
			add(fieldValuePanel);
			Button okButton = new Button("Ok");
			Button cancelButton = new Button("Cancel");
			okButton.addActionListener(this);
			cancelButton.addActionListener(this);
			okButton.setActionCommand("ok");
			cancelButton.setActionCommand("cancel");
			add(okButton);
			add(cancelButton);
			
			pack();
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			String command = ae.getActionCommand();
			if (command == "ok") {
				if (fieldNameTextField_.getText().length() == 0) {
					return;
				}
				profile_.addField(AGSymbol.jAlloc(fieldNameTextField_.getText()), NATText.atValue(fieldValueTextField_.getText()));
				profileViewer_.addFieldPanel(fieldNameTextField_.getText(), fieldValueTextField_.getText());
				this.dispose();
				return;
			}
			if (command == "cancel") {
				this.dispose();
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		final HashMap propertyMap = new HashMap();
		propertyMap.put("username", "foobar");
		propertyMap.put("firstname", "foo");
		propertyMap.put("lastname", "bar");
		ProfileViewer v = new ProfileViewer(new Profile() {
			 public HashMap propertyHashMap() { return propertyMap; };
			 public NATBoolean isMandatoryField(AGSymbol symbol) { return NATBoolean._FALSE_; };
			 public void addField(AGSymbol name, NATText value) {  };
			 public void removeField(AGSymbol fieldName) {  };
		}, true);
	}
	
}
