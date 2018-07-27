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
package org.primesoft.mcpainter.asyncworldedit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.api.IAsyncWorldEdit;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacer;
import org.primesoft.asyncworldedit.api.playerManager.IPlayerManager;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;
import org.primesoft.asyncworldedit.playerManager.PlayerManager;
import org.primesoft.asyncworldedit.worldedit.ThreadSafeEditSession;

/**
 *
 * @author SBPrime
 */
public class AWEWrapper {

    /**
     * Get instance of AWE plugin
     *
     * @return
     */
    private static AsyncWorldEditMain getAsyncWorldEdit(Plugin plugin) {
        try {
            Plugin wPlugin = plugin.getServer().getPluginManager().getPlugin("AsyncWorldEdit");

            if ((wPlugin == null) || (!(wPlugin instanceof AsyncWorldEditMain))) {
                return null;
            }

            return (AsyncWorldEditMain) wPlugin;
        } catch (NoClassDefFoundError ex) {
            return null;
        }
    }

    /**
     * Get AWE wrapper
     *
     * @param plugin
     * @return
     */
    public static AWEWrapper getWrapper(JavaPlugin plugin) {
        AsyncWorldEditMain aweMain = getAsyncWorldEdit(plugin);

        if (aweMain == null) {
            return null;
        }

        return new AWEWrapper(aweMain.getAPI());
    }

    private final IBlockPlacer m_blockPlacer;
    private final IPlayerManager m_playerManager;

    private AWEWrapper(IAsyncWorldEdit awe) {
        m_blockPlacer = awe.getBlockPlacer();
        m_playerManager = awe.getPlayerManager();
    }

    public void runTask(Player player, String jobName, DrawingTask task) {
        final PlayerEntry playerEntry = m_playerManager.getPlayer(player);
        final ThreadSafeEditSession editSession = task.getEditSession();

        m_blockPlacer.performAsAsyncJob(editSession, playerEntry, jobName, task);
        
        task.getLocalSession().remember(editSession);
    }
}
