; hello.asm
; author: Luciano M. Christofoletti
; date: 04/apr/2015
;
    
    ORG 09100H
    
.start:
        NOP
        XOR   A
        DEC   A
        LD    B,A
        
loop:   HALT
        DEC   B
        DJNZ  loop
        
        RET
        
; end of hello.asm