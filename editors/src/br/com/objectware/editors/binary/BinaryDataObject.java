/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the binaryEditor.
 */
package br.com.objectware.editors.binary;

import br.com.objectware.commons.utils.Default;
import br.com.objectware.editors.CustomDataObject;
import br.com.objectware.editors.cookies.OpenBinaryEditorCookie;
import br.com.objectware.editors.cookies.SaveDataObjectCookie;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;

@MIMEResolver.ExtensionRegistration(
        displayName = "#binary.file.label",
        mimeType = "application/x-binary",
        extension = {"bin", "BIN", "dat", "DAT", "rom", "ROM", "com", "COM"}
)
@DataObject.Registration(
        mimeType = "application/x-binary",
        iconBase = "br/com/objectware/editors/binary/binary-file.png",
        displayName = "#binary.file.label",
        position = 400
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/application/x-binary/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
/**
 * This class stores and manages a binary data file.
 */
public final class BinaryDataObject extends CustomDataObject {
    
    /** The binary file data. It is shared with visual and model elements */
    private byte[] data = new byte[0];
    
    /**
     * This constructor is called when the file icon is showed in the project tree.
     * @param file the file containing the binary data
     * @param loader the multifile loader
     * @throws DataObjectExistsException
     * @throws IOException 
     */
    public BinaryDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        
        super(fo, loader);
        
        // set the opener cookie (loads the binary file)
        this.getCookieSet().add(new OpenBinaryEditorCookie(super.getLookup()));
    }
    
    @Override
    public Lookup getLookup() {
        return this.getCookieSet().getLookup();
    }
    
    /**
     * Loads the binary data from file
     * @throws IOException 
     */
    @Override
    public synchronized void load() throws IOException {
        this.data = this.getPrimaryFile().asBytes();
        this.setChanged(false);
    }
    
    /**
     * Saves the current binary data into the binary file
     * @throws IOException 
     */
    @Override
    public synchronized void save() throws IOException {
        
        FileObject primaryFile = this.getPrimaryFile();
        
        // write the file to disk
        try (OutputStream outputStream = new BufferedOutputStream(primaryFile.getOutputStream())) {
            outputStream.write(this.data);
            outputStream.flush();
        }   
        
        this.setChanged(false);
        
    }
    
    /**
     * Return the value at position (i,j)
     * @param rowIndex
     * @param columnIndex
     * @return 
     */
    public byte getValueAt(int rowIndex, int columnIndex) throws IndexOutOfBoundsException {
        return this.getValueAt(rowIndex * Default.HEX_TABLE_WIDTH + columnIndex);
    }   
    
    /**
     * Return the value at index i in the current data array
     * @param index
     * @return 
     */
    public byte getValueAt(int index) throws IndexOutOfBoundsException {
        return this.data[index];
    }   
    
    /**
     * Set the value at index i in the current data array
     * @param index
     * @param value 
     */
    protected void setValueAt(int index, byte value) throws IndexOutOfBoundsException {
        this.data[index] = value;
    }   
    
    /**
     * Verify if the given indexes (row, column) contains a valid data
     * @param rowIndex
     * @param columnIndex
     * @return 
     */
    public boolean validateIndexes(int rowIndex, int columnIndex) {
        return (rowIndex * Default.HEX_TABLE_WIDTH + columnIndex) < this.data.length;
    }   
    
    /**
     * Return the size of the byte data array.
     * @return 
     */
    public int getLength() {
        return this.data.length;
    }
    
    /**
     * Return the array of byte data.
     * @return 
     */
    public byte[] getData() {
        return this.data;
    }
    
    /**
     * Set the modification status of the binary data.
     * @param changed the binary data was changed
     */
    @Override
    public synchronized void setChanged(boolean changed) {
        
        // this call sets the data object internal change status (changed or not)
        this.setModified(changed);
        
        // get the save data object cookie (if available)
        SaveDataObjectCookie cookie = this.getCookieSet().getCookie(SaveDataObjectCookie.class);
        
        // update the cookie set and notifier status
        if(changed) {
            // the data object is being changed and there is no SaveCookie available
            if(cookie == null) {
                this.getCookieSet().add(new SaveDataObjectCookie(super.getLookup()));
            }
        } else {
            // the data object is being unchanged and there is a SaveCookie in the cookie set
            if(cookie != null) {
                this.getCookieSet().remove(cookie);
            }
        }   
    }
    
}
