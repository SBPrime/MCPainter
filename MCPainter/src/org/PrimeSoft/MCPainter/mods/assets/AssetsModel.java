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
package org.PrimeSoft.MCPainter.mods.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.PrimeSoft.MCPainter.MCPainterMain.log;
import org.PrimeSoft.MCPainter.Texture.TextureDescription;
import org.PrimeSoft.MCPainter.Texture.TextureEntry;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.utils.JSONExtensions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author SBPrime
 */
public class AssetsModel {

    private final static String PROP_TEXTURES = "textures";
    private final static String PROP_ELEMENTS = "elements";
    private final static String PROP_PARENT = "parent";

    /**
     * The parrent
     */
    private AssetsModel m_parrent;

    /**
     * List of all textures
     */
    private final HashMap<String, String> m_textures;

    /**
     * List of elements
     */
    private final List<AssetsCube> m_elements;

    /**
     * The model name
     */
    private final String m_name;

    /**
     * Model usage count
     */
    private int m_usageCount;

    /**
     * The mod ID
     */
    private final String m_mod;

    /**
     * The model name
     *
     * @return
     */
    public String getName() {
        return m_name;
    }

    /**
     * The parrent asset
     *
     * @return
     */
    public AssetsModel getParrent() {
        return m_parrent;
    }

    /**
     * Set the parrent
     *
     * @param parrent
     */
    public void setParrent(AssetsModel parrent) {
        m_parrent = parrent;
    }

    /**
     * Get model usage count
     *
     * @return
     */
    public int getUsageCount() {
        return m_usageCount;
    }

    /**
     * Increase model usage count
     */
    public void incUsageCount() {
        m_usageCount++;
    }

    protected AssetsModel(String name, String mod) {
        m_elements = null;
        m_parrent = null;
        m_textures = null;
        m_name = name;
        m_mod = mod;
    }

    public AssetsModel(String name, String mod, JSONObject data) {
        String parrent = JSONExtensions.tryGetString(data, PROP_PARENT, null);
        JSONObject textures = JSONExtensions.tryGet(data, PROP_TEXTURES);
        JSONArray elements = JSONExtensions.tryGetArray(data, PROP_ELEMENTS);

        m_name = name;
        m_mod = mod;

        if (parrent == null || parrent.isEmpty()) {
            m_parrent = null;
        } else {
            m_parrent = new LinkAssetModel(parrent);
        }

        m_elements = new ArrayList<AssetsCube>();
        m_textures = new HashMap<String, String>();

        if (textures != null) {
            for (Object key : textures.keySet()) {
                String sKey = (String) key;
                String sValue = (String) textures.get(key);

                m_textures.put(sKey, sValue);
            }
        }

        if (elements != null) {
            for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
                Object o = iterator.next();

                if (o instanceof JSONObject) {
                    m_elements.add(new AssetsCube((JSONObject) o));
                }
            }
        }

//        JSONExtensions.printUnused(data,
//                new String[]{PROP_ELEMENTS, PROP_PARENT, PROP_TEXTURES},
//                "Unknown assets model property: ");
    }

    /**
     * Render the model
     *
     * @param rotX
     * @param rotY
     * @param uvLock
     * @param textureManager
     * @param assetsRoot
     */
    public void render(int rotX, int rotY, boolean uvLock,
            TextureManager textureManager, String assetsRoot) {        
        HashMap<String, String> textures = getTextures();
        HashMap<String, TextureEntry> texturesImages = TextureResolver.resolveTextures(m_name, m_mod, 
                assetsRoot, textureManager, textures);
    }

    /**
     * Get all textures from this model and from all parrents
     *
     * @return
     */
    protected HashMap<String, String> getTextures() {
        HashMap<String, String> result;

        if (m_parrent == null) {
            result = new HashMap<String, String>();
        } else {
            result = m_parrent.getTextures();
        }

        for (Map.Entry<String, String> entrySet : m_textures.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();

            if (result.containsKey(key)) {
                result.remove(key);
            }

            result.put(key, value);
        }

        return result;
    }
}
