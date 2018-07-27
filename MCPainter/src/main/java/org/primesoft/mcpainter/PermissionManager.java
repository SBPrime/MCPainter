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
package org.primesoft.mcpainter;

import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.configuration.SizeNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author SBPrime
 */
public class PermissionManager {
    /**
    * List of all permissions
    */
    public enum Perms {
        ReloadConfig, 
        DrawImage, DrawHdImage,
        DrawBlock, 
        RenderModel,
        DrawStatue_File, DrawStatue_Self, DrawStatue_Other, 
        ImageSize,
        FilterCrop, FilterResize, FilterGrayscale, FilterDithering,
        AnnounceVersion,
        DrawMap, 
        SizeBypass, 
        Purge_Self, Purge_Other, Purge_All,
        Jobs_Self, Jobs_Other, Jobs_All, Filter,
        Palette_list, Palette_change, Palette_changeOther
    }
        
    
    /**
     * Plugin permissions top node
     */
    private static String s_prefix = "MCPainter.";

    /**
     * Check if player has a specific permission
     * @param player player
     * @param perms permission to check
     * @return True if permission pressent
     */
    public static boolean isAllowed(Player player, Perms perms) {        
        if (player == null || player.isOp()) {
            return true;
        }

        String s = getPermString(perms);
        if (s == null) {
            return false;
        }

        return player.hasPermission(s);
    }
    
    
    /**
     * Check if player has a specific permission
     * @param player player
     * @param perms permission to check
     * @return True if permission pressent
     */
    public static boolean isAllowed(Player player, String perms) {
        if (player == null || player.isOp()) {
            return true;
        }
        if (perms == null) {
            return false;
        }
        
        return player.hasPermission(perms);
    }

    
    /**
     * Convert permission to string
     * @param perms Permission
     * @return Permission node
     */
    private static String getPermString(Perms perms) {
        switch (perms) {
            case AnnounceVersion:
                return s_prefix + "admin.version";
            case ReloadConfig:
                return s_prefix + "admin.reload";
            case DrawImage:
                return s_prefix + "user.drawImage";
            case DrawHdImage:
                return s_prefix + "user.drawHdImage";
            case DrawBlock:
                return s_prefix + "user.drawBlock";
            case DrawStatue_File:
                return s_prefix + "user.drawStatue.file";
            case DrawStatue_Other:
                return s_prefix + "user.drawStatue.other";
            case DrawStatue_Self:
                return s_prefix + "user.drawStatue.self";
            case ImageSize:
                return s_prefix + "user.size";
            case Filter:
                return s_prefix + "user.filter";
            case FilterCrop:
                return s_prefix + "user.filter.crop";
            case FilterResize:
                return s_prefix + "user.filter.resize";
            case FilterGrayscale:
                return s_prefix + "user.filter.grayscale";
            case FilterDithering:
                return s_prefix + "user.filter.dithering";
            case DrawMap:
                return s_prefix + "user.drawMap";
            case RenderModel:
                return s_prefix + "user.renderModel";
            case SizeBypass:
                return s_prefix + "admin.size.bypass";
            case Purge_Self:
                return s_prefix + "user.purge";
            case Purge_Other:
                return s_prefix + "admin.purge";
            case Purge_All:
                return s_prefix + "admin.purge.all";
            case Jobs_Self:
                return s_prefix + "user.jobs";
            case Jobs_Other:
                return s_prefix + "admin.jobs";
            case Jobs_All:
                return s_prefix + "admin.jobs.all";
            case Palette_list:
                return s_prefix + "user.palette.list";
            case Palette_change:
                return s_prefix + "user.palette.change";
            case Palette_changeOther:
                return s_prefix + "admin.palette.change";
        }

        return null;
    }

    
    /**
     * Check image specific permissions
     * @param player player to check permissions
     * @param w Image width
     * @param h Image height
     * @return True if player is allowed to draw such large image
     */
    public static boolean checkImage(Player player, int w, int h) {
        if (player == null || player.isOp() || isAllowed(player, Perms.SizeBypass))
        {
            return true;
        }
        
        String permName = getPermString(Perms.ImageSize);
        SizeNode maxSize = ConfigProvider.getMaxSize();

        SizeNode[] nodes = ConfigProvider.getSizeNodes();
        if (nodes == null || nodes.length == 0) {
            if (w > maxSize.getW() || h > maxSize.getH()) {
                MCPainterMain.say(player, ChatColor.RED + "You don't have permissions to draw such large images.");
                return false;
            }

            return true;

        }

        for (SizeNode node : nodes) {
            if (player.hasPermission(permName + "." + node.getNode())
                    && node.getW() >= w && node.getH() >= h) {
                return true;
            }
        }

        if (w > maxSize.getW() || h > maxSize.getH()) {
            MCPainterMain.say(player, ChatColor.RED + "You don't have permissions to draw such large images.");
            return false;
        }

        return true;
    }
}