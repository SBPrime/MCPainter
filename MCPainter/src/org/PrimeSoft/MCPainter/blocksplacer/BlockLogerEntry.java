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
package org.PrimeSoft.MCPainter.blocksplacer;

import org.PrimeSoft.MCPainter.utils.BaseBlock;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.bukkit.entity.Player;


/**
 *
 * @author SBPrime
 */
public abstract class BlockLogerEntry {
    protected final BlockLoger m_loger;

    public abstract boolean canRemove();
    
    public BlockLoger getLoger() {
        return m_loger;
    }
    
    public Player getPlayer() {
        return m_loger.getPlayer();
    }

    public BlockLogerEntry(BlockLoger loger) {
        m_loger = loger;
    }
    
    /**
     * Execute the command
     * @param blockPlacer
     */
    public abstract void execute(BlockPlacer blockPlacer);
}
