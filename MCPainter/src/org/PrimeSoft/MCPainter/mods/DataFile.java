/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.PrimeSoft.MCPainter.PluginMain;
import org.PrimeSoft.MCPainter.utils.ExtFileFilter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author SBPrime
 */
public class DataFile {        
    public enum DataFileType {
      ModDefinition,
      Statue,
      Unknown
    };
    
    public static DataFile[] processFiles(File dataDir) {
        PluginMain.log("Loading data files...");
        
        List<DataFile> result = new ArrayList<DataFile>();
        File[] files = dataDir.listFiles(new ExtFileFilter(new String[]{ExtFileFilter.YML}));
        for (File file : files) {
            if (!file.canRead()) {
                continue;
            }
                        
            result.add(new DataFile(file));
        }
        return result.toArray(new DataFile[0]);
    }
    
    private static final String FILD_SKIN_URL = "skinurl";
    private static final String FILD_MOD_ID = "modid";
    private static final String FILD_MOBS = "mobs";
    private static final String FILD_BLOCKS = "blocks";
    
    private final DataFileType m_type;
    private final String m_name;
    private final YamlConfiguration m_config;

    
    public Configuration getConfig() {
        return m_config;
    }

    public String getName() {
        return m_name;
    }
    
    public DataFileType getType() {
        return m_type;
    }
    
    private DataFile(File file) {
        m_config = YamlConfiguration.loadConfiguration(file);
        m_name = file.getName();
        
        HashSet<String> keys = new HashSet<String>();
        for (String s : m_config.getKeys(false)) {
            s = s.toLowerCase();
            if (!keys.contains(s)) {
                keys.add(s);
            }
        }
        
        String type;
        if (keys.contains(FILD_SKIN_URL)) {
            m_type = DataFileType.Statue;
            type = "player statue";
        } else if (keys.contains(FILD_MOD_ID) ||
                keys.contains(FILD_MOBS) ||
                keys.contains(FILD_BLOCKS)) {
            m_type = DataFileType.ModDefinition;
            type = "mod file";
        } else {
            m_type = DataFileType.Unknown;
            type = "unknown type";
        }
        PluginMain.log("* " + m_name + "..." + type);
    }
}