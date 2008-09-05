/**
 * AmbientTalk/2 Project
 * ProfileViewer.java created on 5 sep 2008 at 16:04:09
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
package at.urbiflock.ui;

import edu.vub.at.objects.natives.grammar.AGSymbol;

import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;

/**
 * The UI of a Urbiflock Profile (by default viewer, editor for own profile)
 */
public class ProfileViewer extends Frame {

	public ProfileViewer(Profile p, boolean editable) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	

		Panel unamePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		unamePanel.add(new Label("Username:"));
		TextField uname = new TextField(p.username());
		uname.setEditable(editable);
		unamePanel.add(uname);
		add(unamePanel);
		
		Panel fnamePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		fnamePanel.add(new Label("First name:"));
		TextField fname = new TextField(p.firstname());
		fname.setEditable(editable);
		fnamePanel.add(fname);
		add(fnamePanel);

		Panel lnamePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		lnamePanel.add(new Label("Last name:"));
		TextField lname = new TextField(p.lastname());
		lname.setEditable(editable);
		lnamePanel.add(lname);
		add(lnamePanel);

		Panel dobPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		dobPanel.add(new Label("Date of birth:"));
		TextField dob = new TextField(p.birthdate().toString());
		dob.setEditable(editable);
		dobPanel.add(dob);
		add(dobPanel);

		Panel sexPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		sexPanel.add(new Label("Sex:"));
		Choice SexChooser = new Choice();
		SexChooser.add("Male");
		SexChooser.add("Female");
		SexChooser.setEnabled(editable);
		sexPanel.add(SexChooser);
		if (p.sex().toString().equals("male")) {
			SexChooser.select(0);
		} else {
			SexChooser.select(1);
		}
		add(sexPanel);
		
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		ProfileViewer v = new ProfileViewer(new Profile() {
			  public String username() { return "foo"; }
			  public String firstname() { return "Mr."; }
			  public String lastname() { return "Foo"; }
			  public Date birthdate() { return Calendar.getInstance().getTime(); }
			  public AGSymbol sex() { return AGSymbol.jAlloc("male"); }
		}, true);
	}
	
}
