package at.urbiflock.ui;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import edu.vub.at.objects.natives.NATText;

/**
 * The Application Launcher UI - a grid from where the installed applications
 * can be launched.
 */
public class ApplicationLauncher extends Frame implements ActionListener {
	
	Vector applications_ = new Vector();
	Panel applicationGridPanel_;
	
	public ApplicationLauncher(Vector applications) {
		applications_ = applications;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.setColumns(4);
		gridLayout.setRows(0);
		applicationGridPanel_ = new Panel(gridLayout);
		add(applicationGridPanel_);
		
		Iterator it = applications_.iterator();
		while (it.hasNext()) {
			Application app = (Application)it.next();
			String appName = app.getName().javaValue;
			Button button = new Button(appName);
			button.setActionCommand(appName);
			button.addActionListener(this);
			applicationGridPanel_.add(button);
		}
		
		pack();
		setVisible(true);
	}
	
	public Application findApplication(String name) {
		Iterator it = applications_.iterator();
		Application result = null;
		while (it.hasNext()) {
			Application app = (Application)it.next();
			if (app.getName().javaValue.equals(name)) {
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
			public NATText getName() { return NATText.atValue("app1"); }
			public void start() { System.out.println("app1 started"); }
		};
		Application app2 = new Application() {
			public NATText getName() { return NATText.atValue("app2"); }
			public void start() { System.out.println("app2 started"); }
		};
		Application app3 = new Application() {
			public NATText getName() { return NATText.atValue("jkfkjsfbbskf dfdfj,n defn f"); }
			public void start() { System.out.println("jkfkjsfbbskf dfdfj,n defn f started"); }
		};
		Application app4 = new Application() {
			public NATText getName() { return NATText.atValue("app4"); }
			public void start() { System.out.println("app4 started"); }
		};
		Application app5 = new Application() {
			public NATText getName() { return NATText.atValue("app5"); }
			public void start() { System.out.println("app5 started"); }
		};
		Vector apps = new Vector();
		apps.add(app1);
		apps.add(app2);
		apps.add(app3);
		apps.add(app4);
		apps.add(app5);
		ApplicationLauncher launcher = new ApplicationLauncher(apps);
	}

}
