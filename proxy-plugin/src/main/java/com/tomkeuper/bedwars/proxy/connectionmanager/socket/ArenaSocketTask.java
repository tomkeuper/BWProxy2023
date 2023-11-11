package com.tomkeuper.bedwars.proxy.connectionmanager.socket;

import com.tomkeuper.bedwars.proxy.api.ArenaStatus;
import com.tomkeuper.bedwars.proxy.api.CachedArena;
import com.tomkeuper.bedwars.proxy.api.event.ArenaCacheCreateEvent;
import com.tomkeuper.bedwars.proxy.api.event.ArenaCacheUpdateEvent;
import com.tomkeuper.bedwars.proxy.arenamanager.ArenaManager;
import com.tomkeuper.bedwars.proxy.arenamanager.LegacyArena;
import com.tomkeuper.bedwars.proxy.rejoin.RemoteReJoin;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.tomkeuper.bedwars.proxy.arenamanager.TpRequest;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class ArenaSocketTask implements Runnable {

    private Socket socket;
    private Scanner scanner;
    private PrintWriter out;

    public ArenaSocketTask(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(socket);
    }

    @Override
    public void run() {
        while (ServerSocketTask.compute && socket.isConnected()) {
            if (scanner.hasNext()) {
                String message = scanner.next();
                if (message.isEmpty()) continue;
                final JsonObject json;
                try {
                    JsonElement jse = new JsonParser().parse(message);
                    if (jse.isJsonNull() || !jse.isJsonObject()){
                        BedWarsProxy.getPlugin().getLogger().log(Level.WARNING, "Received bad data from: " + socket.getInetAddress().toString());
                        continue;
                    }
                    json = jse.getAsJsonObject();
                } catch (JsonSyntaxException e) {
                    BedWarsProxy.getPlugin().getLogger().log(Level.WARNING, "Received bad data from: " + socket.getInetAddress().toString());
                    continue;
                }
                //serverName,remoteIdentifier,arenaName,group,status,maxPlayers,currentPlayers,displayNamePerLanguage
                //OPERATION,data
                if (!json.has("type")) continue;
                switch (json.get("type").getAsString()) {
                    case "RC":
                        if (!json.has("server")) break;
                        if (!json.has("arena_id")) break;
                        if (!json.has("uuid")) break;
                        CachedArena arena = ArenaManager.getInstance().getArena(json.get("server").getAsString(), json.get("arena_id").getAsString());
                        if (arena == null) continue;
                        com.tomkeuper.bedwars.proxy.api.RemoteReJoin rrj2 = RemoteReJoin.getReJoin(UUID.fromString(json.get("uuid").getAsString()));
                        if (rrj2 != null) rrj2.destroy();
                        new RemoteReJoin(UUID.fromString(json.get("uuid").getAsString()), arena);
                        break;
                    case "RD":
                        if (!json.has("server")) break;
                        if (!json.has("uuid")) break;
                        com.tomkeuper.bedwars.proxy.api.RemoteReJoin rrj = RemoteReJoin.getReJoin(UUID.fromString(json.get("uuid").getAsString()));
                        if (rrj == null) continue;
                        if (rrj.getArena().getServer().equals(json.get("server").getAsString())) rrj.destroy();
                        break;
                    case "UPDATE":
                        if (!json.has("server_name")) break;
                        if (!json.has("arena_identifier")) break;
                        if (!json.has("arena_max_players")) break;
                        if (!json.has("arena_current_players")) break;
                        if (!json.has("arena_status")) break;
                        if (!json.has("server_name")) break;
                        if (!json.has("arena_group")) break;
                        if (!json.has("arena_current_players")) break;
                        if (!json.has("arena_max_in_team")) break;
                        //update,serverName,remoteIdentifier,arenaName,group,status,maxP,currP,maxInTeam (for parties)
                        CachedArena ca = ArenaManager.getInstance().getArena(json.get("server_name").getAsString(), json.get("arena_identifier").getAsString());
                        if (ca != null) {
                            ca.setLastUpdate(System.currentTimeMillis());
                            boolean modified = false;
                            if (ca.getMaxPlayers() != json.get("arena_max_players").getAsInt()) {
                                ca.setMaxPlayers(json.get("arena_max_players").getAsInt());
                                modified = true;
                            }
                            if (ca.getCurrentPlayers() != json.get("arena_current_players").getAsInt()) {
                                ca.setCurrentPlayers(json.get("arena_current_players").getAsInt());
                                modified = true;
                            }
                            if (ArenaStatus.valueOf(json.get("arena_status").getAsString()) != ca.getStatus()) {
                                ca.setStatus(ArenaStatus.valueOf(json.get("arena_status").getAsString()));
                                modified = true;
                            }

                            if (modified) {
                                CachedArena finalCa = ca;
                                Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> {
                                    ArenaManager.getInstance().registerServerSocket(json.get("server_name").getAsString(), this);
                                    ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(finalCa);
                                    Bukkit.getPluginManager().callEvent(e);
                                });
                            }
                            break;
                        }
                        if (json.get("spectate") == null) {
                            ca = new LegacyArena(json.get("arena_identifier").getAsString(), json.get("server_name").getAsString(), json.get("arena_group").getAsString(), json.get("arena_name").getAsString(),
                                    ArenaStatus.valueOf(json.get("arena_status").getAsString()), json.get("arena_max_players").getAsInt(), json.get("arena_current_players").getAsInt(), json.get("arena_max_in_team").getAsInt());
                        } else {
                            ca = new LegacyArena(json.get("arena_identifier").getAsString(), json.get("server_name").getAsString(), json.get("arena_group").getAsString(), json.get("arena_name").getAsString(),
                                    ArenaStatus.valueOf(json.get("arena_status").getAsString()), json.get("arena_max_players").getAsInt(), json.get("arena_current_players").getAsInt(), json.get("arena_max_in_team").getAsInt(), json.get("spectate").getAsBoolean());
                        }
                        CachedArena finalCa = ca;
                        Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> {
                            ArenaManager.getInstance().registerServerSocket(json.get("server_name").getAsString(), this);
                            ArenaManager.getInstance().registerArena(finalCa);
                            ArenaCacheCreateEvent e = new ArenaCacheCreateEvent(finalCa);
                            Bukkit.getPluginManager().callEvent(e);
                        });
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
            } else {
                try {
                    socket.close();
                    BedWarsProxy.debug("Socket closed: " + socket.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public PrintWriter getOut() {
        return out;
    }
}

