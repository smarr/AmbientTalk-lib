package at.morphic.platform;
import edu.vub.at.objects.ATObject;
import edu.vub.at.objects.natives.NATTable;
import edu.vub.at.objects.natives.grammar.AGSymbol;
import edu.vub.at.objects.symbiosis.JavaObject;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;


public class WorldCanvas extends Canvas implements MouseInputListener, KeyListener {
	
	private Image onscreenBuffer = null;
	
	public WorldCanvas(int width, int height) {
		this.setSize(width, height);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		this.setVisible(true);
	}
	
	public JFrame openInFrame() {
		JFrame frame = new JFrame();
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		return frame;
	}
					
	public void setSize(int w, int h) {
		super.setSize(w, h);
		onscreenBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	}
	
	private boolean isDamaged = true;
	private int damagedRectX;
	private int damagedRectY;
	private int damagedRectWidth;
	private int damagedRectHeight;
	
	private final Vector bufferedEvents_ = new Vector();

	public void paint(Graphics g) {
		// og.setClip(0,0,getSize().width, getSize().height);
		// atListener_.paint(og); //new NATAWTCanvas((Graphics2D) og));
		// copy buffer into actual canvas' graphics context	
		synchronized (onscreenBuffer) {
			if (isDamaged) {
				// TODO: if we only draw damaged section, we might update too little
				// because multiple damage rectangles may have been signalled in between
				// each paint() call
				g.drawImage(onscreenBuffer,
						    0, //damagedRectX,
						    0, //damagedRectY,
						    null); //damagedRectWidth,
						    //damagedRectHeight, null);			
				isDamaged = false;
			}
		}
		// og.dispose();
	}
				
	/**
	 * Ask AWT to repaint a portion of the screen as defined by
	 * the rectangle with opposite sides (x,y) - (x+w, y+h)
	 */
	public void setBuffer(Image offscreenBuffer, int x, int y, int w, int h) {
		synchronized (onscreenBuffer) {
			onscreenBuffer.getGraphics().drawImage(offscreenBuffer, x, y, w, h, null);
			damagedRectX = x;
			damagedRectY = y;
			damagedRectWidth = w;
			damagedRectHeight = h;
			isDamaged = true;
		}
		repaint();
	}
	
	/**
	 * Returns a table of pairs [eventSymbol, javaEventObject] representing
	 * all Java AWT events that have occurred since the last time this method was invoked.
	 */
	public synchronized NATTable capture() {
		ATObject[] events = new ATObject[bufferedEvents_.size()];
		int i = 0;
		for (Iterator iterator = bufferedEvents_.iterator(); iterator.hasNext(); i++) {
			Object[] pair = (Object[]) iterator.next();
			events[i] = NATTable.of((AGSymbol) pair[0], JavaObject.wrapperFor(pair[1]));
		}
		bufferedEvents_.clear();
		return NATTable.atValue(events);
	}
		
	public synchronized void mouseClicked(MouseEvent arg0) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("mouseClicked"), arg0 });
		// atListener_.mouseClicked(arg0);
	}
	public synchronized void mouseEntered(MouseEvent arg0) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("mouseEntered"), arg0 });
		// atListener_.mouseEntered(arg0);			
	}
	public synchronized void mouseExited(MouseEvent arg0) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("mouseExited"), arg0 });
		// atListener_.mouseExited(arg0);						
	}
	public synchronized void mousePressed(MouseEvent arg0) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("mousePressed"), arg0 });
		// atListener_.mousePressed(arg0);
	}
	public synchronized void mouseReleased(MouseEvent arg0) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("mouseReleased"), arg0 });
		// atListener_.mouseReleased(arg0);
	}
	public synchronized void mouseDragged(MouseEvent arg0) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("mouseDragged"), arg0 });
		//atListener_.mouseDragged(arg0);
	}
	public synchronized void mouseMoved(MouseEvent arg0) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("mouseMoved"), arg0 });
		//atListener_.mouseMoved(arg0);
	}
	public synchronized void keyPressed(KeyEvent e) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("keyPressed"), e });
		//atListener_.keyPressed(e);
	}
	public synchronized void keyReleased(KeyEvent e) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("keyReleased"), e });
		//atListener_.keyReleased(e);
	}
	public synchronized void keyTyped(KeyEvent e) {
		bufferedEvents_.add(new Object[] { AGSymbol.jAlloc("keyTyped"), e });
		//atListener_.keyTyped(e);
	}
	
}
