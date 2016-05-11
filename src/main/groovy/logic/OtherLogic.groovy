package logic

import usecases.FileHandler

import java.util.regex.Matcher
import java.util.regex.Pattern

class OtherLogic implements LogicIF {

    @Override
    String getDescription() {
        "Other Logic"
    }

    @Override
    List<File> loadFiles(File directory) {
        def files = []
        directory.eachFileRecurse {
            files << it
        }

        files
    }

    @Override
    void executeCode(String codeToExecute, List<OperationFile> targetFiles) throws Exception {
        FileHandler fh = new usecases.FileHandler()
        fh.setCode(codeToExecute)
        targetFiles.eachWithIndex {opFile, index ->
            Object result = fh.doSomethingWithFile(opFile, index);
            opFile.setOperationResult(result.toString());
        }
    }

    @Override
    String getFilePatternDescription() {
        return "Enter regular expression to match"
    }

    @Override
    void matchFilePattern(String pattern, List<OperationFile> targetFiles) throws Exception {
        Pattern p = Pattern.compile(pattern);
        for (OperationFile opFile : targetFiles) {
            Matcher m = p.matcher(opFile.getName());
            opFile.setMustBeSelected(m.matches());
        }
    }
}
