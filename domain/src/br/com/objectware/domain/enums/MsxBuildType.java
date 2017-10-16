/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.BuildType;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * This enum defines all the MSX output formats currently available in the OpCode IDE.
 * 
 * @author Luciano M. Christofoletti
 * @since 29/Mar/2015
 */
public enum MsxBuildType implements BuildType {
    
    MSX_BINARY("MSX_BINARY", "msx-binary-project", "bin", 20480),
    MSX_DOS("MSX_DOS", "msx-dos-project", "com", 32768),
    MSX_ROM("MSX_ROM", "msx-rom-project", "rom", 32768),
    MSX_MEGAROM("MSX_MEGAROM", "msx-megarom-poject", "rom", 2*65536);
    
    /** The unique Id that identifies the build type target */
    private final String id;
    
    /** The default project name (user friendly) */
    private final String defaultProjectName;
    
    /** The build type file default extension (.rom, .com, .bin, etc) */
    private final String extension;
    
    /** The project icon that is shown in the project tree */
    private final ImageIcon icon;
    
    /** The maximum size of the target output file */
    private final int maxSize;
    
    /** Short text description of the build type */
    private final String description;
    
    /**
     * 
     * @param id the build type string identification
     * @param defaultProjectName the default project name showed to the user when creating a new project
     * @param extension the target file extension
     * @param maxSize maximum output file size (only for validation purposes)
     */
    private MsxBuildType(String id, String defaultProjectName, String extension, int maxSize) {
        
        // setup the main properties
        this.id = id;
        this.defaultProjectName = defaultProjectName;
        this.extension = extension;
        this.maxSize = maxSize;
        
        // properties that requires I18N
        this.icon = I18N.getImageIcon("msx.project.icon");
        this.description = I18N.getString("msx."+extension+".description");
        
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public String getExtension() {
        return this.extension;
    }
    
    @Override
    public ImageIcon getIcon() {
        return this.icon;
    }
    
    @Override
    public int getMaxSize() {
        return this.maxSize;
    }
    
    @Override
    public String getDefaultProjectName() {
        return this.defaultProjectName;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public List<? extends BuildType> list() {
        return Arrays.asList(MsxBuildType.values());
    }
    
    @Override
    public String toString() {
        return this.defaultProjectName;
    }
}
