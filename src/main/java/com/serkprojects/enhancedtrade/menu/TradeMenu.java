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

package com.serkprojects.enhancedtrade.menu;

import com.serkprojects.enhancedtrade.EnhancedTrade;
import gnu.trove.set.hash.THashSet;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class TradeMenu {
    private EnhancedTrade plugin = null;
    private String headerString = "settings.trade.interface.entry.";

    private UUID traderUUID = null;
    private UUID tradeeUUID = null;

    private boolean built = false;

    // Range of slots for the trader
    private int traderRangeStart = 0;
    private int traderRangeEnd = 17;

    // Array of reserved slots
    private int[] reservedSlotArray = new int[] {18, 19, 20, 21, 22, 23, 24, 25, 26};

    // Range of slots for the tradee
    private int tradeeRangeStart = 27;
    private int tradeeRangeEnd = 44;

    private Inventory inventory = null;

    private boolean awaitingAcceptance = true;

    private int traderMoney = 0;
    private int tradeeMoney = 0;

    /**
     * Creates an instance of the trading menu
     * @param plugin plugin used for pulling ItemStack settings
     */
    public TradeMenu(EnhancedTrade plugin) {
        this.plugin = plugin;

        inventory = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("settings.trade.interface.name")));
    }

    /**
     * Call to build inventory
     */
    private void buildInventory() {
        inventory.setItem(reservedSlotArray[0], getNewTraderReadinessStack());
        inventory.setItem(reservedSlotArray[1], getNewTradeeReadinessStack());
        inventory.setItem(reservedSlotArray[2], getNewTradeInfoStack());
        inventory.setItem(reservedSlotArray[3], getNewRemindPlayerStack());
        inventory.setItem(reservedSlotArray[4], getNewAddOneCurrencyStack());
        inventory.setItem(reservedSlotArray[5], getNewRemoveOneCurrencyStack());
        inventory.setItem(reservedSlotArray[6], getNewAddTenCurrencyStack());
        inventory.setItem(reservedSlotArray[7], getNewRemoveTenCurrencyStack());
        inventory.setItem(reservedSlotArray[8], getNewCancelTradeStack());

        built = true;
    }

    /**
     * Returns the current inventory for the trade menu
     * @return the current inventory for the trade menu
     */
    public Inventory getInventory() {
        if(!built) {
            buildInventory();
        }

        return inventory;
    }

    /**
     * Returns the trader's UUID
     *
     * @return the trader's UUID
     */
    public UUID getTraderUUID() {
        return traderUUID;
    }

    /**
     * Returns the tradee's UUID
     * @return the tradee's UUID
     */
    public UUID getTradeeUUID() {
        return tradeeUUID;
    }

    /**
     * Returns if the trade needs to be accepted first
     * @return if the trade needs to be accepted first
     */
    public boolean isAwaitingAcceptance() {
        return awaitingAcceptance;
    }

    /**
     * Checks if a slot is for a reserved menu item
     *
     * @param slot the slot number to check
     * @return if a slot is for a reserved menu item
     */
    public boolean isReservedSlot(int slot) {
        for (int aSlotArray : reservedSlotArray) {
            if (slot == aSlotArray) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns if a slot belongs to the trader
     * @param slot the slot to check
     * @return if a slot belongs to the trader
     */
    public boolean isTraderSlot(int slot) {
        return slot >= traderRangeStart && slot <= traderRangeEnd;
    }

    /**
     * Returns if a slot belongs to the tradee
     * @param slot the slot to check
     * @return if a slot belongs to the tradee
     */
    public boolean isTradeeSlot(int slot) {
        return slot >= tradeeRangeStart && slot <= tradeeRangeEnd;
    }

    /**
     * Checks if the trade is complete
     * @return if the the trade is complete
     */
    public boolean isTradeComplete() {
        if (inventory.getItem(reservedSlotArray[0]) == null || inventory.getItem(reservedSlotArray[1]) == null) {return false;}
        if (!inventory.getItem(reservedSlotArray[0]).hasItemMeta() || !inventory.getItem(reservedSlotArray[1]).hasItemMeta()) {return false;}

        Wool traderWool = (Wool) inventory.getItem(reservedSlotArray[0]).getData();
        Wool tradeeWool = (Wool) inventory.getItem(reservedSlotArray[1]).getData();

        return tradeeWool.getColor() == DyeColor.GREEN && traderWool.getColor() == DyeColor.GREEN;
    }

    /**
     * Called when a reserved slot menu item is clicked
     * @param e the inventory click event related to the click
     */
    public void handleClick(InventoryClickEvent e) {
        if(!isReservedSlot(e.getSlot())) {return;}

        switch (e.getSlot()) {
            case 18: {
                if(!e.getWhoClicked().getUniqueId().equals(traderUUID)) {break;}

                toggleReadinessStack(e);

                if(isTradeComplete()) {
                    plugin.getTradeTickDownTask().addTickingDownTradeMenu(this, plugin.getConfig().getInt("settings.trade.tickDownCounter"));
                    setAllMenuItemAmounts(plugin.getConfig().getInt("settings.trade.tickDownCounter"));
                } else {
                    plugin.getTradeTickDownTask().removeTickingDownTradeMenu(this);
                    setAllMenuItemAmounts(1);
                }

                break;
            }
            case 19: {
                if(!e.getWhoClicked().getUniqueId().equals(tradeeUUID)) {break;}

                toggleReadinessStack(e);

                if(isTradeComplete()) {
                    plugin.getTradeTickDownTask().addTickingDownTradeMenu(this, plugin.getConfig().getInt("settings.trade.tickDownCounter"));
                    setAllMenuItemAmounts(plugin.getConfig().getInt("settings.trade.tickDownCounter"));
                } else {
                    plugin.getTradeTickDownTask().removeTickingDownTradeMenu(this);
                    setAllMenuItemAmounts(1);
                }

                break;
            }
            case 21: {
                if(e.getWhoClicked().getUniqueId().equals(traderUUID)) {
                    if(plugin.getServer().getPlayer(tradeeUUID) != null) {
                        plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(tradeeUUID), true, plugin.getLanguage().getMessage("stillTrading"));
                    }
                } else if(e.getWhoClicked().getUniqueId().equals(tradeeUUID)) {
                    if(plugin.getServer().getPlayer(traderUUID) != null) {
                        plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(traderUUID), true, plugin.getLanguage().getMessage("stillTrading"));
                    }
                }

                break;
            }
            case 22: {
                if(plugin.getEconomy() != null) {
                    int cost;

                    if(e.isShiftClick()) {
                        cost = 10;
                    } else {
                        cost = 1;
                    }

                    if(!plugin.getEconomy().has((OfflinePlayer) e.getWhoClicked(), cost)) {
                        plugin.getMessaging().sendMessage(e.getWhoClicked(), true, plugin.getLanguage().getMessage("canNotAfford"));
                        return;
                    }

                    plugin.getEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), cost);

                    if(traderUUID != null && traderUUID.equals(e.getWhoClicked().getUniqueId())) {
                        traderMoney = traderMoney + cost;
                    } else if(tradeeUUID != null && tradeeUUID.equals(e.getWhoClicked().getUniqueId())) {
                        tradeeMoney = tradeeMoney + cost;
                    }
                }

                refreshTraderInfo(e);
                break;
            }
            case 23: {
                if(plugin.getEconomy() != null) {
                    int returned;

                    if(e.isShiftClick()) {
                        returned = 10;
                    } else {
                        returned = 1;
                    }

                    if(traderUUID != null && traderUUID.equals(e.getWhoClicked().getUniqueId())) {
                        if(traderMoney < returned) {
                            plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), traderMoney);
                            traderMoney = 0;
                        } else {
                            plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), returned);
                            traderMoney = traderMoney - returned;
                        }

                        traderMoney = traderMoney - returned;
                    } else if(tradeeUUID != null && tradeeUUID.equals(e.getWhoClicked().getUniqueId())) {
                        if(tradeeMoney < returned) {
                            plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), tradeeMoney);
                            tradeeMoney = 0;
                        } else {
                            plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), returned);
                            tradeeMoney = tradeeMoney - returned;
                        }
                    }
                }

                refreshTraderInfo(e);
                break;
            }
            case 24: {
                if(plugin.getEconomy() != null) {
                    int cost;

                    if(e.isShiftClick()) {
                        cost = 100;
                    } else {
                        cost = 10;
                    }

                    if(!plugin.getEconomy().has((OfflinePlayer) e.getWhoClicked(), cost)) {
                        plugin.getMessaging().sendMessage(e.getWhoClicked(), true, plugin.getLanguage().getMessage("canNotAfford"));
                        return;
                    }

                    plugin.getEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), cost);

                    if(traderUUID != null && traderUUID.equals(e.getWhoClicked().getUniqueId())) {
                        traderMoney = traderMoney + cost;
                    } else if(tradeeUUID != null && tradeeUUID.equals(e.getWhoClicked().getUniqueId())) {
                        tradeeMoney = tradeeMoney + cost;
                    }
                }

                refreshTraderInfo(e);
                break;
            }
            case 25: {
                int returned;

                if(e.isShiftClick()) {
                    returned = 100;
                } else {
                    returned = 10;
                }

                if(traderUUID != null && traderUUID.equals(e.getWhoClicked().getUniqueId())) {
                    if(traderMoney < returned) {
                        plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), traderMoney);
                        traderMoney = 0;
                    } else {
                        plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), returned);
                        traderMoney = traderMoney - returned;
                    }

                    traderMoney = traderMoney - returned;
                } else if(tradeeUUID != null && tradeeUUID.equals(e.getWhoClicked().getUniqueId())) {
                    if(tradeeMoney < returned) {
                        plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), tradeeMoney);
                        tradeeMoney = 0;
                    } else {
                        plugin.getEconomy().depositPlayer((OfflinePlayer) e.getWhoClicked(), returned);
                        tradeeMoney = tradeeMoney - returned;
                    }
                }

                refreshTraderInfo(e);
                break;
            }
            case 26: {
                cancelTrade();
                break;
            }
            default:
                break;
        }
    }

    /**
     * Returns the default ItemStack used for the trader readiness item
     * @return the default ItemStack used for the trader readiness item
     */
    public ItemStack getNewTraderReadinessStack() {
        Wool wool = new Wool();
        wool.setColor(DyeColor.RED);

        ItemStack itemStack = wool.toItemStack(1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        Player player = plugin.getServer().getPlayer(traderUUID);

        String name;

        if(player != null) {
            name = player.getName();
        } else {
            name = "none";
        }

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "toggleReadinessTrader.name").replace("{name}", name)));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "toggleReadinessTrader.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack used for the tradee readiness item
     * @return the default ItemStack used for the tradee readiness item
     */
    public ItemStack getNewTradeeReadinessStack() {
        Wool wool = new Wool();
        wool.setColor(DyeColor.RED);

        ItemStack itemStack = wool.toItemStack(1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        Player player = plugin.getServer().getPlayer(tradeeUUID);

        String name;

        if(player != null) {
            name = player.getName();
        } else {
            name = "none";
        }

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "toggleReadinessTradee.name").replace("{name}", name)));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "toggleReadinessTradee.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack used for the trade info item
     * @return the default ItemStack used for the trade info item
     */
    public ItemStack getNewTradeInfoStack() {
        ItemStack itemStack = new ItemStack(Material.SIGN);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "tradeInfo.name")));

        List<String> loreList = new ArrayList<String>();

        loreList.add("Current Trade Information:");
        loreList.add("Trader Money: " + "{currency-" + traderMoney + "}");
        loreList.add("Tradee Money: " + "{currency-" + tradeeMoney + "}");

        itemMeta.setLore(formatCurrencyVariables(loreList));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack used for the remind player stack
     * @return the default ItemStack used for the remind player stack
     */
    public ItemStack getNewRemindPlayerStack() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "remindPlayer.name")));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "remindPlayer.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack for the add one currency item
     *
     * @return the default ItemStack for the add one currency item
     */
    public ItemStack getNewAddOneCurrencyStack() {
        ItemStack itemStack = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(formatCurrencyVariable(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "addOneCurrency.name"))));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "addOneCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(formatCurrencyVariables(loreList));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack for the remove one currency item
     * @return the default ItemStack for the remove one currency item
     */
    public ItemStack getNewRemoveOneCurrencyStack() {
        ItemStack itemStack = new ItemStack(Material.GHAST_TEAR);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(formatCurrencyVariable(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "removeOneCurrency.name"))));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "removeOneCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(formatCurrencyVariables(loreList));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack for the add ten currency item
     * @return the default ItemStack for the add ten currency item
     */
    public ItemStack getNewAddTenCurrencyStack() {
        ItemStack itemStack = new ItemStack(Material.GOLD_INGOT);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(formatCurrencyVariable(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "addTenCurrency.name"))));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "addTenCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(formatCurrencyVariables(loreList));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack for the remove ten currency item
     * @return the default ItemStack for the remove ten currency item
     */
    public ItemStack getNewRemoveTenCurrencyStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_INGOT);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(formatCurrencyVariable(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "removeTenCurrency.name"))));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "removeTenCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(formatCurrencyVariables(loreList));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack for the cancel trade item
     * @return the default ItemStack for the cancel trade item
     */
    public ItemStack getNewCancelTradeStack() {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "cancelTrade.name")));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "cancelTrade.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private THashSet<ItemStack> getTraderItemStacks() {
        THashSet<ItemStack> itemStackList = new THashSet<>();

        if(inventory == null) {return itemStackList;}

        for(int i = 0; i < inventory.getContents().length; i++) {
            if(isTraderSlot(i)) {
                if(inventory.getItem(i)!= null && inventory.getItem(i).getType() != Material.AIR) {
                    itemStackList.add(inventory.getItem(i));
                }
            }
        }

        return itemStackList;
    }

    private THashSet<ItemStack> getTradeeItemStacks() {
        THashSet<ItemStack> itemStackList = new THashSet<>();

        if(inventory == null) {return itemStackList;}

        for(int i = 0; i < inventory.getContents().length; i++) {
            if(isTradeeSlot(i)) {
                if(inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                    itemStackList.add(inventory.getItem(i));
                }
            }
        }

        return itemStackList;
    }

    /**
     * Sets the UUID of the trader
     * @param UUID the UUID to set the trader to
     */
    public void setTraderUUID(UUID UUID) {
        traderUUID = UUID;
    }

    /**
     * Sets the UUID of the tradee
     * @param UUID the UUID to set the tradee to
     */
    public void setTradeeUUID(UUID UUID) {
        tradeeUUID = UUID;
    }

    /**
     * Sets all menu items to a given amount, good for countdowns
     * @param amount the amount to set menu items to
     */
    public void setAllMenuItemAmounts(int amount) {
        for(int slot: reservedSlotArray) {
            ItemStack itemStack = inventory.getItem(slot);
            itemStack.setAmount(amount);
            inventory.setItem(slot, itemStack);
        }

        for(HumanEntity humanEntity: inventory.getViewers()) {
            if(humanEntity instanceof Player) {
                ((Player) humanEntity).updateInventory();
            }
        }
    }

    private String formatCurrencyVariable(String string) {
        String[] splitString = string.split(" +");
        for(String entry: splitString) {
            if (entry.startsWith("{currency")) {
                string = string.replace(entry, formatCurrency(Integer.parseInt(entry.replaceAll("[^1-9]", ""))));
            }
        }

        return string;
    }

    private List<String> formatCurrencyVariables(List<String> stringList) {
        List<String> formattedVariables = new ArrayList<String>();

        for(String entry: stringList) {
            String[] splitString = entry.split(" +");

            for(String splitStringEntry: splitString) {
                if (entry.startsWith("{currency")) {
                    entry = entry.replace(splitStringEntry, formatCurrency(Integer.parseInt(splitStringEntry.replaceAll("[^1-9]", ""))));
                }
            }

            formattedVariables.add(entry);
        }

        return formattedVariables;
    }

    private String formatCurrency(int amount) {
        return plugin.getEconomy().format(amount);
    }

    private void refreshTraderInfo(InventoryClickEvent e) {
        ItemStack itemStack = e.getInventory().getItem(reservedSlotArray[2]);
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> loreList = new ArrayList<String>();

        loreList.add("Current Trade Information:");
        loreList.add("Trader Money: " + "{currency-" + traderMoney + "}");
        loreList.add("Tradee Money: " + "{currency-" + tradeeMoney + "}");

        itemMeta.setLore(formatCurrencyVariables(loreList));

        itemStack.setItemMeta(itemMeta);

        e.getView().getTopInventory().setItem(reservedSlotArray[2], itemStack);
    }

    private void toggleReadinessStack(InventoryClickEvent e) {
        if(e.getCurrentItem() == null || e.getCurrentItem().getType() != Material.WOOL) {return;}

        Wool wool = (Wool) e.getCurrentItem().getData();

        if (wool.getColor() == DyeColor.RED) {
            wool.setColor(DyeColor.GREEN);
        } else {
            wool.setColor(DyeColor.RED);
        }

        ItemStack itemStack = wool.toItemStack(1);
        itemStack.setItemMeta(e.getCurrentItem().getItemMeta());

        e.getView().getTopInventory().setItem(e.getRawSlot(), itemStack);
    }

    /**
     * Called when the trade is accepted
     */
    public void acceptTrade() {
        awaitingAcceptance = false;
    }

    /**
     * Called when the trade is denied
     */
    public void denyTrade() {
        plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(tradeeUUID), true, plugin.getLanguage().getMessage("tradeDenied"));
        plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(traderUUID), true, plugin.getLanguage().getMessage("tradeHasBeenDenied"));
        plugin.removeActiveTrade(this);
    }

    /**
     * Called to cancel a trade
     */
    public void cancelTrade() {
        final List<HumanEntity> viewers = inventory.getViewers();

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for (HumanEntity viewer : viewers) {
                    if(viewer == null) {continue;}

                    if(viewer.getUniqueId().equals(traderUUID)) {
                        for (ItemStack itemStack : getTraderItemStacks()) {
                            Item item = viewer.getWorld().dropItem(viewer.getLocation(), itemStack);
                            item.setMetadata("p-protected", new FixedMetadataValue(plugin, traderUUID));
                        }
                    }

                    if(viewer.getUniqueId().equals(tradeeUUID)) {
                        for (ItemStack itemStack : getTradeeItemStacks()) {
                            Item item = viewer.getWorld().dropItem(viewer.getLocation(), itemStack);
                            item.setMetadata("p-protected", new FixedMetadataValue(plugin, tradeeUUID));
                        }
                    }

                    viewer.closeInventory();
                    plugin.getMessaging().sendMessage(viewer, true, plugin.getLanguage().getMessage("tradeCancelled"));
                }
            }
        }, 1);

        plugin.removeActiveTrade(this);
    }

    /**
     * Called to complete the trade
     */
    public void completeTrade() {
        if(!isTradeComplete()) {return;}

        final List<HumanEntity> viewers = inventory.getViewers();

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for (HumanEntity humanEntity : viewers) {
                    humanEntity.closeInventory();
                }
            }
        }, 1);

        if(traderMoney > 0) {
            plugin.getEconomy().depositPlayer(plugin.getServer().getPlayer(traderUUID), tradeeMoney);
            plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(traderUUID), true, plugin.getLanguage().getMessage("tradeReceivedMoney").replace("{amount}", traderMoney + formatCurrency(traderMoney)));
        }

        if(tradeeMoney > 0) {
            plugin.getEconomy().depositPlayer(plugin.getServer().getPlayer(tradeeUUID), traderMoney);
            plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(tradeeUUID), true, plugin.getLanguage().getMessage("tradeReceivedMoney").replace("{amount}", tradeeMoney + formatCurrency(tradeeMoney)));
        }

        HashMap<Integer, ItemStack> remainingTraderStacks = plugin.getServer().getPlayer(traderUUID).getInventory().addItem(getTradeeItemStacks().toArray(new ItemStack[]{}));
        HashMap<Integer, ItemStack> remainingTradeeStacks = plugin.getServer().getPlayer(tradeeUUID).getInventory().addItem(getTraderItemStacks().toArray(new ItemStack[]{}));

        for (ItemStack traderItemStack : remainingTraderStacks.values()) {
            Player tradee = plugin.getServer().getPlayer(tradeeUUID);
            tradee.getWorld().dropItem(tradee.getLocation(), traderItemStack);
        }

        for (ItemStack tradeeItemStack : remainingTradeeStacks.values()) {
            Player trader = plugin.getServer().getPlayer(traderUUID);
            trader.getWorld().dropItem(trader.getLocation(), tradeeItemStack);
        }

        if(remainingTraderStacks.size() > 0) {
            plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(traderUUID), true, plugin.getLanguage().getMessage("tradeItemsDropped"));
        }

        if(remainingTradeeStacks.size() > 0) {
            plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(tradeeUUID), true, plugin.getLanguage().getMessage("tradeItemsDropped"));
        }

        plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(traderUUID), true, plugin.getLanguage().getMessage("tradeComplete"));
        plugin.getMessaging().sendMessage(plugin.getServer().getPlayer(tradeeUUID), true, plugin.getLanguage().getMessage("tradeComplete"));
        plugin.removeActiveTrade(this);
    }
}