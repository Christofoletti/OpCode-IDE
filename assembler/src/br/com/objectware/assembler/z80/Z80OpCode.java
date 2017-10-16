/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.objectware.assembler.z80;

import br.com.objectware.assembler.domain.OpCode;
import br.com.objectware.commons.i18n.I18N;

/**
 * The Z80 processor Opcodes and Mnemonics
 * 
 * @author Luciano M. Christofoletti
 * @since 30/Mar/2015
 */
public class Z80OpCode implements OpCode {
    
    /***/
    private String opCode;
    
    /***/
    private String mnemonic;
    
    /** The opcode template */
    private String template;
    
    /***/
    private String prefix;
    
    /**
     * Define an opcode (hex code + mnemonic) of the Z80 processor.
     * @param template the string format of the opcode
     */
    public Z80OpCode(String template) {
        this.parseTemplate(template);
    }
    
    @Override
    public String getOpCode() {
        return this.opCode;
    }
    
    @Override
    public String getMnemonic() {
        return this.mnemonic;
    }
    
    @Override
    public String getPrefix() {
        return this.prefix;
    }
    
    @Override
    public String getTemplate() {
        return this.template;
    }
    
    /**
     * 
     * @param template 
     */
    private void parseTemplate(String template) throws IllegalArgumentException {
        
        // cleanup the template
		this.template = template.replace('\t', ' ').trim().toUpperCase();
		
        // the template must have the format "XX MM", where
        //     XX is the opcode
        //     MM is the corresponding mnemonic
        // Example of template: "C9 RET"
        // No Z80 mnemonic is shorter than two chars (LD, OR, CP, EI, DI, etc)
        String split[] = this.template.split(" ");
		
		// validate the "split" of current line
        if(split.length < 2 || split[0].length() < 2 || split[1].length() < 2) {
            String errorMessage = I18N.getString("wrong.op.code.template.format", template);
            throw new IllegalArgumentException(errorMessage);
        }
        
        // store the opcode data
        this.opCode = split[0];
        
        // get the mnemonic data (tamplate 
        this.mnemonic = this.template.replaceFirst(this.opCode, "").trim();
        
        // set the opcode prefix
        this.prefix = split[1];
    }   
    
    @Override
    public byte[] parse(String string) {
        return new byte[this.opCode.length()]; // TODO: implement this!
    }
    
}
