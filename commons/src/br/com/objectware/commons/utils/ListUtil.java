/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

/**
 * Some useful list manipulation methods.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 14/apr/2016
 */
public class ListUtil {
    
    /**
     * Move an element down on the list 
     * @param list the list to be updated
     * @param element the element in list to be moded up (towards list tail)
     */
    public static void moveDown(java.util.List<? extends Object> list, Object element) {
        int index = list.indexOf(element);
        if (index + 1 < list.size()) {
            java.util.Collections.swap(list, index, index + 1);
        }   
    }
    
    /**
     * Move an element up on the list 
     * @param list the list to be updated
     * @param element the element in list to be moded up (towards list head)
     */
    public static void moveUp(java.util.List<? extends Object> list, Object element) {
        int index = list.indexOf(element);
        if (index > 0) {
            java.util.Collections.swap(list, index, index - 1);
        }   
    }
    
    /**
     * Useful method for creating a list of repeated elements (a list of black colors, for example)
     * @param size
     * @param element
     * @return 
     */
    @SuppressWarnings("rawtypes")
    public static java.util.List<? extends Object> newListOfElements(int size, Object element) {
        
        java.util.List<Object> list = new java.util.ArrayList<>();
        for (int k = 0; k < size; k++) {
            list.add(element);
        }
        
        return list;
    }
}
