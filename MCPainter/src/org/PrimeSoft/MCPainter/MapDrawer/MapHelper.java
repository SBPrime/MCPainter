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
package org.PrimeSoft.MCPainter.MapDrawer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.bukkit.Bukkit;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * This class contains functions that help in managing maps
 * @author SBPrime
 */
public class MapHelper {
    private HashMap<Integer, List<MapRenderer>> m_mapList = new HashMap<Integer, List<MapRenderer>>();

    /**
     * Delete image map
     * @param mapView map view to remove map
     */
    public void deleteMap(MapView mapView) {
        short mapId = mapView.getId();
        List<MapRenderer> renderers = mapView.getRenderers();
        File fileName = new File(ConfigProvider.getImgFolder(), mapId + ".png");
        if (fileName.exists()) {
            fileName.delete();
        }
        for (MapRenderer r : renderers) {
            mapView.removeRenderer(r);
        }

        synchronized (m_mapList) {
            Integer key = new Integer(mapId);
            if (!m_mapList.containsKey(key)) {
                return;
            }
            renderers = m_mapList.get(key);
            for (MapRenderer mapRenderer : renderers) {
                mapView.addRenderer(mapRenderer);
            }

            m_mapList.remove(key);
        }
    }

    /**
     * Store map image on disk
     * @param mapView map to store
     * @param img current map image
     */
    public void storeMap(MapView mapView, BufferedImage img) {
        List<MapRenderer> renderers = mapView.getRenderers();
        short mapId = mapView.getId();

        for (MapRenderer r : renderers) {
            mapView.removeRenderer(r);
        }

        synchronized (m_mapList) {
            Integer key = new Integer(mapId);
            if (!m_mapList.containsKey(key)) {
                m_mapList.put(key, renderers);
            }
        }

        File fileName = new File(ConfigProvider.getImgFolder(), mapId + ".png");
        try {
            ImageIO.write(img, "png", fileName);
        } catch (IOException ex) {
            MCPainterMain.log("Error storing map image.");
        }
    }

    /**
     * Restore drawing map from disk
     */
    public void restoreMaps() {
        File mapDir = ConfigProvider.getImgFolder();
        File[] files = mapDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });


        MCPainterMain.log("Restoring drawing maps...");
        for (File file : files) {
            String name = file.getName().split("\\.")[0];
            short id;
            try {
                id = (short) Integer.parseInt(name);
            } catch (NumberFormatException ex) {
                MCPainterMain.log("* Invalid file " + file.getName());
                continue;
            }

            MapView mapView = Bukkit.getMap(id);
            if (mapView == null) {
                MCPainterMain.log("* Unable to restore map " + id);
                continue;
            }

            BufferedImage img = null;
            try {
                img = ImageIO.read(file);
            } catch (IOException ex) {
                MCPainterMain.log("* Unable to open file " + file.getName());
            }


            List<MapRenderer> renderers = mapView.getRenderers();

            for (MapRenderer r : renderers) {
                mapView.removeRenderer(r);
            }

            synchronized (m_mapList) {
                Integer key = new Integer(id);
                if (!m_mapList.containsKey(key)) {
                    m_mapList.put(key, renderers);
                }
            }
            drawImage(mapView, img);
            MCPainterMain.log("* map " + id + " restored.");
        }
    }

    /**
     * Draw image on map - set map renderer
     * @param mapView map vied
     * @param img map image
     */
    public void drawImage(MapView mapView, BufferedImage img) {
        mapView.addRenderer(new ImgRenderer(img));
    }
}