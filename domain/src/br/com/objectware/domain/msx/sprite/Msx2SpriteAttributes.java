/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.msx.sprite;

import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.SpriteFormat;

/**
 * The MSX 2 sprite attributes set.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 08/sep/2015
 */
public class Msx2SpriteAttributes extends Msx1SpriteAttributes {
    
    /** The offset of the first sprite line color */
    public static final int COLOR_OFFSET = Msx1SpriteAttributes.DATA_LENGTH;
    
    /** The size of color data (for each row) */
    public static final int COLOR_DATA_LENGTH = 16;
    
    /** The number of bytes of the MSX sprite mode 2 attributes (attributes + row color data) */
    public static final int EXTENDED_DATA_LENGTH = COLOR_OFFSET + COLOR_DATA_LENGTH;
    
    /**
     * Constructor that initializes the attributes array.
     */
    public Msx2SpriteAttributes() {
        super(EXTENDED_DATA_LENGTH);
    }   
    
    @Override
    public void setColorCode(byte code) {
        for(int index = 0; index < COLOR_DATA_LENGTH; index++) {
            this.setRowColorCode(index, code);
        }   
    }   
    
    /**
     * Return the array of row color codes of sprite mode 2 color table (16 bytes, one for each sprite row).
     * @return 
     */
    public byte[] getRowColorCodes() {
        return super.getData(COLOR_OFFSET, EXTENDED_DATA_LENGTH);
    }   
    
    @Override
    public byte getRowColorCode(int row) {
        return (byte) (super.get(COLOR_OFFSET + row) & COLOR_MASK);
    }   
    
    @Override
    public void setRowColorCode(int row, byte code) {
        
        int colorIndex = COLOR_OFFSET + row;
        
        byte attribute = (byte) (super.get(colorIndex) & CONTROL_MASK); // don't change the status flags
        super.set(colorIndex, (byte) (attribute | (code & COLOR_MASK)));
        
        this.setChanged();
    }   
    
    @Override
    public void setRowColor(int row, MsxColor color) {
        this.setRowColorCode(row, color.getCode());
    }
    
    @Override
    public SpriteFormat getFormat() {
        return SpriteFormat.MSX_MODE2;
    }
}
