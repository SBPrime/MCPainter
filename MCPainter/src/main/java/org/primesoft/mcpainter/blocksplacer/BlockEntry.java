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
package org.primesoft.mcpainter.blocksplacer;

import org.primesoft.mcpainter.MCPainterMain;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;
import org.primesoft.mcpainter.worldEdit.IEditSession;
import org.primesoft.mcpainter.worldEdit.MaxChangedBlocksException;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public class BlockEntry extends BlockLogerEntry {

    private final Vector m_location;
    private final BaseBlock m_newBlock;

    public BlockEntry(BlockLoger loger, Vector location, BaseBlock newBlock) {
        super(loger);

        m_location = location;
        m_newBlock = newBlock;
    }

    @Override
    public void execute(BlockPlacer blockPlacer) {
        Player p = getPlayer();
        IEditSession eSession = m_loger.getEditSession();
        try {
            BaseBlock oldBlock = eSession.getBlock(m_location);
            eSession.setBlock(m_location, m_newBlock);
            blockPlacer.logBlock(m_location, oldBlock, m_newBlock, p, m_loger.getWorld());
        } catch (MaxChangedBlocksException ex) {
            MCPainterMain.say(p, "Max block change reached");
            MCPainterMain.log(ex.getMessage());
        }
    }

    @Override
    public boolean canRemove() {
        return true;
    }

}
