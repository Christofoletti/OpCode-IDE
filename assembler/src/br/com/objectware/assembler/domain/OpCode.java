/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package br.com.objectware.assembler.domain;

/**
 * OpCode interface. Define the OpCode and Mnemonic for each instruction.
 * 
 * @author Luciano M. Christofoletti
 * @since 31/Mar/2015
 */
public interface OpCode {
    
    /** Return the opcode hexadecimal string representation (e.g. CD5F00) */
    public String getOpCode();
    
    /** Return the mnemonic string representation (e.g. DEC A) */
    public String getMnemonic();
    
    /** Return the mnemonic prefix string representation (e.g. "DEC" for a "DEC A" instruction) */
    public String getPrefix();
    
    /** Return the template string stored in the opcode data file (e.g. C9 RET) */
    public String getTemplate();
    
    /** Return the binary array representation of a parsed mnemonic */
    public byte[] parse(String string);
    
}