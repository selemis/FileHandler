package logic;

import usecases.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StevesLogic implements LogicIF {

    @Override
    public String getDescription() {
        return "Steve's implementation";
    }

    @Override
    public List<File> loadFiles(File directory) {
        List<File> files = new ArrayList<File>();
        File[] dirFiles = directory.listFiles();
        for (int i = 0; i < dirFiles.length; i++)
            if (dirFiles[i].isFile()) {
                files.add(dirFiles[i]);
            }
        return files;
    }

    @Override
    public void executeCode(String codeToExecute, List<OperationFile> targetFiles) {
        for (int i = 0; i < targetFiles.size(); i++) {
            OperationFile opFile = targetFiles.get(i);
            FileHandler fh = new usecases.FileHandler();
            fh.setCode(codeToExecute);
            Object result = fh.doSomethingWithFile(opFile, i);
            opFile.setOperationResult(result.toString());
        }
    }

    @Override
    public String getFilePatternDescription() {
        return "Enter regular expression to match";
    }

    @Override
    public void matchFilePattern(String pattern, List<OperationFile> targetFiles) throws PatternSyntaxException {
        Pattern p = Pattern.compile(pattern);
        for (OperationFile opFile : targetFiles) {
            Matcher m = p.matcher(opFile.getName());
            opFile.setMustBeSelected(m.matches());
        }
    }

}
