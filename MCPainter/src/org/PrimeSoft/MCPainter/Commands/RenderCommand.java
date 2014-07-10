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

import org.PrimeSoft.MCPainter.BlockLoger;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.PrimeSoft.MCPainter.utils.Orientation;
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

    public static void Execute(MCPainterMain sender, Player m_player, IWorldEdit worldEdit,
            ColorMap colorMap, String[] m_args) {
        if (!m_player.isOp()) {
            return;
        }

        sender.getServer().getScheduler().runTaskAsynchronously(sender,
                new RenderCommand(sender, m_player, m_args, worldEdit, colorMap));
    }

    private final String[] m_args;
    private final Player m_player;
    private final double m_yaw;
    private final double m_pitch;
    private final Vector m_pPosition;
    private final IEditSession m_session;
    private final ILocalSession m_lSession;
    private final ColorMap m_colorMap;
    private final MCPainterMain m_sender;

    private RenderCommand(MCPainterMain sender, Player player, String[] args, IWorldEdit worldEdit, ColorMap colorMap) {
        m_args = args;
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

        String fileName = m_args.length > 1 ? m_args[1] : "cat.obj";
        int max;
        try {
            max = m_args.length > 2 ? Integer.parseInt(m_args[2]) : 100;
        } catch (NumberFormatException ex) {
            max = 20;
        }

        MCPainterMain.say(m_player, "Loading model...");
        final Model model = WavefrontObj.load(ConfigProvider.getModelFolder(), fileName);
        if (model == null) {
            MCPainterMain.say(m_player, "Error loading model.");
            return;
        }
        
        final Vertex minPos = model.getMin();
        final Vertex maxPos = model.getMax();
        final Vertex size = model.getSize();

        final Matrix matrix = Matrix.getIdentity();
        double scale = max / size.getY();
        matrix.translate(m_pPosition.getBlockX(), m_pPosition.getBlockY(), m_pPosition.getBlockZ());
        matrix.rotateY(m_yaw * Math.PI / 180);
        matrix.rotateX(m_pitch * Math.PI / 180);
        matrix.scale(scale, scale, scale);
        matrix.translate(-minPos.getX(), -minPos.getY(), -minPos.getZ());
                
        ClippingRegion clipping = new ClippingRegion(minPos.getX(), maxPos.getX(), 
                minPos.getY(), maxPos.getY(), 
                minPos.getZ(), maxPos.getZ());
                
        
        render(loger, model, matrix, clipping);
    }        

    
    /**
     * render the model
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
}