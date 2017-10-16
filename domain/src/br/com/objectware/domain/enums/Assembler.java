/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain.enums;

import br.com.objectware.commons.i18n.I18N;
import java.util.Arrays;
import java.util.List;

/**
 * Available assemblers.
 * 
 * @author Luciano M. Christofoletti
 * @since 04/Apr/2015
 * 
 * Useful Docs:
 *     
 */
public enum Assembler {
    
    /** The Z80 assembler */
    GLASS(TargetProcessor.Z80);
    
    /** The assembler target processor */
    private final List<TargetProcessor> targetProcessors;
    
    /** Assembler "user-friendly" description */
    private final String description;
    
    /**
     * 
     * @param assembler
     * @param opCodesFile 
     */
    Assembler(TargetProcessor... targets) {
        
        // set the assembler class and target processor
        this.targetProcessors = Arrays.asList(targets);
        
        // set the assembler description
        this.description = I18N.getString("assembler.for.processor", this.name(), targets[0]);
    }   
    
    /**
     * Get the target processors list.
     * @return 
     */
    public List<TargetProcessor> getTargets() {
        return this.targetProcessors;
    }
    
//    /**
//     * Get the target processor type.
//     * @param target
//     * @return 
//     */
//    public List<TargetProcessor> availableAssemblers(TargetProcessor target) {
//        
//        List<TargetProcessor> targetList = new ArrayList<>();
//        for(Assembler assembler:Assembler.values()) {
//            if(assembler.getTarget().equals(target)) {
//                targetList.add(target);
//            }
//        }
//        
//        return targetList;
//    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
