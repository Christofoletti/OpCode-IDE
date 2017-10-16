/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.enums;

import br.com.objectware.commons.i18n.I18N;

/**
 * The sprite data export options.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 08/jan/2016
 */
public enum SpriteDataToExport {
    
    PATTERNS(I18N.getString("export.patterns.only")),
    ATTRIBUTES(I18N.getString("export.attributes.only")),
    ALL(I18N.getString("export.patterns.and.attributes"));
    
    private final String description;
    
    SpriteDataToExport(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
