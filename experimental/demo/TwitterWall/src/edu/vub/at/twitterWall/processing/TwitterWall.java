
package edu.vub.at.twitterWall.processing;

import java.util.Vector;

import processing.core.PApplet;
import processing.core.PFont;

public class TwitterWall extends PApplet implements TweetInterface {
	// fields accessed from Orb
	public float attractRadius = (float) 50.0;
	public float attractX = (float) 500.0;
	public float attractY = (float) 900.0;
	// private fields
	private Vector<Orb> o = new Vector<Orb>();
	private int numOrbs = 100;
	private float step = (float)0.001;
	private PFont f;
	public static TwitterWall instance;
	

	public void setup() {
		instance = this;
		size(screen.height, screen.width);
	//	background(240); 
		background(127);
		smooth();
		f = createFont("Arial",787,true);
		o.add( createOrb("Hans", 100, attractY, 50, 10, random(3,5)/10) );
		//o.add( createOrb("Piet ", attractX, attractY, 50, 10, random(3,5)/10) );

	} 

	public Orb createOrb(String username, float xpos, float ypos, float rad, float ang, float freq) {
		return new Orb(this, username, xpos, ypos, rad, ang, freq);
	}
	
	public void draw() {
//		translate(screen.height/2, screen.width/2); //TODO: maybe not necessary?
	//	background(240);
		background(127);

		stroke(0);

		 for (int i=0; i<o.size(); i++) {
		   stroke(100, 50);
		   line(attractX, attractY, o.elementAt(i).px, o.elementAt(i).py);
		   o.elementAt(i).display();
		   o.elementAt(i).move();
		 }
		 stroke(0);
		 fill(255, 50);
		 ellipse(attractX, attractY, attractRadius, attractRadius);    // orbs are attracted to this
	}	 
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "edu.vub.at.twitterWall.processing.TwitterWall" });
	}

	@Override
	public void addMessage(int id, String message) {
		// TODO Auto-generated method stub
		System.out.println("Called ADD::: " + message);
	}

	@Override
	public void addUser(String name, int id) {
		// TODO Auto-generated method stub
		System.out.println("Called ADD User::: " + name);
		o.add( createOrb(name, attractX, attractY + 5, 10, random(0, 1000), random(3,5)/10) );
	}

	@Override
	public void removeUser(int id) {
		// TODO Auto-generated method stub
		
	}
}
