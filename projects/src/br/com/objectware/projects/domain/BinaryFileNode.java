/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.domain;

import javax.swing.Action;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Luciano M. Christofoletti
 * @since 20/Jun/2015
 * 
 * Useful docs:
 *      https://blogs.oracle.com/geertjan/entry/node_cut_copy_paste_delete
 */
public class BinaryFileNode extends AbstractNode {
    
    public BinaryFileNode(Children children) {
        super(children);
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    new CutAction(),
                    new CopyAction(),
                    new DeleteAction()
                    //MoveUpAction.get(MoveUpAction.class),
                    //MoveDownAction.get(MoveDownAction.class)
                };
    }
    
    @Override
    public boolean canCut() {
        return true;
    }
    
    @Override
    public boolean canCopy() {
        return true;
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }
}
