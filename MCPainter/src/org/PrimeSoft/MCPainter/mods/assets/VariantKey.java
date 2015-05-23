/*
 * The MIT License
 *
 * Copyright 2015 prime.
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
package org.PrimeSoft.MCPainter.mods.assets;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author SBPrime
 */
public class VariantKey {
    /**
     * The key entries
     */
    private final HashMap<String, String> m_entries = new HashMap<String, String>();
    
    /**
     * The hash code
     */
    private final int m_hash;
            
    
    public VariantKey(String entry)
    {
        if (entry == null) {
            entry = "";
        }
        
        int hash = 0;
        String[] parts = entry.toLowerCase().split(",");
        
        for (String s : parts)
        {
            String[] keyValue = s.split("=");
            String key = (keyValue.length > 0 ? keyValue[0] : "").trim();
            String value = (keyValue.length > 1 ? keyValue[1] : "").trim();
            
            if (m_entries.containsKey(key)) {
                m_entries.remove(key);
            }
            
            m_entries.put(key, value);
            
            hash ^= key.hashCode() ^ value.hashCode();
        }
        
        m_hash = hash;
    }

    @Override
    public int hashCode() {
        return m_hash;
    }

    @Override
    public boolean equals(Object obj) {
        VariantKey other = obj instanceof VariantKey ? (VariantKey)obj : null;
        
        if (other == null) {
            return false;
        }
        
        if (m_hash != other.m_hash) {
            return false;
        }
        
        final HashSet<String> tested = new HashSet<String>();
        
        for (String myKey : m_entries.keySet()) {
            String myValue = m_entries.get(myKey);
            
            String otherValue = other.m_entries.get(myKey);
            
            if (otherValue == null || !otherValue.equalsIgnoreCase(myValue)) {
                return false;
            }
            
            tested.add(myKey);
        }        
        
        for (String key : other.m_entries.keySet()) {            
            if (!tested.contains(key)) {
                return false;
            }
        }
        
        return true;
    }            
}
