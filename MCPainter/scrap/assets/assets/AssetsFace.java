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
package org.primesoft.mcpainter.mods.assets;

import org.primesoft.mcpainter.utils.JSONExtensions;
import org.json.simple.JSONObject;

/**
 *
 * @author SBPrime
 */
public class AssetsFace {

    private final static String PROP_UV = "uv";
    private final static String PROP_TEXTURE = "texture";
    private final static String PROP_ROTATION = "rotation";

    private final String m_texture;
    private final double m_u1;
    private final double m_u2;
    private final double m_v1;
    private final double m_v2;
    private final double m_rotation;

    public String getTexture() {
        return m_texture;
    }

    /**
     * Get the rotatet texture UV map
     *
     * @return
     */
    public double[] getUV() {
        double[] uvMap = new double[]{
            m_u1, m_v1, m_u2, m_v1,
            m_u2, m_v2, m_u1, m_v2
        };
        
        int offset = 2 * ((int) m_rotation / 90) % 4;
        return new double[]{
            uvMap[(0 + offset) % uvMap.length], uvMap[(1 + offset) % uvMap.length],
            uvMap[(2 + offset) % uvMap.length], uvMap[(3 + offset) % uvMap.length],
            uvMap[(6 + offset) % uvMap.length], uvMap[(7 + offset) % uvMap.length],
            uvMap[(4 + offset) % uvMap.length], uvMap[(5 + offset) % uvMap.length]
        };
    }

    public AssetsFace(JSONObject data, double sx, double sy) {
        double[] uv = JSONExtensions.tryGetDoubleArray(data, PROP_UV);
        String texture = JSONExtensions.tryGetString(data, PROP_TEXTURE, null);

        if (texture != null && texture.startsWith("#")) {
            m_texture = texture.substring(1);
        } else {
            m_texture = texture;
        }

        m_rotation = JSONExtensions.tryGetDouble(data, PROP_ROTATION, 0.0);

        m_u1 = uv != null && uv.length > 0 ? uv[0] : 0;
        m_v1 = uv != null && uv.length > 1 ? uv[1] : 0;
        m_u2 = uv != null && uv.length > 2 ? uv[2] : sx;
        m_v2 = uv != null && uv.length > 3 ? uv[3] : sy;

//        JSONExtensions.printUnused(data, 
//                new String[]{PROP_ROTATION, PROP_TEXTURE, PROP_UV}, 
//                "Unknown assets face property: ");
    }
}
