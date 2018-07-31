package org.primesoft.mcpainter.configuration;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.primesoft.mcpainter.texture.TextureDescription;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;

/**
 * @author SBPrime
 */
public class BlockEntry {

    private static final Pattern PATTERN_ENTRY = Pattern.compile("^([^\\[;]+)"+ //Material
            "(\\[([^\\]]+)\\])?;" + //Data
            "([^;]+)"+ //Texture
            "(;([0-9]+),([0-9]+))?" + //HSI
            "(;([bsiBSI]+))?$"); //Block usage

    /**
     * Block of air
     */
    public final static BlockEntry AIR = new BlockEntry();

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

    private BlockEntry() {
        this(Bukkit.getServer().createBlockData(Material.AIR), null,
                EnumSet.of(OperationType.Block, OperationType.Image, OperationType.Statue), null);
    }

    private BlockEntry(BlockData data, TextureDescription textureFile,
            EnumSet<OperationType> type) {
        this(data, textureFile, type, null);
    }

    private BlockEntry(BlockData data, TextureDescription textureFile,
            EnumSet<OperationType> type, int[] color) {
        m_isGrayscale = true;
        m_grayscaleColor = color;
        m_textureFile = textureFile;
        m_type = type;

        m_block = new BaseBlock(data);
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
     * @param name the material name
     * @return Material value
     */
    private static Material getMaterial(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return Material.getMaterial(name.toUpperCase());
    }

    /**
     * Parse block description
     *
     * @param s block description
     * @return Block entry
     */
    public static BlockEntry parse(String s) {
        Matcher m = PATTERN_ENTRY.matcher(s);
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

        Server server = Bukkit.getServer();
        BlockData blockData;
        try {
            if (sData != null && !sData.isEmpty()) {
                blockData = server.createBlockData(sMaterial + "[" + sData + "]");
            } else {
                Material material = getMaterial(sMaterial);
                blockData = material != null ? server.createBlockData(material) : server.createBlockData(sMaterial);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format("Invalid block data %1$s[%2$s]", sMaterial, sData), ex);
        }

        EnumSet<OperationType> type = null;
        int[] gray = null;
        if (sS != null && sH != null) {
            try {
                gray = new int[]{
                    Integer.parseInt(sH),
                    Integer.parseInt(sS)
                };
            } catch (NumberFormatException ex) {
                gray = null;
            }
        }

        if (sBlocks != null) {
            sBlocks = sBlocks.toLowerCase();
            ArrayList<OperationType> blocks = new ArrayList<>();
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
            return new BlockEntry(blockData, tex, type, gray);
        } else {
            return new BlockEntry(blockData, tex, type);
        }
    }
}
