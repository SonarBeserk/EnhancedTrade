/**
 * ********************************************************************************************************************
 * EnhancedTrade - Provides an enhanced trade system
 * ====================================================================================================================
 * Copyright (C) 2015 by SonarBeserk, SerkProjects
 * https://gitlab.com/serkprojects/enhancedtrade
 * *********************************************************************************************************************
 * *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * *
 * *********************************************************************************************************************
 * Please refer to LICENSE for the full license. If it is not there, see <http://www.gnu.org/licenses/>.
 * *********************************************************************************************************************
 */

package com.serkprojects.enhancedtrade;

import com.serkprojects.enhancedtrade.commands.TradeCmd;
import com.serkprojects.enhancedtrade.listeners.MenuListener;
import com.serkprojects.enhancedtrade.listeners.PlayerListener;
import com.serkprojects.enhancedtrade.menu.TradeMenu;
import com.serkprojects.enhancedtrade.tasks.TradeCancelTask;
import com.serkprojects.enhancedtrade.tasks.TradeTickDownTask;
import com.serkprojects.serkcore.plugin.JavaPlugin;
import gnu.trove.set.hash.THashSet;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class EnhancedTrade extends JavaPlugin {
    private Economy economy = null;

    private THashSet<TradeMenu> activeTrades = null;
    private TradeCancelTask tradeCancelTask = null;
    private TradeTickDownTask tradeTickDownTask = null;

    @Override
    public boolean shouldSaveData() {
        return true;
    }

    @Override
    public boolean registerPremadeMainCMD() {
        return true;
    }

    @Override
    public String getPermissionPrefix() {
        return getConfig().getString("settings.permissionPrefix");
    }

    @Override
    public void onReload() {
        for(final TradeMenu tradeMenu: getActiveTrades()) {
            tradeMenu.cancelTrade();
        }
    }

    public void onEnable() {
        super.onEnable();

        activeTrades = new THashSet<TradeMenu>();

        setupEconomy();

        getCommand("trade").setExecutor(new TradeCmd(this));

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        tradeCancelTask = new TradeCancelTask(this);
        tradeCancelTask.runTaskTimer(this, 0, 1200);

        tradeTickDownTask = new TradeTickDownTask(this);
        tradeTickDownTask.runTaskTimer(this, 0, 20);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    /**
     * Returns the economy instance
     * @return the economy instance
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Returns the read-only set of currently active trades
     * @return the read-only set of currently active trades
     */
    public THashSet<TradeMenu> getActiveTrades() {
        return new THashSet<>(activeTrades);
    }

    /**
     * Returns the trade cancel task
     * @return the trade cancel task
     */
    public TradeCancelTask getTradeCancelTask() {
        return tradeCancelTask;
    }

    /**
     * Returns the trade tick down task
     * @return the trade tick down task
     */
    public TradeTickDownTask getTradeTickDownTask() {
        return tradeTickDownTask;
    }

    /**
     * Adds an active trade
     * @param tradeMenu the trade menu instance
     */
    public void addActiveTrade(TradeMenu tradeMenu) {
        if(tradeMenu.getTraderUUID() == null) {return;}

        activeTrades.add(tradeMenu);
    }

    /**
     * Removes am active trade
     * @param tradeMenu the trade menu instance
     */
    public void removeActiveTrade(TradeMenu tradeMenu) {
        activeTrades.remove(tradeMenu);
    }

    /**
     * Returns if a UUID is involved in a trade
     * @param UUID the UUID to check
     * @return if a UUID is involved in a trade
     */
    public boolean isTrading(UUID UUID, boolean onlyAccepted) {
        for(TradeMenu tradeMenu: activeTrades) {
            if(UUID.equals(tradeMenu.getTraderUUID())|| UUID.equals(tradeMenu.getTradeeUUID())) {
                if(onlyAccepted) {
                    if(tradeMenu.isAwaitingAcceptance()) {
                        return false;
                    } else {
                        return true;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public void onDisable() {
        super.onDisable();
    }
}
