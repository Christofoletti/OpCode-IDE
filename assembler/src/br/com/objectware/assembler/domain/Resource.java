/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.domain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Resource file of the project.
 * 
 * @author Luciano M. Christofoletti
 * @since 02/Apr/2015
 */
public class Resource {
    
    /** The unique resource Id */
    private final String id;
    
    /** The type of the resource (source, binary, etc) */
    private final ResourceType type;
    
    /** The stream associated with the resource */
    private final String path;
    
    /** The Resource private constructor. */
    private Resource(String id, ResourceType type, String path) {
        this.id = id;
        this.type = type;
        this.path = path;
    }
    
    /**
     * Create a new resource of source type.
     * 
     * @param id the resource Id
     * @param path the resource path
     * 
     * @return the new resource created
     */
    public static Resource newSourceFile(String id, String path) {
        return new Resource(id, ResourceType.SOURCE, path);
    }
    
    /**
     * Create a new resource of binary type.
     * 
     * @param id the resource Id
     * @param path the resource path
     * 
     * @return the new resource created
     */
    public static Resource newBinaryFile(String id, String path) {        
        return new Resource(id, ResourceType.BINARY, path);
    }
    
    public String getId() {
        return this.id;
    }
    
    public ResourceType getType() {
        return this.type;
    }
    
    public String getPath() {
        return this.path;
    }
    
    /**
     * Get the input stream reader for this resource.
     * @return a BufferedReader that can be used to read data from resource
     */
    public BufferedReader getReader() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.path);
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr);
    }   
    
    @Override
    public boolean equals(Object other) {
        
        if(other == null) {
            return false;
        } else if(other instanceof Resource) {
            return this.id.equals(((Resource) other).id);
        }   
        
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.path);
        return hash;
    }
}
