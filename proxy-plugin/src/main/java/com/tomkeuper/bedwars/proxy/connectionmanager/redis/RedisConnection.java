package com.tomkeuper.bedwars.proxy.connectionmanager.redis;

import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.configuration.ConfigPath;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisConnection {

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


    public boolean connect(){
        try {
            listenerPool.execute(() -> {
                BedWarsProxy.debug("Subscribing to redis channel: " + channel);
                try (final Jedis listenerConnection = subscriptionPool.getResource()){
                    listenerConnection.subscribe(redisPubSubListener, channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                 * Since Jedis PubSub channel listener is thread-blocking,
                 * we can shut down thread when the pub-sub listener stops
                 * or fails.
                 */
                BedWarsProxy.debug("Unsubscribing from redis channel: " + channel);
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

    public void close(){
        BedWarsProxy.debug("Closing redis connections...");
        redisPubSubListener.unsubscribe();
        dataPool.close();
    }

}
