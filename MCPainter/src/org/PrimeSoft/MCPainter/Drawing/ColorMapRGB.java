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
package org.PrimeSoft.MCPainter.Drawing;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.Filters.ColorPaletteRgb;
import org.PrimeSoft.MCPainter.Drawing.Filters.IColorPalette;
import org.PrimeSoft.MCPainter.rgbblocks.RgbWrapper;

/**
 *
 * @author SBPrime.
 */
public class ColorMapRGB implements IColorMap {
    /**
     * Threshold gives the value when pixsl should by drawn as AIR/ignored
     */
    public static final int ALPHA_THRESHOLD = 64;
    
    private static final int MAX_CACHE = 4096;
    
    private final IColorPalette m_palette = new ColorPaletteRgb();
    private final RgbWrapper m_wrapper;
    
    public final LinkedHashMap<Integer, IDrawingBlock> m_colorCatch = new LinkedHashMap<Integer, IDrawingBlock>(MAX_CACHE + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, IDrawingBlock> eldest) {
            return size() > MAX_CACHE;
        }
    };
    
    public ColorMapRGB(RgbWrapper wrapper)
    {
        m_wrapper = wrapper;
    }
    
    
    @Override
    public IDrawingBlock getBlockForColor(Color c, OperationType type) {
        if (m_wrapper == null || c.getAlpha() < ALPHA_THRESHOLD) {
            return DrawingBlockRGB.AIR;
        }
        
        synchronized (m_colorCatch) {
            int color = c.getRGB();
            if (m_colorCatch.containsKey(color)) {
                return m_colorCatch.get(color);
            }
            
            IDrawingBlock result = new DrawingBlockRGB(m_wrapper.createHead(c));
            m_colorCatch.put(color, result);
            
            return result;
        }
    }

    @Override
    public IColorPalette getPalette(OperationType type) {
        return m_palette;
    }

    @Override
    public Boolean isInitialized() {
        return m_wrapper != null;
    }
}