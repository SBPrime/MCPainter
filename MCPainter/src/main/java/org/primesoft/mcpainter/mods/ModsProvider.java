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
package org.primesoft.mcpainter.mods;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.primesoft.mcpainter.utils.ExtFileFilter;
import org.primesoft.mcpainter.MCPainterMain;

/**
 * This class is used to provide access to available mods
 *
 * @author SBPrime
 */
public class ModsProvider {

    /**
     * All known mods
     */
    private HashMap<String, Mod> m_modsMap;

    /**
     * Initialize the mods class
     *
     * @param modDir
     */
    public ModsProvider(File modDir) {
        m_modsMap = new HashMap<String, Mod>();
        InitializeMods(modDir);
    }

    
    /**
     * Get mod
     * @param modIdRegex ModId regex pattern
     * @return 
     */
    public Mod get(String modIdRegex)
    {
        return get(modIdRegex, null);
    }
    
    
    /**
     * Get mod
     *
     * @param modIdRegex ModId regex pattern
     * @param versionRegex Version regex pattern
     * @return
     */
    public Mod get(String modIdRegex, String versionRegex) {
        for (Map.Entry<String, Mod> entry : m_modsMap.entrySet()) {
            String modId = entry.getKey();            
            Mod mod = entry.getValue();

            if (modId.matches(modIdRegex)) {
                if (versionRegex == null || versionRegex.length() == 0) {
                    return mod;
                }
                
                String version = mod.getModVersion();
                if (version != null && version.matches(versionRegex))
                {
                    return mod;
                }
            }
        }

        return null;
    }

    /**
     * Initialize the mod fils
     *
     * @param modDir
     */
    private void InitializeMods(File modDir) {
        File[] modFiles = modDir.listFiles(new ExtFileFilter(new String[]{ExtFileFilter.JAR, ExtFileFilter.ZIP}));

        MCPainterMain.log("Scanning mod files:");
        for (File file : modFiles) {
            try {
                Mod mod = new Mod(file);
                String modId = mod.getModId();
                String version = mod.getModVersion();
                m_modsMap.put(modId, mod);

                MCPainterMain.log("* " + file.getName() + " mod: " + modId + " (" + version + ")");
            } catch (IOException ex) {
                MCPainterMain.log("* " + file.getName() + " error: " + ex.getMessage());
            }
        }
    }
}
