package pl.GabrielW.catan_server.gameEngine;

import pl.GabrielW.catan_server.model.Player;

import java.util.HashSet;

public class Road {
    private Player player;
    private HashSet< Coordinate > coordinates;

    public Road( HashSet< Coordinate > cords , Player player ) {
        this.coordinates = cords;
        this.player = player;
    }
}
