/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.domain;

import java.awt.Image;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.PasteAction;
import org.openide.actions.ToolsAction;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.ProxyLookup;

/**
 * The OpCode default folder node for the project tree.
 * 
 * @author Luciano M. Christofoletti
 * @since 21/Apr/2015
 * 
 * Useful docs:
 *  https://platform.netbeans.org/tutorials/nbm-nodesapi.html
 *  https://platform.netbeans.org/tutorials/nbm-nodesapi2.html
 *  https://platform.netbeans.org/tutorials/nbm-nodesapi3.html
 * 
 * 
 */
public class ProjectFolderNode extends AbstractNode {
    
    // this is the icon showed when the user is opening a project
    //private final ImageIcon projectFolderIcon = I18N.getImageIcon("sources.folder.tree.icon");
    private ImageIcon icon;
    
    /** This is the displayable string that appears in the project tree */
    private final String displayName;
    
    /**
     * 
     * @param node
     * @throws DataObjectNotFoundException 
     */
    public ProjectFolderNode(Node node) throws DataObjectNotFoundException {
        
//        super(node,
        super(
                new FilterNode.Children(node),
//                NodeFactorySupport.createCompositeChildren(
//                    project,
//                    "Projects/br-com-objectware-opcode-project/Nodes"
//                ),
                
                new ProxyLookup(
                    new Lookup[]{
//                        Lookups.singleton(project),
                        node.getLookup()
                    })
                );
        
        this.displayName = node.getDisplayName();
    }
    
    @Override
    public Action[] getActions(boolean argument) {
        //return CommonProjectActions.forType(CommonProjectActions.EXISTING_SOURCES_FOLDER);
        return new Action[] {
            CommonProjectActions.newFileAction(),
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PasteAction.class),
            SystemAction.get(OpenLocalExplorerAction.class),
//            CommonProjectActions.newFileAction(),
//            CommonProjectActions.customizeProjectAction(),
//            CommonProjectActions.deleteProjectAction(),
//            CommonProjectActions.moveProjectAction(),
//            CommonProjectActions.copyProjectAction(),
//            CommonProjectActions.setProjectConfigurationAction(),
//            //PasteAction.get(PasteAction.class),
//            //CutAction.get(CutAction.class),
//            //CopyAction.get(CopyAction.class),
//            //PasteAction.get(PasteAction.class)
//            SystemAction.get(CopyAction.class),
//            SystemAction.get(PasteAction.class),
//            //DeleteAction.get(DeleteAction.class)
//            SystemAction.get(OpenLocalExplorerAction.class),
//            null,
//            SystemAction.get(NewAction.class),
//            null,
//            SystemAction.get(ToolsAction.class),
//            SystemAction.get(PropertiesAction.class)
        };
    }
    
    @Override
    public Image getIcon(int type) {
        return this.icon.getImage();
    }
    
    @Override
    public Image getOpenedIcon(int type) {
        return this.getIcon(type);
    }
    
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    
    @Override
    public String getDisplayName() {
        return this.displayName;
    }
    
    @Override
    public boolean canRename() {
        return false;
    }
//    @Override
//    public boolean canCut() {
//        return true;
//    }
//    
    @Override
    public boolean canCopy() {
        return true;
    }   
    
//    @Override
//    public boolean canDestroy() {
//        return true;
//    }
}
