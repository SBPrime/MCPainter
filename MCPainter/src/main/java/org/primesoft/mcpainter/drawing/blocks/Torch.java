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
import org.primesoft.mcpainter.drawing.Face;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.drawing.ImageHelper;
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
public class Torch extends BaseBlock {

    public final static String NAME = "TORCH";
    private Face m_diagonal;
    private Vector m_size2;

    public Torch(TextureManager textureManager, ConfigurationSection bp) {
        super(textureManager, bp);

        initialize();
    }

    /**
     * Initialize block data
     */
    private void initialize() {
        if (m_faces == null) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            m_faces[i].setDelta(7);
        }
        m_diagonal = m_faces[4];
        m_faces[4] = null;
        m_faces[5] = null;

        m_size2 = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE, 6);
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        if (data == 0) {
            super.draw(data, loger, localPlayer, colorMap);
            return;
        }

        final double[] map = new double[]{
            1, 0, 0,
            0, 1, 0,
            0, -0.3, 1
        };
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(yaw, 0);
        int dx = orientation.calcX(0, 4, 7);
        int dy = orientation.calcY(0, 4, 7);
        int dz = orientation.calcZ(0, 4, 7);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        ImageHelper.drawCube(loger, colorMap, position.add(dx, dy, dz), orientation, m_size,
                m_faces, map, null, true, OperationType.Block);

    }
}
