
[![Documentation](https://i.imgur.com/7QDbrIS.png)](https://wiki.tomkeuper.com/docs/BedWarsProxy/) [![Report a Bug](https://i.imgur.com/Z1qOYLC.png)](https://github.com/tomkeuper/BWProxy2023/issues) [![API](https://i.imgur.com/JfMTMMc.png)](https://javadocs.tomkeuper.com/) [![Discord](https://i.imgur.com/yBySzkU.png)](https://discord.gg/kPaBGwhmjf)

**BWProxy2023** is a plugin for Bungeecord networks that are running BedWars2023 in BUNGEE mode. This plugin provides features for lobby servers: join gui/ signs, placeholders and more.

![Signs](https://i.imgur.com/ggNRp4D.png?1)

**FEATURES**
- Dynamic game signs
- Static game signs
- Global arena selector
- Per group arena selector
- Arena rejoin system
- Admin /bw tp <player> command to catch cheaters
- Per player language system in sync with arenas
- PAPI placeholders
- Internal party system
- API for developers

**HOW TO USE**

All the information you need can be found on its [documentation / wiki](https://wiki.tomkeuper.com/docs/BedWarsProxy/).

**DOWNLOAD**
- [Latest release](#)
- [Development builds](https://github.com/tomkeuper/BWProxy2023/releases)

**MAVEN REPO**
```xml
<repository>
    <id>bedwars2023-releases</id>
    <url>https://repo.tomkeuper.com/repository/releases/</url>
</repository>

<!--Use for Snapshots only!-->
<repository>
    <id>bedwars2023-snapshots</id>
    <url>https://repo.tomkeuper.com/repository/snapshots/</url>
</repository>
<!-- -->

<dependency>
    <groupId>com.tomkeuper.bedwars</groupId>
    <artifactId>proxy-api</artifactId>
    <version>{version}</version>
    <scope>provided</scope>
</dependency>
```

[![Discord](https://discordapp.com/api/guilds/760851292826107926/widget.png?style=shield)](https://discord.gg/kPaBGwhmjf) ![Servers](https://img.shields.io/bstats/servers/20358)