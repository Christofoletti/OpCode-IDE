/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import java.awt.Color;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Project default names/paths/strings, etc...
 * 
 * @author Luciano M. Christofoletti
 * @since 30/Mar/2015
 */
public class Default {
    
    /** The copyright text - must be present in all opcode project descriptors */
    public static final String COPYRIGHT = "OpCode IDE - Objectware Br - www.objectware.net";
    
    // the default hex table width (for both views, hex and ascii)
    public static final int HEX_TABLE_WIDTH = 16;
    
    // the default number of elements per line (used as default for several purposes)
    public static final int ELEMENTS_PER_LINE = 16;
    
    /** the default empty string */
    public static final String EMPTY_STRING = "";
    
    /** the default space char */
    public static final char SPACE_CHAR = ' ';
    
    /** the default space string */
    public static final String SPACE_STRING = " ";
    
    /** the comma char */
    public static final char COMMA = ',';
    
    /** the default dot separator char (used for file extension names) */
    public static final char DOT = '.';
    
    /** the default colom separator char (used for asm labels) */
    public static final char COLOM_CHAR = ':';
    
    /** the default colom separator string (used for asm labels) */
    public static final String COLOM_STRING = ":";
    
    /** the default quotation mark string */
    public static final String QUOTATION_STRING = "\"";
    
    /** the default semi colom separator char (used for asm comments) */
    public static final char SEMICOLOM = ';';
    
    /** the default single cote string */
    public static final String SINGLE_COTE_STRING = "\'";
    
    /** the default tab char */
    public static final char TAB_CHAR = '\t';
    
    /** the default tab space (using spaces, duh...) */
    public static final String TAB_SPACES = "    "; // by default, tab = four spaces
    
    /** the default assembly comment char */
    public static final char ASM_COMMENT_CHAR = SEMICOLOM;
    
    /** the default assembly comment string */
    public static final String ASM_COMMENT_STRING = ";";
    
    /** the default assembly data definition */
    public static final String ASM_DATA_BYTES = "db";
    
    /** the default data separator char */
    public static final char DATA_SEPARATOR_CHAR = COMMA;
    
    /** the default label delimiter char */
    public static final char LABEL_DELIMITER_CHAR = COLOM_CHAR;
    
    /** the default charset */
    public static final Charset ENCODING = StandardCharsets.UTF_8;
    
    /** the platform indenpendent file separator */
    public static final String FS = "/";//System.getProperty("file.separator");
    
    /** The platform line separator (CR + LF) */
    public static final String LS = System.lineSeparator();
    
    /** Carry return */
    public static final String CR = "\r";
    
    /** Line feed */
    public static final String LF = "\n";
    
    /** The OS file separator */
    public static final String OS_FS = System.getProperty("file.separator");
    
    /** the platform file separator */
    public static final String PS = System.getProperty("path.separator");
    
    /** the windows file separator (:P)*/
    public static final String WIN_FS = "\\";
    
    /** the default asm file extension */
    public static final String ASM_FILE_EXTENSION = "asm";
    
    /** the default binary file extension */
    public static final String BIN_FILE_EXTENSION = "bin";
    
    /** the default image file extension */
    public static final String IMG_FILE_EXTENSION = "png";
    
    /** the default library file extension */
    public static final String LIB_FILE_EXTENSION = "lib";
    
    /** the default object file extension */
    public static final String OBJ_FILE_EXTENSION = "obj";
    
    /** the default screen file extension */
    public static final String SCREEN_FILE_EXTENSION = "scr";
    
    /** the default (generic) source file extension */
    public static final String SOURCE_FILE_EXTENSION = "src";
    
    /** the default sprite data file extension */
    public static final String SPRITE_FILE_EXTENSION = "spr";
    
    /** the default symbols file extension */
    public static final String SYM_FILE_EXTENSION = "sym";
    
    /** the default unix script file extension */
    public static final String UNIX_SCRIPT_FILE_EXTENSION = "sh";
    
    /** the default windows script file extension */
    public static final String WINDOWS_SCRIPT_FILE_EXTENSION = "cmd";
    
    /** the default xml file extension */
    public static final String XML_FILE_EXTENSION = "xml";
    
    
    
    // the default project resources folder
    public static final String RESOURCES_FOLDER = "resources";
    
    // the default project build folder
    public static final String BUILD_FOLDER = "build";
    
    /** the default project sources folder */
    public static final String SOURCES_FOLDER = "src";
    
    
    
    // the default project file name
    public static final String PROJECT_FILE_NAME = "opcode";
    
    // the default project file name
    public static final String PROJECT_FULL_FILE_NAME = "opcode.xml";
    
    // the default main file name (base name)
    public static final String MAIN_FILE_NAME = "main";
    
    // the default main file name (with extension)
    public static final String MAIN_FULL_FILE_NAME = "main.asm";
    
    // the default build file name
    public static final String BUILD_FILE_NAME = "output";
    
    // the default build file name
    public static final String BUILD_FULL_FILE_NAME = "output.bin";
    
    
    
    // the default project build script name
    public static final String BUILD_SCRIPT = "make";
    
    // the default project clean and build script name
    public static final String CLEAN_SCRIPT = "clean";
    
    // the default project run script name
    public static final String RUN_SCRIPT = "run";
    
    
    
    // the string that identifies the operational system
    public static String OS_NAME = System.getProperty("os.name").toLowerCase();
    
    /** The background color of a selected area on the binary editor table */
    public static final Color TABLE_BACKGROUND_SELECTION = new Color(64, 64, 192);
    
    /** The background color of a highlighted area on the binary editor table */
    public static final Color TABLE_BACKGROUND_HIGHLIGHT = Color.GRAY;
    
    /**
     * Return default platform script extension
     * @return the script extension (.cmd or .sh)
     */
    private static String getScriptExtension() {
        return DOT + (Default.isWindows() ? WINDOWS_SCRIPT_FILE_EXTENSION : UNIX_SCRIPT_FILE_EXTENSION);
    }
    
    /**
     * Access method to the default build script file name.
     * @return the default build script file name (platform dependent)
     */
    public static final String getBuildScriptFileName() {
        return Default.BUILD_SCRIPT + Default.getScriptExtension();
    }
    
    /**
     * Access method to the default clean script file name.
     * @return the default clean script file name (platform dependent) 
     */
    public static final String getCleanScriptFileName() {
        return Default.CLEAN_SCRIPT + Default.getScriptExtension();
    }
    
    /**
     * Access method to the default run script file name.
     * @return the default run script file name (platform dependent) 
     */
    public static final String getRunScriptFileName() {
        return Default.RUN_SCRIPT + Default.getScriptExtension();
    }
    
    /**
     * 
     * @return ture if the current OS is windows
     */
    public static boolean isWindows() {
        return (OS_NAME.contains("win"));
    }
    
    /**
     * 
     * @return ture if the current OS is mac
     */
    public static boolean isMac() {
        return (OS_NAME.contains("mac"));
    }
    
    /**
     * 
     * @return ture if the current OS is Unix like
     */
    public static boolean isUnix() {
        return (OS_NAME.contains("nux") || OS_NAME.contains("nix"));
    }
}
