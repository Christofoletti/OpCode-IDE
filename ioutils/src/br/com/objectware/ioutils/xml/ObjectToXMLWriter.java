/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.objectware.ioutils.xml;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import javax.xml.bind.JAXBException;

/**
 * Classe responsável por salvar um objeto com as devidas anotações em um arquivo no formato XML.
 * 
 * @author Luciano M. Christofoletti
 * @since 27/mar/2014
 * 
 * Documentos sobre armazenamento de arquivo XML em java:
 *     http://docs.oracle.com/javase/tutorial/jaxb/intro/index.html
 *     http://xstream.codehaus.org/license.html
 *     
 * Tutoriais:
 *     http://www.journaldev.com/1234/jaxb-tutorial-example-to-convert-object-to-xml-and-xml-to-object
 */
public interface ObjectToXMLWriter {
    
    /**
     * Salva um objeto anotado com as tags XML do JAXB em disco no formato XML.
     * 
     * @param object Objeto a ser "salvo" em um arquivo XML
     * @param fileName nome do arquivo a ser salvo
     * @return verdadeiro se o arquivo foi salvo com sucesso
     * 
     * @throws FileAlreadyExistsException se o arquivo já existir e a sobrescrita de arquivos estiver desabilitada
     * @throws IOException se ocorrer algum problema durante a escrita do arquivo
     * @throws JAXBException caso ocorra problemas com a operação de marshalling do objeto
     */
    boolean save(Object object, String fileName) throws FileAlreadyExistsException, IOException, JAXBException;
    
    /**
     * Define se o arquivo destino especificado no método {@link #save} pode ser sobrescrito ou não.
     * 
     * @param enable <code>true</code> permite que o arquivo seja sobrescrito
     */
    void setOverwriteEnabled(boolean enable);
    
}
