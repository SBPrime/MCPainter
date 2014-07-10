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
package org.PrimeSoft.MCPainter.voxelyzer;

import java.util.Arrays;
import java.util.Comparator;
import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.RawImage;

/**
 *
 * @author Prime
 */
public class Triangle {

    /**
     * The vertex comparator
     */
    private final static Comparator<Vertex> s_vertexCompatator = new Comparator<Vertex>() {

        @Override
        public int compare(Vertex o1, Vertex o2) {
            return (int) (Math.signum(o1.getX() - o2.getX()));
        }
    };

    /**
     * Render a voxel triangle
     *
     * @param loger Block logger
     * @param colorMap Color block mapper
     * @param texture Texture
     * @param p1 Vertex 1
     * @param p2 Vertex 2
     * @param p3 Vertex 3
     */
    public static void drawTriangle(BlockLoger loger, ColorMap colorMap,
            ClippingRegion clipping,
            RawImage texture, Vertex p1, Vertex p2, Vertex p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return;
        }
        p1 = p1.round();
        p2 = p2.round();
        p3 = p3.round();

        double[][] map = prepareMap(new Vertex[]{p1, p2, p3});
        double[][] tMap = transponMap(map);

        Vertex[] t = new Vertex[]{
            Vertex.map(p1, map),
            Vertex.map(p2, map),
            Vertex.map(p3, map)
        };

        Arrays.sort(t, s_vertexCompatator);

        final Vertex lenFar = Vertex.sub(t[2], t[0]);
        final Vertex lenUpper = Vertex.sub(t[1], t[0]);
        final Vertex lenLower = Vertex.sub(t[2], t[1]);

        if (Math.abs(lenFar.getX()) < 1) {
            lenFar.setX(1);
        }
        if (Math.abs(lenUpper.getX()) < 1) {
            lenUpper.setX(1);
        }
        if (Math.abs(lenLower.getX()) < 1) {
            lenLower.setX(1);
        }

        Vertex dFar = Vertex.div(lenFar, Math.abs(lenFar.getX()));
        Vertex dUpper = Vertex.div(lenUpper, Math.abs(lenUpper.getX()));
        Vertex dLower = Vertex.div(lenLower, Math.abs(lenLower.getX()));

        Vertex xf = new Vertex(t[0]);
        Vertex xt = new Vertex(t[0]);

        final VoxelCanvas canvas = new VoxelCanvas(t);
        double div = getDiv(dFar, dUpper, dLower);
        dFar = Vertex.div(dFar, div);
        dUpper = Vertex.div(dUpper, div);
        dLower = Vertex.div(dLower, div);
        double kPos = 1 / div;

        final double x1 = t[1].getX();
        final double x2 = t[2].getX();
        double pos = t[0].getX();

        while (pos < x1) {
            xf.setX(pos);
            xt.setX(pos);
            drawLine(clipping, xf.round(), xt.round(), canvas);
            xf = Vertex.add(xf, dFar);
            xt = Vertex.add(xt, dUpper);
            pos += kPos;
        }
        drawLine(clipping, xf.round(), xt.round(), canvas);
        pos = x1;
        xt = new Vertex(t[1]);
        while (pos < x2) {
            xf.setX(pos);
            xt.setX(pos);
            drawLine(clipping, xf.round(), xt.round(), canvas);

            xf = Vertex.add(xf, dFar);
            xt = Vertex.add(xt, dLower);
            pos += kPos;
        }
        drawLine(clipping, xf.round(), xt.round(), canvas);

        canvas.render(loger, colorMap, texture, tMap);
    }

    private static double getDiv(final Vertex dFar, final Vertex dUpper, final Vertex dLower) {
        double div = 1;
        double[] far = Vertex.abs(dFar).getData();
        double[] upper = Vertex.abs(dUpper).getData();
        double[] lower = Vertex.abs(dLower).getData();
        for (int i = 0; i < 2; i++) {
            if (far[i] > div) {
                div = far[i];
            }
            if (upper[i] > div) {
                div = upper[i];
            }
            if (lower[i] > div) {
                div = lower[i];
            }
        }
        return div;
    }

    /**
     * Draw a voxel line
     *
     * @param xf
     * @param xt
     * @param canvas
     */
    private static void drawLine(ClippingRegion clipping,
            Vertex xf, Vertex xt, VoxelCanvas canvas) {
        final Vertex length = Vertex.sub(xt, xf);
        final int lY = (int) Math.round(Math.abs(length.getY()));
        final int lZ = (int) Math.round(Math.abs(length.getZ()));
        final int l = Math.max(Math.max(lY, lZ), 1);

        final Vertex delta = Vertex.div(length, l);

        Vertex pp = new Vertex(xf);

        for (int p = 0; p <= l; p++) {
            if (clipping == null || clipping.testVertex(pp)) {
                try {
                    canvas.putPixel(pp.round());
                } catch (Exception ex) {
                    System.out.println("E: " + pp);
                }
            }
            pp = Vertex.add(pp, delta);
        }
    }

    /**
     * Prepare mapping matrix
     *
     * @param p
     * @return
     */
    private static double[][] prepareMap(Vertex[] p) {
        double[][] map = new double[Vertex.ARRAY_SIZE][Vertex.ARRAY_SIZE];
        for (int i = 0; i < Vertex.ARRAY_SIZE; i++) {
            for (int j = 0; j < Vertex.ARRAY_SIZE; j++) {
                if (i >= 3 || j >= 3) {
                    map[i][j] = i == j ? 1 : Double.NaN;
                }
            }
        }
        double[] min = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY};
        double[] max = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY};
        double[] delta = new double[3];

        final double[] len01 = Vertex.abs(Vertex.sub(p[0], p[1])).getData();
        final double[] len02 = Vertex.abs(Vertex.sub(p[0], p[2])).getData();
        final double[] len12 = Vertex.abs(Vertex.sub(p[1], p[2])).getData();

        for (int i = 0; i < 3; i++) {
            final double[] data = p[i].getData();

            for (int j = 0; j < 3; j++) {
                if (min[j] > data[j]) {
                    min[j] = data[j];
                }
                if (max[j] < data[j]) {
                    max[j] = data[j];
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            delta[i] = max[i] - min[i];
        }

        int[] pp = new int[]{0, 1, 2};

        int p0 = 0;
        int p1 = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                boolean doSwap = false;

                if (!doSwap && delta[i] < delta[j]) {
                    doSwap = true;
                }

                if (!doSwap) {
                    final double[] d = new double[]{len01[i] - len01[j],
                        len02[i] - len02[j], len12[i] - len12[j]};

                    double before = 0;
                    double after = 0;
                    for (int k = 0; k < 3; k++) {
                        if (d[k] > 0) {
                            before += d[k];
                        } else if (d[k] < 0) {
                            after -= d[k];
                        }
                    }

                    if (after > before) {
                        doSwap = true;
                    }
                }

                if (!doSwap) {
                    int same = 0;
                    int sameSwap = 0;
                    final int pp0, pp1;

                    if (i == p0) {
                        pp0 = j;
                    } else if (j == p0) {
                        pp0 = i;
                    } else {
                        pp0 = p0;
                    }

                    if (i == p1) {
                        pp1 = j;
                    } else if (j == p1) {
                        pp1 = i;
                    } else {
                        pp1 = p1;
                    }

                    for (int k = 0; k < 3; k++) {
                        double[] dataK = p[k].getData();
                        for (int l = k + 1; l < 3; l++) {
                            double[] dataL = p[l].getData();

                            if (dataK[p0] == dataL[p0] && dataK[p1] == dataL[p1]) {
                                same++;
                            }

                            if (dataK[pp0] == dataL[pp0] && dataK[pp1] == dataL[pp1]) {
                                sameSwap++;
                            }

                        }
                    }
                    if (sameSwap < same) {
                        doSwap = true;
                    }
                }

                if (doSwap) {
                    double t1 = delta[i];
                    delta[i] = delta[j];
                    delta[j] = t1;

                    t1 = len01[i];
                    len01[i] = len01[j];
                    len01[j] = t1;

                    t1 = len02[i];
                    len02[i] = len02[j];
                    len02[j] = t1;

                    t1 = len12[i];
                    len12[i] = len12[j];
                    len12[j] = t1;

                    int t2 = pp[i];
                    pp[i] = pp[j];
                    pp[j] = t2;

                    if (i == p0) {
                        p0 = j;
                    } else if (j == p0) {
                        p0 = i;
                    }

                    if (i == p1) {
                        p1 = j;
                    } else if (j == p1) {
                        p1 = i;
                    }
                }
            }
        }

        for (int i = 0;
                i < 3; i++) {
            map[i][pp[i]] = 1;
        }
        return map;
    }

    /**
     * Calculate transponent matrix
     *
     * @param map
     * @return
     */
    private static double[][] transponMap(double[][] map) {
        double[][] result = new double[Vertex.ARRAY_SIZE][Vertex.ARRAY_SIZE];

        for (int i = 0; i < Vertex.ARRAY_SIZE; i++) {
            for (int j = 0; j < Vertex.ARRAY_SIZE; j++) {
                result[j][i] = map[i][j];
            }
        }

        return result;
    }
}
