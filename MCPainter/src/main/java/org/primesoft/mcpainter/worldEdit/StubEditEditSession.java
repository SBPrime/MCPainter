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

import java.util.ArrayList;
import java.util.List;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.primesoft.mcpainter.BlocksHubIntegration;
import org.primesoft.mcpainter.blocksplacer.IChange;

/**
 *
 * @author SBPrime
 */
class StubEditEditSession extends BaseEditSession {
    private final List<IChange> m_changeSet= new ArrayList<>();
    
    public StubEditEditSession(ILocalPlayer localPlayer, BlocksHubIntegration bh) {
        super(localPlayer, bh);
    }

    @Override
    public BaseBlock getBlock(Vector location) {
        Location l = new Location(m_world, location.getX(), location.getY(), location.getZ());
        Chunk chunk = l.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        Block b = l.getBlock();

        return new BaseBlock(b.getBlockData().clone());
    }

    @Override
    public void setBlock(Vector location, BaseBlock block) throws MaxChangedBlocksException {
        Location l = new Location(m_world, location.getX(), location.getY(), location.getZ());
        Chunk chunk = l.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }

        Block b = l.getBlock();
        BaseBlock oldBlock = new BaseBlock(b.getBlockData().clone());
        b.setBlockData(block.Data);

        IChange setBlockUndo = new UndoSetBlock(l, oldBlock);
        logBlock(location, oldBlock, block);        
        m_changeSet.add(setBlockUndo);
    }

    @Override
    public void doCustom(IChange command) throws MaxChangedBlocksException {
        command.redo();
        
        m_changeSet.add(command);
    }

    @Override
    public List<IChange> getChangeSet() {
        return m_changeSet;
    }

    private static class UndoSetBlock implements IChange {

        private final Location m_location;
        private final BaseBlock m_block;

        public UndoSetBlock(Location l, BaseBlock oldBlock) {
            m_location = l;
            m_block = oldBlock;
        }

        @Override
        public Location getLocation() {
            return m_location;
        }

        @Override
        public void redo() {
            throw new UnsupportedOperationException("Not supported, this is only for undo.");
        }

        @Override
        public void undo() {
            Chunk chunk = m_location.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }

            Block b = m_location.getBlock();
            b.setBlockData(m_block.Data);
        }
    }
}
