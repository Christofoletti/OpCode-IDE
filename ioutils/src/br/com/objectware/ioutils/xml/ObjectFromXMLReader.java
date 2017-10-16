/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.objectware.ioutils.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;

/**
 * Interface da classe responsável por carregar um objeto a partir de um arquivo no formato XML.
 * 
 * @author Luciano M. Christofoletti
 * @param <T>
 * @since 27/mar/2014
 * 
 * Documentos sobre armazenamento de arquivo XML em java:
 *     http://stackoverflow.com/questions/4453349/what-is-class-objectjava-lang-class-in-java
 *     http://stackoverflow.com/questions/3437897/how-to-get-class-instance-of-generics-type-t
 */
public interface ObjectFromXMLReader<T> {
    
    /**
     * Load object from xml file
     * 
     * @param fileName nome do arquivo (caminho completo)
     * @return instancia do objeto desejado
     * @throws java.io.FileNotFoundException
     * @throws IOException
     * @throws javax.xml.bind.JAXBException
     */
    T load(String fileName) throws FileNotFoundException, IOException, JAXBException;
    
    /**
     * 
     * @param objectClass classe do objeto esperado
     * @param fileName
     * @return instancia do objeto recuperado
     * @throws IOException
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.bind.JAXBException
     */
    T load(Class<T> objectClass, String fileName) throws FileNotFoundException, IOException, JAXBException;
    
}
