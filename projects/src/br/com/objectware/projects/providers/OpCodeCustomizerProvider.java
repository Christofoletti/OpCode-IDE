/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.providers;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.FileUtil;
import br.com.objectware.domain.ProjectProperties;
import br.com.objectware.ioutils.project.ProjectFilesManager;
import br.com.objectware.ioutils.text.TextFileIOUtil;
import br.com.objectware.projects.domain.OpCodeProject;
import br.com.objectware.projects.panels.BuildAndRunOptionsPanel;
import br.com.objectware.projects.panels.ProjectPropertiesPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 * The OpCode project customizer provider.
 * 
 * @author Luciano M. Christofoletti
 * @since 21/Apr/2015
 * 
 * Useful docs:
 *  http://bits.netbeans.org/dev/javadoc/org-netbeans-modules-projectuiapi/org/netbeans/spi/project/ui/support/ProjectCustomizer.html
 *  
 */
public class OpCodeCustomizerProvider implements CustomizerProvider, ActionListener {
    
    /** The OpCodeProject that is associated to this customizer */
    public final OpCodeProject opCodeProject;
    
    /** The project properties dialog */
    private Dialog customizerDialog;
    
    //private ProjectCustomizer.Category[] categories;
    private PropertyPanelsProvider panelsProvider;
    
    /**
     * The project customizer provider is instantiated when the project is opened.
     * @param project
     */
    public OpCodeCustomizerProvider(OpCodeProject project) {
        this.opCodeProject = project;
    }
    
    @Override
    public void showCustomizer() {
        
        // if the current project properties opened, bring it to front
        if(this.customizerDialog != null && this.customizerDialog.isVisible()) {
            this.customizerDialog.toFront();
            return;
        }
        
        // lazy initialization
        if(this.panelsProvider == null) {
            this.panelsProvider = new PropertyPanelsProvider();
        }
        
        // create the properties dialog
        this.customizerDialog = ProjectCustomizer.createCustomizerDialog(
            this.panelsProvider.getCategories(),
            this.panelsProvider,
            null,  // String
            this,  // ActionListener
            null   // HelpCtx
        );
        
        ProjectProperties properties = this.opCodeProject.getProperties();
        String projectPath = this.opCodeProject.getProjectDirectory().getPath();
        
        // get the project properties
        ProjectPropertiesPanel projectPropertiesPanel = this.panelsProvider.getProjectPropertiesPanel();
        
        // fill the dialog project properties fields
        projectPropertiesPanel.clearAll();
        projectPropertiesPanel.setProjectDirectory(this.opCodeProject.getProjectDirectory());
        projectPropertiesPanel.setTargetPlatform(properties.getTargetPlatform());
        projectPropertiesPanel.setCreationDate(properties.getCreationDate());
        projectPropertiesPanel.setLastUpdate(properties.getLastUpdate());
        projectPropertiesPanel.setProjectDescription(properties.getDescription());
        
        // get the project properties
        BuildAndRunOptionsPanel buildAndRunOptionsPanel = this.panelsProvider.getBuildAndRunOptionsPanel();
        buildAndRunOptionsPanel.setCompilerArguments(properties.getBuildOptions());
        buildAndRunOptionsPanel.setBuildSequence(properties.getBuildSequence());
        
        String buildScript = this.loadScript(projectPath, Default.getBuildScriptFileName());
        buildAndRunOptionsPanel.setBuildScript(buildScript);
        
        String cleanAndBuildScript = this.loadScript(projectPath, Default.getCleanScriptFileName());
        buildAndRunOptionsPanel.setCleanAndBuildScript(cleanAndBuildScript);
        
        String runScript = this.loadScript(projectPath, Default.getRunScriptFileName());
        buildAndRunOptionsPanel.setRunProjectScript(runScript);
//        ProjectPropertiesPanel propertiesPanel = (ProjectPropertiesPanel) this.panelProvider.get(PROJECT_PROPERTIES);
//        this.customizerDialog = ProjectCustomizer.createCustomizerDialog(
//            CUSTOMIZER_FOLDER_PATH, // path to layer folder
//            Lookups.fixed(this.opCodeProject), // lookup, which must contain, at least, the project
//            GENERAL_PROPERTIES,   // preselected category
//            this, // ok button listener
//            null  // helpCtx for Help button of dialog
//        );
        
        // show the customizer dialog
        this.customizerDialog.setTitle(ProjectUtils.getInformation(this.opCodeProject).getDisplayName());
        this.customizerDialog.setVisible(true);
        
    }   
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // get the project properties
        ProjectPropertiesPanel projectPropertiesPanel = this.panelsProvider.getProjectPropertiesPanel();
        BuildAndRunOptionsPanel buildAndRunOptionsPanel = this.panelsProvider.getBuildAndRunOptionsPanel();
        
        // update the project properties
        ProjectProperties projectProperties = this.opCodeProject.getProperties();
        projectProperties.setBuildOptions(buildAndRunOptionsPanel.getCompilerArguments());
        projectProperties.setDescription(projectPropertiesPanel.getProjectDescription());
        projectProperties.setBuildSequence(buildAndRunOptionsPanel.getBuildSequence());
        projectProperties.update();
        
        try {
            // set the project files manager
            String path = this.opCodeProject.getProjectDirectory().getPath();
            ProjectFilesManager<ProjectProperties> manager = new ProjectFilesManager<>(path);
            
            // write the project properties
            manager.saveProjectDescriptor(projectProperties);
            
            // update clean/build/run scripts
            this.updateScript(path, Default.getBuildScriptFileName(), buildAndRunOptionsPanel.getBuildScript());
            this.updateScript(path, Default.getCleanScriptFileName(), buildAndRunOptionsPanel.getCleanAndBuildScript());
            this.updateScript(path, Default.getRunScriptFileName(), buildAndRunOptionsPanel.getRunProjectScript());
            
            StatusDisplayer.getDefault().setStatusText(I18N.getString("project.properties.successfully.updated"));
            
        } catch (IOException | JAXBException exception) {
            // in this case, only return null to indicate that the project descriptor is invalid
            String errorMessage = I18N.getString("project.properties.update.error");
            java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
            JOptionPane.showMessageDialog(frame,
                errorMessage, // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
            StatusDisplayer.getDefault().setStatusText(errorMessage);
        }   
        
        this.customizerDialog = null;
        
    }
    
    /**
     * Saves a text file to disk.
     * 
     * @param path
     * @param fileName
     * @param scriptContent the string containing the script
     */
    private void updateScript(String path, String fileName, String scriptContent) {
        
        String scriptFileName = FileUtil.getPath(path, fileName);
        
        try {
            TextFileIOUtil textFileIOUtil = Lookup.getDefault().lookup(TextFileIOUtil.class);
            textFileIOUtil.write(scriptFileName, scriptContent);
        } catch (IOException ioException) {
            java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
            JOptionPane.showMessageDialog(frame,
                I18N.getString("error.writing.file", fileName), // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
        }   
        
    }
    
    /**
     * Loads a text file from disk.
     * 
     * @param path
     * @param fileName
     * @return the string representation of the text file
     */
    private String loadScript(String path, String fileName) {
        
        String scriptFileName = FileUtil.getPath(path, fileName);
        Path pathName = Paths.get(scriptFileName);
        
        // File does not exists yet. Return an empty string to avoid a file reading error
        if(!pathName.toFile().exists()) {
            return Default.EMPTY_STRING;
        }
        
        try {
            TextFileIOUtil textFileIOUtil = Lookup.getDefault().lookup(TextFileIOUtil.class);
            return textFileIOUtil.readAsString(scriptFileName);
        } catch (IOException ioException) {
            
            java.awt.Frame frame = WindowManager.getDefault().getMainWindow();
            JOptionPane.showMessageDialog(frame,
                I18N.getString("error.reading.file", fileName), // dialog message
                I18N.getString("input.output.error"), // dialog title
                JOptionPane.ERROR_MESSAGE);
            
            return Default.EMPTY_STRING;
        }
        
    }
}
