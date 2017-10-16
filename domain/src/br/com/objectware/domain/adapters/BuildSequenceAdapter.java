/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.adapters;

import br.com.objectware.domain.enums.BuildSequence;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * BuildSequence adapter class (used in xml parsing)
 *
 * @author Luciano M. Christofoletti
 * @since 12/Apr/2015
 * 
 * Useful docs:
 *     http://stackoverflow.com/questions/2519432/jaxb-unmarshal-timestamp
 */
public class BuildSequenceAdapter extends XmlAdapter<String, BuildSequence> {
    
    @Override
    public String marshal(BuildSequence buildSequence) throws Exception {
        return buildSequence.getText();
    }
    
    @Override
    public BuildSequence unmarshal(String string) throws Exception {
        
        // search for the target platform using the unique Id
        for(BuildSequence buildSequence : BuildSequence.values()) {
            if(buildSequence.getText().equals(string)) {
                return buildSequence;
            }
        }
        
        throw new IllegalArgumentException("Wrong build sequence specification!\n"+
            "Your project file descriptor may be corrupted...");
    }
    
}
