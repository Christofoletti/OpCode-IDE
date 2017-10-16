/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.sprite;

import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.TextUtil;
import br.com.objectware.domain.enums.SpriteFormat;

/**
 * Generic sprite properties holder.
 * The Sprite object holds the label, format and references to the sprite's pattern and attributes.
 * The Sprite is also setted as observer of Pattern and Attributes objects, so it can notify it's observers
 * when some change occurs on these objects.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 04/sep/2015
 */
public class Sprite extends java.util.Observable implements java.util.Observer {
    
    /** L serial */
    private static final long serialVersionUID = 427685677365787943L;
    
    /** The sprite format (platform dependent) */
    private final SpriteFormat format;
    
    /** The sprite label */
    private String label = Default.EMPTY_STRING;
    
    /** The sprite foreground color  code (used only for rendering and export purposes) */
    private byte foregroundColorCode = 0;
    
    /** The sprite background color  code (used only for rendering and export purposes) */
    private byte backgroundColorCode = 0;
    
    /** The sprite pattern data */
    private SpritePattern pattern;
    
    /** The sprite attributes */
    private SpriteAttributes attributes;
    
    /** The sprite index in the sprites list (this defines the pattern number) */
    private int index = -1;
    
    /**
     * Undefined sprite type constructor.
     */
    public Sprite() {
        this(SpriteFormat.UNDEFINED);
    }   
    
    /**
     * Sprite properties holder.
     * @param format the sprite format (must be a valid sprite format)
     */
    public Sprite(SpriteFormat format) {
        assert format != null;
        this.format = format;
    }   
    
    /**
     * Get the sprite format (MSX, Sega Master System, Intellivision, etc)
     * @return the sprite format
     */
    public final SpriteFormat getFormat() {
        return this.format;
    }   
    
    /**
     * Return the sprite label.
     * @return the sprite label
     */
    public final String getLabel() {
        return this.label;
    }
    
    /**
     * Set the sprite label (this param is used only by the IDE/assembly program)
     * @param label the sprite label
     */
    public final void setLabel(String label) {
        this.label = label;
        this.setChanged();
    }   
    
    /**
     * Return the sprite foreground color code.
     * @return the sprite foreground color code
     */
    public final byte getForegroundColorCode() {
        return this.foregroundColorCode;
    }
    
    /**
     * Set the sprite foreground color code.
     * @param colorCode the sprite foreground color code
     */
    public final void setForegroundColorCode(byte colorCode) {
        this.foregroundColorCode = colorCode;
    }   
    
    /**
     * Return the sprite background color code.
     * @return 
     */
    public final byte getBackgroundColorCode() {
        return this.backgroundColorCode;
    }
    
    /**
     * Set the sprite background color code.
     * @param colorCode the sprite background color code
     */
    public final void setBackgroundColorCode(byte colorCode) {
        this.backgroundColorCode = colorCode;
    }   
    
    /**
     * Return true when the sprite pattern is available (not null)
     * @return 
     */
    public final boolean isPatternAvailable() {
        return (this.pattern != null);
    }   
    
    /**
     * Get the sprite pattern.
     * @return the sprite pattern data
     */
    public SpritePattern getPattern() {
        return this.pattern;
    }   
    
    /**
     * Set the sprite pattern data.
     * @param pattern the sprite pattern (may be null)
     */
    public void setPattern(SpritePattern pattern) {
        
        // update the pattern observer set
        if(this.pattern != null) {
            this.pattern.deleteObserver(this);
        }
        
        // update the pattern reference and observer
        if((this.pattern = pattern) != null) {
            this.pattern.addObserver(this);
        }
        
        this.setChanged();
    }
    
    /**
     * Return true when the sprite attributes is available (not null)
     * @return 
     */
    public final boolean isAttributesAvailable() {
        return (this.attributes != null);
    }   
    
    /**
     * Get the sprite attributes.
     * @return the sprite attributes data
     */
    public SpriteAttributes getAttributes() {
        return this.attributes;
    }
    
    /**
     * Return the sprite attribute at the given index.
     * @param index the sprite attribute index
     * @return 
     */
    public byte getAttribute(int index) {
        return this.attributes.get(index);
    }   
    
    /**
     * Change attribute of given index.
     * @param index the attribute index
     * @param value the new attribute value at given index
     */
    public void setAttribute(int index, byte value) {
        this.attributes.set(index, value);
    }   
    
    /**
     * Set the sprite attributes.
     * @param attributes the sprite attributes
     */
    public void setAttributes(SpriteAttributes attributes) {
        
        // update the attributes observer set
        if(this.attributes != null) {
            this.attributes.deleteObserver(this);
        }
        
        // update the atributes reference
        if((this.attributes = attributes) != null) {
            this.attributes.addObserver(this);
        }   
        
        this.setChanged();
    }   
    
    /**
     * Return the sprite index.
     * @return 
     */
    public final int getIndex() {
        return this.index;
    }
    
    /**
     * Set the sprite index.
     * @param index 
     */
    public final void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * Return the availability status of sprite data (pattern and attributes)
     * @return true if pattern and attributes data are available
     */
    public boolean isDataAvailable() {
        return (this.isPatternAvailable() && this.isAttributesAvailable());
    }   
    
    /**
     * A Sprite attribute/pattern change must be notified to the listeners.
     */
    @Override
    public void notifyObservers() {
        this.notifyObservers(this);
    }   
    
    /**
     * This method is called when some change occurs in the sprite data (pattern or attributes)
     * NOTE: the object param is send to the observers (and not this reference like the notifyObservers() method)
     * @param observable
     * @param object 
     */
    @Override
    public void update(java.util.Observable observable, Object object) {
        this.setChanged();
        this.notifyObservers(object);
    }
    
    /**
     * Used to show tooltips and debug purpose.
     * @return the textual information about the sprite pattern.
     */
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(TextUtil.intToTwoDigitString(this.getIndex()));
        sb.append(": ");
        sb.append(this.getLabel());
        
        // use this snap of code only for debug purposes
//        sb.append(this.getFormat()).append('\n');
//        sb.append(this.getPattern()).append('\n');
//        sb.append(this.getAttributes()).append('\n');
        
        return sb.toString();
    }   
}
