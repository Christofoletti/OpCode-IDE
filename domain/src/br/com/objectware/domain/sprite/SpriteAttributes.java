/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.sprite;

import java.io.Serializable;

/**
 * The sprite attributes. The meaning of each byte in the sprite attributes depends on the
 * final sprite implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 03/sep/2015
 */
public class SpriteAttributes extends java.util.Observable implements Serializable {
    
    /** T serial */
    private static final long serialVersionUID = 428472697942L;
    
    /** The sprite attributes data (position, color, flags, whatever...) */
    private final byte data[];
    
    /**
     * Constructor that initializes the attributes array size.
     * @param length the attributes length
     */
    public SpriteAttributes(int length) {
        // the attributes lenght must be positive
        assert length > 0;
        this.data = new byte[length];
    }   
    
    /**
     * Get the attribute byte data at position [index]
     * @param index
     * @return 
     */
    public final byte get(int index) {
        return this.data[index];
    }   
    
    /**
     * Set the attribute byte data at position [index]
     * @param index
     * @param value 
     */
    public final void set(int index, byte value) {
        this.data[index] = value;
        this.setChanged();
    }   
    
    /**
     * Return the attributes data (the meaning of each byte in the array is dependent of the sprite format).
     * @return a data array
     */
    public byte[] getData() {
        return this.data;
    }   
    
    /**
     * Return the sprite attributes data lenght.
     * @return 
     */
    public int getDataLenght() {
        return this.data.length;
    }   
    
    /**
     * Return the attributes data sub-array (from start to end)
     * @param from start index
     * @param to end index
     * @return a clone of the data array (byte array)
     */
    public byte[] getData(int from, int to) {
        return java.util.Arrays.copyOfRange(this.data, from, to);
    }   
    
    /**
     * Set the sprite attributes (depends on sprite type).
     * The data array cannot be greater than the attributes array.
     * @param data the data array (byte array)
     */
    public void setData(byte data[]) {
        System.arraycopy(data, 0, this.data, 0, data.length);
        this.setChanged();
    }   
    
    /**
     * Reset the attributes data.
     */
    public void reset() {
        this.setData(new byte[this.data.length]);
    }   
    
    /**
     * An attribute change must be notified to the listeners.
     */
    @Override
    public void notifyObservers() {
        this.notifyObservers(this);
    }
    
    /**
     * Used only for debug purposes
     * @return the textual information about the sprite attributes.
     */
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder("Attributes: ");
        for(byte value:this.data) {
            sb.append(String.format("%02X ", value & 0xFF));
        }   
        
        return sb.toString();
    }   
}
