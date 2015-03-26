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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {
    EnhancedTrade plugin = null;

    public PlayerListener(EnhancedTrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void playerQuit(PlayerQuitEvent e) {
        if(e.getPlayer() == null) {return;}
        if(plugin.isTrading(e.getPlayer().getUniqueId())) {return;}

        TradeMenu currentTradeMenu = null;

        for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
            if(tradeMenu.getTraderUUID() != null && tradeMenu.getTraderUUID().equals(e.getPlayer().getUniqueId()) || tradeMenu.getTradeeUUID() != null && tradeMenu.getTradeeUUID().equals(e.getPlayer().getUniqueId())) {
                currentTradeMenu = tradeMenu;
            }
        }

        if(currentTradeMenu == null) {return;}

        currentTradeMenu.cancelTrade();
    }

    @EventHandler(ignoreCancelled = true)
    public void playerKick(PlayerKickEvent e) {
        if(e.getPlayer() == null) {return;}
        if(plugin.isTrading(e.getPlayer().getUniqueId())) {return;}

        TradeMenu currentTradeMenu = null;

        for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
            if(tradeMenu.getTraderUUID() != null && tradeMenu.getTraderUUID().equals(e.getPlayer().getUniqueId()) || tradeMenu.getTradeeUUID() != null && tradeMenu.getTradeeUUID().equals(e.getPlayer().getUniqueId())) {
                currentTradeMenu = tradeMenu;
            }
        }

        if(currentTradeMenu == null) {return;}

        currentTradeMenu.cancelTrade();
    }

    @EventHandler(ignoreCancelled = true)
    public void playerPickItem(PlayerPickupItemEvent e) {
        if(e.getItem() == null || e.getPlayer() == null) {return;}
        if(!e.getItem().hasMetadata("p-protected")) {return;}

        List<MetadataValue> metadataValues = e.getItem().getMetadata("p-protected");

        UUID ownerUUID = null;

        for(MetadataValue metadataValue: metadataValues) {
            ownerUUID = UUID.fromString(metadataValue.asString());
        }

        if(!e.getPlayer().getUniqueId().equals(ownerUUID)) {
            e.setCancelled(true);
        }
    }
}
