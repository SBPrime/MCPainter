/*
 * The MIT License
 *
 * Copyright 2012 SBPrime.
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
package org.primesoft.mcpainter.drawing.blocks;

import org.primesoft.mcpainter.drawing.CubeFace;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.texture.TextureEntry;
import org.primesoft.mcpainter.texture.TextureManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBP
 */
public class StairsMb extends MultiBlock {
    public final static String NAME = "STAIRS";
    
    
    public StairsMb(TextureManager textureManager, ConfigurationSection bp) { 
        this(BlockHelper.parseTextures(textureManager, bp.getStringList("Textures")));
    }
    
    
    /**
     * Constructor required for performance optimization when
     * creating block from ModBlockProvider (YML config)
     * @param textures 
     */
    private StairsMb(RawImage[] textures)
    {
        super(new IDrawableElement[]{
                    new Stairs(textures, false, CubeFace.Right),
                    new Stairs(textures, false, CubeFace.Left),
                    new Stairs(textures, false, CubeFace.Front),
                    new Stairs(textures, false, CubeFace.Back),
                    new Stairs(textures, true, CubeFace.Right),
                    new Stairs(textures, true, CubeFace.Left),
                    new Stairs(textures, true, CubeFace.Front),
                    new Stairs(textures, true, CubeFace.Back)
                }, new short[]{0, 1, 2, 3, 4, 5, 6, 7}, false, (short)0, (short)7);
    }
}