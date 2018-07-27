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

import java.util.List;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.drawing.CubeFace;
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
public class Diagonal extends BaseBlock {

    public final static String NAME = "DIAGONAL";
    private int[] m_grayColor;

    public Diagonal(TextureManager textureManager, ConfigurationSection bp) {
        super(textureManager, bp);

        int[] grayColor = BlockHelper.parseIntListEntry(bp.getIntegerList("Color"));
        if (grayColor.length > 1) {
            m_grayColor = grayColor;
        }

        List<String> facesList = bp.getStringList("Faces");
        boolean[] use = new boolean[6];
        if (facesList == null || facesList.isEmpty()) {
            use[4] = true;
            use[5] = true;
        }
        else {
            for (String string : facesList) {
                switch (CubeFace.valueOf(string)) {
                    case Back:
                        use[0] = true;
                        break;                    
                    case Front:
                        use[1] = true;
                        break;
                    case Left:
                        use[2] = true;
                        break;
                    case Right:
                        use[3] = true;
                        break;
                    case Top:
                        use[4] = true;
                        break;
                    case Bottom:
                        use[5] = true;
                        break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (!use[i]) {
                m_faces[i] = null;
            }
        }

        if (m_grayColor != null) {
            for (int i = 0; i < 6; i++) {
                if (m_faces[i] != null) {
                    m_faces[i].setGray(true);
                }
            }
        }
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(m_useYaw ? yaw : 0, m_usePitch ? pitch : 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        ImageHelper.drawDiagonal(loger, colorMap, position, orientation,
                m_size, m_faces, m_grayColor, true, OperationType.Block);
    }
}
