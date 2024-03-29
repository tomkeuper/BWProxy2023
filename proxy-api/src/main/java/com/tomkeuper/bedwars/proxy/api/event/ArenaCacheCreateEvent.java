package com.tomkeuper.bedwars.proxy.api.event;

import com.tomkeuper.bedwars.proxy.api.CachedArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaCacheCreateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final CachedArena arena;

    /**
     * Called when an arena is enabled successfully. It's called after a restart as well.
     *
     * @param a target arena.
     */
    public ArenaCacheCreateEvent(CachedArena a) {
        this.arena = a;
    }

    /**
     * Get the arena.
     *
     * @return arena.
     */
    public CachedArena getArena() {
        return arena;
    }

    /**
     * Bukkit stuff.
     *
     * @return handlers.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Bukkit stuff.
     *
     * @return handlers.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
