/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.msx.sprite;

import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.factories.AttributeFactory;
import br.com.objectware.domain.sprite.SpriteAttributes;

/**
 * The access methods for the MSX sprite attributes.
 * Note that the sprite attributes and sprite row color atttributes (for Mode 2 sprites)
 * were put togheter in this implementation. This is done this way because to facilitate
 * the attribute access all over the application.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 08/sep/2015
 */
public abstract class MsxSpriteAttributes extends SpriteAttributes implements Cloneable {
    
    /** The index of sprite Y coordinate in the sprite attributes vector */
    protected static final int Y_COORDINATE_INDEX = 0;
    
    /** The index of sprite X coordinate in the sprite attributes vector */
    protected static final int X_COORDINATE_INDEX = 1;
    
    /** The index of sprite EC attribute in the sprite attributes vector */
    protected static final int PATTERN_NUMBER_INDEX = 2;
    
    /** The index of sprite color code in the sprite attributes vector (also holds the EC flag status) */
    protected static final int COLOR_CODE_INDEX = 3;
    
    /** Maximum number of screen lines of all MSX screens */
    protected static final int MAX_SCREEN_LINES = 212;
    
    /** The sprite horizontal shift (in pixels) when EC flag is on */
    protected static final int SPRITE_SHIFT_EC = 32;
    
    /** Maximum number of MSX sprite attributes table (99X8 VDP limit)*/
    public static final int TABLE_SIZE = 32;
    
    /** The EC bit */
    public static final byte EC_BIT = (byte) 0b10000000;
    
    /** The color code bit mask */
    public static final byte COLOR_MASK = (byte) 0b00001111;
    
    /** The controls bit mask */
    public static final byte CONTROL_MASK = (byte) 0b11110000;
    
    /** The byte mask */
    public static final short BYTE_MASK = (short) 0b11111111;
    
    /**
     * The class constructor that initializes the attribute data length.
     * @param length the number of bytes of sprite attributes
     */
    protected MsxSpriteAttributes(int length) {
        super(length);
    }   
    
    /**
     * Get the Y coordinate of the sprite.
     * @return the Y coordinate of the sprite.
     */
    public byte getY() {
        return super.get(Y_COORDINATE_INDEX);
    }
    
    /**
     * Get the Y coordinate of the sprite translated to an integer value
     * considering "one pixel down" shift.
     * @return the X coordinate of the sprite.
     */
    public int getTranslatedY() {
        int y = (this.getY() & BYTE_MASK);
        if(y < MAX_SCREEN_LINES) {
            return (y + 1);
        } else {
            return (y - 0xFF);
        }
    }
    
    /**
     * Set the Y coordinate of the sprite.
     * @param value the new Y coordinate of the sprite.
     */
    public void setY(byte value) {
        super.set(Y_COORDINATE_INDEX, value);
    }
    
    /**
     * Get the X coordinate of the sprite.
     * @return the X coordinate of the sprite.
     */
    public byte getX() {
        return super.get(X_COORDINATE_INDEX);
    }
    
    /**
     * Get the X coordinate of the sprite translated to an integer value.
     * @return the X coordinate of the sprite.
     */
    public int getTranslatedX() {
        return (this.getX() & BYTE_MASK);
    }   
    
    /**
     * Get the X coordinate of the sprite translated to an integer value
     * considering the EC flag status.
     * @return the X coordinate of the sprite.
     */
    public int getTranslatedXEC() {
        return this.getTranslatedX() - (this.getEC() ? SPRITE_SHIFT_EC:0);
    }   
    
    /**
     * Set the X coordinate of the sprite.
     * @param value the new X coordinate of the sprite.
     */
    public void setX(byte value) {
        super.set(X_COORDINATE_INDEX, value);
    }
    
    /**
     * Get the sprite pattern number.
     * @return the sprite pattern number
     */
    public byte getPatternNumber() {
        return super.get(PATTERN_NUMBER_INDEX);
    }   
    
    /**
     * Set the sprite pattern number.
     * @param number the sprite pattern number
     */
    public void setPatternNumber(byte number) {
        super.set(PATTERN_NUMBER_INDEX, number);
    }
    
    /**
     * Get the EC flag status
     * @return the EC flag status
     */
    public boolean getEC() {
        // bit 7 defines the sprite EC (Early clock) status
        return (super.get(COLOR_CODE_INDEX) & EC_BIT) != 0;
    }   
    
    /**
     * Set the EC flag status
     * @param status the EC flag status
     */
    public void setEC(boolean status) {
        byte attribute = (byte) (super.get(COLOR_CODE_INDEX) & COLOR_MASK); // don't change the color code
        super.set(COLOR_CODE_INDEX, (byte) (attribute | (status ? EC_BIT:0)));
    }   
    
    /**
     * Useful method to get the color code of sprite attributes table
     * 
     * @return the color code from sprite attributes (not from sprite row color table)
     */
    public byte getColorCode() {
        // only the LSB bits defines the sprite color (the MSB bits defines the EC flag)
        return (byte) (super.get(COLOR_CODE_INDEX) & COLOR_MASK);
    }   
    
    /**
     * Set the color code for MSX sprites. For mode 2 sprites, sets the colors for all rows.
     * (The row colors for sprites mode 1 are all the same)
     * @param code 
     */
    public abstract void setColorCode(byte code);
    
    /**
     * Get the color code of a line (for msx mode 1 sprites the row parameter is useless)
     * @param row
     * @return the color code of row
     */
    public abstract byte getRowColorCode(int row);
    
    /**
     * Set the color code for row in the sprite row colors table.
     * (for msx mode 1 sprites the row parameter is useless)
     * @param row the sprite pattern row
     * @param code the color code for row
     */
    public abstract void setRowColorCode(int row, byte code);
    
    /**
     * Set the color for row in the sprite row colors table.
     * (for msx mode 1 sprites the row parameter is useless)
     * @param row the sprite pattern row
     * @param color the color for row
     */
    public abstract void setRowColor(int row, MsxColor color);
    
    /**
     * Get the sprite format
     * @return the sprite format (see {@see SpriteFormat} enum)
     */
    public abstract SpriteFormat getFormat();
    
    @Override
    public MsxSpriteAttributes clone() throws CloneNotSupportedException {
        
        super.clone();
        
        // create a copy of the original attributes data
        MsxSpriteAttributes attributes = (MsxSpriteAttributes) AttributeFactory.newAttribute(this.getFormat());
        attributes.setData(this.getData());
        
        return attributes;
    }   
}
