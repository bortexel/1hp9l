package ru.bortexel.hardcore.storage;

import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;

import java.util.UUID;

@Model(table = "players")
public class StoredPlayerData extends DefaultModel {
    @Property(column = "uuid")
    private final UUID uuid;
    @Property(column = "player_name")
    private String playerName;
    @Property(column = "lives")
    private int lives;
    @Property(column = "points")
    private int points;
    @Property(column = "bought_lives")
    private int boughtLives;

    public StoredPlayerData() {
        this.uuid = UUID.randomUUID();
    }

    public StoredPlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.lives = 9;
    }

    public boolean revokePoints(int points) {
        if (points > this.getPoints()) return false;
        this.setPoints(this.getPoints() - points);
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getBoughtLives() {
        return boughtLives;
    }

    public void setBoughtLives(int boughtLives) {
        this.boughtLives = boughtLives;
    }
}
