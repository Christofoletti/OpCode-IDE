/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.msx.sprite.editor;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.domain.enums.MsxColor;
import br.com.objectware.domain.enums.MsxScreen;
import br.com.objectware.domain.factories.SpriteRendererFactory;
import br.com.objectware.domain.msx.sprite.MsxSpriteAttributes;
import br.com.objectware.domain.msx.sprite.MsxSpritePattern;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.editors.sprite.SpritesDataObject;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.domain.sprite.SpritePattern;
import br.com.objectware.domain.sprite.SpriteRenderer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;

/**
 * The MSX draft view panel (show the sprites as it would be shown in a real MSX computer)
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 28/aug/2015
 */
public class MsxSpriteViewPanel extends JPanel 
                                implements KeyListener, ListSelectionListener, MouseInputListener, java.util.Observer {

    private enum MODE {PRESSED, RELEASED, MOVING, DRAGGING, SELECTING};
    
    // the empty cursor used when dragging a sprite
    private final java.awt.Cursor BLANK_CURSOR = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(
        java.awt.Toolkit.getDefaultToolkit().getImage(""), new Point(), "blank cursor");
    
    /** The draft background image */
    private java.awt.Image backgroundImage;
    
    /** Temporary point used to hold the mouse pointer position when the button was pressed */
    private Point startPoint = new Point();
    
    /** Temporary point used to hold the mouse pointer position */
    private Point currentPoint = new Point();
    
    /** The sprite data object */
    private SpritesDataObject spritesDataObject;
    
    /** The sprite renderer. Note that the palette is the same used in the Pattern Editor */
    private SpriteRenderer spriteRenderer;
    
    /** The current selection mode */
    private MODE currentMode = MODE.RELEASED;
    
    /** The list of sprite attributes */
    private final java.util.List<MsxSpriteAttributes> spriteAtributes = new ArrayList<>();
    
    /** The list of sprite attributes in reverse order */
    private final java.util.List<MsxSpriteAttributes> spriteAtributesReverse = new ArrayList<>();
    
    /** The visual component showing the sprite attributes table */
    private javax.swing.JTable spriteAttributesTable;
    
    /** The screen horizontal size */
    private static final int SCREEN_WIDTH = MsxScreen.SCREEN2.getWidth();
    
    /** The screen vertical size */
    private static final int SCREEN_HEIGHT = MsxScreen.SCREEN2.getHeight();
    
    /** The screen border size */
    private static final int BORDER_SIZE = MsxScreen.BORDER_SIZE;
    
    /** The map of selected sprite attributes and it's initial positions */
    private final java.util.Map<MsxSpriteAttributes, Point> selectionMap = new java.util.HashMap<>();
    
    /** The list of "hovered" sprites */
    private final java.util.List<MsxSpriteAttributes> hoveredSprites  = new ArrayList<>();
    
     /** The current sprite scale render parameter */
    private int spriteScale = 2;
    
    /** The sprite magnifier flag */
    private boolean spritesMagnified = false;
    
    /** The sprite left corner cross reference view flag */
    private boolean drawSpriteReference = false;
    
    /** The screen color */
    private Color screenColor = MsxColor.DARK_BLUE.getColor();
    
    /** The screen border color */
    private Color screenBorderColor = MsxColor.BLACK.getColor();
    
    /** The sprite reference image */
    private java.awt.Image spriteReference = I18N.getImage("sprite.reference");
    
    /**
     * The Draft panel constructor. Stores the data object reference and get
     * a new sprite renderer.
     */
    public MsxSpriteViewPanel() {
        this.setupListeners();
        this.setScale(2);
        this.setFocusable(true);
    }   
    
    /**
     * Set the sprites data object
     * @param dataObject the sprite data object (the sprites set)
     */
    void setDataObject(SpritesDataObject dataObject) {
        
        // get the sprite data object information
        this.spritesDataObject = dataObject;
        dataObject.addObserver(this);
        
        // setup the sprite renderer
        this.spriteRenderer = SpriteRendererFactory.newSpriteRenderer(dataObject.getSpriteFormat());
        this.spriteRenderer.setSuppressZeroColorCode(true);
        
        this.setAttributes(dataObject.getAttributes());
    }
    
    /**
     * Set the listeners for this view panel
     */
    private void setupListeners() {
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }   
    
    /**
     * Set the palette colors for the sprite renderer.
     * @param palette 
     */
    void setPalette(java.util.List<java.awt.Color> palette) {
        if(this.isDataAvailable()) {
            this.spriteRenderer.setPalette(palette);
        }
    }   
    
    /**
     * Fill the sprite attributes lists. This is done in this way to avoid
     * converting SpriteAttributes to MsxSpriteAttributes every time a
     * specific MsxSpriteAttributes method is accessed.
     * 
     * @param attributesList 
     */
    void setAttributes(List<SpriteAttributes> attributesList) {
        
        this.spriteAtributes.clear();
        this.spriteAtributesReverse.clear();
        
        for(SpriteAttributes attributes:attributesList) {
            attributes.addObserver(this);
            this.spriteAtributes.add((MsxSpriteAttributes) attributes);
            this.spriteAtributesReverse.add(0, (MsxSpriteAttributes) attributes);
        }   
    }
    
    /**
     * Set the background image of the MSX draft panel
     * @param background 
     */
    public final void setBackgroundImage(java.awt.Image background) {
        
        // the background image
        this.backgroundImage = background;
        
        // get the dimension of the image and set the panel size equals to the image size
        //this.setPanelSize(background.getWidth(this), background.getHeight(this));
    }   
    
    /**
     * Set the sprite attributes table visual component.
     * @param spriteAttributesTable 
     */
    public void setSpriteAttributesTable(javax.swing.JTable spriteAttributesTable) {
        this.spriteAttributesTable = spriteAttributesTable;
        spriteAttributesTable.getSelectionModel().addListSelectionListener(this);
    }   
    
    private void setSelectedSpriteIndex(int index) {
        if(this.spriteAttributesTable != null && index >= 0) {
            this.spriteAttributesTable.setRowSelectionInterval(index, index);
        }
    }
    
    /**
     * Return the current sprite scale.
     * @return 
     */
    private int getSpriteScale() {
        return this.spriteScale;
    }
    
    /**
     * Return the sprite scale corrected by the sprite magnification flag
     * @return 
     */
    private int getSpriteScaleWithMagnification() {
        return (this.spriteScale * (this.spritesMagnified ? 2:1));
    }
    
    /**
     * Setup the sprite (view) scale. This scale has nothing to do with the sprites attributes.
     * @param scale the sprite rendering scale (1x-8x)
     */
    public final void setScale(int scale) {
        this.spriteScale = Math.max(1, Math.min(8, scale));
        this.setPanelSize(this.spriteScale * SCREEN_WIDTH + (2 * BORDER_SIZE),
                          this.spriteScale * SCREEN_HEIGHT + (2 * BORDER_SIZE));
        this.spriteReference = I18N.getScaledImage("sprite.reference", this.spriteScale);
    }   
    
    /**
     * Flag used to enable/disable the sprite reference drawing
     * @param drawSpriteReference 
     */
    public final void setDrawSpriteReference(boolean drawSpriteReference) {
        this.drawSpriteReference = drawSpriteReference;
        this.repaint();
    }
    
    /**
     * Enable/disable the sprite magnification flag
     * @param spritesMagnified 
     */
    public final void setDrawSpritesMagnified(boolean spritesMagnified) {
        this.spritesMagnified = spritesMagnified;
        this.repaint();
    }
    
    /**
     * The background screen color
     * @param color the new screen color 
     */
    public final void setScreenColor(Color color) {
        this.screenColor = color;
        this.repaint();
    }   
    
    /**
     * The screen border color
     * @param color the new screen border color 
     */
    public final void setScreenBorderColor(Color color) {
        this.screenBorderColor = color;
        this.repaint();
    }   
    
    @Override
    public synchronized void paint(java.awt.Graphics graphics) {
        
        super.paint(graphics);
        
        // get the graphics2d and store the original stroke pattern
        java.awt.Graphics2D graphics2D = (java.awt.Graphics2D) graphics.create();
        
        // the screen width and height
        int screenWidth = SCREEN_WIDTH * this.getSpriteScale();
        int screenHeight = SCREEN_HEIGHT * this.getSpriteScale();
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();
        
        // render the background
        graphics2D.translate(BORDER_SIZE, BORDER_SIZE);
        graphics2D.setColor(this.screenColor);
        graphics2D.fillRect(0, 0, screenWidth, screenHeight);
        graphics2D.drawImage(this.backgroundImage, 0, 0, screenWidth, screenHeight, this);
        
        // render all sprites over the background (note that the coordinate system is translated)
        this.drawSprites(graphics2D);
        
        // restore the graphics origin and set the screen border color
        graphics2D.translate(-BORDER_SIZE, -BORDER_SIZE); // back to original position (0,0)
        graphics2D.setColor(this.screenBorderColor);
        
        // draw the screen borders
        graphics2D.fillRect(0, 0, panelWidth, BORDER_SIZE); // top
        graphics2D.fillRect(0, BORDER_SIZE, BORDER_SIZE, screenHeight); // left
        graphics2D.fillRect(screenWidth + BORDER_SIZE, BORDER_SIZE, panelWidth - screenWidth, screenHeight); // right
        graphics2D.fillRect(0, screenHeight + BORDER_SIZE, panelWidth, panelHeight - screenHeight); // bottom
        
        // if there is no current selected sprites, draw the selection reference
        if(this.currentMode.equals(MODE.SELECTING)) {
            this.drawSelectionArea(graphics2D);
        }   
        
        graphics2D.dispose();
    }
    
    /**
     * Draw the sprite patterns over the background panel
     * @param graphics2D 
     */
    private void drawSprites(java.awt.Graphics2D graphics2D) {
        
        // the sprites data object must be available
        if(!this.isDataAvailable()) {
            return;
        }   
        
        // get the graphics2d and set the scaled stroke pattern
        java.awt.Stroke originalStroke = graphics2D.getStroke();
        java.awt.Stroke scaledStroke = new java.awt.BasicStroke(this.spriteScale);
        java.awt.Stroke dashedStroke = new java.awt.BasicStroke(
            this.spriteScale, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0,
            new float[]{2*this.spriteScale}, 0);
        
        // the sprite scale. Note the magnifier factor "correction".
        int scale = this.getSpriteScaleWithMagnification();
        //int offset = Math.max(1, this.spriteScale/2);
        int offset = Math.max(1, this.spriteScale/2);
        int spriteSize = MsxSpritePattern.SPRITE_WIDTH * scale;
        java.util.List<Sprite> sprites = this.spritesDataObject.getSprites();
        
        // renders all sprites
        for(MsxSpriteAttributes attributes : this.spriteAtributesReverse) {
            
            // get the current sprite location and the sprite image
            int x = attributes.getTranslatedXEC() * this.spriteScale;
            int y = attributes.getTranslatedY() * this.spriteScale;
            byte patternNumber = attributes.getPatternNumber();
            
            graphics2D.setPaintMode();
            
            try {
                // try to get the pattern at position patternNumber
                SpritePattern pattern = sprites.get(patternNumber).getPattern();
                
                // render the sprite image using the current palette (from sprites data object)
                this.spriteRenderer.setAttributes(attributes);
                this.spriteRenderer.setPattern(pattern);
                java.awt.Image spriteImage = this.spriteRenderer.renderScaled(scale);
                
                // draw the sprite pattern using the attributes from attributes list
                graphics2D.drawImage(spriteImage, x, y, null);
                
            } catch(IndexOutOfBoundsException exception) {
                // this exception may occur if the selected pattern number is not available
                // in that case, just continue rendering the other sprites
            }
            
            // draw the hovered decoration
            if(this.hoveredSprites.contains(attributes) && this.currentMode.equals(MODE.RELEASED)) {
                
                int selectionSize = spriteSize + 2*offset;
                if(this.spriteScale == 1) {
                    selectionSize--;
                }
                
                graphics2D.setColor(Color.BLACK);
                graphics2D.setStroke(scaledStroke);
                graphics2D.drawRect(x-offset, y-offset, selectionSize, selectionSize);
                
                graphics2D.setColor(Color.WHITE);
                graphics2D.setStroke(dashedStroke);
                graphics2D.drawRect(x-offset, y-offset, selectionSize, selectionSize);
            }   
            
            // draw the sprite cross reference, if enabled
            if(this.drawSpriteReference) {
                
                // get the current attribute x coordinate (it is different from translated)
                int ox = attributes.getTranslatedX() * this.spriteScale - 2*offset;
                int oy = attributes.getTranslatedY() * this.spriteScale - 2*offset;
                if(this.spriteScale == 1) {
                    graphics2D.drawImage(this.spriteReference, ox+1, oy+1, null);
                } else {
                    graphics2D.drawImage(this.spriteReference, ox, oy, null);
                }
            }
        }
        
        // restore stroke and paint mode
        graphics2D.setPaintMode();
        graphics2D.setStroke(originalStroke);
    }
    
    /**
     * Draw the selection area (the rectangular area)
     * @param graphics2D 
     */
    private void drawSelectionArea(java.awt.Graphics2D graphics2D) {
        
        // the selection rectangle position and dimension
        int x0 = Math.min(this.currentPoint.x, this.startPoint.x);
        int y0 = Math.min(this.currentPoint.y, this.startPoint.y);
        int width = Math.abs(this.currentPoint.x - this.startPoint.x);
        int height = Math.abs(this.currentPoint.y - this.startPoint.y);
        
        // draw the alpha composite filled rectangle area
        graphics2D.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(x0, y0, width, height);
        
        // draw the selection border
        graphics2D.setComposite(AlphaComposite.SrcOver);
        graphics2D.setColor(Color.BLUE);
        graphics2D.drawRect(x0, y0, width, height);
    }
    
    @Override
    public void mousePressed(MouseEvent event) {
        
        // the current mouse pointer location
        this.startPoint = event.getPoint();
        this.currentPoint = event.getPoint();
        
        // if the selection map is not empty, enter the drag mode
        if(!this.selectionMap.isEmpty()) {
            this.currentMode = MODE.DRAGGING;
            return;
        }   
        
        // the sprite scale. Note the magnifier flag "correction".
        int scale = this.getSpriteScaleWithMagnification();
        
        // the sprite selection range (considering the magnification flag)
        int selectionRadius = (MsxSpritePattern.SPRITE_WIDTH * scale); // /2
        
        // evaluate the min distance of sprite selection action
        int modifiers = event.getModifiers();
        int selectedSpriteIndex = -1;
        double minDistance = selectionRadius;
        
        // select sprites near the current pointer location
        for(MsxSpriteAttributes attributes : this.spriteAtributesReverse) {
            
            // the x,y coords of the sprite attributes table
            int x = (attributes.getTranslatedXEC() * this.spriteScale) + BORDER_SIZE;
            int y = (attributes.getTranslatedY() * this.spriteScale) + BORDER_SIZE;
            
            // distance from current point location
            Point location = new Point(x, y);
            double distance = location.distance(this.startPoint);
            
            // hold the sprite with the nearest center from current location
            int dx = this.startPoint.x - x;
            int dy = this.startPoint.y - y;
            
            // select only the sprites with a minimum distance
            if(dx > 0 && dx < minDistance && dy > 0 && dy < minDistance) {    
                
                if((modifiers & java.awt.event.InputEvent.CTRL_MASK) == 0) {
                    this.selectionMap.clear();
                }
                
                // update the minimum distance found
                minDistance = distance;
                
                // put the current sprite in the selection map
                this.selectionMap.put(attributes, new Point(attributes.getX(), attributes.getY()));
                selectedSpriteIndex = this.spriteAtributes.indexOf(attributes);
            }   
        }   
        
        // if the selection map is empty, enter the selection mode
        if(this.selectionMap.isEmpty()) {
            this.currentMode = MODE.SELECTING;
        } else {
            this.currentMode = MODE.PRESSED;
            this.setSelectedSpriteIndex(selectedSpriteIndex);
        }   
    }   
    
    @Override
    public void mouseReleased(MouseEvent event) {
        
        // if the current mode is selecting
        if(this.currentMode.equals(MODE.SELECTING)) {
            
            // the selection are position and dimension
            java.awt.Rectangle selection = new java.awt.Rectangle(
                Math.min(this.currentPoint.x, this.startPoint.x),  // x0
                Math.min(this.currentPoint.y, this.startPoint.y),  // y0
                Math.abs(this.currentPoint.x - this.startPoint.x), // width
                Math.abs(this.currentPoint.y - this.startPoint.y)  // height
            );  
            
            // translate the origin to compensate the border size
            selection.translate(-BORDER_SIZE, -BORDER_SIZE);
            this.selectionMap.clear();
            
            // the sprite scale. Note the magnifier flag "correction".
            int scale = this.getSpriteScaleWithMagnification();
            
            // select all sprites that has the reference point inside the selection area
            for(MsxSpriteAttributes attributes : this.spriteAtributes) {
                
                // the x,y coords of the sprite attributes table
                int x = (attributes.getTranslatedX() * scale);
                int y = (attributes.getTranslatedY() * scale);
                
                // select only the sprites with a minimum distance
                if(selection.contains(x, y)) {
                    this.selectionMap.put(attributes, new Point(attributes.getX(), attributes.getY()));
                }   
            }
            
        } else {
            this.selectionMap.clear();
        }   
        
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.currentMode = MODE.RELEASED;
        
        this.repaint();
        
    }   
    
    @Override
    public void mouseDragged(MouseEvent event) {
        
        // evaluate dx and dy only when the attributes selection is not empty
        if(!this.currentMode.equals(MODE.SELECTING)) {
            
            // hide the mouse cursor
            this.setCursor(BLANK_CURSOR);
            this.currentMode = MODE.DRAGGING;
            int scale = this.getSpriteScale();
            
            Point newpoint = event.getPoint();
            int dx = (newpoint.x - this.startPoint.x) / scale;
            int dy = (newpoint.y - this.startPoint.y) / scale;
            
            MsxSpriteAttributes attribute = null;
            for (Entry<MsxSpriteAttributes, Point> entry: this.selectionMap.entrySet()) {
                
                // get the sprite attribute and initial location
                attribute = entry.getKey();
                Point location = entry.getValue();
                
                // update the sprite attribute location
                attribute.setX((byte) (location.getX() + dx));
                attribute.setY((byte) (location.getY() + dy));
            }   
            
            // notify the sprite attributes table (and others, if necessary)
            if(attribute != null) {
                attribute.notifyObservers();
            }   
            
        } else {
            
            // change the cursor type when the dragging movement starts
            if(this.currentPoint.equals(this.startPoint)) {
                this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            }   
            
            this.updateHoveredSritesList(event);
            this.repaint();
        }   
        
        // update the current point location
        this.currentPoint = event.getPoint();
    }
    
    @Override
    public void mouseMoved(MouseEvent event) {
        
        // evaluate dx and dy only when the attributes selection is not empty
        if(this.currentMode.equals(MODE.RELEASED) && this.selectionMap.isEmpty()) {
            
            // the sprite selection range (considering the magnification flag)
            int range = MsxSpritePattern.SPRITE_WIDTH * this.getSpriteScaleWithMagnification();
            Point location = event.getPoint();
            
            // update the current point location
            java.awt.Rectangle selectionArea = new java.awt.Rectangle(
                location.x - BORDER_SIZE - range, // start x (screen border size minus the sprite width)
                location.y - BORDER_SIZE - range, // start y - the same as above
                range, // width
                range  // height
            );
            
            // get the sprites inside the selection area
            this.hoveredSprites.clear();
            this.hoveredSprites.addAll(this.getSpritesInside(selectionArea));
        }   
        
        // set the cursor to the hand shape if there is at least one sprite in the selection range
        if(this.hoveredSprites.isEmpty()) {
            this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        } else {
            this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }   
        this.repaint();
    }
    
    /**
     * 
     * @param event 
     */
    private void updateHoveredSritesList(MouseEvent event) {
        
        Point location = event.getPoint();
        
        // the sprite selection range (considering the magnification flag)
        int x = Math.min(location.x, this.startPoint.x) - BORDER_SIZE;
        int y = Math.min(location.y, this.startPoint.y) - BORDER_SIZE;
        int width = Math.abs(location.x - this.startPoint.x); // width
        int height = Math.abs(location.y - this.startPoint.y);  // height
        
        // evaluate the selection area
        java.awt.Rectangle selectionArea = new java.awt.Rectangle(x, y, width, height);
        
        // get the sprites inside the selection area
        this.hoveredSprites.clear();
        this.hoveredSprites.addAll(this.getSpritesInside(selectionArea));
    }
    
    /**
     * Get all sprites inside the location passed and given range.
     * 
     * @param Rectangle selectionArea the rectangular selection area
     * @return 
     */
    private java.util.List<MsxSpriteAttributes> getSpritesInside(java.awt.Rectangle selectionArea) {
        
        // the sprite selection list
        java.util.List<MsxSpriteAttributes> selection = new ArrayList<>();
        
        // select sprites near the current pointer location
        for (MsxSpriteAttributes attributes : this.spriteAtributes) {
            
            // the x,y coords of the sprite attribute
            int x = attributes.getTranslatedXEC() * this.spriteScale;
            int y = attributes.getTranslatedY() * this.spriteScale;
            
            // select only the sprites with a minimum distance
            if(selectionArea.contains(x, y)) {
                selection.add(attributes);
            }   
        }   
        
        return selection;
    }
    
    // MouseInputListener implementation not used (empty)
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent event) {
        
        switch(event.getKeyCode()) {
            
            case KeyEvent.VK_DELETE:
                for (MsxSpriteAttributes attribute: this.selectionMap.keySet()) {
                    attribute.reset();
                }   
                break;
                
            default:
                break;
        }
        
    }
    
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    
    @Override
    public void valueChanged(ListSelectionEvent event) {
        
        //int index = event.getLastIndex();
        int index = this.spriteAttributesTable.getSelectedRow();
        if(index >= 0) {
            this.hoveredSprites.clear();
            this.hoveredSprites.add(this.spriteAtributes.get(index));
        }
        
        this.repaint();
    }
    
    /**
     * Set the panel size according to the screen 2 width and height
     * @param width
     * @param height 
     */
    private void setPanelSize(int width, int height) {
        
        int newWidth = Math.max(MsxScreen.SCREEN2.getWidth(), width);
        int newHeight = Math.max(MsxScreen.SCREEN2.getHeight(), height);
        
        this.setPreferredSize(new Dimension(newWidth, newHeight));
        
        this.updateUI();
    }   
    
    private boolean isDataAvailable() {
        return (this.spritesDataObject != null);
    }
    
    @Override
    public void update(java.util.Observable observable, Object argument) {
        
        // notify the sprites data object if some change occured in sprite attributes
        if(observable instanceof SpriteAttributes) {
            this.spritesDataObject.setChanged(true);
        }   
        
        this.repaint();
    }   
}
