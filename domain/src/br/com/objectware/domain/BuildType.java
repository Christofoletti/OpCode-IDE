/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain;

import java.util.List;
import javax.swing.ImageIcon;

/**
 * This class define the build target type (e.g. .rom file, .com executable, binary file, etc)
 * 
 * @author Luciano M. Christofoletti
 * @since 29/Mar/2015
 */
public interface BuildType {
    
    String getId();
    
    int getMaxSize();
    
    String getExtension();
    
    ImageIcon getIcon();
    
    String getDefaultProjectName();
    
    String getDescription();
    
    List<? extends BuildType> list();
}
