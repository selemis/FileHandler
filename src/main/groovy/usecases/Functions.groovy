package usecases

class Functions {

    def overLoadFileWithCopy() {
        File.metaClass.copy = { String destName ->
            if (delegate.isFile()) {
                new File(destName).withOutputStream { out ->
                    out.write delegate.readBytes()
                }
            }
        }
    }

    String ext(file) {
        int dot = file.name.lastIndexOf(".");
        return file.name.substring(dot + 1);
    }

    String withoutExt(file) { // gets filename without extension
        file.name.lastIndexOf('.').with { it != -1 ? file.name[0..<it] : file.name }
    }

}


