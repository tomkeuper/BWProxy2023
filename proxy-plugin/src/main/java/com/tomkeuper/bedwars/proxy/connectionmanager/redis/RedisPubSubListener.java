package com.tomkeuper.bedwars.proxy.connectionmanager.redis;

import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.api.CachedArena;
import com.tomkeuper.bedwars.proxy.arenamanager.ArenaManager;
import com.tomkeuper.bedwars.proxy.arenamanager.TpRequest;
import com.tomkeuper.bedwars.proxy.rejoin.RemoteReJoin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisPubSubListener extends JedisPubSub {
    private final String BW_CHANNEL;
    public RedisPubSubListener(String channel) {
        this.BW_CHANNEL = channel;
    }

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equals(BW_CHANNEL)) {
            final JsonObject json;
            try {
                json = new JsonParser().parse(message).getAsJsonObject();
                BedWarsProxy.debug("incoming json message: " + json.toString());
            } catch (JsonSyntaxException e) {
                BedWarsProxy.getPlugin().getLogger().warning("Received bad data from redis message channel " + BW_CHANNEL);
                return;
            }
            if (!json.has("type")) return;

            //serverName,remoteIdentifier,arenaName,group,status,maxPlayers,currentPlayers,displayNamePerLanguage
            //OPERATION,data
            if (!json.has("type")) return;
            switch (json.get("type").getAsString()) {
                case "RC":
                    if (!json.has("server")) break;
                    if (!json.has("arena_id")) break;
                    if (!json.has("uuid")) break;
                    CachedArena arena = ArenaManager.getInstance().getArena(json.get("server").getAsString(), json.get("arena_id").getAsString());
                    if (arena == null) return;
                    com.tomkeuper.bedwars.proxy.api.RemoteReJoin rrj2 = RemoteReJoin.getReJoin(UUID.fromString(json.get("uuid").getAsString()));
                    if (rrj2 != null) rrj2.destroy();
                    new RemoteReJoin(UUID.fromString(json.get("uuid").getAsString()), arena);
                    break;
                case "RD":
                    if (!json.has("server")) break;
                    if (!json.has("uuid")) break;
                    com.tomkeuper.bedwars.proxy.api.RemoteReJoin rrj = RemoteReJoin.getReJoin(UUID.fromString(json.get("uuid").getAsString()));
                    if (rrj == null) return;
                    if (rrj.getArena().getServer().equals(json.get("server").getAsString())) rrj.destroy();
                    break;
                case "Q":
                    if (!json.has("requester")) break;
                    if (!json.has("name")) break;
                    if (!json.has("server_name")) break;
                    if (!json.has("arena_id")) break;
                    TpRequest tr = TpRequest.getTpRequest(UUID.fromString(json.get("requester").getAsString()));
                    if (tr != null && tr.getTarget().equalsIgnoreCase(json.get("name").getAsString())){
                        CachedArena ar = ArenaManager.getInstance().getArena(json.get("server_name").getAsString(),
                                json.get("arena_id").getAsString());
                        if (ar != null) tr.setArena(ar);
                    }
                    break;
                case "PD":
                    // Party Disband
                    if (!json.has("owner")) break;
                    if (BedWarsProxy.getParty().isInternal()) {
                        Bukkit.getLogger().info("Disbanding party with owner: " + json.get("owner").getAsString());
                        BedWarsProxy.getParty().disband(UUID.fromString(json.get("owner").getAsString()));
                    }
                    break;
                case "PR":
                    // Party Remove (player)
                    if (!json.has("owner")) break;
                    if (BedWarsProxy.getParty().isInternal()){
                        Bukkit.getLogger().info("Removing " +json.get("owner").getAsString() + " from party");
                        BedWarsProxy.getParty().removeFromParty(UUID.fromString(json.get("owner").getAsString()));
                    }
                    break;
            }
        }
    }
}
