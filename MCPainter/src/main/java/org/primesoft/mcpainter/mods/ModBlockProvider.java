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
package org.primesoft.mcpainter.mods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.primesoft.mcpainter.drawing.blocks.*;
import org.primesoft.mcpainter.MCPainterMain;
import static org.primesoft.mcpainter.MCPainterMain.log;
import org.primesoft.mcpainter.texture.TextureManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author SBPrime
 */
public class ModBlockProvider implements IBlockProvider {

    private final HashMap<String, IDrawableElement> m_nameBlocks;
    private final int m_cnt;

    public static ModBlockProvider load(TextureManager texture, 
            ConfigurationSection blocksSection) {
        log(" ...loading blocks definitions");
        return new ModBlockProvider(texture, blocksSection);
    }
    
    private ModBlockProvider(TextureManager texture, 
            ConfigurationSection blocksSection) {
        HashMap<String, HashMap<Short, IDrawableElement>> definedBlocks = new HashMap<>();
        m_cnt = loadBlocks(blocksSection, texture, definedBlocks);

        m_nameBlocks = aggregateBlocksName(definedBlocks);
    }

    /**
     * Load all blocks from the blocks section
     *
     * @param blocksSection
     * @param texture
     * @param definedBlocks
     * @return
     */
    private int loadBlocks(ConfigurationSection blocksSection, TextureManager texture,
            HashMap<String, HashMap<Short, IDrawableElement>> definedBlocks) {
        Set<String> sections = blocksSection.getKeys(false);
        int cnt = 0;
        for (String sName : sections) {
            ConfigurationSection blockDefinition = blocksSection.getConfigurationSection(sName);
            if (blockDefinition == null) {
                continue;
            }

            String name = blockDefinition.getString("Name", "").toUpperCase();
            String type = blockDefinition.getString("Type", "").toUpperCase();
            int[] data = getData(blockDefinition);
            int[] flags = BlockHelper.parseIntListEntry(blockDefinition.getIntegerList("Flags"));
            if (flags != null && flags.length > 0) {
                data = addFlags(data, flags);
            }

            ConfigurationSection instruction = blockDefinition.getConfigurationSection("Instruction");

            if (name.length() == 0) {
                MCPainterMain.log(sName + ": block name is required.");
                continue;
            }
            if (instruction == null) {
                MCPainterMain.log(sName + ": no block drawing instructions.");
                continue;
            }

            IDrawableElement block;
            try {
                if (type.equalsIgnoreCase(BaseBlock.BBNAME)) {
                    block = new BaseBlock(texture, instruction);
                } else if (type.equalsIgnoreCase(CustomBlock.NAME)) {
                    block = new CustomBlock(texture, instruction);
                } else if (type.equalsIgnoreCase(Flat.NAME)) {
                    block = new Flat(texture, instruction);
                } else if (type.equalsIgnoreCase(Chest.NAME)) {
                    block = new Chest(texture, instruction);
                } else if (type.equalsIgnoreCase(TrapDoor.NAME_TD)) {
                    block = new TrapDoor(texture, instruction);
                } else if (type.equalsIgnoreCase(Torch.NAME)) {
                    block = new Torch(texture, instruction);
                } else if (type.equalsIgnoreCase(Slab.NAME)) {
                    block = new Slab(texture, instruction);
                } else if (type.equalsIgnoreCase(StairsMb.NAME)) {
                    block = new StairsMb(texture, instruction);
                } else if (type.equalsIgnoreCase(Crops.NAME)) {
                    block = new Crops(texture, instruction);
                } else if (type.equalsIgnoreCase(Door.NAME)) {
                    block = new Door(texture, instruction);
                } else if (type.equalsIgnoreCase(Stem.NAME)) {
                    block = new Stem(texture, instruction);
                } else if (type.equalsIgnoreCase(Diagonal.NAME)) {
                    block = new Diagonal(texture, instruction);
                } else if (type.equalsIgnoreCase(MultiBlock.NAME)) {
                    block = new MultiBlock(texture, instruction);
                } else if (type.equalsIgnoreCase(Plane.NAME)) {
                    block = new Plane(texture, instruction);
                } else {
                    MCPainterMain.log(sName + ": unknown block type \"" + type + "\".");
                    continue;
                }
            } catch (Exception ex) {
                MCPainterMain.log(sName + ": Error parsing block " + ex.getMessage());
                continue;
            }

            if (block instanceof MultiBlock && data != null) {
                MCPainterMain.log(sName + ": Warning multi block detected, ignoring data value");
                data = null;
            }
            boolean added = addBlock(name, definedBlocks, data, sName, block);
            if (added) {
                cnt++;
            }
        }
        return cnt;
    }

    private boolean addBlock(String name, HashMap<String, HashMap<Short, IDrawableElement>> definedBlocks,
            int[] data, String sectionName, IDrawableElement block) {
        if (data == null) {
            data = new int[]{0};
        }
        boolean added = false;
        if (name != null && name.length() != 0) {
            if (!definedBlocks.containsKey(name)) {
                definedBlocks.put(name, new HashMap<>());
            }

            HashMap<Short, IDrawableElement> blockEntries = definedBlocks.get(name);
            for (int i : data) {
                if (i > Short.MAX_VALUE || i < 0) {
                    MCPainterMain.log(sectionName + ": invalid block data " + i + ".");
                    continue;
                }
                if (blockEntries.containsKey((short) i)) {
                    MCPainterMain.log(sectionName + ": duplicate block data value " + i + ".");
                } else {
                    blockEntries.put((short) i, block);
                    added = true;
                }
            }
        }
        return added;
    }

    @Override
    public int getBlocksCount() {
        return m_cnt;
    }

    @Override
    public IDrawableElement getBlock(String name) {
        if (name == null) {
            return null;
        }

        name = name.toUpperCase();
        if (m_nameBlocks.containsKey(name)) {
            return m_nameBlocks.get(name);
        }

        return null;
    }

    /**
     * Aggregate named blocks
     *
     * @param blocks
     * @return
     */
    private HashMap<String, IDrawableElement> aggregateBlocksName(HashMap<String, HashMap<Short, IDrawableElement>> blocks) {
        HashMap<String, IDrawableElement> result = new HashMap<String, IDrawableElement>();

        if (blocks == null) {
            return result;
        }

        Set<String> keys = blocks.keySet();
        for (String name : keys) {
            HashMap<Short, IDrawableElement> hash = blocks.get(name);
            if (hash.size() == 1) {
                result.put(name, (IDrawableElement) hash.values().toArray()[0]);
            } else {
                int max = -1;
                Set<Short> dataSet = hash.keySet();
                short[] data = new short[dataSet.size()];
                IDrawableElement[] drawable = new IDrawableElement[dataSet.size()];
                int idx = 0;
                for (Short d : dataSet) {
                    data[idx] = d;
                    drawable[idx] = hash.get(d);
                    if (max < d) {
                        max = d;
                    }
                    idx++;
                }

                result.put(name, new MultiBlock(drawable, data, false, (short) 0, (short) max));
            }
        }

        return result;
    }

    /**
     * Aggregate named blocks
     *
     * @param blocks
     * @return
     */
    private HashMap<Integer, IDrawableElement> aggregateBlocksId(HashMap<Integer, HashMap<Short, IDrawableElement>> blocks) {
        HashMap<Integer, IDrawableElement> result = new HashMap<Integer, IDrawableElement>();

        if (blocks == null) {
            return result;
        }

        Set<Integer> keys = blocks.keySet();
        for (Integer id : keys) {
            HashMap<Short, IDrawableElement> hash = blocks.get(id);
            if (hash.size() == 1) {
                result.put(id, (IDrawableElement) hash.values().toArray()[0]);
            } else {
                int max = -1;
                Set<Short> dataSet = hash.keySet();
                short[] data = new short[dataSet.size()];
                IDrawableElement[] drawable = new IDrawableElement[dataSet.size()];
                int idx = 0;
                for (Short d : dataSet) {
                    data[idx] = d;
                    drawable[idx] = hash.get(d);
                    if (max < d) {
                        max = d;
                    }
                    idx++;
                }

                result.put(id, new MultiBlock(drawable, data, false, (short) 0, (short) max));
            }
        }

        return result;
    }

    private static int[] getData(ConfigurationSection blockDefinition) {
        List<Integer> lData = blockDefinition.getIntegerList("Data");
        if (lData != null && lData.size() > 0) {
            return BlockHelper.parseIntListEntry(lData);
        }

        int data = blockDefinition.getInt("Data", -1);
        if (data == -1) {
            return null;
        }
        return new int[]{data};
    }

    /**
     * Add flags to data
     *
     * @param data
     * @param flags
     * @return
     */
    private int[] addFlags(int[] data, int[] flags) {
        int[] bits = new int[flags.length];
        int max = 1 << flags.length;
        int val = 1;
        for (int i = 0; i < flags.length; i++) {
            bits[i] = val;
            val = val << 1;
        }

        HashSet<Integer> result = new HashSet<Integer>();
        for (int i : data) {
            if (!result.contains(i)) {
                result.add(i);
            }
        }
        for (int i = 1; i < max; i++) {
            val = 0;
            for (int j = 0; j < bits.length; j++) {
                if ((i & bits[j]) != 0) {
                    val |= flags[j];
                }
            }
            if (val != 0) {
                for (int d : data) {
                    int j = d | val;
                    if (!result.contains(j)) {
                        result.add(j);
                    }
                }
            }
        }

        return BlockHelper.parseIntListEntry(result);
    }
}
