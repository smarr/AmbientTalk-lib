
package edu.vub.at.twitterWall.processing;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PFont;

public class TwitterWall extends PApplet implements TweetInterface {
	// fields accessed from Orb
	public float attractRadius = (float) 250.0;
	public float attractX = (float) 500.0;
	public float attractY = (float) 900.0;
	// private fields
	private Vector<Orb> o = new Vector<Orb>();
	
	private int MAXMESSAGESPERUSER = 10;
	private int SECONDSTOSHOWMESSAGE = 2000000;
	private long numberOfMilliSecondsMessageShown = 0;
	private long previousMessageDrawTime = System.currentTimeMillis();
	private HashMap<Integer, Vector<String>> messages = new HashMap<Integer, Vector<String>>();
	
	private String currentlyShownMessage = null;
	private String currentlyShownUser = null;
	
	private HashMap<Integer, String> users = new HashMap<Integer, String>();
	private Vector<Integer> userIds = new Vector<Integer>();
	
	private int numOrbs = 100;
	private float step = (float)0.001;
	private PFont f;
	public static TwitterWall instance;
	

	public void setup() {
		instance = this;
		size(screen.height, screen.width);
	//	background(240); 
		background(0);
		smooth();
		//f = createFont("Arial",48,true);
		//o.add( createOrb("Hans", 100, attractY, 50, 10, random(3,5)/10) );
		//o.add( createOrb("Piet ", attractX, attractY, 50, 10, random(3,5)/10) );

	} 

	public Orb createOrb(String username, float xpos, float ypos, float rad, float ang, float freq) {
		return new Orb(this, username, xpos, ypos, rad, ang, freq);
	}
	
	private void selectNewMessageToShow() {
		currentlyShownMessage = null;
		currentlyShownUser = null;
		if (userIds.isEmpty()) {
			return;
		}
		int randomIdx = (new Random()).nextInt(userIds.size());
		currentlyShownUser = users.get(userIds.elementAt(randomIdx));
		
		System.out.println("users: " + users);
		System.out.println("userIds: " + userIds);
		System.out.println("random: " + randomIdx);
		System.out.println("messages: " + messages.get(userIds.elementAt(randomIdx)));
		
		currentlyShownMessage = messages.get(userIds.elementAt(randomIdx)).lastElement();
	}
	
	public void draw() {
//		translate(screen.height/2, screen.width/2); //TODO: maybe not necessary?
	//	background(240);
		stroke(0);
		 fill(0);
		 ellipse(attractX, attractY, attractRadius, attractRadius);    // orbs are attracted to this
		
		long currentTime = System.currentTimeMillis();
		numberOfMilliSecondsMessageShown = numberOfMilliSecondsMessageShown + (currentTime - previousMessageDrawTime);
		
		if (numberOfMilliSecondsMessageShown >= SECONDSTOSHOWMESSAGE) {
			selectNewMessageToShow();
			numberOfMilliSecondsMessageShown = 0;
			previousMessageDrawTime = System.currentTimeMillis();
		}
		
		background(0);
		stroke(0);
		
		if ((currentlyShownMessage != null) && (currentlyShownUser != null)) {
			pushMatrix();
			PFont f = createFont("Arial", 56, true);
			textFont(f);       
			fill(255);
			textAlign(LEFT);
			text(currentlyShownUser, (screen.width/10), (screen.height) - (screen.height/10) - 50);
			text(currentlyShownMessage, (screen.width/10), (screen.height) - (screen.height/10));
			popMatrix();
		}

		PFont orbfont = createFont("Arial", 28, true);
		textFont(orbfont);
		 for (int i=0; i<o.size(); i++) {
		   stroke(100, 50);
		   line(attractX, attractY, o.elementAt(i).px, o.elementAt(i).py);
		   o.elementAt(i).display();
		   o.elementAt(i).move();
		 }
		 
	}	 
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "edu.vub.at.twitterWall.processing.TwitterWall" });
	}

	//@Override
	public void addMessage(int id, String message) {
		// TODO Auto-generated method stub
		System.out.println("Called ADD::: " + message);

		Vector<String> msgs = messages.get(id);
		if (msgs == null) {
			msgs = new Vector<String>();
		} else {
			if (msgs.size() >= MAXMESSAGESPERUSER) {
				msgs.removeElementAt(0);
			}
		}
		System.out.println("msgs before: " + msgs);
		msgs.add(message);
		messages.put(id, msgs);
		if (currentlyShownMessage == null) {
			selectNewMessageToShow();
		}
		System.out.println("msgs: " + msgs);
		System.out.println("messages: " + messages.get(id));
	}
	
	public void addUser(String name, Integer id) {
		if( o.size() > 0 ) {
			Orb firstOrb = o.elementAt(0);
			Orb newOrb = createOrb(name, firstOrb.x, firstOrb.y, 10, 0, 1);
			newOrb.angle1 = firstOrb.angle1;
			int newspace = 360 / (o.size() + 1);  
			float a = firstOrb.angle1;
			int i = 1;
			for (Orb orb : o) {
				orb.angle1 -= Math.abs( (Math.abs(a) + (newspace * i)) - Math.abs((orb.angle1)));
				orb.move();
				i += 1;
			}
			o.add(0,newOrb);
		} else {
			o.add( createOrb(name, 100, attractY, 50, 10, 1) );
		}
		users.put(id, name);
		userIds.add(id);
		addMessage(id, "...is present.");
		if (currentlyShownMessage == null) {
			selectNewMessageToShow();
		}
	}

	//@Override
	public void removeUser(Integer id) {
		// TODO Auto-generated method stub
		users.remove(id);
		userIds.remove(id);
		messages.remove(id);
	}
}