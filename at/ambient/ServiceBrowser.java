/**
 * AmbientTalk/2 Project
 * ServiceBrowser.java created on 24 sep 2007 at 15:51:51
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
package at.ambient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 * Graphical User Interface to display provided and discovered services.
 * 
 * @author tvcutsem
 */
public class ServiceBrowser extends JFrame implements DSObserver {

	private static long maxTTL_; 
	
	 private static class TimeoutCellRenderer extends JLabel implements ListCellRenderer {
	     public TimeoutCellRenderer() {
	         setOpaque(true);
	     }
	     public Component getListCellRendererComponent(
	         JList list,
	         Object value,
	         int index,
	         boolean isSelected,
	         boolean cellHasFocus) {

	         long ttl = ((ServiceDescription) value).timeToLive();
	         
	         Color bgcolor = Color.black;
	         float frc = 0f;
	         if (ttl > 0) {
		         // gradient fill green -> red
		         frc = (float) (1.0 * ttl) / maxTTL_;
		         float r = 1 - frc;
		         float g = frc;
		         bgcolor = new Color(r, g, 0f);
	         }
	         setText(value.toString() + "(" + frc*100 + "% fresh)");
	         setBackground(bgcolor);
	         return this;
	     }
	 }
		 
	public interface DSCommand extends EventListener {
		public void provide(String provider, String description);
	}
	
	private String provider = "anonymous";
	 
	private final JFrame window = new JFrame();
	private final DefaultListModel providedServices = new DefaultListModel();
	private final DefaultListModel discoveredServices = new DefaultListModel();
	private JList providedServiceList;
	private JList discoveredServiceList;
	private final JTextField serviceDescriptionField = new JTextField(20);
	private final JTextField providerField = new JTextField(20);
	private final JTextArea statusArea = new JTextArea();
	private final DSCommand command_;
	
	public ServiceBrowser(long max, DSCommand command) {
		super("Service Browser of anonymous");
		maxTTL_ = max;
		command_ = command;
		providerField.setText(provider);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		serviceDescriptionField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// new text entered in the text field
				String description = serviceDescriptionField.getText();
				if (description.length() > 0) {
					command_.provide(provider, description);
				};
			};
		});
		providerField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
			    // new text entered in the provider field
			    provider = providerField.getText();
                setTitle("Service Browser of " + provider);
			};
		});
		
		statusArea.setEditable(false);
		
		// start COMPOSITION
		//BoxLayout topToBottomLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		//setLayout(topToBottomLayout);
		Box box = Box.createVerticalBox();
		add(box);
		
		JScrollPane statusScrollPane = new JScrollPane(statusArea);
		statusScrollPane.setPreferredSize(new Dimension(80, 150));
		statusArea.setAutoscrolls(true);
		
		providedServiceList = new JList(providedServices);
		discoveredServiceList = new JList(discoveredServices);
		discoveredServiceList.setCellRenderer(new TimeoutCellRenderer());
		
		box.add(new JLabel("Provider name: "));
		box.add(providerField);
		box.add(new JLabel("Services provided by me: "));
		box.add(new JScrollPane(providedServiceList));
		box.add(new JLabel("Discovered Services: "));
		box.add(new JScrollPane(discoveredServiceList));
		box.add(new JLabel("Provide new service: "));
		box.add(serviceDescriptionField);
		box.add(new JLabel("Status: "));
		box.add(statusScrollPane);
		pack();
		setVisible(true);
		// end COMPOSITION	
	}

	private void log(String message) {
		statusArea.append(message + "\n");
		statusArea.moveCaretPosition(statusArea.getText().length());
	}
	
	public void broadcasting() {
		log("broadcasting my own view!");
	}

	public void broadcastWithin(long numMillisec) {
		log("Next broadcast scheduled in " + numMillisec + " msec");
	}

	public void discovered(final ServiceDescription serviceItem) {
		log("Discovered new service Item: " + serviceItem.service());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// modify the GUI only in the GUI's own thread to prevent races
				discoveredServices.addElement(serviceItem);
			}
		});
	}

	public void expired(final ServiceDescription serviceItem) {
		log("Service item expired: " + serviceItem.service());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// modify the GUI only in the GUI's own thread to prevent races
				discoveredServices.removeElement(serviceItem);
			}
		});
	}

	public void noticedAboutToExpire() {
		log("One of my services is about to expire, timing out sooner!");
	}

	public void noticedMissingService() {
		log("One of my services is missing from received view, timing out sooner!");
	}

	public void providing(final ServiceDescription serviceItem) {
		log("Providing new service: "+serviceItem.service());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// modify the GUI only in the GUI's own thread to prevent races
				providedServices.addElement(serviceItem);	
			}
		});
	}

	public void receivedBroadcast(int numItemsInView) {
		log("Received broadcast containing " + numItemsInView + " service items");
	}

	public void refresh(ServiceDescription[] localWorldView) {
		discoveredServiceList.repaint();
	}

	public void starting() {
		log("Starting DEAPspace protocol");
	}

	public void stopping() {
		log("Stopping DEAPspace protocol");
	}

	public void stopProviding(final ServiceDescription serviceItem) {
		log("Stopped advertising "+serviceItem.service());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// modify the GUI only in the GUI's own thread to prevent races
				providedServices.removeElement(serviceItem);	
			}
		});
	}

	public void ttlUpdated(ServiceDescription serviceItem) {
		log("TTL of service "+serviceItem.service()+" updated");
		discoveredServiceList.repaint();
	}
	 
}
