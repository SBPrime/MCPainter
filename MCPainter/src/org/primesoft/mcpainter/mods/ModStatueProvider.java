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
package org.PrimeSoft.MCPainter.mods;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import org.PrimeSoft.MCPainter.Drawing.Statue.PlayerStatueDescription;
import org.PrimeSoft.MCPainter.Drawing.Statue.StatueDescription;

/**
 *
 * @author SBPrime
 */
public class ModStatueProvider {

    /**
     * Simple string comparator used for sorting
     */
    private class StringComparer implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            if (o1 == null)
            {
                return 1;
            }
            return o1.compareToIgnoreCase(o2);
        }
    }
    /**
     * List of known statues
     */
    private HashMap<String, StatueDescription> m_statues;
    
    /**
     * The player statue drawing instructions
     */
    private PlayerStatueDescription m_playerStatueDescription;

    public ModStatueProvider() {
        m_statues = new HashMap<String, StatueDescription>();
    }

    /**
     * Remove all registered statues
     */
    public void clear() {
        m_statues.clear();
    }

    /**
     * Register statue description
     *
     * @param statue
     * @return
     */
    public boolean register(StatueDescription statue) {
        if (statue == null) {
            return false;
        }

        String name = statue.getName().toLowerCase();
        if (m_statues.containsKey(name)) {
            return false;
        }

        m_statues.put(name, statue);
        return true;
    }

    
    /**
     * Initialize the player statue drawing instructions
     * @param statue 
     */
    public void registerPlayer(PlayerStatueDescription statue) {
        m_playerStatueDescription = statue;
    }
    
    /**
     * Get statue description
     *
     * @param name
     * @return
     */
    public StatueDescription get(String name) {
        if (name == null) {
            return null;
        }

        name = name.toLowerCase();
        if (m_statues.containsKey(name)) {
            return m_statues.get(name);
        }

        return null;
    }

    /**
     * Get the player statue drawing instructions
     * @return 
     */
    public PlayerStatueDescription getPlayer() {
        return m_playerStatueDescription;
    }
    
    /**
     * Get all known mob names
     *
     * @return
     */
    public String[] getNames() {
        String[] result = m_statues.keySet().toArray(new String[0]);
        Arrays.sort(result, new StringComparer());
        return result;
    }
}
