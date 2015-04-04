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

package com.serkprojects.enhancedtrade.tasks;

import com.serkprojects.enhancedtrade.EnhancedTrade;
import com.serkprojects.enhancedtrade.menu.TradeMenu;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.bukkit.scheduler.BukkitRunnable;

public class TradeCancelTask extends BukkitRunnable {
    private EnhancedTrade plugin = null;
    private THashMap<TradeMenu, Integer> tradesCounterMap = null;

    public TradeCancelTask(EnhancedTrade plugin) {
        this.plugin = plugin;
        tradesCounterMap = new THashMap<TradeMenu, Integer>();
    }

    @Override
    public void run() {
        THashSet<TradeMenu> menusToCancel = new THashSet<TradeMenu>();

        for(TradeMenu tradeMenu: tradesCounterMap.keySet()) {
            if(tradesCounterMap.get(tradeMenu) >= plugin.getConfig().getInt("settings.trade.cancelTime")) {
                menusToCancel.add(tradeMenu);
            } else {
                tradesCounterMap.put(tradeMenu, tradesCounterMap.get(tradeMenu) + 1);
            }
        }

        for(TradeMenu tradeMenu: menusToCancel) {
            tradeMenu.cancelTrade();
            plugin.removeActiveTrade(tradeMenu);
        }
    }

    /**
     * Adds a trade to start counting for
     * @param tradeMenu the trade menu to add
     */
    public void addTradeCounter(TradeMenu tradeMenu) {
        tradesCounterMap.put(tradeMenu, 0);
    }

    /**
     * Removes a trade trade from counting
     * @param tradeMenu the trade menu to stop counting for
     */
    public void removeTradeCounter(TradeMenu tradeMenu) {
        tradesCounterMap.remove(tradeMenu);
    }
}
