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
package org.primesoft.mcpainter.drawing.filters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.PermissionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Resize image filter
 *
 * @author SBPrime
 */
public class ResizeFilter implements IImageFilter {

    private class ResizeParams implements IFilterParams {

        private final int m_w;
        private final int m_h;
        private final boolean m_interpolate;

        /**
         * Get the value of width
         *
         * @return the value of width
         */
        public int getWidth() {
            return m_w;
        }

        /**
         * Get the value of height
         *
         * @return the value of
         */
        public int getHeight() {
            return m_h;
        }

        /**
         * Get the value of interpolate
         *
         * @return the value of interpolate
         */
        public boolean isInterpolate() {
            return m_interpolate;
        }

        private ResizeParams(int w, int h, boolean interpolate) {
            m_w = w;
            m_h = h;
            m_interpolate = interpolate;
        }

        @Override
        public String print() {
            StringBuilder sb = new StringBuilder();
            if (m_w < 1) {
                sb.append("?");
            } else {
                sb.append(m_w);
            }
            sb.append("x");
            if (m_h < 1) {
                sb.append("?");
            } else {
                sb.append(m_h);
            }
            sb.append(" interpolate = ");
            sb.append(m_interpolate ? "on" : "off");
            return sb.toString();
        }
    }

    @Override
    public String getName() {
        return "resize";
    }

    @Override
    public boolean hasPerms(Player player) {
        return PermissionManager.isAllowed(player, PermissionManager.Perms.FilterResize);
    }

    @Override
    public BufferedImage process(BufferedImage src, IFilterParams params) {
        if (params == null || !(params instanceof ResizeParams)) {
            return null;
        }

        ResizeParams p = (ResizeParams) params;

        int w = p.getWidth();
        int h = p.getHeight();
        boolean interpolate = p.isInterpolate();

        int ww = src.getWidth();
        int hh = src.getHeight();

        if (w < 1 && h < 1) {
            w = ww;
            h = hh;
        } else if (w < 1) {
            if (hh > 0) {
                w = h * ww / hh;
            } else {
                w = ww;
            }
        } else if (h < 1) {
            if (ww > 0) {
                h = w * hh / ww;
            } else {
                h = hh;
            }
        }

        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = ImageHelper.getGraphics(result, interpolate);
        g.drawImage(src, 0, 0, w, h, 0, 0, ww, hh, null);
        g.dispose();

        return result;
    }

    @Override
    public FilterEntry getEntry(String[] args) {
        int argc = (args != null) ? args.length : 0;
        if (argc < 2 || argc > 3) {
            return null;
        }

        boolean interpolate;
        if (argc == 3) {
            if (args[2].compareToIgnoreCase("interpolate") != 0) {
                return null;
            }

            interpolate = true;
        } else {
            interpolate = false;
        }

        int w, h;

        try {
            w = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            w = -1;
        }
        try {
            h = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            h = -1;
        }
        if (w < 1 && h < 1) {
            return null;
        }

        return new FilterEntry(
                this, new ResizeParams(w, h, interpolate));
    }

    @Override
    public String[] getHelp() {
        final String[] help = new String[]{
            ChatColor.YELLOW + getName() + ChatColor.WHITE + " - resize the source image",
            ChatColor.BLUE + getName() + " w h [interpolate]",
            ChatColor.YELLOW + "  <w>, <h>" + ChatColor.WHITE + " - new width and height, use ? for automatic calculation",
            ChatColor.YELLOW + "  [interpolate]" + ChatColor.WHITE + " - optional, enables interpolation"
        };

        return help;
    }

    @Override
    public String getPriceName() {
        return "filters.resize";
    }
}
