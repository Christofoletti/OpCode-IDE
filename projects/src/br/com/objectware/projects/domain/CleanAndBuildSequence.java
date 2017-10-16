/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.domain;

import br.com.objectware.commons.i18n.I18N;

/**
 * Clean and Build sequence options
 * 
 * @author Luciano M. Christofoletti
 * @since 15/jun/2015
 */
public enum CleanAndBuildSequence {
    
    ONLY_CLEAN_AND_BUILD("run.only.clean.and.build"),
    FIRST_CLEAN_THEN_BUILD("run.clean.then.build"),
    FIRST_BUILD_THEN_CLEAN("run.build.then.clean");
    
    /** Short text description of the clean and build sequence */
    private final String description;
    
    CleanAndBuildSequence(String textId) {
        this.description = I18N.getString(textId);
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
