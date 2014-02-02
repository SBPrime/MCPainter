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
package org.PrimeSoft.MCPainter.palettes;

import java.util.HashMap;

/**
 *
 * @author SBPrime
 */
public class PaletteManager {

    /**
     * List of all known palettes
     */
    private final HashMap<String, Palette> m_palettes = new HashMap<String, Palette>();

    /**
     * Get the palette
     *
     * @param paletteName
     * @return
     */
    public Palette getPalette(String paletteName) {
        synchronized (m_palettes) {
            return m_palettes.get(paletteName);
        }
    }
    
    /**
     * List of all known palettes
     * @return 
     */
    public String[] getNames() {
        synchronized (m_palettes){
            return m_palettes.keySet().toArray(new String[0]);
        }
    }

    /**
     * Add new palette
     *
     * @param pal
     * @return
     */
    public boolean addPalette(Palette pal) {
        if (pal == null) {
            return false;
        }
        String name = pal.getName();
        synchronized (m_palettes) {
            if (m_palettes.containsKey(name)) {
                return false;
            }
            
            m_palettes.put(name, pal);            
        }
        
        return true;
    }

    /**
     * Clear the palette catch
     */
    public void clear() {
        synchronized (m_palettes) {
            m_palettes.clear();
        }
    }

    /**
     * Number of palettes
     * @return 
     */
    public int getCount() {
        synchronized (m_palettes){
            return m_palettes.size();
        }
    }
}