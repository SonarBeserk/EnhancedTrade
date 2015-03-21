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

import com.serkprojects.enhancedtrade.listeners.TestListener;
import com.serkprojects.enhancedtrade.menu.TradeMenu;
import com.serkprojects.serkcore.plugin.JavaPlugin;
import gnu.trove.set.hash.THashSet;

public class EnhancedTrade extends JavaPlugin {

    private THashSet<TradeMenu> activeTrades = null;
    // Use barriers in place of items waiting to be traded to prevent loss

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

    public void onEnable() {
        super.onEnable();

        activeTrades = new THashSet<TradeMenu>();

        getServer().getPluginManager().registerEvents(new TestListener(this), this);
    }

    /**
     * Returns the set of currently active trades
     * @return the set of currently active trades
     */
    public THashSet<TradeMenu> getActiveTrades() {
        return activeTrades;
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

    public void onDisable() {
        super.onDisable();
    }
}
