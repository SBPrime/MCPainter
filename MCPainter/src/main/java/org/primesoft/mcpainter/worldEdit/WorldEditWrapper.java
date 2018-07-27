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

package org.primesoft.mcpainter.worldEdit;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.primesoft.mcpainter.utils.BaseBlock;
import org.primesoft.mcpainter.utils.Vector;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author SBPrime
 */
public class WorldEditWrapper implements IWorldEdit {
    private final WorldEditPlugin m_worldEdit;
    
    public WorldEditWrapper(Plugin wePlugin) {
        m_worldEdit = (WorldEditPlugin)wePlugin;
    }
    
    
    @Override
    public boolean isRealWorldEdit() {
        return true;
    }

    @Override
    public ILocalSession getSession(Player player) {
        LocalSession lSession = m_worldEdit.getSession(player);
        return new WorldEditLocalSession(lSession);
    }

    @Override
    public ILocalPlayer wrapPlayer(Player player) {
        LocalPlayer lPlayer = m_worldEdit.wrapPlayer(player);
        return new WorldEditLocalPlayer(lPlayer);
    }

    @Override
    public ICuboidSelection getSelection(Player player) {
        Selection selection = m_worldEdit.getSelection(player);
        if (!(selection instanceof CuboidSelection)) {
            return null;
        }
        
        return new WorldEditCuboidSelection((CuboidSelection)selection);
    }
    
    
    public static com.sk89q.worldedit.Vector convert(Vector v) {
        return new com.sk89q.worldedit.Vector(v.getX(), v.getY(), v.getZ());
    }

    public static Vector convert(com.sk89q.worldedit.Vector v) {
        return new Vector(v.getX(), v.getY(), v.getZ());
    }
    
    public static com.sk89q.worldedit.blocks.BaseBlock convert(BaseBlock v) {
        return new com.sk89q.worldedit.blocks.BaseBlock(v.getType(), v.getData());
    }
        
    public static BaseBlock convert(com.sk89q.worldedit.blocks.BaseBlock v) {
        return new BaseBlock(Material.getMaterial(v.getType()), v.getData());
    }
}
