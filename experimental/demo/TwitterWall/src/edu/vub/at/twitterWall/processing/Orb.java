package edu.vub.at.twitterWall.processing;

import processing.core.PApplet;

public class Orb {

	TwitterWallProcessingSketch parent; // The parent PApplet that we will render ourselves onto
	
	float x, y; // x-coordinate, y-coordinate
	float radius;

	float angle;
	float angle1 = 0;
	float px, py;
	float frequency = 2;
	String text;

	// Constructor
	public Orb(TwitterWallProcessingSketch p, String t, float xpos, float ypos, float rad, float ang, float freq) {
		parent = p;
		x = xpos;
		y = ypos;
		radius = rad;
		angle = ang;
		frequency = freq;
		text = t;
	}

	public void display() {
		parent.smooth();
		//parent.text(text,px,py);
		parent.noStroke();
		parent.ellipseMode(parent.CENTER);
		//parent.stroke(100);
		//parent.ellipse(px, py, radius, radius);
		parent.pushMatrix();
		   parent.fill(0, 102, 153, 51);
		      parent.translate(px, py);
		      parent.rotate(PApplet.atan2(parent.attractY-py,parent.attractX-px));
		      parent.text(text, 0, 0); 
		      parent.popMatrix(); 
	}

	public void move() {

		px = parent.attractX + PApplet.cos(parent.radians(angle+angle1)) * ((parent.attractRadius + x)/2);
		py = parent.attractY + PApplet.sin(parent.radians(angle+angle1)) * ((parent.attractRadius + x)/2);

		angle1 -= frequency;

		//println("xpos: " + px + " , ypos: " + py + ", angle" + angle);
	}
}
