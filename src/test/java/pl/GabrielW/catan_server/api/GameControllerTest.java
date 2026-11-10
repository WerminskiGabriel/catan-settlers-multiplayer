package pl.GabrielW.catan_server.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pl.GabrielW.catan_server.gameEngine.Game;
import pl.GabrielW.catan_server.service.GameService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( GameController.class ) // for testing sliced backend: here -> just Gamecontroller
class GameControllerTest {

    @Autowired //injects mockMvc bean instance of webMvcTest bean
    MockMvc mockMvc;

    @MockitoBean // bean is just a class instance constructed and maintained by Spring Boot framework
    GameService gameService; //mocked gameServiced injected with bean

    @Test
    void createGame( ) throws Exception {
        Game game = new Game( );
        game.addPlayer( "G" );
        game.addPlayer( "W" );
        game.startGame( );

        //when controller calls for createGame method with exactyl G and W ->
        // captures it and  returns this created above mocked game
        when( gameService.createGame( List.of( "G" , "W" ) ) ).thenReturn( game );

        mockMvc.perform( post( "/api/games" ) // used to simulate real api calls
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( """
                                {"playerNickNames":["G","W"]}
                                """ ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.players[0]" ).value( "G" ) )
                .andExpect( jsonPath( "$.phase" ).value( "ROLL_PHASE" ) );
    }

    @Test
    void getUnknownGameMaps404( ) throws Exception {
        UUID id = UUID.randomUUID( );
        when( gameService.getGame( id ) )
                .thenThrow( new ResponseStatusException( HttpStatus.NOT_FOUND ) );

        mockMvc.perform( get( "/api/games/" + id ) )
                .andExpect( status( ).isNotFound( ) );
    }
}
