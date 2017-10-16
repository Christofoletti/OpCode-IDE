/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.msx.sprite;

import br.com.objectware.domain.sprite.SpritePattern;

/**
 * The MSX sprite pattern internal representation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 03/sep/2015
 */
public class MsxSpritePattern extends SpritePattern {
    
    /** The byte size (duh!) */
    private static final int BYTE_SIZE = 8;
    
    /** The sprite width (in pixels) */
    public static final int SPRITE_WIDTH = 16;
    
    /** The sprite height (in pixels) */
    public static final int SPRITE_HEIGHT = 16;
    
    /** The sprite pattern size (in bytes) */
    public static final int DATA_SIZE = 32;
    
    /** The bit mask used to get the bit status of a byte */
    private final short BITMASK = (short) 0b10000000;
    
    /** Maximum number of MSX sprite patterns (VDP pattern table limit for 16x16 sprites) */
    public static final int TABLE_SIZE = 256;
    
    /**
     * The MSX sprite pattern.
     */
    public MsxSpritePattern() {
        super(SPRITE_WIDTH, SPRITE_HEIGHT);
    }
    
    @Override
    public byte[] getData() {
        
        // use the current sprite height, since it can change at runtime
        int height = this.getHeight();
        
        // using short type to avoid signal issues
        byte spriteData[] = new byte[DATA_SIZE];
        
        // the MSX 16x16 sprite has 2 "columns" of 16 bytes each
        for (int row = 0; row < height; row++) {
            
            // set/reset pixels accordingly to each bit status
            // process two bytes of each sprite row for each row iteration
            for (int i = 0; i < BYTE_SIZE; i++) {
                
                short bitPosition = (short) (BITMASK >> i);
                
                // process the first byte of the current row
                boolean status = this.isPixelOn(i, row);
                spriteData[row] |= (status ? bitPosition : 0);
                
                // process the second byte of the same row
                status = this.isPixelOn(i + BYTE_SIZE, row);
                spriteData[row + height] |= (status ? bitPosition : 0);
            }
        }
        
        return spriteData;
    }
    
    /**
     * Translate a byte array of data into an "image"
     *
     * @param bytes the byte array
     */
    @Override
    public void setData(byte[] bytes) {
        
        // start filling the "sprite matrix" from left upper corner
        int xPosition = 0;
        int yPosition = 0;
        
        // use the current sprite height, since it can change in runtime
        int height = this.getHeight();
        
        for (int i = 0; i < bytes.length; i++) {
            
            // set/reset pixels accordingly to each bit status
            for (int bit = 0; bit < BYTE_SIZE; bit++, xPosition++) {
                if ((bytes[i] & (BITMASK >> bit)) > 0) {
                    this.setPixel(xPosition, yPosition);
                } else {
                    this.resetPixel(xPosition, yPosition);
                }
            }
            
            // update pixel positions
            xPosition = (i + 1) < height ? 0 : BYTE_SIZE;
            yPosition = (i + 1) % height;
        }
        
        this.setChanged();
    }
}
