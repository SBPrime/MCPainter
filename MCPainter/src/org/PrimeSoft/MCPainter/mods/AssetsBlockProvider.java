/*
 * The MIT License
 *
 * Copyright 2015 SBPrime.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.PrimeSoft.MCPainter.Drawing.Blocks.IBlockProvider;
import org.PrimeSoft.MCPainter.Drawing.Blocks.IDrawableElement;
import static org.PrimeSoft.MCPainter.MCPainterMain.log;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.mods.assets.AssetsModel;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author SBPrime
 */
public class AssetsBlockProvider implements IBlockProvider {
    
    private final HashMap<String, IDrawableElement> m_nameBlocks;
    private final int m_cnt;
    
    public AssetsBlockProvider(TextureManager textureManager, String assets, ZipFile modFile) {
        m_cnt = 0;
        m_nameBlocks = new HashMap<String, IDrawableElement>();
        
        assets = correct(assets);
        
        List<ZipEntry> blockStats = new ArrayList<ZipEntry>();
        List<ZipEntry> modelsBlocks = new ArrayList<ZipEntry>();
        List<ZipEntry> modelsItems = new ArrayList<ZipEntry>();
        
        filterEntries(modFile, correct(assets), blockStats, modelsBlocks, modelsItems);
        
        HashMap<String, AssetsModel> knownModels = loadModels(modFile, modelsBlocks);
        log("Loaded models: " + knownModels.size());
    }

    /**
     * Load the models
     *
     * @param modelsBlocks
     * @return
     */
    private HashMap<String, AssetsModel> loadModels(ZipFile modFile, List<ZipEntry> models) {        
        final HashMap<String, AssetsModel> result = new HashMap<String, AssetsModel>();
        
        for (ZipEntry modelEntry : models) {
            final String[] parts = modelEntry.getName().replace(".json", "").split("/|\\\\");
            final String name = parts[parts.length - 1];
            
            try {
                InputStream is = modFile.getInputStream(modelEntry);
                InputStreamReader reader = new InputStreamReader(is);
                Object o = JSONValue.parseWithException(reader);
                
                if (o instanceof JSONObject) {
                    //log("Parsing: " + name);
                    result.put(name, new AssetsModel(name, (JSONObject) o));
                } else {
                    log("Unable to parse " + name + ": expected JSONOBject found " + o.getClass().getName());
                }
            } catch (IOException ex) {
                log("Unable to read " + name + ": " + ex.getMessage());
            } catch (ParseException ex) {
                log("Invalid format " + name + ": " + ex.getMessage());
            }
        }
        
        return result;
    }

    /**
     * Correct the assets dir
     *
     * @param assets
     * @return
     */
    private static String correct(String assets) {
        final String[] parts = assets.split("/|\\\\");
        final StringBuilder sb = new StringBuilder();
        
        boolean isFirst = true;
        for (String s : parts) {
            if (!s.isEmpty()) {
                if (!isFirst) {
                    sb.append("/");
                } else {
                    isFirst = false;
                }
                sb.append(s);
            }
        }
        sb.append("/");
        
        return sb.toString().toLowerCase();
    }

    /**
     * Filter ansd extract all entries from assets
     *
     * @param modFile
     * @param assets
     * @param blockStats
     * @param modelsBlocks
     * @param modelsItems
     */
    private static void filterEntries(final ZipFile modFile, final String assetsPrefix,
            final List<ZipEntry> blockStats, final List<ZipEntry> modelsBlocks, final List<ZipEntry> modelsItems) {
        
        final String prefixBlockStats = assetsPrefix + "blockstates/";
        final String prefixModelBlocks = assetsPrefix + "models/block/";
        final String prefixModelItems = assetsPrefix + "models/item/";
        
        for (Enumeration<? extends ZipEntry> iterator = modFile.entries(); iterator.hasMoreElements();) {
            ZipEntry item = iterator.nextElement();
            
            if (item.isDirectory()) {
                continue;
            }
            
            String name = item.getName().toLowerCase();
            if (!name.startsWith(assetsPrefix) || !name.endsWith("json")) {
                continue;
            }
            
            if (name.startsWith(prefixBlockStats)) {
                blockStats.add(item);
            }
            
            if (name.startsWith(prefixModelBlocks)) {
                modelsBlocks.add(item);
            }
            
            if (name.startsWith(prefixModelItems)) {
                modelsItems.add(item);
            }
        }
    }
    
    @Override
    public int getBlocksCount() {
        return m_cnt;
    }
    
    @Override
    public IDrawableElement getBlock(String name) {
        if (name == null) {
            return null;
        }
        
        name = name.toUpperCase();
        if (m_nameBlocks.containsKey(name)) {
            return m_nameBlocks.get(name);
        }
        
        return null;
    }
    
    @Override
    public IDrawableElement getBlock(int materialId) {
        return null;
    }
}
