/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.cookies;

import br.com.objectware.editors.binary.BinaryEditorTopComponent;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * This cookie opens the binary data editor when requested by the user/platform.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 15/jul/2015
 * 
 * Useful docs:
 *  https://blogs.oracle.com/geertjan/entry/bye_savecookie_hello_org_netbeans
 * 
 */
public class OpenBinaryEditorCookie extends AbstractOpenEditorCookie {
    
    /**
     * Constructor that initializes the lookup data.
     * @param lookup 
     */
    public OpenBinaryEditorCookie(Lookup lookup) {
        super(lookup);
    }
    
    /**
     * Get the binary editor top component. If the component is not available yet, it is created.
     * @return 
     */
    @Override
    protected TopComponent getEditorTopComponent() {
        return new BinaryEditorTopComponent(this.getLookup());
    }
}
