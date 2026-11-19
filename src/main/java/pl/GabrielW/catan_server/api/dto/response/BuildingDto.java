package pl.GabrielW.catan_server.api.dto.response;

import pl.GabrielW.catan_server.gameEngine.Building;
import pl.GabrielW.catan_server.gameEngine.Coordinate;
import pl.GabrielW.catan_server.gameEngine.PlayerColor;
import pl.GabrielW.catan_server.model.Player;

import java.util.HashSet;

public record BuildingDto( HashSet< Coordinate > coordinates , String playerNickName , PlayerColor playerColor ,
                           boolean isCity ) {
    public static BuildingDto from( Building building ) {
        Player player = building.getPlayer( );
        return new BuildingDto( building.getCoordinates( ) , player.getNickName( ) , player.getColor( ) ,
                building.isCity( ) );
    }

}
