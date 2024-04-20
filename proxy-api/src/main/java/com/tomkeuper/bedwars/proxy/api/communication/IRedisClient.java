package com.tomkeuper.bedwars.proxy.api.communication;

import org.jetbrains.annotations.NotNull;
import com.google.gson.JsonObject;

public interface IRedisClient {
    void sendMessage(@NotNull JsonObject message, @NotNull String addonIdentifier);

    /**
     * Retrieve the data associated with a specific identifier from the Redis database.
     * Settings can be stored by the BedWars plugin.
     *
     * @param redisSettingIdentifier the identifier of the setting to be checked.
     * @return the data as a string associated with the specified identifier
     */
    String retrieveSetting(@NotNull String redisSettingIdentifier);
}
