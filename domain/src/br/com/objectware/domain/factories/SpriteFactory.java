/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.factories;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.Msx2SpriteAttributes;
import br.com.objectware.domain.msx.sprite.MsxSprite;
import br.com.objectware.domain.sprite.Sprite;

/**
 *
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 18/aug/2015
 */
public class SpriteFactory {
    
    /** The new sprite default label */
    private static final String DEFAULT_SPRITE_LABEL = I18N.getString("new.sprite.default.label");
    
    /**
     * Instantiate a new sprite of a given format using the given label.
     * @param format the sprite format
     * 
     * @return a new sprite of a given format
     */
    public static Sprite newSprite(SpriteFormat format) {
        
        Sprite newSprite = null;
        
        switch(format) {
            
            case MSX_MODE1:
            case MSX_MODE2:
                newSprite = SpriteFactory.newMsxSprite(format);
                break;
                
            case UNDEFINED:
                newSprite = new Sprite(format);
                break;
                
            default:
                throw new IllegalArgumentException("Sprite format not supported: " + format);
        }   
        
        return newSprite;
    }
    
    /**
     * Create a new MSX sprite of given format (mode 1 or mode 2)
     * @param format
     * @return
     * @throws IllegalArgumentException 
     */
    private static MsxSprite newMsxSprite(SpriteFormat format) throws IllegalArgumentException {
        
        MsxSprite newSprite = new MsxSprite(format);
        
        // set the pattern and attributes data
        newSprite.setLabel(DEFAULT_SPRITE_LABEL);
        newSprite.setPattern(PatternFactory.newPattern(format));
        newSprite.setAttributes(AttributeFactory.newAttribute(format));
        
        // initialize the color for all rows of the new sprite (for sprites mode 1 and 2)
        for(int i = 0; i < Msx2SpriteAttributes.COLOR_DATA_LENGTH; i++) {
            newSprite.getAttributes().setRowColor(i, MsxColor.BLACK);
        }
        
        return newSprite;
    }
    
}
