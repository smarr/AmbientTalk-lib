package extensions.m2mi.slides;

import edu.rit.m2mi.Eoid;
import edu.rit.slides.Projector;

public interface ScreenObjectI {

	/**
	 * Notify this screen that a projector has the given slides available. The
	 * given array of slide IDs is a complete list of all the slides the
	 * projector has available at this time.
	 *
	 * @param  theProjector
	 *     Unihandle for the projector.
	 * @param  theSlideIDs
	 *     Array of zero or more slide IDs (type {@link edu.rit.m2mi.Eoid
	 *     </CODE>Eoid<CODE>}) the projector has available.
	 */
	public void availableSlides(Projector theProjector, Eoid[] theSlideIDs);

	/**
	 * Display the given slides on this screen. The given array of slide IDs is
	 * a complete list of the slides from the given projector that are to be
	 * displayed at this time. Any slides from the given projector that had been
	 * displayed are first removed from the display, then the given slides are
	 * added to the display.
	 *
	 * @param  theProjector
	 *     Unihandle for the projector that has the slides.
	 * @param  theSlideIDs
	 *     Array of zero or more slide IDs (type {@link edu.rit.m2mi.Eoid
	 *     </CODE>Eoid<CODE>}) the projector has available that are to be
	 *     displayed.
	 */
	public void displaySlides(Projector theProjector, Eoid[] theSlideIDs);

}