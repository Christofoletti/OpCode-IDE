/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.domain;

import br.com.objectware.commons.utils.FileUtil;
import br.com.objectware.commons.utils.LineParser;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 */
public class BuildOptions {
    
    private final List<String> buildOptions;
    
    public static BuildOptions parse(String options) {
        return new BuildOptions(options);
    }
    
    /**
     * The build options for the internal compiler
     * @param options the line options separated by spaces
     */
    private BuildOptions(String options) {
        this.buildOptions = LineParser.parseArguments(options);
    }
    
    /**
     * Return the number of options.
     * @return 
     */
    public int getOptionsCount() {
        return this.buildOptions.size();
    }
    
    /**
     * Get the main source file pathname (if available).
     * @return 
     */
    public Optional<String> getMainSource() {
        
        if(!this.buildOptions.isEmpty()) {
            
            // get the main source file name and path
            String mainSourceFileName = this.buildOptions.get(0);
            String mainSourceFilePath = FileUtil.translatePath(this.trim(mainSourceFileName));
            
            // verify if the main source file is valid
            if(FileUtil.isSourceFile(new java.io.File(mainSourceFilePath))) {
                return Optional.of(mainSourceFilePath);
            }   
        }   
        
        return Optional.empty();
    }
    
    /**
     * Get the main target file pathname (if available).
     * @return 
     */
    public Optional<String> getTargetFile() {
        
        if(this.buildOptions.size() > 1) {
            
            // get the main source file name and path
            String targetFileName = this.buildOptions.get(1);
            String targetFilePath = FileUtil.translatePath(this.trim(targetFileName));
            
            // verify if the target file is not empty
            if(!targetFilePath.isEmpty()) {
                return Optional.of(targetFilePath);
            }   
        }   
        
        return Optional.empty();
    }
    
    /**
     * Get the list of build options
     * @return 
     */
    public List<String> getOptions() {
        return this.buildOptions.subList(2, this.buildOptions.size());
    }   
    
    /**
     * Get the option number passed as parameter.
     * Note: the two first parameters are "skipped", as they define the source and target files.
     * 
     * @param number
     * @return 
     */
    public Optional<String> getOption(int number) {
        if(this.buildOptions.size() > number + 2) {
            return Optional.of(this.buildOptions.get(number + 2));
        } else {
            return Optional.empty();
        }   
    }
    
    /**
     * Replace all double cotes with spaces.
     * @param option
     * @return 
     */
    private String trim(String option) {
        if(option != null) {
            option = option.replace('\"', ' ').trim();
        }
        return option;
    }
}
