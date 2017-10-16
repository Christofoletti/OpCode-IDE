/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.factories;

import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.MsxSpriteRenderer;
import br.com.objectware.domain.sprite.SpriteRenderer;

/**
 * Sprite renderer factory.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 12/jan/2016
 */
public class SpriteRendererFactory {
    
    /**
     * Create a new sprite renderer object for the given sprite format.
     * @param spriteFormat
     * @return the sprite renderer
     */
    public static SpriteRenderer newSpriteRenderer(SpriteFormat spriteFormat) {
        
        switch(spriteFormat) {
            
            case MSX_MODE1:
            case MSX_MODE2:
                return new MsxSpriteRenderer(spriteFormat);
                
            default:
                throw new IllegalArgumentException("Invalid sprite format: " + spriteFormat);
        }   
    }   
}
