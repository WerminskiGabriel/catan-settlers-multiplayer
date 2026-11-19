package pl.GabrielW.catan_server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.GabrielW.catan_server.api.dto.request.CreateGameRequest;
import pl.GabrielW.catan_server.api.dto.request.DiscardRequest;
import pl.GabrielW.catan_server.api.dto.request.GameRequests;
import pl.GabrielW.catan_server.api.dto.response.GameStateResponse;
import pl.GabrielW.catan_server.service.GameService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@RestController
@RequestMapping( "api/games" )
public class GameController {

    private final GameService gameService;

    public GameController( GameService gameService ) {
        this.gameService = gameService;
    }

    @PostMapping()
    public GameStateResponse createGame( @RequestBody CreateGameRequest request ) {
        return gameService.createGame( request.playerNickNames( ) );
    }

    @GetMapping( "/{id}" )
    public GameStateResponse getGame( @PathVariable UUID id ) {
        return gameService.getGame( id );
    }

    @PostMapping( "/{id}/roll" )
    public ResponseEntity< GameStateResponse > rollDice( @PathVariable UUID id ) {
        GameStateResponse response = gameService.rollDice( id );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/end-turn" )
    public ResponseEntity< GameStateResponse > endTurn( @PathVariable UUID id ) {
        GameStateResponse response = gameService.endTurn( id );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/discard" )
    public ResponseEntity< GameStateResponse > discardCards( @PathVariable UUID id , @RequestBody DiscardRequest request ) {
        GameStateResponse response = gameService.discardCards( id , request.playerNickName( ) , request.cards( ) );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/build-road" )
    public ResponseEntity< GameStateResponse > buildRoad(
            @PathVariable UUID id ,
            @RequestBody GameRequests.BuildRequest request
    ) {
        HashSet< pl.GabrielW.catan_server.gameEngine.Coordinate > coords =
                request.coordinates( ) != null ? new HashSet<>( request.coordinates( ) ) : new HashSet<>( );
        GameStateResponse response = gameService.buildRoad( id , coords );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/build-settlement" )
    public ResponseEntity< GameStateResponse > buildSettlement(
            @PathVariable UUID id ,
            @RequestBody GameRequests.BuildRequest request
    ) {
        HashSet< pl.GabrielW.catan_server.gameEngine.Coordinate > coords =
                request.coordinates( ) != null ? new HashSet<>( request.coordinates( ) ) : new HashSet<>( );
        GameStateResponse response = gameService.buildBuilding( id , coords );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/upgrade-settlement" )
    public ResponseEntity< GameStateResponse > upgradeSettlement(
            @PathVariable UUID id ,
            @RequestBody GameRequests.BuildRequest request
    ) {

        HashSet< pl.GabrielW.catan_server.gameEngine.Coordinate > coords =
                request.coordinates( ) != null ? new HashSet<>( request.coordinates( ) ) : new HashSet<>( );

        GameStateResponse response = gameService.upgradeBuilding( id , coords );
        return ResponseEntity.ok( response );
    }


    @PostMapping( "/{id}/place-robber" )
    public ResponseEntity< GameStateResponse > placeRobber(
            @PathVariable UUID id ,
            @RequestBody GameRequests.PlaceRobberRequest request
    ) {
        GameStateResponse response = gameService.placeRobber( id , request.coordinate( ) );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/steal" )
    public ResponseEntity< GameStateResponse > stealCard(
            @PathVariable UUID id ,
            @RequestBody GameRequests.StealRequest request
    ) {
        GameStateResponse response = gameService.stealCard( id , request.victimNickName( ) );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/trade" )
    public ResponseEntity< GameStateResponse > trade(
            @PathVariable UUID id ,
            @RequestBody GameRequests.TradeRequest request
    ) {
        HashMap< pl.GabrielW.catan_server.gameEngine.CardType, Integer > thisCards =
                request.thisCards( ) != null ? new HashMap<>( request.thisCards( ) ) : new HashMap<>( );
        HashMap< pl.GabrielW.catan_server.gameEngine.CardType, Integer > otherCards =
                request.otherCards( ) != null ? new HashMap<>( request.otherCards( ) ) : new HashMap<>( );

        GameStateResponse response = gameService.tradeCards(
                id ,
                thisCards ,
                otherCards ,
                request.otherPlayerNickName( )
        );
        return ResponseEntity.ok( response );
    }

    @PostMapping( "/{id}/bank-trade" )
    public ResponseEntity< GameStateResponse > bankTrade(
            @PathVariable UUID id ,
            @RequestBody GameRequests.BankTradeRequest request
    ) {
        GameStateResponse response = gameService.bankTrade( id , request.give( ) , request.receive( ) );
        return ResponseEntity.ok( response );
    }
}
