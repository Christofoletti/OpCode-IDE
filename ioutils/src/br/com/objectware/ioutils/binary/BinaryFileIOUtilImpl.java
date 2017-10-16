/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.binary;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openide.util.lookup.ServiceProvider;

/**
 * Binary file I/O utils implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @date 24/sep/2015
 * 
 * useful docs:
 *  http://www.javapractices.com/topic/TopicAction.do?Id=245
 *  http://docs.oracle.com/javase/7/docs/api/
 */
@ServiceProvider(service = BinaryFileIOUtil.class, position = 1)
public class BinaryFileIOUtilImpl implements BinaryFileIOUtil {
    
    @Override
    public OutputStream createOutputStream(String path) throws IOException, FileNotFoundException {
        File file = new File(path);
        file.createNewFile();
        return new FileOutputStream(file);
    }   
    
    @Override
    public OutputStream createOutputStream(File file) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        return new BufferedOutputStream(fileOutputStream);
    }   
    
    @Override
    public byte[] readSmallBinaryFile(File file) throws IOException {
        Path path = Paths.get(file.getCanonicalPath());
        return Files.readAllBytes(path);
    }
    
    @Override
    public byte[] readBinaryFile(File file) throws IOException {
        return null; // TODO: implement!
    }
    
    @Override
    public void writeSmallBinaryFile(byte[] data, File file) throws IOException {
        Path path = Paths.get(file.getCanonicalPath());
        Files.write(path, data); //creates, overwrites
    }   
    
    @Override
    public void write(byte[] data, File file) throws FileNotFoundException, IOException {
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
            output.write(data);
        }   
    }
    
    @Override
    public void append(byte[] data, OutputStream stream) throws IOException {
        try (OutputStream output = new BufferedOutputStream(stream)) {
            output.write(data);
            output.flush();
        }   
    }
}
