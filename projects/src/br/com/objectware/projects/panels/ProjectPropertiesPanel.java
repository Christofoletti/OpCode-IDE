/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.panels;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.TargetPlatform;
import java.sql.Timestamp;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Main project properties panel.
 * 
 * @author Luciano M. Christofoletti
 * @since 01/Jun/2015
 */
public class ProjectPropertiesPanel extends javax.swing.JPanel {
    
    /**
     * Create the project properties panel
     */
    public ProjectPropertiesPanel() {
        
        this.initComponents();
        
        // setup i18n
        this.projectFolderLabel.setText(I18N.getString("project.folder"));
        this.targetPlatformLabel.setText(I18N.getString("target.platform"));
        this.creationDateLabel.setText(I18N.getString("creation.date"));
        this.lastUpdateLabel.setText(I18N.getString("last.update"));
        this.projectDescriptionLabel.setText(I18N.getString("project.description"));
        
    }   
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectFolderLabel = new javax.swing.JLabel();
        projectDirectoryTextField = new javax.swing.JTextField();
        lastUpdateLabel = new javax.swing.JLabel();
        creationDateLabel = new javax.swing.JLabel();
        targetPlatformLabel = new javax.swing.JLabel();
        platformLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectDescriptionTextArea = new javax.swing.JTextArea();
        projectDescriptionLabel = new javax.swing.JLabel();
        creationLabel = new javax.swing.JLabel();
        updateLabel = new javax.swing.JLabel();

        projectFolderLabel.setText("Project Folder:");

        projectDirectoryTextField.setEditable(false);
        projectDirectoryTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectDirectoryTextFieldActionPerformed(evt);
            }
        });

        lastUpdateLabel.setText("Last update:");

        creationDateLabel.setText("Creation date:");

        targetPlatformLabel.setText("Target Platform:");

        platformLabel.setText("PLATFORM");

        projectDescriptionTextArea.setColumns(20);
        projectDescriptionTextArea.setRows(5);
        jScrollPane1.setViewportView(projectDescriptionTextArea);

        projectDescriptionLabel.setText("Project Description:");

        creationLabel.setText("16/06/1983");

        updateLabel.setText("04/12/2009");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastUpdateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(creationDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(projectFolderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectDirectoryTextField))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectDescriptionLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(targetPlatformLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(creationLabel)
                            .addComponent(platformLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateLabel))))
                .addGap(0, 217, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectFolderLabel)
                    .addComponent(projectDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetPlatformLabel)
                    .addComponent(platformLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(creationLabel)
                    .addComponent(creationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastUpdateLabel)
                    .addComponent(updateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectDescriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void projectDirectoryTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectDirectoryTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_projectDirectoryTextFieldActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel creationDateLabel;
    private javax.swing.JLabel creationLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lastUpdateLabel;
    private javax.swing.JLabel platformLabel;
    private javax.swing.JLabel projectDescriptionLabel;
    private javax.swing.JTextArea projectDescriptionTextArea;
    private javax.swing.JTextField projectDirectoryTextField;
    private javax.swing.JLabel projectFolderLabel;
    private javax.swing.JLabel targetPlatformLabel;
    private javax.swing.JLabel updateLabel;
    // End of variables declaration//GEN-END:variables
    
    public void setProjectDirectory(FileObject directory) {
        String folder = FileUtil.normalizePath(directory.getPath());
        this.projectDirectoryTextField.setText(folder);
    }
    
    public void setTargetPlatform(TargetPlatform target) {
        this.platformLabel.setText(target.toString());
    }
    
    public void setCreationDate(Timestamp date) {
        String dateFormatted = I18N.formatDate(date);
        this.creationLabel.setText(dateFormatted);
    }
    
    public void setLastUpdate(Timestamp date) {
        String dateFormatted = I18N.formatDateTime(date);
        this.updateLabel.setText(dateFormatted);
    }
    
    public void setProjectDescription(String description) {
        this.projectDescriptionTextArea.setText(description);
    }
    
    public String getProjectDescription() {
        return this.projectDescriptionTextArea.getText();
    }
    
    public void clearAll() {
        // fill the dialog project properties fields
        this.projectDirectoryTextField.setText("");
        this.platformLabel.setText("");
        this.creationLabel.setText("");
        this.updateLabel.setText("");
        this.projectDescriptionTextArea.setText("");
    }
}
