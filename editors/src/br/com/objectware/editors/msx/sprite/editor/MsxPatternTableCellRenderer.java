/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.commons.utils.ColorUtil;
import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.msx.sprite.MsxSpriteMixer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 05/Ago/2015
 * 
 * Useful docs:
 *     http://www.java2s.com/Code/Java/Swing-JFC/Borderofallkinds.htm
 *     http://nadeausoftware.com/articles/2008/11/all_ui_defaults_names_common_java_look_and_feels_windows_mac_os_x_and_linux
 */
public class MsxPatternTableCellRenderer extends DefaultTableCellRenderer {
    
    // pixel off colors (quadrant 1 and quadrant 3)
    private static final Color PIXEL_Q1_Q3_OFF = new Color(208, 208, 208); // same as Color.LIGHT_GRAY
    private static final Color PIXEL_Q2_Q4_OFF = new Color(224, 224, 224); // Light gray a little brighter
    
    // border styles used to render the pattern editor cells
    private static final Border DEFAULT_DATA_BORDER = BorderFactory.createEmptyBorder();
    private static final Border DEFAULT_NO_DATA_BORDER = BorderFactory.createEmptyBorder();
    
    // focused white border (the border of the current single cell selected)
    private static final Border DEFAULT_FOCUSED_WHITE_BORDER = BorderFactory.createEtchedBorder(
        EtchedBorder.RAISED, Color.WHITE, Color.WHITE
    );  
    
    // focused black border (the border of the current single cell selected)
    private static final Border DEFAULT_FOCUSED_BLACK_BORDER = BorderFactory.createEtchedBorder(
        EtchedBorder.RAISED, Color.BLACK, Color.BLACK
    );  
    
    // selected border (range of selected cells)
    private static final Border DEFAULT_WHITE_SELECTED_BORDER = BorderFactory.createDashedBorder(Color.WHITE);
    private static final Border DEFAULT_BLACK_SELECTED_BORDER = BorderFactory.createDashedBorder(Color.BLACK);
    
    /** The default color palette */
    private final java.util.List<Color> palette = MsxColor.getMsx16ColorPalette();
    
    /** Flag that allows render/suppress colored cells */
    private boolean renderInBlackAndWhite = false;
    
    /** 
     * Set the new set of colors (palette) for the pattern editor.
     * @param palette a list of colors
     */
    public void setPalette(java.util.List<Color> palette) {
        this.palette.clear();
        this.palette.addAll(palette);
    }   
    
    /**
     * Enable/Disable redering in black and white or in color mode (using the current pallete)
     * @param renderInBlackAndWhite 
     */
    public void setRenderInBlackAndWhite(boolean renderInBlackAndWhite) {
        this.renderInBlackAndWhite = renderInBlackAndWhite;
    }
    
    @Override
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                      boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (table == null) {
            return this;
        }
        
        // get the color status of the current pixel (foreground and background)
        byte byteValue = (byte) value;
        boolean fgPixelOn = (byteValue & MsxSpriteMixer.FG_PIXEL_ON) != 0;
        boolean bgPixelOn = (byteValue & MsxSpriteMixer.BG_PIXEL_ON) != 0;
        
        // get the pixel color
        int colorCode = byteValue & 0x0F;
        Color pixelColor;
        if(fgPixelOn || bgPixelOn) {
            if(this.renderInBlackAndWhite) {
                pixelColor = Color.BLACK;
            } else {
                pixelColor = this.palette.get(colorCode);
            }
        } else {
            boolean oddQuadrant = (row < 8 && column < 8) || (row > 7 && column > 7);
            pixelColor = oddQuadrant ? PIXEL_Q1_Q3_OFF:PIXEL_Q2_Q4_OFF;
        }
        
        // set the foreground and background colors for the current cell
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setForeground(pixelColor);
        component.setBackground(pixelColor);
        
        // set the border (see https://24ways.org/2010/calculating-color-contrast/)
        javax.swing.JLabel label = (javax.swing.JLabel) component;
        if (hasFocus) {
            if(ColorUtil.evaluateYIQ(pixelColor) < 128) {
                label.setBorder(DEFAULT_FOCUSED_WHITE_BORDER);
            } else {
                label.setBorder(DEFAULT_FOCUSED_BLACK_BORDER);
            }   
        } else if(isSelected) {
            if(ColorUtil.evaluateYIQ(pixelColor) < 128) {
                label.setBorder(DEFAULT_WHITE_SELECTED_BORDER);
            } else {
                label.setBorder(DEFAULT_BLACK_SELECTED_BORDER);
            }   
        } else {
            if(fgPixelOn) {
                label.setBorder(DEFAULT_DATA_BORDER);
            } else {
                label.setBorder(DEFAULT_NO_DATA_BORDER);
            }   
        }   
        
        return label;
    }   
    
}
