/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
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
package org.primesoft.mcpainter.worldEdit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.primesoft.mcpainter.BlocksHubIntegration;

/**
 *
 * @author SBPrime
 */
class WorldEditWrapper implements IWorldEdit {

    private final WorldEditPlugin m_worldEdit;
    private final BlocksHubIntegration m_bh;

    WorldEditWrapper(Plugin wePlugin, BlocksHubIntegration bh) {
        m_bh = bh;
        m_worldEdit = (WorldEditPlugin) wePlugin;
    }

    @Override
    public boolean isRealWorldEdit() {
        return true;
    }

    @Override
    public ILocalSession getSession(Player player) {
        LocalSession lSession = m_worldEdit.getSession(player);
        return new WorldEditLocalSession(lSession, m_bh);
    }

    @Override
    public ILocalPlayer wrapPlayer(Player player) {
        com.sk89q.worldedit.entity.Player lPlayer = m_worldEdit.wrapPlayer(player);
        return new WorldEditLocalPlayer(lPlayer, player);
    }

    @Override
    public CuboidSelection getSelection(Player player) {
        Selection selection = m_worldEdit.getSelection(player);
        Location pMin = selection.getMinimumPoint();
        Location pMax = selection.getMaximumPoint();        
        
        return new CuboidSelection(pMin, pMax);
    }

    static com.sk89q.worldedit.Vector convert(Vector v) {
        return new com.sk89q.worldedit.Vector(v.getX(), v.getY(), v.getZ());
    }

    static Vector convert(com.sk89q.worldedit.Vector v) {
        return new Vector(v.getX(), v.getY(), v.getZ());
    }

    static com.sk89q.worldedit.blocks.BaseBlock convert(BaseBlock v) {
        throw new UnsupportedOperationException("Not supported yet. Need to port to 1.13");     //TODO: 1.13
        //return new com.sk89q.worldedit.blocks.BaseBlock(v.getType(), v.getData());
    }

    static BaseBlock convert(com.sk89q.worldedit.blocks.BaseBlock v) {
        throw new UnsupportedOperationException("Not supported yet. Need to port to 1.13");     //TODO: 1.13
        //return null;//TODO: Implement me! new BaseBlock(Material.getMaterial(v.getType()), v.getData());
    }
}
