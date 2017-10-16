package br.com.objectware.ioutils.xml.services;

import br.com.objectware.ioutils.xml.ObjectToXMLWriter;
import br.com.objectware.ioutils.xml.ObjectToXMLWriterImpl;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import javax.xml.bind.JAXBException;
import org.openide.util.lookup.ServiceProvider;

/**
 * Service to export annotated xml objects into xml files
 * 
 */
@ServiceProvider(service = ObjectToXMLExporter.class, position = 1)
public class ObjectToXMLExporterImpl implements ObjectToXMLExporter {
    
    /**
     * Utility class used to export objects to xml files
     */
    private final ObjectToXMLWriter xmlWriter = new ObjectToXMLWriterImpl();
    
    @Override
    public boolean exportToXML(Object object, String path)
           throws FileAlreadyExistsException, IOException, JAXBException {
        return this.xmlWriter.save(object, path);
    }   
    
    @Override
    public void setOverwriteEnabled(boolean enable) {
        this.xmlWriter.setOverwriteEnabled(enable);
    }
}   
