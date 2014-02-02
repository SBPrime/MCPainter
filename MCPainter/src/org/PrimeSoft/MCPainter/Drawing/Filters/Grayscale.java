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
 *
 * @author prime
 */
public class Grayscale extends BaseRGBFilter {

    @Override
    public final String getName() {
        return "grayscale";
    }

    @Override
    public boolean hasPerms(Player player) {
        return PermissionManager.isAllowed(player, PermissionManager.Perms.FilterGrayscale);
    }

    @Override
    public int[][] process(int[][] rgb, int width, int height,
            boolean hasAlpha, IFilterParams params) {
        int[][] result = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(rgb[y][x], hasAlpha);
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                int a = hasAlpha ? c.getAlpha() : 0xff;

                int gray = (r + g + b) / 3;

                result[y][x] = new Color(gray, gray, gray, a).getRGB();
            }
        }

        return result;
    }

    @Override
    public FilterEntry getEntry(String[] args) {
        return new FilterEntry(this, null);
    }

    @Override
    public String[] getHelp() {
        final String[] help = new String[]{
            ChatColor.YELLOW + getName() + ChatColor.WHITE + " - convert image to grayscale",
            ChatColor.BLUE + getName()
        };

        return help;
    }
    
    
    @Override
    public String getPriceName()
    {
        return "filters.grayscale";
    }
}
