/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import br.com.objectware.commons.i18n.I18N;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Some useful methods for file validation/manipulation
 * 
 * @author Luciano M. Christofoletti
 * @since 08/Jun/2015
 * 
 * Useful docs:
 *     http://www.baeldung.com/java-how-to-rename-or-move-a-file
 *     http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html
 */
public class FileUtil {
    
    // all source files type extensions
    private static final String SOURCE_FILE_EXTENSIONS = 
        (Default.ASM_FILE_EXTENSION +
         Default.LIB_FILE_EXTENSION +
         Default.SOURCE_FILE_EXTENSION).toLowerCase();
    
    /**
     * Verify wheter the file has a valid source file extension.
     * 
     * @param fileObject
     * @return true if the file object is an "asm/lib/src" source file
     */
    public static boolean isSourceFile(FileObject fileObject) {
        
        // the file extension defines the file type
        if(fileObject.isFolder()) {
            return false;
        }
        
        String fileExtension = fileObject.getExt().toLowerCase();
        
        return FileUtil.SOURCE_FILE_EXTENSIONS.contains(fileExtension);
    }
    
    /**
     * Verify wheter the file has a valid source file extension.
     * 
     * @param file
     * @return true if the file object is an "asm/lib/src" source file
     */
    public static boolean isSourceFile(java.io.File file) {
        
        // the file extension defines the file type
        if(file.isDirectory()) {
            return false;
        }   
        
        String fileName = file.getName().toLowerCase();
        
        return fileName.endsWith(Default.ASM_FILE_EXTENSION) ||
               fileName.endsWith(Default.LIB_FILE_EXTENSION) ||
               fileName.endsWith(Default.SOURCE_FILE_EXTENSION);
    }   
    
    /**
     * Normalize the path string accordingly to the current OS
     * 
     * @param path the path to be normalized
     * @return the normalized path
     */
    public static String normalizePath(String path) {
        return org.openide.filesystems.FileUtil.normalizePath(path);
    }
    
    /**
     * Translate the given path to a system independent format (use / as path separator).
     * @param path any given path
     * @return tranlated path
     */
    public static String translatePath(String path) {
        return path.replace(Default.OS_FS, Default.FS).trim();
    }
    
    /**
     * Normalize and translate the given path.
     * @param path
     * @return 
     */
    public static String normalizeAndTranslatePath(String path) {
        return FileUtil.translatePath(org.openide.filesystems.FileUtil.normalizePath(path));
    }
    
    /**
     * Translate the given path to the current operating system format.
     * @param path any given path
     * @return tranlated path
     */
    public static String translateToLocalPath(String path) {
        return path.replace(Default.FS, Default.OS_FS).trim();
    }
    
    /**
     * This is hack used to avoid path name problems when a project is created on Windows and later opened on Linux.
     * @param path the file/folder path
     * @return the translated path
     */
    public static String translateWinFSToUnixFS(String path) {
        return path.replace(Default.WIN_FS, Default.FS).trim();
    }
    
    /**
     * Adjust the file name extension.
     * @param filePath the original file path (may have an extension)
     * @param extension the file extension to be concatenated to the file path
     * @return 
     */
    public static String adjustExtensionFileName(String filePath, String extension) {
        
        if(!filePath.endsWith(extension) && filePath.indexOf(Default.DOT) < 0) {
            return FileUtil.getFileExtension(filePath, extension);
        }   
        
        return filePath;
    }
    
    /**
     * Note: this method uses the "independent system" file separator
     * @param path the relative or absolute path of the file
     * @param fileName the file name with extension
     * @return 
     */
    public static String getPath(String path, String fileName) {
        return (path + Default.FS + fileName);
    }
    
    /**
     * Return the normalized path of path/fileName
     * @param path
     * @param fileName
     * @return 
     */
    public static String getNormalizedPath(String path, String fileName) {
        return FileUtil.normalizePath(path + Default.FS + fileName);
    }
    
    /**
     * Return the normalized path of path/fileName in quotes
     * @param path
     * @param fileName
     * @return 
     */
    public static String getNormalizedPathInQuotes(String path, String fileName) {
        return Default.QUOTATION_STRING + FileUtil.getNormalizedPath(path, fileName) + Default.QUOTATION_STRING;
    }
    
    /**
     * Note: this method uses the "independent system" file separator
     * 
     * @param path the relative or absolute path of the file
     * @param fileName the file name without extension
     * @param extension file extension (without the dot "separator")
     * 
     * @return 
     */
    public static String getPath(String path, String fileName, String extension) {
        return (path + Default.FS + fileName + Default.DOT + extension);
    }   
    
    /**
     * Return a new file name with the given file extension.
     * 
     * @param path the file path
     * @param extension the file extension
     * 
     * @return 
     */
    public static String getFileExtension(String path, String extension) {
        return (path.trim() + Default.DOT + extension);
    }
    
    /**
     * Return the relative path from base path to "reach" the full path.<br>
     * Example:
     * <br>
     * basePath = /home/users/objectware<br>
     * fullPath = /home/users/objectware/secret/code/<br>
     * the returned path will be: secret/code/<br>
     * <br>
     * @param basePath
     * @param fullPath
     * @return 
     */
    public static String getRelativePath(String basePath, String fullPath) {
        String basePathTranslated = FileUtil.translatePath(basePath);
        String pathTranslated = FileUtil.translatePath(fullPath);
        return pathTranslated.replace(basePathTranslated, "");
    }
    
    /**
     * 
     * @param source
     * @param target
     * @return 
     */
    public static boolean remane(String source, String target) {
        
        Path sourcePath = Paths.get(FileUtil.normalizePath(source));
        
        try {
            Files.move(sourcePath, sourcePath.resolveSibling(target));
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     * @param source
     * @param projectRoot
     * @throws IOException 
     */
    public static void unZipFile(InputStream source, FileObject projectRoot) throws IOException {
        
        try {
            ZipInputStream zipInputStream = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    org.openide.filesystems.FileUtil.createFolder(projectRoot, entry.getName());
                } else {
                    FileObject fileObject = org.openide.filesystems.FileUtil.createData(projectRoot, entry.getName());
                    FileUtil.writeFile(zipInputStream, fileObject);
                }
            }
            
        } finally {
            source.close();
        }
    }
    
    /**
     * 
     * @param zipStream
     * @param fileObject
     * @throws IOException
     */
    private static void writeFile(ZipInputStream zipStream, FileObject fileObject) throws IOException {
        try (OutputStream out = fileObject.getOutputStream()) {
            org.openide.filesystems.FileUtil.copy(zipStream, out);
        }
    }
    
    /**
     * <pre>
     * Checks if a string is a valid path.
     * Null safe.
     *  
     * Calling examples:
     *    path("c:/test");      //returns true
     *    path("c:/te:t");      //returns false
     *    path("c:/te?t");      //returns false
     *    path("c/te*t");       //returns false
     *    path("good.txt");     //returns true
     *    path("not|good.txt"); //returns false
     *    path("not:good.txt"); //returns false
     * </pre>
     * @param path
     * @return 
    */
    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        }catch (java.nio.file.InvalidPathException |  NullPointerException ex) {
            return false;
        }
        return true;
    }   
    
    /**
     * Verofy if the selected file name already exists.
     * @param parent
     * @param file the target file
     * @return true if the file name is "valid"
     * 
     * @throws IOException if some error occurs
     */
    public static boolean createNewFile(Component parent, File file) throws IOException {
        
        if(file.exists()) {
            
            Integer userOption = JOptionPane.showConfirmDialog(
                parent, // parent component
                I18N.getString("file.already.exists.overwrite", file.getName()), // dialog message
                I18N.getString("question"), // dialog title
                JOptionPane.YES_NO_OPTION // options
            );
            
            // return true if user wants to overwrite the existing file
            return userOption.equals(JOptionPane.OK_OPTION);
            
        } else {
            // create a new empty file
            return file.createNewFile();
        }   
    }   
    
}
