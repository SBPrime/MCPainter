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

/**
 * This class represents a 3D vertex
 * @author SBPrime
 */
public class Vertex<T> {
    /**
     * X value
     */
    protected T m_x;
    
    /**
     * Y value
     */
    protected T m_y;
    
    /**
     * Z value
     */
    protected T m_z;
    
    /*
     * Texture U value
     */
    protected double m_u;
    
    /*
     * Texture V value
     */
    protected double m_v;

    /**
     * Get the x value
     * @return X
     */
    public T getX() {
        return m_x;
    }

    /**
     * Get the y value
     * @return Y
     */
    public T getY() {
        return m_y;
    }

    /**
     * Get the z value
     * @return Z
     */
    public T getZ() {
        return m_z;
    }

    /**
     * Get the texture u value
     * @return the u value
     */
    public double getU() {
        return m_u;
    }

    /**
     * Get the texture v value
     * @return the v value
     */
    public double getV() {
        return m_v;
    }

    /**
     * Create a new instance of the class
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param u u texture coordinate
     * @param v v texture coordinate
     */
    public Vertex(T x, T y, T z, double u, double v) {
        m_x = x;
        m_y = y;
        m_z = z;
        m_u = u;
        m_v = v;
    }
}
