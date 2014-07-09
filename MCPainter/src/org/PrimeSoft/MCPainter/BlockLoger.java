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
package org.PrimeSoft.MCPainter;

import java.util.ArrayList;
import java.util.List;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.utils.BaseBlock;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.worldEdit.IEditSession;
import org.PrimeSoft.MCPainter.worldEdit.ILocalSession;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public class BlockLoger {

    private final Player m_player;
    private final List<BlockLogerEntry> m_blocks;
    private final ILocalSession m_session;
    private final IEditSession m_editSession;
    private final World m_world;
    private final MCPainterMain m_mainPlugin;
    private final BlockPlacer m_blocksPlacer;

    public World getWorld() {
        return m_world;
    }

    public Player getPlayer() {
        return m_player;
    }

    public BlockLogerEntry[] getEntries() {
        synchronized (this) {
            return m_blocks.toArray(new BlockLogerEntry[0]);
        }
    }

    public IEditSession getEditSession() {
        return m_editSession;
    }

    public ILocalSession getLocalSession() {
        return m_session;
    }

    public BlockLoger(Player player, ILocalSession session, IEditSession eSession,
            MCPainterMain main) {
        m_blocks = new ArrayList<BlockLogerEntry>();
        m_player = player;
        m_session = session;
        m_editSession = eSession;
        m_world = m_player.getWorld();
        m_mainPlugin = main;
        m_blocksPlacer = main.getBlockPlacer();
    }

    public void logCommand(ILoggerCommand command) {
        Location location = command.getLocation();
        if (m_mainPlugin.getBlocksHub().canPlace(m_player.getName(), m_world, location)) {
            synchronized (this) {
                m_blocks.add(new BlockLogerEntry(this, command));
            }
            checkFlush();
        }
    }
    
    public void logBlock(Vector location, BaseBlock block) {
        if (m_mainPlugin.getBlocksHub().canPlace(m_player.getName(), m_world, location)) {
            synchronized (this) {
                m_blocks.add(new BlockLogerEntry(this, location, block));                
            }
            checkFlush();
        }
    }

    public void logEndSession() {
        synchronized (this) {
            m_blocks.add(new BlockLogerEntry(this));
        }
        checkFlush();
    }

    public void logMessage(String msg) {
        synchronized (this) {
            m_blocks.add(new BlockLogerEntry(this, msg));
        }
        checkFlush();
    }

    private void checkFlush() {
        boolean shuldFlush;
        synchronized (this) {
            shuldFlush = m_blocks.size() > ConfigProvider.getQueueHardLimit();
        }
        
        if (shuldFlush) {
            flush();
        }
    }

    public void flush() {
        BlockLogerEntry[] events;
        synchronized (this) {
            events = m_blocks.toArray(new BlockLogerEntry[0]);
            m_blocks.clear();
        }
        m_blocksPlacer.addTasks(events, getPlayer());
    }   
}