package net.elicodes.clearplot.utils;

import net.elicodes.clearplot.Main;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBar {

    public static void send(Player player, String text) {
        if(player != null) {
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replace("&", "§") + "\"}"), (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static void send(Player player, String text, int duration) {
        if(player != null) {
            new BukkitRunnable() {
                int s = 0;
                public void run() {
                    if(s >= duration) cancel();
                    PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replace("&", "§") + "\"}"), (byte) 2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    s++;
                }
            }.runTaskTimerAsynchronously(Main.getPlugin(), 0L, duration * 20L);
        }
    }

    public static void sendAll(String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replace("&", "§") + "\"}"), (byte) 2);
        for (Player player : Bukkit.getOnlinePlayers()) ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendAll(String text, int duration) {
        new BukkitRunnable() {
            int s = 0;
            public void run() {
                if(s >= duration) cancel();
                PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replace("&", "§") + "\"}"), (byte) 2);
                for (Player player : Bukkit.getOnlinePlayers()) ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                s++;
            }
        }.runTaskTimerAsynchronously(Main.getPlugin(), 0L, duration * 20L);
    }
}
