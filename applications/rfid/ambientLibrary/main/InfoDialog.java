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

public class InfoDialog extends JDialog {

	private final JPanel contentPanel_ = new JPanel();
	private JLabel infoLabel_ = new JLabel("Some info");


	public InfoDialog() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(InfoDialog.class.getResource("/javax/swing/plaf/metal/icons/ocean/info.png")));
		setTitle("Information");
		//setModalityType(ModalityType.APPLICATION_MODAL);
		//setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		//setModal(true);
		setBounds(100, 100, 204, 114);
		getContentPane().setLayout(new BorderLayout());
		contentPanel_.setLayout(new FlowLayout());
		contentPanel_.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel_, BorderLayout.CENTER);

		
		infoLabel_.setIcon(new ImageIcon(InfoDialog.class.getResource("/javax/swing/plaf/metal/icons/ocean/info.png")));
		contentPanel_.add(infoLabel_);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton closeButton = new JButton("Close");
		closeButton.setActionCommand("Close");
		closeButton.addActionListener(new ButtonListener());
		buttonPane.add(closeButton);

	}
	
	public void setInfo(String info) {
		infoLabel_.setText(info);
		int size = info.length();
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
