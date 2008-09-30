package at.urbiflock.ui;

import java.awt.Checkbox;
import java.awt.Panel;
import java.util.Iterator;

import edu.vub.at.objects.natives.grammar.AGSymbol;

public class DoesProfileMatchProximityEditor extends ProfileViewer {
	
	public DoesProfileMatchProximityEditor(Flockr flockr, Profile p) {
		super(flockr, p, true);
		this.setTitle("Profile matcher");
	}
	
	public void addMatchingCheckboxForField(Panel panel, String fieldName) {
		Checkbox matchCheckbox = new Checkbox();
		matchCheckbox.setName(fieldName);
		matchCheckbox.setState(getProfile().fieldShouldMatch(AGSymbol.jAlloc(fieldName)));
		getMatchingFieldCheckBoxes().add(matchCheckbox);
		panel.add(matchCheckbox);
	}
	
	
	public void updateTheMatchingProfile() {
		Iterator it = getMatchingFieldCheckBoxes().iterator();
		while (it.hasNext()) {
			Checkbox current = (Checkbox)it.next();
			if (current.getState()) {
				getProfile().setFieldShouldMatch(AGSymbol.jAlloc(current.getName()));
			} else {
				getProfile().setFieldShouldNotMatch(AGSymbol.jAlloc(current.getName()));
			}
		}
		getLocalFlockr().updateMatchingProfile(getProfile());
	}

}
