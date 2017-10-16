/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Arguments parser. The list of arguments must be space separated
 * and may contain quotation marks and/or single quotes.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 19/Jun/2015
 */
public final class LineParser {
    
    public static final char QUOTE = '\"';
    public static final char SINGLE_QUOTE = '\'';
    
    /**
     * 
     * @param line
     * @return
     * @throws IllegalArgumentException 
     */
    public static List<String> parseArguments(String line) throws IllegalArgumentException {
        
        // the list of parsed arguments
        List<String> arguments = new ArrayList<>();
        
        // auxiliary elements
        StringBuilder sb = new StringBuilder();
        boolean insideQuotation = false;
        
        for(char currentChar:line.toCharArray()) {
            
            // verify if the current char is a quotation
            if(LineParser.isQuotation(currentChar)) {
                
                // starting a quotation without a white space before the last argument
                if(!insideQuotation && sb.length() > 0) {
                    throw new IllegalArgumentException("Invalid quotation argument 1!");
                }
                
                sb.append(currentChar);
                
                // verify if the current argument is inside a quotation
                insideQuotation = (sb.length() == 1) || (currentChar != sb.charAt(0));
                
            } else if(!insideQuotation) {
                
                // an space indicates the end of an argument, except when inside a quotation
                if(Character.isWhitespace(currentChar)) {
                    if(sb.length() > 0) {
                        arguments.add(sb.toString().trim());
                        sb = new StringBuilder();
                    }   
                } else {
                    sb.append(currentChar);
                }
                
            } else {
                // add the next char to the current argument being built
                sb.append(currentChar);
            }
        }
        
        // there is a quotation not "finalized"
        if(insideQuotation) {
            throw new IllegalArgumentException("Invalid quotation argument 3!");
        }
        
        // add the last string
        String lastArgument = sb.toString().trim();
        if(lastArgument.length() > 0) {
            arguments.add(lastArgument);
        }
        
        // 
        return arguments;
    }
    
    public static boolean isQuotation(char c) {
        return (c == QUOTE) || (c == SINGLE_QUOTE);
    }
    
}
