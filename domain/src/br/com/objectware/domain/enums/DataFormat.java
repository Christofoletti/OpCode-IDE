/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;

/**
 * The export formats for byte type.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 20/oct/2015
 */
public enum DataFormat {
    
    ASM_HEX_ASM1("Hexadecimal (0BBH)", "0%02XH"),
    ASM_HEX_ASM2("Hexadecimal (#BB)", "#%02X"),
    ASM_HEX_PAS("Hexadecimal ($BB)", "$%02X"),
    ASM_HEX_C("Hexadecimal (0xBB)", "0x%02X"),
    ASM_DEC("Decimal", "%d"),
    // IMPORTANT: the "binary" formatter string does not defines a valid format string.
    // The exporter must takes care of this type in the expot process.
    ASM_BIN(I18N.getString("binary.format"), "binary");
    
    private final String description;
    private final String dataFormat;
    
    DataFormat(String description, String dataFormat) {
        this.description = description;
        this.dataFormat = dataFormat;
    }
    
    public String getFormatter() {
        return this.dataFormat;
    }   
    
    public static String getDefaultFormat() {
        return ASM_HEX_ASM1.getFormatter();
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
