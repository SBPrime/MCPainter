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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.drawing.statue.StatueBlock;
import org.primesoft.mcpainter.drawing.statue.StatueFace;
import org.primesoft.mcpainter.utils.Pair;
import org.primesoft.mcpainter.texture.TextureDescription;
import org.primesoft.mcpainter.texture.TextureEntry;
import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Blocks helper class
 *
 * @author SBPrime
 */
public final class BlockHelper {

    /**
     * Parse the size line
     *
     * @param size
     * @return
     */
    public static Vector parseSize(List<Integer> size) {
        if (size == null || size.size() != 3) {
            return new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
        }

        Integer[] ss = size.toArray(new Integer[0]);
        return new Vector(ss[0], ss[1], ss[2]);
    }

    /**
     * Parse texture entries
     *
     * @param textureManager
     * @param textureList
     * @return
     */
    public static RawImage[] parseTextures(TextureManager textureManager, List<String> textureList) {
        if (textureList == null) {
            return new RawImage[0];
        }

        TextureEntry[] textures = new TextureEntry[textureList.size()];
        int idx = 0;
        for (Iterator<String> it = textureList.iterator(); it.hasNext();) {
            String data = it.next();                        
            TextureDescription td = TextureDescription.parse(data);
            if (td == null) {
                throw new IllegalArgumentException("Unable to parse texture description");
            }

            textures[idx] = textureManager.get(td);
            if (textures[idx] == null) {
                throw new IllegalArgumentException("Unable to get texture " + td.toString());
                //textures[idx] = null;
            }
            idx++;
        }

        int texturesCnt = textures.length;
        RawImage[] result = new RawImage[texturesCnt];

        for (int i = 0; i < texturesCnt; i++) {
            TextureEntry tex = textures[i];
            RawImage img = null;
            if (tex == null) {
                img = null;
            } else {
                RawImage[] imgs = tex.getImages();
                if (imgs != null && imgs.length > 0) {
                    img = imgs[0];
                }
            }
            result[i] = img;
        }

        return result;
    }

    /**
     * Parse texture
     *
     * @param textureManager
     * @param texture
     * @return
     */
    public static RawImage parseTexture(TextureManager textureManager, String texture) {
        if (texture == null) {
            return null;
        }

        TextureDescription td = TextureDescription.parse(texture);
        if (td == null) {
            throw new IllegalArgumentException("Unable to parse texture description");
        }

        TextureEntry tex = textureManager.get(td);
        if (tex == null) {
            throw new IllegalArgumentException("Unable to get texture " + td.toString());
        }

        RawImage[] result = tex.getImages();
        if (result == null || result.length == 0) {
            return null;
        }

        return result[0];
    }

    /**
     * Parse int list
     *
     * @param intList
     * @return
     */
    public static int[] parseIntListEntry(Collection<Integer> intList) {
        if (intList == null) {
            return new int[0];
        }

        int[] result = new int[intList.size()];
        int idx = 0;
        for (Iterator<Integer> it = intList.iterator(); it.hasNext();) {
            result[idx] = it.next();
            idx++;
        }

        return result;
    }

    public static double[] parseDoubleListEntry(List<Double> doubleList) {
        if (doubleList == null) {
            return new double[0];
        }

        double[] result = new double[doubleList.size()];
        int idx = 0;
        for (Iterator<Double> it = doubleList.iterator(); it.hasNext();) {
            result[idx] = it.next();
            idx++;
        }

        return result;
    }

    /**
     * Barse block entries (parts)
     *
     * @param blocksSection
     * @return
     */
    public static Pair<StatueBlock, StatueFace[]>[] parseBlocks(ConfigurationSection blocksSection) {
        if (blocksSection == null) {
            return new Pair[0];
        }

        List<Pair<StatueBlock, StatueFace[]>> result = new ArrayList<Pair<StatueBlock, StatueFace[]>>();
        Set<String> sections = blocksSection.getKeys(false);
        for (String string : sections) {
            ConfigurationSection subSection = blocksSection.getConfigurationSection(string);
            if (subSection == null) {
                continue;
            }
            StatueFace[] faces = parseFaces(subSection.getStringList("Faces"));
            boolean diagonal = subSection.getBoolean("IsDiagonal", false);
            List<Integer> pos = subSection.getIntegerList("Pos");
            List<Integer> size = subSection.getIntegerList("Size");
            double[] map = BlockHelper.parseDoubleListEntry(subSection.getDoubleList("Map"));

            if (map.length != 9) {
                map = null;
            }

            Pair<StatueBlock, StatueFace[]> entry = buildEntry(faces, pos, size, diagonal, map);
            if (entry != null) {
                result.add(entry);
            }
        }
        return result.toArray(new Pair[0]);
    }

    /**
     * Parse face list
     *
     * @param faceList
     * @return
     */
    private static StatueFace[] parseFaces(List<String> faceList) {
        if (faceList == null) {
            return null;
        }

        int cnt = Math.max(faceList.size(), 6);
        StatueFace[] result = new StatueFace[cnt];
        int idx = 0;
        for (String s : faceList) {
            result[idx] = StatueFace.parse(s);
            idx++;
        }
        return result;

    }

    /**
     * Convert block fragments to entity
     *
     * @param faces
     * @param pos
     * @param size
     * @return
     */
    private static Pair<StatueBlock, StatueFace[]> buildEntry(StatueFace[] faces,
            List<Integer> pos, List<Integer> size, boolean isDiagonal, double[] map) {
        if (faces == null || faces.length == 0) {
            return null;
        }
        if (pos == null || pos.size() != 3) {
            return null;
        }
        if (size == null || size.size() != 3) {
            return null;
        }

        Integer[] aSize = size.toArray(new Integer[0]);
        Integer[] aPos = pos.toArray(new Integer[0]);
        return new Pair<StatueBlock, StatueFace[]>(new StatueBlock(aPos[0], aPos[1], aPos[2],
                aSize[0], aSize[1], aSize[2], isDiagonal, map), faces);
    }
}
