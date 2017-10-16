/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.edit;

import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteAttributes;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Change sprite attributes Undo/Redo implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 18/dec/2015
 */
public class SpriteAttributesUndoableEdit extends AbstractUndoableEdit {
    
    /** The sprite added or removed */
    private final Sprite sprite;
    
    /** The sprite attributes data */
    private byte[] spriteAttributesData;
    
    /**
     * Undoable edit for actions that changes the sprite attributes.
     * @param sprite the sprite added or removed
     */
    private SpriteAttributesUndoableEdit(Sprite sprite) {
        this.sprite = sprite;
        this.spriteAttributesData = sprite.getAttributes().getData().clone();
    }   
    
    /**
     * Create an undoable edit for sprite attributes change with no sprite editor selection update.
     * @param sprite
     * @return 
     */
    public static UndoableEdit attributesChangeUndoRedo(Sprite sprite) {
        return new SpriteAttributesUndoableEdit(sprite);
    }   
    
    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public boolean canRedo() {
        return true;
    }
    
    @Override
    public void undo() throws CannotUndoException {
        this.undoRedo();
    }
    
    @Override
    public void redo() throws CannotRedoException {
        this.undoRedo();
    }   
    
    /**
     * Stores the current sprite attributes and overwrite the sprite attributes with the stored one.
     */
    private void undoRedo() {
        
        // store the current sprite attributes data for redo puporses
        SpriteAttributes attributes = this.sprite.getAttributes();
        byte[] attributesData = attributes.getData().clone();
        
        // undo the last change
        attributes.setData(this.spriteAttributesData);
        attributes.notifyObservers();
        
        // store the original sprite attributes data
        this.spriteAttributesData = attributesData;
    }
}
