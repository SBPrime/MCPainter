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
package org.PrimeSoft.MCPainter.Drawing.Blocks;

import java.util.List;
import org.PrimeSoft.MCPainter.blocksplacer.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Configuration.OperationType;
import org.PrimeSoft.MCPainter.Drawing.*;
import org.PrimeSoft.MCPainter.utils.Orientation;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Basic flat block
 * @author SBPrime
 */
public class Flat extends BaseBlock {
    public final static String NAME = "FLAT";
    private final int[] m_grayColor;
    
    private int m_dx;
    private int m_dy;
    private int m_dz;

    public Flat(TextureManager textureManager, ConfigurationSection bp)
    {
        super(true, false);
        
        int[] c = BlockHelper.parseIntListEntry(bp.getIntegerList("Color"));
        if (c.length > 1)
        {
            m_grayColor = c;
        } else {
            m_grayColor = null;
        }
        RawImage img = BlockHelper.parseTexture(textureManager, bp.getString("Texture"));
        List<Integer> size = bp.getIntegerList("Size");
        int width = 16;
        int height = 16;
        int depth = 1;
        if (size != null) {
            Integer[] aSize = size.toArray(new Integer[0]);
            if (aSize.length > 0){
                width = aSize[0];
            }
            if (aSize.length > 1){
                height = aSize[1];
            }
            if (aSize.length > 2){
                depth = aSize[2];
            }
        }
        
        CubeFace face = CubeFace.valueOf(bp.getString("Face", "Bottom"));
        initialize(img, width, height, depth, face);
    }

    /**
     * Initialize class properties
     * @param img
     * @param width
     * @param height
     * @param depth
     * @param face 
     */
    private void initialize(RawImage img, int width, int height, int depth, CubeFace face) {
        m_faces = new Face[6];
                
        int res = img.getRes();
        
        int dx = (res - width) / 2;
        int dy = (res - height) / 2;
        int u1 = dx;
        int v1 = dy;
        int u2 = u1 + res - 1 - dx * 2;
        int v2 = v1 + res - 1 - dy * 2;

        Face quad = new Face(u2, v2, u1, v1, img);
        quad.setDepth(depth);
        
        if (m_grayColor != null)
        {
            quad.setGray(true);
        }

        m_size = initializeFaces(m_faces, face, quad, width, height);
        
        switch (face) {
            case Front:
            case Back:
                m_dx = dx;
                m_dy = dy;
                m_dz = 0;
                break;
            case Left:
            case Right:
                m_dz = dx;
                m_dy = dy;
                m_dx = 0;
                break;
            case Top:                
            case Bottom:
                m_dx = dx;
                m_dz = dy;
                m_dy = 0;
                break;
            default:
                m_dx = 0;
                m_dy = 0;
                m_dz = 0;
        }
    }
    
    protected static Vector initializeFaces(Face[] faces, CubeFace face, Face quad,
            int width, int height) {
        int wx = ConfigProvider.BLOCK_SIZE;
        int wy = ConfigProvider.BLOCK_SIZE;
        int wz = ConfigProvider.BLOCK_SIZE;

        switch (face) {
            case Back:
                faces[0] = quad;
                wx = width;
                wy = height;
                break;
            case Front:
                faces[1] = quad;
                wx = width;
                wy = height;
                break;
            case Left:
                faces[2] = quad;
                wz = width;
                wy = height;
                break;
            case Right:
                faces[3] = quad;
                wz = width;
                wy = height;
                break;
            case Top:
                faces[4] = quad;
                wx = width;
                wz = height;
                break;
            case Bottom:
                faces[5] = quad;
                wx = width;
                wz = height;
                break;
        }

        return new Vector(wx, wy, wz);
    }
    
    @Override
    public void draw(short data, BlockLoger loger, ILocalPlayer localPlayer, IColorMap colorMap) {
        double yaw = localPlayer.getYaw();
        double pitch = localPlayer.getPitch();
        Orientation orientation = new Orientation(m_useYaw ? yaw : 0, m_usePitch ? pitch : 0);                

        Vector position = orientation.moveStart(Utils.getPlayerPos(localPlayer), yaw, pitch);

        int dx = orientation.calcX(m_dx, m_dy, m_dz);
        int dy = orientation.calcY(m_dx, m_dy, m_dz);
        int dz = orientation.calcZ(m_dx, m_dy, m_dz);
        ImageHelper.drawCube(loger, colorMap, position.add(dx, dy, dz), orientation,
                m_size, m_faces, m_grayColor, true, OperationType.Block);
    }    
}
