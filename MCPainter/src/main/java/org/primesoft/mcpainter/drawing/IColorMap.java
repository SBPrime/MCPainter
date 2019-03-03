/*
 * The MIT License
 *
 * Copyright 2015 SBPrime.
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
package org.primesoft.mcpainter.drawing;

import java.awt.Color;
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.drawing.filters.IColorPalette;

/**
 *
 * @author SBPrime
 */
public interface IColorMap {

    /**
     * Get block for color
     *
     * @param c color
     * @param type block operation type
     * @return block entry
     */
    IDrawingBlock getBlockForColor(Color c, OperationType type);

    /**
     * Get color pallete for given operation
     *
     * @param type operation type
     * @return Pallete
     */
    IColorPalette getPalette(OperationType type);

    
    /**
     * Is the color map initialized
     * @return 
     */
    Boolean isInitialized();
    
}
