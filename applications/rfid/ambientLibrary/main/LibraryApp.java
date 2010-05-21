package applications.rfid.ambientLibrary.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;

import applications.rfid.ambientLibrary.main.ATBookWrapper;
import applications.rfid.ambientLibrary.main.HandlerManager.JavaHandler;

public class LibraryApp extends JFrame {

	private static final long serialVersionUID = -4421477872108476695L;
	
	private JPanel contentPane_ = new JPanel();
	private BookTableModel bookTableModel_ = new BookTableModel();
	private JTable bookTable_ = new JTable(bookTableModel_);
	private JScrollPane bookTableScrollPane_ = new JScrollPane(bookTable_);
	private BookTableCellRenderer bookTableCellRenderer_ = new BookTableCellRenderer();
	private IconCellRenderer statusCellRenderer_ = new IconCellRenderer();
	
	private final JPanel keywordPanel_ = new JPanel();
	private final JTextField keywordField_ = new JTextField();
	private final JLabel keywordLabel_ = new JLabel("Keywords:");
	
	private ReviewManager reviewPopup_ = new ReviewManager();
	private KeywordWindow keywordsPopup_ = new KeywordWindow();
	private WarningDialog warningDialog_ = new WarningDialog();
	private InfoDialog infoDialog_ = new InfoDialog();
	
	private JPopupMenu popupMenu_ = new JPopupMenu();
	private static String ADD_REVIEW_CMD = "Add a review";
	private static String CLR_REVIEW_CMD = "Clear all reviews";
	private static String EDIT_KEYWS_CMD = "Edit keywords";
	
	private TableRowSorter<BookTableModel> sorter_;
	
	private HandlerManager handlerManager_ = new HandlerManager();
		
	private Color lightBlue_ = Color.blue.brighter();
	
	private String keywords_ = "";

	public LibraryApp() {
		super("Ambient Library");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bookTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		contentPane_.setBorder(new EmptyBorder(10, 5, 10, 5));
		contentPane_.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane_);
		
		contentPane_.add(bookTableScrollPane_);
		
		contentPane_.add(keywordPanel_, BorderLayout.SOUTH);
		keywordPanel_.setBorder(new EmptyBorder(5, 0, 0, 0));
		keywordPanel_.setLayout(new BorderLayout(5, 0));
		keywordPanel_.add(keywordField_);
		keywordPanel_.add(keywordLabel_, BorderLayout.WEST);
		
		//bookTable_.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		bookTable_.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
				
		bookTable_.setDefaultRenderer(String.class, bookTableCellRenderer_);
		bookTable_.setDefaultRenderer(Icon.class, statusCellRenderer_);
		bookTable_.setDefaultRenderer(ImageIcon.class, statusCellRenderer_);
		bookTable_.setCellSelectionEnabled(false);
		bookTable_.setRowSelectionAllowed(true);
		bookTable_.setSelectionBackground(Color.blue);
		
		JMenuItem addReviewMenuItem = new JMenuItem(ADD_REVIEW_CMD);
		JMenuItem clearReviewsMenuItem = new JMenuItem(CLR_REVIEW_CMD);
		JMenuItem editKeywordsMenuItem = new JMenuItem(EDIT_KEYWS_CMD);
		addReviewMenuItem.addActionListener(new AddReviewActionAdapter(this));
		clearReviewsMenuItem.addActionListener(new ClearReviewsActionAdapter(this));
		editKeywordsMenuItem.addActionListener(new EditKeywordsActionAdapter(this));
	    popupMenu_.add(addReviewMenuItem);
	    popupMenu_.add(clearReviewsMenuItem);
	    popupMenu_.add(editKeywordsMenuItem);
	    
	    MouseListener popupListener = new PopupListener();
	    bookTable_.addMouseListener(popupListener);

	    TableColumn statusColumn = bookTable_.getColumnModel().getColumn(0);
	    statusColumn.setMaxWidth(40);
	    statusColumn.setMaxWidth(40);
	    statusColumn.setPreferredWidth(40);
	    
	    TableColumn ratingColumn = bookTable_.getColumnModel().getColumn(3);
	    ratingColumn.setMaxWidth(60);
	    ratingColumn.setMaxWidth(60);
	    ratingColumn.setPreferredWidth(60);
	    
	    TableColumn favorColumn = bookTable_.getColumnModel().getColumn(4);
	    favorColumn.setMaxWidth(60);
	    favorColumn.setMaxWidth(60);
	    favorColumn.setPreferredWidth(60);
	    
	    sorter_ = new TableRowSorter<BookTableModel>(bookTableModel_);
	    bookTable_.setRowSorter(sorter_);
		sorter_.setSortsOnUpdates(true);
	    
	    ImageIcon starIcon = new ImageIcon(LibraryApp.class.getResource("/icons/flatstar.png"));
	    JLabel ratingLabel = new JLabel("", starIcon, JLabel.CENTER);
	    TableCellRenderer ratingRenderer = new HeaderRenderer();
	    ratingColumn.setHeaderRenderer(ratingRenderer);
	    ratingColumn.setHeaderValue(ratingLabel);
	    
	    ImageIcon heartIcon = new ImageIcon(LibraryApp.class.getResource("/icons/flatheart.png"));
	    JLabel favorLabel = new JLabel("", heartIcon, JLabel.CENTER);
	    TableCellRenderer favorRenderer = new HeaderRenderer();
	    favorColumn.setHeaderRenderer(favorRenderer);
	    favorColumn.setHeaderValue(favorLabel);
	    
	    keywordLabel_.setIcon(heartIcon);
	    keywordField_.addActionListener(new KeywordActionListener());
	    keywordField_.getDocument().addDocumentListener(new KeywordDocumentListener());
	    keywordField_.getDocument().putProperty("name", "Keyword Field");
	    
	    keywordField_.setText("a,e,i,o,u,y");
		
		this.pack();
		this.setVisible(true);

	}


	public void addBook(ATBookWrapper book) {
		bookTableModel_.addBook(book);
	}
	
	public void removeBook(ATBookWrapper book) {
		handlerManager_.clearHandlers(book);
		bookTableModel_.removeBook(book);
	}
	
	public void updateBook(ATBookWrapper book) {
		bookTableModel_.updateBook(book);
	}

	public void setContents(ATBookWrapper[] records) {
		Vector recordsVector = new Vector();
		for (int i = 0; i < records.length; i++) {
			recordsVector.add(records[i]);
		}
		bookTableModel_.setContents(recordsVector);
	}
	
	public void tableDataChanged() {
		bookTableModel_.fireTableDataChanged();
	}
	
	public void setAvailable(ATBookWrapper book, boolean isAvailable) {
		int row = bookTableModel_.findRow(book);
		if (row >= 0) {
			bookTableCellRenderer_.setAvailable(row, isAvailable);
			statusCellRenderer_.setAvailable(row, isAvailable);
			bookTable_.repaint();
		}
	}
	
	public JavaHandler addHandler(ATBookWrapper book) {
		int row = bookTableModel_.findRow(book);
		if (row >= 0) {
			return (new HandlerCounter(book, row));
		}
		return (new DummyHandler());

	}
	
	public JavaHandler addError(ATBookWrapper book, String msg) {
		int row = bookTableModel_.findRow(book);
		if (row >= 0) {
			return (new ErrorMsg(book, row, msg));
		}
		return (new DummyHandler());

	}
	
	// dialogs
	public void showWarningDialog(String warning) {
		warningDialog_.setWarning(warning);
		warningDialog_.setVisible(true);
	}
	
	public void showInfoDialog(String info) {
		infoDialog_.setInfo(info);
		infoDialog_.setVisible(true);
	}
	
	public void showOkDialog(String msg) {
		showInfoDialog(msg);
	}
	
	// actions for popup menu
	public void addReviewActionPerformed(ActionEvent e) {
		int rowSorted = bookTable_.getSelectedRow();
		int row = sorter_.convertRowIndexToModel(rowSorted);
		if(row < 0) {
			System.err.println("No row selected...");
		} else {
			ATBookWrapper selectedBook = bookTableModel_.getBook(row);
			reviewPopup_.setBook(selectedBook);
			reviewPopup_.popUp();
		}
	}
	
	public void clearReviewsActionPerformed(ActionEvent e) {
		int rowSorted = bookTable_.getSelectedRow();
		int row = sorter_.convertRowIndexToModel(rowSorted);
		if(row < 0) {
			System.err.println("No row selected...");
		} else {
			ATBookWrapper selectedBook = bookTableModel_.getBook(row);
			selectedBook.clearReviews();
		}
	}
	
	public void editKeywordsActionPerformed(ActionEvent e) {
		int rowSorted = bookTable_.getSelectedRow();
		int row = sorter_.convertRowIndexToModel(rowSorted);
		if(row < 0) {
			System.err.println("No row selected...");
		} else {
			ATBookWrapper selectedBook = bookTableModel_.getBook(row);
			keywordsPopup_.setBook(selectedBook);
			keywordsPopup_.popUp();
		}
	}
	
	
	
	// listener for rightclick
	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}
		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}
		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu_.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	// actionlisteners for popup menu actions
	class AddReviewActionAdapter implements ActionListener {
		LibraryApp adaptee;

		AddReviewActionAdapter(LibraryApp adaptee) {
			this.adaptee = adaptee;
		}
		public void actionPerformed(ActionEvent e) {
			adaptee.addReviewActionPerformed(e);
		}
	}
	
	class ClearReviewsActionAdapter implements ActionListener {
		LibraryApp adaptee;

		ClearReviewsActionAdapter(LibraryApp adaptee) {
			this.adaptee = adaptee;
		}
		public void actionPerformed(ActionEvent e) {
			adaptee.clearReviewsActionPerformed(e);
		}
	}
	
	class EditKeywordsActionAdapter implements ActionListener {
		LibraryApp adaptee;

		EditKeywordsActionAdapter(LibraryApp adaptee) {
			this.adaptee = adaptee;
		}
		public void actionPerformed(ActionEvent e) {
			adaptee.editKeywordsActionPerformed(e);
		}
	}
	
	
	// renderer for the book table
	class BookTableCellRenderer extends DefaultTableCellRenderer {
		
		private HashSet<Integer> greyOnes_ = new HashSet<Integer>();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int viewRow, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, viewRow, column);
			
			int row = sorter_.convertRowIndexToModel(viewRow);
			
			if(column == 3 || column == 4) {
				setHorizontalAlignment(SwingConstants.CENTER);
			} else {
				setHorizontalAlignment(SwingConstants.LEFT);
			}
			
			if(isSelected) {
				if (greyOnes_.contains(row)) {
					setForeground(Color.lightGray);
				} else {
					setForeground(Color.white);
				}
			} else {
				if (greyOnes_.contains(row)) {
					setForeground(Color.gray);
				} else {
					setForeground(Color.black);
				}
			}

			setText((String) value);
			return this;
		}
		
		public void setAvailable(int row, boolean isAvailable) {
			if(isAvailable) {
				greyOnes_.remove(row);
			} else {
				greyOnes_.add(row);
			}
		}
		
	}
	
	public class DummyHandler implements JavaHandler {
		public DummyHandler() {}
		public void cancel(){}
	}
	
	public class HandlerCounter implements JavaHandler {
	    private int row_; 
	    private ATBookWrapper book_;
	    public HandlerCounter(ATBookWrapper book, int row) {
	    	book_ = book;
	    	row_ = row;
	    	statusCellRenderer_.addHandler(row);
	    	handlerManager_.addHandler(book, this);
	    	bookTable_.repaint();
	    } 
	    public void cancel(){
	    	statusCellRenderer_.cancelHandler(row_);
	    	handlerManager_.removeHandler(book_, this);
	    	bookTable_.repaint();
	    } 
	}
	
	public class ErrorMsg implements JavaHandler {
	    private String msg_; 
	    private int row_;
	    private ATBookWrapper book_;
	    public ErrorMsg(ATBookWrapper book, int row, String msg) {
	    	book_ = book;
	    	row_ = row;
	    	msg_ = msg;
			statusCellRenderer_.addError(row, msg);
	    	handlerManager_.addHandler(book, this);
	    	bookTable_.repaint();
	    } 
	    public void cancel(){
	    	statusCellRenderer_.removeError(row_, msg_);
	    	handlerManager_.removeHandler(book_, this);
	    	bookTable_.repaint();
	    } 
	}
	
	class IconCellRenderer extends DefaultTableCellRenderer {
		
		private HashSet<Integer> greyOnes_ = new HashSet<Integer>();
		private HashMap<Integer, Integer> bookStates_ = new HashMap<Integer, Integer>();
		private HashMap<Integer, Vector<String>> bookErrors_ = new HashMap<Integer, Vector<String>>();
		
		private ImageIcon greenIcon_ = createImageIcon("/icons/green.png");
		private ImageIcon orangeIcon_ = createImageIcon("/icons/orange.png");
		private ImageIcon redIcon_ = createImageIcon("/icons/red.png");
		private ImageIcon greyIcon_ = createImageIcon("/icons/grey.png");
		
		protected ImageIcon createImageIcon(String path) {
			java.net.URL imgURL = getClass().getResource(path);
			if (imgURL != null) {
				return new ImageIcon(imgURL);
			} else {
				System.err.println("Couldn't find file: " + path);
				return null;
			}
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int viewRow, int column) {
						
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, viewRow, column);
			int row = sorter_.convertRowIndexToModel(viewRow);
			
			setHorizontalAlignment(SwingConstants.CENTER);
			this.setOpaque(true);
			this.setToolTipText(null);
			boolean iconSet = false;
			
			Integer current = bookStates_.get(row);
			if(current != null && current > 0) {
				setIcon(orangeIcon_);
				iconSet = true;
			}

			Vector<String> errors = bookErrors_.get(row);
			if(errors != null && !errors.isEmpty()) {
				if(!iconSet) {
					setIcon(redIcon_);
				}
				String errorMsgs = "";
				for(Iterator<String> i = errors.iterator() ; i.hasNext() ; ) {
					errorMsgs = errorMsgs + i.next() + " ; ";
				}
				errorMsgs = errorMsgs.trim();
				this.setToolTipText(errorMsgs);
			} else {
				if(!iconSet) {
					if (greyOnes_.contains(row)) {

						setIcon(greyIcon_);
					} else {
						setIcon(greenIcon_);
					}
				}
			}


			return this;
		}
		
		public void setAvailable(int row, boolean isAvailable) {
			if(isAvailable) {
				greyOnes_.remove(row);
			} else {
				greyOnes_.add(row);
			}
		}
		
		public void addHandler(int row) {
			Integer current = bookStates_.get(row);
			if(current == null){
				current = 0;
			}
			bookStates_.put(row, (current + 1));
		}
		
		public void cancelHandler(int row) {
			Integer current = bookStates_.get(row);
			if(current == null || current < 1){
				System.err.println("Handler level for book at row #" + row + " was 0.");
			} else {
				bookStates_.put(row, (current - 1));
			}
		}
		
		public void addError(int row, String msg) {
			Vector<String> errors = bookErrors_.get(row);
			if(errors == null) {
				errors = new Vector<String>();
				bookErrors_.put(row, errors);
			}
			errors.add(msg);
		}
		
		public void removeError(int row, String msg) {
			Vector<String> errors = bookErrors_.get(row);
			if(errors != null) {
				errors.remove(msg);
			}
		}
	}
	
	// header renderer
	class HeaderRenderer extends DefaultTableCellRenderer {
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    	
	    	// Inherit the colors and font from the header component
	        if (bookTable_ != null) {
	            JTableHeader header = bookTable_.getTableHeader();
	            if (header != null) {
	                setForeground(header.getForeground());
	                setBackground(header.getBackground());
	                setFont(header.getFont());
	            }
	        }

	        if (value instanceof JLabel) {
	            setIcon(((JLabel) value).getIcon());
	            setText(((JLabel) value).getText());
	        } else {
	            setText((value == null) ? "" : value.toString());
	            setIcon(null);
	        }
	        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        setHorizontalAlignment(JLabel.CENTER);
	        return this;
	    }
	};
	
	// model holding data for the book table
	private class BookTableModel extends AbstractTableModel {
		private Vector<ATBookWrapper> books_ = new Vector<ATBookWrapper>();
		
		private final int statsIdx_   = 0;
		private final int titleIdx_   = 1;
		private final int authorsIdx_ = 2;
		private final int ratingIdx_  = 3;
		private final int favorIdx_     = 4;

		public String getColumnName(int col) {
			String[] columnNames = { "", "Title", "Authors", "Rating", "Match" };
			return columnNames[col];
		}
		
		public Class getColumnClass(int col) {
			if(col == statsIdx_) {
				return ImageIcon.class;
			} else {
				return String.class;
			}
		}

		public void addBook(ATBookWrapper book) {
			books_.add(book);
			this.fireTableDataChanged();
		}

		public void removeBook(ATBookWrapper book) {
			books_.remove(book);
			this.fireTableDataChanged();
		}
		
		public void updateBook(ATBookWrapper book) {
			this.fireTableDataChanged();
		}

		public void setContents(Vector books) {
			books_ = books;
			this.fireTableDataChanged();
		}

		public int getColumnCount() { return 5; };
		public int getRowCount() { return books_.size(); };

		public int findRow(ATBookWrapper book) {
			int index = 0;
			ATBookWrapper current;
			for (Iterator i = books_.iterator() ; i.hasNext() ; ) {
				current = (ATBookWrapper) i.next();
				if (current.equals(book)) {
					return index;
				}
				index++;
			}
			return -1; // error
		}
		
		public ATBookWrapper getBook(int row) {
			return books_.elementAt(row);
		}

		public Object getValueAt(int row, int col) {
			if (books_.size() == 0) {
				return null;
			}
			if (row >= books_.size()) {
				return null;
			}
			ATBookWrapper book = (ATBookWrapper)books_.get(row);
			switch(col) {
				case statsIdx_: return null;
				case titleIdx_: return book.getTitle();
				case authorsIdx_: return book.getAuthors();
				case ratingIdx_: return book.getRating();
				case favorIdx_: return book.getMatchFromString(keywords_); //TODO
			}
			return null;
		}
		public boolean isCellEditable(int row, int col)	{ 
			return (col != statsIdx_ && col != ratingIdx_ && col != favorIdx_); 
		}
		public void setValueAt(Object value, int row, int col) {
			ATBookWrapper book = books_.elementAt(row);
			switch(col) {
				case titleIdx_: book.setTitle((String) value); break;
				case authorsIdx_: book.setAuthors((String) value); break;
				default: System.err.println("Cannot edit field in column #" + col); break;
			}
			fireTableCellUpdated(row, col);
		}
	}
	
	// listeners for updated keyword text field
	class KeywordActionListener implements ActionListener {
        // Handles the text field return
        public void actionPerformed(ActionEvent e) {
        	// ignore
        	// System.out.println("Action on keyword field");
        }
    }
	
	class KeywordDocumentListener implements DocumentListener {
		private String newline = "\n";

		public void insertUpdate(DocumentEvent e) {
			updateKeywords(e);
		}
		public void removeUpdate(DocumentEvent e) {
			updateKeywords(e);
		}
		public void changedUpdate(DocumentEvent e) {
			//Plain text components do not fire these events
		}

		public void updateKeywords(DocumentEvent e) {
			Document doc = (Document)e.getDocument();
			keywords_ = keywordField_.getText();
			bookTable_.repaint();
		}
	}

}


