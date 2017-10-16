/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.ioutils.domain.tests;

import br.com.objectware.ioutils.domain.TextFile;
import java.util.Collections;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Text file tests.
 * 
 * @author Luciano M. Christofoletti
 * @since 05/Apr/2015
 * 
 * Useful Docs:
 *     
 */
public class TextFileTest {
    
    private final String TEXT_ID = "test.txt";
    
    private final TextFile textFile = new TextFile(TEXT_ID);
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void textFileAddTest() {
        
        this.textFile.clear();
        assertTrue("Text file must be empty!", this.textFile.getLineCount() == 0);
        assertTrue("Text file Id not set!", this.textFile.getId().equals(TEXT_ID));
        
        this.textFile.add("Start");
        this.textFile.add("second line");
        this.textFile.add("third line...");
        this.textFile.add("[");
        this.textFile.add("]");
        this.textFile.add("End of file");
        
        System.out.println("\nTest: textFileAddTest");
        this.printTextFile(this.textFile);
        
    }
    
    @Test
    public void textFileRemoveTest() {
        
        this.textFile.clear();
        
        this.textFile.add("Start");
        this.textFile.add("line 1");
        this.textFile.add("line 2");
        this.textFile.add("End of file");
        
        this.textFile.setIndex(0);
        this.textFile.removeLine();
        
        this.textFile.removeLine(2);
        
        System.out.println("\nTest: textFileRemoveTest");
        this.printTextFile(this.textFile);
        
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void textFileInserFailtTest() {
        
        this.textFile.clear();
        this.textFile.add("Line 0");
        this.textFile.add("Line 1");
        
        this.textFile.insert("New line");
    }
    
    @Test
    public void textFileInsertTest() {
        
        this.textFile.clear();
        
        this.textFile.add("Line 0");
        this.textFile.add("Line 1");
        this.textFile.add("Line 2");
        this.textFile.add("Line 3");
        
        String newLine = "New line";
        
        // inserting line at the begining
        this.textFile.setIndex(0);
        this.textFile.insert(newLine);
        assertEquals("Text line insertion error!", this.textFile.getLine(), "Line 0"); // index is 1 now
        this.textFile.setIndex(0);
        assertEquals("Text line insertion error!", this.textFile.getLine(), newLine);
        
        // inserting line at the middle
        this.textFile.setIndex(2);
        this.textFile.insert(newLine);
        assertEquals("Text line insertion error!", this.textFile.getLine(), "Line 1"); // index is 3 now
        this.textFile.setIndex(2);
        assertEquals("Text line insertion error!", this.textFile.getLine(), newLine);
        
        // inserting line at the end
        this.textFile.setIndex(5);
        this.textFile.insert(newLine);
        this.textFile.setIndex(5);
        assertEquals("Text line insertion error!", this.textFile.getLine(), newLine);
        
        System.out.println("\nTest: textFileInsertTest");
        this.printTextFile(this.textFile);
        
    }
    
    @Test
    public void textFileAddTextTest() {
        
        this.textFile.clear();
        
        this.textFile.add("Start");
        this.textFile.add("********");
        this.textFile.add("********");
        this.textFile.add("End");
        
        TextFile textFile2 = new TextFile("New Text file");
        textFile2.add("txt2: line 1");
        textFile2.add("txt2: line 2");
        
        this.textFile.setIndex(2);
        this.textFile.insert(textFile2);
        
        this.textFile.setIndex(2);
        assertEquals("Text file insertion error!", this.textFile.getLine(), "txt2: line 1");
        assertEquals("Text file insertion error!", this.textFile.getNextLine(), "txt2: line 2");
        
        this.textFile.setIndex(5);
        assertTrue("Text has next error!", !this.textFile.hasNext());
        
        System.out.println("\nTest: textFileAddTextTest");
        this.printTextFile(this.textFile);
        
    }
    
    @Test
    public void textFileRuntimeAddTextTest() {
        
        this.textFile.clear();
        
        this.textFile.add("Start");
        this.textFile.add("********");
        this.textFile.add("********");
        this.textFile.add("End");
        
        TextFile textFile2 = new TextFile("New Text file");
        textFile2.add("txt2: line 1");
        textFile2.add("txt2: line 2");
        
        
        this.textFile.reset();
        
        // insert a new text T2 file inside a text file T1
        // while iterating the ext file T1
        while(this.textFile.hasNext()) {
            
            this.textFile.getNextLine();
            
            if(this.textFile.getIndex() == 2) {
                this.textFile.insert(textFile2);
            }
            
        }
        
        this.textFile.reset();
        assertEquals("Text file insertion error!", this.textFile.getNextLine(), "Start");
        this.textFile.getNextLine();
        assertEquals("Text file insertion error!", this.textFile.getNextLine(), "txt2: line 1");
        
        this.textFile.setIndex(5);
        assertTrue("Text has next error!", !this.textFile.hasNext());
        
        System.out.println("\nTest: textFileRuntimeAddTextTest");
        this.printTextFile(this.textFile);
        
    }
    
    /**
     * 
     * @param textFile 
     */
    private void printTextFile(TextFile textFile) {
        
        textFile.reset();
        
        while(textFile.hasNext()) {
            String line = textFile.getNextLine();
            System.out.println("Line: ["+line+"]");
        }
    }
}
