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
 * MSX ROM project type template registration.
 * 
 * @author Luciano M. Christofoletti
 * @since 25/may/2015
 */
@TemplateRegistration(folder = "Project/MSX", position = 300,
        displayName = "#msx.rom.type",
        targetName = "msx-rom-project",
        description = "msx-rom-description.html",
        iconBase = "resources/msx.project.icon.png",
        content = "msx-rom-project.zip")
public class MSXROMWizardIterator extends MSXWizardIterator {
    
    public static MSXROMWizardIterator createIterator() {
        return new MSXROMWizardIterator();
    }
    
    @Override
    protected BuildType getBuildType() {
        return MsxBuildType.MSX_ROM;
    }
}
