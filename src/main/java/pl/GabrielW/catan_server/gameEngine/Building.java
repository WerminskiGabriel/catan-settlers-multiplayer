package pl.GabrielW.catan_server.gameEngine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.GabrielW.catan_server.model.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
public class Building {
    private Player player;
    private HashSet< Coordinate > coordinates;

    public Building( HashSet< Coordinate > cords , Player player ) {
        Objects.requireNonNull( cords , "Coordinates can not be null" );
        Objects.requireNonNull( player , "Player can not be null" );
        if( cords.size() != 3 ) {
            throw new IllegalArgumentException( "Building Coordinates must be size 3" );
        }
        List< Coordinate > cordsList = new ArrayList<>( cords );

        for( int i = 0 ; i < 3 ; i++ ) {
            Coordinate cordA = cordsList.get( i );
            Coordinate cordB = cordsList.get( ( i + 1 ) % 3 );

            if( !Board.isNeighbour( cordA , cordB ) ) {
                throw new IllegalArgumentException( "wrong Coordinates of a Building - make sure they are adjacent" );
            }
        }

        this.coordinates = cords;
        this.player = player;
    }

}
