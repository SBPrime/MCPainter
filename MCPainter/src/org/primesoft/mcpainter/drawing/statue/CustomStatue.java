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
package org.primesoft.mcpainter.drawing.statue;

import org.PrimeSoft.MCPainter.Drawing.IColorMap;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.utils.Vector;

/**
 *
 * @author SBPrime
 */
public class CustomStatue extends BaseStatue {
    /**
     * The statue description
     */
    private final StatueDescription m_statueDescription;
    
    
    /**
     * 
     * @param colorMap
     * @param position
     * @param yaw
     * @param pitch
     * @param orientation
     * @param data 
     */
    public CustomStatue(IColorMap colorMap, Vector position, double yaw, double pitch,
            Orientation orientation, 
            StatueDescription data) {        
        super(colorMap, position, yaw, pitch, orientation, data.getSize());
        
        m_statueDescription = data;
    }
    
    @Override
    protected int[] getTextureRes() {
        return m_statueDescription.getTextureRes();
    }

    @Override
    protected StatueBlock[] getBlocks() {
        return m_statueDescription.getBlocks();
    }

    @Override
    protected StatueFace[][] getFaces() {
        return m_statueDescription.getFaces();
    }

    @Override
    protected int[] getColumns() {
        return m_statueDescription.getColumns();
    }

    @Override
    protected int[] getRows() {
        return m_statueDescription.getRows();
    }
    
}
