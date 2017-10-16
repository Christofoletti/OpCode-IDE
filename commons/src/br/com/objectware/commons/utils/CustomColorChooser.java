/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.commons.utils;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 *
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 */
public class CustomColorChooser extends AbstractColorChooserPanel {

    @Override
    protected void buildChooser() {
        setLayout(new GridLayout(0, 3));
        makeAddButton("Red", Color.red);
        makeAddButton("Green", Color.green);
        makeAddButton("Blue", Color.blue);
    }

    @Override
    public void updateChooser() {
    }

    @Override
    public String getDisplayName() {
        return "MyChooserPanel";
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }

    Action setColorAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton) evt.getSource();
            getColorSelectionModel().setSelectedColor(button.getBackground());
        }
    };

    private void makeAddButton(String name, Color color) {
        JButton button = new JButton(name);
        button.setBackground(color);
        button.setAction(setColorAction);
        add(button);
    }
}
