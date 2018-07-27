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
package org.primesoft.mcpainter.texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Drawing.RawImage;

/**
 * This class providess access to all available texture packs (vanila and all
 * mods)
 *
 * @author SBPrime
 */
public class TextureManager {

    /**
     * List of registered textures
     */
    private final List<TexturePack> m_texturePacks;
    /**
     * List of all registered texture packs
     */
    private final HashMap<String, TextureProvider> m_registeredTextures;
    /**
     * The vanilla texture provider
     */
    private VanillaTextureProvider m_vanillaTextureProvider;

    public TextureManager() {
        m_registeredTextures = new HashMap<String, TextureProvider>();
        m_texturePacks = new ArrayList<TexturePack>();
    }

    /**
     * Register the texture pack
     *
     * @param texturePack
     */
    public void register(TexturePack texturePack) {
        synchronized (m_texturePacks) {
            m_texturePacks.add(texturePack);
        }
    }

    /**
     * Register new texture provider
     *
     * @param textureProvider
     * @return true if success
     */
    public boolean register(TextureProvider textureProvider) {
        synchronized (m_registeredTextures) {
            String name = textureProvider.getName();
            String alternative = textureProvider.getAlternativeName();
            boolean result = false;

            if (!m_registeredTextures.containsKey(name)) {
                m_registeredTextures.put(name, textureProvider);
                result |= true;
            }
            if (!m_registeredTextures.containsKey(alternative)) {
                m_registeredTextures.put(alternative, textureProvider);
                result |= true;
            }

            if (ConfigProvider.isTexturePackEnabled()) {
                textureProvider.setTextureManager(this);
            }

            return result;
        }
    }

    /**
     * Get the texture provider
     */
    public TextureProvider get(String name) {
        if (name == null) {
            return null;
        }
        synchronized (m_registeredTextures) {
            if (m_registeredTextures.containsKey(name)) {
                return m_registeredTextures.get(name);
            }
        }

        return null;
    }

    /**
     * The vanilla texture provider wrapper
     *
     * @return
     */
    public VanillaTextureProvider getVanilla() {
        return m_vanillaTextureProvider;
    }

    /**
     * Initialize the vanilla texture provider wrapper
     */
    public void initializeVanilla() {
        m_vanillaTextureProvider = new VanillaTextureProvider();
        m_vanillaTextureProvider.initialize(this);
    }

    /**
     * Dispose the texture manager
     */
    public void dispose() {
        synchronized (m_registeredTextures) {
            for (Map.Entry<String, TextureProvider> entry : m_registeredTextures.entrySet()) {
                TextureProvider textureProvider = entry.getValue();
                textureProvider.dispose();
            }
            m_registeredTextures.clear();
        }
        synchronized (m_texturePacks) {
            for (TexturePack texturePack : m_texturePacks) {
                texturePack.close();
            }
            m_texturePacks.clear();
        }
    }

    /**
     * Try to get a texture based on the texture descriptor
     *
     * @param td
     * @return
     */
    public TextureEntry get(TextureDescription td) {
        if (td == null) {
            return null;
        }

        TextureProvider tp = get(td.getTexturePack());
        if (tp == null) {
            return null;
        }

        return tp.getTexture(new String[]{td.getFile()}, td.getFile(),
                new int[]{td.getColumn()},
                new int[]{td.getRow()});
    }

    /**
     * Try to get the texture from one of the available texture packs
     *
     * @param fileName
     * @return
     */
    public RawImage getFileFromTexturePacks(String fileName) {
        synchronized (m_texturePacks) {
            for (TexturePack texturePack : m_texturePacks) {
                RawImage img = texturePack.getFile(fileName);
                if (img != null) {
                    return img;
                }
            }
        }

        return null;
    }
}