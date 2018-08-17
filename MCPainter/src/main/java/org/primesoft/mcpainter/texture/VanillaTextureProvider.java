/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primesoft.mcpainter.texture;

import org.primesoft.mcpainter.MCPainterMain;

/**
 * Vanilla minecraft (1.6.x) texture provider
 *
 * @author SBPrime
 */
public class VanillaTextureProvider {
    public final static String TEXTURE_NAME = "VANILLA";
    
    
    /**
     * Vanilla minecraft texture provider
     */
    private TextureProvider m_vanillaTextureProvider;
    
    /**
     * Initialize texture provider
     *
     * @return true if all ok
     */
    public boolean initialize(TextureManager textureManager) {        
        m_vanillaTextureProvider = textureManager.get(TEXTURE_NAME);
        
        if (m_vanillaTextureProvider == null) {
            MCPainterMain.log("Unable to initialize the default texture pack.");
            return false;
        }
        
        MCPainterMain.log("The default texture pack initialized.");

        return true;
    }

    /**
     * Get textures from texture pack. First try specific then default
     *
     * @param files Texture files to load
     * @return Array of textures
     */
    protected TextureEntry getTexture(String[] files, String name) {
        final String TEXTURES_DIR = "assets/minecraft/textures/";

        String[] textureFiles = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            textureFiles[i] = TEXTURES_DIR + files[i];
        }

        if (m_vanillaTextureProvider == null)
        {
            return null;            
        }
        
        return m_vanillaTextureProvider.getTexture(textureFiles, name, null, null);
    }

    public TextureEntry getTexture(String fileName, String name) {
        return getTexture(new String[]{fileName}, name);
    }

    public TextureEntry getAnvil() {
        final String[] files = new String[]{"block/anvil.png", "block/anvil_top.png", "block/chipped_anvil_top.png", "block/damaged_anvil_top.png"};
        return getTexture(files, "Anvil");
    }

    public TextureEntry getBeacon() {
        return getTexture("block/beacon.png", "Beacon");
    }

    public TextureEntry getBrewingStand() {
        final String[] files = new String[]{"block/brewing_stand.png", "block/brewing_stand_base.png"};
        return getTexture(files, "BrewingStand");
    }
    
    public TextureEntry getMushroomBlock() {
        final String[] files = new String[]{"block/mushroom_stem.png", "block/mushroom_block_inside.png", "block/red_mushroom_block.png", "block/brown_mushroom_block.png"};
        return getTexture(files, "MushroomBlock");
    }

    public TextureEntry getPiston() {
        final String[] files = new String[]{"block/piston_side.png", "block/piston_bottom.png", "block/piston_top.png", "block/piston_top_sticky.png", "block/piston_inner.png"};
        return getTexture(files, "Piston");
    }
}