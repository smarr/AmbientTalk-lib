package applications.rfid.ambientLibrary.util;

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
import applications.rfid.ambientLibrary.main.ATHandler;
import applications.rfid.ambientLibrary.main.ReviewManager;
import applications.rfid.ambientLibrary.main.HandlerManager.JavaHandler;

public class TagManager extends JFrame {
	
	private static final long serialVersionUID = -23670755322296555L;
	
	private ATTagManager tagManager_;
	
	private JPanel contentPane_ = new JPanel();
	private TagTableModel tagTableModel_ = new TagTableModel();
	private JTable tagTable_ = new JTable(tagTableModel_);
	private JScrollPane tagTableScrollPane_ = new JScrollPane(tagTable_);
	
	private JPopupMenu popupMenu_ = new JPopupMenu();
	private Vector<String> modelNames_ = new Vector<String>();
	
	private String commandPrefix_ = "initialize as ";
	
	public interface ATTagManager {
		public void initialize(String serial, String modelName);
	}


	public TagManager(ATTagManager tm) {
		super("Empty Tags");
		
		tagManager_ = tm;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tagTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		contentPane_.setBorder(new EmptyBorder(10, 5, 10, 5));
		contentPane_.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane_);
		
		contentPane_.add(tagTableScrollPane_);
		
		tagTable_.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		tagTable_.setRowSelectionAllowed(true);
	    
	    MouseListener popupListener = new PopupListener();
	    tagTable_.addMouseListener(popupListener);
		
		this.pack();
		this.setVisible(true);

	}


	public void addTag(String serial) {
		tagTableModel_.addTag(serial);
	}
	
	public void removeTag(String serial) {
		tagTableModel_.removeTag(serial);
	}
	
	public void addModel(String name) {
		modelNames_.add(name);
		JMenuItem modelItem = new JMenuItem(commandPrefix_ + name);
		modelItem.addActionListener(new ModelInitializerActionAdapter(this));
	    popupMenu_.add(modelItem);
	}

	public void tableDataChanged() {
		tagTableModel_.fireTableDataChanged();
	}
	
	// actions for popup menu
	public void initializeTag(ActionEvent e) {
		int row = tagTable_.getSelectedRow();
		if(row < 0) {
			System.err.println("No row selected...");
		} else {
			String selectedTag = tagTableModel_.getTag(row);
			String modelName = e.getActionCommand().substring(commandPrefix_.length()).trim();
			System.out.println("intialize " + selectedTag + " as " + modelName);
			tagManager_.initialize(selectedTag, modelName);
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
	class ModelInitializerActionAdapter implements ActionListener {
		TagManager adaptee;

		ModelInitializerActionAdapter(TagManager adaptee) {
			this.adaptee = adaptee;
		}
		public void actionPerformed(ActionEvent e) {
			adaptee.initializeTag(e);
		}
	}

	
	// model holding data for the book table
	private class TagTableModel extends AbstractTableModel {
		private Vector<String> tags_ = new Vector<String>();

		public String getColumnName(int col) {
			String[] columnNames = { "serial" };
			return columnNames[col];
		}
		
		public Class getColumnClass(int col) {
			return String.class;
		}

		public void addTag(String serial) {
			tags_.add(serial);
			this.fireTableDataChanged();
		}

		public void removeTag(String serial) {
			tags_.remove(serial);
			this.fireTableDataChanged();
		}

		public void setContents(Vector tags) {
			tags_ = tags;
			this.fireTableDataChanged();
		}

		public int getColumnCount() { return 1; };
		public int getRowCount() { return tags_.size(); };

		public int findRow(String serial) {
			int index = 0;
			String current;
			for (Iterator<String> i = tags_.iterator() ; i.hasNext() ; ) {
				current = i.next();
				if (current.equals(serial)) {
					return index;
				}
				index++;
			}
			return -1; // error
		}
		
		public String getTag(int row) {
			return tags_.elementAt(row);
		}

		public Object getValueAt(int row, int col) {
			if (tags_.size() == 0) {
				return null;
			}
			if (row >= tags_.size()) {
				return null;
			}
			String serial = (String) tags_.get(row);
			return serial;
		}
		
		public boolean isCellEditable(int row, int col)	{ 
			return false; 
		}

	}

}


