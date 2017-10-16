/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.commons.utils.Default;
import br.com.objectware.domain.msx.sprite.MsxSpritePattern;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteRenderer;
import java.awt.Color;

/**
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 */
public class PatternViewerButton extends javax.swing.JButton {
    
    /** The rendering scale factor (ranging 1x to 5x)*/
    private static final int MAX_SCALE_FACTOR = 5;
    
    /** The current sprite viewer scale (4x) */
    private int scaleFactor = 4;
    
    /** The list os sprites to be rendered in the background */
    private final java.util.List<Sprite> sprites = new java.util.ArrayList<>();
    
    /** The sprite renderer to be used to render the sprites */
    private SpriteRenderer spriteRenderer;
    
    /** The current selected sprite being edited */
    private Sprite sprite;
    
    /** Flag used to enable/disable the color background */
    private boolean paintBackground = true;
    
    /** Flag used to enable/disable the background grid view */
    private boolean drawBackgroundGrid = false;
    
    /**
     * The PatternViewerButton default constructor.
     */
    public PatternViewerButton() {
        this.initialize();
    }   
    
    /**
     * Initialize the visual properties of JButton
     */
    private void initialize() {
        
        // set the visual properties of the "pattern view button"
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusable(false);
        
        // setup the icon size and icon gep
        this.setIconTextGap(0);
        this.setPreferredSize(
            new java.awt.Dimension(
                MAX_SCALE_FACTOR * MsxSpritePattern.SPRITE_WIDTH,
                MAX_SCALE_FACTOR * MsxSpritePattern.SPRITE_HEIGHT)
        );
    }   
    
    /**
     * Set the sprite renderer.
     * @param spriteRenderer 
     */
    public void setSpriteRenderer(SpriteRenderer spriteRenderer) {
        assert spriteRenderer != null;
        this.spriteRenderer = spriteRenderer;
    }   
    
    /**
     * Return true if the sprite data and the sprite renderer is available (duh!)
     * @return 
     */
    private boolean isRenderingDataAvailable() {
        return (this.sprite != null) && this.sprite.isAttributesAvailable() &&
               (this.spriteRenderer != null);
    }   
    
    /**
     * Set the "paint background color" option status.
     * @param paintBackground 
     */
    public void setPaintBackground(boolean paintBackground) {
        this.paintBackground = paintBackground;
        this.updateUI();
    }
    
    /**
     * Set the status of "draw the background grid reference" option.
     * @param drawBackgroundGrid 
     */
    public void setDrawBackgroundGrid(boolean drawBackgroundGrid) {
        this.drawBackgroundGrid = drawBackgroundGrid;
        this.updateUI();
    }
    
    /**
     * The list of sprites to be rendered in the background.
     * @param sprites 
     */
    public synchronized void setBackgroundSprites(java.util.List<? extends Sprite> sprites) {
        this.sprites.clear();
        if(sprites != null) {
            this.sprites.addAll(sprites);
        }   
    }   
    
    /**
     * Update the scale view scale.
     */
    public void upScale() {
        
        // updating the scale factor this way avoid scale out of range
        if(this.scaleFactor + 1 > MAX_SCALE_FACTOR) {
            this.scaleFactor = 1;
        } else {
            this.scaleFactor++;
        }
        
        this.updateUI();
    }
    
    /**
     * Set the current sprite being edited. Note that the sprite may be null.
     * @param sprite 
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.setToolTipText();
        this.updateUI();
    }
    
    /**
     * Setup the sprite tool tip.
     */
    private void setToolTipText() {
        if(this.sprite != null) {
            this.setToolTipText(this.sprite.toString());
        } else {
            this.setToolTipText(Default.EMPTY_STRING);
        }
    }
    
    @Override
    public synchronized void paint(java.awt.Graphics graphics) {
        
        super.paint(graphics);
        
        // verify if the sprite data and sprite renderer are available for rendering
        if(!this.isRenderingDataAvailable()) {
            return;
        }   
        
        // create a new graphics2d element (avoid changes in the original graphics)
        java.awt.Graphics2D graphics2D = (java.awt.Graphics2D) graphics.create();
        
        // fill the background with bg color (for color codes > 0)
        if(!this.paintBackground) {
            byte backgroundColorCode = this.sprite.getBackgroundColorCode();
            Color backgroundColor = this.spriteRenderer.getPaletteColor(backgroundColorCode);
            graphics2D.setColor(backgroundColor);
        } else {
            graphics2D.setColor(java.awt.Color.WHITE);
        }   
        graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        // create the background sprite image (empty image)
        java.awt.image.BufferedImage bgImage = new java.awt.image.BufferedImage(
            MsxSpritePattern.SPRITE_WIDTH,
            MsxSpritePattern.SPRITE_HEIGHT,
            java.awt.image.BufferedImage.TYPE_INT_ARGB
        );
        
        // TODO: add grid background support
        
        // draw the background sprite image (the original list is cloned to avoid concurrency problems)
        for(Sprite msxSprite:new java.util.ArrayList<>(this.sprites)) {
            this.spriteRenderer.setSprite(msxSprite);
            this.spriteRenderer.renderOver(bgImage);
        }   
        
        // draw the pattern image
        int scale = this.scaleFactor;
        int offset = MAX_SCALE_FACTOR - this.scaleFactor;
        int ox = (MsxSpritePattern.SPRITE_WIDTH * offset) / 2;
        int oy = (MsxSpritePattern.SPRITE_HEIGHT * offset) / 2;
        
        // draw the curent sprite over the background image
        this.spriteRenderer.setSprite(this.sprite);
        if(!this.drawBackgroundGrid) {
            graphics2D.drawImage(this.spriteRenderer.renderOverAndScale(bgImage, scale), ox, oy, null);
        } else {
            graphics2D.drawImage(this.spriteRenderer.renderScaled(scale), ox, oy, null);
        }   
    }
}
