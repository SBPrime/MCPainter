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
package org.primesoft.mcpainter.palettes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.primesoft.mcpainter.configuration.BlockEntry;
import org.primesoft.mcpainter.drawing.ColorMap;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.MCPainterMain;
import org.primesoft.mcpainter.texture.TextureManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author SBPrime
 */
public class Palette implements IPalette {

    /**
     * Palette blocks
     */
    private final BlockEntry[] m_blocks;

    /**
     * Palette name
     */
    private final String m_name;

    /**
     * Palette blocks
     *
     * @return
     */
    public BlockEntry[] getBlocks() {
        return m_blocks;
    }

    /**
     * Palette name
     *
     * @return
     */
    @Override
    public String getName() {
        return m_name;
    }

    /**
     *
     * @param name
     * @param blocks
     */
    private Palette(String name, BlockEntry[] blocks) {
        m_name = name;
        m_blocks = blocks;
    }
    
    /**
     * Create a new instance of color map for this palette
     * @param main
     * @return 
     */
    @Override
    public IColorMap createColorMap(MCPainterMain main)
    {
        return new ColorMap(main.getTextureProvider(), this);
    }

    /**
     * Load palette from file
     *
     * @param file
     * @return
     */
    public static Palette load(File file) {
        Configuration config = YamlConfiguration.loadConfiguration(file);

        String name = config.getString("name", null);
        BlockEntry[] blockEntries = parseBlocksSection(config);
        
        if (name == null) {
            MCPainterMain.log("* " + file.getName() + "...invalid file format, no name palette defined.");
            return null;
        }
        
        name = name.toLowerCase();
        if (blockEntries == null || blockEntries.length < 2) {
            MCPainterMain.log("* " + name + "...invalid file format, minimum number of blocks in palette is 2.");
            return null;
        }
        
        MCPainterMain.log("* " + name + "..." + blockEntries.length + " blocks defined.");
        return new Palette(name, blockEntries);
    }

    /**
     * Parse blocks section entry
     *
     * @param configuration
     * @return
     */
    private static BlockEntry[] parseBlocksSection(Configuration configuration) {
        List<BlockEntry> blocks = new ArrayList();
        for (String string : configuration.getStringList("blocks")) {
            try {
                BlockEntry entry = BlockEntry.parse(string);
                if (entry == null) {
                    MCPainterMain.log("* Error parsing block entry: " + string);
                } else {
                    blocks.add(entry);
                }
            } catch (Exception e) {
                MCPainterMain.log("* Error parsing block entry: " + string);
            }
        }
        return blocks.toArray(new BlockEntry[0]);
    }
}