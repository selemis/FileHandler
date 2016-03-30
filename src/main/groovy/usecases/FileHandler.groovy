package usecases

//TODO replace with traits
@Mixin(Functions)
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

    def loadFunctions() {
        overLoadFileWithCopy()
    }
}
