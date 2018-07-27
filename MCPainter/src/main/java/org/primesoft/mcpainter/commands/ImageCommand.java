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
import org.primesoft.mcpainter.drawing.dilters.FilterManager;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.*;
import org.primesoft.mcpainter.utils.Vector;
import org.primesoft.mcpainter.worldEdit.IEditSession;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;
import org.primesoft.mcpainter.worldEdit.ILocalSession;
import org.primesoft.mcpainter.worldEdit.IWorldEdit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author SBPrime
 */
public class ImageCommand implements Runnable {

    public static void Execte(MCPainterMain sender, Player player, IWorldEdit worldEdit,
            IColorMap colorMap, String[] args) {
        if (args.length != 2) {
            Help.ShowHelp(player, Commands.COMMAND_IMAGE);
            return;
        }

        sender.getServer().getScheduler().runTaskAsynchronously(sender,
                new ImageCommand(sender, player, args, worldEdit, colorMap));
    }
    private final String[] m_args;
    private final Player m_player;
    private final Orientation m_orientation;
    private final double m_yaw;
    private final double m_pitch;
    private final Vector m_pPosition;
    private final IEditSession m_session;
    private final ILocalSession m_lSession;
    private final IColorMap m_colorMap;
    private final MCPainterMain m_sender;

    private ImageCommand(MCPainterMain sender, Player player, String[] args, IWorldEdit worldEdit,
            IColorMap colorMap) {
        m_args = args;
        m_player = player;

        m_lSession = worldEdit.getSession(player);
        ILocalPlayer localPlayer = worldEdit.wrapPlayer(player);
        m_session = m_lSession.createEditSession(localPlayer);

        m_yaw = localPlayer.getYaw();
        m_pitch = localPlayer.getPitch();
        m_orientation = new Orientation(m_yaw, m_pitch);
        m_pPosition = Utils.getPlayerPos(localPlayer);
        m_colorMap = colorMap;
        m_sender = sender;
    }

    public void run() {
        final String url = m_args[1];
        FilterManager fm = FilterManager.getFilterManager(m_player);
        double price = ConfigProvider.getCommandPrice("image") + fm.getPrice();
        synchronized (FoundManager.getMutex()) {
            if (price > 0 && FoundManager.getMoney(m_player) < price) {
                MCPainterMain.say(m_player, ChatColor.RED + "You don't have sufficient funds to apply all the filters and draw the image.");
                return;
            }

            MCPainterMain.say(m_player, "Loading image...");
            BufferedImage img = ImageHelper.downloadImage(url);
            if (img == null) {
                MCPainterMain.say(m_player, ChatColor.RED + "Error downloading image " + ChatColor.WHITE + url);
                return;
            }

            img = fm.applyFilters(img, m_colorMap);

            int hh = img.getHeight();
            int ww = img.getWidth();
            Vector position = m_orientation.moveStart(m_pPosition, m_yaw, m_pitch, ww, hh, 1);

            if (!PermissionManager.checkImage(m_player, ww, hh)) {
                return;
            }

            MCPainterMain.say(m_player, "Drawing image...");
            BlockLoger loger = new BlockLoger(m_player, m_lSession, m_session, m_sender);
            ImageHelper.drawImage(loger, m_colorMap, img, position, m_orientation);

            loger.logMessage("Drawing image done.");
            loger.logEndSession();

            //m_sender.getBlockPlacer().AddTasks(loger);
            loger.flush();
            
            FoundManager.subtractMoney(m_player, price);
        }
    }
}
