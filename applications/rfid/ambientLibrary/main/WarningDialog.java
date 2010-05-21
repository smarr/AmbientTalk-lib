package applications.rfid.ambientLibrary.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import applications.rfid.ambientLibrary.main.ReviewManager.ButtonListener;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

public class WarningDialog extends JDialog {

	private final JPanel contentPanel_ = new JPanel();
	private JLabel warningLabel_ = new JLabel("Something went wrong");


	public WarningDialog() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(WarningDialog.class.getResource("/javax/swing/plaf/metal/icons/ocean/warning.png")));
		setTitle("Warning");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 204, 114);
		getContentPane().setLayout(new BorderLayout());
		contentPanel_.setLayout(new FlowLayout());
		contentPanel_.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel_, BorderLayout.CENTER);

		
		warningLabel_.setIcon(new ImageIcon(WarningDialog.class.getResource("/javax/swing/plaf/metal/icons/ocean/warning.png")));
		contentPanel_.add(warningLabel_);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton closeButton = new JButton("Close");
		closeButton.setActionCommand("Close");
		closeButton.addActionListener(new ButtonListener());
		buttonPane.add(closeButton);

	}
	
	public void setWarning(String warning) {
		warningLabel_.setText(warning);
		int size = warning.length();
		setBounds(100, 100, (10*size), 114);
	}
	
	private void closeDialog(){
		dispose();
	}
	
	// button listeners
	class ButtonListener implements ActionListener {
		ButtonListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Close")) {
				closeDialog();
			}
		}
	}

}
