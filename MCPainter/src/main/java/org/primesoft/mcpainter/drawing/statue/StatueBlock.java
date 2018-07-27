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

import org.primesoft.mcpainter.utils.Vector;

/**
 *
 * @author SBPrime
 */
public class StatueBlock {

    /**
     * Block offset (from starting coords)
     */
    private Vector m_offset;
    /**
     * Is diagonal
     */
    private boolean m_isDiagonal;
    /**
     * Block axis map
     */
    private double[] m_map;
    /**
     * Block size
     */
    private Vector m_size;

    public Vector getOffset() {
        return m_offset;
    }

    public Vector getSize() {
        return m_size;
    }

    public boolean isDiagonal() {
        return m_isDiagonal;
    }
    
    
    public double[] getMap(){
        return m_map;
    }

    
    public StatueBlock(int ox, int oy, int oz, int w, int h, int d) {
        this(ox, oy, oz, w, h, d, false, null);
    }
    
    
    /**
     *
     * @param ox Block offset in X
     * @param oy Block offset in Y
     * @param oz Block offset in Z
     * @param w Block width
     * @param h Block height
     * @param d Block depth
     * @param isDiagonal
     * @param map
     */
    public StatueBlock(int ox, int oy, int oz, int w, int h, int d, 
            boolean isDiagonal, double[] map) {
        m_offset = new Vector(ox, oy, oz);
        m_size = new Vector(w, h, d);
        m_isDiagonal = isDiagonal;
        m_map = map;
    }
}
