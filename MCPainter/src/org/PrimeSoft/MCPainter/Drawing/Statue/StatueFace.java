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
package org.PrimeSoft.MCPainter.Drawing.Statue;

import org.PrimeSoft.MCPainter.Drawing.Face;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.utils.Vector2D;

/**
 *
 * @author SBPrime
 */
public class StatueFace {

    /**
     * Texture row
     */
    private final int m_row;
    /**
     * Texture column
     */
    private final int m_col;
    /**
     * Number of rows covered by this face
     */
    private final int m_rows;
    /**
     * Number of columns covered by this face
     */
    private final int m_cols;
    /**
     * Rotate 90 deg
     */
    private final boolean m_rotate;
    /**
     * Id of the texture to use
     */
    private final int m_textureId;

    /**
     * Texture Id
     *
     * @return
     */
    public int getTextureId() {
        return m_textureId;
    }

    /**
     * Convert statue face to standard cube face
     *
     * @param img Face texture
     * @param cols Statue texture columns
     * @param rows Statue texture rows
     * @param scale Texture scaling
     * @return
     */
    public Face getFace(RawImage img, int[] cols, int[] rows, double scale) {
        if (m_col >= cols.length || m_col + m_cols >= cols.length
                || m_row >= rows.length || m_row + m_rows >= rows.length) {
            return null;
        }

        double u1 = cols[m_col] * scale;
        double u2 = cols[m_col + m_cols] * scale;

        double v1 = rows[m_row] * scale;
        double v2 = rows[m_row + m_rows] * scale;

        if (m_cols > 0) {
            u2--;
        } else {
            u1--;
        }
        if (m_rows > 0) {
            v2--;
        } else {
            v1--;
        }

        if (m_rotate) {
            return new Face(new Vector2D(u1, v1), new Vector2D(u1, v2),
                    new Vector2D(u2, v1), new Vector2D(u2, v2), img);
        }

        return new Face((int) u1, (int) v1, (int) u2, (int) v2, img);
    }

    public StatueFace(int col, int row, int cols, int rows) {
        this(col, row, cols, rows, false);
    }

    public StatueFace(int col, int row, int cols, int rows, int textureId) {
        this(col, row, cols, rows, textureId, false);
    }

    public StatueFace(int col, int row, int cols, int rows,
            boolean rotate) {
        this(col, row, cols, rows, 0, rotate);
    }

    public StatueFace(int col, int row, int cols, int rows,
            int textureId, boolean rotate) {
        m_row = row;
        m_col = col;
        m_rows = rows;
        m_cols = cols;
        m_rotate = rotate;
        m_textureId = textureId;
    }

    public static StatueFace parse(String s) {
        String[] parts = s.split(",");


        if (parts.length < 4) {
            return null;
        }

        int col, row, cols, rows;
        boolean rotate = false;
        int texId = 0;
        try {
            col = Integer.parseInt(parts[0]);
            row = Integer.parseInt(parts[1]);
            cols = Integer.parseInt(parts[2]);
            rows = Integer.parseInt(parts[3]);
        } catch (Exception ex) {
            return null;
        }

        if (parts.length > 4) {
            rotate = parts[4] != null && parts[4].equalsIgnoreCase("true");
        }
        if (parts.length > 5) {
            try {
                texId = Integer.parseInt(parts[5]);
            } catch (Exception ex) {
                //Ignore exception
            }
        }

        return new StatueFace(col, row, cols, rows, texId, rotate);
    }
}