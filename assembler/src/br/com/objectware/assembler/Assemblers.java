/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler;

import br.com.objectware.commons.i18n.I18N;
import java.util.ArrayList;
import java.util.List;
import nl.grauw.glass.GlassAssemblerZ80;

/**
 * Available assemblers.
 * 
 * @author Luciano M. Christofoletti
 * @since 04/Apr/2015
 * 
 * Useful Docs:
 *     
 */
public enum Assemblers {
    
    /** The Z80 assembler */
    GLASS(GlassAssemblerZ80.class, TargetProcessor.Z80);
    
    /** The assembler target processor */
    private final TargetProcessor target;
    
    /** The assembler class name */
    private final Class<Assembler> assemblerClass;
    
    /** Simple compiler description */
    private final String description;
    
    /**
     * 
     * @param assembler
     * @param opCodesFile 
     */
    @SuppressWarnings("unchecked")
    Assemblers(Class<? extends Assembler> assemblerClass, TargetProcessor target) {
        
        // set the assembler class and target processor
        this.assemblerClass = (Class<Assembler>) assemblerClass;
        this.target = target;
        
        // set the assembler description
        this.description = I18N.getString("assembler.for.processor", this.name(), target.name());
    }
    
    /**
     * Get the target processor type.
     * @return 
     */
    public TargetProcessor getTarget() {
        return this.target;
    }
    
    /**
     * Get the target processor type.
     * @param target
     * @return 
     */
    public List<TargetProcessor> availableAssemblers(TargetProcessor target) {
        
        List<TargetProcessor> targetList = new ArrayList<>();
        for(Assemblers assembler:Assemblers.values()) {
            if(assembler.getTarget().equals(target)) {
                targetList.add(target);
            }
        }
        
        return targetList;
    }
    
    /**
     * Return a new instance of the Assembler class.
     * 
     * @return the assembler class
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public Assembler newInstance() throws InstantiationException, IllegalAccessException {
        return this.assemblerClass.newInstance();
    }   
    
    @Override
    public String toString() {
        return this.description;
    }
}
