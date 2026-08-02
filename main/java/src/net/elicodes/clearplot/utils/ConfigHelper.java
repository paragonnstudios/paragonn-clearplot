package net.elicodes.clearplot.utils;

import net.elicodes.clearplot.Main;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigHelper {

    public static String msg(String path) {
        return ChatColor.translateAlternateColorCodes('&', Main.getPlugin().messagesConfig.getString("Mensagens." + path));
    }

    public static List<String> msgList(String path) {
        return Main.getPlugin().messagesConfig.getStringList("Mensagens." + path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }

    public static String menu(String path) {
        return ChatColor.translateAlternateColorCodes('&', Main.getPlugin().menusConfig.getString("Menu." + path));
    }

    public static List<String> menuList(String path) {
        return Main.getPlugin().menusConfig.getStringList("Menu." + path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }

    public static boolean menuBoolean(String path) {
        return Main.getPlugin().menusConfig.getBoolean("Menu." + path);
    }

    public static int menuInt(String path) {
        return Main.getPlugin().menusConfig.getInt("Menu." + path);
    }
}
