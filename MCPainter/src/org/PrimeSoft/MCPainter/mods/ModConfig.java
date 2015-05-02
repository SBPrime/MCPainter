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
package org.PrimeSoft.MCPainter.mods;

import java.io.File;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This class holds description of elements provided by the mod
 *
 * @author SBPrime
 */
public class ModConfig {

    /**
     * The texture resolution
     */
    private final int m_textureRes;
    
    /**
     * Mod ID regexp
     */
    private final String m_modIdRegex;
    
    /**
     * Mod version regex
     */
    private final String m_versionRegex;
    
    /**
     * Defined blocks section
     */
    private final ConfigurationSection m_blocks;
    
    /**
     * Defined mobs section
     */
    private final ConfigurationSection m_mobs;
    
    /**
     * Is the mod initialized
     */    
    private boolean m_isInitialized;
    
    /**
     * Mod file
     */
    private File m_modFile;
    
    /**
     * The mod id
     */
    private String m_modId;
    
    /**
     * Mot alternative Id
     */
    private String m_alternativeId;
    
    /**
     * Mod config display name
     */
    private String m_name;
    
    
    /**
     * Assets directory
     */
    private String m_assets;
    

    /**
     * Is the mod valid
     *
     * @return
     */
    public boolean isValid() {
        return m_modIdRegex != null && m_modIdRegex.length() > 0;
    }

    /**
     * Is the mod initialized
     *
     * @return
     */
    public boolean isInitialized() {
        return m_isInitialized;
    }

    /**
     * The mod id regex
     *
     * @return
     */
    public String getModIdRegex() {
        return m_modIdRegex;
    }

    /**
     * The mod id
     *
     * @return
     */
    public String getModId() {
        return m_modId;
    }

    /**
     * Get mod name
     *
     * @return
     */
    public String getName() {
        String name = m_name;
        if (name == null || name.length() == 0) {
            name = getModAlternativeId();
        }
        if (name == null || name.length() == 0) {
            name = getModId();
        }
        if (name == null || name.length() == 0) {
            name = "unknown";
        }
        return name;
    }

    /**
     * The alternative mod id
     *
     * @return
     */
    public String getModAlternativeId() {
        return m_alternativeId;
    }

    /**
     * The mod file
     *
     * @return
     */
    public File getModFile() {
        return m_modFile;
    }

    /**
     * The mod version regex
     *
     * @return
     */
    public String getVersionRegex() {
        return m_versionRegex;
    }

    /**
     * Get blocks section
     *
     * @return
     */
    public ConfigurationSection getBlocks() {
        return m_blocks;
    }
    
    /**
     * Get the assets path
     * @return 
     */
    public String getAssets() {
        return m_assets;
    }

    /**
     * Get mobs section
     *
     * @return
     */
    public ConfigurationSection getMobs() {
        return m_mobs;
    }

    /**
     * The mod texture res
     *
     * @return
     */
    public int getTextureRes() {
        return m_textureRes;
    }

    /**
     * Initialize mod config based on the configuration file
     *
     * @param config
     */
    public ModConfig(Configuration config) {
        m_isInitialized = false;
        if (config == null) {
            m_blocks = null;
            m_mobs = null;
            m_modIdRegex = null;
            m_textureRes = 0;
            m_versionRegex = null;
            return;
        }

        m_name = config.getString("DisplayName", null);
        m_blocks = config.getConfigurationSection("Blocks");
        m_assets = config.getString("Assets", null);
        m_mobs = config.getConfigurationSection("Mobs");
        m_modIdRegex = config.getString("ModId", null);
        m_alternativeId = config.getString("ModIdAlternative", null);
        m_versionRegex = config.getString("Version", null);
        m_textureRes = config.getInt("TextureRes", 0);
    }

    /**
     * Set the mod file
     *
     * @param mod
     */
    public void setMod(Mod mod) {
        m_modId = mod.getModId();
        m_modFile = mod.getFile();
        m_isInitialized = true;
    }
}