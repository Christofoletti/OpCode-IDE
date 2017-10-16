/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.cookies;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.editors.CustomDataObject;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.cookies.SaveCookie;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 * This cookie saves the Data Object when requested by the user/platform.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 24/may/2015
 */
public class SaveDataObjectCookie implements SaveCookie, Runnable {
    
    /** The Data Object lookup */
    private final Lookup lookup;
    
    /**
     * Saves the the lookup. Better use the lookup instead of DataObject directly because
     * the DataObject may change.
     * 
     * @param lookup the Data Object lookup
     */
    public SaveDataObjectCookie(Lookup lookup) {
        this.lookup = lookup;
    }
    
    @Override
    public void save() {
        // save the Data Object file using a separated thread
        SwingUtilities.invokeLater(new Thread(this));
    }
    
    @Override
    public synchronized void run() {
        
        CustomDataObject dataObject = this.lookup.lookup(CustomDataObject.class);
        assert dataObject != null;
        
        // write the binary file to disk
        try {
            dataObject.save();
        } catch (java.io.IOException exception) {
            JOptionPane.showMessageDialog(
                WindowManager.getDefault().getMainWindow(),
                I18N.getString("error.writing.file", dataObject.getName()), // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
        }   
    }   
}
