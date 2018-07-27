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
import org.primesoft.mcpainter.Configuration.ConfigProvider;
import org.primesoft.mcpainter.Configuration.OperationType;
import org.primesoft.mcpainter.drawing.*;
import org.primesoft.mcpainter.utils.Orientation;
import org.primesoft.mcpainter.texture.TextureEntry;
import org.primesoft.mcpainter.utils.Utils;
import org.primesoft.mcpainter.utils.Vector;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;

/**
 *
 * @author SBPrime
 */
public class Stairs extends BaseBlock {

    private Face[] m_faces2;
    private Vector m_size2;
    private Vector m_add;
    private final boolean m_top;

    public Stairs(RawImage[] tex, boolean top, CubeFace face) {
        super(false, false);
        m_top = top;

        if (tex == null) {
            return;
        }
        RawImage[] img = assignTextures(tex);        
        
        /**
         * TODO: change this?
         */
        int res = img[0].getRes();

        m_size = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE / 2, ConfigProvider.BLOCK_SIZE);
        m_size2 = new Vector(ConfigProvider.BLOCK_SIZE / (face == CubeFace.Left || face == CubeFace.Right ? 2 : 1),
                ConfigProvider.BLOCK_SIZE / 2, ConfigProvider.BLOCK_SIZE / (face == CubeFace.Back || face == CubeFace.Front ? 2 : 1));

        int[] u1a = new int[6];
        int[] v1a = new int[6];
        int[] u2a = new int[6];
        int[] v2a = new int[6];
        int[] u1b = new int[6];
        int[] v1b = new int[6];
        int[] u2b = new int[6];
        int[] v2b = new int[6];

        convertSize(u1a, v1a, u2a, v2a, top, face, res);
        convertSize(u1b, v1b, u2b, v2b, top, face, res);

        m_faces = mapTexture(img, u1a, v1a, u2a, v2a);
        m_faces2 = mapTexture(img, u1b, v1b, u2b, v2b);

        if (face == CubeFace.Back) {
            m_add = new Vector(0, 0, ConfigProvider.BLOCK_SIZE / 2);
        } else if (face == CubeFace.Left) {
            m_add = new Vector(ConfigProvider.BLOCK_SIZE / 2, 0, 0);
        } else {
            m_add = new Vector();
        }
    }

    public Stairs(TextureEntry texture, int[] texIds, boolean top, CubeFace face) {
        super(false, false);
        m_top = top;

        if (texture == null) {
            return;
        }
        RawImage[] all = texture.getImages();
        RawImage[] tex = new RawImage[texIds.length];
        /**
         * TODO: change this?
         */
        int res = all[0].getRes();

        for (int i = 0; i < texIds.length; i++) {
            tex[i] = all[texIds[i]];
        }

        RawImage[] img = assignTextures(tex);

        m_size = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE / 2, ConfigProvider.BLOCK_SIZE);
        m_size2 = new Vector(ConfigProvider.BLOCK_SIZE / (face == CubeFace.Left || face == CubeFace.Right ? 2 : 1),
                ConfigProvider.BLOCK_SIZE / 2, ConfigProvider.BLOCK_SIZE / (face == CubeFace.Back || face == CubeFace.Front ? 2 : 1));

        int[] u1a = new int[6];
        int[] v1a = new int[6];
        int[] u2a = new int[6];
        int[] v2a = new int[6];
        int[] u1b = new int[6];
        int[] v1b = new int[6];
        int[] u2b = new int[6];
        int[] v2b = new int[6];

        convertSize(u1a, v1a, u2a, v2a, top, face, res);
        convertSize(u1b, v1b, u2b, v2b, top, face, res);

        m_faces = mapTexture(img, u1a, v1a, u2a, v2a);
        m_faces2 = mapTexture(img, u1b, v1b, u2b, v2b);

        if (face == CubeFace.Back) {
            m_add = new Vector(0, 0, ConfigProvider.BLOCK_SIZE / 2);
        } else if (face == CubeFace.Left) {
            m_add = new Vector(ConfigProvider.BLOCK_SIZE / 2, 0, 0);
        } else {
            m_add = new Vector();
        }
    }

    private void convertSize(int[] u1, int[] v1, int[] u2, int[] v2,
            boolean top, CubeFace face, int size) {

        int add = (m_top && !top) || (!m_top && top) ? 0 : (size / 2);

        boolean fb = face == CubeFace.Back || face == CubeFace.Front;
        boolean lr = face == CubeFace.Left || face == CubeFace.Right;

        for (int i = 0; i < 2; i++) {
            u1[i] = (lr && top && face == CubeFace.Right ? size / 2 : 0);
            v1[i] = add;
            u2[i] = u1[i] + size / (lr && top ? 2 : 1) - 1;
            v2[i] = v1[i] + size / 2 - 1;
        }
        for (int i = 2; i < 4; i++) {
            u1[i] = (fb && top && face == CubeFace.Back ? size / 2 : 0);
            v1[i] = add;
            u2[i] = u1[i] + size / (fb && top ? 2 : 1) - 1;
            v2[i] = v1[i] + size / 2 - 1;
        }
        for (int i = 4; i < 6; i++) {
            u1[i] = 0;
            v1[i] = 0;
            u2[i] = u1[i] + size / (fb && top ? 2 : 1) - 1;
            v2[i] = v1[i] + size / (lr && top ? 2 : 1) - 1;
        }
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(0, 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        if (m_top) {
            ImageHelper.drawCube(loger, colorMap, position.subtract(m_add),
                    orientation, m_size2, m_faces2, true, OperationType.Block);
            position = position.add(0, ConfigProvider.BLOCK_SIZE / 2, 0);
            ImageHelper.drawCube(loger, colorMap, position, orientation,
                    m_size, m_faces, true, OperationType.Block);
        } else {
            ImageHelper.drawCube(loger, colorMap, position, orientation,
                    m_size, m_faces, true, OperationType.Block);
            position = position.subtract(m_add);
            position = position.add(0, ConfigProvider.BLOCK_SIZE / 2, 0);
            ImageHelper.drawCube(loger, colorMap, position, orientation,
                    m_size2, m_faces2, true, OperationType.Block);
        }
    }
}