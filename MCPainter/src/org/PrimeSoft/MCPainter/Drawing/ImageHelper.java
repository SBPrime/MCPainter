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
package org.PrimeSoft.MCPainter.Drawing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.BlockEntry;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.PrimeSoft.MCPainter.utils.BaseBlock;
import org.PrimeSoft.MCPainter.utils.Pair;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.utils.Vector2D;
import org.PrimeSoft.MCPainter.worldEdit.MaxChangedBlocksException;

/**
 * @author SBPrime
 */
public class ImageHelper {

    public static BufferedImage downloadImage(String fileUrl) {
        try {
            try {
                URL url = new URL(fileUrl);

                return (BufferedImage) ImageIO.read(url);
            } catch (MalformedURLException e) {
                File file = new File(ConfigProvider.getPluginFolder(), fileUrl);
                if (!file.exists() || !file.isFile() || !file.canRead()) {
                    MCPainterMain.log("Error file: file not found or not accessible.");
                    return null;
                } else {
                    return (BufferedImage) ImageIO.read(file);
                }
            }
        } catch (IOException e) {
            MCPainterMain.log("Error downloading file: " + e.getMessage());
            return null;
        }
    }

    public static BufferedImage openImage(File file) {
        try {
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                MCPainterMain.log("Error file: file not found or not accessible.");
                return null;
            } else {
                return (BufferedImage) ImageIO.read(file);
            }
        } catch (IOException e) {
            MCPainterMain.log("Error opening file: " + e.getMessage());
            return null;
        } catch (Exception e) {
            MCPainterMain.log("Unable to opening file: " + e.getMessage());
            return null;
        }
    }

    public static void drawImage(BlockLoger loger, ColorMap colorMap, BufferedImage img,
            Vector pos, Orientation orientation) throws MaxChangedBlocksException {
        int hh = img.getHeight();
        int ww = img.getWidth();

        for (int yy = 0; yy < hh; yy++) {
            for (int xx = 0; xx < ww; xx++) {
                int x = orientation.calcX(xx, yy, 0);
                int y = orientation.calcY(xx, yy, 0);
                int z = orientation.calcZ(xx, yy, 0);

                if (y >= 0 && y <= 255) {
                    Color c = new Color(img.getRGB(xx, hh - yy - 1), true);
                    BlockEntry block = colorMap.getBlockForColor(c, OperationType.Image);

                    Vector location = pos.add(x, y, z);
                    BaseBlock newBlock = block.getBlock();
                    loger.logBlock(location, newBlock);
                }
            }
        }
    }

    public static void drawCube(BlockLoger loger, ColorMap colorMap, Vector pos,
            Orientation orientation, Vector size, Face[] faces, boolean useAlpha,
            OperationType type) throws MaxChangedBlocksException {
        drawCube(loger, colorMap, pos, orientation, size, faces, null, useAlpha, type);
    }

    public static void drawCube(BlockLoger loger, ColorMap colorMap, Vector pos,
            Orientation orientation, Vector size, Face[] faces, int[] grayColor,
            boolean useAlpha, OperationType type) throws MaxChangedBlocksException {
        drawCube(loger, colorMap, pos, orientation, size, faces, null, grayColor, useAlpha, type);
    }

    public static void drawCube(BlockLoger loger, ColorMap colorMap, Vector pos,
            Orientation orientation, Vector size, Face[] faces, double[] maping,
            int[] grayColor, boolean useAlpha, OperationType type) throws MaxChangedBlocksException {

        double[] ddX, ddY, ddZ;
        if (maping == null || maping.length != 9) {
            ddX = new double[]{1, 0, 0};
            ddY = new double[]{0, 1, 0};
            ddZ = new double[]{0, 0, 1};
        } else {
            ddX = new double[]{maping[0], maping[1], maping[2]};
            ddY = new double[]{maping[3], maping[4], maping[5]};
            ddZ = new double[]{maping[6], maping[7], maping[8]};
        }
        int w = (int) (size.getX() - 1);
        int h = (int) (size.getY() - 1);
        int d = (int) (size.getZ() - 1);

        int uS[] = new int[]{w, w, d, d, w, w};
        int vS[] = new int[]{h, h, h, h, d, d};
        final int[] uM = new int[]{0, 0, 2, 2, 0, 0};
        final int[] vM = new int[]{1, 1, 1, 1, 2, 2};
        final int[] masks = new int[]{1, 2, 4, 8, 16, 32};

        int[] map = new int[]{0, 0, 0};
        Color[] colors = new Color[6];
        int[] delta = new int[6];
        int[] depth = new int[6];
        int[] h1 = new int[6];
        int[] h2 = new int[6];
        int[] v1 = new int[6];
        int[] v2 = new int[6];

        for (int i = 0; i < faces.length; i++) {
            if (faces[i] != null) {
                delta[i] = faces[i].getDelta();
                depth[i] = faces[i].getDepth();
                Pair<Integer, Integer> cropH = faces[i].getCropH();
                Pair<Integer, Integer> cropV = faces[i].getCropV();
                if (cropH != null) {
                    h1[i] = cropH.getFirst();
                    h2[i] = cropH.getSecond();
                }
                if (cropV != null) {
                    v1[i] = cropV.getFirst();
                    v2[i] = cropV.getSecond();
                }
            } else {
                delta[i] = 0;
                depth[i] = 0;
            }
        }

        for (int y = 0; y <= h; y++) {
            map[1] = y;
            for (int x = 0; x <= w; x++) {
                map[0] = x;
                for (int z = 0; z <= d; z++) {
                    map[2] = z;

                    int mask = 0;
                    int[] tempDelta = new int[]{
                        d - delta[0] - z,
                        z - delta[1],
                        x - delta[2],
                        w - delta[3] - x,
                        h - delta[4] - y,
                        y - delta[5]
                    };
                    int[] tempH1 = new int[]{
                        x - h1[0],
                        w - x - h1[1],
                        z - h1[2],
                        d - z - h1[3],                        
                        w - x - h1[4],
                        w - x - h1[5],
                    };
                    int[] tempV1 = new int[]{
                        h - y - v1[0],
                        h - y - v1[1],
                        h - y - v1[2],
                        h - y - v1[3],
                        z - v1[4],
                        z - v1[5],
                    };
                    int[] tempH2 = new int[]{
                        w - x - h2[0],
                        x - h2[1],
                        d - z - h2[2],
                        z - h2[3],                        
                        x - h2[4],
                        x - h2[5],
                    };
                    int[] tempV2 = new int[]{
                        y - v2[0],
                        y - v2[1],
                        y - v2[2],
                        y - v2[3],
                        d - z - v2[4],
                        d - z - v2[5],
                    };
                    for (int i = 0; i < 6; i++) {
                        mask |= (tempDelta[i] >= 0
                                && tempH1[i] >= 0 && tempH2[i] >= 0
                                && tempV1[i] >= 0 && tempV2[i] >= 0
                                && tempDelta[i] < depth[i]) ? masks[i] : 0;
                    }

                    int colorCnt = 0;
                    for (int idx = 0; idx < 6; idx++) {
                        Face face = faces[idx];
                        if ((mask & masks[idx]) == masks[idx] && face != null) {

                            Vector2D va = calc(face.getTL(), face.getTR(), map[uM[idx]], uS[idx]);
                            Vector2D vb = calc(face.getBL(), face.getBR(), map[uM[idx]], uS[idx]);
                            Vector2D tex = calc(va, vb, map[vM[idx]], vS[idx]);

                            RawImage rawTex = face.getTexture();
                            if (rawTex == null) {
                                continue;
                            }
                            Color c = getColor(rawTex.getImage(), (int) tex.getX(), (int) tex.getZ());
                            if (face.isGray() && grayColor != null) {
                                c = getColor(c, grayColor);
                            }

                            if (!useAlpha) {
                                int rgb = c.getRGB();
                                int a = (rgb & 0xffffff) != 0 ? 0xff : 0x00;
                                c = new Color((rgb & 0x00ffffff) | (a * 0x01000000));
                            }

                            if (c.getAlpha() > 0) { //255
                                colors[colorCnt] = c;
                                colorCnt++;
                            }
                        }
                    }

                    if (colorCnt > 0) {
                        int r = 0;
                        int g = 0;
                        int b = 0;
                        int a = 0;
                        for (int idx = 0; idx < colorCnt; idx++) {
                            Color c = colors[idx];
                            r += c.getRed();
                            g += c.getGreen();
                            b += c.getBlue();
                            a += c.getAlpha();
                        }

                        BlockEntry block = colorMap.getBlockForColor(new Color(r / colorCnt, g / colorCnt, b / colorCnt, a / colorCnt), type);

                        if (block != BlockEntry.AIR) {
                            int px = (int) (x * ddX[0] + y * ddX[1] + z * ddX[2]);
                            int py = (int) (x * ddY[0] + y * ddY[1] + z * ddY[2]);
                            int pz = (int) (x * ddZ[0] + y * ddZ[1] + z * ddZ[2]);
                            int dx = orientation.calcX(px, py, pz);
                            int dy = orientation.calcY(px, py, pz);
                            int dz = orientation.calcZ(px, py, pz);
                            Vector nPos = pos.add(dx, dy, dz);
                            if (nPos.getBlockY() >= 0 && nPos.getBlockY() <= 255) {
                                loger.logBlock(nPos, block.getBlock());
                            }
                        }
                    }
                }
            }
        }
    }

    public static void drawDiagonal(BlockLoger loger, ColorMap colorMap,
            Vector pos, Orientation orientation, Vector size,
            Face[] faces, int[] grayColor, boolean useAlpha, OperationType type) throws MaxChangedBlocksException {
        int w = (int) (size.getX() - 1);
        int h = (int) (size.getY() - 1);
        int d = (int) (size.getZ() - 1);

        double uS[] = new double[]{
            w, w,
            d, d,
            Math.sqrt(d * d + w * w), Math.sqrt(d * d + w * w),};
        double vS[] = new double[]{
            Math.sqrt(h * h + d * d), Math.sqrt(h * h + d * d),
            Math.sqrt(h * h + w * w), Math.sqrt(h * h + w * w),
            h, h
        };
        double max[] = new double[]{w, h, d};
        final int[] uMa = new int[]{0, 0, 2, 2, 0, 0};
        final int[] uMb = new int[]{3, 3, 3, 3, 2, 2};
        final int[] vMa = new int[]{1, 1, 0, 0, 1, 1};
        final int[] vMb = new int[]{2, 2, 1, 1, 3, 3};
        final int[] flip = new int[]{0, 4, 0, 3, 0, 1};

        final int[] masks = new int[]{1, 2, 4, 8, 16, 32};

        int[] map = new int[]{0, 0, 0, 0};

        Color[] colors = new Color[6];
        double scale = Math.min(h, Math.min(w, d));

        for (int y = 0; y <= h; y++) {
            map[1] = y;
            int cy = (int) ((double) y / (double) h * scale + 0.5);

            for (int x = 0; x <= w; x++) {
                map[0] = x;
                int cx = (int) ((double) x / (double) w * scale + 0.5);

                for (int z = 0; z <= d; z++) {
                    map[2] = z;
                    int cz = (int) ((double) z / (double) d * scale + 0.5);

                    int mask = 0;
                    mask |= (cz == cy) ? 1 : 0;
                    mask |= (scale - cz == cy) ? 2 : 0;
                    mask |= (cx == cy) ? 4 : 0;
                    mask |= (scale - cx == cy) ? 8 : 0;
                    mask |= (cx == cz) ? 16 : 0;
                    mask |= (scale - cx == cz) ? 32 : 0;

                    int colorCnt = 0;
                    for (int idx = 0; idx < 6; idx++) {
                        Face face = faces[idx];
                        if ((mask & masks[idx]) == masks[idx] && face != null) {
                            double pa = map[uMa[idx]];
                            double pb = map[uMb[idx]];
                            int pf = flip[idx];

                            if (pf == 1) {
                                pa = max[uMa[idx]] - pa;
                            }
                            if (pf == 2) {
                                pb = max[uMb[idx]] - pb;
                            }
                            double pp = Math.sqrt(pa * pa + pb * pb);
                            Vector2D va = calc(face.getTL(), face.getTR(), pp, uS[idx]);
                            Vector2D vb = calc(face.getBL(), face.getBR(), pp, uS[idx]);

                            pa = map[vMa[idx]];
                            pb = map[vMb[idx]];
                            if (pf == 3) {
                                pa = max[vMa[idx]] - pa;
                            }
                            if (pf == 4) {
                                pb = max[vMb[idx]] - pb;
                            }
                            pp = Math.sqrt(pa * pa + pb * pb);
                            Vector2D tex = calc(va, vb, pp, vS[idx]);

                            RawImage rawTex = face.getTexture();
                            if (rawTex == null) {
                                continue;
                            }
                            Color c = getColor(rawTex.getImage(), (int) tex.getX(), (int) tex.getZ());
                            if (face.isGray() && grayColor != null) {
                                c = getColor(c, grayColor);
                            }

                            if (!useAlpha) {
                                int rgb = c.getRGB();
                                int a = (rgb & 0xffffff) != 0 ? 0xff : 0x00;
                                c = new Color((rgb & 0x00ffffff) | (a * 0x01000000));
                            }

                            if (c.getAlpha() > 0) { //255
                                colors[colorCnt] = c;
                                colorCnt++;
                            }
                        }
                    }

                    int dx = orientation.calcX(x, y, z);
                    int dy = orientation.calcY(x, y, z);
                    int dz = orientation.calcZ(x, y, z);
                    if (colorCnt > 0) {
                        int r = 0;
                        int g = 0;
                        int b = 0;
                        int a = 0;
                        for (int idx = 0; idx < colorCnt; idx++) {
                            Color c = colors[idx];
                            r += c.getRed();
                            g += c.getGreen();
                            b += c.getBlue();
                            a += c.getAlpha();
                        }

                        BlockEntry block = colorMap.getBlockForColor(new Color(r / colorCnt, g / colorCnt, b / colorCnt, a / colorCnt), type);

                        if (block != BlockEntry.AIR) {
                            Vector nPos = pos.add(dx, dy, dz);
                            if (nPos.getBlockY() >= 0 && nPos.getBlockY() <= 255 && block != null) {
                                loger.logBlock(nPos, block.getBlock());
                            }
                        }
                    }
                }
            }
        }
    }

    public static Color getColor(int[][] img, int u, int v) {
        int h = img.length - 1;
        int w = img[0].length - 1;

        if (u < 0) {
            u = 0;
        }
        if (v < 0) {
            v = 0;
        }
        if (u > w) {
            u = w;
        }
        if (v > h) {
            v = h;
        }

        return new Color(img[v][u], true);
    }

    public static Color getColor(int[][] img, double u, double v) {
        int h = img.length - 1;
        int w = img[0].length - 1;

        if (u < 0) {
            u = 0;
        }
        if (v < 0) {
            v = 0;
        }
        if (u > w) {
            u = w;
        }
        if (v > h) {
            v = h;
        }

        int uu = (int) u;
        int vv = (int) v;
        double pu = 1 - (u - uu);
        double pv = 1 - (v - vv);

        int[][] a = new int[2][2];
        int[][] r = new int[2][2];
        int[][] g = new int[2][2];
        int[][] b = new int[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Color c = getColor(img, uu + i, vv + j);

                a[i][j] = c.getAlpha();
                r[i][j] = c.getRed();
                g[i][j] = c.getGreen();
                b[i][j] = c.getBlue();
            }
        }

        double aa = a[0][0] * pu * pv
                + a[1][0] * (1 - pu) * pv
                + a[0][1] * pu * (1 - pv)
                + a[1][1] * (1 - pu) * (1 - pv);
        double rr = r[0][0] * pu * pv
                + r[1][0] * (1 - pu) * pv
                + r[0][1] * pu * (1 - pv)
                + r[1][1] * (1 - pu) * (1 - pv);
        double gg = g[0][0] * pu * pv
                + g[1][0] * (1 - pu) * pv
                + g[0][1] * pu * (1 - pv)
                + g[1][1] * (1 - pu) * (1 - pv);
        double bb = b[0][0] * pu * pv
                + b[1][0] * (1 - pu) * pv
                + b[0][1] * pu * (1 - pv)
                + b[1][1] * (1 - pu) * (1 - pv);
        return new Color((int) rr, (int) gg, (int) bb, (int) aa);
    }


    /*
     * private static Color getColor(BufferedImage img, int u, int v) { int w =
     * img.getWidth() - 1; int h = img.getHeight() - 1;
     *
     * if (u < 0) { u = 0; } if (v < 0) { v = 0; } if (u > w) { u = w; } if (v >
     * h) { v = h; }
     *
     * return new Color(img.getRGB(u, v), true); }
     */
    public static Graphics2D getGraphics(BufferedImage result, boolean interpolate) {
        Graphics2D g = result.createGraphics();
        if (interpolate) {
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        }

        return g;
    }

    public static Color getColor(Color c, int[] grayColor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int a = c.getAlpha();

        float h = grayColor[0] / 360.0f;
        float s = grayColor[1] / 100.0f;

        if (r == g && g == b && r == b) {
            float p = r / 255.0f;
            int color = Color.HSBtoRGB(h, s, p);
            return new Color((color & 0x00ffffff) | (a * 0x01000000), true);
        }
        return c;
    }

    public static int[][] convertToRGB(BufferedImage image, boolean[] useAlpha) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;
        final int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        boolean uAlpha = false;
        int[][] result = new int[height][width];
        int pixel = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int value = pixels[pixel];
                pixel++;

                int a = value >> 24;
                uAlpha |= a != -1;

                result[row][col] = value;
            }
        }

        if (useAlpha != null) {
            useAlpha[0] = uAlpha;
        }
        return result;
    }

    private static Vector2D calc(Vector2D a, Vector2D b, double pos, double length) {
        if (length == 0) {
            return a;
        }

        double aX = a.getX();
        double bX = b.getX();

        double aZ = a.getZ();
        double bZ = b.getZ();

        double cX = aX + (bX - aX) * pos / length;
        double cZ = aZ + (bZ - aZ) * pos / length;

        return new Vector2D(cX, cZ);
    }

    //http://stackoverflow.com/questions/2103368/color-logic-algorithm/2103608#2103608
    public static double colorDistance(Color c1, Color c2) {
        if (c1 == null || c2 == null) {
            return Double.POSITIVE_INFINITY;
        }
        double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        double b = c1.getBlue() - c2.getBlue();
        double weightR = 2 + rmean / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256.0;

        double a1 = c1.getAlpha();
        double a2 = c2.getAlpha();
        double a = Math.abs(a2 - a1) * (a1 <= a2 ? 1 : 1000);

        return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b) + a * 512;
    }
    //public static double colorDistance(Color c1, Color c2) {
    //    double r = c1.getRed() - c2.getRed();
    //    double g = c1.getGreen() - c2.getGreen();
    //    double b = c1.getBlue() - c2.getBlue();
    // 
    //    return Math.sqrt(r * r + g * g + b * b);
    //}
}
