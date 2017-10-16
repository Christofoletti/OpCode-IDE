/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.editors.enums;

/**
 * This enum defines the sprite editor actions.
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 25/aug/2015
 */
public enum SpriteEditorAction {
    
    // edit/update actions
    UPDATE_VIEW, ATTRIBUTES_CHANGE, PATTERN_CHANGE,
    CHANGE_SPRITE_COLOR, CHANGE_SPRITE_BG_COLOR,
    CHANGE_SPRITE_LABEL, CHANGE_SPRITE_VIEW_SCALE,
    CHANGE_SCREEN_COLOR, CHANGE_SCREEN_BORDER_COLOR,
    
    // edit pattern actions
    SHIFT_RIGHT, SHIFT_LEFT, SHIFT_UP, SHIFT_DOWN,
    FLIP_VERTICAL, FLIP_HORIZONTAL, ROTATE, INVERT,
    MIRROR_UP_DOWN, MIRROR_LEFT_RIGHT, CLEAR_SPRITE,
    
    // general view actions
    EDIT_PALETTE, LOAD_DRAFT_IMAGE, CHANGE_VIEW_SCALE,
    EXPORT_PATTERNS, EXPORT_ATTRIBUTES, 
    DRAW_SPRITE_REFERENCE, DRAW_SPRITES_MAGNIFIED,
    
    // add/remove/move/select patterns
    ADD_SPRITE, INSERT_SPRITE, REMOVE_SPRITE,
    SHIFT_PATTERN_NUMBER_UP, SHIFT_PATTERN_NUMBER_DOWN,
    SELECT_NEXT_PATTERN, SELECT_PREVIOUS_PATTERN,
    
    // toggle button actions
    TOGGLE_PAINT_MODE, TOGGLE_MOUSE_ACTION, TOGGLE_COLOR_MODE_VIEW;
}
