package at.urbiflock.ui;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;

import edu.vub.at.objects.natives.NATText;

/**
 * The Application Launcher UI - a grid from where the installed applications
 * can be launched.
 */
public class ApplicationLauncher extends Frame implements ActionListener {
	
	Application[] applications_;
	Panel applicationGridPanel_;
	
	public ApplicationLauncher(Application[] applications) {
		applications_ = applications;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.setColumns(4);
		gridLayout.setRows(0);
		applicationGridPanel_ = new Panel(gridLayout);
		add(applicationGridPanel_);
		
		for (int i = 0; i < applications_.length; i++) {
			Application app = applications_[i];
			String appName = app.name();
			Button button = new Button(appName);
			button.setActionCommand(appName);
			button.addActionListener(this);
			applicationGridPanel_.add(button);
		}
		
		pack();
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
		        dispose();
		    }
		});
	}
	
	public Application findApplication(String name) {
		Application result = null;
		for (int i = 0; i < applications_.length; i++) {
			Application app = applications_[i];
			if (app.name().equals(name)) {
				result = app;
			}
		}
		return result;
	}
	
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		(findApplication(command)).start();
	}
	
	public static void main(String[] args) {
		Application app1 = new Application() {
			public String name() { return "app1"; }
			public void start() { System.out.println("app1 started"); }
		};
		Application app2 = new Application() {
			public String name() { return "app2"; }
			public void start() { System.out.println("app2 started"); }
		};
		Application app3 = new Application() {
			public String name() { return "jkfkjsfbbskf dfdfj,n defn f"; }
			public void start() { System.out.println("jkfkjsfbbskf dfdfj,n defn f started"); }
		};
		Application app4 = new Application() {
			public String name() { return "app4"; }
			public void start() { System.out.println("app4 started"); }
		};
		Application app5 = new Application() {
			public String name() { return "app5"; }
			public void start() { System.out.println("app5 started"); }
		};
		Vector apps = new Vector();
		apps.add(app1);
		apps.add(app2);
		apps.add(app3);
		apps.add(app4);
		apps.add(app5);
		ApplicationLauncher launcher = new ApplicationLauncher((Application[])apps.toArray(new Application[apps.size()]));
	}

}
