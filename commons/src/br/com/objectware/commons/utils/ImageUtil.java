/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import br.com.objectware.commons.i18n.I18N;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 29/jun/2016
 */
public class ImageUtil {
    
    private static int defaultWidth = 256;
    
    private static int defaultHeight = 256;
    
    public static void setDefaultImageWidth(int width) {
        ImageUtil.defaultWidth = width;
    }
    
    public static void setDefaultImageHeight(int height) {
        ImageUtil.defaultHeight = height;
    }
    
    /**
     * Loads an image from disk.
     * 
     * @param parent
     * @param path 
     * 
     * @return the image loaded from disk
     */
    public static java.awt.Image loadImage(java.awt.Component parent, java.io.File path) {
        
        try {
            // load the image from disk
            if(path != null) {
                return ImageIO.read(path);
            } else {
                // return a default image (clean)
                return new BufferedImage(defaultWidth, defaultHeight, BufferedImage.TYPE_INT_ARGB);
            }
        } catch (IOException ioException) {
            
            JOptionPane.showMessageDialog(parent,
                I18N.getString("error.reading.file", path.getName()), // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
            
            return null;
        }
    }
}
