/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.factories;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.Msx1SpriteAttributes;
import br.com.objectware.domain.msx.sprite.Msx2SpriteAttributes;
import br.com.objectware.domain.sprite.SpriteAttributes;

/**
 * The sprite attribute factory.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 18/aug/2015
 */
public class AttributeFactory {
    
    /**
     * Instantiate a new sprite attribute of a given format.
     * @param format the sprite format
     * 
     * @return a new sprite attribute of a given format
     */
    public static SpriteAttributes newAttribute(SpriteFormat format) throws IllegalArgumentException {
        
        assert format != null;
        
        switch(format) {
            
            case MSX_MODE1:
                return new Msx1SpriteAttributes();
                
            case MSX_MODE2:
                return new Msx2SpriteAttributes();
                
            default:
                throw new IllegalArgumentException(I18N.getString("sprite.format.not.available", format));
        }   
    }
    
}
