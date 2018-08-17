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
import org.primesoft.mcpainter.worldEdit.MaxChangedBlocksException;

/**
 *
 * @author SBPrime
 */
public class ChangeEntry extends BlockLogerEntry {

    private final IChange m_change;

    public ChangeEntry(BlockLoger loger, IChange change) {
        super(loger);

        m_change = change;
    }

    @Override
    public boolean canRemove() {
        return true;
    }

    @Override
    public void execute() {
        try {
            m_loger.getEditSession().doCustom(m_change);
        } catch (MaxChangedBlocksException ex) {
            MCPainterMain.say(getPlayer(), "Max block change reached");
            MCPainterMain.log(ex.getMessage());
        }
    }

}
