/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.asmparser;

import br.com.objectware.assembler.asmlexer.JavaCharStream;
import br.com.objectware.assembler.asmlexer.ASMParserTokenManager;
import br.com.objectware.assembler.asmlexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Assembly lexer.
 * 
 * @author Luciano M. Christofoletti
 * @since 04/May/2015
 */
class ASMLexer implements Lexer<ASMTokenId> {
    
    /***/
    private final LexerRestartInfo<ASMTokenId> info;
    
    /***/
    private final ASMParserTokenManager asmParserTokenManager;
    
    /**
     * 
     * @param info 
     */
    ASMLexer(LexerRestartInfo<ASMTokenId> info) {
        
        this.info = info;
        
        JavaCharStream stream = new JavaCharStream(info.input());
        this.asmParserTokenManager = new ASMParserTokenManager(stream);
        ASMLanguageHierarchy language;
    }   
    
    @Override
    public org.netbeans.api.lexer.Token<ASMTokenId> nextToken() {
        
        Token token = this.asmParserTokenManager.getNextToken();
        if (this.info.input().readLength() < 1) {
            return null;
        }
        
        return this.info.tokenFactory().createToken(ASMLanguageHierarchy.getToken(token.kind));
    }
    
    @Override
    public Object state() {
        return null;
    }
    
    @Override
    public void release() {
    }
}
