package at.urbiflock.ui;

import edu.vub.at.objects.ATTypeTag;


public interface Application {
	public String name();
	public Flockr owner();
	public void start();
	public void pause();
	public void unpause();
	public void stop();
	public void export(ATTypeTag t);
	
	public static class _EmptyApp implements Application {
		public void export(ATTypeTag t) {}
		public String name() { return null; }
		public Flockr owner() { return null; }
		public void pause() {}
		public void start() {}
		public void stop() {}
		public void unpause() {}
	};
}
