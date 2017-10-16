/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.sprite;

import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.editors.enums.SpriteEditorAction;
import org.openide.loaders.DataObject;

/**
 * The sprite editor top component interface.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 */
public interface SpriteEditorTopComponent {
    
    /**
     * Get the associated Data Object.
     * @return 
     */
    DataObject getDataObject();
    
    /**
     * Set the current selected sprite.
     * @param sprite 
     */
    void setSelectedSprite(Sprite sprite);
    
    /**
     * Get the current selected sprite.
     * @return 
     */
    Sprite getSelectedSprite();
    
    /**
     * Send an action event to the sprite editor.
     * @param spriteEditorAction 
     */
    void fireAction(SpriteEditorAction spriteEditorAction);
}
