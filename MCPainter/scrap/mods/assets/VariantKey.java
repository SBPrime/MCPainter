/*
 * The MIT License
 *
 * Copyright 2015 SBPrime.
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
import java.util.Map;
import org.bukkit.block.BlockFace;

/**
 *
 * @author SBPrime
 */
public class VariantKey {
    private final static String FACE_UP = "up";
    private final static String FACE_DOWN = "down";
    private final static String FACE_NORTH = "north";
    private final static String FACE_SOUTH = "south";
    private final static String FACE_EAST = "east";
    private final static String FACE_WEST = "west";

    /**
     * Facing property
     */
    private static final String FACING = "facing";

    /**
     * The key entries
     */
    private final HashMap<String, String> m_entries;

    /**
     * The hash code
     */
    private final int m_hash;

    /**
     * Get the facing property
     *
     * @return
     */
    public final BlockFace getFacing() {
        if (m_entries.containsKey(FACING)) {
            final String facing = m_entries.get(FACING);
            if (facing == null) {
                return BlockFace.SELF;
            }
            
            if (facing.equalsIgnoreCase(FACE_DOWN)) {
                return BlockFace.DOWN;
            } else if (facing.equalsIgnoreCase(FACE_EAST)) {
                return BlockFace.EAST;
            } else if (facing.equalsIgnoreCase(FACE_NORTH)) {
                return BlockFace.NORTH;
            } else if (facing.equalsIgnoreCase(FACE_SOUTH)) {
                return BlockFace.SOUTH;
            } else if (facing.equalsIgnoreCase(FACE_UP)) {
                return BlockFace.UP;
            } else if (facing.equalsIgnoreCase(FACE_WEST)) {
                return BlockFace.WEST;
            } else {
                return BlockFace.SELF;
            }
        }

        return BlockFace.SELF;
    }
    
    
    /**
     * Covert to variant that does not contain the facing
     * @return 
     */
    public VariantKey toNonFacing() {
        int hash = 0;        
        HashMap<String, String> entries = new HashMap<String, String>();
        
        for (Map.Entry<String, String> entrySet : m_entries.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            
            if (!key.equalsIgnoreCase(FACING)) {
                hash ^= key.hashCode() ^ value.hashCode();
                entries.put(key, value);
            }
        }
        
        return new VariantKey(entries, hash);
    }

    
    private VariantKey(HashMap<String, String> entries, int hash) {
        m_entries = entries;
        m_hash = hash;
    }
    
    public VariantKey(String entry) {
        if (entry == null) {
            entry = "";
        }

        int hash = 0;
        String[] parts = entry.toLowerCase().split(",");
        m_entries  = new HashMap<String, String>();

        for (String s : parts) {
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
        VariantKey other = obj instanceof VariantKey ? (VariantKey) obj : null;

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
