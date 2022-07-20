package me.towercraft.connection.server;

import de.dytanic.cloudnet.wrapper.Wrapper;
import me.towercraft.connection.api.ServerConnect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static me.towercraft.connection.ConnectionApi.plugin;
import static me.towercraft.connection.utils.PrintMessageUtil.printMessage;

public class ServerConnectApi implements ServerConnect {

    private static ServerConnect instance;

    public static ServerConnect getInstance() {
        if (instance == null) {
            instance = new ServerConnectApi();
        }
        return instance;
    }

    private ServerConnectApi() {
        init();
    }

    private int countRetryReconnect;
    private Map<String, String> mapConnections;
    private Map<String, BukkitRunnable> mapConnectionsRunnable;

    private void init() {
        countRetryReconnect = plugin.getConfig().getInt("General.countRetryConnect", 10);
        mapConnections = new ConcurrentHashMap<>();
        mapConnectionsRunnable = new ConcurrentHashMap<>();
    }

    private void connect(Player player, String pieceTypeServer, TypeConnect typeConnect, int nowReconnect) {
        if (!player.isOnline())
            return;

        List<ServerModel> servers = new ArrayList<>(InfoServersApi.getInstance().getServers());

        servers = servers
                .stream()
                .filter(s -> s.getName().contains(pieceTypeServer))
                .sorted(Comparator.comparing(ServerModel::getNowPlayer))
                .collect(Collectors.toList());

        if (servers.size() < 1) {
            printMessage(player, plugin.getFileManager().getMSG("Connect.tryReconnect")
                    .replace("%now%", nowReconnect + "")
                    .replace("%all%", countRetryReconnect + "")
            );

            final int finalNowReconnect = nowReconnect;
            if (mapConnections.get(player.getName()) == null && nowReconnect == 0) {
                mapConnections.put(player.getName(), pieceTypeServer);
                BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (finalNowReconnect < countRetryReconnect)
                            connect(player, pieceTypeServer, typeConnect, finalNowReconnect + 1);
                        else
                            mapConnections.remove(player.getName());
                    }
                };
                bukkitRunnable.runTaskLater(plugin, 100L);
                mapConnectionsRunnable.put(player.getName(), bukkitRunnable);
            } else if (mapConnections.get(player.getName()) != null &&
                    mapConnectionsRunnable.get(player.getName()) != null &&
                    nowReconnect > 0) {
                mapConnectionsRunnable.get(player.getName()).runTaskLater(plugin, 100L);
            } else {
                printMessage(player, plugin.getFileManager().getMSG("Connect.alreadyTryReconnect") + mapConnections.get(player.getName()));
            }
            return;
        }

        mapConnections.remove(player.getName());
        BukkitRunnable bukkitRunnable = mapConnectionsRunnable.get(player.getName());
        if (bukkitRunnable != null)
            bukkitRunnable.cancel();
        mapConnectionsRunnable.remove(player.getName());

        switch (typeConnect) {
            case RANDOM:
                Collections.shuffle(servers);
                break;

            case MAX:
                Collections.reverse(servers);
                break;
        }

        if (servers.size() > 0) {

            if (Wrapper.getInstance().getCurrentServiceInfoSnapshot().getName().equalsIgnoreCase(servers.get(0).getName())) {
                printMessage(player, plugin.getFileManager().getMSG("Connect.areYouHere") + pieceTypeServer);
                return;
            }

            if (servers.get(0).getStatus() != TypeStatusServer.ONLINE) {
                printMessage(player, plugin.getFileManager().getMSG("Connect.serverNotAvailable") + pieceTypeServer);
                return;
            }

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(servers.get(0).getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        }
    }

    @Override
    public void connectByServer(Player player, String nameGroupOrServer) {
        connect(player, nameGroupOrServer, TypeConnect.MIN, 0);
    }

    @Override
    public void connectByServer(Player player, String nameGroupOrServer, TypeConnect typeConnect) {
        connect(player, nameGroupOrServer, typeConnect, 0);
    }
}
