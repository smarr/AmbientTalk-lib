package applications.rfid.ambientLibrary.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import applications.rfid.ambientLibrary.main.ATBookWrapper;

public class ReviewManager extends JDialog {
	
	private final JPanel contentPanel_ = new JPanel();
	private JTextArea reviewArea_ = new JTextArea();
	private JTextField reviewField_ = new JTextField();
	
	private ATBookWrapper currentBook_;
	private Vector<ATHandler> enabledHandlers_ = new Vector<ATHandler>();


	public ReviewManager() {
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow(e);
			}
		});
		
		setTitle("Review Manager");
		//setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		//setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		//setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 427, 185);
		getContentPane().setLayout(new BorderLayout());
		contentPanel_.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel_, BorderLayout.CENTER);
		contentPanel_.setLayout(new BorderLayout(0, 0));

		reviewArea_.setEditable(false);
		contentPanel_.add(reviewArea_, BorderLayout.CENTER);
		Border b = UIManager.getBorder("TextField.border");
		reviewArea_.setBorder(b);
		reviewArea_.setLineWrap(true);
		reviewArea_.setTabSize(4);


		JPanel entryPanel_ = new JPanel();
		getContentPane().add(entryPanel_, BorderLayout.SOUTH);
		entryPanel_.setLayout(new BorderLayout(0, 0));
		entryPanel_.setBorder(new EmptyBorder(0, 2, 5, 2));

		JPanel entryBorderPanel_ = new JPanel();
		entryBorderPanel_.setBorder(null);
		entryPanel_.add(entryBorderPanel_, BorderLayout.CENTER);
		entryBorderPanel_.setLayout(new BoxLayout(entryBorderPanel_, BoxLayout.X_AXIS));

		entryBorderPanel_.add(reviewField_);
		reviewField_.setColumns(10);

		JButton submitButton_ = new JButton("Submit");
		submitButton_.addActionListener(new ButtonListener());
		entryBorderPanel_.add(submitButton_);

		JButton cancelButton_ = new JButton("Close");
		cancelButton_.addActionListener(new ButtonListener());
		entryBorderPanel_.add(cancelButton_);
		reviewField_.requestFocus();

	}
	
	public void addToTitle(String title) {
		setTitle("Review Manager" + ": " + title);
	}
	
	public void resetTitle() {
		setTitle("Review Manager");
	}
	
	public void setBook(ATBookWrapper book) {
		currentBook_ = book;
		reviewField_.setText("");
		reviewArea_.setText("loading...");
		ATHandler handler = book.displayReviews(this);
		enabledHandlers_.add(handler);
		addToTitle(book.getTitle());
		reviewField_.requestFocus();
	}
	
	public void popUp() {
		setVisible(true);
		reviewField_.requestFocus();
	}
	
	public void closeWindow(WindowEvent e) {
		currentBook_ = null;
		ATHandler current;
		resetTitle();
		for(Iterator<ATHandler> i = enabledHandlers_.iterator() ; i.hasNext() ; ) {
			current = i.next();
			if(current != null) {
				current.cancel();
			}
		}
		enabledHandlers_.clear();
	}
	
	public void displayReviews(String reviews) {
		reviewArea_.setText(reviews);
	}
	
	private void closeDialog(){
		dispose();
	}
	
	// button listeners
	class ButtonListener implements ActionListener {
		ButtonListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Submit")) {
				String newReview = reviewField_.getText().trim();
				if(!newReview.isEmpty()){
					currentBook_.addReview(newReview);
					String oldReviews = reviewArea_.getText().trim();
					if(!oldReviews.isEmpty()) {
						oldReviews = oldReviews + "\n";
					}
					reviewArea_.setText(oldReviews + newReview);
					reviewField_.setText("");
				}
			}
			if (e.getActionCommand().equals("Close")) {
				closeDialog();
			}
		}
	}

}


