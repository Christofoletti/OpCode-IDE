/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.objectware.ioutils.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Implementação da funcionalidade que armazena um objeto java em um arquivo no formato XML.
 * Esta classe utiliza a biblioteca Java Architecture for XML Binding (JAXB) distribuida diretamente
 * no pacote do JDK. Outras implementações podem ser feitas utilizando-se a interface ObjectToXMLWriter.
 * 
 * @author Luciano M. Christofoletti
 * @since 29/mar/2014
 * 
 * Dependência do módulo: API de dominio
 * 
 * Documentos sobre armazenamento de arquivo XML em java:
 *     http://docs.oracle.com/javase/tutorial/jaxb/intro/
 *     http://blog.bdoughan.com/2011/08/jaxb-and-java-io-files-streams-readers.html
 *     http://www.journaldev.com/1234/jaxb-tutorial-example-to-convert-object-to-xml-and-xml-to-object
 * 
 */
public class ObjectToXMLWriterImpl implements ObjectToXMLWriter {
    
    /** flag que especifica se o arquivo de saída pode ser sobrescrito (por default isso não é permitido) */
    private boolean enableOverwrite = false;
    
    /** Char encoding do arquivo de saída */
    private static final String CHAR_ENCODING = "UTF-8";
    
    @Override
    public boolean save(Object object, String fileName)
                        throws FileAlreadyExistsException, IOException, JAXBException {
        
        // tenta abrir/criar o arquivo XML para escrita
        File outputFile = new File(fileName);
        FileWriter fstream = new FileWriter(outputFile);
        BufferedWriter xmlOutputWriter = new BufferedWriter(fstream);
        
        if(!this.enableOverwrite && outputFile.exists()) {
            throw new FileAlreadyExistsException(fileName);
        }
        
        try {
            this.jaxbObjectToXML(object, xmlOutputWriter);
        } finally {
            xmlOutputWriter.close();
        }   
        
        return (outputFile.length() > 0);
    }
    
    /**
     * Faz o "marshalling" do objeto propriamente dito.
     * @param object
     * @param outputWriter 
     */
    private void jaxbObjectToXML(Object object, Writer outputWriter) throws JAXBException {
        
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        
        // for pretty-print XML in JAXB
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Marshaller.JAXB_ENCODING exemplo: "ISO-8859-1" apenas para referência
        marshaller.setProperty(Marshaller.JAXB_ENCODING, CHAR_ENCODING);
        
        // write to System.out for debugging (TODO: REMOVE THIS LINE IN THE FUTURE)
        //marshaller.marshal(object, System.out);
        
        // write to file
        marshaller.marshal(object, outputWriter);
        
    }
    
    @Override
    public void setOverwriteEnabled(boolean enableOverwrite) {
        this.enableOverwrite = enableOverwrite;
    }
    
}
