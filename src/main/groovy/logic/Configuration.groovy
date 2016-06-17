package logic

import groovy.transform.Memoized

import java.awt.Color


class Configuration {

    static String getEditorFontName() {
        def config = readConfig()
        config.editor.font.name
    }

    static Integer getEditorFontSize() {
        def config = readConfig()
        config.editor.font.size
    }

    static String getTableFontName() {
        def config = readConfig()
        config.table.font.size
    }

    static Integer getTableFontSize() {
        def config = readConfig()
        config.table.font.size
    }

    static Color getTableFontColor() {
        def config = readConfig()
        Color."${config.table.font.color}"
    }

    static Color getTableFontErrorColor() {
        def config = readConfig()
        Color."${config.table.font.error.color}"
    }

    static String getTableFontErrorMark() {
        def config = readConfig()
        "" + (char) config.table.font.error.mark
    }

    static Integer getButtonWidth() {
        def config = readConfig()
        config.button.width
    }


    @Memoized
    private static ConfigObject readConfig() {
        println "Called"
        new ConfigSlurper().parse(new File('FileHandler.config').toURL())
    }


    public static void main(String[] args) {
        println Configuration.getEditorFontName()
        println Configuration.getEditorFontSize()
        println Configuration.getTableFontName()
        println Configuration.getTableFontSize()
    }



}
