/**
 * 
 */

package br.com.objectware.domain.enums;

/**
 * The MSX screen sizes.
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 15/feb/2016
 */
public enum MsxScreen {
	
    SCREEN2(256, 192, SpriteFormat.MSX_MODE1),
    SCREEN3(64, 48, SpriteFormat.MSX_MODE1),
    SCREEN4(256, 192, SpriteFormat.MSX_MODE2),
    SCREEN5(256, 212, SpriteFormat.MSX_MODE2),
    SCREEN6(512, 212, SpriteFormat.MSX_MODE2),
    SCREEN7(512, 212, SpriteFormat.MSX_MODE2);
	
    /** The screen default border size */
    public static final int BORDER_SIZE = 32;
    
	/** The screen width (in pixels) */
	private final int width;
    
    /** The screen height (in pixels) */
    private final int height;
	
    /** The screen height (in pixels) */
    private final SpriteFormat spriteFormat;
    
    /**
     * The MsxScreen descriptor.
     * 
     * @param width width in pixels
     * @param height height in pixels
     * @param format the screen sprite format
     */
	private MsxScreen(int width, int height, SpriteFormat format) {
		this.width = width;
        this.height = height;
        this.spriteFormat = format;
	}   
	
    /**
     * Return the screen width (in pixels).
     * @return the screen width
     */
    public int getWidth() {
		return this.width;
	}   
    
    /**
     * Return the screen height (in pixels).
     * @return the screen height
     */
    public int getHeight() {
		return this.height;
	}   
    
    /**
     * Get the sprite mode for the screen.
     * @return 
     */
    public SpriteFormat getSpriteFormat() {
		return this.spriteFormat;
	}   
}   
