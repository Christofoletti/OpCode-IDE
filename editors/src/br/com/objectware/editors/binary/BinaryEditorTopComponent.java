/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.binary;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.DialogUtil;
import br.com.objectware.commons.utils.TextUtil;
import br.com.objectware.editors.cookies.OpenBinaryEditorCookie;
import br.com.objectware.editors.cookies.SaveDataObjectCookie;
import br.com.objectware.editors.enums.BinaryEditorAction;
import br.com.objectware.editors.export.BinaryExporterDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyVetoException;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * The binary editor.
 * 
 * @author Luciano M. Christofoletti
 * @since 18/May/2015
 * 
 * Useful docs:
 *     http://docs.oracle.com/javase/tutorial/uiswing/components/table.html (JTable examples)
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
public final class BinaryEditorTopComponent
       extends TopComponent
       implements MultiViewElement, PropertyChangeListener, AdjustmentListener, ActionListener, FileChangeListener {
    
    private static final long serialVersionUID = 8841214123495433325L;
    
    // the binary file path key (used when persisting/restoring the active windows)
    private static final String KEY_FILE_PATH = "binary.file.path";
    
    /** The icon showed in the tab (to the left of the file name) */
    private java.awt.Image tabIcon;
    
    /** The binary data object being edited */
    private BinaryDataObject binaryDataObject;
    
    /** The callback for multiview element */
    private MultiViewElementCallback callback;
    
    /** The left side (hex value view) panel */
    private ByteHexValueTableModel byteHexValueTableModel;
    
    /** The right side (ascii value view) panel */
    private ByteAsciiViewTableModel byteAsciiViewTableModel;
    
    /** The binary exporter dialog */
    private BinaryExporterDialog binaryExporterDialog;
    
    /** The first column of the hex table show the byte address. So we have a special cell render for it. */
    private final TableCellRenderer addressColumnRenderer = new AddressTableCellRenderer();
    
    /**
     * The default constructor. Initializes all visual properties that does not depends on
     * the binary data (file) object. Also set all visual elements listeners.
     */
    public BinaryEditorTopComponent() {
        
        // init visual elements properties
        this.initComponents();
        this.cleanStatusPanel();
        
        // the default cell renderer and address cell renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // setup tables visual properties
        this.asciiViewTable.setDefaultRenderer(Object.class, centerRenderer);
        this.asciiViewTable.setSelectionBackground(Default.TABLE_BACKGROUND_HIGHLIGHT);
        this.asciiViewTable.setFillsViewportHeight(true);
        //this.asciiViewTable.setSelectionModel(new BinaryEditorSelectionModel());//////////////////////////////////////
        this.hexViewTable.setDefaultRenderer(Object.class, centerRenderer);
        this.hexViewTable.setSelectionBackground(Default.TABLE_BACKGROUND_HIGHLIGHT);
        this.hexViewTable.setFillsViewportHeight(true);
        //this.hexViewTable.setSelectionModel(new BinaryEditorSelectionModel());//////////////////////////////////////
        
        // setup visual elements listeners
        this.setupListeners();
    }   
    
    /**
     * This constructor is called by OpenBinaryEditorCookie only.
     * @param lookup the data object lookup
     */
    public BinaryEditorTopComponent(org.openide.util.Lookup lookup) {
        
        this();
        
        // set the binary data object being edited
        this.setDataObject(lookup.lookup(BinaryDataObject.class));
    }   
    
    /**
     * Set the data object element.
     * @param dataObject 
     */
    private void setDataObject(BinaryDataObject dataObject) {
        
        // the binary data object cannot be null
        assert dataObject != null;
        this.binaryDataObject = dataObject;
        
        // make the binary data object associations
        dataObject.addPropertyChangeListener(this);
        this.associateLookup(dataObject.getLookup());
        
        // update the tab visual component file name
        this.setDisplayName(dataObject.getPrimaryFile().getNameExt());
        
        // the table models can be setted only when the data object is available
        this.byteHexValueTableModel = new ByteHexValueTableModel(dataObject);
        this.hexViewTable.setModel(this.byteHexValueTableModel);
        this.byteAsciiViewTableModel = new ByteAsciiViewTableModel(dataObject);
        this.asciiViewTable.setModel(this.byteAsciiViewTableModel);
        
        // the cell width of hex view must be large enought to enclose hex and decimal values
        this.hexViewTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        //this.hexViewTable.setCellEditor(new MyCellEditor());
    }
    
    /**
     * Setup all visual elements listeners.
     */
    private void setupListeners() {
        
        // setup the table model listeners
        ByteAsciiViewTableListener.createNewListenerTo(this);
        ByteHexViewTableListener.createNewListenerTo(this);
        
        this.hexViewScrollPane.getVerticalScrollBar().addAdjustmentListener(this);
        this.asciiViewScrollPane.getVerticalScrollBar().addAdjustmentListener(this);
        this.exportButton.addActionListener(this);
        this.selectAsciiViewFontButton.addActionListener(this);
        this.asciiNonPrintableViewToggleButton.addActionListener(this);
        this.decimalViewToggleButton.addActionListener(this);
    }   
    
    protected JTable getAsciiTable() {
        return this.asciiViewTable;
    }
    
    protected JTable getHexTable() {
        return this.hexViewTable;
    }
    
    protected BinaryDataObject getDataObject() {
        return this.binaryDataObject;
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBarPanel = new javax.swing.JPanel();
        separator = new javax.swing.JSeparator();
        exportButton = new javax.swing.JButton();
        mergeButton = new javax.swing.JButton();
        separator2 = new javax.swing.JSeparator();
        decimalViewToggleButton = new javax.swing.JToggleButton();
        asciiNonPrintableViewToggleButton = new javax.swing.JToggleButton();
        selectAsciiViewFontButton = new javax.swing.JButton();
        binaryFileViewSplitPane = new javax.swing.JSplitPane();
        hexViewScrollPane = new javax.swing.JScrollPane();
        hexViewTable = new JTable() {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0) {
                    return getAddressColumnRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
        asciiViewScrollPane = new javax.swing.JScrollPane();
        asciiViewTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        toolBarPanel.setMinimumSize(new java.awt.Dimension(10, 16));
        toolBarPanel.setPreferredSize(new java.awt.Dimension(128, 26));
        toolBarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 2));

        separator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        separator.setPreferredSize(new java.awt.Dimension(1, 19));
        toolBarPanel.add(separator);

        exportButton.setIcon(I18N.getImageIcon("export.binary.icon"));
        exportButton.setToolTipText(I18N.getString("export.binary.file"));
        exportButton.setActionCommand(BinaryEditorAction.EXPORT_BINARY.name());
        exportButton.setBorder(null);
        exportButton.setFocusPainted(false);
        exportButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(exportButton);

        mergeButton.setIcon(I18N.getImageIcon("merge.binary.data.icon"));
        mergeButton.setToolTipText(I18N.getString("merge.data.from.file"));
        mergeButton.setActionCommand(BinaryEditorAction.MERGE_FROM_FILE.name());
        mergeButton.setBorder(null);
        mergeButton.setEnabled(false);
        mergeButton.setFocusPainted(false);
        mergeButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(mergeButton);

        separator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        separator2.setPreferredSize(new java.awt.Dimension(1, 19));
        toolBarPanel.add(separator2);

        decimalViewToggleButton.setIcon(I18N.getImageIcon("toggle.decimal.view.icon"));
        decimalViewToggleButton.setToolTipText(I18N.getString("enable.disable.decimal.values"));
        decimalViewToggleButton.setActionCommand(BinaryEditorAction.TOGGLE_DECIMAL_VIEW.name());
        decimalViewToggleButton.setBorder(null);
        decimalViewToggleButton.setFocusCycleRoot(true);
        decimalViewToggleButton.setFocusPainted(false);
        decimalViewToggleButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(decimalViewToggleButton);

        asciiNonPrintableViewToggleButton.setIcon(I18N.getImageIcon("toggle.ascii.view.icon"));
        asciiNonPrintableViewToggleButton.setToolTipText(I18N.getString("enable.disable.non.printable.chars"));
        asciiNonPrintableViewToggleButton.setActionCommand(BinaryEditorAction.TOGGLE_ASCII_VIEW.name());
        asciiNonPrintableViewToggleButton.setBorder(null);
        asciiNonPrintableViewToggleButton.setFocusCycleRoot(true);
        asciiNonPrintableViewToggleButton.setFocusPainted(false);
        asciiNonPrintableViewToggleButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(asciiNonPrintableViewToggleButton);

        selectAsciiViewFontButton.setIcon(I18N.getImageIcon("font.selection.icon"));
        selectAsciiViewFontButton.setToolTipText(I18N.getString("font.selection.tooltip"));
        selectAsciiViewFontButton.setActionCommand(BinaryEditorAction.SELECT_FONT.name());
        selectAsciiViewFontButton.setBorder(null);
        selectAsciiViewFontButton.setFocusPainted(false);
        selectAsciiViewFontButton.setPreferredSize(new java.awt.Dimension(24, 22));
        toolBarPanel.add(selectAsciiViewFontButton);

        add(toolBarPanel, java.awt.BorderLayout.PAGE_START);

        binaryFileViewSplitPane.setDividerSize(4);
        binaryFileViewSplitPane.setResizeWeight(0.7);
        binaryFileViewSplitPane.setDoubleBuffered(true);
        binaryFileViewSplitPane.setPreferredSize(new java.awt.Dimension(800, 512));

        hexViewScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        hexViewScrollPane.setPreferredSize(new java.awt.Dimension(512, 288));

        hexViewTable.setCellSelectionEnabled(true);
        hexViewTable.setDoubleBuffered(true);
        hexViewTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        hexViewTable.setShowHorizontalLines(false);
        hexViewTable.setShowVerticalLines(false);
        hexViewTable.getTableHeader().setReorderingAllowed(false);
        hexViewScrollPane.setViewportView(hexViewTable);

        binaryFileViewSplitPane.setLeftComponent(hexViewScrollPane);

        asciiViewScrollPane.setPreferredSize(new java.awt.Dimension(320, 288));

        asciiViewTable.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        asciiViewTable.setCellSelectionEnabled(true);
        asciiViewTable.setDoubleBuffered(true);
        asciiViewTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        asciiViewTable.setShowHorizontalLines(false);
        asciiViewTable.setShowVerticalLines(false);
        asciiViewTable.getTableHeader().setReorderingAllowed(false);
        asciiViewScrollPane.setViewportView(asciiViewTable);

        binaryFileViewSplitPane.setRightComponent(asciiViewScrollPane);

        add(binaryFileViewSplitPane, java.awt.BorderLayout.CENTER);

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bottomPanel.setMinimumSize(new java.awt.Dimension(256, 38));
        bottomPanel.setPreferredSize(new java.awt.Dimension(128, 32));
        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 4));

        statusLabel.setText("status");
        bottomPanel.add(statusLabel);

        add(bottomPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton asciiNonPrintableViewToggleButton;
    private javax.swing.JScrollPane asciiViewScrollPane;
    private javax.swing.JTable asciiViewTable;
    private javax.swing.JSplitPane binaryFileViewSplitPane;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JToggleButton decimalViewToggleButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JScrollPane hexViewScrollPane;
    private javax.swing.JTable hexViewTable;
    private javax.swing.JButton mergeButton;
    private javax.swing.JButton selectAsciiViewFontButton;
    private javax.swing.JSeparator separator;
    private javax.swing.JSeparator separator2;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel toolBarPanel;
    // End of variables declaration//GEN-END:variables
    
    // --------------------- TopComponent override
    @Override
    protected String preferredID() {
        return "binary.editor";
    }
    
    // --------------------- TopComponent override
    @Override
    public String getName() {
        return this.binaryDataObject.getName();
    }
    
    // --------------------- TopComponent override
    @Override
    public java.awt.Image getIcon() {
        if(this.tabIcon == null) {
            this.tabIcon = I18N.getImage("binary.file.tree.icon");
        }
        return this.tabIcon;
    }
    
    // --------------------- TopComponent override
    @Override
    public String getHtmlDisplayName() {
        DataObject dataObject = getLookup().lookup(DataObject.class);
        if(dataObject != null && dataObject.isModified()) {
            String binaryFileName = dataObject.getPrimaryFile().getNameExt();
            return "<html><b>" + binaryFileName + "</b></html>";
        }
        return super.getHtmlDisplayName();
    }
    
    // --------------------- TopComponent override
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }   
    
    // --------------------- TopComponent override
    @Override
    public boolean canClose() {
        
        // if the user changed the binary file and not saved it yet, ask for confirmation
        if(this.binaryDataObject.isModified()) {
            
            // save dialog file name and text buttons
            String fileName = this.binaryDataObject.getPrimaryFile().getNameExt();
            int userOption = DialogUtil.saveChanges(this, fileName);
            
            // user chooses the save option
            switch (userOption) {
                
                case JOptionPane.YES_OPTION:
                    // save data and close binary editor window (keep changes)
                    org.openide.util.Lookup lookup = this.binaryDataObject.getLookup();
                    SaveDataObjectCookie saveDataObject = lookup.lookup(SaveDataObjectCookie.class);
                    if(saveDataObject != null) {
                        saveDataObject.save();
                    }   
                    break;
                    
                case JOptionPane.NO_OPTION:
                    // don't save and close editor window (discard changes)
                    try {
                        this.binaryDataObject.setValid(false);
                    } catch (PropertyVetoException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    break;
                    
                default:
                    // keep the editor window open and don't save
                    return false;   
            }   
        }   
        
        return true;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public JComponent getToolbarRepresentation() {
        return this.toolBarPanel;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public Action[] getActions() {
        return new Action[0];
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public void componentOpened() {
        // the binary object should never be null!!!
        if(this.binaryDataObject != null) {
            this.binaryDataObject.getPrimaryFile().addFileChangeListener(this);
        }
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public void componentClosed() {
        // the binary object should never be null!!!
        if(this.binaryDataObject != null) {
            this.binaryDataObject.getPrimaryFile().removeFileChangeListener(this);
        }
    }
    
    // --------------------- MultiViewElement implementation
    @Override public void componentShowing() {}
    @Override public void componentHidden() {}
    @Override public void componentActivated() {}
    @Override public void componentDeactivated() {}
    
    // --------------------- MultiViewElement implementation
    @Override
    public org.openide.awt.UndoRedo getUndoRedo() {
        return org.openide.awt.UndoRedo.NONE;
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
        this.callback.updateTitle(this.binaryDataObject.getPrimaryFile().getNameExt());
    }
    
    // --------------------- MultiViewElement implementation
    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
    
    // --------------------- AdjustmentListener implementation
    @Override
    public void adjustmentValueChanged(AdjustmentEvent event) {
        int value = event.getValue();
        this.hexViewScrollPane.getVerticalScrollBar().setValue(value);
    }
    
    /**
     * Return the address header cell renderer 
     * @param row
     * @param column
     * @return 
     */
    private TableCellRenderer getAddressColumnRenderer() {
        return this.addressColumnRenderer;
    }
    
    protected void cleanStatusPanel() {
        this.statusLabel.setText(Default.EMPTY_STRING);
    }
    
    /**
     * Update the status panel. Shows the byte value representation when a single cell is selected.
     * @param value the byte value
     */
    protected void updateStatusPanel(byte value) {
        try {
            String binary = TextUtil.byteToBinaryString(value);
            if (this.decimalViewToggleButton.isSelected()) {
                String hexadecimal = TextUtil.byteToHexString(value);
                this.statusLabel.setText(I18N.getString("hexadecimal.binary.values", hexadecimal, binary));
            } else {
                this.statusLabel.setText(I18N.getString("decimal.binary.values", (value & 0xFF), binary));
            }   
        } catch (NumberFormatException exception) {
            this.cleanStatusPanel();
        }   
    }   
    
    /**
     * Show the total sum of all selected cells.
     * @param sum 
     */
    protected void updateStatusPanel(long sum) {
        
        String sumString = Long.toString(sum);
        if (!this.decimalViewToggleButton.isSelected()) {
            sumString = TextUtil.longToHexString(sum);
        }
        
        this.statusLabel.setText(I18N.getString("sum.of.selected.positions", sumString));
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        
        BinaryEditorAction action = BinaryEditorAction.valueOf(event.getActionCommand());
        
        switch(action) {
            
            case TOGGLE_ASCII_VIEW:
                boolean showNonPrintableChars = this.asciiNonPrintableViewToggleButton.isSelected();
                this.byteAsciiViewTableModel.setNonPrintableCharsViewEnabled(showNonPrintableChars);
                break;
                
            case TOGGLE_DECIMAL_VIEW:
                boolean showDecimalValues = this.decimalViewToggleButton.isSelected();
                this.byteHexValueTableModel.setDecimalViewEnabled(showDecimalValues);
                break;
                
            case EXPORT_BINARY:
                this.exportBinaryData();
                break;
                
            case MERGE_FROM_FILE:
                this.mergeBinaryData();
                break;
                
            case SELECT_FONT:
                this.selectAsciiViewFont();
                break;
                
            default:
                break;
        }
    }
    
    // --------------------- FileChangeListener implementation
    @Override
    public void fileChanged(FileEvent fe) {
        SwingUtilities.invokeLater(this::updateBinaryDataObject);
    }   
    
    // --------------------- FileChangeListener implementation
    @Override
    public void fileDeleted(FileEvent fileEvent) {
        this.binaryDataObject.setChanged(false);
        SwingUtilities.invokeLater(this::close);
    }
    
    // --------------------- FileChangeListener implementation
    @Override public void fileFolderCreated(FileEvent fe) {}
    @Override public void fileDataCreated(FileEvent fe) {}
    @Override public void fileRenamed(FileRenameEvent fre) {}
    @Override public void fileAttributeChanged(FileAttributeEvent fae) {}
    
    // --------------------- PropertyChangeListener implementation
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        
        if (DataObject.PROP_MODIFIED.equals(event.getPropertyName())) {
            
            // the binary file name with extension
            final DataObject dataObject = getLookup().lookup(DataObject.class);
            final String binaryFileName = dataObject.getPrimaryFile().getNameExt();
            
            SwingUtilities.invokeLater(() -> {
                setDisplayName(Boolean.TRUE.equals(event.getNewValue()) ? binaryFileName + "*" : binaryFileName);
            });
        }
    }
    
    /**
     * 
     * @param properties 
     */
    void writeProperties(java.util.Properties properties) {
        
        DataObject dataObject = this.getLookup().lookup(DataObject.class);
        
        if (dataObject != null) {
            FileObject primaryFile = dataObject.getPrimaryFile();
            if (primaryFile != null) { // could be a virtual template file not really on disk
                String path = primaryFile.getPath();
                properties.setProperty(KEY_FILE_PATH, path);
            }   
        }
    }
    
    /**
     * Read file properties.
     * @param properties 
     */
    void readProperties(java.util.Properties properties) {
        
        String path = properties.getProperty(KEY_FILE_PATH);
        try {
            java.io.File binaryFile = new java.io.File(path);
            if (binaryFile.exists()) {
                
                FileObject fileObject = FileUtil.toFileObject(FileUtil.normalizeFile(binaryFile));
                DataObject dataObject = DataObject.find(fileObject);
                
                // a DataObject always has itself in its Lookup, so do this to cast
                BinaryDataObject binaryData = dataObject.getLookup().lookup(BinaryDataObject.class);
                if (binaryData == null) {
                    throw new java.io.IOException(I18N.getString("wrong.file.type"));
                }
                
                // get the binary data object associated to be visualized
                binaryData.load();
                assert binaryData != null;
                
                // update the data object reference
                this.binaryDataObject = binaryData;
                this.setDataObject(binaryData);
                
                // ensure Open does not create another editor by telling the DataObject about this editor
                OpenBinaryEditorCookie openBinaryCookie = this.getLookup().lookup(OpenBinaryEditorCookie.class);
                openBinaryCookie.setTopComponent(this);
                openBinaryCookie.open();
                
            } else {
                throw new java.io.IOException(I18N.getString("path.does.not.exist", path));
            }   
            
        } catch (java.io.IOException ex) {
            //Could not load the file for some reason
            throw new IllegalStateException(ex);
        }
    }
    
    /** 
     * Reload the binary data object from file.
     * This method is invocated when some change occurs on the binary file (even when the change is made
     * outside from IDE).
     */
    private void updateBinaryDataObject() {
        
        try {
            // reload the binary object to refresh the changes on the editor
            this.binaryDataObject.load();
        } catch (java.io.IOException exception) {
            java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
            JOptionPane.showMessageDialog(frame,
                I18N.getString("error.reading.file", this.binaryDataObject.getName()), // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(this::close);
        }   
        
        // update the ascii and binary views
        this.byteHexValueTableModel.fireTableDataChanged();
        this.byteAsciiViewTableModel.fireTableDataChanged();
    }   
    
    /**
     * Exports the binary data.
     */
    private void exportBinaryData() {
        
        // the sprite data object that holds all sprites to be exported
        final BinaryDataObject dataObject = this.binaryDataObject;
        
        if (this.binaryExporterDialog == null) {
            
            // create the export dialog using the main window as parent
            java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
            this.binaryExporterDialog = new BinaryExporterDialog(frame, true);
            
            // set dialog properties
            this.binaryExporterDialog.setLocationRelativeTo(frame);
            this.binaryExporterDialog.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        }   
        
        // show the export dialog to the user
        this.binaryExporterDialog.setDataObject(dataObject);
        this.binaryExporterDialog.setSelection(this.asciiViewTable.getSelectedRows(),
                                               this.asciiViewTable.getSelectedColumns());
        this.binaryExporterDialog.setVisible(true);
    }   
    
    /**
     * Merge a binary data to the current binary data.
     */
    private void mergeBinaryData() {
        
    }
    
    /**
     * Select the ASCII view font
     */
    private void selectAsciiViewFont() {
        
        // create the font selection dialog and set the current font equals to the ascii view font
        PropertyEditor propertyEditor = PropertyEditorManager.findEditor(java.awt.Font.class);
        propertyEditor.setValue(this.asciiViewTable.getFont());
        DialogDescriptor dd = new DialogDescriptor (
            propertyEditor.getCustomEditor(), I18N.getString("select.ascii.view.font")
        );
        
        // show the selection dialog and update the ascii view font if selecion is ok
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        if (dd.getValue().equals(DialogDescriptor.OK_OPTION)) {
            this.asciiViewTable.setFont((java.awt.Font) propertyEditor.getValue());
        }
    }
    
    /**
     * Cell renderer that specifies the look of address cells in the table.
     * docs: http://docs.oracle.com/javase/7/docs/api/java/awt/SystemColor.html
     */
    private class AddressTableCellRenderer extends DefaultTableCellRenderer {
        
        public AddressTableCellRenderer() {
            this.setOpaque(true);
        }   
        
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object object,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            
            // set the visual properties of the address column
            java.awt.Component component = super.getTableCellRendererComponent(table, object, false, false, row, column);
            component.setBackground(java.awt.SystemColor.scrollbar);
            component.setFocusable(false);
            this.setHorizontalAlignment(SwingConstants.CENTER);
            
            return component;
        }
    }
}   
