/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.asm;

/**
 * Label Wrapper: stores all information about a valid label (text, position, line number and comment)
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 30/Jul/2015
 */
public class LabelWrapper implements Comparable<LabelWrapper> {
    
    private final String label;
    
    private final int position;
    
    private int line;
    
    private String comment;
    
    /**
     * Set the minimum information needed for the label wrapper.
     * 
     * @param label the text string (the label itself)
     * @param position the position of the first char of the label in the document
     */
    public LabelWrapper(String label, int position) {
        
        assert label != null;
        assert position >= 0;
        
        this.label = label;
        this.position = position;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public int getLine() {
        return this.line;
    }   
    
    public void setLine(int line) {
        this.line = line;
    }   
    
    public String getComment() {
        return this.comment;
    }   
    
    public void setComment(String comment) {
        this.comment = comment;
    }   
    
    @Override
    public String toString() {
        if(this.comment != null) {
            return this.label + " - " + this.comment;
        } else {
            return this.label;
        }
    }
    
    @Override
    public int compareTo(LabelWrapper other) {
        
        if(other == null) {
            return 1;
        }
        
        // return the String compareTo result (all lower case)
        if(this.label.equalsIgnoreCase(other.label)) {
            return 1;
        } else {
            return this.label.toLowerCase().compareTo(other.label.toLowerCase());
        }
    }
}
