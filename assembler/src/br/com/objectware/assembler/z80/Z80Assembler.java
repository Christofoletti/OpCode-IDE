/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.z80;

import br.com.objectware.assembler.OpAssembler;
import br.com.objectware.assembler.domain.Resources;
import br.com.objectware.assembler.domain.BinaryData;
import br.com.objectware.assembler.domain.ObjectFile;
import br.com.objectware.assembler.domain.OpCode;
import br.com.objectware.assembler.domain.Resource;
import br.com.objectware.ioutils.domain.TextFile;
import br.com.objectware.ioutils.loaders.TextLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Z80 processor assembler. Defines all methods implementation of the Z80 assembler.
 * 
 * @author Luciano M. Christofoletti
 * @since 30/Mar/2015
 */
public class Z80Assembler implements OpAssembler {
    
    private static final String VERSION = "Z80 Assembler version 0.1 - Objectware Br";
    
    private static final String OPCODES_ID = "z80-opcodes";
    private static final String COMMENT_CHAR = ";";
    private static final String EMPTY_STRING = "";
    
    /**
      Map of opcodes: the string key is defined as the first part of the mnemonic.
      Example:
        'DEC A' instruction: the key is DEC and all "DEC" opcodes 
        will be put in the same list (DEC B, DEC BC, DEC (HL), etc)
    */
    private final Map<String, List<OpCode>> opCodes = new HashMap<>();
    
    /** Map of user defined labels */
    private final Map<String, Character> labels = new HashMap<>();
    
    /** Map of "user defines" (EQU)*/
    private final Map<String, String> defines = new HashMap<>();
    
    public Z80Assembler() {
        System.out.println(Z80Assembler.VERSION);
    }
    
    /** Only for testing purposes...
     * @return  */
    public Map<String, List<OpCode>> getOpCodes() {
        return this.opCodes;
    }
    
    /** 
     * Load and process the Z80 OpCodes templates.
     * The current opCodes map is cleared before reading the op-codes file.
     * @param path
     */
    @Override
    public void loadOpCodes(String path) {
        
        // load the opcodes text file
        System.out.print("Loading Z80 opcodes set...");
        TextFile opCodesTextFile = new TextLoader(this).loadFromClasspath(Z80Assembler.OPCODES_ID, path);
        System.out.println("ok");
        
        // clear opcodes map and reset the text file line index
        this.opCodes.clear();
        opCodesTextFile.reset();
        
        // process all opcodes/mnemonics
        while(opCodesTextFile.hasNext()) {
            
            String opCodeTemplate = opCodesTextFile.getNextLine();
            
            // remove extra blank chars and comments
            opCodeTemplate = this.clean(opCodeTemplate);
            if(!opCodeTemplate.isEmpty()) {
                this.processOpCodeTemplate(opCodeTemplate);
            }
        }
    }
    
    @Override
    public ObjectFile assemble(Resources resources) {
        
        ObjectFile object = new ObjectFile();
        Resource main = resources.getMain();
        BufferedReader reader = main.getReader();
        
        try {
            String line = reader.readLine();
            
            // keep going until the end of file is reached
            while(line != null) {
                
                // remove extra black chars and comments
                line = this.clean(line);
                if(!line.isEmpty()) {
                    //
                }
                
                line = reader.readLine();
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Z80Assembler.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
        
        
        
        return object;
    }
    
    @Override
    public BinaryData link(ObjectFile object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public OpCode parse(String line) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<String> list(Resources resources) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Return a string without leading and trailing white spaces and no comments
     * @param line the text line to be parsed
     * 
     * @return the "cleaned" line
     */
    private String clean(String line) {
        String split[] = line.split(COMMENT_CHAR);
        return (split.length > 0) ? split[0].trim():EMPTY_STRING;
    }
    
    /**
     * 
     * @param opCodeTemplate 
     */
    private void processOpCodeTemplate(String opCodeTemplate) {
        
        // parse the opCode "template"
        Z80OpCode z80OpCode = new Z80OpCode(opCodeTemplate);
        
        // the "prefix" of the Z80 OpCode is used to group the instructions
        String opCodePrefix = z80OpCode.getPrefix();
        
        // get the list that stores the opCodes that share the same prefix
        List<OpCode> opCodesList = this.opCodes.get(opCodePrefix);
        if(opCodesList == null) {
            opCodesList = new ArrayList<>();
            this.opCodes.put(opCodePrefix, opCodesList);
        }
        
        opCodesList.add(z80OpCode);
    }
    
    @Override
    public String toString() {
        return Z80Assembler.VERSION;
    }
}
