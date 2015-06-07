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
package org.PrimeSoft.MCPainter.worldEdit;

import com.sk89q.worldedit.EditSession;
import org.PrimeSoft.MCPainter.utils.BaseBlock;
import org.PrimeSoft.MCPainter.utils.Vector;

/**
 *
 * @author SBPrime
 */
public class WorldEditEditSession implements IEditSession {

    private final EditSession m_editSession;

    public WorldEditEditSession(EditSession editSession) {
        m_editSession = editSession;
    }

    public EditSession getEditSession() {
        return m_editSession;
    }

    @Override
    public BaseBlock getBlock(Vector location) {
        com.sk89q.worldedit.Vector v = WorldEditWrapper.convert(location);
        com.sk89q.worldedit.blocks.BaseBlock block = m_editSession.getBlock(v);

        return WorldEditWrapper.convert(block);
    }

    @Override
    public void setBlock(Vector location, BaseBlock block) throws MaxChangedBlocksException {
        com.sk89q.worldedit.blocks.BaseBlock weBlock = WorldEditWrapper.convert(block);
        com.sk89q.worldedit.Vector weLocaton = WorldEditWrapper.convert(location);
        try {
            m_editSession.setBlock(weLocaton, weBlock);
        } catch (com.sk89q.worldedit.MaxChangedBlocksException ex) {
            throw new MaxChangedBlocksException();
        }
    }
}