package pl.GabrielW.catan_server.gameEngine;

import pl.GabrielW.catan_server.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerFactory {

    private List< PlayerColor > AvailableColors;

    public PlayerFactory() {
        this.AvailableColors = new ArrayList<>( List.of( PlayerColor.values() ) );
    }

    public Player CreatePlayer( String nickName ) {
        PlayerColor color = AvailableColors.removeLast();

        Player player = new Player( nickName , color );

        return player;

    }


}
