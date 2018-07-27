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
import org.primesoft.mcpainter.utils.Orientation;
import org.primesoft.mcpainter.utils.Utils;
import java.awt.image.BufferedImage;
import org.primesoft.mcpainter.Configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.*;
import org.primesoft.mcpainter.drawing.statue.CustomStatue;
import org.primesoft.mcpainter.drawing.statue.PlayerStatueDescription;
import org.primesoft.mcpainter.mods.ModStatueProvider;
import org.primesoft.mcpainter.utils.Vector2D;
import org.primesoft.mcpainter.worldEdit.IEditSession;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;
import org.primesoft.mcpainter.worldEdit.ILocalSession;
import org.primesoft.mcpainter.worldEdit.IWorldEdit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author SBPrime
 */
public class StatueCommand implements Runnable {

    public static void Execte(MCPainterMain sender, final Player player,
            final IWorldEdit worldEdit, final IColorMap colorMap, String[] args) {
        if (args.length < 1 || args.length > 2) {
            Help.ShowHelp(player, Commands.COMMAND_STATUE);
            return;
        }

        ModStatueProvider statueProvider = sender.getStatueProvider();
        PlayerStatueDescription description = statueProvider.getPlayer();
        if (description == null){
            MCPainterMain.say(player, ChatColor.RED + "No player statue defined in the config files.");
            return;
        }
        final String url;
        final String userName;

        final String imgError;
        if (args.length == 1) {
            if (!PermissionManager.isAllowed(player, PermissionManager.Perms.DrawStatue_Self)) {
                MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
                return;
            }

            userName = player.getName();
            url = null;

            imgError = ChatColor.RED + "Error downloading your skin. ";
        } else {
            String tUrl = args[1];
            boolean perms;
            if (tUrl.startsWith("u:")) {
                String user = tUrl.substring(2);

                userName = user;
                url = null;

                perms = PermissionManager.isAllowed(player, PermissionManager.Perms.DrawStatue_Other);
                imgError = ChatColor.RED + "Error downloading skin for user " + ChatColor.WHITE + user;
            } else {
                url = tUrl;
                userName = null;

                perms = PermissionManager.isAllowed(player, PermissionManager.Perms.DrawStatue_File);
                imgError = ChatColor.RED + "Error downloading skin " + ChatColor.WHITE + url;
            }

            if (!perms) {
                MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
                return;
            }
        }

        sender.getServer().getScheduler().runTaskAsynchronously(sender,
                new StatueCommand(sender, player, url, userName, imgError, 
                    worldEdit, colorMap, description));       
    }

    private static Vector2D parse2DVector(String string) {
        String[] parts = string.split(",");
        if (parts.length != 2) {
            return null;
        }
        try {
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);

            return new Vector2D(u, v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Player m_player;
    private final String m_url;
    private final String m_userName;
    private final ILocalSession m_lSession;
    private final IEditSession m_session;
    private final MCPainterMain m_sender;
    private final double m_yaw;
    private final double m_pitch;
    private final Orientation m_orientation;
    private final String m_imgError;
    private final CustomStatue m_playerStatue;

    private final PlayerStatueDescription m_statueDescription;

    private StatueCommand(MCPainterMain sender, Player player, String url, String userName, String imgError,
            IWorldEdit worldEdit, IColorMap colorMap, PlayerStatueDescription description) {
        m_player = player;

        m_lSession = worldEdit.getSession(player);
        ILocalPlayer localPlayer = worldEdit.wrapPlayer(player);
        m_session = m_lSession.createEditSession(localPlayer);
        m_url = url;

        m_userName = userName;
        m_yaw = localPlayer.getYaw();
        m_pitch = localPlayer.getPitch();
        m_orientation = new Orientation(m_yaw, m_pitch);
        m_statueDescription = description;
        
        m_playerStatue = new CustomStatue(colorMap, Utils.getPlayerPos(localPlayer), 
                m_yaw, m_pitch, m_orientation, description);

        m_sender = sender;
        m_imgError = imgError;
    }

    @Override
    public void run() {
        double price = ConfigProvider.getCommandPrice("statue");
        synchronized (FoundManager.getMutex()) {
            if (price > 0 && FoundManager.getMoney(m_player) < price) {
                MCPainterMain.say(m_player, ChatColor.RED + "You don't have sufficient funds to draw the statue.");
                return;
            }

            String url = m_url;

            if (url == null && m_userName == null) {
                MCPainterMain.say(m_player, ChatColor.RED + m_imgError + " No username and no skin url provided.");
                return;
            } else if (url == null) {
                url = m_statueDescription.getSkinFile(m_userName);
            }
            
            if (url == null) {
                MCPainterMain.say(m_player, ChatColor.RED + m_imgError + " Unable to resolve the skin url.");
                return;
            }

            MCPainterMain.say(m_player, "Loading image...");
            BufferedImage img = ImageHelper.downloadImage(url);
            if (img == null) {
                MCPainterMain.say(m_player, m_imgError);
                return;
            }

            MCPainterMain.say(m_player, "Drawing statue...");
            BlockLoger loger = new BlockLoger(m_player, m_lSession, m_session, m_sender);

            boolean[] useAlpha = new boolean[1];
            RawImage rawImg = new RawImage(ImageHelper.convertToRGB(img, useAlpha), img.getWidth());
            m_playerStatue.DrawStatue(loger, new RawImage[]{rawImg}, useAlpha[0]);
            loger.logMessage("Done.");
            loger.logEndSession();

            //m_sender.getBlockPlacer().AddTasks(loger);
            loger.flush();
            FoundManager.subtractMoney(m_player, price);
        }
    }
}