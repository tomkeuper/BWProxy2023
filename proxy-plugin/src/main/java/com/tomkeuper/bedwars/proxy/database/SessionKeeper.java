package com.tomkeuper.bedwars.proxy.database;

public class SessionKeeper implements Runnable {

    private final MySQL db;

    public SessionKeeper(MySQL db){
        this.db = db;
    }

    @Override
    public void run() {
        if (!db.isConnected()) db.connect();
        db.ping();
    }
}
