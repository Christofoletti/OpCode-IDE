/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.sprite;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.ListUtil;
import br.com.objectware.commons.utils.TextUtil;
import br.com.objectware.domain.enums.SpriteFormat;
import br.com.objectware.domain.factories.AttributeFactory;
import br.com.objectware.domain.factories.SpriteFactory;
import br.com.objectware.domain.msx.sprite.MsxSpriteAttributes;
import br.com.objectware.domain.msx.sprite.MsxSpritePattern;
import br.com.objectware.domain.sprite.Sprite;
import br.com.objectware.domain.sprite.SpriteAttributes;
import br.com.objectware.domain.sprite.SpritePattern;
import br.com.objectware.editors.CustomDataObject;
import br.com.objectware.editors.cookies.OpenSpriteEditorCookie;
import br.com.objectware.editors.cookies.SaveDataObjectCookie;
import br.com.objectware.editors.enums.SpriteDataIdentifier;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.MissingFormatArgumentException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;

/**
 * This class holds all important information about the sprite set.
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
        displayName = "#sprite.file.label",
        mimeType = "application/x-sprite",
        extension = {"sprite", "SPRITE", "spr", "SPR"},
        position = 100,
        showInFileChooser = {"Sprite"}
)
@DataObject.Registration(
        mimeType = "application/x-sprite",
        iconBase = "br/com/objectware/editors/sprite/sprite-file.png",
        displayName = "#sprite.file.label",
        position = 500
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/application/x-sprite/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
/**
 * This class stores and manages a sprite data file.
 */
public class SpritesDataObject extends CustomDataObject {
    
    /** The sprite format: {@see SpriteFormat} */
    private SpriteFormat spriteFormat = SpriteFormat.UNDEFINED;
    
    /** The maximum number of sprites allowed (default value is 256) */
    private int maxSprites = 256;
    
    /** The maximum number of attributes allowed (default value is 256) */
    private int maxAttributes = 256;
    
    /** The palette colors */
    private final List<Color> palette = new java.util.ArrayList<>();
    
    /** The list of sprites (patterns) */
    private final List<Sprite> sprites = new java.util.ArrayList<>();
    
    /** The list of sprite attributes */
    private final List<SpriteAttributes> attributes = new java.util.ArrayList<>();
    
    /** The undo/redo manager */
    private final UndoRedo.Manager undoRedoManager = new UndoRedo.Manager();
    
    /** Notifier that can be used to notify changes in the SpritesDataObject */
    private final Notifier notifier = new Notifier();
    
    /**
     * This constructor is called when the file icon is showed in the project tree. (#1)
     * The data object file is not loaded when the constructor is called. This is done
     * when the user double click the data object icon in the project tree.
     * 
     * @param pf FileObject associated to this DataObject
     * @param loader the multi file loader
     * @throws DataObjectExistsException
     * @throws IOException 
     */
    public SpritesDataObject(FileObject fo, MultiFileLoader loader)
                throws DataObjectExistsException, java.io.IOException {
        
        super(fo, loader);
        
        // set the opener cookie (create and open the editor top component)
        this.getCookieSet().add(new OpenSpriteEditorCookie(super.getLookup()));
    }   
    
    @Override
    public Lookup getLookup() {
        return this.getCookieSet().getLookup();
    }   
    
    /**
     * Notify Observers about changes in this data object.
     */
    public void notifyObservers() {
        this.notifier.notifyObservers(this);
    }   
    
    /**
     * Add a new observer for this data object.
     * @param observer 
     */
    public void addObserver(java.util.Observer observer) {
        this.notifier.addObserver(observer);
    }   
    
    /**
     * Remove an observer from notifier of this data object.
     * @param observer 
     */
    public void deleteObserver(java.util.Observer observer) {
        this.notifier.deleteObserver(observer);
    }   
    
    /**
     * Return the current sprite format (MSX,SMS, etc...).
     * @return the current sprite format
     */
    public SpriteFormat getSpriteFormat() {
        return this.spriteFormat;
    }   
    
    /**
     * Return the sprites data object status (data loaded or not)
     * @return 
     */
    public boolean isEmpty() {
        return this.sprites.isEmpty() || this.attributes.isEmpty();
    }   
    
    /**
     * Return the list of current selected palette colors
     * @return the list of palette colors
     */
    public List<Color> getPalette() {
        return java.util.Collections.unmodifiableList(this.palette);
    }   
    
    /**
     * Update the (current) palette
     * @param paletteColors the new palette colors
     */
    public synchronized void setPalette(List<Color> paletteColors) {
        if(paletteColors != null && paletteColors.size() > 0) {
            this.palette.clear();
            this.palette.addAll(paletteColors);
            this.setChanged(true);
        }   
    }   
    
    /**
     * Get the sprite list size.
     * @return the sprite list size
     */
    public int getSpritesCount() {
        return this.sprites.size();
    }   
    
    /**
     * Return the list of sprites
     * @return the observable list of sprites (pattern and color attribute)
     */
    public List<Sprite> getSprites() {
        return java.util.Collections.unmodifiableList(this.sprites);
    }   
    
    /**
     * Return the list of sprite attributes
     * @return the observable list of sprite attributes table
     */
    public List<SpriteAttributes> getAttributes() {
        return java.util.Collections.unmodifiableList(this.attributes);
    }   
    
    /**
     * Return the index of the given sprite in the sprite's list.
     * @param sprite
     * @return 
     */
    public synchronized int getSpriteIndex(Sprite sprite) {
        return this.sprites.indexOf(sprite);
    }   
    
    /**
     * Return the sprite at a given index.
     * @param index
     * @return 
     */
    public synchronized Sprite getSpriteAt(int index) {
        return this.sprites.get(index);
    }   
    
    /**
     * Return the pattern of a given index.
     * @param index the pattern index
     * @return the pattern
     */
    public synchronized SpritePattern getPatternAt(int index) {
        return this.sprites.get(index).getPattern();
    }   
    
    /**
     * Add a new SpritePattern to the current sprites set.
     * @param sprite 
     */
    public synchronized void addSprite(Sprite sprite) {
        
        // add sprite to list and set the sprite pattern number
        this.sprites.add(sprite);
        this.updateSpriteIndexes();
        
        this.setChanged(true);
    }   
    
    /**
     * Add a new SpritePattern to the current sprites set.
     * @param sprite 
     */
    public synchronized void addSpriteAt(int index, Sprite sprite) {
        
        // add sprite to list and set the sprite pattern number
        this.sprites.add(index, sprite);
        this.updateSpriteIndexes();
        
        this.setChanged(true);
    }   
    
    /**
     * Create a new Sprite.
     * The type of the sprite will be the same of the current sprites data object.
     */
    public Sprite createNewSprite() {
        return SpriteFactory.newSprite(this.getSpriteFormat());
    }   
    
    /**
     * Remove an existing Sprite from current sprites set.
     * @param sprite 
     */
    public synchronized void removeSprite(Sprite sprite) {
        
        this.sprites.remove(sprite);
        this.updateSpriteIndexes();
        
        this.setChanged(true);
    }   
    
    /**
     * Move the sprite pattern up in list. This movement changes the sprite pattern number.
     * Moving up means "decrease the pattern number" (shift the sprite towards the head of the list).
     * @param sprite the sprite to be "moved"
     */
    public void moveSpriteUp(Sprite sprite) {
        
        ListUtil.moveUp(this.sprites, sprite);
        this.updateSpriteIndexes();
        
        this.setChanged(true);
    }   
    
    /**
     * Move the sprite pattern down in list. This movement changes the sprite pattern number.
     * Moving down means "increase the pattern number" (shift the sprite towards the tail of the list).
     * @param sprite the sprite to be "moved"
     */
    public void moveSpriteDown(Sprite sprite) {
        
        ListUtil.moveDown(this.sprites, sprite);
        this.updateSpriteIndexes();
        
        this.setChanged(true);
    }   
    
    /**
     * Set the maximum number of sprites
     * @param maxSprites 
     */
    public void setMaxSprites(int maxSprites) {
        this.maxSprites = maxSprites;
    }
    
    /**
     * Set the maximum number of attributes
     * @param maxAttributes 
     */
    public void setMaxAttributes(int maxAttributes) {
        this.maxAttributes = maxAttributes;
    }   
    
    /**
     * Update the sprites indexes according to the current position on the sprite list.
     */
    private void updateSpriteIndexes() {
        
        // get the iterator to run over all sprites of list in the proper order
        java.util.Iterator<Sprite> iterator = this.getSprites().iterator();
        int index = 0;
        
        while(iterator.hasNext()) {
            iterator.next().setIndex(index++);
        }   
    }
    
    /**
     * Getter for the Undo/Redo manager.
     * @return the undo/redo manager
     */
    public UndoRedo.Manager getUndoRedoManager() {
        return this.undoRedoManager;
    }
    
    /**
     * Set the modification status of the sprite data.
     * @param changed the binary data was changed
     */
    @Override
    public synchronized void setChanged(boolean changed) {
        
        // this call sets the data object internal change status (changed or not)
        this.setModified(changed);
        
        // get the save data object cookie (if available)
        SaveDataObjectCookie cookie = this.getCookieSet().getCookie(SaveDataObjectCookie.class);
        
        // update the cookie set and notifier status
        if(changed) {
            // the data object is being changed and there is no SaveCookie available
            if(cookie == null) {
                this.getCookieSet().add(new SaveDataObjectCookie(super.getLookup()));
            }
            this.notifier.setChanged();
            
        } else {
            // the data object is being unchanged and there is a SaveCookie in the cookie set
            if(cookie != null) {
                this.getCookieSet().remove(cookie);
            }
            this.notifier.clearChanged();
        }   
    }
    
    /**
     * Loads the sprite data from file (patterns and attributes).
     * 
     * @throws IOException if some reading error occurs
     * @throws IllegalArgumentException if the input file has some invalid data
     * @throws NumberFormatException if some data entry is invalid
     */
    @Override
    public synchronized void load() 
            throws java.io.IOException, IllegalArgumentException, NumberFormatException {
        
        // this will reference one line at a time
        String line;
        String data;
        SpriteDataIdentifier lineIdentifier;
        
        // the current sprite being processed
        Sprite newSprite = null;
        
        // the current line number (used in error messages)
        int lineNumber = 0;
        int spriteNumber = 0;
        int attributeNumber = 0;
        
        // clears the current sprite list and attributes list
        this.sprites.clear();
        this.attributes.clear();
        
        // FileReader reads text files in the default encoding
        java.io.FileReader fileReader = new java.io.FileReader(this.getPrimaryFile().getPath());
        
        try (java.io.BufferedReader bufferedReader = new java.io.BufferedReader(fileReader)) {
            
            while ((line = bufferedReader.readLine()) != null) {
                
                // update the line number counter
                lineNumber++;
                
                // discard comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }
                
                // get the line label (every non empty line must have a label)
                String lineSplit[] = line.replace(Default.TAB_CHAR, Default.SPACE_CHAR).split(":");
                if (lineSplit.length > 0) {
                    lineIdentifier = SpriteDataIdentifier.valueOf(lineSplit[0].trim());
                } else {
                    throw new MissingFormatArgumentException(I18N.getString("missing.label.at.line", lineNumber));
                }   
                
                // verify if there is any data available (this is an optional field)
                if (lineSplit.length > 1) {
                    data = lineSplit[1].trim();
                } else {
                    data = null;
                }
                
                // setup the sprite format type (this must be done only once)
                // these tags must appear only at the start of the file (before any sprite definition)
                switch (lineIdentifier) {
                    
                    // setup the sprite format
                    case FORMAT:
                        try {
                            // get the sprite format information (on "reload" this must not be changed)
                            if (this.spriteFormat.equals(SpriteFormat.UNDEFINED)) {
                                this.setSpriteFormat(SpriteFormat.valueOf(data));
                            }
                        } catch (IllegalArgumentException exception) {
                            throw new IllegalArgumentException(
                                I18N.getString("sprite.format.not.available", data)
                            );
                        }
                        
                        // format ok. Read the next line!
                        continue;
                        
                    // set the current palette colors (palette used to render all sprites in the patern editor)
                    case PALETTE:
                        
                        // update the color palette
                        List<Color> paletteColors = this.processPaletteDataLine(data);
                        this.setPalette(paletteColors);
                        
                        // palette ok. Read the next line!
                        continue;
                        
                    default:
                        break;
                }   
                
                // create the new sprite and add it to the sprites list
                if (lineIdentifier.equals(SpriteDataIdentifier.SPRITE)) {
                    
                    // add a new sprite to the sprite list (in this case, the line data is the sprite label)
                    if(this.spriteFormat.isDefined()) {
                        
                        newSprite = SpriteFactory.newSprite(this.spriteFormat);
                        newSprite.setLabel(data);    // data contains the sprite label
                        
                        this.addSprite(newSprite);
                        spriteNumber++;
                        
                    } else {
                        // trying to add a new sprite when there is no sprite format defined
                        throw new IllegalStateException(
                            I18N.getString("missing.header.data", this.getPrimaryFile().getPath())
                        );
                    }   
                    
                } else if (lineIdentifier.equals(SpriteDataIdentifier.ATTRIBUTE)) {
                    
                    if(attributeNumber < this.maxAttributes) {
                        
                        // create the new sprite attribute
                        if(this.spriteFormat.isDefined()) {
                            
                            SpriteAttributes spriteAttributes = AttributeFactory.newAttribute(this.spriteFormat);
                            spriteAttributes.setData(TextUtil.parseStringOfHexToByteArray(data));
                            
                            this.attributes.add(spriteAttributes);
                            attributeNumber++;
                            
                        } else {
                            // trying to add a new attribute when there is no sprite format defined
                            throw new IllegalStateException(
                                I18N.getString("missing.header.data", this.getPrimaryFile().getPath())
                            );
                        }   
                    }   
                    
                } else if (newSprite != null) {
                    
                    // get the sprite attributes, pattern and palette
                    switch (lineIdentifier) {
                        
                        case COLOR:
                            
                            byte[] spriteAttributes = TextUtil.parseStringOfHexToByteArray(data);
                            newSprite.getAttributes().setData(spriteAttributes);
                            
                            // the attribute at position [0] defines the sprite background color code
                            byte colorCode = (byte) (spriteAttributes[0] & 0x0F); // Keeping retro compatibility
                            newSprite.setBackgroundColorCode(colorCode); // TODO: Remove these two lines in the future
                            break;
                            
                        // NOT USED!!!
                        case FG_COLOR:
                            byte fgColorCode = TextUtil.parseStringToByte(data);
                            newSprite.setForegroundColorCode(fgColorCode);
                            break;
                            
                        case BG_COLOR:
                            byte bgColorCode = TextUtil.parseStringToByte(data);
                            newSprite.setBackgroundColorCode(bgColorCode);
                            break;
                            
                        case PATTERN:
                            byte[] patternData = TextUtil.parseStringOfHexToByteArray(data);
                            newSprite.getPattern().setData(patternData);
                            break;
                            
                        case DEBUG:
                            System.out.println(newSprite);
                            break;
                            
                        default:
                            throw new IllegalArgumentException(
                                I18N.getString("invalid.label.at.line", lineIdentifier, lineNumber)
                            );
                    }   
                    
                    newSprite.notifyObservers(); // "clean" the changed flag
                    
                } else {
                    // trying to define other properties when there is no sprite data available
                    throw new IllegalArgumentException(
                        I18N.getString("invalid.label.at.line", lineIdentifier, lineNumber)
                    );
                }
            }   
            
        } catch(NullPointerException | NumberFormatException exception) {
            throw new NumberFormatException(I18N.getString("invalid.data.at.line", lineNumber));
        }   
        
        // reset the changed status (disable save button)
        this.notifyObservers();
        this.setChanged(false);
        
        // all edits must be discarded
        this.getUndoRedoManager().discardAllEdits();
    }   
    
    /**
     * Saves the current sprite data into the binary file (patterns and attributes).
     * 
     * @throws IOException if some writing error occurs
     */
    @Override
    public synchronized void save() 
            throws java.io.IOException {
        
        String fileName = this.getPrimaryFile().getPath();
        SpriteFormat format = this.getSpriteFormat();
        
        // write the file to disk (assume default encoding)
        java.io.FileWriter fileWriter = new java.io.FileWriter(fileName);
        
        // write sprite data to file
        try (java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(fileWriter)) {
            
            // write the sprites file header comments
            bufferedWriter.write("; " + format.getDescription() + " data file");
            bufferedWriter.newLine();
            bufferedWriter.write("; " + Default.COPYRIGHT);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            
            // write the sprites file header (general sprites set attributes)
            bufferedWriter.write("; Sprite data file header");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write(SpriteDataIdentifier.FORMAT + ": " + format.name());
            bufferedWriter.newLine();
            
            // write the sprite set palette
            bufferedWriter.write(SpriteDataIdentifier.PALETTE + ": ");
            for(Color color:this.getPalette()) {
                bufferedWriter.write(TextUtil.intToHex6String(color.getRGB()) + " ");
            }
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            
            // write the sprite patterns data
            bufferedWriter.write("; Sprite patterns and attributes");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            
            // write all sprites patterns data
            for(Sprite sprite:this.sprites) {
                
                // write the sprite label
                bufferedWriter.write(SpriteDataIdentifier.SPRITE + ": " + sprite.getLabel());
                bufferedWriter.newLine();
                
                // write the sprite attributes (used only for view purposes)
                bufferedWriter.write(SpriteDataIdentifier.COLOR + ": ");
                byte[] attributesData = sprite.getAttributes().getData();
                for(byte data:attributesData) {
                    bufferedWriter.write(TextUtil.byteToHexString(data) + " ");
                }   
                bufferedWriter.newLine();
                
                // write the sprite background color
                bufferedWriter.write(SpriteDataIdentifier.BG_COLOR + ": ");
                bufferedWriter.write(TextUtil.byteToHexString(sprite.getBackgroundColorCode()));
                bufferedWriter.newLine();
                
                // write the sprite pattern data
                bufferedWriter.write(SpriteDataIdentifier.PATTERN + ": ");
                for(byte data:sprite.getPattern().getData()) {
                    bufferedWriter.write(TextUtil.byteToHexString(data) + " ");
                }
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }   
            
            // write the sprite attributes data
            bufferedWriter.write("; Sprite attributes table");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            
            // write all sprites attributes/data/palette
            for(SpriteAttributes spriteAttributes:this.attributes) {
                
                // write the sprite attributes
                bufferedWriter.write(SpriteDataIdentifier.ATTRIBUTE + ": ");
                for(byte data:spriteAttributes.getData()) {
                    bufferedWriter.write(TextUtil.byteToHexString(data) + " ");
                }   
                bufferedWriter.newLine();
            }   
        }   
        
        this.setChanged(false);
    }
    
    /** Parse the palette data into Color objects */
    private List<Color> processPaletteDataLine(String lineData) throws NumberFormatException {
        
        List<Color> paletteColors = new java.util.ArrayList<>();
        
        for(String data:lineData.split(Default.SPACE_STRING)) {
            int color = Integer.parseInt(data, 16);
            paletteColors.add(new Color(color));
        }   
        
        return paletteColors;
    }   
    
    /**
     * Set the data object sprite params for given sprite format.
     * @param spriteFormat 
     */
    private void setSpriteFormat(SpriteFormat spriteFormat) {
        
        switch (spriteFormat) {
            
            case MSX_MODE1:
            case MSX_MODE2:
                // set the sprite format and the size of attributes table and patters table
                this.spriteFormat = spriteFormat;
                this.setMaxSprites(MsxSpritePattern.TABLE_SIZE);
                this.setMaxAttributes(MsxSpriteAttributes.TABLE_SIZE);
            break;
            
            default:
                throw new IllegalArgumentException(I18N.getString("sprite.format.not.available", spriteFormat));
        }   
    }
    
    /**
     * The notifier used by the SpritesDataObject.
     */
    private class Notifier extends java.util.Observable {
        
        @Override
        protected synchronized void setChanged() {
            super.setChanged();
        }
        
        @Override
        protected synchronized void clearChanged() {
            super.clearChanged();
        }
    }
}
