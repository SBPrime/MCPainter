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
package org.PrimeSoft.MCPainter;

import org.PrimeSoft.MCPainter.utils.BaseBlock;
import org.PrimeSoft.MCPainter.utils.Vector;


/**
 *
 * @author SBPrime
 */
public class BlockLogerEntry {

    private Vector m_location;
    private BaseBlock m_newBlock;
    private boolean m_finalize;
    private String m_message;
    private BlockLoger m_loger;
    private ILoggerCommand m_command;

    public Vector getLocation() {
        return m_location;
    }

    public BaseBlock getNewBlock() {
        return m_newBlock;
    }

    public String getMessage() {
        return m_message;
    }

    public ILoggerCommand getCommand() {
        return m_command;
    }

    public boolean isFinalize() {
        return m_finalize;
    }

    public BlockLoger getLoger() {
        return m_loger;
    }

    public BlockLogerEntry(BlockLoger loger) {
        initialize(loger);

        m_finalize = true;
    }

    public BlockLogerEntry(BlockLoger loger, Vector location,
            BaseBlock newBlock) {
        initialize(loger);
        m_location = location;
        m_newBlock = newBlock;
    }

    public BlockLogerEntry(BlockLoger loger, String msg) {
        initialize(loger);
        m_message = msg;
    }

    public BlockLogerEntry(BlockLoger loger, ILoggerCommand command) {
        initialize(loger);
        m_command = command;
    }

    private void initialize(BlockLoger loger) {
        m_loger = loger;
        m_finalize = false;
        m_message = null;
        m_location = null;
        m_newBlock = null;
        m_command = null;
    }
}
