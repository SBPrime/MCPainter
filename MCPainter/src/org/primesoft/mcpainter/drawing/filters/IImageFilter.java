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

import java.awt.image.BufferedImage;
import org.bukkit.entity.Player;

/**
 * The interface for basic image filters
 * @author SBPrime
 */
public interface IImageFilter {
    /**
     * The filter name
     * @return filter name
     */
    public String getName();
    
    /**
     * Check if player has permissions to use this this filter
     * @param player the player
     * @return True if player has permissions to use this command
     */
    public boolean hasPerms(Player player);
    
    /**
     * Apply image filter
     * @param src The source image
     * @param params Filter arguments
     * @return Filtered image, null if error
     */
    public BufferedImage process(BufferedImage src, IFilterParams params);

    
    /**
     * Get the filter config entry
     * @param args Filter args
     * @return The config entry
     */
    public FilterEntry getEntry(String[] args);

    
    /**
     * Get filter help
     * @return THe filter help
     */
    public String[] getHelp();
    
    
    /**
     * Get the price entry for filter
     * @return Price entry
     */
    public String getPriceName();
}
