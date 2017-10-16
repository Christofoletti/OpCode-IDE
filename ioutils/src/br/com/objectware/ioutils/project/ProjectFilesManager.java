/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.project;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.ioutils.domain.FileSystemResource;
import br.com.objectware.ioutils.domain.FileSystemResourceType;
import br.com.objectware.ioutils.xml.services.ObjectToXMLExporter;
import br.com.objectware.ioutils.xml.services.XMLFileToObjectImporter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.openide.util.Lookup;

/**
 * Project folder/properties manager.
 * 
 * @author Luciano M. Christofoletti
 * @param <T>
 * @since 11/Apr/2015
 */
public class ProjectFilesManager<T> {
    
    /** The base path of the project (all resources are contained within this path structure) */
    private final String basePath;
    
    /***
     * Constructor that initializes the base projects path.
     * @param basePath the project base path
     */
    public ProjectFilesManager(String basePath) {
        this.basePath = basePath;
    }
    
    /**
     * @return the project base path
     */
    public String getBasePath() {
        return this.basePath;
    }
    
    /**
     * Save the project descriptor (project.xml). If the project descriptor already
     * exists, it will be overwrited.
     * 
     * @param descriptor the project properties descriptor
     * @throws java.io.IOException 
     * @throws javax.xml.bind.JAXBException 
     * 
     */
    public void saveProjectDescriptor(T descriptor) throws IOException, JAXBException {
        
        // xml export service (exports java objects to xml files)
        ObjectToXMLExporter xmlExporter = Lookup.getDefault().lookup(ObjectToXMLExporter.class);
        xmlExporter.setOverwriteEnabled(true);
        
        // create the project properties xml file
        String projectPath = this.getFullPath(Default.PROJECT_FULL_FILE_NAME);
        
        // do the hard work man!!!
        xmlExporter.exportToXML(descriptor, projectPath);
    }   
    
    /**
     * 
     * @param fsResource
     * @throws java.io.IOException 
     */
    public void saveResource(FileSystemResource fsResource) throws IOException {
        
        // create the system resource (if necessary)
        if(fsResource.is(FileSystemResourceType.FILE)) {
            
            this.createEmptyFile(fsResource.getPath());
            if(fsResource.getData() != null) {
                String path = fsResource.getPath();
                Object data = fsResource.getData();
                this.saveObjectData(path, data);
            }
            
        } else if(fsResource.is(FileSystemResourceType.FOLDER)) {
            this.createFolder(fsResource.getPath());
        }   

    }
    
    /**
     * Create a new folder from given path.
     * Note: the base path is concatenated as the base for the given path.
     * 
     * @param path the path of the new folder to be created
     * @throws SecurityException 
     */
    public void createFolder(String path) throws SecurityException {
        
        File fsFile = new File(this.getFullPath(path));
        
        if(!fsFile.exists() || fsFile.isFile()) {
            if(!fsFile.mkdir()) {
                throw new SecurityException(I18N.getString("create.res.project.folder.io.error"));
            }
        }
    }
    
    /**
     * Create a new empty file from given path.
     * Note: the base path is concatenated as the base for the given path.
     * 
     * @param path the path of the new file to be created
     * @throws IOException
     * @throws SecurityException 
     */
    public void createEmptyFile(String path) throws IOException, SecurityException {
        
        File fsFile = new File(this.getFullPath(path));
        
        if(!fsFile.exists() || fsFile.isDirectory()) {
            if(!fsFile.createNewFile()) {
                throw new SecurityException(I18N.getString("create.res.project.folder.io.error"));
            }
        }
    }
    
    /**
     * 
     * @param path
     * @param data 
     */
    private void saveObjectData(String path, Object data) {
//        
//        // Convert the string to a byte array.
//        String s = "Hello World! ";
//        byte d[] = s.getBytes();
//        Path p = Paths.get(path);
//        
//        try (OutputStream out = new BufferedOutputStream(
//          Files.newOutputStream(p, CREATE, APPEND))) {
//          out.write(d, 0, d.length);
//        } catch (IOException x) {
//          System.err.println(x);
//        }
        
    }
    
    /**
     * 
     * @param objectClass
     * @param path
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws JAXBException 
     */
    @SuppressWarnings("unchecked")
    public T loadProjectProperties(Class<T> objectClass, String path)
           throws IOException, FileNotFoundException, JAXBException {
        
        // xml export service (exports java objects to xml files)
        XMLFileToObjectImporter<T> objectImporter = Lookup.getDefault().lookup(XMLFileToObjectImporter.class);
        
        // create the project properties xml file
        String projectPath = this.getFullPath(path);
        
        //T classReference = null;
        return (T) objectImporter.importFromXML(objectClass, projectPath);
    }   
    
    /**
     * 
     * @param path
     * @return 
     */
    private String getFullPath(String path) {
        return (this.basePath + Default.FS + path);
    }
}
