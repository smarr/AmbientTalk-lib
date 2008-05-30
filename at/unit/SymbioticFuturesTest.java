/**
 * AmbientTalk/2 Project
 * SymbioticFuturesTest.java created on 2 apr 2008 at 16:33:54
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
package at.unit;

import edu.vub.at.actors.eventloops.BlockingFuture;

/**
 * @author tvcutsem
 *
 * An auxiliary class used in the file bugfixes.at to test the AmbientTalk/Java linguistic symbiosis.
 * 
 */
public class SymbioticFuturesTest {

	private interface Cell {
		public int access();
		public BlockingFuture accessFuture(Class type, int bla);
	}
	
	public static void test(final Cell c, final Runnable cont) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				int x = c.access(); // access returns an AT future which is resolved later
				cont.run();
			}
		});
		t.start();
	}
	
	public static void testFuture(final Cell c, final Runnable cont) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				BlockingFuture fx = c.accessFuture(Integer.class, 1); // access returns an AT future which is resolved later
				try {
					Integer i = (Integer) fx.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				cont.run();
			}
		});
		t.start();
	}
	
}
