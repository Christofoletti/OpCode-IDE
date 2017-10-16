/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.asmparser;

import org.netbeans.api.lexer.TokenId;

/**
 * Assembly token information.
 * 
 * @author Luciano M. Christofoletti
 * @since 04/May/2015
 */
public class ASMTokenId implements TokenId {
    
    private final String name;
    private final String primaryCategory;
    private final int id;
    
    /**
     * The Assembly token data.
     * @param name
     * @param primaryCategory
     * @param id 
     */
    ASMTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }
    
    /**
     * Return the token category (keyword, identifier, label, etc)
     * @return 
     */
    @Override
    public String primaryCategory() {
        return this.primaryCategory;
    }
    
    /**
     * Return the token Id.
     * @return 
     */
    @Override
    public int ordinal() {
        return this.id;
    }
    
    /**
     * Return the name of the token, that is, a string that represents the token.
     * @return 
     */
    @Override
    public String name() {
        return this.name;
    }
    
}
