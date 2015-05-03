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
import java.util.Iterator;
import java.util.List;
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
     * The model name
     * @return 
     */
    public String getName() {
        return m_name;
    }
    
    
    /**
     * The parrent asset
     * @return 
     */
    public AssetsModel getParrent() {
        return m_parrent;
    }
    
    
    /**
     * Set the parrent
     * @param parrent 
     */
    public void setParrent(AssetsModel parrent) {
        m_parrent = parrent;
    }

    protected AssetsModel(String name) {
        m_elements = null;
        m_parrent = null;
        m_textures = null;
        m_name = name;
    }

    public AssetsModel(String name, JSONObject data) {
        String parrent = JSONExtensions.tryGetString(data, PROP_PARENT, null);
        JSONObject textures = JSONExtensions.tryGet(data, PROP_TEXTURES);
        JSONArray elements = JSONExtensions.tryGetArray(data, PROP_ELEMENTS);

        m_name = name;
        
        if (parrent == null || parrent.isEmpty()) {
            m_parrent = null;
        } else {
            m_parrent = new LinkAssetModel(parrent);
        }

        m_elements = new ArrayList<AssetsCube>();
        m_textures = new HashMap<String, String>();

        if (textures != null) {
            for (Object key : textures.keySet()) {
                m_textures.put((String) key, (String) textures.get(key));
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
}
