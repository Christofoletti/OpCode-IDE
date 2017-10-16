/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;

/**
 * This enum defines all the sprite formats currently available on the OpCode IDE.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 28/Jul/2015
 */
public enum SpriteFormat {
    
    MSX_MODE1("MSX Sprite Mode 1"),
    MSX_MODE2("MSX Sprite Mode 2"),
    SMS("Sega Master System Sprite"),
    INTELLIVISION("Intellivision Sprite"),
    
    UNDEFINED("Undefined");
    
    /** The sprite textual description */
    private final String description;
    
    SpriteFormat(String description) {
        this.description = description;
    }
    
    /**
     * The sprite description.
     * @return String sprite description
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Verify if the current sprite format is a valid format.
     * @return true if the sprite format is defined (i.e. not UNDEFINED)
     */
    public boolean isDefined() {
        return !this.equals(SpriteFormat.UNDEFINED);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Format: ").append(this.getDescription());
        return sb.toString();
    }   
}   
