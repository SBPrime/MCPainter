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

import java.text.DecimalFormat;
import java.util.Arrays;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.dilters.FilterEntry;
import org.primesoft.mcpainter.drawing.dilters.FilterManager;
import org.primesoft.mcpainter.drawing.dilters.IImageFilter;
import org.primesoft.mcpainter.Help;
import org.primesoft.mcpainter.PermissionManager;
import org.primesoft.mcpainter.MCPainterMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public class FilterCommand {

    public static void Execte(MCPainterMain sender, Player player, String[] args) {
        if (args.length < 1) {
            Help.ShowHelp(player, Commands.COMMAND_FILTER);
            return;
        }

        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.Filter)) {
            MCPainterMain.say(player, ChatColor.RED + "You have no permissions to do that.");
            return;
        }

        FilterManager fm = FilterManager.getFilterManager(player);

        if (args.length == 1) {
            listFilters(fm, player);
            return;
        }

        String cmd = args[1];
        if (args.length == 2) {
            if (cmd.equalsIgnoreCase("clear")) {
                fm.clearFilters();
                listFilters(fm, player);
                return;
            } else if (cmd.equalsIgnoreCase("list")) {
                listAllFilters(player);
                return;
            }

        }
        if (args.length == 3) {
            String p = args[2];
            if (cmd.equalsIgnoreCase("remove")) {
                try {
                    fm.remove(Integer.parseInt(p));
                    listFilters(fm, player);
                    return;
                } catch (NumberFormatException ex) {
                    Help.ShowHelp(player, Commands.COMMAND_FILTER);
                }
            }
            if (cmd.equalsIgnoreCase("help")) {
                showFilterHelp(p, player);
                return;
            }
            if (cmd.equalsIgnoreCase("add")) {
                addFilter(player, fm, args[2], null);
                return;
            }
        }
        if (args.length > 3) {
            if (cmd.equalsIgnoreCase("add")) {
                addFilter(player, fm, args[2], Arrays.copyOfRange(args, 3, args.length));
                return;
            }
            if (cmd.equalsIgnoreCase("insert")) {
                String name = args[3];
                String[] nArgs = args.length > 4 ? Arrays.copyOfRange(args, 4, args.length) : null;
                try {
                    int id = Integer.parseInt(args[2]);
                    FilterEntry filter = FilterManager.getFilter(player, name, nArgs);
                    if (filter != null) {
                        fm.insert(id, filter);
                        MCPainterMain.say(player, "Filter added.");
                        listFilters(fm, player);
                    }
                } catch (NumberFormatException ex) {
                    Help.ShowHelp(player, Commands.COMMAND_FILTER);
                }
                return;
            }
        }
        Help.ShowHelp(player, Commands.COMMAND_FILTER);
    }

    
    /**
     * Add new filter
     * @param player player instance
     * @param fm filter manager
     * @param name filter name
     * @param args filter args
     */
    private static void addFilter(Player player, FilterManager fm, String name, String[] args) {
        FilterEntry filter = FilterManager.getFilter(player, name, args);
        if (filter != null) {
            fm.add(filter);
            MCPainterMain.say(player, "Filter added.");
            listFilters(fm, player);
        }
    }

    
    /**
     * Show help gor filter
     * @param name filter name
     * @param player current player
     */
    private static void showFilterHelp(String name, Player player) {
        IImageFilter filter = FilterManager.getFilter(name);
        if (filter == null) {
            MCPainterMain.say(player, ChatColor.RED + "Error filter \"" + ChatColor.WHITE + name + ChatColor.RED + "\" not found");
        } else {
            for (String s : filter.getHelp()) {
                MCPainterMain.say(player, s);
            }
        }
    }

    
    /**
     * List all available filters
     * @param player Instance of player
     */
    private static void listAllFilters(Player player) {
        IImageFilter[] all = FilterManager.getAvailable();
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        sb.append(ChatColor.YELLOW);
        sb.append("Available filters: ");
        sb.append(ChatColor.WHITE);
        for (IImageFilter filter : all) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(filter.getName());
        }
        MCPainterMain.say(player, sb.toString());
    }

    
    /**
     * Display all configured filters
     * @param fm The filter manager
     * @param player Caller player
     */
    private static void listFilters(FilterManager fm, Player player) {
        FilterEntry[] filters = fm.getAll();
        MCPainterMain.say(player, ChatColor.YELLOW + "Defined filters:");
        double price = 0;
        for (int i = 0; i < filters.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.BLUE);
            sb.append(i + 1);
            sb.append(". ");
            sb.append(filters[i].print());
            MCPainterMain.say(player, sb.toString());
            
            price += ConfigProvider.getCommandPrice(filters[i].getPriceName());
        }
        
        if (price > 0)
        {
            DecimalFormat df = new DecimalFormat("#.##");
            MCPainterMain.say(player, ChatColor.YELLOW + "You need to pay " + 
                    df.format(price) + " to apply those filters.");
        }   
    }
}
