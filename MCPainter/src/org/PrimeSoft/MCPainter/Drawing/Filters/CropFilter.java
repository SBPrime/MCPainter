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
package org.PrimeSoft.MCPainter.Drawing.Filters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.PermissionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Crop image filter
 *
 * @author SBPrime
 */
public class CropFilter implements IImageFilter {

    public static BufferedImage crop(BufferedImage src, int x1, int y1, int x2, int y2, boolean interpolate) {
        int w = x2 - x1 + 1;
        int h = y2 - y1 + 1;

        int ww = src.getWidth();
        int hh = src.getHeight();

        if (w < 1 || h < 1 || x1 < 0 || x2 >= ww || y1 < 0 || y2 >= hh) {
            return null;
        }

        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = ImageHelper.getGraphics(result, interpolate);
        g.drawImage(src, 0, 0, w, h, x1, y1, x2 + 1, y2 + 1, null);
        g.dispose();
        
        return result;
    }

    private class CropParams implements IFilterParams {

        private final int m_x1;
        private final int m_x2;
        private final int m_y1;
        private final int m_y2;
        private final boolean m_interpolate;

        /**
         * Get the value of x1
         *
         * @return the value of x1
         */
        public int getX1() {
            return m_x1;
        }

        /**
         * Get the value of x2
         *
         * @return the value of x2
         */
        public int getX2() {
            return m_x2;
        }

        /**
         * Get the value of y1
         *
         * @return the value of y1
         */
        public int getY1() {
            return m_y1;
        }

        /**
         * Get the value of y2
         *
         * @return the value of y2
         */
        public int getY2() {
            return m_y2;
        }

        /**
         * Get the value of interpolate
         *
         * @return the value of interpolate
         */
        public boolean isInterpolate() {
            return m_interpolate;
        }

        private CropParams(int x1, int y1, int x2, int y2, boolean interpolate) {
            m_x1 = x1;
            m_x2 = x2;
            m_y1 = y1;
            m_y2 = y2;
            m_interpolate = interpolate;
        }

        @Override
        public String print() {
            StringBuilder sb = new StringBuilder();
            sb.append(m_x1);
            sb.append("x");
            sb.append(m_y1);
            sb.append(" -> ");
            sb.append(m_x2);
            sb.append("x");
            sb.append(m_y2);
            sb.append(" interpolate = ");
            sb.append(m_interpolate ? "on" : "off");
            return sb.toString();

        }
    }

    @Override
    public String getName() {
        return "crop";
    }

    @Override
    public boolean hasPerms(Player player) {
        return PermissionManager.isAllowed(player, PermissionManager.Perms.FilterCrop);
    }

    @Override
    public BufferedImage process(BufferedImage src, IFilterParams params) {
        if (params == null || !(params instanceof CropParams)) {
            return null;
        }

        CropParams p = (CropParams) params;
        int x1 = p.getX1();
        int y1 = p.getY1();
        int x2 = p.getX2();
        int y2 = p.getY2();
        boolean interpolate = p.isInterpolate();

        return crop(src, x1, y1, x2, y2, interpolate);
    }

    @Override
    public FilterEntry getEntry(String[] args) {
        int argc = (args != null) ? args.length : 0;
        if (argc < 4 || argc > 5) {
            return null;
        }

        boolean interpolate;
        if (argc == 5) {
            if (args[4].compareToIgnoreCase("interpolate") != 0) {
                return null;
            }

            interpolate = true;
        } else {
            interpolate = false;
        }

        int x1, x2, y1, y2;

        try {
            x1 = Integer.parseInt(args[0]);
            y1 = Integer.parseInt(args[1]);
            x2 = Integer.parseInt(args[2]);
            y2 = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            return null;
        }

        return new FilterEntry(this, new CropParams(x1, y1, x2, y2, interpolate));
    }

    @Override
    public String[] getHelp() {
        final String[] help = new String[]{
            ChatColor.YELLOW + getName() + ChatColor.WHITE + " - cut a part of the source image",
            ChatColor.BLUE + getName() + " x1 y1 x2 y2 [interpolate]",
            ChatColor.YELLOW + "  <x1>, <x2>, <y1>, <y2>" + ChatColor.WHITE + " - coordinate for crop operation",
            ChatColor.YELLOW + "  [interpolate]" + ChatColor.WHITE + " - optional, enables interpolation"
        };

        return help;
    }

    @Override
    public String getPriceName() {
        return "filters.crop";
    }
}
