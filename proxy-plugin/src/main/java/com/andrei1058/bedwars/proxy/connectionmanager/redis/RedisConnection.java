package com.andrei1058.bedwars.proxy.connectionmanager.redis;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedisConnection {

    private final String channel;
    private final JedisPool dataPool;
    private final JedisPool subscriptionPool;
    private final RedisPubSubListener redisPubSubListener;

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
     * Publishes a message to a specified channel using Redis.
     *
     * @param message the message to be sent
     */
    public void sendMessage(String message){
        try (Jedis jedis = dataPool.getResource()) {
            // Publish the message to the specified channel
            BedWarsProxy.debug("sending message: " + message + " on channel: " + channel);
            jedis.publish(channel, message);
        } catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
        }
    }

    public boolean isClosed(){
        return dataPool.isClosed();
    }

}
