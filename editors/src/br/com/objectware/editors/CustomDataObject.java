/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 * Custom OpCode IDE data object. Defines the load, save and set changed operations.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 23/may/2016
 */
public abstract class CustomDataObject extends MultiDataObject {
    
    /**
     * 
     * @param fo
     * @param loader
     * @throws DataObjectExistsException 
     */
    public CustomDataObject(FileObject fileObject, MultiFileLoader loader)
                            throws DataObjectExistsException, IOException {
        super(fileObject, loader);
    }
    
    /**
     * Load data from file.
     * 
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws NumberFormatException 
     */
    public abstract void load() throws IOException;
    
    /**
     * Save the data object to file.
     * @throws IOException 
     */
    public abstract void save() throws IOException;
    
    /**
     * Set the changed status of the current data object
     * @param changed 
     */
    public abstract void setChanged(boolean changed);
    
}
