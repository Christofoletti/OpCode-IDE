/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler;

import java.util.List;

/**
 * Assembler interface. Defines all methods that an assembler must implement
 * to be able to generate binary/executable files.
 * 
 * @author Luciano M. Christofoletti
 * @since 25/Jun/2015
 */
public interface Assembler {
    
    int parseArguments(List<String> arguments);
    
    int assemble();
    
    String getVersion();
}
