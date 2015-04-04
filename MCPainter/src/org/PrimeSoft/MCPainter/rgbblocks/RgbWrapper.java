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
package org.PrimeSoft.MCPainter.rgbblocks;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.primesoft.customheadapi.CustomHeadApi;
import org.primesoft.customheadapi.IHeadCreator;
import org.primesoft.customheadapi.implementation.CustomHeadCreator;
import org.primesoft.simplehttpserver.SimpleHTTPServerMain;
import org.primesoft.simplehttpserver.api.IApi;
import org.primesoft.simplehttpserver.api.IHttpServer;

/**
 *
 * @author SBPrime
 */
public class RgbWrapper {

    private static final int MAX_CACHE = 4096;
    

    /**
     * Log message on the console
     *
     * @param s
     */
    private static void log(String s) {
        MCPainterMain.log(s);
    }

    /**
     * Create a new instance of the Rgb wrapper
     *
     * @param plugin
     * @return
     */
    public static RgbWrapper create(Plugin plugin) {
        Server server = plugin.getServer();

        IHeadCreator hCreator = initializeCustomHeadAPI(server);
        IApi simpleServer = initializeSimpleServer(server);

        if (hCreator == null || simpleServer == null) {
            return null;
        }

        return new RgbWrapper(hCreator, simpleServer);
    }

    /**
     * Try to initialize the custom head api plugin
     *
     * @param server
     * @return
     */
    private static IHeadCreator initializeCustomHeadAPI(Server server) {
        CustomHeadApi chMain = getCHApi(server);
        if (chMain == null) {
            log("* CustomHeadAPI...not found");
            return null;
        }

        IHeadCreator hCreator = chMain.getHeadCreator();
        if (!(hCreator instanceof CustomHeadCreator)) {
            log("* CustomHeadAPI...not initialized");
            return null;
        }
        log("* CustomHeadAPI...OK");

        return hCreator;
    }

    /**
     * Try to initialize the simple server plugin
     *
     * @param server
     * @return
     */
    private static IApi initializeSimpleServer(Server server) {
        SimpleHTTPServerMain ssMain = getSS(server);
        if (ssMain == null) {
            log("* SimpleHTTPServer...not found");
            return null;
        }

        IApi api = ssMain.getAPI();
        if (api == null) {
            log("* SimpleHTTPServer...API not initialized");
            return null;
        }

        if (api.getServer() == null) {
            log("* SimpleHTTPServer...HTTP server not initialized");
            return null;
        }

        String address = api.getExternalAddress();
        if (address == null || address.isEmpty()) {
            log("* SimpleHTTPServer...external http addres not set");
            return null;
        }

        log("* SimpleHTTPServer...external http addres: " + address);
        log("* SimpleHTTPServer...OK");

        return api;
    }

    /**
     * Get instance of CustomHeadAPI plugin
     *
     * @return
     */
    private static CustomHeadApi getCHApi(Server server) {
        try {
            Plugin wPlugin = server.getPluginManager().getPlugin("CustomHeadApi");

            if ((wPlugin == null) || (!(wPlugin instanceof CustomHeadApi))) {
                return null;
            }

            return (CustomHeadApi) wPlugin;
        } catch (NoClassDefFoundError ex) {
            return null;
        }
    }

    /**
     * Get instance of Simple server plugin
     *
     * @return
     */
    private static SimpleHTTPServerMain getSS(Server server) {
        try {
            Plugin wPlugin = server.getPluginManager().getPlugin("SimpleHTTPServer");

            if ((wPlugin == null) || (!(wPlugin instanceof SimpleHTTPServerMain))) {
                return null;
            }

            return (SimpleHTTPServerMain) wPlugin;
        } catch (NoClassDefFoundError ex) {
            return null;
        }
    }

    public final LinkedHashMap<Integer, ItemStack> m_colorCatch = new LinkedHashMap<Integer, ItemStack>(MAX_CACHE + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, ItemStack> eldest) {
            return size() > MAX_CACHE;
        }
    };

    private final IHeadCreator m_headCreator;

    private final IApi m_simpleServer;

    private final IHttpServer m_httpServer;

    private RgbWrapper(IHeadCreator hCreator, IApi simpleServer) {
        m_headCreator = hCreator;
        m_simpleServer = simpleServer;
        m_httpServer = simpleServer.getServer();        
    }

    public void enable() {
        m_httpServer.registerService(RgbHeadService.SERVICE, new RgbHeadService());
        log("* Registered RGB head at: " + m_simpleServer.getExternalAddress() + RgbHeadService.SERVICE.substring(1));
    }

    public void disable() {
        m_httpServer.unregisterService(RgbHeadService.SERVICE);
    }

    public ItemStack createHead(Color c) {
        synchronized (m_colorCatch) {
            int color = c.getRGB();            
            if (m_colorCatch.containsKey(color)) {
                return m_colorCatch.get(color);
            }
                        
            String url = RgbHeadService.buildUrl(c, m_simpleServer.getExternalAddress());
            ItemStack head = m_headCreator.createItemStack(url);
            m_colorCatch.put(color, head);
            
            return head;
        }                
    }
}
