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
package org.PrimeSoft.MCPainter.Drawing.Blocks;

import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.IColorMap;
import org.PrimeSoft.MCPainter.Drawing.Face;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.Texture.TextureEntry;
import org.PrimeSoft.MCPainter.Texture.VanillaTextureProvider;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.utils.Vector2D;
import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;

/**
 *
 * @author SBPrime
 */
public class PistonExtension extends BaseBlock {

    private Face[] m_side;
    private Face[] m_tops;
    private Face m_bottom;

    public PistonExtension(VanillaTextureProvider textureProvider) {
        super(false, false);

        m_faces = null;

        TextureEntry texture = textureProvider.getPiston();
        if (texture == null) {
            return;
        }

        RawImage[] tex = texture.getImages();
        int[] res = new int[tex.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = tex[i].getRes();
        }

        m_tops = new Face[]{
            new Face(0, 0, res[2] - 1, res[2] - 1, tex[2]),
            new Face(0, 0, res[3] - 1, res[3] - 1, tex[3])
        };

        m_bottom = new Face(0, 0, res[2] - 1, res[2] - 1, tex[2]);
        initializeSides(res[0], tex[0]);
    }

    private void drawPlate(int data, Orientation orientation, BlockLoger loger,
            IColorMap colorMap, Vector position) {
        Face[] faces = null;

        int topId = (data & 0x8) == 0x8 ? 1 : 0;
        int w = ConfigProvider.BLOCK_SIZE;
        int h = ConfigProvider.BLOCK_SIZE;
        int d = ConfigProvider.BLOCK_SIZE;
        int x = 0;
        int y = 0;
        int z = 0;

        switch (data & 0x7) {
            case 0:
                faces = new Face[]{m_side[0], m_side[0], m_side[0], m_side[0], m_bottom, m_tops[topId]};
                h = 4;
                break;
            case 1:
                faces = new Face[]{m_side[0], m_side[0], m_side[0], m_side[0], m_tops[topId], m_bottom};
                h = 4;
                y = 12;
                break;
            case 2:
                faces = new Face[]{m_tops[topId], m_bottom, m_side[1], m_side[1], m_side[0], m_side[0]};
                d = 4;
                z = 12;
                break;
            case 3:
                faces = new Face[]{m_bottom, m_tops[topId], m_side[1], m_side[1], m_side[0], m_side[0]};
                d = 4;
                break;
            case 4:
                faces = new Face[]{m_side[1], m_side[1], m_bottom, m_tops[topId], m_side[1], m_side[1]};
                w = 4;
                x = 12;
                break;
            case 5:
                faces = new Face[]{m_side[1], m_side[1], m_tops[topId], m_bottom, m_side[1], m_side[1]};
                w = 4;
                break;
        }

        int dx = orientation.calcX(x, y, z);
        int dy = orientation.calcY(x, y, z);
        int dz = orientation.calcZ(x, y, z);

        Vector size = new Vector(w, h, d);

        ImageHelper.drawCube(loger, colorMap, position.add(dx, dy, dz), orientation, size,
                faces, true, OperationType.Block);
    }

    private void drawRot(int data, Orientation orientation, BlockLoger loger,
            IColorMap colorMap, Vector position) {
        Face[] faces = null;

        int topId = (data & 0x8) == 0x8 ? 1 : 0;
        int w = 4;
        int h = 4;
        int d = 4;
        int x = 6;
        int y = 6;
        int z = 6;

        switch (data & 0x7) {
            case 0:
                faces = new Face[]{m_side[1], m_side[1], m_side[1], m_side[1], null, null};
                h = ConfigProvider.BLOCK_SIZE;
                y = 4;
                break;
            case 1:
                faces = new Face[]{m_side[1], m_side[1], m_side[1], m_side[1], null, null};
                h = ConfigProvider.BLOCK_SIZE;
                y = -4;
                break;
            case 2:
                faces = new Face[]{null, null, m_side[0], m_side[0], m_side[1], m_side[1]};
                d = ConfigProvider.BLOCK_SIZE;
                z = -4;
                break;
            case 3:
                faces = new Face[]{null, null, m_side[0], m_side[0], m_side[1], m_side[1]};
                d = ConfigProvider.BLOCK_SIZE;
                z = 4;
                break;
            case 4:
                faces = new Face[]{m_side[0], m_side[0], null, null, m_side[0], m_side[0]};
                w = ConfigProvider.BLOCK_SIZE;
                x = -4;
                break;
            case 5:
                faces = new Face[]{m_side[0], m_side[0], null, null, m_side[0], m_side[0]};
                w = ConfigProvider.BLOCK_SIZE;
                x = 4;
                break;
        }

        int dx = orientation.calcX(x, y, z);
        int dy = orientation.calcY(x, y, z);
        int dz = orientation.calcZ(x, y, z);

        Vector size = new Vector(w, h, d);

        ImageHelper.drawCube(loger, colorMap, position.add(dx, dy, dz), orientation, size,
                faces, true, OperationType.Block);
    }

    /**
     * Initialize the sides
     *
     * @param res
     * @param tex
     */
    private void initializeSides(int res, RawImage tex) {
        final int h = res / 4;
        final int[][] p = new int[][]{
            new int[]{0, 1, 2, 3},
            new int[]{2, 0, 3, 1}
        };
        m_side = new Face[2];
        for (int i = 0; i < 2; i++) {
            Vector2D v[] = new Vector2D[4];
            for (int j = 0; j < 4; j++) {
                switch (p[i][j]) {
                    case 0:
                        v[j] = new Vector2D(0, 0);
                        break;
                    case 1:
                        v[j] = new Vector2D(res - 1, 0);
                        break;
                    case 2:
                        v[j] = new Vector2D(0, h - 1);
                        break;
                    case 3:
                        v[j] = new Vector2D(res - 1, h - 1);
                        break;
                }
            }

            m_side[i] = new Face(v[0], v[1], v[2], v[3], tex);
        }
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(0, 0);
        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        drawPlate(data, orientation, loger, colorMap, position);
        drawRot(data, orientation, loger, colorMap, position);
    }
}
