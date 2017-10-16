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
 * This class is very similar to ByteHexViewTableListener. Some methods and fields of this class
 * could be moved to a parent class for both listeners (ascii and hex). Will stay in the TODO list...
 * 
 * @author Luciano M. Christofoletti
 * @since 17/May/2015
 */
public class ByteAsciiViewTableListener
       implements TableModelListener, KeyListener, ListSelectionListener, FocusListener {
    
    /** The ascii table visual component (share the same binary data) */
    private final JTable asciiTable;
    
    /** The ascii table visual component */
    private final JTable hexTable;
    
    /** The main visual component that must be update by the listener */
    private final BinaryEditorTopComponent visualElement;
    
    /**
     * Listener for the ascii viewer.
     * @param visualElement the associated visual element 
     */
    public ByteAsciiViewTableListener(BinaryEditorTopComponent visualElement) {
        
         // get the visual elements references
        this.visualElement = visualElement;
        this.asciiTable = visualElement.getAsciiTable();
        this.hexTable = visualElement.getHexTable();
        
        this.setupListeners();
    }
    
    /**
     * 
     * @param visualElement
     * @return 
     */
    public static ByteAsciiViewTableListener createNewListenerTo(BinaryEditorTopComponent visualElement) {
        return new ByteAsciiViewTableListener(visualElement);
    }
    
    /**
     * 
     */
    private void setupListeners() {
        
        this.asciiTable.getModel().addTableModelListener(this);
        this.asciiTable.addKeyListener(this);
        this.asciiTable.getSelectionModel().addListSelectionListener(this);
        this.asciiTable.getColumnModel().getSelectionModel().addListSelectionListener(this);
        
        this.asciiTable.addFocusListener(this);
    }
    
    @Override
    public void tableChanged(TableModelEvent e) {
        if(this.hexTable != null) {
            this.hexTable.updateUI();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent event) {
        
        boolean dataChanged = true;
        int row = this.asciiTable.getSelectedRow();
        int column = this.asciiTable.getSelectedColumn();
        
        switch(event.getKeyChar()) {
            
            case KeyEvent.VK_ENTER:
                dataChanged = false;
                break;
                
            case KeyEvent.VK_BACK_SPACE:
                this.fillSelection((char) 0);
                // go to previous byte cell
                if(row < 1) {
                    column = Math.max(0, --column);
                } else if(--column < 0) {
                    column = this.asciiTable.getColumnCount() - 1;
                    row--;
                }   
                break;
                
            case KeyEvent.VK_DELETE:
                this.fillSelection((char) 0);
                break;
                
            case KeyEvent.VK_TAB:
                column--;
                this.hexTable.requestFocus();
                dataChanged = false;
                break;
                
            default:
                this.fillSelection(event.getKeyChar());
                // go to next byte cell
                if (++column >= this.asciiTable.getColumnCount()) {
                    column = 0;
                    row = (row + 1 < this.asciiTable.getRowCount()) ? ++row:row;
                }
            
        }
        
        // fire data changed event
        if(dataChanged) {
            BinaryDataObject dataObject = this.visualElement.getDataObject();
            if(dataObject != null) {
                dataObject.setChanged(true);
            }
        }
        
        // update ascii table selected cell
        this.asciiTable.setColumnSelectionInterval(column, column);
        this.asciiTable.setRowSelectionInterval(row, row);
        this.visualElement.cleanStatusPanel();
        
        // consume the event
        event.consume();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    /**
     * Fill the selected cells with value
     * @param value the char value to fill the selection
     */
    private void fillSelection(char value) {
        for(int row : this.asciiTable.getSelectedRows()) {
            for(int column : this.asciiTable.getSelectedColumns()) {
                this.asciiTable.setValueAt(value, row, column);
            }
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent event) {
        
        // update the ascii table only when this component has the focus
        if(!this.asciiTable.hasFocus()) {
            return;
        }
        
        // assume that there is no valid selection
        this.visualElement.cleanStatusPanel();
        
        // assume that there is no valid selection
        int selectedRows[] = this.asciiTable.getSelectedRows();
        int selectedColumns[] = this.asciiTable.getSelectedColumns();
        
        // update the ascii table only when the selection is valid
        if(selectedRows.length > 0) {
            int startRow = selectedRows[0];
            int endRow = selectedRows[selectedRows.length - 1];
            this.hexTable.getSelectionModel().setSelectionInterval(startRow, endRow);
        }   
        if(selectedColumns.length > 0) {
            int startColumn = selectedColumns[0];
            int endColumn = selectedColumns[selectedColumns.length - 1];
            this.hexTable.getColumnModel().getSelectionModel().setSelectionInterval(startColumn + 1, endColumn + 1);
        }   
        
        BinaryDataObject dataObject = this.visualElement.getDataObject();
        
        // show the correct satus accordingly to the user selction
        if(selectedRows.length == 1 && selectedColumns.length == 1) {
            
            // the original data is a hexa string, so we must parse it to int
            try {
                byte value = dataObject.getValueAt(selectedRows[0], selectedColumns[0]);
                this.visualElement.updateStatusPanel(value);
            } catch(IndexOutOfBoundsException exception) {
                this.visualElement.cleanStatusPanel();
            }
            
        } else {
            
            // show checksum value
            long sum = 0L;
            for(int row:selectedRows) {
                for(int column:selectedColumns) {
                    try {
                        sum += (0xFF & dataObject.getValueAt(row, column));
                    } catch(IndexOutOfBoundsException exception) {
                        // there may be an selected cell that does not has a value
                    }
                }
            }
            
            // show the total sum of all selected cells
            this.visualElement.updateStatusPanel(sum);
        }
    }
    
    @Override
    public void focusGained(FocusEvent event) {
        this.asciiTable.setSelectionForeground(Color.WHITE);
        this.asciiTable.setSelectionBackground(Default.TABLE_BACKGROUND_SELECTION);
    }
    
    @Override
    public void focusLost(FocusEvent event) {
        this.asciiTable.setSelectionForeground(Color.BLACK);
        this.asciiTable.setSelectionBackground(Default.TABLE_BACKGROUND_HIGHLIGHT);
    }
    
}
