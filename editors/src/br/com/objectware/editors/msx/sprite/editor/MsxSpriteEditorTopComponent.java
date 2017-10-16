/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.editors.sprite.SpritesDataObject;
import br.com.objectware.editors.msx.sprite.dialogs.ColorCodeSelectionDialog;
import br.com.objectware.editors.enums.SpriteEditorAction;
import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.DialogUtil;
import br.com.objectware.commons.utils.TextUtil;
import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.factories.SpriteRendererFactory;
import br.com.objectware.domain.msx.sprite.MsxSprite;
import br.com.objectware.domain.msx.sprite.MsxSpriteAttributes;
import br.com.objectware.domain.msx.sprite.MsxSpritePattern;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.PatternUtils;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.domain.sprite.SpriteRenderer;
import br.com.objectware.editors.cookies.SaveDataObjectCookie;
import br.com.objectware.editors.edit.AddRemoveSpriteUndoableEdit;
import br.com.objectware.editors.edit.PatternShiftUndoableEdit;
import br.com.objectware.editors.edit.SpriteAttributesUndoableEdit;
import br.com.objectware.editors.edit.SpriteLabelUndoableEdit;
import br.com.objectware.editors.edit.SpritePatternUndoableEdit;
import br.com.objectware.editors.export.SpriteExporterDialog;
import br.com.objectware.editors.msx.sprite.dialogs.PaletteColorSelectionDialog;
import br.com.objectware.editors.sprite.SpriteEditorTopComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * The sprite editor main panel. Holds all visual elements of the editor (pattern editor, tools panel and sprites)
 * 
 * @author Luciano M. Christofoletti
 * @since 22/Jul/2015
 * 
 * Useful docs:
 *     http://docs.oracle.com/javase/tutorial/uiswing/components/table.html
 *     https://docs.oracle.com/javase/tutorial/uiswing/events/tablemodellistener.html
 *     http://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#SimpleTableDemo
 *     http://nadeausoftware.com/articles/2010/12/java_tip_how_use_systemcolors_access_os_user_interface_theme_colors
 *     http://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
 * 
 * additional reading:
 *     http://www.drdobbs.com/jvm/a-sound-file-editor-for-netbeans/184405998
 *     https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html (message dialog)
 *     https://platform.netbeans.org/tutorials/nbm-propertyeditors-integration.html (custom editor)
 */

/**
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * 
 * IMPORTANT: this component must not be registered (using annotations) beacuse it must
 * not appear in the window menu of the IDE.
 */
public final class MsxSpriteEditorTopComponent
            extends TopComponent
            implements SpriteEditorTopComponent, PropertyChangeListener, FileChangeListener, MultiViewElement,
                       java.awt.event.ActionListener, java.util.Observer {
    
    private static final long serialVersionUID = 8833884811122433325L;
    
    // the sprite file path key (used when persisting/restoring the active windows)
    //private static final String KEY_FILE_PATH = "sprite.file.path";
    private static final byte DEFAULT_FG_COLOR_CODE = MsxColor.WHITE.getCode();
    
    /** The sprites data object being edited */
    private SpritesDataObject spritesDataObject;
    
    /** The icon showed in the tab (to the left of the file name) */
    private java.awt.Image tabIcon;
    
    /** The sprite renderer */
    private SpriteRenderer spriteRenderer;
    
    /** The sprite exporter dialog */
    private SpriteExporterDialog spriteExporterDialog;
    
    /** The pattern (view) editor */
    private final MsxPatternEditorPanel patternEditor = new MsxPatternEditorPanel();
    
    /** The pattern viewer (it is a button...) */
    private final PatternViewerButton patternViewerButton = new PatternViewerButton();
    
    /** The draft panel for sprite attributes editing */
    private final DraftComponentPanel draftComponentPanel = new DraftComponentPanel();
    
    /** Buttons map: needed to facilitate the removal of the button from buttons panel */
    private final java.util.Map<Sprite, SpriteButton> buttonsMap = new java.util.HashMap<>();
    
    /** Not used (yet) */
    private transient MultiViewElementCallback callback;
    
    /**
     * The MSX sprite editor. Works for sprites mode 1 and 2.
     */
    private MsxSpriteEditorTopComponent() {
        
        // init visual elements properties
        this.initComponents();
        
        // add the custom visual components
        this.editorPanel.add(this.patternEditor); // the sprite pattern editor
        this.patternViewerPanel.add(this.patternViewerButton); // the sprite pattern viewer
        this.bottomPanel.add(this.draftComponentPanel); // the sprite attributes editor draft panel
        
        // initialize the mouse selection mode and setup visual elements listeners
        this.setMouseSelectionMode();
        this.setupListeners();
    }   
    
    /**
     * This constructor is called by OpenSpriteCookie only.
     * @param lookup the data object lookup
     */
    public MsxSpriteEditorTopComponent(org.openide.util.Lookup lookup) {
        
        this();
        
        // set the sprite data object being edited
        this.setDataObject(lookup.lookup(SpritesDataObject.class));
    }   
    
    /**
     * Set the data object element and initializes all other components that depends on it.
     * @param dataObject the sprites data object
     */
    private void setDataObject(final SpritesDataObject dataObject) {
        
        // the sprite data object cannot be null
        assert dataObject != null;
        this.spritesDataObject = dataObject;
        
        // make the sprite data object associations
        dataObject.addPropertyChangeListener(this);
        this.associateLookup(Lookups.fixed(this, dataObject, new SpriteTypeLookupHint()));
        
        // update the tab visual component file name
        this.setDisplayName(dataObject.getPrimaryFile().getNameExt());
        
        // setup the color sprite pattern renderer
        this.spriteRenderer = SpriteRendererFactory.newSpriteRenderer(dataObject.getSpriteFormat());
        
        // setup the sprite viewer component
        this.patternViewerButton.setSpriteRenderer(this.spriteRenderer);
        
        // set the sprites data object reference (it cannot be changed from here)
        this.patternEditor.setDataObject(dataObject); // needs the data object for update and undo/redo purposes
        this.draftComponentPanel.setDataObject(dataObject);
        
        // set the palette and the high contrast view initial status
        this.setPalette(dataObject.getPalette());
        this.setViewMode();
    }   
    
    /**
     * Setup all visual elements listeners.
     */
    private void setupListeners() {
        
        // setup tools bar buttons listeners
        this.exportButton.addActionListener(this);
        this.mouseSelectionToggleButton.addActionListener(this);
        this.highContrastModeToggleButton.addActionListener(this);
        this.editPaletteButton.addActionListener(this);
        
        // pattern view listeners
        this.patternMagnifierButton.addActionListener(this);
        this.foregroundColorSelectionButton.addActionListener(this);
        this.backgroundColorSelectionButton.addActionListener(this);
        
        // setup editor tools buttons listeners
        this.mirrorUpDownButton.addActionListener(this);
        this.mirrorLeftRightButton.addActionListener(this);
        this.togglePaintModeButton.addActionListener(this);
        this.flipHorizontalButton.addActionListener(this);
        this.flipVerticalButton.addActionListener(this);
        this.invertButton.addActionListener(this);
        this.shiftRightButton.addActionListener(this);
        this.shiftLeftButton.addActionListener(this);
        this.shiftUpButton.addActionListener(this);
        this.shiftDownButton.addActionListener(this);
        this.rotateButton.addActionListener(this);
        this.clearButton.addActionListener(this);
        
        // setup the sprite pattern set buttons listeners
        this.addPattern.addActionListener(this);
        this.removePattern.addActionListener(this);
        this.movePatternUp.addActionListener(this);
        this.movePatternDown.addActionListener(this);
    }   
    
    /**
     * Get the list of mixed sprites in the pattern editor
     * @return 
     */
    private java.util.List<MsxSprite> getMixedSprites() {
        return this.patternEditor.getSpriteMixer().getSprites();
    }   
    
    /**
     * Get the list of sprites from data object
     * @return 
     */
    private java.util.List<Sprite> getSprites() {
        return this.spritesDataObject.getSprites();
    }   
    
    /**
     * Set the sprites list.
     * @param sprites the sprites list
     */
    private void setSprites(java.util.List<Sprite> sprites) {
        
        // clean up the sprites list
        this.removeAllSpriteButtons();
        
        // add all sprites from list to the sprites panel
        sprites.stream().forEach((sprite) -> {
            this.addSpriteButton((MsxSprite) sprite);
        });   
        
        // set the first sprite as the current sprite being edited
        this.patternEditor.resetBackground();
        
        // set the first sprite of the list as the current selected sprite
        if(!sprites.isEmpty()) {
            this.setSelectedSprite((MsxSprite) sprites.get(0));
        }   
    }   
    
    /**
     * Get the list of sprite attributes from data object
     * @return 
     */
    private java.util.List<SpriteAttributes> getAttributes() {
        return this.spritesDataObject.getAttributes();
    }   
    
    /**
     * Set the sprite attributes
     * @param attributes the sprite attributes list
     */
    private void setAttributes(java.util.List<SpriteAttributes> attributes) {
        this.draftComponentPanel.setAttributes(attributes);
    }   
    
    /**
     * Return the list of colors of current palette
     * @return 
     */
    private java.util.List<java.awt.Color> getPalette() {
        return this.spritesDataObject.getPalette();
    }
    
    /**
     * Propagate the new palette colors to other components.
     * @param paletteColors the new palette colors
     */
    private void setPalette(java.util.List<java.awt.Color> paletteColors) {
        
        // set the new palette for all components that use it
        this.spriteRenderer.setPalette(paletteColors);
        this.patternEditor.setPalette(paletteColors);
        this.draftComponentPanel.setPalette(paletteColors);
        
        // update view
        this.spritesPanel.updateUI();
        this.updatePatternViewerImage();
    }   
    
    /**
     * Get the sprite button for the given sprite.
     * @param sprite the sprite
     * @return 
     */
    private SpriteButton getSpriteButton(Sprite sprite) {
        return this.buttonsMap.get(sprite);
    }   
    
    /**
     * Add a new (visual) sprite button to the sprites panel.
     * @param sprite 
     */
    private void addSpriteButton(Sprite sprite) {
        
        // ensure that there is a selected sprite
        if(sprite != null) {
            
            // create the sprite "view" button and setup the action listener for it
            SpriteButton spriteViewButton = new SpriteButton(sprite, this.spriteRenderer); // #0
            spriteViewButton.updateUI();
            spriteViewButton.addActionListener(this); // #1
            
            // link the sprite and sprite view button
            this.buttonsMap.put(sprite, spriteViewButton); // #2
            
            // the button must be notified if some change occurs in some palette color
            this.spritesPanel.add(spriteViewButton); // #3
            this.spritesPanel.updateUI();
        }   
    }   
    
    /**
     * Remove a sprite button from sprites panel (and all visual resources created in the above method).
     * @param sprite 
     */
    private void removeSpriteButton(Sprite sprite) {
        
        // ensure that there is a selected sprite
        if(sprite != null) {
            
            // get the sprite "view" button and remove the action listener from it
            SpriteButton spriteViewButton = this.getSpriteButton(sprite);
            if(spriteViewButton != null) {
                spriteViewButton.freeResources(); // ~#0
                spriteViewButton.removeActionListener(this); // ~#1
                this.spritesPanel.remove(spriteViewButton); // ~#3
            }   
            
            // remove sprite button from buttons map
            this.buttonsMap.remove(sprite); // ~#2
            this.spritesPanel.updateUI();
        }
    }   
    
    /**
     * Removes all sprite buttons.
     */
    private void removeAllSpriteButtons() {
        // create a new list with the buttons map keys to avoid concurrent modification exceptions
        java.util.List<Sprite> buttonsList = new java.util.ArrayList<>(this.buttonsMap.keySet());
        buttonsList.stream().forEach((sprite) -> {
            this.removeSpriteButton((MsxSprite) sprite);
        });   
    }
    
    /**
     * Return the data object availability status.
     * @return 
     */
    private boolean isDataObjectAvailable() {
        return (this.spritesDataObject != null);
    }
    
    /**
     * Return the associated data object.
     * @return 
     */
    @Override
    public SpritesDataObject getDataObject() {
        return this.spritesDataObject;
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBarPanel = new javax.swing.JPanel();
        separator1 = new javax.swing.JSeparator();
        exportButton = new javax.swing.JButton();
        mouseSelectionToggleButton = new javax.swing.JToggleButton();
        highContrastModeToggleButton = new javax.swing.JToggleButton();
        separator2 = new javax.swing.JSeparator();
        editPaletteButton = new javax.swing.JButton();
        separator3 = new javax.swing.JSeparator();
        patternNumberLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        upperPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        editorPanel = new javax.swing.JPanel();
        patternToolsPanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(6, 80), new java.awt.Dimension(6, 32767));
        patternViewerPanel = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(92, 12), new java.awt.Dimension(32767, 4));
        patternMagnifierButton = new javax.swing.JButton();
        foregroundColorSelectionButton = new javax.swing.JButton();
        backgroundColorSelectionButton = new javax.swing.JButton();
        mirrorUpDownButton = new javax.swing.JButton();
        mirrorLeftRightButton = new javax.swing.JButton();
        togglePaintModeButton = new javax.swing.JToggleButton();
        flipHorizontalButton = new javax.swing.JButton();
        shiftUpButton = new javax.swing.JButton();
        flipVerticalButton = new javax.swing.JButton();
        shiftLeftButton = new javax.swing.JButton();
        rotateButton = new javax.swing.JButton();
        shiftRightButton = new javax.swing.JButton();
        invertButton = new javax.swing.JButton();
        shiftDownButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        rightPanel = new javax.swing.JPanel();
        spritesScrollPane = new javax.swing.JScrollPane();
        spritesPanel = new javax.swing.JPanel();
        spritesControlPanel = new javax.swing.JPanel();
        addPattern = new javax.swing.JButton();
        removePattern = new javax.swing.JButton();
        movePatternUp = new javax.swing.JButton();
        movePatternDown = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        bottomPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(620, 164));
        setPreferredSize(new java.awt.Dimension(854, 256));
        setLayout(new java.awt.BorderLayout(2, 2));

        toolBarPanel.setMinimumSize(new java.awt.Dimension(256, 18));
        toolBarPanel.setPreferredSize(new java.awt.Dimension(128, 26));
        toolBarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 2));

        separator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        separator1.setPreferredSize(new java.awt.Dimension(1, 19));
        toolBarPanel.add(separator1);

        exportButton.setIcon(I18N.getImageIcon("export.binary.icon"));
        exportButton.setToolTipText(I18N.getString("export.sprites"));
        exportButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.EXPORT_PATTERNS.name());
        exportButton.setBorder(null);
        exportButton.setFocusPainted(false);
        exportButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(exportButton);

        mouseSelectionToggleButton.setIcon(I18N.getImageIcon("toggle.editor.mode"));
        mouseSelectionToggleButton.setToolTipText(I18N.getString("toggle.mouse.selection.mode"));
        mouseSelectionToggleButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.TOGGLE_MOUSE_ACTION.name());
        mouseSelectionToggleButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(mouseSelectionToggleButton);

        highContrastModeToggleButton.setIcon(I18N.getImageIcon("toggle.view.icon"));
        highContrastModeToggleButton.setToolTipText(I18N.getString("toggle.pattern.color.view"));
        highContrastModeToggleButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.TOGGLE_COLOR_MODE_VIEW.name());
        highContrastModeToggleButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(highContrastModeToggleButton);

        separator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        separator2.setPreferredSize(new java.awt.Dimension(1, 19));
        toolBarPanel.add(separator2);

        editPaletteButton.setIcon(I18N.getImageIcon("edit.palette.icon"));
        editPaletteButton.setToolTipText(I18N.getString("edit.palette"));
        editPaletteButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.EDIT_PALETTE.name());
        editPaletteButton.setBorder(null);
        editPaletteButton.setFocusPainted(false);
        editPaletteButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(editPaletteButton);

        separator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        separator3.setPreferredSize(new java.awt.Dimension(1, 19));
        toolBarPanel.add(separator3);

        org.openide.awt.Mnemonics.setLocalizedText(patternNumberLabel, "pattern:"); // NOI18N
        toolBarPanel.add(patternNumberLabel);

        add(toolBarPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setPreferredSize(new java.awt.Dimension(854, 320));
        mainPanel.setLayout(new java.awt.BorderLayout(4, 4));

        upperPanel.setPreferredSize(new java.awt.Dimension(800, 296));
        upperPanel.setVerifyInputWhenFocusTarget(false);
        upperPanel.setLayout(new java.awt.BorderLayout(4, 2));

        leftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), I18N.getString("pattern.editor")));
        leftPanel.setMinimumSize(new java.awt.Dimension(496, 260));
        leftPanel.setPreferredSize(new java.awt.Dimension(408, 244));
        leftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        editorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        editorPanel.setMinimumSize(new java.awt.Dimension(272, 276));
        editorPanel.setPreferredSize(new java.awt.Dimension(272, 260));
        editorPanel.setLayout(new java.awt.BorderLayout());
        leftPanel.add(editorPanel);

        patternToolsPanel.setPreferredSize(new java.awt.Dimension(104, 264));
        java.awt.FlowLayout flowLayout2 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2);
        flowLayout2.setAlignOnBaseline(true);
        patternToolsPanel.setLayout(flowLayout2);
        patternToolsPanel.add(filler1);

        patternViewerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        patternViewerPanel.setPreferredSize(new java.awt.Dimension(85, 85));
        patternViewerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        patternToolsPanel.add(patternViewerPanel);
        patternToolsPanel.add(filler2);

        patternMagnifierButton.setIcon(I18N.getImageIcon("pattern.magnifier.icon"));
        patternMagnifierButton.setToolTipText(I18N.getString("change.pattern.view.scale"));
        patternMagnifierButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.CHANGE_SPRITE_VIEW_SCALE.name());
        patternMagnifierButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternMagnifierButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(patternMagnifierButton);

        foregroundColorSelectionButton.setIcon(I18N.getImageIcon("change.pattern.fg.color.icon")
        );
        foregroundColorSelectionButton.setToolTipText(I18N.getString("select.pattern.view.foreground.color"));
        foregroundColorSelectionButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.CHANGE_SPRITE_COLOR.name());
        foregroundColorSelectionButton.setPreferredSize(new java.awt.Dimension(32, 30));
        foregroundColorSelectionButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(foregroundColorSelectionButton);

        backgroundColorSelectionButton.setIcon(I18N.getImageIcon("change.pattern.bg.color.icon"));
        backgroundColorSelectionButton.setToolTipText(I18N.getString("select.pattern.view.background.color"));
        backgroundColorSelectionButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.CHANGE_SPRITE_BG_COLOR.name());
        backgroundColorSelectionButton.setPreferredSize(new java.awt.Dimension(32, 30));
        backgroundColorSelectionButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(backgroundColorSelectionButton);

        mirrorUpDownButton.setIcon(I18N.getImageIcon("mirror.pattern.up.down.icon"));
        mirrorUpDownButton.setToolTipText(I18N.getString("mirror.pattern.up.down"));
        mirrorUpDownButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.MIRROR_UP_DOWN.name());
        mirrorUpDownButton.setFocusable(false);
        mirrorUpDownButton.setPreferredSize(new java.awt.Dimension(32, 30));
        mirrorUpDownButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(mirrorUpDownButton);

        mirrorLeftRightButton.setIcon(I18N.getImageIcon("mirror.pattern.left.right.icon"));
        mirrorLeftRightButton.setToolTipText(I18N.getString("mirror.pattern.left.right"));
        mirrorLeftRightButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.MIRROR_LEFT_RIGHT.name());
        mirrorLeftRightButton.setFocusable(false);
        mirrorLeftRightButton.setPreferredSize(new java.awt.Dimension(32, 30));
        mirrorLeftRightButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(mirrorLeftRightButton);

        togglePaintModeButton.setIcon(I18N.getImageIcon("paint.icon"));
        togglePaintModeButton.setToolTipText(I18N.getString("toggle.paint.mode"));
        togglePaintModeButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.TOGGLE_PAINT_MODE.name());
        togglePaintModeButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternToolsPanel.add(togglePaintModeButton);

        flipHorizontalButton.setIcon(I18N.getImageIcon("flip.pattern.horizontally.icon"));
        flipHorizontalButton.setToolTipText(I18N.getString("flip.pattern.horizontally"));
        flipHorizontalButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.FLIP_HORIZONTAL.name());
        flipHorizontalButton.setFocusable(false);
        flipHorizontalButton.setPreferredSize(new java.awt.Dimension(32, 30));
        flipHorizontalButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(flipHorizontalButton);

        shiftUpButton.setIcon(I18N.getImageIcon("move.pattern.up.icon"));
        shiftUpButton.setToolTipText(I18N.getString("shift.pattern.up"));
        shiftUpButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.SHIFT_UP.name());
        shiftUpButton.setFocusable(false);
        shiftUpButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternToolsPanel.add(shiftUpButton);

        flipVerticalButton.setIcon(I18N.getImageIcon("flip.pattern.vertically.icon"));
        flipVerticalButton.setToolTipText(I18N.getString("flip.pattern.vertically"));
        flipVerticalButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.FLIP_VERTICAL.name());
        flipVerticalButton.setFocusable(false);
        flipVerticalButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternToolsPanel.add(flipVerticalButton);

        shiftLeftButton.setIcon(I18N.getImageIcon("move.pattern.left.icon"));
        shiftLeftButton.setToolTipText(I18N.getString("shift.pattern.left"));
        shiftLeftButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.SHIFT_LEFT.name());
        shiftLeftButton.setFocusable(false);
        shiftLeftButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternToolsPanel.add(shiftLeftButton);

        rotateButton.setIcon(I18N.getImageIcon("rotate.pattern.clockwise.icon"));
        rotateButton.setToolTipText(I18N.getString("rotate.pattern.clockwise"));
        rotateButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.ROTATE.name());
        rotateButton.setFocusable(false);
        rotateButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternToolsPanel.add(rotateButton);

        shiftRightButton.setIcon(I18N.getImageIcon("move.pattern.right.icon"));
        shiftRightButton.setToolTipText(I18N.getString("shift.pattern.right"));
        shiftRightButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.SHIFT_RIGHT.name());
        shiftRightButton.setFocusable(false);
        shiftRightButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternToolsPanel.add(shiftRightButton);

        invertButton.setIcon(I18N.getImageIcon("invert.pixels.from.pattern.icon"));
        invertButton.setToolTipText(I18N.getString("invert.pixels.status.on.off"));
        invertButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.INVERT.name());
        invertButton.setFocusable(false);
        invertButton.setPreferredSize(new java.awt.Dimension(32, 30));
        invertButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(invertButton);

        shiftDownButton.setIcon(I18N.getImageIcon("move.pattern.down.icon"));
        shiftDownButton.setToolTipText(I18N.getString("shift.pattern.down"));
        shiftDownButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.SHIFT_DOWN.name());
        shiftDownButton.setFocusable(false);
        shiftDownButton.setPreferredSize(new java.awt.Dimension(32, 30));
        patternToolsPanel.add(shiftDownButton);

        clearButton.setIcon(I18N.getImageIcon("clear.pattern.icon"));
        clearButton.setToolTipText(I18N.getString("clear.pattern"));
        clearButton.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.CLEAR_SPRITE.name());
        clearButton.setFocusable(false);
        clearButton.setPreferredSize(new java.awt.Dimension(32, 30));
        clearButton.setRequestFocusEnabled(false);
        patternToolsPanel.add(clearButton);

        leftPanel.add(patternToolsPanel);

        upperPanel.add(leftPanel, java.awt.BorderLayout.LINE_START);

        rightPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), I18N.getString("pattern.set")));
        rightPanel.setPreferredSize(new java.awt.Dimension(128, 22));
        rightPanel.setLayout(new java.awt.BorderLayout(4, 4));

        spritesScrollPane.setBorder(null);
        spritesScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spritesScrollPane.setDoubleBuffered(true);
        spritesScrollPane.setPreferredSize(new java.awt.Dimension(100, 226));

        spritesPanel.setPreferredSize(new java.awt.Dimension(80, 680));
        spritesPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));
        spritesScrollPane.setViewportView(spritesPanel);

        rightPanel.add(spritesScrollPane, java.awt.BorderLayout.CENTER);

        spritesControlPanel.setPreferredSize(new java.awt.Dimension(128, 44));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 2, 6);
        flowLayout1.setAlignOnBaseline(true);
        spritesControlPanel.setLayout(flowLayout1);

        addPattern.setIcon(I18N.getImageIcon("add.pattern.icon"));
        addPattern.setToolTipText(I18N.getString("add.new.sprite.pattern"));
        addPattern.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.ADD_SPRITE.name());
        addPattern.setFocusable(false);
        addPattern.setPreferredSize(new java.awt.Dimension(32, 30));
        spritesControlPanel.add(addPattern);

        removePattern.setIcon(I18N.getImageIcon("remove.pattern.icon"));
        removePattern.setToolTipText(I18N.getString("remove.selected.sprite.pattern"));
        removePattern.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.REMOVE_SPRITE.name());
        removePattern.setFocusable(false);
        removePattern.setPreferredSize(new java.awt.Dimension(32, 30));
        spritesControlPanel.add(removePattern);

        movePatternUp.setIcon(I18N.getImageIcon("move.pattern.left.icon"));
        movePatternUp.setToolTipText(I18N.getString("move.selected.pattern.up"));
        movePatternUp.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.SHIFT_PATTERN_NUMBER_UP.name());
        movePatternUp.setFocusable(false);
        movePatternUp.setPreferredSize(new java.awt.Dimension(32, 30));
        spritesControlPanel.add(movePatternUp);

        movePatternDown.setIcon(I18N.getImageIcon("move.pattern.right.icon"));
        movePatternDown.setToolTipText(I18N.getString("move.selected.pattern.down"));
        movePatternDown.setActionCommand(br.com.objectware.editors.enums.SpriteEditorAction.SHIFT_PATTERN_NUMBER_DOWN.name());
        movePatternDown.setFocusable(false);
        movePatternDown.setPreferredSize(new java.awt.Dimension(32, 30));
        spritesControlPanel.add(movePatternDown);
        spritesControlPanel.add(filler3);

        rightPanel.add(spritesControlPanel, java.awt.BorderLayout.SOUTH);

        upperPanel.add(rightPanel, java.awt.BorderLayout.CENTER);

        mainPanel.add(upperPanel, java.awt.BorderLayout.NORTH);

        bottomPanel.setPreferredSize(new java.awt.Dimension(784, 212));
        bottomPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.add(bottomPanel, java.awt.BorderLayout.CENTER);

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPattern;
    private javax.swing.JButton backgroundColorSelectionButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton editPaletteButton;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JButton exportButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton flipHorizontalButton;
    private javax.swing.JButton flipVerticalButton;
    private javax.swing.JButton foregroundColorSelectionButton;
    private javax.swing.JToggleButton highContrastModeToggleButton;
    private javax.swing.JButton invertButton;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton mirrorLeftRightButton;
    private javax.swing.JButton mirrorUpDownButton;
    private javax.swing.JToggleButton mouseSelectionToggleButton;
    private javax.swing.JButton movePatternDown;
    private javax.swing.JButton movePatternUp;
    private javax.swing.JButton patternMagnifierButton;
    private javax.swing.JLabel patternNumberLabel;
    private javax.swing.JPanel patternToolsPanel;
    private javax.swing.JPanel patternViewerPanel;
    private javax.swing.JButton removePattern;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton rotateButton;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JSeparator separator3;
    private javax.swing.JButton shiftDownButton;
    private javax.swing.JButton shiftLeftButton;
    private javax.swing.JButton shiftRightButton;
    private javax.swing.JButton shiftUpButton;
    private javax.swing.JPanel spritesControlPanel;
    private javax.swing.JPanel spritesPanel;
    private javax.swing.JScrollPane spritesScrollPane;
    private javax.swing.JToggleButton togglePaintModeButton;
    private javax.swing.JPanel toolBarPanel;
    private javax.swing.JPanel upperPanel;
    // End of variables declaration//GEN-END:variables
    
    // --------------------- TopComponent override
    @Override
    protected String preferredID() {
        return "sprite.editor";
    }
    
    // --------------------- TopComponent override
    @Override
    public void open() {
        super.open();
        this.updateVisualComponents();
    }
    
    // --------------------- TopComponent override
    @Override
    public String getName() {
        return this.spritesDataObject.getName();
    }
    
    // --------------------- TopComponent override
    @Override
    public java.awt.Image getIcon() {
        if(this.tabIcon == null) {
            this.tabIcon = I18N.getImage("sprite.file.tree.icon");
        }
        return this.tabIcon;
    }
    
    // --------------------- TopComponent override
    @Override
    public String getHtmlDisplayName() {
        DataObject dataObject = getLookup().lookup(DataObject.class);
        if(dataObject != null && dataObject.isModified()) {
            String spritesFileName = dataObject.getPrimaryFile().getNameExt();
            return "<html><b>" + spritesFileName + "</b></html>";
        }
        return super.getHtmlDisplayName();
    }   
    
    // --------------------- TopComponent override
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public javax.swing.JComponent getToolbarRepresentation() {
        return this.toolBarPanel;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public javax.swing.JComponent getVisualRepresentation() {
        return this;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public javax.swing.Action[] getActions() {
        return new javax.swing.Action[0];
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public void componentOpened() {
        // the sprites data object should never be null!!!
        if(this.isDataObjectAvailable()) {
            this.spritesDataObject.getPrimaryFile().addFileChangeListener(this);
        }
    }   
    
    // --------------------- MultiViewElement implementation
    @Override
    public void componentClosed() {
        
        // the binary object should never be null!!!
        if(this.isDataObjectAvailable()) {
            this.spritesDataObject.getPrimaryFile().removeFileChangeListener(this);
        }
        
        // remove observer from current sprite
        this.getSelectedSprite().deleteObserver(this);
    }
    
    // --------------------- MultiViewElement implementation
    @Override public void componentShowing() {}
    @Override public void componentHidden() {}
    @Override public void componentDeactivated() {}
    
    @Override
    public void componentActivated() {
        Sprite sprite = this.getSelectedSprite();
        if(this.buttonsMap.containsKey(sprite)) {
            this.getSpriteButton(sprite).requestFocus();
        }   
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public UndoRedo getUndoRedo() {
        return this.spritesDataObject.getUndoRedoManager();
    }   
    
    // --------------------- MultiViewElement implementation
    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }   
    
    /**
     * Fire an action event to this editor.
     * @param spriteEditorAction 
     */
    @Override
    public void fireAction(SpriteEditorAction spriteEditorAction) {
        if(spriteEditorAction != null) {
            this.actionPerformed(
                new java.awt.event.ActionEvent(
                    this, java.awt.event.ActionEvent.ACTION_PERFORMED, spriteEditorAction.name()
                )
            );
        }   
    }   
    
    // --------------------- ActionListener implementation
    @Override
    public void actionPerformed(java.awt.event.ActionEvent event) {
        
        // the event properties
        Object source = event.getSource();
        String command = event.getActionCommand();
        int modifiers = event.getModifiers();
        
        // the action command must be a valid SpriteEditorAction
        SpriteEditorAction editorAction = SpriteEditorAction.valueOf(command);
        MsxSprite sprite = this.getSelectedSprite();
        MsxSpritePattern pattern = sprite.getPattern();
        java.util.List<Sprite> sprites = this.getSprites();
        
        switch(editorAction) {
            
            case UPDATE_VIEW:
                
                // set/reset the panel editor background
                if((modifiers & java.awt.Event.SHIFT_MASK) == java.awt.Event.SHIFT_MASK) {
                    this.patternEditor.addCurrentSpriteToBackground();
                } else if((modifiers & java.awt.Event.CTRL_MASK) != java.awt.Event.CTRL_MASK) {
                    this.patternEditor.resetBackground();
                }   
                
                // inform the pattern model the sprite being edited
                if(source instanceof SpriteButton) {
                    SpriteButton spriteButton = (SpriteButton) source;
                    this.setSelectedSprite((MsxSprite) spriteButton.getSprite());
                }
                
                break;
                
            case ADD_SPRITE:
                if(this.addNewSprite()) {
                    // add the undo/redo for insert sprite action (note the use of this.getSelectedSprite())
                    this.addUndoRedo(editorAction, this.getSelectedSprite());
                }
                break;
                
            case INSERT_SPRITE:
                if(this.insertNewSprite()) {
                    // add the undo/redo for the new sprite (note the use of this.getSelectedSprite())
                    this.addUndoRedo(editorAction, this.getSelectedSprite());
                }
                break;
                
            case REMOVE_SPRITE:
                if(this.removeSprite(sprite)) {
                    // add the undo/redo for sprite removed (the sprite element references the removed sprite)
                    this.addUndoRedo(editorAction, sprite);
                }
                break;
                
            case SHIFT_PATTERN_NUMBER_DOWN:
                if(sprite.getIndex() + 1 < sprites.size()) {
                    this.addUndoRedo(SpriteEditorAction.SHIFT_PATTERN_NUMBER_DOWN, sprite);
                    this.moveSpriteDown(sprite);
                }   
                break;
                
            case SHIFT_PATTERN_NUMBER_UP:
                if(sprite.getIndex() > 0) {
                    this.addUndoRedo(SpriteEditorAction.SHIFT_PATTERN_NUMBER_UP, sprite);
                    this.moveSpriteUp(sprite);
                }   
                break;
                
            case CHANGE_SPRITE_COLOR:
                this.changeSpriteColor(sprite);
                break;
                
            case CHANGE_SPRITE_BG_COLOR:
                this.changePatternViewerBackgroundColor(sprite);
                break;
                
            case CHANGE_SPRITE_LABEL:
                this.changeSpriteLabel(sprite);
                break;
                
            case EDIT_PALETTE:
                this.editPaletteColors(); // TODO: add undo/redo support...
                break;
                
            case CLEAR_SPRITE:
                this.addPatternUndoRedo();
                PatternUtils.clear(pattern);
                break;
                
            case SHIFT_RIGHT:
                this.addPatternUndoRedo();
                PatternUtils.shiftRight(pattern);
                break;
                
            case SHIFT_LEFT:
                this.addPatternUndoRedo();
                PatternUtils.shiftLeft(pattern);
                break;
                
            case SHIFT_UP:
                this.addPatternUndoRedo();
                PatternUtils.shiftUp(pattern);
                break;
                
            case SHIFT_DOWN:
                this.addPatternUndoRedo();
                PatternUtils.shiftDown(pattern);
                break;
                
            case FLIP_VERTICAL:
                this.addPatternUndoRedo();
                PatternUtils.flipVertical(pattern);
                break;
                
            case FLIP_HORIZONTAL:
                this.addPatternUndoRedo();
                PatternUtils.flipHorizontal(pattern);
                break;
                
            case ROTATE:
                this.addPatternUndoRedo();
                PatternUtils.rotateClockwise(pattern);
                break;
                
            case INVERT:
                this.addPatternUndoRedo();
                PatternUtils.invertPixelsStatus(pattern);
                break;
                
            case MIRROR_LEFT_RIGHT:
                this.addPatternUndoRedo();
                PatternUtils.mirrorLeftToRight(pattern);
                break;
                
            case MIRROR_UP_DOWN:
                this.addPatternUndoRedo();
                PatternUtils.mirrorUpToDown(pattern);
                break;
            
            case TOGGLE_PAINT_MODE:
                // the undo/redo action is done in the pattern editor, when the action is in fact executed
                if(((JToggleButton) source).isSelected()) {
                    this.patternEditor.setPaintMode();
                } else {
                    this.patternEditor.setDefaultMode();
                }
                break;
                
            // **** from here on, the actions does not need to be "undoable" ****
                
            case TOGGLE_MOUSE_ACTION:
                this.setMouseSelectionMode();
                break;
                
            case TOGGLE_COLOR_MODE_VIEW:
                this.setViewMode();
                break;
                
            case CHANGE_SPRITE_VIEW_SCALE:
                this.patternViewerButton.upScale();
                break;
                
            case SELECT_NEXT_PATTERN:
                int nextSpriteIndex = sprites.indexOf(sprite) + 1;
                if(nextSpriteIndex < sprites.size()) {
                    this.setSelectedSprite((MsxSprite) sprites.get(nextSpriteIndex));
                }
                break;
                
            case SELECT_PREVIOUS_PATTERN:
                int currentSpriteIndex = sprites.indexOf(sprite);
                if(currentSpriteIndex > 0) {
                    this.setSelectedSprite((MsxSprite) sprites.get(currentSpriteIndex - 1));
                }
                break;
                
            case EXPORT_PATTERNS:
                this.exportSprites();
                break;
                
            case EXPORT_ATTRIBUTES:
                break;
        }   
        
        // notify pattern observers if necessary
        if(pattern.hasChanged()) {
            pattern.notifyObservers();
        }
    }
    
    /**
     * Set the pattern editor selection mode.
     * @param singleSelection true for single selection
     */
    private void setMouseSelectionMode() {
        if(this.mouseSelectionToggleButton.isSelected()) {
            this.patternEditor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            this.patternEditor.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        }   
    }
    
    /**
     * Set the high contrast or color view mode.
     */
    private void setViewMode() {
        
        // set the renderer status (color or B&W)
        boolean status = this.highContrastModeToggleButton.isSelected();
        
        this.spriteRenderer.setRenderInBlackAndWhite(status);
        this.patternEditor.setRenderInBlackAndWhite(status);
        this.patternViewerButton.setPaintBackground(status);
        
        // set the paint background status for all sprites views
        this.spritesPanel.updateUI();
        
    }
    
    /**
     * Generate a new pattern change Undo/Redo UndoableEdit.
     */
    private void addPatternUndoRedo() {
        this.addUndoRedo(SpriteEditorAction.PATTERN_CHANGE, this.getSelectedSprite());
    }
    
    /**
     * Update the undo/redo manager events.
     * @param action the type of action
     * @param sprite the sprite changed
     */
    private void addUndoRedo(SpriteEditorAction action, MsxSprite sprite) {
        
        // only non null sprites can be processed
        if(sprite == null) {
            return;
        }
        
        UndoableEdit undoableEdit = null;
        
        // generate the undo/redo event accordingly to the action done
        switch(action) {
            
            case PATTERN_CHANGE:
                undoableEdit = SpritePatternUndoableEdit.patternChangeUndoRedo(this.spritesDataObject, sprite);
            break;
            
            case CHANGE_SPRITE_COLOR:
            case CHANGE_SPRITE_BG_COLOR:
                undoableEdit = SpriteAttributesUndoableEdit.attributesChangeUndoRedo(sprite);
            break;
            
            case CHANGE_SPRITE_LABEL:
                undoableEdit = SpriteLabelUndoableEdit.labelChangeUndoRedo(this.spritesDataObject, sprite);
            break;
            
            case ADD_SPRITE:
            case INSERT_SPRITE:
                undoableEdit = AddRemoveSpriteUndoableEdit.addSpriteUndoRedo(this, sprite);
            break;
            
            case REMOVE_SPRITE:
                undoableEdit = AddRemoveSpriteUndoableEdit.removeSpriteUndoRedo(this, sprite);
            break;
            
            case SHIFT_PATTERN_NUMBER_UP:
                undoableEdit = PatternShiftUndoableEdit.shiftPatternUpUndoRedo(this, sprite);
                break;
            
            case SHIFT_PATTERN_NUMBER_DOWN:
                undoableEdit = PatternShiftUndoableEdit.shiftPatternDownUndoRedo(this, sprite);
                break;
        }   
        
        // update the Undo/Redo Manager
        if (undoableEdit != null) {
            UndoRedo.Manager undoRedoManager = this.spritesDataObject.getUndoRedoManager();
            undoRedoManager.undoableEditHappened(new UndoableEditEvent(this, undoableEdit));
            this.spritesDataObject.setChanged(true);
            this.spritesDataObject.notifyObservers(); //////////////////////////////////////////////////////////////////
        }
    }
    
    // --------------------- PropertyChangeListener implementation
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        
        if (DataObject.PROP_MODIFIED.equals(event.getPropertyName())) {
            
            // the binary file name with extension
            final String spritesFileName = this.spritesDataObject.getPrimaryFile().getNameExt();
            
            SwingUtilities.invokeLater(() -> {
                boolean changed = Boolean.TRUE.equals(event.getNewValue());
                setDisplayName(changed ? spritesFileName + "*" : spritesFileName);
            });
        }
    }
    
    @Override
    public void update(java.util.Observable observable, Object object) {
        this.updatePatternViewerImage();
    }   
    
    /**
     * Update the pattern viewer (button) image.
     */
    private void updatePatternViewerImage() {
        if(this.getSelectedSprite() != null) {
            this.patternViewerButton.setBackgroundSprites(this.getMixedSprites());
            this.patternViewerButton.setSprite(this.getSelectedSprite());
        }
    }   
    
    /** Update the sprite editor */
    private void updateVisualComponents() {
        
        // validate the sprites data object
        if(!this.isDataObjectAvailable()) {
            return;
        }   
        
        // the sprites data lists
        final java.util.List<Sprite> sprites = this.getSprites();
        final java.util.List<SpriteAttributes> attributes = this.getAttributes();
        final java.util.List<java.awt.Color> palette = this.getPalette();
        
        // fill the sprites (visual) list
        // the invokeLater must be used to avoid unsafe thread errors from Swing components
        SwingUtilities.invokeLater(() -> {
            setSprites(sprites);
            setAttributes(attributes);
            setPalette(palette);
        });
        
        // update the tab visual components (file name)
        this.setDisplayName(this.spritesDataObject.getPrimaryFile().getNameExt());
        //this.setSelectedSprite(selectedSprite);
        
        // update the sprite table model
        this.updateUI();
    }   
    
    /** Reload the sprites data object from file */
    private void updateSpritesDataObject() {
        
        try {
            // reload the binary object to refresh the changes on the editor
            this.spritesDataObject.load();
        } catch (java.io.IOException exception) {
            java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
            JOptionPane.showMessageDialog(frame,
                I18N.getString("error.reading.file", this.spritesDataObject.getName()), // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(this::close);
        }   
        
        this.updateVisualComponents();
    }   
    
    // --------------------- FileChangeListener implementation
    @Override
    public void fileChanged(FileEvent fe) {
        SwingUtilities.invokeLater(this::updateSpritesDataObject);
    }
    
    // --------------------- FileChangeListener implementation
    @Override
    public void fileDeleted(FileEvent fileEvent) {
        this.spritesDataObject.setChanged(false);
        SwingUtilities.invokeLater(this::close);
    }
    
    // --------------------- FileChangeListener implementation
    @Override public void fileFolderCreated(FileEvent fe) {}
    @Override public void fileDataCreated(FileEvent fe) {}
    @Override public void fileRenamed(FileRenameEvent fre) {}
    @Override public void fileAttributeChanged(FileAttributeEvent fae) {}
    
    /**
     * Export the sprites data.
     */
    private void exportSprites() {
        
        // the sprite data object that holds all sprites to be exported
        final SpritesDataObject dataObject = this.spritesDataObject;
        final SpriteExporterDialog exporterDialog = this.getSpriteExporterDialog();
        
        /* Create and display the dialog */
        SwingUtilities.invokeLater(() -> {
            exporterDialog.setDataObject(dataObject);
            DialogUtil.show(exporterDialog);
        });
    }
    
    /**
     * Get the current selected sprite.
     * @return 
     */
    @Override
    public MsxSprite getSelectedSprite() {
        return this.patternEditor.getSprite();
    }
    
    /**
     * Set the current selected sprite.
     * @param sprite 
     */
    @Override
    public void setSelectedSprite(Sprite sprite) {
        
        // update the sprite pattern model and editor
        if(sprite instanceof MsxSprite) {
            
            // set the current sprite as selected
            MsxSprite msxSprite = (MsxSprite) sprite;
            if(this.buttonsMap.containsKey(msxSprite)) {
                this.getSpriteButton(msxSprite).requestFocus();
            }   
            
            // update the pattern observer reference
            // this is necessary because the pattern viewer must be updated on every change
            this.getSelectedSprite().deleteObserver(this);
            msxSprite.addObserver(this);
            
            // update the sprite reference in the pattern editor
            this.patternEditor.setSprite(msxSprite);
            
            // update the sprite viewer image
            this.updatePatternViewerImage();
            
            // update the sprite observer reference
            String patternNumber = TextUtil.intToTwoDigitString(msxSprite.getIndex());
            this.patternNumberLabel.setText(I18N.getString("pattern.number", patternNumber));
            
        } else {
            this.patternNumberLabel.setText(Default.EMPTY_STRING);
        }
    }
    
    /**
     * Return the sprite exporter dialog.
     * The dialog is created only when requested the export feature (lazy construction).
     * @return 
     */
    private SpriteExporterDialog getSpriteExporterDialog() {
        
        // instantiate the export dialog on request
        if (this.spriteExporterDialog == null) {
            
            // create the export dialog
            java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
            this.spriteExporterDialog = new SpriteExporterDialog(frame, true);
            
            // set dialog properties
            this.spriteExporterDialog.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        }   
        
        return this.spriteExporterDialog;
    }
    
    /**
     * Update the sprite buttons panel accordingly to the current sprite pattern order.
     */
    public void updateSpriteButtonsPanel() {
        
        // remove all buttons from panel
        this.spritesPanel.removeAll();
        
        // add all sprites available in the sprites data object
        for(Sprite sprite : this.getSprites()) {
            SpriteButton spriteButton = this.getSpriteButton((MsxSprite) sprite);
            if(spriteButton != null) {
                this.spritesPanel.add(spriteButton);
            }
        }
        
        // update the sprite patterns view panel
        this.spritesPanel.updateUI();
    }
    
    /**
     * Add a new sprite at the last position of the sprite set.
     */
    public boolean addNewSprite() {
        MsxSprite newSprite = (MsxSprite) this.spritesDataObject.createNewSprite();
        newSprite.getAttributes().set(0, DEFAULT_FG_COLOR_CODE); // set background to color code 15 by default
        return this.addSprite(newSprite);
    }   
    
    /**
     * Add the given sprite at the last position of the sprite set.
     * @param sprite the sprite to be added to the sprite set
     */
    public boolean addSprite(MsxSprite sprite) {
        return this.insertSprite(this.getSprites().size(), sprite);
    }   
    
    /**
     * Insert a new sprite at the current selected sprite position.
     * @return MsxSprite
     */
    private boolean insertNewSprite() {
        MsxSprite newSprite = (MsxSprite) this.spritesDataObject.createNewSprite();
        newSprite.getAttributes().set(0, DEFAULT_FG_COLOR_CODE); // set background to color code 15 by default
        return this.insertSprite(this.getSelectedSprite().getIndex(), newSprite);
    }
    
    /**
     * Insert a given sprite at a given index.
     * 
     * @param index the sprite index
     * @param sprite the sprite to be added to the sprite set
     */
    public boolean insertSprite(int index, MsxSprite sprite) {
        
        if (this.getSprites().size() < MsxSpritePattern.TABLE_SIZE) {
            
            // add the sprite to the current sprites data object at given index
            this.spritesDataObject.addSpriteAt(index, sprite);
            this.spritesDataObject.notifyObservers();
            
            // add the sprite button to the sprite view
            this.addSpriteButton(sprite);
            this.updateSpriteButtonsPanel();
            this.setSelectedSprite(sprite);
            
            return true;
            
        } else {
            
            JOptionPane.showMessageDialog(
                    WindowManager.getDefault().getMainWindow(),
                    I18N.getString("maximum.number.of.patterns.reached"), // dialog message
                    I18N.getString("information"), // dialog title
                    JOptionPane.INFORMATION_MESSAGE);
            
            return false;
        }   
    }   
    
    /**
     * Removes the current selected sprite.
     * @param sprite 
     */
    public boolean removeSprite(Sprite sprite) {
        
        // the sprite and it's index must be available
        //int index = this.spritesDataObject.getSpriteIndex(sprite);
        if (sprite == null || sprite.getIndex() < 0) {
            return false;
        }   
        
        // remove the sprite from data object and it's associated resources
        this.spritesDataObject.removeSprite(sprite);
        this.removeSpriteButton(sprite);
        this.updateSpriteButtonsPanel();
        
        // get the current sprites count
        int spritesCount = this.spritesDataObject.getSpritesCount();
        
        // when the index is lower than zero, the sprite list is empty and we must add an empty sprite
        if(spritesCount > 0) {
            
            // get the index of the removed sprite (cannot be bigger than pattern list size)
            int index =  Math.min(sprite.getIndex(), spritesCount-1);
            
            // set the current sprite
            Sprite newSelectedSprite = this.spritesDataObject.getSpriteAt(index);
            this.setSelectedSprite((MsxSprite) newSelectedSprite);
            
        } else {
            
            // add a new sprite to avoid an empty pattern set (this action also sets the current sprite)
            this.addNewSprite();
        }   
        
        // set the current selected pattern from sprites list
        this.spritesDataObject.notifyObservers();
        
        return true;
    }
    
    /**
     * Move the sprite pattern to "up" in list. This movement changes the sprite pattern number.
     * Moving to left means "decrease the pattern number" (up -> towards list head).
     * @param sprite 
     */
    public void moveSpriteUp(MsxSprite sprite) {
        
        java.util.List<Sprite> sprites = this.getSprites();
        
        if(sprites.indexOf(sprite) > 0) {
            
            this.spritesDataObject.moveSpriteUp(sprite);
            this.spritesDataObject.notifyObservers();
            
            this.updateSpriteButtonsPanel();
            this.setSelectedSprite(sprite);
        }
    }   
    
    /**
     * Move the sprite pattern to "right" in list. This movement changes the sprite pattern number.
     * Moving to right means "increase the pattern number" (down -> towards list tail).
     * @param sprite 
     */
    public void moveSpriteDown(MsxSprite sprite) {
        
        java.util.List<Sprite> sprites = this.getSprites();
        
        if(sprites.indexOf(sprite) + 1 < sprites.size()) {
            
            this.spritesDataObject.moveSpriteDown(sprite);
            this.spritesDataObject.notifyObservers();
            
            this.updateSpriteButtonsPanel();
            this.setSelectedSprite(sprite);
        }
    }   
    
    // --------------------- TopComponent override
    @Override
    public boolean canClose() {
        
        // if the user changed the binary file and not saved it yet, ask for confirmation
        if(this.spritesDataObject.isModified()) {
            
            // save dialog file name and text buttons
            String fileName = this.spritesDataObject.getPrimaryFile().getNameExt();
            int userOption = DialogUtil.saveChanges(this, fileName);
            
            // user chooses the save option
            switch (userOption) {
                
                case JOptionPane.YES_OPTION:
                    // save data and close binary editor window (keep changes)
                    org.openide.util.Lookup lookup = this.spritesDataObject.getLookup();
                    SaveDataObjectCookie saveDataObject = lookup.lookup(SaveDataObjectCookie.class);
                    if(saveDataObject != null) {
                        saveDataObject.save();
                    }   
                    break;
                    
                case JOptionPane.NO_OPTION:
                    // don't save and close editor window (discard changes)
                    try {
                        this.spritesDataObject.setValid(false);
                    } catch (PropertyVetoException ex) {
                        org.openide.util.Exceptions.printStackTrace(ex);
                    }   
                    break;
                    
                default:
                    // keep the editor window open and don't save
                    return false;   
            }   
        }   
        
        return true;
    }
    
    /**
     * Change the current sprite color(s).
     * @param sprite the sprite data
     */
    private void changeSpriteColor(MsxSprite msxSprite) {
        
        // the MsxSpriteColorChooser handles mode1/mode2 sprite color selection
        MsxSpriteColorChooser colorAttributeEditor = new MsxSpriteColorChooser();
        colorAttributeEditor.setPalette(this.getPalette());
        Optional<MsxSpriteAttributes> attributes = colorAttributeEditor.select(msxSprite.getAttributes());
        
        // update the sprite attributes if the changes were accepted
        if (attributes.isPresent()) {
            this.addUndoRedo(SpriteEditorAction.CHANGE_SPRITE_COLOR, msxSprite);
            msxSprite.setAttributes(attributes.get());
            msxSprite.getAttributes().notifyObservers();
        }   
    }   
    
    /**
     * Change the sprite viewer background color
     * @param msxSprite 
     */
    private void changePatternViewerBackgroundColor(MsxSprite msxSprite) {
        
        java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
        
        // the sprite color selection dialog
        ColorCodeSelectionDialog colorCodeChooserDialog = new ColorCodeSelectionDialog(frame, true);
        colorCodeChooserDialog.setPalette(this.getPalette());
        colorCodeChooserDialog.setSelectedColorCode(msxSprite.getBackgroundColorCode());
        DialogUtil.show(colorCodeChooserDialog);
        
        // update the background color of the pattern viewer
        if (colorCodeChooserDialog.isSelectionAccepted()) {
            
            this.addUndoRedo(SpriteEditorAction.CHANGE_SPRITE_BG_COLOR, msxSprite);
            byte selectedColorCode = colorCodeChooserDialog.getSelectedColorCode();
            
            // set the color code and background color
            msxSprite.setBackgroundColorCode(selectedColorCode);
            msxSprite.getAttributes().notifyObservers();
        }
    }
    
    /**
     * Edit the color palette entries
     */
    private void editPaletteColors() {
        
        java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
        
        // create and show the palette color chooser dialog
        PaletteColorSelectionDialog paletteColorsChooser = new PaletteColorSelectionDialog(frame, true);
        paletteColorsChooser.setPalette(this.getPalette());
        DialogUtil.show(paletteColorsChooser);
        
        // update the palette colors list
        if(paletteColorsChooser.isSelectionAccepted()) {
            
            // update the color palette
            java.util.List<java.awt.Color> paletteColors = paletteColorsChooser.getPaletteColors();
            
            // update the data object palette
            this.spritesDataObject.setPalette(paletteColors);
            this.spritesDataObject.notifyObservers();
            
            // propagate the new palette colors to other components
            this.setPalette(paletteColors);
        }   
    }   
    
    /**
     * Open the sprite label change dialog.
     */
    public void changeSpriteLabel(MsxSprite sprite) {
        
        // setup and show sprite properties dialog
        String newLabel = JOptionPane.showInputDialog(
            this,
            I18N.getString("sprite.label"),
            sprite.getLabel()
        );  
        
        // if the user did not canceled the change label operation
        if(newLabel != null) {
            
            // update the sprite label
            this.addUndoRedo(SpriteEditorAction.CHANGE_SPRITE_LABEL, sprite);
            sprite.setLabel(newLabel);
            
            // notify observers about the label change
            this.spritesDataObject.notifyObservers(); // notifies sprite navigator and attributes table model
            sprite.notifyObservers(); // notify sprite button and sprite viewer
        }   
    }   
    
    class SpriteTypeLookupHint implements NavigatorLookupHint {
        @Override
        public String getContentType() {
            return "application/x-sprite";
        }
    }
}
