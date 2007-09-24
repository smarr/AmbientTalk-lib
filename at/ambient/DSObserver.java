/**
 * AmbientTalk/2 Project
 * DSObserver.java created on 24 sep 2007 at 17:09:29
 * (c) Programming Technology Lab, 2006 - 2007
 * Authors: Tom Van Cutsem & Stijn Mostinckx
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package at.ambient;

import java.util.EventListener;

/**
 * Interface for making DEAPspace communicate with a GUI written in Java.
 * @author tvcutsem
 */
public interface DSObserver extends EventListener {
	 public void starting();
	 public void stopping();
	 public void discovered(ServiceDescription serviceItem);
	 public void expired(ServiceDescription serviceItem);
	 public void ttlUpdated(ServiceDescription serviceItem); 
	 public void providing(ServiceDescription serviceItem);
	 public void stopProviding(ServiceDescription serviceItem); 
	 public void refresh(ServiceDescription[] localWorldView);
	 public void broadcasting();
	 public void receivedBroadcast(int numItemsInView); 
	 public void broadcastWithin(long numMillisec);
	 public void noticedMissingService();
	 public void noticedAboutToExpire();
}

interface ServiceDescription {
	 public Object publisher();
	 public long timeToLive();
	 public Object type();
	 public Object service();
}