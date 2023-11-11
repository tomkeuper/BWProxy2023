package com.tomkeuper.bedwars.proxy.api.communication;

import org.jetbrains.annotations.NotNull;
import com.google.gson.JsonObject;

public interface IRedisClient {
    void sendMessage(@NotNull JsonObject message, @NotNull String addonIdentifier);
}
