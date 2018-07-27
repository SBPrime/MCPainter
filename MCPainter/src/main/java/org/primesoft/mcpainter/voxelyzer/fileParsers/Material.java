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
package org.primesoft.mcpainter.voxelyzer.fileParsers;

import org.primesoft.mcpainter.drawing.RawImage;

/**
 *
 * @author SBPrime
 */
public class Material {
    private final String m_name;
    private final int[] m_color;
    private final RawImage m_texture;
    
    public Material(String name, int[] color) {
        m_name = name;
        m_color = color;
        m_texture = null;
    }
    
    public Material(String name, RawImage img) {
        m_name = name;
        m_color = null;
        m_texture = img;
    }
    
    
    public String getName()
    {
        return m_name;
    }
    
    public int[] getColor()
    {
        return m_color;
    }
    
    public RawImage getImage(){
        return m_texture;
    }
}
