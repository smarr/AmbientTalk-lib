/**
 * AmbientTalk/2 Project
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
package at.demo.mumama;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * The GUI of the MuMaMa application.
 * 
 * @author tvcutsem
 */
public class MuMaMa extends JFrame {

	public static interface InputListener extends EventListener {
		public void connect();
		public void disconnect();
		public void startTransmission(Peer peer);
	}
	
	public static interface Peer {
		public String username();
		public Object ref();
	}
	
	private final JButton connectButton_ = new JButton("Connect");
	private final DefaultListModel nearbyUsers_ = new DefaultListModel();
	private final JList nearbyUserList_ = new JList(nearbyUsers_);
	private final DefaultTableModel myLibModel_ = new DefaultTableModel(0,1);
	private final DefaultTableModel peerLib_ = new DefaultTableModel(0,1);
	private JTable peerLibTable_;
	private final JLabel status_ = new JLabel("offline");
	private final InputListener listener_;
	private Set myLib_;
	
	public MuMaMa(String username, InputListener listener) {
		super("MuMaMa: "+username);
		
		listener_ = listener;
		myLib_ = new HashSet();
		
		JPanel libPanel = new JPanel();
		libPanel.setLayout(new BoxLayout(libPanel, BoxLayout.X_AXIS));
		
		JTable myLib = createLibraryTable("My Library", myLibModel_);
		peerLibTable_ = createLibraryTable("Peer Library", peerLib_);
		
		libPanel.add(myLib);
		libPanel.add(peerLibTable_);
		
		setLayout(new BorderLayout());
		//add(status_, BorderLayout.NORTH);
		add(libPanel, BorderLayout.CENTER);
		
		nearbyUserList_.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		Box southBox = Box.createVerticalBox();
		southBox.add(new JScrollPane(nearbyUserList_));
				
		//add(connectButton_, BorderLayout.SOUTH);
		Box statusBox = Box.createHorizontalBox();
		statusBox.add(connectButton_);
		statusBox.add(new JLabel("Status: "));
		statusBox.add(status_);
		connectButton_.setAlignmentX(Component.LEFT_ALIGNMENT);
		statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		southBox.add(statusBox);
		
		add(southBox, BorderLayout.SOUTH);

		connectButton_.addActionListener(new ActionListener() {
			private boolean isConnected = false;
			public void actionPerformed(ActionEvent ae) {
				isConnected = !isConnected;
				if (isConnected) {
					connectButton_.setText("Disconnect");
					listener_.connect();
					status_.setText("online");
				} else {
					connectButton_.setText("Connect");
					listener_.disconnect();
					status_.setText("offline");
				}
			}
		});
		
		// use a custom cell renderer that renders only the username part of the Peer
		nearbyUserList_.setCellRenderer(new DefaultListCellRenderer() {
		     public Component getListCellRendererComponent(
		             JList list,
		             Object value,
		             int index,
		             boolean isSelected,
		             boolean cellHasFocus) {
		    	   // NOTE: this is dangerous! the .username() call to AmbientTalk
		    	   // will block the GUI thread until it received a response from the AT actor
		    	   return super.getListCellRendererComponent(list, ((Peer)value).username(), index, isSelected, cellHasFocus);
		         }
		});
		
		nearbyUserList_.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Peer peer = (Peer) nearbyUserList_.getSelectedValue();
					if (peer != null) {
						clearPeerLibrary();						
						status_.setText("downloading "+peer.username()+"'s library");
						listener_.startTransmission(peer);
					}	
				}
			}
		});
		
		setSize(400, 600);
		//pack();
	}
	
	private static final Color lightGreen = new Color(160,255,160);
	
	public JTable createLibraryTable(String name, TableModel model) {
		JTable table = new JTable(model){
			public Component prepareRenderer
			(TableCellRenderer renderer,int Index_row, int Index_col) {
				Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
				comp.setForeground(Color.black);
				//even index, selected or not selected
				if (Index_row % 2 == 0 /*&& !isCellSelected(Index_row, Index_col)*/) {
					comp.setBackground(lightGreen);
				} 
				else {
					comp.setBackground(Color.white);
				}
				return comp;
			}
		};
		//table.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), name, TitledBorder.CENTER, TitledBorder.TOP));
		table.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setEnabled(false);
		table.setShowGrid(true);
		table.setAlignmentY(Component.TOP_ALIGNMENT);
		return table;
	}
	
	// public interface
	
	public void setUserLibrary(Set userLib) {
		myLib_ = userLib;
		myLibModel_.setRowCount(0); // reset table
		for (Iterator iterator = userLib.iterator(); iterator.hasNext();) {
			Song item = (Song) iterator.next();
			myLibModel_.addRow(new Object[] { item.asString() });
		}
		repaint();
	}
	
	public void addPeerSong(Song item) {
		peerLib_.addRow(new Object[] { item.asString() });
		repaint();
	}
	
	public void peerDiscovered(Peer peer) {
		nearbyUsers_.addElement(peer);
		nearbyUserList_.repaint();
	}
	
	public void peerLost(Peer peer) {
		nearbyUsers_.removeElement(peer);
		nearbyUserList_.repaint();
	}
	
	public void peerDisconnected(Peer peer) {
		status_.setText(peer.username() + " disconnected");
	}
	
	public void peerReconnected(Peer peer) {
		status_.setText(peer.username() + " reconnected");
	}
	
	public void peerExpired(Peer peer) {
		status_.setText(peer.username() + " dropped");
		clearPeerLibrary();						
	}
	
	protected void clearPeerLibrary() {
		peerLib_.setRowCount(0);
		peerLibTable_.repaint();
	}
	
	public void transferComplete(int match) {
		status_.setText("transfer complete, "+match+"% songs match");
		/*int numSongs = peerLib_.getRowCount();
		for (int i = 0; i < numSongs; i++) {
			Object song = peerLib_.getValueAt(i, 0);
			if (myLib_.contains(song)) {
				System.out.println("setting to bold: " + song);
				JLabel songLabel = new JLabel(song.toString());
				songLabel.setFont(songLabel.getFont().deriveFont(Font.BOLD));
				peerLib_.setValueAt(songLabel, i, 0);
			}
		}*/
	}

	public static void main(String[] args) {
		MuMaMa m = new MuMaMa("testname", new InputListener() {
			public void connect() {
				System.out.println("Connected");
			}
			public void disconnect() {
				System.out.println("Disconnected");
			}
			public void startTransmission(Peer peer) {
				System.out.println("clicked "+peer.username());
			}
			
		});
		Set userLib = new HashSet();
		userLib.add(createSong("song1"));
		userLib.add(createSong("song2"));
		userLib.add(createSong("song3"));
		userLib.add(createSong("song4"));
		m.setUserLibrary(userLib);
		m.setVisible(true);
		
		Peer p1 = new Peer() {
			public Object ref() {
				return null;
			}
			public String username() {
				return "user1";
			}
		};
		
		m.peerDiscovered(p1);
		//m.peerDiscovered("user2");
		//m.peerLost("user2");
		m.addPeerSong(createSong("song1"));
		m.addPeerSong(createSong("song2"));
		m.addPeerSong(createSong("song6"));
		m.transferComplete(50);
		// m.peerExpired("user2");
	}
	
	private static Song createSong(final String name) {
		return new Song() {
			public String asString() { return name; }
			public boolean equals(Object other) {
				return asString().equals(((Song) other).asString());
			}
			public int hashCode() { return name.hashCode(); }
			public int compareTo(Object other) {
				return asString().compareTo(((Song) other).asString());
			}
		};
	}
	
}
