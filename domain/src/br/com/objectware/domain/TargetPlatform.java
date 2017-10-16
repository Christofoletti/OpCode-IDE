/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain;

import java.util.List;
import javax.swing.ImageIcon;

/**
 * This interface defines the target platform default methods.
 * 
 * @author Luciano M. Christofoletti
 * @since 29/Mar/2015
 */
public interface TargetPlatform {
    
    String getId();
    
    String getName();
    
    String getLongName();
    
    ImageIcon getIcon();
    
    String getDescription();
    
    List<? extends TargetPlatform> list();
    
    List<BuildType> getBuildTypes();
}
