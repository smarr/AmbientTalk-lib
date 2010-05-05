package at.rfid.demo.libraryapp;

import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.sun.codemodel.internal.JLabel;

public class LibraryWindow extends JFrame {

	private static final long serialVersionUID = -4421477872108476695L;

	private BookTableModel bookTableModel_ = new BookTableModel();
	private JTable bookTable_ = new JTable(bookTableModel_);
	private JScrollPane bookTableScrollPane_ = new JScrollPane(bookTable_);
	private BookTableCellRenderer bookTableCellRenderer_ = new BookTableCellRenderer();

	public interface ATBookWrapper {
		public String getTitle();
		public String getAuthors();
		public String getRating();
	}

	public LibraryWindow() {
		super("Ambient Library");
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(bookTableScrollPane_);
		
		bookTable_.setDefaultRenderer(String.class, bookTableCellRenderer_);
		
		this.pack();
		this.setVisible(true);

	}


	public void addBook(ATBookWrapper book) {
		bookTableModel_.addBook(book);
	}
	
	public void removeBook(ATBookWrapper book) {
		bookTableModel_.removeBook(book);
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
		try {
			if (row >= 0) {
				bookTableCellRenderer_.setAvailable(row, isAvailable);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	class BookTableCellRenderer extends DefaultTableCellRenderer {
		
		HashSet<Integer> greyOnes = new HashSet<Integer>();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (greyOnes.contains(row)) {
				setForeground(Color.gray);
			} else {
				setForeground(Color.black);
			}
			setText((String) value);
			this.invalidate();
			this.repaint();
			return this;
		}
		
		public void setAvailable(int row, boolean isAvailable) {
			if(isAvailable) {
				greyOnes.remove(row);
			} else {
				greyOnes.add(row);
			}
		}
	}

	private class BookTableModel extends AbstractTableModel {
		private Vector books_ = new Vector();
		private int titleIdx_   = 0;
		private int authorsIdx_ = 1;
		private int ratingIdx_  = 2;

		public String getColumnName(int col) {
			String[] columnNames = { "Title", "Authors", "Rating" };
			return columnNames[col];
		}
		
		public Class getColumnClass(int col) {
			return String.class;
		}

		public void addBook(ATBookWrapper book) {
			books_.add(book);
			this.fireTableDataChanged();
		}

		public void removeBook(ATBookWrapper book) {
			books_.remove(book);
			this.fireTableDataChanged();
		}

		public void setContents(Vector books) {
			books_ = books;
			this.fireTableDataChanged();
		}

		public int getColumnCount() { return 3; };
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

		public Object getValueAt(int row, int col) {
			if (books_.size() == 0) {
				return null;
			}
			if (row >= books_.size()) {
				return null;
			}
			ATBookWrapper book = (ATBookWrapper)books_.get(row);
			if (col == titleIdx_) {
				return book.getTitle();
			}
			if (col == authorsIdx_) {
				return book.getAuthors();
			}
			if (col == ratingIdx_) {
				return book.getRating();
			}
			return null;
		}
	}
}


