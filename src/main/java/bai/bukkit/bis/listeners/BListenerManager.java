package bai.bukkit.bis.listeners;

import bai.bukkit.bis.BaiInvStols;
import bai.bukkit.bis.configs.BConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class BListenerManager implements Listener {

    private static Map<Player, Long> player_times = new HashMap<>();

    public static BListenerManager getNewInstance() {
        return new BListenerManager();
    }

    public static void register() {
        Bukkit.getPluginManager().registerEvents(getNewInstance(), BaiInvStols.getInstance());
    }

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInventory player_inv = player.getInventory();
        for (Integer key : BConfigManager.main_config.Stols.keySet()) {
            if (player_inv.getItem(key) == null) {
                player_inv.setItem(key, BConfigManager.main_config.getItem(key));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getView().getPlayer();
        int slot = event.getSlot();
        int rawslot = event.getRawSlot();
        InventoryType inventoryType = event.getView().getType();
        InventoryType.SlotType slotType = event.getView().getSlotType(rawslot);
        BaiInvStols.debug("当前物品栏类型: " + inventoryType);
        BaiInvStols.debug("当前点击的格子类型: " + slotType);
        BaiInvStols.debug("当前点击的格子的ID: " + slot);
        BaiInvStols.debug("当前点击的格子真实ID: " + rawslot);
        if (!BConfigManager.main_config.getStolsInt().contains(slot)) {
            return;
        }
        if (inventoryType != InventoryType.CRAFTING) {
            if ((slot >= 0 && slot <= 8) && slotType == InventoryType.SlotType.QUICKBAR && BConfigManager.main_config.getStolsInt().contains(slot))
                event.setCancelled(true);
            if (slot != rawslot)
                event.setCancelled(true);
            return;
        }
        InventoryAction action = event.getAction();
        switch (action) {
            case SWAP_WITH_CURSOR: {
                final ItemStack player_item = event.getCursor();
                final ItemStack inv_item = event.getCurrentItem();
                if (!BConfigManager.main_config.is(player_item, slot)) {
                    event.setCancelled(true);
                    return;
                }
                String lore = BConfigManager.main_config.getItemLore(player_item);
                boolean unique = BConfigManager.main_config.Lores.get(lore).unique;
                if (unique) {
                    if (BConfigManager.main_config.is_unique(player, lore)) {
                        Long time = System.currentTimeMillis();
                        String mes = BConfigManager.main_config.getMess("Unique").replace("&{Unique}", lore);
                        if (player_times.containsKey(player)) {
                            if (time >= player_times.get(player))
                                player.sendMessage(mes);
                            event.setCancelled(true);
                            return;
                        } else {
                            player.sendMessage(mes);
                            player_times.put(player, time + 1000);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                final ItemStack p_item = BConfigManager.main_config.getItem(slot);
                event.setCurrentItem(player_item);
                if (p_item.isSimilar(inv_item)) {
                    event.setCursor(null);
                } else {
                    event.setCursor(inv_item);
                    String inv_lore = BConfigManager.main_config.getItemLore(inv_item);
                    if (BConfigManager.main_config.is_unique(player, inv_lore)) {
                        BConfigManager.main_config.dle_unique(player, inv_lore);
                    }
                }
                event.setCancelled(true);
                if (unique) {
                    BConfigManager.main_config.add_unique(player, lore, slot);
                }
                return;
            }
            case PICKUP_ALL: {
                final ItemStack inv_item = event.getCurrentItem();
                final ItemStack p_item = BConfigManager.main_config.getItem(slot);
                if (p_item.isSimilar(inv_item)) {
                    event.setCancelled(true);
                    return;
                }
                event.setCurrentItem(p_item);
                event.setCursor(inv_item);
                String inv_lore = BConfigManager.main_config.getItemLore(inv_item);
                if (BConfigManager.main_config.is_unique(player, inv_lore)) {
                    BConfigManager.main_config.dle_unique(player, inv_lore);
                }
                event.setCancelled(true);
                return;
            }
            default: {
                event.setCancelled(true);
                return;
            }
        }
    }

}
