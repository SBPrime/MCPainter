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

import java.util.HashMap;
import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Multiple blocks container block. This class allows simple operation to blocks
 * that have data values
 *
 * @author SBPrime
 */
public class MultiBlock implements IDrawableElement {
    public final static String NAME = "MULTI";
    
    
    /**
     * Data values to blocks map
     */
    private HashMap<Short, IDrawableElement> m_blocks;
    /**
     * Maximum data value
     */
    private short m_maxValue;
    /**
     * The top half binary mask for data value
     */
    private short m_topFlag;
    /**
     * Automaticly set the data value for upper half of the block when player
     * looks up?
     */
    private boolean m_useTopFlag;

    /**
     * Automaticly build basic, cube blocks from texture entries
     *
     * @param textureManager
     * @param bp
     */
    public MultiBlock(TextureManager textureManager, ConfigurationSection bp) {
        m_blocks = new HashMap<Short, IDrawableElement>();
        RawImage[] img = BlockHelper.parseTextures(textureManager, bp.getStringList("Textures"));
        int[] ids = BlockHelper.parseIntListEntry(bp.getIntegerList("Ids"));
        int cnt = Math.min(img.length, 256);
        if (ids.length > 0) {
            cnt = Math.min(cnt, ids.length);
        } else {
            ids = new int[cnt];
            for (int i = 0; i < cnt; i++) {
                ids[i] = i;
            }
        }

        for (int i = 0; i < cnt; i++) {
            m_blocks.put((short) ids[i], new BaseBlock(img[i]));
        }
        m_maxValue = (short) (m_blocks.size() - 1);
        m_useTopFlag = false;
    }

    /**
     *
     * @param blocks List of block variants
     * @param data Block variants data values
     * @param useTopFlag Automaticly set data value for top half of the block
     * @param topFlag The top half data value
     * @param maxValue
     */
    public MultiBlock(IDrawableElement[] blocks, short[] data, boolean useTopFlag, short topFlag,
            short maxValue) {
        m_blocks = new HashMap<Short, IDrawableElement>();
        for (int i = 0; i < Math.min(blocks.length, data.length); i++) {
            m_blocks.put(data[i], blocks[i]);
        }
        m_maxValue = maxValue;
        m_useTopFlag = useTopFlag;
        m_topFlag = topFlag;
    }

    /**
     * Map data value to block data
     *
     * @param data
     * @return
     */
    protected short MapData(short data) {
        if (m_maxValue == 0) {
            return data;
        }
        return (short) (data % m_maxValue);
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, ColorMap colorMap) {
        if (m_useTopFlag) {
            double pitch = localPlayer.getPitch();
            if (pitch < -45) {
                data |= m_topFlag;
            }
        }

        if (!m_blocks.containsKey(data)) {
            data = MapData(data);

            if (!m_blocks.containsKey(data)) {
                MCPainterMain.log("Error drawing multiblock, data value " + data + " not found.");
                loger.logMessage("Error drawing multiblock, data value " + data + " not found.");
                return;
            }
        }
        m_blocks.get(data).draw(data, loger, localPlayer, colorMap);
    }
}
