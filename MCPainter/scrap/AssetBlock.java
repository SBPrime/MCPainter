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
package org.primesoft.mcpainter.drawing.blocks;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import java.util.HashMap;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.mods.assets.FacingBlock;
import org.primesoft.mcpainter.mods.assets.VariantKey;
import org.primesoft.mcpainter.utils.Orientation;
import org.primesoft.mcpainter.utils.Utils;

/**
 *
 * @author SBPrime
 */
public class AssetBlock implements IDrawableElement {
    /**
     * The variant list
     */
    private final HashMap<VariantKey, FacingBlock> m_variants;
    
    
    public AssetBlock(HashMap<VariantKey, FacingBlock> variants) {
        m_variants = variants;
    }
    
    
    
    @Override
    public void draw(short data, BlockLoger loger, LocalPlayer localPlayer, IColorMap colorMap) 
            throws MaxChangedBlocksException {
        
        BukkitPlayer bPlayer = (BukkitPlayer)localPlayer;
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(yaw, pitch);
        
        Vector oPosition = Utils.getPlayerPos(localPlayer);
        Vector position = orientation.moveStart(oPosition, yaw, pitch);
        
        FacingBlock fb = m_variants.values().iterator().next();
        fb.render(orientation.getFacing(), 0, position, bPlayer.getPlayer(), loger, colorMap, null);
    }
}
