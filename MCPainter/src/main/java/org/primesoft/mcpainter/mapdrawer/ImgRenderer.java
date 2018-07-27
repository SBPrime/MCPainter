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
package org.primesoft.mcpainter.mapdrawer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * This class is used to draw images on maps
 * @author SBPrime
 */
public class ImgRenderer extends MapRenderer {
    /*
     * Maximum image size
     */
    private final int MAX_SIZE = 128;
    
    /*
     * Is the map already rendered
     */
    private boolean m_isRendered;
    
    
    /*
     * Map image
     */
    private final byte[] m_img;

    /**
     * Initialize new instance of the class
     * @param img The image to draw on the map
     */
    public ImgRenderer(BufferedImage img) {
        super(false);

        int hh = Math.min(img.getHeight(), MAX_SIZE);
        int ww = Math.min(img.getWidth(), MAX_SIZE);

        BufferedImage lImg;

        if (hh != MAX_SIZE || ww != MAX_SIZE) {
            lImg = new BufferedImage(MAX_SIZE, MAX_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = lImg.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
        } else {
            lImg = img;
        }

        m_isRendered = false;
        m_img = MapPalette.imageToBytes(lImg);
    }

    /**
     * Render image on the map
     * @param mv Map item map view
     * @param mc Map item map canvas
     * @param player Player that the redraw is performed for
     */
    @Override
    public void render(final MapView mv, final MapCanvas mc, Player player) {
        if (m_isRendered) {
            return;
        }

        m_isRendered = true;
        int idx = 0;
        for (int y = 0; y < MAX_SIZE; y++) {
            for (int x = 0; x < MAX_SIZE; x++) {
                mc.setPixel(x, y, m_img[idx]);
                idx++;
            }
        }
    }
}
