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
package demo.contextwatcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * @author tvcutsem
 */
public class CWGUI extends JFrame {

    // interface from GUI to ContextWatcher
    public interface ATContextWatcher {
      public void sendMessage(String[] to, String text) throws Exception;
      public void vote(String poll) throws Exception;
      public void changeStatus(String newstatus) throws Exception;
      public void toggleOnline() throws Exception;
    }
    
    private final JTextArea textBuffer_;
    private final JTextField textInput_;
    private final DefaultTableModel buddyList_;
    private final JTable buddyTable_;
    private final ATContextWatcher atIm_;
    private final JTextField status_;
    private final JCheckBox chkVote_;
    
    public CWGUI(ATContextWatcher atIm, String username) {
        super("ContextWatcher of " + username);
        this.setSize(250,350);
        
        atIm_ = atIm;
        
        getContentPane().setLayout(new BorderLayout(2,2));
        
        textInput_ = new JTextField(16);
        status_ = new JTextField("Status",16);
        
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Send:"));
        inputPanel.add(textInput_);
        chkVote_ = new JCheckBox("Vote?",false);
        inputPanel.add(chkVote_);
        
        JPanel statusPanel = new JPanel();
        statusPanel.add(new JLabel("Status:")); statusPanel.add(status_);
        
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(inputPanel);
        top.add(statusPanel);
        getContentPane().add(top, BorderLayout.NORTH);
        
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        
        textBuffer_ = new JTextArea(5,20); textBuffer_.setName("Messages");
        JScrollPane scrollPane = new JScrollPane(textBuffer_);
        scrollPane.setSize(textBuffer_.getWidth(), textBuffer_.getHeight());
        bottom.add(scrollPane);
        
        JCheckBox chkOnline = new JCheckBox("Online", true);
        chkOnline.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent ie) {
        		try {
        			atIm_.toggleOnline();
        		} catch (Exception e) {
        			e.printStackTrace(System.out);
        		}
        	}
        });
        bottom.add(chkOnline);
        
        getContentPane().add(bottom, BorderLayout.SOUTH);
	
        textInput_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                CWGUI.this.sendButtonPressed();
            }           
        });

        status_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                	atIm_.changeStatus(status_.getText());
                } catch (Exception e) {
                	e.printStackTrace(System.out);
                }
            }           
        });
        
        buddyList_ = new DefaultTableModel(new Object[] { "Online", "Buddy", "Status"}, 0);
        buddyTable_ = new JTable(buddyList_);
        buddyTable_.setName("Buddies");
        JScrollPane tableScrollPane = new JScrollPane(buddyTable_);
        buddyTable_.setBorder(BorderFactory.createRaisedBevelBorder());
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        
        addWindowListener(new WindowAdapter() { 
            public void windowClosing(WindowEvent e) { 
              setVisible(false); 
              dispose(); 
              System.exit(0); 
            } 
        });
        
        this.pack();
        this.setVisible(true);
    }
    
    public void sendButtonPressed() {
    	if (chkVote_.isSelected()) {
    		try {
    			atIm_.vote(textInput_.getText());
    		} catch (Exception e) {
    			e.printStackTrace(System.out);
    		}
    	} else {
    		int[] rows = buddyTable_.getSelectedRows();
    		if (rows.length > 0) {
    			String[] receivers = new String[rows.length];
    			for (int i = 0; i < receivers.length; i++) {
    				receivers[i] = buddyList_.getValueAt(rows[i], 1).toString();
    			}
    			try {
    				atIm_.sendMessage(receivers, textInput_.getText());
    			} catch (Exception e) {
    				e.printStackTrace(System.out);
    			}
    		}	
    	}
    }
    
    // interface from ContextWatcher to GUI
    
    public void flagDiscovered(String name, String status) {
    	buddyList_.addRow(new Object[] { "ONLINE", name, status });
    }
    
    public void flagOnline(String name) {
    	Vector v = buddyList_.getDataVector();
    	Iterator it = v.iterator();
    	while (it.hasNext()) {
			Vector row = (Vector) it.next();
			if (row.get(1).equals(name)) {
				row.set(0, "ONLINE");
				break;
			}
		}
    	buddyList_.fireTableDataChanged();
    }
    
    public void flagOffline(String name) {
    	Vector v = buddyList_.getDataVector();
    	Iterator it = v.iterator();
    	while (it.hasNext()) {
			Vector row = (Vector) it.next();
			if (row.get(1).equals(name)) {
				row.set(0, "OFFLINE");
				break;
			}
		}
    	buddyList_.fireTableDataChanged();
    }
    
    public void showTextMessage(String from, String msg) {
    	display(from + ": " + msg + "\n");
    }
    
    public void notifySent(String to, String text) {
    	display("to " + to + ":" + text + "\n");
    }
    
    public void updateStatus(String name, String status) {
    	Vector v = buddyList_.getDataVector();
    	Iterator it = v.iterator();
    	while (it.hasNext()) {
			Vector row = (Vector) it.next();
			if (row.get(1).equals(name)) {
				row.set(2, status);
				break;
			}
		}
    	buddyList_.fireTableDataChanged();
    }
    
    public void showVoteResults(String vote, String receivedVotes) {
    	display("results for " + vote + "\n" + receivedVotes);
    }
    
    public String askVote(String from, String choices) {
    	return JOptionPane.showInputDialog(from + " asks for your vote:\n"+choices);
    }
    
    private void display(String s) {
        int begapp = textBuffer_.getText().length();
        textBuffer_.append(s);
        if (this.isVisible()) {
          int endapp = textBuffer_.getText().length();
          textBuffer_.setCaretPosition(endapp);
          textBuffer_.setSelectionStart(begapp);
          textBuffer_.setSelectionEnd(endapp);
        }
        this.repaint();
    }
}
