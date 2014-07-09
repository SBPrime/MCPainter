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
     * @param dataDir
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
        boolean valid = statues || blocks;
        if (!result.isValid() && !valid) {
            MCPainterMain.log("* " + file.getName() + "...bad file format.");
            return null;
        }

        String text = "";
        if (statues && blocks) {
            text = "blocks and statues";
        } else if (statues) {
            text = "statues";
        } else if (blocks) {
            text = "blocks";
        }
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
}
