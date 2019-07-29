package tk.shanebee.testplugin.data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {

    private Player player;
    private ItemStack[] contents;
    private ItemStack[] armor;
    private int hotbarSlot;

    public PlayerData(Player player) {
        this.player = player;
        this.contents = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.hotbarSlot = player.getInventory().getHeldItemSlot();
    }

    public void restoreInventory() {
        player.getInventory().setContents(this.contents);
        player.getInventory().setHeldItemSlot(this.hotbarSlot);
    }

    public int getSlot() {
        return this.hotbarSlot;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public void setContents(ItemStack[] items) {
        this.contents = items;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

}
