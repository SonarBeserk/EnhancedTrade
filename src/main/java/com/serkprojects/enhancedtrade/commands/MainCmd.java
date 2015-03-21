/**
 * *********************************************************************************************************************
 * SerkCore - Provides the core for plugins made by SerkProjects
 * =====================================================================================================================
 * Copyright (C) 2014 by SonarBeserk, SerkProjects
 * https://gitlab.com/serkprojects/serkcore
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
import org.bukkit.entity.HumanEntity;

public class MainCmd implements CommandExecutor {
    private EnhancedTrade plugin = null;

    public MainCmd(EnhancedTrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            helpSubCommand(sender);
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                helpSubCommand(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                reloadSubCommand(sender);
                return true;
            }
        }

        helpSubCommand(sender);
        return true;
    }

    private void helpSubCommand(CommandSender sender) {
        plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("usageMain").replace("{name}", plugin.getDescription().getName()));
    }

    private void reloadSubCommand(CommandSender sender) {
        if (!sender.hasPermission(plugin.getPermissionPrefix() + ".commands.reload")) {
            plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("noPermission"));
            return;
        }

        plugin.getLanguage().reload();
        plugin.getData().reload();
        plugin.reloadConfig();

        for(TradeMenu tradeMenu: plugin.getActiveTrades()) {
            for(HumanEntity humanEntity: tradeMenu.getInventory().getViewers()) {
                humanEntity.closeInventory();
            }

            tradeMenu.buildInventory();

            for(HumanEntity humanEntity: tradeMenu.getInventory().getViewers()) {
                humanEntity.openInventory(tradeMenu.getInventory());
            }
        }

        plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("reloaded"));
        return;
    }
}
