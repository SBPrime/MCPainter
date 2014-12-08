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

import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.Face;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.Texture.VanillaTextureProvider;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;

/**
 *
 * @author SBPrime
 */
public class Shroom extends BaseBlock {
    /**
     * @param texture
     * @param skinId Shroom skin Id (red or brown)
     */
    public Shroom(VanillaTextureProvider texture, int skinId) {
        super(texture.getMushroomBlock(), new int[]{0, skinId, 1});
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, ColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(0, 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        Face[] faces = new Face[6];
        for (int i = 0; i < 6; i++) {
            faces[i] = m_faces[5];
        }

        if (data == 10) {
            for (int i = 0; i < 4; i++) {
                faces[i] = m_faces[i];
            }
        } else if (data == 14) {
            for (int i = 0; i < 6; i++) {
                faces[i] = m_faces[4];
            }
        } else if (data == 15) {
            for (int i = 0; i < 6; i++) {
                faces[i] = m_faces[0];
            }
        } else if (data > 0) {
            final int[][] map = new int[][]{
                new int[]{4, 5, 5, 4},  //1
                new int[]{4, 5, 5, 5},  //2
                new int[]{4, 5, 4, 5},  //3
                new int[]{5, 5, 5, 4},  //4
                new int[]{5, 5, 5, 5},  //5
                new int[]{5, 5, 4, 5},  //6
                new int[]{5, 4, 5, 4},  //7
                new int[]{5, 4, 5, 5},  //8
                new int[]{5, 4, 4, 5},  //9
            };
            for (int i = 0; i < 4; i++) {
                faces[i] = m_faces[map[data - 1][i]];
            }
            faces[4] = m_faces[4];
        }

        ImageHelper.drawCube(loger, colorMap, position, orientation,
                m_size, faces, null, true, OperationType.Block);
    }
}
