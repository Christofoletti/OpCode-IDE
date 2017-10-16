/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.edit;

import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.editors.sprite.SpritesDataObject;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Change sprite label Undo/Redo implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 18/dec/2015
 */
public class SpriteLabelUndoableEdit extends AbstractUndoableEdit {
    
    /** The sprites data object to be notified when undo/redo occurs */
    private final SpritesDataObject dataObject;
    
    /** The sprite added or removed */
    private final Sprite sprite;
    
    /** The sprite label */
    private String spriteLabel;
    
    /**
     * Undoable edit for actions of sprite label change.
     * 
     * @param sprite the sprite added or removed
     */
    private SpriteLabelUndoableEdit(SpritesDataObject dataObject, Sprite sprite) {
        this.dataObject = dataObject;
        this.sprite = sprite;
        this.spriteLabel = sprite.getLabel();
    }   
    
    /**
     * Create an undoable edit for sprite label change with no sprite editor selection update.
     * 
     * @param dataObject
     * @param sprite
     * @return the undoable edit
     */
    public static UndoableEdit labelChangeUndoRedo(SpritesDataObject dataObject, Sprite sprite) {
        return new SpriteLabelUndoableEdit(dataObject, sprite);
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
        
        // store the current sprite label data for redo puporses
        String label = this.sprite.getLabel();
        
        // undo the last change
        this.sprite.setLabel(this.spriteLabel);
        this.sprite.notifyObservers();
        
        // store the original sprite label data
        this.spriteLabel = label;
        
        // notify all observers about the change
        this.dataObject.setChanged(true);
        this.dataObject.notifyObservers();
    }
}
