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

import org.primesoft.mcpainter.Configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.CubeFace;
import org.primesoft.mcpainter.drawing.Face;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.texture.TextureManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBPrime
 */
public class TrapDoor extends Flat {

    public static final String NAME_TD = "TRAP_DOOR";

    public TrapDoor(TextureManager textureManager, ConfigurationSection bp) {
        super(textureManager, bp);

        RawImage img = BlockHelper.parseTexture(textureManager, bp.getString("Texture"));
        CubeFace face = CubeFace.valueOf(bp.getString("Face", "Bottom"));

        int res = img.getRes();

        Face quad = new Face(res - 1, res - 1, 0, 0, img);
        quad.setDepth(3);
        initializeFaces(m_faces, face, quad, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
    }
}
