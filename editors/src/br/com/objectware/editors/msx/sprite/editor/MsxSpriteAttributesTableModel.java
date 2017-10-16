/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.editors.sprite.SpritesDataObject;
import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.MsxSpriteAttributes;
import br.com.objectware.domain.sprite.SpriteAttributes;
import java.util.List;

/**
 * This class defines the table model for viewing/editing sprite attributes.
 * 
 * @author Luciano M. Christofoletti
 * @since 31/oct/2015
 */
public final class MsxSpriteAttributesTableModel
             extends javax.swing.table.AbstractTableModel
             implements java.util.Observer   {
    
    /** The sprites data object being edited */
    private SpritesDataObject spritesDataObject;
    
    /** The MSX sprite attributes table columns names */
    private static final String COLUMN_NAMES[] = {
        I18N.getString("sprite"),
        "Y",
        "X",
        I18N.getString("pattern"),
        "EC",
        I18N.getString("color")
    };
    
    /** The table cell column classes */
    private static final Class<?> COLUMN_CLASSES[] = {
        String.class,
        String.class,
        String.class,
        String.class,
        Boolean.class,
        java.awt.Color.class
    };
    
    /** The sprite number formatter */
    private static final String SPRITE_NUMBER = "%02d";
    
    /** The sprite set palette (here is stored the original palette reference, since updates may occur) */
    private final java.util.List<java.awt.Color> palette = 
            new java.util.ArrayList<>(MsxColor.MSX_PALETTE_SIZE);
    
    /** The list of sprites attributes */
    private final java.util.List<MsxSpriteAttributes> spriteAttributes = 
            new java.util.ArrayList<>(MsxSpriteAttributes.TABLE_SIZE);
    
    /**
     * Set the sprites data object
     * @param dataObject 
     */
    void setDataObject(SpritesDataObject dataObject) {
        
        // the sprite attributes TableModel must be notified when changes occur on attributes
        this.spritesDataObject = dataObject;
        dataObject.addObserver(this);
        
        // update the sprite attributes list
        this.setAttributes(dataObject.getAttributes());
        this.fireTableDataChanged();
    }
    
    /**
     * Set the palette colors
     * @param palette 
     */
    void setPalette(java.util.List<java.awt.Color> palette) {
        this.palette.clear();
        this.palette.addAll(palette);
    }
    
    /**
     * Set the attributes list
     * @param attributesList 
     */
    void setAttributes(List<SpriteAttributes> attributesList) {
        this.spriteAttributes.clear();
        for(SpriteAttributes attributes: attributesList) {
            attributes.addObserver(this);
            this.spriteAttributes.add((MsxSpriteAttributes) attributes);
        }   
    }
    
    /**
     * Get attribute at the given index.
     * @param index the attribute index
     * @return the MSX sprite attribute
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    MsxSpriteAttributes getAttribute(int index) {
        return this.spriteAttributes.get(index);
    }   
    
    @Override
    public Class<?> getColumnClass(int column) {
        return COLUMN_CLASSES[column];
    }
    
    @Override
    public String getColumnName(int index) {
        return COLUMN_NAMES[index];
    }   
    
    @Override
    public int getRowCount() {
        return this.spriteAttributes.size();
    }   
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }   
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return (column > 0);
    }   
    
    @Override
    public Object getValueAt(int row, int column) {
        
        // the pattern must be available
        MsxSpriteAttributes atributes = this.getAttribute(row);
        assert atributes != null;
        
        switch(column) {
            
            // the sprite number column
            case 0: return String.format(SPRITE_NUMBER, row);
            
            // return the Y coordinate of sprite
            case 1: return String.format("%3d", atributes.getY() & 0xFF);
            
            // return the X coordinate of sprite
            case 2: return String.format("%3d", atributes.getX() & 0xFF);
            
            // return the sprite pattern number
            case 3: return String.format("%02d", atributes.getPatternNumber() & 0xFF);
            
            // return the sprite EC flag status
            case 4:
                return atributes.getEC();
            
            // return the sprite color (defined in the sprites data object palette)
            case 5:
                if(atributes.getFormat().equals(SpriteFormat.MSX_MODE1)) {
                    return this.palette.get(atributes.getColorCode());
                } else {
                    return java.awt.SystemColor.scrollbar;
                }
            
            default:
                return atributes;
        }
        
    }
    
    @Override
    public void setValueAt(Object value, int row, int column) {
        
        // the sprite column number is not editable
        if(column <= 0) {
            return;
        }
        
        // the pattern must be available
        MsxSpriteAttributes atributes = this.getAttribute(row);
        
        // try to get the byte value of given object (if applicable)
        Byte byteValue = 0;
        try {
            if(column > 0 && column < 4) {
                String stringValue = ((String) value).trim();
                byteValue = (byte) (Integer.parseInt(stringValue) & 0xFF);
            }
        } catch(NumberFormatException bfe) {
            return;
        }   
        
        switch(column) {
            // set the Y coordinate of sprite
            case 1: atributes.setY(byteValue); break;
            
            // set the X coordinate of sprite
            case 2: atributes.setX(byteValue); break;
            
            // set the sprite pattern number
            case 3: atributes.setPatternNumber(byteValue); break;
            
            // set the sprite EC flag status
            case 4: atributes.setEC((boolean) value); break;
            
            default:
                break;
        }   
        
        // fire update event
        atributes.notifyObservers();
        //this.fireTableCellUpdated(row, column);
    }   
    
    @Override
    public void update(java.util.Observable observable, Object argument) {
        
        // notify the sprites data object if some change occured in sprite attributes
        if(observable instanceof SpriteAttributes) {
            this.spritesDataObject.setChanged(true);
        }   
        
        this.fireTableDataChanged();
    }
    
}