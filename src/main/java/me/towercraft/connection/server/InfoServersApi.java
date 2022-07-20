package me.towercraft.connection.server;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import me.towercraft.connection.api.InfoServers;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static me.towercraft.connection.ConnectionApi.plugin;

public class InfoServersApi implements InfoServers {

    private static InfoServers instance;

    public static InfoServers getInstance() {
        if (instance == null) {
            instance = new InfoServersApi();
        }
        return instance;
    }

    private final long period;
    private final long startTimeServer;

    private final List<ServerModel> servers = new ArrayList<>();
    private final Map<String, Long> serversMap = new ConcurrentHashMap<>();

    private InfoServersApi() {
        period = plugin.getConfig().getLong("General.updateInterval", 3) * 20L;
        startTimeServer = plugin.getConfig().getLong("General.startTimeServer", 30) * 1000;
        init();
    }

    private void init() {
        plugin.getLogger().info("Start get servers");
        new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (servers) {
                    servers.clear();

                    for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices()) {
                        ServerModel.ServerModelBuilder modelBuilder = new ServerModel.ServerModelBuilder();
                        modelBuilder.group(cloudService.getName().split("-")[0]);
                        modelBuilder.dynamic(!cloudService.getConfiguration().isStaticService());
                        modelBuilder.name(cloudService.getName());
                        modelBuilder.maxPlayers(cloudService.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0));
                        modelBuilder.nowPlayer(cloudService.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0));
                        modelBuilder.mapName(cloudService.getProperty(BridgeServiceProperty.MOTD).orElse("NameMap"));

                        if (serversMap.get(cloudService.getName()) == null) {
                            serversMap.put(cloudService.getName(), System.currentTimeMillis() + startTimeServer);
                        }

                        TypeStatusServer status = TypeStatusServer.OFFLINE;

                        if (serversMap.get(cloudService.getName()) > System.currentTimeMillis())
                            status = TypeStatusServer.STARTING;
                        else if (cloudService.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false)) {
                            status = TypeStatusServer.ONLINE;

                            if (cloudService.getProperty(BridgeServiceProperty.IS_IN_GAME).orElse(false)) {
                                status = TypeStatusServer.IN_GAME;
                            }
                        } else if (cloudService.getProperty(BridgeServiceProperty.IS_STARTING).orElse(false) ||
                                !cloudService.isConnected()) {
                            status = TypeStatusServer.STARTING;
                        }

                        modelBuilder.status(status);
                        servers.add(modelBuilder.build());
                    }

                    List<String> temp = new ArrayList<>(serversMap.keySet());
                    temp.removeAll(servers.stream().map(ServerModel::getName).collect(Collectors.toList()));
                    temp.forEach(serversMap::remove);
                }
            }
        }.runTaskTimer(plugin, 0, period);
    }

    @Override
    public List<ServerModel> getServers() {
        synchronized (servers) {
            return new ArrayList<>(servers);
        }
    }

    public int getMaxOnlineByGroup(String group) {
        synchronized (servers) {
            return servers
                    .stream()
                    .filter(s -> s.getName().contains(group))
                    .map(ServerModel::getMaxPlayers)
                    .reduce(0, Integer::sum);
        }
    }

    public int getCountOnlineByGroup(String group) {
        synchronized (servers) {
            return servers
                    .stream()
                    .filter(s -> s.getName().contains(group))
                    .map(ServerModel::getNowPlayer)
                    .reduce(0, Integer::sum);
        }
    }
}
