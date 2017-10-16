/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.parsers.tests;

import br.com.objectware.assembler.Assemblers;
import br.com.objectware.assembler.domain.OpCode;
import br.com.objectware.assembler.z80.Z80Assembler;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author Luciano M. Christofoletti
 * @since 31/Mar/2015
 * 
 * Useful Docs:
 *     JUnit: http://junit.sourceforge.net/javadoc/org/junit/package-tree.html
 */
public class Z80AssemblerTest {
    
    private static Z80Assembler z80Assembler;
    
    private static final String Z80_OPCODES_PATH = "br/com/objectware/assembler/resources/z80/";
    
    private static final String Z80_OPCODES_FILE_NAME = "z80-opcodes.dat";
    
    public Z80AssemblerTest() {
    }
    
    /* This method runs once before all tests */
    @BeforeClass
    public static void setUpClass() {
        
        // create the z80 assembler instance for testing
        Z80AssemblerTest.z80Assembler = new Z80Assembler();
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /* This method runs before each test */
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void findOpCodesDataFileTest() throws FileNotFoundException, IOException {
        
        String z80OpCodesPath = Z80_OPCODES_PATH + Z80_OPCODES_FILE_NAME;
        
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(z80OpCodesPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        List<String> lines = new ArrayList<>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Z80AssemblerTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Z80AssemblerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        assertTrue(lines.size() > 0);
        System.out.println("Total of lines read: "+lines.size());

    }
    
    @Test
    public void loadOpCodesTest() {
        
        String z80OpCodesPath = Z80_OPCODES_PATH + Z80_OPCODES_FILE_NAME;
        Z80AssemblerTest.z80Assembler.loadOpCodes(z80OpCodesPath);
        
        Map<String, List<OpCode>> opCodes = Z80AssemblerTest.z80Assembler.getOpCodes();
        for(String prefix:opCodes.keySet()) {
            
            System.out.println("OpCode set: ["+prefix+"]\tSize: "+opCodes.get(prefix).size());
            
            List<OpCode> opCodesList = opCodes.get(prefix);
            for(OpCode opCode:opCodesList) {
                System.out.println("\tOpCode: "+opCode.getOpCode()+" ["+opCode.getMnemonic()+"]");
            }
            
        }
    }
    
    @Test
    public void newInstanceTest() {
        
//        try {
//            
//            // test the newInstance() method from "Assemblers" enum
//            Assemblers.Z80.newInstance();
//            
//        } catch (InstantiationException | IllegalAccessException exception) {
//            Exceptions.printStackTrace(exception);
//        }
    }
}
