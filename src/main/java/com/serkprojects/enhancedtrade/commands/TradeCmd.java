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

package com.serkprojects.enhancedtrade.commands;

import com.serkprojects.enhancedtrade.EnhancedTrade;
import com.serkprojects.enhancedtrade.menu.TradeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCmd implements CommandExecutor {
    private EnhancedTrade plugin = null;

    public TradeCmd(EnhancedTrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("commandPlayerRequired"));
            return true;
        }

        Player senderPlayer = (Player) sender;

        if (args.length == 0) {
            for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
                if(tradeMenu.getTraderUUID().equals(senderPlayer.getUniqueId()) || tradeMenu.getTradeeUUID().equals(senderPlayer.getUniqueId())) {
                    senderPlayer.openInventory(senderPlayer.getInventory());
                }
            }

            help(sender);
            return true;
        }

        if (args.length > 0) {
            if(plugin.isTrading(senderPlayer.getUniqueId())) {
                plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("stillTrading"));
                return true;
            }

            Player player = plugin.getServer().getPlayer(args[0]);

            if(player == null) {
                plugin.getMessaging().sendMessage(sender, false, plugin.getLanguage().getMessage("playerNotFound"));
                return true;
            }

            TradeMenu tradeMenu = new TradeMenu(plugin);
            tradeMenu.setTraderUUID(player.getUniqueId());
        }

        help(sender);
        return true;
    }

    private void help(CommandSender sender) {
        plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("usageTrade").replace("{name}", plugin.getDescription().getName()));
    }
}
