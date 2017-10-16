package br.com.objectware.ioutils.xml.services;

import br.com.objectware.ioutils.xml.ObjectFromXMLReader;
import br.com.objectware.ioutils.xml.ObjectFromXMLReaderImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.openide.util.lookup.ServiceProvider;

/**
 * Classe responsável por carregar as informações de um arquivo e criar um novo objeto.
 * @param <T>
 */
@ServiceProvider(service = XMLFileToObjectImporter.class, position = 1)
public class XMLFileToObjectImporterImpl<T extends Object> implements XMLFileToObjectImporter<T> {
    
    /**
     * Classe utilitária responsável por salvar o modelo em disco no formato XML
     */
    private final ObjectFromXMLReader<T> xmlReader = new ObjectFromXMLReaderImpl<>();
    
    @Override
    public Object importFromXML(Class<T> objectClass, String path)
           throws FileNotFoundException, IOException, JAXBException {
        return this.xmlReader.load(objectClass, path);
    }
}
