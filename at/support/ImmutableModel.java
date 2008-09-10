/**
 * AmbientTalk/2 Project
 * (c) Programming Technology Lab, 2006 - 2008
 * Authors: Tom Van Cutsem & Stijn Mostinckx
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * @author Christophe Scholliers
 * @email cfscholl@vub.ac.be
 */
package at.support;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author cfscholl
 */

public class ImmutableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImmutableModel(String[] columnNames, int rowCount)  {
		super(columnNames, rowCount);
	}
	
	public void addCellDescriptor(CellDescriptor cd,int row, int col) {
		super.setValueAt(cd, row, col);
	}
	
	public void addRow(CellDescriptor name,CellDescriptor val) {
		CellDescriptor[] tab = {name,val};
		super.addRow(tab);
	}

	public boolean isCellEditable(int row,int col) {
		CellDescriptor cellDiscriptor = (CellDescriptor)getValueAt(row, col);
		return cellDiscriptor.isField();
 	};
 	
 	public void setValueAt(Object value, int row, int col) {
 		 System.out.println("Setting value at " + row + "," + col
                 + " to " + value
                 + " (an instance of "
                 + value.getClass() + ")");  
 		CellDescriptor cellDiscriptor =  (CellDescriptor)getValueAt(row, col);
		cellDiscriptor.setValue(value);
		fireTableCellUpdated(row, col);
      }

	static public int indexOf(String string, String pattern, int pos) {
		return string.indexOf(pattern,pos);
	};
	
	static public Matcher matcher(Pattern p, String s) {
		return p.matcher(s);
	};
 	
}
