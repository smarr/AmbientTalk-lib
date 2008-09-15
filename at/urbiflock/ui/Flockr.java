package at.urbiflock.ui;

import edu.vub.at.objects.natives.NATObject;

public interface Flockr {

	public void addBuddy(Profile profile);
	public void removeBuddy(Profile profile);
	public boolean isBuddy(String uid);
	public Profile getBuddy(String uid);
	public Flock[] getFlocks();
	public void removeFlock(Flock f);
	public void registerProfileChangedListener(Object l);
	public void removeProfileChangedListener(Object l);
}
