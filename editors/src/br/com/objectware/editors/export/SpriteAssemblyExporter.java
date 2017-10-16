/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.export;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.domain.enums.DataFormat;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.msx.sprite.Msx1SpriteAttributes;
import br.com.objectware.domain.msx.sprite.Msx2SpriteAttributes;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.ioutils.text.DataFileIOUtil;
import br.com.objectware.editors.sprite.SpritesDataObject;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Lookup;

/**
 * Sprite exporter. Methods for exporting a set of sprites in assembly format.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @date 15/sep/2015
 * 
 * useful docs:
 */
public class SpriteAssemblyExporter {
    
    /** The default line prefix (appended at the beginning of each data line) */
    private String linePrefix = Default.ASM_DATA_BYTES;
    
    /** The data formatter */
    private String dataFormat = DataFormat.getDefaultFormat();
    
    /** The default number of bytes per line of export */
    private int bytesPerLine = Default.ELEMENTS_PER_LINE;
    
    /** Defines if the sprite patterns must be exported */
    private boolean exportPatterns = true;
    
    /** Defines if the sprite attributes must be exported */
    private boolean exportAttributes = true;
    
    /** Defines if the labels must be exported */
    private boolean exportLabels = true;
    
    /** The target file of the export action */
    private final java.io.File file;
    
    /**
     * The sprite assembly exporter.
     * @param file 
     */
    public SpriteAssemblyExporter(java.io.File file) {
        assert file != null;
        this.file = file;
    }   
    
    /**
     * Sets the string data formatter.
     * @param dataFormat 
     */
    public void setDataFormat(String dataFormat) {
        assert dataFormat != null;
        this.dataFormat = dataFormat;
    }   
    
    /**
     * Set the line prefix for each exported line.
     * @param linePrefix 
     */
    public void setLinePrefix(String linePrefix) {
        assert linePrefix != null;
        this.linePrefix = linePrefix;
    }   
    
    /**
     * Set number of bytes per line in the exporting file.
     * @param bytesPerLine 
     */
    public void setBytesPerLine(int bytesPerLine) {
        if(bytesPerLine > 0) {
            this.bytesPerLine = bytesPerLine;
        } else {
            throw new IllegalArgumentException(I18N.getString("invalid.parameter.value"));
        }   
    }   
    
    /**
     * Export patterns?
     * @param exportPatterns 
     */
    public void setExportPatterns(boolean exportPatterns) {
        this.exportPatterns = exportPatterns;
    }
    
    /**
     * Export attributes?
     * @param exportAttributes 
     */
    public void setExportAttributes(boolean exportAttributes) {
        this.exportAttributes = exportAttributes;
    }
    
    /**
     * Export labels?
     * @param exportLabels 
     */
    public void setExportLabels(boolean exportLabels) {
        this.exportLabels = exportLabels;
    }   
    
    /**
     * Export the sprite data to text data file. 
     * @param dataObject the sprite data object
     * @throws IOException if some problem occur.
     */
    public void export(SpritesDataObject dataObject) throws java.io.IOException {
        
        // the list of lines to export
        List<String> exportData = new ArrayList<>();
        exportData.add(Default.ASM_COMMENT_CHAR + Default.SPACE_STRING + Default.COPYRIGHT);
        
        // get the data (formatted) file I/O util object
        DataFileIOUtil dataFileIOUtil = Lookup.getDefault().lookup(DataFileIOUtil.class);
        dataFileIOUtil.setLinePrefix(this.linePrefix);
        dataFileIOUtil.setDataFormat(this.dataFormat);
        dataFileIOUtil.setBytesPerLine(this.bytesPerLine);
        
        // -------------------------------- export patterns
        if(this.exportPatterns) {
            exportData.addAll(this.exportMsxSpritePatternsTable(dataObject, dataFileIOUtil));
        }
        
        // -------------------------------- export attributes
        if(this.exportAttributes) {
            
            SpriteFormat spriteFormat = dataObject.getSpriteFormat();
            switch (spriteFormat) {
                
                case MSX_MODE1:
                    exportData.addAll(this.exportMsxSpriteAttributesTable(dataObject, dataFileIOUtil));
                    break;
                    
                case MSX_MODE2:
                    exportData.addAll(this.exportMsxSpriteAttributesTable(dataObject, dataFileIOUtil));
                    exportData.addAll(this.exportMsxSpriteRowColorsTable(dataObject, dataFileIOUtil));
                    break;
                    
                default:
                   throw new IllegalArgumentException(I18N.getString("sprite.format.not.available", spriteFormat));
            }   
        }   
        
        // write the file to disk
        dataFileIOUtil.write(this.file.getCanonicalPath(), exportData);
    }   
    
    /**
     * 
     * @param dataObject
     * @param dataFileIOUtil
     * @return 
     */
    private List<String> exportMsxSpritePatternsTable(SpritesDataObject dataObject, DataFileIOUtil dataFileIOUtil) {
        
        List<String> exportData = new ArrayList<>();
        
        exportData.add(Default.EMPTY_STRING);
        exportData.add(I18N.getString("sprite.patterns.table.comment", Default.ASM_COMMENT_CHAR));
        exportData.add(Default.EMPTY_STRING);
        
        for(Sprite sprite : dataObject.getSprites()) {
            
            // output the sprite pattern label (if flag setted)
            if (this.exportLabels) {
                String label = sprite.getLabel().toUpperCase();
                exportData.add(label+Default.LABEL_DELIMITER_CHAR);
            }   
            
            // generate the sprite pattern lines of data
            byte data[] = sprite.getPattern().getData();
            exportData.addAll(dataFileIOUtil.translate(data));
        }   
        
        return exportData;
    }
    
    /**
     * 
     * @param dataObject
     * @param dataFileIOUtil
     * @return 
     */
    private List<String> exportMsxSpriteAttributesTable(SpritesDataObject dataObject, DataFileIOUtil dataFileIOUtil) {
        
        List<String> exportData = new ArrayList<>();
        int attribute = 0;
        
        // export the sprite attributes comment line
        exportData.add(Default.EMPTY_STRING);
        exportData.add(I18N.getString("sprite.attributes.table.comment", Default.ASM_COMMENT_CHAR));
        exportData.add(Default.EMPTY_STRING);
        
        for (SpriteAttributes attributes : dataObject.getAttributes()) {
            
            // output the attribute label (if flag setted)
            if (this.exportLabels) {
                String suffix = String.format("%02d", attribute++);
                exportData.add(I18N.getString("attribute.label", suffix, Default.LABEL_DELIMITER_CHAR));
            }   
            
            // generate the sprite pattern lines of data
            byte data[] = attributes.getData(0, Msx1SpriteAttributes.DATA_LENGTH);
            exportData.addAll(dataFileIOUtil.translate(data));
        }   
        
        return exportData;
    }
    
    /**
     * 
     * @param dataObject
     * @param dataFileIOUtil
     * @return 
     */
    private List<String> exportMsxSpriteRowColorsTable(SpritesDataObject dataObject, DataFileIOUtil dataFileIOUtil) {
        
        // export the sprites attributes table first
        List<String> exportData = new ArrayList<>();
        int spriteNumber = 0;
        
        exportData.add(Default.EMPTY_STRING);
        exportData.add(I18N.getString("sprite.colors.table.comment", Default.ASM_COMMENT_CHAR));
        exportData.add(Default.EMPTY_STRING);
        
        for (SpriteAttributes attributes : dataObject.getAttributes()) {
            
            // output the color label (if flag setted)
            if (this.exportLabels) {
                String suffix = String.format("%02d", spriteNumber++);
                exportData.add(I18N.getString("color.label", suffix, Default.LABEL_DELIMITER_CHAR));
            }   
            
            // generate the sprite row colors lines of data
            byte data[] = attributes.getData(
                    Msx1SpriteAttributes.DATA_LENGTH,
                    Msx2SpriteAttributes.EXTENDED_DATA_LENGTH);
            exportData.addAll(dataFileIOUtil.translate(data));
        }   
        
        return exportData;
    }
}
