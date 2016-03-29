package gui;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class Tables
{
	private static List<TableData> tableData = new ArrayList<TableData>();
	private static List<EditController> editControllers = new ArrayList<EditController>();

	/**
	 * Allows and disallows fixed table cells to be edited
	 */
	private static class EditController
	{
		JTable table;
		List<Point> editable = new ArrayList<Point>();
		List<Point> nonEditable = new ArrayList<Point>();

		public EditController(JTable table)
		{
			this.table = table;
		}
	}

	private static class TableData
	{
		JTable table = null;
		int column = 0;
		int width = -1;
		String title = "";
	}

	public static void setTableRows(JTable table, int rows)
	{
		if (table == null) return;
		((DefaultTableModel)table.getModel()).setRowCount(rows);
	}

	public static void setTableFont(JTable table, String fontName, int size, boolean boldHeader)
	{
		if (table == null || fontName == null || fontName.trim().length() == 0) return;
		fontName = fontName.trim();
		table.setFont(new Font(fontName, Font.PLAIN, size));
		JTableHeader header = table.getTableHeader();
		if (header == null) return;
		header.setFont(new Font(fontName, boldHeader ? Font.BOLD : Font.PLAIN, size));
	}

	public static void setTableFont(JTable table, Font font, boolean boldHeader)
	{
		if (font == null) return;
		setTableFont(table, font.getFontName(), font.getSize(), boldHeader);
	}

	/**
	 * This will set the column width AFTER all columns have been defined. Cancels the presetColumn and finalizeColumnWidths
	 * operations
	 */
	public static void changeColumnWidth(JTable table, int columnNo, int width)
	{
		if (table == null || columnNo < 0 || columnNo >= table.getColumnModel().getColumnCount()) return;
		TableColumn column = table.getColumnModel().getColumn(columnNo);
		column.setMaxWidth(width);
		column.setMinWidth(width);
		column.setPreferredWidth(width);
	}

	/**
	 * Cancels the presetColumn and finalizeColumnWidths operations
	 */
	public static boolean changeColumnTitle(JTable table, int columnNo, String title)
	{
		if (table == null || columnNo < 0 || columnNo >= table.getColumnModel().getColumnCount()) return false;
		TableColumn column = table.getColumnModel().getColumn(columnNo);
		column.setHeaderValue(title);
		return true;
	}

	/**
	 * Need to call finalizeColumn when all columns have been defined. Use width==-1 to make column resizable
	 */
	public static void presetColumn(JTable table, int columnNo, String title, int width)
	{
		presetColumn(table, columnNo, true, title, true, width);
	}

	/**
	 * Need to call finalizeColumn when all columns have been defined. Use width==-1 to make column resizable
	 */
	public static void presetColumnTitle(JTable table, int columnNo, String title)
	{
		presetColumn(table, columnNo, true, title, false, 0);
	}

	/**
	 * Need to call finalizeColumn when all columns have been defined. Use width==-1 to make column resizable
	 */
	public static void presetColumn(JTable table, String title, int width)
	{
		if (table == null) return;
		int col = 0;
		for (int i = 0; i < tableData.size(); i++)
			if (tableData.get(i).table == table) col++;
		presetColumn(table, col, true, title, true, width);
	}

	/**
	 * Need to call finalizeColumn when all columns have been defined. Use width==-1 to make column resizable
	 */
	public static void presetColumnWidth(JTable table, int columnNo, int width)
	{
		presetColumn(table, columnNo, false, "", true, width);
	}

	private static void presetColumn(JTable table, int columnNo, boolean setTitle, String title, boolean setWidth, int width)
	{
		if (table == null || columnNo < 0) return;
		int at = -1;
		for (int i = 0; i < tableData.size(); i++)
		{
			TableData data = tableData.get(i);
			if (data.table == table && columnNo == data.column)
			{
				at = i;
				break;
			}
		}
		TableData data;
		if (at >= 0)
			data = tableData.get(at);
		else
		{
			data = new TableData();
			data.table = table;
			data.column = columnNo;
		}
		if (setTitle) data.title = title;
		if (setWidth) data.width = width;
		if (at < 0) tableData.add(data);
	}

	private static int getColumnCountForTable(JTable table)
	{
		if (table == null) return 0;
		int columnCount = 0;
		for (int i = tableData.size() - 1; i >= 0; i--)
		{
			TableData data = tableData.get(i);
			if (data.table == null || data.table != table) continue;
			if (data.column + 1 > columnCount) columnCount = data.column + 1;
		}
		return columnCount;
	}

	/**
	 * To apply changes made with presetColumn*
	 */
	public static void finalizeColumns(JTable table)
	{
		if (table == null) return;
		int columnCount = getColumnCountForTable(table);
		if (columnCount < 1) return;
		boolean definedColumns[] = new boolean[columnCount];
		for (int i = 0; i < tableData.size(); i++)
		{
			TableData data = tableData.get(i);
			if (data.table == null || data.table != table) continue;
			definedColumns[tableData.get(i).column] = true;
		}
		boolean columnsMissing = false;
		for (int i = 0; i < columnCount; i++)
		{
			if (!definedColumns[i])
			{
				columnsMissing = true;
				presetColumn(table, i, "xxx", -1);
			}
		}
		if (columnsMissing)
		{
			finalizeColumns(table);
			return;
		}
		List<TableData> thisData = new ArrayList<TableData>();
		for (int i = tableData.size() - 1; i >= 0; i--)
		{
			TableData data = tableData.get(i);
			if (data.table == null) tableData.remove(i);
			if (data.table != table) continue;
			thisData.add(data);
			if (data.column + 1 > columnCount) columnCount = data.column + 1;
			tableData.remove(i);
		}
		String titles[] = new String[columnCount];
		DefaultTableModel grid = (DefaultTableModel)table.getModel();
		grid.setColumnCount(0);
		for (int i = 0; i < columnCount; i++)
		{
			grid.addColumn("col" + i);
			titles[i] = "";
		}
		for (int i = 0; i < columnCount; i++)
		{
			TableData data = thisData.get(i);
			int columnNo = data.column;
			titles[columnNo] = data.title;
		}
		grid.setColumnIdentifiers(titles);
		for (int i = 0; i < columnCount; i++)
		{
			TableData data = thisData.get(i);
			int columnNo = data.column;
			titles[columnNo] = data.title;
			int width = data.width;
			if (width < 0) continue;
			TableColumn column = table.getColumnModel().getColumn(columnNo);
			column.setMaxWidth(width);
			column.setMinWidth(width);
			column.setPreferredWidth(width);
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // default behaviour
	}

	/**
	 * To apply changes to all tables made with presetColumn*
	 */
	public static void finalizeTablesColumns()
	{
		while (tableData.size() > 0)
			finalizeColumns(tableData.get(0).table);
	}

	public static void setColumns(JTable table, int columnCount)
	{
		if (table == null || columnCount < 0) return;
		DefaultTableModel grid = (DefaultTableModel)table.getModel();
		grid.setColumnCount(0);
		for (int i = 0; i < columnCount; i++)
			grid.addColumn("");
	}

	public static int jumpSelectTableRow(JTable table, int columnNo, char c)
	{
		if (table == null) return -1;
		int row = table.getSelectedRow();
		if (!Character.isLetterOrDigit(c)) return row;
		String s = ("" + c).toLowerCase();
		int found = -1;
		int upTo = table.getRowCount() - 1;
		for (int i = row + 1; i <= upTo; i++)
		{
			String value = table.getModel().getValueAt(i, columnNo).toString();
			if (value.toLowerCase().startsWith(s))
			{
				found = i;
				break;
			}
		}
		if (found == -1) for (int i = 0; i < row; i++)
		{
			String value = table.getModel().getValueAt(i, columnNo).toString();
			if (value.toLowerCase().startsWith(s))
			{
				found = i;
				break;
			}
		}
		if (found >= 0)
		{
			selectTableRow(table, found);
			return found;
		}
		return row;
	}

	public static int scrollToLastRow(final JTable table)
	{
		if (table == null) return -1;
		return scrollToRow(table, table.getRowCount() - 1);
	}

	public static int scrollToRow(final JTable table, int row)
	{
		if (table == null) return -1;
		int rows = table.getRowCount();
		if (rows < 2) return -1;
		if (row < 0) row = 0;
		if (row >= rows) row = rows - 1;
		final int targetRow = row;
		Runnable scroll = new Runnable()
		{
			public void run()
			{
				table.scrollRectToVisible(table.getCellRect(targetRow, 0, true));
			}
		};
		SwingUtilities.invokeLater(scroll);
		return targetRow;
	}

	/**
	 * Cancels all previous editing instructions for individual cells. Must be called after all columns have been inserted into
	 * the table and before finalizeTablesColumns
	 */
	public static void setCellEditable(JTable table, final boolean editable)
	{
		if (table == null) return;
		int columns[] = new int[getColumnCountForTable(table)];
		for (int i = 0; i < columns.length; i++)
			columns[i] = i;
		setCellEditable(table, editable, columns);
	}

	/**
	 * Cancels all previous editing instructions for individual cells
	 */
	private static void setCellEditable(final JTable table, final boolean isEditable, final int... columns)
	{
		if (table == null) return;
		EditController c = getEditController(table);
		if (c != null) editControllers.remove(c);
		DefaultTableModel grid = new DefaultTableModel()
		{
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				final EditController controller = getEditController(table);
				if (controller != null)
				{
					Point point = new Point(rowIndex, columnIndex);
					for (int i = 0; i < controller.nonEditable.size(); i++)
						if (controller.nonEditable.get(i).equals(point)) return false;
					for (int i = 0; i < controller.editable.size(); i++)
						if (controller.editable.get(i).equals(point)) return true;
				}
				for (int i = 0; i < columns.length; i++)
				{
					if (columnIndex == columns[i]) return isEditable;
				}
				return !isEditable;
			}
		};
		table.setModel(grid);
	}

	private static EditController getEditController(JTable table)
	{
		if (table == null) return null;
		for (int i = 0; i < editControllers.size(); i++)
			if (editControllers.get(i).table == table) return editControllers.get(i);
		return null;
	}

	/**
	 * Changes editability of a particular cell without affecting the rest of the cells
	 */
	public static void setCellEditable(JTable table, final int row, final int column, final boolean isEditable)
	{
		if (table == null) return;
		EditController controller = getEditController(table);
		if (controller == null)
		{
			controller = new EditController(table);
			editControllers.add(controller);
		}
		Point point = new Point(row, column);
		for (int i = controller.nonEditable.size() - 1; i >= 0; i--)
			if (controller.nonEditable.get(i).equals(point)) controller.nonEditable.remove(i);
		for (int i = controller.editable.size() - 1; i >= 0; i--)
			if (controller.editable.get(i).equals(point)) controller.editable.remove(i);
		if (isEditable)
			controller.editable.add(point);
		else
			controller.nonEditable.add(point);
	}

	public static void resetEditController(JTable table)
	{
		if (table == null) return;
		for (int i = editControllers.size() - 1; i >= 0; i--)
			if (editControllers.get(i).table == table) editControllers.remove(i);
	}

	/**
	 * Cancels all previous editing instructions for individual cells
	 */
	public static void setCellEditable(JTable table, final int... editableColumns)
	{
		setCellEditable(table, true, editableColumns);
	}

	/**
	 * Cancels all previous editing instructions for individual cells
	 */
	public static void setCellEditableExcept(JTable table, final int... nonEditableColumns)
	{
		setCellEditable(table, false, nonEditableColumns);
	}

	public static void addTableRow(JTable table)
	{
		addTableRows(table, 1, "");
	}

	public static void addTableRows(JTable table, int noOfRows, Object... colValues)
	{
		if (noOfRows < 1 || table == null) return;
		int cols = table.getColumnCount();
		Object s[] = new Object[cols];
		for (int i = 0; i < noOfRows; i++)
		{
			for (int col = 0; col < cols; col++)
			{
				if (colValues == null || col >= colValues.length)
					s[col] = null;
				else
					s[col] = colValues[col];
			}
			((DefaultTableModel)table.getModel()).addRow(s);
		}
	}

	/** if row does not exist, a new row is added to the end of the table */
	public static void setTableRow(JTable table, int row, Object... colValues)
	{
		if (table == null) return;
		if (row < 0) row = 0;
		int rows = table.getRowCount();
		if (row >= rows)
		{
			addTableRows(table, 1, colValues);
			return;
		}
		int cols = table.getColumnCount();
		for (int col = 0; col < cols; col++)
		{
			Object value = null;
			if (colValues != null && col < colValues.length) value = colValues[col];
			((DefaultTableModel)table.getModel()).setValueAt(value, row, col);
		}
	}

	/**
	 * Selects a single row (unselecting previously selected ones).
	 *
	 * @param table
	 * @param row
	 * @return
	 */
	public static int selectTableRow(JTable table, int row)
	{
		return selectTableRow(table, row, false);
	}

	public static int selectTableRow(JTable table, int row, boolean addToExisting)
	{
		if (table == null) return -1;
		int rows = table.getRowCount();
		if (row < 0 || row > rows - 1)
		{
			if (!addToExisting) table.clearSelection();
			return -1;
		}
		if (addToExisting)
			table.addRowSelectionInterval(row, row);
		else
			table.setRowSelectionInterval(row, row);
		return row;
	}

	/**
	 *
	 * @param table
	 * @param row
	 *            -1 will return header of table (useful for loops)
	 * @return
	 */
	public static String[] getTableRow(JTable table, int row)
	{
		if (table == null || row < -1 || row > table.getRowCount() - 1 || table.getColumnCount() == 0) return new String[0];
		if (row == -1) return getTableHeaders(table);
		String s[] = new String[table.getColumnCount()];
		for (int i = 0; i < table.getColumnCount(); i++)
			s[i] = table.getModel().getValueAt(row, i).toString();
		return s;
	}

	public static String[] getTableHeaders(JTable table)
	{
		if (table == null || table.getColumnCount() == 0) return new String[0];
		String s[] = new String[table.getColumnCount()];
		Enumeration<TableColumn> columns = table.getTableHeader().getColumnModel().getColumns();
		int i = 0;
		while (columns.hasMoreElements())
		{
			TableColumn column = columns.nextElement();
			Object header = column.getHeaderValue();
			if (header != null && header instanceof String)
				s[i] = (String)header;
			else
				s[i] = "";
			i++;
		}
		return s;
	}

	public static String getTableCell(JTable table, int row, int column)
	{
		if (row < 0 || row > table.getRowCount() - 1 || column < 0 || column > table.getColumnCount() - 1) return "";
		Object o = table.getModel().getValueAt(row, column);
		if (o == null) return "";
		return o.toString();
	}

	public static void setTableCell(JTable table, int row, int column, String text)
	{
		if (row < 0 || row > table.getRowCount() - 1 || column < 0 || column > table.getColumnCount() - 1) return;
		table.getModel().setValueAt(text, row, column);
	}

	/**
	 * Return the row and column where this mouse event took place.
	 *
	 * @param table
	 *            the table to check for mouse click
	 * @param mouseEvent
	 *            the mouse event
	 * @return a Point with the (row, cell) coordinates. If the table is null, the mouse event is null, or the mouse event did
	 *         not take place within the table, the returned value is null
	 */
	public static Point getTableCell(JTable table, MouseEvent mouseEvent)
	{
		if (table == null || mouseEvent == null) return null;
		Point at = new Point(mouseEvent.getX(), mouseEvent.getY());
		return getTableCell(table, at);
	}

	/**
	 * Return the row and column where a coordinate is found.
	 *
	 * @param table
	 *            the table to check for mouse click
	 * @param coordinates
	 *            the point to check for
	 * @return a Point with the (row, cell) coordinates. If the table is null, the coordinates point is null, or the coordinates
	 *         point is not inside the table, the returned value is null
	 */
	public static Point getTableCell(JTable table, Point coordinates)
	{
		if (table == null || coordinates == null) return null;
		int row = table.rowAtPoint(coordinates);
		int col = table.columnAtPoint(coordinates);
		if (col < 0 || row < 0) return null;
		return new Point(row, col);
	}

	public static boolean isRowSelected(JTable table, int row)
	{
		if (table == null) return false;
		int rows[] = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++)
			if (rows[i] == row) return true;
		return false;
	}

	public static void repaintCells(JTable table)
	{
		if (table == null) return;
		table.tableChanged(new TableModelEvent(table.getModel()));
	}
}
