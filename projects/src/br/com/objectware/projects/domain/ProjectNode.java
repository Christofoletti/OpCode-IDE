/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.domain;

import br.com.objectware.commons.i18n.I18N;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * The OpCode project node view for the project tree.
 * 
 * @author Luciano M. Christofoletti
 * @since 21/Apr/2015
 * 
 * Useful docs:
 *     https://platform.netbeans.org/tutorials/nbm-projecttypeant.html
 */
public class ProjectNode extends FilterNode {
    
    // this is the icon showed when the user is opening a project
    private final ImageIcon defaultProjectIcon = I18N.getImageIcon("project.icon");
    
    /** The project data associated to the node representation */
    private final OpCodeProject project;
    
    /**
     * The project root node. This node has some special properties and actions.
     * 
     * @param node the project root node
     * @param project the opcode project
     * 
     * @throws DataObjectNotFoundException 
     */
    public ProjectNode(Node node, OpCodeProject project) throws DataObjectNotFoundException {
        
        super(node,
                // new FilterNode.Children(node), // show everything in the projects folder
                NodeFactorySupport.createCompositeChildren(
                    project,
                    "Projects/br-com-objectware-opcode-project/Nodes"
                ),
                
                new ProxyLookup(
                    new Lookup[]{
                        Lookups.singleton(project),
                        node.getLookup()
                    })
                );
        
        this.project = project;
    }
    
    @Override
    public Action[] getActions(boolean argument) {
        return new Action[] {
            //ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_BUILD, "Build Project", null),
            CommonProjectActions.setAsMainProjectAction(),
            CommonProjectActions.customizeProjectAction(),
            //CommonProjectActions.deleteProjectAction(),
            CommonProjectActions.closeProjectAction(),
            //CommonProjectActions.copyProjectAction(),
            CommonProjectActions.setProjectConfigurationAction()
        };
    }
    
    @Override
    public Image getIcon(int type) {
        try {
            return this.project.getProperties().getTargetPlatform().getIcon().getImage();
        } catch(NullPointerException npe) {
            return this.defaultProjectIcon.getImage();
        }
    }
    
    @Override
    public Image getOpenedIcon(int type) {
        return this.getIcon(type);
    }
    
    @Override
    public String getDisplayName() {
        return "[" + project.getProperties().getName() + "]";
    }
}
