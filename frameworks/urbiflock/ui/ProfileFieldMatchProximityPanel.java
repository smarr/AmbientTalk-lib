/**
 * 
 */
package frameworks.urbiflock.ui;

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
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;

import edu.vub.at.objects.natives.NATNumber;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.grammar.AGSymbol;

/**
 * @author alombide
 *
 */
public class ProfileFieldMatchProximityPanel extends Panel implements ItemListener, ActionListener {
	
	private FlockEditor editor_;
	private Panel parentPanel_;
	private Choice fieldNameChooser_ = new Choice();
	//private Choice fieldTypeChooser_ = new Choice();
	private Choice comparatorChooser_ = new Choice();
	private Component fieldValueField_ = new TextField("", 20);
	private Button removeProximityButton_ = new Button("-");
	private AbstractFieldType[] possibleTypes_;
	private AbstractFieldType selectedFieldType_;
	final int fieldValueFieldIndex_ = 2;
	

	public ProfileFieldMatchProximityPanel(FlockEditor editor, Panel parentPanel) {
		super(new FlowLayout(FlowLayout.LEFT));
		
		editor_ = editor;
		parentPanel_ = parentPanel;
		
		Profile profile = editor_.getFlockr().getProfile();
		Set possibleFields = profile.getFieldNamesSet();
		
		Iterator possibleFieldsIt = possibleFields.iterator();
		while (possibleFieldsIt.hasNext()) {
			fieldNameChooser_.add(((AGSymbol)possibleFieldsIt.next()).toString());
		}
		
		possibleTypes_ = profile.possibleTypes();
		selectedFieldType_ = possibleTypes_[0];
		
		removeProximityButton_.setActionCommand("removeProximity");
		removeProximityButton_.addActionListener(this);
		
		/*for (int i = 0; i < possibleTypes_.length; i++) {
			fieldTypeChooser_.add((possibleTypes_[i]).name());
		}
		fieldTypeChooser_.addItemListener(this);*/
		
		fieldNameChooser_.addItemListener(this);
		
		add(fieldNameChooser_);
		//add(fieldTypeChooser_);
		add(comparatorChooser_);
		add(fieldValueField_);
		add(removeProximityButton_);
		
		//fieldTypeChooser_.select(0);

		fieldNameChooser_.select(0);
		switchFieldValueFieldType();
	}
	
	public String getFieldName() { 
		return fieldNameChooser_.getSelectedItem(); 
	}
	
	public AbstractFieldType getFieldType() { 
		return selectedFieldType_;
	}
	
	public AGSymbol getComparator() {
		return AGSymbol.jAlloc(comparatorChooser_.getSelectedItem());
	}
	
	public Object getFieldValue() { 
		return getFieldValueFromComponent(fieldValueField_);
	}
	
	private void updateComparatorChooser() {
		AGSymbol[] possibleComparators = selectedFieldType_.comparators();
		comparatorChooser_.removeAll();
		for (int i = 0; i < possibleComparators.length; i++) {
			comparatorChooser_.add((possibleComparators[i].toString()));
		}
	}
	
	public Component createComponentForFieldType(AbstractFieldType fieldType) {
		if (fieldType.isString()) {
			TextField tf = new TextField(((NATText)fieldType.defaultValue()).javaValue, fieldType.getFieldSize());
			return tf;
		}
		if (fieldType.isInteger()) {
			TextField tf = new TextField(((NATNumber)fieldType.defaultValue()).toString(), fieldType.getFieldSize());
			return tf;
		}
		if (fieldType.isEnumeration()) {
			Choice chooser = new Choice();
			AGSymbol[] choices = fieldType.getPossibleValues();
			for (int i = 0; i < choices.length; i++) {
				chooser.add(choices[i].toString());
			}
			chooser.select(((AGSymbol)fieldType.defaultValue()).toString());
			return chooser;
		}
		if (fieldType.isDate()) {
			Calendar calendar = (Calendar)fieldType.defaultValue();
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			TextField dayField = new TextField(Integer.toString(day));
			TextField monthField = new TextField(Integer.toString(month + 1));
			TextField yearField = new TextField(Integer.toString(year));
			dayField.setName("day");
			monthField.setName("month");
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
	
	private Object getFieldValueFromComponent(Component component) throws IllegalArgumentException {
		AGSymbol key = AGSymbol.jAlloc(component.getName());
		Profile profile = editor_.getFlockr().getProfile();
		AbstractFieldType fieldType = profile.getFieldType(AGSymbol.jAlloc(fieldNameChooser_.getSelectedItem()));
		if (fieldType.isString()) {
			String theString = ((TextField)component).getText();
			NATText value = NATText.atValue(theString);
			if (fieldType.isPossibleValue(value)) {
				return value;
			} else {
				throw new IllegalArgumentException();
			}
		}
		if (fieldType.isInteger()) {
			try {
				NATNumber value = NATNumber.atValue(Integer.parseInt(((TextField)component).getText()));
				if (fieldType.isPossibleValue(value)) {
					return value;
				} else {
					throw new IllegalArgumentException();
				}
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException();
			}
		}
		if (fieldType.isEnumeration()) {
			AGSymbol value = AGSymbol.jAlloc(((Choice)component).getSelectedItem());
			if (fieldType.isPossibleValue(value)) {
				return value;
			} else {
				throw new IllegalArgumentException();
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
					return calendar;
				} else {
					throw new IllegalArgumentException();
				}
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException();
			}
		}
		throw new IllegalArgumentException();
	}
	
	public void switchFieldValueFieldType() {
		selectedFieldType_ = editor_.getFlockr().getProfile().getFieldType(AGSymbol.jAlloc(fieldNameChooser_.getSelectedItem()));
		fieldValueField_ = createComponentForFieldType(selectedFieldType_);
		remove(fieldValueFieldIndex_);
		add(fieldValueField_, fieldValueFieldIndex_);
		updateComparatorChooser();
		validate();
		editor_.pack();
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().equals(fieldNameChooser_)) {
			switchFieldValueFieldType();
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = (String)e.getActionCommand();
		if (command == "removeProximity") {
			editor_.removeProximityPanel(parentPanel_);
			return;
		}
	}
}
