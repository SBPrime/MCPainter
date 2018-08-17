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

import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.drawing.*;
import org.primesoft.mcpainter.utils.Orientation;
import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.utils.Utils;
import org.primesoft.mcpainter.utils.Vector;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBPrime
 */
public class Door extends BaseBlock {

    public final static String NAME = "DOOR";
    private Face[] m_faces2;

    public Door(TextureManager textureManager, ConfigurationSection bp) {
        super(true, false);

        CubeFace face = CubeFace.valueOf(bp.getString("Face", "Front"));
        boolean flip = bp.getBoolean("Flip", false);
        RawImage[] tex = BlockHelper.parseTextures(textureManager, bp.getStringList("Textures"));
        initialize(tex, flip, face);
    }

    /**
     * Initialize block data
     *
     * @param tex
     * @param flip
     * @param face
     */
    private void initialize(RawImage[] tex, boolean flip, CubeFace face) {
        m_faces = new Face[6];
        m_faces2 = new Face[6];

        int[] u2, v2;
        v2 = new int[tex.length];
        u2 = new int[tex.length];
        for (int i = 0; i < tex.length; i++) {
            u2[i] = tex[i].getRes() - 1;
            v2[i] = tex[i].getRes() - 1;
        }

        Face quadDown = new Face(flip ? u2[0] : 0, v2[0], flip ? 0 : u2[0], 0, tex[0]);
        Face quadUp = new Face(flip ? u2[1] : 0, v2[1], flip ? 0 : u2[1], 0, tex[1]);

        quadUp.setDepth(3);
        quadDown.setDepth(3);

        Flat.initializeFaces(m_faces, face, quadUp, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
        Flat.initializeFaces(m_faces2, face, quadDown, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(yaw, 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch,
                m_size.getBlockX(), m_size.getBlockY(), m_size.getBlockZ());

        int dx = orientation.calcX(0, ConfigProvider.BLOCK_SIZE, 0);
        int dy = orientation.calcY(0, ConfigProvider.BLOCK_SIZE, 0);
        int dz = orientation.calcZ(0, ConfigProvider.BLOCK_SIZE, 0);

        ImageHelper.drawCube(loger, colorMap, position, orientation,
                m_size, m_faces2, true, OperationType.Block);

        ImageHelper.drawCube(loger, colorMap, position.add(dx, dy, dz),
                orientation, m_size, m_faces, true, OperationType.Block);

    }
}
