/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * File name document filter.
 * 
 * @author Luciano M. Christofoletti
 * @since 17/Apr/2015
 */
public class FileNameFilter extends PlainDocument {
    
    private static final String INVALID_CHARS = "\\/:*?\"<>|";
    
    @Override
    public void insertString(int offs, String text, AttributeSet a) throws BadLocationException {
        
        String cleanText = text;
        for (char character : INVALID_CHARS.toCharArray()) {
            cleanText = cleanText.replace(character, '$');
        }
        
        super.insertString(offs, cleanText, a);
    }
}
