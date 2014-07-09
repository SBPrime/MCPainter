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

import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;

/**
 *
 * @author SBPrime
 */
public class Model {

    /**
     * Vertex
     */
    private final Vertex[] m_vertex;
    /**
     * LIst of faces
     */
    private final Face[] m_faces;
    /**
     * Minimum coordinate
     */
    private final Vertex m_min;
    /**
     * Maximum coordinate
     */
    private final Vertex m_max;
    /**
     * Model size
     */
    private final Vertex m_size;

    public Vertex getMin() {
        return m_min;
    }

    public Vertex getMax() {
        return m_max;
    }

    public Vertex getSize() {
        return m_size;
    }

    /**
     * Create new instance of model class
     *
     * @param vertex
     * @param faces
     */
    public Model(Vertex[] vertex, Face[] faces) {
        m_faces = faces;
        m_vertex = vertex;

        final double[] min = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0};
        final double[] max = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, 0, 0};
        for (Vertex v : vertex) {
            final double[] data = v.getData();
            for (int i = 0; i < 3; i++) {
                if (min[i] > data[i]) {
                    min[i] = data[i];
                }
                if (max[i] < data[i]) {
                    max[i] = data[i];
                }
            }
        }

        m_min = new Vertex(min);
        m_max = new Vertex(max);
        m_size = Vertex.sub(m_max, m_min);
    }

    /**
     * Render model
     *
     * @param loger
     * @param colorMap
     * @param matrix
     */
    public void render(BlockLoger loger, ColorMap colorMap, Matrix matrix) {
        for (Face face : m_faces) {
            face.render(loger, colorMap, matrix, m_vertex);
        }
    }
}