package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OperationFile extends File {

    /**
     * <ul>
     * <li>If <code>true</code> then the row on the table that contains this file was selected before the start of the
     * operation.</li>
     * <li>If <code>false</code> then the row on the table that contains this file was not selected before the start of the
     * operation.</li>
     * <li>If <code>null</code> then the row on the table that contains this file was not checked for selection before the start
     * of the operation.</li>
     * </ul>
     */
    private Boolean wasSelected = null;

    /**
     * <ul>
     * <li>If <code>true</code> then the row on the table that contains this file must be selected at the end of the operation.
     * </li>
     * <li>If <code>false</code> then the row on the table that contains this file must not be selected at the end of the
     * operation.</li>
     * <li>If <code>null</code> then the row on the table that contains this file must retain its selection mode at the end of
     * the operation.</li>
     * </ul>
     */
    private Boolean mustBeSelected = null;

    /** A text description of the operation's result. This will typically be added to the table row that contains the File. */
    private String operationResult = null;

    /** An indication that the operation was successful. */
    private Boolean operationSuccessful = null;

    public static List<OperationFile> asList(List<File> files) {
        List<OperationFile> operationFiles = new ArrayList<OperationFile>();
        for (File file : files)
            operationFiles.add(new OperationFile(file));
        return operationFiles;
    }

    public OperationFile(String pathname) {
        super(pathname);
    }

    public OperationFile(File file) {
        super(file.getPath());
    }

    /**
     * <ul>
     * <li>If <code>true</code> then the row on the table that contains this file was selected before the start of the
     * operation.</li>
     * <li>If <code>false</code> then the row on the table that contains this file was not selected before the start of the
     * operation.</li>
     * <li>If <code>null</code> then the row on the table that contains this file was not checked for selection before the start
     * of the operation.</li>
     * </ul>
     */
    public Boolean getWasSelected() {
        return wasSelected;
    }

    /**
     * <ul>
     * <li>If <code>true</code> then the row on the table that contains this file was selected before the start of the
     * operation.</li>
     * <li>If <code>false</code> then the row on the table that contains this file was not selected before the start of the
     * operation.</li>
     * <li>If <code>null</code> then the row on the table that contains this file was not checked for selection before the start
     * of the operation.</li>
     * </ul>
     */
    public void setWasSelected(Boolean wasSelected) {
        this.wasSelected = wasSelected;
    }

    /**
     * <ul>
     * <li>If <code>true</code> then the row on the table that contains this file must be selected at the end of the operation.
     * </li>
     * <li>If <code>false</code> then the row on the table that contains this file must not be selected at the end of the
     * operation.</li>
     * <li>If <code>null</code> then the row on the table that contains this file must retain its selection mode at the end of
     * the operation.</li>
     * </ul>
     */
    public Boolean getMustBeSelected() {
        return mustBeSelected;
    }

    /**
     * <ul>
     * <li>If <code>true</code> then the row on the table that contains this file must be selected at the end of the operation.
     * </li>
     * <li>If <code>false</code> then the row on the table that contains this file must not be selected at the end of the
     * operation.</li>
     * <li>If <code>null</code> then the row on the table that contains this file must retain its selection mode at the end of
     * the operation.</li>
     * </ul>
     */
    public void setMustBeSelected(Boolean mustBeSelected) {
        this.mustBeSelected = mustBeSelected;
    }

    /** A text description of the operation's result. This will typically be added to the table row that contains the File. */
    public String getOperationResult() {
        return operationResult;
    }

    /** A text description of the operation's result. This will typically be added to the table row that contains the File. */
    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    /** An indication that the operation was successful. */
    public Boolean isOperationSuccessful() {
        return operationSuccessful;
    }

    /** An indication that the operation was successful. */
    public void setOperationSuccessful(Boolean operationSuccessful) {
        this.operationSuccessful = operationSuccessful;
    }

}
