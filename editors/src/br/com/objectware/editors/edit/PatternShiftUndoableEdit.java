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
 * Pattern shift Undo/Redo implementation.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 17/dec/2015
 */
public class PatternShiftUndoableEdit extends AbstractUndoableEdit {
    
    /** The sprite editor reference */
    private final MsxSpriteEditorTopComponent spriteEditor;
    
    /** The sprite added or removed */
    private final MsxSprite sprite;
    
    /** The "type" of the action (pattern shifted up or down) */
    private boolean patternShiftedUp;
    
    /**
     * Undoable edit for actions of add and remove sprite.
     * @param  spriteEditor the sprite editor
     * @param sprite the sprite added or removed
     */
    private PatternShiftUndoableEdit(MsxSpriteEditorTopComponent spriteEditor, MsxSprite sprite) {
        this.spriteEditor = spriteEditor;
        this.sprite = sprite;
    }   
    
    /**
     * The "type" of the move action (true = sprite moved up)
     * @param patternShiftedUp 
     */
    public void setPatternShiftedUp(boolean patternShiftedUp) {
        this.patternShiftedUp = patternShiftedUp;
    }   
    
    public static UndoableEdit shiftPatternUpUndoRedo(MsxSpriteEditorTopComponent spriteEditor, MsxSprite sprite) {
        
        PatternShiftUndoableEdit undoableEdit = new PatternShiftUndoableEdit(spriteEditor, sprite);
        undoableEdit.setPatternShiftedUp(true);
        
        return undoableEdit;
    }   
    
    public static UndoableEdit shiftPatternDownUndoRedo(MsxSpriteEditorTopComponent spriteEditor, MsxSprite sprite) {
        
        PatternShiftUndoableEdit undoableEdit = new PatternShiftUndoableEdit(spriteEditor, sprite);
        undoableEdit.setPatternShiftedUp(false);
        
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
        this.undoRedo(this.patternShiftedUp);
    }
    
    @Override
    public void redo() throws CannotRedoException {
        this.undoRedo(!this.patternShiftedUp);
    }   
    
    /**
     * Execute the undo/redo action
     */
    private void undoRedo(boolean shiftUp) {
        if(shiftUp) {
            this.spriteEditor.moveSpriteDown(this.sprite);
        } else {
            this.spriteEditor.moveSpriteUp(this.sprite);
        }
    }
}
