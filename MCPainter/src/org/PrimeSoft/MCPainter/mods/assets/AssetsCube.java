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
public class AssetsCube {

    private final static String PROP_FROM = "from";
    private final static String PROP_TO = "to";
    private final static String PROP_FACES = "faces";
    private final static String PROP_ROTATION = "rotation";

    private final static String PROP_F_UP = "up";
    private final static String PROP_F_DOWN = "down";
    private final static String PROP_F_NORTH = "north";
    private final static String PROP_F_SOUTH = "south";
    private final static String PROP_F_WEST = "west";
    private final static String PROP_F_EAST = "east";

    private final Vector m_from;
    private final Vector m_to;

    private final AssetsFace m_faceUp;
    private final AssetsFace m_faceDown;
    private final AssetsFace m_faceNorth;
    private final AssetsFace m_faceSouth;
    private final AssetsFace m_faceWeast;
    private final AssetsFace m_faceEast;

    private final AssetsRotation m_rotation;

    public AssetsCube(JSONObject data) {
        final double[] from = JSONExtensions.tryGetDoubleArray(data, PROP_FROM);
        final double[] to = JSONExtensions.tryGetDoubleArray(data, PROP_TO);
        final JSONObject faces = JSONExtensions.tryGet(data, PROP_FACES);
        final JSONObject rotation = JSONExtensions.tryGet(data, PROP_ROTATION);

        if (from != null && from.length == 3) {
            m_from = new Vector(from[0], from[1], from[2]);
        } else {
            m_from = null;
        }

        if (to != null && to.length == 3) {
            m_to = new Vector(to[0], to[1], to[2]);
        } else {
            m_to = null;
        }
        
        if (faces == null) {
            m_faceDown = null;
            m_faceEast = null;
            m_faceNorth = null;
            m_faceSouth = null;
            m_faceUp = null;
            m_faceWeast = null;
        } else {
            JSONObject fDown = JSONExtensions.tryGet(faces, PROP_F_DOWN);
            JSONObject fEast = JSONExtensions.tryGet(faces, PROP_F_EAST);
            JSONObject fNorth = JSONExtensions.tryGet(faces, PROP_F_NORTH);
            JSONObject fSouth = JSONExtensions.tryGet(faces, PROP_F_SOUTH);
            JSONObject fUp = JSONExtensions.tryGet(faces, PROP_F_UP);
            JSONObject fWest = JSONExtensions.tryGet(faces, PROP_F_WEST);

            JSONExtensions.printUnused(faces, new String[]{PROP_F_DOWN, PROP_F_EAST,
                PROP_F_NORTH, PROP_F_SOUTH, PROP_F_UP, PROP_F_WEST}, "Unknown assets cube faces: ");

            m_faceDown = fDown != null ? new AssetsFace(fDown) : null;
            m_faceEast = fEast != null ? new AssetsFace(fEast) : null;
            m_faceNorth = fNorth != null ? new AssetsFace(fNorth) : null;
            m_faceSouth = fSouth != null ? new AssetsFace(fSouth) : null;
            m_faceUp = fUp != null ? new AssetsFace(fUp) : null;
            m_faceWeast = fWest != null ? new AssetsFace(fWest) : null;
        }
        
        m_rotation = rotation != null ? new AssetsRotation(rotation) : null;

        JSONExtensions.printUnused(data, 
                new String[]{PROP_FROM, PROP_TO, PROP_FACES, PROP_ROTATION}, 
                "Unknown assets cube property: ");
    }
}
