/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.export;

import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.ioutils.binary.BinaryFileIOUtil;
import br.com.objectware.editors.sprite.SpritesDataObject;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.util.Lookup;

/**
 * Sprite data exporter. Methods for exporting a set of sprites in binary format.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @date 15/sep/2015
 * 
 * useful docs:
 */
public class SpriteDataExporter {
    
    /** The target file of the export action */
    private final File file;
    
    /** Defines if the sprite patterns must be exported */
    private boolean exportPatterns = true;
    
    /** Defines if the sprite attributes must be exported */
    private boolean exportAttributes = true;
    
    /**
     * The binary sprite data exporter. Exports the sprite data as a binary file.
     * @param file the target file (export)
     */
    public SpriteDataExporter(File file) {
        assert file != null;
        this.file = file;
    }   
    
    /**
     * Set export patterns status.
     * @param exportPatterns 
     */
    public void setExportPatterns(boolean exportPatterns) {
        this.exportPatterns = exportPatterns;
    }
    
    /**
     * Set export atributes status.
     * @param exportAttributes 
     */
    public void setExportAttributes(boolean exportAttributes) {
        this.exportAttributes = exportAttributes;
    }
    
    /**
     * Export the sprite data to a binary file.
     * @param dataObject the sprites data object
     * @throws IOException 
     */
    public void export(SpritesDataObject dataObject) throws IOException {
        
        // get the binary file I/O util object
        BinaryFileIOUtil binaryFileIOUtil = Lookup.getDefault().lookup(BinaryFileIOUtil.class);
        
        // -------------------------------- export patterns
        try (OutputStream outputStream = binaryFileIOUtil.createOutputStream(this.file)) {
            
            // -------------------------------- export patterns
            if(this.exportPatterns) {
                for(Sprite sprite : dataObject.getSprites()) {
                    outputStream.write(sprite.getPattern().getData());
                    outputStream.flush();
                }   
            }   
            
            // -------------------------------- export attributes
            if(this.exportAttributes) {
                for(SpriteAttributes attributes : dataObject.getAttributes()) {
                    outputStream.write(attributes.getData());
                    outputStream.flush();
                }   
            }   
        }
    }
}
