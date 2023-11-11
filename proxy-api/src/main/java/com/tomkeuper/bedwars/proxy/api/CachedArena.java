package com.tomkeuper.bedwars.proxy.api;

import org.bukkit.entity.Player;

public interface CachedArena {

    /**
     *
     */
    String getRemoteIdentifier();

    /**
     * Get the used world name for the arena.
     * @return The name of the world used by the arena.
     */
    String getArenaName();

    /**
     *
     */
    String getServer();

    /**
     * Get the arena name as a message that can be used on signs, etc.
     * @param lang The language for which to retrieve the display name.
     * @return The translated display name.
     */
    String getDisplayName(Language lang);

    /**
     * Get the group of the arena.
     * @return The group of the arena.
     */
    String getArenaGroup();

    /**
     * Get the arena display group for the given player.
     * @param lang The language for which to retrieve the display group.
     * @return The translated display group.
     */
    String getDisplayGroup(Language lang);

    /**
     * Set the arena group of the arena.
     * @param group The arena group name to set.
     */
    void setArenaGroup(String group);

    /**
     * Set the used world name for the arena.
     * @param newName New world name for the arena.
     */
    void setArenaName(String newName);

    /**
     * Get the current status of the arena.
     * @return The current status of the arena.
     */
    ArenaStatus getStatus();

    /**
     * Get the display status for the arena. This returns a message that can be used on signs, etc.
     * @param lang The language used to retrieve the display status message.
     * @return The display status message for the arena.
     */
    String getDisplayStatus(Language lang);

    /**
     * Change the game status of the arena.
     * @param arenaStatus The game status to change to.
     */
    void setStatus(ArenaStatus arenaStatus);

    /**
     * Get the maximum number of players allowed in the arena.
     * @return The maximum number of players allowed.
     */
    int getMaxPlayers();

    /**
     * Get the current players in the arena.
     * @return The number of players inside the arena.
     */
    int getCurrentPlayers();

    /**
     *
     */
    long getLastUpdate();

    /**
     * Set the current players in the arena.
     * @param players The number of players inside the arena.
     */
    void setCurrentPlayers(int players);

    /**
     *
     */
    void setLastUpdate(long time);

    /**
     * Set the maximum number of players allowed in the arena.
     * @param players The maximum number of players allowed.
     */
    void setMaxPlayers(int players);

    /**
     * Get the maximum number of players allowed in a team.
     * @return The maximum number of players allowed in a team.
     */
    int getMaxInTeam();

    /**
     * Set the maximum number of players allowed in a team.
     * @param max The maximum number of players allowed in a team.
     */
    void setMaxInTeam(int max);

    /**
     * Add a player as Spectator
     * @param player Player to add.
     * @param targetPlayer Target player to add.
     * @return true if was added
     */
    boolean addSpectator(Player player, String targetPlayer);

    /**
     * Add a player to the arena
     * NULL if not party
     * @param player Player to add.
     * @param partyOwnerName The name of the party owner.
     * @return true if was added.
     */
    boolean addPlayer(Player player, String partyOwnerName);

    /**
     * Rejoin an arena.
     * @param player The player who wants to rejoin the arena.
     * @return true if the player can rejoin the arena, false otherwise.
     */
    boolean reJoin(RemoteReJoin player);

    /**
     *
     */
    boolean equals(CachedArena arena);
}
