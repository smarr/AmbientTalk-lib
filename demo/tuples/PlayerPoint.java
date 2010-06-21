/**
 * AmbientTalk/2 Project
 * (c) Programming Technology Lab, 2006 - 2009
 * Authors: PROG Ambient Group
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
package demo.tuples;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class PlayerPoint {
	
	boolean moving = false;
	int x, y, zoomX, zoomY, p, r;
	String username; 
	String team;
	Font font;
	Color original; 
	Color current;
	
	private void setupPlayerData(String aTeam, String aUsr, int pX, int pY){
		username = aUsr; team = aTeam;
		x = pX;	y = pY;
	};
	
	private void setupPlayerDisplay(Color c){
		r =2;		p = 1;
		original = c; 
		current = original;
		font = new Font(null, Font.BOLD, 10);
	}
	
	public PlayerPoint(String team, String usr, int x, int y) {
		setupPlayerData(team, usr, x, y);
		setupPlayerDisplay(new Color(randomLightColor(),randomLightColor(),randomLightColor()));
	}
	
	public PlayerPoint(Color c, String team, String usr, int x, int y) {
		setupPlayerData(team, usr, x, y);
		setupPlayerDisplay(c);
	}
	
	public PlayerPoint(Color c, String name, int x, int y) {
		setupPlayerData("no-team", name, x, y);
		setupPlayerDisplay(c);
	}
	
	//Getters and setters
	public String getTeam() {return team;}
	public String getUsername() { return username; }
	public int[] getPosition() { int[] coord = new int[2]; coord[0] = x; coord[y] = y;return coord;}
	public int getX(){ return x;}
	public int getY(){return y;}
	public void setPosition(int x, int y) { this.x = x; this.y = y; }
	public void setColor(Color aColor) {original = aColor;}
	public Color getColor() {return original;}
	//public void changeToBlack() { current = Color.BLACK; }
	public void changeToOriginal() { current = original; }
	public void changeColor(Color c) { current = c;}
	
	public void paint(Graphics g) {
		g.setColor(current);
		g.fillOval(x - r, y - r, 2 * r, 2 * r);
		g.drawOval(x - p, y - p, 2 * p, 2 * p);
		g.setFont(font);
		g.drawString(username, x+2*r,y-2*r);
	}
		
	public boolean in(int x, int y) {
		return Math.abs(this.x - x) <= r && Math.abs(this.y - y) <= r;
	}
	
	private float randomLightColor(){
		float r = (new Random()).nextFloat();
		if (r<=0.2) r += 0.8;
		else if (r>0.2 && r<0.5) r+= 0.5;
		else if (r>=0.5 && r<0.8) r+= 0.2;
		return r;
	}
}