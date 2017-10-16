/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.parsers.tests;

import br.com.objectware.assembler.domain.Label;
import org.junit.Test;

/**
 * Validate Label expressions (valid and invalid cases are tested).
 * 
 * @author Luciano M. Christofoletti
 * @since 03/Apr/2015
 */
public class LabelTest {
    
    @Test
    public void validLabelsTest()  {
        
        String validLabels[] = {
            "xx", "d1", "AF0H", "B000111",
            "LABEL", "_LOOP", "_.?!~@#A", "a_______", "HL0000", "B_0___",
            "setup", "_test_", "@kill", "ok!", "____", "_0run", "DI", "HALT",
            "AA", "DD", "AI", "MSX", "MSX2", "ATARI_2600", "@macro", ".label",
            "_x", ".x", "?x", "!x", "~x", "@x", "#x",
            "_AF", ".BC", "?DE", "!HL", "~IX", "@IY", "#R", "BA", "ED",
            "AF_", "BC.", "DE?", "HL!", "IX~", "IY@", "R#", "Z80", "equ"
        };
        
        System.out.println("\n\t**** Valid labels ****");
        for(String label:validLabels) {
            System.out.println("Valid label: [" + new Label(label, 0) + "]");
        }
        
    }
    
    @Test() // not using the "expected" argument because we expect several exceptions
    public void invalidLabelsTest() {
        
        String invalidLabels[] = {
            "", " ", "\t", "_", ".", "?", "!", "~", "@", "#", "1", "2", "0", "1d", "66",
            "colon:", ":colon", "tabbed\tlabel", "spaced label", "A space B", "0AF0H",
            "0_first_is_digit", "-arg", "*.*", "hl", "A", "B", "C", "D", "E", "F",
            "H", "L", "R", "I", "AF", "BC", "DE", "HL", "IX", "IY", "(label", "label)",
            "slash/", "backs\\", "xx;", "function()", "test[", "test]", "test[]", "equals="
        };
        
        System.out.println("\n\t**** Invalid labels ****");
        for(String label:invalidLabels) {
            try {
                System.out.println("FIX: " + new Label(label, 0));
                throw new VerifyError("Label [" + label + "] is valid!");
            } catch(IllegalArgumentException iae) {
                System.out.println("Invalid label [" + label.replaceAll("", "") + "]");
            }
        }
        
    }
}
