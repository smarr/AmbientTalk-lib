/**
 * 
 */
package frameworks.urbiflock.ui;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
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
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;

import edu.vub.at.objects.natives.grammar.AGSymbol;

/**
 * @author alombide
 *
 */
public class FlockEditor extends Frame implements ActionListener, ItemListener {
	
	private Flock theFlock_;
	private Flockr owner_;
	
	TextField flockNameField_ = new TextField("", 30);
	private Button saveButton_ = new Button("Save");
	
	private Panel theProximitiesPanel_;
	private Vector theProximityPanels_ = new Vector();
	private Choice theProximityTypeChooser_ = new Choice();
	private Button addProximityButton_ = new Button("+");
	private Panel isFriendPanel_ = new Panel(new FlowLayout(FlowLayout.LEFT));
	private Panel isNearbyPanel_ = new Panel(new FlowLayout(FlowLayout.LEFT));
	private Checkbox isFriendCheckbox_ = new Checkbox("Is friend");
	private Checkbox isNearbyCheckbox_ = new Checkbox("Is nearby");
	private Button removeIsFriendProximityButton_ = new Button("-");
	private Button removeIsNearbyProximityButton_ = new Button("-");
	private Panel isFriendProximityPanel_;
	private Panel isNearbyProximityPanel_;
	
	boolean shouldCreateIsFriendProximity_ = false;
	boolean shouldCreateIsNearbyProximity_ = false;
	
	public FlockEditor(Flock flock, Flockr flockr) {
		super("Flock Editor");
		
		if (flockr.hasOpenFlockEditor()) {
			new OpenFlockEditorDialog();
			dispose();
			return;
		}
		
		theFlock_ = flock;
		owner_ = flockr;
	
		theProximityTypeChooser_.add("Matches profile");
		theProximityTypeChooser_.add("Is friend");
		theProximityTypeChooser_.add("Is nearby");
		theProximityTypeChooser_.addItemListener(this);
		
		addProximityButton_.setActionCommand("addProximity");
		addProximityButton_.addActionListener(this);
		
		isFriendPanel_.add(isFriendCheckbox_);
		removeIsFriendProximityButton_.setActionCommand("removeIsFriendProximity");
		removeIsFriendProximityButton_.addActionListener(this);
		isFriendPanel_.add(removeIsFriendProximityButton_);
		isNearbyPanel_.add(isNearbyCheckbox_);
		removeIsNearbyProximityButton_.setActionCommand("removeIsNearbyProximity");
		removeIsNearbyProximityButton_.addActionListener(this);
		isNearbyPanel_.add(removeIsNearbyProximityButton_);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Panel flockNamePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		Label flockNameLabel = new Label("Name:");
		flockNamePanel.add(flockNameLabel);
		flockNamePanel.add(flockNameField_);
		add(flockNamePanel);
		
		theProximitiesPanel_ = new Panel();
		theProximitiesPanel_.setLayout(new BoxLayout(theProximitiesPanel_, BoxLayout.Y_AXIS));
		add(theProximitiesPanel_);
		
		saveButton_.setActionCommand("Save");
		saveButton_.addActionListener(this);
		add(saveButton_);
		
		addEmptyProximityPanel();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
		        dispose();
		    }
		});
		
		owner_.setHasOpenFlockEditor(true);
		
		pack();
		setVisible(true);
	}
	
	public void dispose() {
		owner_.setHasOpenFlockEditor(false);
		super.dispose();
	}
	
	private void addEmptyProximityPanel() {
		Panel thePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		thePanel.add(theProximityTypeChooser_);
		theProximityPanels_.add(thePanel);
		theProximitiesPanel_.add(thePanel);
		pack();
	}
	
	private void addAddProximityButton() {
		theProximitiesPanel_.add(addProximityButton_);
		pack();
	}
	
	private void replaceEmptyProximityPanelWithProfileFieldProximityPanel() {
		Panel thePanel = (Panel)theProximityPanels_.lastElement();
		thePanel.removeAll();
		thePanel.add(new ProfileFieldMatchProximityPanel(this, thePanel));
		
		pack();
	}
	
	private void replaceEmptyProximityPanelWithIsFriendProximityPanel() {
		Panel thePanel = (Panel)theProximityPanels_.lastElement();
		thePanel.removeAll();
		thePanel.add(isFriendPanel_);
		isFriendProximityPanel_ = thePanel;
		theProximityTypeChooser_.remove("Is friend");
		
		pack();
	}
	
	private void replaceEmptyProximityPanelWithIsNearbyProximityPanel() {
		Panel thePanel = (Panel)theProximityPanels_.lastElement();
		thePanel.removeAll();
		thePanel.add(isNearbyPanel_);
		isNearbyProximityPanel_ = thePanel;
		theProximityTypeChooser_.remove("Is nearby");
		
		pack();
	}
	
	public void removeProximityPanel(Panel panel) {
		theProximityPanels_.remove(panel);
		theProximitiesPanel_.remove(panel);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		String fieldName = "";
		if (command == "Save") {
			try {
				// Here a new flock is being created.
				boolean shouldBeFriend = false;
				boolean shouldBeNearby = false;
				// Keep a representation of the GUI contents such that they can
				// be processed by the part of the framework written in AmbientTalk.
				Vector fieldMatchers = new Vector();
				Iterator proximityPanelIterator = theProximityPanels_.iterator();
				while (proximityPanelIterator.hasNext()) {
					Panel proximityPanel = (Panel)((Panel)(proximityPanelIterator.next())).getComponent(0);
					if (proximityPanel.equals(isFriendPanel_)) {
						shouldBeFriend = ((Checkbox)proximityPanel.getComponent(0)).getState();
						break;
					}
					if (proximityPanel.equals(isNearbyPanel_)) {
						shouldBeNearby = ((Checkbox)proximityPanel.getComponent(0)).getState();
						break;
					}
					// For each profile field proximity panel, create a fieldMatcher representation.
					ProfileFieldMatchProximityPanel fieldPanel = (ProfileFieldMatchProximityPanel)proximityPanel;
					Vector fieldMatcher = new Vector();
					fieldName = fieldPanel.getFieldName();
					fieldMatcher.add(AGSymbol.jAlloc(fieldName));
					fieldMatcher.add(fieldPanel.getFieldType());
					fieldMatcher.add(fieldPanel.getComparator());
					fieldMatcher.add(fieldPanel.getFieldValue());
					fieldMatchers.add(fieldMatcher);
				}
				// Make the local flockr create a new flock using the information extracted from the GUI.
				owner_.createFlockFromFieldMatchers(
						flockNameField_.getText(), 
						fieldMatchers,
						(shouldCreateIsFriendProximity_ & shouldBeFriend),
						(shouldCreateIsNearbyProximity_ & shouldBeNearby)
				);
				dispose();
				return;
			} catch(IllegalArgumentException exc) {
				showInvalidValueDialog(fieldName);
			}
		}
		if (command == "addProximity") {
			theProximitiesPanel_.remove(addProximityButton_);
			addEmptyProximityPanel();
			pack();
			return;
		}
		if (command == "removeIsFriendProximity") {
			theProximitiesPanel_.remove(isFriendProximityPanel_);
			theProximityPanels_.remove(isFriendProximityPanel_);
			theProximityTypeChooser_.add("Is friend");
			shouldCreateIsFriendProximity_ = false;
			pack();
			return;
		}
		if (command == "removeIsNearbyProximity") {
			theProximitiesPanel_.remove(isNearbyProximityPanel_);
			theProximityPanels_.remove(isNearbyProximityPanel_);
			theProximityTypeChooser_.add("Is nearby");
			shouldCreateIsNearbyProximity_ = false;
			pack();
			return;
		}
	}

	public void itemStateChanged(ItemEvent e) {
		String selectedItem = (String)e.getItem();
		if (selectedItem == "Matches profile") {
			replaceEmptyProximityPanelWithProfileFieldProximityPanel();
		}
		if (selectedItem == "Is friend") {
			replaceEmptyProximityPanelWithIsFriendProximityPanel();
			shouldCreateIsFriendProximity_ = true;
		}
		if (selectedItem == "Is nearby") {
			replaceEmptyProximityPanelWithIsNearbyProximityPanel();
			shouldCreateIsNearbyProximity_ = true;
		}
		addAddProximityButton();
	}
	
	public Flockr getFlockr() {
		return owner_;
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
	
	private class OpenFlockEditorDialog extends Frame implements ActionListener {
		public OpenFlockEditorDialog() {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new Label("You have already an open Flock Editor."));
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

}
