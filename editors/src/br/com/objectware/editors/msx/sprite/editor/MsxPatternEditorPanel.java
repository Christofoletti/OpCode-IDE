/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.TextUtil;
import br.com.objectware.domain.msx.sprite.MsxSprite;
import br.com.objectware.editors.sprite.SpritesDataObject;
import br.com.objectware.domain.msx.sprite.MsxSpriteMixer;
import br.com.objectware.domain.sprite.PatternUtils;
import br.com.objectware.domain.sprite.SpritePattern;
import br.com.objectware.editors.edit.SpritePatternUndoableEdit;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.undo.UndoableEdit;
import org.openide.awt.UndoRedo;

/**
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 30/Jul/2015
 */
public final class MsxPatternEditorPanel extends javax.swing.JPanel
    implements java.awt.event.ActionListener,
               java.awt.event.KeyListener,
               java.awt.event.MouseListener,
               javax.swing.event.TableModelListener,
               javax.swing.event.ListSelectionListener,
               java.util.Observer {
    
    /** The copy string identifier */
    private static final String COPY = "Copy";
    
    /** The paste string identifier */
    private static final String PASTE = "Paste";
    
    private enum MODE {
        DEFAULT, SET, RESET, PAINT, VERTICAL_MIRROR, HORIZONTAL_MIRROR;
    }   
    
    /** The current edit mode */
    private MODE currentMode = MODE.DEFAULT;
    
    /** The sprite pattern table cell renderer (evaluate the cell color according to the pixel status) */
    private final MsxPatternTableCellRenderer patternTableRenderer = new MsxPatternTableCellRenderer();
    
    /** The pattern model - the model must be available in this class because the editor access it */
    private final MsxPatternViewTableModel patternModel = new MsxPatternViewTableModel();
    
    /** The sprites data object being edited */
    private SpritesDataObject spritesDataObject;
    
    /** The system clipboard */
    private final java.awt.datatransfer.Clipboard clipboard;
    
    /** The mouse paint cursor */
    private final java.awt.Cursor paintCursor;
    
    /**
     * Creates new form PatternEditorPanel
     */
    public MsxPatternEditorPanel() {
        
        // the sprite renderer must be created before visual elements
        this.initComponents();
        
        // setup the pattern model for the pattern table
        this.patternTable.setModel(this.patternModel);
        this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        // create the custom paint cursor
        java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Image paintCursorImage = I18N.getImage("paint.cursor");
        this.paintCursor = toolkit.createCustomCursor(
            paintCursorImage , new java.awt.Point(1, 12), "Paint Cursor");
        
        // get the system clipboard
        this.clipboard = toolkit.getSystemClipboard();
        
        this.setupListeners();
    }   
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        patternTable = new javax.swing.JTable() {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return getPixelCellRenderer();
            }
        };

        setPreferredSize(new java.awt.Dimension(264, 256));
        setLayout(new java.awt.BorderLayout());

        patternTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        patternTable.setColumnSelectionAllowed(true);
        patternTable.setDoubleBuffered(true);
        patternTable.setGridColor(new java.awt.Color(180, 180, 180));
        patternTable.setPreferredSize(new java.awt.Dimension(248, 248));
        add(patternTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable patternTable;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Get the sprite object.
     * @return 
     */
    public MsxSprite getSprite() {
        return this.patternModel.getSprite();
    }
    
    /**
     * Sets the current MSX sprite to be edited.
     * @param sprite 
     */
    public void setSprite(MsxSprite sprite) {
        
        // update the pattern observer reference
        this.getSprite().deleteObserver(this);
        sprite.addObserver(this);
        
        // update the sprite pattern model
        this.patternModel.setSprite(sprite);
        this.patternTable.clearSelection();
        this.updateUI();
    }   
    
    /**
     * Set the pattern editor to Default mode
     */
    protected void setDefaultMode() {
        this.setCursor(java.awt.Cursor.getDefaultCursor());
        this.currentMode = MODE.DEFAULT;
    }
    
    /**
     * Set the pattern editor to Paint mode
     */
    protected void setPaintMode() {
        this.setCursor(this.paintCursor);
        this.currentMode = MODE.PAINT;
    }   
    
    /**
     * Sets the selection mode: ListSelectionModel.SINGLE_SELECTION, or SINGLE_INTERVAL_SELECTION
     * @param selectionMode the selection mode ({@see #ListSelectionModel})
     */
    public void setSelectionMode(int selectionMode) {
        this.patternTable.setSelectionMode(selectionMode);
        this.patternTable.setColumnSelectionAllowed(!this.isSingleSelection());
        this.patternTable.setRowSelectionAllowed(!this.isSingleSelection());
    }   
    
    /**
     * Get the sprite mixer object.
     * @return 
     */
    public final MsxSpriteMixer getSpriteMixer() {
        return this.patternModel.getSpriteMixer();
    }
    
    /**
     * Remove all sprites from background sprites list.
     */
    public void resetBackground() {
        this.getSpriteMixer().clear();
        this.updateUI();
    }   
    
    /**
     * Update the background using the current sprite pattern.
     */
    public void addCurrentSpriteToBackground() {
        this.patternModel.addCurrentSpriteToBackground();
        this.updateUI();
    }   
    
    /**
     * Set the sprite renderer palette colors (black/white or color palette)
     * @param paletteColors 
     */
    public void setPalette(java.util.List<java.awt.Color> paletteColors) {
        this.patternTableRenderer.setPalette(paletteColors);
        this.updateUI();
    }   
    
    /**
     * Enable/Disable redering in black and white or in color mode (using the current pallete)
     * @param renderInBlackAndWhite 
     */
    public void setRenderInBlackAndWhite(boolean renderInBlackAndWhite) {
        this.patternTableRenderer.setRenderInBlackAndWhite(renderInBlackAndWhite);
        this.updateUI();
    }
    
    /**
     * Return the selection status (single or region)
     * @return true for single selection mode, false otherwise
     */
    private boolean isSingleSelection() {
        int selectionMode = this.patternTable.getSelectionModel().getSelectionMode();
        return (selectionMode == ListSelectionModel.SINGLE_SELECTION);
    }   
    
    private TableCellRenderer getPixelCellRenderer() {
        return this.patternTableRenderer;
    }
    
    protected void setDataObject(SpritesDataObject dataObject) {
        
        // the binary data object cannot be null
        assert dataObject != null;
        
        this.spritesDataObject = dataObject;
        this.setPalette(dataObject.getPalette());
    }   
    
    /**
     * 
     */
    private void setupListeners() {
        
        // setup the patern table custom listeners
        this.patternTable.getModel().addTableModelListener(this);
        this.patternTable.addKeyListener(this);
        this.patternTable.addMouseListener(this);
        this.patternTable.getSelectionModel().addListSelectionListener(this);
        this.patternTable.getColumnModel().getSelectionModel().addListSelectionListener(this);
        
        // register the copy n paste listener
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.event.ActionEvent.CTRL_MASK, false);
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.event.ActionEvent.CTRL_MASK, false);
        this.patternTable.registerKeyboardAction(this, COPY, copy, JComponent.WHEN_FOCUSED);
        this.patternTable.registerKeyboardAction(this, PASTE, paste, JComponent.WHEN_FOCUSED);
    }   
    
    @Override
    public void tableChanged(TableModelEvent e) {}
    
    /**
     * http://www.javaworld.com/article/2077579/learn-java/java-tip-77--enable-copy-and-paste-functionality-between-swing-s-jtables-and-excel.html
     * @param actionEvent 
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        
        if (actionEvent.getActionCommand().equals(COPY)) {
            
            // build the selection content using a string format
            StringBuilder sb = new StringBuilder();
            sb.append(Default.COPYRIGHT).append(Default.LS); // add some easter here :)
            
            // build the string to be transfered to the clipboard
            for(int row :this.patternTable.getSelectedRows()) {
                for(int column :this.patternTable.getSelectedColumns()) {
                    sb.append(this.patternTable.getValueAt(row, column)).append(Default.SPACE_CHAR);
                }   
                sb.append(Default.LS);
            }   
            
            // copy the selected data to the clipboard
            java.awt.datatransfer.StringSelection stringSelection = 
                new java.awt.datatransfer.StringSelection(sb.toString());
            this.clipboard.setContents(stringSelection, stringSelection);
            
        } else if (actionEvent.getActionCommand().equals(PASTE)) {
            
            int row = this.patternTable.getSelectedRow();
            int column = this.patternTable.getSelectedColumn();
            
            try {
                
                String clipData = (String)(this.clipboard.getContents(this)
                    .getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor)
                );
                String clipDataSplit[] = clipData.split(Default.COPYRIGHT);
                
                // validate the paste data (and the easter ;P)
                if(clipDataSplit.length != 2) {
                    return;
                }   
                
                // generate the undo/redo event
                this.addPatternUndoRedo();
                this.spritesDataObject.setChanged(true);
                
                // split the clip data into string lines
                String lines[] = clipDataSplit[1].trim().split(Default.LS);
                
                // parse each line of the clip data into a byte array
                for(String line:lines) {
                    line = line.replace(Default.TAB_CHAR, Default.SPACE_CHAR);
                    this.setRowVauesAt(TextUtil.parseStringOfIntegersToByteArray(line), row++, column);
                }
                
            } catch (java.lang.NumberFormatException | 
                     java.awt.datatransfer.UnsupportedFlavorException | 
                     java.io.IOException exception) {
                // exceptions may be discarded in this case...
            }
            
        }
    }
    
    @Override
    public void keyTyped(KeyEvent keyEvent) {}
    
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        
        // get the row and column of the current selected cell
        int row = patternTable.getSelectedRow();
        int column = patternTable.getSelectedColumn();
        
        switch(keyEvent.getKeyCode()) {
            
            case KeyEvent.VK_SPACE:
                this.addPatternUndoRedo();
                byte value = (byte) this.patternTable.getModel().getValueAt(row, column);
                this.updateCellValueAndEditorMode(value, row, column);
                keyEvent.consume();
                break;
                
            case KeyEvent.VK_DELETE:
                this.addPatternUndoRedo();
                this.fillSelection(SpritePattern.PIXEL_OFF);
                this.spritesDataObject.setChanged(true);
                this.spritesDataObject.notifyObservers();
                keyEvent.consume();
                break;
                
            case KeyEvent.VK_1:
                this.addPatternUndoRedo();
                this.updateCellValueAndEditorMode(SpritePattern.PIXEL_OFF, row, column);
                keyEvent.consume();
                break;
                
            case KeyEvent.VK_0:
                this.addPatternUndoRedo();
                this.updateCellValueAndEditorMode(SpritePattern.PIXEL_ON, row, column);
                keyEvent.consume();
                break;
                
            case KeyEvent.VK_ESCAPE:
                this.patternTable.setColumnSelectionInterval(column, column);
                this.patternTable.setRowSelectionInterval(row, row);
                this.setDefaultMode();
                keyEvent.consume();
                break;
                
            default:
                break;
        }   
    }
    
    @Override
    public void keyReleased(KeyEvent keyEvent) {
        
        switch(keyEvent.getKeyCode()) {
            
            case KeyEvent.VK_0:
            case KeyEvent.VK_1:
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_DELETE:
                if(this.isCurrentMode(MODE.SET) || this.isCurrentMode(MODE.RESET)) {
                    
                    // back to default mode
                    this.setDefaultMode();
                    
                    // change the sprites data object status and notify all observers about the changes
                    this.spritesDataObject.setChanged(true);
                    this.spritesDataObject.notifyObservers();
                }   
                keyEvent.consume();
            break;
        }
    }   
    
    @Override
    public void mousePressed(java.awt.event.MouseEvent event) {
        
        int row = this.patternTable.getSelectedRow();
        int column = this.patternTable.getSelectedColumn();
        event.consume();
        
        // row and column numbers must be greater than zero
        if(row < 0 || column < 0) {
            return;
        }
        
        if(this.isSingleSelection() || this.isCurrentMode(MODE.PAINT)) {
            
            // generate the undo/redo event
            this.addPatternUndoRedo();
            
            // update the cell value
            byte value = (byte) this.patternTable.getModel().getValueAt(row, column);
            this.updateCellValueAndEditorMode(value, row, column);
        }
    }
    
    @Override
    public void mouseReleased(java.awt.event.MouseEvent event) {
        
        if(this.isSingleSelection()) {
            
            if(this.isCurrentMode(MODE.SET) || this.isCurrentMode(MODE.RESET)) {
                this.setDefaultMode();
            }
            
            // change the sprites data object status and notify all observers about the changes
            this.spritesDataObject.setChanged(true);
            this.spritesDataObject.notifyObservers();
        }   
        
        event.consume();
    }
    
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {}
    
    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {}
    
    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {}
    
    /**
     * This method update the cell value when the mouse pointer is being dragged in SET or RESET mode
     * Implementation of ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent event) {
        if(this.isCurrentMode(MODE.SET)) {
            this.fillSelection(SpritePattern.PIXEL_ON);
        } else if(this.isCurrentMode(MODE.RESET)) {
            this.fillSelection(SpritePattern.PIXEL_OFF);
        }   
    }   
    
    /**
     * Fill the selected cells with value
     * @param value the char value to fill the selection
     */
    private void fillSelection(byte value) {
        
        TableModel model = this.patternTable.getModel();
        
        // for single selection, sets only the selected cell
        if(this.isSingleSelection()) {
            int row = this.patternTable.getSelectedRow();
            int column = this.patternTable.getSelectedColumn();
            model.setValueAt(value, row, column);
        } else {
            for(int row : this.patternTable.getSelectedRows()) {
                for(int column : this.patternTable.getSelectedColumns()) {
                    model.setValueAt(value, row, column);
                }
            }
            this.updateUI();
        }   
    }   
    
    /**
     * Set the pattern pixel status according to the cell values
     * @param cellValues
     * @param row
     * @param column 
     */
    private void setRowVauesAt(byte[] cellValues, int row, int column) {
        
        if(row < this.patternTable.getRowCount()) {
            
            TableModel model = this.patternTable.getModel();
            
            for(byte value:cellValues) {
                
                // set/reset pixels inside the pattern table range only
                if(column < this.patternTable.getColumnCount()) {
                    // set/reset the pixel status according to the cell values
                    byte pixelStatus = ((value & MsxSpriteMixer.FG_PIXEL_ON) > 0) ?
                            SpritePattern.PIXEL_ON:SpritePattern.PIXEL_OFF;
                    model.setValueAt(pixelStatus, row, column);
                } else {
                    break;
                }
                
                // go to next column
                column++;
            }
        }
    }
    
    /**
     * Sets the editor mode accordingly to the given value (cell value = pixel status)
     * @param value the current selected cell value (pixel status)
     */
    private void updateCellValueAndEditorMode(byte value, int row, int column) {
        
        if(this.isCurrentMode(MODE.PAINT)) {
            
            // if the current mode is painting, paint all pixels from current selected pixel
            PatternUtils.paint(this.getSprite().getPattern(), column, row);
            this.getSprite().getPattern().notifyObservers();
            
            // change the sprites data object status and notify all observers about the changes
            this.spritesDataObject.setChanged(true);
            this.spritesDataObject.notifyObservers();
            
        } else if ((value & MsxSpriteMixer.FG_PIXEL_ON) > 0) {
            
            // if current pixel is on, turn it off and enter the reset mode
            this.fillSelection(SpritePattern.PIXEL_OFF);
            this.currentMode = MODE.RESET;
            
        } else {
            
            // if current pixel is off, turn it on and enter the set mode
            this.fillSelection(SpritePattern.PIXEL_ON);
            this.currentMode = MODE.SET;
        }   
    }
    
    @Override
    public void update(java.util.Observable observable, Object object) {
        // this component must be updated when some change occurs in the sprite pattern
        if(!this.isCurrentMode(MODE.SET) && !this.isCurrentMode(MODE.RESET)) {
            this.updateUI();
        }
    }
    
    /**
     * Generate an Undo/Redo event
     */
    private void addPatternUndoRedo() {
        
        // create the undoable edit for pattern changes
        UndoableEdit undoableEdit = SpritePatternUndoableEdit.patternChangeUndoRedo(
            this.spritesDataObject, this.getSprite()
        );
        
        // update the Undo/Redo manager in the sprites data object
        UndoRedo.Manager undoRedoManager = this.spritesDataObject.getUndoRedoManager();
        undoRedoManager.undoableEditHappened(new UndoableEditEvent(this, undoableEdit));
    }
    
    /**
     * Verify if the current mode is equals "mode".
     * @param mode
     * @return 
     */
    private boolean isCurrentMode(MODE mode) {
        return this.currentMode.equals(mode);
    }
}