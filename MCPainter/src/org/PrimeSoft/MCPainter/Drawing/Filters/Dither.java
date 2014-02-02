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
package org.PrimeSoft.MCPainter.Drawing.Filters;

import java.awt.Color;
import org.PrimeSoft.MCPainter.PermissionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * The dithering filter
 *
 * @author SBPrime
 */
public class Dither extends BaseRGBFilter {

    private class DitherParams implements IPaletteParam {

        /**
         * The pallete
         */
        private Color[] m_palette;

        @Override
        public void setPalette(Color[] pal) {
            m_palette = pal;
        }

        public Color[] getPalette() {
            return m_palette;
        }

        @Override
        public String print() {
            return "";
        }
    }

    @Override
    public String getName() {
        return "dithering";
    }

    @Override
    public boolean hasPerms(Player player) {
        return PermissionManager.isAllowed(player, PermissionManager.Perms.FilterDithering);
    }

    @Override
    public int[][] process(int[][] rgb, int width, int height,
            boolean hasAlpha, IFilterParams params) {
        int[][] result = new int[height][width];
        ColorEx[][] pix = new ColorEx[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pix[y][x] = new ColorEx(rgb[y][x]);
            }
        }

        Color[] cPal = ((DitherParams) params).getPalette();
        ColorEx[] palette = new ColorEx[cPal.length];
        for (int i = 0; i < cPal.length; i++) {
            palette[i] = new ColorEx(cPal[i]);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ColorEx oldColor = pix[y][x];
                
                ColorEx newColor = findClosestColor(oldColor, palette);
                ColorEx err = ColorEx.sub(oldColor, newColor);

                result[y][x] = newColor.toRGB();
                if (x + 1 < width) {
                    pix[y][x + 1] = ColorEx.clamp(ColorEx.add(pix[y][x + 1], ColorEx.mul(err, 7.0 / 16)));

                    if (y + 1 < height) {
                        pix[y + 1][x + 1] = ColorEx.clamp(ColorEx.add(pix[y + 1][x + 1], ColorEx.mul(err, 1.0 / 16)));
                    }
                }
                if (y + 1 < height) {
                    pix[y + 1][x] = ColorEx.clamp(ColorEx.add(pix[y + 1][x], ColorEx.mul(err, 5.0 / 16)));
                    if (x - 1 >= 0) {
                        pix[y + 1][x - 1] = ColorEx.clamp(ColorEx.add(pix[y + 1][x - 1], ColorEx.mul(err, 3.0 / 16)));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public FilterEntry getEntry(String[] args) {
        return new FilterEntry(this, new DitherParams());
    }

    @Override
    public String[] getHelp() {
        final String[] help = new String[]{
            ChatColor.YELLOW + getName() + ChatColor.WHITE + " - convert image to palette using dithering",
            ChatColor.BLUE + getName()
        };

        return help;
    }

    private ColorEx findClosestColor(ColorEx c, ColorEx[] palette) {
        if (c.isTransparent()) {
            return ColorEx.TRANSPARENT;
        }

        double delta = Double.POSITIVE_INFINITY;
        int result = -1;
        for (int i =0;i<palette.length;i++)
        {
            ColorEx palC = palette[i];
            double d = ColorEx.dist(c, palC);
            if (d < delta) {
                result = i;
                delta = d;
            }
        }

        return result != -1 ? palette[result] : ColorEx.TRANSPARENT;
    }
    
    
    @Override
    public String getPriceName()
    {
        return "filters.dithering";
    }
}
