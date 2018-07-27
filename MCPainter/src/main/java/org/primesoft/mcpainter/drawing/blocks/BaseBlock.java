/*
 * The MIT License
 *
 * Copyright 2012 SBPrime.
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
package org.primesoft.mcpainter.drawing.blocks;

import java.util.List;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.Configuration.ConfigProvider;
import org.primesoft.mcpainter.Configuration.OperationType;
import org.primesoft.mcpainter.drawing.Face;
import org.primesoft.mcpainter.drawing.IColorMap;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.drawing.RawImage;
import org.primesoft.mcpainter.utils.Orientation;
import org.primesoft.mcpainter.texture.TextureEntry;
import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.utils.Pair;
import org.primesoft.mcpainter.utils.Utils;
import org.primesoft.mcpainter.utils.Vector;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * The base block (cube) class
 *
 * @author SBPrime
 */
public class BaseBlock implements IDrawableElement {

    public final static String BBNAME = "BASE";
    /**
     * Block drawing slope
     */
    private double[] m_map;
    /**
     * Gray color
     */
    private int[] m_grayColor;
    /**
     * 6 cube faces (texture file and mapping)
     */
    protected Face[] m_faces;
    /**
     * Block size
     */
    protected Vector m_size;
    /**
     * Block shuld rotate using player yaw?
     */
    protected final boolean m_useYaw;
    /**
     * Block shuld rotate using player pitch?
     */
    protected final boolean m_usePitch;

    /**
     * Initialize the base block using YML config section
     *
     * @param textureManager
     * @param bp
     */
    public BaseBlock(TextureManager textureManager, ConfigurationSection bp) {
        m_size = BlockHelper.parseSize(bp.getIntegerList("Size"));

        List<String> textures = bp.getStringList("Textures");
        String texture = bp.getString("Texture");
        RawImage[] tex = null;
        if (textures != null && textures.size() > 0) {
            tex = BlockHelper.parseTextures(textureManager, textures);
        }
        if (texture != null) {
            tex = new RawImage[]{BlockHelper.parseTexture(textureManager, texture)};
        }

        int[] c = BlockHelper.parseIntListEntry(bp.getIntegerList("Color"));
        int[] grayFaces = BlockHelper.parseIntListEntry(bp.getIntegerList("Gray"));
        int[] delta = BlockHelper.parseIntListEntry(bp.getIntegerList("Delta"));
        int[] cropH = BlockHelper.parseIntListEntry(bp.getIntegerList("CropFacesH"));
        int[] cropV = BlockHelper.parseIntListEntry(bp.getIntegerList("CropFacesV"));
        double[] map = BlockHelper.parseDoubleListEntry(bp.getDoubleList("Map"));

        if (map.length == 9) {
            m_map = map;
        } else {
            m_map = null;
        }
        m_usePitch = bp.getBoolean("UsePitch", false);
        m_useYaw = bp.getBoolean("UseYaw", false) || tex.length == 4;

        RawImage[] img = assignTextures(tex);
        m_faces = mapTexture(img);

        if (c.length > 1 && grayFaces.length > 0) {
            m_grayColor = c;

            for (int i : grayFaces) {
                if (i >= 0 && i < m_faces.length) {
                    if (m_faces[i] != null) {
                        m_faces[i].setGray(true);
                    }
                }
            }
        }

        if (delta.length > 0) {
            for (int i = 0; i < Math.min(m_faces.length, delta.length); i++) {
                if (m_faces[i] != null) {
                    m_faces[i].setDelta(delta[i]);
                }
            }
        }
        
        if (cropH.length > 0 || cropV.length > 0) {
            for (int i = 0; i < m_faces.length; i++) {
                int cH1 = (i * 2 + 0) < cropH.length ? cropH[i * 2 + 0] : 0;
                int cH2 = (i * 2 + 1) < cropH.length ? cropH[i * 2 + 1] : 0;
                int cV1 = (i * 2 + 0) < cropV.length ? cropV[i * 2 + 0] : 0;
                int cV2 = (i * 2 + 1) < cropV.length ? cropV[i * 2 + 1] : 0;
                
                if (m_faces[i] != null) {
                    m_faces[i].setCropH(new Pair<Integer, Integer>(cH1, cH2));
                    m_faces[i].setCropV(new Pair<Integer, Integer>(cV1, cV2));
                }
            }
        }
    }

    /**
     * Default constructor for child classes
     *
     * @param useYaw Block shuld rotate using player yaw?
     * @param usePitch Block shuld rotate using player pitch?
     */
    protected BaseBlock(boolean useYaw, boolean usePitch) {
        m_usePitch = usePitch;
        m_useYaw = useYaw;
        m_faces = null;
        m_size = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
    }

    /**
     * Create cube block from single image
     *
     * @param image
     */
    public BaseBlock(RawImage image) {
        m_usePitch = false;
        m_useYaw = false;

        RawImage[] tex = new RawImage[]{image};
        RawImage[] img = assignTextures(tex);

        m_size = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
        m_faces = mapTexture(img);
    }

    /**
     *
     * @param texture Texture package to use when drawing the block
     * @param texIds The texture Id to use
     */
    public BaseBlock(TextureEntry texture, int[] texIds) {
        if (texture == null) {
            m_usePitch = false;
            m_useYaw = false;
            return;
        }

        RawImage[] all = texture.getImages();
        RawImage[] tex = new RawImage[texIds.length];

        for (int i = 0; i < texIds.length; i++) {
            tex[i] = all[texIds[i]];
        }

        m_useYaw = tex.length == 4;
        m_usePitch = false;
        RawImage[] img = assignTextures(tex);

        m_size = new Vector(ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE, ConfigProvider.BLOCK_SIZE);
        m_faces = mapTexture(img);
    }

    /**
     * Arrange the textures to facces
     *
     * @param tex
     * @return
     * @throws UnsupportedOperationException
     */
    protected final RawImage[] assignTextures(RawImage[] tex) throws UnsupportedOperationException {
        RawImage[] img;
        if (tex == null) {
            return null;
        }
        switch (tex.length) {
            case 1:
                img = new RawImage[]{tex[0], tex[0], tex[0], tex[0], tex[0], tex[0]};
                break;
            case 2:
                img = new RawImage[]{tex[0], tex[0], tex[0], tex[0], tex[1], tex[1]};
                break;
            case 3:
                img = new RawImage[]{tex[0], tex[0], tex[0], tex[0], tex[1], tex[2]};
                break;
            case 4:
                img = new RawImage[]{tex[0], tex[1], tex[1], tex[1], tex[2], tex[3]};
                break;
            case 6:
                img = new RawImage[]{tex[0], tex[1], tex[2], tex[3], tex[4], tex[5]};
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented (Count = " + tex.length + ")");
        }
        return img;
    }

    /**
     * Map textures to faces
     *
     * @param img textur
     * @return The faces
     */
    public static Face[] mapTexture(RawImage[] img) {
        int length = img.length;
        Face[] result = new Face[6];

        final int u1 = 0;
        final int[] u2 = new int[img.length];//- 1;
        final int v1 = 0;
        final int[] v2 = new int[img.length];//- 1;
        for (int i = 0; i < img.length; i++) {
            int res = img[i] != null ? img[i].getRes() : 0;
            u2[i] = res - 1;
            v2[i] = res - 1;
        }

        if (length > 0 && img[0] != null) {
            //Back            
            result[0] = new Face(u1, v2[0], u2[0], v1, img[0]);
        }

        if (length > 1 && img[1] != null) {
            //Front
            result[1] = new Face(u2[1], v2[1], u1, v1, img[1]);
        }

        if (length > 2 && img[2] != null) {
            //Left
            result[2] = new Face(u1, v2[2], u2[2], v1, img[2]);
        }

        if (length > 3 && img[3] != null) {
            //Right
            result[3] = new Face(u2[3], v2[3], u1, v1, img[3]);
        }

        if (length > 4 && img[4] != null) {
            //Top
            result[4] = new Face(u2[4], v2[4], u1, v1, img[4]);
        }

        if (length > 5 && img[5] != null) {
            //Bottom
            result[5] = new Face(u2[5], v2[5], u1, v1, img[5]);
        }

        return result;
    }

    /**
     * Map textures to faces
     *
     * @param img textures for faces
     * @param u1
     * @param v1
     * @param u2
     * @param v2
     * @return The faces
     */
    public static Face[] mapTexture(RawImage[] img, int[] u1, int[] v1, int[] u2, int[] v2) {
        int length = img.length;
        Face[] result = new Face[6];

        if (length > 0) {
            //Back            
            result[0] = new Face(u1[0], v2[0], u2[0], v1[0], img[0]);
        }

        if (length > 1) {
            //Front
            result[1] = new Face(u2[1], v2[1], u1[1], v1[1], img[1]);
        }

        if (length > 2) {
            //Left
            result[2] = new Face(u1[2], v2[2], u2[0], v1[2], img[2]);
        }

        if (length > 3) {
            //Right
            result[3] = new Face(u2[3], v2[3], u1[3], v1[3], img[3]);
        }

        if (length > 4) {
            //Top
            result[4] = new Face(u2[4], v2[4], u1[4], v1[4], img[4]);
        }

        if (length > 5) {
            //Bottom
            result[5] = new Face(u2[5], v2[5], u1[5], v1[5], img[5]);
        }

        return result;
    }

    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(m_useYaw ? yaw : 0, m_usePitch ? pitch : 0);

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        if (m_grayColor != null) {
            ImageHelper.drawCube(loger, colorMap, position, orientation, m_size,
                    m_faces, m_map, m_grayColor, true, OperationType.Block);
        } else {
            ImageHelper.drawCube(loger, colorMap, position, orientation, m_size,
                    m_faces, m_map, null, true, OperationType.Block);
        }
    }
}
