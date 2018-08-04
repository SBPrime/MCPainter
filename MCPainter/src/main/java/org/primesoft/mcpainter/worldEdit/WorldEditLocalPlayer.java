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

import com.sk89q.worldedit.LocalPlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.primesoft.mcpainter.utils.Vector;

/**
 *
 * @author SBPrime
 */
public class WorldEditLocalPlayer implements ILocalPlayer {
    private final com.sk89q.worldedit.entity.Player m_localPlayer;
    private final Player m_player;
        
    public WorldEditLocalPlayer(com.sk89q.worldedit.entity.Player localPlayer, Player player) {
        m_localPlayer = localPlayer;
        m_player = player;
    }
    
    public com.sk89q.worldedit.entity.Player getLocalPlayer(){
        return m_localPlayer;
    }
    
    @Override
    public Vector getPosition() {
        return WorldEditWrapper.convert(m_localPlayer.getPosition());
    }

    @Override
    public double getYaw() {
        return m_localPlayer.getYaw();
    }

    @Override
    public double getPitch() {
        return m_localPlayer.getPitch();
    }

    @Override
    public World getWorld() {
        return m_player.getWorld();
    }

    @Override
    public Player getPlayer() {
        return m_player;
    }
}
