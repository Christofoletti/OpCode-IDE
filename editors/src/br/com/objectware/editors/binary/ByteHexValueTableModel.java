/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.binary;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import javax.swing.table.AbstractTableModel;

/**
 * This class define the table model for viewing/editing binary files (hexadecimal values view).
 * The table model manages the data (get/set) and it's representation
 * 
 * @author Luciano M. Christofoletti
 * @since 28/apr/2015
 */
public final class ByteHexValueTableModel extends AbstractTableModel {
    
    /** The header of hexadecimal view table columns */
    private static final String COLUMNS_TITLES[] = {
        I18N.getString("address"),
        "00", "01", "02", "03", "04", "05", "06", "07",
        "08", "09", "0A", "0B", "0C", "0D", "0E", "0F"
    };
    
    // address and empty cell formatters
    private static final String EMPTY_CELL = "##";
    private static final String ADDRESS_FORMATTER_SHORT = "0x%04X";
    private static final String ADDRESS_FORMATTER_LONG = "0x%08X";
    private static final String DEC_VIEW = "%03d";
    private static final String HEX_VIEW = "%02X";
    
    // the current address formatter
    private String addressFormatter = ADDRESS_FORMATTER_SHORT;
    
    /** The binary data object */
    private BinaryDataObject dataObject;
    
    /** Hexadecimal data formatter */
    private final javax.xml.bind.annotation.adapters.HexBinaryAdapter hexAdapter;
    
    /** The byte view format flag (true for decimal, false for hexadecimal) */
    private boolean decimalViewEnabled = false;
    
    /**
     * The binary hex/dec view model
     * @param dataObject 
     */
    public ByteHexValueTableModel(BinaryDataObject dataObject) {
        this.setData(dataObject);
        this.hexAdapter = new javax.xml.bind.annotation.adapters.HexBinaryAdapter();
    }   
    
    /**
     * Binary data object access method.
     * @return 
     */
    private BinaryDataObject getData() {
        return this.dataObject;
    }   
    
    /**
     * Set the binary data of table.
     * @param data the binary data object
     */
    protected void setData(BinaryDataObject dataObject) {
        
        // do not set null data
        assert dataObject != null;
        this.dataObject = dataObject;
        
        this.adjustAddressFormatter(dataObject.getLength());
        this.fireTableDataChanged();
    }   
    
    private void adjustAddressFormatter(int length) {
        // setup the address formatter
        if (length < 65536) {
            this.addressFormatter = ADDRESS_FORMATTER_SHORT;
        } else {
            this.addressFormatter = ADDRESS_FORMATTER_LONG;
        }
    }
    
    @Override
    public String getColumnName(int index) {
        return COLUMNS_TITLES[index];
    }
    
    @Override
    public int getRowCount() {
        double dataLength = this.getData().getLength();
        return (int) Math.ceil(dataLength / Default.HEX_TABLE_WIDTH);
    }   
    
    @Override
    public int getColumnCount() {
        return COLUMNS_TITLES.length;
    }
    
    @Override
    public String getValueAt(int row, int column) {
        
        // return the row start address for the first column
        if(column == 0) {
            int address = row * Default.HEX_TABLE_WIDTH;
            return String.format(this.addressFormatter, address & 0xFFFFFFFF);
        }   
        
        if(this.getData().validateIndexes(row, column-1)) {
            int value = (this.getData().getValueAt(row, column - 1) & 0xFF);
            return String.format(this.decimalViewEnabled ? DEC_VIEW:HEX_VIEW, value);
        } else {
            // return an empty cell representation
            return EMPTY_CELL;
        }   
    }   
    
    @Override
    public boolean isCellEditable(int row, int column) {
        // with exception of the first column, all other cells are editable
        return (column > 0);
    }   
    
    /**
     * Set the decimal representation view format
     * @param decimalView 
     */
    public void setDecimalViewEnabled(boolean decimalView) {
        this.decimalViewEnabled = decimalView;
        this.fireTableDataChanged();
    }   
    
    @Override
    public void setValueAt(Object value, int row, int column) {
        
        int index = row * Default.HEX_TABLE_WIDTH + (column - 1);
        
        try {
            String stringValue = value.toString();
            
            if(this.decimalViewEnabled) {
                this.getData().setValueAt(index, (byte) Integer.parseInt(stringValue));
            } else {
                byte[] byteValue = this.hexAdapter.unmarshal(stringValue);
                if(byteValue != null && byteValue.length > 0) {
                    this.getData().setValueAt(index, byteValue[0]);
                }   
            }   
            
        } catch(IllegalArgumentException iae) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch(IndexOutOfBoundsException exception) {
            // do nothing here. this may occur in the last cells of the table
        }   
        
        this.fireTableCellUpdated(row, column);
        //dataObject.setChanged(true);
    }
}
