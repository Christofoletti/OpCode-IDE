/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.msx.sprite;

import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.domain.sprite.SpritePattern;
import br.com.objectware.domain.sprite.SpriteRenderer;
import java.awt.image.BufferedImage;

/**
 * The MSX sprite renderer.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 03/sep/2015
 */
public final class MsxSpriteRenderer extends SpriteRenderer {
    
    /**
     * The sprite renderer.
     * @param spriteFormat
     */
    public MsxSpriteRenderer(SpriteFormat spriteFormat) {
        super();
    }   
    
    /**
     * Render a MSX sprite using the current palette and the given pattern and attributes.
     * @param pattern
     * @param attributes
     * @return 
     */
    public synchronized java.awt.Image renderMsxSprite(MsxSpritePattern pattern, MsxSpriteAttributes attributes) {
        
        // ensure that the minimum information to render the sprite is available
        if(!this.isPatternAvailable() || !this.isAttributesAvailable()) {
            return null;
        }   
        
        // make a copy of the original palette colors, since it can change while rendering is being executed
        java.util.List<java.awt.Color> palette = this.getPalette();
        
        // get the current sprite width and height, since it can change in runtime
        int width = pattern.getWidth();
        int height = pattern.getHeight();
        
        // create the sprite image object (to be rendered)
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics graphics = image.getGraphics();
        
        // the MSX sprites has 16x16 pixels
        for(int row = 0; row < height; row++) {
            
            // get the color of the current row (all pixels in the row have the same color)
            byte colorCode = attributes.getRowColorCode(row);
            
            // render the current sprite row if the color code is greater than zero
            if(colorCode > 0 || !this.isSuppressZeroColorCode()) {
                
                // set the pixel (on) color
                if(this.isRenderInBlackAndWhite()) {
                    graphics.setColor(MsxColor.BLACK.getColor());
                } else {
                    graphics.setColor(palette.get(colorCode));
                }
                
                for(int column = 0; column < width; column++) {
                    if(pattern.isPixelOn(column, row)) {
                        graphics.fillRect(column, row, 1, 1);
                    }   
                }   
            }
        }   
        
        return image;
    }   
    
    @Override
    public synchronized java.awt.Image render(SpritePattern pattern, SpriteAttributes attributes) {
        
        // only msx sprites instances can be rendered by this renderer...
        if(attributes instanceof MsxSpriteAttributes) {
            return this.renderMsxSprite((MsxSpritePattern) pattern, (MsxSpriteAttributes) attributes);
        }   
        
        throw new IllegalArgumentException("Error: Cannot render type " + attributes.getClass().getName());
    }   
    
}
