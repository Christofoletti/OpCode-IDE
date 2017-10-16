/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.actions;

import br.com.objectware.projects.domain.OpCodeProject;
import org.netbeans.api.project.Project;

/**
 * Provides the build interface for OpCode projects.
 * 
 * @author Luciano M. Christofoletti
 * @since 24/Apr/2015
 */
public interface BuildProjectAction {
    
    Object build(OpCodeProject project);
    
    Project getProject();
    
    boolean isProcessing();
}
