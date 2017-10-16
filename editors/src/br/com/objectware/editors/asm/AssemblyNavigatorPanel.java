/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.asm;

import br.com.objectware.commons.i18n.I18N;
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
    mimeType="text/x-asm", displayName="#source.file.navigator"
)
public class AssemblyNavigatorPanel implements NavigatorPanel, FileChangeListener, LookupListener {
    
    /** holds UI of this panel */
    private final AssemblyNavigatorVisualPanel navigatorPanel = new AssemblyNavigatorVisualPanel();
    
    /** find data in given context */
    private static final Lookup.Template<AssemblyDataObject> DATA_TEMPLATE = 
        new Lookup.Template<>(AssemblyDataObject.class);
    
    /** current context to work on */
    private Lookup.Result<AssemblyDataObject> currentContext;
    
    /** The dataobject... */
    private DataObject dataObject;
    
    /** public no arg constructor needed for system to instantiate provider well */
    public AssemblyNavigatorPanel() {
    }
    
    @Override
    public String getDisplayHint() {
        return I18N.getString("assembly.labels.navigator.tree");
    }
    
    @Override
    public String getDisplayName() {
        return I18N.getString("assembly.labels.navigator.tree");
    }   
    
    @Override
    public AssemblyNavigatorVisualPanel getComponent() {
        return this.navigatorPanel;
    }   
    
    /**
     * This method is called when the navigator panel is opened.
     * @param context 
     */
    @Override
    public void panelActivated(Lookup context) {
        
        // lookup context and listen to result to get notified about context changes
        this.currentContext = context.lookup(AssemblyNavigatorPanel.DATA_TEMPLATE);
        this.currentContext.addLookupListener(this);
        
        // get actual data and recompute content
        this.setNewContent(this.currentContext.allInstances());
        
        // add the file listener
        if(this.dataObject != null) {
            this.dataObject.getPrimaryFile().addFileChangeListener(this);
        }
    }   
    
    /**
     * This method is called when the navigator panel is closed.
     */
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
    
    /**
     * This method is called every time the selection of a source code is done (in tree or in the editor panel)
     * @param lookupEvent 
     */
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        this.setNewContent(((Lookup.Result) lookupEvent.getSource()).allInstances());
    }   
    
    /************* non - public part ************/
    
    /**
     * This is called every time that a selection action is made.
     * @param newData 
     */
    private synchronized void setNewContent(java.util.Collection newData) {
        
        // put your code here that grabs information you need from given
        // collection of data, recompute UI of your panel and show it.
        // Note - be sure to compute the content OUTSIDE event dispatch thread,
        // just final repainting of UI should be done in event dispatch thread.
        // Please use RequestProcessor and Swing.invokeLater to achieve this.
        
        if(newData != null) {
            java.util.Iterator<DataObject> iterator = newData.iterator();
            if(iterator.hasNext()) {
                this.dataObject = iterator.next();
                this.getComponent().setDataObject(this.dataObject);
            }
        }   
    }
    
    /**
     * Remove the current file change listener
     */
    private void removeFileChangeListener() {
        if(this.dataObject != null) {
            this.dataObject.getPrimaryFile().removeFileChangeListener(this);
        }
    }
}
