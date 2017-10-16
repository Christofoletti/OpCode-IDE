/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.adapters;

import br.com.objectware.domain.BuildType;
import br.com.objectware.domain.TargetPlatform;
import br.com.objectware.domain.enums.Platform;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * BuildType adaptor class (used in xml parsing)
 * 
 * @author Luciano M. Christofoletti
 * @since 12/Apr/2015
 * 
 * Useful docs:
 *     http://stackoverflow.com/questions/4216745/java-string-to-date-conversion
 *     http://stackoverflow.com/questions/2519432/jaxb-unmarshal-timestamp
 */
public class BuildTypeAdapter extends XmlAdapter<String, BuildType> {
    
    @Override
    public String marshal(BuildType buildType) throws Exception {
        return buildType.getId();
    }
    
    @Override
    public BuildType unmarshal(String string) throws Exception {
        
        // for all platforms, search for the build type using the unique Id
        for(TargetPlatform targetPlatform : Platform.values()) {
            
            // for each platform, tries to find the build type by Id
            for(BuildType buildType : targetPlatform.getBuildTypes()) {
                if(buildType.getId().equals(string)) {
                    return buildType;
                }
            }
        }
        
        throw new IllegalArgumentException("Wrong build-type specification!\n"+
            "Your project file descriptor may be corrupted...");
    }
    
}
