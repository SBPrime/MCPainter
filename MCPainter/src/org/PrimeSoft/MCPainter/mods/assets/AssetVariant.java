/*
 * The MIT License
 *
 * Copyright 2015 prime.
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
package org.PrimeSoft.MCPainter.mods.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.PrimeSoft.MCPainter.MCPainterMain.log;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.utils.JSONExtensions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author SBPrime
 */
public class AssetVariant {

    private final static String PROP_VARIANTS = "variants";

    /**
     * The block name
     */
    private final String m_name;

    /**
     * List of all variants for this block
     */
    private final HashMap<VariantKey, List<VariantEntry>> m_variants = new HashMap<VariantKey, List<VariantEntry>>();

    /**
     * The variant name (material name)
     *
     * @return
     */
    public String getName() {
        return m_name;
    }

    /**
     * Is the variant empty
     *
     * @return
     */
    public boolean isEmpty() {
        return m_variants.isEmpty();
    }

    public AssetVariant(String name, JSONObject data) {
        m_name = name;

        JSONObject variants = JSONExtensions.tryGet(data, PROP_VARIANTS);
        if (variants == null) {
            return;
        }

        for (Object key : variants.keySet()) {
            VariantKey vKey = new VariantKey(key.toString());
            List<VariantEntry> entries;

            if (m_variants.containsKey(vKey)) {
                entries = m_variants.get(vKey);
            } else {
                entries = new ArrayList<VariantEntry>();
                m_variants.put(vKey, entries);
            }

            Object v = variants.get(key);
            if (v instanceof JSONObject) {
                entries.add(new VariantEntry((JSONObject) v));
            } else if (v instanceof JSONArray) {
                JSONArray array = (JSONArray) v;

                for (Iterator iterator = array.iterator(); iterator.hasNext();) {
                    Object next = iterator.next();

                    if (next instanceof JSONObject) {
                        entries.add(new VariantEntry((JSONObject) next));
                    }
                }
            }
        }

//        JSONExtensions.printUnused(data,
//                new String[]{PROP_VARIANTS},
//                "Unknown assets variant property: ");        
    }

    /**
     * Resolve variant models
     *
     * @param models
     */
    public void resolveModel(HashMap<String, AssetsModel> models) {
        final List<VariantKey> toRemove = new ArrayList<VariantKey>();

        int id = 1;
        for (VariantKey key : m_variants.keySet()) {
            final List<VariantEntry> entries = m_variants.get(key);
            final List<VariantEntry> toRemoveChild = new ArrayList<VariantEntry>();

            for (VariantEntry entry : entries) {
                String name = entry.getModel().getName();
                if (models.containsKey(name)) {
                    AssetsModel model = models.get(name);
                    model.incUsageCount();
                    entry.setModel(model);
                } else {
                    toRemoveChild.add(entry);
                }
            }

            if (!toRemoveChild.isEmpty()) {
                log(String.format("       Model %s removing variant #%d entries (%d of %d).",
                        m_name, id, toRemoveChild.size(), entries.size()));
                entries.removeAll(toRemoveChild);
            }

            if (entries.isEmpty()) {
                log(String.format("       Model %s variant #%d is empty.",
                        m_name, id, toRemoveChild.size(), entries.size()));
                toRemove.add(key);
            }

            id++;
        }
        
        for (VariantKey key : toRemove) {
            m_variants.remove(key);
        }
    }

    
    /**
     * Render the variant
     * @param textureManager 
     */
    public void render(TextureManager textureManager) {        
        for (Map.Entry<VariantKey, List<VariantEntry>> entrySet : m_variants.entrySet()) {
            VariantKey key = entrySet.getKey();
            List<VariantEntry> values = entrySet.getValue();
            
            for (VariantEntry variant : values) {
                variant.render(textureManager);
            }
        }
    }
}
