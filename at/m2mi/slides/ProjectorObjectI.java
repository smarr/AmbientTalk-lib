package at.m2mi.slides;

import edu.rit.m2mi.Eoid;
import edu.rit.slides.Projector;
import edu.rit.slides.SlideShow;

import java.util.EventListener;

public interface ProjectorObjectI extends Projector, EventListener {

	/**
	 * Set the theatre in which this projector object will participate.
	 *
	 * @param  theTheatre
	 *     {@link AsyncScreen </CODE>Screen<CODE>} multihandle for the theatre, or
	 *     null not to participate in a theatre.
	 */
	public void setTheatre(AsyncScreen theTheatre);

	/**
	 * Set the slide show this projector will display. The projector starts by
	 * displaying the first slide group in the slide show.
	 *
	 * @param  theSlideShow
	 *     Slide show to display, or null not to display a slide show.
	 * Forced to be synchronous to avoid races between actor and GUI thread.
	 * (if made async, GUI refreshes too fast, actor won't have updated the slideshow yet)
	 */
	public void setSlideShow(SlideShow theSlideShow) throws RuntimeException;

	/**
	 * Display the first slide group in this projector's slide show.
	 * Forced to be synchronous to avoid races between actor and GUI thread.
	 * (if made async, GUI refreshes too fast, actor won't have updated the slideshow yet)
	 */
	public void displayFirst() throws RuntimeException;

	/**
	 * Display the last slide group in this projector's slide show.
	 * Forced to be synchronous to avoid races between actor and GUI thread.
	 * (if made async, GUI refreshes too fast, actor won't have updated the slideshow yet)
	 */
	public void displayLast() throws RuntimeException;

	/**
	 * Display the next slide group in this projector's slide show.
	 * Forced to be synchronous to avoid races between actor and GUI thread.
	 * (if made async, GUI refreshes too fast, actor won't have updated the slideshow yet)
	 */
	public void displayNext() throws RuntimeException;

	/**
	 * Display the previous slide group in this projector's slide show.
	 * Forced to be synchronous to avoid races between actor and GUI thread.
	 * (if made async, GUI refreshes too fast, actor won't have updated the slideshow yet)
	 */
	public void displayPrevious() throws RuntimeException;

	/**
	 * Returns the index of the slide group this projector is currently
	 * displaying. If this projector is not displaying any slide group, -1 is
	 * returned.
	 */
	public int getSlideGroupIndex();

	/**
	 * Get the given slide from this projector.
	 *
	 * @param  theSlideID
	 *     Slide ID (type {@link edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}).
	 */
	public void getSlide(Eoid theSlideID);

	/**
	 * Blank or unblank the display.
	 *
	 * @param  blanked  True to blank the display, false to unblank the display.
	 * Forced to be synchronous to avoid races between actor and GUI thread.
	 * (if made async, GUI refreshes too fast, actor won't have updated the slideshow yet)
	 */
	public void setBlanked(boolean blanked) throws RuntimeException;

	/**
	 * Determine if the display is blanked.
	 *
	 * @return True if the display is blanked, false otherwise.
	 */
	public boolean isBlanked();

}