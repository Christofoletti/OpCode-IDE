/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.enums;

import br.com.objectware.commons.i18n.I18N;

/**
 * The sprite scale factors.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 08/jan/2016
 */
public enum SpriteScale {
    
    FACTOR_1X("1x"),
    FACTOR_2X("2x"),
    FACTOR_4X("4x"),
    FACTOR_8X("8x");
    
    private final String description;
    
    SpriteScale(String scale) {
        this.description = I18N.getString("scale.factor", scale);
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
