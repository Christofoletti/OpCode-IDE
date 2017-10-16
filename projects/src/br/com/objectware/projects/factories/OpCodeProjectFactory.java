/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.factories;

import br.com.objectware.commons.utils.Default;
import br.com.objectware.domain.ProjectProperties;
import br.com.objectware.projects.domain.OpCodeProject;
import java.io.IOException;
import java.util.Optional;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectFactory2;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 * The OpCode projects factory. Used to identify and select OpCode projects.
 * 
 * @author Luciano M. Christofoletti
 * @since 18/Apr/2015
 */
@ServiceProvider(service=ProjectFactory.class)
public class OpCodeProjectFactory implements ProjectFactory2 {
    
    /**
     * 
     * @param projectDirectory
     * @return true if the file object is a valid opcode project descriptor
     */
    @Override
    public boolean isProject(FileObject projectDirectory) {
        
        // try to get the file object using the default project descriptor file name (see Default java class)
        FileObject fileDescriptor = projectDirectory.getFileObject(Default.PROJECT_FULL_FILE_NAME);
        
        // if the project descriptor file exists, then
        // try to load the project properties to verify if it is valid
        if(fileDescriptor != null) {
            try {
                return (new OpCodeProject(projectDirectory, null).loadProperties() != null);
            } catch (IOException ioe) {
                // invalid project descriptor: return false
            }
        }
        
        return false;
    }
    
    /**
     * This method is called for every directory entry that is read when you
     * select an directory while opening a project. When you select a directory that
     * has a project inside, the loadProject method is called and a new Project
     * Object is created in memory.
     * If you select another directory and following that you select a directory that
     * has a project inside that was selected before, the project is not created again.
     * When you close the project directory selection dialog, all created projects that
     * were not loaded are discarded.
     * 
     * @param projectDirectory the selected directory where the project is supposed to be
     * 
     * @return 
     */
    @Override
    public ProjectManager.Result isProject2(FileObject projectDirectory) {
        
        // the project manager result
        ProjectManager.Result result = null;
        
        // try to get the file object using the default project name (see Default java class)
        FileObject fileDescriptor = projectDirectory.getFileObject(Default.PROJECT_FULL_FILE_NAME);
        
        // if the project descriptor file exists, then try to load the project properties
        if(fileDescriptor != null) {
            
            // load the project properties object (return as Object to avoid unncessary casting)
            OpCodeProject project = new OpCodeProject(projectDirectory, null);
            
            try {
                // set the project manager if the project properties is valid
                Optional<ProjectProperties> projectProperties = project.loadProperties();
                if(projectProperties.isPresent()) {
                    Icon projectIcon = project.getProjectIcon();
                    result = new ProjectManager.Result(projectDirectory.getPath(), "Project", projectIcon);
                }
                
            } catch (IOException ioe) {
                // invalid project descriptor: return null
            }
        }
        
        return result;
    }
    
    /**
     * If the current directory contains a project and the use selects it,
     * this method is called and the project is created in memory. Note that
     * the project is not loaded by the IDE at this point.
     * 
     * @param dir selected dir
     * @param state the project state
     * @return the loaded project, if available
     * @throws IOException if some error occurred
     */
    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        
        OpCodeProject opCodeProject = null;
        dir.refresh(true);
        
//        System.out.println("[INFO] loading project: " + dir.getPath());
//        for(FileObject file:dir.getChildren()) {
//            System.out.println("\t[INFO] content: " + file.getNameExt());
//            if(file.getChildren().length > 0) {
//                System.out.println("\t\tfile: " + file.getChildren()[0].getNameExt());
//            }
//        }
        
        if(this.isProject(dir)) {
            opCodeProject = new OpCodeProject(dir, state);
            opCodeProject.loadProperties();
        }   
        
        return opCodeProject;
    }
    
    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {
        System.out.println("Saving: [" + project.getProjectDirectory() + "]");
        
        
//        try {
//            
//            //OpCodeProject opCodeProject = (OpCodeProject) project;
//            
//            // set the project files manager
//            String path = project.getProjectDirectory().getPath();
//            //String projectFile = fileDescriptor.getNameExt();
//            ProjectFilesManager manager = new ProjectFilesManager(path);
//            
//            // try loading the project properties
//            manager.saveProjectDescriptor(ProjectProperties.class);
//            
//            System.out.println("Ok, project saved!");
//            
//        } catch (IOException | JAXBException exception) {
//            // in this case, only return null to indicate that the project descriptor is invalid
//            Exceptions.printStackTrace(exception);
//        }
    }
}

