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
package org.PrimeSoft.MCPainter.MC3D;

import com.sk89q.worldedit.blocks.BaseBlock;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;

/**
 * Util functions used for drawing 3d primitives
 *
 * @author SBPrime
 */
public class Utils3D {

    /**
     * Draw a 3d voxel based triangle
     *
     * @param v vertices
     * @param loger block placer
     * @param colorMap color map
     * @param texture texture
     * @param useAlpha use alpha color
     * @param isGray is this triangle gray
     * @param grayColor gray color swaper
     * @param type drawing block type
     */
    public static void drawTriangle(DoubleVertex[] v, BlockLoger loger,
            ColorMap colorMap, int[][] texture, boolean useAlpha, boolean isGray, 
            int[] grayColor, OperationType type) {
        List<DoubleVertex> blocks = new ArrayList<DoubleVertex>();

        loger.LogMessage("Selecting blocks to fill...");
        drawTriangle(v, blocks);
        
        loger.LogMessage("Drawing blocks...");
        for (DoubleVertex vertex : blocks) {
            Color c = ImageHelper.getColor(texture, vertex.getU(), vertex.getV());
            if (isGray && grayColor != null) {
                c = ImageHelper.getColor(c, grayColor);
            }

            boolean use;
            if (useAlpha) {
                use = c.getAlpha() > ColorMap.ALPHA_THRESHOLD;
            } else {
                use = (c.getRGB() & 0xffffff) != 0;
            }

            if (!use) {
                continue;
            }

            BaseBlock block = colorMap.getBlockForColor(c, type).getBlock();

            //BaseBlock block = new BaseBlock(1);
            double y = vertex.getY();
            if (y >= 0 && y <= 255) {
                loger.LogBlock(vertex.getVector(), block);
            }
        }
    }

    private static void drawTriangle(DoubleVertex[] v, List<DoubleVertex> blocks) {
        double[][] verts = new double[][]{v[0].getArray(), v[1].getArray(), v[2].getArray()};

        final int intIdx = findShortestEdge(verts);
        final int mainIdx = (intIdx + 1) % 3;
        final int secIdx = (mainIdx + 1) % 3;

        Arrays.sort(verts, new Comparator<double[]>() {

            @Override
            public int compare(double[] o1, double[] o2) {
                return (int) Math.signum(o1[mainIdx] - o2[mainIdx]);
            }
        });

        double[] lenFar = new double[5];
        double[] lenUpper = new double[5];
        double[] lenLower = new double[5];
        double[] dFar = new double[5];
        double[] dUpper = new double[5];
        double[] dLower = new double[5];
        double[] xf = new double[5];
        double[] xt = new double[5];

        calcLen(verts, mainIdx, lenFar, lenUpper, lenLower);
        int[] minPos = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        int[] maxPos = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
        for (int i = 0; i < 3; i++) {
            minPos[0] = Math.min(minPos[0], (int) verts[i][mainIdx]);
            maxPos[0] = Math.max(maxPos[0], (int) verts[i][mainIdx]);
            minPos[1] = Math.min(minPos[1], (int) verts[i][secIdx]);
            maxPos[1] = Math.max(maxPos[1], (int) verts[i][secIdx]);
        }


        for (int i = 0; i < 5; i++) {
            dFar[i] = lenFar[i] / Math.abs(lenFar[mainIdx]);
            dUpper[i] = lenUpper[i] / Math.abs(lenUpper[mainIdx]);
            dLower[i] = lenLower[i] / Math.abs(lenLower[mainIdx]);
        }

        for (int i = 0; i < 5; i++) {
            xf[i] = verts[0][i];
            xt[i] = xf[i] + dUpper[i];
        }

        List<double[]> tmp = new ArrayList<double[]>();
        for (double pos = verts[0][mainIdx]; pos < verts[2][mainIdx]; pos++) {
            xf[mainIdx] = pos;
            xt[mainIdx] = pos;

            drawLine(xf, xt, tmp);

            double[] add = pos < verts[1][mainIdx] ? dUpper : dLower;
            for (int i = 0; i < 5; i++) {
                xf[i] += dFar[i];
                xt[i] += add[i];
            }
        }

        interpolate(mainIdx, secIdx, intIdx, minPos, maxPos, tmp, blocks);
    }

    private static void drawLine(double[] pos1, double[] pos2, List<double[]> blocks) {
        double[] l = new double[5];
        double[] p = new double[5];

        for (int i = 0; i < 5; i++) {
            l[i] = pos2[i] - pos1[i];
            p[i] = pos1[i];
        }

        int maxIdx = 0;
        double max = -1;
        for (int i = 0; i < 3; i++) {
            double tmp = Math.abs(l[i]);
            if (tmp > max) {
                max = tmp;
                maxIdx = i;
            }
        }

        double[] d = new double[5];
        for (int i = 0; i < 5; i++) {
            d[i] = Math.abs(max) >= 1 ? l[i] / max : 0;
        }

        blocks.add(new double[]{p[0], p[1], p[2], p[3], p[4]});
        for (int j = 0; j < max; j++) {
            for (int i = 0; i < 5; i++) {
                p[i] += d[i];
            }

            blocks.add(new double[]{p[0], p[1], p[2], p[3], p[4]});
        }
    }

    private static void interpolate(int mainIdx, int secIdx, int intIdx,
            int[] min, int[] max,
            List<double[]> tmp, List<DoubleVertex> blocks) {
        final int lenMain = max[0] - min[0] + 1;
        final int lenSec = max[1] - min[1] + 1;

        double[][][] map = new double[lenMain][lenSec][];

        for (double[] d : tmp) {
            int main = (int) d[mainIdx] - min[0];
            int sec = (int) d[secIdx] - min[1];

            if (main < 0 || sec < 0) {
                continue;
            }
            if (main >= lenMain || sec >= lenSec) {
                continue;
            }

            map[main][sec] = d;
        }

        double[] p11, p21;
        double[] p12;

        tmp.clear();
        for (int main = 0; main < lenMain; main++) {
            for (int sec = 0; sec < lenSec; sec++) {
                p11 = map[main][sec];
                if (p11 == null) {
                    continue;
                }

                p21 = null;
                p12 = null;
                int a = 1;
                while (p21 == null && sec + a < lenSec) {
                    p21 = map[main][sec + a];
                    a++;
                }

                a = 1;
                while (p12 == null && main + a < lenMain) {
                    p12 = map[main + a][sec];
                    a++;
                }

                if (p21 != null) {
                    drawLine(p11, p21, tmp);
                }
                if (p12 != null) {
                    drawLine(p11, p12, tmp);
                }
                tmp.add(p11);
            }
        }
        for (double[] p : tmp) {
            blocks.add(new DoubleVertex((int)p[0], (int)p[1], (int)p[2], p[3], p[4]));
        }
    }

    /**
     * Find shortest edge for axis
     * @param verts Vertices
     * @return Axis ID (0 - x, 1 - y, 2 - z)
     */
    private static int findShortestEdge(double[][] verts) {
        double[] max = new double[]{-1, -1, -1};
        for (int i = 0; i < 3; i++) {
            double[] v1 = verts[i];
            double[] v2 = verts[(i + 1) % 3];
            for (int j = 0; j < 3; j++) {
                double l = Math.abs(v1[j] - v2[j]);
                max[j] = Math.max(max[j], l);
            }
        }

        double min = Double.POSITIVE_INFINITY;
        int intIdx = -1;
        for (int i = 0; i < 3; i++) {
            if (max[i] < min) {
                min = max[i];
                intIdx = i;
            }
        }

        return intIdx;
    }

    /**
     * Calculate length between vertices
     *
     * @param verts vertices
     * @param mainIdx main axis id
     * @param lenFar furthest edge length
     * @param lenUpper upper edge length
     * @param lenLower lower edge length
     */
    private static void calcLen(double[][] verts, int mainIdx,
            double[] lenFar, double[] lenUpper, double[] lenLower) {
        for (int i = 0; i < 5; i++) {
            lenFar[i] = verts[2][i] - verts[0][i];
            lenUpper[i] = verts[1][i] - verts[0][i];
            lenLower[i] = verts[2][i] - verts[1][i];
        }

        if (Math.abs(lenFar[mainIdx]) < 1) {
            lenFar[mainIdx] = 1;
        }
        if (Math.abs(lenUpper[mainIdx]) < 1) {
            lenUpper[mainIdx] = 1;
        }
        if (Math.abs(lenLower[mainIdx]) < 1) {
            lenLower[mainIdx] = 1;
        }
    }
}