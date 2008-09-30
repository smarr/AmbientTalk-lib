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
import java.awt.Choice;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;

import edu.vub.at.actors.natives.NATFarReference;
import edu.vub.at.objects.natives.NATNil;
import edu.vub.at.objects.natives.NATNumber;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.grammar.AGSymbol;

/**
 * The UI of a Urbiflock Profile (by default viewer, editor for own profile)
 */
public class ProfileViewer extends Frame implements ActionListener {

	private boolean editable_ = false;
	private Profile profile_;
	private Panel fieldsPanel_ = new Panel();
	private Vector textFields_ = new Vector();
	private Flockr localFlockr_;
	private Vector matchingFieldCheckBoxes_ = new Vector();
	
	public ProfileViewer(Flockr theLocalFlockr, Profile p, boolean editable) {
		super("Profile Viewer");

		fieldsPanel_.setLayout(new BoxLayout(fieldsPanel_, BoxLayout.Y_AXIS));
		editable_ = editable;
		profile_ = p;
		localFlockr_ = theLocalFlockr;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(fieldsPanel_);
		updateGUIWithProfile(p);
		
		Button addFieldButton = new Button("Add Field");
		addFieldButton.setEnabled(editable_);
		addFieldButton.setActionCommand("addField");
		addFieldButton.addActionListener(this);
		add(addFieldButton);
		
		Button okButton = new Button("Ok");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		add(okButton);
		
		pack();
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
		        dispose();
		    }
		});
		
		localFlockr_.registerProfileChangedListener(this);
	}
	
	public void dispose() {
		localFlockr_.removeProfileChangedListener(this);
		super.dispose();
	}
	
	public Flockr getLocalFlockr() {
		return localFlockr_;
	}
	
	public Profile getProfile() {
		return profile_;
	}
	
	public Vector getFields() {
		return textFields_;
	}
	
	public Panel getFieldsPanel() {
		return fieldsPanel_;
	}
	
	public boolean isEditable() {
		return editable_;
	}
	
	public Vector getMatchingFieldCheckBoxes() {
		return matchingFieldCheckBoxes_;
	}
	
	public Component createComponentForFieldType(AbstractFieldType fieldType, Object fieldValue) {
		if (fieldType.isString()) {
			TextField tf = new TextField(((NATText)fieldValue).javaValue, fieldType.getFieldSize());
			tf.setEditable(editable_);
			return tf;
		}
		if (fieldType.isInteger()) {
			TextField tf = new TextField(((NATNumber)fieldValue).toString());
			tf.setEditable(editable_);
			return tf;
		}
		if (fieldType.isEnumeration()) {
			Choice chooser = new Choice();
			AGSymbol[] choices = fieldType.getPossibleValues();
			for (int i = 0; i < choices.length; i++) {
				chooser.add(choices[i].toString());
			}
			chooser.select(((AGSymbol)fieldValue).toString());
			chooser.setEnabled(editable_);
			return chooser;
		}
		if (fieldType.isDate()) {
			Calendar calendar = (Calendar)fieldValue;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			TextField dayField = new TextField(Integer.toString(day));
			TextField monthField = new TextField(Integer.toString(month + 1));
			TextField yearField = new TextField(Integer.toString(year));
			dayField.setName("day");
			dayField.setEditable(editable_);
			monthField.setEditable(editable_);
			monthField.setName("month");
			yearField.setEditable(editable_);
			yearField.setName("year");
			Panel datePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			datePanel.add(dayField);
			datePanel.add(new Label("/"));
			datePanel.add(monthField);
			datePanel.add(new Label("/"));
			datePanel.add(yearField);
			return datePanel;
		}
		return null;
	}
	
	public void addMatchingCheckboxForField(Panel panel, String fieldName) {
		
	}
	
	public void addFieldPanel(String fieldName, Object fieldValue) {
		Panel thePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		addMatchingCheckboxForField(thePanel, fieldName);
		Label label = new Label(fieldName);
		thePanel.add(label);
		
		AbstractFieldType fieldType = profile_.getFieldType(AGSymbol.jAlloc(fieldName));
		Component theEditableComponent = createComponentForFieldType(fieldType, fieldValue);
		theEditableComponent.setName(fieldName);
		textFields_.add(theEditableComponent);
		thePanel.add(theEditableComponent);
		
		Button removeFieldButton = new Button("remove");
		removeFieldButton.setEnabled(editable_);
		removeFieldButton.setActionCommand("removeField_" + fieldName);
		removeFieldButton.addActionListener(this);
		thePanel.add(removeFieldButton);
		thePanel.setName(fieldName);
		fieldsPanel_.add(thePanel);
		this.pack();
	}
	
	private void showInvalidValueDialog(String fieldKey) {
		new InvalidValueDialog(fieldKey);
	}
	
	private class InvalidValueDialog extends Frame implements ActionListener {
		
		public InvalidValueDialog(String fieldKey) {
			
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new Label("Invalid value for field: " + fieldKey));
			Button okButton = new Button("Ok");
			okButton.addActionListener(this);
			okButton.setActionCommand("ok");
			add(okButton);
		
			pack();
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			this.dispose();
		}
	}
	
	private boolean setProfileFieldWithValueFromComponent(Component component) {
		AGSymbol key = AGSymbol.jAlloc(component.getName());
		AbstractFieldType fieldType = profile_.getFieldType(key);
		if (fieldType.isString()) {
			NATText value = NATText.atValue(((TextField)component).getText());
			if (fieldType.isPossibleValue(value)) {
				profile_.setField(key, value);
				return true;
			} else {
				showInvalidValueDialog(key.toString());
				return false;
			}
		}
		if (fieldType.isInteger()) {
			try {
				NATNumber value = NATNumber.atValue(Integer.parseInt(((TextField)component).getText()));
				if (fieldType.isPossibleValue(value)) {
					profile_.setField(key, value);
					return true;
				} else {
					showInvalidValueDialog(key.toString());
					return false;
				}
			} catch(NumberFormatException e) {
				showInvalidValueDialog(key.toString());
				return false;
			}
		}
		if (fieldType.isEnumeration()) {
			AGSymbol value = AGSymbol.jAlloc(((Choice)component).getSelectedItem());
			if (fieldType.isPossibleValue(value)) {
				profile_.setField(key, value);
				return true;
			} else {
				showInvalidValueDialog(key.toString());
				return false;
			}
		}
		if (fieldType.isDate()) {
			Panel datePanel = (Panel)component;
			try {
				int day = Integer.parseInt(((TextField)datePanel.getComponent(0)).getText());
				int month = Integer.parseInt(((TextField)datePanel.getComponent(2)).getText());
				int year = Integer.parseInt(((TextField)datePanel.getComponent(4)).getText());
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, (month - 1), day);
				if (fieldType.isPossibleValue(calendar)) {
					profile_.setField(key, calendar);
					return true;
				} else {
					showInvalidValueDialog(key.toString());
					return false;
				}
			} catch(NumberFormatException e) {
				showInvalidValueDialog(key.toString());
				return false;
			}
		}
		return false;
	}
	
	public void updateTheMatchingProfile() { }
	
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command == "ok") {
			Iterator it = textFields_.iterator();
			boolean fieldsOk = true;
			while (it.hasNext()) {
				Component component = (Component)it.next();
				if (!setProfileFieldWithValueFromComponent(component)) {
					fieldsOk = false;
				}
			}
			if (fieldsOk) { 
				updateTheMatchingProfile();
				this.dispose(); 
			};
			return;
		}
		if (command == "addField") {
			new AddFieldDialog(this, profile_);
			return;
		}
		if ((command.substring(0, 12)).equals("removeField_")) {
			String fieldNameString = command.substring(12);
			AGSymbol fieldName = AGSymbol.jAlloc(fieldNameString);
			if (profile_.isMandatoryField(fieldName)) {
				new MandatoryFieldRemovalDialog();
			} else {
				Component[] components = fieldsPanel_.getComponents();
				for (int i = 0; i < components.length; i++) {
					if (components[i].getName().equals(fieldNameString)) {
						fieldsPanel_.remove(components[i]);
						profile_.removeField(fieldName);
						this.pack();
					}
				}
				Iterator tfIt = textFields_.iterator();
				while (tfIt.hasNext()) {
					Component field = (Component)tfIt.next();
					if (field.getName().equals(fieldNameString)) {
						tfIt.remove();
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
	
	private class AddFieldDialog extends Frame implements ActionListener, ItemListener {
		
		TextField fieldNameTextField_ = new TextField(10);
		TextField fieldValueTextField_ = new TextField();
		Vector enumerationValuePanels_ = new Vector();
		Vector enumerationValuesTextFields_ = new Vector();
		Button addChoiceButton_;
		boolean isShowingEnumerationType_ = false;
		Choice typeChooser_;
		
		Profile profile_;
		ProfileViewer profileViewer_;
		
		public AddFieldDialog(ProfileViewer pv, Profile p) {
			super("Add profile info");
			profileViewer_ = pv;
			profile_ = p;
			
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			Panel fieldNamePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			fieldNamePanel.add(new Label("Name"));
			fieldNamePanel.add(fieldNameTextField_);
			add(fieldNamePanel);
			//Panel fieldValuePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			//fieldNamePanel.add(new Label("Value"));
			//fieldNamePanel.add(fieldValueTextField_);
			//add(fieldValuePanel);
			
			Panel fieldTypePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			typeChooser_ = new Choice();
			AbstractFieldType[] types = p.possibleTypes();
			for (int i = 0; i < types.length; i++) {
				typeChooser_.add((types[i]).name());
				typeChooser_.addItemListener(this);
			}
			fieldTypePanel.add(typeChooser_);
			add(fieldTypePanel);
			
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
		
		private AbstractFieldType createTypeObject() {
			String choice = typeChooser_.getSelectedItem();
			AbstractFieldType result = null;
			if (choice.equals("Text")) {
				result = profile_.makeStringFieldTypeObject();
			}
			if (choice.equals("Integer")) {
				result = profile_.makeIntegerTypeFieldObject(0, 100000);
			}
			if (choice.equals("Choice")) {
				Vector enumerationValues = new Vector();
				Iterator it = enumerationValuesTextFields_.iterator();
				while (it.hasNext()) {
					enumerationValues.add(AGSymbol.jAlloc(((TextField)it.next()).getText()));
				}
				result = profile_.makeEnumerationFieldTypeObject(enumerationValues.toArray());
			}
			if (choice.equals("Date")) {
				result = profile_.makeDateTypeFieldObject(null, null);
			}
			return result;
		}
		
		public void actionPerformed(ActionEvent ae) {
			String command = ae.getActionCommand();
			if (command == "ok") {
				if (fieldNameTextField_.getText().length() == 0) {
					return;
				}
				AGSymbol fieldName = AGSymbol.jAlloc(fieldNameTextField_.getText());
				AbstractFieldType typeObject = createTypeObject();
				profile_.addField(fieldName, typeObject.defaultValue(), typeObject);
				profileViewer_.addFieldPanel(fieldNameTextField_.getText(), typeObject.defaultValue());
				this.dispose();
				return;
			}
			if (command == "cancel") {
				this.dispose();
				return;
			}
			if (command == "add") {
				addEnumerationValuePanel(enumerationValuePanels_.size());
				return;
			}
			// Remove enumeration value field
			int position = Integer.parseInt(command);
			remove((Panel)enumerationValuePanels_.elementAt(position));
			enumerationValuePanels_.remove(position);
			enumerationValuesTextFields_.remove(position);
			pack();
		}
		
		private void addEnumerationValuePanel(int position) {
			Panel enumerationValuePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
			TextField valueTf = new TextField(20);
			valueTf.setName(Integer.toString(position));
			enumerationValuesTextFields_.add(valueTf);
			enumerationValuePanel.add(valueTf);
			if (position > 0) {
				Button removeButton = new Button("Remove");
				removeButton.setName(Integer.toString(position));
				removeButton.setActionCommand(Integer.toString(position));
				removeButton.addActionListener(this);
				enumerationValuePanel.add(removeButton);
			}
			enumerationValuePanels_.add(enumerationValuePanel);
			add(enumerationValuePanel, (getComponentCount() - 3));
			pack();
		}
		
		public void itemStateChanged(ItemEvent ie) {
			String value = (String)ie.getItem();
			if ((value.equals("Choice")) && (!isShowingEnumerationType_)) {
				addChoiceButton_ = new Button("Add choice");
				addChoiceButton_.setName("add");
				addChoiceButton_.setActionCommand("add");
				addChoiceButton_.addActionListener(this);
				add(addChoiceButton_, (getComponentCount() - 2));
				addEnumerationValuePanel(0);
				isShowingEnumerationType_ = true;
				return;
			}
			if (!value.equals("Choice") && isShowingEnumerationType_) {
				Iterator it = enumerationValuePanels_.iterator();
				while (it.hasNext()) {
					remove((Panel)it.next());
				}
				remove(addChoiceButton_);
				enumerationValuePanels_ = new Vector();
				enumerationValuesTextFields_ = new Vector();
				isShowingEnumerationType_ = false;
				pack();
				return;
			}
		}
	}
	
	
	/*
	 * This event should only be signaled when viewing another (remote) flockr's profile,
	 * since changes to a local flockr's own profile only happen by the use of this GUI.
	 */
	public void notifyProfileChanged(NATFarReference remoteFlockr, Profile profile) {
		updateGUIWithProfile(profile);
	}
	
	private void updateGUIWithProfile(Profile profile) {
		fieldsPanel_.removeAll();
		Iterator keysIterator = profile.propertyHashMap().keySet().iterator();
		while (keysIterator.hasNext()) {
			AGSymbol key = (AGSymbol)keysIterator.next();
			Object value = profile.propertyHashMap().get(key);
			addFieldPanel(key.toString(), value);
		}
	}
	
//	public static void main(String[] args) {
//		final HashMap propertyMap = new HashMap();
//		propertyMap.put(AGSymbol.jAlloc("username"), NATText.atValue("foobar"));
//		propertyMap.put(AGSymbol.jAlloc("firstname"), NATText.atValue("foo"));
//		propertyMap.put(AGSymbol.jAlloc("lastname"), NATText.atValue("bar"));
//		ProfileViewer v = new ProfileViewer(new Profile() {
//			 public HashMap propertyHashMap() { return propertyMap; };
//			 public boolean isMandatoryField(AGSymbol symbol) { return false; };
//			 public void addField(AGSymbol name, NATText value) {  };
//			 public void removeField(AGSymbol fieldName) {  };
//			 public void setField(AGSymbol fieldName, NATText value) {  };
//		}, true);
//	}
	
}
