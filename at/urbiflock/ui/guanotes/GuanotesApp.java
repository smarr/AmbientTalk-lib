/**
 * AmbientTalk/2 Project
 * GuanotesApp.java created on 3 nov 2008 at 16:12:14
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
package at.urbiflock.ui.guanotes;

import edu.vub.at.objects.ATTypeTag;

import java.util.Vector;

import at.urbiflock.ui.Application;
import at.urbiflock.ui.Flock;
import at.urbiflock.ui.FlockListener;
import at.urbiflock.ui.Flockr;
import at.urbiflock.ui.Profile;
import at.urbiflock.ui.Subscription;

/**
 * The Java interface to an AmbientTalk Guanote urbiflock application.
 * 
 * @author tvcutsem
 */
public interface GuanotesApp extends Application {

	public Guanote makeGuanoteFromNames(String receiver, String sender, String message) throws Exception;
	public void sendGuanote(Guanote g) throws Exception;
	public void listenForGuanotesToOwner(GuanoteListener l) throws Exception;
	
	
	
	// for testing purposes only
	
	public static final GuanotesApp _TESTAPP_ = new GuanotesApp() {
		public void listenForGuanotesToOwner(GuanoteListener l) throws Exception {
		}
		public Guanote makeGuanoteFromNames(final String receiver, final String sender,
				final String message) throws Exception {
			return new Guanote() {
				public String message() throws Exception {
					return message;
				}

				public String[] getReceiverList() throws Exception {
					return new String[] { receiver };
				}

				public String getSenderName() throws Exception {
					return sender;
				}
				
				public String toString() {
					try {
						return "from " + getSenderName() + ": " + message();
					} catch (Exception e) {
						return "Guanote " + e.getMessage();
					}
				}
			};
		}

		public String name() throws Exception { return "Guanotes"; }
		
		public void sendGuanote(Guanote g) throws Exception {
			System.out.println("Guanote "+g+" sent");
		}
		public Flockr owner() throws Exception {
			return new Flockr() {
				public void addBuddy(Profile profile) {}
				public void createFlockFromFieldMatchers(String flockName,
						Vector fieldMatchers, boolean shouldBeFriend,
						boolean shouldBeNearby) {}
				public void deleteFlock(Flock f) {}
				public Profile getBuddy(String uid) { return null; }
				public Flock[] getFlocks() {
					return new Flock[] {
						new Flock() {
							public Subscription addListener(FlockListener l) {
								return null;
							}
							public String[] getFlockrList() {
								return new String[0];
							}
							public String getName() { return "testflock"; }
							public Profile getProfile(String username) {
								return null;
							}
							public boolean isDefaultFlock() {
								return false;
							}
							public Profile[] listProfiles() {
								return new Profile[0];
							}	
						}
					};
				}
				public Profile getProfile() { return null; }
				public boolean isBuddy(String uid) { return false; }
				public void openFlockEditorOnNewFlock() {}
				public void registerBuddyListListener(Object l) {}
				public void registerDiscoveryListener(Object l) {}
				public void registerProfileChangedListener(Object l) {}
				public void removeBuddy(Profile profile) {}
				public void removeBuddyListListener(Object l) {}
				public void removeDiscoveryListener(Object l) {}
				public void removeFlock(Flock f) {}
				public void removeProfileChangedListener(Object l) {}
				public void updateMatchingProfile(Profile p) {}
				public void updateProfile() {}
			};
		}

		public void export(ATTypeTag t) {}
		public void start() throws Exception {}
		public void pause() {}
		public void stop() throws Exception {}
		public void unpause() {}
		
	};
}
