import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class TablesApp extends JFrame {
    public TablesApp() {
        setupFrame();
        setupComponents();
        setupGui();
        setupTableContent();
    }

    private void setupFrame() {
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("TablesApp");
    }

    private void setupComponents() {
        filesTable = setupTable();
        //
        buttonsPanel = new JPanel();
        buttonsPanel.setPreferredSize(new Dimension(0, 40));
        buttonsPanel.setLayout(null);
        buttonsPanel.add(shiftButton("Top", 0, -99999));
        buttonsPanel.add(shiftButton("Up", 100, -1));
        buttonsPanel.add(shiftButton("Down", 200, 1));
        buttonsPanel.add(shiftButton("Bottom", 300, 99999));

        JButton performButton = new JButton("Perform");
        performButton.setBounds(400, 11, 91, 23);
        performButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //				List<File> files = getSelectedFiles(filesTable);
                DefaultGroovyMethods.println(this, codeArea.getText());
                doSomethingWithFile(codeArea.getText());

            }

        });
        buttonsPanel.add(performButton);

        JButton removeButton = new JButton("Remove");
        removeButton.setBounds(500, 11, 91, 23);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = filesTable.getSelectedRow();
                JOptionPane.showMessageDialog(null, "The removed item : " + selectedRow);
                ((DefaultTableModel) filesTable.getModel()).removeRow(selectedRow);
            }

        });
        buttonsPanel.add(removeButton);

        //
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(filesTable);

    }

    private JButton shiftButton(String title, int x, final int shift) {
        JButton button = new JButton(title);
        button.setBounds(x, 11, 91, 23);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveTableRows(filesTable, shift);
            }

        });
        return button;
    }

    private void setupGui() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);
        contentPane.add(createTopPanel(), BorderLayout.NORTH);
    }

    private JTable setupTable() {
        final JTable table = new JTable();
        //
        Tables.presetColumn(table, COLUMN_ID, "ID", 60);
        Tables.presetColumn(table, COLUMN_FILENAME, "Filename", -1);
        Tables.presetColumn(table, COLUMN_SIZE, "Size", 60);
        Tables.presetColumn(table, COLUMN_RESULT, "Result", -1);
        //
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setShowHorizontalLines(true);
        Tables.setCellEditable(table, false);
        Tables.finalizeColumns(table);
        //
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) doFileRunAction();
                else doFileSelectAction();
            }

        });
        return table;
    }

    private void setupTableContent() {
        java.util.List<File> projectFiles = loadFiles(new File("./"));
        setTableData(projectFiles);
    }

    private List<File> loadFiles(File directory) {
        List<File> files = new ArrayList<File>();
        addDirectoryFiles(files, directory);
        return files;
    }

    private void addDirectoryFiles(List<File> currentFiles, File directory) {
        File[] dirFiles = directory.listFiles();
        for (int i = 0; i < dirFiles.length; i++) {
            if (dirFiles[i].isDirectory()) addDirectoryFiles(currentFiles, dirFiles[i]);
            else if (dirFiles[i].isFile()) currentFiles.add(dirFiles[i]);
        }

    }

    private void setTableData(List<File> files) {
        JTable table = filesTable;
        Tables.setTableRows(table, Math.max(files.size(), 1));
        TableModel model = filesTable.getModel();
        for (int row = 0; row < files.size(); row++) {
            File file = files.get(row);
            model.setValueAt("ID" + (101 + row), row, COLUMN_ID);
            model.setValueAt(String.format("%,d", file.length()), row, COLUMN_SIZE);
            model.setValueAt(file, row, COLUMN_FILENAME);
        }

    }

    /**
     * Rows are always returned from smallest index to largest.
     */
    private List<Integer> getSelectedRows(JTable table) {
        int[] sourceArray = table.getSelectedRows();
        Integer[] targetArray = new Integer[sourceArray.length];
        for (int i = 0; i < sourceArray.length; DefaultGroovyMethods.next(i))
            targetArray[i] = Integer.valueOf(sourceArray[i]);
        List<Integer> rows = Arrays.asList(targetArray);
        // Collections.sort(rows);
        return rows;
    }

    private List<File> getSelectedFiles(JTable table) {
        List<Integer> rows = getSelectedRows(table);
        return getFilesFromRows(rows);
    }

    private List<File> getFilesFromRows(List<Integer> rows) {
        List<File> files = new ArrayList<File>();
        for (int i = 0; i < rows.size(); i++) {
            File file = (File) filesTable.getValueAt(rows.get(i), COLUMN_FILENAME);
            ((ArrayList<File>) files).add(file);
        }

        return files;
    }

    private void moveTableRows(JTable table, int shift) {
        if (table.getSelectedRowCount() == 0) return;

        List<Integer> rows = getSelectedRows(table);
        table.clearSelection();
        for (int i = 0; i < rows.size(); i++)
            if (shift < 0) moveTableRow(table, rows, i, shift);
            else moveTableRow(table, rows, (int) rows.size() - i - 1, shift);
    }

    private void moveTableRow(JTable table, List<Integer> rows, int selectedRowIndex, int shift) {
        int selectedRow = rows.get(selectedRowIndex);
        int targetRow = selectedRow + shift;
        if (targetRow != selectedRow) {
            targetRow = Math.max(selectedRowIndex, targetRow);
            targetRow = Math.min(targetRow, (int) table.getRowCount() - (rows.size() - selectedRowIndex));
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.moveRow(selectedRow, selectedRow, targetRow);
        }

        Tables.selectTableRow(table, targetRow, true);
    }

    private void doFileSelectAction() {
        List<File> files = getSelectedFiles(filesTable);
        for (int i = 0; i < files.size(); i++) {
            if (i == 0) System.out.println("****************");
            System.out.println("selected file = " + files.get(i).getPath());
        }

    }

    private void doFileRunAction() {
        List<File> files = getSelectedFiles(filesTable);
        if (files.size() > 0) {
            System.out.println("****************");
            System.out.println("First selected file to run = " + files.get(0).getPath());
        }

    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.add(createTemplateLabel());
        panel.add(createComboBox());
        codeArea = new JTextArea();
        codeArea.setRows(3);
        codeArea.setColumns(50);
        panel.add(codeArea);
        return panel;
    }

    private JLabel createTemplateLabel() {
        return new JLabel("Choose Template:");
    }

    private JComboBox createComboBox() {
        final JComboBox cBox = new JComboBox();
        cBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //	                clearTextArea();
                //	                loadConfigurationLine(cBox);
            }

        });
        //	        loadConfigurationToComboBox(cBox);
        cBox.addItem("Hello");

        return cBox;
    }

    public void doSomethingWithFile(String code) {
        TableModel model = filesTable.getModel();
        List<Integer> allRows = new ArrayList<Integer>();
        for (int count = 0; count < model.getRowCount(); count++) ((ArrayList<Integer>) allRows).add(count);

        List<File> files = getFilesFromRows(allRows);
        FileHandler fh = new FileHandler();
        fh.setCode(code);
        for (int row = 0; row < files.size(); row++) {
            File file = files.get(row);
            Object result = fh.doSomethingWithFile(file, row);
            model.setValueAt(result, row, COLUMN_RESULT);
        }

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }

        TablesApp application = new TablesApp();
        application.setVisible(true);
    }

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_FILENAME = 1;
    private static final int COLUMN_SIZE = 2;
    private static final int COLUMN_RESULT = 3;
    private JScrollPane scrollPane;
    private JTable filesTable;
    private JPanel buttonsPanel;
    private JTextArea codeArea;
}
