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
package org.PrimeSoft.MCPainter.utils;

import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;
import org.apache.commons.lang.NullArgumentException;

/**
 *
 * @author SBPrime
 */
public class Utils {
    public static Vector getPlayerPos(ILocalPlayer player) {
        Vector location = player.getPosition();
        double x = (int) location.getX();
        double y = (int) location.getY();
        double z = (int) location.getZ();

        if (x < 0) {
            x -= 0.6;
        } else {
            x += 0.4;
        }
        if (z < 0) {
            z -= 0.6;
        } else {
            z += 0.4;
        }

        return new Vector(x, y, z);
    }

    public static boolean tryParse(String s, InOutParam<Integer> out) {
        if (out == null) {
            throw new NullArgumentException("out");
        }

        if (s == null || s.isEmpty()) {
            return false;
        }

        try {
            out.setValue(Integer.parseInt(s));
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
