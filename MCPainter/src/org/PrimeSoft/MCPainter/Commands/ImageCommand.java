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

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.utils.Utils;
import java.awt.image.BufferedImage;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.Filters.FilterManager;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.*;
import org.PrimeSoft.MCPainter.Drawing.IColorMap;
import org.PrimeSoft.MCPainter.asyncworldedit.DrawingTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author SBPrime
 */
public class ImageCommand extends DrawingTask {

    public static void Execte(MCPainterMain sender, Player player, WorldEditPlugin worldEdit,
            IColorMap colorMap, String[] args) {
        if (args.length != 2) {
            Help.ShowHelp(player, Commands.COMMAND_IMAGE);
            return;
        }

        DrawingTask task = new ImageCommand(worldEdit, player,
                sender, args, colorMap);
        
        sender.getAWE().runTask(player, "Image", task);
    }
    private final String[] m_args;
    private final Orientation m_orientation;
    private final double m_yaw;
    private final double m_pitch;
    private final Vector m_pPosition;
    private final IColorMap m_colorMap;
    private final MCPainterMain m_sender;

    private ImageCommand(WorldEditPlugin worldEditPlugin, Player player, 
            MCPainterMain sender, String[] args, 
            IColorMap colorMap) {
        super(worldEditPlugin, player);
        
        m_args = args;

        m_yaw = m_localPlayer.getYaw();
        m_pitch = m_localPlayer.getPitch();
        m_orientation = new Orientation(m_yaw, m_pitch);
        m_pPosition = Utils.getPlayerPos(m_localPlayer);
        m_colorMap = colorMap;
        m_sender = sender;
    }

    public void draw(BlockLoger loger) throws MaxChangedBlocksException {
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
            try {
                ImageHelper.drawImage(loger, m_colorMap, img, position, m_orientation);
            } catch (MaxChangedBlocksException ex) {
                loger.logMessage("Maximum number of blocks changed, operation canceled.");
            }

            loger.logMessage("Drawing image done.");
            
            FoundManager.subtractMoney(m_player, price);
        }
    }
}
