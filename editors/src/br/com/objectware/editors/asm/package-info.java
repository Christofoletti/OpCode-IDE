/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
@TemplateRegistrations({
    @TemplateRegistration(
        folder = "OpCode",
        targetName = "source",
        content = "source-template.asm",
        displayName = "#source.file.label.asm"
    ),
    @TemplateRegistration(
        folder = "OpCode",
        targetName = "source",
        content = "source-template.mac",
        displayName = "#source.file.label.mac"
    ),
    @TemplateRegistration(
        folder = "OpCode",
        targetName = "source",
        content = "source-template.s",
        displayName = "#source.file.label.s"
    )
})
package br.com.objectware.editors.asm;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
