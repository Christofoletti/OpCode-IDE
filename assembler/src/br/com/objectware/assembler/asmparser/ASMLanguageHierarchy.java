/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.assembler.asmparser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * 
 * Useful docs:
 *     https://platform.netbeans.org/tutorials/nbm-javacc-lexer.html
 *     http://wiki.netbeans.org/New_Language_Support_Tutorial_Antlr
 *     http://wiki.netbeans.org/SyntaxColoringANTLR
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since whenever!
 */
public class ASMLanguageHierarchy extends LanguageHierarchy<ASMTokenId> {
    
    /** List of all availble tokens */
    private static List<ASMTokenId> TOKENS;
    
    /** Map of tokens (id, token) */
    private static final Map<Integer, ASMTokenId> ID_TO_TOKEN = new HashMap<>();
    
    // initializes the token list and id_to_token map
    static {
        init();
    }
    
    private static void init() {
        
        // Tokens from Z80AsmCC.jj version 1.1
        TOKENS = Arrays.<ASMTokenId>asList(new ASMTokenId[] {
            
            new ASMTokenId("EOF", "whitespace", 0),
            new ASMTokenId("SINGLE_LINE_COMMENT", "comment", 8),
            new ASMTokenId("MULTI_LINE_COMMENT", "comment", 9),
            
            new ASMTokenId("A", "register", 11),
            new ASMTokenId("B", "register", 12),
            new ASMTokenId("C", "register", 13),
            new ASMTokenId("D", "register", 14),
            new ASMTokenId("E", "register", 15),
            new ASMTokenId("H", "register", 16),
            new ASMTokenId("L", "register", 17),
            new ASMTokenId("I", "register", 18),
            new ASMTokenId("R", "register", 19),
            new ASMTokenId("AF", "register", 20),
            new ASMTokenId("BC", "register", 21),
            new ASMTokenId("DE", "register", 22),
            new ASMTokenId("HL", "register", 23),
            new ASMTokenId("IX", "register", 24),
            new ASMTokenId("IY", "register", 25),
            new ASMTokenId("SP", "register", 26),
            new ASMTokenId("IXH", "register", 27),
            new ASMTokenId("IXL", "register", 28),
            new ASMTokenId("IYH", "register", 29),
            new ASMTokenId("IYL", "register", 30),
            
            new ASMTokenId("M", "register", 31),
            new ASMTokenId("NC", "register", 32),
            new ASMTokenId("NZ", "register", 33),
            new ASMTokenId("P", "register", 34),
            new ASMTokenId("PE", "register", 35),
            new ASMTokenId("PO", "register", 36),
            new ASMTokenId("Z", "register", 37),
            
            new ASMTokenId("ADC", "keyword", 38),
            new ASMTokenId("ADD", "keyword", 39),
            new ASMTokenId("AND", "keyword", 40),
            new ASMTokenId("BIT", "keyword", 41),
            new ASMTokenId("BYTE", "keyword", 42),
            new ASMTokenId("CALL", "keyword", 43),
            new ASMTokenId("CCF", "keyword", 44),
            new ASMTokenId("CP", "keyword", 45),
            new ASMTokenId("CPD", "keyword", 46),
            new ASMTokenId("CPDR", "keyword", 47),
            new ASMTokenId("CPI", "keyword", 48),
            new ASMTokenId("CPIR", "keyword", 49),
            new ASMTokenId("CPL", "keyword", 50),
            new ASMTokenId("DAA", "keyword", 51),
            new ASMTokenId("DEC", "keyword", 52),
            new ASMTokenId("DI", "keyword", 53),
            new ASMTokenId("DJNZ", "keyword", 54),
            new ASMTokenId("EI", "keyword", 55),
            new ASMTokenId("EX", "keyword", 56),
            new ASMTokenId("EXX", "keyword", 57),
            new ASMTokenId("HALT", "keyword", 58),
            new ASMTokenId("IM", "keyword", 59),
            new ASMTokenId("IN", "keyword", 60),
            new ASMTokenId("INC", "keyword", 61),
            new ASMTokenId("IND", "keyword", 62),
            new ASMTokenId("INDR", "keyword", 63),
            new ASMTokenId("INI", "keyword", 64),
            new ASMTokenId("INIR", "keyword", 65),
            new ASMTokenId("JP", "keyword", 66),
            new ASMTokenId("JR", "keyword", 67),
            new ASMTokenId("LD", "keyword", 68),
            new ASMTokenId("LDD", "keyword", 69),
            new ASMTokenId("LDDR", "keyword", 70),
            new ASMTokenId("LDI", "keyword", 71),
            new ASMTokenId("LDIR", "keyword", 72),
            new ASMTokenId("NEG", "keyword", 73),
            new ASMTokenId("NOP", "keyword", 74),
            new ASMTokenId("OR", "keyword", 75),
            new ASMTokenId("OTDR", "keyword", 76),
            new ASMTokenId("OTIR", "keyword", 77),
            new ASMTokenId("OUT", "keyword", 78),
            new ASMTokenId("OUTD", "keyword", 79),
            new ASMTokenId("OUTI", "keyword", 80),
            new ASMTokenId("POP", "keyword", 81),
            new ASMTokenId("PUSH", "keyword", 82),
            new ASMTokenId("RES", "keyword", 83),
            new ASMTokenId("RET", "keyword", 84),
            new ASMTokenId("RETI", "keyword", 85),
            new ASMTokenId("RETN", "keyword", 86),
            new ASMTokenId("RL", "keyword", 87),
            new ASMTokenId("RLA", "keyword", 88),
            new ASMTokenId("RLC", "keyword", 89),
            new ASMTokenId("RLCA", "keyword", 90),
            new ASMTokenId("RLD", "keyword", 91),
            new ASMTokenId("RR", "keyword", 92),
            new ASMTokenId("RRA", "keyword", 93),
            new ASMTokenId("RRC", "keyword", 94),
            new ASMTokenId("RRCA", "keyword", 95),
            new ASMTokenId("RRD", "keyword", 96),
            new ASMTokenId("RST", "keyword", 97),
            new ASMTokenId("SBC", "keyword", 98),
            new ASMTokenId("SCF", "keyword", 99),
            new ASMTokenId("SET", "keyword", 100),
            new ASMTokenId("SLA", "keyword", 101),
            new ASMTokenId("SLL", "keyword", 102),
            new ASMTokenId("SRA", "keyword", 103),
            new ASMTokenId("SRL", "keyword", 104),
            new ASMTokenId("SUB", "keyword", 105),
            new ASMTokenId("XOR", "keyword", 106),
            
            new ASMTokenId("IF", "directive", 107),
            new ASMTokenId("DB", "directive", 108),
            new ASMTokenId("DS", "directive", 109),
            new ASMTokenId("DW", "directive", 110),
            new ASMTokenId("END", "directive", 111),
            new ASMTokenId("EQU", "directive", 112),
            new ASMTokenId("ORG", "directive", 113),
            new ASMTokenId("DEFB", "directive", 114),
            new ASMTokenId("DEFW", "directive", 115),
            new ASMTokenId("ELSE", "directive", 116),
            new ASMTokenId("ENDIF", "directive", 117),
            new ASMTokenId("DEFINE", "directive", 118),
            new ASMTokenId("INCBIN", "directive", 119),
            new ASMTokenId("INCSRC", "directive", 120),
            new ASMTokenId("INCLUDE", "directive", 121),
            new ASMTokenId("ONCE", "directive", 122),
            new ASMTokenId("REPT", "directive", 123),
            new ASMTokenId("ENDR", "directive", 124),
            new ASMTokenId("PROC", "directive", 125),
            new ASMTokenId("ENDP", "directive", 126),
            new ASMTokenId("MACRO", "directive", 127),
            new ASMTokenId("ENDM", "directive", 128),
            new ASMTokenId("SECTION", "directive", 129),
            
            new ASMTokenId("LPAREN", "operator", 130),
            new ASMTokenId("RPAREN", "operator", 131),
            new ASMTokenId("LBRACE", "operator", 132),
            new ASMTokenId("RBRACE", "operator", 133),
            new ASMTokenId("LBRACKET", "operator", 134),
            new ASMTokenId("RBRACKET", "operator", 135),
            new ASMTokenId("COMMA", "operator", 136),
            new ASMTokenId("DOT", "operator", 137),
            
            new ASMTokenId("ASSIGN", "operator", 138),
            new ASMTokenId("GT", "operator", 139),
            new ASMTokenId("LT", "operator", 140),
            new ASMTokenId("BANG", "operator", 141),
            new ASMTokenId("TILDE", "operator", 142),
            new ASMTokenId("HOOK", "operator", 143),
            new ASMTokenId("COLON", "operator", 144),
            new ASMTokenId("EQ", "operator", 145),
            new ASMTokenId("LE", "operator", 146),
            new ASMTokenId("GE", "operator", 147),
            new ASMTokenId("NE", "operator", 148),
            new ASMTokenId("SC_OR", "operator", 149),
            new ASMTokenId("SC_AND", "operator", 150),
            new ASMTokenId("INCR", "operator", 151),
            new ASMTokenId("DECR", "operator", 152),
            new ASMTokenId("PLUS", "operator", 153),
            new ASMTokenId("MINUS", "operator", 154),
            new ASMTokenId("STAR", "operator", 155),
            new ASMTokenId("SLASH", "operator", 156),
            new ASMTokenId("BIT_AND", "operator", 157),
            new ASMTokenId("BIT_OR", "operator", 158),
            new ASMTokenId("BIT_XOR", "operator", 159),
            new ASMTokenId("REM", "operator", 160),
            
            new ASMTokenId("BR", "other", 161),
            new ASMTokenId("OBJECTWARE", "other", 162),
            
            new ASMTokenId("INTEGER_LITERAL", "number", 163),
            new ASMTokenId("DECIMAL_LITERAL", "number", 164),
            new ASMTokenId("HEX_LITERAL", "number", 165),
            new ASMTokenId("OCTAL_LITERAL", "number", 166),
            new ASMTokenId("BINARY_LITERAL", "number", 167),
            new ASMTokenId("FLOATING_POINT_LITERAL", "number", 168),
            new ASMTokenId("EXPONENT", "number", 169),
            new ASMTokenId("CHARACTER_LITERAL", "literal", 170),
            new ASMTokenId("STRING_LITERAL", "string", 171),
            new ASMTokenId("IDENTIFIER", "identifier", 172),
            new ASMTokenId("LABEL", "identifier", 173),
            new ASMTokenId("LETTER", "other", 174),
            new ASMTokenId("DIGIT", "other", 175),
            new ASMTokenId("ERROR", "error", 176)
            
        });
        
        //ASMLanguageHierarchy.ID_TO_TOKEN = new HashMap<>();
        for (ASMTokenId token : TOKENS) {
            ID_TO_TOKEN.put(token.ordinal(), token);
        }   
    }   
    
    static synchronized ASMTokenId getToken(int id) {
        return ID_TO_TOKEN.get(id);
    }
    
    @Override
    protected synchronized Collection<ASMTokenId> createTokenIds() {
        return TOKENS;
    }   
    
    @Override
    protected synchronized Lexer<ASMTokenId> createLexer(LexerRestartInfo<ASMTokenId> info) {
        return new ASMLexer(info);
    }
    
    @Override
    protected String mimeType() {
        return "text/x-asm";
    }
}
