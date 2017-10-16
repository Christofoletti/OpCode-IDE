/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.objectware.assembler.domain;

import br.com.objectware.commons.i18n.I18N;

/**
 * Binary data representation. An object of this class may be used to generate the 
 * compiler's output binary file or a binary data to be included (attached)
 * inside a source file in the project.
 * 
 * @author Luciano M. Christofoletti
 * @since 28/Mar/2015
 */
public class BinaryData {
    
    private final byte data[];
    
    private int pointer;
    
    public BinaryData(int size) {
        this.data = new byte[size];
        this.setPointer(0);
    }
    
    public int getPointer() {
        return this.pointer;
    }
    
    /**
     * 
     * @param pointer 
     */
    public final void setPointer(int pointer) {
        if(pointer >= 0 && pointer < this.data.length) {
            this.pointer = pointer;
        } else {
            throw new IndexOutOfBoundsException(I18N.getString("invalid.pointer.address"));
        }
    }
    
    /**
     * 
     * @param position
     * @return 
     */
    public byte getByte(int position) {
        
        // update the pointer position (resets the pointer if the end of 
        this.pointer = (position + 1) % this.data.length;
        
        return this.data[position];
    }
    
    /**
     * 
     * @param position
     * @param size
     * @return 
     */
    public byte[] getBytes(int position, int size) {
        
        byte bytes[] = new byte[size];
        System.arraycopy(this.data, position, bytes, 0, size);
        
        this.pointer += size;
        
        return bytes;
    }
    
    /**
     * 
     * @param size
     * @return 
     */
    public byte[] getBytes(int size) {
        return this.getBytes(this.pointer, size);
    }
    
    /**
     * 
     * @param position
     * @param bytes 
     */
    public void setBytes(int position, byte bytes[]) {
        System.arraycopy(bytes, 0, this.data, position, bytes.length);
        this.setPointer(position + bytes.length);
    }
    
    /**
     * 
     * @param bytes 
     */
    public void setBytes(byte bytes[]) {
        System.arraycopy(bytes, 0, this.data, this.pointer, bytes.length);
        this.setPointer(this.pointer + bytes.length);
    }
    
    /**
     * 
     * @param data 
     */
    public void setByte(byte data) {
        this.data[this.pointer] = data;
        this.setPointer(this.pointer + 1);
    }
}
