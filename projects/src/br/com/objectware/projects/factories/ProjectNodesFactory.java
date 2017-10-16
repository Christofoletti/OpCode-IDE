/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.factories;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.projects.domain.OpCodeProject;
import br.com.objectware.projects.domain.ProjectFolderNode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * The OpCode project default folders factory for the project tree.
 * 
 * @author Luciano M. Christofoletti
 * @since 21/Apr/2015
 */
@NodeFactory.Registration(projectType = "br-com-objectware-opcode-project", position = 1)
public class ProjectNodesFactory implements NodeFactory {
    
    @Override
    public NodeList<?> createNodes(Project project) {
        
        OpCodeProject opCodeProject = project.getLookup().lookup(OpCodeProject.class);
        assert opCodeProject != null;
        
        return new ProjectFolderNodeList(opCodeProject);
    }
    
    /**
     * 
     */
    private class ProjectFolderNodeList implements NodeList<Node> {
        
        private final OpCodeProject project;
        
        private final ImageIcon sourcesIcon = I18N.getImageIcon("sources.folder.tree.icon");
        
        private final ImageIcon resourcesIcon = I18N.getImageIcon("resources.folder.tree.icon");
        
        private final ImageIcon buildIcon = I18N.getImageIcon("build.folder.tree.icon");
        
        public ProjectFolderNodeList(OpCodeProject project) {
            this.project = project;
        }
        
        @Override
        public List<Node> keys() {
            
            FileObject projectDirectory = this.project.getProjectDirectory();
            List<Node> result = new ArrayList<>();
            
            // show only the default project directories
            try {
                ProjectFolderNode sourcesFolder = this.getNodeForFolder(projectDirectory, Default.SOURCES_FOLDER);
                sourcesFolder.setIcon(this.sourcesIcon);
                result.add(sourcesFolder);
                
                ProjectFolderNode resourcesFolder = this.getNodeForFolder(projectDirectory, Default.RESOURCES_FOLDER);
                resourcesFolder.setIcon(this.resourcesIcon);
                result.add(resourcesFolder);
                
                ProjectFolderNode buildFolder = this.getNodeForFolder(projectDirectory, Default.BUILD_FOLDER);
                buildFolder.setIcon(this.buildIcon);
                result.add(buildFolder);
                
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            
            return result;
        }
        
        /**
         * 
         * @param projectFolder
         * @param folderName
         * @return
         * @throws DataObjectNotFoundException 
         */
        private ProjectFolderNode getNodeForFolder(FileObject projectFolder, String folderName)
                throws DataObjectNotFoundException {
            
            // obtain the project directory's node:
            FileObject folder = projectFolder.getFileObject(folderName);
            DataFolder dataFolder = DataFolder.findFolder(folder);
            
            return new ProjectFolderNode(dataFolder.getNodeDelegate());
        }
        
        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }
        
        // This notification occurs when the project is opened
        @Override
        public void addNotify() {
        }
        
        // This notification may occur after closing the project
        @Override
        public void removeNotify() {
        }
        
        @Override
        public void addChangeListener(ChangeListener cl) {
        }
        
        // This notification may occur after closing the project
        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
        
    }
    
}