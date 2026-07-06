package pl.GabrielW.catan_server.gameEngine;

import pl.GabrielW.catan_server.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerFactory {

    private List< PlayerColor > availableColors;

    public PlayerFactory( ) {
        this.availableColors = new ArrayList<>( List.of( PlayerColor.values( ) ) );
    }

    public Player createPlayer( String nickName ) {
        PlayerColor color = availableColors.removeLast( );

        Player player = new Player( nickName , color );

        return player;
    }

    public void addColor( PlayerColor color ) {
        availableColors.add( color );
    }


}
