package pl.GabrielW.catan_server.gameEngine;

import org.junit.jupiter.api.Test;
import pl.GabrielW.catan_server.model.Player;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuildingTests {

    @Test
    public void buildingInit() {
        PlayerFactory playerFactory = new PlayerFactory();
        Player player = playerFactory.CreatePlayer( "Aang" );

        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > cordsCorrect = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        assertDoesNotThrow( () -> new Building( cordsCorrect , player ) , "Correct Coordinates should not throw errors " );

        HashSet< Coordinate > cordsIncorrect = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.WEST ) ) );
        assertThrows( IllegalArgumentException.class , () -> new Building( cordsIncorrect , player ) , "incorrect not adjacent should Coordinates throw Exception" );

        HashSet< Coordinate > cordsIncorrectSize = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );
        assertThrows( IllegalArgumentException.class , () -> new Building( cordsIncorrectSize , player ) , "incorrect not size 3  Coordinates should throw Exception" );

    }
}
