/*
 * The MIT License
 *
 * Copyright 2012 SBPrime.
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
package org.primesoft.mcpainter.drawing;

import org.primesoft.mcpainter.utils.Pair;
import org.primesoft.mcpainter.utils.Vector2D;

/**
 *
 * @author SBPrime
 */
public class Face {

    private Vector2D m_tl;
    private Vector2D m_tr;
    private Vector2D m_bl;
    private Vector2D m_br;
    private boolean m_isGray;
    private int m_delta;
    private int m_depth;
    private RawImage m_texture;
    private Pair<Integer, Integer> m_cropH;
    private Pair<Integer, Integer> m_cropV;

    public Vector2D getTL() {
        return m_tl;
    }

    public Vector2D getTR() {
        return m_tr;
    }

    public Vector2D getBL() {
        return m_bl;
    }

    public Vector2D getBR() {
        return m_br;
    }

    public boolean isGray() {
        return m_isGray;
    }

    public int getDelta() {
        return m_delta;
    }

    public int getDepth() {
        return m_depth;
    }

    private Face(Face source) {
        m_bl = source.m_bl;
        m_br = source.m_br;
        m_cropH = source.m_cropH;
        m_cropV = source.m_cropV;
        m_delta = source.m_delta;
        m_depth = source.m_depth;
        m_isGray = source.m_isGray;
        m_texture = source.m_texture;
        m_tl = source.m_tl;
        m_tr = source.m_tr;
    }

    public Face(int u1, int v1, int u2, int v2, RawImage img) {
        this(new Vector2D(u1, v1), new Vector2D(u2, v1),
                new Vector2D(u1, v2), new Vector2D(u2, v2), img, false);
    }

    public Face(Vector2D tl, Vector2D tr, Vector2D bl, Vector2D br, RawImage img) {
        this(tl, tr, bl, br, img, false);
    }

    public Face(Vector2D tl, Vector2D tr, Vector2D bl, Vector2D br, RawImage img,
            boolean isGray) {
        m_tl = tl;
        m_tr = tr;
        m_bl = bl;
        m_br = br;
        m_isGray = isGray;
        m_delta = 0;
        m_depth = 1;

        if (img != null) {
            setTexture(img);
        }
    }

    public void setGray(boolean state) {
        m_isGray = state;
    }

    public void setDelta(int delta) {
        m_delta = delta;
    }

    public void setDepth(int depth) {
        m_depth = depth;
    }

    /**
     * Set the texture
     *
     * @param img
     */
    public void setTexture(RawImage img) {
        m_texture = img;
    }

    /**
     * Get the face texture
     *
     * @return the texture
     */
    public RawImage getTexture() {
        return m_texture;
    }

    public Pair<Integer, Integer> getCropH() {
        return m_cropH;
    }

    public void setCropH(Pair<Integer, Integer> crop) {
        m_cropH = crop;
    }

    public void setCropV(Pair<Integer, Integer> crop) {
        m_cropV = crop;
    }

    public Pair<Integer, Integer> getCropV() {
        return m_cropV;
    }

    public static Face clone(Face face) {
        if (face == null) {
            return null;
        }
        
        return new Face(face);
    }
}
