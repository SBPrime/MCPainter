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
package org.PrimeSoft.MCPainter.voxelyzer;

import com.sk89q.worldedit.MaxChangedBlocksException;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.RawImage;

/**
 * 3D face
 * @author SBPrime
 */
public class Face {
    
    private final int[] m_vIdx;
    private final int[] m_color;
    private final RawImage m_texture;
    private final double[][] m_textureMapping;
    
    /**
     * New instance of the face
     * @param v Vertices index
     */
    public Face(int[] v) {
        m_vIdx = v;
        m_color = null;
        m_texture = null;
        m_textureMapping = null;
    }

    
    /**
     * New instance of the face
     * @param v vertices index
     * @param color vertices colors
     */
    public Face(int[] v, int[] color) {
        m_vIdx = v;
        m_color = color;
        m_texture = null;
        m_textureMapping = null;
    }

    
    /**
     * New instance of the face
     * @param v Vertices index
     * @param texture Face texture
     * @param mapping Texture UV mapping
     */
    public Face(int[] v, RawImage texture, double[][] mapping) {
        m_vIdx = v;
        m_texture = texture;
        m_textureMapping = mapping;
        m_color = null;
    }

    
    /**
     * Render the face
     * @param loger Bock logger
     * @param colorMap clolor mapper
     * @param matrix transformation matrix
     * @param clipping
     * @param v vertices
     * @throws org.PrimeSoft.MCPainter.worldEdit.MaxChangedBlocksException
     */
    public void render(BlockLoger loger, ColorMap colorMap, Matrix matrix,
            ClippingRegion clipping, Vertex[] v) 
            throws MaxChangedBlocksException {
        final Vertex[] vOut = new Vertex[3];
        if (v == null) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            int idx = m_vIdx[i];
            if (idx < 0 || idx >= v.length) {
                return;
            }
            if (v[i] == null) {
                return;
            }
            vOut[i] = matrix.applyMatrix(v[idx]);            
        }
        if (m_textureMapping != null) {
            for (int i = 0; i < 3; i++) {
                vOut[i].setMapping(m_textureMapping[i]);
            }
        } else if (m_color != null) {
            for (int i = 0; i < 3; i++) {
                vOut[i].setColor(m_color);
            }
        }
        Triangle.drawTriangle(loger, colorMap, clipping, m_texture, vOut[0], vOut[1], vOut[2]);
    }
}
