package org.PrimeSoft.MCPainter.Drawing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.PrimeSoft.MCPainter.Configuration.BlockEntry;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.PluginMain;
import org.PrimeSoft.MCPainter.Texture.TextureDescription;
import org.PrimeSoft.MCPainter.Texture.TextureEntry;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.palettes.Palette;

/**
 * @author SBPrime
 */
public class ColorMap {

    /**
     * Threshold gives the value when pixsl should by drawn as AIR/ignored
     */
    //public static final int ALPHA_THRESHOLD = 64;
    private Boolean m_isInitialized = false;
    private BlockEntry[] m_blocks = new BlockEntry[0];

    public Boolean isInitialized() {
        return m_isInitialized;
    }

    public ColorMap(TextureManager textureManager, Palette palette) {
        if (textureManager == null) {
            return;
        }

        BlockEntry[] blocks = palette != null ? palette.getBlocks() : null;
        if (blocks == null || blocks.length < 2) {
            PluginMain.log("Not enough blocks deffined in pallete.");
            return;
        }


        for (BlockEntry blockEntry : blocks) {
            try {
                TextureDescription tex = blockEntry.getTextureFile();
                int[] grayscaleColor = blockEntry.getGrayscaleColor();

                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                int sumA = 0;
                int cnt = 0;                  
                TextureEntry img = textureManager.get(tex);
                if (img == null) {
                    PluginMain.log("Error processing block node " + blockEntry.toString());
                    continue;
                }

                RawImage rawImage = img.getImages()[0];
                boolean hasAlpha = rawImage.hasAlpha();
                int textureRes = rawImage.getRes();
                int[][] image = rawImage.getImage();
                for (int i = 0; i < textureRes; i++) {
                    for (int j = 0; j < textureRes; j++) {
                        int rgb = image[i][j];
                        Color c = new Color(rgb, hasAlpha);
                        cnt++;
                        if (grayscaleColor != null) {
                            c = ImageHelper.getColor(c, grayscaleColor);
                        }
                        int r = c.getRed();
                        int g = c.getGreen();
                        int b = c.getBlue();
                        int a = c.getAlpha();

                        sumR += r;
                        sumG += g;
                        sumB += b;
                        sumA += a;
                    }
                }

                blockEntry.setColor(new Color(sumR / cnt, sumG / cnt, sumB / cnt, sumA / cnt));
            } catch (Exception ex) {
                PluginMain.log("Error processing block node " + blockEntry.toString());
            }
        }

        m_isInitialized = true;
        m_blocks = blocks;
    }

    /**
     * Get block for color
     *
     * @param c color
     * @param type block operation type
     * @return block entry
     */
    public BlockEntry getBlockForColor(Color c, OperationType type) {
        /*if (c.getAlpha() < ALPHA_THRESHOLD) {
            return BlockEntry.AIR;
        }*/
        if (!m_isInitialized) {
            return BlockEntry.AIR;
        }

        BlockEntry closest = BlockEntry.AIR;
        double closestDistance = ImageHelper.colorDistance(c, BlockEntry.AIR_COLOR);

        for (BlockEntry blockEntry : m_blocks) {
            if (blockEntry.getType().contains(type)) {
                double dist = ImageHelper.colorDistance(c, blockEntry.getColor());

                if (dist < closestDistance) {
                    closest = blockEntry;
                    closestDistance = dist;
                }
            }
        }

        return closest;
    }

    /**
     * Get color pallete for given operation
     *
     * @param type operation type
     * @return Pallete
     */
    public Color[] getPalette(OperationType type) {
        List<Color> result = new ArrayList<Color>();
        for (BlockEntry blockEntry : m_blocks) {
            if (blockEntry.getType().contains(type)) {
                Color c = blockEntry.getColor();
                if (c != null) {
                    result.add(c);
                }
            }
        }

        return result.toArray(new Color[0]);
    }
}