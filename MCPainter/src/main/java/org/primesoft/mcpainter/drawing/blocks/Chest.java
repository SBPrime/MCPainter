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
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.drawing.Face;
import org.primesoft.mcpainter.drawing.IColorMap;
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
public class Chest extends BaseBlock {

    public static final String NAME = "CHEST";
    private Face[] m_faces2;
    private Face[] m_faces3;
    private Vector m_size2;
    private Vector m_size3;
    private boolean m_isLarge;

    public Chest(TextureManager textureManager, ConfigurationSection bp) {
        super(true, false);
        m_faces = new Face[6];
        m_faces2 = new Face[6];
        m_faces3 = new Face[6];

        boolean large = bp.getBoolean("IsLarge", false);
        RawImage tex = BlockHelper.parseTexture(textureManager, bp.getString("Texture"));
        initialize(large, tex);

    }

    private void initialize(boolean large, RawImage tex) {
        m_isLarge = large;
        double scale = tex.getRes() / 16;

        m_faces = new Face[6];
        m_faces2 = new Face[6];
        m_faces3 = new Face[6];

        if (m_isLarge) {
            initializeLarge(tex, scale);
        } else {
            initializeSmall(tex, scale);
        }


        m_faces3[0] = new Face((int) (1 * scale + 0.5), (int) (5 * scale + 0.5) - 1,
                (int) (3 * scale + 0.5) - 1, (int) (1 * scale + 0.5), tex);
        m_size3 = new Vector(2, 4, 1);
    }

    /**
     * Initialize faces for small chest
     *
     * @param tex
     * @param scale
     */
    private void initializeSmall(RawImage tex, double scale) {
        final int[] ROWS = new int[]{0, 14, 19, 33, 43};
        final int[] COLS = new int[]{0, 14, 28, 42, 56};

        int[] row = new int[ROWS.length];
        int[] col = new int[COLS.length];
        for (int i = 0; i < row.length; i++) {
            row[i] = (int) (ROWS[i] * scale + 0.5);
        }
        for (int i = 0; i < col.length; i++) {
            col[i] = (int) (COLS[i] * scale + 0.5);
        }

        m_faces = new Face[]{
            new Face(col[1], row[4] - 1, col[2] - 1, row[3] - 0, tex),
            new Face(col[3], row[4] - 1, col[4] - 1, row[3] - 0, tex),
            new Face(col[0], row[4] - 1, col[1] - 1, row[3] - 0, tex),
            new Face(col[2], row[4] - 1, col[3] - 1, row[3] - 0, tex),
            new Face(col[1], row[2] - 0, col[2] - 1, row[3] - 1, tex),
            new Face(col[2], row[2] - 0, col[3] - 1, row[3] - 1, tex)
        };

        m_size = new Vector(14, 10, 14);

        m_faces2 = new Face[]{
            new Face(col[1], row[2] - 1, col[2] - 1, row[1] - 0, tex),
            new Face(col[3], row[2] - 1, col[4] - 1, row[1] - 0, tex),
            new Face(col[0], row[2] - 1, col[1] - 1, row[1] - 0, tex),
            new Face(col[2], row[2] - 1, col[3] - 1, row[1] - 0, tex),
            new Face(col[1], row[0] - 0, col[2] - 1, row[1] - 1, tex),
            new Face(col[2], row[0] - 0, col[3] - 1, row[1] - 1, tex)
        };

        m_size2 = new Vector(14, 5, 14);
    }

    /**
     * Initialize faces for large chest
     *
     * @param tex
     * @param scale
     */
    private void initializeLarge(RawImage tex, double scale) {
        final int[] ROWS = new int[]{0, 14, 19, 33, 43};
        final int[] COLS = new int[]{0, 14, 44, 58, 74, 88};

        int[] row = new int[ROWS.length];
        int[] col = new int[COLS.length];
        for (int i = 0; i < row.length; i++) {
            row[i] = (int) (ROWS[i] * scale + 0.5);
        }
        for (int i = 0; i < col.length; i++) {
            col[i] = (int) (COLS[i] * scale + 0.5);
        }

        m_faces = new Face[]{
            new Face(col[1], row[4] - 1, col[2] - 1, row[3] - 0, tex),
            new Face(col[3], row[4] - 1, col[5] - 1, row[3] - 0, tex),
            new Face(col[0], row[4] - 1, col[1] - 1, row[3] - 0, tex),
            new Face(col[2], row[4] - 1, col[3] - 1, row[3] - 0, tex),
            new Face(col[1], row[2] - 0, col[2] - 1, row[3] - 1, tex),
            new Face(col[2], row[2] - 0, col[4] - 1, row[3] - 1, tex)
        };

        m_size = new Vector(30, 10, 14);

        m_faces2 = new Face[]{
            new Face(col[1], row[2] - 1, col[2] - 1, row[1] - 0, tex),
            new Face(col[3], row[2] - 1, col[5] - 1, row[1] - 0, tex),
            new Face(col[0], row[2] - 1, col[1] - 1, row[1] - 0, tex),
            new Face(col[2], row[2] - 1, col[3] - 1, row[1] - 0, tex),
            new Face(col[1], row[0] - 0, col[2] - 1, row[1] - 1, tex),
            new Face(col[2], row[0] - 0, col[4] - 1, row[1] - 1, tex)
        };

        m_size2 = new Vector(30, 5, 14);
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(yaw, 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch,
                m_size.getBlockX(), m_size.getBlockY(), m_size.getBlockZ());

        int px = orientation.calcX(1, 0, -1);
        int py = orientation.calcY(1, 0, -1);
        int pz = orientation.calcZ(1, 0, -1);
        position = position.add(px, py, pz);

        int dx = orientation.calcX(0, 9, 0);
        int dy = orientation.calcY(0, 9, 0);
        int dz = orientation.calcZ(0, 9, 0);

        int move = m_isLarge ? 14 : 6;

        int dx2 = orientation.calcX(move, 7, 14);
        int dy2 = orientation.calcY(move, 7, 14);
        int dz2 = orientation.calcZ(move, 7, 14);

        ImageHelper.drawCube(loger, colorMap, position, orientation,
                m_size, m_faces, true, OperationType.Block);

        ImageHelper.drawCube(loger, colorMap, position.add(dx, dy, dz),
                orientation, m_size2, m_faces2, true, OperationType.Block);

        ImageHelper.drawCube(loger, colorMap, position.add(dx2, dy2, dz2),
                orientation, m_size3, m_faces3, true, OperationType.Block);

    }
}
