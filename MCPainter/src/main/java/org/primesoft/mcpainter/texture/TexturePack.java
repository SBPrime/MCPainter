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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.RawImage;

/**
 *
 * @author SBPrime
 */
public class TexturePack {

    private final HashMap<String, RawImage> m_imgCatch = new HashMap<String, RawImage>();
    private int m_textureRes = -1;
    private ZipFile m_zipFile = null;
    private File m_dir = null;

    /**
     * Get the texture pack resolution
     *
     * @return texture res
     */
    public int getTextureRes() {
        return m_textureRes;
    }

    /**
     * Load texture
     *
     * @param fileName texture file or directory
     * @param textureRes The texture ressolution
     * @return true if load ok
     */
    public boolean load(String fileName, int textureRes) {
        return load(new File(ConfigProvider.getPluginFolder(), fileName), textureRes);
    }

    /**
     * Load texture
     *
     * @param f texture file or directory
     * @return true if load ok
     */
    public boolean load(File f, int textureRes) {
        m_textureRes = textureRes;
        if (!f.exists()) {
            return false;
        }

        if (f.isDirectory()) {
            m_dir = f;
            return true;
        } else {
            try {
                m_zipFile = new ZipFile(f);
                return true;
            } catch (ZipException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
    }

    /**
     * Dispose the texture pack
     */
    public void close() {
        if (m_zipFile != null) {
            try {
                m_zipFile.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Get image from file
     *
     * @param filePath image path
     * @return The image
     */
    public RawImage getFile(String filePath) {
        int res = getTextureRes();
        synchronized (m_imgCatch) {
            if (m_imgCatch.containsKey(filePath)) {
                return m_imgCatch.get(filePath);
            }

            if (m_dir != null) {
                String[] parts = filePath.split("/");
                File file = m_dir;
                for (String dir : parts) {
                    file = new File(file, dir);
                }

                if (!file.exists() || !file.isFile() || !file.canRead()) {
                    return null;
                }

                try {
                    RawImage result = new RawImage((BufferedImage) ImageIO.read(file), res);
                    m_imgCatch.put(filePath, result);
                    return result;
                } catch (IOException e) {
                    return null;
                }
            }

            if (m_zipFile != null) {
                ZipEntry entry = m_zipFile.getEntry(filePath);
                if (entry == null) {
                    return null;
                }
                try {
                    InputStream is = m_zipFile.getInputStream(entry);
                    RawImage result = new RawImage((BufferedImage) ImageIO.read(is), res);
                    return result;
                } catch (IOException ex) {
                    return null;
                }
            }

            return null;
        }
    }
}