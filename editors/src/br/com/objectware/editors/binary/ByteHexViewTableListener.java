/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.binary;

import br.com.objectware.commons.utils.Default;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * This class is very similar to ByteAsciiViewTableListener. Some methods and fields of this class
 * could be moved to a parent class for both listeners (ascii and hex). Will stay in the TODO list...
 * 
 * @author Luciano M. Christofoletti
 * @since 17/May/2015
 */
public class ByteHexViewTableListener
       implements TableModelListener, KeyListener, ListSelectionListener, FocusListener {
    
    /** The hex table visual component */
    private final JTable hexTable;
    
    /** The ascii table visual component (share the same binary data) */
    private final JTable asciiTable;
    
    /** The main visual component that must be update by the listener */
    private final BinaryEditorTopComponent visualElement;
    
    /**
     * Listener for the hex viewer.
     * @param visualElement the associated visual element 
     */
    public ByteHexViewTableListener(BinaryEditorTopComponent visualElement) {
        
        // get the visual elements references
        this.visualElement = visualElement;
        this.hexTable = visualElement.getHexTable();
        this.asciiTable = visualElement.getAsciiTable();
        
        this.setupListeners();
    }
    
    /**
     * 
     * @param visualElement
     * @return 
     */
    public static ByteHexViewTableListener createNewListenerTo(BinaryEditorTopComponent visualElement) {
        return new ByteHexViewTableListener(visualElement);
    }
    
    /**
     * 
     */
    private void setupListeners() {
        
        this.hexTable.getModel().addTableModelListener(this);
        this.hexTable.addKeyListener(this);
        this.hexTable.getSelectionModel().addListSelectionListener(this);
        this.hexTable.getColumnModel().getSelectionModel().addListSelectionListener(this);
        
        this.hexTable.addFocusListener(this);
    }
    
    @Override
    public void tableChanged(TableModelEvent event) {
        if(this.asciiTable != null) {
            this.asciiTable.updateUI();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent event) {
        
        Object source = event.getSource();
        int modifiers = event.getModifiers();
        boolean dataChanged = true;
        
        int row = this.hexTable.getSelectedRow();
        int column = this.hexTable.getSelectedColumn();
        
        switch(event.getKeyChar()) {
            
            case KeyEvent.VK_ENTER:
                dataChanged = false;
                break;
                
            case KeyEvent.VK_TAB:
                column--;
                this.asciiTable.requestFocus();
                dataChanged = false;
                break;
                
            default:
                break;
        }
        
        // update ascii table selected cell
        this.hexTable.setColumnSelectionInterval(column, column);
        this.hexTable.setRowSelectionInterval(row, row);
        
        // fire data changed event
        if(dataChanged) {
            BinaryDataObject dataObject = this.visualElement.getDataObject();
            if(dataObject != null) {
                dataObject.setChanged(true);
            }
        }
        
        this.visualElement.cleanStatusPanel();
        
        // consume the event
        event.consume();
    }
    
    @Override
    public void keyPressed(KeyEvent event) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void valueChanged(ListSelectionEvent event) {
        
        // assume that there is no valid selection
        this.visualElement.cleanStatusPanel();
        
        // update the hex table only when this component has the focus
        if(!this.hexTable.hasFocus()) {
            return;
        }
        
        int selectedRows[] = this.hexTable.getSelectedRows();
        int selectedColumns[] = this.hexTable.getSelectedColumns();
        
//        // the first column is not "selectable"
//        if(this.hexTable.getSelectedColumn() < 1) {
//            
//            // 
//            //this.hexTable.getColumnModel().getSelectionModel().setSelectionInterval(1, 1);
//            selectedRows = Arrays.copyOfRange(selectedRows, 1, selectedRows.length - 1);
//            int startColumn = selectedColumns[0];
//            int endColumn = selectedColumns[selectedColumns.length - 1];
//            this.hexTable.getColumnModel().getSelectionModel().setSelectionInterval(startColumn, endColumn);
//            
//        }
        
        if(selectedRows.length > 0) {
            int startRow = selectedRows[0];
            int endRow = selectedRows[selectedRows.length - 1];
            this.asciiTable.getSelectionModel().setSelectionInterval(startRow, endRow);
        }   
        if(selectedColumns.length > 0) {
            int startColumn = selectedColumns[0];
            int endColumn = selectedColumns[selectedColumns.length - 1];
            this.asciiTable.getColumnModel().getSelectionModel()
                .setSelectionInterval(startColumn - 1, endColumn - 1);
        }   
        
        BinaryDataObject dataObject = this.visualElement.getDataObject();
        
        // show the correct satus accordingly to the user selction
        if(selectedRows.length == 1 && selectedColumns.length == 1) {
            
            // the original data is a hexa string, so we must parse it to int
            try {
                byte value = dataObject.getValueAt(selectedRows[0], selectedColumns[0] - 1);
                this.visualElement.updateStatusPanel(value);
            } catch(IndexOutOfBoundsException exception) {
                this.visualElement.cleanStatusPanel();
            }
            
        } else {
            
            // show checksum value
            long sum = 0L;
            for(int row:selectedRows) {
                for(int column:selectedColumns) {
                    if(column > 0) {
                        try {
                            sum += (0xFF & dataObject.getValueAt(row, column - 1));
                        } catch(IndexOutOfBoundsException exception) {
                            // there may be an selected cell that does not has a value
                        }   
                    }
                }
            }
            
            // show the total sum of all selected cells
            this.visualElement.updateStatusPanel(sum);
        }
    }   
    
    @Override
    public void focusGained(FocusEvent e) {
        this.hexTable.setSelectionForeground(Color.WHITE);
        this.hexTable.setSelectionBackground(Default.TABLE_BACKGROUND_SELECTION);
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        this.hexTable.setSelectionForeground(Color.BLACK);
        this.hexTable.setSelectionBackground(Default.TABLE_BACKGROUND_HIGHLIGHT);
    }
    
}
