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
package org.primesoft.mcpainter.voxelyzer;

/**
 * The clipping region (use non transformed coordinates)
 *
 * @author SBPrime
 */
public class ClippingRegion {

    private double m_xMin;
    private double m_yMin;
    private double m_zMin;
    private double m_xMax;
    private double m_yMax;
    private double m_zMax;

    public ClippingRegion(double xMin, double xMax,
            double yMin, double yMax,
            double zMin, double zMax) {
        double t;
        if (xMin > xMax) {
            t = xMin;
            xMin = xMax;
            xMax = t;
        }
        if (yMin > yMax) {
            t = yMin;
            yMin = yMax;
            yMax = t;
        }
        if (zMin > zMax) {
            t = xMin;
            zMin = zMax;
            zMax = t;
        }

        m_xMin = xMin;
        m_yMin = yMin;
        m_zMin = zMin;

        m_xMax = xMax;
        m_yMax = yMax;
        m_zMax = zMax;
    }

    public boolean testVertex(Vertex v) {
        if (v == null) {
            return true;
        }
        
        v = v.getAttached();
        if (v == null) {
            return true;
        }
        
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();
        
        if (x < m_xMin || x > m_xMax) {
            return false;
        }
        if (y < m_yMin || y > m_yMax) {
            return false;
        }
        if (z < m_zMin || z > m_zMax) {
            return false;
        }
        
        return true;
    }
}
