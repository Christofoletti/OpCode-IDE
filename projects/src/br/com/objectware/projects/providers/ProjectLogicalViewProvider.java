/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.providers;

import br.com.objectware.projects.domain.OpCodeProject;
import br.com.objectware.projects.domain.ProjectNode;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * The OpCode project tree root node view provider.
 * 
 * @author Luciano M. Christofoletti
 * @since 20/Apr/2015
 */
public class ProjectLogicalViewProvider implements LogicalViewProvider {
    
    private final OpCodeProject project;
    
    public ProjectLogicalViewProvider(OpCodeProject project) {
        this.project = project;
    }
    
    @Override
    public Node createLogicalView() {
        
        try {
            
            // obtain the project directory's node
            FileObject projectDirectory = this.project.getProjectDirectory();
            DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
            Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
            
            // decorate the project directory's node
            return new ProjectNode(nodeOfProjectFolder, this.project);
            
        } catch (DataObjectNotFoundException excepion) {
            Exceptions.printStackTrace(excepion);
            // fallback-the directory couldn't be created -
            // read-only filesystem or something evil happened
            return new AbstractNode(Children.LEAF);
        }
    }
    
    /**
     * This method is called everytime a new node is added or removed from
     * projects tree.
     * 
     * @param node the project node
     * @param object the new object created (folder)
     * @return 
     */
    @Override
    public Node findPath(Node node, Object object) {
        return null;
    }
    
}
