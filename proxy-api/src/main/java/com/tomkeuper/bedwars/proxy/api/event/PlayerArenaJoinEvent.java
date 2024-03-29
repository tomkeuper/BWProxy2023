package com.tomkeuper.bedwars.proxy.api.event;

import com.tomkeuper.bedwars.proxy.api.CachedArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerArenaJoinEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final CachedArena arena;
    private boolean spectator = false;

    /**
     * Called when a player joins an arena.
     * This is called before sending him to the server.
     *
     * @param player player.
     * @param arena  arena.
     */
    public PlayerArenaJoinEvent(Player player, CachedArena arena) {
        this.player = player;
        this.arena = arena;
    }

    public PlayerArenaJoinEvent(Player player, CachedArena arena, boolean spectator) {
        this(player, arena);
        this.spectator = spectator;
    }

    /**
     * Get player.
     *
     * @return player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get arena.
     *
     * @return arena.
     */
    public CachedArena getArena() {
        return arena;
    }

    /**
     * Check if the player has joined as spectator.
     *
     * @return true if the player is a spectator, otherwise false.
     */
    public boolean isSpectator() {
        return spectator;
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

    /**
     * Check if event is cancelled.
     *
     * @return true if canceled.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Change value.
     *
     * @param cancel cancel event.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}