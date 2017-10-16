/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.sprite;

/**
 * Pattern operations (shift, flip, rotate, etc).
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 11/sep/2015
 */
public final class PatternUtils {
    
    /**
     * Clears the pattern matrix (fill with zeros)
     * @param pattern
     */
    public static void clear(SpritePattern pattern) {
        for(int y = 0; y < pattern.getHeight(); y++) {
            for(int x = 0; x < pattern.getWidth(); x++) {
                pattern.resetPixel(x, y);
            }   
        }   
    }
    
    /**
     * Shifts all sprite pixel's to the right.
     * @param pattern the pattern to shift the pixels
     */
    public static void shiftRight(SpritePattern pattern) {
        for(int y = 0; y < pattern.getHeight(); y++) {
            byte lastPixel = pattern.getPixel(pattern.getWidth()-1,y);
            for(int x = pattern.getWidth()-1; x > 0; x--) {
                pattern.setPixel(pattern.getPixel(x-1, y), x, y);
            }   
            pattern.setPixel(lastPixel, 0, y);
        }   
    }
    
    /**
     * Shifts all sprite pixel's to the left.
     * @param pattern the pattern to shift the pixels
     */
    public static void shiftLeft(SpritePattern pattern) {
        for(int y = 0; y < pattern.getHeight(); y++) {
            byte firstPixel = pattern.getPixel(0, y);
            for(int x = 1; x < pattern.getWidth(); x++) {
                pattern.setPixel(pattern.getPixel(x, y), x-1, y);
            }   
            pattern.setPixel(firstPixel, pattern.getWidth()-1, y);
        }   
    }
    
    /**
     * Shifts all sprite pixel's up.
     * @param pattern
     */
    public static void shiftUp(SpritePattern pattern) {
        byte[] firstRow = pattern.getRow(0);
        for(int y = 1; y < pattern.getHeight(); y++) {
            pattern.setRow(pattern.getRow(y), y-1);
        }
        pattern.setRow(firstRow, pattern.getHeight()-1);
    }   
    
    /**
     * Shifts all sprite pixel's down.
     * @param pattern
     */
    public static void shiftDown(SpritePattern pattern) {
        byte[] lastRow = pattern.getRow(pattern.getHeight()-1);
        for(int y = pattern.getHeight()-1; y > 0; y--) {
            pattern.setRow(pattern.getRow(y-1), y);
        }
        pattern.setRow(lastRow, 0);
    }   
    
    /**
     * Flip all sprite pixel's upside down.
     * @param pattern
     */
    public static void flipVertical(SpritePattern pattern) {
        for(int y = 0; y < pattern.getHeight()/2; y++) {
            byte[] row = pattern.getRow(y);
            pattern.setRow(pattern.getRow(pattern.getHeight()-y-1), y);
            pattern.setRow(row, pattern.getHeight()-y-1);
        }   
    }
    
    /**
     * Flip all sprite pixel's left to right.
     * @param pattern
     */
    public static void flipHorizontal(SpritePattern pattern) {
        for(int y = 0; y < pattern.getHeight(); y++) {
            for(int x = 0; x < pattern.getWidth()/2; x++) {
                byte value = pattern.getPixel(x, y);
                pattern.setPixel(pattern.getPixel(pattern.getHeight()-x-1, y), x, y);
                pattern.setPixel(value, pattern.getHeight()-x-1, y);
            }   
        }   
    }
    
    /**
     * Rotate the sprite clockwise.
     * Important: this method DOES NOT change the current sprite with, height. That is, this
     * method works only for sprites with identical width and height.
     * @param pattern
     */
    public static void rotateClockwise(SpritePattern pattern) {
        
        // the method works only for "square" patterns
        if(pattern.getWidth() != pattern.getHeight()) {
            return;
        }
        
        // rotate the pixel matrix clockwise
        byte[][] pixels = pattern.getPixels();
        for(int y = 0; y < pattern.getHeight(); y++) {
            for(int x = 0; x < pattern.getWidth(); x++) {
                pattern.setPixel(pixels[y][x], pattern.getHeight()-y-1, x);
            }   
        }   
    }
    
    /**
     * Invert the status of all sprite pixel's
     * @param pattern
     */
    public static void invertPixelsStatus(SpritePattern pattern) {
        for(int y = 0; y < pattern.getHeight(); y++) {
            for(int x = 0; x < pattern.getWidth(); x++) {
                pattern.invertPixel(x, y);
            }   
        }   
    }   
    
    /**
     * Copy the left side (half) to the right side of the sprite pattern.
     * @param pattern
     */
    public static void mirrorLeftToRight(SpritePattern pattern) {
        int patternWidth = pattern.getWidth();
        for(int y = 0; y < pattern.getHeight(); y++) {
            for(int x = 0; x < patternWidth/2; x++) {
                pattern.setPixel(pattern.getPixel(x, y), patternWidth-x-1, y);
            }   
        }   
    }
    
    /**
     * Copy the up side (half) to the down side of the sprite pattern.
     * @param pattern
     */
    public static void mirrorUpToDown(SpritePattern pattern) {
        int patternHeight = pattern.getHeight();
        for(int y = 0; y < patternHeight/2; y++) {
            pattern.setRow(pattern.getRow(y).clone(), patternHeight-y-1);
        }   
    }
    
    /**
     * Paint a blank region of a sprite pattern
     * @param pattern
     * @param x
     * @param y 
     */
    public static void paint(SpritePattern pattern, int x, int y) {
        
        if(pattern.isPixelOn(x, y)) {
            return;
        }
        
        // paint the current pixel
        pattern.setPixel(x, y);
        
        // paint the pixel to the left
        if(x > 0) {
            paint(pattern, x - 1, y);
        }   
        
        // paint the pixel to the right
        if(x + 1 < pattern.getWidth()) {
            paint(pattern, x + 1, y);
        }   
        
        // paint the pixel above the current pixel
        if(y > 0) {
            paint(pattern, x, y - 1);
        }   
        
        // paint the pixel below the current pixel
        if(y + 1 < pattern.getHeight()) {
            paint(pattern, x, y + 1);
        }   
    }
}
