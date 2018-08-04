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
package org.primesoft.mcpainter.blocksplacer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.MCPainterMain;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author SBPrime
 */
public class BlockPlacer implements Runnable {
    /**
     * The MTA mutex
     */
    private final Object m_mutex = new Object();

    /**
     * Bukkit scheduler
     */
    private final BukkitScheduler m_scheduler;
    /**
     * Current scheduler task
     */
    private final BukkitTask m_task;
    /**
     * Logged events queue (per player)
     */
    private final HashMap<String, Queue<BlockLogerEntry>> m_blocks;
    /**
     * Should block places shut down
     */
    private boolean m_shutdown;
    /**
     * queue soft size
     */
    private final int m_queueSoft;
    /**
     * queue hard size
     */
    private final int m_queueHard;

    /**
     * Initialize new instance of the block placer
     *
     * @param plugin parent
     */
    public BlockPlacer(MCPainterMain plugin) {
        m_blocks = new HashMap<>();
        m_scheduler = plugin.getServer().getScheduler();
        m_queueHard = ConfigProvider.getQueueHardLimit();
        m_queueSoft = ConfigProvider.getQueueSoftLimit();
        m_task = m_scheduler.runTaskTimer(plugin, this,
                ConfigProvider.getInterval(), ConfigProvider.getInterval());
    }

    /**
     * Block placer main loop
     */
    @Override
    public void run() {
        List<BlockLogerEntry> entries = new ArrayList<BlockLogerEntry>(ConfigProvider.getBlockCount());
        synchronized (m_mutex) {
            String[] keys = m_blocks.keySet().toArray(new String[0]);
            int keyPos = 0;
            boolean added = keys.length > 0;
            final int blockCnt = ConfigProvider.getBlockCount();
            for (int i = 0; i < blockCnt && added; i++) {
                added = false;

                Queue<BlockLogerEntry> queue = m_blocks.get(keys[keyPos]);
                if (queue != null) {
                    if (!queue.isEmpty()) {
                        entries.add(queue.poll());
                        added = true;
                    }
                    if (queue.isEmpty()) {
                        m_blocks.remove(keys[keyPos]);
                    }
                }
                keyPos = (keyPos + 1) % keys.length;
            }

            if (!added && m_shutdown) {
                stop();
            }
        }

        for (BlockLogerEntry entry : entries) {
            process(entry);
        }
    }

    /**
     * Queue stop command
     */
    public void queueStop() {
        m_shutdown = true;
    }

    /**
     * Stop block logger
     */
    public void stop() {
        m_task.cancel();
    }

    /**
     * Add task to perform in async mode
     *
     * @param events Event to log
     * @param player
     */
    public void addTasks(BlockLogerEntry[] events, Player player) {
        Queue<BlockLogerEntry> queue = null;
        final String name = player.getName();
        synchronized (m_mutex) {
            for (BlockLogerEntry entry : events) {
                if (queue == null) {
                    if (!m_blocks.containsKey(name)) {
                        queue = new ArrayDeque<BlockLogerEntry>();
                        m_blocks.put(name, queue);
                    } else {
                        queue = m_blocks.get(name);
                    }
                }
                queue.add(entry);
            }
        }

        if (queue.size() > m_queueHard) {
            MCPainterMain.say(player, "Queue size limit reached. Block placing postponed...");
            while (queue.size() > m_queueSoft && !m_shutdown) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            MCPainterMain.say(player, "Block placing resumed.");
        }
    }

    /**
     * Remove all entries for player
     *
     * @param player
     */
    public void purge(String player) {
        synchronized (m_mutex) {
            if (m_blocks.containsKey(player)) {
                BlockLogerEntry[] entries = m_blocks.get(player).toArray(new BlockLogerEntry[0]);
                m_blocks.remove(player);

                Queue<BlockLogerEntry> queue = new ArrayDeque<BlockLogerEntry>();
                for (BlockLogerEntry e : entries) {
                    if (!e.canRemove()) {
                        queue.add(e);
                    }
                }

                if (!queue.isEmpty()) {
                    m_blocks.put(player, queue);
                }
            }
        }
    }

    /**
     * Remove all entries
     */
    public void purgeAll() {
        synchronized (m_mutex) {
            for (String user : getAllPlayers()) {
                purge(user);
            }
        }
    }

    /**
     * Get all players in log
     *
     * @return players list
     */
    public String[] getAllPlayers() {
        synchronized (m_mutex) {
            return m_blocks.keySet().toArray(new String[0]);
        }
    }

    /**
     * Gets the number of events for a player
     *
     * @param player player login
     * @return number of stored events
     */
    public int getPlayerEvents(String player) {
        synchronized (m_mutex) {
            if (m_blocks.containsKey(player)) {
                return m_blocks.get(player).size();
            }

            return 0;
        }
    }

    /**
     * Process logged event
     *
     * @param entry event to process
     */
    private void process(BlockLogerEntry entry) {
        if (entry == null) {
            return;
        }
        entry.execute();
    }
}
