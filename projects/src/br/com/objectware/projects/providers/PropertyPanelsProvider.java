/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.providers;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.projects.panels.BuildAndRunOptionsPanel;
import br.com.objectware.projects.panels.ProjectPropertiesPanel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;

/**
 * The OpCode property panels provider.
 * 
 * @author Luciano M. Christofoletti
 * @since 14/Jun/2015
 * 
 * Useful docs:
 *  http://bits.netbeans.org/dev/javadoc/org-netbeans-modules-projectuiapi/org/netbeans/spi/project/ui/support/ProjectCustomizer.html
 */
public class PropertyPanelsProvider implements ProjectCustomizer.CategoryComponentProvider {
    
    /*  */
    public static final String PROJECT_PROPERTIES = "project.properties";
    public static final String BUILD_AND_RUN_OPTIONS = "build.and.run.options";
    
    /* The visual panels */
    private final ProjectPropertiesPanel projectPropertiesPanel;
    private final BuildAndRunOptionsPanel buildAndRunOptionsPanel;
    
    /* If things goes wrong... */
    private static final JLabel CATEGORY_UNAVAILABLE = new JLabel(I18N.getString("project.properties.not.found"));
    
    /* The registered categories */
    private final ProjectCustomizer.Category[] categories;
    
    /** The registered categories and its respective panels */
    private final Map<ProjectCustomizer.Category, JComponent> panels = new HashMap<>();
    
    /**
     * Register all categories and panels
     */
    public PropertyPanelsProvider() {
        
        ProjectCustomizer.Category projectProperties = ProjectCustomizer.Category.create(
                PROJECT_PROPERTIES,
                I18N.getString("project.properties"),
                null,
                (ProjectCustomizer.Category[]) null // sub categories
        );
        
        ProjectCustomizer.Category buildAndRunOptions = ProjectCustomizer.Category.create(
                BUILD_AND_RUN_OPTIONS,
                I18N.getString("build.and.run.options"),
                null,
                (ProjectCustomizer.Category[]) null // sub categories
        );
        
        this.categories = new ProjectCustomizer.Category[]{
            projectProperties,
            buildAndRunOptions
        };
        
        // setup the properties (visual) panels
        this.projectPropertiesPanel = new ProjectPropertiesPanel();
        this.buildAndRunOptionsPanel = new BuildAndRunOptionsPanel();
        
        this.panels.put(projectProperties, this.projectPropertiesPanel);
        this.panels.put(buildAndRunOptions, this.buildAndRunOptionsPanel);
    }
    
    public final ProjectPropertiesPanel getProjectPropertiesPanel() {
        return this.projectPropertiesPanel;
    }
    
    public final BuildAndRunOptionsPanel getBuildAndRunOptionsPanel() {
        return this.buildAndRunOptionsPanel;
    }
    
    /**
     * Return the array list of all categories.
     * @return
     */
    public ProjectCustomizer.Category[] getCategories() {
        return this.categories;
    }
    
    @Override
    public JComponent create(ProjectCustomizer.Category category) {
        JComponent panel = this.panels.get(category);
        return (panel == null ? CATEGORY_UNAVAILABLE : panel);
    }
    
}
