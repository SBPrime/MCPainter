/*
 * The MIT License
 *
 * Copyright 2015 prime.
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
package org.PrimeSoft.MCPainter.asyncworldedit;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.utils.FuncParamEx;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;
import org.primesoft.asyncworldedit.worldedit.ThreadSafeEditSession;

/**
 *
 * @author SBPrime
 */
public abstract class DrawingTask implements FuncParamEx<Integer, CancelabeEditSession, MaxChangedBlocksException> {

    private final ThreadSafeEditSession m_editSession;
    protected final LocalSession m_lSession;
    protected final Player m_player;
    protected final LocalPlayer m_localPlayer;

    /**
     * The local session
     *
     * @return
     */
    public LocalSession getLocalSession() {
        return m_lSession;
    }

    /**
     * The edit session
     * @return 
     */
    public ThreadSafeEditSession getEditSession() {
        return m_editSession;
    }

    public DrawingTask(WorldEditPlugin worldEditPlugin, Player player) {
        WorldEdit worldEdit = worldEditPlugin.getWorldEdit();
        
        final AsyncEditSessionFactory factory = (AsyncEditSessionFactory)worldEdit.getEditSessionFactory();
        
        m_player = player;
        m_localPlayer = worldEditPlugin.wrapPlayer(player);
        m_lSession = worldEditPlugin.getSession(player);   
        
        m_editSession = factory.getThreadSafeEditSession(m_localPlayer.getWorld(), 
                m_lSession.getBlockChangeLimit() , m_lSession.getBlockBag(m_localPlayer), m_localPlayer);
    }

    @Override
    public Integer Execute(CancelabeEditSession editSession) throws MaxChangedBlocksException {
        final BlockLoger loger = new BlockLoger(m_player, m_lSession, editSession);

        try {
            draw(loger);
        } catch (MaxChangedBlocksException ex) {
            loger.logMessage("Maximum number of blocks changed, operation canceled.");
        }

        return 0;
    }

    public abstract void draw(BlockLoger blockLoger) throws MaxChangedBlocksException;
}
