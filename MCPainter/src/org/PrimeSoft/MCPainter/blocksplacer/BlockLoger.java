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
package org.PrimeSoft.MCPainter.blocksplacer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;

/**
 *
 * @author SBPrime
 */
public class BlockLoger {

    /**
     * The MTA mutex
     */
    private final Object m_mutex = new Object();

    private final Player m_player;
    private final LocalSession m_session;
    private final CancelabeEditSession m_editSession;
    private final World m_world;

    public Player getPlayer() {
        return m_player;
    }


    public EditSession getEditSession() {
        return m_editSession;
    }

    public LocalSession getLocalSession() {
        return m_session;
    }

    public BlockLoger(Player player, LocalSession session, CancelabeEditSession eSession) {
        m_player = player;
        m_session = session;
        m_editSession = eSession;
        m_world = m_player.getWorld();
    }

    public void logCommand(ILoggerCommand command) {
        //Location location = command.getLocation();
        //synchronized (m_mutex) {
        //    m_blocks.add(new CommandEntry(this, command));
        //}
        //checkFlush();
        
        //TODO: Implement me!
    }

    public void logBlock(Vector location, BaseBlock block) throws MaxChangedBlocksException {        
        m_editSession.setBlock(location, block);        
    }

    public void logMessage(String msg) {
        MCPainterMain.say(getPlayer(), msg);
    }
}
