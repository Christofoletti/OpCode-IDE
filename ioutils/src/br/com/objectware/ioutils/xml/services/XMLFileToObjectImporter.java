package br.com.objectware.ioutils.xml.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;

/**
 * Interface que define as operações de carregar um arquivo XML e criar um novo objeto.
 * @param <T>
 */
public interface XMLFileToObjectImporter<T extends Object> {
    
    /**
     * Load a xml file from disk and return an object instance of the given type.
     * @param objectClass the requested object ype
     * @param path the full path of the xml file to be loaded
     * 
     * @return 
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws javax.xml.bind.JAXBException 
     */
    Object importFromXML(Class<T> objectClass, String path)
           throws FileNotFoundException, IOException, JAXBException;
    
}
