package at.m2mi.slides;

import edu.rit.slides.Screen;

import java.util.EventListener;

public interface DiscoverableScreenObjectI extends EventListener {

	/**
	 * Associate this discoverable screen object with the given theatre. If
	 * <TT>theHandle</TT> is not null, it must be the {@link Screen
	 * </CODE>Screen<CODE>} multihandle for the theatre, and this discoverable
	 * screen object starts reporting that theatre's presence. If
	 * <TT>theHandle</TT> is null, this discoverable screen object stops
	 * reporting the theatre's presence.
	 *
	 * @param  theHandle
	 *     The {@link Screen </CODE>Screen<CODE>} multihandle used for
	 *     performing method calls on all screen objects in the theatre.
	 * @param  theName
	 *     Theatre name.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theHandle</TT> is not null and
	 *     <TT>theName</TT> is null.
	 */
	public void associate(Screen theHandle, String theName);
	public Screen makeTheatre(String name);
}