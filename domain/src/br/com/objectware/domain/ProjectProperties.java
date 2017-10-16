/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.domain;

import br.com.objectware.commons.utils.Default;
import br.com.objectware.domain.adapters.BuildSequenceAdapter;
import br.com.objectware.domain.adapters.TargetPlatformAdapter;
import br.com.objectware.domain.adapters.TimestampAdapter;
import br.com.objectware.domain.enums.BuildSequence;
import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Project properties.
 * This class contains all information about the project.
 * 
 * @author Luciano M. Christofoletti
 * @since 28/mar/2015
 */
@XmlRootElement(name = "opcode-project")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {
    "copyright", "targetPlatform", "buildOptions", "buildSequence",
    "description", "creationDate", "lastUpdate"
})
public final class ProjectProperties {
    
    /** Long text description of the project */
    @XmlElement(name = "copyright", required = true)
    private String copyright;
    
    /** The project name (unique project identifyer) */
    @XmlAttribute(name = "name", required = true)
    private String name;
    
    /** The project Id (unique project identifyer) */
    @XmlAttribute(name = "id", required = true)
    private final String id;
    
    /** The project descriptor format version */
    @XmlAttribute(name = "version", required = true)
    private final String version = "0.1";
    
    /** Defines the target platform (MSX, Atari 2600, Intellivision, etc)*/
    @XmlElement(name = "target-platform", required = true)
    @XmlJavaTypeAdapter(TargetPlatformAdapter.class)
    private TargetPlatform targetPlatform;
    
    /** The project (internal) build options */
    @XmlElement(name = "build-options", required = true)
    private String buildOptions;
    
    /** The project build sequence */
    @XmlElement(name = "build-sequence", required = true)
    @XmlJavaTypeAdapter(BuildSequenceAdapter.class)
    private BuildSequence buildSequence = BuildSequence.ONLY_EMBEDDED;
    
    /** Long text description of the project */
    @XmlElement(name = "description", required = false)
    private String description;
    
    /** Defines the projects creation date */
    @XmlElement(name = "creation-date", required = true)
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    private Timestamp creationDate;
    
    /** When the project was last updated (saved) */
    @XmlElement(name = "last-update", required = true)
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    private Timestamp lastUpdate;
    
    /**
     * Default constructor: this is necessary to be used in the xml bind process.
     */
    private ProjectProperties() {
        this.id = Long.toHexString(System.currentTimeMillis()).toUpperCase();
        this.copyright = Default.COPYRIGHT;
    }
    
    /**
     * Constructor of the OpCodeProject that initializes the project name.
     * @param name the project name
     */
    public ProjectProperties(String name) {
        this();
        this.name = name;
    }
    
    public final String getCopyright() {
        return this.copyright;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final void setName(String name) {
        this.name = name;
    }
    
    public final TargetPlatform getTargetPlatform() {
        return this.targetPlatform;
    }
    
    public final void setTargetPlatform(TargetPlatform targetPlatform) {
        this.targetPlatform = targetPlatform;
    }
    
    public final String getBuildOptions() {
        return this.buildOptions;
    }
    
    public final void setBuildOptions(String buildOptions) {
        this.buildOptions = buildOptions;
    }
    
    public final BuildSequence getBuildSequence() {
        return this.buildSequence;
    }
    
    public final void setBuildSequence(BuildSequence buildSequence) {
        this.buildSequence = buildSequence;
    }
    
    public final String getDescription() {
        return this.description;
    }
    
    public final void setDescription(String description) {
        this.description = description;
    }
    
    public final Timestamp getCreationDate() {
        return this.creationDate;
    }
    
    public final void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    
    public final Timestamp getLastUpdate() {
        return this.lastUpdate;
    }
    
    public final void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
        this.copyright = Default.COPYRIGHT; // trick copyright update!
    }
    
    public void update() {
        this.setLastUpdate(new Timestamp(System.currentTimeMillis()));
    }
    
    public boolean validateCopyright() {
        return Default.COPYRIGHT.equals(this.copyright);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
