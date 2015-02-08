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
package org.PrimeSoft.MCPainter.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This class contains configuration
 *
 * @author SBPrime
 */
public class ConfigProvider {
    private static final int CONFIG_VERSION = 5;

    public static final int BLOCK_SIZE = 16;

    private static final HashMap<String, Double> m_commandPrice = new HashMap<String, Double>();

    private static SizeNode[] m_sizeEntries = new SizeNode[0];

    private static SizeNode m_maxImage = new SizeNode("0x0");

    private static String[] m_texturePacks;

    private static boolean m_checkUpdate = false;

    private static boolean m_isConfigUpdate = false;

    private static File m_pluginFolder;

    private static File m_imgFolder;

    private static File m_modelFolder;
    
    private static File m_paletteFolder;

    private static File m_modFolder;

    private static File m_dataFolder;

    private static String m_configVersion;
    
    private static String m_defaultPalette;

    private static boolean m_checkAccess;

    private static boolean m_logBlocks;

    public static String getConfigVersion() {
        return m_configVersion;
    }

    /**
     * Plugin root folder
     *
     * @return
     */
    public static File getPluginFolder() {
        return m_pluginFolder;
    }

    /**
     * Folder where the palettes are stored
     *
     * @return
     */
    public static File getPaletteFolder() {
        return m_paletteFolder;
    }    
    
    /**
     * Folder where the models are stored
     *
     * @return
     */
    public static File getModelFolder() {
        return m_modelFolder;
    }

    /**
     * Folder where the image maps are stored
     *
     * @return
     */
    public static File getImgFolder() {
        return m_imgFolder;
    }

    /**
     * The plugin data folder
     *
     * @return
     */
    public static File getDataFolder() {
        return m_dataFolder;
    }

    /**
     * The default palette name
     * @return 
     */
    public static String getDefaultPalette() {
        return m_defaultPalette;
    }

    public static SizeNode[] getSizeNodes() {
        return m_sizeEntries;
    }

    public static SizeNode getMaxSize() {
        return m_maxImage;
    }

    /**
     * Is update checking enabled
     *
     * @return true if enabled
     */
    public static boolean getCheckUpdate() {
        return m_checkUpdate;
    }

    /**
     * Is block login enabled
     *
     * @return
     */
    public static boolean getLogBlocks() {
        return m_logBlocks;
    }

    /**
     * Is block perms checking enabled
     *
     * @return
     */
    public static boolean getCheckAccess() {
        return m_checkAccess;
    }

    /**
     * Is the configuration up to date
     *
     * @return
     */
    public static boolean isConfigUpdated() {
        return m_isConfigUpdate;
    }

    /**
     * The mod (jar) folder
     */
    public static File getModFolder() {
        return m_modFolder;
    }

    /**
     * All texture packs
     *
     * @return
     */
    public static String[] getTexturePacks() {
        return m_texturePacks;
    }

    /**
     * Is any of the texture packs enabled
     *
     * @return
     */
    public static boolean isTexturePackEnabled() {
        return m_texturePacks != null && m_texturePacks.length > 0;
    }

    /**
     * Load configuration
     *
     * @param plugin parent plugin
     * @return true if config loaded
     */
    public static boolean load(MCPainterMain plugin) {
        if (plugin == null) {
            return false;
        }

        plugin.saveDefaultConfig();

        Configuration config = plugin.getConfig();
        m_pluginFolder = plugin.getDataFolder();
        m_imgFolder = new File(m_pluginFolder, "img");
        if (!m_imgFolder.exists()) {
            m_imgFolder.mkdir();
        }
        m_modelFolder = new File(m_pluginFolder, "models");
        if (!m_modelFolder.exists()) {
            m_modelFolder.mkdir();
        }
        m_paletteFolder = new File(m_pluginFolder, "palette");
        if (!m_paletteFolder.exists()) {
            m_paletteFolder.mkdir();
        }
        m_dataFolder = new File(m_pluginFolder, "data");
        if (!m_dataFolder.exists()) {
            m_dataFolder.mkdir();
        }

        ConfigurationSection mainSection = config.getConfigurationSection("mcpainter");
        if (mainSection == null) {
            return false;
        }
        m_configVersion = mainSection.getString("version", "?");
        parsePriceSection(mainSection);

        m_checkUpdate = mainSection.getBoolean("checkVersion", true);
        m_isConfigUpdate = mainSection.getInt("version", 1) == CONFIG_VERSION;
        m_maxImage = new SizeNode(mainSection.getString("maxSize", "0x0"));
        m_modFolder = new File(m_pluginFolder, mainSection.getString("modsFolder", "mods"));
        m_defaultPalette = mainSection.getString("palette", "default").toLowerCase();

        m_texturePacks = parseTextures(mainSection.getStringList("texturePacks"));

        if (!m_modFolder.exists()) {
            m_modFolder.mkdir();
        }

        m_sizeEntries = parseSizeNodeSection(mainSection);
        parseBlocksHubSection(mainSection.getConfigurationSection("blocksHub"));
        
        MCPainterMain.log(m_sizeEntries.length + " size nodes defined in config file.");
        return true;
    }

    /**
     * Initialize blocks hub configuration
     *
     * @param bhSection
     */
    private static void parseBlocksHubSection(ConfigurationSection bhSection) {
        if (bhSection == null) {
            m_logBlocks = true;
            m_checkAccess = false;
        } else {
            m_logBlocks = bhSection.getBoolean("logBlocks", true);
            m_checkAccess = bhSection.getBoolean("checkAccess", false);
        }
    }

    /**
     * Parse the node size section
     *
     * @param mainSection
     * @return
     */
    private static SizeNode[] parseSizeNodeSection(
            ConfigurationSection mainSection) {
        List<SizeNode> sizeEntries = new ArrayList();
        for (String string : mainSection.getStringList("maxSizeNodes")) {
            try {
                sizeEntries.add(new SizeNode(string));
            }
            catch (Exception e) {
                MCPainterMain.log("Error parsing config entry: " + string);
            }
        }
        return sizeEntries.toArray(new SizeNode[0]);
    }

    /**
     * Parse the price section entry
     *
     * @param mainSection
     */
    private static void parsePriceSection(ConfigurationSection mainSection) {
        for (String string : mainSection.getStringList("price")) {
            try {
                String[] parts = string.split(":");
                if (parts.length != 2) {
                    MCPainterMain.log("* Error parsing price entry: " + string);
                    continue;
                }

                String command = parts[0];
                double price = Double.parseDouble(parts[1]);

                m_commandPrice.put(command, price);
            }
            catch (Exception e) {
                MCPainterMain.log("* Error parsing price entry: " + string);
            }
        }
    }

    /**
     * Get price for command
     *
     * @param command The command
     * @return Command price
     */
    public static double getCommandPrice(String command) {
        if (m_commandPrice.containsKey(command)) {
            return m_commandPrice.get(command);
        }

        return 0;
    }

    /**
     * Parse texture packs entrie
     *
     * @param stringList
     * @return
     */
    private static String[] parseTextures(List<String> stringList) {
        if (stringList == null) {
            return new String[0];
        }

        return stringList.toArray(new String[0]);
    }
}