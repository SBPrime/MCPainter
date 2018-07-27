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

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author SBPrime
 */
public class FoundManager {

    /**
     * The MTA mutex
     */
    private static final Object s_mutex = new Object();

    /**
     * The economy manager
     */
    private static Economy s_economy = null;

    /**
     * Is found manager enabled
     */
    private static boolean s_isEnabled = false;

    /**
     * Load permissions
     *
     * @param plugin parent plugin
     * @return true if load ok
     */
    public static boolean load(JavaPlugin plugin) {
        s_isEnabled = false;

        try {
            RegisteredServiceProvider<Economy> economyProvider
                    = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                s_economy = economyProvider.getProvider();
            }

            s_isEnabled = s_economy != null;
            return (s_economy != null);
        } catch (NoClassDefFoundError ex) {
            return false;
        }
    }

    /**
     * Get the player money
     *
     * @param player Player money
     * @return Current player money
     */
    public static double getMoney(Player player) {
        if (!s_isEnabled || s_economy == null) {
            return 0;
        }

        return s_economy.getBalance(player.getName());
    }

    /**
     * Subtract money from player
     *
     * @param player The player
     * @param n Ammount of money to subtract
     */
    public static void subtractMoney(Player player, double n) {
        if (!s_isEnabled || s_economy == null) {
            return;
        }

        s_economy.withdrawPlayer(player.getName(), n);
    }

    /**
     * Add money to player
     *
     * @param player The player
     * @param n Ammount of money to add
     */
    public static void addMoney(Player player, double n) {
        if (!s_isEnabled || s_economy == null) {
            return;
        }

        s_economy.depositPlayer(player.getName(), n);
    }

    /**
     * Get the MTA access mutex
     *
     * @return the mutex
     */
    public static Object getMutex() {
        return s_mutex;
    }
}
