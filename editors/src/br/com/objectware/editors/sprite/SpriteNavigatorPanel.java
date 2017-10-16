/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.sprite;

import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * 
 * @author Luciano M. Christofoletti
 * @since 28/Jun/2015
 * 
 * Useful docs:
 * http://wiki.netbeans.org/NetBeansDeveloperFAQ#Editor_and_Edited_Files
 * http://bits.netbeans.org/8.0/javadoc/org-netbeans-spi-navigator/overview-summary.html
 */
@NavigatorPanel.Registration(
    mimeType="application/x-sprite", displayName="#sprite.navigator.panel"
)
public class SpriteNavigatorPanel implements NavigatorPanel, FileChangeListener, LookupListener {
    
    /** holds UI of this panel (must be one instance only for all sprites set) */
    private final SpriteNavigatorVisualPanel navigatorPanel = new SpriteNavigatorVisualPanel();
    
    /** find data in given context */
    private static final Lookup.Template<SpritesDataObject> DATA_TEMPLATE = 
        new Lookup.Template<>(SpritesDataObject.class);
    
    /** current context to work on */
    private Lookup.Result<SpritesDataObject> currentContext;
    
    /** The data object to be explored in the navigator panel */
    private DataObject dataObject;
    
    @Override
    public String getDisplayHint() {
        return "application/x-sprite";
    }
    
    @Override
    public String getDisplayName() {
        return "Sprites navigator panel";
    }
    
    @Override
    public SpriteNavigatorVisualPanel getComponent() {
        return this.navigatorPanel;
    }   
    
    // this method is called when the user selects the sprite file node
    // in the project tree (and not when the visual editor panel is selected)
    @Override
    public void panelActivated(Lookup context) {
        
        // lookup context and listen to result to get notified about context changes
        this.currentContext = context.lookup(SpriteNavigatorPanel.DATA_TEMPLATE);
        this.currentContext.addLookupListener(this);
        
        // get actual data and recompute content
        this.setNewContent(this.currentContext.allInstances());
        
        // update the file listener
        this.addFileChangeListener();
    }   
    
    @Override
    public void panelDeactivated() {
        
        this.currentContext.removeLookupListener(this);
        this.currentContext = null;
        
        // remove the file listener
        this.removeFileChangeListener();
    }   
    
    @Override
    public Lookup getLookup () {
        // go with default activated Node strategy
        //return this.dataObject.getLookup();
        return null;
    }   
    
    @Override
    public void fileChanged(FileEvent fe) {
        // get actual data and recompute content
        if(this.currentContext != null) {
            this.setNewContent(this.currentContext.allInstances());
        }   
    }
    
    @Override public void fileDeleted(FileEvent fe) {
        this.removeFileChangeListener();
    }
    
    @Override public void fileRenamed(FileRenameEvent fre) {
        this.removeFileChangeListener();
    }
    
    @Override public void fileFolderCreated(FileEvent fe) {}
    @Override public void fileDataCreated(FileEvent fe) {}
    @Override public void fileAttributeChanged(FileAttributeEvent fae) {}
    
    // LookupListener implementation
    // This method is called when the sprite tree view is active and the user selects
    // another sprite set node (it is not called when the selection is changed to another file type
    // and then back to another sprites set node)
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        this.setNewContent(((Lookup.Result) lookupEvent.getSource()).allInstances());
    }   
    
    /************* non - public part ************/
    
    private synchronized void setNewContent(java.util.Collection newData) {
        
        // update date object reference (if available)
        if(newData != null) {
            java.util.Iterator<DataObject> iterator = newData.iterator();
            if(iterator.hasNext()) {
                this.dataObject = iterator.next();
                this.getComponent().setDataObject(this.dataObject);
            }
        }
    }
    
    private void addFileChangeListener() {
        if(this.dataObject != null) {
            this.dataObject.getPrimaryFile().addFileChangeListener(this);
        }
    }
    
    private void removeFileChangeListener() {
        if(this.dataObject != null) {
            this.dataObject.getPrimaryFile().removeFileChangeListener(this);
        }
    }
}
