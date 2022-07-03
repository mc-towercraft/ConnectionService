package me.towercraft.connection.command;

import me.towercraft.connection.server.ServerConnectApi;
import me.towercraft.connection.server.TypeConnect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConnectionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.split("_")[0].equalsIgnoreCase("connect"))
            if (args.length > 1) {
                TypeConnect typeConnect = null;
                try {
                    typeConnect = TypeConnect.valueOf(args[1]);
                } catch (Exception e) {

                }

                if (typeConnect != null)
                    ServerConnectApi.getInstance().connectByServer((Player) sender, args[0], typeConnect);
                else
                    ServerConnectApi.getInstance().connectByServer((Player) sender, args[0]);
            }
        return true;
    }
}
