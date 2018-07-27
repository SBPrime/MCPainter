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
package org.PrimeSoft.MCPainter.mods.assets;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import org.PrimeSoft.MCPainter.Drawing.IColorMap;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.voxelyzer.ClippingRegion;
import org.PrimeSoft.MCPainter.voxelyzer.Matrix;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public class CompiledModel {

    private final CompiledCube[] m_elements;

    public CompiledModel(CompiledCube[] elements) {
        m_elements = elements != null ? elements : new CompiledCube[0];
    }

    public void render(Vector origin, Player player,
            BlockLoger loger, IColorMap colorMap, ClippingRegion clipping,
            Matrix matrix)
            throws MaxChangedBlocksException {
        
        for (CompiledCube element : m_elements) {
            element.render(origin, player, loger, colorMap, clipping, matrix);
        }
    }
}
