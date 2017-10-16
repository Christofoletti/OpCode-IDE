/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.sprite;

import java.awt.Image;

/**
 * The sprite renderer. This is an abstract class. The concrete implementation depends on the sprite
 * pattern and attributes implementations.
 * 
 * NOTE: the concrete implementation of a sprite renderer must ensure that pattern and attributes
 * data are available!
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 03/sep/2015
 */
public abstract class SpriteRenderer extends java.util.Observable {
    
    /** The color palette to be used to render the sprite */
    private final java.util.List<java.awt.Color> palette = new java.util.ArrayList<>();
    
    /** The sprite pattern */
    private SpritePattern pattern;
    
    /** The sprite attributes */
    private SpriteAttributes attributes;
    
    /** Flag that allows render/suppress color code zero (0) in the rendering process */
    private boolean suppressZeroColorCode = false;
    
    /** Flag that allows render/suppress colored pixels */
    private boolean renderInBlackAndWhite = false;
    
    /**
     * Set the current sprite data to be rendered.
     * Note that the sprite pattern and attributes references are stored here.
     * @param sprite the sprite
     */
    public final void setSprite(Sprite sprite) {
        this.setPattern(sprite.getPattern());
        this.setAttributes(sprite.getAttributes());
    }   
    
    /**
     * Return true if the sprite pattern is available
     * @return 
     */
    public final boolean isPatternAvailable() {
        return (this.pattern != null);
    }   
    
    /**
     * 
     * @param pattern 
     */
    public final void setPattern(SpritePattern pattern) {
        this.pattern = pattern;
    }   
    
    /**
     * Return true if the sprite attributes are available
     * @return 
     */
    public final boolean isAttributesAvailable() {
        return (this.attributes != null);
    }   
    
    /**
     * 
     * @param atributes 
     */
    public final void setAttributes(SpriteAttributes atributes) {
        this.attributes = atributes;
    }   
    
    /**
     * Get the palette color of given color code.
     * @param code the color code
     * @return the palette color
     */
    public final java.awt.Color getPaletteColor(byte code) {
        return this.palette.get(code);
    }   
    
    /**
     * Set a new color for color at the given index.
     * @param index
     * @param color 
     */
    public synchronized final void setPaletteColor(int index, java.awt.Color color) {
        
        assert color != null;
        this.palette.set(index, color);
        
        this.setChanged();
    }   
    
    /**
     * Get the current palette colors.
     * @return the current palette colors
     */
    public final java.util.List<java.awt.Color> getPalette() {
        return java.util.Collections.unmodifiableList(this.palette);
    }   
    
    /**
     * Set the new palette colors.
     * @param palette the list of palette colors
     */
    public synchronized final void setPalette(java.util.List<java.awt.Color> palette) {
        
        assert palette != null;
        
        this.palette.clear();
        this.palette.addAll(palette);
        
        this.setChanged();
    }   
    
    /**
     * The renderer must suppress the color code zero?
     * @return 
     */
    public boolean isSuppressZeroColorCode() {
        return this.suppressZeroColorCode;
    }
    
    /**
     * Set the flag status of suppress color code zero in the rendering process
     * @param suppressZeroColorCode 
     */
    public void setSuppressZeroColorCode(boolean suppressZeroColorCode) {
        this.suppressZeroColorCode = suppressZeroColorCode;
    }
    
    /**
     * The B&W rendering status.
     * @return 
     */
    public boolean isRenderInBlackAndWhite() {
        return this.renderInBlackAndWhite;
    }
    
    /**
     * Enable/Disable redering in black and white or in color mode (using the current pallete)
     * @param renderInBlackAndWhite 
     */
    public void setRenderInBlackAndWhite(boolean renderInBlackAndWhite) {
        this.renderInBlackAndWhite = renderInBlackAndWhite;
    }
    
    /**
     * Render the sprite using the current palette colors and sprite attributes.
     * @param sprite
     * @return 
     */
    public final Image render(Sprite sprite) {
        return this.render(sprite.getPattern(), sprite.getAttributes());
    }   
    
    /**
     * Render the sprite using the current palette.
     * Note: the current attributes and pattern must be available (not null)!
     * @return the sprite image
     */
    public final Image render() {
        return this.render(this.pattern, this.attributes);
    }   
    
    /**
     * Render scaled sprite using the current palette.
     * @param scale the scale of the sprite
     * @return the sprite image
     */
    public final Image renderScaled(int scale) {
        
        Image spriteImage = this.render();
        
        // it is necessary an available palette to render the current sprite
        if(spriteImage != null && scale > 1) {
            int width = spriteImage.getWidth(null);
            int height = spriteImage.getHeight(null);
            spriteImage = spriteImage.getScaledInstance(scale*width, scale*height, Image.SCALE_FAST);
        }   
        
        return spriteImage;
    }   
    
    /**
     * Renders the sprite using the sprite palette colors using the given image as background.
     * @param background the image to be used as background
     * 
     * @return The rendered sprite Image
     */
    public final Image renderOver(Image background) {
        
        Image spriteImage = this.render();
        if(background != null) {
            background.getGraphics().drawImage(spriteImage, 0, 0, null);
        }
        
        return background;
    }   
    
    /**
     * Renders the sprite using the sprite palette colors using the given image as background.
     * IMPORTANT: the resulting image will have the background scaled size, that is, the 
     * scale operation is made upon the resulting background image.
     * 
     * @param scale the scale factor (1x, 2x, 3x, ...)
     * @param background the image to be used as background
     * 
     * @return The rendered sprite Image
     */
    public final Image renderOverAndScale(Image background, int scale) {
        
        assert background != null;
        
        // render the current sprite over the given background (the original bg image is not changed)
        Image spriteImage = this.renderOver(background);
        
        // get the width and height for scale computation
        int imageWidth = background.getWidth(null);
        int imageHeight = background.getHeight(null);
        
        return spriteImage.getScaledInstance(scale*imageWidth, scale*imageHeight, Image.SCALE_FAST);
    }   
    
    /**
     * Renders the sprite using the current palette colors.
     * 
     * @param pattern the sprite pattern to be rendered
     * @param attributes the sprite attributes (color codes)
     * 
     * @return The rendered sprite Image
     */
    public abstract Image render(SpritePattern pattern, SpriteAttributes attributes);
    
}
