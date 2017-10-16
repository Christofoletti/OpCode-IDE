/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.parsers;

/**
 * Hexadecimal parser. 
 * 
 * @author Luciano M. Christofoletti
 * @since 30/Mar/2015
 */
public class HexParser {
    
    public static byte parseByte(String byteString) {
        
        if(byteString.isEmpty() || byteString.length() > 2) {
            throw new IllegalArgumentException("Byte value cannot be parsed ("+byteString+")"); // TODO: I18N
        }
        
        return 0;
    }
    
    public static String valiate(String byteString) {
        return null;
    }
}
