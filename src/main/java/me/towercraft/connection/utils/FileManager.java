package me.towercraft.connection.utils;

import com.google.common.io.ByteStreams;
import me.towercraft.connection.ConnectionApi;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileManager {

    private final ConnectionApi plugin;
    private Configuration message;

    public FileManager(ConnectionApi plugin) {
        this.plugin = plugin;
        createMessages();
    }

    public void createMessages() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        File file = new File(this.plugin.getDataFolder(), "Messages.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (final InputStream resourceAsStream = this.plugin.getResource("Messages.yml")) {
                    final FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        ByteStreams.copy(resourceAsStream, fileOutputStream);
                        this.message = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "Messages.yml"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return;
            } catch (IOException ex2) {
                throw new RuntimeException("Unable to create config file", ex2);
            }
        }
        message = YamlConfiguration.loadConfiguration(file);
    }

    public String getMSG(String key) {
        return message.getString(key, "Not found String [" + key + "] in Message.yml");
    }

}
