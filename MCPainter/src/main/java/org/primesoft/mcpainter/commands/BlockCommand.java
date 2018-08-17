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
package org.primesoft.mcpainter.commands;

import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.blocks.IBlockProvider;
import org.primesoft.mcpainter.drawing.blocks.IDrawableElement;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.FoundManager;
import org.primesoft.mcpainter.Help;
import org.primesoft.mcpainter.MCPainterMain;
import org.primesoft.mcpainter.worldEdit.IEditSession;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;
import org.primesoft.mcpainter.worldEdit.ILocalSession;
import org.primesoft.mcpainter.worldEdit.IWorldEdit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author SBPrime
 */
public class BlockCommand {
    /**
     * Blocks provider
     */
    private final IBlockProvider m_blockProvider;

    public BlockCommand(MCPainterMain plugin) {
        m_blockProvider = plugin.getBlockProvider();
    }

    public void Execte(MCPainterMain sender, final Player player, IWorldEdit worldEdit,
            final IColorMap colorMap, String[] args) {
        if (args.length < 1 || args.length > 2) {
            Help.ShowHelp(player, Commands.COMMAND_BLOCK);
            return;
        }

        Material blockMaterial = null;
        short blockData;
        String blockName = null;

        if (args.length == 1) {
            ItemStack inHand = player.getItemInHand();
            MaterialData tmp = inHand.getData();

            blockMaterial = tmp.getItemType();
            blockData = tmp.getData();
        } else {
            String[] materialParts = args[1].split(":");
            int data = 0;

            if (materialParts.length == 0 || materialParts[0] == null
                    || materialParts[0].length() == 0) {
                ItemStack inHand = player.getItemInHand();
                MaterialData tmp = inHand.getData();

                blockMaterial = tmp.getItemType();
                if (materialParts.length > 1) {
                    int value = 0;
                    try {
                        value = Integer.parseInt(materialParts[1]);
                    } catch (NumberFormatException e) {
                        value = tmp.getData();
                    }
                    blockData = (short) value;
                } else {
                    blockData = tmp.getData();
                }
            } else {
                blockName = materialParts[0].toUpperCase();                

                if (materialParts.length > 1) {
                    int value = 0;
                    try {
                        value = Integer.parseInt(materialParts[1]);
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    blockData = (short) value;
                } else {
                    blockData = 0;
                }
            }

            if (blockMaterial == null && blockName == null) {
                MCPainterMain.say(player, ChatColor.RED + "Unknown material");
                return;
            }
        }

        if (blockMaterial != null) {
            blockName = blockMaterial.toString();
        }
        String name = blockName;
        MCPainterMain.say(player, "Drawing block " + name + "...");

        final IDrawableElement element = m_blockProvider.getBlock(blockName);

        if (element == null) {
            MCPainterMain.say(player, ChatColor.RED + "Block " + name + " not supported");
            return;
        }

        sender.getServer().getScheduler().runTaskAsynchronously(sender,
                new CommandThread(sender, player, element, blockData, worldEdit, colorMap));
    }

    private class CommandThread implements Runnable {

        private final ILocalSession m_lSession;
        private final IEditSession m_session;
        private final MCPainterMain m_sender;
        private final IColorMap m_colorMap;
        private final IDrawableElement m_element;
        private final ILocalPlayer m_localPlayer;
        private final short m_blockData;
        private final Player m_player;

        private CommandThread(MCPainterMain sender, Player player, IDrawableElement element,
                short blockData, IWorldEdit worldEdit, IColorMap colorMap) {
            m_player = player;

            m_lSession = worldEdit.getSession(player);
            m_localPlayer = worldEdit.wrapPlayer(player);
            m_session = m_lSession.createEditSession(m_localPlayer);
            m_colorMap = colorMap;
            m_sender = sender;
            m_element = element;
            m_blockData = blockData;
        }

        @Override
        public void run() {
            double price = ConfigProvider.getCommandPrice("block");
            synchronized (FoundManager.getMutex()) {
                if (price > 0 && FoundManager.getMoney(m_player) < price) {
                    MCPainterMain.say(m_player, ChatColor.RED + "You don't have sufficient funds to draw blocks.");
                    return;
                }
                BlockLoger loger = new BlockLoger(m_player, m_lSession, m_session, m_sender);
                m_element.draw(m_blockData, loger, m_localPlayer, m_colorMap);

                loger.logMessage("Drawing block done.");
                loger.logEndSession();

                //m_sender.getBlockPlacer().AddTasks(loger);
                loger.flush();
                FoundManager.subtractMoney(m_player, price);
            }
        }
    }
}