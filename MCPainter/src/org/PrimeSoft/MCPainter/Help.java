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
package org.PrimeSoft.MCPainter;

import org.PrimeSoft.MCPainter.Commands.Commands;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public final class Help {

    private final static String[] HelpGlobal = new String[]{
        ChatColor.YELLOW + "MCPainter help:\n",
        ChatColor.BLUE + "Palette" + ChatColor.WHITE + " - set current drawing palette",
        ChatColor.BLUE + "Block" + ChatColor.WHITE + " - draw minecraft block",
        ChatColor.BLUE + "Filter" + ChatColor.WHITE + " - manage image filters",
        ChatColor.BLUE + "Help" + ChatColor.WHITE + " - diaplay help screen",
        ChatColor.BLUE + "Image" + ChatColor.WHITE + " - draw image",
        ChatColor.BLUE + "ImageHd" + ChatColor.WHITE + " - draw HD image using maps and frames",
        ChatColor.BLUE + "ImageMap" + ChatColor.WHITE + " - draw image directly to the map",
        ChatColor.BLUE + "Jobs" + ChatColor.WHITE + " - display queued block operations",
        ChatColor.BLUE + "Purge" + ChatColor.WHITE + " - remove all queued block operations",
        ChatColor.BLUE + "Reload" + ChatColor.WHITE + " - reload configuration",
        ChatColor.BLUE + "Statue" + ChatColor.WHITE + " - draw player statue",
        ChatColor.BLUE + "Mob" + ChatColor.WHITE + " - draw mob statue",
        ChatColor.YELLOW + "To display help on command use: " + ChatColor.BLUE + "/Help <command>"
    };
    private final static String[] HelpBlock = new String[]{
        ChatColor.YELLOW + "Block " + ChatColor.WHITE + " - draw minecraft block",
        ChatColor.BLUE + " Block" + ChatColor.WHITE + " - draw the block in hand",
        ChatColor.BLUE + " Block <block name>[:<data>]" + ChatColor.WHITE + " - draw block specified by name and data (data is optional)",
        ChatColor.BLUE + " Block <block id>[:<data>]" + ChatColor.WHITE + " - draw block specified by id and data (data is optional)"
    };
    private final static String[] HelpPalette = new String[]{
        ChatColor.YELLOW + "Palette " + ChatColor.WHITE + " - set current drawing palette",
        ChatColor.BLUE + " Palette" + ChatColor.WHITE + " - list available palettes",
        ChatColor.BLUE + " Palette <name>" + ChatColor.WHITE + " - set palette",
        ChatColor.BLUE + " Palette <playerName> <name>" + ChatColor.WHITE + " - set palette for player",
    };
    private final static String[] HelpImage = new String[]{
        ChatColor.YELLOW + "Image " + ChatColor.WHITE + " - draw image",
        ChatColor.BLUE + " Image <image file>" + ChatColor.WHITE + " - draw image",
        ChatColor.YELLOW + "  <image file>" + ChatColor.WHITE + " - the url of the image"
    };
    private final static String[] HelpHdImage = new String[]{
        ChatColor.YELLOW + "ImageHd" + ChatColor.WHITE + " - draw image directly on frames",
        ChatColor.BLUE + " ImageHd <image file>" + ChatColor.WHITE + " - draw image",
        ChatColor.YELLOW + "  <image file>" + ChatColor.WHITE + " - the url of the image",
        ChatColor.YELLOW + "  You need to select an area using world edit to use this command",
    };
    private final static String[] HelpImageMap = new String[]{
        ChatColor.YELLOW + "ImageMap " + ChatColor.WHITE + " - draw image directly on the map",
        ChatColor.BLUE + " ImageMap reset" + ChatColor.WHITE + " - reset map to standard map",
        ChatColor.BLUE + " ImageMap <mapId> reset" + ChatColor.WHITE + " - reset map to standard map",
        ChatColor.BLUE + " ImageMap <image file>" + ChatColor.WHITE + " - draw image on map in hand",
        ChatColor.BLUE + " ImageMap <mapId> <image file>" + ChatColor.WHITE + " - draw image on specified map",
        ChatColor.YELLOW + "  <mapId>" + ChatColor.WHITE + " - map image id",
        ChatColor.YELLOW + "  <image file>" + ChatColor.WHITE + " - the url of the image"
    };
    private final static String[] HelpMob = new String[]{
        ChatColor.YELLOW + "Mob " + ChatColor.WHITE + " - draw mob statue",
        ChatColor.BLUE + " Mob" + ChatColor.WHITE + " - list known mobs",
        ChatColor.BLUE + " Mob <mobName> [<texture id>]" + ChatColor.WHITE + " - draw mob statue",
        ChatColor.YELLOW + "  <mobName>" + ChatColor.WHITE + " - mob name",
        ChatColor.YELLOW + "  <texture id>" + ChatColor.WHITE + " - use alternative mob texture"
    };
    private final static String[] HelpStatue = new String[]{
        ChatColor.YELLOW + "Statue " + ChatColor.WHITE + " - draw statue of player",
        ChatColor.BLUE + " Statue" + ChatColor.WHITE + " - draw yours statue",
        ChatColor.BLUE + " Statue <u:playerName>" + ChatColor.WHITE + " - draw statue of other player",
        ChatColor.BLUE + " Statue <skinFile>" + ChatColor.WHITE + " - draw statue using skin file",};
    private final static String[] HelpPurge = new String[]{
        ChatColor.YELLOW + "Purge " + ChatColor.WHITE + " - remove all queued block operations",
        ChatColor.BLUE + " Purge" + ChatColor.WHITE + " - purges your operations",
        ChatColor.BLUE + " Purge <u:playerName>" + ChatColor.WHITE + " - purges other player operations",
        ChatColor.BLUE + " Purge all" + ChatColor.WHITE + " - purges all operations",};
    private final static String[] HelpJobs = new String[]{
        ChatColor.YELLOW + "Jobs " + ChatColor.WHITE + " - display queued block operations",
        ChatColor.BLUE + " Jobs" + ChatColor.WHITE + " - displays your operations",
        ChatColor.BLUE + " Jobs <u:playerName>" + ChatColor.WHITE + " - displays other player operations",
        ChatColor.BLUE + " Jobs all" + ChatColor.WHITE + " - displays all queued operations",};
    private final static String[] HelpFilter = new String[]{
        ChatColor.YELLOW + "Filter " + ChatColor.WHITE + " - manage image filters",
        ChatColor.BLUE + " Filter" + ChatColor.WHITE + " - display current filter list",
        ChatColor.BLUE + " Filter clear" + ChatColor.WHITE + " - remove all defined filters",
        ChatColor.BLUE + " Filter remove <id>" + ChatColor.WHITE + " - remove filter id",
        ChatColor.BLUE + " Filter list" + ChatColor.WHITE + " - display all available filters",
        ChatColor.BLUE + " Filter help <name>" + ChatColor.WHITE + " - display help for filter <name>",
        ChatColor.BLUE + " Filter add <name> <arg1> <arg2> ..." + ChatColor.WHITE + " - add new filter",
        ChatColor.BLUE + " Filter insert <pos> <name> <arg1> <arg2> ..." + ChatColor.WHITE + " - add new filter",
        ChatColor.YELLOW + "  <name>" + ChatColor.WHITE + " - filter name",
        ChatColor.YELLOW + "  <id>" + ChatColor.WHITE + " - filter id to remove",
        ChatColor.YELLOW + "  <pos>" + ChatColor.WHITE + " - position where to add new filter"
    };

    public static boolean ShowHelp(Player player, String command) {
        String[] help = HelpGlobal;

        if (command != null) {
            if (command.equalsIgnoreCase(Commands.COMMAND_BLOCK)) {
                help = HelpBlock;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_PALETTE)) {
                help = HelpPalette;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_IMAGE)) {
                help = HelpImage;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_IMAGEMAP)) {
                help = HelpImageMap;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_STATUE)) {
                help = HelpStatue;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_MOB)) {
                help = HelpMob;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_PURGE)) {
                help = HelpPurge;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_JOBS)) {
                help = HelpJobs;
            } else if (command.equalsIgnoreCase(Commands.COMMAND_FILTER)) {
                help = HelpFilter;
/*            } else if (command.equalsIgnoreCase(Commands.COMMAND_IMAGEHD)) {
                help = HelpHdImage;*/
            }
        }

        for (String string : help) {
            MCPainterMain.say(player, string);
        }

        return true;
    }
}