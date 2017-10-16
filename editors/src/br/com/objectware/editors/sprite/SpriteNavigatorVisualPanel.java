/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.sprite;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.TextUtil;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.editors.enums.SpriteEditorAction;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.Optional;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 25/mar/2016
 * 
 * Old code for searching top components:
 * 
        // current nodes in the application lookup
        Node[] nodes = TopComponent.getRegistry().getCurrentNodes();
        
        for (Node node : nodes) {
            
            // get the editor that is showing the source file
            TopComponent component = node.getLookup().lookup(TopComponent.class);
            if(component instanceof TopComponent) {
                return component;
            }
        } // end of for
 */
public class SpriteNavigatorVisualPanel extends javax.swing.JPanel
        implements java.lang.Runnable,
                   java.util.Observer,
                   java.awt.event.KeyListener,
                   java.beans.PropertyChangeListener,
                   javax.swing.event.TreeSelectionListener {
    
    /** The data object to be explored in the navigator panel */
    private SpritesDataObject dataObject;
    
    /** The current activated sprite editor top component */
    private Optional<SpriteEditorTopComponent> msxEditorTopComponent = Optional.empty();
    
    /**
     * Creates new form AssemblyNavigatorVisualPanel
     */
    public SpriteNavigatorVisualPanel() {
        this.initComponents();
        this.setNavigatorTreeProperties();
    }   
    
    /**
     * Setup the navigator tree visual properties.
     */
    private void setNavigatorTreeProperties() {
        
        this.navigatorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.navigatorTree.addTreeSelectionListener(this);
        this.navigatorTree.addKeyListener(this);
        
        this.navigatorTree.setCellRenderer(new DefaultTreeCellRenderer() {
            
            private final javax.swing.Icon rootIcon = I18N.getImageIcon("sprite.file.tree.icon");
            private final javax.swing.Icon labelIcon = I18N.getImageIcon("sprite.tree.icon");
            
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean selected, boolean expanded,
                    boolean isLeaf, int row, boolean focused) {
                
                Component component = super.getTreeCellRendererComponent(tree, value,
                        selected, expanded, isLeaf, row, focused);
                
                // set the tree cell icon (root or leaf)
                if (isLeaf && row > 0) {
                    this.setIcon(this.labelIcon);
                } else {
                    this.setIcon(this.rootIcon);
                }
                
                return component;
            }   
        });
        
        // listen to changes in the Top Component registry
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }   
    
    /**
     * Get the current activated editor top component.
     * @return 
     */
    private Optional<SpriteEditorTopComponent> getTopComponent() {
        return this.msxEditorTopComponent;
    }   
    
    /**
     * Set the current activated editor top component.
     * @param msxEditorTopComponent 
     */
    private void setTopComponent(SpriteEditorTopComponent msxEditorTopComponent) {
        this.msxEditorTopComponent = Optional.ofNullable(msxEditorTopComponent);
    }   
    
    /**
     * Send an action event to all Action Listeners listening this button.
     * @param editorAction the editor action event to be sent
     */
    private void sendActionEvent(SpriteEditorAction editorAction) {
        Optional<SpriteEditorTopComponent> topComponent = this.getTopComponent();
        if(topComponent.isPresent()) {
            topComponent.get().fireAction(editorAction);
        }   
    }   
    
    /**
     * Set the sprites data object to be displayed in the navigator panel
     * @param dataObject the data object to be scanned
     */
    protected void setDataObject(DataObject dataObject) {
        
        if(dataObject instanceof SpritesDataObject) {
            
            // remove this from current selected data object
            if(this.dataObject != null) {
                this.dataObject.deleteObserver(this);
            }   
            
            // set new data object reference
            this.dataObject = (SpritesDataObject) dataObject;
            this.dataObject.addObserver(this);
            
            // update the sprite tree view
            synchronized(this) {
                SwingUtilities.invokeLater(this);
            }   
        } else {
            this.navigatorTree.setModel(null);
        }   
    }   
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        treeScrollPane = new javax.swing.JScrollPane();
        navigatorTree = new javax.swing.JTree();

        setLayout(new java.awt.BorderLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        navigatorTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeScrollPane.setViewportView(navigatorTree);

        add(treeScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree navigatorTree;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void valueChanged(TreeSelectionEvent event) {
        
        // get the selected node
        Object selectedComponent = this.navigatorTree.getLastSelectedPathComponent();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedComponent;
        
        // if the selected node is not the root node, that is, a leaf...
        if(treeNode != null && treeNode.isLeaf()) {
            this.selectSpriteInEditor((SpriteNode) treeNode.getUserObject());
        }   
    }   
    
    @Override
    public synchronized void run() {
        
        // return if there is no data available
        if(this.dataObject == null) {
            return;
        }   
        
        // load the sprites and attributes data (if not loaded yet)
        if(this.dataObject.isEmpty()) {
            try {
                this.dataObject.load();
            } catch (java.io.IOException ex) {
                // if there are any problems loading the sprite set, then return
                return;
            }   
        }   
        
        // get the source file reference and create the root node
        FileObject sourceFile = this.dataObject.getPrimaryFile();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(sourceFile.getNameExt());
        
        // add all sprite labels (sorted) to the root node
        for (Sprite sprite : this.dataObject.getSprites()) {
            SpriteNode spriteNode = new SpriteNode(sprite);
            rootNode.add(new DefaultMutableTreeNode(spriteNode));
        }
        
        this.navigatorTree.setModel(new DefaultTreeModel(rootNode));
    }
    
    /**
     * Set the current selected sprite in the sprite editor.
     * @param SpriteNode the label info
     */
    private void selectSpriteInEditor(SpriteNode spriteNode) {
        
        // get the editor that owns the current sprite
        Optional<SpriteEditorTopComponent> topComponent = this.getTopComponent();
        if (topComponent.isPresent()) {
            topComponent.get().setSelectedSprite(spriteNode.getSprite());
        }   
    }   
    
    @Override
    public void update(java.util.Observable observable, Object object) {
        synchronized(this) {
            SwingUtilities.invokeLater(this);
        }   
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        
        switch(keyEvent.getKeyCode()) {
            
            case KeyEvent.VK_L:
            case KeyEvent.VK_ENTER:
                this.sendActionEvent(SpriteEditorAction.CHANGE_SPRITE_LABEL);
                synchronized(this) {
                    SwingUtilities.invokeLater(this);
                }   
                keyEvent.consume();
            break;
        }   
        
        //keyEvent.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
    /**
     * Listener for TopComponent selection changes
     * @param event 
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        
        // deal only activation events
        if(event.getPropertyName().equals(TopComponent.Registry.PROP_ACTIVATED)) {
            
            // get the current activated top component
            TopComponent editorPanel = WindowManager.getDefault().getRegistry().getActivated();
            
            // if it is a Msx Sprite editor, then update the navigator tree
            if(editorPanel instanceof SpriteEditorTopComponent) {
                
                // the current activated msx sprite editor panel
                SpriteEditorTopComponent msxEditorPanelActivated = (SpriteEditorTopComponent) editorPanel;
                
                // update references and view
                this.setTopComponent(msxEditorPanelActivated);
                this.setDataObject(msxEditorPanelActivated.getDataObject());
            }
        }   
    }
    
    /**
     * This class defines the sprite node properties
     */
    private class SpriteNode {
        
        private final String label;
        private final Sprite sprite;
        
        protected SpriteNode(Sprite sprite) {
            
            // the sprite reference
            this.sprite = sprite;
            
            // set the sprite tree label
            this.label = I18N.getString("pattern.number.label",
                               TextUtil.intToTwoDigitString(sprite.getIndex()),
                               sprite.getLabel());
        }
        
        @Override
        public String toString() {
            return this.label;
        }
        
        protected Sprite getSprite() {
            return this.sprite;
        }
    }
    
} // end of class
