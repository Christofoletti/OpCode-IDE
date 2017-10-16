/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler;

import br.com.objectware.assembler.asmparser.ASMLanguageHierarchy;
import br.com.objectware.assembler.asmparser.ASMTokenId;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;

/**
 * Assembly language mime type registration.
 * 
 * @author Luciano M. Christofoletti
 * @since 04/May/2015
 */
@LanguageRegistration(mimeType = "text/x-asm")
public class ASMLanguage extends DefaultLanguageConfig {
    
    /** The assembly language display name */
    private static final String DISPLAY_NAME = "ASM";
    
    @Override
    public Language<ASMTokenId> getLexerLanguage() {
        return new ASMLanguageHierarchy().language();
    }
    
    @Override
    public String getDisplayName() {
        return ASMLanguage.DISPLAY_NAME;
    }
}
