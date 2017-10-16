/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.BuildType;
import br.com.objectware.domain.TargetPlatform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * This class define the target platform types type (e.g. MSX, ATARI 2600, etc)
 * 
 * @author Luciano M. Christofoletti
 * @since 11/Apr/2015
 */
public enum Platform implements TargetPlatform {
    
    MSX("msx", MsxBuildType.values()),
    SMS("sms", SMSBuildType.values());
    
    /** The platform internal id */
    private final String id;
    
    /** The platform text name */
    private final String name;
    
    /** The platform text name (user friendly) */
    private final String longName;
    
    /** The project icon that is shown in the project tree */
    private final ImageIcon icon;
    
    /** The platform textual description */
    private final String description;
    
    /** List of available build types for the platform */
    private final List<BuildType> buildTypes = new ArrayList<>();
    
    /** Define the platform properties */
    private Platform(String id, BuildType buildTypes[]) {
        
        this.id = id;
        
        // properties that requires I18N
        this.name = I18N.getString(id+".platform.name");
        this.longName = I18N.getString(id+".platform.long.name");
        this.icon = I18N.getImageIcon(id+".platform.icon");
        this.description = I18N.getString(id+".platform.description");
        
        this.buildTypes.addAll(Arrays.asList(buildTypes));
    }   
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getLongName() {
        return this.longName;
    }
    
    @Override
    public ImageIcon getIcon() {
        return this.icon;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Returns the list of available build types for the target platform
     * @return the list of build formats
     */
    @Override
    public List<BuildType> getBuildTypes() {
        return Collections.unmodifiableList(this.buildTypes);
    }
    
    @Override
    public String toString() {
        return this.longName;
    }
    
    @Override
    public List<? extends TargetPlatform> list() {
        return Arrays.asList(Platform.values());
    }
}
