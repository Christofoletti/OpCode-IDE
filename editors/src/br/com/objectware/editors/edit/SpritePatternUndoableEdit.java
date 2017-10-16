/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.edit;

import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpritePattern;
import br.com.objectware.editors.sprite.SpritesDataObject;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Change sprite pattern Undo/Redo implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 11/dec/2015
 */
public class SpritePatternUndoableEdit extends AbstractUndoableEdit {
    
    /** The sprites data object to be notified when undo/redo occurs */
    private final SpritesDataObject dataObject;
    
    /** The sprite added or removed */
    private final Sprite sprite;
    
    /** The sprite pattern data */
    private byte[] spritePatternData;
    
    /**
     * Undoable edit for actions of add and remove sprite.
     * 
     * @param dataObject
     * @param sprite the sprite added or removed
     */
    private SpritePatternUndoableEdit(SpritesDataObject dataObject, Sprite sprite) {
        this.dataObject = dataObject;
        this.sprite = sprite;
        this.spritePatternData = sprite.getPattern().getData();
    }   
    
    /**
     * Add a pattern  change undo/redo edit.
     * 
     * @param dataObject
     * @param sprite
     * 
     * @return 
     */
    public static UndoableEdit patternChangeUndoRedo(SpritesDataObject dataObject, Sprite sprite) {
        return new SpritePatternUndoableEdit(dataObject, sprite);
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
     * Stores the current sprite pattern and overwrite the  sprite pattern with the stored one.
     */
    private void undoRedo() {
        
        // store the current sprite data for redo puporses
        SpritePattern pattern = this.sprite.getPattern();
        byte[] patternData = pattern.getData();
        
        // undo the last change
        pattern.setData(this.spritePatternData);
        pattern.notifyObservers();
        
        // the sprites data object must notify its observers too
        this.dataObject.setChanged(true);
        this.dataObject.notifyObservers();
        
        // store the original sprite data
        this.spritePatternData = patternData;
    }
}
