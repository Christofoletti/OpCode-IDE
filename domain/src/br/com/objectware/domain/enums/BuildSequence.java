/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;

/**
 * Build sequence options. This gives some flexibility to the build proccess.
 * 
 * @author Luciano M. Christofoletti
 * @since 14/jun/2015
 */
public enum BuildSequence {
    
    ONLY_EMBEDDED("run.only.embedded.compiler"),
    ONLY_BUILD_SCRIPT("run.only.build.script"),
    FIRST_EMBEDDED_THEN_SCRIPT("run.fisrt.embedded.then.external");
    
    /** Short text description of the build sequence */
    private final String description;
    
    BuildSequence(String textId) {
        this.description = I18N.getString(textId);
    }
    
    public String getText() {
        return this.name();
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
