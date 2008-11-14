/**
 * AmbientTalk/2 Project
 * Guanote.java created on 3 nov 2008 at 16:11:48
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

/**
 * The Java interface to an AmbientTalk Guanote object.
 * 
 * @author tvcutsem
 */
public interface Guanote {
	
	public String getSenderName() throws Exception;
	public String[] getReceiverList() throws Exception;
	public String message() throws Exception;
	
	public static Guanote _EMPTY_ = new Guanote() {
		public String message() throws Exception {
			return "";
		}
		public String[] getReceiverList() throws Exception {
			return new String[] { "" };
		}
		public String getSenderName() throws Exception {
			return "";
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
	
