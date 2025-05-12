package com.tomkeuper.bedwars.proxy.connectionmanager.redis;

import com.google.gson.JsonObject;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.api.communication.IRedisClient;
import com.tomkeuper.bedwars.proxy.configuration.ConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.material.Bed;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RedisConnection implements IRedisClient {

    private final String channel;
    private final JedisPool dataPool;
    private final JedisPool subscriptionPool;
    private final RedisPubSubListener redisPubSubListener;
    private final ExecutorService listenerPool = Executors.newCachedThreadPool();

    public RedisConnection() {
        JedisPoolConfig config = new JedisPoolConfig();
        dataPool = new JedisPool(config, BedWarsProxy.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_HOST),
                BedWarsProxy.config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PORT),0,
                BedWarsProxy.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PASSWORD));

        // Need a new pool for the subscriptions since they will allow only `(P|S)SUBSCRIBE / (P|S)UNSUBSCRIBE / PING / QUIT / RESET` commands while being subscribed.
        subscriptionPool = new JedisPool(config, BedWarsProxy.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_HOST),
                BedWarsProxy.config.getInt(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PORT),0,
                BedWarsProxy.config.getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PASSWORD));

        this.channel = BedWarsProxy.config.getYml().getString(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_CHANNEL);

        redisPubSubListener = new RedisPubSubListener(channel);
    }


    public boolean connect() {
        try {
            listenerPool.execute(() -> {
                BedWarsProxy.debug("Subscribing to redis channel: " + channel);

                while (!Thread.currentThread().isInterrupted()) {
                    try (final Jedis listenerConnection = subscriptionPool.getResource()) {
                        BedWarsProxy.getPlugin().getLogger().info("Successfully connected to Redis channel: " + channel);
                        listenerConnection.subscribe(redisPubSubListener, channel);
                    } catch (Exception e) {
                        if (BedWarsProxy.debug) e.printStackTrace();
                        else {
                            if(e.getMessage() != null && e.getMessage().contains("end of stream")) {
                                BedWarsProxy.getPlugin().getLogger().warning("Redis connection lost. Attempting to reconnect...");
                            } else {
                                BedWarsProxy.getPlugin().getLogger().severe("An error occurred while trying to connect to Redis: " + e.getMessage());
                            }
                        }

                        try {
                            // Wait before retrying to avoid rapid reconnect loops
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break; // Exit loop if thread is interrupted
                        }
                    }
                }

                BedWarsProxy.getPlugin().getLogger().info("Stopped listening to redis channel: " + channel);
                listenerPool.shutdown();
            });
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }


    /**
     * Retrieves a map of available arenas and their associated data from the Redis database.
     *
     * @return a map containing information about available arenas, with each arena name mapped to its corresponding data
     */
    public Map<String, Map<String, String>> getAvailableArenas() {
        Map<String, Map<String, String>> availableArenas = new HashMap<>();

        try (Jedis jedis = dataPool.getResource()) {
            // Get all keys starting with the server identifier.
            Set<String> keys = jedis.keys("bwa-*");

            // Retrieve values for each key and store them in the availableArenas map.
            for (String key : keys) {
                Map<String, String> arenaDataMap = jedis.hgetAll(key);
                availableArenas.put(key, arenaDataMap);
            }
        } catch (Exception ignored) {
        }

        return availableArenas;
    }

    /**
     * Retrieve the data associated with a specific identifier from the Redis database.
     *
     * @param redisSettingIdentifier the identifier of the setting to be checked.
     * @return the data as a string associated with the specified identifier
     */
    public String retrieveSetting(String redisSettingIdentifier){
        try (Jedis jedis = dataPool.getResource()) {
            String key = "settings";;
            if (jedis.exists(key)) {
                String retrievedSetting = jedis.hget(key, redisSettingIdentifier);
                return retrievedSetting;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Publishes a message to a specified channel using Redis.
     *
     * @param message the message to be sent
     */
    public void sendMessage(String message){
        if (message == null) return;
        if (message.isEmpty()) return;
        try (Jedis jedis = dataPool.getResource()) {
            // Publish the message to the specified channel
            BedWarsProxy.debug("sending message: " + message + " on channel: " + channel);
            jedis.publish(channel, message);
        } catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(@NotNull JsonObject data, @NotNull String addonIdentifier) {
        try (Jedis jedis = dataPool.getResource()) {
            // Publish the message to the specified channel

            JsonObject json = new JsonObject();
            json.addProperty("type", "AM");
            json.addProperty("addon_name", addonIdentifier); // PR = Party Remove
            json.addProperty("addon_data", data.toString());

            BedWarsProxy.debug("sending message: " + json + " on channel: " + channel);
            jedis.publish(channel, json.toString());
        } catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
        }
    }

    public boolean isClosed(){
        return dataPool.isClosed();
    }

    public void close(){
        BedWarsProxy.debug("Closing redis connections...");
        redisPubSubListener.unsubscribe();
        dataPool.close();
    }

}
