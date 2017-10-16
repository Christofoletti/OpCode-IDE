/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.commons.utils.TextUtil;
import br.com.objectware.domain.msx.sprite.MsxSpritePattern;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteRenderer;
import br.com.objectware.editors.enums.SpriteEditorAction;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static java.awt.Event.CTRL_MASK;
import java.awt.SystemColor;

/**
 * This class defines a visual representation of a SpritePattern in the sprite table.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 13/aug/2015
 * 
 * Useful docs:
 *     https://docs.oracle.com/javase/tutorial/uiswing/misc/action.html
 */
public class SpriteButton extends javax.swing.JButton
                          implements java.awt.event.KeyListener, java.util.Observer {
    
    /** The sprite image scale */
    private static final int SPRITE_SCALE = 2;
    
    /** The sprite button border size in pixels */
    private static final int BORDER_SIZE = 8;
    
    /** The sprite button dimension */
    private static final Dimension BUTTON_SIZE = new Dimension(50, 48);
    
    /** The sprite number font style */
    private static final Font SPRITE_NUMBER_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
    
    /** The sprite data */
    private final Sprite sprite;
    
    /** The sprite renderer class */
    private final SpriteRenderer spriteRenderer;
    
    /**
     * This class represents an sprite button "view".
     * @param sprite the sprite that this button will show in the GUI
     * @param spriteRenderer the sprite renderer
     */
    protected SpriteButton(Sprite sprite, SpriteRenderer spriteRenderer) {
        
        super();
        
        // must not hold null sprites
        assert sprite != null && spriteRenderer != null;
        this.sprite = sprite;
        this.spriteRenderer = spriteRenderer;
        
        this.initialize();
    }   
    
    /**
     * Initializes all resources of this button.
     */
    private void initialize() {
        
        // set the visual properties of the "sprite view button"
        this.setActionCommand(SpriteEditorAction.UPDATE_VIEW.name());
        this.setPreferredSize(SpriteButton.BUTTON_SIZE);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusable(true);
        this.setToolTipText(this.sprite.toString());
        
        // setup the observer reference for this sprite
        this.sprite.addObserver(this);
        this.addKeyListener(this);
    }   
    
    /**
     * Free all resources associated to this button.
     */
    public void freeResources() {
        this.sprite.deleteObserver(this);
        this.removeKeyListener(this);
    }
    
    /**
     * Return the sprite associated to this button.
     * @return 
     */
    public Sprite getSprite() {
        return this.sprite;
    }   
    
    /**
     * Return true if the sprite data is available (duh!)
     * @return 
     */
    public boolean isSpriteAvailable() {
        return (this.sprite != null);
    }
    
    /**
     * This method is responsible for updating the sprite image when a change event is fired.
     * @param observable
     * @param object 
     */
    @Override
    public void update(java.util.Observable observable, Object object) {
        this.updateUI();
        this.setToolTipText(this.sprite.toString());
    }   
    
    /**
     * Send an action event to all Action Listeners listening this button.
     * @param editorAction the editor action event to be sent
     */
    private void sendActionEvent(SpriteEditorAction editorAction) {
        for (ActionListener listener : this.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, editorAction.name())
            );
        }
    }
    
    @Override
    public void keyTyped(KeyEvent keyEvent) {}
    
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        
        int modifiers = keyEvent.getModifiers();
        
        switch(keyEvent.getKeyCode()) {
            
            case KeyEvent.VK_INSERT:
                this.sendActionEvent(SpriteEditorAction.INSERT_SPRITE);
                break;
            
            case KeyEvent.VK_DELETE:
                this.sendActionEvent(SpriteEditorAction.REMOVE_SPRITE);
                break;
            
            case KeyEvent.VK_LEFT:
                if((modifiers & CTRL_MASK) == CTRL_MASK) {
                    this.sendActionEvent(SpriteEditorAction.SHIFT_PATTERN_NUMBER_UP);
                } else {
                    this.sendActionEvent(SpriteEditorAction.SELECT_PREVIOUS_PATTERN);
                }
                break;
            
            case KeyEvent.VK_RIGHT:
                if((modifiers & CTRL_MASK) == CTRL_MASK) {
                    this.sendActionEvent(SpriteEditorAction.SHIFT_PATTERN_NUMBER_DOWN);
                } else {
                    this.sendActionEvent(SpriteEditorAction.SELECT_NEXT_PATTERN);
                }
                break;
            
            case KeyEvent.VK_L:
            case KeyEvent.VK_ENTER:
                this.sendActionEvent(SpriteEditorAction.CHANGE_SPRITE_LABEL);
                break;
            
            case KeyEvent.VK_C:
                this.sendActionEvent(SpriteEditorAction.CHANGE_SPRITE_COLOR);
                break;
        }   
        
        keyEvent.consume();
    }
    
    @Override
    public void keyReleased(KeyEvent keyEvent) {
        keyEvent.consume();
    }
    
    @Override
    public synchronized void paint(java.awt.Graphics graphics) {
        
        super.paint(graphics);
        
        // verify if the sprite data is available for rendering
        if(!this.isSpriteAvailable()) {
            return;
        }
        
        // create a new graphics2d element (avoid changes in the original graphics)
        java.awt.Graphics2D graphics2D = (java.awt.Graphics2D) graphics.create();
        
        // set the background color according to the selected status of contrast view
        if(this.spriteRenderer.isRenderInBlackAndWhite()) {
            graphics2D.setColor(Color.WHITE);
        } else {
            byte colorCode = this.sprite.getBackgroundColorCode();
            Color bgColor = this.spriteRenderer.getPaletteColor(colorCode);
            graphics2D.setColor(bgColor);
        }
        graphics2D.fillRect(BORDER_SIZE/2, // x position
                            BORDER_SIZE/2, // y position
                            this.getWidth()-BORDER_SIZE, // width
                            this.getHeight()-BORDER_SIZE); // height
        
        // set the scaled stroke if the button is the focus owner
        graphics2D.setColor(SystemColor.windowBorder);
        graphics2D.drawRect(1, 1, this.getWidth()-3, this.getHeight()-3);
        
        // render the curent sprite over the background image
        this.spriteRenderer.setSprite(this.sprite);
        java.awt.Image spriteImage = this.spriteRenderer.renderScaled(SpriteButton.SPRITE_SCALE);
        graphics2D.drawImage(spriteImage, BORDER_SIZE, BORDER_SIZE, null);
        
        // get the background color code (stored in the sprite attribute index 0)
        int spriteIndex = this.sprite.getIndex();
        
        // draw the sprite pattern index (for patterns lower than MsxSpritePattern.TABLE_SIZE)
        if(spriteIndex < MsxSpritePattern.TABLE_SIZE) {
            
            String spriteIndexStr = TextUtil.intToTwoDigitString(spriteIndex);
            
            // set the paint properties for sprite number "label"
            graphics2D.setPaintMode();
            graphics2D.setFont(SPRITE_NUMBER_FONT);
            graphics2D.setColor(Color.BLACK);
            graphics2D.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            
            // draw the string and background rectangle according to the number of digits of sprite number
            if(spriteIndex < 100) {
                graphics2D.fillRoundRect(32, 33, 14, 11, 4, 4);
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawString(spriteIndexStr, 32, 43);
            } else {
                graphics2D.fillRoundRect(25, 33, 21, 11, 4, 4);
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawString(spriteIndexStr, 25, 43);
            }
        }
    }
}
