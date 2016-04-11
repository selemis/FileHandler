package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class ThomasLogic implements LogicIF {

    @Override
    public String getDescription() {
        return "Thomas' implementation";
    }

    @Override
    public List<File> loadFiles(File directory) {
        List<File> files = new ArrayList<File>();
        addDirectoryFiles(files, directory);
        return files;
    }

    private void addDirectoryFiles(List<File> currentFiles, File directory) {
        File[] dirFiles = directory.listFiles();
        for (int i = 0; i < dirFiles.length; i++) {
            if (dirFiles[i].isDirectory())
                addDirectoryFiles(currentFiles, dirFiles[i]);
            else if (dirFiles[i].isFile())
                currentFiles.add(dirFiles[i]);
        }
    }

    @Override
    public void executeCode(String codeToExecute, List<OperationFile> targetFiles) {
        String[] results = { "Run aborted", "Try later", "Known Exception", "Collector garbaged", "Messed up",
                "Searching for backup", "Spectacular failure", "No network detected", "Cannot unzip",
                "Incorrect PGP key", "Quality rejected", "Missing VBRUN300.DLL", "Requires Windows 98",
                "Missing driver", "USB slot busy", "Needs Java 11", "Error reading HDD", "Insufficient voltage",
                "Encoding not supported", "No spool space", "Disk full", "RAM stuck" };
        for (int i = 0; i < targetFiles.size(); i++) {
            int result = (int)(Math.random() * results.length);
            targetFiles.get(i).setOperationResult(i + 1 + ":" + results[result]);
            targetFiles.get(i).setOperationSuccessful(!results[result].toLowerCase().contains("fail"));
        }
    }

    @Override
    public String getFilePatternDescription() {
        return "Enter case-insensitive text\n\n" + //
                "dir\\              to match directories containing 'dir'\n" + //
                "name          to match filenames containing 'name'\n" + //
                "dir\\name    to match a combination of both\n";
    }

    @Override
    public void matchFilePattern(String pattern, List<OperationFile> targetFiles) throws PatternSyntaxException {
        int at = pattern.lastIndexOf("\\");
        String dir = "";
        String name = "";
        if (at == -1)
            name = pattern;
        else if (at == 0)
            name = pattern.substring(1);
        else if (at == pattern.length() - 1)
            dir = pattern.substring(0, at);
        else {
            dir = pattern.substring(0, at);
            name = pattern.substring(at + 1);
        }
        String searchDir = dir.toLowerCase();
        String searchName = name.toLowerCase();
        for (OperationFile file : targetFiles) {
            file.setMustBeSelected(false);
            if (searchDir.length() > 0) {
                String fileDir = file.getParent().toLowerCase();
                if (!fileDir.contains(searchDir))
                    continue;
            }
            if (searchName.length() > 0) {
                String fileName = file.getName().toLowerCase();
                if (!fileName.contains(searchName))
                    continue;
            }
            file.setMustBeSelected(true);
        }

    }

}
