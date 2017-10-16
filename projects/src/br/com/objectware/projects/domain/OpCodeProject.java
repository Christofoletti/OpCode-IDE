/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.domain;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.domain.ProjectProperties;
import br.com.objectware.domain.TargetPlatform;
import br.com.objectware.ioutils.project.ProjectFilesManager;
import br.com.objectware.projects.information.OpCodeProjectInformation;
import br.com.objectware.projects.providers.BuildProjectActionProvider;
import br.com.objectware.projects.providers.OpCodeCustomizerProvider;
import br.com.objectware.projects.providers.ProjectLogicalViewProvider;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * The OpCode project properties and resources location.
 * 
 * @author Luciano M. Christofoletti
 * @since 19/Apr/2015
 */
public class OpCodeProject implements Project {
    
    /** The "base" path of the project. The opcode.xml file must be inside this directory */
    private final FileObject projectDirectory;
    
    /** The project state: see netbeans documentation */
    private final ProjectState state;
    
    /** The project properties read from opcode.xml file */
    private ProjectProperties projectProperties;
    
    /** The lookup of the project */
    private Lookup lookup;
    
    /**
     * Constructor that initializes the minimum elements that a project need.
     * @param dir the project base directory
     * @param state the project state (modified)
     */
    public OpCodeProject(FileObject dir, ProjectState state) {
        dir.refresh();
        this.projectDirectory = dir;
        this.state = state;
    }
    
    @Override
    public FileObject getProjectDirectory() {
        return this.projectDirectory;
    }
    
    public ProjectProperties getProperties() {
        return this.projectProperties;
    }
    
    public boolean isPropertiesAvailable() {
        return (this.projectProperties != null);
    }
    
    public ProjectState getState() {
        return this.state;
    }
    
    /**
     * This method is called right after the creation of a Project object.
     * @return 
     */
    @Override
    public Lookup getLookup() {
        
        if (this.lookup == null) {
            
            // create the fixed lookup
            this.lookup = Lookups.fixed(new Object[] {
                // register your features here
                this,
                new OpCodeCustomizerProvider(this),    // project configuraion tabs
                new OpCodeProjectInformation(this),    // display information inside IDE
                new ProjectLogicalViewProvider(this),  // project tree view display
                new BuildProjectActionProvider(this)   // project builder provider
            });
        }
        
        return this.lookup;
    }
    
    /**
     * Load project properties.
     * 
     * NOTE: this method throws only IOExceptions!
     * 
     * @return ProjectProperties the project properties
     * @throws java.io.IOException
     */
    public java.util.Optional<ProjectProperties> loadProperties() throws java.io.IOException {
        
        try {
            
            // set the project files manager
            String path = this.projectDirectory.getPath();
            ProjectFilesManager<ProjectProperties> manager = new ProjectFilesManager<>(path);
            
            // try loading the project properties from xml file
            this.projectProperties = manager.loadProjectProperties(ProjectProperties.class,
                                                                   Default.PROJECT_FULL_FILE_NAME);
            
        } catch (javax.xml.bind.JAXBException exception) {
            throw new java.io.IOException(exception);
        }
        
        return java.util.Optional.ofNullable(this.projectProperties);
    }
    
    /**
     * Get the project icon related to the target platform.
     * 
     * @param targetPlatform the target platform
     * @return an Icon
     */
    public static javax.swing.Icon getProjectIcon(TargetPlatform targetPlatform) {
        try {
            return targetPlatform.getIcon();
        } catch(NullPointerException npe) {
            return I18N.getImageIcon("unknown.project.icon");
        }
    }
    
    /**
     * Get the project icon of the current project.
     * @return an Icon
     */
    public javax.swing.Icon getProjectIcon() {
        if(this.isPropertiesAvailable()) {
            TargetPlatform targetPlatform = this.getProperties().getTargetPlatform();
            return OpCodeProject.getProjectIcon(targetPlatform);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.projectDirectory.getName();
    }
}
