package tk.shanebee.testplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {

    public static void sendColoredMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static String getColString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
