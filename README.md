# ConnectionApi

## Maven
```xml

<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.mc-towercraft</groupId>
    <artifactId>ConnectionService</artifactId>
    <version>connection-api</version>
    <scope>provided</scope>
</dependency>


```

## Code check plugin
```java

 Plugin serverConnectionApi = Bukkit.getPluginManager().getPlugin("ConnectionApi");
        if (serverConnectionApi != null) {
            PluginManager pm = Bukkit.getPluginManager();
            getLogger().info("ConnectionApi start detected");
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (pm.isPluginEnabled(serverConnectionApi)) {
                       //Buisnes logic
                    }
                }
            }).start();
        } else
            throw new RuntimeException("Could not find ConnectionApi Plugin!! Plugin can not work without it!");

```

## Example Api

```java
//Get info for all servers to List
List<ServerModel> infoServers =  InfoServersApi.getInstance().getServers();

//Connect player to other server
ServerConnectApi.getInstance().connectByServer(Player, nameServerOrGroup, [TypeConnect(MIN, MAX, RANDOM)])
```

## Example Event