package edu.vub.at.twitterWall.processing;

import java.awt.Font;

import processing.core.PApplet;

public class Orb {
	TwitterWall parent; // The parent PApplet that we will render ourselves onto
	float x, y; // x-coordinate, y-coordinate
	float radius;

	float angle;
	float angle1 = 0;
	float px, py;
	float frequency = 2;
	String text;

	// Constructor
	public Orb(TwitterWall p, String t, float xpos, float ypos, float rad, float ang, float freq) {
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
		parent.noStroke();
		parent.fill(255, 255, 255, 255);
		parent.ellipseMode(parent.CENTER);
		//parent.ellipse(px, py, radius, radius);
		parent.pushMatrix();
			parent.color(255, 255, 255);
			//parent.fill(0, 102, 153, 51);
			parent.fill(255, 255, 255, 255);
			parent.translate(px, py);
			parent.rotate(PApplet.atan2(parent.attractY-py,parent.attractX-px));
			parent.text(text, 0, 0); 
		parent.popMatrix(); 
	}

	public void move() {
		px = parent.attractX + PApplet.cos(parent.radians(angle+angle1)) * ((parent.attractRadius + x)/2);
		py = parent.attractY + PApplet.sin(parent.radians(angle+angle1)) * ((parent.attractRadius + x)/2);
		angle1 -= frequency;
	}
}
