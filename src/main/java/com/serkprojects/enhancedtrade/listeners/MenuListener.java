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

package com.serkprojects.enhancedtrade.listeners;

import com.serkprojects.enhancedtrade.EnhancedTrade;
import com.serkprojects.enhancedtrade.menu.TradeMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class MenuListener implements Listener {
    private EnhancedTrade plugin = null;

    public MenuListener(EnhancedTrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void inventoryClick(InventoryClickEvent e) {
        if(e.getInventory() == null || e.getInventory().getType() != InventoryType.CHEST || e.getWhoClicked() == null || !(e.getWhoClicked() instanceof Player)) {return;}
        if(e.getInventory().getTitle() == null || e.getInventory().getTitle().trim().equalsIgnoreCase("")) {return;}

        String menuTitle = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("settings.trade.interface.name"));

        if(!e.getInventory().getTitle().equalsIgnoreCase(menuTitle)) {return;}

        TradeMenu currentTradeMenu = null;

        for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
            if(tradeMenu.getTraderUUID().equals(e.getWhoClicked().getUniqueId()) || tradeMenu.getTradeeUUID().equals(e.getWhoClicked().getUniqueId())) {
                currentTradeMenu = tradeMenu;
                break;
            }
        }

        if(currentTradeMenu == null) {return;}

        if(currentTradeMenu.isReservedSlot(e.getSlot())) {
            e.setCancelled(true);

            if(e.getCursor() != null) {
                currentTradeMenu.handleClick(e);
            }

            return;
        }

        if(currentTradeMenu.getTraderUUID() != null && currentTradeMenu.isTraderSlot(e.getSlot()) && !currentTradeMenu.getTraderUUID().equals(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
        } else if(currentTradeMenu.getTradeeUUID() != null && currentTradeMenu.isTradeeSlot(e.getSlot()) && !currentTradeMenu.getTradeeUUID().equals(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
