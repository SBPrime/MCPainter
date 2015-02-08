/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
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

import java.util.List;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.Face;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.utils.Pair;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.utils.Vector2D;
import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;
import org.PrimeSoft.MCPainter.worldEdit.MaxChangedBlocksException;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBPrime
 */
public class Plane extends BaseBlock {

    public static final String NAME = "PLANE";

    private final Face[] m_main;
    private final Face[] m_top;

    public Plane(TextureManager textureManager, ConfigurationSection bp) {
        super(false, false);
        List<String> textures = bp.getStringList("Textures");
        String texture = bp.getString("Texture");

        RawImage[] tex = null;
        if (textures != null && textures.size() > 0) {
            tex = BlockHelper.parseTextures(textureManager, textures);
        }
        if (texture != null) {
            tex = new RawImage[]{BlockHelper.parseTexture(textureManager, texture)};
        }

        int[] res = new int[tex.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = tex[i] != null ? tex[i].getRes() : 0;
        }

        m_main = new Face[]{
            new Face(res[0] - 1, res[0] - 1, 0, 0, tex[0]),
            new Face(res[0] - 1, res[0] - 1, 0, 0, tex[0])
        };

        for (int i = 0; i < 2; i++) {
            Face face = m_main[i];
            face.setDepth(2);
            face.setDelta(ConfigProvider.BLOCK_SIZE / 2 - 1);
            face.setDepth(2);
        }

        if (tex.length > 1 && tex[1] != null) {
            m_top = new Face[]{
                new Face(0, 0, res[0] - 1, res[0] - 1, tex[1]),
                new Face(new Vector2D(0, 0), new Vector2D(0, res[0] - 1),
                new Vector2D(res[0] - 1, 0), new Vector2D(res[0] - 1, res[0] - 1), tex[1])
            };
        } else {
            m_top = new Face[2];
        }
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, ColorMap colorMap) 
            throws MaxChangedBlocksException {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(0, 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch,
                m_size.getBlockX(), m_size.getBlockY(), m_size.getBlockZ());

        Face[] main = new Face[]{Face.clone(m_main[0]), Face.clone(m_main[1])};
        Face[] top = new Face[]{Face.clone(m_top[0]), Face.clone(m_top[1])};

        int[] crop1 = new int[]{0, 0};
        int[] crop2 = new int[]{0, 0};
        int[] crop3 = new int[]{0, 0};
        int[] crop4 = new int[]{0, 0};

        if ((data & 0x10) != 0) {
            crop1[0] = ConfigProvider.BLOCK_SIZE / 2;
            crop3[0] = ConfigProvider.BLOCK_SIZE / 2;
        } else if ((data & 0x100) != 0) {
            crop1[0] = 1;
        }
        if ((data & 0x20) != 0) {
            crop1[1] = ConfigProvider.BLOCK_SIZE / 2;
            crop3[1] = ConfigProvider.BLOCK_SIZE / 2;
        } else if ((data & 0x200) != 0) {
            crop1[1] = 1;
        }
        if ((data & 0x40) != 0) {
            crop2[0] = ConfigProvider.BLOCK_SIZE / 2;
            crop4[0] = ConfigProvider.BLOCK_SIZE / 2;
        } else if ((data & 0x400) != 0) {
            crop2[0] = 1;
        }
        if ((data & 0x80) != 0) {
            crop2[1] = ConfigProvider.BLOCK_SIZE / 2;
            crop4[1] = ConfigProvider.BLOCK_SIZE / 2;
        } else if ((data & 0x800) != 0) {
            crop2[1] = 1;
        }

        if (crop1[0] != 0 || crop1[1] != 0
                || crop3[0] != 0 || crop3[1] != 0) {
            main[0].setCropH(new Pair<Integer, Integer>(crop1[0], crop1[1]));
            top[1].setCropH(new Pair<Integer, Integer>(crop3[1], crop3[0]));
        }
        if (crop2[0] != 0 || crop2[1] != 0
                || crop4[0] != 0 || crop4[1] != 0) {
            main[1].setCropH(new Pair<Integer, Integer>(crop2[0], crop2[1]));
            top[0].setCropV(new Pair<Integer, Integer>(crop4[0], crop4[1]));
        }

        ImageHelper.drawCube(loger, colorMap, position, orientation, m_size,
                new Face[]{main[0], null, main[1], null, null, null}, true, OperationType.Block);
        ImageHelper.drawCube(loger, colorMap, position, orientation, m_size,
                new Face[]{null, null, null, null, top[0], top[0]}, true, OperationType.Block);
        ImageHelper.drawCube(loger, colorMap, position, orientation, m_size,
                new Face[]{null, null, null, null, top[1], top[1]}, true, OperationType.Block);
    }
}
