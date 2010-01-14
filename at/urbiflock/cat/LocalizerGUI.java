/**
 * AmbientTalk/2 Project
 * (c) Programming Technology Lab, 2006 - 2009
 * Authors: PROG Ambient Group

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
package at.urbiflock.cat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Container;
import java.awt.Button;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class LocalizerGUI extends Frame implements MouseListener, MouseMotionListener {
  
  // interface from GUI to Localizer
	public interface ATLocalizer {
	  public void goOnline() throws Exception;
      public void updateLocation(int [] location) throws Exception;
	}
	
  // ImagePanel extends the class Canvas and is used to repaint the map 
  // and the players with double buffering overriding the method paint
  class ImagePanel extends Canvas {
  	Image image; 

   	public ImagePanel(Image image) {
    	this.image = image;
    }
	 public void paint(Graphics g) {
	 	//paint background and players in an offscreen image
	   Image offscreen = createImage(250, 270);
	   Graphics gfx = offscreen.getGraphics();
       gfx.drawImage(image, 0, 0, this);
	   if (!players.isEmpty()) {paintPlayers(gfx);}
       //Check the zoomIn flag
	   if (zoom){
		   //Capture the subimage
		   Image subimage = createImage(100, 108);
		   Graphics subg = subimage.getGraphics();
		   subg.drawImage(offscreen, -myPlayerPoint.x+50, -myPlayerPoint.y+54, this);
		   //Scale the subimage and fill it on the screen
		   subimage = subimage.getScaledInstance(250, 270, Image.SCALE_SMOOTH);
		   g.drawImage(subimage, 0, 0, this);
       } else{
    	   g.drawImage(offscreen, 0, 0, this);
       }
   }
	 // Paint all the PlayerPoints stored in the players set.
	 private void paintPlayers(Graphics g){
		 Collection pps = players.values();
		 Iterator it = pps.iterator();
		 while (it.hasNext()){
			 PlayerPoint pp = (PlayerPoint)it.next();
			 pp.paint(g);
		 }
	 }
  }
  public static final String SHOW_ITEMS 		= "Show items";
  public static final String DISPOSE_ITEMS 		= "Dispose items";
  public static final String ZOOM_IN 		= "+";
  public static final String ZOOM_OUT 		= "-";
  public static final String EXIT 		= "Exit";

  private PlayerPoint myPlayerPoint;
  private HashMap players = new HashMap();
  private TextField inputArea_;
  private ImagePanel imagePanel;
  private Panel itemsPanel;
  private Label coords;
  private Button items;
  private Button zoomIn;
  private Button zoomOut;
  private int nItems;
  private boolean zoom;
  private Color teamColor;
  private ATLocalizer atLoc_;
	  
  public LocalizerGUI(ATLocalizer atLocalizer, String aTeam, String aUsername) {
      super("Localizer of " + aUsername);
      atLoc_ = atLocalizer;
      this.setSize(250,340);
      this.setEnabled(false);
      this.setResizable(false);
      // Get the image from the path and sets the size 
      Image backgroundImage = Toolkit.getDefaultToolkit().getImage( System.getProperty("AT_HOME") +"/at/labsessions/vubZoomIn.png"); 
      myPlayerPoint = new PlayerPoint(aTeam, aUsername, 0, 0);
      nItems=2; zoom=false;
	  // Create the map container to display the image in an ImagePanel
      imagePanel = new ImagePanel(backgroundImage);
      imagePanel.setBounds(0, 0, 250, 270);
      imagePanel.addMouseListener(this);
      imagePanel.addMouseMotionListener(this);
      this.add(imagePanel,BorderLayout.CENTER);  //in the center of the frame
      // Create the information container with the toolbar panel (downContentPanel)
      Container downContentPanel = new Container();
      downContentPanel.setLayout(new BorderLayout());
      this.add(downContentPanel, BorderLayout.SOUTH); //in the south of the frame
      coords = new Label("", Label.CENTER);
      downContentPanel.add(coords, BorderLayout.CENTER);
      Panel toolbarPanel = new Panel();
      toolbarPanel.setLayout(new GridLayout(1,2));

      ActionListener buttonListener = new ActionListener() {    
    	  public void actionPerformed(ActionEvent ae){
    		  String action = ae.getActionCommand();  
    		  if (action.equals(LocalizerGUI.SHOW_ITEMS)) {
    			  items.setLabel(LocalizerGUI.DISPOSE_ITEMS);
    			  imagePanel.setVisible(false);
    			  itemsPanel = new Panel();
    			  itemsPanel.setVisible(true);
    		  } else if (action.equals(LocalizerGUI.DISPOSE_ITEMS)) {
    			  items.setLabel(LocalizerGUI.SHOW_ITEMS);
    			  itemsPanel.setVisible(false);
    			  imagePanel.setVisible(true);
    		  } else if (action.equals(LocalizerGUI.ZOOM_IN)){
    			  zoom=true;
    			  imagePanel.repaint();
    			  zoomIn.setEnabled(false);
    			  zoomOut.setEnabled(true);
    		  } else if (action.equals(LocalizerGUI.ZOOM_OUT)) {
    			  zoom=false;
    			  imagePanel.repaint();
    			  zoomIn.setEnabled(true);
    			  zoomOut.setEnabled(false);
    		  } else if (action.equals(LocalizerGUI.EXIT)) {
		        setVisible(false); 
		        dispose();
		        System.exit(0);
              }
    	  };
      };
	
      Panel zoomPanel = new Panel();
      zoomPanel.setLayout(new GridLayout(1,2));
      zoomOut = new Button(LocalizerGUI.ZOOM_OUT);
      zoomOut.addActionListener(buttonListener);
      zoomOut.setEnabled(false);
      zoomPanel.add(zoomOut);
      zoomIn = new Button(LocalizerGUI.ZOOM_IN);
      zoomIn.addActionListener(buttonListener);
      zoomIn.setEnabled(false);
      zoomPanel.add(zoomIn);

      Button exit = new Button(LocalizerGUI.EXIT);
      exit.addActionListener(buttonListener);
      Panel rightToolbarPanel = new Panel();
      rightToolbarPanel.setLayout(new GridLayout(1,2));
      rightToolbarPanel.add(zoomPanel);
      rightToolbarPanel.add(exit);
      items = new Button(LocalizerGUI.SHOW_ITEMS);
      items.setSize(items.getWidth()+20, items.getHeight());
      items.addActionListener(buttonListener);
      toolbarPanel.add(items);
      toolbarPanel.add(rightToolbarPanel);
      downContentPanel.add(toolbarPanel, BorderLayout.SOUTH);        

      addWindowListener(new WindowAdapter() { 
    	  public void windowClosing(WindowEvent e) { 
    		  setVisible(false); 
    		  dispose();
    		  System.exit(0); 
    	  } 
      });
      this.setVisible(true);
      this.setEnabled(true);
  }
    
    // MouseListener interface
    protected boolean firstClick = true;
    public void mouseClicked(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}
    public void mouseEntered(MouseEvent evt) {}
    public void mouseReleased(MouseEvent evt) { 
    	myPlayerPoint.moving = false;
    }
    public void mousePressed(MouseEvent evt) { 
    	int x = evt.getX();
    	int y = evt.getY();
    	if (firstClick == true){
    		try {
    		    atLoc_.goOnline();
    	    } catch (Exception e) { e.printStackTrace(System.out); }
    		firstClick = false;
			myPlayerPoint.setPosition(x, y);
    		try {  
    			atLoc_.updateLocation(new int[] {x, y});
    		} catch (Exception e2) { e2.printStackTrace(System.out); }
    		players.put(myPlayerPoint.getUsername(),myPlayerPoint);
    		imagePanel.repaint();
    		zoomIn.setEnabled(true);
    	} else {
    		if (myPlayerPoint.in(x, y))
    			myPlayerPoint.moving = true;
    	}
    }
	// MouseMotionListener interface
    public void mouseDragged(MouseEvent evt) {
    	if (imagePanel.contains(evt.getX(), evt.getY())){
    		if (myPlayerPoint.moving) {
    			myPlayerPoint.setPosition(evt.getX(), evt.getY());
    			try { atLoc_.updateLocation(new int[] {myPlayerPoint.x,myPlayerPoint.y}); }
    			catch (Exception e2) { e2.printStackTrace(System.out); }
    			imagePanel.repaint();
    		}
    	} else coords.setText("Dragging outside the area");
    }
	public void mouseMoved(MouseEvent evt) {}
 
  // interface from localizer to GUI
	public void display(String text) { coords.setText(text); }
	public void updatePlayerPointPosition(String aTeam, String aUsername, int[] loc) {
		PlayerPoint pp = (PlayerPoint) players.get(aUsername);
		if (pp != null) {
			pp.setPosition(loc[0],loc[1]);
		} else {
			pp = new PlayerPoint(aTeam,aUsername,loc[0],loc[1]); 
			if (myPlayerPoint.getTeam().equals(aTeam)){
				pp.setColor(myPlayerPoint.getColor());
			}
		}
		players.put(aUsername,pp);
		imagePanel.repaint();
	}
	public void showOffline(String username){
		PlayerPoint pp = (PlayerPoint) players.get(username);
		if (pp != null) {
			pp.changeToBlack();
		}
		imagePanel.repaint();
	}
	public void showOnline(String username){
		PlayerPoint pp = (PlayerPoint) players.get(username);
		if (pp != null) {
			pp.changeToOriginal();
		}
		imagePanel.repaint();
	}
}