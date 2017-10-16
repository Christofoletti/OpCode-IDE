/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.domain;

import br.com.objectware.commons.i18n.I18N;
import java.util.regex.Pattern;

/**
 * Label element. A label is defined once at an specific address and
 * cannot be changed after that.
 * 
 * @author Luciano M. Christofoletti
 * @since 02/Apr/2015
 * 
 * Useful docs about regular expressions:
 *     https://msdn.microsoft.com/en-us/library/az24scfc%28v=vs.110%29.aspx
 *     http://www.grymoire.com/Unix/Regular.html
 *     http://www.regular-expressions.info/
 *     http://www.cs.cf.ac.uk/Dave/Internet/NEWS/regexp.html
 */
public final class Label {
    
    /** The label textual representation */
    private final String text;
    
    /** The address in memory associated to this label */
    private final int address;
    
    /** Regular Expression that defines valid assembly labels */
	private static final String LABELS_REGEX =
        "^(?!AF$)(?!BC$)(?!DE$)(?!HL$)(?!IX$)(?!IY$)" +          // 16 bit registers are reserved words
        "[_.?!~@#A-Za-z]+[_.?!~@#A-Za-z\\d]+([_.?!~@#A-Za-z\\d])*$"; // the other label syntax rules...
    
    /** The pattern matcher */
    private static final Pattern PATTERN = Pattern.compile(Label.LABELS_REGEX);
    
    /**
     * Label constructor. Set all parameters of the label (they are all finals).
     * @param text the label's text
     * @param address 
     */
    public Label(String text, int address) {
        this.text = this.validate(text);
        this.address = address;
    }
    
    public String getText() {
        return this.text;
    }
    
    public int getAddress() {
        return this.address;
    }
    
    /**
     * Validate the label "text" accordingly to the rules defined in the LABEL_REGEX expression.
     * 
     * @param label the label text to be validated
     * @return the validated label
     */
    public String validate(final String label) {
        
        String upperCaseLabel = label.toUpperCase();
        
        if(!PATTERN.matcher(upperCaseLabel).matches()) {
            throw new IllegalArgumentException(I18N.getString("invalid.label", label));
        }
        
        // stores only the label text, without the colon char
		return upperCaseLabel;
    }   
    
    @Override
    public String toString() {
        return this.text;
    }
}