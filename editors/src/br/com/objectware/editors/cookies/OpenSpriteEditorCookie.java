/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.cookies;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.editors.sprite.SpritesDataObject;
import br.com.objectware.editors.msx.sprite.editor.MsxSpriteEditorTopComponent;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * This cookie opens the sprite pattern editor when requested by the user/platform.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 24/jul/2015
 * 
 * Useful docs:
 *  https://blogs.oracle.com/geertjan/entry/bye_savecookie_hello_org_netbeans
 * 
 */
public class OpenSpriteEditorCookie extends AbstractOpenEditorCookie {
    
    /**
     * Constructor that initializes the lookup data.
     * @param lookup 
     */
    public OpenSpriteEditorCookie(Lookup lookup) {
        super(lookup);
    }   
    
    /**
     * Get the sprite editor top component. If the component is not available yet, it is created.
     * @return the sprite editor top component
     */
    @Override
    protected TopComponent getEditorTopComponent() {
        
        SpritesDataObject dataObject = this.getLookup().lookup(SpritesDataObject.class);
        SpriteFormat spriteFormat = dataObject.getSpriteFormat();
        TopComponent newSpriteEditor;
        
        switch (spriteFormat) {
             
            case MSX_MODE1:
            case MSX_MODE2:
                newSpriteEditor = new MsxSpriteEditorTopComponent(this.getLookup());
                break;
                
            default:
                throw new IllegalArgumentException(I18N.getString("sprite.format.not.available", spriteFormat));
        }   
        
        return newSpriteEditor;
    }   
}
