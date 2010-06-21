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
package demo.tuples;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
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
import java.io.File;
import java.net.URL;

import javax.swing.BoxLayout;

public class LocalizerGUI extends Frame implements MouseListener, MouseMotionListener {
  
  // interface from GUI to Localizer
	public interface ATLocalizer {
	  public void initialize() throws Exception;
      public void updatePlayerLocation(int [] location) throws Exception;
	}

  // ImagePanel extends the class Canvas and is used to repaint the map 
  // and the players with double buffering overriding the method paint
  class ImageCanvas extends Canvas {
  	Image image; 

   	public ImageCanvas(Image image) {
    	this.image = image;
    }
	 public void paint(Graphics g) {
	 	//paint background and players in an offscreen image
	   Image offscreen = createImage(250, 270);
	   Graphics gfx = offscreen.getGraphics();
       gfx.drawImage(image, 0, 0, this);
	   if (!players.isEmpty()) {paintCollection(players.values(), gfx);}
	   if (!targets.isEmpty()) {paintCollection(targets, gfx);}
	   msgBar.setText("");
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
	 private void paintCollection( Collection pps, Graphics g){
		 //Collection pps = players.values();
		 Iterator it = pps.iterator();
		 while (it.hasNext()){
			 PlayerPoint pp = (PlayerPoint)it.next();
			 pp.paint(g);
		 }
	 }
  }
  public static final String SHOW_ITEMS 		= "Show items";
  public static final String DISPOSE_ITEMS 		= "Hide items";
  public static final String ZOOM_IN 		= "+";
  public static final String ZOOM_OUT 		= "-";
  public static final String EXIT 		= "Stop Game";

  private PlayerPoint myPlayerPoint;
  private HashMap players; 
  private Vector targets; 
  private List virtualItems;
  private ImageCanvas imageCanvas;
  private Panel mainPanel;
  //private Container itemsPanel;
  private Label msgBar;
  private Button items;
  private Button zoomIn;
  private Button zoomOut;
  private int nItems;
  private boolean zoom;
  private ATLocalizer atLoc_;
	  
  public LocalizerGUI(ATLocalizer atLocalizer, String aTeam, String aUsername) {
      super( aUsername + " as " + aTeam);
      atLoc_ = atLocalizer;
      // init data structures
      players = new HashMap();
      targets = new Vector();
            
      myPlayerPoint = new PlayerPoint(aTeam, aUsername, 0, 0);
      //init display parameters.
      this.setSize(250,370);
      this.setEnabled(false);
      this.setResizable(false);
      mainPanel = new Panel();
      mainPanel.setSize(250, 300);
      nItems=2; zoom=false;
      
      // Get the image from the path and sets the size 
      URL imageUrl = LocalizerGUI.class.getResource("vubZoomIn.png");
      Image backgroundImage = Toolkit.getDefaultToolkit().getImage(imageUrl.getPath());
      imageCanvas = new ImageCanvas(backgroundImage);
      imageCanvas.setBounds(mainPanel.getBounds());
      imageCanvas.addMouseListener(this);
      imageCanvas.addMouseMotionListener(this);
      imageCanvas.setVisible(true);
      mainPanel.add(imageCanvas,BorderLayout.CENTER);
      
      virtualItems = new List();
      virtualItems.setBounds(mainPanel.getBounds());
      virtualItems.setBackground(Color.WHITE);
      virtualItems.setMultipleMode(false);
      virtualItems.setVisible(false);
      mainPanel.add(virtualItems);
      mainPanel.setVisible(true);
      this.add(mainPanel,BorderLayout.CENTER);  //in the center of the frame

      // Create the information container with the toolbar panel (downContentPanel)
      Container downContentPanel = new Container();
      downContentPanel.setLayout(new BorderLayout());
      this.add(downContentPanel, BorderLayout.SOUTH); //in the south of the frame
      msgBar = new Label("", Label.CENTER);
      downContentPanel.add(msgBar, BorderLayout.CENTER);
      Panel toolbarPanel = new Panel();
      toolbarPanel.setLayout(new GridLayout(1,2));

      ActionListener buttonListener = new ActionListener() {    
    	  public void actionPerformed(ActionEvent ae){
    		  String action = ae.getActionCommand();  
    		  if (action.equals(LocalizerGUI.SHOW_ITEMS)) {
    			  items.setLabel(LocalizerGUI.DISPOSE_ITEMS);
    			  imageCanvas.setVisible(false);
    			  virtualItems.setVisible(true);
    			  pack();
    		  } else if (action.equals(LocalizerGUI.DISPOSE_ITEMS)) {
    			  items.setLabel(LocalizerGUI.SHOW_ITEMS);
    			  virtualItems.setVisible(false);
    			  imageCanvas.setVisible(true);
    			  pack();
    		  } else if (action.equals(LocalizerGUI.ZOOM_IN)){
    			  zoom=true;
    			  imageCanvas.repaint();
    			  zoomIn.setEnabled(false);
    			  zoomOut.setEnabled(true);
    		  } else if (action.equals(LocalizerGUI.ZOOM_OUT)) {
    			  zoom=false;
    			  imageCanvas.repaint();
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
      /*Panel rightToolbarPanel = new Panel();
      rightToolbarPanel.setLayout(new GridLayout(1,2));
      rightToolbarPanel.add(zoomPanel);
      rightToolbarPanel.add(exit);*/
      items = new Button(LocalizerGUI.SHOW_ITEMS);
      items.setSize(items.getWidth()+20, items.getHeight());
      items.addActionListener(buttonListener);
      toolbarPanel.add(items);
      //toolbarPanel.add(rightToolbarPanel);
      toolbarPanel.add(zoomPanel);
      toolbarPanel.add(exit);
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
    		    atLoc_.initialize();
    	    } catch (Exception e) { e.printStackTrace(System.out); }
    		firstClick = false;
        myPlayerPoint.setPosition(x, y);
        players.put(myPlayerPoint.getUsername(),myPlayerPoint);
    		try {  
    			atLoc_.updatePlayerLocation(new int[] {x, y});
    		} catch (Exception e2) { e2.printStackTrace(System.out); }
    		
    		imageCanvas.repaint();
    		zoomIn.setEnabled(true);
    	} else {
    		if (myPlayerPoint.in(x, y))
    			myPlayerPoint.moving = true;
    	}
    }
	// MouseMotionListener interface
    public void mouseDragged(MouseEvent evt) {
    	if (imageCanvas.contains(evt.getX(), evt.getY())){
    		if (myPlayerPoint.moving) {
    			myPlayerPoint.setPosition(evt.getX(), evt.getY());
    			try { atLoc_.updatePlayerLocation(new int[] {myPlayerPoint.x,myPlayerPoint.y}); }
    			catch (Exception e2) { e2.printStackTrace(System.out); }
    			imageCanvas.repaint();
    		}
    	} else msgBar.setText("Dragging outside the area");
    }
	public void mouseMoved(MouseEvent evt) {}
 
  // interface from localizer to GUI
	public void display(String text) { msgBar.setText(text); }
	public void displayPlayerPosition(String aTeam, String aUsername, int[] loc) {
		PlayerPoint pp = (PlayerPoint) players.get(aUsername);
		if (pp != null) {
			pp.setPosition(loc[0],loc[1]);
			pp.changeToOriginal();
		} else {
			if (myPlayerPoint.getTeam().equals(aTeam)){
				pp = new PlayerPoint(myPlayerPoint.getColor(),aTeam,aUsername,loc[0],loc[1]);
			} else{
				pp = new PlayerPoint(aTeam,aUsername,loc[0],loc[1]);
			}
			players.put(aUsername,pp);
		}		
		imageCanvas.repaint();
	}

	private PlayerPoint findCrimeTarget(String name, int[]loc ){
		Iterator it = targets.iterator();
		while (it.hasNext()){
			 PlayerPoint pp = (PlayerPoint)it.next();
			 if ((pp.getUsername() == name) && (pp.getX() == loc[0]) &&(pp.getY() == loc[1])){
				 return pp;
			 }
		 }
		return null;
		
	}
	public void displayCrimeTarget(String name, int[] loc, boolean online){
		PlayerPoint pp = findCrimeTarget(name, loc);
		if (null == pp) {
			pp = new PlayerPoint(Color.GREEN, name, loc[0], loc[1]);
			targets.add(pp);
		} 
		if (!online) {
			pp.changeColor(Color.RED);
		};
		imageCanvas.repaint();
	};
	
	public void addVirtualObject(String object) {
		msgBar.setText("You found a new virtual object: " + object);
		virtualItems.add(object);
	};
	
	public void removeVirtualObject(String object) {
		msgBar.setText("You consumed a new virtual object: " + object);
		virtualItems.remove(object);
	};
	
	public void showOffline(String username){
		PlayerPoint pp = (PlayerPoint) players.get(username);
		if (pp != null) {
			pp.changeColor(Color.BLACK);
		}
		imageCanvas.repaint();
	}
	public void showOnline(String username){
		PlayerPoint pp = (PlayerPoint) players.get(username);
		if (pp != null) {
			pp.changeToOriginal();
		}
		imageCanvas.repaint();
	}
}