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
package org.primesoft.mcpainter.drawing.blocks;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a block provider that agregates multiple providers
 *
 * @author SBPrime
 */
public class MultiBlockProvider implements IBlockProvider {
    /**
     * List of all known block providers
     */
    private final List<IBlockProvider> m_blockProviders;

    public MultiBlockProvider() {
        m_blockProviders = new ArrayList<IBlockProvider>();
    }

    /**
     * Clear all known block providers
     */
    public void clear() {
        m_blockProviders.clear();
    }

    /**
     * Register new block provider
     *
     * @param blockProvider
     * @return
     */
    public boolean register(IBlockProvider blockProvider) {
        if (blockProvider == null || blockProvider.getBlocksCount() == 0) {
            return false;
        }

        m_blockProviders.add(blockProvider);
        return true;
    }

    @Override
    public int getBlocksCount() {
        int sum = 0;
        for (IBlockProvider bp : m_blockProviders) {
            sum += bp.getBlocksCount();
        }
        return sum;
    }

    @Override
    public IDrawableElement getBlock(String name) {
        synchronized (m_blockProviders) {
            for (IBlockProvider blockProvider : m_blockProviders) {
                IDrawableElement result = blockProvider.getBlock(name);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public IDrawableElement getBlock(int materialId) {
        synchronized (m_blockProviders) {
            for (IBlockProvider blockProvider : m_blockProviders) {
                IDrawableElement result = blockProvider.getBlock(materialId);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}
