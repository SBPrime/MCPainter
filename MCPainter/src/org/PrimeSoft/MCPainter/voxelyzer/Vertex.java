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

/**
 *
 * @author SBPrime
 */
public class Vertex {

    public static final int ARRAY_SIZE = 6;
    /**
     * The vertex data
     */
    private final double[] m_data;

    /**
     * The attached vertex
     */
    private final Vertex m_attached;

    /**
     * Get the attached vertex
     *
     * @return
     */
    public Vertex getAttached() {
        return m_attached;
    }

    public Vertex(double x, double y, double z) {
        m_data = new double[]{x, y, z, Double.NaN, Double.NaN, Double.NaN};
        m_attached = null;
    }

    public Vertex(double x, double y, double z, double u, double v) {
        m_data = new double[]{x, y, z, u, v, Double.NaN};
        m_attached = null;
    }

    public Vertex(double x, double y, double z, double r, double g, double b) {
        m_data = new double[]{x, y, z, r, g, b};
        m_attached = null;
    }

    public Vertex(Vertex source) {
        this(source.m_data, source.m_attached);
    }

    public Vertex(double[] data) {
        this(data, null);
    }

    public Vertex(double[] data, Vertex attached) {
        m_attached = attached;
        m_data = new double[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            m_data[i] = i < data.length ? data[i] : Double.NaN;
        }
    }

    public double[] getData() {
        return m_data;
    }

    public void setX(double x) {
        m_data[0] = x;
    }

    public double getX() {
        return m_data[0];
    }

    public double getY() {
        return m_data[1];
    }

    public double getZ() {
        return m_data[2];
    }

    public double getU() {
        return m_data[3];
    }

    public double getV() {
        return m_data[4];
    }

    public double getR() {
        return m_data[3];
    }

    public double getG() {
        return m_data[4];
    }

    public double getB() {
        return m_data[5];
    }

    public void setColor(int[] color) {
        for (int i = 0; i < 3; i++) {
            m_data[3 + i] = color[i];
        }
    }

    public void setMapping(double[] mapping) {
        for (int i = 0; i < 2; i++) {
            m_data[3 + i] = mapping[i];
        }
    }

    public Vertex round() {
        double[] data = new double[ARRAY_SIZE];
        for (int i = 0; i < 3; i++) {
            data[i] = round(m_data[i]);
        }
        for (int i = 3; i < ARRAY_SIZE; i++) {
            data[i] = m_data[i];
        }

        return new Vertex(data, m_attached);
    }

    public static int round(double d) {
        return (int) Math.round(d);
    }

    public static Vertex abs(Vertex a) {
        if (a == null) {
            return null;
        }
        double[] result = new double[ARRAY_SIZE];
        double[] data = a.getData();

        for (int i = 0; i < ARRAY_SIZE; i++) {
            result[i] = Math.abs(data[i]);
        }

        return new Vertex(result, abs(a.m_attached));
    }

    public static Vertex sub(Vertex a, Vertex b) {
        if (a == null || b == null) {
            return null;
        }
        double[] d1 = a.m_data;
        double[] d2 = b.m_data;
        double[] d3 = new double[ARRAY_SIZE];

        for (int i = 0; i < ARRAY_SIZE; i++) {
            d3[i] = d1[i] - d2[i];
        }

        return new Vertex(d3, sub(a.m_attached, b.m_attached));
    }

    public static Vertex add(Vertex a, Vertex b) {
        if (a == null || b == null) {
            return null;
        }
        double[] d1 = a.m_data;
        double[] d2 = b.m_data;
        double[] d3 = new double[ARRAY_SIZE];

        for (int i = 0; i < ARRAY_SIZE; i++) {
            d3[i] = d1[i] + d2[i];
        }

        return new Vertex(d3, add(a.m_attached, b.m_attached));
    }

    public static Vertex mul(Vertex a, double b) {
        if (a == null) {
            return null;
        }
        double[] d1 = a.m_data;
        double[] d3 = new double[ARRAY_SIZE];

        for (int i = 0; i < ARRAY_SIZE; i++) {
            d3[i] = d1[i] * b;
        }

        return new Vertex(d3, mul(a.m_attached, b));
    }

    public static Vertex div(Vertex a, double b) {
        if (a == null) {
            return null;
        }
        double[] d1 = a.m_data;
        double[] d3 = new double[ARRAY_SIZE];

        for (int i = 0; i < ARRAY_SIZE; i++) {
            d3[i] = d1[i] / b;
        }

        return new Vertex(d3, div(a.m_attached, b));
    }

    public static Vertex map(Vertex a, double[][] map) {
        double[] d1 = a.m_data;
        double[] d2 = new double[ARRAY_SIZE];

        for (int i = 0; i < ARRAY_SIZE; i++) {
            double sum = Double.NaN;
            double[] mul = map[i];
            for (int j = 0; j < ARRAY_SIZE; j++) {
                if (!Double.isNaN(d1[j]) && !Double.isNaN(mul[j])) {
                    if (Double.isNaN(sum)) {
                        sum = 0;
                    }
                    sum += d1[j] * mul[j];
                }
            }

            d2[i] = sum;
        }

        return new Vertex(d2, a.m_attached);
    }

    @Override
    public String toString() {
        return m_data[0] + " " + m_data[1] + " " + m_data[2];
    }
}
