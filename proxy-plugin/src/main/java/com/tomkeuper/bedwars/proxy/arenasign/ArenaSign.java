package com.tomkeuper.bedwars.proxy.arenasign;

import com.tomkeuper.bedwars.proxy.api.CachedArena;

public interface ArenaSign {

    String getGroup();

    String getArena();

    CachedArena getAssignedArena();

    void refresh();

    void remove();

    boolean equals(String world, int x, int y, int z);

    void setStatus(SignStatus status);

    SignStatus getStatus();

    enum SignStatus {
        REFRESHING, NO_DATA, FOUND
    }
}
