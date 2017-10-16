/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package br.com.objectware.editors.msx.sprite.editor;

/* 
 * MsxSpriteColorChooser.java (compiles with releases 1.3 and 1.4) is used by 
 * TableDialogEditDemo.java.
 */

import br.com.objectware.commons.utils.DialogUtil;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.Msx2SpriteAttributes;
import br.com.objectware.domain.msx.sprite.MsxSpriteAttributes;
import br.com.objectware.editors.msx.sprite.dialogs.ColorCodeSelectionDialog;
import br.com.objectware.editors.msx.sprite.dialogs.RowColorCodesSelectionDialog;
import java.util.Optional;
import org.openide.windows.WindowManager;

/**
 * MSX sprite color attribute editor.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 01/jan/2016
 */
public class MsxSpriteColorChooser
       extends javax.swing.AbstractCellEditor
       implements javax.swing.table.TableCellEditor, java.awt.event.ActionListener {
    
    /** The visual (color button) element */
    private final javax.swing.JButton button = new javax.swing.JButton();
    
    /** The sprites data object (holds the palette and sprites data)*/
    private final java.util.List<java.awt.Color> palette = new java.util.ArrayList<>();
    
    /** The sprite atributes to be have the colors changed */
    private MsxSpriteAttributes attributes;
    
    /**
     * The MSX sprite color selector.
     * @param spriteFormat 
     */
    public MsxSpriteColorChooser() {
        this.initialize();
    }   
    
    /**
     * Initialize visual properties of the component.
     */
    private void initialize() {
        this.button.setBorderPainted(false);
        this.button.addActionListener(this);
    }   
    
    /**
     * Handles events from the editor button.
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent event) {
        
        Optional<MsxSpriteAttributes> changedAttributes = this.select(this.attributes);
        if(changedAttributes.isPresent()) {
            this.attributes.setData(changedAttributes.get().getData());
        }   
        
        this.fireEditingStopped();
    }
    
    /**
     * Set the current sprite attributes to be edited
     * @param attributes
     */
    public void setAttributes(MsxSpriteAttributes attributes) {
        this.attributes = attributes;
    }   
    
    /**
     * Get the changed attributes element.
     * @return 
     */
    public Optional<MsxSpriteAttributes> getAttributes() {
        return Optional.of(this.attributes);
    }   
    
    /**
     * Set the palette colors
     * @param palette 
     */
    public final void setPalette(java.util.List<java.awt.Color> palette) {
        this.palette.clear();
        this.palette.addAll(palette);
    }   
    
    // implement the one CellEditor method that AbstractCellEditor doesn't.
    @Override
    public Object getCellEditorValue() {
        return this.attributes;
    }   
    
    // implement the one method defined by TableCellEditor.
    @Override
    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        return this.button;
    }
    
    /**
     * Change the sprite attributes accordingly to the user actions.
     */
    public Optional<MsxSpriteAttributes> select(MsxSpriteAttributes attributes) {
        
        // the palette colors must be available to enable the user selection
        if(this.palette.isEmpty() || attributes == null) {
            return Optional.empty();
        }   
        
        java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
        Optional<MsxSpriteAttributes> changedAttributes = Optional.empty();
        
        // the sprite format must be valid for this operation
        if(attributes.getFormat().equals(SpriteFormat.MSX_MODE1)) {
            
            // the sprite color selection dialog
            ColorCodeSelectionDialog colorCodeChooserDialog = new ColorCodeSelectionDialog(frame, true);
            colorCodeChooserDialog.setPalette(this.palette);
            colorCodeChooserDialog.setSelectedColorCode(attributes.getColorCode());
            DialogUtil.show(colorCodeChooserDialog);
            
            // set the color code for sprite mode 1 (note that the original attributes field is not changed here)
            if(colorCodeChooserDialog.isSelectionAccepted()) {
                changedAttributes = this.copy(attributes);
                if(changedAttributes.isPresent()) {
                    changedAttributes.get().setColorCode(colorCodeChooserDialog.getSelectedColorCode());
                }
            }   
            
        } else if(attributes.getFormat().equals(SpriteFormat.MSX_MODE2)) {
            
            // the sprite color selection dialog
            RowColorCodesSelectionDialog colorCodesChooserDialog = new RowColorCodesSelectionDialog(frame, true);
            colorCodesChooserDialog.setPalette(this.palette);
            colorCodesChooserDialog.setSelectedColorCodes(attributes.getData(
                Msx2SpriteAttributes.COLOR_OFFSET,
                Msx2SpriteAttributes.EXTENDED_DATA_LENGTH)
            );
            DialogUtil.show(colorCodesChooserDialog);
            
            // update colors of each sprite line (note that the original attributes field is not changed here)
            if(colorCodesChooserDialog.isSelectionAccepted()) {
                changedAttributes = this.copy(attributes);
                if(changedAttributes.isPresent()) {
                    byte[] selectedColorCodes = colorCodesChooserDialog.getSelectedColorCodes();
                    for(int k = 0; k < selectedColorCodes.length; k++) {
                        changedAttributes.get().setRowColorCode(k, selectedColorCodes[k]);
                    }   
                }
            }   
        }   
        
        return changedAttributes;
    }
    
    /**
     * Make a copy (clone) of the sprite attributes
     * @param attributes
     * @return 
     */
    private Optional<MsxSpriteAttributes> copy(MsxSpriteAttributes attributes) {
        try {
            return Optional.of(attributes.clone());
        } catch (CloneNotSupportedException exception) {
            return Optional.empty();
        }
    }
    
}

