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
package org.PrimeSoft.MCPainter.Texture;

import java.io.File;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.PluginMain;

/**
 *
 * @author SBPrime
 */
public class TextureProvider {

    private TexturePack m_specified;
    private String m_name;
    private String m_nameAlternative;
    private TextureManager m_textureManager;

    /**
     * Constructor for sub classes
     *
     * @param name Texture pack name
     */
    public TextureProvider(String name, String alternativeName) {
        m_name = name;
        m_nameAlternative = alternativeName;
    }

    /**
     * Initialize texture provider
     *
     * @return true if all ok
     */
    public boolean initialize(String textureFile, int textureRes) {
        m_specified = new TexturePack();
        if (textureFile != null) {
            m_specified = new TexturePack();
            if (!m_specified.load(textureFile, textureRes)) {
                m_specified = null;
                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Initialize texture provider
     *
     * @return true if all ok
     */
    public boolean initialize(File f, int textureRes) {
        m_specified = new TexturePack();
        if (f != null) {
            m_specified = new TexturePack();
            if (!m_specified.load(f, textureRes)) {
                m_specified = null;
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Get textures from texture pack. First try specific then default
     *
     * @param files Texture files to load
     * @return Array of textures
     */
    protected TextureEntry getTexture(String[] files, String name, int[] columns, int[] rows) {
        //PluginMain.log("Loading file: " + name);
        if (m_specified == null) {
            return null;
        }

        RawImage[] images = new RawImage[files.length];

        for (int i = 0; i < files.length; i++) {
            RawImage img = null;
            if (m_textureManager != null) {
                //Try to get the texture from one of the available texture packs
                img = m_textureManager.getFileFromTexturePacks(files[i]);
            }
            if (img == null) {
                img = m_specified.getFile(files[i]);
            }
            if (img == null) {
                return null;
            }


            int column = columns != null && i < columns.length ? columns[i] : -1;
            int row = rows != null && i < rows.length ? rows[i] : -1;

            if (column >= 0 || row >= 0) {
                img = img.subImage(column, row);
            }
            images[i] = img;
        }

        return new TextureEntry(images, name);
    }

    /**
     * Finilize the texture pack provider
     */
    public void dispose() {
        if (m_specified != null) {
            m_specified.close();
            m_specified = null;
        }
    }

    /**
     * The texture pack name
     *
     * @return
     */
    public String getName() {
        return m_name;
    }

    /**
     * The texture pack alternative name
     *
     * @return
     */
    public String getAlternativeName() {
        return m_nameAlternative;
    }

    /**
     * The texture manager
     *
     * @param textureManager
     */
    public void setTextureManager(TextureManager textureManager) {
        m_textureManager = textureManager;
    }
}