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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeMenu {
    private EnhancedTrade plugin = null;
    private String headerString = "settings.trade.interface.entry.";

    private UUID traderUUID = null;
    private UUID tradeeUUID = null;

    // Range of slots for the trader
    private int traderRangeStart = 0;
    private int traderRangeEnd = 17;

    // Array of reserved slots
    private int[] reservedSlotArray = new int[] {18, 19, 20, 21, 22, 23, 24, 25, 26};

    // Range of slots for the tradee
    private int tradeeRangeStart = 27;
    private int tradeeRangeEnd = 44;

    private ItemStack traderReadinessStack = null;
    private ItemStack tradeeReadinessStack = null;
    private ItemStack tradeInfoStack = null;
    private ItemStack remindPlayerStack = null;
    private ItemStack addOneCurrencyStack = null;
    private ItemStack removeOneCurrencyStack = null;
    private ItemStack addTenCurrencyStack = null;
    private ItemStack removeTenCurrencyStack = null;
    private ItemStack cancelTradeStack = null;

    private Inventory inventory = null;

    private int traderMoney = 0;
    private int tradeeMoney = 0;

    /**
     * Creates an instance of the trading menu
     * @param plugin plugin used for pulling ItemStack settings
     */
    public TradeMenu(EnhancedTrade plugin) {
        this.plugin = plugin;
    }

    private void buildItemStacks() {
        traderReadinessStack = getNewTraderReadinessStack();
        tradeeReadinessStack = getNewTradeeReadinessStack();
        tradeInfoStack = getNewTradeInfoStack();
        remindPlayerStack = getNewRemindPlayerStack();
        addOneCurrencyStack = getNewAddOneCurrencyStack();
        removeOneCurrencyStack = getNewRemoveOneCurrencyStack();
        addTenCurrencyStack = getNewAddTenCurrencyStack();
        removeTenCurrencyStack = getNewRemoveTenCurrencyStack();
        cancelTradeStack = getNewCancelTradeStack();
    }

    /**
     * Call to build inventory
     */
    public void buildInventory() {
        buildItemStacks();

        inventory = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("settings.trade.interface.name")));

        inventory.setItem(reservedSlotArray[0], traderReadinessStack);
        inventory.setItem(reservedSlotArray[1], tradeeReadinessStack);
        inventory.setItem(reservedSlotArray[2], tradeInfoStack);
        inventory.setItem(reservedSlotArray[3], remindPlayerStack);
        inventory.setItem(reservedSlotArray[4], addOneCurrencyStack);
        inventory.setItem(reservedSlotArray[5], removeOneCurrencyStack);
        inventory.setItem(reservedSlotArray[6], addTenCurrencyStack);
        inventory.setItem(reservedSlotArray[7], removeTenCurrencyStack);
        inventory.setItem(reservedSlotArray[8], cancelTradeStack);
    }

    /**
     * Returns the current inventory for the trade menu
     * @return the current inventory for the trade menu
     */
    public Inventory getInventory() {
        if(inventory == null) {
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

    public void handleClick(InventoryClickEvent e) {
        if(!isReservedSlot(e.getSlot())) {return;}

        switch (e.getSlot()) {
            case 18: {
                if(!e.getWhoClicked().getUniqueId().equals(traderUUID)) {break;}

                Wool wool = (Wool) e.getCurrentItem().getData();

                if (wool.getColor() == DyeColor.RED) {
                    wool.setColor(DyeColor.GREEN);
                } else {
                    wool.setColor(DyeColor.RED);
                }

                ItemStack itemStack = wool.toItemStack();
                itemStack.setAmount(1);
                itemStack.setItemMeta(e.getCurrentItem().getItemMeta());

                e.setCurrentItem(itemStack);

                for(HumanEntity humanEntity: e.getInventory().getViewers()) {
                    Player vPlayer = (Player) humanEntity;
                    vPlayer.updateInventory();
                }

                break;
            }
            case 19: {
                if(!e.getWhoClicked().getUniqueId().equals(tradeeUUID)) {break;}

                Wool wool = (Wool) e.getCurrentItem().getData();

                if (wool.getColor() == DyeColor.RED) {
                    wool.setColor(DyeColor.GREEN);
                } else {
                    wool.setColor(DyeColor.RED);
                }

                ItemStack itemStack = wool.toItemStack();
                itemStack.setAmount(1);
                itemStack.setItemMeta(e.getCurrentItem().getItemMeta());

                e.setCurrentItem(itemStack);

                for(HumanEntity humanEntity: e.getInventory().getViewers()) {
                    Player vPlayer = (Player) humanEntity;
                    vPlayer.updateInventory();
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
            }
            case 22: {

            }


            default:
                break;
        }
    }

    /**
     * Returns the ItemStack used for the trader readiness item
     * @return the ItemStack used for the trader readiness item
     */
    public ItemStack getTraderReadinessStack() {
        return traderReadinessStack;
    }

    /**
     * Returns the ItemStack used for the tradee readiness item
     * @return the ItemStack used for the tradee readiness item
     */
    public ItemStack getTradeeReadinessStack() {
        return tradeeReadinessStack;
    }

    /**
     * Returns the ItemStack used for the trade info item
     * @return the ItemStack used for the trade info item
     */
    public ItemStack getTradeInfoStack() {
        return tradeInfoStack;
    }

    /**
     * Returns the ItemStack used for the remind player item
     * @return the ItemStack used for the remind player item
     */
    public ItemStack getRemindPlayerStack() {
        return remindPlayerStack;
    }

    /**
     * Returns the ItemStack used for the remind player item
     * @return the ItemStack used for the remind player item
     */
    public ItemStack getAddOneCurrencyStack() {
        return addOneCurrencyStack;
    }

    /**
     * Returns the ItemStack used for the remove one currency item
     * @return the ItemStack used for the remove one currency item
     */
    public ItemStack getRemoveOneCurrencyStack() {
        return removeOneCurrencyStack;
    }

    /**
     * Returns the ItemStack used for the add ten currency item
     * @return the ItemStack used for the add ten currency item
     */
    public ItemStack getAddTenCurrencyStack() {
        return addTenCurrencyStack;
    }

    /**
     * Returns the ItemStack used for the remove ten currency item
     * @return the ItemStack used for the remove ten currency item
     */
    public ItemStack getRemoveTenCurrencyStack() {
        return removeTenCurrencyStack;
    }

    /**
     * Returns the ItemStack used for the cancel trade item
     * @return the ItemStack used for the cancel trade item
     */
    public ItemStack getCancelTradeStack() {
        return cancelTradeStack;
    }

    /**
     * Returns the default ItemStack used for the trader readiness item
     * @return the default ItemStack used for the trader readiness item
     */
    public ItemStack getNewTraderReadinessStack() {
        Wool wool = new Wool();
        wool.setColor(DyeColor.RED);

        ItemStack itemStack = wool.toItemStack();
        itemStack.setAmount(1);
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

        ItemStack itemStack = wool.toItemStack();
        itemStack.setAmount(1);
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

        for(String loreString: plugin.getConfig().getStringList(headerString + "tradeInfo.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString));
        }

        itemMeta.setLore(loreList);
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

        String currencyName;

        if(plugin.getEconomy() != null) {
            currencyName = plugin.getEconomy().currencyNameSingular();
        } else {
            currencyName = "none";
        }

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "addOneCurrency.name").replace("{currency}", currencyName)));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "addOneCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString.replace("{currency}", currencyName)));
        }

        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Returns the default ItemStack for the remove one currency item
     * @return the default ItemStack for the remove one currency item
     */
    public ItemStack getNewRemoveOneCurrencyStack() {
        ItemStack itemStack = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String currencyName;

        if(plugin.getEconomy() != null) {
            currencyName = plugin.getEconomy().currencyNameSingular();
        } else {
            currencyName = "none";
        }

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "removeOneCurrency.name").replace("{currency}", currencyName)));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "removeOneCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString.replace("{currency}", currencyName)));
        }

        itemMeta.setLore(loreList);
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

        String currencyName;

        if(plugin.getEconomy() != null) {
            currencyName = plugin.getEconomy().currencyNamePlural();
        } else {
            currencyName = "none";
        }

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "addTenCurrency.name").replace("{currency}", currencyName)));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "addTenCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString.replace("{currency}", currencyName)));
        }

        itemMeta.setLore(loreList);
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

        String currencyName;

        if(plugin.getEconomy() != null) {
            currencyName = plugin.getEconomy().currencyNamePlural();
        } else {
            currencyName = "none";
        }

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(headerString + "removeTenCurrency.name").replace("{currency}", currencyName)));

        List<String> loreList = new ArrayList<String>();

        for(String loreString: plugin.getConfig().getStringList(headerString + "removeTenCurrency.lore")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreString.replace("{currency}", currencyName)));
        }

        itemMeta.setLore(loreList);
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

    /**
     * Sets the UUID of the trader
     * @param UUID the UUID to set the trader to
     */
    public void setTraderUUID(UUID UUID) {
        traderUUID = UUID;
        buildInventory();
    }

    /**
     * Sets the UUID of the tradee
     * @param UUID the UUID to set the tradee to
     */
    public void setTradeeUUID(UUID UUID) {
        tradeeUUID = UUID;
        buildInventory();
    }
}
