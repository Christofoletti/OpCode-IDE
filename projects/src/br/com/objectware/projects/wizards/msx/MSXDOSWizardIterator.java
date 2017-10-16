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
 * MSX DOS project type template registration.
 * 
 * @author Luciano M. Christofoletti
 * @since 25/may/2015
 */
@TemplateRegistration(folder = "Project/MSX", position = 200,
        displayName = "#msx.dos.type",
        targetName = "msx-dos-project",
        description = "msx-dos-description.html",
        iconBase = "resources/msx.project.icon.png",
        content = "msx-dos-project.zip")
public class MSXDOSWizardIterator extends MSXWizardIterator {
    
    public static MSXDOSWizardIterator createIterator() {
        return new MSXDOSWizardIterator();
    }
    
    @Override
    protected BuildType getBuildType() {
        return MsxBuildType.MSX_DOS;
    }
}
