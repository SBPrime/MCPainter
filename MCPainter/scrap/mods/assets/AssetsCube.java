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

import com.sk89q.worldedit.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.texture.TextureEntry;
import org.primesoft.mcpainter.utils.InOutParam;
import org.primesoft.mcpainter.utils.JSONExtensions;
import org.primesoft.mcpainter.voxelyzer.Face;
import org.primesoft.mcpainter.voxelyzer.Matrix;
import org.primesoft.mcpainter.voxelyzer.Vertex;
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

            final double[] size = new double[3];
            for (int i = 0; i < 3; i++) {
                size[i] = (to != null && to.length > i ? to[i] : 0) - (from != null && from.length > i ? from[i] : 0);
            }

//            JSONExtensions.printUnused(faces, new String[]{PROP_F_DOWN, PROP_F_EAST,
//                PROP_F_NORTH, PROP_F_SOUTH, PROP_F_UP, PROP_F_WEST}, "Unknown assets cube faces: ");
            m_faceDown = fDown != null ? new AssetsFace(fDown, size[0], size[2]) : null;
            m_faceEast = fEast != null ? new AssetsFace(fEast, size[2], size[1]) : null;
            m_faceNorth = fNorth != null ? new AssetsFace(fNorth, size[0], size[1]) : null;
            m_faceSouth = fSouth != null ? new AssetsFace(fSouth, size[0], size[1]) : null;
            m_faceUp = fUp != null ? new AssetsFace(fUp, size[0], size[2]) : null;
            m_faceWeast = fWest != null ? new AssetsFace(fWest, size[2], size[1]) : null;
        }

        m_rotation = rotation != null ? new AssetsRotation(rotation) : null;

//        JSONExtensions.printUnused(data, 
//                new String[]{PROP_FROM, PROP_TO, PROP_FACES, PROP_ROTATION}, 
//                "Unknown assets cube property: ");
    }

    public CompiledCube compile(HashMap<String, TextureEntry> textures) {
        if (m_from == null || m_to == null) {
            return null;
        }

        final InOutParam<Vector> fromO = InOutParam.Out();
        final InOutParam<Vector> sizeO = InOutParam.Out();

        getOffsetAndSize(m_from, m_to, fromO, sizeO);

        final Vector from = fromO.getValue();
        final Vector size = sizeO.getValue();

        final AssetsFace[] faces = filterFaces(m_faceUp, m_faceDown, m_faceSouth, m_faceNorth,
                m_faceWeast, m_faceEast, size, textures);

        final Vertex[] modelVertex = createVertices(from, size,
                m_rotation != null ? m_rotation.getMatrix() : Matrix.getIdentity(),
                m_rotation != null && m_rotation.isRescaling());

        final Face[] modelFace = createFaces(faces, modelVertex, textures);

        if (modelFace == null || modelFace.length == 0) {
            return null;
        }

        return new CompiledCube(modelVertex, modelFace);
    }

    /**
     * Filter the provided faces based on the size and texture availablility
     *
     * @param faceUp
     * @param faceDown
     * @param faceSouth
     * @param faceNorth
     * @param faceWeast
     * @param faceEast
     * @param size
     * @param textures
     * @return
     */
    private AssetsFace[] filterFaces(AssetsFace faceUp, AssetsFace faceDown,
            AssetsFace faceSouth, AssetsFace faceNorth,
            AssetsFace faceWeast, AssetsFace faceEast,
            final Vector size, final HashMap<String, TextureEntry> textures) {

        if (faceUp != null && !textures.containsKey(faceUp.getTexture())) {
            faceUp = null;
        }
        if (faceDown != null && !textures.containsKey(faceDown.getTexture())) {
            faceDown = null;
        }
        if (faceSouth != null && !textures.containsKey(faceSouth.getTexture())) {
            faceSouth = null;
        }
        if (faceNorth != null && !textures.containsKey(faceNorth.getTexture())) {
            faceNorth = null;
        }
        if (faceWeast != null && !textures.containsKey(faceWeast.getTexture())) {
            faceWeast = null;
        }
        if (faceEast != null && !textures.containsKey(faceEast.getTexture())) {
            faceEast = null;
        }

        if (size.getX() == 0 && faceWeast != null && faceEast != null) {
            faceWeast = null;
        }

        if (size.getY() == 0 && faceUp != null && faceDown != null) {
            faceDown = null;
        }

        if (size.getZ() == 0 && faceNorth != null && faceSouth != null) {
            faceNorth = null;
        }

        return new AssetsFace[]{faceUp, faceDown, faceSouth, faceNorth, faceWeast, faceEast};
    }

    /**
     * Get the size and offset for cube
     *
     * @param from
     * @param to
     * @param min
     * @param size
     */
    private static void getOffsetAndSize(Vector from, Vector to,
            InOutParam<Vector> min, InOutParam<Vector> size) {
        double x1, x2;
        double y1, y2;
        double z1, z2;

        x1 = from.getX();
        y1 = from.getY();
        z1 = from.getZ();

        x2 = to.getX();
        y2 = to.getY();
        z2 = to.getZ();

        double t;
        if (x2 < x1) {
            t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y2 < x1) {
            t = x1;
            y1 = x2;
            y2 = t;
        }
        if (z2 < z1) {
            t = z1;
            z1 = z2;
            z2 = t;
        }

        min.setValue(new Vector(x1, y1, z1));
        size.setValue(new Vector(x2 - x1, y2 - y1, z2 - z1));
    }

    /**
     * Calculate the vertices
     *
     * @param from
     * @param size
     * @param matrix
     * @return
     */
    private static Vertex[] createVertices(Vector from, Vector size, Matrix matrix, boolean resize) {
        double x = from.getX();
        double y = from.getY();
        double z = from.getZ();
        double sx = size.getX() - 1;
        double sy = size.getY() - 1;
        double sz = size.getZ() - 1;

        if (sx < 0) {
            sx = 0;
        }
        if (sy < 0) {
            sy = 0;
        }
        if (sz < 0) {
            sz = 0;
        }

        Vertex[] result = new Vertex[]{
            new Vertex(x, y, z), new Vertex(x + sx, y, z),
            new Vertex(x, y + sy, z), new Vertex(x + sx, y + sy, z),
            new Vertex(x, y, z + sz), new Vertex(x + sx, y, z + sz),
            new Vertex(x, y + sy, z + sz), new Vertex(x + sx, y + sy, z + sz)
        };

        double maxx = Double.MIN_VALUE;
        double maxy = Double.MIN_VALUE;
        double maxz = Double.MIN_VALUE;

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double minz = Double.MAX_VALUE;

        for (int i = 0; i < result.length; i++) {
            Vertex t = matrix.applyMatrix(result[i]);

            result[i] = t;

            double tx = t.getX();
            double ty = t.getY();
            double tz = t.getZ();

            if (tx < minx) {
                minx = tx;
            }
            if (ty < miny) {
                miny = ty;
            }
            if (tz < minz) {
                minz = tz;
            }

            if (tx > maxx) {
                maxx = tx;
            }
            if (ty > maxy) {
                maxy = ty;
            }
            if (tz > maxz) {
                maxz = tz;
            }
        }

        sx = Math.max(0, maxx - minx) + 1;
        sy = Math.max(0, maxy - miny) + 1;
        sz = Math.max(0, maxz - minz) + 1;

        
        //TODO: Resize is broken
        if (resize
                && (sx < ConfigProvider.BLOCK_SIZE || sy < ConfigProvider.BLOCK_SIZE || sz < ConfigProvider.BLOCK_SIZE)) {
            matrix = Matrix.getScaling(ConfigProvider.BLOCK_SIZE / sx, ConfigProvider.BLOCK_SIZE / sy, ConfigProvider.BLOCK_SIZE / sz);

            for (int i = 0; i < result.length; i++) {
                result[i] = matrix.applyMatrix(result[i]);
            }
        }

        return result;
    }

    /**
     * Create the face based on the asset face
     *
     * @param faces
     * @param modelVertex
     * @return
     */
    private static Face[] createFaces(AssetsFace[] faces, Vertex[] modelVertex,
            HashMap<String, TextureEntry> textures) {
        final int[][] verticeIndex = new int[][]{
            new int[]{6, 7, 2, 3}, //UP
            new int[]{4, 5, 0, 1}, //DOWN
            new int[]{2, 3, 0, 1}, //SOUTH
            new int[]{7, 6, 5, 4}, //NORTH
            new int[]{6, 2, 4, 0}, //WEAST
            new int[]{3, 7, 1, 5} //EAST
        };

        final List<Face> result = new ArrayList<Face>();

        for (int i = 0; i < 6; i++) {
            AssetsFace aFace = faces[i];
            if (aFace == null) {
                continue;
            }

            final TextureEntry textureEntry = textures.get(aFace.getTexture());
            final RawImage img = textureEntry.getImages()[0];
            final int[] vIdx = verticeIndex[i];
            final double[] uv = aFace.getUV();

            Face f1 = new Face(new int[]{vIdx[0], vIdx[1], vIdx[2]},
                    img,
                    new double[][]{
                        new double[]{uv[0], uv[1]},
                        new double[]{uv[2], uv[3]},
                        new double[]{uv[4], uv[5]}
                    });

            Face f2 = new Face(new int[]{vIdx[3], vIdx[1], vIdx[2]},
                    img,
                    new double[][]{
                        new double[]{uv[6], uv[7]},
                        new double[]{uv[2], uv[3]},
                        new double[]{uv[4], uv[5]}
                    });

            result.add(f1);
            result.add(f2);
        }

        return result.toArray(new Face[0]);
    }
}
