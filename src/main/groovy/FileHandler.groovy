import javax.swing.table.TableModel

class FileHandler {

    String code

    FileHandler() {
        loadFunctions()
    }

    def doSomethingWithFile(File file, Integer row) {
        def shell = new GroovyShell()
        def closureCode = "{f, r ->${code}}"
        def clj = shell.evaluate(closureCode)
        clj.delegate = this
        clj.call(file, row)
    }

    String ext(file) {
        int dot = file.name.lastIndexOf(".");
        return file.name.substring(dot + 1);
    }

    String withoutExt(file) { // gets filename without extension
        file.name.lastIndexOf('.').with { it != -1 ? file.name[0..<it] : file.name }
    }

    def loadFunctions() {
        File.metaClass.copy = { String destName ->
            if (delegate.isFile()) {
                new File(destName).withOutputStream { out ->
                    out.write delegate.readBytes()
                }
            }

        }
    }
}
