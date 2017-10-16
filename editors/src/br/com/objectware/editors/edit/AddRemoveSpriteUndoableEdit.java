/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.edit;

import br.com.objectware.domain.msx.sprite.MsxSprite;
import br.com.objectware.editors.msx.sprite.editor.MsxSpriteEditorTopComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Add and remove sprites Undo/Redo implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 12/dec/2015
 */
public class AddRemoveSpriteUndoableEdit extends AbstractUndoableEdit {
    
    /** The sprite editor reference */
    private final MsxSpriteEditorTopComponent spriteEditor;
    
    /** The sprite added or removed */
    private final MsxSprite sprite;
    
    /** The sprite index */
    private int spriteIndex;
    
    /** The "type" of the action (add or remove) */
    private boolean spriteAdded;
    
    /**
     * Undoable edit for actions of add and remove sprite.
     * @param  spriteEditor the sprite editor
     * @param sprite the sprite added or removed
     */
    private AddRemoveSpriteUndoableEdit(MsxSpriteEditorTopComponent spriteEditor, MsxSprite sprite) {
        this.spriteEditor = spriteEditor;
        this.sprite = sprite;
    }   
    
    /**
     * The index of the sprite in the sprite set (add or remove)
     * @param spriteIndex 
     */
    public void setSpriteIndex(int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }
    
    /**
     * The "type" of the current action (true = new sprite added to the current sprite set)
     * @param spriteAdded 
     */
    public void setSpriteAdded(boolean spriteAdded) {
        this.spriteAdded = spriteAdded;
    }
    
    public static UndoableEdit addSpriteUndoRedo(MsxSpriteEditorTopComponent spriteEditor, MsxSprite sprite) {
        
        AddRemoveSpriteUndoableEdit undoableEdit = new AddRemoveSpriteUndoableEdit(spriteEditor, sprite);
        undoableEdit.setSpriteIndex(sprite.getIndex());
        undoableEdit.setSpriteAdded(true);
        
        return undoableEdit;
    }   
    
    public static UndoableEdit removeSpriteUndoRedo(MsxSpriteEditorTopComponent spriteEditor, MsxSprite sprite) {
        
        AddRemoveSpriteUndoableEdit undoableEdit = new AddRemoveSpriteUndoableEdit(spriteEditor, sprite);
        undoableEdit.setSpriteIndex(sprite.getIndex());
        undoableEdit.setSpriteAdded(false);
        
        return undoableEdit;
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
        this.undoRedo(!this.spriteAdded);
    }
    
    @Override
    public void redo() throws CannotRedoException {
        this.undoRedo(this.spriteAdded);
    }   
    
    /**
     * Execute the undo/redo action
     * @param addAction type of action (true = add sprite)
     */
    private void undoRedo(boolean addAction) {
        
        if(addAction) {
            
            // add/insert a new sprite
            if(this.spriteIndex >= 0) {
                this.spriteEditor.insertSprite(this.spriteIndex, this.sprite);
            } else {
                this.spriteEditor.addSprite(this.sprite);
            }   
            
        } else {
            this.spriteEditor.removeSprite(this.sprite);
        }
    }
}
