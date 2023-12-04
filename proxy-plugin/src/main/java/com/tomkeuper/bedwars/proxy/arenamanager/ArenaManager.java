package com.tomkeuper.bedwars.proxy.arenamanager;

import com.tomkeuper.bedwars.proxy.api.*;
import com.tomkeuper.bedwars.proxy.api.event.ArenaCacheRemoveEvent;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.configuration.ConfigPath;
import com.tomkeuper.bedwars.proxy.language.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArenaManager implements BedWars.ArenaUtil {

    private LinkedList<CachedArena> arenas = new LinkedList<>();
    private static ArenaManager instance = null;

    private ArenaManager() {
        instance = this;
    }

    public static ArenaManager getInstance() {
        return instance == null ? new ArenaManager() : instance;
    }

    public void registerArena(@NotNull CachedArena arena) {
        if (getArena(arena.getServer(), arena.getRemoteIdentifier()) != null) return;
        arenas.add(arena);
    }

    public void resetArenaMap() {
        arenas = new LinkedList<>();
    }

    public CachedArena getArena(String server, String remoteIdentifier) {

        List<CachedArena> arenaList = getArenas();

        for (CachedArena ca : arenaList) {
            if (ca.getServer().equals(server) && ca.getRemoteIdentifier().equals(remoteIdentifier)) return ca;
        }
        return null;
    }

    public static List<CachedArena> getArenas() {
        return Collections.unmodifiableList(getInstance().arenas);
    }

    public static Comparator<? super CachedArena> getComparator() {
        return new Comparator<CachedArena>() {
            @Override
            public int compare(CachedArena o1, CachedArena o2) {
                if (o1.getStatus() == ArenaStatus.STARTING && o2.getStatus() == ArenaStatus.STARTING) {
                    return Integer.compare(o2.getCurrentPlayers(), o1.getCurrentPlayers());
                } else if (o1.getStatus() == ArenaStatus.STARTING && o2.getStatus() != ArenaStatus.STARTING) {
                    return -1;
                } else if (o2.getStatus() == ArenaStatus.STARTING && o1.getStatus() != ArenaStatus.STARTING) {
                    return 1;
                } else if (o1.getStatus() == ArenaStatus.WAITING && o2.getStatus() == ArenaStatus.WAITING) {
                    // balance nodes
                    if (o1.getServer().equals(o2.getServer())){
                        return -1;
                    }
                    return Integer.compare(o2.getCurrentPlayers(), o1.getCurrentPlayers());
                } else if (o1.getStatus() == ArenaStatus.WAITING && o2.getStatus() != ArenaStatus.WAITING) {
                    return -1;
                } else if (o2.getStatus() == ArenaStatus.WAITING && o1.getStatus() != ArenaStatus.WAITING) {
                    return 1;
                } else if (o1.getStatus() == ArenaStatus.PLAYING && o2.getStatus() == ArenaStatus.PLAYING) {
                    return -1;
                } else if (o1.getStatus() == ArenaStatus.PLAYING && o2.getStatus() != ArenaStatus.PLAYING) {
                    return -1;
                } else return 1;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof CachedArena;
            }
        };
    }

    /**
     * Check if given string is an integer.
     */
    @SuppressWarnings("unused")
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Add a player to the most filled arena from a group.
     */
    public boolean joinRandomFromGroup(@NotNull Player p, String group) {
        if (BedWarsProxy.getParty().hasParty(p.getUniqueId()) && !BedWarsProxy.getParty().isOwner(p.getUniqueId())) {
            p.sendMessage(LanguageManager.get().getMsg(p, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }

        List<CachedArena> arenaList = new ArrayList<>();
        getArenas().forEach(a -> {
            if (a.getArenaGroup().equalsIgnoreCase(group)) arenaList.add(a);
        });

        //shuffle if determined in config
        if (BedWarsProxy.config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_RANDOMARENAS)){
            Collections.shuffle(arenaList);
        } else {
            arenaList.sort(getComparator());
        }


        int amount = BedWarsProxy.getParty().hasParty(p.getUniqueId()) ? BedWarsProxy.getParty().getMembers(p.getUniqueId()).size() : 1;
        for (CachedArena a : arenaList) {
            if (a.getCurrentPlayers() >= a.getMaxPlayers()) continue;
            if (a.getMaxPlayers() - a.getCurrentPlayers() >= amount) {
                a.addPlayer(p, null);
                return true;
            }
        }
        p.sendMessage(LanguageManager.get().getMsg(p, Messages.COMMAND_JOIN_NO_EMPTY_FOUND));
        return true;
    }

    /**
     * Check if arena group exists.
     */
    public static boolean hasGroup(String arenaGroup) {
        for (CachedArena ad : getArenas()) {
            if (ad.getArenaGroup().equalsIgnoreCase(arenaGroup)) return true;
        }
        return false;
    }

    /**
     * Add a player to the most filled arena.
     * Check if is the party owner first.
     */
    public boolean joinRandomArena(@NotNull Player p) {
        if (BedWarsProxy.getParty().hasParty(p.getUniqueId()) && !BedWarsProxy.getParty().isOwner(p.getUniqueId())) {
            p.sendMessage(LanguageManager.get().getMsg(p, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
            return false;
        }
        List<CachedArena> arenaList = new ArrayList<>(getArenas());
        arenaList.sort(getComparator());

        int amount = BedWarsProxy.getParty().hasParty(p.getUniqueId()) ? BedWarsProxy.getParty().getMembers(p.getUniqueId()).size() : 1;
        for (CachedArena a : arenaList) {
            if (a.getCurrentPlayers() >= a.getMaxPlayers()) continue;
            if (a.getMaxPlayers() - a.getCurrentPlayers() >= amount) {
                a.addPlayer(p, null);
                break;
            }
        }
        return true;
    }

    public void disableArena(CachedArena a) {
        arenas.remove(a);
        Bukkit.getPluginManager().callEvent(new ArenaCacheRemoveEvent(a));
    }

    @SuppressWarnings("unused")
    @Nullable
    public static CachedArena getArenaByIdentifier(String identifier) {
        for (CachedArena ca : getArenas()) {
            if (ca.getRemoteIdentifier().equals(identifier)) {
                return ca;
            }
        }
        return null;
    }

    @Override
    public void destroyReJoins(CachedArena arena) {
        List<RemoteReJoin> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, RemoteReJoin> rrj : com.tomkeuper.bedwars.proxy.rejoin.RemoteReJoin.getRejoinByUUID().entrySet()) {
            if (rrj.getValue().getArena().equals(arena)) {
                toRemove.add(rrj.getValue());
            }
        }
        toRemove.forEach(RemoteReJoin::destroy);
    }

    @Override
    public RemoteReJoin getReJoin(UUID player) {
        return com.tomkeuper.bedwars.proxy.rejoin.RemoteReJoin.getReJoin(player);
    }
}
