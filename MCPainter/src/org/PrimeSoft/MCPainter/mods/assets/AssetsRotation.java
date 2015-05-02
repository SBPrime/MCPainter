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

import com.sk89q.worldedit.Vector;
import org.PrimeSoft.MCPainter.utils.JSONExtensions;
import org.json.simple.JSONObject;

/**
 *
 * @author SBPrime
 */
public class AssetsRotation {

    private final static String PROP_ORIGIN = "origin";
    private final static String PROP_AXIS = "axis";
    private final static String PROP_ANGLE = "angle";
    private final static String PROP_RESCALE = "rescale";

    private final Vector m_origin;
    private final String m_axis;
    private final double m_angle;
    private final boolean m_rescale;

    public AssetsRotation(JSONObject data) {
        double[] origin = JSONExtensions.tryGetDoubleArray(data, PROP_ORIGIN);
        m_axis = JSONExtensions.tryGetString(data, PROP_AXIS, null);
        m_angle = JSONExtensions.tryGetDouble(data, PROP_ANGLE, 0);
        m_rescale = JSONExtensions.tryGetBool(data, PROP_RESCALE, false);

        if (origin != null && origin.length == 3) {
            m_origin = new Vector(origin[0], origin[1], origin[2]);
        } else {
            m_origin = null;
        }

        JSONExtensions.printUnused(data, new String[]{
            PROP_ANGLE, PROP_AXIS, PROP_ORIGIN, PROP_RESCALE
        }, "Unknown assets rotation properties: ");
    }
}
