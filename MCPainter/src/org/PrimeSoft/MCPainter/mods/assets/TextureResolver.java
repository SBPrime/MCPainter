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
import java.util.List;
import java.util.Map;
import static org.PrimeSoft.MCPainter.MCPainterMain.log;
import org.PrimeSoft.MCPainter.Texture.TextureDescription;
import org.PrimeSoft.MCPainter.Texture.TextureEntry;
import org.PrimeSoft.MCPainter.Texture.TextureManager;

/**
 *
 * @author SBPrime
 */
public class TextureResolver {
    public static HashMap<String, TextureEntry> resolveTextures(String name, String mod, String assetsRoot,
            TextureManager textureManager, HashMap<String, String> textures) {
        final String textureFileFormat = "%stextures/%s.png";
        
        HashMap<String, TextureEntry> texturesImages = new HashMap<String, TextureEntry>();

        validateTextureEntries(textures, name);
        HashMap<String, String> resolved = resolveTextureLinks(textures);
        HashMap<String, List<String>> reverseTextureMap = reverseHash(resolved);

        for (Map.Entry<String, List<String>> entrySet : reverseTextureMap.entrySet()) {
            String textureFile = entrySet.getKey();
            List<String> links = entrySet.getValue();

            TextureEntry te = textureManager.get(new TextureDescription(mod, String.format(textureFileFormat, assetsRoot, textureFile)));
            if (te == null) {
                log(String.format("    %s: Texture %s: %s not found.", name, mod, textureFile));
            } else {
                for (String l : links) {
                    texturesImages.put(l, te);
                }
            }
        }
        
        return texturesImages;
    }
    
    /**
     * Try to resolve the texture links
     *
     * @param textures
     * @return
     */
    private static HashMap<String, String> resolveTextureLinks(HashMap<String, String> textures) {
        final HashMap<String, String> resolved = new HashMap<String, String>();
        final HashMap<String, String> links = new HashMap<String, String>();

        for (Map.Entry<String, String> entrySet : textures.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();

            if (value.startsWith("#")) {
                links.put(key, value);
            } else {
                resolved.put(key, value);
            }
        }

        while (!links.isEmpty()) {
            HashSet<String> toRemove = new HashSet<String>();
            for (Map.Entry<String, String> entrySet : links.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue().substring(1);

                if (resolved.containsKey(value)) {
                    resolved.put(key, resolved.get(value));
                    toRemove.add(key);
                }
            }

            for (String s : toRemove) {
                links.remove(s);
            }
        }

        return resolved;
    }

    /**
     * Validate all texture entries
     *
     * @param textures
     * @param name
     */
    private static void validateTextureEntries(HashMap<String, String> textures, String name) {
        List<String> toRemove = new ArrayList<String>();

        for (Map.Entry<String, String> entrySet : textures.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            HashSet<String> scanned = new HashSet<String>();

            while (value.startsWith("#")) {
                if (scanned.contains(key)) {
                    if (!toRemove.contains(key)) {
                        log(String.format("    %s: Found cyclic texture link %s = %s.", name, key, value));
                        toRemove.add(key);
                    }
                    break;
                } else {
                    scanned.add(key);
                }
                if (textures.containsKey(value.substring(1))) {
                    key = value.substring(1);
                    value = textures.get(key);
                } else {
                    key = "";
                    value = "";
                }
            }
        }

        for (String entry : toRemove) {
            textures.remove(entry);
        }

        do {
            toRemove.clear();
            for (Map.Entry<String, String> entrySet : textures.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();

                if (value.startsWith("#") && !textures.containsKey(value.substring(1))) {
                    log(String.format("    %s: Unresolved texture link %s = %s.", name, key, value));
                    toRemove.add(key);
                }
            }

            for (String entry : toRemove) {
                textures.remove(entry);
            }
        } while (!toRemove.isEmpty());
    }
    
    
    /**
     * Reverse provided hash map
     *
     * @param hash
     * @return
     */
    private static HashMap<String, List<String>> reverseHash(HashMap<String, String> hash) {
        final HashMap<String, List<String>> result = new HashMap<String, List<String>>();

        for (Map.Entry<String, String> entrySet : hash.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();

            final List<String> values;
            if (result.containsKey(value)) {
                values = result.get(value);
            } else {
                values = new ArrayList<String>();
                result.put(value, values);
            }

            values.add(key);
        }

        return result;
    }
}