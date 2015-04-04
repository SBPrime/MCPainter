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
package org.PrimeSoft.MCPainter.Drawing.Blocks;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.IColorMap;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBPrime
 */
public class Slab extends BaseBlock {

    public final static String NAME = "SLAB";
    /**
     * Slab is located in the upper half of the block?
     */
    private final boolean m_top;

    public Slab(TextureManager textureManager, ConfigurationSection bp) {
        super(false, false);

        m_top = bp.getBoolean("IsTop", false);
        RawImage texture = BlockHelper.parseTexture(textureManager, bp.getString("Texture"));
        RawImage[] tex = BlockHelper.parseTextures(textureManager, bp.getStringList("Textures"));

        if (tex == null || tex.length == 0) {
            tex = new RawImage[]{texture};
        }
        initialize(tex);
    }

    /**
     * Initialize block data
     *
     * @param tex
     */
    private void initialize(RawImage[] tex) {
        if (tex == null || tex.length == 0) {
            return;
        }

        /**
         * TODO: change this?
         */
        int res = tex[0].getRes();

        RawImage[] img = assignTextures(tex);

        m_size = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE / 2, ConfigProvider.BLOCK_SIZE);

        int[] u1 = new int[]{0, 0, 0, 0, 0, 0};
        int[] v1 = new int[6];
        int[] u2 = new int[6];
        int[] v2 = new int[6];
        convertSize(v1, u2, v2, res);

        m_faces = mapTexture(img, u1, v1, u2, v2);
    }

    private void convertSize(int[] v1, int[] u2, int[] v2, int res) {
        int add = m_top ? 0 : (res / 2);
        for (int i = 0; i < 4; i++) {
            v1[i] = add;
            u2[i] = res - 1;
            v2[i] = add + res / 2 - 1;
        }
        for (int i = 4; i < 6; i++) {
            v1[i] = 0;
            u2[i] = res - 1;
            v2[i] = res - 1;
        }
    }

    @Override
    public void draw(short data, BlockLoger loger, LocalPlayer localPlayer, IColorMap colorMap)
            throws MaxChangedBlocksException {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(0, 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);
        if (m_top) {
            position = position.add(0, ConfigProvider.BLOCK_SIZE / 2, 0);
        }

        ImageHelper.drawCube(loger, colorMap, position, orientation, m_size,
                m_faces, true, OperationType.Block);
    }
}
