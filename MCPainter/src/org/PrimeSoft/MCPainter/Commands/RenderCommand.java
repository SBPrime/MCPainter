/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
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
package org.PrimeSoft.MCPainter.Commands;

import java.io.File;
import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Help;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.PrimeSoft.MCPainter.utils.Vector;
import org.PrimeSoft.MCPainter.voxelyzer.ClippingRegion;
import org.PrimeSoft.MCPainter.voxelyzer.Matrix;
import org.PrimeSoft.MCPainter.voxelyzer.Model;
import org.PrimeSoft.MCPainter.voxelyzer.Vertex;
import org.PrimeSoft.MCPainter.voxelyzer.fileParsers.WavefrontObj;
import org.PrimeSoft.MCPainter.worldEdit.IEditSession;
import org.PrimeSoft.MCPainter.worldEdit.ILocalPlayer;
import org.PrimeSoft.MCPainter.worldEdit.ILocalSession;
import org.PrimeSoft.MCPainter.worldEdit.IWorldEdit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author SBPrime
 */
public class RenderCommand implements Runnable {

    private final static int YAW = 30;
    private final static int YAW2 = 14;
    private final static int PITCH = 45;
    private final static int PITCH2 = 22;

    public static void Execute(MCPainterMain sender, Player player, IWorldEdit worldEdit,
            ColorMap colorMap, String[] args) {
        if (args.length < 3 || args.length > 5) {
            Help.ShowHelp(player, Commands.COMMAND_RENDER);
            return;
        }

        String modelName = args[1];
        String sizeArg = args[2];
        String clipArg = args.length > 3 ? args[3] : "0/0,0/0,0/0";
        String offsetArg = args.length > 4 ? args[4] : "0,0,0";

        File f = new File(ConfigProvider.getModelFolder(), modelName);
        if (!f.exists() || !f.canRead()) {
            MCPainterMain.say(player, ChatColor.RED + "Model " + ChatColor.WHITE + modelName
                    + ChatColor.RED + " not found.");
            return;
        }

        Vector size = parseSize(sizeArg);
        Vector[] clip = parseClip(clipArg);
        Vector offset = parseOffset(offsetArg);
        if (size == null || clip == null || offset == null) {
            Help.ShowHelp(player, Commands.COMMAND_RENDER);
            return;
        }

        sender.getServer().getScheduler().runTaskAsynchronously(sender,
                new RenderCommand(sender, player, worldEdit, colorMap,
                        modelName, size, clip[0], clip[1], offset));
    }

    private static Vector parseSize(String sizeArg) {
        if (sizeArg == null) {
            return null;
        }

        String[] parts = sizeArg.split(",");
        if (parts.length != 3) {
            return null;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(parts[0]);
        } catch (NumberFormatException ex) {
            x = Double.NaN;
        }
        try {
            y = Double.parseDouble(parts[1]);
        } catch (NumberFormatException ex) {
            y = Double.NaN;
        }
        try {
            z = Double.parseDouble(parts[2]);
        } catch (NumberFormatException ex) {
            z = Double.NaN;
        }

        if (x == Double.NaN && y == Double.NaN && z == Double.NaN) {
            return null;
        }

        return new Vector(x, y, z);
    }

    private static Vector[] parseClip(String clipArg) {
        if (clipArg == null) {
            return null;
        }

        String[] parts = clipArg.split(",");
        if (parts.length != 3) {
            return null;
        }
        String[] xc = parts[0].split("/");
        String[] yc = parts[1].split("/");
        String[] zc = parts[2].split("/");

        if (xc.length != 2 || yc.length != 2 || zc.length != 2) {
            return null;
        }
        try {
            return new Vector[] {
              new Vector(Double.parseDouble(xc[0]), Double.parseDouble(yc[0]), Double.parseDouble(zc[0])),
              new Vector(Double.parseDouble(xc[1]), Double.parseDouble(yc[1]), Double.parseDouble(zc[1]))
            };
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Vector parseOffset(String offsetArg) {
        if (offsetArg == null) {
            return null;
        }

        String[] parts = offsetArg.split(",");
        if (parts.length != 3) {
            return null;
        }

        try {
            return new Vector(Double.parseDouble(parts[0]), 
                    Double.parseDouble(parts[1]), 
                    Double.parseDouble(parts[2]));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private final Player m_player;
    private final double m_yaw;
    private final double m_pitch;
    private final Vector m_pPosition;
    private final IEditSession m_session;
    private final ILocalSession m_lSession;
    private final ColorMap m_colorMap;
    private final MCPainterMain m_sender;
    private final String m_modeFile;
    private final Vector m_size;
    private final Vector m_clipMin;
    private final Vector m_clipMax;
    private final Vector m_offset;

    private RenderCommand(MCPainterMain sender, Player player, IWorldEdit worldEdit,
            ColorMap colorMap, String modeFile, Vector size, Vector clipMin,
            Vector clipMax, Vector offset) {
        m_modeFile = modeFile;
        m_size = size;
        m_clipMax = clipMax;
        m_clipMin = clipMin;
        m_offset = offset;
        m_player = player;

        m_lSession = worldEdit.getSession(m_player);
        ILocalPlayer localPlayer = worldEdit.wrapPlayer(m_player);
        m_session = m_lSession.createEditSession(localPlayer);

        final int y = (int) (360 + localPlayer.getYaw() + YAW2) % 360;
        final int p = (int) (360 + localPlayer.getPitch() + PITCH2) % 360;

        m_yaw = y - y % YAW;
        m_pitch = -(p - p % PITCH);
        m_pPosition = Utils.getPlayerPos(localPlayer);
        m_colorMap = colorMap;
        m_sender = sender;
    }

    @Override
    public void run() {
        BlockLoger loger = new BlockLoger(m_player, m_lSession, m_session, m_sender);

        MCPainterMain.say(m_player, ChatColor.WHITE + "Loading model "
                + ChatColor.YELLOW + m_modeFile + ChatColor.WHITE + "...");
        final Model model = WavefrontObj.load(ConfigProvider.getModelFolder(), m_modeFile);
        if (model == null) {
            MCPainterMain.say(m_player, ChatColor.RED + "Error loading model " + ChatColor.YELLOW + m_modeFile);
            return;
        }

        final Vertex minPos = model.getMin();
        final Vertex maxPos = model.getMax();
        final Vector size = getSafeSize(model.getSize());
        final Vector scale = calcScale(size);
        final Vector oneBlock = new Vector(size.getX() / scale.getX(), size.getY() / scale.getY(), size.getZ() / scale.getZ());

        final Matrix matrix = Matrix.getIdentity();

        matrix.translate(m_pPosition.getBlockX() + m_offset.getBlockX(),
                m_pPosition.getBlockY() + m_offset.getBlockY(),
                m_pPosition.getBlockZ() + m_offset.getBlockZ());
        matrix.rotateY(m_yaw * Math.PI / 180);
        matrix.rotateX(m_pitch * Math.PI / 180);
        matrix.scale(scale.getX(), scale.getY(), scale.getZ());
        matrix.translate(-minPos.getX(), -minPos.getY(), -minPos.getZ());

        ClippingRegion clipping = new ClippingRegion(
                minPos.getX() + oneBlock.getX() * m_clipMin.getX(), maxPos.getX() - oneBlock.getX() * m_clipMax.getX(),
                minPos.getY() + oneBlock.getY() * m_clipMin.getY(), maxPos.getY() - oneBlock.getY() * m_clipMax.getY(),
                minPos.getZ() + oneBlock.getZ() * m_clipMin.getZ(), maxPos.getZ() - oneBlock.getZ() * m_clipMax.getZ());

        render(loger, model, matrix, clipping);
    }

    /**
     * Calculate scaling
     *
     * @param modelSize
     * @return
     */
    private Vector calcScale(final Vector modelSize) {
        double maxX = m_size.getX();
        double maxY = m_size.getY();
        double maxZ = m_size.getZ();
        if (maxX == Double.NaN || maxY == Double.NaN || maxZ == Double.NaN) {
            double scale = 1;
            if (maxX != Double.NaN) {
                scale = maxX / modelSize.getX();
            }
            if (maxY != Double.NaN) {
                scale = maxY / modelSize.getY();
            }
            if (maxZ != Double.NaN) {
                scale = maxZ / modelSize.getZ();
            }
            return new Vector(scale, scale, scale);
        }

        return new Vector(maxX / modelSize.getX(), maxY / modelSize.getY(), maxZ / modelSize.getZ());
    }

    /**
     * render the model
     *
     * @param loger
     * @param model
     * @param matrix
     */
    private void render(BlockLoger loger, final Model model,
            final Matrix matrix, final ClippingRegion clipping) {
        MCPainterMain.say(m_player, "Rendering model...");
        loger.logMessage("Drawing blocks...");
        model.render(m_player, loger, m_colorMap, clipping, matrix);
        MCPainterMain.say(m_player, "Render done.");
        loger.logMessage("Drawing block done.");
        loger.logEndSession();
        loger.flush();
    }

    private Vector getSafeSize(Vector size) {
        double x = size.getX();
        double y = size.getY();
        double z = size.getZ();

        return new Vector(x != 0 ? x : 1, y != 0 ? y : 1, z != 0 ? z : 1);
    }
}
