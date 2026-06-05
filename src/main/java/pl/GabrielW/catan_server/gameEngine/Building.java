package pl.GabrielW.catan_server.gameEngine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.GabrielW.catan_server.model.Player;

import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class Building {
    private Player player;
    private HashSet< Coordinate > coordinates;

    public Building( HashSet< Coordinate > cords , Player player ) {
        this.coordinates = cords;
        this.player = player;
    }

}
