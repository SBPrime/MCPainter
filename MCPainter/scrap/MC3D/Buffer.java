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
package org.PrimeSoft.MCPainter.MC3D;

import com.sk89q.worldedit.LocalPlayer;
import java.awt.image.BufferedImage;
import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;

/**
 * Vertice buffer
 *
 * @author SBPrime
 */
public class Buffer {
    public enum Type {

        List,
        Strip,
        Fan
    }
    
    /**
     * Texture to use when drawing a buffer
     */
    
    private final BufferedImage m_texture;
    /**
     * Vertices
     */
    
    private final DoubleVertex[] m_vertices;
    /**
     * Is the triangle strip gray
     */
    private final boolean m_isGray;
    /**
     * Gray color
     */
    private final int[] m_grayColor;
    /**
     * Buffer type
     */
    private final Type m_type;
        
    /**
     * Current block type
     */
    private final OperationType m_blockType;

    /**
     * Initialize new instance of the class
     *
     * @param v vertices
     * @param textImage Texture image
     * @param isGray Is texture grayscale
     * @param grayColor Gray color
     * @param type Buffer type
     * @param blockType Current operation block type
     */
    public Buffer(DoubleVertex[] v, BufferedImage textImage, boolean isGray,
            int[] grayColor, Type type, OperationType blockType) {
        m_texture = textImage;
        m_vertices = v;
        m_isGray = isGray;
        m_grayColor = grayColor;
        m_type = type;
        m_blockType = blockType;
    }

    /**
     * Draw the triangle strip buffer
     *
     * @param forceAlpha Force the use of alpha channel
     * @param player Player that draws the items
     * @param loger block logger
     * @param colorMap color to block map
     * @param m transformation matrix
     */
    public void Draw(BlockLoger loger, ColorMap colorMap, boolean forceAlpha, Matrix m) {
        if (m_vertices.length < 3) {
            return;
        }
        
        loger.LogMessage("Applying transformation...");
        boolean[] useAlpha = new boolean[1];
        int[][] rawImg = ImageHelper.convertToRGB(m_texture, useAlpha);
        DoubleVertex[] v = new DoubleVertex[m_vertices.length];
        if (m != null) {
            for (int i = 0; i < m_vertices.length; i++) {
                v[i] = new DoubleVertex(m_vertices[i].transform(m));
            }
        } else {
            for (int i = 0; i < m_vertices.length; i++) {
                v[i] = new DoubleVertex(m_vertices[i]);
            }
        }

        forceAlpha |= useAlpha[0];
        
        switch (m_type)
        {
            case Fan:
                DrawFan(v, loger, colorMap, rawImg, forceAlpha);
                break;
            case List:
                DrawList(v, loger, colorMap, rawImg, forceAlpha);
                break;
            case Strip:
                DrawStrip(v, loger, colorMap, rawImg, forceAlpha);
                break;
        }        
    }

    private void DrawStrip(DoubleVertex[] v, BlockLoger loger, ColorMap colorMap,
            int[][] rawImg, boolean useAlpha) {
        DoubleVertex[] vv = new DoubleVertex[3];
        for (int i = 2; i < m_vertices.length; i++) {
            vv[0] = v[i - 2];
            vv[1] = v[i - 1];
            vv[2] = v[i - 0];
            Utils3D.drawTriangle(vv, loger, colorMap, rawImg, useAlpha, m_isGray, 
                    m_grayColor, m_blockType);
        }
    }

    private void DrawList(DoubleVertex[] v, BlockLoger loger, ColorMap colorMap,
            int[][] rawImg, boolean useAlpha) {
        DoubleVertex[] vv = new DoubleVertex[3];
        for (int i = 2; i < m_vertices.length; i += 3) {
            vv[0] = v[i - 2];
            vv[1] = v[i - 1];
            vv[2] = v[i - 0];
            Utils3D.drawTriangle(vv, loger, colorMap, rawImg, useAlpha, m_isGray, 
                    m_grayColor, m_blockType);
        }
    }
    
    
    private void DrawFan(DoubleVertex[] v, BlockLoger loger, ColorMap colorMap,
            int[][] rawImg, boolean useAlpha) {
        DoubleVertex[] vv = new DoubleVertex[3];
        vv[0] = v[0];
        for (int i = 2; i < m_vertices.length; i ++) {            
            vv[1] = v[i - 1];
            vv[2] = v[i - 0];
            Utils3D.drawTriangle(vv, loger, colorMap, rawImg, useAlpha, m_isGray, 
                    m_grayColor, m_blockType);
        }
    }
}
