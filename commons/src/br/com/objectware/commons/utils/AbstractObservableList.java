/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import java.util.Observable;

/**
 * This class is used to manager a list of elements that must notify observers about changes in the list.
 * It is possible to notify all observers about any change in the list (clear, add, remove, etc)
 * 
 * NOTE: changes in elements from this list may not notified to obeservers.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @param <T> the element type
 * @since 14/jan/2016
 */
public abstract class AbstractObservableList<T> extends java.util.ArrayList<T> implements java.util.Observer {
    
    /**
     * Return a list of all elements in this observable list.
     * The caller is free to change the list as desired without affecting the original list.
     * @return the list of elements
     */
    public java.util.List<T> list() {
        return new java.util.ArrayList<>(this);
    }   
    
    @Override
    public T set(int index, T element) {
        
        T oldElement = super.set(index, element);
        if(oldElement != null) {
            this.setChanged();
        }   
        
//        // remove the old element from observable list
//        if(oldElement instanceof Observable) {
//            ((Observable) oldElement).deleteObserver(this);
//        }
//        
//        if(element instanceof Observable) {
//            ((Observable) element).addObserver(this);
//        }
        
        return oldElement;
    }
    
    @Override
    public boolean add(T element) {
        
//        if(element instanceof Observable) {
//            ((Observable) element).addObserver(this);
//        }
        
        if(super.add(element)) {
            this.setChanged();
        }
        
        return this.hasChanged();
    }
    
    @Override
    public void add(int index, T element) {
        
//        if(element instanceof Observable) {
//            ((Observable) element).addObserver(this);
//        }
        this.setChanged();
        
        super.add(index, element);
    }
    
    @Override
    public boolean addAll(java.util.Collection<? extends T> collection) {
        
        // if addAll changed the original list, then update the observable list
        if (super.addAll(collection)) {
            
//            for (T element : collection) {
//                if (element instanceof Observable) {
//                    ((Observable) element).addObserver(this);
//                }
//            }
            
            this.setChanged();
        }
        
        return this.hasChanged();
    }   
    
    @Override
    public boolean addAll(int index, java.util.Collection<? extends T> collection) {
        
        // if addAll changed the original list, then update the observable list
        if (super.addAll(index, collection)) {
            
//            for (T element : collection) {
//                if (element instanceof Observable) {
//                    ((Observable) element).addObserver(this);
//                }
//            }
            
            this.setChanged();
        }
        
        return this.hasChanged();
    }   
    
    @Override
    public T remove(int index) {
        
        T oldElement = super.remove(index);
        
        if(oldElement instanceof Observable) {
//            ((Observable) oldElement).deleteObserver(this);
            this.setChanged();
        }
        
        return oldElement;
    }
    
    @Override
    public boolean remove(Object element) {
        
//        if(element instanceof Observable) {
//            ((Observable) element).deleteObserver(this);
//        }
        
        if(super.remove(element)) {
            this.setChanged();
        }   
        
        return this.hasChanged();
    }
    
    @Override
    public boolean removeAll(java.util.Collection<?> collection) {
        
        if(super.removeAll(collection)) {
            this.setChanged();
        }
        
//        for (Object element : collection) {
//            if (element instanceof Observable) {
//                ((Observable) element).deleteObserver(this);
//            }
//        }
        
        return this.hasChanged();
    }
    
    @Override
    public void clear() {
        for(T element:this.list()) {
            this.remove(element);
        }   
    }   
    
    /**
     * Move the given element one index up (that is, increment the element index).
     * @param element the element to be moved (must be available in the list)
     * @return 
     */
    public boolean moveUp(T element) {
        
        int index = this.indexOf(element);
        if (index + 1 < this.size()) {
            this.remove(element);
            this.add(index + 1, element);
        }
        
        return this.hasChanged();
    }
    
    /**
     * Move the given element one index down (that is, decrement the element index).
     * @param element the element to be moved (must be available in the list)
     * @return 
     */
    public boolean moveDown(T element) {
        
        int index = this.indexOf(element);
        if (index > 0) {
            this.remove(element);
            this.add(index - 1, element);
        }
        
        return this.hasChanged();
    }
    
    @Override
    public void update(Observable observable, Object object) {
        this.setChanged();
        this.notifyObservers(object);
    }
    
    /**
     * Marks this <tt>Observable</tt> object as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    protected abstract void setChanged();
    
    /**
     * Tests if this object has changed.
     *
     * @return  <code>true</code> if and only if the <code>setChanged</code>
     *          method has been called more recently than the
     *          <code>clearChanged</code> method on this object;
     *          <code>false</code> otherwise.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#setChanged()
     */
    public abstract  boolean hasChanged();
    
    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     * 
     * @param   object   any object.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public abstract  void notifyObservers(Object object);
}
