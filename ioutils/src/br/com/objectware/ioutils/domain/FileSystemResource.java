/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.domain;

/**
 * This class define a single File System Resource.
 * 
 * @author Luciano M. Christofoletti
 * @since 14/Apr/2015
 */
public class FileSystemResource {
    
    /** The resource Id */
    private final String id;
    
    /** The file system resource type (source file, folder, link, etc...) */
    private final FileSystemResourceType type;
    
    /** The resource path (relative to the projects folder) */
    private String path;
    
    /***/
    private Object resourceData;
    
    /**
     * 
     * @param id
     * @param type 
     */
    public FileSystemResource(String id, FileSystemResourceType type) {
        this.id = id;
        this.type = type;
    }
    
    public String getId() {
        return this.id;
    }
    
    public FileSystemResourceType getType() {
        return this.type;
    }
    
    public boolean is(FileSystemResourceType type) {
        return this.type.equals(type);
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setData(Object resourceData) {
        this.resourceData = resourceData;
    }
    
    public Object getData() {
        return this.resourceData;
    }
}
