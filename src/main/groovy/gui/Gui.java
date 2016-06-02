package gui;import java.awt.*;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import java.awt.event.MouseAdapter;import java.awt.event.MouseEvent;import java.beans.Beans;import java.io.BufferedReader;import java.io.File;import java.io.FileReader;import java.io.IOException;import java.util.ArrayList;import java.util.List;import javax.swing.JButton;import javax.swing.JFileChooser;import javax.swing.JFrame;import javax.swing.JLabel;import javax.swing.JMenu;import javax.swing.JMenuBar;import javax.swing.JMenuItem;import javax.swing.JOptionPane;import javax.swing.JPanel;import javax.swing.JPopupMenu;import javax.swing.JScrollPane;import javax.swing.JSeparator;import javax.swing.JSplitPane;import javax.swing.JTabbedPane;import javax.swing.JTable;import javax.swing.JTextPane;import javax.swing.ListSelectionModel;import javax.swing.SwingConstants;import javax.swing.SwingUtilities;import javax.swing.filechooser.FileFilter;import javax.swing.filechooser.FileNameExtensionFilter;import javax.swing.table.DefaultTableCellRenderer;import javax.swing.table.DefaultTableModel;import logic.LogicIF;import logic.OperationFile;public class Gui extends JFrame {    private static final int BUTTON_WIDTH = 100;    private static final int COLUMN_ID = 0;    private static final int COLUMN_FILE = 1;    private static final int COLUMN_SIZE = 2;    private static final int COLUMN_RESULT = 3;    private static final Object CLIENT_PROPERTY_MENU_FILE = "client.property.file";    /**     * Any string that starts with this character is rendered using <code>TABLE_FONT_COLOR_ERROR</code> color instead of default     * color.     */    private static final String TABLE_TEXT_ERROR_MARK = "" + (char) 5;    private static final Color TABLE_FONT_COLOR_ERROR = Color.RED;    private static final Color TABLE_FONT_COLOR_DEFAULT = Color.BLACK;    private JSplitPane splitPane;    private JPanel tablePanel;    private JTabbedPane tabbedPane;    private JPanel codePanel;    private JPanel resultsPanel;    private JScrollPane resultsScrollPane;    private JTextPane resultsEditor;    private JScrollPane codeScrollPane;    private JTextPane codeEditor;    private JPanel consolePanel;    private JPanel consoleButtonsPanel;    private JPanel tableButtonsPanel;    private JScrollPane tablePane;    private JMenuBar mainMenu;    private JMenu fileMenu;    private JMenuItem openDirectoryMenu;    private JMenuItem addDirectoryMenu;    private JMenuItem codeFileMenu;    private JMenuItem exitMenu;    private JButton performButton;    private JTable filesTable;    private JMenuItem selectAllFilesMenu;    private JMenuItem selectNoFilesMenu;    private JMenuItem selectFilesPatternMenu;    private JMenuItem invertSelectedFilesMenu;    private JMenuItem keepSelectedFilesMenu;    private JMenuItem removeSelectedFilesMenu;    private JMenuItem copyFilePathMenu;    private JMenuItem openFileMenu;    private JMenuItem editFileMenu;    private LogicIF logic;    private String lastFilesPattern = "";    private File lastSelectedDir = null;    public Gui(LogicIF logic) {        this.logic = logic;        setupFrame();        setupGui();        getResultsEditor().setText("Logic implementation module description:\n\n" + logic.getDescription() + "\n\n"                + logic.getClass().getSimpleName() + ".java" + "\n\n\n\n\n\n\n");        getTabbedPane().setSelectedComponent(getResultsPanel());    }    private void setupFrame() {        if (Beans.isDesignTime())            setSize(600, 400);        else            setSize(800, 600);        setLocationRelativeTo(null);        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        setTitle("File Handler");    }    private void setupGui() {        getContentPane().setLayout(new BorderLayout(0, 0));        getContentPane().add(getSplitPane());        getContentPane().add(getMainMenu(), BorderLayout.NORTH);    }    private JSplitPane getSplitPane() {        if (splitPane == null) {            splitPane = new JSplitPane();            splitPane.setResizeWeight(0.7);            splitPane.setBorder(null);            splitPane.setDividerSize(4);            splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);            splitPane.setLeftComponent(getTablePanel());            splitPane.setRightComponent(getConsolePanel());        }        return splitPane;    }    private JPanel getTablePanel() {        if (tablePanel == null) {            tablePanel = new JPanel();            tablePanel.setLayout(new BorderLayout(0, 0));            tablePanel.add(getTablePane(), BorderLayout.CENTER);            tablePanel.add(getTableButtonsPanel(), BorderLayout.SOUTH);        }        return tablePanel;    }    private JTabbedPane getTabbedPane() {        if (tabbedPane == null) {            tabbedPane = new JTabbedPane(SwingConstants.TOP);            tabbedPane.addTab("Code", null, getCodePanel(), null);            tabbedPane.addTab("Results", null, getResultsPanel(), null);        }        return tabbedPane;    }    private JPanel getCodePanel() {        if (codePanel == null) {            codePanel = new JPanel();            codePanel.setLayout(new BorderLayout(0, 0));            codePanel.add(getCodeScrollPane(), BorderLayout.CENTER);        }        return codePanel;    }    private JPanel getResultsPanel() {        if (resultsPanel == null) {            resultsPanel = new JPanel();            resultsPanel.setLayout(new BorderLayout(0, 0));            resultsPanel.add(getResultsScrollPane(), BorderLayout.CENTER);        }        return resultsPanel;    }    private JScrollPane getResultsScrollPane() {        if (resultsScrollPane == null) {            resultsScrollPane = new JScrollPane();            resultsScrollPane.setViewportView(getResultsEditor());        }        return resultsScrollPane;    }    private JTextPane getResultsEditor() {        if (resultsEditor == null) {            resultsEditor = new JTextPane();            // resultsEditor.setEditable(false);            new CutCopyPasteMenu(resultsEditor, true);        }        return resultsEditor;    }    private JScrollPane getCodeScrollPane() {        if (codeScrollPane == null) {            codeScrollPane = new JScrollPane();            codeScrollPane.setViewportView(getCodeEditor());        }        return codeScrollPane;    }    private JTextPane getCodeEditor() {        if (codeEditor == null) {            codeEditor = new JTextPane();            codeEditor.setFont(new Font("Consolas", Font.PLAIN, 12));            new CutCopyPasteMenu(codeEditor, true);        }        return codeEditor;    }    private JPanel getConsolePanel() {        if (consolePanel == null) {            consolePanel = new JPanel();            consolePanel.setLayout(new BorderLayout(0, 0));            consolePanel.add(getTabbedPane(), BorderLayout.CENTER);            consolePanel.add(getConsoleButtonsPanel(), BorderLayout.SOUTH);        }        return consolePanel;    }    private JPanel getConsoleButtonsPanel() {        if (consoleButtonsPanel == null) {            consoleButtonsPanel = new JPanel();            consoleButtonsPanel.setPreferredSize(new Dimension(0, 25));            consoleButtonsPanel.setLayout(null);            consoleButtonsPanel.add(getPerformButton());        }        return consoleButtonsPanel;    }    private JPanel getTableButtonsPanel() {        if (tableButtonsPanel == null) {            tableButtonsPanel = new JPanel();            tableButtonsPanel.setPreferredSize(new Dimension(0, 25));            tableButtonsPanel.setLayout(null);            int leftX = 2;            int spaceX = 3;            tableButtonsPanel.add(moveRowButton("Top", 0 * (BUTTON_WIDTH + spaceX) + leftX, -99999));            tableButtonsPanel.add(moveRowButton("Up", 1 * (BUTTON_WIDTH + spaceX) + leftX, -1));            tableButtonsPanel.add(moveRowButton("Down", 2 * (BUTTON_WIDTH + spaceX) + leftX, 1));            tableButtonsPanel.add(moveRowButton("Bottom", 3 * (BUTTON_WIDTH + spaceX) + leftX, 99999));        }        return tableButtonsPanel;    }    private JButton moveRowButton(String title, int buttonPosX, final int rowsMoveAmount) {        JButton button = new JButton(title);        button.setBounds(buttonPosX, 2, BUTTON_WIDTH, 20);        button.setMargin(new Insets(0, 0, 0, 0));        button.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e) {                Tables.moveSelectedRows(getFilesTable(), rowsMoveAmount);            }        });        return button;    }    private JScrollPane getTablePane() {        if (tablePane == null) {            tablePane = new JScrollPane();            tablePane.setViewportView(getFilesTable());        }        return tablePane;    }    private JMenuBar getMainMenu() {        if (mainMenu == null) {            mainMenu = new JMenuBar();            mainMenu.add(getFileMenu());        }        return mainMenu;    }    private JMenu getFileMenu() {        if (fileMenu == null) {            fileMenu = new JMenu("File");            fileMenu.add(getOpenDirectoryMenu());            fileMenu.add(getAddDirectoryMenu());            fileMenu.add(getCodeFileMenu());            fileMenu.add(new JSeparator());            fileMenu.add(getExitMenu());        }        return fileMenu;    }    private JMenuItem getOpenDirectoryMenu() {        if (openDirectoryMenu == null) {            openDirectoryMenu = new JMenuItem("Open directory");            openDirectoryMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    File directory = selectDirectory(null);                    if (directory != null) {                        clearSelectedRows();                        List<File> files = loadFiles(directory);                        if (files != null)                            setTableRows(files);                    }                }            });        }        return openDirectoryMenu;    }    private JMenuItem getAddDirectoryMenu() {        if (addDirectoryMenu == null) {            addDirectoryMenu = new JMenuItem("Add directory");            addDirectoryMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    File directory = selectDirectory(null);                    if (directory != null) {                        List<File> files = loadFiles(directory);                        if (files != null)                            addTableRows(files);                    }                }            });        }        return addDirectoryMenu;    }    private JMenuItem getCodeFileMenu() {        if (codeFileMenu == null) {            codeFileMenu = new JMenuItem("Read Code");            codeFileMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    String code = selectCodeFile();                    codeEditor.setText(code);                }            });        }        return codeFileMenu;    }    private JMenuItem getExitMenu() {        if (exitMenu == null) {            exitMenu = new JMenuItem("Exit");            exitMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    System.exit(0);                }            });        }        return exitMenu;    }    private JButton getPerformButton() {        if (performButton == null) {            performButton = new JButton("Perform");            performButton.setBounds(2, 2, BUTTON_WIDTH, 20);            performButton.setMargin(new Insets(0, 0, 0, 0));            performButton.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    doPerform();                }            });        }        return performButton;    }    private JTable getFilesTable() {        if (filesTable == null) {            filesTable = new JTable();            filesTable.setDefaultRenderer(Object.class, new FileHandlerRenderer());            //            Tables.presetColumn(filesTable, COLUMN_ID, "ID", 60);            Tables.presetColumn(filesTable, COLUMN_FILE, "Filename", -1);            Tables.presetColumn(filesTable, COLUMN_SIZE, "Size", 78);            Tables.presetColumn(filesTable, COLUMN_RESULT, "Result", -1);            //            filesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);            filesTable.setRowSelectionAllowed(true);            filesTable.setShowHorizontalLines(true);            Tables.setCellEditable(filesTable, false);            Tables.finalizeColumns(filesTable);            //            filesTable.addMouseListener(new MouseAdapter() {                public void mouseClicked(MouseEvent e) {                    // if (e.getClickCount() == 2)                    // doFileRunAction();                    // else                    // doFileSelectAction();                }            });            filesTable.addMouseListener(new MouseAdapter() {                public void mouseReleased(MouseEvent e) {                    if (SwingUtilities.isRightMouseButton(e)) {                        Point coords = Tables.getTableCellCoords(filesTable, e);                        getFilesTablePopupMenu(coords.y, coords.x).show(filesTable, e.getX(), e.getY());                    }                }            });            return filesTable;        }        return filesTable;    }    /**     * <p>     * Implements the following rules:     * <ol>     * <li>Table columns with File values must show filenames only, and have a tooltip with the full file path</li>     * <li>Table columns with file sizes must be aligned to the right</l>     * <li>Text starting with <code>TABLE_TEXT_ERROR_MARK</code> is rendered in <code>TABLE_FONT_COLOR_ERROR</code></l>     * <li>All other columns have default rendering (default color text aligned to the left)</l>     * </p>     */    private static final class FileHandlerRenderer extends DefaultTableCellRenderer {        @Override        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,                                                       int row, int column) {            String tooltip = null;            if (value instanceof File) {                tooltip = ((File) value).getPath();                value = ((File) value).getName();            }            boolean isError = value != null && value.toString().startsWith(TABLE_TEXT_ERROR_MARK);            if (isError)                value = value.toString().substring(TABLE_TEXT_ERROR_MARK.length());            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);            JLabel label = (JLabel) component;            label.setToolTipText(tooltip);            label.setFont(new Font("Ubuntu Mono", Font.PLAIN, 16));            if (column == COLUMN_SIZE)                label.setHorizontalAlignment(SwingConstants.RIGHT);            else                label.setHorizontalAlignment(SwingConstants.LEFT);            if (isError)                label.setForeground(TABLE_FONT_COLOR_ERROR);            else                label.setForeground(TABLE_FONT_COLOR_DEFAULT);            return component;        }    }    private JPopupMenu getFilesTablePopupMenu(int row, int col) {        JPopupMenu filesTablePopupMenu = new JPopupMenu();        filesTablePopupMenu.add(getSelectAllFilesMenu());        filesTablePopupMenu.add(getSelectNoFilesMenu());        filesTablePopupMenu.add(getSelectFilesPatternMenu());        filesTablePopupMenu.add(new JSeparator());        filesTablePopupMenu.add(getInvertSelectedFilesMenu());        filesTablePopupMenu.add(new JSeparator());        filesTablePopupMenu.add(getKeepSelectedFilesMenu());        filesTablePopupMenu.add(getRemoveSelectedFilesMenu());        if (col == COLUMN_FILE) {            File file = (File) Tables.getTableCell(filesTable, row, col);            getCopyFilePathMenu().putClientProperty(CLIENT_PROPERTY_MENU_FILE, file);            getOpenFileMenu().putClientProperty(CLIENT_PROPERTY_MENU_FILE, file);            getEditFileMenu().putClientProperty(CLIENT_PROPERTY_MENU_FILE, file);            filesTablePopupMenu.add(new JSeparator());            filesTablePopupMenu.add(getCopyFilePathMenu());            filesTablePopupMenu.add(getOpenFileMenu());            filesTablePopupMenu.add(getEditFileMenu());        } else {            getCopyFilePathMenu().putClientProperty(CLIENT_PROPERTY_MENU_FILE, null);            getOpenFileMenu().putClientProperty(CLIENT_PROPERTY_MENU_FILE, null);            getEditFileMenu().putClientProperty(CLIENT_PROPERTY_MENU_FILE, null);        }        return filesTablePopupMenu;    }    private JMenuItem getSelectAllFilesMenu() {        if (selectAllFilesMenu == null) {            selectAllFilesMenu = new JMenuItem("Select all");            selectAllFilesMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    selectAllRows();                }            });        }        return selectAllFilesMenu;    }    private JMenuItem getSelectNoFilesMenu() {        if (selectNoFilesMenu == null) {            selectNoFilesMenu = new JMenuItem("Select none");            selectNoFilesMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    clearSelectedRows();                }            });        }        return selectNoFilesMenu;    }    private JMenuItem getSelectFilesPatternMenu() {        if (selectFilesPatternMenu == null) {            selectFilesPatternMenu = new JMenuItem("Select pattern");            selectFilesPatternMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    String s = JOptionPane.showInputDialog(tabRight(logic.getFilePatternDescription() + "\n"),                            lastFilesPattern);                    if (s != null) {                        lastFilesPattern = s;                        selectFilePatternRows(lastFilesPattern);                    }                }            });        }        return selectFilesPatternMenu;    }    private JMenuItem getInvertSelectedFilesMenu() {        if (invertSelectedFilesMenu == null) {            invertSelectedFilesMenu = new JMenuItem("Invert selected");            invertSelectedFilesMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    invertSelectedRows();                }            });        }        return invertSelectedFilesMenu;    }    private JMenuItem getKeepSelectedFilesMenu() {        if (keepSelectedFilesMenu == null) {            keepSelectedFilesMenu = new JMenuItem("Keep selected");            keepSelectedFilesMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    keepTableFiles(true, false);                }            });        }        return keepSelectedFilesMenu;    }    private JMenuItem getRemoveSelectedFilesMenu() {        if (removeSelectedFilesMenu == null) {            removeSelectedFilesMenu = new JMenuItem("Remove selected");            removeSelectedFilesMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    keepTableFiles(false, true);                }            });        }        return removeSelectedFilesMenu;    }    private JMenuItem getCopyFilePathMenu() {        if (copyFilePathMenu == null) {            copyFilePathMenu = new JMenuItem("Copy file path");            copyFilePathMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    copyFilePath((File) openFileMenu.getClientProperty(CLIENT_PROPERTY_MENU_FILE));                }            });        }        return copyFilePathMenu;    }    private JMenuItem getOpenFileMenu() {        if (openFileMenu == null) {            openFileMenu = new JMenuItem("Open file");            openFileMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    openFile((File) openFileMenu.getClientProperty(CLIENT_PROPERTY_MENU_FILE));                }            });        }        return openFileMenu;    }    private JMenuItem getEditFileMenu() {        if (editFileMenu == null) {            editFileMenu = new JMenuItem("Edit file");            editFileMenu.addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e) {                    editFile((File) editFileMenu.getClientProperty(CLIENT_PROPERTY_MENU_FILE));                }            });        }        return editFileMenu;    }    private void doPerform() {        String errorHeader = "Perform";        if (getCodeEditor().getText().trim().length() == 0) {            showError(errorHeader, "Execution code is required first");            return;        }        List<File> selectedFiles = getTableFiles(true, false);        if (selectedFiles.size() == 0) {            showError(errorHeader, "One or more files must be selected first");            return;        }        List<OperationFile> operationFiles = OperationFile.asList(selectedFiles);        try {            logic.executeCode(getCodeEditor().getText(), operationFiles);        } catch (Exception ex) {            showError(errorHeader, exceptionMessage(ex));        }        showResults(operationFiles);    }    /**     * @param startDirectory to define a specific starting dir, or <code>null</code> for the last selected directory     * @return     */    private File selectDirectory(File startDirectory) {        JFileChooser chooser = new JFileChooser();        if (startDirectory == null)            startDirectory = lastSelectedDir;        if (startDirectory == null)            startDirectory = new java.io.File(".");        chooser.setCurrentDirectory(startDirectory);        chooser.setDialogTitle("Select directory");        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {            lastSelectedDir = chooser.getSelectedFile();            return lastSelectedDir;        }        return null;    }    private String selectCodeFile() {        JFileChooser chooser = new JFileChooser();        chooser.setCurrentDirectory(new File("."));        chooser.setDialogTitle("Select code file");        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);        FileFilter filter = new FileNameExtensionFilter("Code scripts","script");        chooser.setFileFilter(filter);        String code = "";        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {            File codeFile = chooser.getSelectedFile();            try (BufferedReader br = new BufferedReader(new FileReader(codeFile))) {                String sCurrentLine;                while ((sCurrentLine = br.readLine()) != null) {                    code += sCurrentLine + "\n";                }            } catch (IOException e) {                e.printStackTrace();            }        }        return code;    }    private List<File> loadFiles(File directory) {        try {            return logic.loadFiles(directory);        } catch (Exception e) {            showError("Load files", exceptionMessage(e));            return null;        }    }    private String exceptionMessage(Exception e) {        if (e.getMessage() != null)            return e.getMessage() + "\n";        StackTraceElement[] trace = e.getStackTrace();        return e.getClass().getName() + ":\n\n" + trace[0].toString() + "\n";    }    private void setTableRows(List<File> filesToSet) {        removeAllRows();        addTableRows(filesToSet);    }    private void addTableRows(List<File> filesToAdd) {        if (filesToAdd.size() == 0)            return;        List<File> currentFiles = getTableFiles(true, true);        int lastRow = getFilesTable().getModel().getRowCount() - 1;        Tables.addTableRows(getFilesTable(), Math.max(filesToAdd.size(), 1));        for (int row = lastRow + 1; row <= lastRow + filesToAdd.size(); row++) {            File file = filesToAdd.get(row - lastRow - 1);            if (currentFiles.contains(file))                continue;            String id = Integer.toHexString(file.hashCode()).toUpperCase();            getFilesTable().getModel().setValueAt(id, row, COLUMN_ID);            getFilesTable().getModel().setValueAt(String.format("%,d", file.length()), row, COLUMN_SIZE);            getFilesTable().getModel().setValueAt(file, row, COLUMN_FILE);            currentFiles.add(file);        }    }    protected void keepTableFiles(boolean keepSelected, boolean keepUnselected) {        for (int row = getFilesTable().getModel().getRowCount() - 1; row >= 0; row--) {            boolean selected = getFilesTable().isRowSelected(row);            if (selected && !keepSelected || !selected && !keepUnselected)                ((DefaultTableModel) getFilesTable().getModel()).removeRow(row);        }    }    private List<File> getTableFiles(boolean includeSelected, boolean includeUnselected) {        List<File> files = new ArrayList<File>();        for (int row = 0; row < getFilesTable().getModel().getRowCount(); row++) {            boolean selected = getFilesTable().isRowSelected(row);            File file = (File) Tables.getTableCell(getFilesTable(), row, COLUMN_FILE);            if (includeSelected && selected || includeUnselected && !selected)                files.add(file);        }        return files;    }    private void invertSelectedRows() {        for (int i = 0; i < getFilesTable().getRowCount(); i++) {            if (!getFilesTable().isRowSelected(i))                getFilesTable().addRowSelectionInterval(i, i);            else                getFilesTable().removeRowSelectionInterval(i, i);        }    }    private void clearSelectedRows() {        getFilesTable().clearSelection();    }    private void selectAllRows() {        getFilesTable().setRowSelectionInterval(0, getFilesTable().getModel().getRowCount() - 1);    }    private void removeAllRows() {        Tables.setTableRows(getFilesTable(), 0);    }    private void selectFilePatternRows(String pattern) {        List<File> files = getTableFiles(true, true);        List<OperationFile> operationfiles = OperationFile.asList(files);        try {            logic.matchFilePattern(pattern, operationfiles);        } catch (Exception e) {            showError("Match error", exceptionMessage(e));            return;        }        for (int row = 0; row < getFilesTable().getRowCount(); row++) {            OperationFile opFile = operationfiles.get(row);            if (opFile.getMustBeSelected())                getFilesTable().addRowSelectionInterval(row, row);        }    }    private void openFile(File file) {        if (file == null)            return;        try {            Desktop.getDesktop().open(file);        } catch (IOException e) {            e.printStackTrace();            showError("Open file", exceptionMessage(e));        }    }    private void editFile(File file) {        if (file == null)            return;        try {            Runtime.getRuntime().exec(System.getenv("windir") + "\\notepad.exe \"" + file.getAbsolutePath() + "\"");        } catch (IOException e) {            e.printStackTrace();            showError("Edit file", exceptionMessage(e));        }    }    private void copyFilePath(File file) {        if (file == null)            return;        CutCopyPasteMenuItems.sendToClipboard(null, file.getAbsolutePath());    }    private void showInfo(String header, String message) {        JOptionPane.showMessageDialog(this, tabRight(message + "\n"), header, JOptionPane.INFORMATION_MESSAGE);    }    private void showError(String header, String message) {        JOptionPane.showMessageDialog(this, tabRight(message + "\n"), header, JOptionPane.ERROR_MESSAGE);    }    /**     * Adds a few spaces to the end of each line of the message. Used in message dialogs so that there is some space between the     * right end of the text and the right border of the dialog box.     */    private Object tabRight(String message) {        if (message == null)            return null;        String spaces = "          ";        String newMessage = "";        int at = 0;        while (at < message.length()) {            int nl = message.indexOf("\n", at);            if (nl == -1)                nl = message.length();            newMessage += message.substring(at, nl) + spaces + "\n";            at = nl + 1;        }        return newMessage;    }    /**     * Updates the result column of each file in the list with the corresponding result text from the operation list.     */    protected void showResults(List<OperationFile> operationFiles) {        clearResultsColumn();        List<File> allFiles = getTableFiles(true, true);        for (int fileNo = 0; fileNo < operationFiles.size(); fileNo++) {            OperationFile opFile = operationFiles.get(fileNo);            int row = allFiles.indexOf(opFile);            if (row == -1)                continue; // TODO : should never happen, confirm!            String result = opFile.getOperationResult();            if (opFile.isOperationSuccessful() != null && opFile.isOperationSuccessful() == false)                result = TABLE_TEXT_ERROR_MARK + result;            Tables.setTableCell(getFilesTable(), row, COLUMN_RESULT, result);        }    }    private void clearResultsColumn() {        for (int i = 0; i < getFilesTable().getRowCount(); i++)            Tables.setTableCell(getFilesTable(), i, COLUMN_RESULT, "");    }}