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
package org.PrimeSoft.MCPainter.Drawing;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import org.apache.commons.lang.NullArgumentException;

/**
 *
 * @author SBPrime
 */
public class RawImage {

    /**
     * The raw image
     */
    private final int[][] m_img;
    /**
     * Is alpha pressent
     */
    private final boolean m_hasAlpha;
    /**
     * Width
     */
    private final int m_width;
    /**
     * Height
     */
    private final int m_height;
    /**
     * Texture res
     */
    private final int m_res;

    /**
     * The height
     */
    public final int getHeight() {
        return m_height;
    }

    /**
     * The width
     *
     * @return
     */
    public final int getWidth() {
        return m_width;
    }

    /**
     * The texture res
     *
     * @return
     */
    public final int getRes() {
        return m_res;
    }

    /**
     * Gets the raw image
     */
    public int[][] getImage() {
        return m_img;
    }

    /**
     * Has alpha channel
     *
     * @return
     */
    public boolean hasAlpha() {
        return m_hasAlpha;
    }

    /**
     * Create new instance of raw image
     *
     * @param img Source image (RAW)
     */
    public RawImage(int[][] img, int res) {
        if (img == null) {
            throw new NullArgumentException("img");
        }
        m_img = img;
        m_hasAlpha = false;
        m_height = m_img.length;
        m_width = m_height > 0 ? m_img[0].length : 0;
        m_res = res;
    }

    /**
     * Create ne instance of raw image
     *
     * @param rawImage
     * @param hasAlpha
     * @param res
     */
    private RawImage(int[][] rawImage, boolean hasAlpha, int res) {
        m_img = rawImage;
        m_hasAlpha = hasAlpha;
        m_height = m_img.length;
        m_width = m_height > 0 ? m_img[0].length : 0;
        m_res = res;
    }

    /**
     * Get sub image
     *
     * @param column
     * @param row
     * @return
     */
    public RawImage subImage(int col, int row) {
        row *= m_res;
        col *= m_res;
        int[][] raw = m_img;
        int toRow = row + m_res;
        int toCol = col + m_res;

        if (row >= 0 && m_height > m_res && toRow <= m_height) {
            raw = Arrays.copyOfRange(raw, row, toRow);
        }
        if (col >= 0 && m_width > m_res && toCol <= m_width) {
            for (int i = 0; i < raw.length; i++) {
                raw[i] = Arrays.copyOfRange(raw[i], col, toCol);
            }
        }

        return new RawImage(raw, m_hasAlpha, m_res);
    }

    /**
     * Create new instance of raw image
     *
     * @param img Source image
     */
    public RawImage(BufferedImage img, int res) {
        boolean[] alpha = new boolean[1];
        m_img = ImageHelper.convertToRGB(img, alpha);
        m_hasAlpha = alpha[0];
        m_height = m_img.length;
        m_width = m_height > 0 ? m_img[0].length : 0;
        m_res = res;
    }
}
