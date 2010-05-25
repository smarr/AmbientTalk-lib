package frameworks.urbiflock.ui;

import edu.vub.at.objects.ATTypeTag;


public interface Application {
	public String name() throws Exception;
	public Flockr owner() throws Exception;
	public void start() throws Exception;
	public void pause();
	public void unpause();
	public void stop() throws Exception;
	public void export(ATTypeTag t);
	
	public static class _EmptyApp implements Application {
		public void export(ATTypeTag t) {}
		public String name() throws Exception { return null; }
		public Flockr owner() throws Exception { return null; }
		public void pause() {}
		public void start() throws Exception {}
		public void stop() throws Exception {}
		public void unpause() {}
	};
}
