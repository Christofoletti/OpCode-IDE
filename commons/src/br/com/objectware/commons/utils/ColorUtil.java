/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import java.awt.Color;

/**
 * Color manipulation utils.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 03/nov/2015
 */
public class ColorUtil {
    
    /**
     * Return white/black color that contrasts with the color passed as parameter. <br>
     * <br>
     * See {@see https://24ways.org/2010/calculating-color-contrast/}
     * @param color the reference color
     * @return 
     */
    public static Color getContrastColor(Color color) {
        return (evaluateYIQ(color) > 128) ? Color.BLACK:Color.WHITE;
    }   
    
    /**
     * Evaluate the YIQ factor for a given color.
     * @param color
     * @return the YIQ value (integer)
     */
    public static int evaluateYIQ(Color color) {
        
        // get the RGB color components
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        // evaluate the YIQ rgb value (see https://24ways.org/2010/calculating-color-contrast/)
        return ((r * 299) + (g * 587) + (b * 114)) / 1000;
    }   
}
