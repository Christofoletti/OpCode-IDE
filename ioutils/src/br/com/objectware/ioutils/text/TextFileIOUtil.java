/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Text file IO utils interface.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 27/Jul/2015
 */
public interface TextFileIOUtil {
    
    /**
     * Get the current charset encoding.
     * @return the charset encoding
     */
    Charset getEncoding();
    
    /**
     * Sets the file encoding.
     * @param encoding the charset encoding.
     */
    void setEncoding(Charset encoding);
    
    /**
     * Read all lines from file using current text encoding. Use this method when the text file
     * is large (larger than 100 Kb, for example).
     * 
     * @param path
     * @return the lines of text from file
     * @throws IOException
     * @throws FileNotFoundException 
     */
    List<String> readLargeFileAsLines(String path) throws IOException, FileNotFoundException;
    
    /**
     * Read all lines from file using current text encoding.
     * Use this method for small text files.
     * 
     * @param path
     * @return the lines of text from file
     * @throws IOException
     * @throws FileNotFoundException 
     */
    List<String> readAsLines(String path) throws IOException, FileNotFoundException;
    
    /**
     * Read all lines from file using current text encoding into a single String.
     * Use this method for small text files.
     * @param path
     * @return
     * @throws IOException
     * @throws FileNotFoundException 
     */
    String readAsString(String path) throws IOException, FileNotFoundException;
    
    /**
     * Writes a list of lines as a text file using the current encoding.
     * @param path
     * @param lines
     * @throws IOException 
     */
    void write(String path, List<String> lines) throws IOException;
    
    /**
     * Writes the content of a string into a text file using the current encoding.
     * @param path
     * @param content
     * @throws IOException 
     */
    void write(String path, String content) throws IOException;
}
