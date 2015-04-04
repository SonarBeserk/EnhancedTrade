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
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCmd implements CommandExecutor {
    private EnhancedTrade plugin = null;

    /**
     * Creates an instance of the CommandExecutor for the trade command
     * @param plugin the instance of the plugin to draw settings from
     */
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
            if(plugin.getConfig().getBoolean("settings.trade.allowedCreativeTrading") && senderPlayer.getGameMode() == GameMode.CREATIVE) {
                plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeCreativeNotAllowed"));
                return true;
            }

            help(sender);
            return true;
        }

        if (args.length > 0) {
            if(plugin.getConfig().getBoolean("settings.trade.allowedCreativeTrading") && senderPlayer.getGameMode() == GameMode.CREATIVE) {
                plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeCreativeNotAllowed"));
                return true;
            }

            if(args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("request")) {
                requestSubCommand(sender, args);
                return true;
            }

            if(args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("accept")) {
                acceptSubCommand(sender);
                return true;
            }

            if(args[0].equals("d") || args[0].equals("deny")) {
                denySubCommand(sender);
                return true;
            }

            if(args[0].equalsIgnoreCase("o") || args[0].equalsIgnoreCase("open")) {
                openSubCommand(sender);
                return true;
            }
        }

        help(sender);
        return true;
    }

    private void help(CommandSender sender) {
        plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("usageTrade").replace("{name}", plugin.getDescription().getName()));
    }

    private void requestSubCommand(CommandSender sender, String[] args) {
        Player senderPlayer = (Player) sender;

        if(plugin.isTrading(senderPlayer.getUniqueId(), false)) {
            plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeStillGoing"));
            return;
        }

        Player targetPlayer = plugin.getServer().getPlayer(args[1]);

        if(targetPlayer == null) {
            plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("playerNotFound").replace("{name}", args[1]));
            return;
        }

        if(targetPlayer.getUniqueId().equals(senderPlayer.getUniqueId())) {
            plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeSelfNotAllowed"));
            return;
        }

        TradeMenu tradeMenu = new TradeMenu(plugin);
        tradeMenu.setTraderUUID(senderPlayer.getUniqueId());
        tradeMenu.setTradeeUUID(targetPlayer.getUniqueId());

        plugin.addActiveTrade(tradeMenu);
        plugin.getTradeCancelTask().addTradeCounter(tradeMenu);

        plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("tradePlayer").replace("{name}", targetPlayer.getName()));
        plugin.getMessaging().sendMessage(targetPlayer, true, plugin.getLanguage().getMessage("tradeSent").replace("{name}", targetPlayer.getName()));
    }

    private void acceptSubCommand(CommandSender sender) {
        Player senderPlayer = (Player) sender;

        if(plugin.isTrading(senderPlayer.getUniqueId(), true)) {
            plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeStillGoing"));
            return;
        }

        for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
            if(tradeMenu.getTraderUUID() != null && tradeMenu.getTradeeUUID().equals(senderPlayer.getUniqueId())) {
                Player traderPlayer = plugin.getServer().getPlayer(tradeMenu.getTraderUUID());
                plugin.getTradeCancelTask().removeTradeCounter(tradeMenu);
                tradeMenu.acceptTrade();
                plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeAccept").replace("{name}", traderPlayer.getName()));
                plugin.getMessaging().sendMessage(traderPlayer, true, plugin.getLanguage().getMessage("tradeAccepted").replace("{name}", senderPlayer.getName()));
                return;
            }
        }
    }

    private void denySubCommand(CommandSender sender) {
        Player senderPlayer = (Player) sender;

        if(!plugin.isTrading(senderPlayer.getUniqueId(), true)) {
            plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeNotWaiting"));
            return;
        }

        TradeMenu currentTradeMenu = null;
        for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
            if(tradeMenu.getTraderUUID().equals(senderPlayer.getUniqueId()) && tradeMenu.isAwaitingAcceptance() || tradeMenu.getTradeeUUID().equals(senderPlayer.getUniqueId()) && tradeMenu.isAwaitingAcceptance()) {
                currentTradeMenu = tradeMenu;
            }
        }

        if(currentTradeMenu == null) {
            plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeNotWaiting"));
        } else {
            currentTradeMenu.denyTrade();
        }
    }

    private void openSubCommand(CommandSender sender) {
        Player senderPlayer = (Player) sender;

        if(!plugin.isTrading(senderPlayer.getUniqueId(), true)) {
            plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeStillWaiting"));
            return;
        }

        TradeMenu currentTradeMenu = null;
        for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
            if(tradeMenu.getTraderUUID().equals(senderPlayer.getUniqueId()) && tradeMenu.isAwaitingAcceptance() || tradeMenu.getTradeeUUID().equals(senderPlayer.getUniqueId()) && tradeMenu.isAwaitingAcceptance()) {
                plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("tradeWaiting"));
                return;
            }

            currentTradeMenu = tradeMenu;
        }

        if(currentTradeMenu != null) {
            senderPlayer.openInventory(currentTradeMenu.getInventory());
        }
    }
}
