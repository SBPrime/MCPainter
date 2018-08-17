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
package org.primesoft.mcpainter.voxelyzer;

import java.awt.Color;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.Material;

/**
 *
 * @author SBPrime
 */
public class VoxelCanvas {

    private final static BaseBlock STONE = new BaseBlock(Material.STONE);
    private final int m_minX;
    private final int m_minY;
    private final int m_resX;
    private final int m_resY;
    private final double[][][] m_canvasA;
    private final double[][][] m_canvasB;

    public VoxelCanvas(Vertex[] p) {
        final double[] min = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        final double[] max = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};

        for (Vertex p1 : p) {
            final double[] value = p1.getData();
            for (int j = 0; j < 2; j++) {
                if (min[j] > value[j]) {
                    min[j] = value[j];
                }
                if (max[j] < value[j]) {
                    max[j] = value[j];
                }
            }
        }

        m_minX = (int) min[0] - 1;
        m_minY = (int) min[1] - 1;

        int maxX = (int) max[0] + 1;
        int maxY = (int) max[1] + 1;
        m_resX = maxX - m_minX + 1;
        m_resY = maxY - m_minY + 1;
        m_canvasA = new double[m_resX][m_resY][];
        m_canvasB = new double[m_resX][m_resY][];
    }

    public void putPixel(Vertex p) {
        double[] data = p.getData();

        int x = (int) data[0] - m_minX;
        int y = (int) data[1] - m_minY;
        int z = (int) data[2];

        if (x < 0 || y < 0 || x >= m_resX || y >= m_resY) {
            //This should not happen but in case it does...
            //System.out.println("E: " + p + " " + x + " " + y + " " + 
            //        m_minX + " " + m_minY + " " +
            //        m_resX + " " + m_resY);
            return;
        }
        if (m_canvasA[x][y] == null) {
            m_canvasA[x][y] = (double[]) (data.clone());
            m_canvasB[x][y] = (double[]) (data.clone());
        } else {
            final double zA = m_canvasA[x][y][2];
            final double zB = m_canvasB[x][y][2];
            if (z < zA) {
                m_canvasA[x][y] = (double[]) (data.clone());
            }
            if (z > zB) {
                m_canvasB[x][y] = (double[]) (data.clone());
            }
        }
    }

    private void expand() {
        double[][] rowA = m_canvasA[0];
        double[][] rowB = m_canvasB[0];
        for (int i = 1; i < m_resX; i++) {
            double[][] nextRowA = m_canvasA[i];
            double[][] nextRowB = m_canvasB[i];

            double[] A00 = rowA[0];
            double[] A01 = nextRowA[0];
            double[] B00 = rowB[0];
            double[] B01 = nextRowB[0];
            for (int j = 1; j < m_resY; j++) {
                double[] A10 = rowA[j];
                double[] A11 = nextRowA[j];
                double[] B10 = rowB[j];
                double[] B11 = nextRowB[j];

                expandB(B00, A10);
                expandB(B00, A11);
                expandB(B00, A01);
                expandA(A00, B10);
                expandA(A00, B11);
                expandA(A00, B01);

                A00 = A10;
                A01 = A11;
                B00 = B10;
                B01 = B11;
            }

            rowA = nextRowA;
            rowB = nextRowB;
        }
    }

    private static void expandB(double[] B, double[] A) {
        if (A == null || B == null || B[2] >= A[2]) {
            return;
        }

        B[2] = A[2];
    }

    private static void expandA(double[] A, double[] B) {
        if (A == null || B == null || A[2] <= B[2]) {
            return;
        }

        A[2] = B[2];
    }

    public void render(Vector origin, 
            BlockLoger loger, IColorMap colorMap, RawImage texture, double[][] map) {
        expand();
        for (int i = 0; i < m_resX; i++) {
            for (int j = 0; j < m_resY; j++) {
                double[] pA = m_canvasA[i][j];
                double[] pB = m_canvasB[i][j];
                if (pA != null) {

                    //System.out.println(pA[0] + " " + pA[1] + " " + pA[2] + " " + pB[2]);
                    double[] vA = new double[Vertex.ARRAY_SIZE];
                    double[] vB = new double[Vertex.ARRAY_SIZE];

                    for (int k = 0; k < Vertex.ARRAY_SIZE; k++) {
                        double sumA = Double.NaN;
                        double sumB = Double.NaN;
                        for (int l = 0; l < Vertex.ARRAY_SIZE; l++) {
                            final double m = map[k][l];
                            if (!Double.isNaN(m)) {
                                if (!Double.isNaN(pA[l])) {
                                    if (Double.isNaN(sumA)) {
                                        sumA = 0;
                                    }
                                    sumA += map[k][l] * pA[l];
                                }
                                if (!Double.isNaN(pB[l])) {
                                    if (Double.isNaN(sumB)) {
                                        sumB = 0;
                                    }
                                    sumB += map[k][l] * pB[l];
                                }
                            }
                        }
                        vA[k] = sumA;
                        vB[k] = sumB;
                    }

                    Vertex v1 = new Vertex(vA);
                    Vertex v2 = new Vertex(vB);
                    drawLine(origin, loger, colorMap, texture, v1, v2);
                }
            }
        }
    }

//    public void render(BlockLoger loger, IColorMap colorMap, RawImage texture, double[][] map) {
//        for (int i = 0; i < m_resX; i++) {
//            for (int j = 0; j < m_resY; j++) {
//                double[] p = m_canvas[i][j];
//                double[] pR = i + 1 < m_resX ? m_canvas[i + 1][j] : null;
//                double[] pD = j + 1 < m_resY ? m_canvas[i][j + 1] : null;
//                if (p != null) {
//
//                    double[][] v = new double[3][];
//                    v[0] = new double[Vertex.ARRAY_SIZE];
//                    if (pR != null) {
//                        v[1] = new double[Vertex.ARRAY_SIZE];
//                    }
//                    if (pD != null) {
//                        v[2] = new double[Vertex.ARRAY_SIZE];
//                    }
//                    for (int k = 0; k < Vertex.ARRAY_SIZE; k++) {
//                        double sum = 0;
//                        double sumR = 0;
//                        double sumD = 0;
//                        for (int l = 0; l < Vertex.ARRAY_SIZE; l++) {
//                            sum += map[k][l] * p[l];
//                            if (pR != null) {
//                                sumR += map[k][l] * pR[l];
//                            }
//                            if (pD != null) {
//                                sumD += map[k][l] * pD[l];
//                            }
//                        }
//                        v[0][k] = sum;
//                        if (pR != null) {
//                            v[1][k] = sumR;
//                        }
//                        if (pD != null) {
//                            v[2][k] = sumD;
//                        }
//                    }
//
//
//                    //TODO: Implement color acquiring
//                    if (pD == null && pR == null) {
//                        loger.LogBlock(new Vector(v[0][0], v[0][1], v[0][2]), new BaseBlock(1));
//                    } else {
//                        Vertex vvR = pR != null ? new Vertex(v[1]) : null;
//                        Vertex vvD = pD != null ? new Vertex(v[2]) : null;
//                        Vertex vv = new Vertex(v[0]);
//                        if (vvR != null) {
//                            drawLine(loger, colorMap, texture, vv, vvR);
//                        }
//                        if (pD != null) {
//                            drawLine(loger, colorMap, texture, vv, vvD);
//                        }
//                    }
//                }
//            }
//        }
//    }
    /**
     * Check if provided value is a valid RGB
     *
     * @param val
     * @return
     */
    private static boolean isValidRGB(double val) {
        if (Double.isNaN(val)) {
            return false;
        }

        val = Math.round(val);
        return val >= 0 && val <= 255;
    }

    private void drawLine(Vector origin, 
            BlockLoger loger, IColorMap colorMap, RawImage texture, Vertex p1, Vertex p2) {
        final Vertex len = Vertex.sub(p2, p1);
        final int cnt = (int) Math.round(Math.max(len.getX(), Math.max(len.getY(), len.getZ())));
        final Vertex delta = Vertex.div(len, cnt);
        final double w = texture != null ? texture.getWidth() : 1;
        final double h = texture != null ? texture.getHeight() : 1;
        final int[][] textureImage = texture != null ? texture.getImage() : null;

        Vertex pp = new Vertex(p1);
        for (int i = 0; i <= cnt; i++) {
            final BaseBlock block;
            final Color color;
            final double[] data = pp.getData();

            if (textureImage != null && !Double.isNaN(data[3]) && !Double.isNaN(data[4])) {
                double u = data[3];
                double v = data[4];

                int uC = (int) (u / w);
                int vC = (int) (v / h);
                u = u - w * uC;
                v = v - h * vC;
                if (u < 0) {
                    u = w - u;
                }
                if (v < 0) {
                    v = h - v;
                }

                color = ImageHelper.getColor(textureImage, u, v);
            } else if (isValidRGB(data[3]) && isValidRGB(data[4]) && isValidRGB(data[5])) {
                color = new Color((int) Math.round(data[3]), (int) Math.round(data[4]), (int) Math.round(data[5]), 0xff);
            } else {
                color = null;
            }
           
            Vector l = new Vector(data[0], data[1], data[2]);
            if (color != null) {                
                colorMap.getBlockForColor(color, OperationType.Statue)
                        .place(origin, l, loger);
            } else {
                loger.logBlock(l, STONE);
            }
            pp = Vertex.add(pp, delta);
        }
    }
}
