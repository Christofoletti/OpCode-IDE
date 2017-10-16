/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.msx.sprite;

import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.domain.sprite.SpritePattern;

/**
 * The MSX sprite data (pattern and attributes).
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 */
public class MsxSprite extends Sprite {
    
    /**
     * The valid MSX sprite types are: MSX_MODE1 and MSX_MODE2
     * @param format 
     */
    public MsxSprite(SpriteFormat format) {
        super(format);
        this.setBackgroundColorCode(MsxColor.WHITE.getCode());
    }   
    
    /**
     * Get the sprite pattern (as MsxSpritePattern type)
     * @return the msx sprite pattern data
     */
    @Override
    public MsxSpritePattern getPattern() {
        return (MsxSpritePattern) super.getPattern();
    }   
    
    /**
     * Set the MSX sprite pattern data.
     * @param pattern 
     */
    @Override
    public void setPattern(SpritePattern pattern) {
        assert pattern instanceof MsxSpritePattern;
        super.setPattern(pattern);
    }
    
    /**
     * Get the MSX sprite attributes (as MsxSpriteAttributes type)
     * @return the msx sprite attributes data
     */
    @Override
    public MsxSpriteAttributes getAttributes() {
        return (MsxSpriteAttributes) super.getAttributes();
    }   
    
    /**
     * Set the MSX sprite attributes data.
     * @param attributes 
     */
    @Override
    public void setAttributes(SpriteAttributes attributes) {
        assert attributes instanceof MsxSpriteAttributes;
        super.setAttributes(attributes);
    }
}
