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
package org.PrimeSoft.MCPainter.Commands;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.Drawing.Statue.BaseStatue;
import org.PrimeSoft.MCPainter.Drawing.Statue.CustomStatue;
import org.PrimeSoft.MCPainter.Drawing.Statue.StatueDescription;
import org.PrimeSoft.MCPainter.*;
import org.PrimeSoft.MCPainter.asyncworldedit.DrawingTask;
import org.PrimeSoft.MCPainter.mods.ModStatueProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author SBPrime
 */
public class MobCommand extends DrawingTask {

    public static void Execte(MCPainterMain sender, final Player player,
            final WorldEditPlugin worldEdit, final ColorMap colorMap, String[] args) {
        if (args.length > 3) {
            Help.ShowHelp(player, Commands.COMMAND_MOB);
            return;
        }

        ModStatueProvider statueProvider = sender.getStatueProvider();
        if (args.length == 1) {
            listMobs(player, statueProvider);
            return;
        }

        int textureId = 0;
        if (args.length > 2 && args[2] != null) {
            try {
                textureId = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                //Ignore
            }
        }

        BaseStatue mob;

        LocalPlayer localPlayer = worldEdit.wrapPlayer(player);
        double m_yaw = localPlayer.getYaw();
        double m_pitch = localPlayer.getPitch();
        Orientation m_orientation = new Orientation(m_yaw, m_pitch);

        String mobName = args[1].toLowerCase();

        StatueDescription statue = statueProvider.get(mobName);
        Vector playerPos = Utils.getPlayerPos(localPlayer);
        boolean allowed;
        RawImage[] image;

        if (statue == null) {
            MCPainterMain.say(player, "Unknown mob name: \"" + mobName + "\"");
            listMobs(player, statueProvider);
            return;
        } else {
            mob = new CustomStatue(colorMap, playerPos, m_yaw, m_pitch, m_orientation, statue);
            allowed = PermissionManager.isAllowed(player, statue.getPermission());
            image = statue.getTextures(textureId);
        }

        if (!allowed) {
            MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
            return;
        }

        if (image == null) {
            MCPainterMain.say(player, ChatColor.RED + "Error drawing statue.");
            MCPainterMain.log("Error loading " + mobName + " texture file");
            return;
        }
        
        DrawingTask task = new MobCommand(worldEdit, player,
                sender, mob, image);
        sender.getAWE().runTask(player, "Mob", task);
    }

    /**
     * List all known mobs
     *
     * @param player
     * @param statueProvider
     */
    private static void listMobs(Player player, ModStatueProvider statueProvider) {
        StringBuilder sb = new StringBuilder("Known mobs: ");

        String[] names = statueProvider.getNames();
        for (int i = 0; i < names.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(names[i]);
        }

        MCPainterMain.say(player, sb.toString());
    }
    private final MCPainterMain m_sender;
    private final BaseStatue m_statue;
    private final RawImage[] m_textures;

    private MobCommand(WorldEditPlugin worldEditPlugin, Player player, 
            MCPainterMain sender,
            BaseStatue statue, RawImage[] textures) {
        super(worldEditPlugin, player);
        
        m_textures = textures;
        m_statue = statue;
        m_sender = sender;
    }

    @Override
    public void draw(BlockLoger loger) throws MaxChangedBlocksException {
        double price = ConfigProvider.getCommandPrice("statue");
        synchronized (FoundManager.getMutex()) {
            if (price > 0 && FoundManager.getMoney(m_player) < price) {
                MCPainterMain.say(m_player, ChatColor.RED + "You don't have sufficient funds to draw the statue.");
                return;
            }

            MCPainterMain.say(m_player, "Drawing statue...");
            try {
                m_statue.DrawStatue(loger, m_textures, true);
            } catch (MaxChangedBlocksException ex) {
                loger.logMessage("Maximum number of blocks changed, operation canceled.");
            }
            loger.logMessage("Done.");
            //m_sender.getBlockPlacer().AddTasks(loger);
            FoundManager.subtractMoney(m_player, price);
        }
    }
}
