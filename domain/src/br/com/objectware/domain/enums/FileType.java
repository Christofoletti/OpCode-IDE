/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;

/**
 * The most used file types (export, import, asm code, ect)
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 20/sep/2015
 */
public enum FileType {
    
    ASM(I18N.getString("assembly.code.file") + " (*.asm)", "asm"),
    BIN(I18N.getString("binary.data.file") + " (*.bin)", "bin"),
    PNG(I18N.getString("image.file") + " (*.png)", "png");
    
    private final String description;
    private final String extension;
    
    FileType(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getExtension() {
        return this.extension;
    }
}   
