/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
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
package org.primesoft.mcpainter.utils;

import org.bukkit.Location;

/**
 *
 * @author SBPrime
 */
public class Vector {

    private final double m_x;
    private final double m_y;
    private final double m_z;

    public double getX() {
        return m_x;
    }

    public double getY() {
        return m_y;
    }

    public double getZ() {
        return m_z;
    }

    public Vector(double x, double y, double z) {
        m_x = x;
        m_y = y;
        m_z = z;
    }

    public Vector(Location location) {
        m_x = location.getX();
        m_y = location.getY();
        m_z = location.getZ();
    }

    public Vector(Vector v) {
        m_x = v.m_x;
        m_y = v.m_y;
        m_z = v.m_z;
    }
    
    public Vector() {
        m_x = 0;
        m_y = 0;
        m_z = 0;
    }

    public Vector add(Vector v) {
        return new Vector(m_x + v.m_x, m_y + v.m_y, m_z + v.m_z);
    }

    public Vector add(int x, int y, int z) {
        return new Vector(m_x + x, m_y + y, m_z + z);
    }

    public int getBlockX() {
        return (int)Math.round(m_x);
    }
    
    public int getBlockY() {
        return (int)Math.round(m_y);
    }
    
    public int getBlockZ() {
        return (int)Math.round(m_z);
    }

    public Vector subtract(Vector v) {
        return new Vector(m_x - v.m_x, m_y - v.m_y, m_z - v.m_z);
    }
    
    public static Vector parse(String s) {
        if (s == null) {
            return null;
        }

        String[] parts = s.split(",");
        if (parts.length != 3) {
            return null;
        }

        try {
            return new Vector(Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
