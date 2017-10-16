/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.wizards.msx;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.TimerUtil;
import br.com.objectware.domain.BuildType;
import br.com.objectware.domain.ProjectProperties;
import br.com.objectware.domain.enums.Platform;
import br.com.objectware.ioutils.project.ProjectFilesManager;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

// TODO define position attribute
/**
 *
 * @author Luciano M. Christofoletti
 */
@SuppressWarnings("rawtypes")
public abstract class MSXWizardIterator implements WizardDescriptor.InstantiatingIterator {
    
    // the wizard panels
    private WizardDescriptor.Panel[] panels;
    private int index;
    
    /** Wizard descriptor: stores the user selected properties while creating the project */
    private WizardDescriptor wizardDescriptor;
    
    /**
     * This abstract method must be implemented by the subtypes (templates) project iterators.
     * @return the selected build type
     */
    protected abstract BuildType getBuildType();
    
    /** Instantiate the wizard panels. In this case, just one */
    @SuppressWarnings("unchecked")
    protected WizardDescriptor.Panel<JComponent>[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new MSXProjectWizardPanel()
        };
    }
    
    /** Create the list of wizard steps (panel's names) */
    private String[] createSteps() {
        return new String[] {
            I18N.getString("name.and.location")
        };
    }
    
    /**
     * This method is invoked when the user selects the project type and goes to the next panel
     * in the project wizard.
     * 
     * @param wizardDescriptor the wizard descriptor used to store the project properties
     */
    @Override
    public void initialize(WizardDescriptor wizardDescriptor) {
        
        this.wizardDescriptor = wizardDescriptor;
        this.index = 0;
        this.panels = this.createPanels();
        
        // Make sure list of steps is accurate.
        String[] steps = this.createSteps();
        
        for (int i = 0; i < this.panels.length; i++) {
            
            JComponent jComponent = (JComponent) this.panels[i].getComponent();
            if (steps[i] == null) {
                // default step name to component name of panel
                // useful for getting the name of the target chooser to appear in the list of steps
                steps[i] = jComponent.getName();
            }
            
            jComponent.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
            jComponent.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
        }   
        
        // stores the project build type selected by the user in the first step of the wizard
        this.wizardDescriptor.putProperty("project.build.type", this.getBuildType());
    }   
    
    /**
     * This method is invoked when the user click the finish button in the last wizard panel.
     * 
     * @return
     * @throws IOException 
     */
    @Override
    public Set<FileObject> instantiate() throws IOException {
        
        Set<FileObject> resultSet = new LinkedHashSet<>();
        File selectedFolder = FileUtil.normalizeFile((File) this.wizardDescriptor.getProperty("project.dir"));
        
        // tries to create the project folder
        System.out.println("[INFO] creating project folder: " + selectedFolder);
        if(!selectedFolder.mkdirs()) {
            // TODO: show error message to the user
            return resultSet;
        }
        
        // unzip the project structure template
        FileObject template = Templates.getTemplate(this.wizardDescriptor);
        FileObject projectFolder = FileUtil.toFileObject(selectedFolder);
        br.com.objectware.commons.utils.FileUtil.unZipFile(template.getInputStream(), projectFolder);
        
        // wait for system update
        TimerUtil.sleep(200);
        
        // rename the main source file
        String mainFileName = (String) this.wizardDescriptor.getProperty("main.file.name");
        String mainFromTemplate = br.com.objectware.commons.utils.FileUtil.translatePath(
            br.com.objectware.commons.utils.FileUtil.getPath(
                selectedFolder.getCanonicalPath(), Default.SOURCES_FOLDER + Default.FS + Default.MAIN_FULL_FILE_NAME
            )
        );
        
        br.com.objectware.commons.utils.FileUtil.remane(mainFromTemplate, mainFileName);
        
        // refresh the project folder contents
        TimerUtil.sleep(3000);
        projectFolder.refresh();
        
        // always open top dir as a project:
        resultSet.add(projectFolder);
        
        // save the project descriptor filled with all selected properties
        this.saveProjectDescriptor(projectFolder);
        TimerUtil.sleep(200);
        
        return resultSet;
    }
    
    /**
     * 
     * @param dir
     * @throws IOException 
     */
    private void saveProjectDescriptor(FileObject dir) throws IOException {
        
        try {
            
            // get the project properties
            String projectName = (String) this.wizardDescriptor.getProperty("project.name");
            String mainFileName = (String) this.wizardDescriptor.getProperty("main.file.name");
            String targetFileName = (String) this.wizardDescriptor.getProperty("target.file.name");
            String projectDescription = (String) this.wizardDescriptor.getProperty("project.description");
            
            // setup the build parameters for the internal compiler
            StringBuilder buildOptions = new StringBuilder();
            
            // the main source file name location (source)
            buildOptions.append(Default.SOURCES_FOLDER).append(Default.OS_FS).append(mainFileName).append(' ');
            
            // the target file name location (object)
            buildOptions.append(Default.BUILD_FOLDER).append(Default.OS_FS).append(targetFileName).append(' ');
            
            // the symbols file name location (symbol)
            buildOptions.append("-symbols");
            
            // setup the project properties
            ProjectProperties projectProperties = new ProjectProperties(projectName);
            projectProperties.setBuildOptions(buildOptions.toString());
            projectProperties.setDescription(projectDescription);
            
            // properties that are filled by the system
            projectProperties.setTargetPlatform(Platform.MSX);
            projectProperties.setCreationDate(new Timestamp(System.currentTimeMillis()));
            projectProperties.update();
            
            // set the project files manager
            ProjectFilesManager<ProjectProperties> manager = new ProjectFilesManager<>(dir.getPath());
            
            // save the project properties xml descriptor
            manager.saveProjectDescriptor(projectProperties);
            
        } catch (JAXBException exception) {
            // throws an IOException that will be handled by the caller metohd
            throw new IOException(exception);
        }
    }
    
    @Override
    public void uninitialize(WizardDescriptor wiz) {
        
        this.wizardDescriptor.putProperty("project.dir", null);
        this.wizardDescriptor.putProperty("name", null);
        
        this.wizardDescriptor = null;
        this.panels = null;
    }
    
    @Override
    public String name() {
        return I18N.getString("stepn.of.stepm", this.index + 1, this.panels.length);
    }
    
    @Override
    public boolean hasNext() {
        return (this.index + 1 < this.panels.length);
    }
    
    @Override
    public boolean hasPrevious() {
        return (this.index > 0);
    }
    
    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        this.index++;
    }
    
    @Override
    public void previousPanel() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException();
        }
        this.index--;
    }
    
    @Override
    public WizardDescriptor.Panel current() {
        return this.panels[this.index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public final void addChangeListener(ChangeListener changeListener) {
    }
    
    @Override
    public final void removeChangeListener(ChangeListener changeListener) {
    }
    
}
