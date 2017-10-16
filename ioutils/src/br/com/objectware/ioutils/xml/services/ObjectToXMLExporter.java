package br.com.objectware.ioutils.xml.services;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import javax.xml.bind.JAXBException;

/**
 * This interface defines the "object to xml" exporter methods. 
 */
public interface ObjectToXMLExporter {
    
    /**
     * Saves an annotated java object to a xml file.
     * 
     * @param object objeto a ser salvo (devidamente anotado com as tags da JAXB)
     * @param path caminho onde o modelo será salvo (ex: c:\modelo.xml)
     * @return <code>true</code> caso o modelo seja salvo com sucesso
     * @throws java.nio.file.FileAlreadyExistsException
     * @throws java.io.IOException
     * @throws javax.xml.bind.JAXBException
     */
    public boolean exportToXML(Object object, String path)
           throws FileAlreadyExistsException, IOException, JAXBException;
    
    /**
     * Enables overriding of the output file (if the file exists, it is overwritten)
     * @param enable <code>true</code> habilita a sobreposição
     */
    void setOverwriteEnabled(boolean enable);
}
