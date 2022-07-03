package me.towercraft.connection.api;

import me.towercraft.connection.server.TypeConnect;
import org.bukkit.entity.Player;

public interface ServerConnect {

    void connectByServer(Player player, String nameGroupOrServer);

    void connectByServer(Player player, String nameGroupOrServer, TypeConnect typeConnect);

}
