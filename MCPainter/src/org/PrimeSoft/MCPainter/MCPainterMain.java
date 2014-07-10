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
package org.PrimeSoft.MCPainter;

import org.PrimeSoft.MCPainter.utils.ExtFileFilter;
import org.PrimeSoft.MCPainter.utils.VersionChecker;
import org.PrimeSoft.MCPainter.palettes.PaletteManager;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.PrimeSoft.MCPainter.Commands.*;
import org.PrimeSoft.MCPainter.Configuration.ConfigProvider;
import org.PrimeSoft.MCPainter.Drawing.Blocks.IBlockProvider;
import org.PrimeSoft.MCPainter.Drawing.Blocks.MultiBlockProvider;
import org.PrimeSoft.MCPainter.Drawing.Blocks.VanillaBlockProvider;
import org.PrimeSoft.MCPainter.Drawing.ColorMap;
import org.PrimeSoft.MCPainter.Drawing.Filters.FilterManager;
import org.PrimeSoft.MCPainter.Drawing.Statue.PlayerStatueDescription;
import org.PrimeSoft.MCPainter.Drawing.Statue.StatueDescription;
import org.PrimeSoft.MCPainter.MCStats.MetricsLite;
import org.PrimeSoft.MCPainter.MapDrawer.MapHelper;
import org.PrimeSoft.MCPainter.Texture.TextureManager;
import org.PrimeSoft.MCPainter.Texture.TexturePack;
import org.PrimeSoft.MCPainter.Texture.TextureProvider;
import org.PrimeSoft.MCPainter.mods.*;
import org.PrimeSoft.MCPainter.palettes.Palette;
import org.PrimeSoft.MCPainter.worldEdit.IWorldEdit;
import org.PrimeSoft.MCPainter.worldEdit.WorldEditFactory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author SBPrime
 */
public class MCPainterMain extends JavaPlugin {

    private static final Logger s_log = Logger.getLogger("Minecraft.MCPainter");
    private static ConsoleCommandSender s_console;
    private static String s_prefix = null;
    private static final String s_logFormat = "%s %s";
    private Boolean m_isInitialized = false;
    private ColorMap m_colorMap = null;
    private IWorldEdit m_worldEdit = null;
    private MetricsLite m_metrics;
    private BlockPlacer m_blockPlacer;
    private TextureManager m_textureManager;
    private PaletteManager m_paletteManager;
    private final EventListener m_listener = new EventListener(this);
    private MapHelper m_mapHelper;
    private BlocksHubIntegration m_blocksHub;
    private BlockCommand m_blockCommand;
    private MapCommand m_mapCommand;
    private HdImageCommand m_hdImageCommand;
    private ModsProvider m_modProvider;
    private MultiBlockProvider m_blocksProvider;
    private ModStatueProvider m_statueProvider;
    private final HashMap<String, ColorMap> m_playerPaletes = new HashMap<String, ColorMap>();

    public static String getPrefix() {
        return s_prefix;
    }

    public PaletteManager getPaletteManager() {
        return m_paletteManager;
    }

    public TextureManager getTextureProvider() {
        return m_textureManager;
    }

    public BlocksHubIntegration getBlocksHub() {
        return m_blocksHub;
    }

    public BlockPlacer getBlockPlacer() {
        return m_blockPlacer;
    }

    public ModsProvider getModsProvider() {
        return m_modProvider;
    }

    public IBlockProvider getBlockProvider() {
        return m_blocksProvider;
    }

    public ModStatueProvider getStatueProvider() {
        return m_statueProvider;
    }

    public static void log(String msg) {
        if (s_log == null || msg == null || s_prefix == null) {
            return;
        }

        s_log.log(Level.INFO, String.format(s_logFormat, s_prefix, msg));
    }

    public static void say(Player player, String msg) {
        if (player == null) {
            s_console.sendRawMessage(msg);
        } else {
            player.sendRawMessage(msg);
        }
    }

    /**
     * Remove player configuration
     *
     * @param playerName
     */
    public void removePlayer(String playerName) {
        synchronized (m_playerPaletes) {
            m_playerPaletes.remove(playerName.toLowerCase());
        }
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile desc = getDescription();
        s_prefix = String.format("[%s]", desc.getName());
        m_isInitialized = false;

        try {
            m_metrics = new MetricsLite(this);
            m_metrics.start();
        } catch (IOException e) {
            log("Error initializing MCStats: " + e.getMessage());
        }

        if (!FoundManager.load(this)) {
            log("Error initializing eco.");
        }

        s_console = getServer().getConsoleSender();

        m_blocksProvider = new MultiBlockProvider();
        m_statueProvider = new ModStatueProvider();
        m_worldEdit = WorldEditFactory.getWorldEditWrapper(this);
        if (m_worldEdit == null) {
            log("World edit not found.");
        }

        m_textureManager = new TextureManager();
        m_paletteManager = new PaletteManager();
        if (!ConfigProvider.load(this)) {
            log("Error loading config");
            return;
        }

        if (ConfigProvider.getCheckUpdate()) {
            log(VersionChecker.CheckVersion(desc.getVersion()));
        }

        if (!ConfigProvider.isConfigUpdated()) {
            log("Please update your config file!");
        }

        String result = initializeConfig();
        if (result != null) {
            log(result);
            return;
        }

        m_mapHelper = new MapHelper();
        m_mapHelper.restoreMaps();

        FilterManager.initializeFilters();

        getServer().getPluginManager().registerEvents(m_listener, this);

        m_isInitialized = true;

        initializeCommands();

        log("Enabled");
    }

    @Override
    public void onDisable() {
        m_textureManager.dispose();
        m_blockPlacer.stop();
        log("Disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (sender instanceof Player) ? (Player) sender : null;

        String commandName = command.getName();
        if (commandName.equalsIgnoreCase(Commands.ALT_IMAGE)) {
            args = Commands.insertArgs(args, Commands.COMMAND_IMAGE);
        } else if (commandName.equalsIgnoreCase(Commands.ALT_MAP)) {
            args = Commands.insertArgs(args, Commands.COMMAND_IMAGEMAP);
        } else if (commandName.equalsIgnoreCase(Commands.ALT_MOB)) {
            args = Commands.insertArgs(args, Commands.COMMAND_MOB);
        } else if (commandName.equalsIgnoreCase(Commands.ALT_STATUE)) {
            if (args.length > 0) {
                args[0] = "u:" + args[0];
            }
            args = Commands.insertArgs(args, Commands.COMMAND_STATUE);
        } else if (commandName.equalsIgnoreCase(Commands.ALT_STATUE_URL) && args.length > 0) {
            args = Commands.insertArgs(args, Commands.COMMAND_STATUE);
        } else if (commandName.equalsIgnoreCase(Commands.ALT_BLOCK)) {
            args = Commands.insertArgs(args, Commands.COMMAND_BLOCK);
        } else if (commandName.equalsIgnoreCase(Commands.ALT_RENDER)) {
            args = Commands.insertArgs(args, Commands.COMMAND_RENDER);
        } else if (!commandName.equalsIgnoreCase(Commands.COMMAND_MAIN)) {
            return false;
        }

        String name = (args != null && args.length > 0) ? args[0] : "";

        if (name.equalsIgnoreCase(Commands.COMMAND_RELOAD)) {
            doReloadConfig(player);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_HELP)) {
            String arg = args != null && args.length > 1 ? args[1] : null;
            return Help.ShowHelp(player, arg);
        }

        if (player == null) {
            return Help.ShowHelp(player, null);
        }

        if (name.equalsIgnoreCase(Commands.COMMAND_PALETTE)) {
            doPalette(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_IMAGE)) {
            doImage(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_STATUE)) {
            doStatue(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_BLOCK)) {
            doBlockCommand(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_IMAGEMAP)) {
            doMap(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_IMAGEHD)) {
            doHdImage(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_PURGE)) {
            doPurge(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_JOBS)) {
            doJobs(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_FILTER)) {
            doFilter(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_MOB)) {
            doMobStatue(player, args);
            return true;
        } else if (name.equalsIgnoreCase(Commands.COMMAND_RENDER)) {
            doRender(player, args);
            return true;
        }

        return Help.ShowHelp(player, null);
    }

    /**
     * Initialize commands
     */
    private void initializeCommands() {
        m_blockCommand = new BlockCommand(this);
        m_mapCommand = new MapCommand(m_mapHelper);
        m_hdImageCommand = new HdImageCommand(m_mapHelper);
    }

    /**
     * Do reload configuration command
     *
     * @param player
     */
    private void doReloadConfig(Player player) {
        if (player != null) {
            if (!PermissionManager.isAllowed(player, PermissionManager.Perms.ReloadConfig)) {
                say(player, ChatColor.RED + "You have no permissions to do that.");
                return;
            }
        }

        log(player != null ? player.getName() : "console " + " reloading config...");

        reloadConfig();
        m_isInitialized = false;
        m_colorMap = null;

        if (!ConfigProvider.load(this)) {
            say(player, "Error loading config");
            return;
        }

        String result = initializeConfig();

        if (result == null) {
            result = "Config reloaded";
        }
        say(player, result);
    }

    /**
     * Initialize all elements of the plugin based on the configuration
     *
     * @return
     */
    private String initializeConfig() {
        m_textureManager.dispose();
        m_paletteManager.clear();
        m_blocksHub = new BlocksHubIntegration(this);

        if (m_blockPlacer != null) {
            m_blockPlacer.queueStop();
        }
        m_blockPlacer = new BlockPlacer(this, m_blocksHub);
        m_modProvider = new ModsProvider(ConfigProvider.getModFolder());

        DataFile[] dataFiles = DataFile.processFiles(ConfigProvider.getDataFolder());
        ModConfig[] configs = DataProvider.loadMods(this, dataFiles);
        initializeTextures(configs);
        initializeTextures(ConfigProvider.getTexturePacks());
        initializeBlocks(configs);
        initializeMobs(configs);
        initializePlayerStatue(dataFiles);
        initializePalettes();

        Palette pal = m_paletteManager.getPalette(ConfigProvider.getDefaultPalette());
        if (pal == null) {
            m_colorMap = null;
            m_isInitialized = false;

            return "Default palette " + ConfigProvider.getDefaultPalette() + " not found";
        } else {
            m_colorMap = new ColorMap(m_textureManager, pal);
        }

        m_isInitialized = m_worldEdit != null;
        return null;
    }

    /**
     * Initialize palettes
     */
    private void initializePalettes() {
        log("Loading palettes...");
        m_paletteManager.clear();
        File paletteDir = ConfigProvider.getPaletteFolder();
        File[] files = paletteDir.listFiles(new ExtFileFilter(new String[]{ExtFileFilter.YML}));

        for (File file : files) {
            if (!file.canRead()) {
                continue;
            }
            try {
                Palette pal = Palette.load(file);
                if (pal != null) {
                    if (!m_paletteManager.addPalette(pal)) {
                        MCPainterMain.log("* " + pal + "...duplicate, ingoring");
                    }
                }
            } catch (Exception ex) {
                MCPainterMain.log("* " + file.getName() + "...unknown error, " + ex.getMessage());
            }
        }

        log("Loaded " + m_paletteManager.getCount() + " palettes.");
    }

    /**
     * Initialize blocks defined in config files
     *
     * @param configs
     */
    private void initializeBlocks(ModConfig[] mods) {
        log("Registering blocks providers...");
        m_blocksProvider.clear();
        for (ModConfig modConfig : mods) {
            ConfigurationSection blocks = modConfig.getBlocks();
            if (blocks == null) {
                continue;
            }

            IBlockProvider bProvider = new ModBlockProvider(m_textureManager, blocks);
            boolean result = m_blocksProvider.register(bProvider);
            if (result) {
                log("* " + modConfig.getName() + "...registered " + bProvider.getBlocksCount() + " blocks.");
            } else {
                log("* " + modConfig.getName() + "...not registered");
            }
        }

        /**
         * Vanilla blocks provider as the last provider to allow block override
         */
        m_blocksProvider.register(new VanillaBlockProvider(m_textureManager));
    }

    /**
     * Initialize player statue
     *
     * @param dataFiles
     */
    private void initializePlayerStatue(DataFile[] dataFiles) {
        log("Initializing player statue...");
        for (DataFile df : dataFiles) {
            if (df.getType() == DataFile.DataFileType.Statue) {
                m_statueProvider.registerPlayer(new PlayerStatueDescription(df.getConfig()));
                log("* Player statue found");
                return;
            }
        }

        log("* Player statue not found");
    }

    /**
     * Initialize mobs defined in config files
     *
     * @param configs
     */
    private void initializeMobs(ModConfig[] mods) {
        log("Registering mob statue providers...");
        m_statueProvider.clear();
        for (ModConfig modConfig : mods) {
            ConfigurationSection mobs = modConfig.getMobs();
            if (mobs == null) {
                continue;
            }

            log("Scanning " + modConfig.getName() + "...");
            Set<String> keys = mobs.getKeys(false);
            for (String mobName : keys) {
                ConfigurationSection mob = mobs.getConfigurationSection(mobName);
                if (mob != null) {
                    StatueDescription description = new StatueDescription(m_textureManager, mob);
                    boolean registered = m_statueProvider.register(description);
                    log(" * " + description.getName() + "..."
                            + (registered ? "added" : "duplicate"));
                }
            }
        }
    }

    /**
     * Initialize mods texture packs
     *
     * @param mods
     */
    private void initializeTextures(ModConfig[] mods) {
        log("Registering mod texture packs...");
        for (ModConfig modConfig : mods) {
            if (modConfig.isInitialized()) {
                TextureProvider provider = new TextureProvider(modConfig.getModId(), modConfig.getModAlternativeId());
                boolean r = provider.initialize(modConfig.getModFile(), modConfig.getTextureRes());

                if (r) {
                    m_textureManager.register(provider);
                    log("* " + modConfig.getName() + " (" + provider.getName() + ")...registered");
                } else {
                    log("* " + modConfig.getName() + " (" + provider.getName() + ")...error");
                }
            }
        }
        m_textureManager.initializeVanilla();
    }

    /**
     * Initialize the config texture packs
     *
     * @param textures
     */
    private void initializeTextures(String[] textures) {
        if (textures == null || textures.length == 0) {
            return;
        }

        log("Registering config texture packs...");
        for (String texture : textures) {
            if (texture == null) {
                continue;
            }
            String[] parts = texture.split(":");
            int res = -1;
            TexturePack tPack = new TexturePack();
            if (parts == null || parts.length != 2
                    || parts[0] == null || parts[0].length() == 0
                    || parts[1] == null || parts[1].length() == 0) {
                tPack = null;
            } else {
                try {
                    res = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ex) {
                    tPack = null;
                }
            }

            if (tPack == null) {
                log("* bad texture entry: " + texture);
            } else if (!tPack.load(parts[0], res)) {
                log("* unable to load texture file: " + parts[0]);
            } else {
                log("* texture pack loaded: " + texture);
                m_textureManager.register(tPack);
            }
        }
    }

    /**
     * set palette for player
     *
     * @param player
     * @param pal
     */
    public void setPalette(String player, Palette pal) {
        synchronized (m_playerPaletes) {
            if (m_playerPaletes.containsKey(player)) {
                m_playerPaletes.remove(player);
            }

            if (pal != null) {
                m_playerPaletes.put(player, new ColorMap(m_textureManager, pal));
            }
        }
    }

    /**
     * Get color map for player
     *
     * @param player
     * @return
     */
    private ColorMap getColorMap(Player player) {
        if (player == null) {
            return m_colorMap;
        }

        ColorMap result;
        synchronized (m_playerPaletes) {
            result = m_playerPaletes.get(player.getName().toLowerCase());
        }

        return result != null ? result : m_colorMap;
    }

    /**
     * Execute the block command
     *
     * @param player
     * @param args
     */
    private void doBlockCommand(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.DrawBlock)) {
            say(player, ChatColor.RED + "You have no permissions to do that.");
            return;
        }

        m_blockCommand.Execte(this, player, m_worldEdit, getColorMap(player), args);
    }

    /**
     * Do the map command
     *
     * @param player
     * @param args
     */
    private void doMap(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.DrawMap)) {
            say(player, ChatColor.RED + "You have no permissions to do that.");
            return;
        }

        m_mapCommand.Execte(this, player, args);
    }

    /**
     * Change the drawing pallete
     *
     * @param player
     * @param args
     */
    private void doPalette(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        PaletteCommand.Execte(this, player, args);
    }

    /**
     * Do the image pixel art command
     *
     * @param player
     * @param args
     */
    private void doImage(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.DrawImage)) {
            say(player, ChatColor.RED + "You have no permissions to do that.");
            return;
        }

        ImageCommand.Execte(this, player, m_worldEdit, getColorMap(player), args);
    }

    /**
     * Do the image pixel art command
     *
     * @param player
     * @param args
     */
    private void doHdImage(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.DrawHdImage)) {
            say(player, ChatColor.RED + "You have no permissions to do that.");
            return;
        }

        m_hdImageCommand.Execute(this, player, m_worldEdit, args);

    }

    /**
     * Do the player statue command
     *
     * @param player
     * @param args
     */
    private void doStatue(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        StatueCommand.Execte(this, player, m_worldEdit, getColorMap(player), args);
    }

    /**
     * Do the mob statue command
     *
     * @param player
     * @param args
     */
    private void doMobStatue(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        MobCommand.Execte(this, player, m_worldEdit, getColorMap(player), args);
    }

    /**
     * Do queue purge command
     *
     * @param player
     * @param args
     */
    private void doPurge(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        PurgeCommand.Execte(this, player, args);
    }

    /**
     * Do jobs command
     *
     * @param player
     * @param args
     */
    private void doJobs(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        JobsCommand.Execte(this, player, args);
    }

    /**
     * Do image filter command
     *
     * @param player
     * @param args
     */
    private void doFilter(Player player, String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }

        FilterCommand.Execte(this, player, args);
    }

    private void doRender(final Player player, final String[] args) {
        if (!m_isInitialized) {
            say(player, ChatColor.RED + "Module not initialized, contact administrator.");
            return;
        }
        
        if (!player.isOp()) {
            return;
        }
        
        RenderCommand.Execute(this, player, m_worldEdit, getColorMap(player), args);
    }

}
