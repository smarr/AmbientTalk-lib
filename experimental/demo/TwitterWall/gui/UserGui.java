package experimental.demo.TwitterWall.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class UserGui extends JFrame implements ActionListener {

	private WallUser user_;
	private JTextArea messageTextArea_ = new JTextArea();
	private JButton sendButton_ = new JButton("Send");
	private JButton closeButton_ = new JButton("Close");
	
	public UserGui(WallUser user) {
		super("Ambient Tweet Cloud");
		
		user_ = user;
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		sendButton_.setActionCommand("Send");
		sendButton_.addActionListener(this);
		
		closeButton_.setActionCommand("Close");
		closeButton_.addActionListener(this);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(sendButton_);
		buttonsPanel.add(closeButton_);
		
		add(messageTextArea_);
		add(buttonsPanel);
		
		pack();
		setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Send") {
			if ((messageTextArea_.getText() != null) && (messageTextArea_.getText().length() > 0)) {
				user_.sendMessage(messageTextArea_.getText());
			}
		}
		if (event.getActionCommand() == "Close") {
			dispose();
			return;
		}
	}
 
	
}
