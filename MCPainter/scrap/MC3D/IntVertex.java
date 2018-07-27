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
package org.primesoft.mcpainter.MC3D;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;

/**
 * This class represents a 3D vertex
 * @author SBPrime
 */
public class IntVertex extends Vertex<Integer> {    
    /**
     * Create a new instance of the class
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param u u texture coordinate
     * @param v v texture coordinate
     */
    public IntVertex(int x, int y, int z, double u, double v) {
        super(x, y, z, u, v);
    }
    
    
    /**
     * Create a new instance of the class based on another vertice
     * @param v source vertice
     */
    public IntVertex(DoubleVertex v)
    {
        super((int)Math.round(v.getX()), (int)Math.round(v.getY()), (int)Math.round(v.getZ()),
                v.getU(), v.getV());
    }
    
    
    /**
     * Get the world edit vector
     * @return the vector
     */
    public Vector getVector()
    {
        return new BlockVector(m_x, m_y, m_z);
    }
}
