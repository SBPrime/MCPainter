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
package org.primesoft.mcpainter.texture;

/**
 *
 * @author SBPrime
 */
public class TextureDescription {

    private String m_texturePack;
    private String m_file;
    private int m_column;
    private int m_row;

    public static TextureDescription parse(String s) {
        if (s == null) {
            return null;
        }
        String[] parts = s.split(":");
        if (parts.length < 2)
        {
            return null;
        }
        
        String tPack = parts[0];
        String file = parts[1];
        int column = -1;
        int row = -1;
        
        if (parts.length >= 3 && parts[2] != null)
        {
            try {
                column = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ex)
            {
                
            }
        }
        if (parts.length >= 4 && parts[3] != null)
        {
            try {
                row = Integer.parseInt(parts[3]);
            } catch (NumberFormatException ex)
            {
                
            }
        }
        
        return new TextureDescription(tPack, file, column, row);
    }

    public TextureDescription(String pack, String file) {
        this(pack, file, 0, 0);
    }

    public TextureDescription(String pack, String file, int column, int row) {
        m_texturePack = pack;
        m_file = file;
        m_column = column;
        m_row = row;
    }

    public String getTexturePack() {
        return m_texturePack;
    }

    public String getFile() {
        return m_file;
    }

    public int getColumn() {
        return m_column;
    }

    public int getRow() {
        return m_row;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(m_texturePack);
        sb.append(":");
        sb.append(m_file);
        sb.append(":");
        sb.append(m_column);
        sb.append("x");
        sb.append(m_row);

        return sb.toString();
    }       
}
