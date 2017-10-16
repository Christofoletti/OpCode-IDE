/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.text;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Assembly/Text Data file IO utils interface.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 26/oct/2015
 */
public interface DataFileIOUtil {
    
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
     * The data line prefix (db, defb, dw, etc...).
     * @param linePrefix 
     */
    void setLinePrefix(String linePrefix);
    
    /**
     * The data format (0xBB, 0BBH, BB, DECIMAL, etc...)
     * @param dataFormat 
     */
    void setDataFormat(String dataFormat);
    
    /**
     * The number of bytes per line.
     * @param bytesPerLine 
     */
    void setBytesPerLine(int bytesPerLine);
    
    /**
     * Translates a given byte array into a text representation (hexa/decimal)
     * @param data an array of bytes
     * @return a list of Strings of data (code)
     */
    List<String> translate(byte data[]);
    
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
