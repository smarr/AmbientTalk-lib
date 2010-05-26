//******************************************************************************
//
// File:    ChatFrame.java
// Package: edu.rit.chat2
// Unit:    Class edu.rit.chat2.ChatFrame
//
// This Java source file is copyright (C) 2006 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
//
// File modified by Tom Van Cutsem to make the M2MI example work using
// ambient references in AmbientTalk.
//
// This Java source file is part of the M2MI Library ("The Library"). The
// Library is free software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version.
//
// The Library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html or by writing to the Free
// Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA.
//
//******************************************************************************

package demo.m2mi.chat;

import edu.rit.util.Transcript;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// note that the listener now extends EventListener such that none of
// its methods block the Java Swing event loop when invoked.
interface ChatFrameListener extends java.util.EventListener {

// Exported operations.

	/**
	 * Report that the user wants to create a new chat room.
	 */
	public void newChatRoom(String name);

	/**
	 * Report that the user selected the given chat room.
	 *
	 * @param  i  Chat room index.
	 */
	public void setChatRoom(Object chatRoom);

	/**
	 * Process the given line of text entered by the user.
	 *
	 * @param  line  Line of text.
	 */
	public void sendText(String line);

	/** to toggle network.online / .offline in AmbientTalk */
    public void toggleOnline();

}

/**
 * Class ChatFrame encapsulates the UI for a rudimentary M2MI-based chat
 * application.
 *
 * @author  Alan Kaminsky
 * @version 26-Mar-2006
 */
public class ChatFrame
	extends JFrame
	{

// Hidden constants.

	private static final int GAP = 10;

	private static final Font CHAT_FONT = new Font ("sanserif", Font.PLAIN, 12);

// Hidden data members.

	private JLabel myChatRoomCount;
	private JComboBox myChatRoomList;
	private JButton myNewButton;
	private Transcript myChatLog;
	private JTextField myMessageField;
	private JButton mySendButton;
	private JButton myNetworkButton;

	private ChatFrameListener myListener;

// Exported constructors.

	/**
	 * Construct a new chat frame.
	 *
	 * @param  title  Title.
	 */
	public ChatFrame
		(String title)
		{
		super (title);

		JPanel theMainPanel = new JPanel();
		add (theMainPanel);
		theMainPanel.setBorder
			(BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));
		theMainPanel.setLayout
			(new BoxLayout (theMainPanel, BoxLayout.Y_AXIS));

		JPanel theChatRoomPanel = new JPanel();
		theMainPanel.add (theChatRoomPanel);
		theChatRoomPanel.setLayout
			(new BoxLayout (theChatRoomPanel, BoxLayout.X_AXIS));

		theChatRoomPanel.add (new JLabel ("Chat room:"));

		theChatRoomPanel.add (Box.createHorizontalStrut (GAP));

		myChatRoomList = new JComboBox();
		theChatRoomPanel.add (myChatRoomList);
		myChatRoomList.setEditable (false);
		myChatRoomList.addActionListener (new ActionListener()
			{
			public void actionPerformed
				(ActionEvent event)
				{
				doSelectChatRoom();
				myMessageField.requestFocus();
				}
			});

		theChatRoomPanel.add (Box.createHorizontalStrut (GAP));

		myChatRoomCount = new JLabel ("None available");
		theChatRoomPanel.add (myChatRoomCount);

		theChatRoomPanel.add (Box.createHorizontalStrut (GAP));

		myNewButton = new JButton ("New...");
		theChatRoomPanel.add (myNewButton);
		myNewButton.addActionListener (new ActionListener()
			{
			public void actionPerformed
				(ActionEvent event)
				{
				doNew();
				}
			});

		theMainPanel.add (Box.createVerticalStrut (GAP));

		myChatLog = new Transcript (CHAT_FONT, 240, 12, 40);
		theMainPanel.add (myChatLog);

		theMainPanel.add (Box.createVerticalStrut (GAP));

		JPanel theEntryPanel = new JPanel();
		theMainPanel.add (theEntryPanel);
		theEntryPanel.setLayout
			(new BoxLayout (theEntryPanel, BoxLayout.X_AXIS));

		myMessageField = new JTextField (40);
		myMessageField.addActionListener (new ActionListener()
			{
			public void actionPerformed
				(ActionEvent e)
				{
				doSend();
				}
			});
		theEntryPanel.add (myMessageField);
		myMessageField.setEnabled (false);

		theEntryPanel.add (Box.createHorizontalStrut (GAP));

		mySendButton = new JButton ("Send");
		mySendButton.addActionListener (new ActionListener()
			{
			public void actionPerformed
				(ActionEvent e)
				{
				doSend();
				}
			});
		theEntryPanel.add (mySendButton);
		mySendButton.setEnabled (false);
		
		myNetworkButton = new JButton("Go online");
		myNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (myListener != null) {
					if (myNetworkButton.getText().equals("Go online")) {
						myNetworkButton.setText("Go offline");
					} else {
						myNetworkButton.setText("Go online");
					}
					myListener.toggleOnline();
				}
			}
		});
		theEntryPanel.add(myNetworkButton);

		setDefaultCloseOperation (EXIT_ON_CLOSE);
		pack();
		setVisible (true);
		}

// Exported operations.

	/**
	 * Register the given chat frame listener. Henceforth, whenever the user
	 * sends a line of text in this chat frame, the given chat frame listener's
	 * {@link ChatFrameListener#sendGuanote(String) send()} method will be called with
	 * the line of text.
	 *
	 * @param  theListener  Chat frame listener.
	 */
	public synchronized void setListener
		(ChatFrameListener theListener)
		{
		myListener = theListener;
		}

	/**
	 * Obtain this chat frame's chat log.
	 *
	 * @return  Chat log (transcript).
	 */
	public synchronized Transcript getChatLog()
		{
		return myChatLog;
		}

	/**
	 * Add the given chat room to this chat frame.
	 *
	 * @param  theChatRoom  Chat room.
	 * @param  isSelected   True to select the newly added chat room, false
	 *                      otherwise.
	 */
	public void addChatRoom
		(Object theChatRoom,
		 boolean isSelected)
		{
		myChatRoomList.addItem (theChatRoom);
		int n = myChatRoomList.getItemCount();
		myChatRoomCount.setText (n + " available");
		myMessageField.setEnabled (true);
		mySendButton.setEnabled (true);
		if (isSelected)
			{
			myChatRoomList.setSelectedIndex (n-1);
			}
		myMessageField.requestFocus();
		}

// Hidden operations.

	/**
	 * Take action when the user selects a chat room from the combo box.
	 */
	private synchronized void doSelectChatRoom()
		{
		if (myListener != null)
			{
			myListener.setChatRoom (myChatRoomList.getSelectedItem()); //myChatRoomList.getSelectedIndex());
			}
		}

	/**
	 * Take action when the user clicks the "New..." button.
	 */
	private synchronized void doNew()
		{
		if (myListener != null) {
				//javax.swing.SwingUtilities.invokeLater(new Runnable() {
				//	public void run() {
						String name = javax.swing.JOptionPane.showInputDialog(ChatFrame.this,
						  "Chat room name:","New Chat Room",javax.swing.JOptionPane.QUESTION_MESSAGE);
						if (!(name == null) && !(name.equals(""))) {
							myListener.newChatRoom(name);
						}
				//	}
				//});
			}
		}

	/**
	 * Take action when the Send button is pressed.
	 */
	private synchronized void doSend()
		{
		String line = myMessageField.getText();
		if (line != null && line.length() > 0 && myListener != null)
			{
			myListener.sendText(line);
			}
		myMessageField.setText ("");
		myMessageField.requestFocus();
		}
	}
