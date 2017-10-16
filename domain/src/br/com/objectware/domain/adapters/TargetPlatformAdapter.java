/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.adapters;

import br.com.objectware.domain.TargetPlatform;
import br.com.objectware.domain.enums.Platform;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * TargetPlatform adapter class (used in xml parsing)
 *
 * @author Luciano M. Christofoletti
 * @since 12/Apr/2015
 * 
 * Useful docs:
 *     http://stackoverflow.com/questions/4216745/java-string-to-date-conversion
 *     http://stackoverflow.com/questions/2519432/jaxb-unmarshal-timestamp
 */
public class TargetPlatformAdapter extends XmlAdapter<String, TargetPlatform> {
    
    @Override
    public String marshal(TargetPlatform targetPlatform) throws Exception {
        return targetPlatform.getName();
    }
    
    @Override
    public TargetPlatform unmarshal(String string) throws Exception {
        
        // search for the target platform using the unique Id
        for(TargetPlatform targetPlatform : Platform.values()) {
            if(targetPlatform.getName().equals(string)) {
                return targetPlatform;
            }
        }
        
        throw new IllegalArgumentException("Wrong target-platform specification!\n"+
            "Your project file descriptor may be corrupted...");
    }
    
}
