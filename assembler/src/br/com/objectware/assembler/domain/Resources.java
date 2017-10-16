/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.objectware.assembler.domain;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Set of all resources needed to construct the project.
 * 
 * @author Luciano M. Christofoletti
 * @since 02/Apr/2015
 */
public class Resources {
    
    /** Map of all available resources in the project */
    private final Map<String, Resource> resources = new HashMap<>();
    
    /** The main resource: this is the starting point of the build process */
    private final Resource main;
    
    /**
     * Maps all resources needed to build the project
     * @param main The main resource of the project (cannot be null)
     */
    public Resources(Resource main) {
        this.main = main;
    }
    
    /**
     * Add a new resource to the map of available resources
     * @param resource the resource to be added to the resources map
     * @return 
     */
    public Resource add(Resource resource) {
        return this.resources.put(resource.getId(), resource);
    }
    
    /**
     * Get the main resource
     * @return 
     */
    public Resource getMain() {
        return this.main;
    }
    
    /**
     * Get a resource by its Id. If the reource is not found, it returns null.
     * @param id The resource Id
     * @return The requested resource
     */
    public Resource get(String id) {
        return this.resources.get(id);
    }
    
//    /**
//     * The path must include the name of the resource file
//     * e.g. home/resources/vdp.asm
//     * @param path
//     * 
//     * @return 
//     */
//    public InputStream getInputStream(String path) {
//        return this.getClass().getClassLoader().getResourceAsStream(path);
//    }
}
