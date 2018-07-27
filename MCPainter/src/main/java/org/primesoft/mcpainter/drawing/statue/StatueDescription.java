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
package org.primesoft.mcpainter.drawing.statue;

import java.util.List;
import org.primesoft.mcpainter.drawing.blocks.BlockHelper;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.utils.Pair;
import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBPrime
 */
public class StatueDescription {

    private final Vector m_size;
    private final int[] m_textureRes;
    private final StatueBlock[] m_blocks;
    private final StatueFace[][] m_faces;
    private final int[] m_columns;
    private final int[] m_rows;
    private final String m_permissionNode;
    private final String m_name;
    private final RawImage[][] m_textures;

    public StatueDescription(TextureManager textureManager, ConfigurationSection bp) {
        m_name = bp.getName();
        m_permissionNode = bp.getString("Permission");
        m_size = BlockHelper.parseSize(bp.getIntegerList("Size"));

        int res = bp.getInt("TextureRes", -1);
        int[] mRes = BlockHelper.parseIntListEntry(bp.getIntegerList("TexturesRes"));

        if (res != -1) {
            m_textureRes = new int[]{res};
        } else if (mRes != null && mRes.length > 0) {
            m_textureRes = mRes;
        } else {
            m_textureRes = new int[]{16};
        }

        int variants = bp.getInt("Variants", -1);

        if (variants > 1) {
            m_textures = new RawImage[variants][];
            for (int i = 0; i < variants; i++) {
                m_textures[i] = getTextures(bp, textureManager, i + 1);
            }
        } else {
            m_textures = new RawImage[][]{getTextures(bp, textureManager, -1)};
        }
        m_columns = BlockHelper.parseIntListEntry(bp.getIntegerList("Columns"));
        m_rows = BlockHelper.parseIntListEntry(bp.getIntegerList("Rows"));
        Pair<StatueBlock, StatueFace[]>[] parts = BlockHelper.parseBlocks(bp.getConfigurationSection("Parts"));
        m_blocks = new StatueBlock[parts.length];
        m_faces = new StatueFace[parts.length][];
        for (int i = 0; i < parts.length; i++) {
            Pair<StatueBlock, StatueFace[]> part = parts[i];
            m_blocks[i] = part.getFirst();
            m_faces[i] = part.getSecond();
        }
    }

    /**
     * Parse texture entry
     *
     * @param bp
     * @param textureManager
     * @param id
     * @return
     */
    private static RawImage[] getTextures(ConfigurationSection bp,
            TextureManager textureManager, int id) {
        String simple;
        String multiple;

        if (id > 0) {
            simple = "Texture_" + id;
            multiple = "Textures_" + id;
        } else {
            simple = "Texture";
            multiple = "Textures";
        }

        List<String> textures = bp.getStringList(multiple);
        String texture = bp.getString(simple);
        RawImage[] tex = null;
        if (textures != null && textures.size() > 0) {
            tex = BlockHelper.parseTextures(textureManager, textures);
        }
        if (texture != null) {
            tex = new RawImage[]{BlockHelper.parseTexture(textureManager, texture)};
        }

        return tex;
    }

    public RawImage[] getTextures(int id) {
        if (id == 0) {
            id = 1;
        }
        id = (id + m_textures.length - 1) % m_textures.length;
        return m_textures[id];
    }

    public Vector getSize() {
        return m_size;
    }

    public int[] getTextureRes() {
        return m_textureRes;
    }

    public StatueBlock[] getBlocks() {
        return m_blocks;
    }

    public StatueFace[][] getFaces() {
        return m_faces;
    }

    public int[] getColumns() {
        return m_columns;
    }

    public int[] getRows() {
        return m_rows;
    }

    public String getName() {
        return m_name;
    }

    public String getPermission() {
        return m_permissionNode;
    }
}
