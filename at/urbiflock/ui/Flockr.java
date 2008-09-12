package at.urbiflock.ui;

public interface Flockr {

	public void addBuddy(Profile profile);
	public void removeBuddy(Profile profile);
	public boolean isBuddy(String uid);
	public Profile getBuddy(String uid);
	public Flock[] getFlocks();
	public void removeFlock(Flock f);
	
}
