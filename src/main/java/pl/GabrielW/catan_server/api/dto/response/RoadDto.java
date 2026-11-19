package pl.GabrielW.catan_server.api.dto.response;

import pl.GabrielW.catan_server.gameEngine.Coordinate;
import pl.GabrielW.catan_server.gameEngine.PlayerColor;
import pl.GabrielW.catan_server.gameEngine.Road;
import pl.GabrielW.catan_server.model.Player;

import java.util.HashSet;

public record RoadDto(
        HashSet< Coordinate > coordinates ,
        String playerNickName ,
        PlayerColor playerColor
) {
    public static RoadDto from( Road road ) {
        Player player = road.getPlayer( );
        return new RoadDto(
                road.getCoordinates( ) ,
                player.getNickName( ) ,
                player.getColor( )
                );
    }

}
