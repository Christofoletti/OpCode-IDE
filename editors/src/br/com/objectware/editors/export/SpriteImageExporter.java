/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.export;

import br.com.objectware.commons.utils.Default;
import br.com.objectware.domain.factories.SpriteRendererFactory;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteRenderer;
import br.com.objectware.editors.sprite.SpritesDataObject;
import java.awt.image.BufferedImage;

/**
 * Sprite exporter. Methods for exporting a set of sprites in image format (png).
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @date 15/sep/2015
 * 
 * useful docs:
 */
public class SpriteImageExporter {
    
    /** The target file of the export action */
    private final java.io.File file;
    
    /** Defines if the sprite colors must be exported */
    private boolean exportSpriteColors = true;
    
    /** Defines the number of sprites per line */
    private int spritesPerLine = Default.ELEMENTS_PER_LINE;
    
    /** The scale factor for sprite images */
    private int scaleFactor = 2;
    
    /** The maximum scale factor for sprite images */
    private static final int MAX_SCALE_FACTOR = 16;
    
    /**
     * The image sprite data exporter. Exports the sprite data as a png image file.
     * @param file the target file (export)
     */
    public SpriteImageExporter(java.io.File file) {
        assert file != null;
        this.file = file;
    }   
    
    /**
     * Defines if the sprites colors must be exported.
     * @param exportColors 
     */
    public void setExportColors(boolean exportColors) {
        this.exportSpriteColors = exportColors;
    }   
    
    /**
     * Number of sprites per line in the export image. (cannot be less than 1)
     * @param spritesPerLine 
     */
    void setSpritesPerLine(int spritesPerLine) {
        if(spritesPerLine > 0) {
            this.spritesPerLine = spritesPerLine;
        }
    }
    
    /**
     * Sets the export image scale.
     * @param factor the scale factor
     */
    void setScale(int factor) {
        if(factor > 0 && factor <= MAX_SCALE_FACTOR) {
            this.scaleFactor = factor;
        }   
    }
    
    /**
     * Export the sprite data to a png image file.
     * @param dataObject
     * @throws IOException if some error occurs
     */
    public void export(SpritesDataObject dataObject) throws java.io.IOException {
        
        java.util.List<Sprite> sprites = new java.util.ArrayList<>(dataObject.getSprites());
        
        // this should never happen...
        if(sprites.isEmpty()) {
            return;
        }
        
        // create a new sprite renderer to avoid palette color conflicts
        SpriteRenderer spriteRenderer = SpriteRendererFactory.newSpriteRenderer(dataObject.getSpriteFormat());
        spriteRenderer.setPalette(dataObject.getPalette());
        
        // set the black palette for rendering if user does not want colored sprites
        if (!this.exportSpriteColors) {
            spriteRenderer.setRenderInBlackAndWhite(true);
        }   
        
        // get the sprite dimension (already considering the scale factor)
        int spriteWidth = sprites.get(0).getPattern().getWidth() * this.scaleFactor;
        int spriteHeight = sprites.get(0).getPattern().getHeight() * this.scaleFactor;
        
        // calculate the image width and height
        int imageWidth = sprites.size() * spriteWidth;
        int imageHeight = spriteHeight;
        if(sprites.size() > this.spritesPerLine) {
            imageWidth = this.spritesPerLine * spriteWidth;
            imageHeight = (int) (Math.ceil(sprites.size()/this.spritesPerLine + 1) * spriteHeight);
        }   
        
        // create the sprite image object (to be rendered)
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics graphics = image.getGraphics();
        
        // render the sprite image scaled
        int x = 0, y = 0;
        for(Sprite sprite : sprites) {
            
            // if the sprite colors must be exported, then set the background color
            if(this.exportSpriteColors) {
                byte backgroundColorCode = sprite.getBackgroundColorCode();
                graphics.setColor(spriteRenderer.getPaletteColor(backgroundColorCode));
                graphics.fillRect(x, y, spriteWidth, spriteHeight);
            }
            
            // render and draw the sprite image
            spriteRenderer.setSprite(sprite);
            java.awt.Image imageScaled = spriteRenderer.renderScaled(this.scaleFactor);
            graphics.drawImage(imageScaled, x, y, null);
            
            // update the sprite rendering position
            x += spriteWidth;
            if(x >= imageWidth) {
                x = 0;
                y += spriteHeight;
            }   
        }   
        
        // save the sprite image
        this.file.createNewFile();
        javax.imageio.ImageIO.write(image, "png", this.file);
    }
    
}
