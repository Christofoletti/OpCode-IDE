/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.text;

import br.com.objectware.commons.utils.Default;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 * Text file utils.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @date 18/jun/2015
 * 
 * useful docs:
 *  http://www.javapractices.com/topic/TopicAction.do?Id=42
 *  http://docs.oracle.com/javase/7/docs/api/
 */
@ServiceProvider(service = TextFileIOUtil.class, position = 1)
public class TextFileIOUtilImpl implements TextFileIOUtil {
    
    /** The current charset encoding */
    private static Charset encoding = Default.ENCODING;
    
    @Override
    public Charset getEncoding() {
        return TextFileIOUtilImpl.encoding;
    }
    
    @Override
    public void setEncoding(Charset encoding) {
        TextFileIOUtilImpl.encoding = encoding;
    }
    
    @Override
    public List<String> readLargeFileAsLines(String path) throws IOException, FileNotFoundException {
        
        List<String> lines = new ArrayList<>();
        
        // fileReader reads text files in the default encoding
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        
        // read line by line
        String line = bufferedReader.readLine();
        while (line != null) {
            lines.add(line);
            line = bufferedReader.readLine();
        }   
        
        return lines;
    }   
    
    @Override
    public List<String> readAsLines(String path) throws IOException, FileNotFoundException {
        Path pathName = Paths.get(path);
        return Files.readAllLines(pathName, this.getEncoding());
    }   
    
    @Override
    public String readAsString(String path) throws IOException, FileNotFoundException {
        
        StringBuilder sb = new StringBuilder();
        
        // open the file for reading
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        
        // read line by line
        String line = bufferedReader.readLine();
        while (true) {
            
            // append the current line at the end of current string builder
            sb.append(line);
            
            // if there is no lines to read anymore, exit from loop
            if((line = bufferedReader.readLine()) == null) {
                break;
            }   
            
            // append the line feed separator to the next line
            sb.append(Default.LS);
        }   
        
        return sb.toString();
    }   
    
    @Override
    public void write(String path, List<String> lines) throws IOException {
        Path pathName = Paths.get(path);
        Files.write(pathName, lines, this.getEncoding());
    }   
    
    @Override
    public void write(String path, String content) throws IOException {
        String[] lines = content.split(Default.LS);
        this.write(path, Arrays.asList(lines));
    }   
}
