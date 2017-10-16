/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.MsxSprite;
import br.com.objectware.domain.msx.sprite.MsxSpriteMixer;
import br.com.objectware.domain.msx.sprite.MsxSpritePattern;

/**
 * This class defines the table model for viewing/editing binary files (hexadecimal values view).
 * 
 * @author Luciano M. Christofoletti
 * @since 28/apr/2015
 */
public final class MsxPatternViewTableModel extends javax.swing.table.AbstractTableModel {
    
    /** The MSX sprite data (pattern + attributes) */
    private MsxSprite sprite = new MsxSprite(SpriteFormat.UNDEFINED);
    
    /** The background sprite (reference) */
    private final MsxSpriteMixer spriteMixer = new MsxSpriteMixer();
    
    /**
     * The class constructor.
     * Reset the sprite background and set an empty sprite as the current sprite.
     */
    public MsxPatternViewTableModel() {
        // the sprite and the sprite pattern cannot be null
        this.sprite.setPattern(new MsxSpritePattern());
    }   
    
    /**
     * Get the sprite object.
     * @return 
     */
    public MsxSprite getSprite() {
        return this.sprite;
    }
    
    /**
     * Sets the current msx sprite to be edited.
     * @param sprite 
     */
    public void setSprite(MsxSprite sprite) {
        this.sprite = sprite;
    }   
    
    /**
     * Get the sprite mixer object.
     * @return 
     */
    public final MsxSpriteMixer getSpriteMixer() {
        return this.spriteMixer;
    }
    
    /**
     * Update the background using the current sprite pattern.
     */
    public void addCurrentSpriteToBackground() {
        this.spriteMixer.add(this.sprite);
    }   
    
    @Override
    public String getColumnName(int index) {
        return null;
    }
    
    @Override
    public int getRowCount() {
        return this.sprite.getPattern().getHeight();
    }   
    
    @Override
    public int getColumnCount() {
        return this.sprite.getPattern().getWidth();
    }   
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }   
    
    @Override
    public Byte getValueAt(int row, int column) {
        
        // the pattern must be available
        MsxSpritePattern pattern = this.sprite.getPattern();
        assert pattern != null;
        
        // the pixel byte value has the following format: 00BFCCCC, where:
        // B is the status of the "background" pixel (0 for off or 1 for "on" pixel)
        // F is the status of the "foreground" pixel (0 for off or 1 for "on" pixel)
        // and CCCC is the color code of the pixel (0000 = code 0, ..., 1111 = code 15)
        // for more information, see the SpritePattern class
        byte pixelStatus = pattern.getPixel(column, row);
        
        // if the pixel is "ON" return the foreground color of the current sprite
        if(pattern.isPixelOn(column, row)) {
            
            // in some cases, the attributes field may be null
            if(this.sprite.getAttributes() != null) {
                byte pixelColorCode = this.sprite.getAttributes().getRowColorCode(row);
                pixelStatus = (byte) (pixelColorCode | MsxSpriteMixer.FG_PIXEL_ON);
            }   
            
        } else {
            
            // set the background pixel color (if available)
            if(this.spriteMixer.isPixelOn(column, row)) {
                byte pixelColorCode = this.spriteMixer.getColorCode(column, row);
                pixelStatus = (byte) (pixelColorCode | MsxSpriteMixer.BG_PIXEL_ON);
            }   
        }   
        
        return pixelStatus;
    }
    
    @Override
    public void setValueAt(Object value, int row, int column) {
        
        // the pixel "value" is defined as SpritePattern.PIXEL_ON or SpritePattern.PIXEL_OFF
        // see the PatternViewTableListener and PatternEditorPanel classes
        if(value instanceof Byte && this.validatePoint(row, column)) {
            this.sprite.getPattern().setPixel((byte) value, column, row);
            this.sprite.getPattern().notifyObservers();
        }   
    }   
    
    /**
     * Validate the x,y coords of a pixel before changing it's status
     * @param x
     * @param y
     * @return 
     */
    private boolean validatePoint(int x, int y) {
        return (x >= 0 && x < MsxSpritePattern.SPRITE_WIDTH) &&
               (y >= 0 && y < MsxSpritePattern.SPRITE_HEIGHT);
    }   
    
}