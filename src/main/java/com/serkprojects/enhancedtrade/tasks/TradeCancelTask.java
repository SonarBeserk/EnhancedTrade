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
        THashSet<TradeMenu> menusToCancel = null;

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
