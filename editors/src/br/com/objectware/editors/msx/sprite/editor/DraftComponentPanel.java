/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.editors.sprite.SpritesDataObject;
import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.ImageUtil;
import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.msx.sprite.MsxSpriteAttributes;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.editors.enums.SpriteEditorAction;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * This is the draft panel. It allows the user preview how the sprite image will
 * appear over a background image.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 28/aug/2015
 */
public class DraftComponentPanel extends javax.swing.JPanel
                                 implements ActionListener, ListSelectionListener {
    
    /** 
     * The colum widths in pixels
     * (this array must have the same size of COLUMN_NAMES array in the MsxSpriteAttributesTableModel)
     */
    private static final int TABLE_COLUMN_WIDTH[] = {32, 16, 16, 40, 16, 24};
    
    /** The sprites data object being edited */
    //private SpritesDataObject spritesDataObject;
    
    /** The sprite viewer panel */
    private final MsxSpriteViewPanel spritesViewPanel = new MsxSpriteViewPanel();
    
    /** The sprite attributes model */
    private final MsxSpriteAttributesTableModel attributesTableModel = new MsxSpriteAttributesTableModel();
    
    /** The sprite color editor */
    private final MsxSpriteColorChooser spriteColorEditor = new MsxSpriteColorChooser();
    
    /** The background image file chooser */
    private javax.swing.JFileChooser imageFileChooser;
    
    /**
     * Creates new form MsxSpriteViewPanel
     */
    public DraftComponentPanel() {
        
        this.initComponents();
        
        // initializations
        this.setupVisualComponentsProperties();
        this.setupListeners();  
    }   
    
    /**
     * Set the visual properties of draft panel components.
     */
    private void setupVisualComponentsProperties() {
        
        // set the sprite scale to 2x
        this.scaleComboBox.setSelectedIndex(1);
        
        // the default table cell renderer and color cell renderers
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // setup the visual properties of the sprite attributes table
        this.spriteAttributesTable.setDefaultRenderer(String.class, cellRenderer);
        this.spriteAttributesTable.setDefaultRenderer(Color.class, new CellColorRenderer());
        this.spriteAttributesTable.setFillsViewportHeight(true);
        
        // the draft panel must be able to access the sprite attributes table for selection purposes (:P)
        this.spritesViewPanel.setSpriteAttributesTable(this.spriteAttributesTable);
        
        // set the model and renderer for background color combo box
        this.bgColorSelectionComboBox.setModel(new javax.swing.DefaultComboBoxModel(MsxColor.values()));
        this.bgColorSelectionComboBox.setRenderer(new ListColorRenderer());
        
        // set the selected background color
        int bgColorCode = MsxColor.DARK_BLUE.getCode();
        this.bgColorSelectionComboBox.setSelectedIndex(bgColorCode);
        this.setScreenBackgroundColorCode(bgColorCode);
    }   
    
    /**
     * Setup buttons and combos listeners.
     */
    private void setupListeners() {
        
        this.spriteAttributesTable.getSelectionModel().addListSelectionListener(this);
        this.spriteAttributesTable.setDefaultEditor(Color.class, this.spriteColorEditor);
        this.selectBackgroundImageButton.addActionListener(this);
        this.bgColorSelectionComboBox.addActionListener(this);
        this.scaleComboBox.addActionListener(this);
        
        this.drawSpriteReferenceToggleButton.addActionListener(this);
        this.drawSpriteMagnifiedToggleButton.addActionListener(this);
        //this.selectScreenColorButton.addActionListener(this);
        //this.selectScreenBorderColorButton.addActionListener(this);
    }   
    
    /**
     * Set the sprite data object.
     * @param dataObject 
     */
    void setDataObject(SpritesDataObject dataObject) {
        
        // the sprite data object cannot be null
        assert dataObject != null;
        
        // set the sprites data object reference (it cannot be changed from here)
        //this.spritesDataObject = dataObject;
        
        // setup the attributes table model and default color editor
        this.attributesTableModel.setDataObject(dataObject);
        //this.spriteAttributesTable.setDefaultEditor(Color.class, this.spriteColorEditor);/////////////
        this.spriteAttributesTable.setModel(this.attributesTableModel);
        
        // add the draft panel to the draft scroll pane
        this.spritesViewPanel.setDataObject(dataObject);
        this.draftScrollPane.setViewportView(this.spritesViewPanel);
        this.draftScrollPane.setPreferredSize(this.spritesViewPanel.getSize());
        
        //this.attributesTableModel.initColumnSizes(this.spriteAttributesTable);
        TableColumnModel columnModel = this.spriteAttributesTable.getColumnModel();
        for (int i = 0; i < TABLE_COLUMN_WIDTH.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(TABLE_COLUMN_WIDTH[i]);
        }   
    }   
    
    /**
     * Update the attributes list.
     * @param attributesList 
     */
    void setAttributes(List<SpriteAttributes> attributesList) {
        this.attributesTableModel.setAttributes(attributesList);
        this.spritesViewPanel.setAttributes(attributesList);
    }
    
    /**
     * Set the current palette colors.
     * @param paletteColors 
     */
    void setPalette(java.util.List<Color> paletteColors) {
        this.spritesViewPanel.setPalette(paletteColors);
        this.attributesTableModel.setPalette(paletteColors);
        this.spriteColorEditor.setPalette(paletteColors);
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonsPanel = new javax.swing.JPanel();
        drawSpriteMagnifiedToggleButton = new javax.swing.JToggleButton();
        drawSpriteReferenceToggleButton = new javax.swing.JToggleButton();
        scaleComboBox = new javax.swing.JComboBox();
        separator1 = new javax.swing.JSeparator();
        bgColorSelectionComboBox = new javax.swing.JComboBox();
        selectBackgroundImageButton = new javax.swing.JButton();
        rightButtonsPanelFiller = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(8, 32767));
        draftSplitPane = new javax.swing.JSplitPane();
        draftScrollPane = new javax.swing.JScrollPane();
        attributesScrollPane = new javax.swing.JScrollPane();
        spriteAttributesTable = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(512, 212));
        setLayout(new java.awt.BorderLayout(4, 4));

        buttonsPanel.setMinimumSize(new java.awt.Dimension(202, 38));
        buttonsPanel.setPreferredSize(new java.awt.Dimension(782, 38));
        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 8));

        drawSpriteMagnifiedToggleButton.setIcon(I18N.getImageIcon("draw.sprites.magnified"));
        drawSpriteMagnifiedToggleButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.DRAW_SPRITES_MAGNIFIED.name());
        drawSpriteMagnifiedToggleButton.setPreferredSize(new java.awt.Dimension(24, 23));
        buttonsPanel.add(drawSpriteMagnifiedToggleButton);

        drawSpriteReferenceToggleButton.setIcon(I18N.getImageIcon("draw.sprite.reference"));
        drawSpriteReferenceToggleButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.DRAW_SPRITE_REFERENCE.name());
        drawSpriteReferenceToggleButton.setPreferredSize(new java.awt.Dimension(24, 23));
        buttonsPanel.add(drawSpriteReferenceToggleButton);

        scaleComboBox.setModel(new javax.swing.DefaultComboBoxModel(
            new String[] {
                I18N.getString("scale.factor", "1x"),
                I18N.getString("scale.factor", "2x"),
                I18N.getString("scale.factor", "4x"),
                I18N.getString("scale.factor", "8x")
            }
        ));
        scaleComboBox.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.CHANGE_VIEW_SCALE.name());
        scaleComboBox.setPreferredSize(new java.awt.Dimension(96, 21));
        buttonsPanel.add(scaleComboBox);

        separator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        separator1.setPreferredSize(new java.awt.Dimension(1, 19));
        buttonsPanel.add(separator1);

        bgColorSelectionComboBox.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.CHANGE_SCREEN_COLOR.name());
        bgColorSelectionComboBox.setPreferredSize(new java.awt.Dimension(96, 21));
        buttonsPanel.add(bgColorSelectionComboBox);

        org.openide.awt.Mnemonics.setLocalizedText(selectBackgroundImageButton, I18N.getString("select.background.image"));
        selectBackgroundImageButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.LOAD_DRAFT_IMAGE.name());
        selectBackgroundImageButton.setPreferredSize(new java.awt.Dimension(160, 23));
        buttonsPanel.add(selectBackgroundImageButton);
        buttonsPanel.add(rightButtonsPanelFiller);

        add(buttonsPanel, java.awt.BorderLayout.PAGE_END);

        draftSplitPane.setBorder(null);
        draftSplitPane.setDividerSize(4);
        draftSplitPane.setResizeWeight(0.8);
        draftSplitPane.setPreferredSize(new java.awt.Dimension(800, 320));

        draftScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), I18N.getString("draft.panel")));
        draftScrollPane.setDoubleBuffered(true);
        draftScrollPane.setMinimumSize(new java.awt.Dimension(256, 192));
        draftScrollPane.setPreferredSize(new java.awt.Dimension(512, 212));
        draftSplitPane.setLeftComponent(draftScrollPane);

        attributesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), I18N.getString("sprite.attributes.table")));
        attributesScrollPane.setMinimumSize(new java.awt.Dimension(256, 128));
        attributesScrollPane.setPreferredSize(new java.awt.Dimension(256, 412));

        spriteAttributesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        attributesScrollPane.setViewportView(spriteAttributesTable);

        draftSplitPane.setRightComponent(attributesScrollPane);

        add(draftSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane attributesScrollPane;
    private javax.swing.JComboBox bgColorSelectionComboBox;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JScrollPane draftScrollPane;
    private javax.swing.JSplitPane draftSplitPane;
    private javax.swing.JToggleButton drawSpriteMagnifiedToggleButton;
    private javax.swing.JToggleButton drawSpriteReferenceToggleButton;
    private javax.swing.Box.Filler rightButtonsPanelFiller;
    private javax.swing.JComboBox scaleComboBox;
    private javax.swing.JButton selectBackgroundImageButton;
    private javax.swing.JSeparator separator1;
    private javax.swing.JTable spriteAttributesTable;
    // End of variables declaration//GEN-END:variables
    
    public MsxSpriteViewPanel getDraftPanel() {
        return this.spritesViewPanel;
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        
        // the event action command
        String command = event.getActionCommand();
        SpriteEditorAction editorAction = SpriteEditorAction.valueOf(command);
        
        switch(editorAction) {
            
            case LOAD_DRAFT_IMAGE:
                this.selectBackgroundImageFile();
                break;
                
            case CHANGE_VIEW_SCALE:
                int index = this.scaleComboBox.getSelectedIndex();
                this.spritesViewPanel.setScale((int) Math.pow(2, index));
                this.draftScrollPane.setPreferredSize(this.spritesViewPanel.getSize());
                break;
                
            case DRAW_SPRITE_REFERENCE:
                this.spritesViewPanel.setDrawSpriteReference(this.drawSpriteReferenceToggleButton.isSelected());
                break;
                
            case DRAW_SPRITES_MAGNIFIED:
                this.spritesViewPanel.setDrawSpritesMagnified(this.drawSpriteMagnifiedToggleButton.isSelected());
                break;
                
            case CHANGE_SCREEN_COLOR:
                int colorCode = this.bgColorSelectionComboBox.getSelectedIndex();
                this.setScreenBackgroundColorCode(colorCode);
                break;
                
            case CHANGE_SCREEN_BORDER_COLOR:
                
                break;
        }   
    }  
    
    /**
     * Set the screen background color and update the combo box color view.
     * @param colorCode 
     */
    private void setScreenBackgroundColorCode(int colorCode) {
        
        Color screenColor = MsxColor.getMsx16Color(colorCode);
        
        // update visual elements
        this.bgColorSelectionComboBox.setForeground(screenColor);
        this.bgColorSelectionComboBox.setBackground(screenColor);
        this.spritesViewPanel.setScreenColor(screenColor);
    }
    
    /**
     * This method is called when a new selection is made (row selection in this case)
     * @param selectionEvent 
     */
    @Override
    public void valueChanged(ListSelectionEvent selectionEvent) {
        
        // get the current selected index of attributes table
        ListSelectionModel source = (ListSelectionModel) selectionEvent.getSource();
        int index = source.getLeadSelectionIndex();
        
        try {
            // set the current selected attributes in the sprite color chooser
            MsxSpriteAttributes attributes = this.attributesTableModel.getAttribute(index);
            this.spriteColorEditor.setAttributes(attributes);
        } catch(IndexOutOfBoundsException exception) {
            //this.spriteColorEditor.setSpriteAttributes(null);
        }
    }
    
    /**
     * 
     * @return 
     */
    private void selectBackgroundImageFile() {
        
        // set up the file chooser
        if(this.imageFileChooser == null) {
            this.imageFileChooser = new javax.swing.JFileChooser();

//            //Add a custom file filter and disable the default
//            //(Accept All) file filter.
//            this.imageFileChooser.addChoosableFileFilter(new ImageFilter());
//            this.imageFileChooser.setAcceptAllFileFilterUsed(false);
//
//            //Add custom icons for file types.
//            this.imageFileChooser.setFileView(new ImageFileView());
//
//            //Add the preview pane.
//            this.imageFileChooser.setAccessory(new ImagePreview(fc));
        }
        
        // show the file chooser dialog
        Integer userOption = this.imageFileChooser.showDialog(this, I18N.getString("select"));
        
        // process the results
        if (userOption.equals(javax.swing.JFileChooser.APPROVE_OPTION)) {
            java.io.File imageFile = this.imageFileChooser.getSelectedFile();
            java.awt.Image backgroundImage = ImageUtil.loadImage(this, imageFile);
            this.spritesViewPanel.setBackgroundImage(backgroundImage);
        }   
        
        // reset the file chooser for the next time it's shown
        this.imageFileChooser.setSelectedFile(null);
    }
    
    /**
     * Color cell renderer used to render colors in the attributes table.
     */
    class CellColorRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        
        private Border unselectedBorder = null;
        private Border selectedBorder = null;
        
        public CellColorRenderer() {
            this.setOpaque(true); // must do this for background to show up
        }
        
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object color,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            
            Color newColor = (Color) color;
            this.setBackground(newColor);
            
            if (isSelected) {
                this.setBorder(this.getSelectedBorder(table));
            } else {
                this.setBorder(this.getUnselectedBorder(table));
            }
            
            this.setToolTipText("RGB: " + newColor.getRed() + ", "
                    + newColor.getGreen() + ", "
                    + newColor.getBlue());
            
            return this;
        }
        
        private Border getSelectedBorder(JTable table) {
            if (this.selectedBorder == null) {
                this.selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
            }
            return this.selectedBorder;
        }
        
        private Border getUnselectedBorder(JTable table) {
            if (this.unselectedBorder == null) {
                this.unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
            }
            return this.unselectedBorder;
        }
    }
    
    /**
     * Using JLabel as the base class.
     * Other option would be "extends javax.swing.DefaultListCellRenderer", but in this
     * case it is not showing the foreground and background colors in the combo box.
     */
    private class ListColorRenderer extends JLabel implements javax.swing.ListCellRenderer<Object> {
        
        public ListColorRenderer() {            
            this.setOpaque(true);            
        }   
        
        @Override
        public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
            this.setText(value.toString());
            if(index >= 0 && index < MsxColor.MSX_PALETTE_SIZE) {
                Color selectedColor = MsxColor.getMsx16Color(index);
                this.setForeground(selectedColor);
                this.setBackground(selectedColor);
            }   
            return this;
        }
    }
}
