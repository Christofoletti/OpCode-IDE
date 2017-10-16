/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.factories;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.MsxSpritePattern;
import br.com.objectware.domain.sprite.SpritePattern;

/**
 * The sprite pattern factory.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 12/jan/2016
 */
public class PatternFactory {
    
    /**
     * Instantiate a new sprite pattern of a given format.
     * @param format the sprite format
     * 
     * @return a new sprite pattern of a given format
     */
    public static SpritePattern newPattern(SpriteFormat format) throws IllegalArgumentException {
        
        assert format != null;
        
        switch(format) {
            
            case MSX_MODE1:
            case MSX_MODE2:
                return new MsxSpritePattern();
                
            default:
                throw new IllegalArgumentException(I18N.getString("sprite.format.not.available", format));
        }   
    }
}
