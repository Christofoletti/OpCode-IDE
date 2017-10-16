/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler;

import br.com.objectware.assembler.domain.Resources;
import br.com.objectware.assembler.domain.BinaryData;
import br.com.objectware.assembler.domain.ObjectFile;
import br.com.objectware.assembler.domain.OpCode;
import java.util.List;

/**
 * OpAssembler interface. Defines all methods that an assembler must implement
 * to be able to generate binary/executable files.
 * 
 * @author Luciano M. Christofoletti
 * @since 30/Mar/2015
 */
public interface OpAssembler {
    
    /**
     * Load the opcode data file. The data file must be well structured accordingly
     * to the target op codes (see documentation for more details)
     * 
     * @param opCodesFileName the full path of the op-code data file (based on the target platform)
     */
    void loadOpCodes(String opCodesFileName);
    
    /**
     * Assembles all resources into one object file.
     * 
     * @param resources set of all resources needed to assemble the object file
     * @return the object file that can be linked to an "executable" file
     */
    ObjectFile assemble(Resources resources);
    
    /**
     * 
     * @param object
     * @return 
     */
    BinaryData link(ObjectFile object);
    
	/**
     * Parse a single line of assembly code.
	 * 
     * @param line the line to be parsed
     * @return The opcode for the parsed line
     */
    OpCode parse(String line);
	
    /**
     * 
     * @param resources
     * @return 
     */
    List<String> list(Resources resources);
    
}
