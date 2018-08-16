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
package org.primesoft.mcpainter.commands;

import java.awt.image.BufferedImage;
import java.util.stream.Stream;
import org.primesoft.mcpainter.blocksplacer.BlockLoger;
import org.primesoft.mcpainter.configuration.ConfigProvider;
import org.primesoft.mcpainter.drawing.dilters.CropFilter;
import org.primesoft.mcpainter.drawing.dilters.FilterManager;
import org.primesoft.mcpainter.drawing.ImageHelper;
import org.primesoft.mcpainter.FoundManager;
import org.primesoft.mcpainter.Help;
import org.primesoft.mcpainter.mapdrawer.MapHelper;
import org.primesoft.mcpainter.PermissionManager;
import org.primesoft.mcpainter.MCPainterMain;
import org.primesoft.mcpainter.worldEdit.CuboidSelection;
import org.primesoft.mcpainter.worldEdit.IEditSession;
import org.primesoft.mcpainter.worldEdit.ILocalPlayer;
import org.primesoft.mcpainter.worldEdit.ILocalSession;
import org.primesoft.mcpainter.worldEdit.IWorldEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.primesoft.mcpainter.blocksplacer.IChange;
import org.primesoft.mcpainter.utils.Vector;

/**
 * @author SBPrime
 */
public class HdImageCommand {

    private final MapHelper m_mapHelper;

    public HdImageCommand(MapHelper mapHelper) {
        m_mapHelper = mapHelper;
    }

    public void Execute(MCPainterMain sender, Player player, IWorldEdit worldEdit, String[] args) {
        if (args.length != 2 && args.length != 4) {
            Help.ShowHelp(player, Commands.COMMAND_IMAGEHD);
            return;
        }

        String url = args[1];
        final CuboidSelection selection;
        if (args.length == 2) {
            selection = worldEdit.getSelection(player);
        } else {
            final World w = player.getWorld();
            Vector v1 = Vector.parse(args[2]);
            Vector v2 = Vector.parse(args[3]);

            if (v1 == null || v2 == null) {
                selection = null;
            } else {
                selection = new CuboidSelection(w, new Vector(
                        Math.min(v1.getX(), v2.getX()),
                        Math.min(v1.getY(), v2.getY()),
                        Math.min(v1.getZ(), v2.getZ())),
                        new Vector(
                                Math.max(v1.getX(), v2.getX()),
                                Math.max(v1.getY(), v2.getY()),
                                Math.max(v1.getZ(), v2.getZ())));
            }
        }

        if (selection == null) {
            MCPainterMain.say(player, ChatColor.RED + "No selection.");
            return;
        }

        sender.getServer().getScheduler().runTaskAsynchronously(sender,
                new CommandThread(this, sender, player, url, worldEdit, selection));
    }

    private static boolean isSolid(Material m) {
        if (m == Material.AIR) {
            return false;
        }
        if (m == Material.LAVA) {
            return false;
        }
        if (m == Material.WATER) {
            return false;
        }
        if (m == Material.END_PORTAL) {
            return false;
        }
        if (m == Material.NETHER_PORTAL) {
            return false;
        }

        return m.isSolid() && m.isBlock();
    }

    private class DrawMapCommand implements IChange {

        private final Location m_location;
        private final BufferedImage m_img;
        private final MapHelper m_mapHelper;
        private final BlockFace m_rotation;

        private Material m_oldMaterial;
        private ItemFrame m_frame;
        private MapView m_mapView;

        private DrawMapCommand(Location location, BlockFace face,
                int offsetX, int offsetY, BufferedImage img,
                MapHelper mapHelper) {
            m_location = location;

            int x = Math.min(offsetX + 127, img.getWidth() - 1);
            int y = Math.min(offsetY + 127, img.getHeight() - 1);

            m_img = CropFilter.crop(img, offsetX, offsetY, x, y, false);
            m_mapHelper = mapHelper;
            m_rotation = face;
        }

        @Override
        public void redo() {
            Chunk chunk = m_location.getChunk();
            if (!chunk.isLoaded()) {
                if (!chunk.load()) {
                    return;
                }
            }

            World w = m_location.getWorld();
            Block block = w.getBlockAt(m_location);
            Material material = block.getType();

            if (!isSolid(material)) {
                m_oldMaterial = material;
                block.setType(Material.BARRIER);
            } else {
                m_oldMaterial = null;
            }

            Location ifLocation = block.getRelative(m_rotation).getLocation();
            ItemFrame tFrame = Stream.of(chunk.getEntities())
                    .filter(e -> EntityType.ITEM_FRAME.equals(e.getType()))
                    .map(e -> new Object() {
                final ItemFrame frame = (ItemFrame) e;
                final BlockFace facing = ((ItemFrame) e).getFacing();
                final Location location = e.getLocation();
            })
                    .filter(e -> m_rotation.equals(e.facing)
                    && ifLocation.getBlockX() == e.location.getBlockX()
                    && ifLocation.getBlockY() == e.location.getBlockY()
                    && ifLocation.getBlockZ() == e.location.getBlockZ())
                    .map(e -> e.frame)
                    .findAny()
                    .orElse((ItemFrame) null);

            if (tFrame == null) {
                tFrame = (ItemFrame) w.spawn(ifLocation, ItemFrame.class);
                tFrame.setFacingDirection(m_rotation, true);
                tFrame.setRotation(Rotation.NONE);
            }

            m_frame = tFrame;
            ItemStack frameContent = m_frame.getItem();
            MapView mapView = null;

            if (Material.FILLED_MAP.equals(frameContent.getType())) {
                mapView = Bukkit.getMap((short) ((MapMeta) frameContent.getItemMeta()).getMapId());
            } else {
                mapView = Bukkit.createMap(w);
                frameContent = new ItemStack(Material.FILLED_MAP, 1);
                MapMeta mm = ((MapMeta)frameContent.getItemMeta());
                mm .setMapId(mapView.getId());
                frameContent.setItemMeta(mm);

                m_frame.setItem(frameContent);                
            }

            m_mapView = mapView;
            m_mapHelper.storeMap(m_mapView, m_img);
            m_mapHelper.drawImage(m_mapView, m_img);
        }

        @Override
        public void undo() {
        }

        @Override
        public Location getLocation() {
            return m_location;
        }
    }

    private class CommandThread implements Runnable {

        private final CuboidSelection m_selection;
        private final String m_url;
        private final Player m_player;
        private final MCPainterMain m_sender;
        private final HdImageCommand m_this;
        private final IEditSession m_session;
        private final ILocalSession m_lSession;
        private final BlockFace m_rotation;

        private CommandThread(HdImageCommand command, MCPainterMain sender, Player player,
                String url, IWorldEdit worldEdit, CuboidSelection selection) {
            m_this = command;
            m_sender = sender;
            m_player = player;
            m_url = url;
            m_selection = selection;
            ILocalPlayer localPlayer = worldEdit.wrapPlayer(player);
            m_rotation = calcHeading(localPlayer.getYaw());
            m_lSession = worldEdit.getSession(player);
            m_session = m_lSession.createEditSession(localPlayer);
        }

        @Override
        public void run() {
            FilterManager fm = FilterManager.getFilterManager(m_player);
            double price = ConfigProvider.getCommandPrice("imagehd") + fm.getPrice();
            synchronized (FoundManager.getMutex()) {
                if (price > 0 && FoundManager.getMoney(m_player) < price) {
                    MCPainterMain.say(m_player, ChatColor.RED + "You don't have sufficient funds to apply all the filters and draw the map.");
                    return;
                }

                MCPainterMain.say(m_player, "Loading image...");
                BufferedImage img = ImageHelper.downloadImage(m_url);
                if (img == null) {
                    MCPainterMain.say(m_player, ChatColor.RED + "Error downloading image " + ChatColor.WHITE + m_url);
                    return;
                }

                img = fm.applyFilters(img, null);
                final BufferedImage fImg = img;
                int imgH = img.getHeight();
                int imgW = img.getWidth();

                if (!PermissionManager.checkImage(m_player, imgW, imgH)) {
                    return;
                }

                Vector minPoint = m_selection.getMinimumPoint();
                Vector maxPoint = m_selection.getMaximumPoint();

                int dx = (int) (maxPoint.getX() - minPoint.getX());
                int dz = (int) (maxPoint.getZ() - minPoint.getZ());
                int dy = (int) (maxPoint.getY() - minPoint.getY());

                int kx, kz;

                if (dx > 1 && dz > 1) {
                    MCPainterMain.say(m_player, ChatColor.RED + "Invalid selection area.");
                    return;
                } else if (dx > dz) {
                    kx = 1;
                    kz = 0;
                } else {
                    kx = 0;
                    kz = 1;
                }

                int bHeight = imgH / 128 + (imgH % 128 != 0 ? 1 : 0);
                int bWidth = imgW / 128 + (imgW % 128 != 0 ? 1 : 0);
                if (dy < bHeight || (dx < bWidth && dz < bWidth)) {
                    MCPainterMain.say(m_player, ChatColor.RED + "The selection is to smal, required: " + bWidth + "x" + bHeight);
                    return;
                }

                Location pos = new Location(m_selection.getWorld(), minPoint.getBlockX(), maxPoint.getBlockY(), minPoint.getBlockZ());
                if (m_rotation == BlockFace.NORTH
                        || m_rotation == BlockFace.EAST) {
                    pos = pos.add(kx * (bWidth - 1), 0, kz * (bWidth - 1));
                    kx *= -1;
                    kz *= -1;
                }
                MCPainterMain.say(m_player, "Drawing image...");
                BlockLoger loger = new BlockLoger(m_player, m_lSession, m_session, m_sender);

                for (int py = 0; py < bHeight; py++) {
                    Location tmp = pos.clone();
                    for (int px = 0; px < bWidth; px++) {
                        loger.logChange(new DrawMapCommand(tmp.clone(), m_rotation,
                                px * 128, py * 128, fImg, m_mapHelper));
                        tmp = tmp.add(kx, 0, kz);
                    }
                    pos = pos.add(0, -1, 0);
                }

                loger.logMessage("Drawing image done.");
                loger.logEndSession();
                loger.flush();

                FoundManager.subtractMoney(m_player, price);
            }
        }

        private BlockFace calcHeading(double yaw) {
            yaw = (yaw + 360) % 360;
            if (yaw < 45) {
                return BlockFace.NORTH;
            } else if (yaw < 135) {
                return BlockFace.EAST;
            } else if (yaw < 225) {
                return BlockFace.SOUTH;
            } else if (yaw < 315) {
                return BlockFace.WEST;
            } else {
                return BlockFace.NORTH;
            }
        }
    }
}
