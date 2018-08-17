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
package org.primesoft.mcpainter.drawing.blocks;

import java.util.HashMap;
import java.util.Map;
import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.texture.VanillaTextureProvider;
import org.bukkit.Material;

/**
 *
 * @author SBPrime
 */
public class VanillaBlockProvider implements IBlockProvider {

    private final VanillaTextureProvider m_tex;
    private final Map<Material, IDrawableElement> m_blockFaces;

    @Override
    public int getBlocksCount() {
        return m_blockFaces.size();
    }

    public VanillaBlockProvider(TextureManager textureManager) {
        if (textureManager == null) {
            m_tex = null;
            m_blockFaces = null;
            return;
        }

        m_tex = textureManager.getVanilla();
        m_blockFaces = new HashMap<>();
        m_blockFaces.put(Material.STICKY_PISTON, new Piston(m_tex, true)); //1d - Sticky piston
        m_blockFaces.put(Material.PISTON, new Piston(m_tex, false)); //21 - Piston
        m_blockFaces.put(Material.PISTON_HEAD, new PistonExtension(m_tex)); //22 - Piston extended
        m_blockFaces.put(Material.BROWN_MUSHROOM_BLOCK, new Shroom(m_tex, 3)); //63 - Huge brow mushroom
        m_blockFaces.put(Material.RED_MUSHROOM_BLOCK, new Shroom(m_tex, 2)); //64 - Huge red mushroom
    }

    @Override
    public IDrawableElement getBlock(String name) {
        Material blockMaterial = Material.getMaterial(name.toUpperCase());
        if (blockMaterial == null) {
            return null;
        }

        int materialId = blockMaterial.getId();
        return m_blockFaces.get(blockMaterial);
    }
}
