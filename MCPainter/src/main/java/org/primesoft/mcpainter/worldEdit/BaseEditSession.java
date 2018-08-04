/*
 * The MIT License
 *
 * Copyright 2018 SBPrime.
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

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.primesoft.mcpainter.BlocksHubIntegration;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;

/**
 *
 * @author SBPrime
 */
abstract class BaseEditSession implements IEditSession {
    private final Player m_player;
    private final BlocksHubIntegration m_bh;
    protected final World m_world;
    
    protected BaseEditSession(ILocalPlayer p, BlocksHubIntegration bh) {
        m_bh = bh;
        m_world = p.getWorld();
        m_player = p.getPlayer();
    }
    
    protected void logBlock(Vector location, BaseBlock oldBlock, BaseBlock newBlock) {
        m_bh.logBlock(m_player, m_world, location, oldBlock, newBlock);
    }
}
