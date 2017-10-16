/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;

/**
 * The currently available export formats.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 20/oct/2015
 */
public enum ExportFormat {
    
    ASSEMBLY(FileType.ASM, I18N.getString("export.as.assembly.code")),
    BINARY(FileType.BIN, I18N.getString("export.as.binary.file")),
    IMAGE(FileType.PNG, I18N.getString("export.as.image.file"));
    
    private final FileType fileType;
    private final String description;
    
    ExportFormat(FileType fileType, String description) {
        this.fileType = fileType;
        this.description = description;
    }
    
    /**
     * Return the file type (asm, bin, png)
     * @return the file type
     */
    public FileType getFileType() {
        return this.fileType;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
