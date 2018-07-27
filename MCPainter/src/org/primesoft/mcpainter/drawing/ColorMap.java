package org.PrimeSoft.MCPainter.Drawing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.PrimeSoft.MCPainter.Configuration.BlockEntry;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.Filters.ColorPalette;
import org.PrimeSoft.MCPainter.Drawing.Filters.IColorPalette;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.PrimeSoft.MCPainter.Texture.TextureDescription;
import org.PrimeSoft.MCPainter.Texture.TextureEntry;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.palettes.Palette;

/**
 * @author SBPrime
 */
public class ColorMap implements IColorMap {

    /**
     * Threshold gives the value when pixsl should by drawn as AIR/ignored
     */
    //public static final int ALPHA_THRESHOLD = 64;
    private final Boolean m_isInitialized;
    private final DrawingBlock[] m_blocks;

    @Override
    public Boolean isInitialized() {
        return m_isInitialized;
    }

    public ColorMap(TextureManager textureManager, Palette palette) {
        if (textureManager == null) {
            m_blocks = new DrawingBlock[0];
            m_isInitialized = false;
            return;
        }

        BlockEntry[] blocks = palette != null ? palette.getBlocks() : null;
        if (blocks == null || blocks.length < 2) {
            MCPainterMain.log("Not enough blocks deffined in pallete.");

            m_blocks = new DrawingBlock[0];
            m_isInitialized = false;
            return;
        }

        List<DrawingBlock> drawingBlocks = new ArrayList<DrawingBlock>();
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
                    MCPainterMain.log("Error processing block node " + blockEntry.toString());
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

                drawingBlocks.add(new DrawingBlock(blockEntry, new Color(sumR / cnt, sumG / cnt, sumB / cnt, sumA / cnt)));
            } catch (Exception ex) {
                MCPainterMain.log("Error processing block node " + blockEntry.toString());
            }
        }

        if (drawingBlocks.size() < 2) {
            MCPainterMain.log("Not enough valid blocks deffined in pallete.");

            m_blocks = new DrawingBlock[0];
            m_isInitialized = false;
            return;
        }

        m_blocks = drawingBlocks.toArray(new DrawingBlock[0]);
        m_isInitialized = true;
    }

    /**
     * Get block for color
     *
     * @param c color
     * @param type block operation type
     * @return block entry
     */
    @Override
    public IDrawingBlock getBlockForColor(Color c, OperationType type) {
        /*if (c.getAlpha() < ALPHA_THRESHOLD) {
         return BlockEntry.AIR;
         }*/
        if (!m_isInitialized) {
            return DrawingBlock.AIR;
        }

        DrawingBlock closest = DrawingBlock.AIR;
        double closestDistance = ImageHelper.colorDistance(c, BlockEntry.AIR_COLOR);

        for (DrawingBlock blockEntry : m_blocks) {
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
    @Override
    public IColorPalette getPalette(OperationType type) {
        List<Color> result = new ArrayList<Color>();
        for (DrawingBlock blockEntry : m_blocks) {
            if (blockEntry.getType().contains(type)) {
                Color c = blockEntry.getColor();
                if (c != null) {
                    result.add(c);
                }
            }
        }

        return new ColorPalette(result.toArray(new Color[0]));
    }
}
