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
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.drawing.Face;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.drawing.RawImage;
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
public class Stem extends BaseBlock {

    public final static String NAME = "STEM";
    private Face[] m_stems;
    private int[] m_grayColor;

    public Stem(TextureManager textureManager, ConfigurationSection bp) {
        super(true, false);

        RawImage[] img = BlockHelper.parseTextures(textureManager, bp.getStringList("Textures"));

        initialize(img, 0);
    }


    private void initialize(RawImage[] img, int offset) {
        m_grayColor = new int[]{96, 61};
        m_stems = new Face[7];
        int[] res = new int[img.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = img[i] != null ? img[i].getRes() : 0;
        }

        m_faces = new Face[6];
        Face f = new Face(0, res[offset + 1] - 1, res[offset + 1] - 1, 0, img[offset + 1]);
        f.setGray(true);
        f.setDelta(ConfigProvider.BLOCK_SIZE / 2);
        m_faces[0] = f;

        for (int i = 0; i < 7; i++) {
            f = new Face(0, (i + 1) * 2 - 1, res[offset + 0] - 1, 0, img[offset + 0]);
            f.setGray(true);

            m_stems[i] = f;
        }

        m_size = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();

        if (data > 7) {
            data = 0;
        }

        Orientation orientation = new Orientation(data == 0 ? yaw : 0, 0);
        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        Face[] faces = new Face[6];
        if (data == 0) {
            faces[4] = m_stems[3];
            faces[5] = m_stems[3];
        } else {
            faces[4] = m_stems[data - 1];
            faces[5] = m_stems[data - 1];
        }

        int h = data == 0 ? ConfigProvider.BLOCK_SIZE / 2 : (1 + data) * 2;
        Vector size = new Vector(ConfigProvider.BLOCK_SIZE, h, ConfigProvider.BLOCK_SIZE);

        ImageHelper.drawDiagonal(loger, colorMap, position, orientation,
                size, faces, m_grayColor, true, OperationType.Block);

        if (data == 0) {
            ImageHelper.drawCube(loger, colorMap, position, orientation,
                    m_size, m_faces, m_grayColor, true, OperationType.Block);
        }
    }
}