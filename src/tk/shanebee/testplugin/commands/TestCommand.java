package tk.shanebee.testplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.shanebee.testplugin.utils.Utils;

public class TestCommand implements CommandExecutor {

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender);
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.AQUA + "Quiver");
            meta.setCustomModelData(10);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
        } else {
            Utils.sendColoredMessage(sender, "&cThis command can only be executed by a player");
        }
        return true;
    }

}
