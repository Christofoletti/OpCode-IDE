/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.binary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Binary file I/O utils interface.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 24/Sep/2015
 */
public interface BinaryFileIOUtil {
    
    /**
     * Create a new output stream. 
     * @param path the path (file name) of the new file.
     * @return the output stream
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     */
    OutputStream createOutputStream(String path) throws IOException, FileNotFoundException;
    
    /**
     * Create a new output stream. 
     * @param file the file
     * @return the output stream
     * @throws java.io.FileNotFoundException
     */
    OutputStream createOutputStream(File file) throws FileNotFoundException;
    
    /**
     * Read data from a small binary file.
     * @param path
     * @return the binary data.
     * @throws IOException 
     */
    byte[] readSmallBinaryFile(File path) throws IOException;
    
    /**
     * Read data from binary file.
     * @param path
     * @return
     * @throws IOException 
     */
    byte[] readBinaryFile(File path) throws IOException;
    
    /**
     * 
     * @param data
     * @param path
     * @throws IOException 
     */
    void writeSmallBinaryFile(byte[] data, File path) throws IOException;
    
    /**
     * Write binary data on the binary file (overwrite all).
     * @param data
     * @param path
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void write(byte[] data, File path) throws FileNotFoundException, IOException;
    
    /**
     * Apeend binary data at the end of the file.
     * @param data
     * @param stream
     * @throws FileNotFoundException
     * @throws IOException 
     */
    void append(byte[] data, OutputStream stream) throws IOException;
    
}
