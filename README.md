# ConnectionApi

## Maven
```

<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.mc-towercraft</groupId>
    <artifactId>ConnectionService</artifactId>
    <version>connection-api</version>
</dependency>


```

## Example Api

```
List<ServerModel> infoServers =  InfoServersApi.getInstance().getServers();

ServerConnectApi.getInstance().connectByServer(Player, nameServerOrGroup, [TypeConnect(MIN, MAX, RANDOM)])
```

## Example Event