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
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class TradeTickDownTask extends BukkitRunnable {
    private EnhancedTrade plugin = null;
    private THashMap<TradeMenu, Integer> tradeInventoriesMap = null;
    private THashSet<TradeMenu> inventoriesToRemove = new THashSet<TradeMenu>();

    public TradeTickDownTask(EnhancedTrade plugin) {
        this.plugin = plugin;
        tradeInventoriesMap = new THashMap<TradeMenu, Integer>();
    }

    @Override
    public void run() {
        if(tradeInventoriesMap.size() == 0) {return;}

        for(TradeMenu tradeMenu : tradeInventoriesMap.keySet()) {
            if(tradeInventoriesMap.get(tradeMenu) > 0) {
                tradeInventoriesMap.put(tradeMenu, tradeInventoriesMap.get(tradeMenu) - 1);
                tradeMenu.setAllMenuItemAmounts(tradeInventoriesMap.get(tradeMenu));
                playSoundToViewers(tradeMenu.getInventory(), Sound.ANVIL_USE);
            }

            if(tradeInventoriesMap.get(tradeMenu) <= 1) {
                tradeMenu.completeTrade();
                inventoriesToRemove.add(tradeMenu);
            }
        }

        for(TradeMenu tradeMenu: inventoriesToRemove) {
            tradeInventoriesMap.remove(tradeMenu);
        }
    }

    /**
     * Adds a trade menu to tick down
     * @param tradeMenu the trade menu
     * @param counter the counter to set for it
     */
    public void addTickingDownTradeMenu(TradeMenu tradeMenu, int counter) {
        tradeInventoriesMap.put(tradeMenu, counter);
    }

    /**
     * Remove a trade menu that is ticking down
     * @param tradeMenu the trade menu to remove
     */
    public void removeTickingDownTradeMenu(TradeMenu tradeMenu) {
        tradeInventoriesMap.remove(tradeMenu);
    }

    private void playSoundToViewers(Inventory inventory, Sound sound) {
        for(HumanEntity humanEntity: inventory.getViewers()) {
            if(humanEntity instanceof Player) {
                Player player = (Player) humanEntity;
                player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
            }
        }
    }
}
