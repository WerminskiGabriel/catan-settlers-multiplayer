package pl.GabrielW.catan_server.service;

import org.springframework.stereotype.Service;
import pl.GabrielW.catan_server.AuthTokens;
import pl.GabrielW.catan_server.api.dto.response.GameStateResponse;
import pl.GabrielW.catan_server.api.dto.response.PlayerJoinGameResponse;
import pl.GabrielW.catan_server.gameEngine.CardType;
import pl.GabrielW.catan_server.gameEngine.Coordinate;
import pl.GabrielW.catan_server.gameEngine.Game;
import pl.GabrielW.catan_server.model.Player;

import java.util.*;

@Service
public class GameService {

    private final HashMap< UUID, Game > games = new HashMap<>( );

    private Game findGameOrThrow( UUID id ) {
        Game game = games.get( id );

        if( game == null ) {
            throw new NoSuchElementException( "Game was not found: " + id );
        }

        return game;
    }

    public GameStateResponse createGame( List< String > playerNickNames ) {
        Game game = new Game( );
        for( String nickName : playerNickNames ) {
            game.addPlayer( nickName );
        }
        UUID id = game.getId( );
        game.startGame( );
        games.put( id , game );

        return GameStateResponse.from( game );
    }

    public GameStateResponse getGame( UUID id ) {
        Game game = findGameOrThrow( id );
        return GameStateResponse.from( game );
    }

    public GameStateResponse getGame( UUID id , String myPlayerNickName ) {
        Game game = findGameOrThrow( id );
        return GameStateResponse.from( game , myPlayerNickName );
    }

    public PlayerJoinGameResponse joinGame( UUID gameId , String nickName ) {
        Game game = findGameOrThrow( gameId );

        String rawToken = AuthTokens.generateToken( );
        String tokenHash = AuthTokens.hashToken( rawToken );

        Player player = game.addPlayer( nickName , tokenHash );

        return new PlayerJoinGameResponse( gameId , player.getId( ) , rawToken );
    }

    public GameStateResponse rollDice( UUID id ) {
        Game game = findGameOrThrow( id );
        game.rollDice( );
        return GameStateResponse.from( game );
    }

    public GameStateResponse discardCards( UUID id, String playerNickName, HashMap<CardType,Integer> cardsToDiscard ) {
        Game game = findGameOrThrow( id );
        game.discardCardsOnePlayer( playerNickName, cardsToDiscard );
        return GameStateResponse.from( game );
    }

    public GameStateResponse stealCard( UUID id , String stealFromPlayerNickName ) {
        Game game = findGameOrThrow( id );
        game.stealCard( stealFromPlayerNickName );
        return GameStateResponse.from( game );
    }

    public GameStateResponse tradeCards( UUID id , HashMap< CardType, Integer > thisCards , HashMap< CardType, Integer > otherCards , String playerNickName ) {
        Game game = findGameOrThrow( id );
        game.trade( thisCards , otherCards , playerNickName );
        return GameStateResponse.from( game );
    }

    public GameStateResponse buildRoad( UUID id , HashSet< Coordinate > newBCoords ) {
        Game game = findGameOrThrow( id );
        game.buildRoad( newBCoords );
        return GameStateResponse.from( game );
    }

    public GameStateResponse buildBuilding( UUID id , HashSet< Coordinate > newBCoords ) {
        Game game = findGameOrThrow( id );
        game.buildBuilding( newBCoords );
        return GameStateResponse.from( game );
    }

    public GameStateResponse upgradeBuilding( UUID id , HashSet< Coordinate > coordinates ) {
        Game game = findGameOrThrow( id );
        game.upgradeBuilding( coordinates );
        return GameStateResponse.from( game );
    }

    public GameStateResponse bankTrade( UUID id , CardType give , CardType receive ) {
        Game game = findGameOrThrow( id );
        game.bankTrade( give , receive );
        return GameStateResponse.from( game );
    }

    public GameStateResponse placeRobber( UUID id , Coordinate newRobber ) {
        Game game = findGameOrThrow( id );
        game.placeRobber( newRobber );

        return GameStateResponse.from( game );
    }

    public GameStateResponse endTurn( UUID id ) {
        Game game = findGameOrThrow( id );
        game.endTurn( );

        return GameStateResponse.from( game );
    }
}
