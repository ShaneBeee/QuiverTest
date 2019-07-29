package tk.shanebee.testplugin.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tk.shanebee.testplugin.TestPlugin;
import tk.shanebee.testplugin.utils.Utils;

import java.io.File;
import java.io.IOException;

public class QuiverData {

    private Player player;
    private ItemStack[] contents;
    private int hotbarSlot;
    private Inventory inventory;

    public QuiverData(Player player) {
        this.player = player;
        this.hotbarSlot = 0;
        this.inventory = Bukkit.createInventory(player, 9, Utils.getColString("&3Quiver"));
        loadData();
    }

    public int getSlot() {
        return this.hotbarSlot;
    }

    public void setSlot(int slot) {
        this.hotbarSlot = slot;
    }

    public ItemStack getArrow() {
        if (this.contents[this.hotbarSlot] != null && this.contents[this.hotbarSlot].getType() != Material.AIR) {
            return this.contents[this.hotbarSlot];
        } else {
            for (int i = 0; i < 9; i++) {
                if (this.contents[i] != null && this.contents[i].getType() != Material.AIR) {
                    return this.contents[i];
                }
            }
        }
        return null;
    }

    public void setContents(ItemStack[] items) {
        this.contents = items;
        updateQuiverData();
    }

    public void updateQuiverData() {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getContents()[i] != null) {
                this.inventory.setItem(i, player.getInventory().getContents()[i]);
            }
        }
        saveData();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void saveData() {
        FileConfiguration data = TestPlugin.plugin.getQuiverData();

        for (int i = 0; i < 9; i++) {
            data.set("players." + player.getUniqueId() + "." + i, this.inventory.getItem(i));
        }
        data.set("players." + player.getUniqueId() + ".quiver-inventory", inventory);

        File quiver_file = new File(TestPlugin.plugin.getDataFolder(), "data.yml");
        try {
            data.save(quiver_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        FileConfiguration data = TestPlugin.plugin.getQuiverData();
        for (int i = 0; i < 9; i++) {
            String path = "players." + player.getUniqueId() + "." + i;
            if (data.isSet(path)) {
                this.inventory.setItem(i, data.getItemStack(path));
            }
        }
    }

}












