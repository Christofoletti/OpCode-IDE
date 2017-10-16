/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.domain;

import br.com.objectware.commons.i18n.I18N;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Text file. This class encapsulates all necessary methods needed to manipulate
 * a text file in memory. All the lines from the file are stored in a array list
 * for fast access and manipulation (get next, insert, remove, etc...)
 * 
 * @author Luciano M. Christofoletti
 * @since 05/Apr/2015
 * 
 * Useful Docs:
 *     
 */
public class TextFile {
    
    /***/
    private static final int RESET_INDEX = -1;
    
    /** The text file Id (may be the file name) */
    private final String id;
    
    /** Index of the current line being processed */
    private int index = RESET_INDEX;
    
    /** List of lines. The entire file is store in a list of lines. */
    private final List<String> lines = new ArrayList<>(1024);
    
    /**
     * 
     * @param id the text file Id
     */
    public TextFile(String id) {
        this.id = id;
    }
    
    /** Return the text file Id.
     * @return  */
    public String getId() {
        return this.id;
    }
    
    /** Return the number of lines of the text file
     * @return  */
    public int getLineCount() {
        return this.lines.size();
    }
    
    /** Return the current line index
     * @return  */
    public int getIndex() {
        return this.index;
    }
    
    /** Set the current line index
     * @param index */
    public void setIndex(int index) {
        this.validateIndex(index);
        this.index = index;
    }
    
    /** Reset the line index. The line index will be set to -1 indicating that the no line is selected. */
    public void reset() {
        this.index = RESET_INDEX;
    }
    
    /** Validate the index before accessing a line */
    private void validateIndex(int index) throws IndexOutOfBoundsException {
        
        if(index < 0 || index >= this.lines.size()) {
            
            String errorMessage = I18N.getString("index.out.of.bounds", this.id, index);
            IndexOutOfBoundsException ioobException = new IndexOutOfBoundsException(errorMessage);
            
            String className = TextFile.class.getName();
            Logger.getLogger(className).log(Level.SEVERE, errorMessage, ioobException);
            
            throw ioobException;
        }
    }
    
    /**
     * Appends a line at the end of lines list.
     * @param line the text line to be added at the end of lines list
     */
    public void add(String line) {
        this.lines.add(line);
    }
    
    /** 
     * Insert a new line at the current index. If the current index was not set, an exception is thrown.
     * NOTE: the index is incremented after the insertion is made.
     * 
     * @param line the text line to be inserted at the current index
     */
    public void insert(String line) {
        this.validateIndex(this.index);
        this.lines.add(this.index++, line);
    }
    
    /**
     * 
     * @param index
     * @param line 
     */
    public void insert(int index, String line) {
        this.validateIndex(index);
        this.lines.add(index, line);
    }
    
    /**
     * Insert all lines from another text file at the current index.
     * If the current index was not set, an exception is thrown.
     * @param textFile 
     */
    public void insert(TextFile textFile) {
        
        this.validateIndex(this.index);
        int insertionPoint = this.index;
        
        for(String line : textFile.lines) {
            this.insert(insertionPoint++, line);
        }
    }
    
    /**
     * 
     * @param index
     * @param textFile 
     */
    public void insert(int index, TextFile textFile) {
        this.setIndex(index);
        this.insert(textFile);
    }
    
    public void removeLine() {
        this.removeLine(this.index);
    }
    
    public void removeLine(int index) {
        
        this.validateIndex(index);
        this.lines.remove(index);
        
        // update the current index if necessary
        if(this.index >= this.lines.size()) {
            this.index--;
        }
    }
    
    /** Removes all lines from list and reset the current line index */
    public void clear() {
        this.lines.clear();
        this.reset();
    }
    
    /**
     * Return the line indexed by the current index.
     * 
     * @return String the current text line
     */
    public String getLine() {
        return this.lines.get(this.index);
    }
    
    /**
     * Return the next line from text file. Return null if the end of lines is reached.
     * NOTE: the internal index is incremented before getting the line
     * @return String the next line
     */
    public String getNextLine() {
        if(this.index + 1 < this.lines.size()) {
            this.index++;
            return this.getLine();
        } else {
            return null;
        }
    }
    
    /**
     * Return true when there are available lines after the current index.
     * @return 
     */
    public boolean hasNext() {
        return (this.index + 1 < this.lines.size());
    }
    
    @Override
    public String toString() {
        return this.id;
    }
}
