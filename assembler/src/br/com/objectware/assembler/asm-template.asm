; MSX DOS Project version 1.0
; OpCode IDE - www.objectware.net
; 
; Author: OpCode User
; Target: MSX DOS
; 
; This program runs in MSX DOS.
    
    include "lib/defs.asm"
    
TARGET:     equ     0
ROMAREA:    equ     08000H
RAMAREA:    equ     0E000H
    
    org	    0100H
    
    ; show hello message using bios STROUT routine
    LD        DE,HELLO_STRING
    LD        C,STROUT
    CALL      05H
    
    ; set error code to zero and return
    LD        C,TERM0
    JP        05H
    
HELLO_STRING:
    db "Hello from OpCode IDE", 13, 10, "$"
    
ROMPAD:
    if TARGET = 1
        ; fill up the rest of the page with zeros (8 Kb ROM)
        ds ROMAREA + ROMSIZE - $
    endif
    