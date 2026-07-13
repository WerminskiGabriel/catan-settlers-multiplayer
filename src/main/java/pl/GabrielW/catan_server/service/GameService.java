package pl.GabrielW.catan_server.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.GabrielW.catan_server.api.dto.GameActionRequest;
import pl.GabrielW.catan_server.gameEngine.Game;
import pl.GabrielW.catan_server.gameEngine.TurnPhase;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final HashMap< UUID, Game > games = new HashMap<>( );

    public Game createGame( List< String > playerNickNames ) {
        Game game = new Game( );
        for( String nickName : playerNickNames ) {
            game.addPlayer( nickName );
        }
        UUID id = game.getId( );
        game.startGame( );
        games.put( id , game );

        return game;
    }

    public Game getGame( UUID id ) {
        Game game = games.get( id );

        if( game == null ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND );
        }

        return game;
    }

    public Game makeAction( UUID id , GameActionRequest action ) {
        Game game = getGame( id );

        switch( action.phase( ) ) {
            case TurnPhase.ROLL_PHASE -> game.rollDice( );
            case TurnPhase.DISCARD_PHASE -> game.discardCards( );
            case TurnPhase.STEAL_PHASE -> game.stealCard( game.getPlayer( action.otherPlayerNickName( ) ) );
            case TurnPhase.GAME_OVER -> ;
            case TurnPhase.TRADE_AND_BUILD ->
                    game.trade( action.TradeCardsOut( ) , action.TradeCardsIn( ) , game.getPlayer( action.otherPlayerNickName( ) ) );
                    game.buildBuilding( action.coordinates() );
                    game.buildRoad(  action.coordinates());
            ;
            case TurnPhase.ROBBER_PLACEMENT -> game.placeRobber( action.coordinate() );
            case TurnPhase.START_MENU -> ;
            default -> throw new IllegalStateException( "Wrong action" );
        }

        return game;
    }

}
