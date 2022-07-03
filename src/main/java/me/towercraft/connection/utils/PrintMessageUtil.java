package me.towercraft.connection.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PrintMessageUtil {

    public static void printMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
