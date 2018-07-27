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

import java.awt.image.BufferedImage;
import org.primesoft.mcpainter.Configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.dilters.FilterManager;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.FoundManager;
import org.primesoft.mcpainter.Help;
import org.primesoft.mcpainter.mapdrawer.MapHelper;
import org.primesoft.mcpainter.MCPainterMain;
import org.primesoft.mcpainter.utils.ExceptionHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

/**
 * @author SBPrime
 */
public class MapCommand {

    private MapHelper m_mapHelper;

    public MapCommand(MapHelper mapHelper) {
        m_mapHelper = mapHelper;
    }

    public void Execte(MCPainterMain sender, Player player, String[] args) {
        if (args.length < 2 || args.length > 5) {
            Help.ShowHelp(player, Commands.COMMAND_IMAGEMAP);
            return;
        }

        short mapId = -1;
        String url = null;
        int offset;

        try {
            mapId = (short) Integer.parseInt(args[1]);
            url = args[2];
            offset = 3;
        } catch (NumberFormatException ex) {
            url = args[1];
            offset = 2;

            ItemStack inHand = player.getItemInHand();

            if (inHand != null && inHand.getType() == Material.MAP) {
                mapId = inHand.getDurability();
            }
        }


        boolean reset = url.equalsIgnoreCase("reset");

        MapView mapView = null;
        if (mapId == -1 && !reset) {
            mapView = Bukkit.createMap(player.getWorld());
            mapId = mapView.getId();
            player.setItemInHand(new ItemStack(Material.MAP, 1, mapId));
        }
        if (mapView == null) {
            try {
                mapView = Bukkit.getMap(mapId);
            } catch (Exception ex) {
                ExceptionHelper.printException(ex, "Unable to get map #" + mapId);
            }

            if (mapView == null) {
                MCPainterMain.say(player, ChatColor.RED + "Map ID " + mapId + " not fond.");
                return;
            }
        }

        if (reset) {
            m_mapHelper.deleteMap(mapView);
        } else {
            sender.getServer().getScheduler().runTaskAsynchronously(sender,
                    new CommandThread(sender, player, args, url, mapView, offset));
        }
    }

    private class CommandThread implements Runnable {

        private final String[] m_args;
        private final String m_url;
        private final Player m_player;
        private final MapView m_mapView;
        private final int m_offset;
        private final MCPainterMain m_sender;

        private CommandThread(MCPainterMain sender, Player player, String[] args,
                String url, MapView mapView, int offset) {
            m_sender = sender;
            m_args = args;
            m_player = player;
            m_url = url;
            m_mapView = mapView;
            m_offset = offset;
        }

        @Override
        public void run() {
            FilterManager fm = FilterManager.getFilterManager(m_player);
            double price = ConfigProvider.getCommandPrice("map") + fm.getPrice();
            synchronized (FoundManager.getMutex()) {
                if (price > 0 && FoundManager.getMoney(m_player) < price) {
                    MCPainterMain.say(m_player, ChatColor.RED + "You don't have sufficient funds to apply all the filters and draw the map.");
                    return;
                }

                MCPainterMain.say(m_player, "Loading image...");
                BufferedImage img = ImageHelper.downloadImage(m_url);
                if (img == null) {
                    MCPainterMain.say(m_player, ChatColor.RED + "Error downloading image " + ChatColor.WHITE + m_url);
                    return;
                }

                img = fm.applyFilters(img, null);
                final BufferedImage fImg = img;
                int hh = img.getHeight();
                int ww = img.getWidth();

                if (ww > 128 || hh > 128) {
                    MCPainterMain.say(m_player, ChatColor.RED + "The images size cannot be greater than 128x128.");
                    return;
                }

                m_sender.getServer().getScheduler().runTask(m_sender, new Runnable() {

                    @Override
                    public void run() {
                        MCPainterMain.say(m_player, "Drawing image...");

                        m_mapHelper.storeMap(m_mapView, fImg);
                        m_mapHelper.drawImage(m_mapView, fImg);

                        MCPainterMain.say(m_player, "Drawing image done.");
                    }
                });
                
                FoundManager.subtractMoney(m_player, price);
            }
        }
    }
}
