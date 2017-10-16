/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Build",
        id = "br.com.objectware.projects.actions.MyBuildAction"
)
@ActionRegistration(
        displayName = "#CTL_MyBuildAction"
)
@ActionReference(path = "Menu/BuildProject", position = -90)
@Messages("CTL_MyBuildAction=Dump project")
public final class DumpAction implements ActionListener {
    
    private final Project project;
    
    /**
     * 
     * @param project 
     */
    public DumpAction(Project project) {
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        
        System.out.println("Dumping project: " + this.project.getProjectDirectory());
        
        FileObject projectDirectory = this.project.getProjectDirectory();
        
        // list project files and folders structure
        System.out.println("\n**** Project file strucutre: ");
        this.listAll("", projectDirectory);
        System.out.println("****\n");
        
        System.out.println("\n**** Project info:");
        System.out.println("Name: " + this.project.toString());
        System.out.println("Directory: " + this.project.getProjectDirectory());
        
    }
    
    /**
     * 
     * @param prefix
     * @param file 
     */
    private void listAll(String prefix, FileObject file) {
        
        System.out.println(prefix + "File: " + file.getNameExt()  + " : " + file.getMIMEType());
        
        if(file.getChildren().length > 0) {
            for(FileObject child : file.getChildren()) {
                this.listAll(prefix + '\t', child);
            }
        }
    }
    
}
