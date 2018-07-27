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

import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author SBPrime
 */
class StubEditEditSession implements IEditSession {

    private World m_world;

    public StubEditEditSession(StubLocalPlayer stubLocalPlayer) {
        m_world = stubLocalPlayer.getWorld();
    }

    @Override
    public BaseBlock getBlock(Vector location) {
        Location l = new Location(m_world, location.getX(), location.getY(), location.getZ());
        Chunk chunk = l.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        Block b = l.getBlock();
        
        return new BaseBlock(b.getType(), b.getData());
    }

    @Override
    public void setBlock(Vector location, BaseBlock block) throws MaxChangedBlocksException {
        Location l = new Location(m_world, location.getX(), location.getY(), location.getZ());
        Chunk chunk = l.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        Block b = l.getBlock();
        b.setType(block.getMaterial());
        //TODO: 1.13
        //TODO: Implement me! b.setData((byte)block.getData());
    }

}
