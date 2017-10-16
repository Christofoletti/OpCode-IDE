/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.cookies;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.editors.CustomDataObject;
import javax.swing.JOptionPane;
import org.openide.cookies.OpenCookie;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * Default implementation of an open editor action.
 * The concrete implementation must define the instatiation rules for Top components
 * that is used by the editor.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 23/may/2016
 */
public abstract class AbstractOpenEditorCookie
                extends Thread
                implements OpenCookie, Runnable {
    
    /** The visual editor (TopComponent) */
    private TopComponent topComponent;
    
    /** The data object lookup */
    private final Lookup lookup;
    
    /**
     * Abstract open editor cookie implementation.
     * @param lookup the data object to be edited
     */
    public AbstractOpenEditorCookie(Lookup lookup) {
        this.lookup = lookup;
    }
    
    /**
     * Access method for the lookup data.
     * @return 
     */
    public Lookup getLookup() {
        return this.lookup;
    }
    
    /**
     * Used when the editor is not opened in the initialization process (IDE initialization)
     * @param topComponent 
     */
    public void setTopComponent(TopComponent topComponent) {
        this.topComponent = topComponent;
    }
    
    @Override
    public void open() {
        // the open window action must be called from a separated thread
        javax.swing.SwingUtilities.invokeLater(this);
    }   
    
    @Override
    public synchronized void run() {
        
        CustomDataObject dataObject = this.lookup.lookup(CustomDataObject.class);
        assert dataObject != null;
        
        try {
            
            // verify if the current file has changes. If so, ask the user for keep or reload
            if(dataObject.isModified()) {
                
                // http://docs.oracle.com/javase/7/docs/api/javax/swing/JOptionPane.html
                int userOption = JOptionPane.showConfirmDialog(
                    this.lookup.lookup(TopComponent.class), // parent component
                    I18N.getString("file.discard.changes.question", dataObject.getPrimaryFile().getNameExt()),
                    I18N.getString("question"), // dialog title
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
                );  
                
                // user don't want to reload the file
                if(userOption != JOptionPane.YES_OPTION) {
                    return;
                }
            }   
            
            // loads the data object from file
            dataObject.load();
            
            // open the binary editor (it is expected that the binary data is refreshed)
            if(!this.isTopComponentAvailable()) {
                this.topComponent = this.getEditorTopComponent();
            }
            
            this.topComponent.open();
            this.topComponent.requestActive();
            
        } catch (java.io.IOException | IllegalArgumentException exception) {
            
            JOptionPane.showMessageDialog(
                this.lookup.lookup(TopComponent.class),
                I18N.getString("error.reading.file", dataObject.getName()), // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
            
            // close the sprite editor when an error occurs
            if(this.isTopComponentAvailable()) {
                this.topComponent.close();
            }
        }   
    }
    
    /**
     * Return the status of availability of the editor top component.
     * @return 
     */
    private boolean isTopComponentAvailable() {
        return (this.topComponent != null);
    }   
    
    /**
     * Get the editor top component. If the component is not available yet, it is created.
     * @return 
     */
    protected abstract TopComponent getEditorTopComponent();
    
}
