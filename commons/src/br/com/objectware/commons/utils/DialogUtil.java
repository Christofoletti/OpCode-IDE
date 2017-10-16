/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import br.com.objectware.commons.i18n.I18N;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Dialog utils. 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 16/jun/2016
 */
public class DialogUtil {
    
    /**
     * Show up the dialog window positioned relative to its parent
     * @param dialog 
     */
    public static void show(java.awt.Dialog dialog) {
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }
    
    /**
     * Ask user for save changes before closing the editor.
     * @param parent
     * @param fileName
     * @return user selected option
     */
    public static int saveChanges(Component parent, String fileName) {
        
        // save dialog text buttons
        Object[] options = {
            I18N.getString("save"),
            I18N.getString("discard"),
            I18N.getString("cancel")
        };  
        
        // http://docs.oracle.com/javase/7/docs/api/javax/swing/JOptionPane.html
        Integer userOption = JOptionPane.showOptionDialog(
            parent, // parent component (we can use the main window reference)
            I18N.getString("file.save.changes.question", fileName), // dialog message
            I18N.getString("question"), // dialog title
            JOptionPane.DEFAULT_OPTION, // option type
            JOptionPane.QUESTION_MESSAGE, // message type
            null, // message icon
            options, // the array of options
            options[0] // the default option (YES_OPTION)
        );  
        
        // return the user option (see JOptionPane.YES_OPTION)
        return userOption;
    }
}
