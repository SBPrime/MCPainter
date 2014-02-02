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
package org.PrimeSoft.MCPainter.Drawing.Filters;

import java.awt.Color;

/**
 *
 * @author SBPrime
 */
public class ColorEx {

    public final static ColorEx TRANSPARENT = new ColorEx(0, 0, 0, 0);
    private int m_r;
    private int m_g;
    private int m_b;
    private int m_a;

    public ColorEx(int c) {
        this(new Color(c, true));
    }

    public ColorEx(Color c) {
        m_r = c.getRed();
        m_g = c.getGreen();
        m_b = c.getBlue();
        m_a = c.getAlpha();
    }

    private ColorEx(int a, int r, int g, int b) {
        m_r = r;
        m_g = g;
        m_b = b;
        m_a = a;
    }

    /**
     * Is this colr transparent
     *
     * @return
     */
    public boolean isTransparent() {
        return m_a < 128;
    }

    public static ColorEx add(ColorEx c1, ColorEx c2) {
        return new ColorEx(c1.m_a + c2.m_a, c1.m_r + c2.m_r, c1.m_g + c2.m_g, c1.m_b + c2.m_g);
    }

    public static ColorEx sub(ColorEx c1, ColorEx c2) {
        return new ColorEx(c1.m_a - c2.m_a, c1.m_r - c2.m_r, c1.m_g - c2.m_g, c1.m_b - c2.m_g);
    }

    public static ColorEx mul(ColorEx c, double m) {
        return new ColorEx((int) (c.m_a * m), (int) (c.m_r * m), (int) (c.m_g * m), (int) (c.m_b * m));
    }

    public static double dist(ColorEx c1, ColorEx c2) {
        double rmean = (c1.m_r + c2.m_r) / 2.0;
        double r = c1.m_r - c2.m_r;
        double g = c1.m_g - c2.m_g;
        int b = c1.m_b - c2.m_b;
        double weightR = 2 + rmean / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256.0;
        return weightR * r * r + weightG * g * g + weightB * b * b;
    }
    
    public static ColorEx clamp(ColorEx c)
    {
        return new ColorEx(clamp(c.m_a), clamp(c.m_r), clamp(c.m_g), clamp(c.m_b));
    }

    /*public static double dist(ColorEx c1, ColorEx c2) {
        double dr = c1.m_r - c2.m_r;
        double dg = c1.m_g - c2.m_g;
        double db = c1.m_b - c2.m_b;

        return dr * dr + dg * dg + db * db;
    }*/

    public Color toColor() {
        return new Color(clamp(m_r), clamp(m_g), clamp(m_b), clamp(m_a));
    }

    public int toRGB() {
        return toColor().getRGB();
    }

    private static int clamp(int c) {
        return Math.max(0, Math.min(255, c));
    }
}