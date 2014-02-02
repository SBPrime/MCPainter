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
package org.PrimeSoft.MCPainter.Drawing.Statue;

import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.Face;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.utils.Vector;

/**
 *
 * @author SBPrime
 */
public abstract class BaseStatue {

    /**
     * Color to block mapper
     */
    private final ColorMap m_colorMap;
    /**
     * Statue start possition
     */
    private final Vector m_pPosition;
    /**
     * Statue orientation
     */
    private final Orientation m_orientation;

    /**
     *
     * @param colorMap Color to block mapper
     * @param position Player position
     * @param yaw Player yaw (used to calculate the start position)
     * @param pitch Player pitch (used to calculate the start position)
     * @param orientation Statue orientation
     * @param size Statue size used to calculate the orientation (the actual
     * size CAN excede this number)
     */
    public BaseStatue(ColorMap colorMap, Vector position, double yaw, double pitch,
            Orientation orientation, Vector size) {
        m_colorMap = colorMap;
        m_orientation = orientation;
        m_pPosition = m_orientation.moveStart(position, yaw, pitch, size);
    }

    /**
     * Get the statue res (height)
     *
     * @return The vertical res
     */
    protected abstract int[] getTextureRes();

    /**
     * Get all the blocks the statue is made of
     *
     * @return
     */
    protected abstract StatueBlock[] getBlocks();

    /**
     * Get the statue cube faces (textures & texture mapping)
     *
     * @return
     */
    protected abstract StatueFace[][] getFaces();

    /**
     * Get the texture file columns locations (in pixels)
     *
     * @return
     */
    protected abstract int[] getColumns();

    /**
     * Get the texture file rows locations (in pixels)
     *
     * @return
     */
    protected abstract int[] getRows();

    /**
     * Draw statue
     *
     * @param loger block placer logger
     * @param rawImg statue texture
     * @param useAlpha use the alpha channel
     */
    public void DrawStatue(BlockLoger loger, RawImage[] rawImg, boolean useAlpha) {
        if (rawImg == null) {
            return;
        }

        int[] res = getTextureRes();
        double[] scale = new double[res.length];
        for (int i = 0; i < res.length; i++) {
            RawImage img = i < rawImg.length ? rawImg[i] : null;
            scale[i] = img == null ? 1 : (img.getRes() / (double) res[i]);
        }

        final StatueBlock[] blocks = getBlocks();
        final StatueFace[][] faces = getFaces();
        int[] cols = getColumns();
        int[] rows = getRows();
        for (int i = 0; i < faces.length; i++) {
            boolean isDiagonal = blocks[i].isDiagonal();
            Vector offset = blocks[i].getOffset();
            Vector size = blocks[i].getSize();
            double[] map = blocks[i].getMap();
            int x = (int) offset.getX();
            int y = (int) offset.getY();
            int z = (int) offset.getZ();
            int dx = m_orientation.calcX(x, y, z);
            int dy = m_orientation.calcY(x, y, z);
            int dz = m_orientation.calcZ(x, y, z);

            StatueFace[] sFaces = faces[i];
            Face[] tex = new Face[sFaces.length];
            for (int j = 0; j < sFaces.length; j++) {
                Face face = null;
                StatueFace sFace = sFaces[j];
                if (sFace != null) {
                    int id = sFace.getTextureId();
                    RawImage img = rawImg[id];
                    double imgScale = scale[id];
                    if (img != null) {
                        face = sFace.getFace(img, cols, rows, imgScale);
                    }
                }

                tex[j] = face;
            }

            if (isDiagonal) {
                ImageHelper.drawDiagonal(loger, m_colorMap, m_pPosition.add(dx, dy, dz),
                        m_orientation, size, tex, null, useAlpha, OperationType.Statue);
            } else {
                ImageHelper.drawCube(loger, m_colorMap, m_pPosition.add(dx, dy, dz),
                        m_orientation, size, tex, map, null, useAlpha, OperationType.Statue);
            }
        }
    }
}
