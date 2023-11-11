package com.tomkeuper.bedwars.proxy.connectionmanager.redis;

import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.api.ArenaStatus;
import com.tomkeuper.bedwars.proxy.api.CachedArena;
import com.tomkeuper.bedwars.proxy.api.event.ArenaCacheCreateEvent;
import com.tomkeuper.bedwars.proxy.api.event.ArenaCacheUpdateEvent;
import com.tomkeuper.bedwars.proxy.arenamanager.ArenaManager;
import com.tomkeuper.bedwars.proxy.arenamanager.LegacyArena;
import org.bukkit.Bukkit;

import java.util.Map;

public class RetrieveArenaTask implements Runnable {

    private final RedisConnection redisConnection;

    public RetrieveArenaTask(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
        Bukkit.getScheduler().runTaskTimerAsynchronously(BedWarsProxy.getPlugin(), this, 10L,10L);
    }

    @Override
    public void run() {
        // Get the available arenas
        Map<String, Map<String, String>> arenaMap = redisConnection.getAvailableArenas();

        // empty the list
        ArenaManager.getInstance().resetArenaMap();

        // Process each arena data
        for (Map.Entry<String, Map<String, String>> entry : arenaMap.entrySet()) {
            Map<String, String> arenaData = entry.getValue();

            CachedArena ca = ArenaManager.getInstance().getArena(arenaData.get("server_name"), arenaData.get("arena_identifier"));
            if (ca != null) {
                ca.setLastUpdate(System.currentTimeMillis());
                boolean modified = false;
                if (ca.getMaxPlayers() != Integer.parseInt(arenaData.get("arena_max_players"))) {
                    ca.setMaxPlayers(Integer.parseInt(arenaData.get("arena_max_players")));
                    modified = true;
                }
                if (ca.getCurrentPlayers() != Integer.parseInt(arenaData.get("arena_current_players"))) {
                    ca.setCurrentPlayers(Integer.parseInt(arenaData.get("arena_current_players")));
                    modified = true;
                }
                if (ArenaStatus.valueOf(arenaData.get("arena_status")) != ca.getStatus()) {
                    ca.setStatus(ArenaStatus.valueOf(arenaData.get("arena_status")));
                    modified = true;
                }
                if (modified) {
                    CachedArena finalCa = ca;
                    Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> {
                        ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(finalCa);
                        Bukkit.getPluginManager().callEvent(e);
                    });
                }
                break;
            }
            if (arenaData.get("spectate") == null) {
                ca = new LegacyArena(arenaData.get("arena_identifier"), arenaData.get("server_name"), arenaData.get("arena_group"), arenaData.get("arena_name"),
                        ArenaStatus.valueOf(arenaData.get("arena_status")), Integer.parseInt(arenaData.get("arena_max_players")), Integer.parseInt(arenaData.get("arena_current_players")), Integer.parseInt(arenaData.get("arena_max_in_team")));
            } else {
                ca = new LegacyArena(arenaData.get("arena_identifier"), arenaData.get("server_name"), arenaData.get("arena_group"), arenaData.get("arena_name"),
                        ArenaStatus.valueOf(arenaData.get("arena_status")), Integer.parseInt(arenaData.get("arena_max_players")), Integer.parseInt(arenaData.get("arena_current_players")), Integer.parseInt(arenaData.get("arena_max_in_team")), Boolean.parseBoolean(arenaData.get("spectate")));
            }
            CachedArena finalCa = ca;
            Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> {
                ArenaManager.getInstance().registerArena(finalCa);
                ArenaCacheCreateEvent e = new ArenaCacheCreateEvent(finalCa);
                Bukkit.getPluginManager().callEvent(e);
            });
        }
    }
}
