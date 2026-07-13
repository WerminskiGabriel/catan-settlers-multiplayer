package pl.GabrielW.catan_server.api.dto;

import pl.GabrielW.catan_server.gameEngine.Game;
import pl.GabrielW.catan_server.gameEngine.TurnPhase;

import java.util.List;
import java.util.UUID;

public record GameStateResponse(
        UUID id ,
        TurnPhase phase ,
        String currentPlayerNickName ,
        List< String > players
) {
    public static GameStateResponse from( UUID id, Game game ){
        return new GameStateResponse(
                id,
                game.getTurnPhase(),
                game.getCurrentPlayer().getNickName(),
                game.getPlayers().stream().map( p -> p.getNickName() ).toList()
        );
    }

}
