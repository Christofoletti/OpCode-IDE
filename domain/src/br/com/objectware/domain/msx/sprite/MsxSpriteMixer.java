/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.msx.sprite;

import br.com.objectware.domain.enums.MsxColor;

/**
 * This class provides the resulting pattern colors of a set of overlaped MSX sprites.
 * The order of inclusion of each sprite determines the status of each pixel in the mixed set. That is, 
 * each sprite added to the sprite mixer is put "over" the sprites already present in the list.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 26/aug/2015
 */
public final class MsxSpriteMixer {
    
    /** The Foreground "pixel on" mask */
    public static final byte FG_PIXEL_ON = 0B010000;
    
    /** The Background "pixel on" mask */
    public static final byte BG_PIXEL_ON = 0B100000;
    
    /** The Background "pixel off" mask */
    public static final byte PIXEL_OFF_COLOR_CODE = MsxColor.TRANSPARENT.getCode();
    
    /** The "background" sprite list */
    private final java.util.List<MsxSprite> sprites = new java.util.ArrayList<>();
    
    /**
     * Clear the background sprite list.
     */
    public void clear() {
        this.sprites.clear();
    }
    
    /**
     * Add a new sprite to the "higher layer" of the background
     * @param sprite 
     */
    public void add(MsxSprite sprite) {
        this.sprites.add(0, sprite);
    }   
    
    /**
     * Get the color code of pixel at a given position.
     * @param x
     * @param y
     * @return the background color code with "background on" flag setted (if the pixel is setted in the background)
     */
    public byte getColorCode(int x, int y) {
        
        // TODO: add support to OR mode of msx sprites
        for(MsxSprite sprite : this.sprites) {
            if(sprite.getPattern().isPixelOn(x, y)) {
                return sprite.getAttributes().getRowColorCode(y); // get color for row y
            }   
        }   
        
        return PIXEL_OFF_COLOR_CODE;
    }
    
    /**
     * Return the status of the pixel at position x,y
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if there is at least one pixel with status "ON"
     */
    public boolean isPixelOn(int x, int y) {
        
        // return true if and only if there is at least one pixel with status "ON"
        for(MsxSprite sprite : this.sprites) {
            if(sprite.getPattern().isPixelOn(x, y)) {
                return true;
            }   
        }   
        
        return false;
    }   
    
    /**
     * Return the sprite list.
     * NOTE: the returned list is a clone of the internal sprite list in reverse order.
     * @return the sprite list
     */
    public java.util.List<MsxSprite> getSprites() {
        
        // create a new list with the sprites in the order of addition
        java.util.List<MsxSprite> spritesList = new java.util.ArrayList<>(this.sprites);
        java.util.Collections.reverse(spritesList);
        
        return spritesList;
    }
}
