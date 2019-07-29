package tk.shanebee.testplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.shanebee.testplugin.TestPlugin;
import tk.shanebee.testplugin.data.PlayerData;
import tk.shanebee.testplugin.data.QuiverData;
import tk.shanebee.testplugin.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Quiver implements Listener {

    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private Map<UUID, QuiverData> quiverData = new HashMap<>();
    private Map<UUID, Inventory> inventoryMap = new HashMap<>();

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking() && canActivateQuiver(player)) {
            playerDataMap.put(player.getUniqueId(), new PlayerData(player));
            clearInventory(player);
            if (quiverData.containsKey(player.getUniqueId())) {
                loadQuiverHotbar(player);
            }
            else {
                quiverData.put(player.getUniqueId(), new QuiverData(player));
                loadQuiverHotbar(player);
            }
        }
        if (!event.isSneaking()) {
            if (playerDataMap.containsKey(player.getUniqueId())) {
                if (hasQuiverData(player)) {
                    getQuiverData(player).updateQuiverData();
                } else {
                    quiverData.put(player.getUniqueId(), new QuiverData(player));
                }
                restoreInventory(player);
            }
        }
    }

    @EventHandler
    private void onOpenQuiver(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (isQuiverMainHand(player)) {
                if (hasQuiverData(player)) {
                    player.openInventory(getQuiverData(player).getInventory());
                } else {
                    quiverData.put(player.getUniqueId(), new QuiverData(player));
                    player.openInventory(getQuiverData(player).getInventory());
                }
            }
            if (canActivateQuiver(player)) {
                ItemStack arrow = getQuiverData(player).getArrow();
                if (arrow != null) {
                    player.getInventory().setItemInOffHand(arrow);
                    arrow.setAmount(arrow.getAmount() - 1);
                    getQuiverData(player).updateQuiverData();
                } else {
                    event.setCancelled(true);
                    ItemStack bow = player.getInventory().getItemInMainHand().clone();
                    player.getInventory().setItemInMainHand(null);
                    Bukkit.getScheduler().runTaskLater(TestPlugin.plugin, () -> {
                        player.getInventory().setItemInMainHand(bow);
                        player.updateInventory();
                    }, 2);
                }
            }
        }
    }

    @EventHandler
    private void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            if (hasQuiverData(player)) {
                player.getInventory().setItemInOffHand(getQuiver(true));
            }
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = ((Player) event.getPlayer());
            Location loc = player.getLocation();
            if (hasQuiverData(player) && event.getInventory() == getQuiverData(player).getInventory()) {
                for (int i = 0; i < 9; i++) {
                    if (event.getInventory().getContents()[i] == null) continue;
                    if (!Tag.ITEMS_ARROWS.isTagged(event.getInventory().getContents()[i].getType())) {
                        loc.getWorld().dropItem(loc, event.getInventory().getContents()[i]);
                        event.getInventory().getContents()[i].setType(Material.AIR);
                    }
                }
                if (hasQuiverData(player)) {
                    getQuiverData(player).saveData();
                }
            }
        }
    }

    @EventHandler
    private void onPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            if (isQuiverActivated(player)) {
                if (!Tag.ITEMS_ARROWS.isTagged(event.getItem().getItemStack().getType())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isQuiverMainHand(Player player) {
        ItemStack main = player.getInventory().getItemInMainHand();
        if (main.getType() == Material.STICK) {
            if (main.getItemMeta().hasCustomModelData()) {
                return main.getItemMeta().getCustomModelData() == 10;
            }
        }
        return false;
    }

    private boolean isQuiverActivated(Player player) {
        ItemStack off = player.getInventory().getItemInOffHand();
        if (off.getType() == Material.STICK) {
            if (off.getItemMeta().hasCustomModelData()) {
                if (off.getItemMeta().getCustomModelData() == 10) {
                    return player.isSneaking();
                }
            }
        }
        return false;
    }

    private boolean canActivateQuiver(Player player) {
        if (player.getInventory().getItemInMainHand().getType() == Material.BOW) {
            return player.getInventory().getItemInOffHand().getType() == Material.STICK;
        }
        return false;
    }

    private void clearInventory(Player player) {
        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, null);
        }
    }

    private ItemStack getQuiver(boolean loaded) {
        ItemStack quiver = new ItemStack(Material.STICK);
        ItemMeta meta = quiver.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Utils.getColString("&3Quiver"));
        meta.setCustomModelData(loaded ? 10 : 11);
        quiver.setItemMeta(meta);
        return quiver;
    }

    private void loadQuiverHotbar(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = getQuiverData(player).getInventory().getItem(i);
            player.getInventory().setItem(i, item);
        }
    }

    private boolean hasQuiverData(Player player) {
        return quiverData.containsKey(player.getUniqueId());
    }

    private QuiverData getQuiverData(Player player) {
        return quiverData.get(player.getUniqueId());
    }

    private void restoreInventory(Player player) {
        if (playerDataMap.containsKey(player.getUniqueId())) {
            player.getInventory().clear();
            playerDataMap.get(player.getUniqueId()).restoreInventory();
            playerDataMap.remove(player.getUniqueId());
        }
    }

}
