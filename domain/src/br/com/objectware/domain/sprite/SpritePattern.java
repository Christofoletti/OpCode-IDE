/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.sprite;

/**
 * The sprite pattern information. This class stores only the pattern data of the sprite.
 * All other attributes must be defined in the SpriteAttributes class.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 21/jul/2015
 */
public abstract class SpritePattern extends java.util.Observable implements java.io.Serializable {
    
    /** F serial */
    private static final long serialVersionUID = 4270766586736542L;
    
    /** The sprite width (in pixels) */
    private final int width;
    
    /** The sprite height (in pixels) */
    private final int height;
    
    private int patternNumber = 0;
    
    /** The pattern data of the sprite [height][width]
     * y1 -> x1 x2 x3 x4
     * y2 -> x1 x2 x3 x4
     * y3 -> x1 x2 x3 x4
     * y4 -> x1 x2 x3 x4
     */
    private final byte pattern[][];
    
    /** The default byte that defines a pixel with status "ON" */
    public static final byte PIXEL_ON = 1;
    
    /** The default byte that defines a pixel with status "OFF" */
    public static final byte PIXEL_OFF = 0;
    
    /**
     * The Sprite constructor. It initializes the minimum data needed to
     * define a sprite pattern.
     * @param width the horizontal size (in pixels) of the sprite's pattern
     * @param height the vertical size (in pixels) of the sprite's pattern
     */
    public SpritePattern(int width, int height) {
        
        // both dimensions must be positive
        assert (width > 0) && (height > 0);
        
        this.width = width;
        this.height = height;
        this.pattern = new byte[height][width];
    }   
    
    /**
     * Get the sprite dimension in pixels.
     * @return the sprite dimension
     */
    public final java.awt.Dimension getDimension() {
        return new java.awt.Dimension(this.width, this.height);
    }   
    
    /**
     * Return the sprite height in pixels.
     * @return the sprite height in pixels.
     */
    public final int getHeight() {
        return this.height;
    }   
    
    /**
     * Return the sprite width in pixels.
     * @return the sprite width in pixels.
     */
    public final int getWidth() {
        return this.width;
    }   
    
    /**
     * Return the pattern number for this sprite.
     * @return 
     */
    public int getPatternNumber() {
        return this.patternNumber;
    }   
    
    /**
     * Set the sprite pattern number.
     * @param patternNumber 
     */
    public void setPatternNumber(int patternNumber) {
        this.patternNumber = patternNumber;
    }
    
    /**
     * Return the pixel "data". The pixel "data" value depends on the concrete sprite implementation.
     * @param x
     * @param y
     * @return the pixel data
     */
    public byte getPixel(int x, int y) {
        return this.pattern[y][x];
    }   
    
    /**
     * Return the pixel "status", ON/OFF.
     * Important: the concept of "ON" for a pixel is defined as "any value greater than zero".
     * @param x
     * @param y
     * @return the pixel "status", ON=true or OFF=false
     */
    public boolean isPixelOn(int x, int y) {
        return ((this.pattern[y][x] & 0xFF) != 0);
    }   
    
    /**
     * Set the pixel value (data).
     * @param value 
     * @param x
     * @param y
     */
    public void setPixel(byte value, int x, int y) {
        this.pattern[y][x] = value;
        this.setChanged();
    }   
    
    /**
     * Sets the pixel status to "on"
     * @param x
     * @param y
     */
    public void setPixel(int x, int y) {
        this.setPixel(PIXEL_ON, x, y);
    }   
    
    /**
     * Sets the pixel status to "off"
     * @param x
     * @param y
     */
    public void resetPixel(int x, int y) {
        this.setPixel(PIXEL_OFF, x, y);
    }   
    
    /**
     * Invert the status of a pixel.
     * @param x
     * @param y
     */
    public void invertPixel(int x, int y) {
        if(this.isPixelOn(x, y)) {
            this.resetPixel(x, y);
        } else {
            this.setPixel(x, y);
        }
    }   
    
    /**
     * Return the byte data of a row.
     * @param row the row index
     * @return byte[] row of pixels
     */
    public byte[] getRow(int row) {
        return this.pattern[row].clone();
    }   
    
    /**
     * Copy the row data over the pattern row.
     * @param rowData
     * @param row 
     */
    public void setRow(byte[] rowData, int row) {
        System.arraycopy(rowData, 0, this.pattern[row], 0, rowData.length);
        this.setChanged();
    }   
    
    /**
     * Return a "clone" of the pattern matrix data (pixel data).
     * @return byte[][] pattern matrix data
     */
    public byte[][] getPixels() {
        
        byte pixels[][] = new byte[this.height][this.width];
        for(int y = 0; y < this.height; y++) {
            System.arraycopy(this.pattern[y], 0, pixels[y], 0, this.width);   
        }   
        
        return pixels;
    }
    
    /**
     * A pattern change must be notified to the listeners.
     */
    @Override
    public void notifyObservers() {
        this.notifyObservers(this);
    }
    
    /**
     * Return the pattern data (must be implemented)
     * @return 
     */
    public abstract byte[] getData();
    
    /**
     * Defines the pattern data (must be implemented).
     * NOTE: this method is supposed to call the setChanged method!
     * @param bytes the byte pattern data
     */
    public abstract void setData(byte bytes[]);
    
    /**
     * Used only for debug purposes
     * @return the textual information about the sprite pattern.
     */
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder("Pattern: ");
        for(byte value:this.getData()) {
            sb.append(String.format("%02X ", value & 0xFF));
        }   
        
        return sb.toString();
    }   
}
