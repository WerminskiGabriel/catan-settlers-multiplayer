package pl.GabrielW.catan_server.api.dto.response;

import pl.GabrielW.catan_server.gameEngine.Coordinate;
import pl.GabrielW.catan_server.gameEngine.Game;
import pl.GabrielW.catan_server.gameEngine.TurnPhase;
import pl.GabrielW.catan_server.model.Player;

import java.util.List;
import java.util.UUID;

public record GameStateResponse(
        UUID id ,
        TurnPhase phase ,
        String currentPlayerNickName ,
        List< PlayerPublicDto > publicPlayers ,
        BoardDto board ,
        Coordinate robberCord ,
        int dicesSum ,
        PlayerPrivateDto myPlayer
) {
    public static GameStateResponse from( Game game ) {
        return from( game , null );
    }

    public static GameStateResponse from( Game game , String myPlayerNickName ) {
        Player player = myPlayerNickName != null ? game.getPlayer( myPlayerNickName ) : null;
        PlayerPrivateDto myPlayer = player != null ? PlayerPrivateDto.from( player ) : null;

        return new GameStateResponse(
                game.getId( ) ,
                game.getTurnPhase( ) ,
                game.getCurrentPlayer( ).getNickName( ) ,
                game.getPlayers( ).stream( ).map( PlayerPublicDto::from ).toList( ) ,
                BoardDto.from( game.getBoard( ) ) ,
                game.getRobberCord( ) ,
                game.getRollingDicesFactory( ).getSum( ) ,
                myPlayer
        );
    }
}
