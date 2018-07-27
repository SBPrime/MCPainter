/*
 * The MIT License
 *
 * Copyright 2015 SBPrime.
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
package org.primesoft.mcpainter.mods.assets;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.voxelyzer.ClippingRegion;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public class FacingBlock {
    private final HashMap<BlockFace, VariantBlock[]> m_blocks;

    FacingBlock(HashMap<BlockFace, List<VariantBlock>> entries) {
        m_blocks = new HashMap<BlockFace, VariantBlock[]>();
        for (Map.Entry<BlockFace, List<VariantBlock>> entrySet : entries.entrySet()) {
            BlockFace key = entrySet.getKey();
            List<VariantBlock> value = entrySet.getValue();
            
            m_blocks.put(key, value.toArray(new VariantBlock[0]));
        }
    }
    
    public void render(BlockFace facing, int number,
            Vector origin, Player player,
            BlockLoger loger, IColorMap colorMap, ClippingRegion clipping)
            throws MaxChangedBlocksException {
        if (!m_blocks.containsKey(facing)) {
            facing = BlockFace.SELF;
        }
        
        if (!m_blocks.containsKey(facing)) {
            return;
        }
        
        VariantBlock[] entry = m_blocks.get(facing);
        if (entry.length == 0) {
            return;
        }
        
        entry[number % entry.length].render(origin, player, loger, colorMap, clipping);
    }
}
