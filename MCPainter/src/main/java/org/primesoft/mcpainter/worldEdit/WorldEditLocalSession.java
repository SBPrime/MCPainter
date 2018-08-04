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
import com.sk89q.worldedit.LocalSession;
import org.primesoft.mcpainter.BlocksHubIntegration;

/**
 *
 * @author SBPrime
 */
public class WorldEditLocalSession implements ILocalSession {
    private final LocalSession m_localSession;
    
    private final BlocksHubIntegration m_bh;

    public WorldEditLocalSession(LocalSession localSession, BlocksHubIntegration bh) {
        m_localSession = localSession;
        m_bh = bh;
    }

    public LocalSession getLocalSession() {
        return m_localSession;
    }

    @Override
    public void remember(IEditSession eSession) {
        if (!(eSession instanceof WorldEditEditSession)){
            throw new UnsupportedOperationException("Invalid argument."); 
        }
        
        m_localSession.remember(((WorldEditEditSession)eSession).getEditSession());
    }

    @Override
    public IEditSession createEditSession(ILocalPlayer localPlayer) {
        if (!(localPlayer instanceof WorldEditLocalPlayer)) {
            throw new UnsupportedOperationException("Invalid argument."); 
        }
        
        EditSession session = m_localSession.createEditSession(((WorldEditLocalPlayer)localPlayer).getLocalPlayer());
        return new WorldEditEditSession(localPlayer, session, m_bh);
    }
}
