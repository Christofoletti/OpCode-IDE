/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.msx.sprite;

import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.SpriteFormat;

/**
 * The MSX 1 sprite attributes set.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 08/sep/2015
 */
public class Msx1SpriteAttributes extends MsxSpriteAttributes {
    
    /** The number of bytes of the MSX sprite attributes */
    public static final int DATA_LENGTH = 4;
    
    /**
     * The attributes length (in bytes)
     */
    public Msx1SpriteAttributes() {
        super(DATA_LENGTH);
    }   
    
    /**
     * This constructor is used by Msx2SpriteAttributes
     * @param length 
     */
    protected Msx1SpriteAttributes(int length) {
        super(length);
    }   
    
    /**
     * Useful method to set the color code for "Mode 1" sprites.
     * (The row colors for sprites mode 1 are all the same)
     * @param code 
     */
    @Override
    public void setColorCode(byte code) {
        byte attribute = (byte) (super.get(COLOR_CODE_INDEX) & CONTROL_MASK); // don't change the EC flag status
        super.set(COLOR_CODE_INDEX, (byte) (attribute | (code & COLOR_MASK)));
    }   
    
    @Override
    public byte getRowColorCode(int row) {
        return this.getColorCode();
    }   
    
    @Override
    public void setRowColorCode(int row, byte code) {
        this.setColorCode(code);
    }   
    
    @Override
    public void setRowColor(int row, MsxColor color) {
        this.setColorCode(color.getCode());
    }   
    
    @Override
    public SpriteFormat getFormat() {
        return SpriteFormat.MSX_MODE1;
    }   
}
