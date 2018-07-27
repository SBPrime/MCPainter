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

import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.Configuration.OperationType;
import org.primesoft.mcpainter.drawing.Face;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.drawing.statue.StatueBlock;
import org.primesoft.mcpainter.drawing.statue.StatueFace;
import org.primesoft.mcpainter.utils.Orientation;
import org.primesoft.mcpainter.utils.Pair;
import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.utils.Utils;
import org.primesoft.mcpainter.utils.Vector;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBPrime
 */
public class CustomBlock implements IDrawableElement {

    public final static String NAME = "CUSTOM";
    private final StatueBlock[] m_blocks;
    private final StatueFace[][] m_faces;
    private final RawImage[] m_textures;
    private final int[] m_textureRes;
    private final double[] m_textureScale;
    private final int[] m_columns;
    private final int[] m_rows;
    private final boolean m_useYaw;
    private final boolean m_usePitch;
    private final Vector m_size;

    /**
     * Initialize class using YML configuration section
     *
     * @param textureManager
     * @param bp
     */
    public CustomBlock(TextureManager textureManager, ConfigurationSection bp) {
        m_usePitch = bp.getBoolean("UsePitch", false);
        m_useYaw = bp.getBoolean("UseYaw", false);
        m_columns = BlockHelper.parseIntListEntry(bp.getIntegerList("TextureColumns"));
        m_rows = BlockHelper.parseIntListEntry(bp.getIntegerList("TextureRows"));
        m_size = BlockHelper.parseSize(bp.getIntegerList("Size"));

        m_textureRes = BlockHelper.parseIntListEntry(bp.getIntegerList("TexturesRes"));
        m_textures = BlockHelper.parseTextures(textureManager, bp.getStringList("Textures"));
        m_textureScale = calcScale();
        Pair<StatueBlock, StatueFace[]>[] parts = BlockHelper.parseBlocks(bp.getConfigurationSection("Parts"));

        m_blocks = new StatueBlock[parts.length];
        m_faces = new StatueFace[parts.length][];
        for (int i = 0; i < parts.length; i++) {
            Pair<StatueBlock, StatueFace[]> part = parts[i];
            m_blocks[i] = part.getFirst();
            m_faces[i] = part.getSecond();
        }
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(m_useYaw ? yaw : 0, m_usePitch ? pitch : 0);
        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch, m_size);

        for (int i = 0; i < m_faces.length; i++) {
            Vector offset = m_blocks[i].getOffset();
            Vector size = m_blocks[i].getSize();
            boolean isDiagonal = m_blocks[i].isDiagonal();
            double[] map = m_blocks[i].getMap();

            int x = (int) offset.getX();
            int y = (int) offset.getY();
            int z = (int) offset.getZ();
            int dx = orientation.calcX(x, y, z);
            int dy = orientation.calcY(x, y, z);
            int dz = orientation.calcZ(x, y, z);

            StatueFace[] sFaces = m_faces[i];
            Face[] tex = new Face[sFaces.length];
            for (int j = 0; j < sFaces.length; j++) {
                StatueFace face = sFaces[j];
                if (face != null) {
                    int id = face.getTextureId();
                    if (id >= 0 && id < m_textureScale.length) {
                        RawImage img = m_textures[id];
                        double scale = m_textureScale[id];
                        tex[j] = face.getFace(img, m_columns, m_rows, scale);
                    } else {
                        tex[j] = null;
                    }
                } else {
                    tex[j] = null;
                }
            }

            if (isDiagonal) {
                ImageHelper.drawDiagonal(loger, colorMap, position.add(dx, dy, dz),
                        orientation, size, tex, null, true, OperationType.Block);
            } else {
                ImageHelper.drawCube(loger, colorMap, position.add(dx, dy, dz),
                        orientation, size, tex, map, null, true, OperationType.Block);
            }
        }
    }

    /**
     * Calculate texture scale
     *
     * @return
     */
    private double[] calcScale() {
        int max = Math.min(m_textureRes.length, m_textures.length);
        double[] result = new double[max];
        for (int i = 0; i < max; i++) {
            if (m_textures[i] != null) {
                result[i] = m_textures[i].getRes() / (double) m_textureRes[i];
            } else {
                result[i] = 1;
            }
        }
        return result;
    }
}