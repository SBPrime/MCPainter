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

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.awt.Color;
import java.util.EnumSet;
import org.primesoft.mcpainter.configuration.BlockEntry;
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;

/**
 *
 * @author SBPrime
 */
public class DrawingBlock implements IDrawingBlock {
    public static DrawingBlock AIR = new DrawingBlock(BlockEntry.AIR, BlockEntry.AIR_COLOR);

    /**
     * The block to place
     */
    private final BaseBlock m_block;

    /**
     * The assigned color
     */
    private final Color m_color;

    /**
     * The list of operations this block is valid for
     */
    private final EnumSet<OperationType> m_type;

    
    public Color getColor() {
        return m_color;
    }

    @Override
    public EnumSet<OperationType> getType() {
        return m_type;
    }
    
    public DrawingBlock(BlockEntry block, Color color) {
        m_block = block.getBlock();
        m_color = color;
        m_type = block.getType();
    }

    @Override
    public void place(Vector origin, Vector offset, BlockLoger loger) throws MaxChangedBlocksException {
        loger.logBlock(origin.add(offset), m_block);
    }
    
    @Override
    public boolean isAir(){
        return this == AIR;
    }
}