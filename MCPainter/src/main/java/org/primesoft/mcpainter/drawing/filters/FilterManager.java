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
package org.primesoft.mcpainter.drawing.filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.configuration.OperationType;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.MCPainterMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;

/**
 *
 * @author SBPrime
 */
public class FilterManager {

    private final static List<IImageFilter> s_filters = new ArrayList();
    private final static HashMap<Player, FilterManager> s_playerFilters = new HashMap<Player, FilterManager>();
    private final static IColorPalette s_mapPalette = getMapPalette();

    /**
     * Initialize default filters
     */
    public static void initializeFilters() {
        s_filters.clear();
        s_playerFilters.clear();
        registerFilter(new CropFilter());
        registerFilter(new ResizeFilter());
        registerFilter(new Grayscale());
        registerFilter(new Dither());
    }
    
    
    /**
     * Initialize the map palette
     *
     * @return Map pallete
     */
    private static IColorPalette getMapPalette() {
        List<Color> result = new ArrayList<Color>();
        try {
            for (int i = 0; i < 256; i++) {
                Color c = MapPalette.getColor((byte)i);
                if (c.getAlpha()<128)
                {
                    continue;
                }
                result.add(c);
            }
        } catch (Exception ex) {
            //Ignore exception
        }

        return new ColorPalette(result.toArray(new Color[0]));
    }


    public static IImageFilter getFilter(String name) {
        for (IImageFilter filter : s_filters) {
            if (filter.getName().equalsIgnoreCase(name)) {
                return filter;
            }
        }

        return null;
    }

    public static FilterEntry getFilter(Player player, String name, String[] args) {
        for (IImageFilter filter : s_filters) {
            if (filter.getName().equalsIgnoreCase(name)) {
                if (filter.hasPerms(player)) {
                    FilterEntry result = filter.getEntry(args);
                    if (result == null) {
                        MCPainterMain.say(player, ChatColor.RED + "Bad filter parameters.");
                        displayHelp(player, filter.getHelp());
                        return null;
                    }

                    return result;
                } else {
                    MCPainterMain.say(player, ChatColor.RED + "You have no permissions to use " + ChatColor.WHITE + name + ChatColor.RED + "filter.");
                    return null;
                }
            }
        }
        MCPainterMain.say(player, ChatColor.RED + "Error filter \"" + ChatColor.WHITE + name + ChatColor.RED + "\" not found");
        return null;
    }

    private static void displayHelp(Player player, String[] help) {
        for (String string : help) {
            MCPainterMain.say(player, string);
        }
    }

    public static void registerFilter(IImageFilter filter) {
        s_filters.add(filter);
    }

    public static FilterManager getFilterManager(Player p) {
        synchronized (s_playerFilters) {
            if (s_playerFilters.containsKey(p)) {
                return s_playerFilters.get(p);
            }

            FilterManager manager = new FilterManager(p);
            s_playerFilters.put(p, manager);
            return manager;
        }
    }

    public static IImageFilter[] getAvailable() {
        synchronized (s_filters) {
            return s_filters.toArray(new IImageFilter[0]);
        }
    }
    private final Player m_player;
    private final List<FilterEntry> m_filters;

    private FilterManager(Player p) {
        m_player = p;
        m_filters = new ArrayList<FilterEntry>();
    }

    public BufferedImage applyFilters(BufferedImage img, IColorMap cMap) {
        IColorPalette palette = cMap != null ? cMap.getPalette(OperationType.Image) : s_mapPalette;

        synchronized (m_filters) {
            for (FilterEntry fe : m_filters) {
                IImageFilter filter = fe.getFilter();
                IFilterParams params = fe.getParams();
                if (params != null && params instanceof IPaletteParam) {
                    ((IPaletteParam) params).setPalette(palette);
                }

                String name = filter.getName();

                MCPainterMain.say(m_player, "Applying filter: " + ChatColor.WHITE + name);
                if (filter.hasPerms(m_player)) {
                    BufferedImage result = filter.process(img, params);
                    if (result == null) {
                        MCPainterMain.say(m_player, ChatColor.RED + "Error applying filter \"" + ChatColor.WHITE + name + ChatColor.RED + "\" check parameters.");
                    } else {
                        img = result;
                    }
                } else {
                    MCPainterMain.say(m_player, ChatColor.RED + "You have no permissions to use " + ChatColor.WHITE + name + ChatColor.RED + "filter.");
                }
            }
            return img;
        }
    }

    public void clearFilters() {
        synchronized (m_filters) {
            m_filters.clear();
        }
    }

    public void remove(int idx) {
        synchronized (m_filters) {
            if (idx < 1 || idx > m_filters.size()) {
                return;
            }

            m_filters.remove(idx - 1);
        }
    }

    public void add(FilterEntry filter) {
        synchronized (m_filters) {
            m_filters.add(filter);
        }
    }

    public void insert(int idx, FilterEntry filter) {
        synchronized (m_filters) {
            if (idx < 1) {
                idx = 1;
            } else if (idx > m_filters.size()) {
                add(filter);
                return;
            }

            m_filters.add(idx - 1, filter);
        }
    }

    /**
     * Get all filters
     *
     * @return All filters
     */
    public FilterEntry[] getAll() {
        synchronized (m_filters) {
            return m_filters.toArray(new FilterEntry[0]);
        }
    }
    
    /**
     * Get price for current command list
     * @return The price
     */
    public double getPrice() {
        double price = 0;
        synchronized (m_filters) {
            for (FilterEntry fe : m_filters) {
                price += ConfigProvider.getCommandPrice(fe.getPriceName());
            }
        }
        
        return price;
    }
}
