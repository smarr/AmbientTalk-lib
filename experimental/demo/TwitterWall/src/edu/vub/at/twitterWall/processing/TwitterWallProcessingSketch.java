
package edu.vub.at.twitterWall.processing;

import processing.core.PApplet;
import processing.core.PFont;

public class TwitterWallProcessingSketch extends PApplet{
	// fields accessed from Orb
	public float attractRadius = (float) 50.0;
	public float attractX = (float) 800.0;
	public float attractY = (float) 600.0;
	// private fields
	private Orb[] o;
	private int numOrbs = 100;
	private float step = (float)0.001;
	private PFont f;

	public void setup() {
		size(screen.height, screen.width);
		background(240);
        
		smooth();
		f = createFont("Arial",47,true);
		o = new Orb[numOrbs];

		for (int i=0; i<numOrbs; i++){
			o[i] = createOrb("Yes", random(width), random(height), 10, random(0, 1000), random(3,5)/10);
		}
	}

	public Orb createOrb(String username, float xpos, float ypos, float rad, float ang, float freq) {
		return new Orb(this, username, xpos, ypos, rad, ang, freq);
	}
	
	public void draw() {
//		translate(screen.height/2, screen.width/2); //TODO: maybe not necessary?
		background(240);
		 stroke(0);

		 for (int i=0; i<numOrbs; i++) {
		   stroke(100, 50);
		   line(attractX, attractY, o[i].px, o[i].py);
		   o[i].display();
		   o[i].move();
		 }
		 stroke(0);
		 fill(255, 50);
		 ellipse(attractX, attractY, attractRadius, attractRadius);    // orbs are attracted to this
	}	 
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "edu.vub.at.twitterWall.processing.TwitterWallProcessingSketch" });
	}
}
