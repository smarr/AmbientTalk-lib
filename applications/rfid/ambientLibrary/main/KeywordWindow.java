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
import java.awt.FlowLayout;

public class KeywordWindow extends JDialog {
	
	private static final long serialVersionUID = -6830865353704995720L;
	
	private final JPanel contentPanel_ = new JPanel();
	private JTextArea keywordArea_ = new JTextArea();
	
	private ATBookWrapper currentBook_;
	private Vector<ATHandler> enabledHandlers_ = new Vector<ATHandler>();


	public KeywordWindow() {
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow(e);
			}
		});
		
		setTitle("Keywords");
		//setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		//setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		//setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 427, 185);
		getContentPane().setLayout(new BorderLayout());
		contentPanel_.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel_, BorderLayout.CENTER);
		contentPanel_.setLayout(new BorderLayout(0, 0));

		keywordArea_.setEditable(true);
		contentPanel_.add(keywordArea_, BorderLayout.CENTER);
		Border b = UIManager.getBorder("TextField.border");
		keywordArea_.setBorder(b);
		keywordArea_.setLineWrap(true);
		keywordArea_.setTabSize(4);


		JPanel entryPanel_ = new JPanel();
		getContentPane().add(entryPanel_, BorderLayout.SOUTH);
		entryPanel_.setLayout(new BorderLayout(0, 0));
		entryPanel_.setBorder(new EmptyBorder(0, 2, 2, 2));

		JPanel entryBorderPanel_ = new JPanel();
		entryBorderPanel_.setBorder(null);
		entryPanel_.add(entryBorderPanel_, BorderLayout.CENTER);
		entryBorderPanel_.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		entryBorderPanel_.add(buttonPanel, BorderLayout.EAST);
				
		//entryBorderPanel_.add(reviewField_);
		//reviewField_.setColumns(10);
				
		JButton saveButton_ = new JButton("Save");
		buttonPanel.add(saveButton_);
		saveButton_.addActionListener(new ButtonListener());
		
		JButton cancelButton_ = new JButton("Close");
		buttonPanel.add(cancelButton_);
		cancelButton_.addActionListener(new ButtonListener());
		keywordArea_.requestFocus();

	}
	
	public void addToTitle(String title) {
		setTitle("Keywords" + ": " + title);
	}
	
	public void resetTitle() {
		setTitle("Keywords");
	}
	
	public void setEditable(boolean isEditable) {
		keywordArea_.setEditable(isEditable);
	}
	
	public void setBook(ATBookWrapper book) {
		currentBook_ = book;
		keywordArea_.setText("loading...");
		setEditable(false);
		ATHandler handler = book.displayKeywords(this);
		enabledHandlers_.add(handler);
		addToTitle(book.getTitle());
		keywordArea_.requestFocus();
	}
	
	public void popUp() {
		setVisible(true);
		keywordArea_.requestFocus();
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
	
	public void displayKeywords(String keywords) {
		keywordArea_.setText(keywords);
	}
	
	private void closeDialog(){
		dispose();
	}
	
	// button listeners
	class ButtonListener implements ActionListener {
		ButtonListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Save")) {
				String newKeywords = keywordArea_.getText().trim();
				if(!newKeywords.isEmpty()){
					currentBook_.setKeywordsFromString(newKeywords);
				}
			}
			if (e.getActionCommand().equals("Close")) {
				closeDialog();
			}
		}
	}

}


