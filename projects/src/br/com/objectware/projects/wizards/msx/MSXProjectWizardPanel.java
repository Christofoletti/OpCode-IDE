/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.wizards.msx;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
@SuppressWarnings("rawtypes")
public class MSXProjectWizardPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {
    
    private WizardDescriptor wizardDescriptor;
    private MSXProjectPanelVisual component;
    
    private final Set<ChangeListener> listeners = new HashSet<>(1); // or can use ChangeSupport in NB 6.0
    
    @Override
    public Component getComponent() {
        if (this.component == null) {
            this.component = new MSXProjectPanelVisual(this);
            this.component.setName(NbBundle.getMessage(MSXProjectWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return this.component;
    }
    
    @Override
    public HelpCtx getHelp() {
        //return new HelpCtx("NEW_MSX_PROJECT_HELP");
        return HelpCtx.DEFAULT_HELP;
    }
    
    @Override
    public boolean isValid() {
        this.getComponent();
        return this.component.valid(this.wizardDescriptor);
    }
    
    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (this.listeners) {
            this.listeners.add(l);
        }
    }
    
    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (this.listeners) {
            this.listeners.remove(l);
        }
    }
    
    protected final void fireChangeEvent() {
        
        Set<ChangeListener> ls;
        synchronized (this.listeners) {
            ls = new HashSet<>(this.listeners);
        }
        ChangeEvent ev = new ChangeEvent(this);
        for (ChangeListener l : ls) {
            l.stateChanged(ev);
        }
    }
    
    @Override
    public void readSettings(Object settings) {
        this.wizardDescriptor = (WizardDescriptor) settings;
        this.component.read(this.wizardDescriptor);
    }
    
    @Override
    public void storeSettings(Object settings) {
        this.component.store((WizardDescriptor) settings);
    }
    
    @Override
    public boolean isFinishPanel() {
        return true;
    }
    
    @Override
    public void validate() throws WizardValidationException {
        this.getComponent();
        this.component.validate(this.wizardDescriptor);
    }
    
}
