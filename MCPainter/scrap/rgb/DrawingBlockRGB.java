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
import com.sk89q.worldedit.extent.Extent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.blocksplacer.ILoggerCommand;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author SBPrime
 */
public class DrawingBlockRGB implements IDrawingBlock {

    /**
     * The list of operations this block is valid for
     */
    private final static EnumSet<OperationType> s_type = EnumSet.of(OperationType.Block, OperationType.Image, OperationType.Statue);

    /**
     * The air block
     */
    public static DrawingBlockRGB AIR = new DrawingBlockRGB(null);

    /**
     * The head
     */
    private final ItemStack m_head;

    public DrawingBlockRGB(ItemStack head) {
        m_head = head;
    }

    @Override
    public boolean isAir() {
        return this == AIR;
    }

    @Override
    public EnumSet<OperationType> getType() {
        return s_type;
    }

    @Override
    public void place(Vector origin, Vector offset, BlockLoger loger) throws MaxChangedBlocksException {
        if (isAir()) {
            return;
        }

        Vector pos = origin.add(offset);
        Location l = new Location(loger.getWorld(), pos.getX(), pos.getY(), pos.getZ());
        loger.logCommand(new RgbBlockCommand(l, m_head));
    }

    private class RgbBlockCommand implements ILoggerCommand {

        private final static double W = 0.425;
        private final static double L = 0.425;
        private final static double H = 0.425;

        private final Location m_location;
        private final ItemStack m_head;
        private Material m_oldMaterial;
        private final List<ArmorStand> m_stand = new ArrayList<ArmorStand>();

        private RgbBlockCommand(Location location, ItemStack head) {
            m_location = location;
            m_head = head;
        }

        @Override
        public boolean isDemanding() {
            return false;
        }

        @Override
        public void redo(BlockLoger loger, Extent extent) {
            Chunk chunk = m_location.getChunk();
            if (!chunk.isLoaded()) {
                if (!chunk.load()) {
                    return;
                }
            }

            World w = m_location.getWorld();
            Block block = w.getBlockAt(m_location);
            m_oldMaterial = block.getType();

            m_stand.clear();

            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    for (int z = 0; z < 2; z++) {
                        Location dest = new Location(w,
                            m_location.getBlockX() + 0.3124 + W * x,
                            m_location.getBlockY() - 1.45 + H * y,
                            m_location.getBlockZ() + 0.3124 + L * z);
                        ArmorStand armor = (ArmorStand) w.spawn(dest, ArmorStand.class);                                                
                        
                        armor.setGravity(false);
                        armor.setVisible(false);
                        armor.teleport(dest);

                        armor.setHelmet(m_head);

                        m_stand.add(armor);
                    }
                }
            }
                        
            block.setType(Material.GLASS);
        }

        @Override
        public void undo(BlockLoger loger, Extent extent) {
            Chunk chunk = m_location.getChunk();
            if (!chunk.isLoaded()) {
                if (!chunk.load()) {
                    return;
                }
            }

            World w = m_location.getWorld();

            if (m_stand != null) {
                for (ArmorStand armor : m_stand)
                {
                    armor.remove();
                }
                m_stand.clear();
            }

            if (m_oldMaterial != null) {
                w.getBlockAt(m_location).setType(m_oldMaterial);
            }
        }

        @Override
        public Location getLocation() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
