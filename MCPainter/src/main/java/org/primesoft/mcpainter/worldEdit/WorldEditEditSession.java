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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.history.UndoContext;
import com.sk89q.worldedit.history.change.Change;
import com.sk89q.worldedit.history.changeset.ChangeSet;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.primesoft.mcpainter.BlocksHubIntegration;
import org.primesoft.mcpainter.blocksplacer.IChange;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;

/**
 *
 * @author SBPrime
 */
class WorldEditEditSession extends BaseEditSession {

    private static class ChangeWrapper implements Change {

        private final IChange m_change;

        public ChangeWrapper(IChange c) {
            m_change = c;
        }

        @Override
        public void undo(UndoContext uc) throws WorldEditException {
            m_change.redo();
        }

        @Override
        public void redo(UndoContext uc) throws WorldEditException {
            m_change.undo();
        }
    }

    private final EditSession m_editSession;

    public WorldEditEditSession(ILocalPlayer p, EditSession editSession, BlocksHubIntegration bh) {
        super(p, bh);
        m_editSession = editSession;
    }

    public EditSession getEditSession() {
        return m_editSession;
    }

    @Override
    public BaseBlock getBlock(Vector location) {
        return WorldEditWrapper.convert(m_editSession.getBlock(WorldEditWrapper.convert(location)));
    }

    @Override
    public void setBlock(Vector location, BaseBlock block) throws MaxChangedBlocksException {
        com.sk89q.worldedit.Vector weLocaton = WorldEditWrapper.convert(location);
        BaseBlock oldBlock = WorldEditWrapper.convert(m_editSession.getBlock(weLocaton));
        com.sk89q.worldedit.blocks.BaseBlock weBlock = WorldEditWrapper.convert(block);
        try {
            m_editSession.setBlock(weLocaton, weBlock);
        } catch (com.sk89q.worldedit.MaxChangedBlocksException ex) {
            throw new MaxChangedBlocksException();
        }

        logBlock(location, oldBlock, block);
    }

    @Override
    public void doCustom(IChange command) throws MaxChangedBlocksException {
        ChangeSet cs = m_editSession.getChangeSet();
        command.redo();
        cs.add(new ChangeWrapper(command));
    }
}
