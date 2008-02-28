package at.m2mi.slides;

import edu.rit.slides.ScreenDiscoveryListener;
import edu.rit.slides.ScreenListener;
import edu.rit.slides.SlideSet;


/** callback to AmbientTalk world to create replacement instances of the core classes */
public interface ATReplacementFactory {
	public DiscoverableScreenObjectI makeDiscovery(AsyncScreenChooser l) throws Exception;
	public ScreenObjectI makeScreen(DiscoverableScreenObjectI d, SlideSet s, ScreenListener l) throws Exception;
	public ProjectorObjectI makeProjector() throws Exception;
}