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
package org.primesoft.mcpainter.commands;

import org.primesoft.mcpainter.Help;
import org.primesoft.mcpainter.PermissionManager;
import org.primesoft.mcpainter.MCPainterMain;
import org.primesoft.mcpainter.palettes.IPalette;
import org.primesoft.mcpainter.palettes.PaletteManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public class PaletteCommand {

    public static void Execte(MCPainterMain sender, Player player, String[] args) {
        if (args.length < 1 || args.length > 3) {
            Help.ShowHelp(player, Commands.COMMAND_PALETTE);
            return;
        }

        PaletteManager pm = sender.getPaletteManager();
        if (args.length == 1) {
            if (!PermissionManager.isAllowed(player, PermissionManager.Perms.Palette_list)) {
                MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            String[] names = pm.getNames();
            for (int i = 0; i < names.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(names[i]);
            }
            MCPainterMain.say(player, ChatColor.YELLOW + "Known palettes: " + ChatColor.WHITE
                    + sb);
        } else {
            String playerName;
            String palName;
            PermissionManager.Perms perm;

            if (args.length == 3) {
                playerName = args[1].toLowerCase();
                palName = args[2].toLowerCase();
                perm = PermissionManager.Perms.Palette_changeOther;
            } else {
                playerName = player.getName().toLowerCase();
                palName = args[1].toLowerCase();
                perm = PermissionManager.Perms.Palette_change;
            }

            if (!PermissionManager.isAllowed(player, perm)) {
                MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
                return;
            }

            IPalette pal = pm.getPalette(palName);
            if (pal == null) {
                MCPainterMain.say(player, ChatColor.RED + "Palette " + ChatColor.WHITE
                        + palName + ChatColor.RED + " not found.");
            } else {
                sender.setPalette(playerName, pal);
                MCPainterMain.say(player, ChatColor.YELLOW + "Palette set to " + ChatColor.WHITE
                        + palName);
            }
        }
    }
}
