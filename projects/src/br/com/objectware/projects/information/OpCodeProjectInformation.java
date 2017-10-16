/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.information;

import br.com.objectware.projects.domain.OpCodeProject;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;

/**
 * The OpCode project information.
 * 
 * @author Luciano M. Christofoletti
 * @since 20/Apr/2015
 */
public final class OpCodeProjectInformation implements ProjectInformation {
    
    @StaticResource()
    public static final String CUSTOMER_ICON = "resources/sub-icon.png";
    
    private final OpCodeProject project;
    
    public OpCodeProjectInformation(OpCodeProject project) {
        this.project = project;
    }
    
    @Override
    public Icon getIcon() {
        return this.project.getProjectIcon();
    }
    
    @Override
    public String getName() {
        return this.project.getProjectDirectory().getName();
    }
    
    @Override
    public String getDisplayName() {
        return this.getName();
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }
    
    @Override
    public Project getProject() {
        return this.project;
    }
}
