# ConnectionApi

## Maven
```typescript jsx

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

```java
//Get info for all servers to List
List<ServerModel> infoServers =  InfoServersApi.getInstance().getServers();

//Connect player to other server
ServerConnectApi.getInstance().connectByServer(Player, nameServerOrGroup, [TypeConnect(MIN, MAX, RANDOM)])
```

## Example Event