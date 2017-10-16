/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.asm;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 * 
 * @author Luciano M. Christofoletti
 * @since 25/Apr/2015
 * 
 * Useful docs:
 * http://www.sitepoint.com/web-foundations/mime-types-complete-list/
 * 
 * .asm     text/x-asm
 * .bin     application/x-binary
 * .com     text/plain
 * .def     text/plain
 * .sprite  application/x-sprite
 * .src     application/x-wais-source
 * .lst     text/plain
 * 
 */
@MIMEResolver.ExtensionRegistration(
        displayName = "#source.file.label",
        mimeType = "text/x-asm",
        extension = {"asm", "ASM", "mac", "MAC", "s", "S", "src", "SRC"},
        position = 100,
        showInFileChooser = {"OpCode"}
)
@DataObject.Registration(
        mimeType = "text/x-asm",
        iconBase = "br/com/objectware/editors/asm/source-file.png",
        displayName = "#source.file.label",
        position = 200
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/text/x-asm/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class AssemblyDataObject extends MultiDataObject {
    
    public AssemblyDataObject(FileObject pf, MultiFileLoader loader)
            throws DataObjectExistsException, java.io.IOException {
        super(pf, loader);
        this.registerEditor("text/x-asm", false);
    }
    
    @Override
    protected int associateLookup() {
        return 1;
    }
    
}
