package me.towercraft.connection;

import me.towercraft.connection.command.ConnectionCommand;
import me.towercraft.connection.server.ServerConnectApi;
import me.towercraft.connection.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConnectionApi extends JavaPlugin {

    public static ConnectionApi plugin;
    private FileManager fileManager;

    @Override
    public void onEnable() {

        //TODO Проверка на наличие клауднета

        plugin = this;
        fileManager = new FileManager(this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getCommand("connect").setExecutor(new ConnectionCommand());
        ServerConnectApi.getInstance();
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin, "BungeeCord");
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
