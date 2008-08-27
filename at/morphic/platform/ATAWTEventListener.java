/**
 * AmbientTalk/2 Project
 * ATAWTEventListener.java created on 21 aug 2008 at 10:48:43
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
package at.morphic.platform;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface ATAWTEventListener extends java.util.EventListener {
	// we make paint return a value such that it is regarded as a
	// synchronous method invocation by the AT/Java symbiosis
	Object paint(Graphics g);
	// the following are now pure async sends from Java to AmbientTalk
	public void mousePressed(MouseEvent arg0);
	public void mouseClicked(MouseEvent arg0);
	public void mouseEntered(MouseEvent arg0);
	public void mouseExited(MouseEvent arg0);
	public void mouseReleased(MouseEvent arg0);
	public void mouseDragged(MouseEvent arg0);
	public void mouseMoved(MouseEvent arg0);
	public void keyTyped(KeyEvent arg0);
	public void keyPressed(KeyEvent arg0);
	public void keyReleased(KeyEvent arg0);
}
