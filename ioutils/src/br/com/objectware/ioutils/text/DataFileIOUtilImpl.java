/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.text;

import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.TextUtil;
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
 * Assembly/Text Data file IO utils implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 26/oct/2015
 */
@ServiceProvider(service = DataFileIOUtil.class, position = 1)
public class DataFileIOUtilImpl implements DataFileIOUtil {
    
    /** The default data prefix (appended at the beginning of each data line) */
    private String linePrefix = Default.ASM_DATA_BYTES;
    
    /** The data formatter */
    private String dataFormat = "0%02XH"; // possibilities: 0%02XH %d
    
    /** The default number of bytes per line of export */
    private int bytesPerLine = Default.ELEMENTS_PER_LINE;
    
    /** The current charset encoding */
    private static Charset encoding = Default.ENCODING;
    
    @Override
    public Charset getEncoding() {
        return DataFileIOUtilImpl.encoding;
    }
    
    @Override
    public void setEncoding(Charset encoding) {
        DataFileIOUtilImpl.encoding = encoding;
    }
    
    @Override
    public void setLinePrefix(String linePrefix) {
        this.linePrefix = linePrefix;
    }
    
    @Override
    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }
    
    @Override
    public void setBytesPerLine(int bytesPerLine) {
        assert bytesPerLine > 0;
        this.bytesPerLine = bytesPerLine;
    }
    
    /**
     * Export a given array of bytes into a list of lines with the given label as identifier.
     * Every line will start with the current defined prefix.
     * 
     * @param data the data to be exported (byte array)
     * @return list of strings (lines) of data to be exported to a file
     */
    @Override
    public List<String> translate(byte data[]) {
        
        // the generated data lines
        List<String> lines = new ArrayList<>();
        
        int index = 0;
        while (index < data.length) {
            
            // reset the string builder and output the identation and data prefix
            StringBuilder sb = new StringBuilder(Default.TAB_SPACES);
            sb.append(this.linePrefix).append(Default.SPACE_STRING);
            
            // write the bytes using the selected format
            for (int i = 0; i < this.bytesPerLine && index < data.length; i++) {
                
                // append the byte to the line and update the bytes exported counter
                byte value = (byte) (data[index++] & 0xFF);
                if(this.dataFormat.toLowerCase().contains("binary")) {
                    sb.append(TextUtil.byteToAsmBinaryString(value));
                } else {
                    sb.append(TextUtil.byteToHexString(this.dataFormat, value));
                }   
                
                // if there is still data to be appended in the line, append a new coma
                if(i + 1 < this.bytesPerLine && index < data.length) {
                    sb.append(Default.DATA_SEPARATOR_CHAR).append(Default.SPACE_STRING);
                }   
            }   
            
            lines.add(sb.toString());
        }   
        
        return lines;
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
