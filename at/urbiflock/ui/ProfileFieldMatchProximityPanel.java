/**
 * 
 */
package at.urbiflock.ui;

import java.awt.Button;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import edu.vub.at.objects.natives.grammar.AGSymbol;

/**
 * @author alombide
 *
 */
public class ProfileFieldMatchProximityPanel extends Panel implements ItemListener, ActionListener {
	
	private FlockEditor editor_;
	private Panel parentPanel_;
	private TextField fieldNameField_ = new TextField("", 20);
	private Choice fieldTypeChooser_ = new Choice();
	private Choice comparatorChooser_ = new Choice();
	private TextField fieldValueField_ = new TextField("", 20);
	private Button removeProximityButton_ = new Button("-");
	private AbstractFieldType[] possibleTypes_;
	private AbstractFieldType selectedFieldType_;
	

	public ProfileFieldMatchProximityPanel(FlockEditor editor, Panel parentPanel) {
		super(new FlowLayout(FlowLayout.LEFT));
		
		editor_ = editor;
		parentPanel_ = parentPanel;
		
		possibleTypes_ = editor_.getFlockr().getProfile().possibleTypes();
		selectedFieldType_ = possibleTypes_[0];
		
		removeProximityButton_.setActionCommand("removeProximity");
		removeProximityButton_.addActionListener(this);
		
		for (int i = 0; i < possibleTypes_.length; i++) {
			fieldTypeChooser_.add((possibleTypes_[i]).name());
		}
		fieldTypeChooser_.addItemListener(this);
		
		add(fieldNameField_);
		add(fieldTypeChooser_);
		add(comparatorChooser_);
		add(fieldValueField_);
		add(removeProximityButton_);
		
		fieldTypeChooser_.select(0);
		
		updateComparatorChooser();
	}
	
	public String getFieldName() { 
		return fieldNameField_.getText(); 
	}
	
	public AbstractFieldType getFieldType() { 
		return selectedFieldType_;
	}
	
	public AGSymbol getComparator() {
		return AGSymbol.jAlloc(comparatorChooser_.getSelectedItem());
	}
	
	public String getFieldValue() { 
		return fieldValueField_.getText(); 
	}
	
	private void updateComparatorChooser() {
		AGSymbol[] possibleComparators = selectedFieldType_.comparators();
		comparatorChooser_.removeAll();
		for (int i = 0; i < possibleComparators.length; i++) {
			comparatorChooser_.add((possibleComparators[i].toString()));
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().equals(fieldTypeChooser_)) {
			selectedFieldType_ = editor_.getFlockr().getProfile().getFieldTypeWithName((String)e.getItem());
			updateComparatorChooser();
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
