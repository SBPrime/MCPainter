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
package org.PrimeSoft.MCPainter.Commands;

import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.utils.Utils;
import java.awt.image.BufferedImage;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.*;
import org.PrimeSoft.MCPainter.Drawing.Statue.CustomStatue;
import org.PrimeSoft.MCPainter.Drawing.Statue.PlayerStatueDescription;
import org.PrimeSoft.MCPainter.mods.ModStatueProvider;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.utils.Vector2D;
import org.PrimeSoft.MCPainter.worldEdit.IEditSession;
import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;
import org.PrimeSoft.MCPainter.worldEdit.ILocalSession;
import org.PrimeSoft.MCPainter.worldEdit.IWorldEdit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author SBPrime
 */
public class StatueCommand implements Runnable {

    public static void Execte(MCPainterMain sender, final Player player,
            final IWorldEdit worldEdit, final ColorMap colorMap, String[] args) {
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
        final String imgError;
        if (args.length == 1) {
            if (!PermissionManager.isAllowed(player, PermissionManager.Perms.DrawStatue_Self)) {
                MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
                return;
            }

            url = description.getSkinFile(player.getName());
            imgError = ChatColor.RED + "Error downloading your skin. ";
        } else {
            String tUrl = args[1];
            boolean perms;
            if (tUrl.startsWith("u:")) {
                String user = tUrl.substring(2);
                url = description.getSkinFile(user);
                perms = PermissionManager.isAllowed(player, PermissionManager.Perms.DrawStatue_Other);
                imgError = ChatColor.RED + "Error downloading skin for user " + ChatColor.WHITE + user;
            } else {
                url = tUrl;
                perms = PermissionManager.isAllowed(player, PermissionManager.Perms.DrawStatue_File);
                imgError = ChatColor.RED + "Error downloading skin " + ChatColor.WHITE + url;
            }

            if (!perms) {
                MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
                return;
            }
        }

        sender.getServer().getScheduler().runTaskAsynchronously(sender,
                new StatueCommand(sender, player, url, imgError, worldEdit, colorMap, description));
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
    private final ILocalSession m_lSession;
    private final IEditSession m_session;
    private final MCPainterMain m_sender;
    private final double m_yaw;
    private final double m_pitch;
    private final Orientation m_orientation;
    private final String m_imgError;
    private final CustomStatue m_playerStatue;

    private StatueCommand(MCPainterMain sender, Player player, String url, String imgError,
            IWorldEdit worldEdit, ColorMap colorMap, PlayerStatueDescription description) {
        m_player = player;

        m_lSession = worldEdit.getSession(player);
        ILocalPlayer localPlayer = worldEdit.wrapPlayer(player);
        m_session = m_lSession.createEditSession(localPlayer);
        m_url = url;

        m_yaw = localPlayer.getYaw();
        m_pitch = localPlayer.getPitch();
        m_orientation = new Orientation(m_yaw, m_pitch);
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


            MCPainterMain.say(m_player, "Loading image...");
            BufferedImage img = ImageHelper.downloadImage(m_url);
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