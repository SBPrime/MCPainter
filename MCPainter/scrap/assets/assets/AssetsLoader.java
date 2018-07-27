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
package org.primesoft.mcpainter.mods.assets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.primesoft.mcpainter.drawing.blocks.AssetBlock;
import static org.primesoft.mcpainter.MCPainterMain.log;
import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.mods.AssetsBlockProvider;
import org.primesoft.mcpainter.mods.ModConfig;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author SBPrime
 */
public class AssetsLoader {

    private final static String TYPE_BLOCK = "block";
    private final static String TYPE_BUILTIN = "builtin";

    public static AssetsBlockProvider load(TextureManager textureManager, 
            ModConfig modConfig) {
        log(" ...loading vanilla assets");

        
        String assets = modConfig.getAssets();
        String modId = modConfig.getModId();
        File modFile = modConfig.getModFile();
        
        AssetsBlockProvider result = null;
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(modFile);

            return safeLoad(textureManager, assets, modId, zipFile);
        } catch (IOException ex) {
            log("    Error: " + ex.getMessage());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                }
            }
        }

        return result;
    }

    private static AssetsBlockProvider safeLoad(TextureManager textureManager, String assets, String mod,
            ZipFile zipFile) {
        assets = correct(assets);

        List<ZipEntry> blockStats = new ArrayList<ZipEntry>();
        List<ZipEntry> modelsBlocks = new ArrayList<ZipEntry>();
        List<ZipEntry> modelsItems = new ArrayList<ZipEntry>();

        filterEntries(zipFile, correct(assets), blockStats, modelsBlocks, modelsItems);
        log(String.format(" ...Found %d block states, %d block models, %d items models.",
                blockStats.size(), modelsBlocks.size(), modelsItems.size()));

        HashMap<String, AssetsModel> knownModels = loadModels(zipFile, modelsBlocks, mod);
        resolveParrents(knownModels);
        log(String.format(" ...Loaded %s models", knownModels.size()));
        
        List<AssetVariant> knownVariants = loadVariants(zipFile, blockStats);
        resolveModel(knownVariants, knownModels);
        log(String.format(" ...Loaded %s model variants", knownVariants.size()));

        //We no longer need the model catche
        knownModels.clear();
        
        
        final HashMap<String, AssetBlock> blocks = new HashMap<String, AssetBlock>();
        for (AssetVariant variant : knownVariants) {
            log(String.format("    Compiling model \"%s\"", variant.getName()));
            AssetBlock aBlock = variant.compile(textureManager, assets);
            if (aBlock != null) {
                blocks.put(variant.getName(), aBlock);
            }
        }
        
        return new AssetsBlockProvider(blocks);
    }

    
    /**
     * Load the model variants
     * 
     * @param modFile
     * @param blockStats
     * @return 
     */
    private static List<AssetVariant> loadVariants(ZipFile modFile, List<ZipEntry> blockStats) {
        final List<AssetVariant> result = new ArrayList<AssetVariant>();
        
        for (ZipEntry modelEntry : blockStats) {
            final String name = extractName(modelEntry);

            //log(String.format(" ...loading %s model definition", name));
            try {
                InputStream is = modFile.getInputStream(modelEntry);
                InputStreamReader reader = new InputStreamReader(is);
                Object o = JSONValue.parseWithException(reader);

                if (o instanceof JSONObject) {
                    result.add(new AssetVariant(name, (JSONObject) o));
                } else {
                    log(String.format("    Model %s: expected JSONOBject found %s", name, o.getClass().getName()));
                }
            } catch (IOException ex) {
                log(String.format("    Model %s: unable to read the file, %s", name, ex.getMessage()));
            } catch (ParseException ex) {
                log(String.format("    Model %s: invalid format, %s", name, ex.getMessage()));
            }
        }

        return result;
    }
    
    
    /**
     * Load the models
     *
     * @param modelsBlocks
     * @return
     */
    private static HashMap<String, AssetsModel> loadModels(ZipFile modFile, List<ZipEntry> models, String mod) {
        final HashMap<String, AssetsModel> result = new HashMap<String, AssetsModel>();

        for (ZipEntry modelEntry : models) {
            final String name = extractName(modelEntry);

            //log(String.format(" ...loading %s model definition", name));
            try {
                InputStream is = modFile.getInputStream(modelEntry);
                InputStreamReader reader = new InputStreamReader(is);
                Object o = JSONValue.parseWithException(reader);

                if (o instanceof JSONObject) {
                    result.put(name, new AssetsModel(name, mod, (JSONObject) o));
                } else {
                    log(String.format("    Model %s: expected JSONOBject found %s", name, o.getClass().getName()));
                }
            } catch (IOException ex) {
                log(String.format("    Model %s: unable to read the file, %s", name, ex.getMessage()));
            } catch (ParseException ex) {
                log(String.format("    Model %s: invalid format, %s", name, ex.getMessage()));
            }
        }

        return result;
    }

    /**
     * Extract name from zip entry
     *
     * @param name
     * @return
     */
    private static String extractName(ZipEntry entry) {
        final String[] parts = entry.getName().replace(".json", "").split("/|\\\\");
        return parts[parts.length - 1];
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

    /**
     * Link models to variants and remove empty variants
     * @param variants
     * @param models 
     */
    private static void resolveModel(List<AssetVariant> variants, HashMap<String, AssetsModel> models) {
        final List<AssetVariant> toRemove = new ArrayList<AssetVariant>();
        
        for (AssetVariant variant : variants)
        {
            variant.resolveModel(models);
            if (variant.isEmpty()) {
                log(String.format("     Model %s has no valid entries, removing.", variant.getName()));
                toRemove.add(variant);
            }
        }
        
        for (AssetVariant variant : toRemove) {
            variants.remove(variant);
        }
    }
    
    
    /**
     * Resolve the models parents
     * @param models 
     */
    private static void resolveParrents(HashMap<String, AssetsModel> models) {
        List<AssetsModel> toRemove = new ArrayList<AssetsModel>();
        
        for (AssetsModel model : models.values()) {
            AssetsModel currentParrent = model.getParrent();

            if (currentParrent instanceof LinkAssetModel) {
                LinkAssetModel lam = (LinkAssetModel) currentParrent;
                String name = lam.getName();
                String type = lam.getType();

                if (type.equals(TYPE_BUILTIN)) {
                    toRemove.add(model);
                    log(String.format(" ...Unsupported parrent BUILTIN type %s for %s.", name, model.getName()));
                } else if (type.equals(TYPE_BLOCK)) {
                    if (models.containsKey(name)) {
                        AssetsModel pModel = models.get(name);
                        AssetsModel tmp = pModel;
                        while (tmp != null && tmp != model) {
                            tmp = tmp.getParrent();
                        }
                        if (tmp == model) {
                            toRemove.add(model);
                            log(String.format(" ...Detected parrent reference loop for BLOCK %s.", model.getName()));
                        } else {                        
                            model.setParrent(pModel);
                        }
                    } else {
                        toRemove.add(model);
                        log(String.format(" ...Undefined parent BLOCK type %s for %s.", name, model.getName()));
                    }
                } else {
                    toRemove.add(model);
                    log(String.format(" ...Unsupported parrent type %s for %s.", type, model.getName()));
                }

            }
        }
        
        int iteration = 1;        
        while (!toRemove.isEmpty())  {
            log(String.format("     Iteration %d: %d models queued for removal.",
                iteration, toRemove.size()));
            AssetsModel[] tmp = toRemove.toArray(new AssetsModel[0]);
            toRemove.clear();
            
            for (AssetsModel modelRemoved : tmp) {
                models.remove(modelRemoved.getName());
                
                int cnt = 0;
                for (AssetsModel model : models.values()) {
                    if (model.getParrent() == modelRemoved)
                    {
                        cnt++;
                        toRemove.add(model);
                    }
                }
                                
                log(String.format("      (%d) Removed model %s and queued %d for removal.",
                        iteration, modelRemoved.getName(), cnt));
            }
            iteration++;
        }
    }   
}
