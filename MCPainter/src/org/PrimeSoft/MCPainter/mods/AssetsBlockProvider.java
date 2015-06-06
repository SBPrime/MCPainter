/*
 * The MIT License
 *
 * Copyright 2015 SBPrime.
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
package org.PrimeSoft.MCPainter.mods;

import java.util.HashMap;
import java.util.Map;
import org.PrimeSoft.MCPainter.Drawing.Blocks.AssetBlock;
import org.PrimeSoft.MCPainter.Drawing.Blocks.IBlockProvider;
import org.PrimeSoft.MCPainter.Drawing.Blocks.IDrawableElement;

/**
 *
 * @author SBPrime
 */
public class AssetsBlockProvider implements IBlockProvider {
    private final HashMap<String, IDrawableElement> m_nameBlocks;
    private final int m_cnt;

    public AssetsBlockProvider(HashMap<String, AssetBlock> blocks) {
        int cnt = 0;
        m_nameBlocks = new HashMap<String, IDrawableElement>();
        for (Map.Entry<String, AssetBlock> entrySet : blocks.entrySet()) {
            m_nameBlocks.put(entrySet.getKey().toUpperCase(), entrySet.getValue());            
            cnt++;
        }
        
        m_cnt = cnt;
    }

    @Override
    public int getBlocksCount() {
        return m_cnt;
    }

    @Override
    public IDrawableElement getBlock(String name) {
        if (name == null) {
            return null;
        }

        name = name.toUpperCase();
        if (m_nameBlocks.containsKey(name)) {
            return m_nameBlocks.get(name);
        }

        return null;
    }

    @Override
    public IDrawableElement getBlock(int materialId) {
        return null;
    }
}
