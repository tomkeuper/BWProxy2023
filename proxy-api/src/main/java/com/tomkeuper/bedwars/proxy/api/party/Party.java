package com.tomkeuper.bedwars.proxy.api.party;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface Party {

    /**
     * Checks if a player has a party.
     * @param p The player to check.
     * @return true if the player has a party, false otherwise.
     */
    boolean hasParty(UUID p);

    /**
     * Retrieves the size of the party that the player belongs to.
     * @param p The player.
     * @return The size of the party that the player belongs to.
     */
    int partySize(UUID p);

    /**
     * Checks if the player is the owner/leader of the party.
     * @param p The player to check.
     * @return true if the player is the owner/leader of the party, false otherwise.
     */
    boolean isOwner(UUID p);

    /**
     * Retrieves a list of all members in the party.
     * @param owner The owner/leader of the party.
     * @return A UUID list of all members in the party.
     */
    List<UUID> getMembers(UUID owner);

    /**
     * Creates a new party with the specified owner and members.
     * @param owner The owner/leader of the party.
     * @param members Additional members to add to the party.
     */
    void createParty(Player owner, Player... members);

    /**
     * Adds a member to the party.
     * @param owner The owner/leader of the party.
     * @param member The member to add to the party.
     */
    void addMember(UUID owner, Player member);

    /**
     * Removes a member from the party.
     * @param member The member to remove from the party.
     */
    void removeFromParty(UUID member);

    /**
     * Disbands the party, removing all members and deleting the party.
     * @param owner The owner/leader of the party.
     */
    void disband(UUID owner);

    /**
     * Checks if a player is a member of a specific party.
     * @param owner The owner/leader of the party.
     * @param check The player to check.
     * @return true if the player is a member of the party, false otherwise.
     */
    boolean isMember(UUID owner, UUID check);

    /**
     * Removes a player from the party.
     * @param owner The owner/leader of the party.
     * @param target The player to remove.
     */
    void removePlayer(UUID owner, UUID target);

    /**
     * Checks if the party implementation is internal or external.
     * @return true if the party implementation is internal, false otherwise.
     */
    boolean isInternal();

    /**
     * Retrieves the owner/leader of the party that the specified member belongs to.
     * @param player The member of the party.
     * @return The owner/leader of the party.
     */
    UUID getOwner(UUID player);
}
