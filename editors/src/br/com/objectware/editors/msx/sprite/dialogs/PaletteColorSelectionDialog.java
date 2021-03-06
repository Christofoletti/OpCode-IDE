/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.dialogs;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.ColorUtil;
import br.com.objectware.domain.enums.MsxColor;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Select the palette color chooser. The dialog colors are disposed accordingly to the palette size.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 24/nov/2015
 */
public class PaletteColorSelectionDialog extends javax.swing.JDialog implements ActionListener {
    
    private enum ACTION {SELECT_COLOR, RESET_PALETTE, ACCEPT, CANCEL};
    
    /** The color buttons selection */
    private final java.util.List<JButton> paletteColorButtons = new java.util.ArrayList<>();
    
    /** Flag used to indicate that the new palette colors must be accepted */
    private boolean selectionAccepted = false;
    
    /** The color chooser */
    private final javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
    
    /**
     * Creates new form ColorCodeSelectionDialog
     */
    public PaletteColorSelectionDialog(java.awt.Frame parent, boolean modal) {
        
        super(parent, modal);
        
        this.initComponents();
        this.setupListeners();
    }   
    
    /**
     * Set the listeners for visual elements.
     */
    private void setupListeners() {
        this.resetPaletteButton.addActionListener(this);
        this.okButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
    }
    
    /**
     * Set the current palette. The panel is filled with buttons representing the color codes.
     * NOTE: the user selection is cleared when this method is called.
     * @param palette the color palette
     */
    public void setPalette(java.util.List<Color> palette) {
        
        // evaluate the color selection table size
        int height = 32 * (palette.size() / 8);
        int width = 64 * (palette.size() / 2);
        
        // adjust the layout to accomodate the color buttons
        this.paletteColorsPanel.setPreferredSize(new java.awt.Dimension(width, height));
        this.paletteColorsPanel.removeAll();
        
        // setup the color selection buttons
        for(Color paletteColor : palette) {
            
            // add the color button to the palette color selection panel
            JButton colorButton = this.newColorButton(paletteColor);
            this.paletteColorsPanel.add(colorButton);
            this.paletteColorButtons.add(colorButton);
            
            // set the button label as the color code number
            int colorNumber = this.paletteColorButtons.indexOf(colorButton);
            String colorCode = String.format("%02d", colorNumber & 0xFF);
            colorButton.setText(colorCode);
        }   
        
        // reset the color selection status
        this.pack();
    }   
    
    /**
     * Status of user selection.
     * @return true if the user accepted the selected color
     */
    public boolean isSelectionAccepted() {
        return this.selectionAccepted;
    }
    
    /**
     * Return the list of palette colors
     * @return 
     */
    public java.util.List<Color> getPaletteColors() {
        
        java.util.List<Color> paletteColors = new java.util.ArrayList<>();
        
        for(JButton colorButton:this.paletteColorButtons) {
            paletteColors.add(colorButton.getBackground());
        }   
        
        return paletteColors;
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
        resetPaletteButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(I18N.getString("palette.color.selection"));

        paletteColorsPanel.setPreferredSize(new java.awt.Dimension(320, 64));
        paletteColorsPanel.setLayout(new java.awt.GridLayout(2, 8, 2, 2));
        getContentPane().add(paletteColorsPanel, java.awt.BorderLayout.CENTER);

        buttonsPanel.setMinimumSize(new java.awt.Dimension(184, 38));
        buttonsPanel.setPreferredSize(new java.awt.Dimension(320, 38));
        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 8));

        org.openide.awt.Mnemonics.setLocalizedText(resetPaletteButton, I18N.getString("reset.palette.colors"));
        resetPaletteButton.setActionCommand(ACTION.RESET_PALETTE.name());
        resetPaletteButton.setMinimumSize(new java.awt.Dimension(80, 23));
        resetPaletteButton.setPreferredSize(new java.awt.Dimension(192, 23));
        buttonsPanel.add(resetPaletteButton);

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
    private javax.swing.JButton okButton;
    private javax.swing.JPanel paletteColorsPanel;
    private javax.swing.JButton resetPaletteButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent event) {
        
        // the event properties
        Object source = event.getSource();
        ACTION actionCommand = ACTION.valueOf(event.getActionCommand());
        
        switch(actionCommand) {
            
            case SELECT_COLOR:
                this.selectNewColor((JButton) source);
                break;
            
            case RESET_PALETTE:
                this.resetPaletteColors();
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
     * Select a new palette color.
     * @param colorButton the color button representing the palette color
     * @throws HeadlessException 
     */
    private void selectNewColor(JButton colorButton) throws HeadlessException {
        
        Color newColor = javax.swing.JColorChooser.showDialog(
                this.colorChooser,
                I18N.getString("select.new.color"),
                colorButton.getBackground());
        
        // set text and color for button
        if(newColor != null) {
            this.updateColorButton(colorButton, newColor);
        }   
    }
    
    /**
     * Create a new color code selector button.
     * @param colorCode the associated color code
     * @param color the color of the button
     * 
     * @return the new button created
     */
    private JButton newColorButton(Color color) {
        
        JButton colorButton = new JButton();
        colorButton.setContentAreaFilled(false);
        colorButton.setOpaque(true);
        
        // set text and color for button
        this.updateColorButton(colorButton, color);
        
        // setup the listener properties for the button
        colorButton.setActionCommand(ACTION.SELECT_COLOR.name());
        colorButton.addActionListener(this);
        
        return colorButton;
    }   
    
    /**
     * Update the color button text and colors
     * @param colorButon
     * @param color 
     */
    private void updateColorButton(JButton colorButton, Color color) {
        
        colorButton.setToolTipText(String.format("RGB #%06X", color.getRGB() & 0xFFFFFF));
        
        colorButton.setBackground(color);
        colorButton.setForeground(ColorUtil.getContrastColor(color));
        
    }
    
    /**
     * Reset the palette colors to original MSX colors palette.
     */
    private void resetPaletteColors() {
        for(int i = 0; i < this.paletteColorButtons.size(); i++) {
            JButton colorButton = this.paletteColorButtons.get(i);
            this.updateColorButton(colorButton, MsxColor.getMsx16Color(i));
        }   
    }
}
