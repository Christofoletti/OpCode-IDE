/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.parser.test;

import br.com.objectware.commons.utils.LineParser;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * LineParser unit tests.
 * 
 * @author Luciano M. Christofoletti
 * @since 20/Jun/2015
 * 
 * @see http://junit.org/
 */
public class LineParserTest {
    
    public LineParserTest() {
    }
    
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
    public void paramterCountTest() {
        
        String parameters = "param1 param2 param3";
        
        List<String> parseArguments = LineParser.parseArguments(parameters);
        
        assertNotNull(parseArguments);
        assertEquals(3, parseArguments.size());
        
        assertEquals(6, parseArguments.get(0).length());
        assertEquals(6, parseArguments.get(1).length());
        assertEquals(6, parseArguments.get(2).length());
    }
    
    @Test
    public void singleQuotesTest() {
        
        String parameters = "'param1' param2 'param3'\t";
        
        List<String> parseArguments = LineParser.parseArguments(parameters);
        
        assertNotNull(parseArguments);
        assertEquals(3, parseArguments.size());
        
        assertEquals(parseArguments.get(0).length(), 8);
        assertEquals(parseArguments.get(1).length(), 6);
        assertEquals(parseArguments.get(2).length(), 8);
        
        assertEquals('\'', parseArguments.get(0).charAt(0));
        assertEquals('\'', parseArguments.get(0).charAt(7));
        
        assertEquals('p', parseArguments.get(1).charAt(0));
        assertEquals('2', parseArguments.get(1).charAt(5));
        
        assertEquals('\'', parseArguments.get(2).charAt(0));
        assertEquals('\'', parseArguments.get(2).charAt(7));
    }
    
    @Test
    public void quotationMarksTest() {
        
        String parameters = "\"param\"\t'param2' 'param3(\"x\")'      ";
        
        List<String> parseArguments = LineParser.parseArguments(parameters);
        
        assertNotNull(parseArguments);
        assertEquals(3, parseArguments.size());
        
        assertEquals(7, parseArguments.get(0).length());
        assertEquals(8, parseArguments.get(1).length());
        assertEquals(13, parseArguments.get(2).length());
        
        assertEquals('\"', parseArguments.get(0).charAt(0));
        assertEquals('\"', parseArguments.get(0).charAt(6));
        
        assertEquals('\'', parseArguments.get(1).charAt(0));
        assertEquals('\'', parseArguments.get(1).charAt(7));
        
        assertEquals('\'', parseArguments.get(2).charAt(0));
        assertEquals('\"', parseArguments.get(2).charAt(8));
        assertEquals('\"', parseArguments.get(2).charAt(10));
        assertEquals('\'', parseArguments.get(2).charAt(12));
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void missingWhiteSpaceTest() {
        
        String parameters = "string1\"quotation bla bla bla... ";
        
        LineParser.parseArguments(parameters);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void missingWhiteSpaceBeginingTest() {
        
        String parameters = "string1\"quotation\" bla bla bla... ";
        
        LineParser.parseArguments(parameters);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void missingStartQuotationTest() {
        
        String parameters = "string1 quotation\" bla bla bla... ";
        
        LineParser.parseArguments(parameters);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void missingEndQuotationTest() {
        
        String parameters = "string1 \"quotation bla bla bla... ";
        
        LineParser.parseArguments(parameters);
    }
}
