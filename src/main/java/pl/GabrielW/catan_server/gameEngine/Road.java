package pl.GabrielW.catan_server.gameEngine;

import pl.GabrielW.catan_server.model.Player;

import java.util.HashSet;
import java.util.Objects;

public class Road {
    private Player player;
    private HashSet< Coordinate > coordinates;

    public Road( HashSet< Coordinate > cords , Player player ) {
        Objects.requireNonNull( cords , "Coordinates can not be null" );
        Objects.requireNonNull( player , "Player can not be null" );
        if (cords.size() != 2 ) {
            throw new IllegalArgumentException("Road Coordinates must be size 2");
        }

        this.coordinates = cords;
        this.player = player;
    }
}
