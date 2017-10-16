/**
 * 
 */

package br.com.objectware.domain.enums;

import java.awt.Color;

/**
 * @author Luciano M. Christofoletti
 * @since 12/dec/2014
 */
public enum MsxColor {
	
	TRANSPARENT(0, 0, 0),
	BLACK(0x6, 0x6, 0x6),
	MEDIUM_GREEN(0x20, 0xD8, 0x20),
	LIGHT_GREEN(0x68, 0xF7, 0x68),
	DARK_BLUE(0x20, 0x20, 0xF7),
	LIGHT_BLUE(0x48, 0x68, 0xF7),
	DARK_RED(0xB0, 0x20, 0x20),
	CYAN(0x48, 0xD8, 0xF7),
	MEDIUM_RED(0xF7, 0x20, 0x20),
	LIGHT_RED(0xF7, 0x68, 0x68),
	DARK_YELLOW(0xD8, 0xD8, 0x20),
	LIGHT_YELLOW(0xD8, 0xD8, 0x90),
	DARK_GREEN(0x20, 0x90, 0x20),
	MAGENTA(0xD8, 0x48, 0xB0),
	GRAY(0xB0, 0xB0, 0xB0),
	WHITE(0xF7, 0xF7, 0xF7);
	
    /** The MSX palette colors size */
    public static final int MSX_PALETTE_SIZE = 16;
    
	/** The RGB color values */
	private final Color color;
	
	/** The colors vector (useful to get the color by number) */
	private static final java.util.List<Color> MSX_16_COLOR_PALETTE = new java.util.ArrayList<>(16);
	
    /** The MSX2 color palette (256 colors) */
	private static final java.util.List<Color> MSX_512_COLOR_PALETTE = new java.util.ArrayList<>(512);
    
    /** MSX 2/2+ rgb values references */
    private static final int COLOR_COMPONENTS[] = new int[] {0x0, 0x20, 0x48, 0x68, 0x90, 0xB0, 0xD8, 0xF7};
    
    static {
        MsxColor.initializeColorPalette();
    }
    
	private MsxColor(int r, int g, int b) {
		this.color = new Color(r, g, b);
	}   
	
    /**
     * Return the color 
     * @return 
     */
	public Color getColor() {
		return this.color;
	}
	
    /**
     * Return the color code (byte).
     * @return 
     */
    public byte getCode() {
		return (byte) this.ordinal();
	}
    
    /**
     * Get color from MSX 16 colors palette
     * @param number the color number
     * @return 
     */
	public static final Color getMsx16Color(int number) {
		return MsxColor.MSX_16_COLOR_PALETTE.get(number);
	}   
    
    /**
     * Get color from MSX 2 512 colors palette
     * @param number the color number
     * @return 
     */
    public static final Color getMsx512Color(int number) {
		return MsxColor.MSX_512_COLOR_PALETTE.get(number);
	}   
    
    /**
     * Return the MSX 1 color palette. The returned object may be changed according to the user's needs.
     * @return list of MSX colors
     */
    public static final java.util.List<Color> getMsx16ColorPalette() {
		return new java.util.ArrayList<>(MsxColor.MSX_16_COLOR_PALETTE);
	}   
    
    /**
     * Return all the MSX 2 colors.
     * @return list of MSX 2 colors (512)
     */
    public static final java.util.List<Color> getMsx512ColorPalette() {
		return new java.util.ArrayList<>(MsxColor.MSX_512_COLOR_PALETTE);
	}   
    
    /** Initializes the palette colors */
    private static void initializeColorPalette() {
        
        // initialize the 16 color palette (MSX 1 color pallete)
        for(MsxColor color:MsxColor.values()) {
            MsxColor.MSX_16_COLOR_PALETTE.add(color.getColor());
        }   
        
        // initialize the 512 color palette (MSX 2/2+/TR color pallete)
        for(int green:COLOR_COMPONENTS) {
            for(int red:COLOR_COMPONENTS) {
                for(int blue:COLOR_COMPONENTS) {
                    MsxColor.MSX_512_COLOR_PALETTE.add(new Color(red, green, blue));
                }   
            }
        }
    }
}
