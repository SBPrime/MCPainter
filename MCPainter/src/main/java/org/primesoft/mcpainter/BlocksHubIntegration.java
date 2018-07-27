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
package org.primesoft.mcpainter;

import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.blockshub.IBlocksHubApi;
import org.primesoft.blockshub.IBlocksHubApiProvider;
import org.primesoft.blockshub.api.platform.BukkitBlockData;

/**
 *
 * @author SBPrime
 */
public class BlocksHubIntegration {

    private final boolean m_isInitialized;
    private final IBlocksHubApi m_blocksApi;

    /**
     * Get instance of the core blocks hub plugin
     *
     * @param plugin
     * @return
     */
    public static IBlocksHubApiProvider getBlocksHub(JavaPlugin plugin) {
        try {
            Plugin cPlugin = plugin.getServer().getPluginManager().getPlugin("BlocksHub");

            if ((cPlugin == null) || (!(cPlugin instanceof IBlocksHubApiProvider))) {
                return null;
            }

            return (IBlocksHubApiProvider) cPlugin;
        } catch (NoClassDefFoundError ex) {
            return null;
        }
    }

    public BlocksHubIntegration(JavaPlugin plugin) {
        IBlocksHubApiProvider bh = getBlocksHub(plugin);
        m_blocksApi = bh != null ? bh.getApi() : null;
        m_isInitialized = m_blocksApi != null && m_blocksApi.getVersion() >= 1.0;
    }

    public boolean canPlace(Player player, World world, Vector location) {
        return canPlace(player, world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    public boolean canPlace(Player player, World world, Location location) {
        return canPlace(player, world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean canPlace(Player player, World world, double x, double y, double z) {
        if (!m_isInitialized || !ConfigProvider.getCheckAccess()) {
            return true;
        }

        return m_blocksApi.hasAccess(player.getUniqueId(), world.getUID(),
                x, y, z);
    }

    public void logBlock(Player player, World world, Vector location, BaseBlock oldBlock, BaseBlock newBlock) {
        if (location == null || !ConfigProvider.getLogBlocks()) {
            return;
        }

        if (oldBlock == null) {
            oldBlock = new BaseBlock(Material.AIR);
        }
        if (newBlock == null) {
            newBlock = new BaseBlock(Material.AIR);
        }

        throw new UnsupportedOperationException("Not supported yet. Need to port to 1.13");  //TODO: 1.13
        /*m_blocksApi.logBlock(                
                player.getUniqueId(), world.getUID(),
                location.getX(), location.getY(), location.getZ(),
                new BukkitBlockData(null),  //TODO: Implement proper block data
                new BukkitBlockData(null));*/
    }
}
