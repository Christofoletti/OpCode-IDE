/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.wizards.msx;

import br.com.objectware.domain.BuildType;
import br.com.objectware.domain.enums.MsxBuildType;
import org.netbeans.api.templates.TemplateRegistration;

/**
 * MSX Binary project type template registration.
 * 
 * @author Luciano M. Christofoletti
 * @since 25/may/2015
 */
@TemplateRegistration(
    folder = "Project/MSX", position = 100,
    displayName = "#msx.binary.type",
    targetName = "msx-binary-project",
    description = "msx-bin-description.html",
    iconBase = "resources/msx.project.icon.png",
    content = "msx-bin-project.zip")
public class MSXBINWizardIterator extends MSXWizardIterator {
    
    public static MSXBINWizardIterator createIterator() {
        return new MSXBINWizardIterator();
    }
    
    @Override
    protected BuildType getBuildType() {
        return MsxBuildType.MSX_BINARY;
    }
}
