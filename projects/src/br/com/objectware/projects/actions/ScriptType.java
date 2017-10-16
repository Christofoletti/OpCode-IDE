/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.actions;

import br.com.objectware.commons.i18n.I18N;

/**
 * Script type enum
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 23/Jun/2015
 */
public enum ScriptType {
    
    BUILD("building", "build"),
    CLEAN("cleaning", "clean.and.build"),
    EXECUTE("executing", "execution"),
    UNDEFINED("default", "undefined");
    
    private final String outputWindowCaption;
    
    private final String actionDescription;
    
    ScriptType(String outputWindowCaption, String actionDescription) {
        this.outputWindowCaption = outputWindowCaption;
        this.actionDescription = actionDescription;
    }
    
    public String getOutputWindowCaption() {
        return I18N.getString(this.outputWindowCaption);
    }
    
    public String getActionDescription() {
        return I18N.getString(this.actionDescription);
    }
}
