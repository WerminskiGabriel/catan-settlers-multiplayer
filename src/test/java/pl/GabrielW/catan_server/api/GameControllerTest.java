package pl.GabrielW.catan_server.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.GabrielW.catan_server.api.dto.GameStateResponse;
import pl.GabrielW.catan_server.gameEngine.Coordinate;
import pl.GabrielW.catan_server.gameEngine.TurnPhase;
import pl.GabrielW.catan_server.service.GameService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( GameController.class )
@Import( GlobalExceptionHandler.class )
class GameControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    GameService gameService;

    private GameStateResponse sampleResponse( UUID id , TurnPhase phase ) {
        return new GameStateResponse( id , phase , "G" , List.of( "G" , "W" ) );
    }

    @Test
    void createGame( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.createGame( List.of( "G" , "W" ) ) )
                .thenReturn( sampleResponse( id , TurnPhase.ROLL_PHASE ) );

        mockMvc.perform( post( "/api/games" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( """
                                {"playerNickNames":["G","W"]}
                                """ ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.players[0]" ).value( "G" ) )
                .andExpect( jsonPath( "$.phase" ).value( "ROLL_PHASE" ) );
    }

    @Test
    void getUnknownGameReturns404( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.getGame( id ) )
                .thenThrow( new NoSuchElementException( "Game was not found: " + id ) );

        mockMvc.perform( get( "/api/games/" + id ) )
                .andExpect( status( ).isNotFound( ) )
                .andExpect( jsonPath( "$.error" ).value( "Game was not found: " + id ) );
    }

    @Test
    void rollDiceSuccess( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.rollDice( id ) )
                .thenReturn( sampleResponse( id , TurnPhase.TRADE_AND_BUILD ) );

        mockMvc.perform( post( "/api/games/" + id + "/roll" ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.phase" ).value( "TRADE_AND_BUILD" ) );
    }

    @Test
    void rollDiceWrongPhaseReturns400( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.rollDice( id ) )
                .thenThrow( new IllegalStateException( "Wrong turnPhase" ) );

        mockMvc.perform( post( "/api/games/" + id + "/roll" ) )
                .andExpect( status( ).isBadRequest( ) )
                .andExpect( jsonPath( "$.error" ).value( "Wrong turnPhase" ) );
    }

    @Test
    void buildRoadSuccess( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.buildRoad( eq( id ) , any( ) ) )
                .thenReturn( sampleResponse( id , TurnPhase.TRADE_AND_BUILD ) );

        mockMvc.perform( post( "/api/games/" + id + "/build-road" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( """
                                {
                                  "coordinates": [
                                    {"q": 0, "r": 0},
                                    {"q": 1, "r": 0}
                                  ]
                                }
                                """ ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.id" ).value( id.toString( ) ) );
    }

    @Test
    void buildSettlementSuccess( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.buildBuilding( eq( id ) , any( ) ) )
                .thenReturn( sampleResponse( id , TurnPhase.TRADE_AND_BUILD ) );

        mockMvc.perform( post( "/api/games/" + id + "/build-settlement" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( """
                                {
                                  "coordinates": [
                                    {"q": 0, "r": 0},
                                    {"q": 1, "r": 0},
                                    {"q": 0, "r": 1}
                                  ]
                                }
                                """ ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.currentPlayerNickName" ).value( "G" ) );
    }

    @Test
    void endTurnSuccess( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.endTurn( id ) )
                .thenReturn( sampleResponse( id , TurnPhase.ROLL_PHASE ) );

        mockMvc.perform( post( "/api/games/" + id + "/end-turn" ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.phase" ).value( "ROLL_PHASE" ) );
    }

    @Test
    void placeRobberSuccess( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.placeRobber( eq( id ) , eq( new Coordinate( 1 , 1 ) ) ) )
                .thenReturn( sampleResponse( id , TurnPhase.STEAL_PHASE ) );

        mockMvc.perform( post( "/api/games/" + id + "/place-robber" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( """
                                {
                                  "coordinate": {"q": 1, "r": 1}
                                }
                                """ ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.phase" ).value( "STEAL_PHASE" ) );
    }

    @Test
    void stealCardSuccess( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.stealCard( id , "W" ) )
                .thenReturn( sampleResponse( id , TurnPhase.TRADE_AND_BUILD ) );

        mockMvc.perform( post( "/api/games/" + id + "/steal" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( """
                                {
                                  "victimNickName": "W"
                                }
                                """ ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.phase" ).value( "TRADE_AND_BUILD" ) );
    }

    @Test
    void tradeSuccess( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.tradeCards( eq( id ) , any( ) , any( ) , eq( "W" ) ) )
                .thenReturn( sampleResponse( id , TurnPhase.TRADE_AND_BUILD ) );

        mockMvc.perform( post( "/api/games/" + id + "/trade" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( """
                                {
                                  "thisCards": {"WOOD": 1},
                                  "otherCards": {"BRICK": 1},
                                  "otherPlayerNickName": "W"
                                }
                                """ ) )
                .andExpect( status( ).isOk( ) );
    }
}
