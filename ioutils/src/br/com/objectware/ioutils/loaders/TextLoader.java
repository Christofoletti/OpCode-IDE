/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.loaders;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.ioutils.domain.TextFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Text file loader service.
 * 
 * @author Luciano M. Christofoletti
 * @since 05/Apr/2015
 * 
 * Useful Docs:
 *     https://www.caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
 */
public class TextLoader {
    
    private final ClassLoader classLoader; 
    
    /**
     * Service class that loads a resource using the source object classpath.
     * @param source 
     */
    public TextLoader(Object source) {
        this.classLoader = source.getClass().getClassLoader();
    }
    
    /**
     * Load and process the file specified in path.
     * The current opCodes map is cleared before reading the op-codes file.
     * @param id
     * @param path
     * @return TextFile
     */
    public TextFile loadFromClasspath(String id, String path) {
        // the path must include the name of the file data file (e.g. home/resources/source.asm)
        //InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        //InputStream in = TextLoader.class.getResourceAsStream(path);
        InputStream in = this.classLoader.getResourceAsStream(path);
        return this.load(id, in);
    }
    
    /**
     * 
     * @param id
     * @param path
     * @return 
     */
    public TextFile load(String id, String path) {
        // the path must include the name of the file data file (e.g. home/resources/source.asm)
        
        //InputStream in = new BufferedInputStream(path);
        return null;//this.load(id, null);
    }
    
    /**
     * Load a text file resource from a given input stream.
     * @param id the Id of the TextFile object that will be created
     * @param inputStream the input stream
     * @return the TextFile object containing all lines of the input stream
     */
    public TextFile load(String id, InputStream inputStream) {
        
        // the path must include the name of the file data file (e.g. home/resources/source.asm)
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        // setup the text file resource
        TextFile textFile = new TextFile(id);
        
        try {
            
            // read the first line from opcode data file
            String line = reader.readLine();
            
            // keep going until the end of file is reached
            while(line != null) {
                
                // add the current line to the text file object
                textFile.add(line);
                
                // read the next line
                line = reader.readLine();
            }
            
        } catch (IOException ioe) {
            String className = TextLoader.class.getName();
            String errorMessage = I18N.getString("error.reading.resource", id);
            Logger.getLogger(className).log(Level.SEVERE, errorMessage, ioe);
        } finally {
            try {
                reader.close();
            } catch (IOException ioe) {
                String className = TextLoader.class.getName();
                String errorMessage = I18N.getString("error.reading.resource", id);
                Logger.getLogger(className).log(Level.SEVERE, errorMessage, ioe);
            }
        }
        
        return textFile;
    }   
    
} // end of class
