;--------------------------------------------
; PROGRAMA HELLO WORLD, PARA O MSX
;--------------------------------------------

.org 8000h
.start 8000h
.rom
    
    INITXT equ 050eh
    LDIRVM equ 0744h
    
INICIO:
    
    CALL     INITXT
    
    LD       BC, 12
    LD       DE, 0
    LD       HL, TEXTO
    
    CALL     LDIRVM
    
FIM:
    JR FIM

TEXTO:

    db "Hello World!"
