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

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
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
        LocalSession session = m_worldEdit.getSession(player);
        final com.sk89q.worldedit.world.World sWorld = session.getSelectionWorld();
        if (sWorld == null) {
            return null;
        }

        Region selection = null;
        try {
            selection = session.getSelection(sWorld);
        } catch (IncompleteRegionException ex) {
            //Simply ignore me
        }

        if (selection == null) {
            return null;
        }

        com.sk89q.worldedit.Vector pMin = selection.getMinimumPoint();
        com.sk89q.worldedit.Vector pMax = selection.getMaximumPoint();

        Server s = Bukkit.getServer();
        World w = s.getWorld(sWorld.getName());

        return new CuboidSelection(w,
                new Vector(pMin.getX(), pMin.getY(), pMin.getZ()),
                new Vector(pMax.getX(), pMax.getY(), pMax.getZ())
        );
    }

    static com.sk89q.worldedit.Vector convert(Vector v) {
        return new com.sk89q.worldedit.Vector(v.getX(), v.getY(), v.getZ());
    }

    static Vector convert(com.sk89q.worldedit.Vector v) {
        return new Vector(v.getX(), v.getY(), v.getZ());
    }

    static com.sk89q.worldedit.world.block.BlockState convert(BaseBlock v) {
        return BukkitAdapter.adapt(v.Data);
    }

    static BaseBlock convert(com.sk89q.worldedit.world.block.BlockState v) {
        return new BaseBlock(BukkitAdapter.adapt(v));
    }

    @Override
    public void undo(Player player) {
        com.sk89q.worldedit.entity.Player lPlayer = m_worldEdit.wrapPlayer(player);
        m_worldEdit.getSession(player).undo(null, lPlayer);

    }
}
