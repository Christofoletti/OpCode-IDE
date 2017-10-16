package br.com.objectware.ioutils.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Implementação da classe responsável por criar um objeto a partir de um arquivo no formato XML.
 * Este serviço utiliza a biblioteca Java Architecture for XML Binding (JAXB) distribuida diretamente
 * no pacote do JDK. Outras implementações podem ser feitas utilizando-se a interface LoadRRModelFromXML.
 * 
 * @param <T>
 * 
 * Documentos sobre armazenamento de arquivo XML em java:
 *     http://www.journaldev.com/1234/jaxb-tutorial-example-to-convert-object-to-xml-and-xml-to-object
 */
public class ObjectFromXMLReaderImpl<T extends Object> implements ObjectFromXMLReader<T> {
    
    private Class<T> objectClass;
    
    @Override
    public T load(String fileName) throws FileNotFoundException, IOException, JAXBException {
        return this.load(this.objectClass, fileName);
    }   
    
    @Override
    public T load(Class<T> objectClass, String fileName) throws FileNotFoundException, IOException, JAXBException {
        
        // abre o arquivo XML para leitura
        File inputFile = new File(fileName);
        FileReader fstream = new FileReader(inputFile);
        BufferedReader xmlInputReader = new BufferedReader(fstream);
        
        if(!inputFile.exists() || inputFile.length() <= 0) {
            throw new FileNotFoundException(fileName);
        }   
        
        try {
            return this.jaxbXMLToObject(objectClass, xmlInputReader);
        } finally {
            xmlInputReader.close();
        }
    }   
    
    /**
     * Realiza a operação de "unmarshalling" propriamente dita.
     * @param objectClass
     * @param inputReader
     * @return T objeto
     * @throws JAXBException 
     */
    @SuppressWarnings("unchecked")
    private T jaxbXMLToObject(Class<T> objectClass, Reader inputReader) 
            throws JAXBException, ClassCastException {
        
        JAXBContext context = JAXBContext.newInstance(objectClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
        return (T) unmarshaller.unmarshal(inputReader);
    }
}
