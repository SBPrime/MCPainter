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
package org.primesoft.mcpainter.mods.assets;

import org.primesoft.mcpainter.texture.TextureManager;
import org.primesoft.mcpainter.utils.JSONExtensions;
import org.json.simple.JSONObject;

/**
 *
 * @author SBPrime
 */
public class VariantEntry {
    private final static String PROP_MODEL = "model";
    private final static String PROP_RX = "x";
    private final static String PROP_RY = "y";
    private final static String PROP_UVLOCK = "uvlock:";
    

    /**
     * X axis rotation
     */
    private final double m_rotX;

    /**
     * Y axis rotation
     */
    private final double m_rotY;

    /**
     * Do not rotate the texture
     */
    private final boolean m_uvLock;

    /**
     * The model
     */
    private AssetsModel m_model;

    /**
     * Get the model
     *
     * @return
     */
    public AssetsModel getModel() {
        return m_model;
    }

    /**
     * Update the variant model
     *
     * @param model
     */
    public void setModel(AssetsModel model) {
        m_model = model;
    }

    /**
     * Get the X axis rotation
     * @return 
     */
    public double getRotateX() {
        return m_rotX;
    }
    
    /**
     * Get the Y axis rotation
     * @return 
     */
    public double getRotateY() {
        return m_rotY;
    }
    
    /**
     * Is the texture UV locked
     * @return 
     */
    public boolean isUVLock() {
        return m_uvLock;
    }

    public VariantEntry(JSONObject data) {
        String model = JSONExtensions.tryGetString(data, PROP_MODEL, null);
        
        m_rotX = JSONExtensions.tryGetDouble(data, PROP_RX, 0);
        m_rotY = JSONExtensions.tryGetDouble(data, PROP_RY, 0);
        m_uvLock = JSONExtensions.tryGetBool(data, PROP_UVLOCK, false);
        
        m_model = new LinkAssetModel("fake/" + model);
        
//        JSONExtensions.printUnused(data,
//                new String[]{PROP_MODEL, PROP_RX, PROP_RY, PROP_UVLOCK},
//                "Unknown assets variant entry property: ");
    }

    
    /**
     * Render the variant
     * @param textureManager 
     * @param assetsRoot 
     * @return  
     */
    public VariantBlock compile(TextureManager textureManager, String assetsRoot) {
        final CompiledModel model = m_model.compile(textureManager, assetsRoot);
        
        if (model == null) {
            return null;
        }
        
        return new VariantBlock(model, m_uvLock, m_rotX, m_rotY);
    }
}
