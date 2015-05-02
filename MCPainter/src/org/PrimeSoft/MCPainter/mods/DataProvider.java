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

import java.util.ArrayList;
import java.util.List;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.bukkit.configuration.Configuration;

/**
 * This class provides block data
 *
 * @author SBPrime
 */
public class DataProvider {

    /**
     * Load all available mod configurations
     *
     * @param pluginMain
     * @param dataFiles
     * @return
     */
    public static ModConfig[] loadMods(MCPainterMain pluginMain, DataFile[] dataFiles) {
        MCPainterMain.log("Loading mod files...");

        List<ModConfig> result = new ArrayList<ModConfig>();
        ModsProvider modsProvider = pluginMain.getModsProvider();

        for (DataFile file : dataFiles) {
            if (file.getType() == DataFile.DataFileType.ModDefinition) {
                try {
                    ModConfig modConfig = initializeConfig(modsProvider, file);

                    if (modConfig != null) {
                        result.add(modConfig);
                    }
                } catch (Exception ex) {
                    MCPainterMain.log("* " + file.getName() + "...unknown error, " + ex.getMessage());
                }
            }
        }
        return result.toArray(new ModConfig[0]);
    }

    /**
     * Initialize the mod configuration
     *
     * @param modsProvider
     * @param file
     * @return
     */
    private static ModConfig initializeConfig(ModsProvider modsProvider, DataFile file) {
        Configuration config = file.getConfig();
        ModConfig result = new ModConfig(config);

        boolean statues = result.getMobs() != null;
        boolean blocks = result.getBlocks() != null;
        boolean assets = result.getAssets() != null;
        boolean valid = statues || blocks || assets;
        if (!result.isValid() && !valid) {
            MCPainterMain.log("* " + file.getName() + "...bad file format.");
            return null;
        }

        String text = buildAssetsText(statues, blocks, assets);
        if (result.isValid()) {
            Mod mod = modsProvider.get(result.getModIdRegex(), result.getVersionRegex());
            if (mod == null) {
                MCPainterMain.log("* " + file.getName()
                        + "...mod not available."
                        + (text.length() > 0 ? (" Using " + text) : "")
                        + ".");
            } else {
                result.setMod(mod);
                MCPainterMain.log("* " + file.getName() + " (" + result.getName()
                        + ") initialized texture"
                        + (text.length() > 0 ? (", " + text) : "")
                        + ".");
            }
        } else {
            MCPainterMain.log("* " + file.getName() + " (" + result.getName()
                    + ") " + text + " definition initialized.");
        }

        return result;
    }

    /**
     * Build proper assets text message
     *
     * @param statues
     * @param blocks
     * @param assets
     * @return
     */
    private static String buildAssetsText(boolean statues, boolean blocks, boolean assets) {
        List<String> strings = new ArrayList<String>();
        if (statues) {
            strings.add("Statues");
        }
        if (blocks) {
            strings.add("Blocks");
        }
        if (assets) {
            strings.add("Vanilla blocks");
        }

        StringBuilder sb = new StringBuilder();
        int cnt = strings.size() - 1;
        int i = 0;
        for (String s : strings) {
            if (i == 0) {
                sb.append(s);
            } else if (i < cnt) {
                sb.append(", ");
                sb.append(s);
            } else {
                sb.append(" and ");
                sb.append(s);
            }
            i++;
        }
        
        
        return sb.toString();
    }
}
