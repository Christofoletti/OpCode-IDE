/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.dialogs;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.ColorUtil;
import br.com.objectware.commons.utils.DialogUtil;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * MSX mode 2 sprite row color selector.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 02/nov/2015
 */
public class RowColorCodesSelectionDialog extends javax.swing.JDialog implements ActionListener {

    private enum ACTION {CHANGE_ALL, SELECT_COLOR, ACCEPT, CANCEL};
    
    /** The color buttons selection */
    private final java.util.List<JButton> colorCodeButtons = new java.util.ArrayList<>();
    
    /** The color codes selection */
    private final java.util.List<Byte> colorCodes = new java.util.ArrayList<>();
    
    /** The current sprite set palette colors */
    private final java.util.List<Color> palette = new java.util.ArrayList<>();
    
    /** The color code chooser dialog */
    private final ColorCodeSelectionDialog colorCodeChooserDialog;
    
    /** Flag used to indicate that a selection was made*/
    private boolean selectionAccepted = false;
    
    /** The last user color code selection */
    private byte lastSelectedColorCode = 1;
    
    /**
     * Creates new form MsxSpriteLineColorsSelectionDialog
     */
    public RowColorCodesSelectionDialog(java.awt.Frame parent, boolean modal) {
        
        super(parent, modal);
        
        this.initComponents();
        this.setupListeners();
        
        // setup the color code chooser dialog
        this.colorCodeChooserDialog = new ColorCodeSelectionDialog(parent, true);
    }   
    
    /**
     * Set the listeners for visual elements.
     */
    private void setupListeners() {
        this.changeAllColorsButton.addActionListener(this);
        this.okButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
    }
    
    /**
     * Set the current palette. The panel is filled with buttons representing the color codes.
     * NOTE: THIS METHOD MUST BE CALLED BEFORE setSelectedColorCodes().
     * @param palette the color palette
     */
    public void setPalette(java.util.List<Color> palette) {
        if(palette != null) {
            this.palette.clear();
            this.palette.addAll(palette);
            this.colorCodeChooserDialog.setPalette(palette);
        }   
    }   
    
    /**
     * Set the current color codes array.
     * @param colorCodes the array of color codes (MSX)
     */
    public void setSelectedColorCodes(byte[] colorCodes) {
        
        // cleanup the lists of buttons and color codes
        this.colorCodeButtons.clear();
        this.colorCodes.clear();
        
        // evaluate the color selection table size
        int height = 16 * colorCodes.length;
        int width = 192;
        
        // adjust the layout to accomodate the color buttons
        this.paletteColorsPanel.setPreferredSize(new java.awt.Dimension(width, height));
        this.paletteColorsPanel.removeAll();
        
        // add the color buttons to the color selection panel
        for(int row = 0; row < colorCodes.length; row++) {
            byte colorCode = colorCodes[row];
            JButton colorButton = this.newColorButton(colorCode);
            this.paletteColorsPanel.add(colorButton);
        }   
    }   
    
    /**
     * Return the selected color codes array.
     * @return 
     */
    public byte[] getSelectedColorCodes() {
        
        // convert the current list of color codes into a byte array
        byte[] colorCodesArray = new byte[this.colorCodes.size()];
        for(int k = 0; k < this.colorCodes.size(); k++) {
            colorCodesArray[k] = this.colorCodes.get(k);
        }   
        
        return colorCodesArray;
    }   
    
    /**
     * Status of user selection.
     * @return true if the user accepted the selected color
     */
    public boolean isSelectionAccepted() {
        return this.selectionAccepted;
    }
    
    /**
     * Reset the user selection flag
     */
    public void clearUserSelection() {
        this.selectionAccepted = false;
    }   
    
    /**
     * Create a new color code selector button.
     * @param colorCode the associated color code
     * 
     * @return the new button created
     */
    private JButton newColorButton(byte colorCode) {
        
        // the color code visual representation
        JButton colorButon = new JButton();
        this.colorCodeButtons.add(colorButon);
        this.colorCodes.add(colorCode);
        
        // get the row and color code of button
        int row = this.colorCodeButtons.indexOf(colorButon);
        Color color = this.palette.get(colorCode);
        
        // set the visual properties of the button
        colorButon.setText(this.getButtonText(row, colorCode));
        colorButon.setBackground(color);
        colorButon.setForeground(ColorUtil.getContrastColor(color));
        colorButon.setContentAreaFilled(false);
        colorButon.setOpaque(true);
        
        // setup the listener property for the button
        colorButon.setActionCommand(ACTION.SELECT_COLOR.name());
        colorButon.addActionListener(this);
        
        return colorButon;
    }   
    
    /**
     * Generate the button text according to the selected color code and row (I18N).
     * @param row the MSX sprite row
     * @param colorCode the MSX color code
     * @return 
     */
    private String getButtonText(int row, byte colorCode) {
        String buttonTextFormat = I18N.getString("sprite.row.color.code");
        return String.format(buttonTextFormat, row, colorCode);
    }   
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paletteColorsPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        changeAllColorsButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(I18N.getString("select.color.for.each.row")
        );
        setMinimumSize(new java.awt.Dimension(212, 352));
        setPreferredSize(new java.awt.Dimension(352, 486));

        paletteColorsPanel.setPreferredSize(new java.awt.Dimension(320, 6448));
        paletteColorsPanel.setLayout(new java.awt.GridLayout(16, 1, 2, 2));
        getContentPane().add(paletteColorsPanel, java.awt.BorderLayout.CENTER);

        buttonsPanel.setMinimumSize(new java.awt.Dimension(184, 32));
        buttonsPanel.setPreferredSize(new java.awt.Dimension(412, 38));
        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 8));

        org.openide.awt.Mnemonics.setLocalizedText(changeAllColorsButton, I18N.getString("change.all.colors"));
        changeAllColorsButton.setActionCommand(ACTION.CHANGE_ALL.name());
        changeAllColorsButton.setMinimumSize(new java.awt.Dimension(80, 23));
        changeAllColorsButton.setPreferredSize(new java.awt.Dimension(128, 23));
        buttonsPanel.add(changeAllColorsButton);

        org.openide.awt.Mnemonics.setLocalizedText(okButton, I18N.getString("ok"));
        okButton.setActionCommand(ACTION.ACCEPT.name());
        okButton.setMinimumSize(new java.awt.Dimension(80, 23));
        okButton.setPreferredSize(new java.awt.Dimension(80, 23));
        buttonsPanel.add(okButton);

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, I18N.getString("cancel"));
        cancelButton.setActionCommand(ACTION.CANCEL.name());
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 23));
        cancelButton.setPreferredSize(new java.awt.Dimension(80, 23));
        buttonsPanel.add(cancelButton);

        getContentPane().add(buttonsPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton changeAllColorsButton;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel paletteColorsPanel;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void actionPerformed(ActionEvent event) {
        
        // the event properties
        Object source = event.getSource();
        ACTION actionCommand = ACTION.valueOf(event.getActionCommand());
        
        switch(actionCommand) {
            
            case CHANGE_ALL:
                this.updateAllColorCodes();
                break;
                
            case SELECT_COLOR:
                this.updateSelectedColorCode((JButton) source);
                break;
                
            case ACCEPT:
                this.selectionAccepted = true;
                this.setVisible(false);
                break;
                
            case CANCEL:
                this.setVisible(false);
                break;
        }
    }
    
    /**
     * Update the selected color code. The button's color is also updated.
     * The button is the key to the color code.
     * @param colorCodeButton
     */
    private void updateSelectedColorCode(JButton button) {
        
        // get the color code associated to the button
        int index = this.colorCodeButtons.indexOf(button);
        byte colorCode = this.colorCodes.get(index);
        
        // show the color code selection dialog
        this.colorCodeChooserDialog.clearUserSelection();
        this.colorCodeChooserDialog.setSelectedColorCode(colorCode);
        DialogUtil.show(this.colorCodeChooserDialog);
        
        // if there is a valid user selection
        if(this.colorCodeChooserDialog.isSelectionAccepted()) {
            
            // update the color codes list
            byte selectedColorCode = this.colorCodeChooserDialog.getSelectedColorCode();
            this.colorCodes.set(index, selectedColorCode);
            
            // update the buttons color and text
            this.setColorForButton(button, index, selectedColorCode);
            this.lastSelectedColorCode = selectedColorCode;
        }   
    }
    
    /**
     * 
     */
    private void updateAllColorCodes() {
        
        // show the color code selection dialog
        this.colorCodeChooserDialog.clearUserSelection();
        this.colorCodeChooserDialog.setSelectedColorCode(this.lastSelectedColorCode);
        DialogUtil.show(this.colorCodeChooserDialog);
        
        // if there is a valid user selection
        if(this.colorCodeChooserDialog.isSelectionAccepted()) {
            
            // get the selected color code
            byte selectedColorCode = this.colorCodeChooserDialog.getSelectedColorCode();
            
            // update all buttons and color codes with selected color code
            for(int index = 0; index < this.colorCodes.size(); index++) {
                
                this.colorCodes.set(index, selectedColorCode);
                
                // update the buttons color and text
                JButton button = this.colorCodeButtons.get(index);
                this.setColorForButton(button, index, selectedColorCode);
            }
        }
    }
    
    /**
     * Set the new color for the given button and update the button's text
     * @param colorCodeButton
     * @param colorCode 
     */
    private void setColorForButton(JButton colorCodeButton, int index, byte colorCode) {
        
        // update the buttons color and text
        Color color = this.palette.get(colorCode);
        
        // update the button properties
        colorCodeButton.setBackground(color);
        colorCodeButton.setForeground(ColorUtil.getContrastColor(color));
        colorCodeButton.setText(this.getButtonText(index, colorCode));
    }
}
