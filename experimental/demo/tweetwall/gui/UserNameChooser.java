package experimental.demo.tweetwall.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UserNameChooser extends JFrame implements ActionListener {
	
	private UserATApplication atApp_;
	private JTextField userNameField_ = new JTextField(30);
	private JButton okButton_ = new JButton("Ok");
	private JButton closeButton_ = new JButton("Close");
	
	public UserNameChooser(UserATApplication atApp) {
		super("Select your user name");
	
		atApp_ = atApp;
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		okButton_.setActionCommand("Ok");
		okButton_.addActionListener(this);
		
		closeButton_.setActionCommand("Close");
		closeButton_.addActionListener(this);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(okButton_);
		buttonsPanel.add(closeButton_);
		
		add(userNameField_);
		add(buttonsPanel);
		
		pack();
		setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Ok") {
			if ((userNameField_.getText() != null) && (userNameField_.getText().length() > 0)) {
				atApp_.createUser(userNameField_.getText());
			}
			dispose();
			return;
		}
		if (event.getActionCommand() == "Close") {
			dispose();
			return;
		}
	}

}
