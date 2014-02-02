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

import java.awt.image.BufferedImage;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.bukkit.entity.Player;

/**
 * Basic RGB filter
 *
 * @author SBPrime
 */
public abstract class BaseRGBFilter implements IImageFilter {

    @Override
    public abstract String getName();

    @Override
    public abstract boolean hasPerms(Player player);

    @Override
    public BufferedImage process(BufferedImage src, IFilterParams params) {
        boolean[] useAlpha = new boolean[1];
        int[][] rgb = ImageHelper.convertToRGB(src, useAlpha);

        int width = src.getWidth();
        int height = src.getHeight();
        rgb = process(rgb, width, height, useAlpha[0], params);

        BufferedImage result = new BufferedImage(rgb[0].length, rgb.length, BufferedImage.TYPE_INT_ARGB);        
        for (int i = 0; i < height; i++) {
            int[] tmp = rgb[i];
            result.setRGB(0, i, width, 1, tmp, 0, width);
        }

        return result;
    }

    public abstract int[][] process(int[][] rgb, int width, int height, boolean hasAlpha, IFilterParams params);

    @Override
    public abstract FilterEntry getEntry(String[] args);

    @Override
    public abstract String[] getHelp();
    
    @Override
    public abstract String getPriceName();
}