/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

/**
 *
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 30/Jul/2015
 */
public final class TextUtil {
    
    /**
     * Test if a given char is a white space (space or tab).
     * @param character the char to be tested
     * 
     * @return true if the char is a whitespace
     */
    public static boolean isWhitespace(char character) {
        return (character == Default.SPACE_CHAR ||
                character == Default.TAB_CHAR);
    }   
    
    /**
     * 
     * @param text
     * @return 
     */
    public static boolean beginWithWhitespace(String text) {
        
        // the string text cannot be null nor empty
        if(text != null && !text.isEmpty()) {
            return TextUtil.isWhitespace(text.charAt(0));
        }   
        
        return false;
    }
    
    /**
     * 
     * @param formatString
     * @param value
     * @return 
     */
    public static String intToHexString(String formatString, int value) {
        return String.format(formatString, value);
    }   
    
    /**
     * 
     * @param formatString
     * @param value
     * @return 
     */
    public static String byteToHexString(String formatString, byte value) {
        return String.format(formatString, value & 0xFF);
    }   
    
    /**
     * Formats a byte value to a string hexadecimal representation.
     * @param value the byte value
     * @return String
     */
    public static String byteToHexString(byte value) {
        return String.format("%02X", value & 0xFF);
    }
    
    /**
     * 
     * @param value
     * @return 
     */
    public static String intToHex4String(int value) {
        return String.format("%04X", value & 0xFFFF);
    }   
    
    /**
     * 
     * @param value
     * @return 
     */
    public static String intToHex6String(int value) {
        return String.format("%06X", value & 0xFFFFFF);
    }   
    
    /**
     * 
     * @param value
     * @return 
     */
    public static String longToHexString(long value) {
        if(value > 65535) {
            return String.format("0x%08X", value);
        } else {
            return String.format("0x%04X", value);
        }
    }   
    
    /**
     * Format an integer number as a two (at least) digit format
     * (useful for represent integer values raging from 0 to 255)
     * @param value
     * @return 
     */
    public static String intToTwoDigitString(int value) {
        return String.format("%02d", value & 0xFF);
    }   
//    
//    /**
//     * Format an integer number as a three (at least) digit format
//     * (useful for represent integer values raging from 0 to 255)
//     * @param value
//     * @return 
//     */
//    public static String intToThreeDigitString(int value) {
//        return String.format("%03d", value & 0xFF);
//    }   
    
    /**
     * Formats a byte value to a string binary representation.
     * @param value the byte value
     * @return String
     */
    public static String byteToBinaryString(byte value) {
        int intValue = (value & 0xFF);
        return String.format("%8s", Integer.toBinaryString(intValue)).replace(' ', '0');
    }
    
    /**
     * Formats a byte value to a assembly format string binary representation.
     * @param value the byte value
     * @return String
     */
    public static String byteToAsmBinaryString(byte value) {
        int intValue = (value & 0xFF);
        return String.format("%8sb", Integer.toBinaryString(intValue)).replace(' ', '0');
    }
    
    /**
     * Parse the entire line into a byte array. It is assumed that the data is sepatated using spaces
     * and the data is in decimal format.
     * @param line
     * @return array of bytes parsed from line
     * @throws IllegalArgumentException if some parse error occurs
     */
    public static byte[] parseStringOfIntegersToByteArray(String line) throws IllegalArgumentException {
        
        // split the current line using spaces as separators
        String data[] = line.trim().split(Default.SPACE_STRING);
        byte byteData[] = new byte[data.length];
        
        for(int i = 0; i < data.length; i++) {
            byteData[i] = Byte.parseByte(data[i]);
        }   
        
        return byteData;
    }   
    
    /**
     * Parse the entire line into a byte array. It is assumed that the data is sepatated using spaces
     * and the data is in hexadecimal format.
     * @param line
     * @return array of bytes parsed from line
     * @throws IllegalArgumentException if some parse error occurs
     */
    public static byte[] parseStringOfHexToByteArray(String line) throws IllegalArgumentException {
        
        // split the current line using spaces as separators
        String data[] = line.trim().split(Default.SPACE_STRING);
        byte byteData[] = new byte[data.length];
        
        for(int i = 0; i < data.length; i++) {
            byteData[i] = TextUtil.parseStringToByte(data[i]);
        }   
        
        return byteData;
    }   
    
    /**
     * Parse a byte string into a byte value.
     * @param value the string to be parsed
     * @return the byte value represented by the original string
     */
    public static byte parseStringToByte(String value) {
        return (byte) Integer.parseInt(value, 16);
    }
}
