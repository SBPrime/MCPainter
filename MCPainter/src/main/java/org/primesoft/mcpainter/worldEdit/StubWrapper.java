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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.primesoft.mcpainter.BlocksHubIntegration;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.blocksplacer.BlockLogerEntry;
import org.primesoft.mcpainter.blocksplacer.BlockPlacer;
import org.primesoft.mcpainter.blocksplacer.IChange;

/**
 *
 * @author SBPrime
 */
class StubWrapper implements IWorldEdit, Listener {

    private static class Entry {

        public List<IChange> ChangeSet;
    }

    private final BlocksHubIntegration m_bh;
    private final BlockPlacer m_bp;
    private final ConcurrentMap<UUID, Entry> m_changeSets = new ConcurrentHashMap<>();

    public StubWrapper(BlocksHubIntegration bh, BlockPlacer bp) {
        m_bh = bh;
        m_bp = bp;
    }

    @Override
    public boolean isRealWorldEdit() {
        return false;
    }

    @Override
    public ILocalSession getSession(Player player) {
        return new StubLocalSession(player, m_bh, this);
    }

    @Override
    public ILocalPlayer wrapPlayer(Player player) {
        return new StubLocalPlayer(player);
    }

    @Override
    public CuboidSelection getSelection(Player player) {
        return null;
    }

    void setChangeset(Player player, final List<IChange> changeSet) {
        m_changeSets.computeIfPresent(player.getUniqueId(), (uuid, entry) -> {
            entry.ChangeSet = changeSet;
            return entry;
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        m_changeSets.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        m_changeSets.put(e.getPlayer().getUniqueId(), new Entry());
    }

    @Override
    public void undo(Player player) {
        m_changeSets.computeIfPresent(player.getUniqueId(), (uuid, entry) -> {            
            List<IChange> changes = entry.ChangeSet;
            entry.ChangeSet = null;

            if (changes == null) {
                return entry;
            }
                        
            BlockLogerEntry[] entries = new BlockLogerEntry[changes.size()];
            int idx = 0;
            for (IChange ch : changes) {
                entries[idx] = new UndoEntry(ch);
                idx++;
            }
            m_bp.addTasks(entries, player);
            return entry;
        });
    }

    private static class UndoEntry extends BlockLogerEntry {

        private final IChange m_change;

        public UndoEntry(IChange ch) {
            super(null);
            m_change = ch;
        }

        @Override
        public boolean canRemove() {
            return true;
        }

        @Override
        public void execute() {
            m_change.undo();
        }
    }
}
