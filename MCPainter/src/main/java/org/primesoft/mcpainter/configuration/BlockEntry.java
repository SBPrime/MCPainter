package org.primesoft.mcpainter.configuration;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.primesoft.mcpainter.texture.TextureDescription;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.bukkit.Material;

/**
 * @author SBPrime
 */
public class BlockEntry {

    /**
     * Block of air
     */
    public final static BlockEntry AIR = new BlockEntry(Material.AIR, 0, null,
            EnumSet.of(OperationType.Block, OperationType.Image, OperationType.Statue));
    public final static Color AIR_COLOR = new Color(0, 0, 0, 0);
    
    private final TextureDescription m_textureFile;
    private boolean m_isGrayscale;
    private int[] m_grayscaleColor;    
    private final BaseBlock m_block;
    private final EnumSet<OperationType> m_type;

    public TextureDescription getTextureFile() {
        return m_textureFile;
    }

    public int[] getGrayscaleColor() {
        return m_isGrayscale ? m_grayscaleColor : null;
    }

    public BaseBlock getBlock() {
        return m_block;
    }

    public EnumSet<OperationType> getType() {
        return m_type;
    }

    private BlockEntry(Material m, int data, TextureDescription textureFile, 
            EnumSet<OperationType> type) {
        this(m, data, textureFile, type, null);        
    }

    private BlockEntry(Material m, int data, TextureDescription textureFile, 
            EnumSet<OperationType> type, int[] color) {
        m_isGrayscale = true;
        m_grayscaleColor = color;
        m_textureFile = textureFile;
        m_type = type;

        m_block = new BaseBlock(m, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TEX: ");
        sb.append(m_textureFile.toString());
        sb.append(" Block: ");
        sb.append(m_block);

        return sb.toString();
    }
    
        /**
     * Parse string to material
     *
     * @param s strng
     * @return Material value
     */
    private static Material getMaterial(String s) {
        Material material;
        int matId;
        try {
            matId = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            matId = -1;
        }
        if (matId == -1) {
            material = Material.getMaterial(s);
        } else {
            material = Material.getMaterial(matId);
        }
        return material;
    }    
    
    /**
     * Parse string to int data
     *
     * @param s string
     * @return int data
     */
    private static int getData(String s) {
        if (s != null) {
            try {
                return Integer.parseInt(s);
            }
            catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    /**
     * Parse block description
     *
     * @param s block description
     * @return Block entry
     */
    public static BlockEntry parse(String s) {
        final Pattern pattern = Pattern.compile("^([^:-]+)(\\:([^-]+))?-"
                + "([^-]+)(-([0-9]+),([0-9]+))?(-([bsiBSI]+))?$");

        Matcher m = pattern.matcher(s);
        if (!m.matches()) {
            throw new NullPointerException("Invalid material entry " + s);
        }

        String sMaterial = m.group(1);
        String sData = m.group(3);
        String sTexture = m.group(4);
        String sH = m.group(6);
        String sS = m.group(7);
        String sBlocks = m.group(9);
        TextureDescription tex = TextureDescription.parse(sTexture);

        if (sMaterial == null || tex == null) {
            throw new NullPointerException("Invalid material entry " + s);
        }

        Material material = getMaterial(sMaterial);

        if (material == null) {
            throw new NullPointerException("Invalid material " + sMaterial);
        }

        int data = getData(sData);

        EnumSet<OperationType> type = null;
        int[] gray = null;

        if (sS != null && sH != null) {
            try {
                gray = new int[]{
                    Integer.parseInt(sH),
                    Integer.parseInt(sS)
                };
            }
            catch (NumberFormatException ex) {
                gray = null;
            }
        }

        if (sBlocks != null) {
            sBlocks = sBlocks.toLowerCase();
            ArrayList<OperationType> blocks = new ArrayList<OperationType>();
            if (sBlocks.contains("b")) {
                blocks.add(OperationType.Block);
            }
            if (sBlocks.contains("i")) {
                blocks.add(OperationType.Image);
            }
            if (sBlocks.contains("s")) {
                blocks.add(OperationType.Statue);
            }
            if (!blocks.isEmpty()) {
                type = EnumSet.copyOf(blocks);
            }
        }
        if (type == null) {
            type = EnumSet.allOf(OperationType.class);
        }
        if (gray != null) {
            return new BlockEntry(material, data, tex, type, gray);
        } else {
            return new BlockEntry(material, data, tex, type);
        }
    }
}